#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BG_DIR="$ROOT/src/main/resources/assets/siege/textures/gui/backgrounds"
MUSIC_SOURCE_DIR="$ROOT/assets-source/music-full"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

DVN_UNIVERSE_ID='3293525400'
ROBLOX_THUMBNAILS_API="https://thumbnails.roblox.com/v1/games/multiget/thumbnails?universeIds=${DVN_UNIVERSE_ID}&countPerUniverse=10&defaults=true&size=768x432&format=Png&isCircular=false"
ARC_ENEMY_URL='https://soundcloud.com/potoe-50708490/arc-enemy'

mkdir -p "$BG_DIR" "$MUSIC_SOURCE_DIR"

echo '→ DVN 5.40: resolving current official Roblox thumbnails...'
python3 - "$ROBLOX_THUMBNAILS_API" "$TMP" <<'PY'
from pathlib import Path
from urllib.request import Request, urlopen
from PIL import Image, ImageStat
import io
import json
import sys

api = sys.argv[1]
tmp = Path(sys.argv[2])
headers = {"User-Agent": "SIEGE/5.40 noncommercial fan project media fetch"}


def get_bytes(url: str) -> bytes:
    req = Request(url, headers=headers)
    with urlopen(req, timeout=30) as response:
        return response.read()


payload = json.loads(get_bytes(api).decode("utf-8"))
urls = []
for item in payload.get("data", []):
    for thumb in item.get("thumbnails", []):
        if thumb.get("state") == "Completed" and thumb.get("imageUrl"):
            urls.append(thumb["imageUrl"])
    if item.get("state") == "Completed" and item.get("imageUrl"):
        urls.append(item["imageUrl"])

# Preserve Roblox ordering while removing duplicates. Generic scene IDs are used
# because the official gallery can be reordered by Roblox without warning.
urls = list(dict.fromkeys(urls))
if len(urls) < 6:
    raise SystemExit(f"Roblox returned only {len(urls)} completed DVN thumbnails; need 6")

for index, url in enumerate(urls[:6], start=1):
    raw = get_bytes(url)
    with Image.open(io.BytesIO(raw)) as image:
        image.load()
        rgb = image.convert("RGB")
        if rgb.size != (768, 432):
            raise SystemExit(f"Unexpected DVN thumbnail size #{index}: {rgb.size}")
        if rgb.width * 9 != rgb.height * 16:
            raise SystemExit(f"DVN thumbnail #{index} is not 16:9: {rgb.size}")
        gray = rgb.convert("L")
        stat = ImageStat.Stat(gray)
        if stat.stddev[0] < 7.0 or stat.mean[0] < 10.0:
            raise SystemExit(
                f"DVN thumbnail #{index} looks corrupt/flat: mean={stat.mean[0]:.2f}, std={stat.stddev[0]:.2f}"
            )
        target = tmp / f"dvn_official_{index:02d}.png"
        # Keep the official source at native size. Do not invent detail through upscaling.
        rgb.save(target, "PNG", optimize=True, compress_level=9)
        print(f"✓ resolved official DVN thumbnail {index}: {rgb.width}x{rgb.height}")
        print(f"  source: {url}")
PY

for index in 01 02 03 04 05 06; do
  install -m 0644 "$TMP/dvn_official_${index}.png" "$BG_DIR/dvn_official_${index}.png"
done

# Old experimental names must not survive a build and silently duplicate scenes.
rm -f "$BG_DIR/dvn_arctic_standoff.png" "$BG_DIR/dvn_coastal_assault.png"

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
echo 'DVN 5.40 media sources prepared.'
