resource "kubernetes_ingress_v1" "mxd-ingress" {
  metadata {
    name = "mxd-ingress"
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
          path      = "/miw(/|$)(.*)"
          path_type = "Prefix"
          backend {
            service {
              name = kubernetes_service.miw.metadata.0.name
              port {
                number = var.miw-api-port
              }
            }
          }
        }
        path {
          path = "/kc(/|$)(.*)"
          backend {
            service {
              name = kubernetes_service.keycloak.metadata.0.name
              port {
                number = var.keycloak-port
              }
            }
          }
        }

        path {
          path = "/pg(/|$)(.*)"
          backend {
            service {
              name = kubernetes_service.pg-service.metadata.0.name
              port {
                number = var.postgres-port
              }
            }
          }
        }
      }
    }
  }
}
