variable "image-pull-policy" {
  default     = "Always"
  type        = string
  description = "Kubernetes ImagePullPolicy for all images"
}

variable "participantId" {
  type    = string
  default = "Participant ID of the connector. Required."
}

variable "database-host" {
  description = "IP address (ClusterIP) or host name of the postgres database host"

}
variable "database-port" {
  default     = 5432
  description = "Port where the Postgres database is reachable, defaults to 5432."
}

variable "database-name" {
  description = "Name for the Postgres database. Cannot contain special characters"
}

variable "database-credentials" {
  default = {
    user     = "postgres"
    password = "password"
  }
}

variable "ssi-config" {
  default = {
    miw-url            = ""
    miw-authorityId    = ""
    oauth-tokenUrl     = ""
    oauth-clientid     = ""
    oauth-clientsecret = ""
    oauth-secretalias  = ""
  }
}