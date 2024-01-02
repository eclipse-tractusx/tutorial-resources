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

terraform {
  required_providers {
    helm = {
      source = "hashicorp/helm"
    }
    // for generating passwords, clientsecrets etc.
    random = {
      source = "hashicorp/random"
    }

    keycloak = {
      source  = "mrparkers/keycloak"
      version = "4.3.1"
    }
    kubernetes = {
      source = "hashicorp/kubernetes"
    }
  }
}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

# First connector
module "alice-connector" {
  depends_on        = [module.azurite]
  source            = "./modules/connector"
  humanReadableName = "alice"
  participantId     = var.alice-bpn
  database-host     = local.pg-ip
  database-name     = "alice"
  database-credentials = {
    user     = "postgres"
    password = "postgres"
  }
  ssi-config = {
    miw-url            = "http://${kubernetes_service.miw.metadata.0.name}:${var.miw-api-port}"
    miw-authorityId    = var.miw-bpn
    oauth-tokenUrl     = "http://${kubernetes_service.keycloak.metadata.0.name}:${var.keycloak-port}/realms/miw_test/protocol/openid-connect/token"
    oauth-clientid     = "alice_private_client"
    oauth-secretalias  = "client_secret_alias"
    oauth-clientsecret = "alice_private_client"
  }
  azure-account-name    = var.alice-azure-account-name
  azure-account-key     = local.alice-azure-key-base64
  azure-account-key-sas = var.alice-azure-key-sas
  azure-url             = module.azurite.azurite-url
}

# Second connector
module "bob-connector" {
  depends_on        = [module.azurite]
  source            = "./modules/connector"
  humanReadableName = "bob"
  participantId     = var.bob-bpn
  database-host     = local.pg-ip
  database-name     = "bob"
  database-credentials = {
    user     = "postgres"
    password = "postgres"
  }
  ssi-config = {
    miw-url            = "http://${kubernetes_service.miw.metadata.0.name}:${var.miw-api-port}"
    miw-authorityId    = var.miw-bpn
    oauth-tokenUrl     = "http://${kubernetes_service.keycloak.metadata.0.name}:${var.keycloak-port}/realms/miw_test/protocol/openid-connect/token"
    oauth-clientid     = "bob_private_client"
    oauth-secretalias  = "client_secret_alias"
    oauth-clientsecret = "bob_private_client"
  }
  azure-account-name    = var.bob-azure-account-name
  azure-account-key     = local.bob-azure-key-base64
  azure-account-key-sas = var.bob-azure-key-sas
  azure-url             = module.azurite.azurite-url
}

module "azurite" {
  source           = "./modules/azurite"
  azurite-accounts = "${var.alice-azure-account-name}:${local.alice-azure-key-base64};${var.bob-azure-account-name}:${local.bob-azure-key-base64}"
}

locals {
  alice-azure-key-base64 = base64encode(var.alice-azure-account-key)
  bob-azure-key-base64   = base64encode(var.bob-azure-account-key)
}
