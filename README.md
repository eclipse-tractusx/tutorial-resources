# Eclipse Tractus-X Tutorials

Hands-on tutorials from the Eclipse Tractus-X community: run a dataspace on your laptop, write
connector policies, build a product passport, give your product an end-to-end test.

**Website:** <https://eclipse-tractusx.github.io/tutorial-resources/>. The home page lists every
tutorial, and every tutorial opens there as a site. Web apps open ready to try.
Tutorials marked deprecated are kept for reference and may not match the current
Tractus-X release.

## Tutorials

| Tutorial | What you do | Open |
| --- | --- | --- |
| **E2E tests for Tractus-X products** | Create automated tests for Eclipse Tractus-X products using AI, with the Eclipse Tractus-X TestLab testing framework: build an end-to-end test in about an hour and run it on every pull request | [Site](https://eclipse-tractusx.github.io/tutorial-resources/e2e-testing/) · [Source](e2e-testing/README.md) |
| **EDC Policy Playground** | Put together Tractus-X connector policies in the browser and see the policy JSON they become | [Site](https://eclipse-tractusx.github.io/tutorial-resources/edc-policy-playground/) · [Try the app](https://eclipse-tractusx.github.io/tutorial-resources/policy-playground/) · [Source](edc-policy-playground/README.md) |
| **Minimum Tractus-X Dataspace (MXD)** · ⚠️ deprecated | Deploy your own Tractus-X connectors, or a whole small dataspace, on a local Kubernetes cluster. Follow-up guides cover access policies, file transfers and EDR transfers | [Site](https://eclipse-tractusx.github.io/tutorial-resources/mxd/) · [Source](mxd/README.md) |
| **Digital Product Passport** · ⚠️ deprecated | Create a passport for your part and read it over the dataspace (Tractus-X Community Days workstream) | [Site](https://eclipse-tractusx.github.io/tutorial-resources/dpp-tutorial/) · [Source](dpp-tutorial/README.md) |
| **IRS frontend challenge with GenAI** | Build a frontend for the Item Relationship Service with an AI assistant, in three phases | [Site](https://eclipse-tractusx.github.io/tutorial-resources/icc-irs-challenges/) · [Source](icc-irs-challenges/README.md) |
| **Migration guides** | Move the PostgreSQL and Keycloak of a deployment from the Bitnami Helm charts to the CloudPirates charts | [Site](https://eclipse-tractusx.github.io/tutorial-resources/migration-guides/) · [PostgreSQL](migration-guides/GENERIC_POSTGRESQL_MIGRATION_GUIDE.md) · [Keycloak](migration-guides/GENERIC_BITNAMI_TO_CLOUDPIRATES_KEYCLOAK_MIGRATION_GUIDE.md) |

## What is in this repository

| Folder | Contents |
| --- | --- |
| [`e2e-testing/`](e2e-testing/) | The E2E testing tutorial: pages, product catalog, template test project, worked example, administrator kit |
| [`edc-policy-playground/`](edc-policy-playground/) | The Policy Playground, an Angular app |
| [`mxd/`](mxd/), [`mxd-runtimes/`](mxd-runtimes/) | The MXD Terraform setup and the runtime images it builds and loads into the cluster |
| [`dpp-tutorial/`](dpp-tutorial/) | The Digital Product Passport workstream |
| [`icc-irs-challenges/`](icc-irs-challenges/) | The IRS frontend challenge and its AI assistant |
| [`migration-guides/`](migration-guides/) | Helm chart migration guides |
| [`pages/`](pages/) | The home page of the website |

## The website

[`pages.yml`](.github/workflows/pages.yml) builds the website and publishes it with GitHub Pages
whenever `main` changes:

| Address | Built from |
| --- | --- |
| `/` | [`pages/index.html`](pages/index.html) |
| `/e2e-testing/` | [`e2e-testing/`](e2e-testing/), with MkDocs |
| `/policy-playground/` | [`edc-policy-playground/`](edc-policy-playground/), with Angular |
| `/edc-policy-playground/`, `/mxd/`, `/dpp-tutorial/`, `/icc-irs-challenges/`, `/migration-guides/` | The Markdown in those folders, listed in [`pages/markdown-sites.yml`](pages/markdown-sites.yml) |

A tutorial that is only Markdown needs no site setup of its own. Add its folder to
[`pages/markdown-sites.yml`](pages/markdown-sites.yml) and the workflow builds a site from it
with the Tractus-X look: its `README.md` is the home page (a contents page is made when there
is none), the other Markdown files and pictures become pages, and links to anything else (a
Postman collection, Terraform, test data) open the file on GitHub. `deprecated: true` puts a
note on every page; `app:` gives the home page a "Try the app" button for a web app built
from the tutorial, over a blurred screenshot of the app when `app_image:` names one.

To preview a site on your machine:

```shell
# E2E testing tutorial, on http://localhost:8000
cd e2e-testing && pip install -r requirements.txt && mkdocs serve

# Policy Playground, on http://localhost:4200
cd edc-policy-playground && npm ci && npm start

# The Markdown tutorials, into _site/ (open _site/mxd/index.html)
pip install -r e2e-testing/requirements.txt
python .github/scripts/build_markdown_sites.py --out _site
```

## Add a tutorial

1. Put it in its own folder with a `README.md` that says what you build, what you need and how
   long it takes.
2. Add a row to the [Tutorials](#tutorials) table above.
3. Add a card to [`pages/index.html`](pages/index.html), so it shows on the website. Put its
   picture in [`pages/media/`](pages/media/), with a `.license` file next to it. Set
   `data-path` to its folder: when the site is built, the card's Added and Updated dates and
   its authors (the GitHub accounts with commits in that folder) are filled in from the
   history. Give it version 1.0 and add it to [Versions](#versions).
4. If it is only Markdown, add its folder to
   [`pages/markdown-sites.yml`](pages/markdown-sites.yml) and point the card to `<folder>/`. If it
   has a site or a web app of its own, build it into its own folder of `_site` in
   [`pages.yml`](.github/workflows/pages.yml) and point the card there.

## Versions

Every tutorial has its own version, separate from the versions of the software it uses. It
starts at 1.0 when it is published. The minor number goes up when the steps change, for
example a new chapter or an update for a new Tractus-X release. The major number goes up when
the tutorial is rebuilt. Change it on the tutorial's card in
[`pages/index.html`](pages/index.html) and add a line below.

| Tutorial | Version | History |
| --- | --- | --- |
| E2E tests for Tractus-X products | 1.0 | 1.0 (Nov 2026): first published |
| EDC Policy Playground | 1.1 | 1.0 (Aug 2023): first published · 1.1 (May 2024): policies for Tractus-X EDC 0.7 |
| IRS frontend challenge with GenAI | 1.0 | 1.0 (Dec 2024): the Community Days challenge, with fixes since |
| Migration guides | 1.2 | 1.0 (Jan 2026): PostgreSQL guide · 1.1 (Feb 2026): Keycloak guide · 1.2 (Apr 2026): PostgreSQL guide reworked |
| Minimum Tractus-X Dataspace (MXD) | 2.2 | 1.0 (Aug 2023): first published · 2.0 (Nov 2024): IdentityHub and STS replace MIW and Keycloak · 2.1 (Jan 2025): IdentityHub upgrade · 2.2 (Mar 2025): DCP issuance, Tractus-X EDC 0.9 |
| Digital Product Passport | 1.0 | 1.0 (Dec 2024): the Community Days workstream |

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md) and the [pull request etiquette](pr_etiquette.md) before
you open a pull request. Report security issues as described in [SECURITY.md](SECURITY.md). This
project follows the [Code of Conduct](CODE_OF_CONDUCT.md).

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).
Code in this repository is licensed under [Apache-2.0](LICENSE).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2023, 2026 Contributors to the Eclipse Foundation
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
