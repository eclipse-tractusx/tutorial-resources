# 1 Introduction

With the Minimum Tractus-X Dataspace, IT departments can set up their own little data space to perform a data exchange between two parties (Bob and Alice in our example). The MXD can be used as a sandbox for testing.

For whom is that relevant?
This tutorial is designed for companies that want to perform data exchange in a "real" Catena-X data space infrastructure.

Who should execute the tutorial?
IT-Employees with the following skills and previous experience:

- Replace with skill 1
- Replace with skill 2
- Replace with skill 3

## 1.1 Components & Architecture

By performing this tutorial a data space will be set up including the following components:

- 2 EDC Connectors (Called Bob and Alice)
- 1 Managed Identity Wallet
- 1 Keycloak instance
- 1 Postgres data base

## 1.2 Prerequisites

In order to run the Minimum Tractus-X Dataspace "MXD" on your local machine, please make sure the following
preconditions are met.

- Have a local Kubernetes runtime ready. We've tested this setup with [KinD](https://kind.sigs.k8s.io/), but other
  runtimes such
  as [Minikube](https://minikube.sigs.k8s.io/docs/start/) may work as well, we just haven't tested them. All following
  instructions will assume KinD.
- Install [Terraform](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli).
- a POSIX-compliant shell, e.g. `bash` or `zsh` unless stated otherwise
- basic knowledge about Helm and Kubernetes
- [Optional] a cli tool to easily print logs of a K8S deployment, such as [`stern`](https://github.com/stern/stern)
- [Optional] a graphical tool to inspect your Kubernetes environment, such as [Lens](https://k8slens.dev/).
  Not mandatory of course, but all screenshots in this doc are created off of Lens.
- [Optional] a graphical tool to inspect Postgres databases, such as [PgAdmin](https://www.pgadmin.org/). Screenshots in
  this guide are created off of PgAdmin.
- [Optional] a graphical tool to send REST requests, such as [Postman](https://www.postman.com/). This sample will
  include Postman collections that can be imported.
  