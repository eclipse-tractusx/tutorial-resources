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

output "aes_key" {
  value = local.aes_key_b64
}

output "client_secret" {
  value = local.client_secret
}
output "database-name" {
  value = var.database-name
}

output "urls" {
  value = {
    management = local.management_url
    health     = local.health_url
    proxy      = local.proxy_url
    public     = local.public_url
  }
}

output "node-ip" {
  value = kubernetes_service.controlplane-service.spec.0.cluster_ip
}
