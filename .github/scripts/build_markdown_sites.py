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


"""Builds the Markdown tutorials listed in pages/markdown-sites.yml as sites.

For each entry: copy the folder's Markdown files and pictures into a staging
folder, write a mkdocs.yml with the Tractus-X look (taken from the E2E testing
site), and build it into <out>/<folder>/. markdown_site_hook.py then fixes the
links that pointed to files the site does not carry. Not strict: these pages
were written for GitHub, so a broken anchor is reported, not fatal.

    python .github/scripts/build_markdown_sites.py --out _site
"""

import argparse
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[2]
PICTURES = {".png", ".jpg", ".jpeg", ".gif", ".svg", ".webp"}
SKIP = {"node_modules", ".git", ".terraform", "target", "dist"}
THEME = ROOT / "e2e-testing" / "docs" / "assets"
SITE_URL = "https://eclipse-tractusx.github.io/tutorial-resources/"

CONFIG = """\
site_name: {title}
site_description: {description}
site_url: {site_url}{folder}/
repo_url: https://github.com/eclipse-tractusx/tutorial-resources
repo_name: eclipse-tractusx/tutorial-resources
edit_uri: edit/main/{folder}/
docs_dir: docs
copyright: Copyright &copy; Contributors to the Eclipse Foundation · CC-BY-4.0

theme:
  name: material
  logo: site-assets/logo_tractus-x.svg
  favicon: site-assets/favicon.ico
  font:
    text: Manrope
    code: JetBrains Mono
  palette:
    - media: "(prefers-color-scheme: light)"
      scheme: default
      primary: custom
      accent: custom
      toggle:
        icon: material/weather-night
        name: Switch to dark mode
    - media: "(prefers-color-scheme: dark)"
      scheme: slate
      primary: custom
      accent: custom
      toggle:
        icon: material/weather-sunny
        name: Switch to light mode
  features:
    - navigation.sections
    - navigation.indexes
    - navigation.top
    - content.code.copy
    - search.highlight
    - toc.follow

extra_css:
  - site-assets/tractusx.css
  - site-assets/markdown-site.css

extra:
  # The logo leads back to the list of all tutorials.
  homepage: {site_url}
  tutorial_site:
    folder: {folder}
    root: {root}
    app: {app}
    app_image: {app_image}
    deprecated: {deprecated}

hooks:
  - {hook}

markdown_extensions:
  - admonition
  - attr_list
  - md_in_html
  - tables
  - github-callouts
  - pymdownx.details
  - pymdownx.highlight
  - pymdownx.inlinehilite
  - pymdownx.superfences:
      custom_fences:
        - name: mermaid
          class: mermaid
          format: !!python/name:pymdownx.superfences.fence_code_format
  - pymdownx.tasklist:
      custom_checkbox: true
  - toc:
      permalink: true

plugins:
  - search

validation:
  nav:
    omitted_files: info
    not_found: info
  links:
    not_found: info
    anchors: info
    absolute_links: info
    unrecognized_links: info
"""


def title_of(page: Path) -> str:
    for line in page.read_text(encoding="utf-8", errors="replace").splitlines():
        if line.startswith("# "):
            return line[2:].strip()
    return page.stem.replace("_", " ").replace("-", " ").capitalize()


def stage(folder: Path, docs: Path) -> list[Path]:
    pages = []
    for path in sorted(folder.rglob("*")):
        relative = path.relative_to(folder)
        if any(part in SKIP or part.startswith(".") for part in relative.parts) or not path.is_file():
            continue
        if path.suffix.lower() == ".md" or path.suffix.lower() in PICTURES:
            target = docs / relative
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(path, target)
            if path.suffix.lower() == ".md":
                pages.append(relative)
    return pages


def contents_page(docs: Path, pages: list[Path], site: dict) -> None:
    """A home page for a folder without a README: what it is and its pages."""
    lines = [f"# {site['title']}", "", site.get("description", ""), ""]
    for page in pages:
        link = page.as_posix().replace(" ", "%20")
        lines.append(f"- [{title_of(docs / page)}]({link})")
    (docs / "index.md").write_text("\n".join(lines) + "\n", encoding="utf-8")


def build(site: dict, out: Path, work: Path) -> None:
    folder = ROOT / site["folder"]
    if not folder.is_dir():
        sys.exit(f"{site['folder']}: no such folder")
    base = work / site["folder"]
    docs = base / "docs"
    pages = stage(folder, docs)
    if not pages:
        sys.exit(f"{site['folder']}: no Markdown files")
    if not (docs / "README.md").exists() and not (docs / "index.md").exists():
        contents_page(docs, pages, site)
    assets = docs / "site-assets"
    assets.mkdir()
    for name in ("logo_tractus-x.svg", "favicon.ico", "stylesheets/tractusx.css"):
        shutil.copy2(THEME / name, assets / Path(name).name)
    shutil.copy2(Path(__file__).with_name("markdown-site.css"), assets / "markdown-site.css")
    if site.get("app_image"):
        image = ROOT / site["app_image"]
        shutil.copy2(image, assets / ("app-preview" + image.suffix))
    (base / "mkdocs.yml").write_text(
        CONFIG.format(
            title=json_string(site["title"]),
            description=json_string(site.get("description", "")),
            site_url=SITE_URL,
            folder=site["folder"],
            root=json_string(str(ROOT)),
            app=json_string(site.get("app", "")),
            app_image=json_string(site.get("app_image", "")),
            deprecated=str(bool(site.get("deprecated"))).lower(),
            hook=json_string(str(Path(__file__).with_name("markdown_site_hook.py"))),
        ),
        encoding="utf-8",
    )
    subprocess.run(
        [sys.executable, "-m", "mkdocs", "build", "--quiet", "--config-file", str(base / "mkdocs.yml"),
         "--site-dir", str(out / site["folder"])],
        check=True,
    )
    if not (out / site["folder"] / "index.html").exists():
        sys.exit(f"{site['folder']}: the build has no home page")
    print(f"{site['folder']}: {len(pages)} pages")


def json_string(value: str) -> str:
    # YAML accepts JSON strings, which keeps quotes and colons safe.
    import json
    return json.dumps(value)


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--out", required=True, type=Path, help="the website folder, _site")
    parser.add_argument("--only", help="build this folder only")
    args = parser.parse_args()
    sites = yaml.safe_load((ROOT / "pages" / "markdown-sites.yml").read_text(encoding="utf-8"))["sites"]
    with tempfile.TemporaryDirectory() as work:
        for site in sites:
            if not args.only or site["folder"] == args.only:
                build(site, args.out.resolve(), Path(work))


if __name__ == "__main__":
    main()
