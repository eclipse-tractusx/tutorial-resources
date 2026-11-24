# One green check for every product

**7th Eclipse Tractus-X Community Days · 3–4 December 2026 · ARENA2036, Stuttgart**

In about an hour your team builds an end-to-end test for one Eclipse Tractus-X product,
with an AI assistant doing the typing and you doing the thinking. You leave with a
pull request in that product's repository and a test that runs on every change.

No test framework to learn. No code to write. A laptop and curiosity are enough.

[Start the tutorial](tutorial/01-start-here.md){ .md-button .md-button--primary }
[New here? Read the story first](story/why.md){ .md-button }

## The problem we solve together

Tractus-X has about thirty products. Each one tests itself.
Whether they work **together**, the way a partner uses them, is checked by people,
by hand, in the three weeks before a release.

That is late, and it does not scale. So here is the plan:

> Every product keeps an [E2E test](reference/glossary.md#e2e-test) in its own repository.
> It is written in one common, tool-independent format, a [TCK](reference/glossary.md#tck).
> It runs in GitHub Actions on every pull request.

Today you write the first one for your product.

## Your route

```mermaid
flowchart LR
    A["1 Start"] --> B["2 Your product"] --> C["3 Choose your AI"] --> D["4 Build"]
    D --> E["5 Run"] --> F["6 Repository"] --> G["7 Pull request"] --> H["8 Next"]
```

| | Chapter | Minutes | At the end you have |
| --- | --- | --- | --- |
| 1 | [Start here](tutorial/01-start-here.md) | 5 | Your handout, and the mission |
| 2 | [Your product and your connectors](tutorial/02-your-product.md) | 5 | One sentence that says what you will prove |
| 3 | [Choose your AI](tutorial/03-choose-your-ai.md): [A · API key](tutorial/03-open-the-ide.md), [B · local model](tutorial/03b-local-model.md) or [C · your own AI tool](tutorial/03-alternative-your-own-ai-tool.md) | 5–10 | An assistant that answers |
| | *Route C builds on your laptop and replaces chapter 4* | *20* | |
| 4 | [Build the test with the assistant](tutorial/04-build-the-test.md) | 15 | A test project that compiles |
| 5 | [Run it against your connectors](tutorial/05-run-it.md) | 10 | A green run, for the right reasons |
| 6 | [Add it to your repository](tutorial/06-add-to-repository.md) | 10 | The test and its workflow, committed |
| 7 | [Link it and open the pull request](tutorial/07-link-and-pull-request.md) | 5 | A pull request with a green check |
| 8 | [What comes next](tutorial/08-what-comes-next.md) | 5 | A plan for the second test |
| | | **about 60** | |

The minutes are a guide for a team that has done it before. A first-time reader in our dry
run needed closer to 90: if your session is longer, spend the extra time on chapters 4 and 5.

## Where to start

| You are | Go to |
| --- | --- |
| A participant, ten minutes before the workshop | [Before you come](reference/prepare.md) |
| A participant, and the clock is running | [1 · Start here](tutorial/01-start-here.md) |
| Curious about the words on the slides | [Glossary](reference/glossary.md) and [the story](story/why.md) |
| An administrator of the session | [For administrators](admin/index.md) |

## What you work with

| Thing | What it is |
| --- | --- |
| **TestLab** | The open-source test engine of Eclipse Tractus-X. It reads a TCK and runs it. |
| **A TCK project** | A folder of YAML files that describes your test. This is what you commit. |
| **The Test Suite IDE** | A browser tool for building TCK projects with blocks and an AI assistant. One way to get there faster; the files it produces are plain YAML you could also write by hand. |
| **A reusable workflow** | A few lines of GitHub Actions that validate, compile and run your test. |

The IDE in this workshop is provided by the Catena-X Association. The TCK format and
TestLab are not tied to one dataspace: the same approach works for tests of other
dataspaces and other standards.

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
