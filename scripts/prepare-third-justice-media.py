#!/usr/bin/env python3
"""Prepare visible Third Justice captures and, when available, the full test video.

The legacy captures were accidentally exported with an extremely dark two-level
palette. 5.30 expands their visible luminance range without applying a fake
cinematic tint. If the original supplied MP4 is present at
assets-source/third-justice/third_justice_full.mp4, the complete duration is
converted to a Forge-native 10 fps frame sequence. This avoids shipping a JVM
video decoder while preserving the entire test from start to finish.
"""
from __future__ import annotations

from pathlib import Path
from PIL import Image, ImageEnhance, ImageOps, ImageStat
import shutil
import subprocess

GUIDE = Path("src/main/resources/assets/siege/textures/gui/guide")
VIDEO_SRC = Path("assets-source/third-justice/third_justice_full.mp4")
VIDEO_DIR = GUIDE / "third_justice_video"
MANIFEST = Path("src/main/resources/assets/siege/third_justice_video.properties")
FPS = 10
FRAME_SIZE = "640:360"
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
    """Lift a dark capture while preserving its existing spatial detail."""
    rgb = image.convert("RGB")
    gray = rgb.convert("L")
    lo = percentile(gray, 0.01)
    hi = percentile(gray, 0.99)
    if hi <= lo:
        return rgb

    # The old assets can occupy only a narrow 55..101-ish luma range. Expand that
    # into a readable 28..238 range, preserving colours where they still exist.
    scale = 210.0 / max(1, hi - lo)
    lut = []
    for value in range(256):
        mapped = round(28 + (value - lo) * scale)
        lut.append(max(0, min(255, mapped)))
    channels = [channel.point(lut) for channel in rgb.split()]
    fixed = Image.merge("RGB", channels)

    # Mild contrast only; no darkness veil and no heavy sharpen/filter.
    fixed = ImageEnhance.Contrast(fixed).enhance(1.06)
    return fixed


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


def ffprobe_duration_ms(path: Path) -> int:
    proc = subprocess.run(
        [
            "ffprobe", "-v", "error", "-show_entries", "format=duration",
            "-of", "default=nw=1:nk=1", str(path),
        ],
        check=True, text=True, capture_output=True,
    )
    seconds = float(proc.stdout.strip())
    return max(1, round(seconds * 1000))


def prepare_full_video() -> None:
    VIDEO_DIR.mkdir(parents=True, exist_ok=True)
    for old in VIDEO_DIR.glob("frame_*.png"):
        old.unlink()

    if not VIDEO_SRC.is_file():
        # Keep an explicit fallback manifest. Runtime then uses the three repaired
        # historical reel frames. The build does not pretend the full MP4 exists.
        MANIFEST.parent.mkdir(parents=True, exist_ok=True)
        MANIFEST.write_text(
            "mode=fallback\nframes=3\nfps=1\nduration_ms=3300\nwidth=640\nheight=360\n",
            encoding="utf-8",
        )
        print(
            "! Third Justice full MP4 is not present; repaired 3-frame fallback retained.\n"
            "  Expected: assets-source/third-justice/third_justice_full.mp4"
        )
        return

    if shutil.which("ffmpeg") is None or shutil.which("ffprobe") is None:
        raise SystemExit("ffmpeg and ffprobe are required to prepare the complete Third Justice video")

    duration_ms = ffprobe_duration_ms(VIDEO_SRC)
    print(f"→ Third Justice: extracting complete {duration_ms / 1000:.2f}s test at {FPS} fps...")
    subprocess.run(
        [
            "ffmpeg", "-hide_banner", "-loglevel", "error", "-y",
            "-i", str(VIDEO_SRC),
            "-vf", f"fps={FPS},scale={FRAME_SIZE}:flags=lanczos:force_original_aspect_ratio=decrease,pad=640:360:(ow-iw)/2:(oh-ih)/2:black",
            "-vsync", "0", "-compression_level", "8",
            str(VIDEO_DIR / "frame_%05d.png"),
        ],
        check=True,
    )
    frames = sorted(VIDEO_DIR.glob("frame_*.png"))
    if not frames:
        raise SystemExit("Full Third Justice video extraction produced no frames")

    expected = max(1, round(duration_ms * FPS / 1000))
    # ffmpeg rounding can differ by one frame at the end.
    if abs(len(frames) - expected) > 2:
        raise SystemExit(f"Unexpected complete-video frame count: {len(frames)} vs ~{expected}")

    for sample in (frames[0], frames[len(frames)//2], frames[-1]):
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
