# Restricting users from accessing an asset

Bob will once again be the data provider and Alice is interested in Bob’s data assets. Bob, as a data provider, creates an asset. The data asset should have the following properties:

| ID          | 2                                                                                            |
|-------------|----------------------------------------------------------------------------------------------|
| Description | Tractus-X EDC Demo Asset 2                                                                   |
| Type        | HttpData                                                                                     |
| URL         | [https://jsonplaceholder.typicode.com/todos/1](https://jsonplaceholder.typicode.com/todos/2) |

Action (Bob): Create an asset using the following curl command:

```shell
curl -X POST "${BOB_DATAMGMT_URL}/data/assets" \
    --header 'X-Api-Key: password' \
    --header 'Content-Type: application/json' \
    --data '{
             "asset": {
                "properties": {
                        "asset:prop:id": "2",
                        "asset:prop:description": "Tractus-X EDC Demo Asset 2"
                    }
                },
                "dataAddress": {
                    "properties": {
                        "type": "HttpData",
                        "baseUrl": "https://jsonplaceholder.typicode.com/todos/2"
                    }
                }
            }' \
    -s -o /dev/null -w 'Response Code: %{http_code}\n' 
```

Now that the asset is created, a policy must be created to define who can access the asset . This time Bob does not want Alice to see and access the asset. So he defines a policy restricting Alice from accessing his asset.

Action (Bob): Create the access policy using the following curl command:

```shell
{
    "@context": {
        "odrl": "http://www.w3.org/ns/odrl/2/"
    },
    "@type": "PolicyDefinitionRequestDto",
    "@id": "{{POLICY_ID}}",
    "policy": {
        "@type": "Policy",
        "odrl:permission": [
            {
                "odrl:action": "USE",
                "odrl:constraint": {
                    "@type": "LogicalConstraint",
                    "odrl:and": [
                        {
                            "@type": "Constraint",
                            "odrl:leftOperand": "BusinessPartnerNumber",
                            "odrl:operator": {
                                "@id": "odrl:eq"
                            },
                            "odrl:rightOperand": "{{BPN6789}}"
                        }
                    ]
                }
            }
        ]
    }
}  
```

Lastly, the asset and the access policy must be combined in a contract definition.
Action (Bob): Create a contract definition including the asset and the policy you´ve created. For this, use the following curl command:

```shell
curl -X POST "${BOB_DATAMGMT_URL}/data/contractdefinitions" \
    --header 'X-Api-Key: password' \
    --header 'Content-Type: application/json' \
    --data '{
                "id": "1",
                "criteria": [
                    {
                        "operandLeft": "asset:prop:id",
                        "operator": "=",
                        "operandRight": "2"
                    }
                ],
                "accessPolicyId": "1",
            }' \
    -s -o /dev/null -w 'Response Code: %{http_code}\n' 
```

Let´s see if Alice can see the Asset.

Action (Alice): Execute a request using the following curl  command

```shell
curl -G -X GET "${ALICE_DATAMGMT_URL}/data/catalog" \
    --data-urlencode "providerUrl=${BOB_IDS_URL}/api/v1/ids/data" \
    --header 'X-Api-Key: password' \
    --header 'Content-Type: application/json' \
    -s | jq
```

Bob’s asset should not be displayed. The access policy successfully restricts Alice from seeing and obtaining Bob’s asset. Now Bob is able to manage who sees which of his sensitive data assets.

Action: Before you start the next tutorial please delete all data:

```shell
minikube kubectl -- delete pvc -n edc-all-in-one –all
 
minikube kubectl -- delete pv -n edc-all-in-one --all
```
