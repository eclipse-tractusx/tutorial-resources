# Minimum Tractus-X Dataspace

## 1. Prerequisites

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

## 2. Basic dataspace setup

The "MXD" dataspace initially consists of several components: `Alice` and `Bob` (two Tractus-X EDC connectors),
a `vault` instance each, a Postgres database, a Managed Identity Wallet app, a Keycloak instance. `Alice` and `Bob` will
be our dataspace participants. Each of them stores their secrets in their "private" vault instance, and there is a
shared Postgres server, where each of them has a database. MIW and Keycloak are "central" components, they only exist
once and are accessible by all participants.

For the most bare-bones installation of the dataspace, execute the following commands in a shell:

```shell
kind create cluster -n mxd
# the next step is specific to KinD and will be different for other Kubernetes runtimes!
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
cd <path/of/mxd>
terraform init
terraform apply
# type "yes" and press enter when prompted to do so 
```

Notice that the `kubectl apply` command deploys a Kubernetes Ingress Controller to the cluster and is required to reach
our applications from outside the cluster. Specifically, it deploys an NGINX ingress controller. Notice also, that the
command is *specific to KinD* and will likely not work on other Kubernetes runtimes or with other ingress controllers!

Wait. Then wait some more. It will take a couple of minutes until all services are booted up. If your machine is a
potato, it'll take even longer. Just get a coffee. Eventually, it should look similar to this:

![img.png](assets/img.png)

### Inspect the databases

Please be aware, that all services and applications that were deployed in the previous step, are **not** accessible from
outside the Kubernetes cluster. That means, for example, the Postgres database cannot be reached out-of-the-box.

Naturally there are several ways to enable access to those services (Load balancers, Ingresses, etc.) but for the sake
of simplicity we will use a plain Kubernetes port-forwarding:

```shell
kubectl port-forward postgres-5b788f6bdd-bvt9b 5432:5423
``` 

> Note that the actual pod name will be slightly different in your local cluster.

Then, using PgAdmin, connect to the Postgres server at `jdbc:postgresql://localhost:5432/` using `user=postgres`
and `password=postgres`:

![img_1.png](assets/scr_pgadmin1.png)

Every service in the cluster has their own database, but for the sake of simplicity, they are hosted in one Postgres
server. We will show in [later sections](#8-improving-the-setup), how the databases can be segregated out. Feel free to
inspect all the databases
and tables, but there is not much data in there yet. Particularly the connector databases will be empty. We will fill
them in the subsequent steps.

### Verify your local installation

In order to check that the connectors were deployed successfully, please execute the following commands in a shell:

```shell
curl -X GET http://localhost/bob/health/api/check/liveness
curl -X GET http://localhost/alice/health/api/check/liveness
```

which should return something similar to this, the important part being the `isSystemHealthy: true` bit:

```json
{
  "componentResults": [
    {
      "failure": null,
      "component": "Observability API",
      "isHealthy": true
    },
    {
      "failure": null,
      "component": null,
      "isHealthy": true
    }
  ],
  "isSystemHealthy": true
}
```

## 3. Add some data

In this step we will focus on inserting data into our participant Alice using
the [Management API](https://app.swaggerhub.com/apis/eclipse-edc-bot/management-api/0.1.4-SNAPSHOT). We will use plain
CLI tools (`curl`) for this, but feel free to use graphical tools such as Postman or Insomnia.

### 3.1 Add a basic `asset`, `policy` and `contract-definition`

Now we will add an asset titled "simple-asset", an access policy, a contract policy, and then we'll combine them in a
contract definition. We'll do this only for Alice, but feel free to try this out with Bob as well.
There will not be any restrictions on that asset, meaning, every dataspace member will be able to "see" it.

> TODO: add link to policy generator tool

> TODO: curl commands

### 3.2 Add a restricted `asset`

We will add another asset, but this time we'll put a restriction on it: only participants that have the `Dismantler`
credential will be able to see it. Technically, that means, that the access policy contains a restriction.

> TODO: add link to policy generator tool

> TODO: curl commands

### 3.3 [Optional] Add Kubernetes deployment for a `newman` container

Now that we've done the work manually, we'll learn an easy and convenient way to automatically seed data directly after
the cluster starts up and the connector apps are deployed. For that, we'll use a command line tool called `newman` and
run it in a pod, which executes a Postman collection and tests for the HTTP results.

## 4. Bob gets Alice's catalog

Bob wants to get Alice's data catalog. Since Bob does not have the `Dismantler` credential, he will only be able to see
the "simple-asset" from [step 3.1](#31-add-a-basic-asset-policy-and-contract-definition).

> TODO: curl commands
> TODO: example JSON response

## 5. Bob transfers data from Alice

Now that Bob knows what Alice is offering, he wants to transmit data. Before Bob can actually request data, he needs to
negotiate a contract with Alice. We'll do this using the management API:

> TODO: curl commands, example response, show how IDs correlate

Once Bob and Alice have reached an agreement, Bob can start requesting data from Alice. Keeping in mind that he is only
permitted to see the "simple-asset", so that's what he'll request. The "simple-asset" is actually a REST API that is
hosted in Alice's private network realm. Alice will proxy the access to this API. We call this a "
consumer-pull-transfer"
and it is the most basic transfer available.

> TODO: curl commands, show data response

## 6. Simplify negotiation and transfer using the EDR API

In [step 5](#5-bob-transfers-data-from-alice) we saw how a contract negotiation and a transfer can be executed using the
management API, first the negotiation and then the transfer phase, but there is a simpler way to do this: enter the EDR
API.
Using this convenient tool, we don't have to care about the intricacies of negotiation and transfer anymore, we can
simply request an API token to Alice's proxy, and start sucking data out of it.
We don't even need to worry about token expiry - the EDR API has a little gizmo that automatically refreshes the token
if it nears expiry.

> TODO: repeat negotiation + transfer using EDR API

## 7. Add new participant Trudy

## 8. Improving the setup

Improvements can be made to these aspects:

- separate out databases: deploy one Postgres per connector, plus one Postgres each for MIW and KC
- add ingresses for all the various connector endpoints, MIW and Keycloak