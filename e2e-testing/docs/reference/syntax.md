# Syntax cheat sheet

The parts of a TCK project on one page. Full reference:
[TestLab TCK syntax](https://eclipse-tractusx.github.io/tractusx-testlab/tck-syntax/).

[Home](../index.md) · [3 · Hello, TestLab](../tutorial/03-hello-testlab.md)

## The folder

```text
tck/e2e/
├── index.yaml      the manifest
├── tests/          one file per test
└── schemas/        optional JSON schemas
```

## The manifest, `index.yaml`

```yaml
kind: tck
syntax: v1-alpha
id: my-product-e2e              # every test repeats this as `namespace`
metadata:
  name: "My Product — E2E test"
  version: "v0.1.0"
env:
  variables:
    - ...
tests:                          # a test not listed here does not run
  - id: says_hello.yaml
    name: The service says hello
```

## A test

```yaml
kind: test
syntax: v1-alpha
namespace: my-product-e2e       # = the manifest's id
id: says-hello
metadata:
  name: "The service says hello"
  version: "v0.1.0"
setup: []                       # optional
execution:                      # the steps
  - id: say_hello
    uses: http/http_request     # the action
    with:                       # its inputs
      url: "${{ env.service_url }}/hello"
    returns:                    # the outputs you use
      status_code: { type: integer }
    validate:                   # the checks
      - uses: validate/assert
        with: { input: status_code, operator: equals, value: 200 }
teardown: []                    # optional, runs even after a failure
```

See an action's inputs and outputs with `testlab docs -s http/http_request -o -`, or in the
IDE's toolbox.

## References

| Write | To get |
| --- | --- |
| `${{ env.<id> }}` | A variable from the manifest |
| `${{ execution.<step>.<output> }}` | An output of an earlier step (`setup.` for setup steps) |
| `${{ infrastructure.sut.connector.dsp_url }}` | The connector under test |

## Variables

```yaml
- id: service_url
  uses: variable/type/string
  with:
    source: value               # or `input` with `scope: sut`, to ask at run time
    value: "http://localhost:8080"
  returns:
    value: { type: string }
```

## Checks

| `uses` | Checks |
| --- | --- |
| `validate/assert` | One output: `input`, `operator`, `value` |
| `validate/field` | A field inside an output: adds `path` |
| `validate/schema` | An output against a JSON schema |

Common operators: `equals`, `contains`, `not_empty`, `gt`, `lt`, `matches_regex`.

## Commands

```bash
testlab validate index.yaml
testlab compile index.yaml -o my.tck
testlab run index.yaml --yes
```

---

[← The worked example](worked-example.md) · [Home](../index.md) · [The Test Suite IDE →](ide.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
