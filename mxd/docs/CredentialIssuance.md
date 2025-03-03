# DCP Credential Issuance

This document shows how new VerifiableCredentials can be issued by the central Dataspace Issuer Service. Familiarity
with
the [DCP Issuance Protocol](https://eclipse-dataspace-dcp.github.io/decentralized-claims-protocol/v1.0-RC1/#credential-issuance-protocol)
is assumed and terms won't be explained here again.

Furthermore, please check out
this [IdentityHub documentation](https://github.com/eclipse-edc/IdentityHub/blob/main/docs/developer/architecture/issuer/issuance/issuance.process.md)
to learn about the implementation of DCP issuance in IdentityHub.

## Dataspace setup

The key element is a central dataspace component called the `dataspace-issuer-service`. This runtime is based on
the [Tractus-X IssuerService](https://github.com/eclipse-tractusx/tractusx-issuerservice) and its purpose is to take
credential requests using the DCP protocol and issue credential to eligible participants.

### Creating dataspace accounts

Every dataspace participant that wants to request credentials must have an "account" with the issuer service. In a real
world scenario such an account would be handled during onboarding by the onboarding application.
Here, we create the accounts during the seeding phase, please refer to
the [respective Postman collection](../postman/mxd-seed.json) for details.

### Creating attestations and credential definitions

In MXD, the Dataspace Issuer Service can issue credentials of type `"NameCredential"`, with the sole purpose of
attesting to a participant's ID. This is how the `NameCredential` looks similar to this:

```json
{
  "credentialSubject": [
    {
      "id": "e2e4613b-b756-4493-ac7f-364aa1f3b944",
      "name": "did:web:bob-ih%3A7083:bob"
    }
  ],
  "id": "45090039-163e-4244-9861-9cad752e7adf",
  "type": [
    "VerifiableCredential",
    "NameCredential"
  ],
  "issuer": {
    "id": "did:web:dataspace-issuer-service%3A10016:issuer"
  },
  "issuanceDate": "2025-03-03T08:44:24Z",
  "expirationDate": "2025-03-03T08:44:24Z"
}
```

The appropriate AttestationDefinition and CredentialDefinition objects are automatically created during the data seeding
of the dataspace.
For reference check out the "Seed Dataspace Issuer" folder inside the [MXD seed collection](../postman/mxd-seed.json).

## Making a credential request

Dataspace participants that wish to have the `NameCredential` issued to them may request them by triggering a DCP
issuance flow through their IdentityHub's IdentityAPI, for example Bob:

```shell
curl --location 'http://localhost/bob-ih/cs/api/identity/v1alpha/participants/cGFydGljaXBhbnQtYm9i/credentials/request' \
--header 'Content-Type: application/json' \
--header 'X-Api-Key: <API_KEY>' \
--data '{
    "issuerDid": "did:web:dataspace-issuer-service%3A10016:issuer",
    "holderPid": "credential-request-1",
    "credentials": [{
        "format": "VC1_0_JWT",
        "credentialType": "NameCredential"
    }]
}'
```

This invokes an API endpoint on Bob's IdentityHub which in turn triggers Bob's credential state machine to request the
`NameCredential`, serialized as VC DataModel 1.1 compliant JSON, secured with a JWT as proof. The response is a String
containing the request ID `credential-request-1`.

To keep track of the credential request, the following API call can be made against Bob's IdentityHub:

```shell
curl --location 'http://localhost/bob-ih/cs/api/identity/v1alpha/participants/cGFydGljaXBhbnQtYm9i/credentials/request/credential-request-1' \
--header 'X-Api-Key: <API_KEY>' \
--data ''
```

which will return a status object that indicates the current credential request state:

```json
{
  "issuerDid": "did:web:dataspace-issuer-service%3A10016:issuer",
  "holderPid": "credential-request-1",
  "issuerPid": "9d99ec27-2be9-493d-a9cd-e9ff0c0b04fa",
  "status": "ISSUED",
  "typesAndFormats": {
    "NameCredential": "VC1_0_JWT"
  }
}
```

Note that the `issuerPid` is the issuer-side tracking ID of the request.

## Verifying the issued credential

We can now verify that the credential was delivered to Bob by the Dataspace Issuer Service. To do that, we simply query
Bob's IdentityHub for credentials, using the following request:

```shell
curl --location 'http://localhost/bob-ih/cs/api/identity/v1alpha/participants/ZGlkOndlYjpib2ItaWglM0E3MDgzOmJvYg==/credentials?type=NameCredential' \
--header 'X-Api-Key: c3VwZXItdXNlcg==.c3VwZXItc2VjcmV0LWtleQo='
```

Notice how this encodes the Bob's participant ID (`participant-bob`) as base64 String in the URL path. This should
return the internal representation of a Verifiable Credential.

## Limitations and extending the issuance

Currently, the main credentials `MembershipCredential` and `DataExchangeGovernance` are still delivered during the data
seeding phase, when MXD is being deployed. Consequently, they are _not_ yet issued by the Dataspace Issuer Service,
which is why there also is a `dataspace-issuer-server` to host a static DID document.

This will eventually be replaced such that _all_ credentials are being issued by the Dataspace Issuer Service during
dataspace deployment.

In order to extend the issuance, e.g. to introduce new credentials, three things are required:

- an `AttestationDefinition`: this is the template for attestation data and can be created using the Dataspace Issuer
  Service's Admin API. No additional code is required here.
- a `CredentialDefinition`: defines how data from the attestation is mapped onto the verifiable credential. Again, use
  the Dataspace Issuer Service's Admin API. No additional code is required here.
- an `AttestationSource`: together with the `AttestationSourceFactory` this is used to obtain the credential data, for
  example from a government service, a database or another VerifiablePresentation. This _will_ require additional code
  modules and will thus require a redeployment of the issuer service.


