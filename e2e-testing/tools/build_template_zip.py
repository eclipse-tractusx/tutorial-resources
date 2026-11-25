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

"""Builds the archives the IDE opens with "Open File":

- docs/downloads/e2e-test-template.zip from template/tck/e2e (chapter 4)
- docs/downloads/hello-start.zip from examples/hello/start (chapter 3)


    python tools/build_template_zip.py            # write the archive
    python tools/build_template_zip.py --check    # fail if it is out of date

File times are fixed, so the same files always give the same bytes.
"""

from __future__ import annotations

import io
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ARCHIVES = {
    ROOT / "docs" / "downloads" / "e2e-test-template.zip": ROOT / "template" / "tck" / "e2e",
    ROOT / "docs" / "downloads" / "hello-start.zip": ROOT / "examples" / "hello" / "start",
}


def build(source: Path) -> bytes:
    buffer = io.BytesIO()
    with zipfile.ZipFile(buffer, "w", zipfile.ZIP_DEFLATED) as archive:
        for path in sorted(p for p in source.rglob("*") if p.is_file()):
            info = zipfile.ZipInfo(path.relative_to(source).as_posix(), date_time=(2026, 1, 1, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            info.external_attr = 0o644 << 16
            archive.writestr(info, path.read_bytes())
    return buffer.getvalue()


def main() -> None:
    stale = False
    for target, source in ARCHIVES.items():
        data = build(source)
        if "--check" in sys.argv:
            if not target.exists() or target.read_bytes() != data:
                print(f"{target.relative_to(ROOT)} is out of date: run python tools/build_template_zip.py", file=sys.stderr)
                stale = True
            continue
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_bytes(data)
        print(f"wrote {target.relative_to(ROOT)}")
    if stale:
        raise SystemExit(1)
    if "--check" in sys.argv:
        print("archives are up to date")


if __name__ == "__main__":
    main()
