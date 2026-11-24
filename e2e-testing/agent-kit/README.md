# Agent kit

Teaches an AI coding assistant to write an E2E test as a TCK project, so that you can
build the test on your laptop with the assistant you already have, and open the result in
the IDE to run it.

How to use it, step by step: the tutorial page
[Build with your own AI tool](../docs/tutorial/03-alternative-your-own-ai-tool.md).

## What is in it

| Path | What | For |
| --- | --- | --- |
| [`skills/tractusx-e2e-test/`](skills/tractusx-e2e-test/SKILL.md) | An Agent Skill: the way of working, the syntax, the patterns, a review checklist, the template and the worked example | Assistants that load skills, such as Claude Code |
| [`agents/e2e-test-author.md`](agents/e2e-test-author.md) | An agent definition that uses the skill | Claude Code |
| [`AGENTS.md`](AGENTS.md) | The same instructions in five lines, pointing at the skill | Any assistant that can read files |

## Install

For Claude Code, from the folder that holds this README:

```bash
mkdir -p ~/.claude/skills ~/.claude/agents
cp -r skills/tractusx-e2e-test ~/.claude/skills/
cp agents/e2e-test-author.md ~/.claude/agents/
```

For any other assistant, start your first message with:

```text
Read <path to this folder>/AGENTS.md and follow it.
```

## How it was checked

| Check | Result |
| --- | --- |
| The template and the example inside the skill | Validate and compile with TestLab 1.0.0a24, and pass in GitHub Actions |
| The commands in `SKILL.md` | Run as written |
| The skill, used by an assistant with no other knowledge | It built a two-test project for the BPN-DID Resolution Service that validates and compiles, in three rounds. Its eleven findings are fixed in the kit. The project was not run. |
| Assistants other than Claude Code | **Not tried** |

## Keep it in sync

`assets/` inside the skill is a copy of `template/tck/e2e` and
`examples/digital-twin-registry/tck/e2e`. After changing either, run:

```bash
python tools/build_agent_kit.py
```

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
