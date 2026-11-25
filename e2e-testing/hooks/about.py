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

"""An "About this tutorial" button in the header of every page.

It opens a panel with the tutorial's version and its history (extra.tutorial
in mkdocs.yml), when it was added and last updated (the git history of this
folder), and its authors (the GitHub accounts with commits in this folder,
from the GitHub API; set GITHUB_TOKEN to avoid its rate limit). What cannot
be found is left out: a shallow clone has no dates, no network no avatars.
"""

import html
import json
import os
import subprocess
from collections import Counter
from datetime import date
from pathlib import Path
from urllib.error import URLError
from urllib.request import Request, urlopen

PALETTE = '<form class="md-header__option" data-md-component="palette">'
SHOWN_AUTHORS = 8

_panel = None


def _git(folder: Path, *args: str) -> str:
    return subprocess.run(
        ["git", *args], cwd=folder, check=True, capture_output=True, text=True
    ).stdout.strip()


def _dates(folder: Path) -> tuple[str, str] | None:
    try:
        if _git(folder, "rev-parse", "--is-shallow-repository") == "true":
            return None
        dates = _git(folder, "log", "--format=%as", "--", ".").split()
    except (OSError, subprocess.CalledProcessError):
        return None
    if not dates:
        return None
    return tuple(date.fromisoformat(d).strftime("%b %Y") for d in (dates[-1], dates[0]))


def _authors(repository: str, path: str) -> list[tuple[str, str]]:
    headers = {"Accept": "application/vnd.github+json", "User-Agent": "tutorial-resources-docs"}
    if os.environ.get("GITHUB_TOKEN"):
        headers["Authorization"] = "Bearer " + os.environ["GITHUB_TOKEN"]
    counts, avatars, page = Counter(), {}, 1
    try:
        while True:
            url = f"https://api.github.com/repos/{repository}/commits?path={path}&per_page=100&page={page}"
            with urlopen(Request(url, headers=headers), timeout=20) as response:
                commits = json.load(response)
            for commit in commits:
                author = commit.get("author") or {}
                login = author.get("login")
                if login and author.get("type") != "Bot" and not login.endswith("[bot]"):
                    counts[login] += 1
                    avatars[login] = author["avatar_url"]
            if len(commits) < 100:
                break
            page += 1
    except (URLError, OSError, ValueError):
        return []
    return [(login, avatars[login]) for login, _ in counts.most_common()]


def _build_panel(config) -> str:
    folder = Path(config.config_file_path).parent
    info = (config.extra or {}).get("tutorial", {})
    repository = config.repo_url.rstrip("/").split("github.com/")[-1]
    path = folder.name
    esc = html.escape

    facts = []
    if info.get("version"):
        facts.append(("Version", "v" + esc(str(info["version"]))))
    dates = _dates(folder)
    if dates:
        facts += [("Added", dates[0]), ("Updated", dates[1])]
    rows = "".join(f"<div><dt>{k}</dt><dd>{v}</dd></div>" for k, v in facts)

    authors = _authors(repository, path)
    people = ""
    if authors:
        items = "".join(
            f'<li><a href="https://github.com/{esc(login)}"><img src="{esc(avatar)}&amp;s=56" alt="" '
            f'width="28" height="28" loading="lazy">{esc(login)}</a></li>'
            for login, avatar in authors[:SHOWN_AUTHORS]
        )
        if len(authors) > SHOWN_AUTHORS:
            items += f'<li class="tx-about__more">and {len(authors) - SHOWN_AUTHORS} more</li>'
        people = f'<h3>Authors</h3><ul class="tx-about__authors">{items}</ul>'

    history = ""
    if info.get("history"):
        items = "".join(
            f"<li><b>{esc(str(h['version']))}</b> <span>{esc(str(h['date']))}</span> {esc(str(h['note']))}</li>"
            for h in info["history"]
        )
        history = f'<h3>Versions</h3><ol class="tx-about__history" reversed>{items}</ol>'

    source = f"{config.repo_url.rstrip('/')}/tree/main/{path}"
    home = config.site_url.rstrip("/").rsplit("/", 1)[0] + "/"
    return (
        '<div id="tx-about" class="tx-about" popover>'
        '<div class="tx-about__head"><h2>About this tutorial</h2>'
        '<button type="button" class="tx-about__close" popovertarget="tx-about" popovertargetaction="hide" '
        'aria-label="Close">&times;</button></div>'
        f'<dl class="tx-about__facts">{rows}</dl>{people}{history}'
        f'<p class="tx-about__links"><a href="{esc(source)}">Source on GitHub</a> · '
        f'<a href="{esc(home)}">All tutorials</a> · '
        '<a href="https://creativecommons.org/licenses/by/4.0/">CC-BY-4.0</a></p>'
        "</div>"
    )


def on_config(config):
    global _panel
    _panel = _build_panel(config)
    return config


def on_post_page(output: str, page, config) -> str:
    if PALETTE not in output or "</body>" not in output:
        return output
    button = (
        '<button type="button" class="md-header__button md-icon tx-about-button" popovertarget="tx-about" '
        f'title="About this tutorial" aria-label="About this tutorial">About</button>'
    )
    # Inside the light/dark switch's form: one control group, laid out and
    # spaced by the same flex box, whatever Material does around it.
    output = output.replace(PALETTE, PALETTE + button, 1)
    return output.replace("</body>", _panel + "</body>", 1)
