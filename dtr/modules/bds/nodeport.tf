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


# technically, the Helm chart for the dtr already brings a Kubernetes service, but there is no way to get a hold of it from terraform, so lets just declare another one
# (easy) way to get a hold of it from terraform, so lets just declare another one

resource "kubernetes_service" "dtr-service" {
  metadata {
    name = "${var.humanReadableName}-dtr"
  }
  spec {
    type = "NodePort"
    selector = {
      "app.kubernetes.io/instance" = "${var.humanReadableName}-bds"
      "app.kubernetes.io/name"     = "bds"
    }
    port {
      name = "default"
      port = 8080
    }
  }
}
