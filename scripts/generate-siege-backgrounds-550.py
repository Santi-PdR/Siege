#!/usr/bin/env python3
"""Generate SIEGE 5.50 tactical variants from verified DVN source art.

The purpose is not to fake new source art or upscale detail. These are clearly labeled
SIEGE treatments built from already verified official DVN thumbnails: one Nucleus
signal scene, one Tesla/electrical breach scene and one Stronghold red-alert scene.
All outputs stay at the native 768x432 size of their source material.
"""
from __future__ import annotations

from pathlib import Path
import random

from PIL import Image, ImageChops, ImageDraw, ImageEnhance, ImageFilter

ROOT = Path("src/main/resources/assets/siege/textures/gui/backgrounds")
SIZE = (768, 432)
SEED = 0x550534745


def load(name: str) -> Image.Image:
    path = ROOT / f"{name}.png"
    if not path.is_file():
        raise SystemExit(f"Missing verified DVN base scene: {path}")
    with Image.open(path) as source:
        source.load()
        image = source.convert("RGB")
    if image.size != SIZE:
        raise SystemExit(f"{name}: expected native {SIZE}, got {image.size}")
    return image


def save(name: str, image: Image.Image) -> None:
    if image.size != SIZE:
        raise SystemExit(f"{name}: generated wrong size {image.size}")
    target = ROOT / f"{name}.png"
    image.convert("RGB").save(target, "PNG", optimize=True, compress_level=9)
    print(f"SIEGE 5.50 background: {name} -> {image.width}x{image.height}")


def nucleus_interference(base: Image.Image) -> Image.Image:
    rng = random.Random(SEED ^ 0x11)
    work = ImageEnhance.Contrast(base).enhance(1.08)
    work = ImageEnhance.Brightness(work).enhance(0.83)

    r, g, b = work.split()
    r = ImageChops.offset(r, 3, 0)
    b = ImageChops.offset(b, -3, 0)
    work = Image.merge("RGB", (r, g, b))

    overlay = Image.new("RGBA", SIZE, (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    for y in range(0, SIZE[1], 5):
        alpha = 11 if (y // 5) % 2 == 0 else 5
        draw.rectangle((0, y, SIZE[0], min(SIZE[1], y + 1)), fill=(70, 210, 195, alpha))

    for _ in range(13):
        y = rng.randint(12, SIZE[1] - 18)
        h = rng.randint(2, 9)
        x1 = rng.randint(260, 520)
        x2 = min(SIZE[0], x1 + rng.randint(90, 240))
        alpha = rng.randint(20, 48)
        draw.rectangle((x1, y, x2, y + h), fill=(62, 230, 210, alpha))
        if rng.random() < 0.55:
            draw.rectangle((x1 - 7, y + h + 1, x2 + 12, y + h + 2), fill=(255, 255, 255, alpha // 2))

    draw.rectangle((724, 30, 731, 402), fill=(60, 210, 190, 26))
    for y in range(42, 397, 18):
        width = rng.randint(10, 26)
        draw.rectangle((735, y, min(766, 735 + width), y + 2), fill=(130, 255, 238, 55))

    return Image.alpha_composite(work.convert("RGBA"), overlay).convert("RGB")


def tesla_breach(base: Image.Image) -> Image.Image:
    rng = random.Random(SEED ^ 0x22)
    work = ImageEnhance.Contrast(base).enhance(1.12)
    work = ImageEnhance.Color(work).enhance(0.88)
    work = ImageEnhance.Brightness(work).enhance(0.80)

    glow = Image.new("RGBA", SIZE, (0, 0, 0, 0))
    sharp = Image.new("RGBA", SIZE, (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    sharp_draw = ImageDraw.Draw(sharp)

    starts = [(700, 46), (655, 116), (742, 192), (676, 285)]
    for sx, sy in starts:
        points = [(sx, sy)]
        x, y = sx, sy
        segments = rng.randint(5, 8)
        for _ in range(segments):
            x -= rng.randint(35, 78)
            y += rng.randint(-34, 35)
            x = max(255, x)
            y = max(18, min(SIZE[1] - 18, y))
            points.append((x, y))
        glow_draw.line(points, fill=(80, 190, 255, 150), width=11, joint="curve")
        sharp_draw.line(points, fill=(205, 240, 255, 235), width=2, joint="curve")

        if len(points) >= 4:
            px, py = points[len(points) // 2]
            fork = [(px, py), (px - rng.randint(26, 54), py + rng.randint(-42, 42)),
                    (px - rng.randint(58, 88), py + rng.randint(-58, 58))]
            glow_draw.line(fork, fill=(70, 170, 255, 105), width=7)
            sharp_draw.line(fork, fill=(175, 225, 255, 205), width=1)

    glow = glow.filter(ImageFilter.GaussianBlur(radius=10))
    composite = Image.alpha_composite(work.convert("RGBA"), glow)
    composite = Image.alpha_composite(composite, sharp)

    light = Image.new("L", SIZE, 0)
    light_draw = ImageDraw.Draw(light)
    light_draw.ellipse((430, -40, 880, 470), fill=110)
    light = light.filter(ImageFilter.GaussianBlur(65))
    blue = Image.new("RGB", SIZE, (20, 105, 170))
    lit = Image.composite(blue, composite.convert("RGB"), light)
    return Image.blend(lit, composite.convert("RGB"), 0.72)


def stronghold_red_alert(base: Image.Image) -> Image.Image:
    rng = random.Random(SEED ^ 0x33)
    work = ImageEnhance.Contrast(base).enhance(1.10)
    work = ImageEnhance.Brightness(work).enhance(0.78)

    haze = Image.new("RGBA", SIZE, (0, 0, 0, 0))
    haze_draw = ImageDraw.Draw(haze)
    haze_draw.ellipse((430, -180, 880, 250), fill=(210, 42, 28, 105))
    haze_draw.ellipse((560, 120, 900, 520), fill=(180, 28, 24, 64))
    haze = haze.filter(ImageFilter.GaussianBlur(58))
    composite = Image.alpha_composite(work.convert("RGBA"), haze)

    overlay = Image.new("RGBA", SIZE, (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    draw.rectangle((0, 0, SIZE[0], 9), fill=(155, 20, 16, 80))
    draw.rectangle((0, SIZE[1] - 7, SIZE[0], SIZE[1]), fill=(120, 16, 14, 65))
    for x in range(510, 768, 42):
        if rng.random() < 0.82:
            draw.rectangle((x, 22, min(767, x + 18), 26), fill=(255, 84, 60, rng.randint(50, 85)))
    for _ in range(70):
        x = rng.randint(330, 755)
        y = rng.randint(24, 418)
        a = rng.randint(20, 65)
        draw.point((x, y), fill=(255, 150, 90, a))
    return Image.alpha_composite(composite, overlay).convert("RGB")


if __name__ == "__main__":
    save("nucleus_interference", nucleus_interference(load("dvn_official_02")))
    save("tesla_breach", tesla_breach(load("dvn_official_05")))
    save("stronghold_red_alert", stronghold_red_alert(load("dvn_official_01")))
