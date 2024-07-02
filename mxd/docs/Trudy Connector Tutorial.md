
# Add new participant Trudy

In our current setup, there are two participants named "Alice" and "Bob". Now we will add a new participant named "Trudy".  
Following are the steps needed to accomplish this.

## 1 Create Keycloak Client
Currently, Alice and Bob each have a Keycloak client named alice-private-client and `bob-private-client`. Trudy also needs to be assigned a Keycloak client. For simplicity, a client named `trudy_private_client` has already been created.

If you need to create a client, you can do so in the Keycloak Admin Console, where you can configure roles, service accounts, and other settings for that client. Creating a client using Keycloak APIs is complicated, so this client was created using the Keycloak Admin Console and exported as a [realm file](../keycloak/miw_test_realm.json).

## 2 Create Wallet in MIW
A wallet is needed for Trudy associated with its BPN number (`BPNL000000000003`).
It has been already created along with Alice and Bob's wallet.

If you want to manually create a wallet for trudy, follow the steps below.
#### a. Setup Port Forwarding for MIW and Keycloak
Forward the Keycloak and MIW ports with the following commands, using the pod names from your setup.
```shell
# forward Keycloak port
kubectl port-forward Keycloak-pod-name 8080:8080
# forward MIW port
kubectl port-forward MIW-pod-name 8000:8000
```

#### b. Create Keycloak access token:
Create Keycloak access token with the following curl command.
```shell
curl --location 'http://localhost:8080/realms/miw_test/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=miw_private_client' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'client_secret=miw_private_client' \
--data-urlencode 'scope=openid'
```

It returns `access_token` which will be used for creating wallet.

#### c. Create Trudy Wallet
Create a wallet using the following curl command, replacing `keycloak_access_token` with the token obtained in the previous step.
```shell
curl --location 'http://localhost:8000/api/wallets' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <keycloak_access_token>' \
--data '{
    "name": "Trudy-Wallet",
    "bpn": "BPNL000000000003"
}'
```

## 3 Create a database for Trudy
We need a database for Trudy. A database named `trudy` has already been created on the existing PostgreSQL server.

If you want to manually create the `trudy` database, follow the steps below.

#### a. Setup Port Forwarding for Postgres
Forward the Postgres port with the following command, using the pod name from your setup.
```shell
kubectl port-forward postgres-pod-name 5432:5432
```

#### b. Install PgAdmin Tool and Create Database
Install PgAdmin and connect to the PostgreSQL server at `jdbc:postgresql://localhost:5432/` using the user `postgres` and the password `postgres`. Then, create the `trudy` database with PgAdmin.

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
    "baseUrl": "https://jsonplaceholder.typicode.com/todos"
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
