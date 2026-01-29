# Generic Migration Guide: Bitnami PostgreSQL → CloudPirates PostgreSQL

**Version**: 1.0  
**Last Updated**: January 2026  
**Estimated Time**: 15-60 minutes depending on database size

> ⚠️ **Important**: This migration requires downtime. Schedule a maintenance window.

## Overview

This guide provides a generic, reusable procedure for migrating PostgreSQL deployments from **Bitnami Helm charts** to **CloudPirates Helm charts** in Kubernetes environments. It is designed to be adaptable to any project using these chart types.

### What This Guide Covers

- Full database backup using `pg_dumpall`
- Safe uninstallation of Bitnami PostgreSQL
- Installation of CloudPirates PostgreSQL
- Data restoration with integrity verification
- Rollback procedure if issues occur

### Version Compatibility

| Component | Tested Versions |
|-----------|-----------------|
| Source | Bitnami PostgreSQL 12.x, 15.x |
| Target | CloudPirates PostgreSQL 18.x |
| Helm | 3.x |
| Kubernetes | 1.25+ |

---

## Prerequisites

- Kubernetes cluster access with `kubectl`
- Helm 3.x installed
- Maintenance window scheduled
- Sufficient local disk space for database backup (2-3x database size recommended)
- Access to modify Helm chart files (Chart.yaml, values.yaml)

---

## Configuration Variables

Set these variables according to your deployment **before starting**:

```bash
# === CONFIGURE THESE FOR YOUR DEPLOYMENT ===
export NAMESPACE="your-namespace"                    # e.g., "production", "staging"
export RELEASE_NAME="your-release"                   # e.g., "my-app", "backend-service"
export POSTGRES_POD="${RELEASE_NAME}-postgresql-0"   # StatefulSet pod name
export PG_USER="postgres"                            # PostgreSQL admin user
export PG_DATABASE="your-database"                   # Main database name
export SECRET_NAME="your-postgres-secret"            # Secret containing passwords
export SECRET_KEY="postgres-password"                # Key in secret for admin password
export CHART_PATH="/path/to/your/chart"              # Path to Helm chart directory

# Backup configuration
export BACKUP_DIR=~/pg-migration-backup
export BACKUP_DATE=$(date +%Y%m%d_%H%M%S)
export BACKUP_FILE="${BACKUP_DIR}/backup_${BACKUP_DATE}.sql"
```

### Getting Password from Secret

```bash
# Get password from Kubernetes secret
export PG_PASSWORD=$(kubectl get secret ${SECRET_NAME} -n ${NAMESPACE} \
  -o jsonpath="{.data.${SECRET_KEY}}" | base64 --decode)
echo "Password retrieved successfully"
```

---

## Migration Steps

### Step 1: Pre-Migration Checks

```bash
# Verify connectivity to PostgreSQL pod
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c 'SELECT version();'

# Check current database size (helps estimate backup time)
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c \
  "SELECT pg_database.datname, pg_size_pretty(pg_database_size(pg_database.datname)) 
   FROM pg_database ORDER BY pg_database_size(pg_database.datname) DESC;"

# List all databases
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c '\l'

# Document current row counts (IMPORTANT: save this for verification later)
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -d ${PG_DATABASE} -c \
  "SELECT schemaname, relname as table_name, n_live_tup as row_count 
   FROM pg_stat_user_tables ORDER BY n_live_tup DESC;"
```

**Save the output** - you'll compare this after migration to verify data integrity.

---

### Step 2: Create Full Backup

```bash
# Create backup directory
mkdir -p ${BACKUP_DIR} && cd ${BACKUP_DIR}

# Create full backup with pg_dumpall (includes all databases, roles, permissions)
echo "Starting backup at $(date)..."
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" pg_dumpall -U ${PG_USER} > ${BACKUP_FILE}
echo "Backup completed at $(date)"

# Save backup reference
echo "${BACKUP_FILE}" > ${BACKUP_DIR}/LATEST_BACKUP.txt

# Create checksum for integrity verification
sha256sum ${BACKUP_FILE} > ${BACKUP_FILE}.sha256

# Verify backup file
echo "=== Backup Verification ==="
ls -lh ${BACKUP_FILE}
```

---

### Step 3: Validate Backup Integrity

```bash
# Check backup has valid PostgreSQL dump header
if grep -q "PostgreSQL database" ${BACKUP_FILE}; then
  echo "✅ Backup contains valid PostgreSQL dump header"
else
  echo "❌ ERROR: Backup appears invalid - DO NOT PROCEED"
  exit 1
fi

# Check backup file size (should be > 1KB for any real database)
BACKUP_SIZE=$(stat -c%s "${BACKUP_FILE}" 2>/dev/null || stat -f%z "${BACKUP_FILE}")
if [ ${BACKUP_SIZE} -gt 1000 ]; then
  echo "✅ Backup file size: $(ls -lh ${BACKUP_FILE} | awk '{print $5}')"
else
  echo "❌ ERROR: Backup file too small (${BACKUP_SIZE} bytes) - DO NOT PROCEED"
  exit 1
fi

# Verify checksum
sha256sum -c ${BACKUP_FILE}.sha256 && echo "✅ Checksum verified"

# Preview backup content
echo "=== First 30 lines of backup ==="
head -n 30 ${BACKUP_FILE}
```

