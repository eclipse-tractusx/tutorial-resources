# The worked example

A complete E2E test project for the **Digital Twin Registry**
([`sldt-digital-twin-registry`](https://github.com/eclipse-tractusx/sldt-digital-twin-registry)).
It validates, compiles and passes in GitHub Actions, in about five and a half minutes.

[Home](../index.md) · Files: [`examples/digital-twin-registry`](https://github.com/eclipse-tractusx/tutorial-resources/tree/main/e2e-testing/examples/digital-twin-registry)

## The sentence it proves

> A partner can find and read a twin through the owner's connector, and a twin that
> does not exist is refused with "not found".

## What is in the folder

```text
examples/digital-twin-registry/
├── .tractusx                               the metafile, with the link to the test
├── .github/workflows/e2e-test.yml          the workflow: 12 lines that matter
└── tck/e2e/
    ├── index.yaml                          the manifest
    ├── schemas/shell_descriptor.json       the shape a twin must have
    └── tests/
        ├── register_and_read.yaml          positive + contract
        ├── partner_reads_through_connector.yaml   the journey
        └── unknown_twin_is_not_found.yaml  negative
```

## The three tests

| Test | Kind | What happens |
| --- | --- | --- |
| **Register and read** | Positive, contract | The owner registers a twin, reads it back, and both answers are checked against the JSON schema. The twin is removed. |
| **A partner reads through the connector** | Positive, the journey | Setup offers the registry in the owner's connector and registers a twin the partner may see. The test negotiates access, finds the twin by its part number and reads it. Everything is removed. |
| **An unknown twin is not found** | Negative | The registry is asked for a twin it never held and must answer 404. |

## What a run prints

```text
TCK RUN SUMMARY
  ✓ Register and read                          PASS
  ✓ A partner reads through the connector      PASS
  ✓ An unknown twin is not found               PASS
RESULT: PASS  |  3 passed  0 failed  0 skipped
```

## It failed first, and that was useful

The first run in CI was red:

```text
✗ partner-reads-through-connector[read_twin]  FAIL
  Schema validation failed (1 error(s)): submodelDescriptors: [] should be non-empty
```

The test had registered a twin without any pointer to data. The schema check caught it:
a twin with no submodel descriptor is of no use to a partner. The fix was one more setup
step, `create_submodel`.

That is what a contract check is for. A check on "status 200" would have stayed green.

## Read it in this order

1. [`index.yaml`](https://github.com/eclipse-tractusx/tutorial-resources/blob/main/e2e-testing/examples/digital-twin-registry/tck/e2e/index.yaml):
   look at `infrastructure`, then at `tests`.
2. [`unknown_twin_is_not_found.yaml`](https://github.com/eclipse-tractusx/tutorial-resources/blob/main/e2e-testing/examples/digital-twin-registry/tck/e2e/tests/unknown_twin_is_not_found.yaml):
   the smallest possible test, one step and one check.
3. [`partner_reads_through_connector.yaml`](https://github.com/eclipse-tractusx/tutorial-resources/blob/main/e2e-testing/examples/digital-twin-registry/tck/e2e/tests/partner_reads_through_connector.yaml):
   setup, execution, teardown.

## Run it yourself

With Python 3.12 or newer:

```bash
pip install "tractusx-testlab==1.0.0a24"
cd e2e-testing/examples/digital-twin-registry/tck/e2e
testlab validate index.yaml
testlab compile index.yaml -o /tmp/dtr-e2e.tck
testlab inspect /tmp/dtr-e2e.tck --variables --infrastructure
```

`validate` ends with `OK — index.yaml is valid (no issues)`. `inspect` lists 3 tests,
19 steps and 13 checks.

To **run** it you need two connectors and a registry. The
[workflow](workflow.md) creates them for you.

---

[Home](../index.md) · [The workflow →](workflow.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
