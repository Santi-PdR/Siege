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
if ! command -v sha256sum >/dev/null 2>&1; then
  echo "sha256sum is required to verify the owner-supplied masters." >&2
  exit 1
fi

mkdir -p "$TARGET_DIR"
rm -f "$TARGET_DIR"/*.ogg
: > "$DURATION_FILE"

# Exact clean masters uploaded by the owner on 2026-09-09. Keeping the hashes
# here prevents the previous damaged ~25 s copies from silently returning.
declare -A EXPECTED_SHA256=(
  [tale_cruel_world]="96d66d0c0a645279764d42f787f4bb1244b25aa0dc595623a75b40c0c7573b35"
  [darkest_of_days]="b5797d634e6a25ce58469fca11a58b0fa3e015d167ed75c0646729283d2a7060"
  # Legacy internal key. The source is the owner's complete 08:59.54 DVN lobby mix.
  [kaptain_music_box]="61a498de84b0052f0cf359eda84de624af91d18b0a0248cad3755006f52f2928"
  [heavens_hell_sent_gift]="24ffffa0537ae9a9483bf36596ec760d8f52305e2daf9d7140c2097ab4e62412"
)

tracks=(
  tale_cruel_world
  darkest_of_days
  kaptain_music_box
  heavens_hell_sent_gift
)

for key in "${tracks[@]}"; do
  source="$SOURCE_DIR/$key.ogg"
  target="$TARGET_DIR/$key.ogg"
  test -f "$source"

  actual_sha="$(sha256sum "$source" | awk '{print $1}')"
  if [ "$actual_sha" != "${EXPECTED_SHA256[$key]}" ]; then
    echo "Wrong or damaged full master for $key" >&2
    echo " expected: ${EXPECTED_SHA256[$key]}" >&2
    echo " actual:   $actual_sha" >&2
    exit 1
  fi

  source_duration="$(ffprobe -v error -show_entries format=duration -of csv=p=0 "$source")"
  printf 'SIEGE source: %-28s %9ss  sha256=OK\n' "$key" "$source_duration"

  if [ "$key" = "kaptain_music_box" ]; then
    # The uploaded DVN file is a three-song lobby compilation. The old SIEGE
    # build can be fingerprint-matched at 178.500 s, proving that the intended
    # track is the SECOND lobby song, not the whole 8:59 compilation. Preserve
    # that song in full: start at the same 178.500 s boundary and stop halfway
    # through the following silent gap, before song three begins.
    ffmpeg -hide_banner -loglevel error -y \
      -i "$source" \
      -map_metadata -1 -vn \
      -af "atrim=start=178.500:end=320.781792,asetpts=PTS-STARTPTS,loudnorm=I=-18:TP=-1.5:LRA=11" \
      -ar 48000 -c:a libvorbis -q:a 5 \
      "$target"
  else
    # These three files are already complete clean masters. Do not cut them.
    ffmpeg -hide_banner -loglevel error -y \
      -i "$source" \
      -map_metadata -1 -vn \
      -af "loudnorm=I=-18:TP=-1.5:LRA=11" \
      -ar 48000 -c:a libvorbis -q:a 5 \
      "$target"
  fi

  codec="$(ffprobe -v error -select_streams a:0 -show_entries stream=codec_name -of csv=p=0 "$target")"
  test "$codec" = "vorbis"

  duration="$(ffprobe -v error -show_entries format=duration -of csv=p=0 "$target")"
  duration_ms="$(awk -v seconds="$duration" 'BEGIN { printf "%.0f", seconds * 1000.0 }')"

  # Guard against accidental truncation. Music Box is the selected complete
  # song from the DVN compilation; the other three are complete source tracks.
  case "$key" in
    tale_cruel_world) min_ms=260000 ;;
    darkest_of_days) min_ms=280000 ;;
    kaptain_music_box) min_ms=140000 ;;
    heavens_hell_sent_gift) min_ms=215000 ;;
  esac
  if [ "$duration_ms" -lt "$min_ms" ]; then
    echo "Prepared track $key is unexpectedly short: ${duration_ms}ms (minimum ${min_ms}ms)" >&2
    exit 1
  fi

  printf '%s=%s\n' "$key" "$duration_ms" >> "$DURATION_FILE"
  printf 'SIEGE music:  %-28s %8sms  codec=%s\n' "$key" "$duration_ms" "$codec"
done

printf '\nGenerated duration metadata:\n'
cat "$DURATION_FILE"
