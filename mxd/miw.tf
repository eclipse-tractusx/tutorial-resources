resource "kubernetes_deployment" "miw" {
  metadata {
    name = "miw"
    labels = {
      App = "miw"
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        App = "miw"
      }
    }
    template {
      metadata {
        labels = {
          App = "miw"
        }
      }
      spec {
        container {
          name              = "miw"
          image             = "ghcr.io/catenax-ng/tx-managed-identity-wallets_miw_service:latest-java-did-web"
          image_pull_policy = "Always"

          port {
            container_port = var.miw-api-port
            name           = "api-port"
          }
          port {
            container_port = 8090
            name           = "mgmt-port"
          }
          env_from {
            config_map_ref {
              name = kubernetes_config_map.miw-config.metadata[0].name
            }
          }
          resources {
            limits = {
              cpu    = "2"
              memory = "512Mi"
            }
            requests = {
              cpu    = "500m"
              memory = "100Mi"
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_config_map" "miw-config" {
  metadata {
    name = "miw-config"
  }
  data = {
    DB_HOST      = local.pg-ip
    DB_PORT      = var.postgres-port
    DB_USER      = "postgres"
    DB_NAME      = var.miw-database
    DB_USER_NAME = var.miw-db-user
    DB_PASSWORD  = local.miw-pg-pwd

    KEYCLOAK_CLIENT_ID              = "miw_private_client"
    ENCRYPTION_KEY                  = "Woh9waid4Ei5eez0aitieghoow9so4oe"
    AUTHORITY_WALLET_BPN            = "BPNL000000000000"
    AUTHORITY_WALLET_DID            = "did:web:miw:BPNL000000000000"
    AUTHORITY_WALLET_NAME           = "Catena-X"
    KEYCLOAK_REALM                  = "miw_test"
    VC_SCHEMA_LINK                  = "https://www.w3.org/2018/credentials/v1, https://catenax-ng.github.io/product-core-schemas/businessPartnerData.json"
    VC_EXPIRY_DATE                  = "01-01-2025"
    SUPPORTED_FRAMEWORK_VC_TYPES    = "cx-behavior-twin=Behavior Twin,cx-pcf=PCF,cx-quality=Quality,cx-resiliency=Resiliency,cx-sustainability=Sustainability,cx-traceability=ID_3.0_Trace"
    MIW_HOST_NAME                   = "localhost:8000"
    ENFORCE_HTTPS_IN_DID_RESOLUTION = false
    AUTH_SERVER_URL                 = "http://${local.keycloak-url}"
    DEV_ENVIRONMENT                 = "docker"
    APPLICATION_PORT                = var.miw-api-port
    MANAGEMENT_PORT                 = 8090
    APPLICATION_ENVIRONMENT         = "dev"
    APP_LOG_LEVEL                   = "DEBUG"
  }
}

resource "kubernetes_service" "miw" {
  metadata {
    name = "miw"
  }
  spec {
    selector = {
      App = kubernetes_deployment.miw.spec.0.template.0.metadata[0].labels.App
    }
    port {
      name        = "miw-app-port"
      port        = var.miw-api-port
      target_port = var.miw-api-port
    }

    port {
      port        = 8090
      name        = "mgmt-port"
      target_port = 8090
    }
  }
}
