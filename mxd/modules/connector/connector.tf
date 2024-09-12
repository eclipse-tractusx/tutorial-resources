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



resource "helm_release" "connector" {
  name              = lower(var.humanReadableName)
  namespace         = var.namespace
  force_update      = true
  dependency_update = true
  reuse_values      = true
  cleanup_on_fail   = true
  replace           = true

  repository = "https://eclipse-tractusx.github.io/charts/dev"
  chart      = "tractusx-connector"
  version    = "0.8.0-rc4"

  values = [
    file("${path.module}/values.yaml"),
    # dynamically set the vault secrets
    yamlencode({
      "vault" : {
        "server" : {
          "postStart" : [
            "sh",
            "-c",
            join(" && ", [
              "sleep 5",
              "/bin/vault kv put secret/edc.aws.access.key content=${var.minio-config.username}",
              "/bin/vault kv put secret/edc.aws.secret.access.key content=${var.minio-config.password}",
              "/bin/vault kv put secret/${var.azure-account-name}-key content=${var.azure-account-key}",
              "/bin/vault kv put secret/${var.azure-account-name}-sas content='${local.azure-sas-token}'",
            ])
          ]
        }
      }
    }),
    yamlencode({
      controlplane : {
        volumeMounts : [
          {
            name : "participants"
            mountPath : "/app/participants.json"
            subPath : "participants.json"
          }
        ]

        volumes : [
          {
            name : "participants"
            configMap : {
              name : kubernetes_config_map.participants-map.metadata[0].name
              items : [
                {
                  key : "participants.json"
                  path : "participants.json"
                }
              ]
            }
          }
        ]
      }
      }
    ),
    yamlencode({
      iatp : {
        id : var.dcp-config.id
        sts : {
          oauth : {
            token_url : var.dcp-config.sts_token_url
            client : {
              id : var.dcp-config.sts_client_id
              secret_alias : var.dcp-config.sts_clientsecret_alias
            }
          }
        }
      }
      controlplane : {
        env : {
          "TX_SSI_ENDPOINT_AUDIENCE" : "http://${kubernetes_service.controlplane-service.metadata.0.name}:8084/api/v1/dsp"
          "EDC_DSP_CALLBACK_ADDRESS" : "http://${kubernetes_service.controlplane-service.metadata.0.name}:8084/api/v1/dsp"
          "EDC_HOSTNAME" : "${var.humanReadableName}-tractusx-connector-controlplane"
          "EDC_BLOBSTORE_ENDPOINT_TEMPLATE" : local.edc-blobstore-endpoint-template
          "EDC_DATAPLANE_SELECTOR_DEFAULTPLANE_SOURCETYPES" : "HttpData,AmazonS3,AzureStorage"
          "EDC_DATAPLANE_SELECTOR_DEFAULTPLANE_DESTINATIONTYPES" : "HttpProxy,AmazonS3,AzureStorage"
          "EDC_IAM_DID_WEB_USE_HTTPS" : "false"
          "EDC_IAM_TRUSTED-ISSUER_DATASPACE-ISSUER_ID" : "did:web:dataspace-issuer"
          "EDC_IAM_TRUSTED-ISSUER_DATASPACE-ISSUER_SUPPORTEDTYPES" : "[\"*\"]"
          "EDC_COMPONENT_ID" : var.humanReadableName
        }
        bdrs : {
          server : {
            url : "http://bdrs-server:8082/api/directory"
          }
        }
        catalog : {
          enabled : true
          crawler : {
            #   num : 1
            period : 10
            initialDelay : 10
            targetsFile : "/app/participants.json"
          }
        }
      }
      dataplane : {
        token : {
          signer : {
            privatekey_alias : var.dataplane.privatekey-alias
          }
          verifier : {
            publickey_alias : var.dataplane.publickey-alias
          }
        }
        env : {
          "EDC_BLOBSTORE_ENDPOINT_TEMPLATE" : local.edc-blobstore-endpoint-template
          "EDC_IAM_DID_WEB_USE_HTTPS" : false
        }
        aws : {
          endpointOverride : "http://${local.minio-url}"
        }
      }
    })
  ]
  set {
    name  = "participant.id"
    value = var.participantId
  }

  set {
    name  = "postgresql.jdbcUrl"
    value = local.jdbcUrl
  }

  set {
    name  = "postgresql.auth.username"
    value = var.database-credentials.user
  }

  set {
    name  = "postgresql.auth.password"
    value = var.database-credentials.password
  }
  // we'll use a central postgres
  set {
    name  = "install.postgresql"
    value = "false"
  }

}

resource "kubernetes_config_map" "participants-map" {
  metadata {
    name      = "${var.humanReadableName}-participants"
    namespace = var.namespace
  }

  data = {
    "participants.json" = file(var.participant-list-file)
  }
}

locals {
  jdbcUrl                         = "jdbc:postgresql://${var.database-host}:${var.database-port}/${var.database-name}"
  edc-blobstore-endpoint-template = "${var.azure-url}/%s"
  azure-sas-token                 = jsonencode({ edctype = "dataspaceconnector:azuretoken", sas = var.azure-account-key-sas })
  minio-url                       = var.minio-config.url
}
