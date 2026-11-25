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


"""MkDocs hook for the Markdown tutorial sites (build_markdown_sites.py).

The pages were written for GitHub, so links may point to files the site does
not carry: a Postman collection, a Terraform module, test data, another
folder. Those now open on GitHub; pictures outside the tutorial load from
GitHub too. File names with spaces are encoded. Two GitHub habits that
Python-Markdown reads differently are evened out: a list right under a
paragraph, without a blank line, and lists nested by two spaces. A deprecated
tutorial gets a note on every page, and a tutorial with a web app a
"Try the app" button on its home page: over the app's screenshot, out of
focus, when the mapping gives one.
"""

import posixpath
import re
from pathlib import Path
from urllib.parse import quote, unquote

REPO = "https://github.com/eclipse-tractusx/tutorial-resources"
RAW = "https://raw.githubusercontent.com/eclipse-tractusx/tutorial-resources/main"
PICTURES = {".png", ".jpg", ".jpeg", ".gif", ".svg", ".webp"}
SITE = "https://eclipse-tractusx.github.io/tutorial-resources/"

# [text](target "title") and <img src="target"> / <a href="target">
MD_LINK = re.compile(r"(\]\()(<[^>]+>|[^)\s]+(?: [^)\s\"]+)*)((?:\s+\"[^\"]*\")?\))")
HTML_LINK = re.compile(r"""((?:src|href)=["'])([^"']+)(["'])""")

DEPRECATED = (
    '!!! warning "Deprecated"\n'
    "    This tutorial is kept for reference. It may not match the current Eclipse\n"
    "    Tractus-X release. [All tutorials]({site})\n\n"
)
APP = "[Try the app &rarr;]({url}){{ .md-button .md-button--primary }}\n\n"
APP_PREVIEW = (
    '<a class="tx-app-preview" href="{url}"><img src="{image}" alt="">'
    '<span class="md-button md-button--primary">&#9654;&ensp;Try the app</span></a>\n\n'
)
LIST_ITEM = re.compile(r"^( *)([*+-]|\d+[.)]) ")
FENCE = re.compile(r"^ *(```|~~~)")


def _github_lists(markdown: str) -> str:
    """A blank line before a list that follows a paragraph; four spaces per level."""
    lines = markdown.split("\n")
    in_fence = False
    two_space = any(
        (m := LIST_ITEM.match(line)) and len(m.group(1)) % 4 == 2 for line in lines
    )
    out = []
    for line in lines:
        if FENCE.match(line):
            in_fence = not in_fence
        item = None if in_fence else LIST_ITEM.match(line)
        if item:
            if two_space and item.group(1):
                line = " " * (len(item.group(1)) * 2) + line[len(item.group(1)):]
            previous = out[-1] if out else ""
            if previous.strip() and not LIST_ITEM.match(previous) and not previous.startswith((" ", "#", "|", ">", "<")):
                out.append("")
        out.append(line)
    return "\n".join(out)


def _rewrite(target: str, page_dir: str, info: dict, files) -> str:
    target = target.strip("<>")
    if re.match(r"^[a-z][a-z0-9+.-]*:|^#|^/", target, re.I):
        return target
    path, _, anchor = target.partition("#")
    if not path:
        return target
    in_docs = posixpath.normpath(posixpath.join(page_dir, unquote(path)))
    suffix = Path(in_docs).suffix.lower()
    if not in_docs.startswith("../") and (suffix == ".md" or suffix in PICTURES) and files.get_file_from_path(in_docs):
        return quote(unquote(path)) + ("#" + anchor if anchor else "")
    in_repo = posixpath.normpath(posixpath.join(info["folder"], in_docs))
    if in_repo.startswith("../"):
        return target
    if suffix in PICTURES:
        return f"{RAW}/{quote(in_repo)}"
    kind = "tree" if (Path(info["root"]) / in_repo).is_dir() else "blob"
    return f"{REPO}/{kind}/main/{quote(in_repo)}" + ("#" + anchor if anchor else "")


def _title_line(lines: list[str]) -> int | None:
    """The line of the page's first "# " heading, outside code and comments."""
    in_fence = in_comment = False
    for number, line in enumerate(lines):
        if FENCE.match(line):
            in_fence = not in_fence
        elif not in_fence:
            if "<!--" in line and "-->" not in line.split("<!--", 1)[1]:
                in_comment = True
            elif in_comment and "-->" in line:
                in_comment = False
            elif not in_comment and line.startswith("# "):
                return number
    return None


def on_page_markdown(markdown: str, page, config, files) -> str:
    info = config.extra["tutorial_site"]
    page_dir = posixpath.dirname(page.file.src_uri)
    markdown = MD_LINK.sub(lambda m: m.group(1) + _rewrite(m.group(2), page_dir, info, files) + m.group(3), markdown)
    markdown = HTML_LINK.sub(lambda m: m.group(1) + _rewrite(m.group(2), page_dir, info, files) + m.group(3), markdown)
    markdown = _github_lists(markdown)
    prefix = ""
    if info.get("deprecated"):
        prefix += DEPRECATED.format(site=SITE)
    if info.get("app") and page.is_homepage:
        if info.get("app_image"):
            image = posixpath.relpath("site-assets/app-preview" + Path(info["app_image"]).suffix, page_dir or ".")
            prefix += APP_PREVIEW.format(url=SITE + info["app"], image=image)
        else:
            prefix += APP.format(url=SITE + info["app"])
    if prefix:
        # After the page's title, so the title stays first.
        lines = markdown.split("\n")
        at = _title_line(lines)
        if at is None:
            markdown = prefix + markdown
        else:
            markdown = "\n".join(lines[: at + 1] + ["", prefix.rstrip("\n"), ""] + lines[at + 1:])
    return markdown
