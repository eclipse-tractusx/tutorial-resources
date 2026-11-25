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

"""Writes the dates and the authors of each tutorial into the home page.

Each card in pages/index.html names its folder (data-path="mxd"). The "Added"
and "Updated" dates are the first and the latest commit that touched that
folder; they need the full history (actions/checkout with fetch-depth: 0).

The authors are the GitHub accounts with commits in that folder, most commits
first, bots left out. A tutorial that moved here from another repository names
it too (data-authors-from="eclipse-tractusx/digital-product-pass:dpp-tutorial"),
so its first authors still count. They come from the GitHub API; set
GITHUB_TOKEN to avoid its rate limit. If the API cannot be reached, the authors
already in the page stay.

    python .github/scripts/stamp_tutorials.py _site/index.html
"""

import html as html_lib
import json
import os
import re
import subprocess
import sys
from collections import Counter
from datetime import date
from pathlib import Path
from urllib.error import URLError
from urllib.request import Request, urlopen

REPOSITORY = "eclipse-tractusx/tutorial-resources"
ROOT = Path(__file__).resolve().parents[2]
SHOWN_AUTHORS = 5

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


def github_authors(repository: str, path: str) -> tuple[Counter, dict]:
    """Commits per GitHub account in a folder, and each account's avatar."""
    headers = {"Accept": "application/vnd.github+json", "User-Agent": "tutorial-resources-pages"}
    if os.environ.get("GITHUB_TOKEN"):
        headers["Authorization"] = "Bearer " + os.environ["GITHUB_TOKEN"]
    counts, avatars, page = Counter(), {}, 1
    while True:
        url = f"https://api.github.com/repos/{repository}/commits?path={path}&per_page=100&page={page}"
        with urlopen(Request(url, headers=headers), timeout=30) as response:
            commits = json.load(response)
        for commit in commits:
            author = commit.get("author") or {}
            login = author.get("login")
            if not login or author.get("type") == "Bot" or login.endswith("[bot]"):
                continue
            counts[login] += 1
            avatars[login] = author["avatar_url"]
        if len(commits) < 100:
            return counts, avatars
        page += 1


def authors_html(card_html: str, path: str) -> str | None:
    sources = [(REPOSITORY, path)]
    extra = re.search(r'data-authors-from="([^"]+)"', card_html)
    if extra:
        sources += [tuple(s.split(":", 1)) for s in extra.group(1).split()]
    counts, avatars = Counter(), {}
    try:
        for repository, folder in sources:
            c, a = github_authors(repository, folder)
            counts.update(c)
            avatars.update(a)
    except (URLError, OSError, ValueError) as error:
        print(f"authors of {path} not updated: {error}", file=sys.stderr)
        return None
    if not counts:
        return None
    logins = [login for login, _ in counts.most_common()]
    items = []
    for login in logins[:SHOWN_AUTHORS]:
        name = html_lib.escape(login)
        avatar = html_lib.escape(avatars[login] + ("&" if "?" in avatars[login] else "?") + "s=56")
        items.append(
            f'<li><a href="https://github.com/{name}" title="{name}">'
            f'<img src="{avatar}" alt="{name}" width="28" height="28" loading="lazy"></a></li>'
        )
    if len(logins) > SHOWN_AUTHORS:
        more = len(logins) - SHOWN_AUTHORS
        items.append(
            f'<li class="more"><a href="https://github.com/{REPOSITORY}/commits/main/{path}" '
            f'title="{more} more: see the history">+{more}</a></li>'
        )
    return '<dd class="authors"><ul>' + "".join(items) + "</ul></dd>"


def stamp(card: re.Match) -> str:
    html = card.group(0)
    authors = authors_html(html, card.group("path"))
    if authors:
        html = re.sub(r'<dd class="authors">.*?</dd>', lambda _: authors, html, flags=re.S)
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
