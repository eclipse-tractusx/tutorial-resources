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

resource "kubernetes_job" "azurite-init" {

  metadata {
    name = local.appName
  }
  spec {
    // run only once
    completions     = 1
    completion_mode = "NonIndexed"
    // clean up any job pods after 90 seconds, failed or succeeded
    ttl_seconds_after_finished = "90"
    template {
      metadata {
        name = local.appName
      }
      spec {
        container {
          name    = local.appName
          image   = "mcr.microsoft.com/azure-cli"
          command = ["sh", "-c"]
          args    = ["${local.cmd-container-create} && ${local.cmd-file-upload}"]
          volume_mount {
            mount_path = local.file-mount-path
            name       = "${local.appName}-document"
          }
        }
        volume {
          name = "${local.appName}-document"
          config_map {
            name = kubernetes_config_map.document.metadata.0.name
          }
        }
        // only restart when failed
        restart_policy = "OnFailure"
      }
    }
  }
}

resource "kubernetes_config_map" "document" {
  metadata {
    name = "${local.appName}-document"
  }
  data = {
    (local.file-name) = file("./assets/${local.file-name}")
  }
}

locals {
  appName              = "${var.humanReadableName}-azurite-init"
  container-name       = "${var.humanReadableName}-container"
  file-name            = "test-document.txt"
  file-mount-path      = "/opt/documents"
  connection-string    = "AccountName=${var.azure-account-name};AccountKey=${var.azure-account-key};DefaultEndpointsProtocol=http;BlobEndpoint=${var.azure-url}/${var.azure-account-name}"
  cmd-container-create = "az storage container create --verbose --debug --name ${local.container-name} --connection-string '${local.connection-string}'"
  cmd-file-upload      = "az storage blob upload --verbose --debug --container-name ${local.container-name} --name ${var.humanReadableName}-${local.file-name} --file ${local.file-mount-path}/${local.file-name} --overwrite --connection-string '${local.connection-string}'"
}
