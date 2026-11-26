# Set up your AI tool

How to give your own AI coding assistant the **agent kit**, so that it can write a
[TCK project](glossary.md#tck-project). You need this only for the
[route C](../tutorial/03-alternative-your-own-ai-tool.md), when you build on
your laptop instead of with the assistant in the IDE.

[Home](../index.md) · Files: [`agent-kit`](https://github.com/eclipse-tractusx/tutorial-resources/tree/main/e2e-testing/agent-kit)

## What the kit is

| Part | What it does |
| --- | --- |
| **A skill**, `tractusx-e2e-test` | Instructions your assistant loads when you ask for an E2E test: the way of working, the syntax, the patterns, the template, the worked example and a review checklist |
| **An agent**, `e2e-test-author` | A ready-made test author that uses the skill. Claude Code only. |
| **`AGENTS.md`** | The same instructions in five lines, for tools without skills |

The skill follows the open Agent Skills format: a folder with a `SKILL.md`. Several
assistants read it. They differ only in **where** they look for it.

## First, for every tool

```bash
pip install "tractusx-testlab==1.0.0a24"
git clone --depth 1 https://github.com/eclipse-tractusx/tutorial-resources.git
cd tutorial-resources/e2e-testing/agent-kit
```

Then pick your tool.

## Claude Code

Terminal, the VS Code extension and the desktop app share one setup.
A Claude subscription is enough: no API key.

1. Install the skill and the agent:

    ```bash
    ./install.sh claude
    ```

    It copies the skill to `~/.claude/skills/tractusx-e2e-test` and the agent to
    `~/.claude/agents/e2e-test-author.md`.

2. Open your product's repository in Claude Code and start a new session.
3. Type `/` and check that `tractusx-e2e-test` is in the list.
4. Ask for your test. Start with `Use the tractusx-e2e-test skill.` or, to hand the whole
   job to the agent, with `Use the e2e-test-author agent.`

## GitHub Copilot in VS Code

1. Install the skill:

    ```bash
    ./install.sh copilot
    ```

    It copies the skill to `~/.copilot/skills/tractusx-e2e-test`.

2. Open your product's repository in VS Code and open the Chat view.
3. Choose the mode in which Copilot may edit files and run terminal commands.
4. Type `/` and check that `tractusx-e2e-test` is in the list. Then ask for your test.
5. Allow Copilot to run `testlab validate` when it asks.

## Cursor

1. Install the skill:

    ```bash
    ./install.sh cursor
    ```

    It copies the skill to `~/.cursor/skills/tractusx-e2e-test`. Cursor also reads
    `~/.claude/skills`, so `./install.sh claude` works too.

2. Open your product's repository in Cursor and start a new Agent chat.
3. Type `/` and check that `tractusx-e2e-test` is in the list. Then ask for your test.

## OpenAI Codex

1. Install the skill:

    ```bash
    ./install.sh codex
    ```

    It copies the skill to `~/.agents/skills/tractusx-e2e-test`.

2. Start Codex in your product's repository.
3. Type `/skills` and check that `tractusx-e2e-test` is listed. Then ask for your test;
   `$tractusx-e2e-test` names the skill in a message.

## Any other assistant that can read files

No installation. Start your first message with the path to the kit:

```text
Read ../tutorial-resources/e2e-testing/agent-kit/AGENTS.md and follow it.
```

If your tool only reads files inside the open folder, copy the kit into your repository
first and keep it out of Git:

```bash
cp -R ../tutorial-resources/e2e-testing/agent-kit .agent-kit
echo ".agent-kit/" >> .git/info/exclude
```

Then write `Read .agent-kit/AGENTS.md and follow it.`

## A chat app without file access

ChatGPT, Claude or another chat in the browser. It works, with you as the hands.

1. Upload these files from `agent-kit/skills/tractusx-e2e-test` to the chat: `SKILL.md`,
   the three files in `reference/`, and the three YAML files in `assets/template/`.
2. Generate the step catalog and upload it too:

    ```bash
    testlab docs -o - > tck-steps.md
    ```

3. Send the prompt from the [route C](../tutorial/03-alternative-your-own-ai-tool.md), step 5.
4. Save each file the chat gives you under `tck/e2e` in your repository.
5. Run `testlab validate tck/e2e/index.yaml` and paste the output back. Repeat until it
   prints `OK`.

## How to tell it worked

Ask your assistant, before anything else:

```text
Which steps does a TCK test have for reading a twin from a Digital Twin Registry through a connector?
```

A good answer names real steps such as
`digital-twin-registry/consumer/dataplane/get_shell_descriptor`, and says it looked them
up with `testlab docs`. An answer with invented names means the skill was not loaded: start
a new chat, or use the `AGENTS.md` line above.

## What was tried

| Tool | Status |
| --- | --- |
| The install script, all four targets | Run. It copies to the folders named above. |
| The skill, followed by an assistant with no other knowledge | Run. It built a two-test project for the BPN-DID Resolution Service that validates and compiles. That project was not run against connectors. |
| Claude Code loading the skill and the agent from those folders | **Not tried.** The folders are the ones its documentation names. |
| GitHub Copilot in VS Code, Cursor, Codex | **Not tried.** The folders and the commands are the ones each tool's documentation names. Menu names may differ in your version. |
| A chat app without file access | **Not tried.** |

## Remove it again

Delete the folder the install script named, for example:

```bash
rm -rf ~/.claude/skills/tractusx-e2e-test ~/.claude/agents/e2e-test-author.md
```

---

[← Prerequisites](prepare.md) · [Home](../index.md) · [Route C →](../tutorial/03-alternative-your-own-ai-tool.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
