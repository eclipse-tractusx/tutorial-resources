#
#  Copyright (c) 2024 Contributors to the Eclipse Foundation
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

resource "helm_release" "bdrs-server" {
  name              = "bdrs-server"
  force_update      = true
  dependency_update = true
  reuse_values      = true
  cleanup_on_fail   = true
  replace           = true

  repository = "https://eclipse-tractusx.github.io/charts/dev"
  chart      = "bdrs-server"
  version    = "0.0.2"

  values = [
    yamlencode({
      postgresql : {
        auth : {
          password : "postgres"
        }
        jdbcUrl : "jdbc:postgresql://${local.pg-host}/bdrs"
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
  bdrs-port           = 8080
  bdrs-mgmt-port      = 8081
  bdrs-directory-port = 8082
  bdrs-directory-url  = "http://${local.bdrs-service}:${local.bdrs-port}/api"
  bdrs-mgmt-url       = "http://${local.bdrs-service}:${local.bdrs-mgmt-port}/api/management"
}
