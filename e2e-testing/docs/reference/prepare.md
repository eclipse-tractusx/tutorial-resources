# Before you come

Ten minutes, best done **before** the workshop. Conference Wi-Fi is not the place to
install things, and the session has no slack for it.

[Home](../index.md)

## 1 · A browser

A current Chrome, Edge or Firefox. The IDE runs in it; nothing is installed.

## 2 · A GitHub account that may contribute to Eclipse

Your pull request goes to an Eclipse project. Two things must be true:

- [ ] You have an Eclipse account and have signed the
  [Eclipse Contributor Agreement](https://www.eclipse.org/legal/eca/) ([ECA](glossary.md#eca)).
- [ ] The email of your Git commits is the email of that Eclipse account.

Check the second one:

```bash
git config user.email
```

A different email is the usual reason the ECA check on a pull request turns red.

## 3 · Git that can push

```bash
git --version
```

Make sure a `git push` to one of your own GitHub repositories works from this laptop,
with a token or an SSH key. If you have never pushed from it, do it once now.

## 4 · A model for the assistant

You bring your own. No key is shared in the workshop. One of these is enough. Not sure which? See [Choose your AI option](ai-options.md).

| Option | What you need | Good to know |
| --- | --- | --- |
| **OpenAI** | An API key from the OpenAI developer platform | A ChatGPT subscription is not an API key. |
| **Anthropic** | An API key from the Anthropic console | A Claude subscription is not an API key. |
| **Catena-X AI** | Your Catena-X AI key and agent ID | For members who have access. |
| **Local or self-hosted** | A server with an OpenAI-compatible API, for example Ollama or LM Studio, and a model that supports tool calls | Nothing leaves your machine. The server must allow the IDE's address as an origin. With **Small model mode**, `qwen2.5:3b` runs on a laptop with 8 GB of RAM: see [AI options](ai-options.md#b-a-local-model-in-the-ide). Download it at home first. |

The key stays in your browser.

Only a subscription, such as Claude, ChatGPT or Copilot, and no API key? Then build with
your own assistant on your laptop: install it before you come, and see
[Set up your AI tool](ai-tool-setup.md). This route also needs step 5 below.

No model at all? Sit with a team that has one. You can also write the YAML by hand: the
[template](https://github.com/eclipse-tractusx/tutorial-resources/tree/main/e2e-testing/template)
and the [worked example](worked-example.md) show how.

## 5 · Optional: the command line

You do not need it in the workshop. The IDE compiles and runs for you, and GitHub Actions
does the same in CI. If you like a terminal, with Python 3.12 or newer:

```bash
pip install "tractusx-testlab==1.0.0a24"
testlab --help
```

You should see the commands `validate`, `compile`, `inspect` and `run`.

## Ready?

- [ ] Browser is current.
- [ ] ECA is signed, and `git config user.email` shows the same email.
- [ ] `git push` works from this laptop.
- [ ] You have a key or a local model for the assistant.

---

[Home](../index.md) · [Start the tutorial →](../tutorial/01-start-here.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
