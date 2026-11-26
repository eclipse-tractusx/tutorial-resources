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

"""The home page is HTML (pages/index.html: the cards, their filters and
script), not Markdown: this puts it in as the page inside the shared theme's
header and footer (template home.html), with the authors of each tutorial.

The authors are not written in index.html. Each card names its tutorial's
folder (data-path="mxd"), and the tutorial lists its authors, GitHub accounts,
in its own settings:

    pages/markdown-sites.yml      authors: [hemantxpatel, paullatzelsperger]
    <folder>/mkdocs.yml           extra: {tutorial: {authors: [matbmoser]}}

A tutorial without authors gets no Authors row on its card.
"""

import html
import re
from pathlib import Path

import yaml
from mkdocs.exceptions import PluginError

ROOT = Path(__file__).resolve().parents[2]
REPOSITORY = "eclipse-tractusx/tutorial-resources"
SHOWN_AUTHORS = 5
GITHUB_LOGIN = re.compile(r"^[A-Za-z0-9](?:[A-Za-z0-9]|-(?=[A-Za-z0-9])){0,38}$")

# A card runs from its <li data-path=...> to the </li> at the same indentation
# (the lists inside a card, its chips, are indented deeper).
CARD = re.compile(
    r'^(?P<indent>[ ]*)<li [^>]*data-path="(?P<path>[^"]+)"[^>]*>.*?\n(?P=indent)</li>', re.S | re.M
)


class _Loader(yaml.SafeLoader):
    """Reads a mkdocs.yml without importing what its !!python tags name."""


_Loader.add_multi_constructor("", lambda loader, suffix, node: None)


def _read(path: Path) -> dict:
    return yaml.load(path.read_text(encoding="utf-8"), Loader=_Loader) or {}


def tutorial_authors(folder: str) -> list[str]:
    """The authors a tutorial lists in pages/markdown-sites.yml or its mkdocs.yml."""
    for site in _read(ROOT / "pages" / "markdown-sites.yml").get("sites", []):
        if site.get("folder") == folder:
            authors = site.get("authors") or []
            break
    else:
        config = ROOT / folder / "mkdocs.yml"
        tutorial = ((_read(config).get("extra") or {}).get("tutorial") or {}) if config.is_file() else {}
        authors = tutorial.get("authors") or []
    if isinstance(authors, str) or not all(isinstance(a, str) and GITHUB_LOGIN.match(a) for a in authors):
        raise PluginError(f"{folder}: authors must be a list of GitHub accounts, not {authors!r}")
    return authors


def authors_html(folder: str, authors: list[str], indent: str) -> str:
    items = []
    for login in authors[:SHOWN_AUTHORS]:
        name = html.escape(login)
        items.append(
            f'<li><a href="https://github.com/{name}" title="{name}">'
            f'<img src="https://github.com/{name}.png?size=56" alt="{name}" width="28" height="28" loading="lazy"></a></li>'
        )
    rest = authors[SHOWN_AUTHORS:]
    if rest:
        items.append(
            f'<li class="more"><a href="https://github.com/{REPOSITORY}/commits/main/{html.escape(folder)}" '
            f'title="{html.escape(", ".join(rest))}">+{len(rest)}</a></li>'
        )
    return (
        f'{indent}<div class="facts__authors"><dt>Authors</dt>'
        f'<dd class="authors"><ul>{"".join(items)}</ul></dd></div>\n'
    )


def _add_authors(card: re.Match) -> str:
    authors = tutorial_authors(card.group("path"))
    if not authors:
        return card.group(0)
    # The Authors row is the last of the card's facts.
    return re.sub(
        r"^([ ]*)</dl>",
        lambda end: authors_html(card.group("path"), authors, end.group(1) + "  ") + end.group(0),
        card.group(0),
        count=1,
        flags=re.M,
    )


def on_page_content(html_, page, config, files):
    if not page.is_homepage:
        return html_
    index = (Path(config.config_file_path).parent / "index.html").read_text(encoding="utf-8")
    return CARD.sub(_add_authors, index)


def on_serve(server, config, builder):
    # mkdocs serve: rebuild when a tutorial's authors change.
    server.watch(str(ROOT / "pages" / "markdown-sites.yml"))
    for config_file in ROOT.glob("*/mkdocs.yml"):
        server.watch(str(config_file))
    return server
