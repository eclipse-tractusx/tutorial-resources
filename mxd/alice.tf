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

# First connector
module "alice-connector" {
  depends_on        = [module.azurite]
  source            = "./modules/connector"
  humanReadableName = var.alice-humanReadableName
  namespace         = kubernetes_namespace.mxd-ns.metadata.0.name
  participantId     = var.alice-bpn
  database-host     = local.alice-postgres.database-host
  database-name     = local.databases.alice.database-name
  database-credentials = {
    user     = local.databases.alice.database-username
    password = local.databases.alice.database-password
  }
  dcp-config = {
    id                     = var.alice-did
    sts_token_url          = "http://alice-ih:7084/api/sts/token"
    sts_client_id          = var.alice-did
    sts_clientsecret_alias = "${var.alice-did}-sts-client-secret"
  }
  dataplane = {
    privatekey-alias = "${var.alice-did}#signing-key-1"
    publickey-alias  = "${var.alice-did}#signing-key-1"
  }

  azure-account-name    = var.alice-azure-account-name
  azure-account-key     = local.alice-azure-key-base64
  azure-account-key-sas = var.alice-azure-key-sas
  azure-url             = module.azurite.azurite-url

  ingress-host = var.alice-ingress-host

  minio-config = {
    username = module.alice-minio.minio-username
    password = module.alice-minio.minio-password
    url      = module.alice-minio.minio-url
  }
  useSVE = var.useSVE
}

module "alice-identityhub" {
  depends_on = [module.alice-connector] // depends because of the vault
  source     = "./modules/identity-hub"
  database = {
    user     = local.databases.alice.database-username
    password = local.databases.alice.database-password
    url      = "jdbc:postgresql://${local.alice-postgres.database-host}/${local.databases.alice.database-name}"
  }
  humanReadableName = var.alice-identityhub-host
  namespace         = kubernetes_namespace.mxd-ns.metadata.0.name
  participantId     = var.alice-did
  vault-url         = local.vault-url
  url-path          = var.alice-identityhub-host
  useSVE            = var.useSVE
}

# alice's catalog server
module "alice-catalog-server" {
  depends_on = [module.alice-connector]

  source            = "./modules/catalog-server"
  humanReadableName = "alice-catalogserver"
  serviceName       = var.alice-catalogserver-host
  namespace         = kubernetes_namespace.mxd-ns.metadata.0.name
  participantId     = var.alice-bpn
  vault-url         = local.vault-url
  bdrs-url          = "http://bdrs-server:8082/api/directory"
  database = {
    user     = local.databases.alice-catalogserver.database-username
    password = local.databases.alice-catalogserver.database-password
    url      = "jdbc:postgresql://${local.catalogserver-postgres.database-host}/${local.databases.alice-catalogserver.database-name}"
  }
  dcp-config = {
    id                     = var.alice-did
    sts_token_url          = "${module.alice-identityhub.sts-token-url}/token"
    sts_client_id          = var.alice-did
    sts_clientsecret_alias = "${var.alice-did}-sts-client-secret"
  }
  useSVE = var.useSVE
}


module "alice-minio" {
  source            = "./modules/minio"
  humanReadableName = lower(var.alice-humanReadableName)
  minio-username    = "aliceawsclient"
  minio-password    = "aliceawssecret"
}

locals {
  alice-azure-key-base64 = base64encode(var.alice-azure-account-key)
  vault-url              = "http://alice-vault:8200"
}