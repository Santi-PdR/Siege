#!/usr/bin/env python3
"""Prepare visible Third Justice captures and the complete 5.30 test reel.

The legacy captures were accidentally exported with an extremely dark two-level
palette. 5.30 expands their visible luminance range without applying a fake
cinematic tint.

The supplied Third Justice test is kept in the repository as a compact base64
transport source (assets-source/third-justice/third_justice_full.b64). During the
build it is decoded and converted to a Forge-native 10 fps, 640x360 frame
sequence. The transport copy is deliberately much smaller than the original
75 MB upload but preserves the complete ~31 second timeline from start to end.
A direct third_justice_full.mp4 is still accepted for local development.

Generated reel PNGs are losslessly re-packed at maximum PNG compression before
the Forge build. This keeps every prepared pixel while leaving enough headroom
for GitHub's 100 MiB repository-file limit when CI publishes the validated JAR.
"""
from __future__ import annotations

import base64
from pathlib import Path
from PIL import Image, ImageEnhance, ImageStat
import shutil
import subprocess

GUIDE = Path("src/main/resources/assets/siege/textures/gui/guide")
VIDEO_SRC = Path("assets-source/third-justice/third_justice_full.mp4")
VIDEO_B64 = Path("assets-source/third-justice/third_justice_full.b64")
DECODED_VIDEO = Path("build/third-justice-media/third_justice_full.mp4")
VIDEO_DIR = GUIDE / "third_justice_video"
MANIFEST = Path("src/main/resources/assets/siege/third_justice_video.properties")
FPS = 10
FRAME_SIZE = "640:360"
MIN_FULL_DURATION_MS = 30_000
MIN_EMBEDDED_BYTES = 10_000
STATIC = [
    "third_justice_tooltip.png",
    "third_justice_field.png",
    "third_justice_reel_01.png",
    "third_justice_reel_02.png",
    "third_justice_reel_03.png",
]


def percentile(gray: Image.Image, fraction: float) -> int:
    hist = gray.histogram()
    total = sum(hist)
    target = max(0, min(total - 1, round((total - 1) * fraction)))
    running = 0
    for value, count in enumerate(hist):
        running += count
        if running > target:
            return value
    return 255


def remap_visible(image: Image.Image) -> Image.Image:
    rgb = image.convert("RGB")
    gray = rgb.convert("L")
    lo = percentile(gray, 0.01)
    hi = percentile(gray, 0.99)
    if hi <= lo:
        return rgb
    scale = 210.0 / max(1, hi - lo)
    lut = [max(0, min(255, round(28 + (value - lo) * scale))) for value in range(256)]
    channels = [channel.point(lut) for channel in rgb.split()]
    return ImageEnhance.Contrast(Image.merge("RGB", channels)).enhance(1.06)


def check_visible(name: str, image: Image.Image) -> str:
    probe = image.convert("L")
    stat = ImageStat.Stat(probe)
    mean = stat.mean[0]
    contrast = stat.stddev[0]
    lo = percentile(probe, 0.05)
    hi = percentile(probe, 0.95)
    if mean < 42:
        raise SystemExit(f"{name}: still too dark after repair (mean luma={mean:.1f})")
    if hi - lo < 38:
        raise SystemExit(f"{name}: still too flat after repair (range={hi-lo})")
    return f"{name}: mean={mean:.1f} contrast={contrast:.1f} p05-p95={hi-lo}"


def repair_static() -> None:
    print("→ Third Justice: removing legacy darkness from captures...")
    for name in STATIC:
        path = GUIDE / name
        if not path.is_file():
            raise SystemExit(f"Missing Third Justice image: {path}")
        with Image.open(path) as source:
            source.load()
            fixed = remap_visible(source)
        print("✓", check_visible(name, fixed))
        fixed.save(path, "PNG", optimize=True, compress_level=9)


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


def ffprobe_duration_ms(path: Path) -> int:
    proc = subprocess.run(
        ["ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "default=nw=1:nk=1", str(path)],
        check=True, text=True, capture_output=True,
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
        print("! Third Justice full source is not present; repaired 3-frame fallback retained.")
        return

    if shutil.which("ffmpeg") is None or shutil.which("ffprobe") is None:
        raise SystemExit("ffmpeg and ffprobe are required to prepare the complete Third Justice video")

    try:
        duration_ms = ffprobe_duration_ms(source)
    except (subprocess.CalledProcessError, ValueError) as exc:
        raise SystemExit("Third Justice embedded source could not be decoded as a valid video") from exc

    if duration_ms < MIN_FULL_DURATION_MS:
        raise SystemExit(
            f"Third Justice source is incomplete: {duration_ms / 1000:.2f}s; expected the ~31s full test"
        )

    print(f"→ Third Justice: extracting complete {duration_ms / 1000:.2f}s test at {FPS} fps...")
    subprocess.run(
        [
            "ffmpeg", "-hide_banner", "-loglevel", "error", "-y",
            "-i", str(source),
            "-vf", f"fps={FPS},scale={FRAME_SIZE}:flags=lanczos:force_original_aspect_ratio=decrease,pad=640:360:(ow-iw)/2:(oh-ih)/2:black",
            "-fps_mode", "passthrough", "-compression_level", "9",
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
    repair_static()
    prepare_full_video()
