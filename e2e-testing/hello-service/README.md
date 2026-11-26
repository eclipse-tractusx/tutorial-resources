# Hello service

A tiny read-only API for the first test of the E2E testing tutorial
([chapter 3 · Hello, TestLab](../docs/tutorial/03-hello-testlab.md)). Participants write a test
against it by hand before an AI assistant writes anything.

Stock nginx answers from the JSON files in [`site/`](site); there is no code to build and no
image to publish. Every answer is JSON, errors included. The full specification is
[`site/openapi.yaml`](site/openapi.yaml), and the service serves it at `/openapi.yaml`.

| Call | Answer |
| --- | --- |
| `GET /hello` | `200` `{"message": "Hello, Tractus-X!"}` |
| `GET /parts` | `200` the two parts, `P-1001` and `P-1002` |
| `GET /parts/{partId}` | `200` the part, or `404` with a problem body |
| `GET /health` | `200` `{"status": "UP"}` |
| Anything else | `404`; another method than `GET`: `405` |

## Run it on your laptop

With Docker, from this folder:

```bash
docker run --rm -p 8080:8080 \
  -v "$PWD/nginx.conf:/etc/nginx/conf.d/default.conf:ro" \
  -v "$PWD/site:/usr/share/nginx/html:ro" \
  nginxinc/nginx-unprivileged:1.28-alpine
```

Then `curl http://localhost:8080/hello`.

## Deploy it for a workshop

One instance serves every team. Change the host name in
[`kustomization.yaml`](kustomization.yaml), then:

```bash
kubectl create namespace hello-service
kubectl apply -k e2e-testing/hello-service -n hello-service
```

The Ingress expects a TLS secret `hello-service-tls`, or a cert-manager annotation added by
you. The pod runs as a non-root user with a read-only file system and needs 16 MiB of memory.
Put the address on the handouts as `helloUrl` in the inventory
([Handouts](../docs/admin/handouts.md)).

## NOTICE

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2026 Contributors to the Eclipse Foundation
- SPDX-FileCopyrightText: 2026 Catena-X Automotive Network e.V.
- Source URL: <https://github.com/eclipse-tractusx/tutorial-resources>
