#
#  Copyright (c) 2024 Metaform Systems, Inc.
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  SPDX-License-Identifier: Apache-2.0
#
#  Contributors:
#       Metaform Systems, Inc. - initial API and implementation
#

resource "kubernetes_deployment" "sts" {
  metadata {
    name      = lower(var.humanReadableName)
    namespace = var.namespace
    labels = {
      App = lower(var.humanReadableName)
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        App = lower(var.humanReadableName)
      }
    }

    template {
      metadata {
        labels = {
          App = lower(var.humanReadableName)
        }
      }

      spec {
        container {
          image_pull_policy = "Never"
          image             = "tx-sts:latest"
          name              = "tx-sts"

          env_from {
            config_map_ref {
              name = kubernetes_config_map.sts-config.metadata[0].name
            }
          }
          port {
            container_port = var.ports.accounts
            name           = "accounts-port"
          }

          port {
            container_port = var.ports.debug
            name           = "debug"
          }
          port {
            container_port = var.ports.web
            name           = "default-port"
          }
          port {
            container_port = var.ports.sts
            name           = "sts-port"
          }

          liveness_probe {
            http_get {
              port = var.ports.web
              path = "/api/check/liveness"
            }
            failure_threshold = 10
            period_seconds    = 5
            timeout_seconds   = 30
          }

          readiness_probe {
            http_get {
              port = var.ports.web
              path = "/api/check/readiness"
            }
            failure_threshold = 10
            period_seconds    = 5
            timeout_seconds   = 30
          }

          startup_probe {
            http_get {
              port = var.ports.web
              path = "/api/check/startup"
            }
            failure_threshold = 10
            period_seconds    = 5
            timeout_seconds   = 30
          }
        }
      }
    }
  }
}

resource "kubernetes_config_map" "sts-config" {
  metadata {
    name      = "${lower(var.humanReadableName)}-config"
    namespace = var.namespace
  }

  data = {
    # STS variables
    WEB_HTTP_STS-ACCOUNTS_KEY       = var.accounts-api-key
    WEB_HTTP_ACCOUNTS_PORT          = var.ports.accounts
    WEB_HTTP_ACCOUNTS_PATH          = var.paths.accounts
    WEB_HTTP_STS_PORT               = var.ports.sts
    WEB_HTTP_STS_PATH               = var.paths.sts
    WEB_HTTP_PORT                   = var.ports.web
    WEB_HTTP_PATH                   = var.paths.web
    EDC_DATASOURCE_DEFAULT_URL      = var.database.url
    EDC_DATASOURCE_DEFAULT_USER     = var.database.user
    EDC_DATASOURCE_DEFAULT_PASSWORD = var.database.password
    EDC_SQL_SCHEMA_AUTOCREATE       = true
    EDC_VAULT_HASHICORP_URL         = var.vault-url
    EDC_VAULT_HASHICORP_TOKEN       = var.vault-token
    JAVA_TOOL_OPTIONS               = "${var.useSVE ? "-XX:UseSVE=0 " : ""}-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044"
  }
}