# The `.tractusx` link

Every Tractus-X repository has a [`.tractusx` metafile](glossary.md#tractusx-metafile)
in its root. Add one block so that people and tools can find your E2E test.

[Home](../index.md)

## Add this block

```yaml
e2eTests:
  - name: "Digital Twin Registry E2E test"
    path: "tck/e2e"
    workflow: ".github/workflows/e2e-test.yml"
```

| Field | Meaning |
| --- | --- |
| `name` | What the test is called, for a human. |
| `path` | The folder that holds `index.yaml`, from the root of the repository. |
| `workflow` | The workflow that runs it. |

A complete file is in the
[worked example](https://github.com/eclipse-tractusx/tutorial-resources/blob/main/e2e-testing/examples/digital-twin-registry/.tractusx).

## Where it goes

In the [leading repository](glossary.md#leading-repository) of your product, next to the
fields that are already there:

```yaml
product: "Digital Twin Registry"
leadingRepository: "https://github.com/eclipse-tractusx/sldt-digital-twin-registry"
repositories:
  - name: "sldt-digital-twin-registry"
    usage: "This is the repository which contains the source code for the SLDT digital twin registry"
    url: "https://github.com/eclipse-tractusx/sldt-digital-twin-registry.git"

e2eTests:
  - name: "Digital Twin Registry E2E test"
    path: "tck/e2e"
    workflow: ".github/workflows/e2e-test.yml"
```

> [!IMPORTANT]
> `e2eTests` is a **proposed** field. It is defined in the draft
> [TRG](glossary.md#trg) for E2E tests, and the name is an open decision there.
> Metafiles already carry fields the release checks do not read, such as `openApiSpecs`,
> so an extra field does not break them.

---

[← The workflow](workflow.md) · [Home](../index.md) · [Troubleshooting →](troubleshooting.md)

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
