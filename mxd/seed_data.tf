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

# This job's purpose is to supply some very basic master data to both connectors.
# It will each create two assets, a policy and two contract definitions.
# To achieve that, the Job mounts a Postman collection as ConfigMap, and runs it using newman

resource "kubernetes_job" "seed_connectors_via_mgmt_api" {
  // wait until the connectors are running, otherwise terraform may report an error
  depends_on = [module.alice-connector, module.bob-connector]
  metadata {
    name = "seed-connectors"
  }
  spec {
    // run only once
    completions     = 1
    completion_mode = "NonIndexed"
    // clean up any job pods after 90 seconds, failed or succeeded
    ttl_seconds_after_finished = "90"
    template {
      metadata {
        name = "seed-connectors"
      }
      spec {
        // this container seeds data to the BOB connector
        container {
          name  = "newman-bob"
          image = "postman/newman:ubuntu"
          command = [
            "newman", "run",
            "--folder", "SeedData",
            "--env-var", "MANAGEMENT_URL=http://${module.bob-connector.node-ip}:8081/management/v2",
            "--env-var", "POLICY_BPN=${var.alice-bpn}",
            "/opt/collection/${local.newman_collection_name}"
          ]
          volume_mount {
            mount_path = "/opt/collection"
            name       = "seed-collection"
          }
        }
        // this container seeds data to the ALICE connector
        container {
          name  = "newman-alice"
          image = "postman/newman:ubuntu"
          command = [
            "newman", "run",
            "--folder", "SeedData",
            "--env-var", "MANAGEMENT_URL=http://${module.alice-connector.node-ip}:8081/management/v2",
            "--env-var", "POLICY_BPN=${var.bob-bpn}",
            "/opt/collection/${local.newman_collection_name}"
          ]
          volume_mount {
            mount_path = "/opt/collection"
            name       = "seed-collection"
          }
        }

        // this container seeds data to the miw service
        container {
          name  = "newman-miw"
          image = "postman/newman:ubuntu"
          command = [
            "newman", "run",
            "--folder", "SeedMIW",
            "--env-var", "MIW_URL=http://${local.miw-url}",
            "--env-var", "KEYCLOAK_URL=${local.keycloak-url}/realms/${local.keycloak-realm}",
            "--env-var", "MIW_CLIENT_ID=miw_private_client",
            "--env-var", "MIW_CLIENT_SECRET=miw_private_client",
            "--env-var", "ALICE_BPN=${var.alice-bpn}",
            "--env-var", "BOB_BPN=${var.bob-bpn}",
            "--env-var", "TRUDY_BPN=${var.trudy-bpn}",
            "/opt/collection/${local.newman_collection_name}"
          ]
          volume_mount {
            mount_path = "/opt/collection"
            name       = "seed-collection"
          }
        }
        volume {
          name = "seed-collection"
          config_map {
            name = kubernetes_config_map.seed-collection.metadata.0.name
          }
        }
        // only restart when failed
        restart_policy = "OnFailure"
      }
    }
  }
}

resource "kubernetes_config_map" "seed-collection" {
  metadata {
    name = "seed-collection"
  }
  data = {
    (local.newman_collection_name) = file("./postman/mxd-seed.json")
  }
}

locals {
  newman_collection_name = "mxd-seed.json"
}