**⛔ STOP HERE if any verification step fails!**

---

### Step 4: Stop Current Deployment

```bash
cd ${CHART_PATH}

# Uninstall current Helm release
helm uninstall ${RELEASE_NAME} --namespace ${NAMESPACE}

# Wait for pods to terminate
kubectl wait --for=delete pod/${POSTGRES_POD} -n ${NAMESPACE} --timeout=120s

# Delete old PVC (ONLY after backup is verified!)
kubectl delete pvc data-${POSTGRES_POD} -n ${NAMESPACE}

# Wait for PVC deletion
kubectl wait --for=delete pvc/data-${POSTGRES_POD} -n ${NAMESPACE} --timeout=60s

# Verify cleanup
echo "=== Remaining resources ==="
kubectl get pods,pvc -n ${NAMESPACE} | grep -i postgres || echo "No PostgreSQL resources found"
```

**Expected**: No PostgreSQL pods or PVCs remaining.

---

### Step 5: Update Chart Configuration

#### Update `Chart.yaml`

Replace the Bitnami dependency with CloudPirates:

```yaml
# Before (Bitnami)
dependencies:
  - condition: postgresql.enabled
    name: postgresql
    repository: https://charts.bitnami.com/bitnami
    version: 12.x.x

# After (CloudPirates)
dependencies:
  - name: postgres
    alias: postgresql
    condition: postgresql.enabled
    repository: oci://registry-1.docker.io/cloudpirates
    version: 0.11.0
```

#### Update `values.yaml`

Adjust the PostgreSQL configuration for CloudPirates:

```yaml
# CloudPirates PostgreSQL configuration
postgresql:
  enabled: true
  fullnameOverride: ""           # Override the full name
  
  image:
    registry: docker.io
    repository: postgres
    # tag: "18.0"                # Optional: pin specific version
  
  auth:
    password: ""                 # Will use existing secret
    database: "your-database"    # Your database name
    existingSecret: "your-postgres-secret"
    
  persistence:
    enabled: true                # Enable for production
    size: 10Gi
    storageClass: ""             # Use standard or specify
    
  # Init scripts ConfigMap (if you have initialization scripts)
  initdb:
    scriptsConfigMap: ""         # Optional: ConfigMap with init scripts
```

---

### Step 6: Deploy CloudPirates PostgreSQL

```bash
cd ${CHART_PATH}

# Update Helm dependencies
helm dependency update

# Install with new chart
helm install ${RELEASE_NAME} . \
  --namespace ${NAMESPACE} \
  --create-namespace \
  --timeout=300s

# Wait for PostgreSQL pod to be ready
kubectl wait --for=condition=ready pod/${POSTGRES_POD} -n ${NAMESPACE} --timeout=300s

# Refresh password (secret may have been recreated)
export PG_PASSWORD=$(kubectl get secret ${SECRET_NAME} -n ${NAMESPACE} \
  -o jsonpath="{.data.${SECRET_KEY}}" | base64 --decode)

# Verify PostgreSQL version
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c 'SELECT version();'
```

**Expected**: PostgreSQL 18.x version string.

---

### Step 7: Restore Data

```bash
cd ${BACKUP_DIR}

# Get backup file path
BACKUP_FILE=$(cat LATEST_BACKUP.txt)
echo "Restoring from: ${BACKUP_FILE}"

# Restore backup
echo "Starting restore at $(date)..."
cat ${BACKUP_FILE} | kubectl exec -i -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER}
echo "Restore completed at $(date)"
```

### Expected Output During Restore

You will see various messages during restore. Here's what to expect:

| Message | Meaning |
|---------|---------|
| `ERROR: role "xxx" already exists` | ✅ Normal - role was pre-created |
| `ERROR: database "xxx" already exists` | ✅ Normal - database was pre-created |
| `ERROR: relation "xxx" already exists` | ✅ Normal - table was pre-created by init scripts |
| `COPY N` | ✅ Success - N rows were inserted |
| `setval` | ✅ Success - sequence was updated |

**Key indicator**: Look for `COPY N` messages - these confirm data is being restored.

---

### Step 8: Verify Migration

```bash
# Verify PostgreSQL version
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c 'SELECT version();'

# List all databases
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c '\l'

# List all tables in the main database
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -d ${PG_DATABASE} -c '\dt'

# Verify row counts match pre-migration
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -d ${PG_DATABASE} -c \
  "SELECT schemaname, relname as table_name, n_live_tup as row_count 
   FROM pg_stat_user_tables ORDER BY n_live_tup DESC;"
```

