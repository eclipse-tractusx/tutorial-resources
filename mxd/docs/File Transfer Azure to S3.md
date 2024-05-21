# File Transfer: Azure Blob Storage to Amazon S3

## 1. Description
This tutorial illustrates the process through which a provider participant can transfer a file stored in its Azure Blob Storage to Amazon S3 belonging to a consumer participant.  
For this tutorial, we assume `Alice` as a provider participant and `Bob` as consumer participant.
- We use a self-contained version of Amazon S3 named [MinIO](https://github.com/minio/minio), and we deploy two instances, one each for `Alice` and `Bob`.
- We also use a self-contained version of Azure Blob Storage named [Azurite](https://learn.microsoft.com/en-us/azure/storage/common/storage-use-azurite?tabs=docker-hub), and we deploy single `Azurite` instance for both connectors, due to `a limitation in overriding endpoints during trasfer process`.

## 2. Prerequisites
- [File Transfer: Azure Blob Storage to Azure Blob Storage](./File Transfer Azure to Azure.md)
- [File Transfer: Amazon S3 to Amazon S3](./File Transfer S3 to S3.md)

This tutorial is similar to existing tutorial [File Transfer: Azure Blob Storage to Azure Blob Storage](./File Transfer Azure to Azure.md).
Only difference is in the initiate transfer process. So please follow existing tutorial to initiate negotiation.

## 3. Initiate Transfer
While initiating transfer, we need to provide a data destination, so in this case we will be providing an Amazon S3 destination.
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
        "type": "AmazonS3",
		"keyName": "alice-test-document.txt",
		"bucketName": "bob-bucket",
		"region": "us-east-1",
		"endpointOverride": "http://bob-minio:9000",
		"accessKeyId": "bobawsclient",
		"secretAccessKey": "bobawssecret"
  }
}'
```

This API should return below response.
```json
{
  "@type": "IdResponse",
  "@id": "b227dc51-8108-44cb-bc62-dea7bea56482",
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
Please replace transfer process id before running command.
```shell
curl --location 'http://localhost/bob/management/v2/transferprocesses/<TRANSFER_PROCESS_ID>' \
--header 'X-Api-Key: password'
```

It should return below response:
```json
{
  "@id": "b227dc51-8108-44cb-bc62-dea7bea56482",
  "@type": "TransferProcess",
  "correlationId": "b227dc51-8108-44cb-bc62-dea7bea56482",
  "state": "COMPLETED",
  "stateTimestamp": 1704173831187,
  "type": "CONSUMER",
  "assetId": "30",
  "contractId": "e0b386a4-c46f-4d38-bbe0-6db2dfccf0e2",
  "callbackAddresses": [],
  "dataDestination": {
    "@type": "DataAddress",
    "secretAccessKey": "bobawssecret",
    "accessKeyId": "bobawsclient",
    "endpointOverride": "http://bob-minio:9000",
    "region": "us-east-1",
    "type": "AmazonS3",
    "keyName": "alice-test-document.txt",
    "bucketName": "bob-bucket"
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
> Please note, `state` should be `COMPLETED`. If it is `TERMINATED`, it means, transfer has failed. Please check logs for more details.

Once transfer has completed, the asset file should be available in `bob-bucket`.
