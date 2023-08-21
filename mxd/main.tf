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
  source            = "./modules/connector"
  humanReadableName = "alice"
  participantId     = var.aliceBPN
  database-host     = local.pg-ip
  database-name     = "alice"
  database-credentials = {
    user     = "postgres"
    password = "postgres"
  }
  ssi-config = {
    miw-url            = "http://${local.alice-miw-ip}:${var.miw-api-port}"
    miw-authorityId    = var.aliceBPN
    oauth-tokenUrl     = "http://${kubernetes_service.keycloak.spec.0.cluster_ip}:${var.keycloak-port}/realms/miw_test/protocol/openid-connect/token"
    oauth-clientid     = "miw_private_client"
    oauth-secretalias  = "client_secret_alias"
    oauth-clientsecret = "miw_private_client"
  }
}

# Second connector
module "bob-connector" {
  source            = "./modules/connector"
  humanReadableName = "bob"
  participantId     = var.bobBPN
  database-host     = local.pg-ip
  database-name     = "bob"
  database-credentials = {
    user     = "postgres"
    password = "postgres"
  }
  ssi-config = {
    miw-url            = "http://${local.bob-miw-ip}:${var.miw-api-port}"
    miw-authorityId    = var.bobBPN
    oauth-tokenUrl     = "http://${kubernetes_service.keycloak.spec.0.cluster_ip}:${var.keycloak-port}/realms/miw_test/protocol/openid-connect/token"
    oauth-clientid     = "miw_private_client_bob"
    oauth-secretalias  = "client_secret_alias_bob"
    oauth-clientsecret = "miw_private_client_bob"
  }
}
