output "connector1-aeskey" {
  value = module.alice-connector.aes_key
}

output "connector1-client-secret" {
  value = module.alice-connector.client_secret
}

output "connector2-aeskey" {
  value = module.bob-connector.aes_key
}

output "connector2-client-secret" {
  value = module.bob-connector.client_secret
}

output "postgres-url" {
  value = "jdbc:postgresql://${local.pg-host}/"
}

output "keycloak-ip" {
  value = kubernetes_service.keycloak.spec.0.cluster_ip
}

output "keycloak-database-credentials" {
  value = {
    user     = var.miw-db-user
    password = nonsensitive(local.kc-pg-pwd)
    database = var.miw-database
  }
}

output "miw-database-pwd" {

  value = {
    user     = var.keycloak-db-user
    password = nonsensitive(local.miw-pg-pwd)
    database = var.keycloak-database
  }
}

output "bob-urls" {
  value = {
    management = "http://localhost/bob/management/v2"
    health     = "http://localhost/bob/health"
  }
}

output "alice-management-url" {
  value = {
    management = "http://localhost/alice/management/v2"
    health     = "http://localhost/alice/health"
  }
}