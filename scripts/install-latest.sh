#!/usr/bin/env bash
set -Eeuo pipefail

REPO="Santi-PdR/Siege"
BRANCH="main"
MODS_DIR="${1:-/home/Santipdr/.sklauncher/instances/test-1/mods}"
STAGED_JAR=""
WORK_DIR="$(mktemp -d -t siege-install-XXXXXX)"

cleanup() {
    if [[ -n "$STAGED_JAR" ]]; then rm -f -- "$STAGED_JAR"; fi
    rm -rf -- "$WORK_DIR"
}
trap cleanup EXIT

echo "SIEGE // Descargando build validado desde GitHub..."
for dependency in gh python3 sha256sum; do
    if ! command -v "$dependency" >/dev/null 2>&1; then
        echo "ERROR: falta $dependency. Instalalo antes de continuar." >&2
        exit 1
    fi
done
gh auth status >/dev/null
# Pin metadata and artifact to one immutable repository revision.
REVISION="$(gh api "repos/$REPO/commits/$BRANCH" --jq .sha)"
gh api "repos/$REPO/contents/dist/manifest.json?ref=$REVISION" -H 'Accept: application/vnd.github.raw+json' > "$WORK_DIR/manifest.json"
mapfile -t METADATA < <(python3 - "$WORK_DIR/manifest.json" <<'PYMETA'
import json, re, sys
m = json.load(open(sys.argv[1]))
assert re.fullmatch(r"siege-menu-[0-9]+\.[0-9]+\.[0-9]+\.jar", m["jar"]), "Invalid jar name"
assert re.fullmatch(r"[0-9a-f]{64}", m["sha256"]), "Invalid SHA256"
assert re.fullmatch(r"[0-9a-f]{40}", m["commit"]), "Invalid revision"
print(m["jar"])
print(m["sha256"])
print(m["commit"])
PYMETA
)
if (( ${#METADATA[@]} != 3 )); then echo "ERROR: manifiesto inválido." >&2; exit 1; fi
JAR_FILE="$WORK_DIR/${METADATA[0]}"
gh api "repos/$REPO/contents/dist/${METADATA[0]}?ref=$REVISION" -H 'Accept: application/vnd.github.raw+json' > "$JAR_FILE"
printf '%s  %s\n' "${METADATA[1]}" "$JAR_FILE" | sha256sum --check --status
python3 - "$JAR_FILE" <<'PYCHECK'
import sys, zipfile
with zipfile.ZipFile(sys.argv[1]) as archive:
    assert archive.testzip() is None, "Corrupt JAR"
    assert "META-INF/mods.toml" in archive.namelist(), "Not a Forge mod"
PYCHECK
echo "Build verificado: ${METADATA[2]}"

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

