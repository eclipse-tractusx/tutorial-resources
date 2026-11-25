---
name: tractusx-e2e-test
description: Write, extend or review an end-to-end (E2E) test for an Eclipse Tractus-X product as a TCK project for Tractus-X TestLab (index.yaml plus YAML test files), and get it to validate and compile. Use when the user asks for an E2E test, a TCK, a TestLab test, or a test of a connector, Digital Twin Registry, wallet or another dataspace product.
---

# E2E test for a Tractus-X product (TCK project)

You help a team write the first E2E test for one Eclipse Tractus-X product. The test is a
**TCK project**: a folder with a manifest `index.yaml` and one YAML file per test. Eclipse
Tractus-X TestLab validates, compiles and runs it. You write YAML, never test code.

The human decides **what** must be proven. You write **how**, and you prove with the
compiler that what you wrote is well-formed.

## Before you write anything

1. Ask for, or read from the conversation, these three things. Do not guess them.
   - The product and its repository.
   - One sentence: "A partner can ..., and ... is refused."
   - Where the project goes. Default: `tck/e2e` in the product's repository.
2. Check the tools:

   ```bash
   testlab --help
   ```

   If the command is missing: `pip install "tractusx-testlab==1.0.0a24"` (Python 3.12 or newer).
3. Generate the step catalog once and keep it out of the repository:

   ```bash
   testlab docs -o - > /tmp/tck-steps.md
   ```

   It lists every step a test may name in `uses:`, with its inputs and outputs. It is
   long. Search it, do not read it top to bottom:

   ```bash
   grep -n '^### `' /tmp/tck-steps.md              # every step
   testlab docs -s connector/consumer/negotiate -o -   # one step in full
   ```

## How to work

1. **Start from the template**, [assets/template/](assets/template/index.yaml) next to this file. Copy it to the
   project folder if it is not there yet. It validates and compiles as it is.
2. **Plan first, in plain words.** List the tests, and for each check say what the product
   would have to do wrong for it to turn red. Wait for the human to agree.
3. **Rename first, in one go.** Change the manifest's `id` and the `namespace` of every
   test file together. In between, `testlab validate` is red: the template's tests still
   carry the old name. That is expected.
4. **Write one test at a time.** After each file:

   ```bash
   testlab validate tck/e2e/index.yaml
   ```

   Fix what it reports and nothing else. Repeat until it prints
   `OK — index.yaml is valid (no issues)`.
5. **Check every input by hand.** The validator catches an unknown step, an unknown
   output and a broken reference. It does **not** catch an unknown input under `with:`:
   with a misspelt or invented input, validate and compile still pass. Compare each
   `with:` key with the step's page:

   ```bash
   testlab docs -s <the step's uses value> -o -
   ```
6. **Compile and inspect** when all tests are in:

   ```bash
   testlab compile tck/e2e/index.yaml -o /tmp/e2e-test.tck
   testlab inspect /tmp/e2e-test.tck --variables --infrastructure
   ```

   Show the human the `inspect` output. It is what a run will need.
7. **Hand over.** You cannot run the test: a run needs two connectors. Say so. The human
   runs it in the Test Suite IDE or in GitHub Actions.

Read these when you need them:

| File | When |
| --- | --- |
| [reference/syntax.md](reference/syntax.md) | Before you write or change any YAML |
| [reference/patterns.md](reference/patterns.md) | To pick the shape of a test: product behind a connector, Digital Twin Registry, a refusal |
| [reference/review.md](reference/review.md) | Before you say you are done, and when asked to review a project |
| [assets/example/index.yaml](assets/example/index.yaml) and its `tests/` | A complete project for the Digital Twin Registry that passes in CI |

## Rules

- **Only steps from the catalog.** If `/tmp/tck-steps.md` does not list it, it does not
  exist. Never invent a `uses:` value, an input name or an output name. The catalog covers
  steps only: the three kinds of **variable** (`variable/type/string`,
  `config/connector/asset`, `config/connector/policy`) are in `reference/syntax.md`.
- **Say when the kit does not cover the product.** If no pattern fits, or you cannot tell
  whether a green run would really exercise the product, say that before you write, and
  propose the closest honest test.
- **Nothing from an environment in a file.** No addresses, no BPNs, no DIDs, no keys.
  Use `${{ infrastructure.* }}` references or declare an input variable.
- **Two sides.** `sut` is the product and the connector in front of it. `engine` is the
  partner the test runs from. A test acts as the partner.
- **Every test checks a value the product returned.** "It answered" is not a check.
- **At least one positive and one negative test.** The negative test names the exact
  answer that is expected, for example status 404 or an empty list.
- **Clean up.** What `setup` or `execution` creates, `teardown` removes. A test must pass
  twice in a row.
- **Keep the licence header** at the top of every YAML file, and `license: Apache-2.0`
  in the manifest.
- **Do not weaken a check to make the compiler happy.** Fix the reference instead.
- **Say what you did not verify.** You validated and compiled. You did not run.

## Done means

- `testlab validate` reports no issues and `testlab compile` writes a package.
- Every item in `reference/review.md` is answered.
- You told the human, in three lines: what each test proves, what a run needs, and that
  the run is theirs to do.

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
