#!/usr/bin/env python3
"""Prepare the corrected Third Justice captures and complete 5.30 test reel.

The original static captures that reached the repository were accidentally
quantized to 1-bit/2-bit PNGs. Brightness/contrast remapping cannot recover
colors or detail that no longer exist, so the two canonical screenshots are
stored as compact full-color WebP transport sources under
assets-source/third-justice/static-correct/ and restored to RGB PNGs at build
time.

The supplied Third Justice test is kept as a compact base64 transport source
(assets-source/third-justice/third_justice_full.b64). During the build it is
decoded and converted to a Forge-native 10 fps, 640x360 frame sequence. Three
static reel previews are regenerated from that full-color sequence instead of
reusing the old posterized placeholders.

Generated reel PNGs are losslessly re-packed at maximum PNG compression before
the Forge build. This keeps every prepared pixel while leaving enough headroom
for GitHub's 100 MiB repository-file limit when CI publishes the validated JAR.
"""
from __future__ import annotations

import base64
from io import BytesIO
from pathlib import Path
from PIL import Image
import shutil
import subprocess

GUIDE = Path("src/main/resources/assets/siege/textures/gui/guide")
STATIC_SOURCE_DIR = Path("assets-source/third-justice/static-correct")
VIDEO_SRC = Path("assets-source/third-justice/third_justice_full.mp4")
VIDEO_B64 = Path("assets-source/third-justice/third_justice_full.b64")
DECODED_VIDEO = Path("build/third-justice-media/third_justice_full.mp4")
VIDEO_DIR = GUIDE / "third_justice_video"
MANIFEST = Path("src/main/resources/assets/siege/third_justice_video.properties")
FPS = 10
FRAME_SIZE = "640:360"
MIN_FULL_DURATION_MS = 30_000
MIN_EMBEDDED_BYTES = 10_000

CORRECT_STATIC = {
    "third_justice_tooltip.png": ("third_justice_tooltip.webp.b64.part", (762, 207)),
    "third_justice_field.png": ("third_justice_field.webp.b64.part", (1024, 579)),
}
REEL_STATIC = (
    "third_justice_reel_01.png",
    "third_justice_reel_02.png",
    "third_justice_reel_03.png",
)


def _joined_transport(prefix: str) -> bytes:
    parts = sorted(STATIC_SOURCE_DIR.glob(prefix + "*"))
    if not parts:
        raise SystemExit(f"Missing corrected Third Justice transport source: {prefix}*")
    try:
        encoded = "".join(part.read_text(encoding="ascii").strip() for part in parts)
        payload = base64.b64decode(encoded, validate=True)
    except (ValueError, OSError) as exc:
        raise SystemExit(f"Invalid corrected Third Justice transport source {prefix}: {exc}") from exc
    if len(payload) < 4_000:
        raise SystemExit(f"Corrected Third Justice source {prefix} is unexpectedly small")
    return payload


def _color_count_probe(image: Image.Image) -> int:
    probe = image.convert("RGB").resize((96, 96))
    return len(set(probe.getdata()))


def _verify_full_color(name: str, image: Image.Image, expected_size: tuple[int, int]) -> None:
    if image.size != expected_size:
        raise SystemExit(f"{name}: wrong size {image.size}; expected {expected_size}")
    colors = _color_count_probe(image)
    if colors < 64:
        raise SystemExit(
            f"{name}: still looks posterized ({colors} sampled colors); "
            "refusing to ship another 1-bit/2-bit capture"
        )
    print(f"✓ {name}: {image.size[0]}x{image.size[1]}, sampled colors={colors}")


def restore_static_captures() -> None:
    """Restore the two user-supplied screenshots from full-color transport data."""
    print("→ Third Justice: restoring canonical full-color screenshots...")
    GUIDE.mkdir(parents=True, exist_ok=True)
    for name, (prefix, expected_size) in CORRECT_STATIC.items():
        payload = _joined_transport(prefix)
        try:
            with Image.open(BytesIO(payload)) as source:
                source.load()
                fixed = source.convert("RGB")
        except OSError as exc:
            raise SystemExit(f"{name}: corrected source could not be decoded: {exc}") from exc
        _verify_full_color(name, fixed, expected_size)
        fixed.save(GUIDE / name, "PNG", optimize=True, compress_level=9)


def decode_embedded_source() -> Path | None:
    if not VIDEO_B64.is_file():
        return None
    try:
        encoded = "".join(VIDEO_B64.read_text(encoding="ascii").split())
        payload = base64.b64decode(encoded, validate=True)
    except (ValueError, OSError) as exc:
        raise SystemExit(f"Third Justice embedded video source is invalid: {exc}") from exc
    if len(payload) < MIN_EMBEDDED_BYTES:
        raise SystemExit(
            f"Third Justice embedded video source is unexpectedly small ({len(payload)} bytes)"
        )
    DECODED_VIDEO.parent.mkdir(parents=True, exist_ok=True)
    DECODED_VIDEO.write_bytes(payload)
    print(f"→ Third Justice: decoded embedded full test ({len(payload)} bytes)")
    return DECODED_VIDEO


def resolve_video_source() -> Path | None:
    """Prefer the validated embedded transport copy over stale direct MP4 remnants."""
    embedded = decode_embedded_source()
    if embedded is not None:
        return embedded

    if VIDEO_SRC.is_file():
        size = VIDEO_SRC.stat().st_size
        if size < MIN_EMBEDDED_BYTES:
            print(f"! Ignoring stale/truncated direct Third Justice MP4 ({size} bytes)")
            return None
        print(f"→ Third Justice: using direct MP4 source ({size} bytes)")
        return VIDEO_SRC
    return None


