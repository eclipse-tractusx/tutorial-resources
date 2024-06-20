# Simplify negotiation and transfer using the EDR API

A contract negotiation and a transfer can be executed using the
management API, first the negotiation and then the transfer phase, but there is a simpler way to do this using EDR API.
Using this convenient tool, we don't have to care about the intricacies of negotiation and transfer anymore, we can
simply request an API token to Alice's proxy, and start sucking data out of it.
We don't even need to worry about token expiry - the EDR API has a little gizmo that automatically refreshes the token
if it nears expiry.

A detailed documentation about the EDR API is available [here](https://github.com/eclipse-tractusx/tractusx-edc/blob/main/docs/usage/management-api-walkthrough/07_edrs.md).

The EDR API is a tiny wrapper on top of the contract negotiation and transfer state machines. With a single request the system will track the EDR negotiation
for us, and it will store it locally for future usage. The API for starting a new EDR negotiation is similar to the contract negotiation one.

We can start a new EDR negotiation with this `curl` command:

```shell
curl --location 'http://localhost/bob/management/edrs' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data-raw '{
	"@context": {
		"odrl": "http://www.w3.org/ns/odrl/2/"
	},
	"@type": "NegotiationInitiateRequestDto",
	"counterPartyAddress": "http://alice-controlplane:8084/api/v1/dsp",
	"protocol": "dataspace-protocol-http",
	"counterPartyId": "BPNL000000000001",
	"providerId": "BPNL000000000001",
	"offer": {
		"offerId": "MQ==:MQ==:MDJlMGRlOWUtNzdhZS00N2FhLTg5ODktYzEyMTdhMDE4ZjJh",
		"assetId": "1",
		"policy": {
			"@type": "odrl:Set",
			"odrl:permission": {
			    "odrl:target": "1",
				"odrl:action": {
					"odrl:type": "USE"
				},
				"odrl:constraint": {
					"odrl:or": {
						"odrl:leftOperand": "BusinessPartnerNumber",
						"odrl:operator": { "@id": "odrl:eq" },
						"odrl:rightOperand": "BPNL000000000002"
					}
				}
			},
			"odrl:prohibition": [],
			"odrl:obligation": [],
			"odrl:target": "1"
		}
	}
}' | jq
```

For requesting an EDR we have to specify:

- `connectorId` and `providerId`: the participantId returned from the catalog request
- `connectorAddress`: in `dcat:service` field returned from the catalog request
- `offer`: it's derived by the chosen `dcat:Dataset` returned from the catalog request

If everithing is ok, we'll get this as response:

```json
{
  "@type": "edc:IdResponse",
  "@id": "2f911118-657d-4001-b36c-73cb45222a4a",
  "edc:createdAt": 1694446314832,
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

The `@id` here is the id of the contract negotiation that has been started.

Since the EDR negotiation sits on top of two state machines, contract negotiation and transfer process, it's an asyncronous process itself.
In order to be notified without polling, we could configure the EDC callbacks for being notified on state transition.

We could for example add this in the original request:

```json
{
  ...
  "callbackAddresses": [
    {
      "uri": "http://localhost:8080/hooks",
      "events": [
        "transfer.process.started"
      ],
      "transactional": false
    }
  ]
}
```

and be notified when the transfer process transition to the state `STARTED` (EDR negotiatied).

For having a list of the negotiatied EDR for the `assedId` `1` we can use this `curl` command:

```shell
curl --location 'http://localhost/bob/management/edrs?assetId=1' --header 'X-Api-Key: password' | jq
```

and the response should look like this:

```json
[
  {
    "@type": "tx:EndpointDataReferenceEntry",
    "edc:agreementId": "MQ==:MQ==:MWI5OTg2N2YtNTc0ZS00MzUwLTk2NmMtNDFiODE2MzllZTVi",
    "edc:transferProcessId": "89f0d94e-670e-4b0a-a8d9-a6adc726c005",
    "edc:assetId": "1",
    "edc:providerId": "BPNL000000000001",
    "tx:edrState": "NEGOTIATED",
    "tx:expirationDate": 1694447767000,
    "@context": {
      "dct": "https://purl.org/dc/terms/",
      "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
      "edc": "https://w3id.org/edc/v0.0.1/ns/",
      "dcat": "https://www.w3.org/ns/dcat/",
      "odrl": "http://www.w3.org/ns/odrl/2/",
      "dspace": "https://w3id.org/dspace/v0.8/"
    }
  }
]
```

To be able to fetch the data with only the `assetId` via consumer data plane proxy (see more later), only one
`EndPointDataReferenceEntry` associated with an `assetId` should be valid (`Negotiated` or `Refreshing`) at one point in time.
This will allow the proxy to fetch the right `EDR` while requesting data with the `assetId`. Alternatively if we negotiated multiple
`EDRs` for the same `assetId` for some reason, we should use the `transferprocessId` for transferring the data.

The renewal of the token is automatically handled by the `EDR` extension, we just need to fire the first EDR negotiation
and start fetching the data.

Since the EDR is renewed automatically, it can happen that while fetching the EDRs for a particular `assetId` we can see multiple entries like this:

```json
[
  {
    "@type": "tx:EndpointDataReferenceEntry",
    "edc:agreementId": "MQ==:MQ==:MWI5OTg2N2YtNTc0ZS00MzUwLTk2NmMtNDFiODE2MzllZTVi",
    "edc:transferProcessId": "ff468685-0f9b-49a1-8ec6-ea40d5a2dc88",
    "edc:assetId": "1",
    "edc:providerId": "BPNL000000000001",
    "tx:edrState": "NEGOTIATED",
    "tx:expirationDate": 1694448312000,
    "@context": {
      "dct": "https://purl.org/dc/terms/",
      "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
      "edc": "https://w3id.org/edc/v0.0.1/ns/",
      "dcat": "https://www.w3.org/ns/dcat/",
      "odrl": "http://www.w3.org/ns/odrl/2/",
      "dspace": "https://w3id.org/dspace/v0.8/"
    }
  },
  {
    "@type": "tx:EndpointDataReferenceEntry",
    "edc:agreementId": "MQ==:MQ==:MWI5OTg2N2YtNTc0ZS00MzUwLTk2NmMtNDFiODE2MzllZTVi",
    "edc:transferProcessId": "89f0d94e-670e-4b0a-a8d9-a6adc726c005",
    "edc:assetId": "1",
    "edc:providerId": "BPNL000000000001",
    "tx:edrState": "EXPIRED",
    "tx:expirationDate": 1694447767000,
    "@context": {
      "dct": "https://purl.org/dc/terms/",
      "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
      "edc": "https://w3id.org/edc/v0.0.1/ns/",
      "dcat": "https://www.w3.org/ns/dcat/",
      "odrl": "http://www.w3.org/ns/odrl/2/",
      "dspace": "https://w3id.org/dspace/v0.8/"
    }
  }
]
```

which means that the second `EDR` is now expired and marked for removal later in time.

The EDR itself is stored in the configured vault. To retrieve it we can use this `curl` command:

```shell
curl --location 'http://localhost/bob/management/edrs/ff468685-0f9b-49a1-8ec6-ea40d5a2dc88' --header 'X-Api-Key: password' | jq
```

where `ff468685-0f9b-49a1-8ec6-ea40d5a2dc88` is the transfer process id of negotiated EDR. Each EDR is bound to one and only transfer process,
and the automatic EDR renewal process will just fire another transfer request with the same configuration when it's near expiry.

The response will look like this:

```json
{
  "@type": "edc:DataAddress",
  "edc:type": "EDR",
  "edc:authCode": "eyJhbGciOiJFUzI1NiJ9.eyJleHAiOjE2OTQ1MjIwNTksImRhZCI6ImNoTTlvVTVLNXQzbDlWMFRsL1ZZdDlLU1J4YmNOSUdzM1FtazNlNktWOWpWcTBkeUhjUDU2Mm82Qk0zSitxeTRwRVg2d0EvWUFsdW9EdGptYnYxZlJoN3VmVmsvQjNONzhBMUhyZ01ENnk2enFsK1BEYzBXa00yTm9ycUJWQUl0TWpVNEFNbGhFMXE1Ym9EQ1lWcVRsQVZnbm9uTlB5MmlVUzVSVTJHTkZtOWFkZVZYR1ZLaDFDWEMzVDV0RkRCS21EMjExWVZYdDExRUlXbCtIU3VISm1PL0xwUUdibFkvaGFicXZ6aUZ0YlppbGlKSDNLdGVhZTZQRkdQTjNWT1Z4YlFrZTNmODNRN3VNeStBNzV4YS9VR1BMcjJlQkJzb0ZVbTBYeFFJS2dBOUROdGxXcnBuR3hwdG9tL1VWY00wQ1RwcWM5eFRRdGlnK3JMVlJ4dUhrb2RreG5KUXhiSENVMnNObnFhdXZJcDV4L04rbGdJN0F1amhtQWxiN2NwUWs0RDhSWWtZSnkvVUZGdGZmZUJLU2k2MnZDeC9QSFJsSERlUGM4VldDaEJTNFF1Q1FXY1pOK2oyUjR5b2Q2a3JlN2JtUStFK3pLUmYva3JhQkJkR041TDR5ZVdIYU0wS3oraGxiSVR5WHg2bjdrQ0VkVVVSREtCUHY3SHdzbHhLTzlxN05ReHplMHFDM0phR2pyWVdHZmJHTzB4SDlJRndsSWpqclZHMzE0WUVxNGdSTjNNPSIsImNpZCI6Ik1RPT06TVE9PTpOV0ZqTTJJeVkyWXRNRGt4WkMwME9UQmxMV0poTXpNdE1ERmxNRGhtTUdNNU5tVTIifQ.2UT3_mIjchrC242TqlLFWoyYPiCOPLLivaN5Xd4_MxhcQkxRkOxrK0IXkXVuRVjC1ReGPi3iaco9LDUxvF3FPw",
  "edc:endpoint": "http://alice-tractusx-connector-dataplane:8081/api/public",
  "edc:id": "ff468685-0f9b-49a1-8ec6-ea40d5a2dc88",
  "edc:authKey": "Authorization",
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

### Fetching data

For fetching the data we can use two strategies depending on the use case:

- Provider data-plane
- Consumer data-plane (proxy)

#### Provider data-plane

Once the right EDR has been identified using the EDR management API, we can use the `endpont`, `authCode` and `authKey` to make the data request:

```shell
curl --location 'http://localhost/alice/api/public' \
--header 'Authorization: eyJhbGciOiJFUzI1NiJ9.eyJleHAiOjE2OTQ1MjIwNTksImRhZCI6ImNoTTlvVTVLNXQzbDlWMFRsL1ZZdDlLU1J4YmNOSUdzM1FtazNlNktWOWpWcTBkeUhjUDU2Mm82Qk0zSitxeTRwRVg2d0EvWUFsdW9EdGptYnYxZlJoN3VmVmsvQjNONzhBMUhyZ01ENnk2enFsK1BEYzBXa00yTm9ycUJWQUl0TWpVNEFNbGhFMXE1Ym9EQ1lWcVRsQVZnbm9uTlB5MmlVUzVSVTJHTkZtOWFkZVZYR1ZLaDFDWEMzVDV0RkRCS21EMjExWVZYdDExRUlXbCtIU3VISm1PL0xwUUdibFkvaGFicXZ6aUZ0YlppbGlKSDNLdGVhZTZQRkdQTjNWT1Z4YlFrZTNmODNRN3VNeStBNzV4YS9VR1BMcjJlQkJzb0ZVbTBYeFFJS2dBOUROdGxXcnBuR3hwdG9tL1VWY00wQ1RwcWM5eFRRdGlnK3JMVlJ4dUhrb2RreG5KUXhiSENVMnNObnFhdXZJcDV4L04rbGdJN0F1amhtQWxiN2NwUWs0RDhSWWtZSnkvVUZGdGZmZUJLU2k2MnZDeC9QSFJsSERlUGM4VldDaEJTNFF1Q1FXY1pOK2oyUjR5b2Q2a3JlN2JtUStFK3pLUmYva3JhQkJkR041TDR5ZVdIYU0wS3oraGxiSVR5WHg2bjdrQ0VkVVVSREtCUHY3SHdzbHhLTzlxN05ReHplMHFDM0phR2pyWVdHZmJHTzB4SDlJRndsSWpqclZHMzE0WUVxNGdSTjNNPSIsImNpZCI6Ik1RPT06TVE9PTpOV0ZqTTJJeVkyWXRNRGt4WkMwME9UQmxMV0poTXpNdE1ERmxNRGhtTUdNNU5tVTIifQ.2UT3_mIjchrC242TqlLFWoyYPiCOPLLivaN5Xd4_MxhcQkxRkOxrK0IXkXVuRVjC1ReGPi3iaco9LDUxvF3FPw' | jq
```

and the response will look like this:

```json
[
    {
        "userId": 1,
        "id": 1,
        "title": "delectus aut autem",
        "completed": false
    },
    {
        "userId": 1,
        "id": 2,
        "title": "quis ut nam facilis et officia qui",
        "completed": false
    },
    {
        "userId": 1,
        "id": 3,
        "title": "fugiat veniam minus",
        "completed": false
    },
    ...
]
```

The provider receives the token, does some security checks and if all it's good it forwards the request to the configured
baseUrl in the DataAddress of Asset, which in this case will do a `GET` request to `https://jsonplaceholder.typicode.com/todos`.

> Replace the Authorization header with the negotiated one.

> The endpoint in the curl above is different from the one returned by the EDR GET API for testing purpose and deployment reason.

#### Consumer data-plane (proxy)

This option is a simplification of the above one. At this point we know that we negotiated an EDR with the EDR API with a provider for an `assetId`, and
that EDR will be automatically renewed.

We can simply use the `proxy` API for fetching the data with a `POST` request:

```shell
curl --location 'http://localhost/bob/proxy/aas/request' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data '{
    "assetId": "1",
    "providerId": "BPNL000000000001"
}' | jq
```

and get the same results as the provider data-plane option:

```json
[
    {
        "userId": 1,
        "id": 1,
        "title": "delectus aut autem",
        "completed": false
    },
    {
        "userId": 1,
        "id": 2,
        "title": "quis ut nam facilis et officia qui",
        "completed": false
    },
    {
        "userId": 1,
        "id": 3,
        "title": "fugiat veniam minus",
        "completed": false
    },
    ...
]
```

The consumer proxy will fetch the `EDR` associated with the `assetId` and the `provider` id in the local storage, and it will
do a `GET` request to the provider data-plane for us.

> Note that if multiple `EDRs` are associated to the `assetId` and `providerId`, the proxy will fail to fulfill the request. In this case the `transferprocessid` should be used

Since the `DataAddress` of the asset has been configured to proxy also `pathSegments`, we could add another
parameter in the request to fetch exactly one item from the list:

```shell
curl --location 'http://localhost/bob/proxy/aas/request' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: password' \
--data '{
    "assetId": "1",
    "providerId": "BPNL000000000001",
    "pathSegments": "/1"
}' | jq
```

which will give us:

```json
{
  "userId": 1,
  "id": 1,
  "title": "delectus aut autem",
  "completed": false
}
```

and the proxied URL will be `https://jsonplaceholder.typicode.com/todos/1`.
