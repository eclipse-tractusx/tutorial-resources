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

module "azurite" {
  source           = "./modules/azurite"
  namespace        = kubernetes_namespace.mxd-ns.metadata.0.name
  azurite-accounts = "${var.alice-azure-account-name}:${local.alice-azure-key-base64};${var.bob-azure-account-name}:${local.bob-azure-key-base64};${var.trudy-azure-account-name}:${local.trudy-azure-key-base64};"
}

locals {
  trudy-azure-key-base64 = base64encode(var.trudy-azure-account-key)
}

resource "kubernetes_namespace" "mxd-ns" {
  metadata {
    name = var.namespace
  }
}