#!/bin/bash

edcManagementBaseUrl="http://localhost/bob/management"
edcApiKey="password"

# Asset
# assetId="$(uuidgen)"
assetId="0bc6a8af-8682-4dba-86b1-0433f9762e42"
clusterInternalBdsBaseUrl="http://bobs-bds-bds:8080"
bdsDataId="bobs-data"
assetUrl="${clusterInternalBdsBaseUrl}/data/${bdsDataId}"

# Policy
# policyId="$(uuidgen)"
policyId="1bc6a8af-8682-4dba-86b1-0433f9762e42"

# Contract Definition
# contractDefinitionId="$(uuidgen)"
contractDefinitionId="2bc6a8af-8682-4dba-86b1-0433f9762e42"

curl -i -X POST "${edcManagementBaseUrl}/v3/assets" -H "X-Api-Key: ${edcApiKey}" -H "Content-Type: application/json" --data-raw "{
    \"@context\": {},
    \"@id\": \"${assetId}\",
    \"properties\": {
        \"description\": \"Product EDC Demo Asset\"
    },
    \"dataAddress\": {
        \"@type\": \"DataAddress\",
        \"baseUrl\": \"${assetUrl}\",
        \"type\": \"HttpData\"
    }
}" | jq

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
}" | jq

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
}" | jq
