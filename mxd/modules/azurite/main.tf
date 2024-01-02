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

resource "kubernetes_deployment" "azurite" {
  metadata {
    name = local.appName
    labels = {
      App = local.appName
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        App = local.appName
      }
    }
    template {
      metadata {
        labels = {
          App = local.appName
        }
      }
      spec {
        container {
          name              = local.appName
          image             = "mcr.microsoft.com/azure-storage/azurite"
          image_pull_policy = "Always"

          port {
            container_port = local.port
            name           = "blob-port"
          }
          env_from {
            config_map_ref {
              name = kubernetes_config_map.azurite-config.metadata[0].name
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_config_map" "azurite-config" {
  metadata {
    name = "${local.appName}-config"
  }
  data = {
    AZURITE_ACCOUNTS = var.azurite-accounts
  }
}

resource "kubernetes_service" "azurite" {
  metadata {
    name = local.appName
  }
  spec {
    selector = {
      App = kubernetes_deployment.azurite.spec.0.template.0.metadata[0].labels.App
    }
    port {
      name = "blob-port"
      port = local.port
    }
  }
}

locals {
  appName = "azurite"
  port    = 10000
}
