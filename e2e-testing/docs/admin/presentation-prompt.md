# Presentation prompt

A prompt for an AI presentation tool. It produces the ten-minute opening the
administrators give **before** the clock starts: the problem, the idea, the mission.

[Home](../index.md) · [For administrators](index.md)

## How to use it

1. Fill in the four values under **Fill in before you paste**.
2. Paste the whole prompt into your presentation tool. If the organisers gave you a slide
   template, attach it and keep the line that says so.
3. Check every number on the slides against [Why we are here](../story/why.md) and the
   [product catalog](../reference/catalog.md). The tool must not invent any.

> [!NOTE]
> The prompt was written for this tutorial and has **not been run** through a
> presentation tool. Treat the first result as a draft.

## The prompt

````text
Create a presentation of 10 main slides plus 4 backup slides.

FILL IN BEFORE YOU PASTE
- IDE address: <https://ide.example.org>
- Tutorial address: <https://eclipse-tractusx.github.io/tutorial-resources/e2e-testing/>
- Number of teams today: <N>
- Where to get help in the room: <who, and which chat room>

OCCASION
The opening of a hands-on session, "AI and Test Management", at the
7th Eclipse Tractus-X Community Days (3-4 December 2026, ARENA2036,
Stuttgart). It is presented live by the session administrators and takes 10
minutes. The hands-on work starts right after the last slide.

AUDIENCE
Developers, architects and product people from companies that build or use
Eclipse Tractus-X products. Many have never written a test for a dataspace.
Some do not know what a connector is. They sit in teams; each team has been
given one product.

WHAT THE AUDIENCE MUST TAKE AWAY
1. The problem: every product tests itself, nobody tests the journey, and the
   release pays for it.
2. The idea: one E2E test in every product repository, in one common format,
   running on every pull request.
3. The mission for the next hour, and that it is doable.

TONE
A community session, not a product presentation. Warm, concrete, a little
playful. People should want to start. No superlatives, no marketing words, no
"revolutionary". Short sentences. One idea per slide.

Mention the Test Suite IDE as one way to build tests faster, provided by the
Catena-X Association. Say once, on slide 6, that the format and the engine are
not tied to one dataspace: the same approach works for tests of other
dataspaces and standards. Do not repeat it and do not sell it.

FACTS YOU MAY STATE. Use them exactly. Do not add figures of your own.
- Tractus-X has about thirty products.
- Before each release there is an E2E testing phase: typically three weeks,
  a daily meeting, one shared integration environment.
- TRG 5.09 (Helm test) already makes every product prove that it installs
  and starts, and calls itself "the base for future e2e tests".
- A TCK (Technology Compatibility Kit) is a package of tests. A TCK project
  is a folder of YAML files: a manifest (index.yaml) and one file per test.
- Eclipse Tractus-X TestLab is the open-source engine that validates,
  compiles and runs TCKs.
- A test has three phases: setup, execution, teardown.
- The workflow creates a small dataspace inside the GitHub runner, the
  Eclipse Tractus-X developer toolbox: two connectors, two wallets, a Digital Twin Registry,
  a BDRS and an IssuerService. It needs no secrets and no external cluster.
- The worked example (Digital Twin Registry) has 3 tests and 19 steps and
  runs in GitHub Actions in about five and a half minutes.
- The worked example's first run in CI failed: a twin had been registered
  without a submodel descriptor, and the schema check caught it.
- A draft Tractus-X Release Guideline proposes that every product hosts an
  E2E test, links it in its .tractusx metafile and runs it in GitHub Actions.
- Participants bring their own model for the assistant: OpenAI, Anthropic,
  Catena-X AI, or a local or self-hosted one. No key is shared.

STORYLINE: one slide per minute
1. Title (0:00-0:30). "One green check for every product". Subtitle: AI and
   Test Management, 7th Eclipse Tractus-X Community Days. Nothing else.
2. A release, three weeks, many people (0:30-1:30). The E2E testing phase as
   it is today: three weeks, daily meeting, shared environment. One line at
   the bottom: "It works. It is also the most expensive way to find out
   that something broke."
3. Each product tests itself. Nobody tests the journey (1:30-2:30). Left: a
   row of products, each with its own green tick. Right: the same products
   in a chain with a question mark on every hand-over.
4. The partner's question (2:30-3:30). One quote, large: "I am another
   company. I find your offer, I sign the contract, I ask for the data. Do I
   get it?" Under it: "That is an E2E test."
5. The idea (3:30-4:30). Four short lines that build one after the other: one
   E2E test in every product repository · written in one common format ·
   running on every pull request · linked in .tractusx so tools can find it.
6. What a test looks like (4:30-5:30). A folder tree (tck/e2e, index.yaml,
   tests/) beside the three phases setup, execution, teardown. Caption: "YAML
   made of ready-made steps. No framework to learn." This is the slide for
   the sentence about other dataspaces and standards.
7. Why one session is enough (5:30-6:30). Three cards: ready-made steps · an AI
   assistant that writes the YAML while you decide what must be proven · a
   dataspace in a box, created by the workflow.
8. It failed first, and that was useful (6:30-7:30). The worked example's
   red run, as a terminal block with the failing line "submodelDescriptors:
   [] should be non-empty", then the green run. Caption: "A check on
   'status 200' would have stayed green."
9. Your mission (7:30-9:00). The route as eight stops with minutes: Start 5 ·
   Your product 5 · IDE + LLM 5 · Build 15 · Run 10 · Repository 10 · Pull
   request 5 · Next 5. Large line: "You win when your pull request shows a
   green check."
10. Go (9:00-10:00). Three things only: the tutorial address as a QR code,
   the IDE address, where to get help. Then: "Driver, Navigator, Skeptic:
   pick your roles. The clock starts now."

BACKUP SLIDES, hidden, for questions
B1. A dataspace in five minutes: two companies, two connectors, the three
    steps catalog, negotiation, transfer.
B2. The glossary on one slide: TCK, SUT, engine, connector, BPN, EDR, TRG.
B3. What the draft TRG asks for: where the project lives, the metafile
    field, what the workflow runs, what the test must cover, what counts as
    passing.
B4. Three mistakes an assistant makes: an invented step, a check that cannot
    fail, an address written into a test.

SPEAKER NOTES
Give every main slide speaker notes: the time window from the storyline, then
three to five short lines of what to say, in spoken language. On slide 9, the
notes say out loud what each of the eight stops produces. On backup slides,
the note says when to use the slide.

DESIGN
Use the organisers' template if one is attached. Otherwise: black and white
with the Tractus-X orange (#FAA023) as the only accent, and a thin
orange-to-green (#B3CB2D) line as a divider. Large type, much white space, at
most 25 words per slide outside code blocks. Monospaced type for file names
and terminal output. No stock photos, no clip-art people, no AI-brain images.
Diagrams are simple boxes and arrows.

DO NOT
- Do not add a slide about pricing, licences, roadmaps or features of the IDE.
- Do not invent statistics, customer names, logos or quotes.
- Do not put more than one idea on a slide.
- Do not end with "Thank you" or "Questions?". The last slide starts the work.
````

## If you run long

Skip slide 8. The worked example's story is also in
[The worked example](../reference/worked-example.md), and teams meet it in chapter 2.

---

[← Run of show](run-of-show.md) · [Home](../index.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
