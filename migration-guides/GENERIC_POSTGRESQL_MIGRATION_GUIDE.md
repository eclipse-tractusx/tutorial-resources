# Generic Migration Guide: Bitnami PostgreSQL → CloudPirates PostgreSQL

**Version**: 1.2  
**Last Updated**: April 2026  
**Estimated Time**: 15-60 minutes depending on database size

> ⚠️ **Important**: This migration requires downtime. Schedule a maintenance window.

## Overview

This guide provides a generic, reusable procedure for migrating PostgreSQL deployments from **Bitnami Helm charts** to **CloudPirates Helm charts** in Kubernetes environments. It is designed to be adaptable to any project using these chart types.

### Quick Navigation

| Step | Action | Details |
|------|--------|---------|
| [1](#step-1-pre-migration-checks) | Pre-migration checks | Connectivity, extensions, parameters, row counts |
| [2](#step-2-create-full-backup) | Backup (CLI) | `pg_dumpall` via `kubectl exec` |
| [2b](#step-2b-alternative-backup-database-using-pgadmin) | Backup (GUI) | pgAdmin alternative if no `kubectl exec` access |
| [3](#step-3-validate-backup-integrity) | Validate backup | Header, size, checksum |
| [4](#step-4-stop-current-deployment) | Stop Bitnami | Uninstall release, delete PVC |
| [5](#step-5-update-chart-configuration) | Update chart config | Switch `Chart.yaml` + `values.yaml` to CloudPirates |
| [6](#step-6-deploy-cloudpirates-postgresql) | Deploy CloudPirates | `helm install`, verify version |
| [7](#step-7-restore-data) | Restore data | Pipe backup into new pod |
| [8](#step-8-verify-migration) | Verify & test | Data integrity, extensions, app connectivity, functional tests |

### Scope: INT/TEST vs PROD Environments

> ⚠️ This guide is **primarily designed for INT/TEST environments**. For PROD, additional validation and specialist involvement is required.

| Aspect | INT/TEST | PROD |
|--------|----------|------|
| Data loss tolerance | Limited loss acceptable | Not acceptable |
| Recovery | Re-onboard with test data | Certified recovery procedures |
| Pre-migration testing | This guide is sufficient | Shadow PROD migration first |
| Stakeholders | Development team | Database specialists + change management |

**Version Parity**: Test environments should run the **same PostgreSQL version as PROD** to detect behavioral changes early.

### Version Compatibility

| Component | Tested Versions |
|-----------|-----------------|
| Source | Bitnami PostgreSQL 12.x, 15.x |
| Target | CloudPirates PostgreSQL 18.x |
| Helm | 3.x |
| Kubernetes | 1.25+ |

> ⚠️ **Major Version Upgrade Note**: Migration from PostgreSQL 15 to 18 spans multiple major versions. Query plans, SQL behavior, and extension compatibility may differ. **Test in parallel on duplicate databases before production migration.** See [PostgreSQL Release Notes](https://www.postgresql.org/docs/release/) for details.

### Chart Version Pinning

The CloudPirates chart is released more frequently than Bitnami. For reproducibility and stability, **always pin a specific chart version** in your `Chart.yaml`. See [Step 5](#step-5-update-chart-configuration) for the recommended configuration.

---

## Prerequisites

- Kubernetes cluster access with `kubectl` (the `pg_dumpall` method in Step 2 requires `kubectl exec` / RBAC `pods/exec` permissions)
- Helm 3.x installed
- Maintenance window scheduled
- Sufficient local disk space for database backup (2-3x database size recommended)
- Access to modify Helm chart files (Chart.yaml, values.yaml)

> 💡 **No `kubectl exec` access?** If your environment is managed exclusively through ArgoCD or you lack direct cluster shell access, use the [pgAdmin alternative (Step 2b)](#step-2b-alternative-backup-database-using-pgadmin) instead of `pg_dumpall`.

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

#### 1a. Basic Connectivity and Documentation

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

#### 1b. Check PostgreSQL Extensions

Document all extensions before migration — they may require reinstallation in the new version:

```bash
# List all installed extensions
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c '\dx'

# Get detailed extension info
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c \
  "SELECT extname, extversion, extschema FROM pg_extension ORDER BY extname;"
```

**Before proceeding**: Review the [PostgreSQL version upgrade notes](https://www.postgresql.org/docs/release/) to verify all extensions are supported in your target version.

#### 1c. Check Customized Database Parameters

Document any non-default parameters so they can be reapplied after migration:

```bash
# List all non-default configuration values
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c \
  "SELECT name, setting, unit FROM pg_settings WHERE NOT source IN ('default', 'override') ORDER BY name;"

# Export configuration to file for reference
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c \
  "SELECT name, setting FROM pg_settings WHERE NOT source IN ('default', 'override') ORDER BY name;" > custom_parameters.txt
```

**Important**: After migration, verify these parameters are still valid for PostgreSQL 18.x (some parameter names or ranges may have changed).

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

### Step 2b (Alternative): Backup Database Using pgAdmin

If you do not have `kubectl exec` access (e.g., environments managed exclusively via ArgoCD), you can use **pgAdmin** to create the backup through a web UI instead.

> 💡 **Tip**: If your Helm chart already deploys pgAdmin as a subchart (e.g., `pgadmin4.enabled: true`), you can use that instance directly — it already has network access to the PostgreSQL service within the cluster.
>
> Example subchart configuration:
> ```yaml
> pgadmin4:
>   enabled: true
>   env:
>     email: admin@example.com
>     password: admin-password
>   ingress:
>     enabled: true
>     hosts:
>       - host: pgadmin.example.com
>         paths:
>           - path: /
>             pathType: Prefix
> ```

**Option A — pgAdmin runs inside the same cluster (recommended):**

Since pgAdmin can resolve Kubernetes service names directly, no port-forward is needed:

| Field | Value |
|-------|-------|
| **Host name/address** | `<release-name>-postgresql` (Kubernetes service name) |
| **Port** | `5432` |
| **Maintenance database** | your database name (e.g., `PG_DATABASE`) |
| **Username** | your PostgreSQL admin user (e.g., `postgres`) |
| **Password** | *(retrieve from your Kubernetes secret, see below)* |

**Option B — pgAdmin runs outside the cluster:**

Create a port-forward first, then connect to `localhost`:

```bash
kubectl port-forward svc/<release-name>-postgresql 5432:5432 -n <namespace> &
```

| Field | Value |
|-------|-------|
| **Host name/address** | `localhost` |
| **Port** | `5432` |
| **Maintenance database** | your database name (e.g., `PG_DATABASE`) |
| **Username** | your PostgreSQL admin user (e.g., `postgres`) |
| **Password** | *(retrieve from your Kubernetes secret, see below)* |

To retrieve the database password from your Kubernetes secret:
```bash
kubectl get secret <your-db-secret> -n <namespace> -o jsonpath='{.data.<password-key>}' | base64 -d
```

**Register the server and create the backup:**

1. Open pgAdmin and log in
2. Right-click **Servers** → **Register** → **Server...**
3. In the **General** tab, set a name (e.g., `Migration PostgreSQL`)
4. In the **Connection** tab, fill in the values from the table above
5. Click **Save**
6. In the browser tree, expand **Servers → Migration PostgreSQL → Databases**
7. Right-click your database → **Backup...**
8. Configure the backup:
   - **Filename**: `postgresql_backup` (pgAdmin will add the extension)
   - **Format**: `Custom` (recommended, supports selective restore) or `Plain` (SQL text)
   - **Encoding**: `UTF8`
9. *(Optional)* In the **Data/Objects** tab:
   - Enable **Include CREATE DATABASE statement** for a full restore option
   - Enable **Use Column Inserts** for maximum compatibility
10. Click **Backup**
11. Verify the backup completed successfully in the pgAdmin notifications panel (bell icon, bottom-right)

> ⚠️ **Note**: Unlike `pg_dumpall`, a pgAdmin single-database backup does **not** include roles or other databases. If you need to preserve roles and permissions, either use `pg_dumpall` (Step 2) or back up the global objects separately via pgAdmin's **Backup Server** option.

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
    version: 0.11.0   # Pin an exact version — CloudPirates releases frequently
```

> ⚠️ **Always pin an exact chart version.** The CloudPirates chart is updated more frequently than Bitnami. Pinning a specific version prevents unexpected changes between deployments. Check the [CloudPirates registry](https://hub.docker.com/r/cloudpirates/postgres/tags) for the latest stable release and update the pinned version deliberately after testing.

#### Update `values.yaml`

Adjust the PostgreSQL configuration for CloudPirates:

```yaml
# CloudPirates PostgreSQL configuration
postgresql:
  enabled: true
  nameOverride: "my-service-postgresql"  # Optional: set a unique value when deploying
                                          # multiple PostgreSQL instances in the same release
  
  image:
    registry: docker.io
    repository: postgres
    # tag: "18.0"                # Optional: pin specific PostgreSQL version
  
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

#### 8a. Data Verification

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

# Verify row counts match pre-migration (compare with Step 1 output)
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -d ${PG_DATABASE} -c \
  "SELECT schemaname, relname as table_name, n_live_tup as row_count 
   FROM pg_stat_user_tables ORDER BY n_live_tup DESC;"
```

> Note: `n_live_tup` is a statistics estimate and may deviate slightly.

#### 8b. Extended Integrity Checks

Run these additional checks, especially after major version upgrades:

```bash
# Verify indexes
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -d ${PG_DATABASE} -c \
  "SELECT schemaname, tablename, indexname FROM pg_indexes 
   WHERE schemaname NOT IN ('pg_catalog', 'information_schema') 
   ORDER BY schemaname, tablename;"

# Verify sequences
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -d ${PG_DATABASE} -c \
  "SELECT schemaname, sequencename, last_value FROM pg_sequences 
   WHERE schemaname NOT IN ('pg_catalog', 'information_schema') 
   ORDER BY schemaname, sequencename;"

# Verify roles and permissions
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c \
  "SELECT usename, usesuper, usecreatedb, usecanlogin FROM pg_user ORDER BY usename;"

# Verify extensions (compare with Step 1b output)
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c \
  "SELECT extname, extversion FROM pg_extension ORDER BY extname;"
```

If extensions are missing, they may need manual reinstallation. Check compatibility with PostgreSQL 18.x.

#### 8c. Application Connectivity

```bash
# Get application pod name (adjust label selector for your deployment)
APP_POD=$(kubectl get pod -n ${NAMESPACE} \
  -l app.kubernetes.io/name=${RELEASE_NAME} \
  -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

if [ -n "$APP_POD" ]; then
  kubectl logs -n ${NAMESPACE} ${APP_POD} --tail=100 | grep -i "database\|postgres\|connection"
  kubectl exec -n ${NAMESPACE} ${APP_POD} -- curl -s http://localhost:8080/health || true
else
  echo "No application pods found - verify separately"
fi
```

#### 8d. Functional Testing Checklist

After successful migration, validate application behavior — especially important after major version upgrades:

- [ ] **Business workflows**: Create/read/update/delete operations work correctly
- [ ] **Data consistency**: Totals, counts, and aggregations match expectations
- [ ] **Query performance**: Critical queries execute within acceptable time
- [ ] **Date/time operations**: Time-based queries and calculations are correct
- [ ] **Batch operations**: Data export/import and batch processing work as expected
- [ ] **Critical query plans**: Run `EXPLAIN ANALYZE` on key queries and compare with pre-migration plans

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
| 1.2 | April 2026 | Added chart version pinning recommendation, streamlined verification steps, improved readability |
| 1.1 | April 2026 | Added extension checks, custom parameter documentation, enhanced data integrity checks, business testing, INT/TEST vs PROD scope clarification, major version upgrade warning |
| 1.0 | January 2026 | Initial release, tested with PostgreSQL 15→18 migration |

---

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- SPDX-FileCopyrightText: 2026 LKS Next
