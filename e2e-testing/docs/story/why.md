# Why we are here

<span class="route">The story · 1 of 4 · optional reading, 3 minutes</span>

## A release, three weeks, many people

Eclipse Tractus-X ships its products together, several times a year.
Before each release there is an **E2E testing phase**: typically three weeks, a daily
meeting, and one shared integration environment. People deploy every product there and
check, case by case, that the products still work together.

It works. It is also the most expensive way to find out that something broke.

| What happens today | What it costs |
| --- | --- |
| A change breaks the hand-over between two products | Nobody notices until the test phase, weeks later |
| The test phase finds it | Someone has to work out which of the changes since the last release did it |
| The fix arrives | The whole chain is tested again, by hand |

## Each product tests itself. Nobody tests the journey.

Every product has unit tests. Many have a Helm test that proves the product starts.
What is missing is the test a **partner** would write:

> "I am another company. I find your offer, I sign the contract, I ask for the data.
> Do I get it?"

That is an end-to-end test, an [E2E test](../reference/glossary.md#e2e-test).
It does not care how the product is built. It cares whether the promise holds.

## The idea

1. **Every product keeps one E2E test in its own repository.** The team that knows the
   product writes it. The test travels with the code.
2. **The test is written in one common format**, a [TCK](../reference/glossary.md#tck):
   plain YAML made of ready-made steps such as "create an asset" or "negotiate a contract".
   Nobody has to learn thirty test frameworks to read thirty tests.
3. **The test runs on every pull request**, in GitHub Actions, against a small dataspace
   that is created inside the runner and thrown away afterwards.
4. **The repository says where its test is**, in its `.tractusx`
   [metafile](../reference/glossary.md#tractusx-metafile), so tools can find it.

When every product has one, the three weeks before a release get a lot quieter:
a broken hand-over shows up as a red check on the pull request that caused it.

## Why one session is enough

Writing a test like this used to mean days of reading API documents.
Three things changed:

- **Ready-made steps.** [TestLab](../reference/glossary.md#testlab) already knows how to
  talk to a connector, a Digital Twin Registry and a wallet. You combine steps; you do not
  program them.
- **An AI assistant** that knows the step catalog and writes the YAML while you decide
  what must be proven.
- **A dataspace in a box.** The workflow brings up connectors, wallets and a registry by
  itself. You do not need a cluster.

## What we ask of you today

Write the **first** E2E test for your product. Not the complete one. One journey that
matters, one refusal that matters, running in CI and linked from `.tractusx`.

The second test is much easier than the first. That is the point of today.

---

[Home](../index.md) · [Next: A dataspace in five minutes →](dataspace.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
