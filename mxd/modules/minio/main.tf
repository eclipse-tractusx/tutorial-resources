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

resource "kubernetes_deployment" "minio" {
  metadata {
    name = "${var.humanReadableName}-minio"
    labels = {
      app = "${var.humanReadableName}-minio"
    }
  }

  spec {
    selector {
      match_labels = {
        app = "${var.humanReadableName}-minio"
      }
    }
    strategy {
      type = "Recreate"
    }

    template {
      metadata {
        labels = {
          app = "${var.humanReadableName}-minio"
        }
      }

      spec {
        container {
          image = "minio/minio:RELEASE.2022-03-17T06-34-49Z"
          name  = "minio"
          args  = ["server", "/storage", "--console-address=:${var.minio-console-port}"]
          env {
            name  = "MINIO_ROOT_USER"
            value = var.minio-username
          }
          env {
            name  = "MINIO_ROOT_PASSWORD"
            value = var.minio-password
          }
          port {
            name           = "api-port"
            container_port = var.minio-api-port
          }
          port {
            name           = "console-port"
            container_port = var.minio-console-port
          }
        }
      }
    }
  }
}

locals {
  minio-ip   = kubernetes_service.minio-service.metadata.0.name
  minio-port = kubernetes_service.minio-service.spec.0.port.0.port
  minio-url  = "${local.minio-ip}:${local.minio-port}"
}
