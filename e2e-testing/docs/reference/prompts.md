# Starter prompts

Paste them into **Ask AI** in the IDE. Replace the parts in `<angle brackets>`.
Each prompt says which mode to use: **Ask**, **Plan** or **Agent**.

[Home](../index.md) · [Where the AI helps](../story/ai.md)

## 1 · Plan the test

Mode: **Plan**. Use it in [chapter 4](../tutorial/04-build-the-test.md).

```text
I am building the first E2E test for the Eclipse Tractus-X product
"<product name>" (<repository URL>).

What it must prove, in one sentence:
"<A partner can ..., and ... is refused.>"

The open project is a template. Plan how to turn it into this test:
- Keep the two-sided shape: my product is behind the "sut" connector, the
  test runs from the "engine" connector as a partner would.
- One positive test for the first half of the sentence, one negative test
  for the second half.
- Use only steps from the block catalog. Do not invent step names.
- Every check must be able to fail: say for each one what the product would
  have to do wrong for it to turn red.
- Addresses, BPNs and keys never go into a test file. Use variables or
  infrastructure references.
- Everything a test creates is removed again in teardown.
- Rename every "my-product" and "My Product" in the template to
  "<product-id>" and "<product name>".

Do not write files yet. Ask me what you do not know.
```

## 2 · Fix what the compiler reports

Mode: **Agent**.

```text
Compile the project and fix what the compiler reports. Change nothing else.
```

## 3 · Add a refusal

Mode: **Agent**.

```text
Add one more negative test: <what must be refused, and how the product
says so, e.g. "a request without a token is answered with 401 or 403">.
Use only steps from the block catalog. Register the test in index.yaml.
Then compile and fix what the compiler reports.
```

## 4 · Check the shape of an answer

Mode: **Agent**. Have the JSON schema ready, or the link to it.

```text
Add a JSON schema check to the step "<step name>": the answer must match
this schema. Put the schema in the project's schemas folder, register it in
index.yaml and reference it from the step.

<paste the schema, or its URL>
```

## 5 · Let it play the Skeptic

Mode: **Ask**.

```text
Review the open project like a maintainer who has to trust a red result.
For every check: can it fail, and what would a failure tell me?
List the checks that can never fail, and the things the tests assume but
do not check. Do not change anything.
```

## 6 · Start from an OpenAPI document

Mode: none, this is a menu. For a product whose API a partner calls directly.

1. Choose **File**, then **Import OpenAPI…**.
2. Under **Spec source**, choose **URL** and paste the link to your product's OpenAPI
   document. The [catalog](catalog.md) tells you whether your product publishes one.
3. Under **Test cases**, keep **Success response** and add at least one refusal, for
   example **Unknown id → 404**.
4. Press **Translate**. Nothing changes in your project until you apply the result.

Then check the result with prompt 5.

## 7 · Write the pull request text

Mode: **Ask**.

```text
Write the description for a pull request that adds this E2E test to
<repository>. Three short parts: what the test proves (one sentence per
test), what a run needs, and how it runs in CI (the workflow
.github/workflows/e2e-test.yml). Plain Markdown, no headings above level 2.
```

## When the assistant goes wrong

| It does this | Say this |
| --- | --- |
| Invents a step | `That step does not exist. Use only steps from the block catalog.` |
| Writes an address into a test | `No addresses in test files. Use an infrastructure reference or a variable.` |
| Checks only "not null" | `That check cannot fail. Check the value the product promises.` |
| Rewrites files you did not ask for | Press **Undo all**, switch to **Plan**, and ask again in smaller steps. |

> [!NOTE]
> How the prompts were checked is stated in the pull request that added this page.
> A model may answer differently on your day: the plan is yours to judge.

---

[Home](../index.md) · [The worked example →](worked-example.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
