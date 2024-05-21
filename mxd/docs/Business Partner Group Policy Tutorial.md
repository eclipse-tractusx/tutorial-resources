# Business Partner Group Policy

## 1. Description
This tutorial demonstrates the process of creating a business partner group and creating a policy that grants access to assets for this business partner group.  
For this tutorial, we assume `Alice` as a provider participant and `Bob` as consumer participant.  

## 2. Create Business Partner Group
`Alice` adds his partner `Bob` (with BPN: `BPNL000000000002`) to a business partner group named `gold-partners`.
> Note: If you are running MXD, you can skip this step as it has already been done.
```shell
curl --location 'http://localhost/alice/management/business-partner-groups' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
  "@context": {
    "tx": "https://w3id.org/tractusx/v0.0.1/ns/"
  },
  "@id": "BPNL000000000002",
  "tx:groups": [
    "gold-partners"
  ]
}'
```
`Alice` can also update business partner group for `Bob`.
```shell
curl --location --request PUT 'http://localhost/alice/management/business-partner-groups' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
  "@context": {
    "tx": "https://w3id.org/tractusx/v0.0.1/ns/"
  },
  "@id": "BPNL000000000002",
  "tx:groups": [
    "gold-partners",
    "platinum-partners"
  ]
}'
```

`Alice` can check which business partner groups, `Bob` is currently associated.
```shell
curl --location 'http://localhost/alice/management/business-partner-groups/BPNL000000000002' \
--header 'X-Api-Key: password'
```
It should return a response similar to this.
```json
{
  "@id": "BPNL000000000002",
  "tx:groups": [
    "gold-partners",
    "platinum-partners"
  ],
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
    "edc": "https://w3id.org/edc/v0.0.1/ns/",
    "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
    "odrl": "http://www.w3.org/ns/odrl/2/"
  }
}
```

## 3. Create An Asset
```shell
curl --location 'http://localhost/alice/management/v3/assets' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
    "@context": {
        "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
        "edc": "https://w3id.org/edc/v0.0.1/ns/",
        "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
        "odrl": "http://www.w3.org/ns/odrl/2/"
    },
    "@id": "30", 
    "properties": {
        "description": "Product EDC Demo Asset"
    },
    "dataAddress": {
        "@type": "DataAddress",
        "type": "HttpData",
        "baseUrl": "https://jsonplaceholder.typicode.com/todos/30"
    }
}'
```

## 4. Create Policy

### 4.1 Create Access Policy
```shell
curl --location 'http://localhost/alice/management/v2/policydefinitions' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
    "edc": "https://w3id.org/edc/v0.0.1/ns/",
    "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
    "odrl": "http://www.w3.org/ns/odrl/2/"
  },
  "@type": "PolicyDefinitionRequestDto",
  "@id": "301",
  "policy": {
    "@type": "odrl:Set",
    "odrl:permission": [
      {
        "odrl:action": "use",
        "odrl:constraint": {
          "@type": "LogicalConstraint",
          "odrl:or": [
            {
              "@type": "Constraint",
              "odrl:leftOperand": "BpnCredential",
              "odrl:operator": {
                "@id": "odrl:eq"
              },
              "odrl:rightOperand": "active"
            }
          ]
        }
      }
    ]
  }
}'
```
### 4.2 Create Business Partner Group Policy
`Alice` creates a policy for `gold-partners` business partner group. This policy applies to all business partner in that group.
```shell
curl --location 'http://localhost/alice/management/v2/policydefinitions' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
    "edc": "https://w3id.org/edc/v0.0.1/ns/",
    "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
    "odrl": "http://www.w3.org/ns/odrl/2/"
  },
  "@type": "PolicyDefinitionRequestDto",
  "@id": "302",
  "policy": {
    "@type": "odrl:Set",
    "odrl:permission": [
      {
        "odrl:action": "use",
        "odrl:constraint": {
          "@type": "LogicalConstraint",
          "odrl:or": [
            {
              "@type": "Constraint",
              "odrl:leftOperand": "https://w3id.org/tractusx/v0.0.1/ns/BusinessPartnerGroup",
              "odrl:operator": {
                "@id": "odrl:eq"
              },
              "odrl:rightOperand": "gold-partners"
            }
          ]
        }
      }
    ]
  }
}'
```

