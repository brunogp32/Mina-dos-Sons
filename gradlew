#!/usr/bin/env sh
set -e
GRADLE_VERSION=8.10.2
BASE="${HOME}/.gradle/codex-dists"
GRADLE_DIR="${BASE}/gradle-${GRADLE_VERSION}"
GRADLE_BIN="${GRADLE_DIR}/bin/gradle"
if [ ! -x "${GRADLE_BIN}" ]; then
  mkdir -p "${BASE}"
  ZIP="${BASE}/gradle-${GRADLE_VERSION}-bin.zip"
  if command -v curl >/dev/null 2>&1; then
    curl -L "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "${ZIP}"
  else
    wget "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -O "${ZIP}"
  fi
  unzip -o "${ZIP}" -d "${BASE}"
fi
exec "${GRADLE_BIN}" "$@"
