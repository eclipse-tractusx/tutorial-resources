# Configure

What must exist in the Test Suite before the first team sits down.

[Home](../index.md) · [For administrators](index.md)

> [!IMPORTANT]
> The screens named here were checked on a local Test Suite without connectors, up to the
> **Run** button of the dialog. A run to green in a hosted Test Suite is **not verified**:
> it is the first item of the dry run. The same test project is verified in GitHub Actions.

## Per team

| Create | With | Why |
| --- | --- | --- |
| **An organization** | The team's name | Drafts, runs and registered components belong to it |
| **A user** | The account printed in box **B** | One per team is enough; the Driver signs in |
| **A plan with the IDE and enough parallel runs** | For every workshop organization | The IDE is not part of every plan, and twenty teams run within ten minutes |
| **An app** | The product's name and a version | The run dialog asks for the **App under test** |
| **A connector** | Name, **DSP URL**, **Participant ID (BPNL)** and **DID** of the team's provider connector | The run dialog asks for the **EDC Connector** |
| **A registry**, if the team's test uses one | Name and **Base URL** of the team's Digital Twin Registry | The run dialog asks for the **Digital Twin Registry** |

A team can register these itself: the run dialog offers **Register an app**,
**Register a connector** and **Register a registry**. Doing it for them saves two minutes of the ten in chapter 5.

## Three things to settle in the dry run

### 1 · The engine's own connector

The Test Suite's engine runs the tests from **its own** connector. A team cannot supply
that side: values in the `infrastructure.engine` namespace are refused. So:

- The engine needs a connector registered for the release the tests declare (`saturn`).
  Without it every run fails at once with "requires infrastructure that is not fully
  bound: engine.connector". This was seen in the dry run.
- That connector must be a participant of the **same dataspace** as the teams'
  connectors: same BDRS, same issuer, credentials issued.

If you give every team a pair, the second connector of each pair is the engine's connector
for that team's organization. Box **D** of the handout then only tells the team who the
partner is; nobody types it.

### 2 · Tests that prepare their own offers

The template and the worked example create an asset, two policies and a contract
definition on the team's connector in their `setup`, and remove them in `teardown`. That
needs the connector's **Management API address and key**.

The form **Register a connector** does not ask for them: in a hosted Test Suite the engine
normally only negotiates against a connector and never drives it. The engine's interface
for registering a connector does accept a management address and a key.

> [!WARNING]
> **Not run.** Decide in the dry run which of these holds for your deployment:
>
> - **A.** Register each team's connector with its management address and key, through the
>   engine's interface. The tests then run unchanged.
> - **B.** Create the template's asset, policies and contract definition on each team's
>   connector yourself, before the session. The **SUT Setup** step of the run dialog lists
>   exactly what is expected, with **Copy** and **Download** for each definition.
> - **C.** Skip the run in the IDE, and let every team see its green run in GitHub Actions
>   in chapter 7. This always works.

### 3 · Addresses must be public host names

The engine refuses a run when an input names a host that is not a fully qualified public
host name, for example `http://my-product:8080`. Every address on the handout, including
the two "as the connector reaches it", must therefore be a public name such as
`https://provider-dtr.team-01.example.org/api/v3`.

## The assistant

Nothing is configured for it on the server. Each participant connects a model in the
browser, and the key stays there.

| Option in the IDE | What must be true on your side |
| --- | --- |
| A **GPT** or **Claude** model | The venue network lets browsers reach the provider |
| **Catena-X AI** | Participants who use it have their key and agent ID |
| **Local / self-hosted LLM** | The deployed IDE includes the option. A participant's own server must allow the IDE's address as an origin; the IDE's address must be `https://`. |

## After the event

- [ ] Delete the workshop organizations, users and their drafts.
- [ ] Remove the teams' connector registrations.
- [ ] Tear down the connector pairs.
- [ ] Destroy the real connector inventory and the printed handouts: they hold keys.

---

[← Deploy](deploy.md) · [Home](../index.md) · [Assign products →](assign.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
