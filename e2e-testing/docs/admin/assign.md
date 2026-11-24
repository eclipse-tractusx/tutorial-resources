# Assign products

One team, one product, one repository. The assignment lives in its own file, so the
catalog stays stable.

[Home](../index.md) · [For administrators](index.md)

## The two files

| File | Holds | Changes |
| --- | --- | --- |
| [`catalog/products.json`](https://github.com/eclipse-tractusx/tutorial-resources/blob/main/e2e-testing/catalog/products.json) | Every product, what it needs, a scenario, difficulty | Rarely |
| [`catalog/assignments.json`](https://github.com/eclipse-tractusx/tutorial-resources/blob/main/e2e-testing/catalog/assignments.json) | Which team has which product | For every event |

## Steps

1. Open the [product catalog](../reference/catalog.md) and look at the first table,
   **Fits the workshop**. These products can be built **and run** with what
   every team gets.
2. Give each team one of them. Prefer a product the team maintains or uses.
3. More teams than products in that table? Choose, in this order:

    | Option | Cost |
    | --- | --- |
    | Give a second team the same product with another scenario from **More scenarios** | None. Two pull requests to one repository: tell its committers. |
    | Give a team a product from the second table | The team builds and compiles, and skips the run in chapter 5. |
    | Deploy more of the Umbrella for the event | Your time. See [Deploy](deploy.md). |

4. Edit `catalog/assignments.json`. One entry per team:

    ```json
    { "team": "team-01", "teamName": "Team 01", "productId": "sldt-digital-twin-registry" }
    ```

    `team` is the short name used for the handout file and the connector inventory.
    `productId` is an `id` from `products.json`. Add `"scenario": "..."` when the team
    takes a scenario other than the product's first one.

5. Set `"sample": false` at the top of the file once the real teams are in.
6. Check the files:

    ```bash
    pip install -r requirements.txt
    python catalog/build_catalog.py --check
    ```

    It fails with a clear message when a team is listed twice or names a product that
    does not exist.

7. Build the [handouts](handouts.md).

## Before you publish the list

- [ ] Every product's committers know that a pull request is coming.
- [ ] No team has a product marked **advanced** unless it asked for it.
- [ ] Teams that share a product have different scenarios.

> [!NOTE]
> The file in the repository holds five invented teams (`"sample": true`). The real team
> list is an open decision of the organisers.

---

[← Configure](configure.md) · [Home](../index.md) · [Handouts →](handouts.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
