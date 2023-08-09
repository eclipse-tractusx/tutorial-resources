# This job's purpose is to supply some very basic master data to both connectors.
# It will each create two assets, a policy and two contract definitions.
# To achieve that, the Job mounts a Postman collection as ConfigMap, and runs it using newman

resource "kubernetes_job" "seed_connectors_via_mgmt_api" {
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
            "--env-var",
            "MANAGEMENT_URL=http://${module.bob-connector.node-ip}:8081/management/v2",
            "--env-var", "POLICY_BPN=BPNALICE",
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
            "--env-var",
            "MANAGEMENT_URL=http://${module.alice-connector.node-ip}:8081/management/v2",
            "--env-var", "POLICY_BPN=BPNBOB",
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

