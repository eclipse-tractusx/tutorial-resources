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
          path = "/bdrs-mgmt(/|$)(.*)"
          backend {
            service {
              name = local.bdrs-service
              port {
                number = local.bdrs-mgmt-port
              }
            }
          }
        }
        path {
          path = "/bdrs-dir(/|$)(.*)"
          backend {
            service {
              name = local.bdrs-service
              port {
                number = local.bdrs-directory-port
              }
            }
          }
        }
      }
    }
  }
}
