# Where the AI helps

<span class="route">The story · 4 of 4 · optional reading, 3 minutes</span>

## You decide what. It writes how.

The assistant in the IDE knows the catalog of steps and the rules of the format.
It does not know what your product promises. That part is yours.

| You | The assistant |
| --- | --- |
| Say which promise must be proven | Picks the steps and wires them together |
| Judge whether a check can actually fail | Writes the YAML, file by file |
| Decide what a red result means | Runs the compiler and fixes what it reports |

## Three modes

| Mode | It does | Use it for |
| --- | --- | --- |
| **Ask** | Answers. Never changes files. | "Which steps read a twin through the connector?" |
| **Plan** | Lays out the changes. Never changes files. | Seeing whether it understood, before it writes anything |
| **Agent** | Changes your project files. Every change can be undone. | Building the test |

Start with **Plan**. A plan costs a few seconds and saves you from forty lines of YAML
that test the wrong thing.

## Three mistakes to look for, every time

| Mistake | How to spot it |
| --- | --- |
| **An invented step.** `uses:` names something that does not exist. | Compile reports it. |
| **A check that cannot fail.** For example `not_null` on a value that is always there. | Ask: what would the product have to do wrong for this to turn red? |
| **An environment written into the test.** An address or a BPN inside a test file. | Search the YAML for `http` and `BPNL`. |

None of these is a reason not to use the assistant. They are the reason you read what it wrote.

## Which model?

You bring your own. The IDE connects from your browser straight to the provider you pick:
OpenAI, Anthropic, Catena-X AI, or a server of your own (Ollama, LM Studio or any other
server with an OpenAI-compatible API).

- No key is shared in the workshop, and none is stored on the IDE's server.
- Larger models follow the step catalog more reliably. A small local model works too:
  **Small model mode** gives it short instructions and lets the IDE do the writing. See
  [AI options](../reference/ai-options.md).
- What you paste into the assistant goes to the provider you chose. Do not paste secrets.

---

[← What a test is made of](test-anatomy.md) · [Home](../index.md) · [Start the tutorial →](../tutorial/01-start-here.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
