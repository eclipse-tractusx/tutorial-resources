#
#  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  SPDX-License-Identifier: Apache-2.0
#
#  Contributors:
#      Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
#
#

variable "alice-bpn" {
  default = "BPNL000000000001"
}

variable "alice-humanReadableName" {
  default = "alice"
}

variable "alice-identityhub-host" {
  description = "Kubernetes service name of the IdentityHub"
  default     = "alice-ih"
}

variable "alice-catalogserver-host" {
  description = "Kubernetes service name (=Host) for Alice's Catalog Server"
  default     = "alice-cs"
}

variable "alice-did" {
  default = "did:web:alice-ih%3A7083:alice"
}

variable "alice-azure-account-name" {
  default = "aliceazureaccount"
}

variable "alice-azure-account-key" {
  default = "aliceazurekey"
}

variable "alice-azure-key-sas" {
  default = "sv=2023-01-03&ss=btqf&srt=sco&st=2023-11-23T13%3A17%3A09Z&se=2030-01-01T00%3A00%3A00Z&sp=rwdxftlacup&sig=uvPcqmLj7%2FMuadAKXMCA7SvWCnClQ9EA1b15OB0m1bc%3D"
}

variable "alice-ingress-host" {
  default = "localhost"
}

variable "alice-namespace" {
  type        = string
  description = "Kubernetes namespace to use"
  default     = "mxd"
}