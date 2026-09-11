#!/usr/bin/env bash
set -Eeuo pipefail

REPO="Santi-PdR/Siege"
REPO_URL="https://github.com/Santi-PdR/Siege.git"
BRANCH="main"
MODS_DIR="/home/Santipdr/.sklauncher/instances/test-1/mods"
STAGED_JAR=""
WORK_DIR="$(mktemp -d -t siege-install-XXXXXX)"

cleanup() {
    if [[ -n "$STAGED_JAR" ]]; then rm -f -- "$STAGED_JAR"; fi
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
# Stage and compare before moving any installed version out of the mods directory.
STAGED_JAR="$(mktemp "$MODS_DIR/.siege-stage-XXXXXX")"
install -m 0644 -- "$JAR_FILE" "$STAGED_JAR"
if [[ ! -s "$STAGED_JAR" ]] || ! cmp -s -- "$JAR_FILE" "$STAGED_JAR"; then
    echo "ERROR: la copia no coincide. Se conserva la version instalada." >&2
    exit 1
fi
BACKUP_DIR="$(mktemp -d "${MODS_DIR%/mods}/siege-backup-XXXXXX")"
mapfile -d '' OLD_JARS < <(find "$MODS_DIR" -maxdepth 1 -type f -name 'siege-menu-*.jar' -print0)
for old_jar in "${OLD_JARS[@]}"; do
    cp -p -- "$old_jar" "$BACKUP_DIR/"
done
INSTALLED_JAR="$MODS_DIR/$(basename "$JAR_FILE")"
mv -f -- "$STAGED_JAR" "$INSTALLED_JAR"
STAGED_JAR=""
for old_jar in "${OLD_JARS[@]}"; do
    if [[ "$old_jar" != "$INSTALLED_JAR" ]]; then rm -- "$old_jar"; fi
done
if (( ${#OLD_JARS[@]} > 0 )); then
    echo "Version anterior guardada en: $BACKUP_DIR"
else
    rmdir -- "$BACKUP_DIR"
fi

echo
echo "SIEGE instalado correctamente:"
echo "$INSTALLED_JAR"

