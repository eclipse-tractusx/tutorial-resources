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

# Second connector
module "bob-connector" {
  depends_on        = [module.azurite]
  source            = "./modules/connector"
  humanReadableName = var.bob-humanReadableName
  namespace         = kubernetes_namespace.mxd-ns.metadata.0.name
  participantId     = var.bob-bpn
  database-host     = local.bob-postgres.database-host
  database-name     = local.databases.bob.database-name
  database-credentials = {
    user     = local.databases.bob.database-username
    password = local.databases.bob.database-password
  }
  dcp-config = {
    id                     = var.bob-did
    sts_token_url          = "http://${var.bob-identityhub-host}:7084/api/credentials/token"
    sts_client_id          = var.bob-did
    sts_clientsecret_alias = "participant-bob-sts-client-secret"
  }
  dataplane = {
    privatekey-alias = "${var.bob-did}#signing-key-1"
    publickey-alias  = "${var.bob-did}#signing-key-1"
  }

  azure-account-name    = var.bob-azure-account-name
  azure-account-key     = local.bob-azure-key-base64
  azure-account-key-sas = var.bob-azure-key-sas
  azure-url             = module.azurite.azurite-url
  ingress-host          = var.bob-ingress-host
  minio-config = {
    username = module.bob-minio.minio-username
    password = module.bob-minio.minio-password
    url      = module.bob-minio.minio-url
  }
}

module "bob-identityhub" {
  depends_on = [module.bob-connector]
  source     = "./modules/identity-hub"
  database = {
    user     = local.databases.bob.database-username
    password = local.databases.bob.database-password
    url      = "jdbc:postgresql://${local.bob-postgres.database-host}/${local.databases.bob.database-name}"
  }
  humanReadableName = var.bob-identityhub-host
  namespace         = kubernetes_namespace.mxd-ns.metadata.0.name
  participantId     = var.bob-did
  vault-url         = "http://bob-vault:8200"
  url-path          = var.bob-identityhub-host
}

module "bob-minio" {
  source            = "./modules/minio"
  humanReadableName = lower(var.bob-humanReadableName)
  minio-username    = "bobawsclient"
  minio-password    = "bobawssecret"
}

locals {
  bob-azure-key-base64 = base64encode(var.bob-azure-account-key)
}