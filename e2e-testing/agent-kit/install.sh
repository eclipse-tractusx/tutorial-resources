#!/usr/bin/env bash
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

# Installs the tractusx-e2e-test skill for your AI coding assistant.
#
#   ./install.sh claude     Claude Code (terminal, VS Code extension, desktop app)
#   ./install.sh copilot    GitHub Copilot in VS Code
#   ./install.sh cursor     Cursor
#   ./install.sh codex      OpenAI Codex
#   ./install.sh all        all of the above
#
# It copies one folder into your home directory and changes nothing else.
# Run it again to update. To remove the skill, delete the folder it names.

set -euo pipefail

here="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
skill="tractusx-e2e-test"

copy_skill() {
  local target="$1/${skill}"
  mkdir -p "$1"
  rm -rf "${target}"
  cp -R "${here}/skills/${skill}" "${target}"
  echo "skill  -> ${target}"
}

install_claude() {
  copy_skill "${HOME}/.claude/skills"
  mkdir -p "${HOME}/.claude/agents"
  cp "${here}/agents/e2e-test-author.md" "${HOME}/.claude/agents/e2e-test-author.md"
  echo "agent  -> ${HOME}/.claude/agents/e2e-test-author.md"
}

case "${1:-}" in
  claude)  install_claude ;;
  copilot) copy_skill "${HOME}/.copilot/skills" ;;
  cursor)  copy_skill "${HOME}/.cursor/skills" ;;
  codex)   copy_skill "${HOME}/.agents/skills" ;;
  all)
    install_claude
    copy_skill "${HOME}/.copilot/skills"
    copy_skill "${HOME}/.cursor/skills"
    copy_skill "${HOME}/.agents/skills"
    ;;
  *)
    echo "usage: $0 claude | copilot | cursor | codex | all" >&2
    exit 2
    ;;
esac

echo "Done. Restart your assistant, or start a new chat, so that it finds the skill."
