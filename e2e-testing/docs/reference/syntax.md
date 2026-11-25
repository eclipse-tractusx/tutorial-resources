# Syntax cheat sheet

Everything a TCK project for an E2E test is made of, on one page. Every example here is taken
from projects that validate, compile and pass. The complete reference is TestLab's
[TCK syntax](https://eclipse-tractusx.github.io/tractusx-testlab/tck-syntax/); when this page
and the compiler disagree, the compiler is right.

[Home](../index.md) · [3 · Hello, TestLab](../tutorial/03-hello-testlab.md)

## The folder

```text
tck/e2e/
├── index.yaml      the manifest: what the project is, what a run needs, which tests belong to it
├── tests/          one file per test
└── schemas/        JSON schemas an answer is checked against (only if you use one)
```

## The manifest, `index.yaml`

```yaml
kind: tck
syntax: v1-alpha

id: my-product-e2e                # lower case, hyphens; every test repeats it as `namespace`

metadata:
  name: "My Product — E2E test"
  version: "v0.1.0"
  description: >
    What a green run proves, in one or two sentences.
  authors:
    - name: My team
      company: My company
  license: Apache-2.0

dataspace:                        # only when the test uses connectors
  ecosystem: Catena-X
  version: saturn

infrastructure:                   # what a run must bring, per side
  engine:                         # the partner, where the test runs from
    connector:
      required: true
  sut:                            # your side, the system under test
    connector:
      required: true

env:
  variables:                      # values the tests use, see "Variables"
    - ...
  schemas:                        # only with a schemas/ folder
    - id: part
      source: part.json

tests:                            # a test file not listed here is not part of the project
  - id: says_hello.yaml           # the file name in tests/
    name: The service says hello
```

The [hello project](../tutorial/03-hello-testlab.md) needs no connector, so it has no
`dataspace` and no `infrastructure`.

## A test file

```yaml
kind: test
syntax: v1-alpha

namespace: my-product-e2e         # the manifest's id
id: says-hello                    # lower case, hyphens

metadata:
  name: "The service says hello"
  version: "v0.1.0"
  description: What this one test proves.

setup:                            # optional: prepare what the test needs
  - ...
execution:                        # required: do it, and check the answers
  - ...
teardown:                         # optional: remove what was created, even after a failure
  - ...
```

## A step

```yaml
- id: say_hello                               # lower case, underscores; unique in the test
  uses: http/http_request                     # the action, from the step catalog
  name: Ask the service to say hello          # what a human reads in the result
  with:                                       # the action's inputs
    url: "${{ env.service_url }}/hello"
  returns:                                    # the outputs you want to use
    status_code:
      type: integer
    body:
      type: object
  validate:                                   # the checks on those outputs
    - uses: validate/assert
      with: { input: status_code, operator: equals, value: 200 }
```

Inputs and outputs are not free-form: each action has its own. Look one up in the IDE's
block catalog, or on the command line:

```bash
testlab docs -s http/http_request -o -
```

Two actions you meet first:

| `uses` | Does | Outputs you use most |
| --- | --- | --- |
| `http/http_request` | One HTTP request to any address | `status_code`, `body` |
| `connector/consumer/query_catalog_with_filters` | Asks a partner's connector for its offers | `datasets` |

## References

`${{ ... }}` puts in a value from somewhere else. Always quote a value that contains one.

| Write | To get |
| --- | --- |
| `${{ env.<variable id> }}` | A variable from the manifest |
| `${{ env.schemas.<schema id> }}` | A JSON schema from the manifest |
| `${{ setup.<step id>.<output> }}` | An output of a step in `setup` |
| `${{ execution.<step id>.<output> }}` | An output of an earlier step in `execution` |
| `${{ infrastructure.sut.connector.dsp_url }}` | Where a partner reaches the connector under test |
| `${{ infrastructure.sut.connector.participant_id }}` | The identity of the connector under test (a DID) |
| `${{ infrastructure.engine.connector.participant_id }}` | The identity of the partner, the test side |
| `${{ infrastructure.sut.dtr.base_url }}` | The Digital Twin Registry under test |

## Variables

A value written in the manifest:

```yaml
- id: service_url
  uses: variable/type/string
  name: Address of the hello service
  with:
    source: value
    value: "http://localhost:8080"
  returns:
    value:
      type: string
```

A value the run must supply, for one side:

```yaml
- id: product_internal_url
  uses: variable/type/string
  name: Product API, as its own connector reaches it
  with:
    source: input
    scope: sut                    # sut or engine: whose value it is
  returns:
    value:
      type: string
```

The command line overrides either kind with `testlab run index.yaml --var service_url=...`.
In the IDE, change them under **☰ → Environment**.

## Checks

| `uses` | Checks | `with` |
| --- | --- | --- |
| `validate/assert` | One output | `input`, `operator`, and `value` when the operator takes one |
| `validate/field` | A value inside an output, by path | `input`, `path`, `operator`, `value` |
| `validate/schema` | An output against a JSON schema | `input`, `schema` |

Operators: `equals`, `not_equals`, `contains`, `not_contains`, `matches_regex`, `one_of`,
`none_of`, `is_in`, `has_key`, `not_has_key`, `gt`, `gte`, `lt`, `lte`, `between`
(`min`, `max`), `length_equals`, `length_gt`, `length_lt`, `not_null`, `is_null`,
`not_empty`.

A path reaches into lists by a field's value:

```yaml
- uses: validate/field
  with:
    input: body
    path: "specificAssetIds[name='manufacturerPartId'].value"
    operator: equals
    value: "e2e-part-4711"
```

Add `severity: SOFT` to a check to report it as a warning instead of failing the step.

## Three things that are easy to get wrong

- **`namespace` in every test file must equal the manifest's `id`.** Rename both together.
- **A test file not listed under `tests:` is not part of the project.** Register each one.
- **A reference to a step starts with its phase**: `setup.create_asset.asset_id`, not
  `create_asset.asset_id`.

## Commands

```bash
testlab validate index.yaml                # are the files well-formed?
testlab compile index.yaml -o my.tck       # pack them into one package
testlab inspect my.tck --variables         # what the package will need and do
testlab run index.yaml --yes               # run it, and approve its checkpoints
```

---

[← The worked example](worked-example.md) · [Home](../index.md) · [The Test Suite IDE →](ide.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
