#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SOURCE_DIR="$ROOT/assets-source/music-full"
TARGET_DIR="$ROOT/src/main/resources/assets/siege/sounds/music"
DURATION_FILE="$ROOT/src/main/resources/assets/siege/music_durations.properties"

if ! command -v ffmpeg >/dev/null 2>&1 || ! command -v ffprobe >/dev/null 2>&1; then
  echo "ffmpeg/ffprobe is required to prepare the SIEGE soundtrack." >&2
  exit 1
fi

mkdir -p "$TARGET_DIR"
rm -f "$TARGET_DIR"/*.ogg
: > "$DURATION_FILE"

TALE_SOURCE="$SOURCE_DIR/tale_cruel_world.ogg"
DARKEST_SOURCE="$SOURCE_DIR/darkest_of_days.ogg"
DVN_SOURCE="$SOURCE_DIR/dvn_lobby_music.ogg"
HEAVEN_SOURCE="$SOURCE_DIR/heavens_hell_sent_gift.ogg"

# The clean owner uploads supplied in chat on 2026-09-09 are the authoritative
# masters. Tale, Darkest and Heaven are preserved from first packet to final
# packet. The DVN lobby mix contains several songs; only Kaptain - Music Box is
# part of the SIEGE soundtrack. Its boundaries are the two long silence gaps
# detected in the supplied mix:
#   silence ends  179.599646 s -> Kaptain begins
#   silence starts 319.568250 s -> Kaptain ends
KAPTAIN_START="179.599646"
KAPTAIN_END="319.568250"
KAPTAIN_DURATION="139.968604"

for source in "$TALE_SOURCE" "$DARKEST_SOURCE" "$DVN_SOURCE" "$HEAVEN_SOURCE"; do
  if [ ! -f "$source" ]; then
    echo "Missing clean soundtrack master: $source" >&2
    exit 1
  fi
done

probe_ms() {
  local file="$1"
  local seconds
  seconds="$(ffprobe -v error -show_entries format=duration -of csv=p=0 "$file")"
  awk -v s="$seconds" 'BEGIN { printf "%.0f", s * 1000.0 }'
}

validate_source() {
  local label="$1"
  local file="$2"
  local minimum_ms="$3"
  local duration_ms
  duration_ms="$(probe_ms "$file")"
  if [ "$duration_ms" -lt "$minimum_ms" ]; then
    echo "Source master $label is truncated: ${duration_ms}ms (expected >= ${minimum_ms}ms)" >&2
    exit 1
  fi
  printf 'SIEGE source: %-24s %8sms\n' "$label" "$duration_ms"
}

# Guard against ever reintroducing the old ~22-25 second damaged repository
# blobs. These minima are intentionally slightly below the supplied durations.
validate_source "Tale of a Cruel World" "$TALE_SOURCE" 260000
validate_source "Darkest of Days" "$DARKEST_SOURCE" 280000
validate_source "DVN lobby mix" "$DVN_SOURCE" 535000
validate_source "Heaven's Hell-Sent Gift" "$HEAVEN_SOURCE" 215000

encode_full() {
  local key="$1"
  local source="$2"
  local target="$TARGET_DIR/$key.ogg"

  # Minecraft 1.20.1 expects Vorbis. No -ss, -t, atrim or other time filter is
  # allowed for a full track: the clean source is converted from beginning to end.
  ffmpeg -hide_banner -loglevel error -y \
    -i "$source" \
    -map_metadata -1 -vn \
    -ar 48000 -c:a libvorbis -q:a 5 \
    "$target"
}

encode_full "tale_cruel_world" "$TALE_SOURCE"
encode_full "darkest_of_days" "$DARKEST_SOURCE"
encode_full "heavens_hell_sent_gift" "$HEAVEN_SOURCE"

# DVN is the only source intentionally split. Extract Kaptain - Music Box from
# the exact music interval between the two silence separators; do not package
# the rest of the 8:59 lobby compilation.
ffmpeg -hide_banner -loglevel error -y \
  -ss "$KAPTAIN_START" -i "$DVN_SOURCE" \
  -t "$KAPTAIN_DURATION" \
  -map_metadata -1 -vn \
  -ar 48000 -c:a libvorbis -q:a 5 \
  "$TARGET_DIR/kaptain_music_box.ogg"

tracks=(
  tale_cruel_world
  darkest_of_days
  kaptain_music_box
  heavens_hell_sent_gift
)

# Expected ranges are based on the exact clean files supplied by the owner.
# They catch accidental truncation or accidentally packaging all of DVN.
declare -A min_ms=(
  [tale_cruel_world]=260000
  [darkest_of_days]=280000
  [kaptain_music_box]=139000
  [heavens_hell_sent_gift]=215000
)
declare -A max_ms=(
  [tale_cruel_world]=263000
  [darkest_of_days]=283000
  [kaptain_music_box]=141000
  [heavens_hell_sent_gift]=219000
)

for key in "${tracks[@]}"; do
  target="$TARGET_DIR/$key.ogg"
  codec="$(ffprobe -v error -select_streams a:0 -show_entries stream=codec_name -of csv=p=0 "$target")"
  if [ "$codec" != "vorbis" ]; then
    echo "Prepared track $key is not Ogg Vorbis: $codec" >&2
    exit 1
  fi

  duration_ms="$(probe_ms "$target")"
  if [ "$duration_ms" -lt "${min_ms[$key]}" ] || [ "$duration_ms" -gt "${max_ms[$key]}" ]; then
    echo "Prepared track $key has unexpected duration: ${duration_ms}ms" >&2
    exit 1
  fi

  printf '%s=%s\n' "$key" "$duration_ms" >> "$DURATION_FILE"
  printf 'SIEGE music: %-28s %8sms  codec=%s\n' "$key" "$duration_ms" "$codec"
done

printf '\nGenerated duration metadata:\n'
cat "$DURATION_FILE"
