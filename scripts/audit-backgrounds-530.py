#!/usr/bin/env python3
"""Fail the build when a normal SIEGE background is malformed, too small or flat.

This is intentionally a source-quality audit, not an AI/aesthetic score. It protects
aspect ratio, real dimensions, visible dynamic range and basic edge detail while
allowing naturally dark/night scenes.
"""
from pathlib import Path
from PIL import Image, ImageFilter, ImageStat

ROOT = Path("src/main/resources/assets/siege/textures/gui/backgrounds")
EXPECTED = {
    "dummies_assault": (960, 540),
    "dvn_official_01": (768, 432),
    "dvn_official_02": (768, 432),
    "dvn_official_03": (768, 432),
    "anniversary": (960, 540),
    "frontline_19": (960, 540),
    "cyborg": (960, 540),
    "last_stand": (960, 540),
    "vought_siege": (960, 540),
    "earth_orbit": (960, 540),
    "canyon_engagement": (960, 540),
    "night_battle": (960, 540),
    "night_operation": (720, 405),
    "urban_rendezvous": (720, 405),
    "rooftop_squad": (896, 504),
}


def percentile(values, fraction):
    values = sorted(values)
    return values[min(len(values) - 1, max(0, round((len(values) - 1) * fraction)))]


def audit(name: str, expected: tuple[int, int]) -> str:
    path = ROOT / f"{name}.png"
    if not path.is_file():
        raise SystemExit(f"Missing normal background: {path}")
    with Image.open(path) as source:
        source.load()
        image = source.convert("RGB")

    if image.size != expected:
        raise SystemExit(f"{name}: expected real prepared size {expected}, got {image.size}")
    if image.width * 9 != image.height * 16:
        raise SystemExit(f"{name}: normal background must be exact 16:9, got {image.size}")
    if image.width < 640 or image.height < 360:
        raise SystemExit(f"{name}: background below 640x360 quality floor")

    # Downsample only for analysis. This does not touch the game asset.
    probe = image.copy()
    probe.thumbnail((320, 180), Image.Resampling.BILINEAR)
    gray = probe.convert("L")
    stat = ImageStat.Stat(gray)
    mean = stat.mean[0]
    contrast = stat.stddev[0]
    samples = list(gray.getdata())
    p05, p95 = percentile(samples, 0.05), percentile(samples, 0.95)
    dynamic = p95 - p05
    edge = ImageStat.Stat(gray.filter(ImageFilter.FIND_EDGES)).mean[0]
    colors = probe.getcolors(maxcolors=320 * 180)
    unique = len(colors) if colors is not None else 320 * 180

    if mean < 9.0:
        raise SystemExit(f"{name}: almost black export (mean luma {mean:.1f})")
    if contrast < 6.0 or dynamic < 22:
        raise SystemExit(
            f"{name}: suspiciously flat export (std {contrast:.1f}, p05-p95 {dynamic})"
        )
    if edge < 3.0:
        raise SystemExit(f"{name}: suspiciously blurred/blank export (edge score {edge:.1f})")
    if unique < 96:
        raise SystemExit(f"{name}: suspiciously posterized export ({unique} probe colors)")

    return (
        f"{name:22s} {image.width:4d}x{image.height:<4d} "
        f"mean={mean:6.1f} contrast={contrast:5.1f} dynamic={dynamic:3d} "
        f"edge={edge:5.1f} colors={unique:5d}"
    )


if __name__ == "__main__":
    lines = [audit(name, expected) for name, expected in EXPECTED.items()]
    print("SIEGE 5.30 background quality audit")
    print("\n".join(lines))
    print(f"PASS: {len(lines)} normal backgrounds checked; no fake-HD requirement.")
