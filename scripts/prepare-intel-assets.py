#!/usr/bin/env python3
"""Polish dossier affiliation blocks while preserving supplied troop pixels."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageFilter, ImageEnhance
from io import BytesIO
import base64
import math
import random
import zlib
import numpy as np

ROOT = Path(__file__).resolve().parents[1]
INTEL = ROOT / "src/main/resources/assets/siege/textures/gui/intel"
RAW_TANKS = ROOT / "assets-source/intel-raw/tanks"
RAW_BOSSES = ROOT / "assets-source/intel-raw/bosses"
RAW_ELITES = ROOT / "assets-source/intel-raw/elites"
RAW_SUPER_UNITS = ROOT / "assets-source/intel-raw/super-units"

CONFIRMED = {
    "sniper", "grenadier", "gunner", "patriot",
    "specialist", "demoman", "artiller", "cloaker", "apu", "missiler",
    "zapper", "combatant",
}
ADVANCED = {"specialist", "demoman", "artiller", "cloaker", "apu", "missiler"}
SECURE = {"agreement"}
TANK_SPECS = {
    "zapper": ("TNK-001", "ZAPPER"),
    "combatant": ("TNK-002", "COMBATANT"),
    "agreement": ("TNK-003", "AGREEMENT"),
    "jagant": ("TNK-004", "JAGANT"),
    "strider": ("TNK-005", "STRIDER"),
}
BOSS_SPECS = {
    "tempest": ("BOS-001", "TEMPEST"),
    "fusilier": ("BOS-002", "FUSILIER"),
    "achilles": ("BOS-003", "ACHILLES"),
    "trident": ("BOS-004", "TRIDENT"),
    "prometheus": ("BOS-005", "PROMETHEUS"),
    "daedalus": ("BOS-006", "DAEDALUS"),
    "hermes": ("BOS-007", "HERMES"),
    "lelantos": ("BOS-008", "LELANTOS"),
    "gaia": ("BOS-009", "GAIA"),
}
ELITE_SPECS = {
    "agares": ("ELT-001", "AGARES"),
    "ghost": ("ELT-002", "GHOST"),
    "aurelionis": ("ELT-003", "AURELIONIS"),
}
SUPER_UNIT_SPECS = {
    "atlas": ("SUP-001", "ATLAS"),
}
BOSS_FRAME_COUNT = 6
EXPECTED = {
    "infantry", "shielder", "saboteur", "stalker", "natzuka", "sniper", "grenadier", "gunner", "jetpacker", "patriot",
    "specialist", "demoman", "artiller", "cloaker", "apu", "missiler",
    *TANK_SPECS.keys(),
    *ELITE_SPECS.keys(),
    *SUPER_UNIT_SPECS.keys(),
}

FONT_MONO = "/usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf"
FONT_MONO_B = "/usr/share/fonts/truetype/dejavu/DejaVuSansMono-Bold.ttf"
FONT_SERIF = "/usr/share/fonts/truetype/dejavu/DejaVuSerif.ttf"
FONT_SERIF_B = "/usr/share/fonts/truetype/dejavu/DejaVuSerif-Bold.ttf"


def seed_for(name: str, salt: int = 0) -> int:
    return (zlib.crc32(name.encode("utf-8")) + salt * 0x9E3779B1) & 0xFFFFFFFF


def font(path: str, size: int):
    try:
        return ImageFont.truetype(path, size)
    except OSError:
        return ImageFont.load_default()


def paper_noise(size, base, seed, amp=6):
    rng = np.random.default_rng(seed)
    w, h = size
    rgb = np.empty((h, w, 3), dtype=np.float32)
    rgb[:] = base
    rgb += rng.normal(0, amp, (h, w, 1))
    rgb = np.clip(rgb, 0, 255).astype(np.uint8)
    alpha = np.full((h, w, 1), 255, dtype=np.uint8)
    return Image.fromarray(np.concatenate([rgb, alpha], axis=2), "RGBA")


def star_points(cx, cy, radius):
    points = []
    for i in range(10):
        angle = -math.pi / 2 + i * math.pi / 5
        r = radius if i % 2 == 0 else radius * 0.42
        points.append((cx + math.cos(angle) * r, cy + math.sin(angle) * r))
    return points


def nusia_flag(w, h, seed, advanced=False):
    yellow = (190, 164, 51) if advanced else (204, 177, 57)
    blue = (24, 77, 120) if advanced else (35, 78, 116)
    green = (47, 95, 74) if advanced else (70, 108, 74)
    image = Image.new("RGBA", (w, h))
    draw = ImageDraw.Draw(image)
    a = w // 3
    b = 2 * w // 3
    draw.rectangle((0, 0, a - 1, h - 1), fill=(*yellow, 255))
    draw.rectangle((a, 0, b - 1, h - 1), fill=(*blue, 255))
    draw.rectangle((b, 0, w - 1, h - 1), fill=(*green, 255))
    centers = [(w * .50, h * .27), (w * .43, h * .50), (w * .57, h * .50), (w * .50, h * .73)]
    for cx, cy in centers:
        draw.polygon(star_points(cx, cy, max(2.0, h * .075)), fill=(*yellow, 245))
    arr = np.array(image).astype(np.int16)
    rng = np.random.default_rng(seed)
    arr[:, :, :3] = np.clip(arr[:, :, :3] + rng.normal(0, 4, (h, w, 1)), 0, 255)
    arr = arr.astype(np.uint8)
    for y in range(0, h, 7):
        arr[y, :, 3] = 220
    return Image.fromarray(arr, "RGBA")


def distress_alpha(size, seed, density=.035):
    w, h = size
    rng = np.random.default_rng(seed)
    alpha = np.full((h, w), 238, dtype=np.uint8)
    speckles = rng.random((h, w)) < density
    alpha[speckles] = rng.integers(120, 225, speckles.sum(), dtype=np.uint8)
    return Image.fromarray(alpha, "L").filter(ImageFilter.GaussianBlur(.2))


def fit_cover(image, size):
    target_w, target_h = size
    scale = max(target_w / image.width, target_h / image.height)
    resized = image.resize((round(image.width * scale), round(image.height * scale)), Image.Resampling.LANCZOS)
    left = (resized.width - target_w) // 2
    top = (resized.height - target_h) // 2
    return resized.crop((left, top, left + target_w, top + target_h))


def fit_contain(image, size):
    target_w, target_h = size
    scale = min(target_w / image.width, target_h / image.height)
    return image.resize((max(1, round(image.width * scale)), max(1, round(image.height * scale))), Image.Resampling.LANCZOS)


def tank_dossier(name, code, display_name, source):
    seed = seed_for(name, 7)
    rng = random.Random(seed)
    canvas = paper_noise((640, 360), (205, 194, 164), seed, 9)
    draw = ImageDraw.Draw(canvas, "RGBA")
    accent = (190, 116, 34, 245)

    raw = Image.open(source).convert("RGBA")
    photo_box = (13, 58, 627, 326)
    photo_size = (photo_box[2] - photo_box[0], photo_box[3] - photo_box[1])
    if name == "strider":
        backdrop = paper_noise(photo_size, (47, 50, 47), seed_for(name, 8), 12)
        for y in range(photo_size[1]):
            shade = int(22 * y / max(1, photo_size[1] - 1))
            ImageDraw.Draw(backdrop).line((0, y, photo_size[0], y), fill=(38 + shade, 42 + shade, 39 + shade, 255))
        arr = np.array(raw)
        alpha = 255 - np.clip((arr[:, :, :3].min(axis=2) - 210) * 6, 0, 255).astype(np.uint8)
        arr[:, :, 3] = np.minimum(arr[:, :, 3], alpha)
        cutout = fit_contain(Image.fromarray(arr, "RGBA"), (530, 225))
        shadow = Image.new("RGBA", cutout.size, (0, 0, 0, 0))
        shadow.putalpha(cutout.getchannel("A").filter(ImageFilter.GaussianBlur(7)))
        px = (photo_size[0] - cutout.width) // 2
        py = (photo_size[1] - cutout.height) // 2
        backdrop.alpha_composite(shadow, (px + 5, py + 8))
        backdrop.alpha_composite(cutout, (px, py))
        photo = backdrop
    else:
        photo = fit_cover(raw, photo_size)
        gray = photo.convert("L").convert("RGBA")
        photo = Image.blend(photo, gray, .22)
        photo = photo.filter(ImageFilter.GaussianBlur(.25))

    canvas.alpha_composite(photo, (photo_box[0], photo_box[1]))
    draw.rectangle(photo_box, outline=(64, 54, 42, 190), width=2)
    draw.rectangle((3, 3, 636, 356), outline=accent, width=5)
    draw.line((20, 51, 438, 51), fill=(74, 62, 46, 170), width=2)
    draw.text((20, 13), f"{code} / {display_name}", font=font(FONT_SERIF_B, 24), fill=(42, 36, 28, 245))
    draw.text((24, 76), "FIELD INTEL: TANK", font=font(FONT_MONO_B, 8), fill=(224, 214, 183, 190))
    draw.text((24, 89), f"REF: {code}", font=font(FONT_MONO, 8), fill=(224, 214, 183, 170))
    draw.text((24, 102), "STATUS: CLASSIFIED", font=font(FONT_MONO_B, 8), fill=(183, 65, 47, 205))

    stamp = Image.new("RGBA", (390, 92), (0, 0, 0, 0))
    stamp_draw = ImageDraw.Draw(stamp, "RGBA")
    stamp_draw.rectangle((6, 8, 383, 84), outline=(161, 16, 13, 210), width=5)
    stamp_draw.rectangle((13, 15, 376, 77), outline=(161, 16, 13, 155), width=2)
    stamp_draw.text((31, 17), "CLASSIFIED", font=font(FONT_SERIF_B, 48), fill=(169, 17, 13, 215))
    stamp = stamp.rotate(10, resample=Image.Resampling.BICUBIC, expand=True)
    original_alpha = np.array(stamp.getchannel("A"), dtype=np.uint16)
    wear = np.array(distress_alpha(stamp.size, seed_for(name, 9), .10), dtype=np.uint16)
    stamp.putalpha(Image.fromarray((original_alpha * wear // 255).astype(np.uint8), "L"))
    canvas.alpha_composite(stamp, (238, 225))

    draw.rectangle((18, 330, 215, 351), outline=(82, 68, 49, 130), width=1)
    draw.text((26, 336), "TACTICAL INTELLIGENCE ARCHIVE", font=font(FONT_MONO_B, 7), fill=(61, 52, 41, 190))
    draw.rectangle((482, 330, 622, 351), outline=(82, 68, 49, 130), width=1)
    draw.text((492, 336), "ACCESS: PRIORITY-03", font=font(FONT_MONO_B, 7), fill=(130, 48, 39, 170))

    for _ in range(28):
        x = rng.randrange(8, 632)
        y = rng.randrange(6, 354)
        length = rng.randrange(4, 34)
        draw.line((x, y, min(633, x + length), y + rng.choice((-1, 0, 1))), fill=(80, 68, 50, rng.randrange(18, 55)), width=1)
    canvas.convert("RGB").save(INTEL / f"{name}.png", optimize=True)


def generate_tank_dossiers():
    missing = []
    for name, (code, display_name) in TANK_SPECS.items():
        source = RAW_TANKS / f"{name}.png"
        if not source.exists():
            missing.append(str(source.relative_to(ROOT)))
            continue
        tank_dossier(name, code, display_name, source)
    if missing:
        raise SystemExit("Missing Tank sources: " + ", ".join(missing))


def boss_dossier_frame(name, code, display_name, frame_index, source, destination):
    """Turn one supplied boss-video frame into a compact archival motion record."""
    seed = seed_for(name, 30 + frame_index)
    rng = random.Random(seed)
    canvas = paper_noise((640, 360), (199, 190, 169), seed, 8)
    draw = ImageDraw.Draw(canvas, "RGBA")
    accent = (157, 25, 39, 235)

    raw = Image.open(source).convert("RGBA")
    # The supplied videos carry a presentation title in the upper-left corner.
    # Remove that capture-only overlay before fitting the evidence into SIEGE's dossier.
    raw = raw.crop((0, min(120, raw.height // 4), raw.width, raw.height))
    photo_box = (13, 58, 627, 326)
    photo_size = (photo_box[2] - photo_box[0], photo_box[3] - photo_box[1])
    photo = fit_cover(raw, photo_size)
    photo = ImageEnhance.Color(photo).enhance(.58)
    photo = ImageEnhance.Contrast(photo).enhance(1.12)
    gray = photo.convert("L").convert("RGBA")
    photo = Image.blend(photo, gray, .18)
    tint = Image.new("RGBA", photo_size, (72, 33, 28, 22))
    photo = Image.alpha_composite(photo, tint)

    canvas.alpha_composite(photo, (photo_box[0], photo_box[1]))
    draw.rectangle(photo_box, outline=(58, 48, 39, 205), width=2)
    for scan_y in range(photo_box[1] + 2, photo_box[3], 5):
        draw.line((photo_box[0] + 2, scan_y, photo_box[2] - 2, scan_y), fill=(27, 24, 22, 24), width=1)
    for _ in range(18):
        y = rng.randrange(photo_box[1] + 2, photo_box[3] - 2)
        x = rng.randrange(photo_box[0] + 2, photo_box[2] - 24)
        draw.line((x, y, min(photo_box[2] - 2, x + rng.randrange(8, 55)), y), fill=(228, 218, 191, 18), width=1)

    draw.rectangle((3, 3, 636, 356), outline=accent, width=5)
    draw.line((20, 51, 438, 51), fill=(67, 55, 44, 170), width=2)
    draw.text((20, 13), f"{code} / {display_name}", font=font(FONT_SERIF_B, 24), fill=(39, 32, 27, 245))
    draw.text((24, 76), "FIELD INTEL: BOSS", font=font(FONT_MONO_B, 8), fill=(231, 219, 194, 205))
    draw.text((24, 89), "SOURCE: ARCHIVAL VIDEO", font=font(FONT_MONO, 8), fill=(231, 219, 194, 185))
    draw.text((24, 102), "STATUS: VISUAL EVIDENCE", font=font(FONT_MONO_B, 8), fill=(194, 73, 68, 220))

    tag = paper_noise((168, 52), (208, 198, 177), seed_for(name, 60 + frame_index), 5)
    tag_draw = ImageDraw.Draw(tag, "RGBA")
    tag_draw.rectangle((0, 0, 167, 51), outline=(72, 61, 49, 170), width=1)
    tag_draw.text((8, 8), "MOTION RECORD", font=font(FONT_MONO_B, 9), fill=(54, 47, 39, 230))
    tag_draw.text((8, 23), "STRONGHOLD 5-5", font=font(FONT_MONO_B, 8), fill=(112, 42, 40, 210))
    tag_draw.text((8, 37), "VISUAL EVIDENCE", font=font(FONT_MONO, 7), fill=(73, 65, 55, 190))
    tag.putalpha(distress_alpha(tag.size, seed_for(name, 70 + frame_index), .025))
    canvas.alpha_composite(tag, (459, 8))

    stamp = Image.new("RGBA", (330, 62), (0, 0, 0, 0))
    stamp_draw = ImageDraw.Draw(stamp, "RGBA")
    stamp_draw.rectangle((5, 6, 324, 56), outline=(153, 18, 27, 210), width=4)
    stamp_draw.text((23, 13), "BOSS // CLASSIFIED", font=font(FONT_SERIF_B, 28), fill=(161, 20, 30, 215))
    stamp = stamp.rotate(8, resample=Image.Resampling.BICUBIC, expand=True)
    original_alpha = np.array(stamp.getchannel("A"), dtype=np.uint16)
    wear = np.array(distress_alpha(stamp.size, seed_for(name, 80 + frame_index), .09), dtype=np.uint16)
    stamp.putalpha(Image.fromarray((original_alpha * wear // 255).astype(np.uint8), "L"))
    canvas.alpha_composite(stamp, (286, 272))

    draw.rectangle((18, 330, 238, 351), outline=(82, 68, 49, 130), width=1)
    draw.text((26, 336), "TACTICAL VIDEO INTELLIGENCE", font=font(FONT_MONO_B, 7), fill=(61, 52, 41, 190))
    draw.rectangle((482, 330, 622, 351), outline=(82, 68, 49, 130), width=1)
    draw.text((492, 336), "ACCESS: PRIORITY-01", font=font(FONT_MONO_B, 7), fill=(130, 48, 39, 180))

    destination.parent.mkdir(parents=True, exist_ok=True)
    canvas.convert("RGB").save(destination, compress_level=6)


def generate_boss_dossiers():
    missing = []
    unexpected = []
    expected_directories = set(BOSS_SPECS)
    if RAW_BOSSES.exists():
        unexpected.extend(
            str(path.relative_to(ROOT))
            for path in RAW_BOSSES.iterdir()
            if path.is_dir() and path.name not in expected_directories
        )
    for name, (code, display_name) in BOSS_SPECS.items():
        expected_sources = {f"frame_{index:02d}.jpg" for index in range(BOSS_FRAME_COUNT)}
        source_directory = RAW_BOSSES / name
        if source_directory.exists():
            unexpected.extend(
                str(path.relative_to(ROOT))
                for path in source_directory.iterdir()
                if path.is_file() and path.name not in expected_sources
            )
        for frame_index in range(BOSS_FRAME_COUNT):
            source = source_directory / f"frame_{frame_index:02d}.jpg"
            if not source.exists():
                missing.append(str(source.relative_to(ROOT)))
                continue
            destination = INTEL / "bosses" / name / f"frame_{frame_index:02d}.png"
            boss_dossier_frame(name, code, display_name, frame_index, source, destination)
            with Image.open(destination) as verified:
                verified.verify()
            with Image.open(destination) as verified:
                if verified.size != (640, 360) or verified.format != "PNG":
                    raise SystemExit(
                        f"Boss frame must be a valid 640x360 PNG: {destination.relative_to(ROOT)}"
                    )
        print(f"SIEGE intel: prepared {display_name} motion record ({BOSS_FRAME_COUNT} frames)")
    if missing:
        raise SystemExit("Missing Boss frame sources: " + ", ".join(missing))
    if unexpected:
        raise SystemExit("Unexpected Boss frame sources: " + ", ".join(sorted(unexpected)))


def elite_dossier(name, code, display_name, source):
    """Turn one supplied Elite-video still into a classified static dossier."""
    seed = seed_for(name, 90)
    rng = random.Random(seed)
    canvas = paper_noise((640, 360), (198, 190, 174), seed, 7)
    draw = ImageDraw.Draw(canvas, "RGBA")
    accent = (121, 55, 151, 240)

    raw = Image.open(source).convert("RGBA")
    # The logo occupies the upper-left quadrant; frame the subject from the clean
    # portion of the capture instead of baking promotional text into the dossier.
    raw = raw.crop((min(470, raw.width // 3), min(120, raw.height // 4), raw.width, raw.height))
    photo_box = (13, 58, 627, 326)
    photo_size = (photo_box[2] - photo_box[0], photo_box[3] - photo_box[1])
    photo = fit_cover(raw, photo_size)
    photo = ImageEnhance.Color(photo).enhance(.72)
    photo = ImageEnhance.Contrast(photo).enhance(1.10)
    photo = Image.alpha_composite(photo, Image.new("RGBA", photo_size, (62, 30, 77, 18)))
    canvas.alpha_composite(photo, (photo_box[0], photo_box[1]))

    draw.rectangle(photo_box, outline=(62, 43, 70, 210), width=2)
    for scan_y in range(photo_box[1] + 2, photo_box[3], 6):
        draw.line((photo_box[0] + 2, scan_y, photo_box[2] - 2, scan_y), fill=(25, 19, 28, 24), width=1)
    draw.rectangle((3, 3, 636, 356), outline=accent, width=5)
    draw.line((20, 51, 438, 51), fill=(75, 49, 81, 175), width=2)
    draw.text((20, 13), f"{code} / {display_name}", font=font(FONT_SERIF_B, 24), fill=(39, 31, 42, 245))
    draw.text((24, 76), "FIELD INTEL: ELITE", font=font(FONT_MONO_B, 8), fill=(237, 222, 239, 215))
    draw.text((24, 89), "SOURCE: AERIAL RECON", font=font(FONT_MONO, 8), fill=(237, 222, 239, 190))
    draw.text((24, 102), "STATUS: UNKNOWN" if name == "aurelionis" else "STATUS: HOSTILE ELITE",
              font=font(FONT_MONO_B, 8), fill=(228, 175, 238, 225))

    stamp = Image.new("RGBA", (350, 66), (0, 0, 0, 0))
    stamp_draw = ImageDraw.Draw(stamp, "RGBA")
    stamp_draw.rectangle((5, 6, 344, 60), outline=(111, 39, 142, 220), width=4)
    stamp_draw.text((21, 13), "ELITE // CLASSIFIED", font=font(FONT_SERIF_B, 27), fill=(119, 42, 151, 220))
    stamp = stamp.rotate(7, resample=Image.Resampling.BICUBIC, expand=True)
    original_alpha = np.array(stamp.getchannel("A"), dtype=np.uint16)
    wear = np.array(distress_alpha(stamp.size, seed_for(name, 91), .09), dtype=np.uint16)
    stamp.putalpha(Image.fromarray((original_alpha * wear // 255).astype(np.uint8), "L"))
    canvas.alpha_composite(stamp, (266, 269))

    draw.rectangle((18, 330, 240, 351), outline=(82, 58, 91, 140), width=1)
    draw.text((26, 336), "STRONGHOLD 5-5 // ELITE FILE", font=font(FONT_MONO_B, 7), fill=(65, 48, 70, 200))
    for _ in range(22):
        x = rng.randrange(8, 632)
        y = rng.randrange(6, 354)
        draw.line((x, y, min(633, x + rng.randrange(4, 30)), y), fill=(74, 53, 79, rng.randrange(15, 45)), width=1)
    canvas.convert("RGB").save(INTEL / f"{name}.png", optimize=True)


def generate_elite_dossiers():
    INTEL.mkdir(parents=True, exist_ok=True)
    missing = []
    expected_sources = {f"{name}.jpg" for name in ELITE_SPECS}
    if RAW_ELITES.exists():
        unexpected = sorted(
            str(path.relative_to(ROOT)) for path in RAW_ELITES.iterdir()
            if path.is_file() and path.name not in expected_sources
        )
        if unexpected:
            raise SystemExit("Unexpected Elite sources: " + ", ".join(unexpected))
    for name, (code, display_name) in ELITE_SPECS.items():
        source = RAW_ELITES / f"{name}.jpg"
        if not source.exists():
            missing.append(str(source.relative_to(ROOT)))
            continue
        elite_dossier(name, code, display_name, source)
        destination = INTEL / f"{name}.png"
        with Image.open(destination) as verified:
            if verified.size != (640, 360) or verified.format != "PNG":
                raise SystemExit(f"Elite dossier must be a valid 640x360 PNG: {destination.relative_to(ROOT)}")
        print(f"SIEGE intel: prepared {display_name} Elite dossier")
    if missing:
        raise SystemExit("Missing Elite sources: " + ", ".join(missing))


def super_unit_dossier(name, code, display_name, source):
    """Create a high-alert gold dossier while preserving the supplied unit render."""
    seed = seed_for(name, 120)
    rng = random.Random(seed)
    canvas = paper_noise((640, 360), (194, 185, 160), seed, 6)
    draw = ImageDraw.Draw(canvas, "RGBA")
    gold = (205, 160, 48, 242)

    raw = Image.open(BytesIO(base64.b64decode(source.read_text(encoding="ascii")))).convert("RGBA")
    # The supplied clip has a title block in the upper-left. Keep the complete
    # Atlas silhouette and exclude that promotional block from the dossier.
    photo_box = (13, 58, 627, 326)
    photo_size = (photo_box[2] - photo_box[0], photo_box[3] - photo_box[1])
    photo = fit_contain(raw, photo_size)
    photo = ImageEnhance.Color(photo).enhance(.78)
    photo = ImageEnhance.Contrast(photo).enhance(1.14)
    background = Image.new("RGBA", photo_size, (47, 35, 19, 255))
    background.alpha_composite(photo, ((photo_size[0] - photo.width) // 2, (photo_size[1] - photo.height) // 2))
    canvas.alpha_composite(background, (photo_box[0], photo_box[1]))

    draw.rectangle(photo_box, outline=(74, 55, 27, 230), width=2)
    for scan_y in range(photo_box[1] + 2, photo_box[3], 6):
        draw.line((photo_box[0] + 2, scan_y, photo_box[2] - 2, scan_y), fill=(19, 15, 8, 30), width=1)
    draw.rectangle((3, 3, 636, 356), outline=gold, width=5)
    draw.rectangle((8, 8, 631, 351), outline=(94, 67, 25, 155), width=1)
    draw.line((20, 51, 438, 51), fill=(91, 68, 33, 190), width=2)
    draw.text((20, 13), f"{code} / {display_name}", font=font(FONT_SERIF_B, 24), fill=(43, 34, 23, 250))
    draw.text((24, 76), "FIELD INTEL: SUPER UNIT", font=font(FONT_MONO_B, 8), fill=(255, 225, 143, 230))
    draw.text((24, 89), "VITAL READING: 125,000,000 HP", font=font(FONT_MONO_B, 8), fill=(255, 204, 82, 235))
    draw.text((24, 102), "STATUS: MAXIMUM ALERT", font=font(FONT_MONO_B, 8), fill=(255, 174, 71, 235))

    stamp = Image.new("RGBA", (390, 66), (0, 0, 0, 0))
    stamp_draw = ImageDraw.Draw(stamp, "RGBA")
    stamp_draw.rectangle((5, 6, 384, 60), outline=(174, 125, 30, 225), width=4)
    stamp_draw.text((18, 13), "SUPER UNIT // OMEGA", font=font(FONT_SERIF_B, 27), fill=(177, 128, 31, 225))
    stamp = stamp.rotate(6, resample=Image.Resampling.BICUBIC, expand=True)
    original_alpha = np.array(stamp.getchannel("A"), dtype=np.uint16)
    wear = np.array(distress_alpha(stamp.size, seed_for(name, 121), .075), dtype=np.uint16)
    stamp.putalpha(Image.fromarray((original_alpha * wear // 255).astype(np.uint8), "L"))
    canvas.alpha_composite(stamp, (220, 270))

    draw.rectangle((18, 330, 258, 351), outline=(94, 68, 26, 150), width=1)
    draw.text((26, 336), "STRONGHOLD 5-5 // OMEGA FILE", font=font(FONT_MONO_B, 7), fill=(75, 56, 29, 210))
    for _ in range(26):
        x = rng.randrange(8, 632)
        y = rng.randrange(6, 354)
        draw.line((x, y, min(633, x + rng.randrange(4, 30)), y), fill=(93, 68, 29, rng.randrange(15, 42)), width=1)
    canvas.convert("RGB").save(INTEL / f"{name}.png", optimize=True)


def generate_super_unit_dossiers():
    expected_sources = {f"{name}.jpg.b64" for name in SUPER_UNIT_SPECS}
    actual_sources = {path.name for path in RAW_SUPER_UNITS.iterdir() if path.is_file()} if RAW_SUPER_UNITS.exists() else set()
    unexpected = actual_sources - expected_sources
    missing = expected_sources - actual_sources
    if unexpected:
        raise SystemExit("Unexpected Super Unit sources: " + ", ".join(sorted(unexpected)))
    if missing:
        raise SystemExit("Missing Super Unit sources: " + ", ".join(sorted(missing)))
    for name, (code, display_name) in SUPER_UNIT_SPECS.items():
        super_unit_dossier(name, code, display_name, RAW_SUPER_UNITS / f"{name}.jpg.b64")
        destination = INTEL / f"{name}.png"
        with Image.open(destination) as verified:
            if verified.size != (640, 360) or verified.format != "PNG":
                raise SystemExit(f"Super Unit dossier must be a valid 640x360 PNG: {destination.relative_to(ROOT)}")
        print(f"SIEGE intel: prepared {display_name} Super Unit dossier")


def common_patch(base, name, confirmed):
    x, y, w, h = 454, 10, 174, 60
    seed = seed_for(name)
    patch = paper_noise((w, h), (211, 199, 169), seed, 6)
    draw = ImageDraw.Draw(patch)
    draw.rectangle((0, 0, w - 1, h - 1), outline=(102, 84, 59, 120), width=1)
    draw.line((5, 6, w - 7, 6), fill=(101, 81, 56, 90), width=1)
    draw.line((5, h - 7, w - 7, h - 7), fill=(101, 81, 56, 70), width=1)
    draw.ellipse((137, 12, 168, 43), outline=(120, 54, 44, 45), width=1)
    draw.ellipse((141, 16, 164, 39), outline=(120, 54, 44, 28), width=1)

    if confirmed:
        flag = nusia_flag(58, 34, seed, False)
        draw.rectangle((8, 12, 69, 49), outline=(94, 77, 52, 145), width=1)
        patch.alpha_composite(flag, (10, 14))
        draw.text((76, 11), "REPUBLIC OF", font=font(FONT_SERIF_B, 7), fill=(57, 49, 37, 225))
        draw.text((76, 20), "NUSIA", font=font(FONT_SERIF_B, 12), fill=(45, 39, 31, 238))
        draw.line((75, 34, 133, 34), fill=(100, 82, 58, 105), width=1)
        draw.text((76, 38), "ORIGIN VERIFIED", font=font(FONT_MONO_B, 6), fill=(126, 50, 43, 185))
    else:
        draw.text((9, 10), "AFFILIATION RECORD", font=font(FONT_SERIF_B, 8), fill=(58, 50, 39, 225))
        draw.text((9, 21), "FIELD ORIGIN", font=font(FONT_MONO, 6), fill=(94, 79, 59, 180))
        rng = random.Random(seed_for(name, 1))
        for bar_y, bar_w in ((31, 101), (41, 76)):
            points = [
                (9, bar_y + rng.randint(-1, 1)),
                (9 + bar_w, bar_y + rng.randint(-1, 1)),
                (9 + bar_w, bar_y + 7 + rng.randint(-1, 1)),
                (9, bar_y + 7 + rng.randint(-1, 1)),
            ]
            draw.polygon(points, fill=(39, 33, 27, 236))
            for _ in range(7):
                sx = rng.randrange(10, 8 + bar_w)
                sy = rng.randrange(bar_y, bar_y + 7)
                draw.line((sx, sy, min(9 + bar_w, sx + rng.randrange(2, 7)), sy), fill=(103, 87, 65, 65), width=1)
        draw.text((116, 43), "SEALED", font=font(FONT_MONO_B, 6), fill=(126, 50, 43, 155))

    patch.putalpha(distress_alpha((w, h), seed_for(name, 2), .03))
    base.alpha_composite(patch, (x, y))


def advanced_patch(base, name):
    x, y, w, h = 454, 8, 174, 61
    seed = seed_for(name)
    patch = Image.new("RGBA", (w, h), (5, 19, 31, 243))
    draw = ImageDraw.Draw(patch)
    for gx in range(0, w, 12):
        draw.line((gx, 0, gx, h), fill=(28, 91, 126, 47), width=1)
    for gy in range(0, h, 10):
        draw.line((0, gy, w, gy), fill=(28, 91, 126, 42), width=1)
    draw.rectangle((1, 1, w - 2, h - 2), outline=(51, 145, 198, 190), width=1)
    draw.text((7, 8), "AFFILIATION // NUSIA", font=font(FONT_MONO_B, 7), fill=(89, 190, 238, 225))
    draw.text((7, 23), "ORIGIN VERIFIED", font=font(FONT_MONO_B, 6), fill=(183, 201, 205, 200))
    draw.text((7, 36), "MIL-INTEL // 2044", font=font(FONT_MONO, 6), fill=(72, 145, 177, 155))
    flag = nusia_flag(52, 30, seed, True)
    draw.rectangle((112, 9, 168, 43), outline=(55, 148, 200, 155), width=1)
    patch.alpha_composite(flag, (114, 11))
    draw.line((7, 49, 164, 49), fill=(48, 129, 173, 100), width=1)
    patch.putalpha(distress_alpha((w, h), seed_for(name, 3), .045))
    base.alpha_composite(patch, (x, y))


def secure_patch(base, name):
    x, y, w, h = 454, 10, 174, 60
    seed = seed_for(name, 12)
    patch = paper_noise((w, h), (207, 200, 181), seed, 5)
    draw = ImageDraw.Draw(patch)
    draw.rectangle((0, 0, w - 1, h - 1), outline=(69, 72, 66, 150), width=1)
    draw.rectangle((8, 9, 52, 51), outline=(58, 62, 58, 185), width=2)
    draw.text((19, 15), "S", font=font(FONT_SERIF_B, 23), fill=(40, 44, 41, 230))
    draw.text((60, 10), "SECURE", font=font(FONT_SERIF_B, 9), fill=(48, 49, 44, 230))
    draw.text((60, 22), "CONTAIN", font=font(FONT_SERIF_B, 9), fill=(48, 49, 44, 230))
    draw.text((60, 34), "PROTECT", font=font(FONT_SERIF_B, 9), fill=(48, 49, 44, 230))
    draw.text((60, 47), "CORP. FILE", font=font(FONT_MONO_B, 6), fill=(120, 49, 42, 180))
    patch.putalpha(distress_alpha((w, h), seed_for(name, 13), .035))
    base.alpha_composite(patch, (x, y))


def main():
    INTEL.mkdir(parents=True, exist_ok=True)
    generate_tank_dossiers()
    generate_boss_dossiers()
    generate_elite_dossiers()
    generate_super_unit_dossiers()
    files = sorted(INTEL.glob("*.png"))
    names = {path.stem for path in files}
    if names != EXPECTED:
        missing = ", ".join(sorted(EXPECTED - names)) or "none"
        unexpected = ", ".join(sorted(names - EXPECTED)) or "none"
        raise SystemExit(f"Intel texture set mismatch; missing: {missing}; unexpected: {unexpected}")
    for path in files:
        name = path.stem
        image = Image.open(path).convert("RGBA")
        if name in ADVANCED:
            advanced_patch(image, name)
        elif name in SECURE:
            secure_patch(image, name)
        else:
            common_patch(image, name, name in CONFIRMED)
        image.convert("RGB").save(path, optimize=True)
        with Image.open(path) as verified:
            if verified.size != (640, 360):
                raise SystemExit(f"Intel texture must be 640x360: {path.name} is {verified.size}")
        print(f"SIEGE intel: polished {path.name}")


if __name__ == "__main__":
    main()
