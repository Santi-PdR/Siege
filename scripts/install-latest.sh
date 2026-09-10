#!/usr/bin/env bash
set -Eeuo pipefail

REPO="Santi-PdR/Siege"
REPO_URL="https://github.com/Santi-PdR/Siege.git"
BRANCH="main"
MODS_DIR="/home/Santipdr/.sklauncher/instances/test-1/mods"
WORK_DIR="$(mktemp -d -t siege-install-XXXXXX)"

cleanup() {
    rm -rf -- "$WORK_DIR"
}
trap cleanup EXIT

echo "SIEGE // Descargando build validado desde GitHub..."
if command -v gh >/dev/null 2>&1 && gh auth status >/dev/null 2>&1; then
    gh repo clone "$REPO" "$WORK_DIR" -- --depth 1 --branch "$BRANCH"
else
    git clone --depth 1 --branch "$BRANCH" "$REPO_URL" "$WORK_DIR"
fi

JAR_FILE="$(find "$WORK_DIR/dist" -maxdepth 1 -type f     -name 'siege-menu-*.jar'     ! -name '*-sources.jar'     ! -name '*-javadoc.jar'     -print -quit)"

if [[ -z "$JAR_FILE" ]]; then
    echo "ERROR: no se encontro un JAR validado dentro de dist/." >&2
    exit 1
fi

mkdir -p -- "$MODS_DIR"
find "$MODS_DIR" -maxdepth 1 -type f -name 'siege-menu-*.jar' -delete
install -m 0644 -- "$JAR_FILE" "$MODS_DIR/"

INSTALLED_JAR="$MODS_DIR/$(basename "$JAR_FILE")"
if [[ ! -s "$INSTALLED_JAR" ]]; then
    echo "ERROR: la copia instalada no existe o esta vacia." >&2
    exit 1
fi

echo
echo "SIEGE instalado correctamente:"
echo "$INSTALLED_JAR"
