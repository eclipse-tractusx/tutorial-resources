<span class="route">Getting Started · chapter 2 of 9 · ● ● ○ ○ ○ ○ ○ ○ ○</span>

# 2 · Your product and your connectors

> **Goal:** you know your product, your two connectors and the scenario your test will prove.
> **Time:** 5 minutes

## Steps

1. Open your product's repository (handout, box **A**) and read in its README
   what the product does.
2. Press **Fork** at the top right of the repository page, then **Create fork**.
   Leave the tab open; you need the [fork](../reference/glossary.md#fork-and-pull-request)
   in chapter 7.
3. Find your product in the [product catalog](../reference/catalog.md) and read its
   suggested scenario. It is the same as on the handout, box **A**: what a partner can
   do with your product, and what is refused. Your test proves both.

    Does your product have an [E2E spec](../specs/index.md)? Then read it instead: it
    says what each test must prove, which steps to use and what the finished TCK looks
    like. You don't need to work out the product's API yourself.
4. Look at boxes **C** and **D** of the handout. They are your two
   [connectors](../reference/glossary.md#connector):

    | Box | Plays | In the test it is called |
    | --- | --- | --- |
    | **C · Your side** | Your company, with your product | [SUT](../reference/glossary.md#sut), the system under test |
    | **D · Partner side** | Another company that uses your product | [Engine](../reference/glossary.md#engine), where the test runs from |

## You should now see

Your fork on GitHub, named `<your-user>/<your-product>`.

![The Fork button at the top right of a repository page on GitHub](../assets/screenshots/02-fork-button.png)

<details>
<summary>The pilot team's scenario</summary>

The pilot team got the **Digital Twin Registry**. Their scenario: a partner finds and
reads a twin through the owner's connector, and a twin that does not exist is refused
with "not found".

Their finished project is the [worked example](../reference/worked-example.md).

</details>

> [!TIP]
> **Stuck?**
>
> - **The scenario has no refusal:** "something that was never offered is not found" is
>   always true and always worth checking. The template starts with exactly that.
> - **Your product is not behind a connector:** then the partner calls its API directly.
>   The scenario stays the same; say so to the assistant in chapter 5.
> - **Your product is marked "needs more than the workshop environment" in the catalog:**
>   you build and compile today, and skip the run in chapter 6. Tell a facilitator.

---

[← 1 · Start here](01-start-here.md) · [Home](../index.md) · [Next: 3 · Hello, TestLab →](03-hello-testlab.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
