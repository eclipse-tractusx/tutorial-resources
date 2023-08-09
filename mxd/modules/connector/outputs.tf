output "aes_key" {
  value = local.aes_key_b64
}

output "client_secret" {
  value = local.client_secret
}
output "database-name" {
  value = var.database-name
}

output "urls" {
  value = {
    management = local.management_url
    health     = local.health_url
  }
}
output "node-ip" {
  value = kubernetes_service.controlplane-service.spec.0.cluster_ip
}