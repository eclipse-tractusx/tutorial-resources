# Provide and consume data

As described in the introduction, a data exchange between Bob (Data Provider) and Alice (Data Consumer) is to be tested.

To maximize the benefit from this tutorial it is recommended to follow the tutorial in the given order.

## Provide data

In this step we will focus on inserting data into our participant Alice using
the [Management API](https://app.swaggerhub.com/apis/eclipse-edc-bot/management-api/0.1.4-SNAPSHOT). We will use plain
CLI tools (`curl`) for this, but feel free to use graphical tools such as Postman or Insomnia.

Alice, as a data consumer, wants to consume data from Bob. Bob, as a data provider, needs to create an asset for Alice. The data asset should have the following properties:

| ID          | 1                                                                                            |
|-------------|----------------------------------------------------------------------------------------------|
| Description | Tractus-X EDC Demo Asset                                                                     |
| Type        | HttpData                                                                                     |
| URL         | [https://jsonplaceholder.typicode.com/todos/1](https://jsonplaceholder.typicode.com/todos/1) |

Action (Bob): Create this asset using the following curl command:

```shell
curl -X POST "${BOB_DATAMGMT_URL}/data/assets" \
    --header 'X-Api-Key: password' \
    --header 'Content-Type: application/json' \
    --data '{
             "asset": {
                "properties": {
                        "asset:prop:id": "1",
                        "asset:prop:description": "Tractus-X EDC Demo Asset"
                    }
                },
                "dataAddress": {
                    "properties": {
                        "type": "HttpData",
                        "baseUrl": "https://jsonplaceholder.typicode.com/todos/1"
                    }
                }
            }' \
    -s -o /dev/null -w 'Response Code: %{http_code}\n'
```

Bob tells Alice, that he created an asset, and she should now be able to request it. In the next step, Alice requests a contract offer catalog. In this catalog, all contract offers for Alice are listed.

Action (Alice): Execute a request using the following curl commands:

```shell
curl -G -X GET "${ALICE_DATAMGMT_URL}/data/catalog" \
    --data-urlencode "providerUrl=${BOB_IDS_URL}/api/v1/ids/data" \
    --header 'X-Api-Key: password' \
    --header 'Content-Type: application/json' \
    -s | jq
```

Let´s see if Alice can see the Asset. Can you find it?

As you can see in the response, the data offer "Tractus-X EDC Demo Asset" does not appear. Unfortunately, Alice sees some contract offers but she cannot find the contract offer from Bob.

Alice calls Bob and says she can´t see the asset. Bob remembers that he did not create an access policy. An access policy defines who is allowed to see a data offering. To create a policy that allows Alice to access the data offering, Bob needs Alice's Business Partner Number (BPN). Alice´s BPN is BPNL000000000001.

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
                    "odrl:and":
                        {
                            "@type": "Constraint",
                            "odrl:leftOperand": "BusinessPartnerNumber",
                            "odrl:operator": {
                                "@id": "odrl:eq"
                            },
                            "odrl:rightOperand": "{{BPN123}}"
                        },
                }
            }
        ]
    }
} 
```

Bob tells Alice that he has created the right policy. Let´s see if Alice can now find the data asset. Execute the request again using the following curl command:

```shell
curl -G -X GET "${ALICE_DATAMGMT_URL}/data/catalog" \
    --data-urlencode "providerUrl=${BOB_IDS_URL}/api/v1/ids/data" \
    --header 'X-Api-Key: password' \
    --header 'Content-Type: application/json' \
    -s | jq
```

Let´s see if Alice can see the Asset. Can you find it?

Once again Alice cannot find the data offer. This is by design and to be expected since Bob has only created an asset and a policy definition. An asset and a policy cannot be displayed to Alice as a consumer without a contract definition.
**This is the first lesson for this tutorial: A contract must be defined between two parties that want to exchange data. This contract must always contain an asset and a policy.**

Add image showing that a conctract defiition consists of a data asset and a policy definition

Action (Bob): Create a contract definition including the asset and the policy you have created. For this, use the following curl command:

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
                        "operandRight": "1"
                    }
                ],
                "accessPolicyId": "1",
            }' \
    -s -o /dev/null -w 'Response Code: %{http_code}\n'
```

Let´s see if Alice can finally see the Asset.
action (Alice): Execute the request again using the following curl command:

```shell
curl -G -X GET "${ALICE_DATAMGMT_URL}/data/catalog" \
    --data-urlencode "providerUrl=${BOB_IDS_URL}/api/v1/ids/data" \
    --header 'X-Api-Key: password' \
    --header 'Content-Type: application/json' \
    -s | jq
```

Finally Alice can see the Contract Offer from Bob.
Congratulations on yor first successful data exchange in your own data sapce!

Before you start the next tutorial please delete all data:

```shell
minikube kubectl -- delete pvc -n edc-all-in-one –all
 
minikube kubectl -- delete pv -n edc-all-in-one --all
```
