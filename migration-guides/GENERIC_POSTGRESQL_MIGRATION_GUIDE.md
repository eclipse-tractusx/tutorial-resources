# Generic Migration Guide: Bitnami PostgreSQL → CloudPirates PostgreSQL

**Version**: 1.0  
**Last Updated**: January 2026  
**Estimated Time**: 15-60 minutes depending on database size

> ⚠️ **Important**: This migration requires downtime. Schedule a maintenance window.

## Overview

This guide provides a generic, reusable procedure for migrating PostgreSQL deployments from **Bitnami Helm charts** to **CloudPirates Helm charts** in Kubernetes environments. It is designed to be adaptable to any project using these chart types.

### Scope: INT/TEST vs PROD Environments

⚠️ **Important**: This guide is **primarily designed for INT/TEST environments**. Production migrations with customer data require additional validation and security measures.

**For INT/TEST environments:**
- Limited data loss is generally acceptable
- Missing database can be re-onboarded with test data
- This guide provides sufficient checks for test deployments

**For PROD environments:**
- Requires comprehensive data integrity validation (beyond this guide)
- May require external backup solutions and certified recovery procedures
- Should involve database specialists and change management processes
- Test data should first be migrated to shadow PROD to verify behavior changes

**Version Parity Recommendation**: Test environments should run the **same PostgreSQL version as PROD** to detect potential behavioral changes early, before they occur in production. Treat version upgrades in test as mandatory validation steps, not as optional trials.

### What This Guide Covers

- Pre-migration verification (connectivity, extensions, custom parameters)
- Full database backup using `pg_dumpall` (CLI) or pgAdmin (GUI alternative)
- Safe uninstallation of Bitnami PostgreSQL
- Installation of CloudPirates PostgreSQL
- Data restoration with comprehensive integrity verification
- Business/functional testing post-migration
- Rollback procedure if issues occur

### Version Compatibility

| Component | Tested Versions |
|-----------|-----------------|
| Source | Bitnami PostgreSQL 12.x, 15.x |
| Target | CloudPirates PostgreSQL 18.x |
| Helm | 3.x |
| Kubernetes | 1.25+ |

