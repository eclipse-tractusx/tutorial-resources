# Migration Guide: Bitnami Keycloak → CloudPirates Keycloak

**Version**: Keycloak 25.x (Bitnami) → Keycloak 26.x (CloudPirates)

> ⚠️ **Important**: This migration requires downtime. Schedule a maintenance window.

## Overview

This guide covers migrating from the Bitnami Keycloak Helm chart to the CloudPirates Keycloak Helm chart. CloudPirates provides a lighter, more maintainable chart using the official Keycloak image.

### Key Differences

| Aspect | Bitnami | CloudPirates |
|--------|---------|--------------|
| Image | `bitnami/keycloak` (custom) | `keycloak/keycloak` (official) |
| Chart version | 23.x | 0.13.x |
| Keycloak version | 25.x | 26.x |
| Configuration style | Flat structure | Nested under `keycloak.*` |
| Themes path | `/opt/bitnami/keycloak/themes/` | `/opt/keycloak/themes/` |

---

## Chart.yaml Changes

Update your Keycloak dependency:

```yaml
# Before (Bitnami)
dependencies:
  - condition: keycloak.enabled
    name: keycloak
    repository: oci://registry-1.docker.io/bitnamicharts
    version: 23.0.0

# After (CloudPirates)
dependencies:
  - condition: keycloak.enabled
    name: keycloak
    repository: oci://registry-1.docker.io/cloudpirates
    version: 0.13.6
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
| `keycloak.externalDatabase.user` | Via `existingSecret` | See below |
| `keycloak.externalDatabase.password` | Via `existingSecret` | See below |
| `keycloak.externalDatabase.existingSecret` | `keycloak.database.existingSecret` | Different key names |

### Database Secret Format

**Critical Change**: CloudPirates expects different secret keys:

| Bitnami Keys | CloudPirates Keys |
|--------------|-------------------|
| `password` or custom | `db-password` |
| `username` or custom | `db-username` |

Create a secret with the correct format:
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: keycloak-db-secret
type: Opaque
stringData:
  db-username: "your_db_user"
  db-password: "your_db_password"
```

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

**Path changes for volume mounts:**
- Bitnami: `/opt/bitnami/keycloak/...`
- CloudPirates: `/opt/keycloak/...`

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

The chart concatenates `httpRelativePath + /realms/master`:
- `/auth` → `/auth/realms/master` ✅
- `/auth/` → `/auth//realms/master` ❌

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

## Migration Steps

### Step 1: Backup Current Installation

```bash
# Get the Keycloak admin password
KEYCLOAK_PASSWORD=$(kubectl get secret <release-name>-keycloak -n <namespace> \
  -o jsonpath="{.data.admin-password}" | base64 --decode)

# Port forward to Keycloak
kubectl port-forward svc/<release-name>-keycloak 8080:80 -n <namespace> &

# Access Admin Console and export realm:
# 1. Navigate to http://localhost:8080/auth/admin
# 2. Login with admin credentials
# 3. Go to Realm Settings → Action → Partial Export
# 4. Select all options and export to JSON

# If using dedicated PostgreSQL, backup the database:
kubectl exec <postgresql-pod> -n <namespace> -- \
  pg_dump -U keycloak -d keycloak > keycloak_backup.sql
```

### Step 2: Update Chart.yaml

Change the Keycloak dependency:

```yaml
# Before (Bitnami)
dependencies:
  - condition: keycloak.enabled
    name: keycloak
    repository: oci://registry-1.docker.io/bitnamicharts
    version: 23.0.0

# After (CloudPirates)
dependencies:
  - condition: keycloak.enabled
    name: keycloak
    repository: oci://registry-1.docker.io/cloudpirates
    version: 0.13.6
```

### Step 3: Update values.yaml

Migrate configuration using the mapping tables above. Key changes:

```yaml
keycloak:
  # Image (CloudPirates uses official image)
  image:
    tag: "26.5.2"
  
  # Admin config - nested under keycloak.*
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

CloudPirates requires specific secret keys:

```bash
kubectl create secret generic keycloak-db-secret -n <namespace> \
  --from-literal=db-username="keycloak_user" \
  --from-literal=db-password="your_db_password"
```

Or via template:
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

### Step 5: Update Volume Mounts (if using custom themes)

```yaml
# Before (Bitnami)
extraVolumeMounts:
  - name: themes
    mountPath: /opt/bitnami/keycloak/themes/custom

# After (CloudPirates)
extraVolumeMounts:
  - name: themes
    mountPath: /opt/keycloak/themes/custom
```

### Step 6: Uninstall Current Deployment

```bash
# Uninstall current Keycloak
helm uninstall <release-name> -n <namespace>

# Optional: Delete PVCs if you want a clean start
kubectl delete pvc data-<release-name>-keycloak-0 -n <namespace>

# Wait for pods to terminate
kubectl wait --for=delete pod/<release-name>-keycloak-0 -n <namespace> --timeout=120s
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

# Check Keycloak version
kubectl logs <release-name>-keycloak-0 -n <namespace> | head -20

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
  --env="KEYCLOAK_URL=http://<release-name>-keycloak" \
  --env="KEYCLOAK_USER=admin" \
  --env="KEYCLOAK_PASSWORD=<admin-password>" \
  -- java -jar /app/keycloak-config-cli.jar --import.files.locations=/realm.json
```

---

## Migration Checklist

- [ ] Backup existing realm data (export from Admin Console)
- [ ] Backup database (if applicable)
- [ ] Update `Chart.yaml` dependency
- [ ] Update `values.yaml` configuration structure
- [ ] Create/update database secret with `db-username`/`db-password` keys
- [ ] Update volume mount paths (`/opt/bitnami/` → `/opt/keycloak/`)
- [ ] Update ingress configuration to array format
- [ ] Remove trailing slash from `httpRelativePath`
- [ ] Run `helm dependency update`
- [ ] Deploy and verify

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
  
  extraVolumes:
    - name: themes
      emptyDir: {}
  
  extraVolumeMounts:
    - name: themes
      mountPath: /opt/keycloak/themes/custom
```

---

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2025 Contributors to the Eclipse Foundation
