
# Add new participant Trudy

In our current setup, there are two participants named "Alice" and "Bob". Now we will add a new participant named "Trudy".  
Following are the steps needed to accomplish this.

## 1 Create Keycloak Client
Presently, Alice and Bob each have a Keycloak client name `alice-private-client` and `bob-private-client`.  
Trudy should be assigned a Keycloak client as well.
For simplicity, a client named `trudy-private-client` has already been created.

## 2 Create Wallet in MIW
A wallet is needed for Trudy associated with its BPN number (`BPNL000000000003`).
It has been already created along with Alice and Bob's wallet.

## 3 Create a database for Trudy
We need a database for Trudy.
A database named `trudy` has already been created on the existing PostgreSQL server.

## 4 Deploy Trudy Connector
A terraform config has already been defined in [trudy.tfignore](../trudy.tfignore).  
Run the following commands to deploy trudy connector.
```shell
mv ./trudy.tfignore ./trudy.tf # Rename the file
terraform init                 # Let terraform init this new trudy config
terraform apply                # Apply changes for this new trudy config
# type "yes" and press enter when prompted to do so 
```

## 5 Verify Trudy Connector Deployment
If `terraform apply` command in the previous step runs successfully,  execute following command to check connector health.
```shell
curl --fail http://localhost/trudy/health/api/check/readiness
```

## 6 Create Assets / Access Policy / Contract Definition
Let's insert some data into `Trudy` connector.

### 6.1 Create Asset
Create an asset using the following `curl` command:
```shell
curl --location 'http://localhost/trudy/management/v3/assets' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
  "@context": {},
  "@id": "1",
  "properties": {
    "description": "Product EDC Demo Asset 1"
  },
  "dataAddress": {
    "@type": "DataAddress",
    "type": "HttpData",
    "baseUrl": "https://jsonplaceholder.typicode.com/todos/1"
  }
}'
```
This `curl` command can be used to fetch Trudy's assets:
```shell
curl -X POST http://localhost/trudy/management/v3/assets/request -H "x-api-key: password" -H "content-type: application/json"
```

### 6.2 Create Access Policy
Create an access policy using the following `curl` command:
```shell
curl --location 'http://localhost/trudy/management/v2/policydefinitions' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
  "@context": {
    "odrl": "http://www.w3.org/ns/odrl/2/"
  },
  "@type": "PolicyDefinitionRequestDto",
  "@id": "1",
  "policy": {
    "@type": "odrl:Set",
    "odrl:permission": [
      {
        "odrl:action": "USE",
        "odrl:constraint": {
          "@type": "LogicalConstraint",
          "odrl:or": [
            {
              "@type": "Constraint",
              "odrl:leftOperand": "BusinessPartnerNumber",
              "odrl:operator": {
                "@id": "odrl:eq"
              },
              "odrl:rightOperand": "BPNL000000000001"
            }
          ]
        }
      }
    ]
  }
}'
```
Trudy defines an access policy which allows only Alice (with BPN Number `BPNL000000000001`) to view its catalog.

This `curl` command can be used to fetch Trudy's access policies:
```shell
curl -X POST http://localhost/trudy/management/v2/policydefinitions/request -H "x-api-key: password" -H "content-type: application/json"
```

### 6.3 Create Contract Definition
Create a contract definition using the following `curl` command:
```shell
curl --location 'http://localhost/trudy/management/v2/contractdefinitions' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
    "@context": {},
    "@id": "1",
    "@type": "ContractDefinition",
    "accessPolicyId": "1",
    "contractPolicyId": "1",
    "assetsSelector" : {
        "@type" : "CriterionDto",
        "operandLeft": "https://w3id.org/edc/v0.0.1/ns/id",
        "operator": "=",
        "operandRight": "1"
    }
}'
```
This `curl` command can be used to fetch Trudy's contract definitions:
```shell
curl -X POST http://localhost/trudy/management/v2/contractdefinitions/request -H "x-api-key: password" -H "content-type: application/json"
```

### 6.4 Query Trudy's Catalog
Now we have everything in place, we can now demonstrate connector to connector communications.  
Trudy (Provider) wants to exchange data with Alice (Consumer).

