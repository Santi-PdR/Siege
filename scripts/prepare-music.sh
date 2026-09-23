#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SOURCE_DIR="$ROOT/assets-source/music-full"
GENERATED_DIR="$ROOT/build/generated-music"
TARGET_DIR="$ROOT/src/main/resources/assets/siege/sounds/music"
DURATION_FILE="$ROOT/src/main/resources/assets/siege/music_durations.properties"

if ! command -v ffmpeg >/dev/null 2>&1 || ! command -v ffprobe >/dev/null 2>&1; then
  echo "ffmpeg/ffprobe is required to prepare the SIEGE soundtrack." >&2
  exit 1
fi

mkdir -p "$TARGET_DIR" "$GENERATED_DIR"
rm -f "$TARGET_DIR"/*.ogg
: > "$DURATION_FILE"

TALE_SOURCE="$SOURCE_DIR/tale_cruel_world.ogg"
DARKEST_SOURCE="$SOURCE_DIR/darkest_of_days.ogg"
DVN_SOURCE="$SOURCE_DIR/dvn_lobby_music.ogg"
HEAVEN_SOURCE="$SOURCE_DIR/heavens_hell_sent_gift.ogg"
ARC_SOURCE="$(find "$SOURCE_DIR" -maxdepth 1 -type f -name 'arc_enemy.*' | head -n1 || true)"
STRONGHOLD_SOURCE="$GENERATED_DIR/stronghold_black_signal.wav"

KAPTAIN_START="179.599646"
KAPTAIN_END="319.568250"
KAPTAIN_DURATION="139.968604"
OUTPUT_RATE="44100"
HEADROOM_DB="-3dB"

for source in "$TALE_SOURCE" "$DARKEST_SOURCE" "$DVN_SOURCE" "$HEAVEN_SOURCE"; do
  if [ ! -f "$source" ]; then
    echo "Missing clean soundtrack master: $source" >&2
    exit 1
  fi
done
if [ -z "$ARC_SOURCE" ] || [ ! -f "$ARC_SOURCE" ]; then
  echo "Missing DVN Arc - Enemy source. Run scripts/fetch-dvn-media-510.sh first." >&2
  exit 1
fi

# 5.40 adds one fully original SIEGE track generated deterministically during the
# build, so the new music does not depend on another third-party master.
python3 "$ROOT/scripts/generate-stronghold-signal.py" "$STRONGHOLD_SOURCE"

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

validate_source "Tale of a Cruel World" "$TALE_SOURCE" 260000
validate_source "Darkest of Days" "$DARKEST_SOURCE" 280000
validate_source "DVN lobby mix" "$DVN_SOURCE" 535000
validate_source "Heaven's Hell-Sent Gift" "$HEAVEN_SOURCE" 215000
validate_source "Arc - Enemy" "$ARC_SOURCE" 60000
validate_source "Stronghold Black Signal" "$STRONGHOLD_SOURCE" 131000

encode_full() {
  local key="$1"
  local source="$2"
  local target="$TARGET_DIR/$key.ogg"

  # Leave decoded headroom before Vorbis encoding. This avoids inter-sample
  # clipping that can sound harsh on some OpenAL/device combinations.
  ffmpeg -hide_banner -loglevel error -y \
    -i "$source" \
    -map_metadata -1 -vn \
    -af "volume=$HEADROOM_DB" \
    -ar "$OUTPUT_RATE" -ac 2 -c:a libvorbis -q:a 5 \
    "$target"
}

encode_full "tale_cruel_world" "$TALE_SOURCE"
encode_full "darkest_of_days" "$DARKEST_SOURCE"
encode_full "heavens_hell_sent_gift" "$HEAVEN_SOURCE"
encode_full "arc_enemy" "$ARC_SOURCE"
encode_full "stronghold_black_signal" "$STRONGHOLD_SOURCE"

ffmpeg -hide_banner -loglevel error -y \
  -ss "$KAPTAIN_START" -i "$DVN_SOURCE" \
  -t "$KAPTAIN_DURATION" \
  -map_metadata -1 -vn \
  -af "volume=$HEADROOM_DB" \
  -ar "$OUTPUT_RATE" -ac 2 -c:a libvorbis -q:a 5 \
  "$TARGET_DIR/kaptain_music_box.ogg"

tracks=(
  tale_cruel_world
  darkest_of_days
  kaptain_music_box
  heavens_hell_sent_gift
  arc_enemy
  stronghold_black_signal
)

declare -A min_ms=(
  [tale_cruel_world]=260000
  [darkest_of_days]=280000
  [kaptain_music_box]=139000
  [heavens_hell_sent_gift]=215000
  [arc_enemy]=60000
  [stronghold_black_signal]=131000
)
declare -A max_ms=(
  [tale_cruel_world]=263000
  [darkest_of_days]=283000
  [kaptain_music_box]=141000
  [heavens_hell_sent_gift]=219000
  [arc_enemy]=600000
  [stronghold_black_signal]=133000
)

for key in "${tracks[@]}"; do
  target="$TARGET_DIR/$key.ogg"
  codec="$(ffprobe -v error -select_streams a:0 -show_entries stream=codec_name -of csv=p=0 "$target")"
  rate="$(ffprobe -v error -select_streams a:0 -show_entries stream=sample_rate -of csv=p=0 "$target")"
  channels="$(ffprobe -v error -select_streams a:0 -show_entries stream=channels -of csv=p=0 "$target")"
  if [ "$codec" != "vorbis" ]; then
    echo "Prepared track $key is not Ogg Vorbis: $codec" >&2
    exit 1
  fi
  if [ "$rate" != "$OUTPUT_RATE" ]; then
    echo "Prepared track $key has wrong sample rate: $rate" >&2
    exit 1
  fi
  if [ "$channels" -ne 2 ]; then
    echo "Prepared track $key must remain stereo: $channels channels" >&2
    exit 1
  fi

  ffmpeg -v error -xerror -i "$target" -f null -

  duration_ms="$(probe_ms "$target")"
  if [ "$duration_ms" -lt "${min_ms[$key]}" ] || [ "$duration_ms" -gt "${max_ms[$key]}" ]; then
    echo "Prepared track $key has unexpected duration: ${duration_ms}ms" >&2
    exit 1
  fi

  peak="$(ffmpeg -hide_banner -nostats -i "$target" -af volumedetect -f null - 2>&1 \
    | sed -n 's/.*max_volume: \([-0-9.]*\) dB.*/\1/p' | tail -n1)"
  if [ -z "$peak" ]; then
    echo "Could not measure decoded peak for $key" >&2
    exit 1
  fi
  if ! awk -v p="$peak" 'BEGIN { exit !(p <= -1.0) }'; then
    echo "Prepared track $key has insufficient decoded headroom: ${peak} dB" >&2
    exit 1
  fi

  printf '%s=%s\n' "$key" "$duration_ms" >> "$DURATION_FILE"
  printf 'SIEGE music: %-28s %8sms  codec=%s rate=%s peak=%sdB\n' "$key" "$duration_ms" "$codec" "$rate" "$peak"
done

printf '\nGenerated duration metadata:\n'
cat "$DURATION_FILE"
