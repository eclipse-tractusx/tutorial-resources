#
#  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  SPDX-License-Identifier: Apache-2.0
#
#  Contributors:
#      Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
#
#

resource "kubernetes_deployment" "data-service-api" {

  metadata {
    name      = "data-service-api"
    namespace = kubernetes_namespace.mxd-ns.metadata.0.name
    labels = {
      App = "data-service-api"
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        App = "data-service-api"
      }
    }
    template {
      metadata {
        labels = {
          App = "data-service-api"
        }
      }
      spec {
        container {
          name              = "data-service-api"
          image             = "data-service-api:latest"
          image_pull_policy = "Never"

          port {
            container_port = 8080
            name           = "api-port"
          }

          env {
            name  = "web.http.data.port"
            value = 8080
          }
          env {
            name  = "web.http.data.path"
            value = "/"
          }
          env {
            name  = "web.http.port"
            value = 8181
          }
          env {
            name  = "web.http.path"
            value = "/api"
          }
          env {
            name  = "JAVA_TOOL_OPTIONS"
            value = "${var.useSVE ? "-XX:UseSVE=0 " : ""}-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044"
          }
          readiness_probe {
            http_get {
              path = "/api/check/readiness"
              port = 8181
            }
            initial_delay_seconds = 5
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 3
            success_threshold     = 1
          }
          liveness_probe {
            http_get {
              path = "/api/check/liveness"
              port = 8181
            }
            initial_delay_seconds = 5
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 3
            success_threshold     = 1
          }

          startup_probe {
            http_get {
              path = "/api/check/startup"
              port = 8181
            }
            initial_delay_seconds = 5
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 3
            success_threshold     = 1
          }
        }
      }
    }
  }
}


resource "kubernetes_service" "data-service-api" {
  metadata {
    name      = "data-service-api"
    namespace = var.namespace
  }
  spec {
    type = "NodePort"
    selector = {
      App = kubernetes_deployment.data-service-api.spec.0.template.0.metadata[0].labels.App
    }
    port {
      port = 8080
      name = "api-port"
    }
  }
}

resource "kubernetes_ingress_v1" "api-ingress" {
  metadata {
    name      = "${var.humanReadableName}-ingress"
    namespace = var.namespace
    annotations = {
      "nginx.ingress.kubernetes.io/rewrite-target" = "/$2"
      "nginx.ingress.kubernetes.io/use-regex"      = "true"
    }
  }
  spec {
    ingress_class_name = "nginx"
    rule {
      host = var.ingress-host
      http {
        path {
          path = "/data-service(/|$)(.*)"
          backend {
            service {
              name = "data-service-api"
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

variable "ingress-host" {
  description = "Ingress Host"
  default     = "localhost"
}

variable "humanReadableName" {
  type        = string
  description = "Human readable name of the data service, NOT the ID!!. Required."
  default     = "data-service"
}

