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

# Keeps the pull request previews on the pr-previews branch, one folder per
# pull request: <number>/ holds the site built for
# https://eclipse-tractusx.github.io/tutorial-resources/pr-preview/<number>/.
# pages.yml copies the branch into the site it deploys.
#
#   pr_previews.sh add <number> <site folder>   replace <number>/ with the site
#   pr_previews.sh remove <number>              delete <number>/
#
# It only ever writes <number>/ (and a README on a new branch), refuses
# symbolic links, and retries when someone else pushed meanwhile. It needs
# GITHUB_TOKEN and GITHUB_REPOSITORY, or PREVIEWS_REMOTE (any git remote, for
# tests).

set -euo pipefail

BRANCH="${PREVIEWS_BRANCH:-pr-previews}"

die() { echo "pr_previews.sh: $*" >&2; exit 1; }

mode="${1:-}"
number="${2:-}"
[[ "$mode" == add || "$mode" == remove ]] || die "usage: pr_previews.sh add <number> <site folder> | remove <number>"
[[ "$number" =~ ^[1-9][0-9]{0,6}$ ]] || die "not a pull request number: '$number'"

site=""
if [[ "$mode" == add ]]; then
  [[ -n "${3:-}" && -d "$3" ]] || die "no site folder: '${3:-}'"
  site="$(cd "$3" && pwd)"
  [[ -f "$site/index.html" ]] || die "$site has no index.html"
  [[ -z "$(find "$site" -type l -print -quit)" ]] || die "$site contains symbolic links"
fi

if [[ -n "${PREVIEWS_REMOTE:-}" ]]; then
  remote="$PREVIEWS_REMOTE"
else
  [[ -n "${GITHUB_TOKEN:-}" && -n "${GITHUB_REPOSITORY:-}" ]] || die "GITHUB_TOKEN and GITHUB_REPOSITORY are needed"
  remote="https://x-access-token:${GITHUB_TOKEN}@github.com/${GITHUB_REPOSITORY}.git"
fi

work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT
git -C "$work" init -q
git -C "$work" remote add origin "$remote"
g() { git -C "$work" -c user.name="github-actions[bot]" \
  -c user.email="41898282+github-actions[bot]@users.noreply.github.com" "$@"; }

for attempt in 1 2 3 4 5; do
  if g ls-remote --exit-code --heads origin "$BRANCH" >/dev/null; then
    g fetch -q --depth 1 origin "$BRANCH"
    g checkout -q -B "$BRANCH" FETCH_HEAD
    g reset -q --hard FETCH_HEAD
    g clean -qfdx
  else
    [[ "$mode" == add ]] || { echo "No $BRANCH branch: nothing to remove."; exit 0; }
    g checkout -q --orphan "new-${BRANCH}-${attempt}"
    g rm -rqf --ignore-unmatch . >/dev/null
    g clean -qfdx
    cat > "$work/README.md" <<'EOF'
# Pull request previews

Written by .github/workflows/pages-preview.yml: one folder per pull request,
published by pages.yml at
https://eclipse-tractusx.github.io/tutorial-resources/pr-preview/<number>/
and removed when the pull request is closed. Do not edit by hand.
EOF
  fi

  rm -rf "${work:?}/${number}"
  if [[ "$mode" == add ]]; then
    mkdir "$work/$number"
    cp -R "$site/." "$work/$number/"
    message="Preview of pull request #${number}"
  else
    message="Remove the preview of pull request #${number}"
  fi

  g add -A
  if g diff --cached --quiet; then
    echo "The preview of #${number} is already up to date."
    exit 0
  fi
  g commit -q -m "$message"
  if g push -q origin "HEAD:refs/heads/${BRANCH}"; then
    echo "${message}: pushed to ${BRANCH}."
    exit 0
  fi
  echo "Push refused (attempt ${attempt}); someone else pushed meanwhile. Retrying." >&2
  sleep $((attempt * 5))
done
die "could not update ${BRANCH} after 5 attempts"
