# Patterns

Pick the shape that fits the product. Each one is in `assets/` as a file that passes.

## 1 · A product behind a connector

Most products. A partner reaches the product's API through the provider's connector.

File: `assets/template/tests/partner_uses_my_product.yaml`

| Phase | Steps |
| --- | --- |
| `setup` | `connector/provider/create_policy` twice (access, usage) → `connector/provider/create_asset` → `connector/provider/create_contract_definition` |
| `execution` | `connector/consumer/pull_data_filtered` (catalog, negotiation and transfer in one step) → `connector/dataplane/http_request` (call the product with the token) |
| `teardown` | delete the contract definition, the asset and both policies, in that order |

What to change: the asset's `base_url` variable, the `path` and `method` of the call, and
the checks on the answer. Check what the product promises, not only status 200.

## 2 · A Digital Twin Registry

A partner finds and reads a twin through the owner's connector.

File: `assets/example/tests/partner_reads_through_connector.yaml`

| Phase | Steps |
| --- | --- |
| `setup` | policies → an asset of type `https://w3id.org/catenax/taxonomy#DigitalTwinRegistry` → contract definition → `digital-twin-registry/provider/wizard/create_shell_descriptor` → `digital-twin-registry/provider/create_submodel_descriptor` |
| `execution` | `connector/discover/digital-twin-registry/auth` → `digital-twin-registry/consumer/dataplane/lookup_shell` → `digital-twin-registry/consumer/dataplane/get_shell_descriptor` |
| `teardown` | delete the twin, then the offer |

A twin needs at least one submodel descriptor. Without it the shell descriptor schema
rejects the answer, which is correct: a twin that points at no data is of no use.

## 3 · The owner's view, with a contract check

Register, read back, compare with a JSON schema.

File: `assets/example/tests/register_and_read.yaml`

Use `util/generate_uuid` for an identifier that is new in every run, so the test can run
twice in a row.

## 4 · A refusal

The smallest useful test: ask for something that must not be there, and expect the exact
answer.

| File | Asks for | Expects |
| --- | --- | --- |
| `assets/template/tests/unknown_offer_is_not_found.yaml` | An asset that was never published | An empty list of datasets |
| `assets/example/tests/unknown_twin_is_not_found.yaml` | A twin the registry never held | Status 404 |

A catalog request needs no asset of its own: the template's negative test creates nothing.

Other refusals worth a test: a call without a token, an offer under a policy the partner
cannot fulfil, a twin the partner is not allowed to see.

## 5 · A service the connectors rely on

The BPN-DID Resolution Service (BDRS), the wallet and the issuer are not behind a
connector, and no step calls them directly. A test proves them **through an exchange that
cannot work without them**. Say this to the human: the test shows the service by its
effect, and a red result may also be the connector's fault.

| Service | The exchange that needs it | Steps |
| --- | --- | --- |
| BDRS | Reaching a partner with nothing but its BPN: the connector asks the directory for the DID | `connector/consumer/discover_connector`, `connector/consumer/query_catalog_by_bpnl`, `connector/consumer/do_dsp_with_bpnl` |
| Wallet and issuer | Any contract negotiation: both sides present credentials | `connector/consumer/negotiate`, `connector/consumer/pull_data_filtered` with a policy that asks for a credential |

A check that makes the BDRS test worth having: the identity that `discover_connector`
returns as `counter_party_id` equals `${{ infrastructure.sut.connector.participant_id }}`.
A BPN resolved to any other identity is a wrong directory entry.

The partner's BPN is an input variable. See "There is no BPN field" in the syntax page.

## 6 · A product a partner calls directly

For a service that is not behind a connector, call it with `http/http_request` and an
input variable for its address. Declare only `engine` and `sut` capabilities the tests
really use.

## What does not fit

- **A product none of the patterns reaches.** Say so before you write. Propose the closest
  honest test and name what it does not prove.
- **A user interface.** TestLab tests interfaces between systems. Test the API calls the
  interface makes.
- **A test that needs to read the product's logs or database.** An E2E test only sees what
  a partner sees.

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
