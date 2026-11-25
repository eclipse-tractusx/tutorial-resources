# Troubleshooting

Find the symptom, try the fix, raise your hand if it does not help.

[Home](../index.md)

## The assistant

| Symptom | Try |
| --- | --- |
| It says the key is not valid | Copy the key again, without spaces. A ChatGPT or Claude **subscription** is not an API key. |
| A local model cannot be reached | The server must allow the IDE's address as an origin (CORS), and the address must end in `/v1`. |
| A local model answers as if it never heard of TCKs | Switch on **Small model mode** in the assistant's settings. Without it, the IDE sends about 29,000 tokens before your first question, and the model needs 64,000 tokens of context. |
| A local model "thinks" for minutes and never answers | Switch on **Small model mode**: the first answer then takes about 20 seconds instead of 7 minutes. On a laptop without a GPU it can still take a minute or more. Then use a hosted model, or the [own AI tool](../tutorial/03-alternative-your-own-ai-tool.md) route. |
| A local model changes the wrong thing | Ask for one change per message, and name the file and the step, for example `In tests/my_test.yaml add a check to the step find_offer: datasets is not null`. Undo the wrong change above the message box. |
| It invents steps | Say: `Use only steps from the block catalog.` Smaller models need the reminder. |
| It stops halfway | Say: `Continue.` Or ask for one test at a time. |
| It changed files you did not want changed | Press **Undo all**, switch to **Plan**, ask again in smaller steps. |

## Compile

| Symptom | Try |
| --- | --- |
| **Compile** stays red after three rounds | Switch to **Ask**, paste the finding, and ask what the compiler expects there before letting the assistant edit again. |
| "unknown step" | The `uses:` name does not exist. Ask the assistant which step from the catalog does that job. |
| A reference cannot be resolved | A `${{ ... }}` points at a step or variable that is not there. Check the spelling of the step `id`. |
| Everything is red and you are out of time | Open the template again and change only the names. A small green test beats a large red one. |

## Run

| Symptom | Try |
| --- | --- |
| The run fails at once: "requires infrastructure that is not fully bound: engine.connector" | The connector the tests run from is not set up for your organization. That is the administrators' part: go to the Desk. |
| The run waits and nothing happens | Give it a minute. Then ask at the Desk whether your connectors are up. |
| The first step fails with a connection error | A value from the handout is mistyped. Compare box **C** and box **D** character by character. |
| The negotiation is refused | The usage policy of your test asks for something the partner cannot prove. Use the policies of the template unchanged first. |
| The data plane answers 502 | The address "as the connector reaches it" is wrong or not reachable from the connector. |
| A step is red and you cannot tell why | Open the step and read the request and the answer. Decide: is it the environment, or is it a finding? |

## Git and GitHub

| Symptom | Try |
| --- | --- |
| `git push` asks for a password and refuses it | GitHub needs a token or an SSH key, not your password. A teammate whose laptop can push can be the Driver. |
| The ECA check on the pull request is red | The email of your commit is not the one of your Eclipse account. Fix it with `git config user.email`, then `git commit --amend -s --reset-author` and `git push --force-with-lease`. |
| The checks say "waiting for approval" | Normal for a first contribution to a repository. A committer of the product has to approve the run. |
| You want to see the workflow run without waiting | Open a second pull request **inside your fork**, from your branch to your fork's `main`. It runs at once. |
| The workflow cannot find `e2e-test.yml@main` | The reusable workflow is not published yet. Ask a facilitator for the reference to use. |

## The workflow is red

| Where | Meaning |
| --- | --- |
| **Validate and compile** | The project in the repository is not the one that compiled in the IDE. Did all files arrive? Check `tck/e2e/index.yaml` and `tck/e2e/tests/`. |
| A deploy step of **Run in Eclipse Tractus-X developer toolbox** | The environment, not your test. Run the job again. |
| **testlab run** | A finding, or a value your test expects that the developer toolbox does not provide. Read **E2E test result** on the run page. |

---

[← The .tractusx link](metafile.md) · [Home](../index.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
