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
  depends_on = [module.alice-connector, module.alice-identityhub, module.bob-connector, module.bob-identityhub]
  metadata {
    name      = "seed-connectors"
    namespace = kubernetes_namespace.mxd-ns.metadata.0.name
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
        // this container seeds data to the ALICE connector
        container {
          name  = "seed-alice-connector"
          image = "postman/newman:ubuntu"
          command = [
            "newman", "run",
            "--folder", "SeedData",
            "--env-var", "MANAGEMENT_URL=http://${module.alice-connector.node-ip}:8081/management",
            "/opt/collection/${local.newman_collection_name}"
          ]
          volume_mount {
            mount_path = "/opt/collection"
            name       = "seed-collection"
          }
        }

        container {
          name  = "seed-alice-catalogserver"
          image = "postman/newman:ubuntu"
          command = [
            "newman", "run",
            "--folder", "SeedCatalogServer",
            "--env-var", "MANAGEMENT_URL=http://${module.alice-catalog-server.management-endpoint}/api/management",
            "--env-var", "PROVIDER_DSP_ENDPOINT=http://alice-controlplane:8084/api/v1/dsp",
            "/opt/collection/${local.newman_collection_name}"
          ]
          volume_mount {
            mount_path = "/opt/collection"
            name       = "seed-collection"
          }
        }

        // seed the BDRS Server
        container {
          name  = "seed-bdrs"
          image = "postman/newman:ubuntu"
          command = [
            "newman", "run",
            "--folder", "SeedBDRS",
            "--env-var", "BDRS_MGMT_URL=${local.bdrs-mgmt-url}",
            "--env-var", "ALICE_DID=${var.alice-did}",
            "--env-var", "BOB_DID=${var.bob-did}",
            "--env-var", "TRUDY_DID=${var.trudy-did}",
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

        // this container seeds ALICE's IdentityHub
        container {
          name  = "membership-cred-alice"
          image = "postman/newman:ubuntu"
          command = [
            "newman", "run",
            "--folder", "SeedIH",
            "--env-var", "IH_URL=http://${var.alice-identityhub-host}:7081",
            "--env-var", "PARTICIPANT_DID=${var.alice-did}",
            "--env-var", "CONTROL_PLANE_HOST=alice-controlplane",
            "--env-var", "PARTICIPANT_CONTEXT_ID=${var.alice-did}",
            "--env-var", "PARTICIPANT_CONTEXT_ID_BASE64=ZGlkOndlYjphbGljZS1paCUzQTcwODM6YWxpY2U=",
            "--env-var",
            "IDENTITYHUB_URL=http://${var.alice-identityhub-host}:${module.alice-identityhub.ports.credentials-api}/api/credentials",
            "--env-var", "MEMBERSHIP_CREDENTIAL=${file("${path.module}/assets/alice.membership.jwt")}",
            "--env-var", "FRAMEWORK_CREDENTIAL=${file("${path.module}/assets/alice.dataexchangegov.jwt")}",
            "--env-var", "BPN=${var.alice-bpn}",
            "/opt/collection/${local.newman_collection_name}"
          ]
          volume_mount {
            mount_path = "/opt/collection"
            name       = "seed-collection"
          }
        }

        // this container seeds BOB's IdentityHub
        container {
          name  = "membership-cred-bob"
          image = "postman/newman:ubuntu"
          command = [
            "newman", "run",
            "--folder", "SeedIH",
            "--env-var", "IH_URL=http://${var.bob-identityhub-host}:7081",
            "--env-var", "PARTICIPANT_DID=${var.bob-did}",
            "--env-var", "CONTROL_PLANE_HOST=bob-controlplane",
            "--env-var", "PARTICIPANT_CONTEXT_ID=${var.bob-did}",
            "--env-var", "PARTICIPANT_CONTEXT_ID_BASE64=ZGlkOndlYjpib2ItaWglM0E3MDgzOmJvYg==",
            "--env-var",
            "IDENTITYHUB_URL=http://${var.bob-identityhub-host}:${module.bob-identityhub.ports.credentials-api}/api/credentials",
            "--env-var", "MEMBERSHIP_CREDENTIAL=${file("${path.module}/assets/bob.membership.jwt")}",
            "--env-var", "FRAMEWORK_CREDENTIAL=${file("${path.module}/assets/bob.dataexchangegov.jwt")}",
            "--env-var", "BPN=${var.bob-bpn}",
            "/opt/collection/${local.newman_collection_name}"
          ]
          volume_mount {
            mount_path = "/opt/collection"
            name       = "seed-collection"
          }
        }

        // seed the dataspace issuer service
        container {
          name  = "dataspace-issuer"
          image = "postman/newman:ubuntu"
          command = [
            "newman", "run",
            "--folder", "Seed Dataspace Issuer",
            "--env-var", "ISSUER_ADMIN_URL=http://${module.dataspace-issuer.endpoints.admin}",
            "--env-var", "ISSUER_CS_URL=http://${module.dataspace-issuer.endpoints.identity}",
            "--env-var", "CONSUMER_ID=${var.alice-did}",
            "--env-var", "CONSUMER_NAME=MXD Participant Alice",
            "--env-var", "PROVIDER_DID=${var.bob-did}",
            "--env-var", "PROVIDER_NAME=MXD Participant Bob",
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
    name      = "seed-collection"
    namespace = kubernetes_namespace.mxd-ns.metadata.0.name
  }
  data = {
    (local.newman_collection_name) = file("./postman/mxd-seed.json")
  }
}

locals {
  newman_collection_name = "mxd-seed.json"
}
