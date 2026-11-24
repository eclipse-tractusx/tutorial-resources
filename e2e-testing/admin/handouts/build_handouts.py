#################################################################################
# Copyright (c) 2026 Contributors to the Eclipse Foundation
#
# See the NOTICE file(s) distributed with this work for additional
# information regarding copyright ownership.
#
# This program and the accompanying materials are made available under the
# terms of the Apache License, Version 2.0 which is available at
# https://www.apache.org/licenses/LICENSE-2.0.
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.
#
# SPDX-License-Identifier: Apache-2.0
#################################################################################

"""Builds one printable handout per team.

    python admin/handouts/build_handouts.py --inventory admin/handouts/inventory.sample.json

Reads catalog/products.json, catalog/assignments.json and a connector
inventory, and writes out/<team>.html for every team. With a Chrome, Chromium
or Edge on the machine it also writes out/<team>.pdf (use --browser to name
the executable, --no-pdf to skip).

The real inventory holds addresses and keys. Keep it out of Git: the
.gitignore next to this file ignores inventory.json and out/.
"""

from __future__ import annotations

import argparse
import html
import io
import json
import shutil
import subprocess
import sys
from pathlib import Path
from string import Template

import segno
from jsonschema import Draft202012Validator

HERE = Path(__file__).resolve().parent
CATALOG = HERE.parent.parent / "catalog"

NEEDS = {
    "connector": "connector",
    "dtr": "Digital Twin Registry",
    "wallet": "wallet",
    "bdrs": "BDRS",
    "iam": "identity provider",
    "portal": "Portal",
    "bpdm": "BPDM",
    "discovery-finder": "Discovery Finder",
    "bpn-discovery": "BPN Discovery",
    "semantic-hub": "Semantic Hub",
    "submodel-server": "submodel server",
    "irs": "IRS",
    "kafka": "Kafka",
    "database": "database",
    "kubernetes": "Kubernetes cluster",
    "browser": "browser",
}

# Label on the handout -> key in the inventory. Order is the print order.
SIDE_FIELDS = [
    ("Management API", "managementUrl"),
    ("API key", "apiKey"),
    ("DSP address", "dspUrl"),
    ("Participant ID", "participantId"),
    ("BPN", "bpn"),
    ("Digital Twin Registry", "dtrUrl"),
    ("Registry, as the connector reaches it", "dtrInternalUrl"),
    ("Product, as the connector reaches it", "productInternalUrl"),
]

BROWSERS = [
    "google-chrome",
    "chromium",
    "chromium-browser",
    "microsoft-edge",
    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
    "/Applications/Chromium.app/Contents/MacOS/Chromium",
    "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge",
    "C:/Program Files/Google/Chrome/Application/chrome.exe",
    "C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe",
]


def load(path: Path, schema_path: Path) -> dict:
    data = json.loads(path.read_text(encoding="utf-8"))
    schema = json.loads(schema_path.read_text(encoding="utf-8"))
    errors = sorted(Draft202012Validator(schema).iter_errors(data), key=lambda e: list(e.path))
    if errors:
        for error in errors:
            where = "/".join(str(part) for part in error.path) or "(root)"
            print(f"{path.name}: {where}: {error.message}", file=sys.stderr)
        raise SystemExit(1)
    return data


def qr_svg(text: str) -> str:
    buffer = io.BytesIO()
    segno.make(text, error="m").save(buffer, kind="svg", xmldecl=False, svgns=True, border=0, scale=4)
    return buffer.getvalue().decode("utf-8")


def rows(side: dict) -> str:
    return "".join(
        f"<dt>{html.escape(label)}</dt><dd>{html.escape(str(side[key]))}</dd>"
        for label, key in SIDE_FIELDS
        if key in side
    )


def find_browser(explicit: str | None) -> str | None:
    for candidate in [explicit] if explicit else BROWSERS:
        found = shutil.which(candidate) or (candidate if Path(candidate).is_file() else None)
        if found:
            return found
    return None


def print_pdf(browser: str, page: Path, pdf: Path) -> None:
    subprocess.run(
        [browser, "--headless=new", "--disable-gpu", "--no-pdf-header-footer", f"--print-to-pdf={pdf}", page.as_uri()],
        check=True,
        capture_output=True,
        timeout=120,
    )


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__.split("\n", 1)[0])
    parser.add_argument("--inventory", type=Path, required=True, help="connector inventory (JSON)")
    parser.add_argument("--out", type=Path, default=HERE / "out", help="output folder (default: admin/handouts/out)")
    parser.add_argument("--browser", help="Chrome, Chromium or Edge executable used to print the PDFs")
    parser.add_argument("--no-pdf", action="store_true", help="write the HTML pages only")
    args = parser.parse_args()

    products = {p["id"]: p for p in load(CATALOG / "products.json", CATALOG / "products.schema.json")["products"]}
    assignments = load(CATALOG / "assignments.json", CATALOG / "assignments.schema.json")
    inventory = load(args.inventory, HERE / "inventory.schema.json")

    problems = []
    for entry in assignments["assignments"]:
        if entry["productId"] not in products:
            problems.append(f"team '{entry['team']}' names the unknown product '{entry['productId']}'")
        if entry["team"] not in inventory["teams"]:
            problems.append(f"team '{entry['team']}' has no entry in the inventory")
    if problems:
        print("\n".join(problems), file=sys.stderr)
        raise SystemExit(1)

    browser = None if args.no_pdf else find_browser(args.browser)
    if not args.no_pdf and not browser:
        print("No Chrome, Chromium or Edge found: writing HTML only. Name one with --browser.", file=sys.stderr)

    template = Template((HERE / "template.html").read_text(encoding="utf-8"))
    code = qr_svg(inventory["tutorialUrl"])
    args.out.mkdir(parents=True, exist_ok=True)

    for entry in assignments["assignments"]:
        product = products[entry["productId"]]
        team = inventory["teams"][entry["team"]]
        page = args.out / f"{entry['team']}.html"
        page.write_text(
            template.substitute(
                team_id=html.escape(entry["team"]),
                team_name=html.escape(entry["teamName"]),
                event=html.escape(assignments["event"]),
                product_name=html.escape(product["name"]),
                product_description=html.escape(product["description"]),
                product_repository=html.escape(product["repository"]),
                scenario=html.escape(entry.get("scenario") or product["scenario"]),
                needs=html.escape(", ".join(NEEDS[need] for need in product["needs"])),
                ide_url=html.escape(inventory["ideUrl"]),
                tutorial_url=html.escape(inventory["tutorialUrl"]),
                username=html.escape(team["account"]["username"]),
                password=html.escape(team["account"].get("password") or "handed out at the desk"),
                sut_rows=rows(team["sut"]),
                engine_rows=rows(team["engine"]),
                help=html.escape(inventory["help"]),
                qr_svg=code,
            ),
            encoding="utf-8",
        )
        if browser:
            print_pdf(browser, page, args.out / f"{entry['team']}.pdf")
        print(f"{entry['team']}: {page.name}" + (f", {entry['team']}.pdf" if browser else ""))

    if assignments.get("sample"):
        print("Note: assignments.json is still marked as a sample.", file=sys.stderr)


if __name__ == "__main__":
    main()
