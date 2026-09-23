#!/usr/bin/env python3
"""Prepare SIEGE menu backgrounds without inventing source detail.

SIEGE keeps checked-in art at honest native/prepared resolutions instead of turning
small images into synthetic 1920x1080 masters. Native 16:9 sources remain at their
real resolution. Near-16:9 captures are cropped slightly instead of enlarged. Rooftop
Squad keeps the full 4:3 illustration over a softened 16:9 extension while the
foreground is never upscaled.

SIEGE 5.40 fetches six official DVN thumbnails separately and deliberately keeps them
at their native 768x432 resolution. Tempest Jutcherson remains an easter egg and is
never processed by this normal-background pipeline. Third Justice is prepared by its
own script at the end of this stage so the corrected full-color captures and complete
reel are always regenerated before validation.
"""
from pathlib import Path
from PIL import Image, ImageFilter, ImageStat
import runpy

ROOT = Path("src/main/resources/assets/siege/textures/gui/backgrounds")

# Real prepared dimensions used by SiegeSceneCatalog.
TARGETS = {
    "dummies_assault": (960, 540),
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

# Two checked-in captures are genuinely night scenes, but their source shadows are
# so compressed that the menu readability layer makes them almost disappear. A
# gentle gamma lift recovers source detail while retaining blacks/highlights and
# avoids the destructive brightness/contrast filters used by older builds.
SHADOW_GAMMA = {
    "night_operation": 0.78,
    "urban_rendezvous": 0.68,
}


def center_crop(image: Image.Image, target: tuple[int, int]) -> Image.Image:
    tw, th = target
    if image.width < tw or image.height < th:
        raise SystemExit(
            f"Refusing to upscale {image.size} into {target}; better source required"
        )
    left = (image.width - tw) // 2
    top = (image.height - th) // 2
    return image.crop((left, top, left + tw, top + th))


def gamma_lift(image: Image.Image, gamma: float) -> Image.Image:
    if gamma <= 0.0 or abs(gamma - 1.0) < 0.001:
        return image
    lut = [max(0, min(255, round(((value / 255.0) ** gamma) * 255.0))) for value in range(256)]
    # Pillow expects one LUT per RGB channel. Applying the same curve channel by
    # channel preserves the original colour balance instead of flattening to gray.
    channels = [channel.point(lut) for channel in image.split()]
    return Image.merge(image.mode, channels)


def rooftop_composite(image: Image.Image) -> Image.Image:
    target = TARGETS["rooftop_squad"]
    # Background extension may be soft because it is decorative only. The actual
    # illustration stays sharp, fully visible and is downscaled rather than enlarged.
    bg = image.resize(target, Image.Resampling.LANCZOS).filter(
        ImageFilter.GaussianBlur(radius=14)
    )
    bg = bg.convert("RGBA")
    shade = Image.new("RGBA", target, (0, 0, 0, 46))
    bg = Image.alpha_composite(bg, shade)

    fg = image.copy()
    fg.thumbnail((672, 504), Image.Resampling.LANCZOS)
    x = (target[0] - fg.width) // 2
    y = (target[1] - fg.height) // 2
    bg.alpha_composite(fg.convert("RGBA"), (x, y))
    return bg.convert("RGB")


def quality_check(name: str, image: Image.Image) -> None:
    w, h = image.size
    if w * 9 != h * 16:
        raise SystemExit(f"{name}: prepared image is not exact 16:9 ({w}x{h})")
    if w < 640 or h < 360:
        raise SystemExit(f"{name}: background below quality floor ({w}x{h})")

    gray = image.convert("L")
    stat = ImageStat.Stat(gray)
    mean = stat.mean[0]
    deviation = stat.stddev[0]
    # These are conservative corruption checks, not aesthetic grading. Dark scenes
    # are allowed; a nearly blank/flat export is not.
    if deviation < 7.0:
        raise SystemExit(f"{name}: suspiciously flat image (luma stddev={deviation:.2f})")
    if mean < 10.0:
        raise SystemExit(f"{name}: suspiciously black image (mean luma={mean:.2f})")


def prepare(name: str, target: tuple[int, int]) -> None:
    path = ROOT / f"{name}.png"
    if not path.is_file():
        raise SystemExit(f"Missing background source: {path}")
    with Image.open(path) as source:
        source.load()
        image = source.convert("RGB")
        original = image.size

    if name == "rooftop_squad":
        out = rooftop_composite(image)
    elif image.size == target:
        out = image
    else:
        out = center_crop(image, target)

    if name in SHADOW_GAMMA:
        out = gamma_lift(out, SHADOW_GAMMA[name])

    if out.size != target:
        raise SystemExit(f"Preparation failed for {name}: {out.size} != {target}")
    quality_check(name, out)
    out.save(path, "PNG", optimize=True, compress_level=9)
    action = "kept native" if original == target else f"prepared {target[0]}x{target[1]}"
    if name in SHADOW_GAMMA:
        action += f", shadows lifted γ={SHADOW_GAMMA[name]:.2f}"
    print(f"{name}: {original[0]}x{original[1]} -> {action}")


if __name__ == "__main__":
    for scene, target in TARGETS.items():
        prepare(scene, target)

    # Keep player-facing visual repair in one CI stage. Third Justice restores the
    # canonical full-color screenshots and prepares the complete supplied video reel.
    runpy.run_path("scripts/prepare-third-justice-media.py", run_name="__main__")
