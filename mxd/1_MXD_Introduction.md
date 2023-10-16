# 1 Introduction

With the Minimum Tractus-X Dataspace (MXD), IT departments can set up their own data space to perform a data exchange between two parties (Bob and Alice in our example). The MXD can be used as a sandbox for testing.

## 1.1 Intended Audience

This tutorial is designed for developers who want to get their hands dirty, for companies that want to perform data exchange in actual Catena-X data space infrastructure and generally for curious minds who want to explore dataspaces.

Required knowledge and skills:

- Beginner level docker and kubernetes
- Beginner level terraform
- Basic linux system commands

## 1.2 Components & Architecture

By performing this tutorial a data space will be set up including the following components:

- 2 Tractus-X EDC Connectors (Called Bob and Alice). The EDC is the key component to exchanging data within data spaces.
- 1 Managed Identity Wallet. The Managed Identity Wallet (MIW) service is a central component storing technical identities. Connectors can request the identity, including properties, in order to take decisions if a contract-offer will be made, a contract agreement will be negotiated, or a data-exchange will be processed.
- 1 Keycloak instance. Keycloak is used for Identiy and Access Management of technical users.
- 1 Postgres data base. A data base building the foundation for the MXD setup.

## 1.3 Prerequisites

In order to run the MXD on your local machine, please make sure the following
preconditions are met.

- Have a local Kubernetes runtime ready. We've tested this setup with [KinD](https://kind.sigs.k8s.io/), but other
  runtimes such as [Minikube](https://minikube.sigs.k8s.io/docs/start/) may work as well, we just haven't tested them. All following instructions will assume KinD.
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
  
## Notice

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2023 sovity GmbH
- SPDX-FileCopyrightText: 2023 SAP SE
- SPDX-FileCopyrightText: 2023 msg systems AG
- Source URL: [https://github.com/eclipse-tractusx/tutorial-resources](https://github.com/eclipse-tractusx/tutorial-resources)
