# File Transfer: Amazon S3 to Azure Blob Storage

## 1. Description
This tutorial illustrates the process through which a provider participant can transfer a file stored in its Amazon S3 to Azure Blob Storage belonging to a consumer participant.  
For this tutorial, we assume `Alice` as a provider participant and `Bob` as consumer participant.  
- We use a self-contained version of Amazon S3 named [MinIO](https://github.com/minio/minio), and we deploy two instances, one each for `Alice` and `Bob`.  
- We also use a self-contained version of Azure Blob Storage named [Azurite](https://learn.microsoft.com/en-us/azure/storage/common/storage-use-azurite?tabs=docker-hub), and we deploy single `Azurite` instance for both connectors, due to `a limitation in overriding endpoints during trasfer process`.

## 2. Prerequisites
- [File Transfer: Amazon S3 to Amazon S3](./File Transfer S3 to S3.md)
- [File Transfer: Azure Blob Storage to Azure Blob Storage](./File Transfer Azure to Azure.md)

This tutorial is similar to existing tutorial [File Transfer: Amazon S3 to Amazon S3](./File Transfer S3 to S3.md).
The only difference is in the initiate transfer process. So please follow existing tutorial to initiate negotiation.

## 3. Initiate Transfer
When initiating the transfer, we need to provide a data destination, so in this case we will be providing an Azure Blob Storage destination.
```shell
curl --location 'http://localhost/bob/management/v2/transferprocesses' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
  },
  "@type": "https://w3id.org/edc/v0.0.1/ns/TransferRequest",
  "protocol": "dataspace-protocol-http",
  "counterPartyAddress": "http://alice-controlplane:8084/api/v1/dsp",
  "contractId": "<Contract Agreement Id from Get Negotiation Response>",
  "assetId": "<Asset Id>",
  "transferType": "application/octet-stream",
  "dataDestination":  {
    "type": "AzureStorage",
    "account": "bobazureaccount",
    "container": "bob-container",
    "keyName": "bobazureaccount-sas"
  }
}'
```

This API should return below response.
```json
{
  "@type": "IdResponse",
  "@id": "26e02359-56a1-4d94-afad-61319799675a",
  "createdAt": 1700740670220,
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
    "edc": "https://w3id.org/edc/v0.0.1/ns/",
    "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
    "odrl": "http://www.w3.org/ns/odrl/2/"
  }
}
```
> Please take note of the transfer process id (`@id` field) in the response.

## 4. Get Transfer
Just wait for few seconds and check the transfer state.
Please replace the transfer process id before executing the command.
```shell
curl --location 'http://localhost/bob/management/v2/transferprocesses/<TRANSFER_PROCESS_ID>' \
--header 'X-Api-Key: password'
```

It should return a response similar to this (IDs will be different):
```json
{
  "@id": "26e02359-56a1-4d94-afad-61319799675a",
  "@type": "TransferProcess",
  "correlationId": "26e02359-56a1-4d94-afad-61319799675a",
  "state": "COMPLETED",
  "stateTimestamp": 1702622716915,
  "type": "CONSUMER",
  "assetId": "20",
  "contractId": "cb4089c9-fedb-4da5-afad-beb19526ec89",
  "callbackAddresses": [],
  "dataDestination": {
    "@type": "DataAddress",
    "container": "bob-container",
    "type": "AzureStorage",
    "account": "bobazureaccount",
    "keyName": "bobazureaccount-sas"
  },
  "connectorId": "BPNL000000000001",
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
    "edc": "https://w3id.org/edc/v0.0.1/ns/",
    "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
    "odrl": "http://www.w3.org/ns/odrl/2/"
  }
}
```
> Please note, `state` should be `COMPLETED`. If it is `TERMINATED`, that means the transfer has failed. Please check logs for more details.

Once the transfer has completed, the asset file should be available in `bob-container`.
