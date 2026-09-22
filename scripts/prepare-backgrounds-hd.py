#!/usr/bin/env python3
"""Build 1920x1080 menu masters from the checked-in scene sources.

This does not pretend that upscaling creates missing source detail. It provides a
single high-quality resampling/compositing path so low-resolution source art is
not repeatedly stretched by the runtime. Recent non-16:9 art is handled without
geometric distortion: Night Operation drops its captured letterbox, while
Rooftop Squad keeps the full illustration over a softened 16:9 extension.

Tempest Jutcherson is intentionally excluded: as of SIEGE 2.50 it is reserved as
an easter-egg asset, not a normal menu background/gallery entry.
"""
from pathlib import Path
from PIL import Image, ImageFilter

ROOT = Path("src/main/resources/assets/siege/textures/gui/backgrounds")
TARGET = (1920, 1080)
SCENES = [
    "dummies_assault", "anniversary", "frontline_19", "cyborg", "last_stand",
    "vought_siege", "earth_orbit", "canyon_engagement", "night_battle",
    "night_operation", "urban_rendezvous", "rooftop_squad",
]


def crop_cover(image: Image.Image, target_ratio: float) -> Image.Image:
    w, h = image.size
    ratio = w / h
    if abs(ratio - target_ratio) < 0.002:
        return image
    if ratio > target_ratio:
        nw = max(1, round(h * target_ratio))
        left = (w - nw) // 2
        return image.crop((left, 0, left + nw, h))
    nh = max(1, round(w / target_ratio))
    top = (h - nh) // 2
    return image.crop((0, top, w, top + nh))


def upscale_16_9(image: Image.Image) -> Image.Image:
    image = crop_cover(image, 16 / 9)
    image = image.resize(TARGET, Image.Resampling.LANCZOS)
    return image.filter(ImageFilter.UnsharpMask(radius=0.8, percent=55, threshold=3))


def rooftop_composite(image: Image.Image) -> Image.Image:
    bg = crop_cover(image, 16 / 9).resize(TARGET, Image.Resampling.LANCZOS)
    bg = bg.filter(ImageFilter.GaussianBlur(radius=18))
    shade = Image.new("RGBA", TARGET, (0, 0, 0, 72))
    bg = Image.alpha_composite(bg.convert("RGBA"), shade)

    fg = image.copy()
    fg.thumbnail(TARGET, Image.Resampling.LANCZOS)
    x = (TARGET[0] - fg.width) // 2
    y = (TARGET[1] - fg.height) // 2
    bg.alpha_composite(fg.convert("RGBA"), (x, y))
    return bg.convert("RGB").filter(ImageFilter.UnsharpMask(radius=0.7, percent=45, threshold=3))


def prepare(name: str) -> None:
    path = ROOT / f"{name}.png"
    if not path.is_file():
        raise SystemExit(f"Missing background source: {path}")
    with Image.open(path) as source:
        image = source.convert("RGB")

    if name == "night_operation" and image.size == (735, 490):
        image = image.crop((0, 38, 735, 452))

    if name == "rooftop_squad" and image.width * 9 != image.height * 16:
        out = rooftop_composite(image)
    else:
        out = upscale_16_9(image)

    if out.size != TARGET:
        raise SystemExit(f"HD preparation failed for {name}: {out.size}")
    out.save(path, "PNG", optimize=True, compress_level=9)
    print(f"{name}: {image.size[0]}x{image.size[1]} -> {TARGET[0]}x{TARGET[1]}")


if __name__ == "__main__":
    for scene in SCENES:
        prepare(scene)
