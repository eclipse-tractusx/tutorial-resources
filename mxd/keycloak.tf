#
#  Copyright (c) 2023 Contributors to the Eclipse Foundation
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

resource "kubernetes_deployment" "keycloak" {
  metadata {
    name = "keycloak"
    labels = {
      App = "miwkeycloak"
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        App = "miwkeycloak"
      }
    }
    template {
      metadata {
        labels = {
          App = "miwkeycloak"
        }
      }
      spec {
        container {
          image = "quay.io/keycloak/keycloak:21.1"
          name  = "keycloak"
          args  = ["start-dev", "--import-realm"]

          port {
            container_port = var.keycloak-port
            name           = "web-ui"
          }
          volume_mount {
            mount_path = "/opt/keycloak/data/import/"
            name       = "miw-test-realm"
          }
          env_from {
            config_map_ref {
              name = kubernetes_config_map.keycloak_env.metadata[0].name
            }
          }
          #           Uncomment this to assign (more) resources
          #          resources {
          #            limits = {
          #              cpu    = "2"
          #              memory = "512Mi"
          #            }
          #            requests = {
          #              cpu    = "500m"
          #              memory = "50Mi"
          #            }
          #          }
          readiness_probe {
            http_get {
              path = "/health/ready"
              port = var.keycloak-port
            }
            initial_delay_seconds = 30
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 10
            success_threshold     = 1
          }
          liveness_probe {
            http_get {
              path = "/health/live"
              port = var.keycloak-port
            }
            initial_delay_seconds = 30
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 10
            success_threshold     = 1
          }
        }
        volume {
          name = "miw-test-realm"
          config_map {
            name = kubernetes_config_map.keycloak-realm.metadata.0.name
          }
        }
      }
    }
  }
}

resource "kubernetes_config_map" "keycloak_env" {
  metadata {
    name = "keycloak-env"
  }
  data = {
    KC_DB                      = "postgres"
    KC_DB_SCHEMA               = "public"
    KC_DB_PASSWORD             = local.kc-pg-pwd
    KC_DB_USERNAME             = var.keycloak-db-user
    KC_DB_URL                  = "jdbc:postgresql://${local.pg-host}/${var.keycloak-database}"
    KEYCLOAK_MIW_PUBLIC_CLIENT = "miw_public"
    KEYCLOAK_ADMIN             = "admin"
    KEYCLOAK_ADMIN_PASSWORD    = "admin"
    # the KC_HOSTNAME must be known in advance, so that Keycloak's token contain valid `iss` claims

    KC_HOSTNAME       = local.keycloak-ip
    KC_HEALTH_ENABLED = true
    MIW_BPN           = var.miw-bpn
    ALICE_BPN         = var.alice-bpn
    BOB_BPN           = var.bob-bpn
    TRUDY_BPN         = var.trudy-bpn
  }
}

# NodePort for external access
resource "kubernetes_service" "keycloak" {
  metadata {
    name = "keycloak"
  }
  spec {
    selector = {
      App = kubernetes_deployment.keycloak.spec.0.template.0.metadata[0].labels.App
    }
    # we need a stable IP, otherwise Keycloak will issue invalid tokens
    cluster_ip = local.keycloak-ip
    port {
      name        = "kc-web-ui-port"
      port        = var.keycloak-port
      target_port = var.keycloak-port
    }
  }
}

locals {
  keycloak-ip  = "10.96.103.80"
  keycloak-url = "${local.keycloak-ip}:${var.keycloak-port}"
}