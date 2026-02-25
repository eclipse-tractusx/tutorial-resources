# Generic Migration Guide: Bitnami Keycloak → CloudPirates Keycloak

**Version**: Keycloak 25.x (Bitnami) → Keycloak 26.x (CloudPirates)

> ⚠️ **Important**: This migration requires downtime. Schedule a maintenance window.

This guide provides generic migration instructions for any project using the Bitnami Keycloak Helm chart that wants to migrate to CloudPirates. For Industry Core Hub specific instructions, see the [ICHub Migration Guide](./BITNAMI_TO_CLOUDPIRATES_KEYCLOAK_MIGRATION_GUIDE.md).

---

## Why Migrate?

| Aspect | Bitnami | CloudPirates |
|--------|---------|--------------|
| Docker Image | Custom Bitnami image | Official Keycloak image |
| Maintenance | Bitnami wrapper layer | Direct from Keycloak team |
| Keycloak Version | 25.x | 26.x (latest) |
| Chart Complexity | Heavy, many features | Lightweight, focused |
| Future Support | May lag behind upstream | Tracks upstream closely |

---

## Migration Steps

### Step 1: Backup Current Installation

#### 1a. Export Realm from Keycloak Admin Console

```bash
# Get the Keycloak admin password
KEYCLOAK_PASSWORD=$(kubectl get secret <release-name>-keycloak -n <namespace> \
  -o jsonpath="{.data.admin-password}" | base64 --decode)

# Port forward to Keycloak (skip if using ingress)
kubectl port-forward svc/<release-name>-keycloak 8080:80 -n <namespace> &
```

1. Navigate to the Keycloak Admin Console (`http://localhost:8080/auth/admin` or your ingress URL)
2. Login with admin credentials
3. Go to **Realm Settings → Action → Partial Export**
4. Select all options and export to JSON
5. Save the JSON file (e.g., `realm-export.json`)

#### 1b. Backup Database Using pgAdmin

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
| **Maintenance database** | your Keycloak database name |
| **Username** | your Keycloak database user |
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
| **Maintenance database** | your Keycloak database name |
| **Username** | your Keycloak database user |
| **Password** | *(retrieve from your Kubernetes secret, see below)* |

To retrieve the database password from your Kubernetes secret:
```bash
kubectl get secret <your-db-secret> -n <namespace> -o jsonpath='{.data.<password-key>}' | base64 -d
```

**Register the server and create the backup:**

1. Open pgAdmin and log in
2. Right-click **Servers** → **Register** → **Server...**
3. In the **General** tab, set a name (e.g., `Keycloak PostgreSQL`)
4. In the **Connection** tab, fill in the values from the table above
5. Click **Save**
6. In the browser tree, expand **Servers → Keycloak PostgreSQL → Databases**
7. Right-click your database → **Backup...**
8. Configure the backup:
   - **Filename**: `keycloak_backup` (pgAdmin will add the extension)
   - **Format**: `Custom` (recommended, supports selective restore) or `Plain` (SQL text)
   - **Encoding**: `UTF8`
9. *(Optional)* In the **Data/Objects** tab:
   - Enable **Include CREATE DATABASE statement** for a full restore option
   - Enable **Use Column Inserts** for maximum compatibility
10. Click **Backup**
11. Verify the backup completed successfully in the pgAdmin notifications panel (bell icon, bottom-right)

### Step 2: Update Chart.yaml

Change the Keycloak dependency:

```yaml
# Before (Bitnami)
dependencies:
  - condition: keycloak.enabled
    name: keycloak
    repository: oci://registry-1.docker.io/bitnamicharts  # or https://raw.githubusercontent.com/bitnami/charts/...
    version: 23.0.0

# After (CloudPirates)
dependencies:
  - condition: keycloak.enabled
    name: keycloak
    repository: oci://registry-1.docker.io/cloudpirates
    version: 0.13.6
```

### Step 3: Update values.yaml

