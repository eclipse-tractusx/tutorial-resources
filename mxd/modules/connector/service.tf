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

# technically, the Helm chart for the connector already brings a Kubernetes service, but there is no
# (easy) way to get a hold of it from terraform, so lets just declare another one

resource "kubernetes_service" "controlplane-service" {
  metadata {
    name      = "${var.humanReadableName}-controlplane"
    namespace = var.namespace
  }
  spec {
    type = "NodePort"
    selector = {
      "app.kubernetes.io/instance" = "${var.humanReadableName}-controlplane"
      "app.kubernetes.io/name"     = "tractusx-connector-controlplane"
    }
    port {
      name = "management"
      port = 8081
    }
    port {
      name = "health"
      port = 8080
    }
    port {
      name = "protocol"
      port = 8084
    }
    port {
      name = "debug"
      port = 1044
    }
    port {
      name = "catalog"
      port = 8085
    }
  }
}