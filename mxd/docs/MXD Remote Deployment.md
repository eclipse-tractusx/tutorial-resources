# Deploying MXD on Remote Kubernetes Cluster

We should be able to deploy MXD on a Kubernetes cluster deployed on a local machine / computer as per the steps defined in the [MXD README](../README.md#2-basic-dataspace-setup).
But there are certain changes needed if we want to deploy it on a remote Kubernetes cluster.

## Provide Static IP for MIW / Keycloak
MIW and Keycloak both require static IPs.
Different Kubernetes clusters may have different allowed IP ranges.
So, we can pass these IPs as Terraform variable while running `terraform apply`.
```shell
terraform.exe apply -var miw-static-ip="10.96.81.225" -var keycloak-static-ip="10.96.103.90"
```
> These static IPs are randomly chosen among range of available IPs.

## Provide Ingress Host
MXD uses ingress rules to route requests to different connectors (`Alice` & `Bob`).
Ingress host defaults to `localhost` and hence ingress rules may not work when MXD is deployed on a remote cluster.
So, to override ingress host for `Alice` and `Bob`, we can pass these as terraform variables.
```shell
terraform.exe apply -var alice-ingress-host="alice-edc.ingress.example.com" -var bob-ingress-host="bob-edc.ingress.example.com"
```
> Please check [ingress configurations](../modules/connector/ingress.tf) for more details.
