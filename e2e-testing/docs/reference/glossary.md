# Glossary

Every term the tutorial links to, in one or two lines. Simple, but not wrong.

[Home](../index.md)

## The test

### E2E test

End-to-end test. It uses a product the way a partner would, through the same doors, and
checks that the promise holds. It does not look inside the product.

### TCK

Technology Compatibility Kit. A package of tests that proves a system does what a
standard or an API promises. In this tutorial the E2E test of a product *is* a TCK.

### TCK project

The editable form of a TCK: a folder with `index.yaml`, a `tests/` folder and optional
`schemas/` and `testdata/`. This is what you commit.

### Manifest

The file `index.yaml`. It names the TCK, lists its tests, declares its variables and says
what a run needs on each side.

### Step

One action in a test, such as "create an asset" or "negotiate a contract". A step takes
values in (`with`), gives values back (`returns`) and checks them (`validate`).

### Setup, execution, teardown

The three phases of a test: prepare what is needed, do the thing and check it, remove what
was created.

### Positive, negative and contract test

Does the promise hold? Is the wrong thing refused? Does the answer have the agreed shape?

### Validate, compile, run

Validate checks the files. Compile packs the project into one `.tck` package. Run executes
the package against real systems and reports PASS or FAIL.

### SUT

System under test. Your product, and the connector in front of it.

### Engine

The side that runs the test. It acts as the partner company and has a connector of its own.

### TestLab

The open-source test engine of Eclipse Tractus-X
([`tractusx-testlab`](https://github.com/eclipse-tractusx/tractusx-testlab)). It validates,
compiles and runs TCKs, from the command line or behind the IDE.

### Test Suite IDE

A browser tool for building TCK projects with blocks, a YAML editor and an AI assistant.
Provided by the Catena-X Association. It produces plain TCK projects.

## The dataspace

### Dataspace

A set of rules and building blocks that lets companies exchange data directly, each
keeping control of its own data. There is no central database.

### Participant

A company in the dataspace. It has an identity, a wallet and at least one connector.

### BPN

Business Partner Number. The number that identifies a company, for example
`BPNL000000000001`. The `L` stands for legal entity.

### DID

Decentralized Identifier. The digital identity of a participant, for example
`did:web:provider.example:BPNL000000000001`. Current connectors identify each other by
DID; the BPN is still what people and applications use.

### BDRS

BPN-DID Resolution Service. The directory that translates a BPN into a DID.

### Connector

The component through which a participant offers and consumes data. Every exchange between
two companies goes from connector to connector. The Tractus-X connector is built on the
Eclipse Dataspace Components (EDC).

### Control plane and data plane

The two halves of a connector. The control plane handles catalogs, contracts and transfers.
The data plane carries the actual data once a contract exists.

### Management API

The interface a company uses to operate its **own** connector: create assets and policies,
start negotiations. It is protected by an API key and never exposed to partners.

### DSP

Dataspace Protocol. The language two connectors speak to each other: catalog, negotiation,
transfer. The **DSP address** is where a partner reaches your connector.

### Asset and offer

An asset is something a provider makes available, for example an API. An offer is the asset
plus the policy under which a partner may use it. A catalog is a list of offers.

### Policy

The terms of an offer, written so that a machine can check them: who may see it (access
policy) and what it may be used for (usage policy).

### Contract agreement

The result of a successful negotiation: both connectors have agreed on an offer.

### EDR

Endpoint Data Reference. A short-lived token and an address the consumer receives after a
contract is agreed. With it the consumer fetches the data from the provider's data plane.

### Wallet

Holds a participant's verifiable credentials and presents them on request. In Tractus-X
this is the IdentityHub.

### Verifiable credential

A digitally signed proof, for example "this company is a member of the dataspace".
Connectors check them during a negotiation.

### Digital twin

The digital description of a real thing, such as a part or a vehicle. It says which data
exists about the thing and where to get it.

### Digital Twin Registry

The service in which a provider lists its digital twins so that partners can find them.
Also called DTR. An entry is a **shell descriptor**; the pointers to the actual data are
**submodel descriptors**.

### Eclipse Tractus-X developer toolbox

The small dataspace the E2E workflow creates in memory inside the GitHub runner: two
connectors, two wallets, a Digital Twin Registry, a BDRS and an IssuerService in a
Kubernetes cluster in Docker. It is built from the [Umbrella](#umbrella) chart with a fixed
dataspace configuration, and it is thrown away when the job ends.

### Umbrella

The Tractus-X Umbrella is a Helm chart that deploys the Tractus-X products together as one
small dataspace. The [developer toolbox](#eclipse-tractus-x-developer-toolbox) is built
from it.

## The repository

### `.tractusx` metafile

A YAML file in the root of every Tractus-X repository. It names the product and its
repositories, so that tools can check them. This tutorial adds the link to the E2E test.

### Leading repository

The main repository of a product that is spread over several repositories. The E2E test
lives there.

### TRG

Tractus-X Release Guideline. A numbered rule every product follows, for example "every
repository has a `.tractusx` metafile" (TRG 2.05).

### ECA

Eclipse Contributor Agreement. You sign it once with your Eclipse account. A pull request to
an Eclipse project is only accepted when the email of its commits belongs to an account
with a signed ECA.

### Signed-off commit

A commit whose message ends with `Signed-off-by: Your Name <your@email>`.
`git commit -s` adds the line.

### Reusable workflow

A GitHub Actions workflow kept in one place and called from other repositories in a few
lines. The E2E test workflow is one.

### Fork and pull request

A fork is your own copy of a repository on GitHub. A pull request asks the original
repository to take a change from your fork.

---

[Home](../index.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
