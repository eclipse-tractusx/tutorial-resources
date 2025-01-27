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

resource "kubernetes_deployment" "catalog-server" {
  metadata {
    name      = lower(var.humanReadableName)
    namespace = var.namespace
    labels = {
      App = lower(var.serviceName)
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        App = lower(var.serviceName)
      }
    }

    template {
      metadata {
        labels = {
          App = lower(var.serviceName)
        }
      }

      spec {
        container {
          name              = lower(var.humanReadableName)
          image             = "tx-catalog-server:latest"
          image_pull_policy = "Never"

          env_from {
            config_map_ref {
              name = kubernetes_config_map.catalog-server-config.metadata[0].name
            }
          }

          port {
            container_port = var.ports.web
            name           = "default-port"
          }
          port {
            container_port = var.ports.debug
            name           = "debug-port"
          }

          liveness_probe {
            http_get {
              port = var.ports.web
              path = "/api/check/liveness"
            }
            failure_threshold = 5
            period_seconds    = 5
            timeout_seconds   = 10
          }

          readiness_probe {
            http_get {
              port = var.ports.web
              path = "/api/check/readiness"
            }
            failure_threshold = 5
            period_seconds    = 5
            timeout_seconds   = 10
          }

          startup_probe {
            http_get {
              port = var.ports.web
              path = "/api/check/startup"
            }
            failure_threshold = 5
            period_seconds    = 5
            timeout_seconds   = 10
          }

          volume_mount {
            mount_path = "/etc/registry"
            name       = "registry-volume"
          }
        }

        volume {
          name = "registry-volume"
          config_map {
            name = kubernetes_config_map.catalog-server-config.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_config_map" "catalog-server-config" {
  metadata {
    name      = "${lower(var.serviceName)}-config"
    namespace = var.namespace
  }

  data = {
    EDC_IAM_ISSUER_ID               = var.dcp-config.id
    EDC_IAM_DID_WEB_USE_HTTPS       = false
    WEB_HTTP_PORT                   = var.ports.web
    WEB_HTTP_PATH                   = "/api"
    WEB_HTTP_PROTOCOL_PORT          = var.ports.protocol
    WEB_HTTP_PROTOCOL_PATH          = "/api/dsp"
    WEB_HTTP_MANAGEMENT_PORT        = var.ports.management
    WEB_HTTP_MANAGEMENT_PATH        = "/api/management"
    WEB_HTTP_CATALOG_PORT           = var.ports.catalog
    WEB_HTTP_CATALOG_PATH           = "/api/catalog"
    EDC_DSP_CALLBACK_ADDRESS        = "http://${var.serviceName}:${var.ports.protocol}/api/dsp"
    EDC_IAM_STS_PRIVATEKEY_ALIAS    = "${var.participantId}#${var.aliases.sts-private-key}"
    EDC_IAM_STS_PUBLICKEY_ID        = "${var.participantId}#${var.aliases.sts-public-key-id}"
    JAVA_TOOL_OPTIONS               = "${var.useSVE ? "-XX:UseSVE=0 " : ""}-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=${var.ports.debug}"
    EDC_IH_AUDIENCE_REGISTRY_PATH   = "/etc/registry/registry.json"
    EDC_PARTICIPANT_ID              = var.participantId
    EDC_VAULT_HASHICORP_URL         = var.vault-url
    EDC_VAULT_HASHICORP_TOKEN       = var.vault-token
    TX_IAM_IATP_BDRS_SERVER_URL     = var.bdrs-url
    EDC_MVD_PARTICIPANTS_LIST_FILE  = "/etc/participants/participants.json"
    EDC_DATASOURCE_DEFAULT_URL      = var.database.url
    EDC_DATASOURCE_DEFAULT_USER     = var.database.user
    EDC_DATASOURCE_DEFAULT_PASSWORD = var.database.password
    EDC_SQL_SCHEMA_AUTOCREATE       = true
    EDC_COMPONENT_ID                = var.serviceName

    # remote STS configuration
    EDC_IAM_STS_OAUTH_TOKEN_URL           = var.dcp-config.sts_token_url
    EDC_IAM_STS_OAUTH_CLIENT_ID           = var.dcp-config.sts_client_id
    EDC_IAM_STS_OAUTH_CLIENT_SECRET_ALIAS = var.dcp-config.sts_clientsecret_alias

    EDC_IAM_DID_WEB_USE_HTTPS                              = "false"
    EDC_IAM_TRUSTED-ISSUER_DATASPACE-ISSUER_ID             = "did:web:dataspace-issuer"
    EDC_IAM_TRUSTED-ISSUER_DATASPACE-ISSUER_SUPPORTEDTYPES = "[\"*\"]"
  }
}
