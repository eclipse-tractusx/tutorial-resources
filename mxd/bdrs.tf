#################################################################################
#
#  Copyright (c) 2024 SAP SE
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  SPDX-License-Identifier: Apache-2.0
#
#  Contributors:
#       SAP SE - initial API and implementation
#
#################################################################################

resource "helm_release" "bdrs-server" {
  name              = "bdrs-server"
  force_update      = true
  dependency_update = true
  reuse_values      = true
  cleanup_on_fail   = true
  replace           = true

  repository = "https://eclipse-tractusx.github.io/charts/dev"
  chart      = "bdrs-server"
  version    = "0.0.4"

  values = [
    yamlencode({
      server : {
        trustedIssuers : ["did:web:miw:${var.miw-bpn}"]
        env : {
          EDC_API_AUTH_KEY : "password"
          EDC_DATASOURCE_DIDENTRY_USER : local.databases.bdrs.database-username
          EDC_DATASOURCE_DIDENTRY_PASSWORD : local.databases.bdrs.database-password
        }
      }
      postgresql : {
        jdbcUrl : "jdbc:postgresql://${local.bdrs-postgres.database-url}/${local.databases.bdrs.database-name}"
      }
    })
  ]

  set {
    name  = "install.postgresql"
    value = false
  }
}

locals {
  bdrs-service        = "bdrs-server"
  bdrs-mgmt-port      = 8081
  bdrs-directory-port = 8082
  bdrs-mgmt-url       = "http://${local.bdrs-service}:${local.bdrs-mgmt-port}/api/management"
  bdrs-directory-url  = "http://${local.bdrs-service}:${local.bdrs-directory-port}/api/directory"
}
