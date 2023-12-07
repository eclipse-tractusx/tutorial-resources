#!/bin/bash

# id="$(uuidgen)"
id="bobs-data"
bdsBaseUrl="http://localhost/bobs-bds"
clusterInternalBdsBaseUrl="http://bobs-bds-bds"

curl -i -X POST "${bdsBaseUrl}/data/${id}" -H "Content-Type: application/json" --data-raw '{
    "diameter": 380,
    "length": 810,
    "width": 590,
    "weight": 85,
    "height": 610
}'

echo "Successfully stored data in backend-data-service (bds):"
echo " ID: ${id}"
echo " URL for EDC Asset: ${clusterInternalBdsBaseUrl}/data/${id}"
