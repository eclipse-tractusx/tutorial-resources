#
#  Copyright (c) 2023 Contributors to the Eclipse Foundation
#
#  See the NOTICE file(s) distributed with this work for additional
#  information regarding copyright ownership.
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#  License for the specific language governing permissions and limitations
#  under the License.
#
#  SPDX-License-Identifier: Apache-2.0
#


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
          image             = "tractusx/mxd-backend-service:0.0.1,latest"
          image_pull_policy = "Always"

          port {
            container_port = 8080
            name           = "backend-port"
          }
          env {
            name  = "database-connection-url"
            value = "jdbc:postgresql://${local.pg-ip}:${var.postgres-port}/backendservicedb"
          }

          env {
            name  = "database-username"
            value = "postgres"
          }
          env {
            name  = "database-password"
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