Migrate your configuration using the [Configuration Mapping](#configuration-mapping) below.

**Key changes to make:**

```yaml
keycloak:
  # Image (CloudPirates uses official image)
  image:
    tag: "26.5.2"
  
  # Admin config - now nested under keycloak.*
  keycloak:
    adminUser: admin
    adminPassword: "your-password"
    proxyHeaders: "xforwarded"  # Was: proxy: edge
    production: false
    httpRelativePath: /auth  # NO trailing slash!
  
  # Database - different structure
  database:
    type: postgres
    host: "postgresql-service"
    name: keycloak
    existingSecret: "keycloak-db-secret"  # Must have db-username, db-password keys
  
  postgres:
    enabled: false  # Use external PostgreSQL
```

### Step 4: Create Database Secret

CloudPirates requires a secret with specific keys:

```bash
kubectl create secret generic keycloak-db-secret -n <namespace> \
  --from-literal=db-username="keycloak_user" \
  --from-literal=db-password="your_db_password"
```

Or create a template:
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: keycloak-db-secret
type: Opaque
stringData:
  db-username: "keycloak_user"
  db-password: "your_db_password"
```

### Step 5: Update Init Containers and Volume Mounts

Update paths from `/opt/bitnami/keycloak/` to `/opt/keycloak/`:

```yaml
# Before (Bitnami)
initContainers:
  - name: import
    volumeMounts:
      - name: themes
        mountPath: /opt/bitnami/keycloak/themes/custom

# After (CloudPirates)
extraInitContainers:
  - name: import-themes
    volumeMounts:
      - name: themes
        mountPath: /opt/keycloak/themes/custom
```

### Step 6: Uninstall Current Deployment

```bash
# Uninstall current Keycloak
helm uninstall <release-name> -n <namespace>

# Wait for pods to terminate
kubectl wait --for=delete pod/<release-name>-keycloak-0 -n <namespace> --timeout=120s

# Optional: Delete PVCs if you want a clean start
# kubectl delete pvc data-<release-name>-keycloak-0 -n <namespace>
```

### Step 7: Deploy CloudPirates Keycloak

```bash
# Update dependencies
helm dependency update

# Install new version
helm install <release-name> . -n <namespace>

# Wait for Keycloak to be ready
kubectl wait --for=condition=ready pod/<release-name>-keycloak-0 -n <namespace> --timeout=600s
```

### Step 8: Verify Migration

```bash
# Check pod status
kubectl get pods -n <namespace>

# Port forward and test
kubectl port-forward svc/<release-name>-keycloak 8080:80 -n <namespace> &

# Verify Keycloak is responding
curl -s http://localhost:8080/auth/realms/master | jq .realm

# Access admin console
echo "Admin Console: http://localhost:8080/auth/admin"
```

### Step 9: Import Realm (if needed)

If realm was not auto-imported, use the backup from Step 1:

```bash
# Via Admin Console:
# 1. Go to http://localhost:8080/auth/admin
# 2. Create realm → Import → Select your backup JSON

# Or via keycloak-config-cli:
kubectl run realm-import --rm -i --restart=Never \
  --image=adorsys/keycloak-config-cli:latest-26 \
  -- java -jar /app/keycloak-config-cli.jar \
  --keycloak.url=http://<release-name>-keycloak \
  --keycloak.user=admin \
  --keycloak.password=<admin-password> \
  --import.files.locations=/realm.json
```

---

## Configuration Mapping

### Admin Configuration

| Bitnami | CloudPirates | Notes |
|---------|--------------|-------|
| `keycloak.auth.adminUser` | `keycloak.keycloak.adminUser` | Nested under `keycloak` |
| `keycloak.auth.adminPassword` | `keycloak.keycloak.adminPassword` | |
| `keycloak.auth.existingSecret` | `keycloak.keycloak.existingSecret` | |

### Server Configuration

| Bitnami | CloudPirates | Notes |
|---------|--------------|-------|
| `keycloak.proxy` | `keycloak.keycloak.proxyHeaders` | Values: `xforwarded`, `forwarded` |
| `keycloak.production` | `keycloak.keycloak.production` | |
| `keycloak.httpRelativePath` | `keycloak.keycloak.httpRelativePath` | **No trailing slash!** Use `/auth` not `/auth/` |

### Database Configuration

| Bitnami | CloudPirates | Notes |
|---------|--------------|-------|
| `keycloak.postgresql.enabled` | `keycloak.postgres.enabled` | Subchart name changed |
| `keycloak.externalDatabase.host` | `keycloak.database.host` | |
| `keycloak.externalDatabase.port` | `keycloak.database.port` | |
| `keycloak.externalDatabase.database` | `keycloak.database.name` | |
| `keycloak.externalDatabase.existingSecret` | `keycloak.database.existingSecret` | Different key names! |

### Database Secret Keys

**Critical Change**: CloudPirates expects different secret keys:

| Bitnami Keys | CloudPirates Keys |
|--------------|-------------------|
| `password` or custom | `db-password` |
| `username` or custom | `db-username` |

### Ingress Configuration

| Bitnami | CloudPirates | Notes |
|---------|--------------|-------|
| `keycloak.ingress.hostname` | `keycloak.ingress.hosts[].host` | Array format |
| `keycloak.ingress.tls: true` | `keycloak.ingress.tls: []` | Array of TLS configs |

**Bitnami format:**
```yaml
keycloak:
  ingress:
    enabled: true
    hostname: keycloak.example.com
    tls: true
```

**CloudPirates format:**
```yaml
keycloak:
  ingress:
    enabled: true
    hosts:
      - host: keycloak.example.com
        paths:
          - path: /
            pathType: Prefix
    tls:
      - secretName: keycloak-tls
        hosts:
          - keycloak.example.com
```

### Init Containers & Volumes

| Bitnami | CloudPirates |
|---------|--------------|
| `keycloak.initContainers` | `keycloak.extraInitContainers` |
| `keycloak.extraVolumes` | `keycloak.extraVolumes` |
| `keycloak.extraVolumeMounts` | `keycloak.extraVolumeMounts` |
| `/opt/bitnami/keycloak/...` | `/opt/keycloak/...` |

---

## Common Issues & Solutions

### 1. Pod Shows 0/1 Ready - Readiness Probe Fails

**Symptom**: Pod status shows `0/1` but Keycloak appears to be running

**Cause**: Double slash in readiness probe path (`/auth//realms/master`)

**Solution**: Use `httpRelativePath` without trailing slash:
```yaml
keycloak:
  keycloak:
    httpRelativePath: /auth  # NOT /auth/
```

### 2. Database Authentication Failed

**Symptom**: `password authentication failed for user`

**Cause**: CloudPirates expects secret keys `db-username` and `db-password`

**Solution**: Create secret with correct keys:
```yaml
stringData:
  db-username: "keycloak_user"
  db-password: "keycloak_password"
```

### 3. Database Connection Refused

**Symptom**: `Connection to localhost:5432 refused`

**Cause**: `database.host` not configured

**Solution**: Set the correct database host:
```yaml
keycloak:
  database:
    host: "your-postgresql-service"
    port: 5432
    name: keycloak
```

### 4. Theme Not Loading

**Symptom**: Login page shows default Keycloak theme

**Cause**: Theme path changed

**Solution**: Update volume mounts:
```yaml
keycloak:
  extraVolumeMounts:
    - name: themes
      mountPath: /opt/keycloak/themes/custom-theme  # NOT /opt/bitnami/...
```

### 5. Proxy/SSL Issues

**Symptom**: Redirect loops or SSL errors

**Solution**: Update proxy configuration:
```yaml
# Bitnami
keycloak:
  proxy: edge

# CloudPirates
keycloak:
  keycloak:
    proxyHeaders: "xforwarded"
```

---

## Migration Checklist

- [ ] Backup existing realm data (export from Admin Console)
- [ ] Backup database using pgAdmin (or equivalent tool)
- [ ] Update `Chart.yaml` dependency
- [ ] Update `values.yaml` configuration structure
- [ ] Create/update database secret with `db-username`/`db-password` keys
- [ ] Update volume mount paths (`/opt/bitnami/` → `/opt/keycloak/`)
- [ ] Update ingress configuration to array format
- [ ] Remove trailing slash from `httpRelativePath`
- [ ] Run `helm dependency update`
- [ ] Deploy and verify
- [ ] Test all authentication flows

---

## Quick Reference: values.yaml Template

```yaml
keycloak:
  enabled: true
  
  image:
    tag: "26.5.2"
  
  replicaCount: 1
  
  keycloak:
    adminUser: admin
    adminPassword: "your-admin-password"
    # existingSecret: "keycloak-admin-secret"
    proxyHeaders: "xforwarded"
    production: false
    httpRelativePath: /auth  # No trailing slash!
  
  database:
    type: postgres
    host: "postgresql-service"
    port: 5432
    name: keycloak
    existingSecret: "keycloak-db-secret"
  
  postgres:
    enabled: false  # Use external PostgreSQL
  
  ingress:
    enabled: true
    hosts:
      - host: keycloak.example.com
        paths:
          - path: /
            pathType: Prefix
    tls:
      - secretName: keycloak-tls
        hosts:
          - keycloak.example.com
  
  service:
    httpPort: 80
  
  extraVolumes:
    - name: themes
      emptyDir: {}
  
  extraVolumeMounts:
    - name: themes
      mountPath: /opt/keycloak/themes/custom
  
  extraInitContainers:
    - name: import-themes
      image: your-themes-image:tag
      command: ["sh", "-c", "cp -R /themes/* /dest"]
      volumeMounts:
        - name: themes
          mountPath: /dest
```

---

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
