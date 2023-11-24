resource "kubernetes_deployment" "backend-service" {
  metadata {
    name = "backend-service"
    labels = {
      App = "backend-service"
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        App = "backend-service"
      }
    }
    template {
      metadata {
        labels = {
          App = "backend-service"
        }
      }
      spec {
        container {
          name              = "backend-service"
          image             = "ravinderkumar02095/backend-service:latest"
          image_pull_policy = "Always"

          port {
            container_port = 8080
            name           = "backend-port"
          }
          env {
            name  = "backend-service-host"
            value = "jdbc:postgresql://${local.pg-ip}:${var.postgres-port}/backendservicedb"
          }

          env {
            name  = "backend-service-username"
            value = "postgres"
          }
          env {
            name  = "backend-service-password"
            value = "postgres"
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "backend-service" {
  metadata {
    name = "backend-service"
  }
  spec {
    selector = {
      app = "backend-service"
    }
    port {
      port = 8080
      name = "backend-port"
    }
  }
}




