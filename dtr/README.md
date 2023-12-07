# dDTR flow

This example is based on the [`mxd`](../mxd) and should be installed/applied on the same cluster.

The Bob EDC will be used as the data provider part and Alice can be used as the consumer.

## Prerequisites

You just need the MXD Kubernetes cluster with the EDCs (and MIW and Keycloak). Instruction can be found in the [mxd documentation](../mxd/README.md)).

## Deploying everything

```shell
terraform init
terraform apply
```

## Data Provisioning

> We prepared some scripts for the data provisioning part (they are laying next to this README.md file and called `1,2,3,4-....sh`). In the following sections we will just describe the general flow. Further information can be also found in the [Digital Twin KIT](https://eclipse-tractusx.github.io/docs-kits/next/kits/Digital%20Twin%20Kit/Software%20Development%20View/Specification%20Digital%20Twin%20KIT).
>
> In the following we try to describe the flow, but don't get confused by the order we are referencing the scripts. Please use the order of the file names when executing them! 😉

As a data provider you need to host/store somewhere your submodels, which you want to share. For this reason we created a service that is called ["backend-data-service" (short `bds`)](#backend-data-service). This service can store any text based data (e.g. JSON, XML, plain text) under a specific ID. This data can be received again, by using the same ID. As the data provider we will use this service to host our data ([`./1-create-data.sh`](./1-create-data.sh)).

The dDTR is a registry in which the consumer can search for the information where and how to get the data he wants. Therefor the provider needs to register his submodels in the DTR ([`./3-create-dtr-entry.sh`](./3-create-dtr-entry.sh)). As we want a sovereign data exchange we can't link directly to the bds. Therefor we need to create an Contract Offer (EDC Asset, Policy, Contract Definition) in the EDC first (see [`./2-create-edc-objects.sh`](./2-create-edc-objects.sh)) to provide the access to our data.

As we are talking about decentralization and sovereign data exchange, we also want to restrict the access to our DTR and will keep it behind the EDC too. This is done [`./4-create-edc-objects-for-dtr-access.sh`](./4-create-edc-objects-for-dtr-access.sh).

## Data Consuming

On the consumer side (Alice EDC) we now need to do two steps:

1. Get the information from the DTR which Asset on which EDC is providing the data
2. Receive the data from this EDC

To keep it simple we simply used the same EDC (bob) for providing the access to the DTR and the data itself. In a real world scenario they could be different EDCs.

For getting the access to the DTR simply follow the consumer flow from the MXD. Only while picking the Contract Offer which you want to use you need to pick the right one and at the end, the call through the DataPlanes is a little different, because you can put `http://.../public` anything (path, query params) you want to interact with the DTR api. But this is again already described in the [Digitial Twin KIT](https://eclipse-tractusx.github.io/docs-kits/next/kits/Digital%20Twin%20Kit/Software%20Development%20View/Specification%20Digital%20Twin%20KIT).

After you have the access to the DTR you need to find a submodel descriptor from which you want to fetch the data. Everything you need then is the edc dps endpoint and asset id. If you take a look at the script `./3-create-dtr-entry.sh` you should be able to identify where this information is been stored in the submodel descriptor.

The second step is then just the normal flow to receive the data from the EDC.

## Backend-Data-Service

Call to store data:

```shell
curl -X GET "http://localhost:80/bobs-bds/data/123" -H "Content-Type: application/json" --data-raw '{
    "diameter": 380,
    "length": 810,
    "width": 590,
    "weight": 85,
    "height": 610
}'
```

Call to store data:

```shell
curl -X GET "http://localhost:80/bobs-bds/data/123"
```

For the moment the source code of this application can be found here: [cluetec/tractusx-community-days-dtr-flow](https://github.com/cluetec/tractusx-community-days-dtr-flow/tree/main/backend-data-service)
