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

resource "null_resource" "kind_load" {
  provisioner "local-exec" {
    command = "kind load docker-image backend-service:1.0.0 --name mxd"
  }
}

resource "kubernetes_deployment" "backend-service" {
  depends_on = [null_resource.kind_load]
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
          image             = "backend-service:1.0.0"
          image_pull_policy = "Never"

          port {
            container_port = 8080
            name           = "backend-port"
          }
          env {
            name  = "edc.datasource.backendservice.url"
            value = "jdbc:postgresql://${local.pg-ip}:${var.postgres-port}/backendservicedb"
          }

          env {
            name  = "edc.datasource.backendservice.user"
            value = "postgres"
          }
          env {
            name  = "edc.datasource.backendservice.password"
            value = "postgres"
          }
          env {
            name  = "web.http.port"
            value = 8080
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

