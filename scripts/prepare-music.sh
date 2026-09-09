#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SOURCE_DIR="$ROOT/assets-source/music-full"
TARGET_DIR="$ROOT/src/main/resources/assets/siege/sounds/music"
DURATION_FILE="$ROOT/src/main/resources/assets/siege/music_durations.properties"
REPAIRED_DIR="$ROOT/build/siege-music-repaired"

if ! command -v ffmpeg >/dev/null 2>&1 || ! command -v ffprobe >/dev/null 2>&1; then
  echo "ffmpeg/ffprobe is required to prepare the SIEGE soundtrack." >&2
  exit 1
fi

mkdir -p "$TARGET_DIR" "$REPAIRED_DIR"
rm -f "$TARGET_DIR"/*.ogg "$REPAIRED_DIR"/*.ogg
: > "$DURATION_FILE"

tracks=(
  tale_cruel_world
  darkest_of_days
  kaptain_music_box
  heavens_hell_sent_gift
)

for key in "${tracks[@]}"; do
  source="$SOURCE_DIR/$key.ogg"
  repaired="$REPAIRED_DIR/$key.ogg"
  target="$TARGET_DIR/$key.ogg"
  test -f "$source"

  # Recovered owner masters were affected by a historical binary transfer that
  # damaged Ogg page CRC fields. Repair only the page checksums; audio packet
  # payloads stay byte-for-byte untouched.
  python3 "$ROOT/scripts/repair-ogg.py" "$source" "$repaired"

  # The owner masters are Opus-in-Ogg. Minecraft 1.20.1 does not decode those
  # reliably, so CI converts the complete repaired stream to Ogg Vorbis.
  # No -t/-ss filter is used: every readable packet in the source is preserved.
  ffmpeg -hide_banner -loglevel error -y \
    -i "$repaired" \
    -map_metadata -1 -vn \
    -af "loudnorm=I=-18:TP=-1.5:LRA=11" \
    -ar 48000 -c:a libvorbis -q:a 5 \
    "$target"

  codec="$(ffprobe -v error -select_streams a:0 -show_entries stream=codec_name -of csv=p=0 "$target")"
  test "$codec" = "vorbis"

  duration="$(ffprobe -v error -show_entries format=duration -of csv=p=0 "$target")"
  duration_ms="$(awk -v seconds="$duration" 'BEGIN { printf "%.0f", seconds * 1000.0 }')"
  if [ "$duration_ms" -lt 10000 ]; then
    echo "Prepared track $key is unexpectedly short: ${duration_ms}ms" >&2
    exit 1
  fi

  printf '%s=%s\n' "$key" "$duration_ms" >> "$DURATION_FILE"
  printf 'SIEGE music: %-28s %8sms  codec=%s\n' "$key" "$duration_ms" "$codec"
done

printf '\nGenerated duration metadata:\n'
cat "$DURATION_FILE"
