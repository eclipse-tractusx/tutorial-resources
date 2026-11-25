# Deploy

What has to run at the venue, in which order, and how to know it works.

[Home](../index.md) · [For administrators](index.md)

> [!IMPORTANT]
> This page describes **what** to bring up and how to check it. The connector pair recipe
> is the one the workflow runs in GitHub Actions, where it is proven. The deployment of the
> Test Suite itself follows its operators' own runbook, which is not public. Nothing on
> this page has been run at ARENA2036 yet: do the dry run.

## The pieces

| Piece | What it is | Who reaches it |
| --- | --- | --- |
| **Test Suite** | The web application with the IDE, behind a sign-in | Participants' browsers |
| **Engine** | The backend that compiles and runs TCKs. It uses [TestLab](../reference/glossary.md#testlab). | The Test Suite; it calls the connectors |
| **Identity provider** | Sign-in for the Test Suite, one organization per team | Participants' browsers |
| **Database** | Drafts, runs and their traces | The engine |
| **Connector pairs** | Per team: two connectors with wallets, a Digital Twin Registry and a BDRS | The engine, and each other |

Placeholders used in this kit: `https://ide.example.org` for the Test Suite and
`*.team-01.example.org` for a team's connectors. Replace them in your own inventory only,
never in this repository.

## Order

1. **Connector pairs first.** They take the longest and everything else points at them.
2. **Test Suite, engine, identity provider, database.**
3. **Configure**: organizations, users, environments. See [Configure](configure.md).
4. **Smoke test** from a guest laptop.

## 1 · Connector pairs

One pair is one small dataspace from the
[Tractus-X Umbrella](../reference/glossary.md#umbrella), chart version `26.03.00`, with its
`values-adopter-decentralized-identityhub.yaml` profile. It gives you:

| Role in the tutorial | Umbrella name | Identity |
| --- | --- | --- |
| **Your side**, the system under test | provider connector, its wallet and its Digital Twin Registry | `BPNL000000000001` |
| **Partner side**, where the test runs from | consumer connector and its wallet | `BPNL000000000002` |
| Shared | BDRS, IssuerService | |

The exact, proven sequence is in the reusable workflow,
[`.github/workflows/e2e-test.yml`](https://github.com/eclipse-tractusx/tutorial-resources/blob/main/.github/workflows/e2e-test.yml),
from the step "Add Tractus-X Helm repo" to "Preflight — check connector and DTR
reachability". Read it as the runbook. Four things the chart does **not** do for you, and
the workflow therefore does itself:

| Must be true | Why | Workflow step |
| --- | --- | --- |
| The connectors' host names resolve **inside** the cluster | The connectors find each other, their wallets and the issuer by host name | "Point in-cluster DNS at the ingress controller" |
| An ingress class is the default | Some ingresses of the profile name no class | "Install ingress-nginx and make it the default IngressClass" |
| Both participants hold their credentials | The chart seeds the claims but does not issue the credentials. Without them every negotiation is refused. | "Issue Catena-X credentials to both participants" |
| The connectors' vault secrets exist | The data plane signs and verifies its tokens with them | "Generate the connectors' in-memory vault secrets" |

### One pair per team

Deploy the release once per team, each in its own namespace and with its own host names.

> [!WARNING]
> **Not run.** The workflow deploys one pair with fixed host names (`provider.local`,
> `consumer-dsp.local`, …). Several pairs in one cluster need those names changed per
> team in the Helm values, and the names are also part of the participants' identities.
> Prove it for two teams before you plan for twenty.

If that does not work in time, the fallback is **one shared pair for all teams**. It works
because each team renames the template's asset and policy ids to its product. Say so on
the handouts: every team then has the same boxes C and D.

### Sizing

One pair runs on a standard GitHub-hosted runner. There the whole job, from an empty
runner to the test result, takes five to six minutes. Plan the cluster from that and from
your own measurement of one pair.

## 2 · The Test Suite

Deploy the Test Suite, its engine, the identity provider and the database with the
operators' runbook. For this session it must have:

| Requirement | Why |
| --- | --- |
| A public `https://` address with a valid certificate | Browsers refuse the assistant's calls from an insecure page |
| The IDE switched on for the workshop organizations | It is the tool of the session |
| The assistant's **Local / self-hosted** option included | For participants who bring their own model server |
| The engine can reach every team's connectors by the host names on the handout | The engine runs the tests |
| Each provider connector can reach the address in **box C, "as the connector reaches it"** | Tests offer the product through the connector's data plane |
| Enough parallel runs for all teams | Twenty teams press **Run** within the same ten minutes |

## 3 · What browsers must reach

| From the participant's browser | For |
| --- | --- |
| The Test Suite and its identity provider | The IDE |
| `api.openai.com`, `api.anthropic.com`, the Catena-X AI endpoint | The assistant. The call goes from the browser, not from the Test Suite. |
| `localhost`, for a model on the participant's own laptop | The assistant, local option |
| `github.com` | Fork, push, pull request |

A venue network that blocks one of the model providers blocks the assistant for everyone
who uses it. Test it on the guest network, not on yours.

## 4 · Smoke test

Do the tutorial's chapters 3 to 5 once, from a guest laptop, with the template unchanged:

1. Sign in, open the IDE, connect a model, get an answer.
2. Open the [template](https://github.com/eclipse-tractusx/tutorial-resources/tree/main/e2e-testing/template), press **Compile**: it must pass.
3. Press **Run** with the values of one team's handout: both tests must pass.

The template's first test needs something behind the provider's connector to call.
The workflow uses a small echo service for that; deploy one per pair, or point
`product_internal_url` at any HTTP service the provider's data plane can reach.

---

[← Overview and checklist](index.md) · [Home](../index.md) · [Configure →](configure.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
