resource "kubernetes_ingress_v1" "mxd-ingress" {
  metadata {
    name = "${var.participantId}-ingress"
    annotations = {
      "nginx.ingress.kubernetes.io/rewrite-target" = "/$2"
      "nginx.ingress.kubernetes.io/use-regex"      = "true"
    }
  }
  spec {
    ingress_class_name = "nginx"
    rule {
      http {
        path {
          path = "/${var.participantId}(/|$)(.*)"
          backend {
            service {
              name = local.control-plane-service
              port {
                number = 8081
              }
            }
          }
        }
        path {
          path = "/${var.participantId}/health(/|$)(.*)"
          backend {
            service {
              name = local.control-plane-service
              port {
                number = 8080
              }
            }
          }
        }
      }
    }
  }
}

locals {
  control-plane-service = "${var.participantId}-tractusx-connector-controlplane"
  management_url        = "http://localhost/${var.participantId}/management/v2"
  health_url            = "http://localhost/${var.participantId}/health"
}
