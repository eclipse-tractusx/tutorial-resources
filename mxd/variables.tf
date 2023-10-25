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

# configuration values for the MIW+Keycloak Postgres db
variable "keycloak-database" {
  default = "keycloak"
}
variable "keycloak-db-user" {
  default = "keycloak_user"
}
variable "miw-database" {
  default = "miw"
}
variable "miw-db-user" {
  default = "miw_user"
}
variable "postgres-port" {
  default = 5432
}

variable "keycloak-port" {
  default = 8080
}

variable "miw-api-port" {
  default = 8000
}

variable "miw-bpn" {
  default = "BPNL000000000000"
}

variable "alice-bpn" {
  default = "BPNL000000000001"
}

variable "bob-bpn" {
  default = "BPNL000000000002"
}

variable "trudy-bpn" {
  default = "BPNL000000000003"
}
