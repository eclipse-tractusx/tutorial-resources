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

output "connector1-aeskey" {
  value = module.alice-connector.aes_key
}

output "connector1-client-secret" {
  value = module.alice-connector.client_secret
}

output "connector2-aeskey" {
  value = module.bob-connector.aes_key
}

output "connector2-client-secret" {
  value = module.bob-connector.client_secret
}

output "postgres-url" {
  value = "jdbc:postgresql://${local.pg-host}/"
}

output "keycloak-cluster-ip" {
  value = kubernetes_service.keycloak.spec.0.cluster_ip
}


output "keycloak-database-credentials" {
  value = {
    user     = var.keycloak-db-user
    password = nonsensitive(local.kc-pg-pwd)
    database = var.keycloak-database
  }
}

output "miw-cluster-ip" {
  value = local.miw-ip
}
output "miw-database-credentials" {

  value = {
    user     = var.miw-db-user
    password = nonsensitive(local.miw-pg-pwd)
    database = var.miw-database
  }
}

output "bob-urls" {
  value = module.bob-connector.urls
}

output "alice-urls" {
  value = module.alice-connector.urls
}

output "bob-node-ip" {
  value = module.bob-connector.node-ip
}

output "alice-node-ip" {
  value = module.alice-connector.node-ip
}
