# TCK syntax, the parts an E2E test needs

Everything here is taken from the template and the example in `assets/`, which validate,
compile and pass. The full reference is the
[TCK syntax reference](https://eclipse-tractusx.github.io/tractusx-testlab/tck-syntax/).
When this page and the compiler disagree, the compiler is right.

## The folder

```text
tck/e2e/
├── index.yaml      the manifest
├── tests/          one file per test, named in the manifest
└── schemas/        JSON schemas, only if a test validates against one
```

## The manifest, `index.yaml`

```yaml
kind: tck
syntax: v1-alpha

id: my-product-e2e                # lower case, hyphens; test files repeat it as `namespace`

metadata:
  name: "My Product — E2E test"
  version: "v0.1.0"
  description: >
    What a green run proves, in one or two sentences.
  authors:
    - name: Eclipse Tractus-X contributors
      company: Eclipse Tractus-X
  copyright_holders:
    - "2026 Contributors to the Eclipse Foundation"
  license: Apache-2.0
  standards: []                   # or: [{ id: CX-0002, version: v2.3.0 }]
  tags:
    - e2e

dataspace:
  ecosystem: Catena-X
  version: saturn

infrastructure:                   # what a run must bring, per side
  engine:
    connector:
      required: true
  sut:
    connector:
      required: true
    dtr:                          # only if a test uses the Digital Twin Registry
      required: true

env:
  variables:                      # see "Variables"
    - ...
  schemas:                        # only with a schemas/ folder
    - id: shell_descriptor
      source: shell_descriptor.json

tests:
  - id: partner_uses_my_product.yaml      # the file name in tests/
    name: A partner finds the offer, negotiates a contract and gets an answer
```

## A test file

```yaml
kind: test
syntax: v1-alpha

namespace: my-product-e2e         # the manifest's id
id: partner-uses-my-product       # lower case, hyphens

metadata:
  name: "A partner uses my product"
  version: "v0.1.0"
  description: >
    What this one test proves.

setup:           # optional: prepare what the test needs
  - ...
execution:       # required: do it, and check the answers
  - ...
teardown:        # optional: remove what was created
  - ...
```

## A step

```yaml
- id: read_twin                                   # lower case, underscores; unique in the test
  uses: digital-twin-registry/consumer/dataplane/get_shell_descriptor
  name: Read the twin as the partner              # a human reads this in the result
  with:                                           # inputs, as the catalog names them
    dataplane_url: "${{ execution.registry_auth.dataplane_url }}"
    edr_token: "${{ execution.registry_auth.edr_token }}"
    aas_identifier: "${{ setup.create_shell.id }}"
  returns:                                        # the outputs later steps or checks read
    id:
      type: string
  validate:                                       # checks on this step's outputs
    - uses: validate/assert
      with: { input: id, operator: equals, value: "${{ setup.create_shell.id }}" }
```

Inputs and outputs are **not** free-form. Look the step up in the catalog first:

```bash
testlab docs -s digital-twin-registry/consumer/dataplane/get_shell_descriptor -o -
```

## References

| Write | To get |
| --- | --- |
| `${{ setup.<step id>.<output> }}` | An output of a step in `setup` |
| `${{ execution.<step id>.<output> }}` | An output of a step in `execution` |
| `${{ env.<variable id> }}` | A variable from the manifest |
| `${{ env.schemas.<schema id> }}` | A JSON schema from the manifest |
| `${{ infrastructure.sut.connector.dsp_url }}` | Where a partner reaches the connector under test |
| `${{ infrastructure.sut.connector.participant_id }}` | The identity the connector under test negotiates under |
| `${{ infrastructure.engine.connector.participant_id }}` | The identity of the partner, the test side |
| `${{ infrastructure.sut.dtr.base_url }}` | The Digital Twin Registry under test |

Always quote a value that contains `${{ ... }}`.

These four are the infrastructure fields a test needs. Two things about them:

- **`participant_id` is a DID, not a BPN**, for the current connector generation
  (`dataspace.version: saturn`), for example `did:web:provider.example:BPNL000000000001`.
- **There is no BPN field.** A test that needs a BPN declares an input variable for it, as
  the example does with `engine_bpnl`.

## Variables

A variable's `uses:` is one of three kinds. They are not steps, so `testlab docs` does not
list them: `variable/type/string`, `config/connector/asset`, `config/connector/policy`.

A value the run must supply:

```yaml
- id: product_internal_url
  uses: variable/type/string
  name: Product API, as its own connector reaches it
  description: >
    One sentence a stranger understands, with an example value.
  with:
    source: input
    scope: sut                    # sut or engine: whose value it is
  returns:
    value:
      type: string
```

An asset or a policy the tests create on the connector under test:

```yaml
- id: product_asset
  uses: config/connector/asset
  name: Your product, offered as an asset
  with:
    source: value
    value:
      asset_id: my-product-e2e-asset
      base_url: "${{ env.product_internal_url }}"
      dct_type: "https://w3id.org/catenax/taxonomy#TestData"
      version: "1.0"
  returns:
    value:
      type: object
      class: Asset

- id: access_policy
  uses: config/connector/policy
  name: Who may see the offer
  with:
    source: value
    value:
      policy_id: my-product-e2e-access-policy
      permissions:
        - action: access
          constraints:
            - left_operand: Membership
              operator: eq
              right_operand: active
  returns:
    value:
      type: object
      class: Policy
```

Copy the usage policy from `assets/template/index.yaml`. Its three constraints are the ones
the participants of a test dataspace can prove.

## Checks

| `uses` | Checks | `with` |
| --- | --- | --- |
| `validate/assert` | An output of the step | `input`, `operator`, and `value` where the operator takes one |
| `validate/field` | A value at a path inside an output | `input`, `path`, `operator`, `value` |
| `validate/schema` | An output against a JSON schema | `input`, `schema` |

Operators: `not_null`, `is_null`, `not_empty`, `equals`, `not_equals`, `contains`,
`not_contains`, `is_in`, `matches_regex`, `one_of`, `none_of`, `has_key`, `not_has_key`,
`gt`, `gte`, `lt`, `lte`, `length_equals`, `length_gt`, `length_lt`, `between` (`min`, `max`).

A path reaches into lists by a field's value:

```yaml
- uses: validate/field
  with:
    input: body
    path: "specificAssetIds[name='manufacturerPartId'].value"
    operator: equals
    value: "e2e-part-4711"
```

## What to do with the template's placeholders

| Placeholder | Do |
| --- | --- |
| `my-product`, `My Product` | Replace everywhere, in the manifest and in every test file |
| `standards: []` | Leave empty unless the human names a standard and its version |
| `dct_type: ...#TestData` | Keep, unless the product is offered under a type of its own. The registry's is `https://w3id.org/catenax/taxonomy#DigitalTwinRegistry`. |
| `UsagePurpose` in the usage policy | Keep `cx.core.industrycore:1`, or use `cx.core.digitalTwinRegistry:1` for a registry. Ask before you use another. |
| `path: "/"` in the call | Replace with an operation of the product's API. Ask for it if you do not know it. |

## Outputs you will meet

The catalog lists each step's outputs. Three appear often and are worth knowing:
`status_code` and `body` on calls through the data plane, and `value` on `util/generate_uuid`
and on string variables.

## Three things that are easy to get wrong

- **`namespace` in every test file must equal the manifest's `id`.** Rename both together.
- **A test file not listed under `tests:` is not part of the project.** Register each one.
- **A reference to a step always starts with its phase**: `setup.create_asset.asset_id`,
  not `create_asset.asset_id`.

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
