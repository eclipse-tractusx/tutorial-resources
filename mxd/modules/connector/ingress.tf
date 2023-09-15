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

resource "kubernetes_ingress_v1" "mxd-ingress" {
  metadata {
    name = "${var.humanReadableName}-ingress"
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
          path = "/${var.humanReadableName}(/|$)(.*)"
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
          path = "/${var.humanReadableName}/health(/|$)(.*)"
          backend {
            service {
              name = local.control-plane-service
              port {
                number = 8080
              }
            }
          }
        }
        path {
          path = "/${var.humanReadableName}(/|$)(proxy/.*)"
          backend {
            service {
              name = local.data-plane-service
              port {
                number = 8186
              }
            }
          }
        }
        path {
          path = "/${var.humanReadableName}(/|$)(api/public.*)"
          backend {
            service {
              name = local.data-plane-service
              port {
                number = 8081
              }
            }
          }
        }
      }
    }
  }
}

locals {
  control-plane-service = "${var.humanReadableName}-tractusx-connector-controlplane"
  data-plane-service    = "${var.humanReadableName}-tractusx-connector-dataplane"
  management_url        = "http://localhost/${var.humanReadableName}/management/v2"
  proxy_url             = "http://localhost/${var.humanReadableName}/proxy"
  health_url            = "http://localhost/${var.humanReadableName}/health"
  public_url            = "http://localhost/${var.humanReadableName}/api/public"
}
