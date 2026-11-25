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

"""Copies the template and the worked example into the agent skill.

    python tools/build_agent_kit.py            # refresh the copies
    python tools/build_agent_kit.py --check    # fail if a copy is out of date

A skill is installed by copying its folder, so it must carry the files it
points at. The originals stay the single source: template/tck/e2e and
examples/digital-twin-registry/tck/e2e.
"""

from __future__ import annotations

import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ASSETS = ROOT / "agent-kit" / "skills" / "tractusx-e2e-test" / "assets"
COPIES = {
    ROOT / "template" / "tck" / "e2e": ASSETS / "template",
    ROOT / "examples" / "digital-twin-registry" / "tck" / "e2e": ASSETS / "example",
}


def files(folder: Path) -> dict[str, bytes]:
    return {p.relative_to(folder).as_posix(): p.read_bytes() for p in sorted(folder.rglob("*")) if p.is_file()}


def main() -> None:
    check = "--check" in sys.argv
    stale = []
    for source, target in COPIES.items():
        if target.exists() and files(source) == files(target):
            continue
        stale.append(target.relative_to(ROOT).as_posix())
        if not check:
            shutil.rmtree(target, ignore_errors=True)
            shutil.copytree(source, target)
    if check and stale:
        print(f"out of date: {', '.join(stale)}. Run python tools/build_agent_kit.py", file=sys.stderr)
        raise SystemExit(1)
    print("agent kit is up to date" if not stale else f"refreshed {', '.join(stale)}")


if __name__ == "__main__":
    main()
