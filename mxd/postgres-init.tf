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

# Separate Postgres Instance for each components
module "postgres" {
  depends_on       = [kubernetes_config_map.postgres-initdb-config]
  source           = "./modules/postgres"
  for_each         = tomap(local.databases)
  instance-name    = each.key
  database-port    = var.postgres-port
  init-sql-configs = ["${each.key}-initdb-config"]
}

resource "kubernetes_config_map" "postgres-initdb-config" {
  for_each = tomap(local.databases)
  metadata {
    name      = "${each.key}-initdb-config"
    namespace = kubernetes_namespace.mxd-ns.metadata.0.name
  }

  data = {

    "${each.key}-initdb-config.sql" = <<-EOT

      CREATE USER ${each.value.database-username} WITH ENCRYPTED PASSWORD '${each.value.database-password}';
      CREATE DATABASE ${each.value.database-name};
      GRANT ALL PRIVILEGES ON DATABASE ${each.value.database-name} TO ${each.value.database-username};
      \c ${each.value.database-name}
      GRANT ALL ON SCHEMA public TO ${each.value.database-username};
    EOT
  }
}

locals {
  bdrs-postgres          = module.postgres["bdrs"]
  alice-postgres         = module.postgres["alice"]
  bob-postgres           = module.postgres["bob"]
  catalogserver-postgres = module.postgres["alice-catalogserver"]

  databases = {
    bdrs = {
      database-name     = "bdrs",
      database-username = "bdrs"
      database-password = "bdrs"
    }

    alice = {
      database-name     = "alice",
      database-username = "alice"
      database-password = "alice"
    }

    bob = {
      database-name     = "bob",
      database-username = "bob"
      database-password = "bob"
    }

    alice-catalogserver = {
      database-name     = "catalogserver"
      database-username = "catalogserver"
      database-password = "catalogserver"
    }
  }
}
