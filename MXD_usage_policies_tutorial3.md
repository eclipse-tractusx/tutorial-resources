# Restricting users from using an asset

Bob will once again be the data provider and Alice is interested in Bob’s data assets. Bob, as a data provider, creates an asset. The data asset should have the following properties:

| ID          | 3                                                                                            |
|-------------|----------------------------------------------------------------------------------------------|
| Description | Tractus-X EDC Demo Asset 3                                                                   |
| Type        | HttpData                                                                                     |
| URL         | [https://jsonplaceholder.typicode.com/todos/1](https://jsonplaceholder.typicode.com/todos/3) |

Action  (Bob): Create this asset using the following curl command:

```shell
curl -X POST "${BOB_DATAMGMT_URL}/data/assets" \
    --header 'X-Api-Key: password' \
    --header 'Content-Type: application/json' \
    --data '{
             "asset": {
                "properties": {
                        "asset:prop:id": "3",
                        "asset:prop:description": "Tractus-X EDC Demo Asset 3"
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

Now that the asset is created, Bob needs to create a policy specifying who can access and use the asset. Consequently, Bob has to create a combined policy enforcing usage and access control.

Action (Bob): Create a policy restricting the access to the asset and the usage  of the asset using a dismantler credential.

Add curl command

Action (Bob): Create the contract definition.

Add curl command

Alice wants to access the asset. Alice fetches catalog.

Action (Alice): Fetch data catalog using cURL command:

Add curl command

Alice can access Bob’s asset. Following, Alice wants to consume the data offer and initiates the contract negotiation.

Add curl command

Unfortunately, the request fails. Alice does not have the necessary dismantler credential.

As you can see in this section of the tutorial you can protect your offered data even beyond the access restriction. With a usage policy you are able to specify what the offered data can be used for, e.g. the traceability use case.

ToDo: Positive outcome hier einfügen, Weg über das Erlangen des richtigen credentials.
