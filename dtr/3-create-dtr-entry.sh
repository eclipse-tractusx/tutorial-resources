#!/bin/bash

dtrBaseUrl="http://localhost/bobs-dtr/api/v3.0"

shellDescriptorId="urn:uuid:$(uuidgen)"
globalAssetId="urn:uuid:$(uuidgen)"
bpn="BPNL00000003B2OM"
submodelId="urn:uuid:$(uuidgen)"

edcAssetId="0bc6a8af-8682-4dba-86b1-0433f9762e42"
# Needs to be the public one
edcDspUrl="http://<HOST>/bob/api/v1/dsp"
# Needs to be the public one
edcPublicDataplaneUrl="http://localhost/bob/public"

curl -X POST "${dtrBaseUrl}/shell-descriptors" -H "Content-Type: application/json" --data-raw "{
    \"id\": \"${shellDescriptorId}\",
    \"idShort\": \"${bpn}_${shellDescriptorId}\",
    \"descriptions\": [],
    \"globalAssetId\": \"${globalAssetId}\",
    \"specificAssetIds\": [],
    \"submodelDescriptors\": [
        {
            \"idShort\": \"PhysicalDimension\",
            \"id\": \"${submodelId}\",
            \"semanticId\": {
                \"type\": \"ExternalReference\",
                \"keys\": [
                    {
                        \"type\": \"GlobalReference\",
                        \"value\": \"urn:bamm:io.catenax.physical_dimension:1.0.0#PhysicalDimension\"
                    }
                ]
            },
            \"endpoints\": [
                {
                    \"interface\": \"SUBMODEL-3.0\",
                    \"protocolInformation\": {
                        \"href\": \"${edcPublicDataplaneUrl}/submodel\",
                        \"endpointProtocol\": \"HTTP\",
                        \"endpointProtocolVersion\": [
                            \"1.1\"
                        ],
                        \"subprotocol\": \"DSP\",
                        \"subprotocolBody\": \"id=${edcAssetId};dspEndpoint=${edcDspUrl}\",
                        \"subprotocolBodyEncoding\": \"plain\",
                        \"securityAttributes\": [
                            {
                                \"type\": \"NONE\",
                                \"key\": \"NONE\",
                                \"value\": \"NONE\"
                            }
                        ]
                    }
                }
            ]
        }
    ]
}" | jq