> ⚠️ **Major Version Upgrade Note**: Migration from PostgreSQL 15 to 18 involves a major version jump (spanning 16, 17, releasing 18). Behavioral changes between versions are expected:
> - Query plans and performance may differ significantly
> - Some SQL syntax or functions may behave differently
> - Extension compatibility should be verified
> - It is **strongly recommended** to test in parallel on duplicate databases before production migration
> - See [PostgreSQL Release Notes](https://www.postgresql.org/docs/release/) for detailed changes between versions

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

Document all extensions currently in use before migration. Extensions may require manual reinstallation or reconfiguration in the new version:

```bash
# List all installed extensions
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c '\dx'

# Get detailed extension info
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c \
  "SELECT extname, extversion, extschema FROM pg_extension ORDER BY extname;"
```

**Before proceeding**: Review the [PostgreSQL version upgrade notes](https://www.postgresql.org/docs/release/) for your target version to verify all extensions are still supported. Document which extensions need to be migrated and whether they require special handling.

#### 1c. Check Customized Database Parameters

If database parameters have been manually adjusted (autovacuum settings, shared_buffers, work_mem, etc.), document them so they can be reapplied to the target instance:

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

**Important**: After migration, verify these parameters are still valid for PostgreSQL 18.x and reapply them if needed (some parameter names or ranges may have changed).

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

**Compare the row counts with Step 1 output** - note that n_live_tup is a statistics estimate and may deviate slightly (especially if statistics were out of date).

#### Step 8b: Extended Data Integrity Checks

For comprehensive validation, especially important after major version upgrades, perform these additional checks:

```bash
# Check constraints and foreign keys
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -d ${PG_DATABASE} -c \
  "SELECT schemaname, tablename, indexname FROM pg_indexes 
   WHERE schemaname NOT IN ('pg_catalog', 'information_schema') 
   ORDER BY schemaname, tablename;"

# Verify sequences are at expected state
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -d ${PG_DATABASE} -c \
  "SELECT schemaname, sequencename, last_value FROM pg_sequences 
   WHERE schemaname NOT IN ('pg_catalog', 'information_schema') 
   ORDER BY schemaname, sequencename;"

# Check roles and permissions were migrated
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c \
  "SELECT usename, usesuper, usecreatedb, usecanlogin FROM pg_user ORDER BY usename;"

# Verify no foreign key constraint violations
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -d ${PG_DATABASE} -c \
  "SELECT constraint_name, table_name FROM information_schema.table_constraints 
   WHERE constraint_type = 'FOREIGN KEY' 
   AND table_schema NOT IN ('pg_catalog', 'information_schema');"
```

**Save this output** for comparison and troubleshooting if issues arise.

#### Step 8c: Verify Extensions Were Migrated

Compare the extensions list from Step 1b with the current state:

```bash
# List extensions again in the new installation
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -c \
  "SELECT extname, extversion FROM pg_extension ORDER BY extname;"
```

**If extensions are missing**: They may need to be manually reinstalled. Check the PostgreSQL documentation for each extension to determine compatibility with version 18.x.

#### Step 8d: Test Critical Queries

After major version upgrades (e.g., 15→18), query behavior can change. Run a sample of critical SQL queries against the migrated database and compare execution plans and results with the source system:

```bash
# Example: Get top 10 slowest queries (if pg_stat_statements is enabled)
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -d ${PG_DATABASE} -c \
  "SELECT query, calls, total_time, mean_time FROM pg_stat_statements 
   ORDER BY mean_time DESC LIMIT 10;" 2>/dev/null || echo "pg_stat_statements not enabled"

# Compare execution plans for critical queries
# Example: replace 'SELECT * FROM orders;' with your actual query
kubectl exec -n ${NAMESPACE} ${POSTGRES_POD} -- \
  env PGPASSWORD="${PG_PASSWORD}" psql -U ${PG_USER} -d ${PG_DATABASE} -c \
  "EXPLAIN ANALYZE SELECT * FROM <your_critical_table> LIMIT 1;"
```

**Document any significant changes** in query performance or execution plans.

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

### Step 10: Business and Functional Testing

After successful migration, run functional/business tests to ensure the application behaves correctly with the new PostgreSQL version. This is especially important after major version upgrades:

```bash
# Example functional tests (adjust for your application)

# 1. Test critical business workflows
echo "Running business workflow tests..."
# - Create a test order/transaction
# - Query data across multiple tables
# - Verify calculations are correct
# - Test report generation

# 2. Test data consistency
echo "Verifying data integrity and calculations..."
# - Run integrity checks for your domain logic
# - Verify totals, counts, and aggregations
# - Test time-based queries and date calculations

# 3. Performance testing
echo "Testing performance characteristics..."
# - Measure query response times
# - Run typical batch operations
# - Verify data export/import performance

# 4. Edge case testing
echo "Testing edge cases..."
# - Test with maximum data volumes
# - Test concurrent user scenarios
# - Test with unusual character sets or data types
```

**Document results**: Record any performance differences or behavioral changes compared to the source system. If significant differences are observed, they may indicate incompatibility with the new PostgreSQL version that requires investigation.

**For major version upgrades specifically**: Compare query execution times and patterns. PostgreSQL 18 introduces various optimizations, but some older queries may need rewriting for optimal performance. Consider running these tests in parallel with the source system to identify needed query optimizations.

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

---

## Important Reminders

### For INT/TEST Environments
- Follow this guide as written
- Document all extensions and custom parameters before migration
- Run business tests after migration to validate behavior
- Use test migration as a learning opportunity for PROD migration planning
- Keep test environment version in sync with PROD for consistency

### For PROD Environments
- Engage database specialists early
- Test on a shadow copy of PROD data first
- Implement additional backup and recovery validations beyond this guide
- Have a certified rollback plan and recovery Time Objective (RTO) agreement
- Schedule downtime during low-usage windows
- Document all manual customizations before migration
- Plan for extended testing and monitoring after cutover

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.1 | April 2026 | Added extension checks, custom parameter documentation, enhanced data integrity checks, business testing, INT/TEST vs PROD scope clarification, major version upgrade warning |
| 1.0 | January 2026 | Initial release, tested with PostgreSQL 15→18 migration |

---

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- SPDX-FileCopyrightText: 2026 LKS Next
