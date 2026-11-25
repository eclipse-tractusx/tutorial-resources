# Review checklist

Go through it before you say you are done, and when you are asked to review a project.
Answer every item with yes, no or not applicable, and name the file and step for every no.

## Each test

- [ ] It states a promise of the product in its `metadata.description`.
- [ ] It acts as the partner: it reaches the product the way a consumer does.
- [ ] It has at least one check on a value the product returned.
- [ ] For each check you can say what the product would have to do wrong to fail it.
- [ ] No check is `not_null` on a value that is always there.
- [ ] Everything created in `setup` or `execution` is removed in `teardown`.
- [ ] It can run twice in a row: identifiers are fixed and removed, or generated per run.

## The project

- [ ] At least one positive and one negative test.
- [ ] The negative test expects one exact answer.
- [ ] No address, BPN, DID, key or token of an environment in `tests/`, or as a value in
  the manifest. Search for `BPNL`, `did:` and `http`. Three kinds of hit are fine: the
  licence header, namespace constants such as `https://w3id.org/...`, and an example inside
  a variable's `description`.
- [ ] Every `uses:` value of a step is in the step catalog.
- [ ] Every key under a step's `with:` is an input the catalog lists for that step. The
  validator does not check this.
- [ ] `infrastructure` declares what the tests use, and nothing more.
- [ ] Every input variable has a description with an example value.
- [ ] `metadata.license` is `Apache-2.0`, and every YAML file starts with the licence header.
- [ ] No placeholder is left: search for `my-product`, `My Product` and `TODO`.

## The proof

- [ ] `testlab validate` reports no issues.
- [ ] `testlab compile` writes a package.
- [ ] `testlab inspect` lists the tests, variables and infrastructure you intended.
- [ ] You said plainly that the test has not been run yet.

## Three mistakes assistants make

| Mistake | How to find it |
| --- | --- |
| An invented step or output | `testlab validate` reports it. Look the step up in the catalog. |
| An invented or misspelt input | **Validate and compile do not report it.** Compare every `with:` key with `testlab docs -s <step> -o -`. |
| A check that cannot fail | Read each check and ask what would turn it red. |
| An environment written into a test | Search `tests/` for `BPNL`, `did:` and `http`. |

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
