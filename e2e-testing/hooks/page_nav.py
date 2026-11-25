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

"""MkDocs hook: the line of links at the end of a page becomes a row of buttons.

Every page ends with a line such as

    [← 1 · Start here](01-start-here.md) · [Home](../index.md) · [Next: 3 · Choose your AI →](03-choose-your-ai.md)

which reads fine on GitHub. On the site, the link with "←" becomes the
"Previous" button on the left, the link with "→" the "Next" button on the
right, and the other links sit in the middle. Lines without an arrow (the
breadcrumbs at the top of some pages) stay as they are.
"""

import html
import re

# A paragraph made only of links joined by " · ", with the rule above it.
_NAV_LINE = re.compile(
    r"(?:<hr\s*/?>\s*)?<p>(?P<links>(?:<a [^>]*>[^<]*</a>(?:\s*·\s*)?)+)</p>\n?"
)
_LINK = re.compile(r'<a (?P<attrs>[^>]*)href="(?P<href>[^"]*)"(?P<rest>[^>]*)>(?P<text>[^<]*)</a>')

_ARROW_LEFT = (
    '<svg class="tx-pager__arrow" viewBox="0 0 24 24" aria-hidden="true">'
    '<path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20z"/></svg>'
)
_ARROW_RIGHT = (
    '<svg class="tx-pager__arrow" viewBox="0 0 24 24" aria-hidden="true">'
    '<path d="M4 11h12.17l-5.59-5.59L12 4l8 8-8 8-1.41-1.41L16.17 13H4z"/></svg>'
)
_HOUSE = (
    '<svg viewBox="0 0 24 24" aria-hidden="true">'
    '<path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/></svg>'
)


def _button(kind: str, href: str, title: str) -> str:
    label = "Previous" if kind == "prev" else "Next"
    arrow = _ARROW_LEFT if kind == "prev" else _ARROW_RIGHT
    return (
        f'<a class="tx-pager__link tx-pager__link--{kind}" href="{href}" rel="{kind}">'
        f"{arrow}"
        f'<span class="tx-pager__text">'
        f'<span class="tx-pager__dir">{label}</span>'
        f'<span class="tx-pager__title">{title}</span>'
        f"</span></a>"
    )


def _middle(href: str, text: str) -> str:
    if html.unescape(text).strip().lower() == "home":
        return f'<a class="tx-pager__home" href="{href}">{_HOUSE}<span>Home</span></a>'
    return f'<a class="tx-pager__home" href="{href}"><span>{text}</span></a>'


def on_page_content(content: str, **_kwargs) -> str:
    matches = [
        m for m in _NAV_LINE.finditer(content) if "←" in m.group("links") or "→" in m.group("links")
    ]
    if not matches:
        return content
    match = matches[-1]
    links = [m.groupdict() for m in _LINK.finditer(match.group("links"))]
    prev = next_ = None
    middle = []
    for link in links:
        text = link["text"].strip()
        if text.startswith("←"):
            prev = (link["href"], text.removeprefix("←").strip())
        elif text.endswith("→"):
            title = text.removesuffix("→").strip()
            next_ = (link["href"], re.sub(r"^Next:\s*", "", title))
        else:
            middle.append(_middle(link["href"], text))
    if prev is None and next_ is None:
        return content

    left = _button("prev", *prev) if prev else '<span class="tx-pager__spacer"></span>'
    right = _button("next", *next_) if next_ else '<span class="tx-pager__spacer"></span>'
    centre = f'<div class="tx-pager__middle">{"".join(middle)}</div>'
    nav = f'<nav class="tx-pager" aria-label="Page navigation">{left}{centre}{right}</nav>'
    return content[: match.start()] + nav + content[match.end() :]
