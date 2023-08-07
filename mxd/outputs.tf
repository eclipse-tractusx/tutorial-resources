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
  value = "jdbc:postgresql://${local.pg-host}/${var.keycloak-database}"
}

output "keycloak-ip" {
  value = kubernetes_service.keycloak.spec.0.cluster_ip
}

output "keycloak-db-pwd" {
  value = nonsensitive(local.kc-pg-pwd)
}

output "miw-db-pwd" {
  value = nonsensitive(local.miw-pg-pwd)
}