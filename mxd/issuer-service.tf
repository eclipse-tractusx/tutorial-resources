#
#  Copyright (c) 2025 Cofinity-X
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  SPDX-License-Identifier: Apache-2.0
#
#  Contributors:
#       Cofinity-X - initial API and implementation
#

module "dataspace-issuer" {
  source            = "./modules/issuerservice"
  humanReadableName = "dataspace-issuer-service"
  participantId     = "did:web:dataspace-issuer" // todo: change
  database = {
    user     = "issuer"
    password = "issuer"
    url      = "jdbc:postgresql://${module.dataspace-issuer-postgres.database-url}/issuer"
  }
  vault-url   = "http://dataspace-issuer-vault:8200"
  vault-token = "root"
  namespace   = kubernetes_namespace.mxd-ns.metadata.0.name
  useSVE      = var.useSVE
}

module "dataspace-issuer-postgres" {
  depends_on       = [kubernetes_config_map.issuer-initdb-config]
  source           = "./modules/postgres"
  instance-name    = "issuer"
  init-sql-configs = ["issuer-initdb-config"]
  namespace        = kubernetes_namespace.mxd-ns.metadata.0.name
}

resource "kubernetes_config_map" "issuer-initdb-config" {
  metadata {
    name      = "issuer-initdb-config"
    namespace = kubernetes_namespace.mxd-ns.metadata.0.name
  }
  data = {
    "issuer-initdb-config.sql" = <<-EOT
        CREATE USER issuer WITH ENCRYPTED PASSWORD 'issuer' SUPERUSER;
        CREATE DATABASE issuer;
        \c issuer issuer
      EOT
  }
}

module "consumer-vault" {
  source            = "./modules/vault"
  humanReadableName = "dataspace-issuer-vault"
  namespace         = kubernetes_namespace.mxd-ns.metadata.0.name
  vault-token       = "root"
}