## 5. Create Contract Definition
```shell
curl --location 'http://localhost/alice/management/v2/contractdefinitions' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
    "edc": "https://w3id.org/edc/v0.0.1/ns/",
    "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
    "odrl": "http://www.w3.org/ns/odrl/2/"
  },
  "@id": "30",
  "@type": "ContractDefinition",
  "accessPolicyId": "301",
  "contractPolicyId": "302",
  "assetsSelector": {
    "@type": "CriterionDto",
    "operandLeft": "https://w3id.org/edc/v0.0.1/ns/id",
    "operator": "=",
    "operandRight": "30"
  }
}'
```

## 6. Query Catalog
Now let's verify whether Bob is able to access the assets or not via querying Alice's catalog.
```shell
curl --location 'http://localhost/bob/management/v2/catalog/request' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
    "edc": "https://w3id.org/edc/v0.0.1/ns/",
    "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
    "odrl": "http://www.w3.org/ns/odrl/2/"
  },
  "@type": "CatalogRequest",
  "counterPartyAddress": "http://alice-controlplane:8084/api/v1/dsp",
  "counterPartyId": "BPNL000000000001",
  "protocol": "dataspace-protocol-http",
  "querySpec": {
    "offset": 0,
    "limit": 50
  }
}'
```

You should be able to find this asset (`id: 30`) in the list of catalog returned by `Alice`.
```json
{
  "@id": "f6dcc216-9948-4807-8e56-6f867f9226f2",
  "@type": "dcat:Catalog",
  "dcat:dataset": [
    {
      "@id": "30",
      "@type": "dcat:Dataset",
      "odrl:hasPolicy": {
        "@id": "MzA=:MzA=:OWNhYzMwM2QtOTYyNi00NzY3LTgxZTUtYWVkNDM4NzAwYmNj",
        "@type": "odrl:Set",
        "odrl:permission": {
          "odrl:target": "30",
          "odrl:action": {
            "odrl:type": "USE"
          },
          "odrl:constraint": {
            "odrl:or": {
              "odrl:leftOperand": "https://w3id.org/tractusx/v0.0.1/ns/BusinessPartnerGroup",
              "odrl:operator": {
                "@id": "odrl:eq"
              },
              "odrl:rightOperand": "gold-partners"
            }
          }
        },
        "odrl:prohibition": [],
        "odrl:obligation": [],
        "odrl:target": {
          "@id": "30"
        }
      },
      "dcat:distribution": [
        {
          "@type": "dcat:Distribution",
          "dct:format": {
            "@id": "HttpProxy-PUSH"
          },
          "dcat:accessService": "b7c41185-0c2f-42cd-b19d-2ef8e2b6b6a6"
        },
        {
          "@type": "dcat:Distribution",
          "dct:format": {
            "@id": "HttpData-PULL"
          },
          "dcat:accessService": "b7c41185-0c2f-42cd-b19d-2ef8e2b6b6a6"
        }
      ],
      "description": "Product EDC Demo Asset",
      "id": "30"
    }
  ],
  "dcat:service": {
    "@id": "b7c41185-0c2f-42cd-b19d-2ef8e2b6b6a6",
    "@type": "dcat:DataService",
    "dct:terms": "connector",
    "dct:endpointUrl": "http://alice-controlplane:8084/api/v1/dsp"
  },
  "participantId": "BPNL000000000001",
  "@context": {}
}
```

## 7. References
- [Business Partner Validation Extension](https://github.com/eclipse-tractusx/tractusx-edc/tree/main/edc-extensions/bpn-validation)
- [Business Partner Group API Swagger Documentation](https://app.swaggerhub.com/apis/eclipse-tractusx-bot/tractusx-edc)
- [EDC Management API Swagger Documentation](https://app.swaggerhub.com/apis/eclipse-edc-bot/management-api)
