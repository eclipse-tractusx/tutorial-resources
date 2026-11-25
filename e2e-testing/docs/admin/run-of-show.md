# Run of show

The session, minute by minute, for the people running it.

[Home](../index.md) · [For administrators](index.md)

## Who does what

| Role | Does | How many |
| --- | --- | --- |
| **Host** | Presents the opening, keeps the clock, calls the checkpoints | 1 |
| **Floor** | Walks between the teams, unblocks | 1 per four teams |
| **Desk** | Watches the IDE, the connectors and the pull requests coming in | 1 |

## Before the doors open

| When | What |
| --- | --- |
| 30 minutes before | Run the [smoke test](index.md#on-the-day-30-minutes-before) from a guest laptop on the venue network. |
| 20 minutes before | Put one handout on each team table. Put the product name on the table sign. |
| 10 minutes before | Open the tutorial home page and the wall of pull requests on the room screen. |

## The opening: 10 minutes

Present the opening deck: the problem, the idea, the mission.
It ends with "the clock starts now". Start a visible timer.

## The session

The table assumes about 75 minutes of work. A dry run with a first-time reader came to
about 90, most of the difference in chapters 5 and 6. With a longer slot, give the extra time to
chapters 5 and 6, and keep the two checkpoints.

| Minute | Teams are in | Host says | Floor looks for |
| --- | --- | --- | --- |
| 0 | [1 · Start here](../tutorial/01-start-here.md) | "Read box A. Pick Driver, Navigator, Skeptic." | Teams with an empty box on the handout |
| 5 | [2 · Your product](../tutorial/02-your-product.md) | "Read your scenario. Press Fork now." | Teams that cannot find a refusal: suggest "what was never offered is not found" |
| 10 | [3 · Hello, TestLab](../tutorial/03-hello-testlab.md) | "Write the first test yourself. No AI yet." | Teams stuck on indentation: point them to the finished files in the chapter |
| 25 | [4 · Choose your AI](../tutorial/04-choose-your-ai.md) | "Hands up when your assistant has answered." | Teams without a model: seat them with a team that has one |
| 30 | [5 · Build](../tutorial/05-build-the-test.md) | "Plan first. Do not let it write before you agree with the plan." | Teams whose compile stays red after three rounds |
| 45 | [6 · Run](../tutorial/06-run-it.md) | **Checkpoint:** "Who has a green compile?" | Runs that wait: check the connectors at the Desk |
| 55 | [7 · Repository](../tutorial/07-add-to-repository.md) | **Checkpoint:** "Who has seen a green run? Who has seen a red one and knows why?" | Git push problems, missing sign-off |
| 65 | [8 · Pull request](../tutorial/08-link-and-pull-request.md) | "Open it as a draft. Then come and ring the bell." | ECA check red: the commit email is wrong |
| 70 | [9 · Next](../tutorial/09-what-comes-next.md) | "Two teams: what did the assistant get wrong?" | Collect the answers |
| 75 | Done | "Count the pull requests on the wall." | |

## Keep it fun

- **The wall.** Show the list of pull requests on the room screen. Every new one gets
  applause. A search for `is:pr "E2E test" org:eclipse-tractusx created:>=2026-12-03`
  is enough.
- **The bell.** A real one. A team rings it when its pull request is open.
- **The Skeptic's prize.** At minute 55, ask for the best mistake a Skeptic caught.
  That story is worth more than the fastest team.

## When a team falls behind

| Behind at | Do |
| --- | --- |
| Minute 25, no API key but an AI coding assistant on the laptop | Send them down the [route C](../tutorial/04c-your-own-ai-tool.md). It needs Python and a `git clone`, so decide early. |
| Minute 35, no assistant | Give them the [worked example](../reference/worked-example.md) to read and adapt by hand, or seat them with another team. |
| Minute 50, compile still red | Have them open the template again and change only the names. A small green test beats a large red one. |
| Minute 60, no green run | Skip the run. Go to chapter 7: the workflow will run the test in CI. |
| Minute 70, no push | A pull request to their own fork still counts on the wall. |

## After the session

- Note which products now have a pull request. Update the count in your report.
- Collect what the assistants got wrong. It is input for the starter prompts.
- Tell the committers of each product that a pull request is waiting, and that a
  first-time contributor's workflow needs their approval to run.
- Reset or delete the workshop accounts and connectors, as described in
  [Configure](configure.md#after-the-event).

---

[← Handouts](handouts.md) · [Home](../index.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
