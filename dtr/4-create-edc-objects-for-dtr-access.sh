#!/bin/bash

edcManagementBaseUrl="http://localhost/bob/management"
edcApiKey="password"

# Asset
# assetId="$(uuidgen)"
assetId="9bc6a8af-8682-4dba-86b1-0433f9762e42"
assetUrl="http://cx-bobs-dtr-registry-svc:8080/api/v3.0"

# Policy
# policyId="$(uuidgen)"
policyId="9cc6a8af-8682-4dba-86b1-0433f9762e42"

# Contract Definition
# contractDefinitionId="$(uuidgen)"
contractDefinitionId="9dc6a8af-8682-4dba-86b1-0433f9762e42"

curl -i -X POST "${edcManagementBaseUrl}/v3/assets" -H "X-Api-Key: ${edcApiKey}" -H "Content-Type: application/json" --data-raw "{
    \"@context\": {
        \"edc\": \"https://w3id.org/edc/v0.0.1/ns/\",
        \"cx-common\": \"https://w3id.org/catenax/ontology/common#\",
        \"cx-taxo\": \"https://w3id.org/catenax/taxonomy#\",
        \"dct\": \"https://purl.org/dc/terms/\"
    },
    \"@id\": \"${assetId}\",
    \"properties\": {
        \"dct:type\": {
            \"@id\": \"cx-taxo:DigitalTwinRegistry\"
        },
        \"cx-common:version\": \"3.0\",
        \"asset:prop:type\": \"data.core.digitalTwinRegistry\"
    },
    \"dataAddress\": {
        \"@type\": \"DataAddress\",
        \"baseUrl\": \"${assetUrl}\",
        \"type\": \"HttpData\",
        \"proxyQueryParams\": \"true\",
        \"proxyPath\": \"true\",
        \"proxyMethod\": \"false\"
    }
}"

curl -i -X POST "${edcManagementBaseUrl}/v2/policydefinitions" -H "X-Api-Key: ${edcApiKey}" -H "Content-Type: application/json" --data-raw "{
    \"@context\": {
        \"odrl\": \"http://www.w3.org/ns/odrl/2/\"
    },
    \"@type\": \"PolicyDefinitionRequestDto\",
    \"@id\": \"${policyId}\",
    \"policy\": {
        \"@type\": \"Policy\",
        \"odrl:permission\": [{
            \"odrl:action\": \"USE\",
            \"odrl:constraint\": {
                \"@type\": \"LogicalConstraint\",
                \"odrl:or\": []
            }
        }]
    }
}"

curl -i -X POST "${edcManagementBaseUrl}/v2/contractdefinitions" -H "X-Api-Key: ${edcApiKey}" -H "Content-Type: application/json" --data-raw "{
    \"@context\": {},
    \"@id\": \"${contractDefinitionId}\",
    \"@type\": \"ContractDefinition\",
    \"accessPolicyId\": \"${policyId}\",
    \"contractPolicyId\": \"${policyId}\",
    \"assetsSelector\" : {
        \"@type\" : \"CriterionDto\",
        \"operandLeft\": \"https://w3id.org/edc/v0.0.1/ns/id\",
        \"operator\": \"=\",
        \"operandRight\": \"${assetId}\"
    }
}"
