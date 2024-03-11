#
#  Copyright (c) 2024 Contributors to the Eclipse Foundation
#
#  See the NOTICE file(s) distributed with this work for additional
#  information regarding copyright ownership.
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#  License for the specific language governing permissions and limitations
#  under the License.
#
#  SPDX-License-Identifier: Apache-2.0
#

resource "kubernetes_deployment" "postgres" {
  metadata {
    name = local.app-name
    labels = {
      App = local.app-name
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        App = local.app-name
      }
    }
    template {
      metadata {
        labels = {
          App = local.app-name
        }
      }
      spec {
        container {
          image = local.pg-image
          name  = local.app-name

          env_from {
            config_map_ref {
              name = kubernetes_config_map.postgres-config.metadata[0].name
            }
          }
          port {
            container_port = 5432
            name           = "postgres-port"
          }

          volume_mount {
            mount_path = "/docker-entrypoint-initdb.d/"
            name       = "pg-initdb"
          }

          # Uncomment this to assign (more) resources
          #          resources {
          #            limits = {
          #              cpu    = "2"
          #              memory = "512Mi"
          #            }
          #            requests = {
          #              cpu    = "250m"
          #              memory = "50Mi"
          #            }
          #          }
          liveness_probe {
            exec {
              command = ["pg_isready", "-U", "postgres"]
            }
            failure_threshold = 10
            period_seconds    = 5
            timeout_seconds   = 30
          }
        }
        volume {
          name = "pg-initdb"
          config_map {
            name = kubernetes_config_map.postgres-config.metadata.0.name
          }
        }
      }
    }
  }
}

# ConfigMap that contains SQL statements to initialize the DB, create a "miw" DB, etc.
resource "kubernetes_config_map" "postgres-config" {
  metadata {
    name = "${local.app-name}-initdb-config"
  }

  ## Create databases for keycloak and MIW, create users and assign privileges
  data = {
    POSTGRES_USER     = "postgres"
    POSTGRES_PASSWORD = "postgres"
    "init.sql"        = <<EOT
      CREATE DATABASE ${var.database-name};
      CREATE USER ${var.database-username} WITH ENCRYPTED PASSWORD '${var.database-password}';
      GRANT ALL PRIVILEGES ON DATABASE ${var.database-name} TO ${var.database-username};
      \c ${var.database-name}
      GRANT ALL ON SCHEMA public TO ${var.database-username};

    EOT
  }
}

# K8S ClusterIP so Keycloak and MIW can access postgres
resource "kubernetes_service" "pg-service" {
  metadata {
    name = "${local.app-name}-service"
  }
  spec {
    selector = {
      App = kubernetes_deployment.postgres.spec.0.template.0.metadata[0].labels.App
    }
    port {
      name        = "pg-port"
      port        = var.database-port
      target_port = var.database-port
    }
  }
}

locals {
  app-name = "postgres-${var.database-name}"
  pg-image = "postgres:15.3-alpine3.18"
  db-ip    = kubernetes_service.pg-service.spec.0.cluster_ip
  db-url   = "${local.db-ip}:${var.database-port}"
}
