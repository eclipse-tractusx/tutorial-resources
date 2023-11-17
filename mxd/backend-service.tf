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
          image             = "backend-service:latest"
          image_pull_policy = "Never"

          port {
            container_port = 9090
            name           = "backend-port"
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
      port = 9090
      name = "backend-port"
    }
  }
}
