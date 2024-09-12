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
variable "postgres-port" {
  default = 5432
}

variable "namespace" {
  type        = string
  description = "Kubernetes namespace to use"
  default     = "mxd"
}

variable "trudy-bpn" {
  default = "BPNL000000000003"
}


variable "trudy-did" {
  default = "did:web:trudy-ih%3A7083:trudy"
}

variable "trudy-azure-account-name" {
  default = "trudyazureaccount"
}

variable "trudy-azure-account-key" {
  default = "trudyazurekey"
}

variable "trudy-azure-key-sas" {
  default = "st=2023-11-23T13%3A18%3A49Z&se=2030-01-01T13%3A18%3A49Z&sp=rwdlacupft&sv=2022-11-02&ss=qftb&srt=sco&sig=lOo5x2U04isnhBdlAZLj2nFk%2BphhiVeuzjv/XGJu3DM%3D"
}

variable "trudy-ingress-host" {
  default = "localhost"
}
