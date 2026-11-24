# Instructions for AI coding assistants

You are asked to write or review an end-to-end test for an Eclipse Tractus-X product.
The test is a TCK project for Tractus-X TestLab: YAML files, no test code.

1. Read [skills/tractusx-e2e-test/SKILL.md](skills/tractusx-e2e-test/SKILL.md), next to
   this file, and follow it. It tells you which other files to read and when.
2. Start from the template in
   [skills/tractusx-e2e-test/assets/template/](skills/tractusx-e2e-test/assets/template/index.yaml).
3. Use only steps that `testlab docs -o -` lists. Never invent a step, an input or an output.
4. Run `testlab validate` after every file you write, and fix only what it reports.
5. You cannot run the test: a run needs two connectors. Say so when you hand over.

If you cannot run commands, ask the person to run `testlab validate tck/e2e/index.yaml`
and paste the output back to you.

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
