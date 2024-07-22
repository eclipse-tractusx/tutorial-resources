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
          image             = "backend-service:1.0.0"
          image_pull_policy = "Never"

          port {
            container_port = 8080
            name           = "backend-port"
          }
          env {
            name  = "edc.datasource.backendservice.url"
            value = "jdbc:postgresql://${local.backendservice-postgres.database-url}/${local.databases.backendservice.database-name}"
          }

          env {
            name  = "edc.datasource.backendservice.user"
            value = local.databases.backendservice.database-username
          }
          env {
            name  = "edc.datasource.backendservice.password"
            value = local.databases.backendservice.database-password
          }
          env {
            name  = "web.http.port"
            value = 8080
          }
          env {
            name  = "JAVA_TOOL_OPTIONS"
            value = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044"
          }
          readiness_probe {
            http_get {
              path = "/api/check/readiness"
              port = 8080
            }
            initial_delay_seconds = 30
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 10
            success_threshold     = 1
          }
          liveness_probe {
            http_get {
              path = "/api/check/liveness"
              port = 8080
            }
            initial_delay_seconds = 30
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 10
            success_threshold     = 1
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
      App = "backend-service"
    }
    port {
      port = 8080
      name = "backend-port"
    }
  }
}

