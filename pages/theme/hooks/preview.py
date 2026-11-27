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

"""Builds a site as the preview of a pull request, when PAGES_PREVIEW_PR is set.

.github/workflows/pages-preview.yml sets it to the pull request's number. The
site is then built for

    https://eclipse-tractusx.github.io/tutorial-resources/pr-preview/<number>/...

instead of the published address: its site_url and the logo's link to the
home page move below pr-preview/<number>/, and main.html shows a banner and
asks search engines not to index the page. Without the variable nothing
changes. List this hook before about.py, which reads site_url.
"""

import os
import re

PUBLISHED = "https://eclipse-tractusx.github.io/tutorial-resources/"
REPOSITORY = "https://github.com/eclipse-tractusx/tutorial-resources"


def preview_url(number: str) -> str:
    return f"{PUBLISHED}pr-preview/{number}/"


def _move(url: str, base: str) -> str:
    return base + url[len(PUBLISHED):] if url and url.startswith(PUBLISHED) else url


def on_config(config):
    number = os.environ.get("PAGES_PREVIEW_PR", "").strip()
    if not number:
        return config
    if not re.fullmatch(r"[1-9][0-9]{0,6}", number):
        raise ValueError(f"PAGES_PREVIEW_PR must be a pull request number, not {number!r}")
    base = preview_url(number)
    config.site_url = _move(config.site_url, base)
    extra = config.extra
    if extra.get("homepage"):
        extra["homepage"] = _move(extra["homepage"], base)
    extra["preview"] = {
        "number": number,
        "home": base,
        "pull_request": f"{REPOSITORY}/pull/{number}",
        "published": PUBLISHED,
    }
    return config
