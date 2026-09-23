#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BG_DIR="$ROOT/src/main/resources/assets/siege/textures/gui/backgrounds"
MUSIC_SOURCE_DIR="$ROOT/assets-source/music-full"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

ARCTIC_URL='https://tr.rbxcdn.com/180DAY-f57d526d7c860645d35d5f1f6ac81be3/768/432/Image/Webp/noFilter'
COASTAL_URL='https://tr.rbxcdn.com/180DAY-436fbbe5a341fdb4293653da659ebe0a/768/432/Image/Webp/noFilter'
ARC_ENEMY_URL='https://soundcloud.com/potoe-50708490/arc-enemy'

mkdir -p "$BG_DIR" "$MUSIC_SOURCE_DIR"

retry_curl() {
  local url="$1"
  local output="$2"
  curl --fail --location --silent --show-error \
    --retry 4 --retry-delay 2 --retry-all-errors \
    -A 'SIEGE/5.10 noncommercial fan project media fetch' \
    "$url" -o "$output"
}

echo '→ DVN: downloading official Roblox thumbnails...'
retry_curl "$ARCTIC_URL" "$TMP/arctic.webp"
retry_curl "$COASTAL_URL" "$TMP/coastal.webp"

python3 - "$TMP" "$BG_DIR" <<'PY'
from pathlib import Path
from PIL import Image
import sys

tmp = Path(sys.argv[1])
out = Path(sys.argv[2])
for source_name, target_name in (
    ("arctic.webp", "dvn_arctic_standoff.png"),
    ("coastal.webp", "dvn_coastal_assault.png"),
):
    src = tmp / source_name
    target = out / target_name
    with Image.open(src) as image:
        image.load()
        rgb = image.convert("RGB")
        if rgb.size != (768, 432):
            raise SystemExit(f"Unexpected DVN thumbnail size for {source_name}: {rgb.size}")
        if rgb.width * 9 != rgb.height * 16:
            raise SystemExit(f"DVN thumbnail is not 16:9: {source_name} -> {rgb.size}")
        # Preserve the official source at its native size. Do not invent detail with an upscale.
        rgb.save(target, "PNG", optimize=True, compress_level=9)
        print(f"✓ {target_name}: {rgb.width}x{rgb.height}")
PY

if ! command -v yt-dlp >/dev/null 2>&1; then
  echo 'yt-dlp is required to fetch the CC-licensed DVN track.' >&2
  exit 1
fi

echo '→ DVN: downloading Arc - Enemy from the creator SoundCloud page...'
rm -f "$MUSIC_SOURCE_DIR"/arc_enemy.*
yt-dlp --no-playlist --no-progress --quiet \
  -f 'bestaudio/best' \
  -o "$MUSIC_SOURCE_DIR/arc_enemy.%(ext)s" \
  "$ARC_ENEMY_URL"

ARC_SOURCE="$(find "$MUSIC_SOURCE_DIR" -maxdepth 1 -type f -name 'arc_enemy.*' | head -n1)"
if [ -z "$ARC_SOURCE" ] || [ ! -s "$ARC_SOURCE" ]; then
  echo 'Arc - Enemy download did not produce an audio source.' >&2
  exit 1
fi

echo "✓ Arc - Enemy source: $(basename "$ARC_SOURCE")"
echo 'DVN 5.10 media sources prepared.'
