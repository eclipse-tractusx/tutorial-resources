terraform {
  required_providers {
    helm = {
      source = "hashicorp/helm"
    }
    // for generating passwords, clientsecrets etc.
    random = {
      source = "hashicorp/random"
    }

    keycloak = {
      source  = "mrparkers/keycloak"
      version = "4.3.1"
    }
    kubernetes = {
      source = "hashicorp/kubernetes"
    }
  }
}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

# First connector
module "alice-connector" {
  source        = "./modules/connector"
  participantId = "alice"
  database-host = local.pg-ip
  database-name = "alice"
  database-credentials = {
    user     = "postgres"
    password = "postgres"
  }
  ssi-config = {
    miw-url            = "http://${kubernetes_service.miw.spec.0.cluster_ip}:${var.miw-api-port}"
    miw-authorityId    = "BPNL000000000000"
    oauth-tokenUrl     = "http://${kubernetes_service.keycloak.spec.0.cluster_ip}:${var.miw-api-port}"
    oauth-clientid     = "miw_private_client"
    oauth-secretalias  = "client_secret_alias"
    oauth-clientsecret = "miw_private_client"
  }
}

# Second connector
module "bob-connector" {
  source        = "./modules/connector"
  participantId = "bob"
  database-host = local.pg-ip
  database-name = "bob"
  database-credentials = {
    user     = "postgres"
    password = "postgres"
  }
  ssi-config = {
    miw-url            = "http://${kubernetes_service.miw.spec.0.cluster_ip}:${var.miw-api-port}"
    miw-authorityId    = "BPNL000000000000"
    oauth-tokenUrl     = "http://${kubernetes_service.keycloak.spec.0.cluster_ip}:${var.miw-api-port}"
    oauth-clientid     = "miw_private_client2"
    oauth-secretalias  = "client_secret_alias"
    oauth-clientsecret = "miw_private_client"
  }
}
