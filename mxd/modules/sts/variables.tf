#
#  Copyright (c) 2024 Metaform Systems, Inc.
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  SPDX-License-Identifier: Apache-2.0
#
#  Contributors:
#       Metaform Systems, Inc. - initial API and implementation
#


variable "ports" {
  type = object({
    web      = number
    accounts = number
    sts      = number
    debug    = number
  })
  default = {
    web      = 8080
    accounts = 8081
    sts      = 8082
    debug    = 1044
  }
}

variable "paths" {
  type = object({
    web      = string
    accounts = string
    sts      = string
  })
  default = {
    web      = "/api"
    accounts = "/api/sts/accounts"
    sts      = "/api/sts"
  }
}

variable "accounts-api-key" {
  type        = string
  description = "static API key for the STS Accounts API"
}

variable "namespace" {
  type = string
}

variable "humanReadableName" {
  type = string
}

variable "database" {
  type = object({
    url      = string
    user     = string
    password = string
  })
}

variable "vault-url" {
  description = "URL of the Hashicorp Vault"
  type        = string
}

variable "vault-token" {
  default     = "root"
  description = "This is the authentication token for the vault. DO NOT USE THIS IN PRODUCTION!"
  type        = string
}

variable "useSVE" {
  type        = bool
  description = "If true, the -XX:UseSVE=0 switch (Scalable Vector Extensions) will be appended to the JAVA_TOOL_OPTIONS. Can help on macOs on Apple Silicon processors"
  default     = false
}
