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

variable "namespace" {
  type        = string
  description = "Kubernetes namespace to use"
  default     = "mxd"
}

variable "humanReadableName" {
  type        = string
  description = "Human readable name of the connector, NOT the BPN!!. Required."
}

variable "participantId" {
  type        = string
  description = "Participant ID of the connector. In Catena-X, this MUST be the BPN"
}

variable "database-host" {
  description = "IP address (ClusterIP) or host name of the postgres database host"
}
variable "database-port" {
  default     = 5432
  description = "Port where the Postgres database is reachable, defaults to 5432."
}

variable "database-name" {
  description = "Name for the Postgres database. Cannot contain special characters"
}

variable "database-credentials" {
  default = {
    user     = "postgres"
    password = "password"
  }
}

variable "participant-list-file" {
  type    = string
  default = "./assets/participants.json"
}


variable "dcp-config" {
  type = object({
    id                     = string
    sts_token_url          = string
    sts_client_id          = string
    sts_clientsecret_alias = string
  })
  default = {
    id                     = ""
    sts_token_url          = ""
    sts_client_id          = ""
    sts_clientsecret_alias = ""
  }
}

variable "dataplane" {
  type = object({
    privatekey-alias = string
    publickey-alias  = string
  })
}

variable "azure-account-name" {
  description = "Azure Account Name for the connector"
}

variable "azure-account-key" {
  description = "Azure Account Key for the connector"
}

variable "azure-account-key-sas" {
  description = "A temporary Azure Account Key to let other participant access our azure storage"
}

variable "azure-url" {
  description = "Azure Url"
}

variable "minio-config" {
  type = object({
    username = string
    password = string
    url      = string
  })
}

variable "ingress-host" {
  description = "Ingress Host"
  default     = "localhost"
}

variable "useSVE" {
  type        = bool
  description = "If true, the -XX:UseSVE=0 switch (Scalable Vector Extensions) will be appended to the JAVA_TOOL_OPTIONS. Can help on macOs on Apple Silicon processors"
  default     = false
}

