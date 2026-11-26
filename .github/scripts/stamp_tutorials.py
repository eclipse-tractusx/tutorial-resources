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

"""Writes the dates of each tutorial into the home page.

Each card in pages/index.html names its folder (data-path="mxd"). The "Added"
and "Updated" dates are the first and the latest commit that touched that
folder; they need the full history (actions/checkout with fetch-depth: 0).
The authors are not stamped: pages/hooks/home.py adds them when the page is
built, from the authors each tutorial lists in its own settings.

    python .github/scripts/stamp_tutorials.py _site/index.html
"""

import re
import subprocess
import sys
from datetime import date
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]

# A card runs from its <li data-path=...> to the </li> at the same indentation
# (the lists inside a card, its chips and authors, are indented deeper).
CARD = re.compile(
    r'^(?P<indent>[ ]*)<li [^>]*data-path="(?P<path>[^"]+)"[^>]*>.*?\n(?P=indent)</li>', re.S | re.M
)
TIME = '<time class="{kind}" datetime="{iso}">{text}</time>'


def commit_dates(path: str) -> list[str]:
    out = subprocess.run(
        ["git", "log", "--format=%as", "--", path],
        cwd=ROOT, check=True, capture_output=True, text=True,
    ).stdout.split()
    return out  # newest first


def month(iso: str) -> str:
    return date.fromisoformat(iso).strftime("%b %Y")


def stamp(card: re.Match) -> str:
    html = card.group(0)
    dates = commit_dates(card.group("path"))
    if not dates:
        sys.exit(f"no commits found for {card.group('path')}: is the history complete?")
    for kind, iso in (("added", dates[-1]), ("updated", dates[0])):
        html = re.sub(
            rf'<time class="{kind}" datetime="[^"]*">[^<]*</time>',
            TIME.format(kind=kind, iso=iso, text=month(iso)),
            html,
        )
    return html


def main() -> None:
    page = sys.argv[1]
    with open(page, encoding="utf-8") as f:
        html = f.read()
    stamped, count = CARD.subn(stamp, html)
    if count == 0:
        sys.exit("no tutorial cards with data-path found")
    with open(page, "w", encoding="utf-8") as f:
        f.write(stamped)
    print(f"stamped {count} tutorials")


if __name__ == "__main__":
    main()