Alice executes this `curl` command to request Trudy's catalog:
```shell
curl --location 'http://localhost/alice/management/v2/catalog/request' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
    "@context": {},
    "protocol": "dataspace-protocol-http",
    "counterPartyAddress": "http://trudy-controlplane:8084/api/v1/dsp",
	"counterPartyId": "BPNL000000000003",
	"protocol": "dataspace-protocol-http",
    "querySpec": {
        "offset": 0,
        "limit": 50
    }
}'
```
It should return following response:
```json
{
  "@id": "8c8e058d-57fc-4c45-9f76-03136e4e3368",
  "@type": "dcat:Catalog",
  "dcat:dataset": {
    "@id": "1",
    "@type": "dcat:Dataset",
    "odrl:hasPolicy": {
      "@id": "MQ==:MQ==:NWYxZTU5OGUtMzA4ZS00NDMzLWE4OGUtM2JkZDJkNWZmYzc5",
      "@type": "odrl:Set",
      "odrl:permission": {
        "odrl:target": "1",
        "odrl:action": {
          "odrl:type": "USE"
        },
        "odrl:constraint": {
          "odrl:or": {
            "odrl:leftOperand": "BusinessPartnerNumber",
            "odrl:operator": {
              "@id": "odrl:eq"
            },
            "odrl:rightOperand": "BPNL000000000001"
          }
        }
      },
      "odrl:prohibition": [],
      "odrl:obligation": [],
      "odrl:target": "1"
    },
    "dcat:distribution": [
      {
        "@type": "dcat:Distribution",
        "dct:format": {
          "@id": "HttpProxy"
        },
        "dcat:accessService": "7d1ddc07-6cf8-4823-a966-75a18e99771c"
      },
      {
        "@type": "dcat:Distribution",
        "dct:format": {
          "@id": "AmazonS3"
        },
        "dcat:accessService": "7d1ddc07-6cf8-4823-a966-75a18e99771c"
      }
    ],
    "edc:description": "Product EDC Demo Asset 1",
    "edc:id": "1"
  },
  "dcat:service": {
    "@id": "7d1ddc07-6cf8-4823-a966-75a18e99771c",
    "@type": "dcat:DataService",
    "dct:terms": "connector",
    "dct:endpointUrl": "http://trudy-controlplane:8084/api/v1/dsp"
  },
  "edc:participantId": "BPNL000000000003",
  "@context": {
    "dct": "https://purl.org/dc/terms/",
    "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
    "edc": "https://w3id.org/edc/v0.0.1/ns/",
    "dcat": "https://www.w3.org/ns/dcat/",
    "odrl": "http://www.w3.org/ns/odrl/2/",
    "dspace": "https://w3id.org/dspace/v0.8/"
  }
}
```
Alice can see data sets offered by Trudy.  
Now what happens when Bob tries to request Trudy's catalog. Let's check it out.

Bob executes this `curl` command to request Trudy's catalog:
```shell
curl --location 'http://localhost/bob/management/v2/catalog/request' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
    "@context": {},
    "protocol": "dataspace-protocol-http",
    "counterPartyAddress": "http://trudy-controlplane:8084/api/v1/dsp",
	"counterPartyId": "BPNL000000000003",
	"protocol": "dataspace-protocol-http",
    "querySpec": {
        "offset": 0,
        "limit": 50
    }
}'
```
It should return following response:
```json
{
  "@id": "1bae8946-e44a-44ad-8d62-d70974a0cb89",
  "@type": "dcat:Catalog",
  "dcat:dataset": [],
  "dcat:service": {
    "@id": "7d1ddc07-6cf8-4823-a966-75a18e99771c",
    "@type": "dcat:DataService",
    "dct:terms": "connector",
    "dct:endpointUrl": "http://trudy-controlplane:8084/api/v1/dsp"
  },
  "edc:participantId": "BPNL000000000003",
  "@context": {
    "dct": "https://purl.org/dc/terms/",
    "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
    "edc": "https://w3id.org/edc/v0.0.1/ns/",
    "dcat": "https://www.w3.org/ns/dcat/",
    "odrl": "http://www.w3.org/ns/odrl/2/",
    "dspace": "https://w3id.org/dspace/v0.8/"
  }
}
```
Bob receives an empty data set. But why?  
That's because, Trudy had created the [access policy](#62-create-access-policy) which allowed only Alice to view its catalog.

Can you create another access policy and contract definition which allows Bob to view Trudy's catalog?  
`Hint:` Create an access policy using Bob's BPN Number (`BPNL000000000002`) and use this access policy id while creating contract definition.
