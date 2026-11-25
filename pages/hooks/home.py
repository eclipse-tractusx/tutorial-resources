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
script), not Markdown: this puts it in as the page, untouched, inside the
shared theme's header and footer (template home.html)."""

from pathlib import Path


def on_page_content(html, page, config, files):
    if not page.is_homepage:
        return html
    return (Path(config.config_file_path).parent / "index.html").read_text(encoding="utf-8")