**Compare the row counts with Step 1 output** - they should match exactly.

---

### Step 9: Test Application Connectivity

```bash
# Get application pod name (adjust label selector for your deployment)
APP_POD=$(kubectl get pod -n ${NAMESPACE} \
  -l app.kubernetes.io/name=${RELEASE_NAME} \
  -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

if [ -n "$APP_POD" ]; then
  # Check application logs for database connectivity
  kubectl logs -n ${NAMESPACE} ${APP_POD} --tail=100 | grep -i "database\|postgres\|connection"
  
  # Check health endpoint (adjust port and path for your app)
  kubectl exec -n ${NAMESPACE} ${APP_POD} -- curl -s http://localhost:8080/health || true
else
  echo "No application pods found - verify separately"
fi
```

---

## Rollback Procedure

If issues occur after migration, follow these steps to rollback:

```bash
# 1. Uninstall CloudPirates deployment
helm uninstall ${RELEASE_NAME} --namespace ${NAMESPACE}

# 2. Wait for cleanup
kubectl wait --for=delete pod/${POSTGRES_POD} -n ${NAMESPACE} --timeout=120s || true

# 3. Delete PostgreSQL PVC
kubectl delete pvc data-${POSTGRES_POD} -n ${NAMESPACE} --ignore-not-found

# 4. Revert Chart.yaml and values.yaml to Bitnami configuration
cd ${CHART_PATH}
git checkout Chart.yaml values.yaml  # Or manually revert changes

# 5. Update dependencies and redeploy Bitnami
helm dependency update
helm install ${RELEASE_NAME} . --namespace ${NAMESPACE} --create-namespace

# 6. Wait for PostgreSQL to be ready
kubectl wait --for=condition=ready pod/${POSTGRES_POD} -n ${NAMESPACE} --timeout=300s

# 7. Refresh password
export PG_PASSWORD=$(kubectl get secret ${SECRET_NAME} -n ${NAMESPACE} \
  -o jsonpath="{.data.${SECRET_KEY}}" | base64 --decode)

# 8. Restore from backup
cat ${BACKUP_FILE} | kubectl exec -i -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER}

# 9. Verify data
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c '\l'
```

---

## Troubleshooting

### Backup file is empty or too small

```bash
# Check if PostgreSQL pod is running
kubectl get pods -n ${NAMESPACE} | grep postgres

# Check PostgreSQL logs
kubectl logs -n ${NAMESPACE} ${POSTGRES_POD}

# Try connecting manually
kubectl exec -it -n ${NAMESPACE} ${POSTGRES_POD} -- bash
```

### Restore fails with permission errors

```bash
# Check PostgreSQL user permissions
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c '\du'

# Verify you're using the superuser (postgres)
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c 'SELECT current_user, session_user;'
```

### Application cannot connect to database

```bash
# Verify service exists
kubectl get svc -n ${NAMESPACE} | grep postgres

# Check service endpoints
kubectl get endpoints -n ${NAMESPACE} | grep postgres

# Test connectivity from application pod
kubectl exec -n ${NAMESPACE} ${APP_POD} -- nc -zv ${RELEASE_NAME}-postgresql 5432

# Check if connection string changed
# CloudPirates may use different service naming
kubectl get svc -n ${NAMESPACE} -o name | grep postgres
```

### Image pull errors

```bash
# Check pod events
kubectl describe pod -n ${NAMESPACE} ${POSTGRES_POD}

# Verify image exists
docker pull docker.io/postgres:18

# Check if you need Docker Hub credentials for OCI registry
helm registry login registry-1.docker.io
```

---

## Post-Migration Cleanup

After verifying the migration is successful (recommended: wait 24-48 hours):

```bash
# Compress backup for long-term storage
gzip ${BACKUP_FILE}

# Remove temporary files
rm -f ${BACKUP_DIR}/LATEST_BACKUP.txt

# Optional: Archive to external storage
# aws s3 cp ${BACKUP_FILE}.gz s3://your-bucket/backups/
```

---

## Quick Reference: Chart Configuration Differences

### Bitnami Chart Structure

```yaml
postgresql:
  auth:
    postgresPassword: "xxx"
    username: "app"
    password: "xxx"
    database: "mydb"
  primary:
    persistence:
      enabled: true
      size: 10Gi
```

### CloudPirates Chart Structure

```yaml
postgresql:  # Note: uses alias in Chart.yaml
  auth:
    password: "xxx"
    database: "mydb"
    existingSecret: "secret-name"
  persistence:
    enabled: true
    size: 10Gi
  image:
    registry: docker.io
    repository: postgres
```

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | January 2026 | Initial release, tested with PostgreSQL 15→18 migration |

---

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2025, 2026 Contributors to the Eclipse Foundation
- Source URL: https://github.com/eclipse-tractusx
