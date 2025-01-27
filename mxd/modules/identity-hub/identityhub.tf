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

resource "kubernetes_deployment" "identityhub" {
  metadata {
    name = lower(var.humanReadableName)
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
          image             = var.image
          name              = "tx-identityhub"

          env_from {
            config_map_ref {
              name = kubernetes_config_map.identityhub-config.metadata[0].name
            }
          }
          port {
            container_port = var.ports.presentation-api
            name           = "pres-port"
          }

          port {
            container_port = var.ports.ih-debug
            name           = "debug"
          }
          port {
            container_port = var.ports.ih-identity-api
            name           = "identity"
          }
          port {
            container_port = var.ports.ih-did
            name           = "did"
          }
          port {
            container_port = var.ports.web
            name           = "default-port"
          }
          port {
            container_port = var.ports.ih-sts
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


resource "kubernetes_config_map" "identityhub-config" {
  metadata {
    name      = "${lower(var.humanReadableName)}-ih-config"
    namespace = var.namespace
  }

  data = {
    # IdentityHub variables
    EDC_IH_IAM_ID                   = var.participantId
    EDC_IAM_DID_WEB_USE_HTTPS       = false
    EDC_IH_IAM_PUBLICKEY_ALIAS      = local.public-key-alias
    EDC_IH_API_SUPERUSER_KEY        = var.ih_superuser_apikey
    WEB_HTTP_PORT                   = var.ports.web
    WEB_HTTP_PATH                   = "/api"
    WEB_HTTP_IDENTITY_PORT          = var.ports.ih-identity-api
    WEB_HTTP_IDENTITY_PATH          = "/api/identity"
    WEB_HTTP_IDENTITY_AUTH_KEY      = "password"
    WEB_HTTP_PRESENTATION_PORT      = var.ports.presentation-api
    WEB_HTTP_PRESENTATION_PATH      = "/api/presentation"
    WEB_HTTP_STS_PORT               = var.ports.ih-sts
    WEB_HTTP_STS_PATH               = "/api/credentials"
    WEB_HTTP_DID_PORT               = var.ports.ih-did
    WEB_HTTP_DID_PATH               = "/"
    JAVA_TOOL_OPTIONS               = "${var.useSVE ? "-XX:UseSVE=0 " : ""}-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=${var.ports.ih-debug}"
    EDC_IAM_STS_PRIVATEKEY_ALIAS    = var.aliases.sts-private-key
    EDC_IAM_STS_PUBLICKEY_ID        = var.aliases.sts-public-key-id
    EDC_MVD_CREDENTIALS_PATH        = "/etc/credentials/"
    EDC_VAULT_HASHICORP_URL         = var.vault-url
    EDC_VAULT_HASHICORP_TOKEN       = var.vault-token
    EDC_DATASOURCE_DEFAULT_URL      = var.database.url
    EDC_DATASOURCE_DEFAULT_USER     = var.database.user
    EDC_DATASOURCE_DEFAULT_PASSWORD = var.database.password
    EDC_SQL_SCHEMA_AUTOCREATE       = true
    EDC_IAM_DID_WEB_USE_HTTPS       = "false"
    EDC_STS_ACCOUNT_API_URL         = var.sts_accounts_url
    EDC_STS_ACCOUNTS_API_AUTH_HEADER_VALUE = var.sts_accounts_api_key
    // only relevant when the STS is not embedded
    WEB_HTTP_STS-ACCOUNTS_KEY       = "password"
  }
}

locals {
  public-key-alias = "${var.humanReadableName}-publickey"
}