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

## Normally, you shouldn't need to change any values here. If you do, please be sure to also change them in the seed script (seed-k8s.sh).
## Neglecting to do that will render the connectors and identity hubs inoperable!

variable "humanReadableName" {
  type        = string
  description = "Human-readable name of the catalog server, e.g. pod names, deployment names, etc."
}

variable "serviceName" {
  type        = string
  description = "Kubernetes name of the catalog server, as it appears in K8S services, URL paths, etc. NOT the ID!!. Required."
}

variable "participantId" {
  type        = string
  description = "DID:WEB identifier of the participant"
}

variable "namespace" {
  type        = string
  description = "Kubernetes namespace to use"
  default     = "mxd"
}

variable "ports" {
  type = object({
    web        = number
    protocol   = number
    debug      = number
    management = number
    catalog    = number
  })
  default = {
    web        = 8080
    protocol   = 8082
    debug      = 1044
    management = 8081
    catalog    = 8083
  }
}

variable "database" {
  type = object({
    url      = string
    user     = string
    password = string
  })
}

variable "vault-token" {
  default     = "root"
  description = "This is the authentication token for the vault. DO NOT USE THIS IN PRODUCTION!"
  type        = string
}

variable "vault-url" {
  description = "URL of the Hashicorp Vault"
  type        = string
}

variable "bdrs-url" {
  description = "URL of the BDRS Service"
  type        = string
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

variable "aliases" {
  type = object({
    sts-private-key   = string
    sts-public-key-id = string
  })
  default = {
    sts-private-key   = "key-1"
    sts-public-key-id = "key-1"
  }
}

locals {
  name = lower(var.serviceName)
}
