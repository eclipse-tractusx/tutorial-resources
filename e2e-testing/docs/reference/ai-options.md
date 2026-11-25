# Choose your AI option

There are three ways to get AI help for your test. Pick the one that fits your laptop
and what you already have. All of them end the same way: a [TCK project](glossary.md#tck-project)
that compiles in the IDE.

[Home](../index.md)

## At a glance

| Option | You need | Speed | Quality | Best for |
| --- | --- | --- | --- | --- |
| **A · API key** in the IDE | A key from OpenAI, Anthropic or Catena-X AI | Seconds | Best | Most people |
| **B · Local model** in the IDE | A model server on your laptop (Ollama, LM Studio) | Depends on your laptop | Good for small, clear changes | No key, or nothing may leave your machine |
| **C · Your own AI tool**, then upload | Claude Code, Copilot, Cursor or Codex on your laptop | Seconds | Best | People who already work with such a tool |

## A · An API key in the IDE

1. Open the IDE and press **Ask AI**.
2. Pick the provider and paste your key.

- A **subscription** (ChatGPT, Claude, Copilot) is not an API key. With only a
  subscription, use option **C**.
- The key stays in your browser. What you type goes to the provider you picked.

## B · A local model in the IDE

The model runs on your own laptop. Nothing leaves your machine, and nothing costs money.
In the IDE, pick **Local / self-hosted LLM** and keep **Small model mode** on.

### What Small model mode does

A local model is slow to read long instructions and gets lost in them. Small model mode
gives it a short version and lets it look things up when it needs them:

| | Normal mode | Small model mode |
| --- | --- | --- |
| Instructions sent before your question | about 29,000 tokens | about 3,000 tokens |
| First answer, 3B model, our test laptop | 7 minutes | 17 seconds |
| How it changes a file | Rewrites the whole file | Uses one IDE action per change, such as **add step**, **add check** or **set value**. The IDE writes the YAML and compiles. |
| How it answers a question | Reads the files you have open | The IDE reads the project first and gives the model the overview |

### Which size fits your laptop?

| Model size | Example | Download | Memory it needs | Laptop | What to expect |
| --- | --- | --- | --- | --- | --- |
| **3B** | `qwen2.5:3b` | 2 GB | about 3 GB | 8 GB RAM or more | Answers questions. Makes one clear change per message: rename, add or remove a step or a check, add a variable. |
| **7B** | `qwen2.5:7b` | 5 GB | about 6 GB | 16 GB RAM | The same changes, and longer explanations. |
| **14B** | `qwen2.5:14b` | 9 GB | about 11 GB | 32 GB RAM, or a GPU with 12 GB | Not tested by us. |
| **30B and more** | `qwen3:30b` | 18 GB or more | 20 GB or more | 32–64 GB RAM on Apple Silicon, or a GPU with 24 GB | Not tested by us. Closer to option A, if your machine can run it. |

Rules of thumb:

- **Apple Silicon (M1 to M4) or a gaming GPU:** answers in seconds.
- **A laptop without a GPU:** expect a minute or more per step. Prefer option A or C.
- Pick a model that supports **tool calls**. Without them the assistant can answer but
  cannot change files.

### What we aim for, and the limits

We aim for this: with a 3B model on an ordinary laptop, you can ask, plan, and make the
small changes the tutorial needs. In our last trial, on an Apple M3 laptop, `qwen2.5:3b`
and `qwen2.5:7b` each did all nine of these, in 5 to 20 seconds each:

- rename the test, and rename a step
- add a check, add a step, remove a step, and copy a step
- add a variable
- answer "Which tests and steps does this project have?" and "Which values can a new
  step use?"

That was one run per model. Earlier runs, before the IDE took over more of the work,
got about one change in ten wrong. Every change is checked and can be undone, so a wrong
one costs you a click.

The limits:

- **Small models make small changes.** Ask for one change at a time. "Write the whole
  test" is too much for them: start from the template instead.
- **Each question stands alone.** In Small model mode the assistant does not see earlier
  questions. Name the file in every question, for example `In tests/my_test.yaml …`.
- **Always read what it changed.** Every change is listed above the message box, with
  **Review** and **Undo**.
- If it keeps failing, switch to option A or C. That is not your fault: it is the size
  of the model.

### Set it up

1. Install [Ollama](https://ollama.com).
2. Download a model and start the server so that the IDE may call it. Replace the address
   with the address of the IDE:

    ```bash
    ollama pull qwen2.5:3b
    ```

    ```bash
    OLLAMA_ORIGINS="https://ide.example.org" OLLAMA_CONTEXT_LENGTH=8192 ollama serve
    ```

3. In the IDE: **Ask AI** › **Local / self-hosted LLM** › **Connect**. Pick your model.

> [!NOTE]
> Small model mode is new. If the IDE you use does not show the **Small model mode**
> switch yet, a local model needs 64,000 tokens of context and a fast machine, and the
> first answer takes minutes. Then take option A or C.

## C · Your own AI tool, then upload to the IDE

You build the test on your laptop with the AI tool you already use, then open it in the
IDE to compile, run and inspect it.

1. Set up the agent kit for your tool: [Set up your AI tool](ai-tool-setup.md).
2. Ask your tool for the test, in your product's repository.
3. Check it on your laptop:

    ```bash
    testlab validate tck/e2e
    ```

4. In the IDE: open the folder or upload the ZIP of `tck/e2e`, then press **Compile**
   and **Run**.

The full route is chapter [3C · Your own AI tool](../tutorial/03-alternative-your-own-ai-tool.md).

## No AI at all?

Sit with a team that has one, or change the template by hand. The template already passes;
renaming it to your product is a good first test.

---

[← Set up your AI tool](ai-tool-setup.md) · [Home](../index.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