def optimize_video_frames(frames: list[Path]) -> None:
    """Losslessly re-pack generated PNGs to reduce the final Forge JAR size."""
    before = sum(frame.stat().st_size for frame in frames)
    for frame in frames:
        temporary = frame.with_name(frame.stem + ".optimized.png")
        with Image.open(frame) as source:
            source.load()
            image = source.copy()
        image.save(temporary, "PNG", optimize=True, compress_level=9)
        temporary.replace(frame)
    after = sum(frame.stat().st_size for frame in frames)
    saved = before - after
    print(
        f"→ Third Justice: lossless PNG repack {before / 1024 / 1024:.2f} MiB -> "
        f"{after / 1024 / 1024:.2f} MiB (saved {saved / 1024 / 1024:.2f} MiB)"
    )


def generate_reel_previews(frames: list[Path]) -> None:
    """Replace the old 2-bit reel stills with representative full-color video frames."""
    if len(frames) < 3:
        raise SystemExit("Not enough Third Justice video frames for reel previews")
    indices = (
        min(len(frames) - 1, max(0, round((len(frames) - 1) * 0.10))),
        min(len(frames) - 1, max(0, round((len(frames) - 1) * 0.50))),
        min(len(frames) - 1, max(0, round((len(frames) - 1) * 0.90))),
    )
    print("→ Third Justice: regenerating full-color reel previews...")
    for name, index in zip(REEL_STATIC, indices):
        with Image.open(frames[index]) as source:
            source.load()
            preview = source.convert("RGB")
        _verify_full_color(name, preview, (640, 360))
        preview.save(GUIDE / name, "PNG", optimize=True, compress_level=9)


def ffprobe_duration_ms(path: Path) -> int:
    proc = subprocess.run(
        [
            "ffprobe", "-v", "error",
            "-show_entries", "format=duration",
            "-of", "default=nw=1:nk=1",
            str(path),
        ],
        check=True,
        text=True,
        capture_output=True,
    )
    seconds = float(proc.stdout.strip())
    return max(1, round(seconds * 1000))


def prepare_full_video() -> None:
    VIDEO_DIR.mkdir(parents=True, exist_ok=True)
    for old in VIDEO_DIR.glob("frame_*.png"):
        old.unlink()

    source = resolve_video_source()
    if source is None:
        MANIFEST.parent.mkdir(parents=True, exist_ok=True)
        MANIFEST.write_text(
            "mode=fallback\nframes=3\nfps=1\nduration_ms=3300\nwidth=640\nheight=360\n",
            encoding="utf-8",
        )
        print("! Third Justice full source is not present; historical reel fallback retained.")
        return

    if shutil.which("ffmpeg") is None or shutil.which("ffprobe") is None:
        raise SystemExit("ffmpeg and ffprobe are required to prepare the complete Third Justice video")

    try:
        duration_ms = ffprobe_duration_ms(source)
    except (subprocess.CalledProcessError, ValueError) as exc:
        raise SystemExit("Third Justice embedded source could not be decoded as a valid video") from exc

    if duration_ms < MIN_FULL_DURATION_MS:
        raise SystemExit(
            f"Third Justice source is incomplete: {duration_ms / 1000:.2f}s; "
            "expected the ~31s full test"
        )

    print(f"→ Third Justice: extracting complete {duration_ms / 1000:.2f}s test at {FPS} fps...")
    subprocess.run(
        [
            "ffmpeg", "-nostdin", "-hide_banner", "-loglevel", "error", "-y",
            "-i", str(source),
            "-vf",
            f"fps={FPS},scale={FRAME_SIZE}:flags=lanczos:"
            "force_original_aspect_ratio=decrease,"
            "pad=640:360:(ow-iw)/2:(oh-ih)/2:black",
            "-fps_mode", "passthrough",
            "-compression_level", "9",
            str(VIDEO_DIR / "frame_%05d.png"),
        ],
        check=True,
    )

    frames = sorted(VIDEO_DIR.glob("frame_*.png"))
    if not frames:
        raise SystemExit("Full Third Justice video extraction produced no frames")

    optimize_video_frames(frames)

    expected = max(1, round(duration_ms * FPS / 1000))
    if abs(len(frames) - expected) > 2:
        raise SystemExit(f"Unexpected complete-video frame count: {len(frames)} vs ~{expected}")
    if len(frames) < 300:
        raise SystemExit(f"Third Justice full reel unexpectedly short: only {len(frames)} frames")

    for sample in (frames[0], frames[len(frames) // 2], frames[-1]):
        with Image.open(sample) as image:
            image.load()
            if image.size != (640, 360):
                raise SystemExit(f"Unexpected video frame size: {sample} -> {image.size}")

    generate_reel_previews(frames)

    MANIFEST.parent.mkdir(parents=True, exist_ok=True)
    MANIFEST.write_text(
        "mode=full\n"
        f"frames={len(frames)}\n"
        f"fps={FPS}\n"
        f"duration_ms={duration_ms}\n"
        "width=640\nheight=360\n",
        encoding="utf-8",
    )
    print(f"✓ Third Justice complete video prepared: {len(frames)} frames, {duration_ms} ms")


if __name__ == "__main__":
    restore_static_captures()
    prepare_full_video()
