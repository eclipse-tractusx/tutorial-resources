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

"""Builds docs/downloads/e2e-test-template.zip from template/tck/e2e.

    python tools/build_template_zip.py            # write the archive
    python tools/build_template_zip.py --check    # fail if it is out of date

The IDE opens the archive with "Open File". File times are fixed, so the same
template always gives the same bytes.
"""

from __future__ import annotations

import io
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SOURCE = ROOT / "template" / "tck" / "e2e"
TARGET = ROOT / "docs" / "downloads" / "e2e-test-template.zip"


def build() -> bytes:
    buffer = io.BytesIO()
    with zipfile.ZipFile(buffer, "w", zipfile.ZIP_DEFLATED) as archive:
        for path in sorted(p for p in SOURCE.rglob("*") if p.is_file()):
            info = zipfile.ZipInfo(path.relative_to(SOURCE).as_posix(), date_time=(2026, 1, 1, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            info.external_attr = 0o644 << 16
            archive.writestr(info, path.read_bytes())
    return buffer.getvalue()


def main() -> None:
    data = build()
    if "--check" in sys.argv:
        if not TARGET.exists() or TARGET.read_bytes() != data:
            print(f"{TARGET.relative_to(ROOT)} is out of date: run python tools/build_template_zip.py", file=sys.stderr)
            raise SystemExit(1)
        print("template archive is up to date")
        return
    TARGET.parent.mkdir(parents=True, exist_ok=True)
    TARGET.write_bytes(data)
    print(f"wrote {TARGET.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
