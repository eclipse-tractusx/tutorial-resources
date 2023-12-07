#  Copyright 2023 cluetec GmbH

#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at

#      http://www.apache.org/licenses/LICENSE-2.0

#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

resource "kubernetes_deployment" "bds" {
  metadata {
    name = "${var.humanReadableName}-bds"
    labels = {
      "app.kubernetes.io/instance" = "${var.humanReadableName}-bds"
      "app.kubernetes.io/name"     = "bds"
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        "app.kubernetes.io/instance" = "${var.humanReadableName}-bds"
        "app.kubernetes.io/name"     = "bds"
      }
    }
    template {
      metadata {
        labels = {
          "app.kubernetes.io/instance" = "${var.humanReadableName}-bds"
          "app.kubernetes.io/name"     = "bds"
        }
      }
      spec {
        container {
          image = var.image
          name  = "bds"
          image_pull_policy = var.image-pull-policy

          port {
            container_port = 8080
            name           = "default"
          }

          # Uncomment this to assign (more) resources
          resources {
            limits = {
              memory = "512Mi"
            }
            requests = {
              memory = "50Mi"
            }
          }
          liveness_probe {
            http_get {
              path = "/actuator/health/liveness"
              port = "default"
            }
            failure_threshold = 10
            period_seconds    = 5
            timeout_seconds   = 30
          }
          readiness_probe {
            http_get {
              path = "/actuator/health/readiness"
              port = "default"
            }
            success_threshold  = 1
            failure_threshold = 10
            period_seconds    = 5
            timeout_seconds   = 30
          }
        }
      }
    }
  }
}

# K8S ClusterIP so Keycloak and MIW can access postgres
resource "kubernetes_service" "bds-service" {
  metadata {
    name = "${var.humanReadableName}-bds"
  }
  spec {
    selector = {
      "app.kubernetes.io/instance" = "${var.humanReadableName}-bds"
      "app.kubernetes.io/name"     = "bds"
    }
    port {
      name        = "default"
      port        = "8080"
      target_port = "8080"
    }
  }
}

locals {
  ip      = kubernetes_service.bds-service.spec.0.cluster_ip
  host    = "${local.ip}:8080"
}
