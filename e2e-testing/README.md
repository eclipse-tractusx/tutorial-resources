# E2E Tests in Eclipse Tractus-X

**One green check for every product.** In about an hour a team builds an end-to-end test for
one Eclipse Tractus-X product, with an AI assistant, commits it to the product's
repository, links it in `.tractusx` and sees it run in GitHub Actions.

Made for the "AI and Test Management" session of the 7th Eclipse Tractus-X Community Days
(3–4 December 2026). It works just as well on your own, any other day.

## Start here

| You are | Go to |
| --- | --- |
| New to all of this | [Why we are here](docs/story/why.md), then [A dataspace in five minutes](docs/story/dataspace.md) |
| A participant, before the workshop | [Before you come](docs/reference/prepare.md) |
| A participant, and the clock is running | [1 · Start here](docs/tutorial/01-start-here.md) |
| Looking up a word | [Glossary](docs/reference/glossary.md) |
| Running the session | [For administrators](docs/admin/index.md) |

## Getting Started

| | Chapter | Minutes |
| --- | --- | --- |
| 1 | [Start here](docs/tutorial/01-start-here.md) | 5 |
| 2 | [Your product and your connectors](docs/tutorial/02-your-product.md) | 5 |
| 3 | [Hello, TestLab: your first test, by hand](docs/tutorial/03-hello-testlab.md) | 15 |
| 4 | [Choose your AI](docs/tutorial/04-choose-your-ai/index.md) | 5 |
| 5 | [Build the test with the assistant](docs/tutorial/05-build-the-test.md) | 15 |
| 6 | [Run it against your connectors](docs/tutorial/06-run-it.md) | 10 |
| 7 | [Add it to your repository](docs/tutorial/07-add-to-repository.md) | 10 |
| 8 | [Link it and open the pull request](docs/tutorial/08-link-and-pull-request.md) | 5 |
| 9 | [What comes next](docs/tutorial/09-what-comes-next.md) | 5 |
| | | **about 75** |

## What is in this folder

| Path | What it is |
| --- | --- |
| [`docs/`](docs/index.md) | Every page of the tutorial. Plain Markdown: read it here, or as a site. |
| [`catalog/`](catalog/) | `products.json`: every product that should get an E2E test. `assignments.json`: which team has which product. |
| [`specs/`](specs/) | One E2E spec per product: what its TCK must test and look like. `build_specs.py` turns them into the pages under `docs/specs/` |
| [`template/`](template/tck/e2e/) | An E2E test project to copy and fill in |
| [`examples/digital-twin-registry/`](examples/digital-twin-registry/) | A complete example that passes in GitHub Actions: the project, the workflow and the `.tractusx` link |
| [`agent-kit/`](agent-kit/) | A skill and an agent that teach your own AI coding tool the format, for teams without an API key |
| [`admin/handouts/`](admin/handouts/) | Generator for one printable page per team |
| [`tools/`](tools/) | Small scripts that keep generated files in step with their sources |
| [`mkdocs.yml`](mkdocs.yml) | Turns `docs/` into a site with navigation and search |

The workflow product repositories call lives in the root of this repository:
[`.github/workflows/e2e-test.yml`](../.github/workflows/e2e-test.yml).

## Read it as a site

```bash
cd e2e-testing
pip install -r requirements.txt
mkdocs serve
```

Then open <http://localhost:8000>.

## Change something

| You changed | Then run |
| --- | --- |
| `catalog/products.json` or `catalog/assignments.json` | `python catalog/build_catalog.py` |
| Anything under `template/tck/e2e` or the example | `python tools/build_template_zip.py` and `python tools/build_agent_kit.py` |
| A spec under `specs/` | `python specs/build_specs.py` |
| A page under `docs/` | `mkdocs build --strict` |

The workflow [`e2e-testing-site.yml`](../.github/workflows/e2e-testing-site.yml) runs the
same checks on every pull request. Once a change is on `main`,
[`pages.yml`](../.github/workflows/pages.yml) publishes the site at
<https://eclipse-tractusx.github.io/tutorial-resources/e2e-testing/>, next to the home page that
lists all the tutorials.

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- SPDX-FileCopyrightText: 2026 LKS Next
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
