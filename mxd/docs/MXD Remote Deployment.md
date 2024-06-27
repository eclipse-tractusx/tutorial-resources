# Deploying MXD on Remote Kubernetes Cluster (AWS / GCP)

We should be able to deploy MXD on a Kubernetes cluster deployed on a local machine / computer as per the steps defined in the [MXD README](../README.md#2-basic-dataspace-setup).
But there are certain changes needed if we want to deploy it on a remote Kubernetes cluster.

## Provide Static IP for MIW / Keycloak
MIW and Keycloak both require static IPs.
Different Kubernetes clusters may have different allowed IP ranges.
So, we can pass these IPs as Terraform variable while running `terraform apply`.

```shell
terraform.exe apply -var miw-static-ip="10.96.81.225" -var keycloak-static-ip="10.96.103.90"
```
