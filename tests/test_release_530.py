#!/usr/bin/env python3
"""Durable SIEGE 5.30 visual-quality, DVN and Third Justice contracts."""
import base64
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
SCENES = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
BACKGROUNDS = read("src/main/java/uy/santipdr/siege/client/SiegeBackgrounds.java")
MEDIA = read("src/main/java/uy/santipdr/siege/client/SiegeMediaReferenceData.java")
FETCH = read("scripts/fetch-dvn-media-510.sh")
PREP_BG = read("scripts/prepare-backgrounds-hd.py")
AUDIT_BG = read("scripts/audit-backgrounds-530.py")
PREP_TJ = read("scripts/prepare-third-justice-media.py")
REEL = read("src/main/java/uy/santipdr/siege/client/SiegeEvidenceReelScreen.java")
VIDEO = read("src/main/java/uy/santipdr/siege/client/SiegeThirdJusticeVideo.java")
GUIDE = read("src/main/java/uy/santipdr/siege/client/SiegeGuideSupplemental.java")
MANIFEST = read("src/main/resources/assets/siege/third_justice_video.properties")
EMBEDDED_VIDEO = ROOT / "assets-source/third-justice/third_justice_full.b64"
STATIC_SOURCE = ROOT / "assets-source/third-justice/static-correct"
GUIDE_TEXTURES = ROOT / "src/main/resources/assets/siege/textures/gui/guide"

match = re.search(r"version\s*=\s*'([0-9]+)\.([0-9]+)\.([0-9]+)'", BUILD)
assert match
major, minor, patch = map(int, match.groups())
assert (major, minor) >= (5, 30), "5.30 contracts require SIEGE 5.30+"

# The original three official Roblox DVN thumbnails remain guaranteed. Later
# releases may add more, but native 768x432 and 16:9 quality remain mandatory.
for scene in ("dvn_official_01", "dvn_official_02", "dvn_official_03"):
    assert f'"{scene}"' in SCENES, scene
assert "urls[:" in FETCH
assert "rgb.size != (768, 432)" in FETCH
assert "Keep the official source at native size" in FETCH
assert "need 3" in FETCH or "need 6" in FETCH
assert 'for index in 01 02 03' in FETCH
assert 'dvn_official_${index}.png' in FETCH
for visual in ("official-gallery-01", "official-gallery-02", "official-gallery-03"):
    assert f'"{visual}"' in MEDIA, visual

# 5.30 removed the old fake-Full-HD background contract.
assert "LEGACY_W = 960" in SCENES and "LEGACY_H = 540" in SCENES
assert "COMPACT_W = 720" in SCENES and "COMPACT_H = 405" in SCENES
assert "ROOFTOP_W = 896" in SCENES and "ROOFTOP_H = 504" in SCENES
assert "TARGET = (1920, 1080)" not in PREP_BG
assert "Refusing to upscale" in PREP_BG
assert "kept native" in PREP_BG
assert "background below quality floor" in PREP_BG
assert "SHADOW_GAMMA" in PREP_BG
assert '"night_operation": 0.78' in PREP_BG
assert '"urban_rendezvous": 0.68' in PREP_BG
assert "EXPECTED =" in AUDIT_BG and '"dvn_official_03": (768, 432)' in AUDIT_BG
assert "suspiciously blurred/blank" in AUDIT_BG
assert "suspiciously posterized" in AUDIT_BG

# Full-screen backgrounds use cover scaling: fill the viewport, keep aspect ratio,
# crop excess on non-16:9 displays, never geometrically stretch the source.
assert "renderInternal(graphics, width, height, now, true);" in BACKGROUNDS
assert "scale = cover ? Math.max" in BACKGROUNDS and "Math.min" in BACKGROUNDS
assert "no bars, no stretching" in BACKGROUNDS

# Third Justice static screenshots were previously destroyed by 1-bit/2-bit
# quantization. The build must restore the supplied full-color captures, not try
# to brighten already-lost data.
assert "restore_static_captures" in PREP_TJ
assert "static-correct" in PREP_TJ
assert "third_justice_tooltip.webp.b64.part" in PREP_TJ
assert "third_justice_field.webp.b64.part" in PREP_TJ
assert "remap_visible" not in PREP_TJ
assert '"-nostdin"' in PREP_TJ
assert "regenerating full-color reel previews" in PREP_TJ
assert "No darkness/filter overlay is composited over evidence" in REEL
assert "SiegeBackgrounds.render(g, width, height" not in REEL
assert "0xE20A0C0F" not in REEL


def decode_transport(prefix: str) -> bytes:
    parts = sorted(STATIC_SOURCE.glob(prefix + "*"))
    assert parts, f"missing corrected Third Justice source: {prefix}*"
    payload = base64.b64decode(
        "".join(part.read_text(encoding="ascii").strip() for part in parts),
        validate=True,
    )
    assert payload[:4] == b"RIFF" and payload[8:12] == b"WEBP", prefix
    assert len(payload) > 4_000, prefix
    return payload


tooltip_source = decode_transport("third_justice_tooltip.webp.b64.part")
field_source = decode_transport("third_justice_field.webp.b64.part")
assert len(tooltip_source) > 20_000
assert len(field_source) > 20_000


def assert_rgb_png(name: str) -> None:
    data = (GUIDE_TEXTURES / name).read_bytes()
    assert data.startswith(b"\x89PNG\r\n\x1a\n"), name
    assert data[24] == 8, f"{name}: expected 8-bit PNG, got {data[24]}-bit"
    assert data[25] in (2, 6), f"{name}: expected RGB/RGBA PNG, color type={data[25]}"


for still in (
    "third_justice_tooltip.png",
    "third_justice_field.png",
    "third_justice_reel_01.png",
    "third_justice_reel_02.png",
    "third_justice_reel_03.png",
):
    assert_rgb_png(still)

# Complete supplied ~31 s Third Justice timeline, not the old 3-frame placeholder.
assert EMBEDDED_VIDEO.is_file() and EMBEDDED_VIDEO.stat().st_size > 10_000
assert "third_justice_full.b64" in PREP_TJ
assert "base64.b64decode" in PREP_TJ
assert "MIN_FULL_DURATION_MS = 30_000" in PREP_TJ
assert "third_justice_full.mp4" in PREP_TJ
assert "fps=10" in PREP_TJ or "FPS = 10" in PREP_TJ
assert "frame_%05d.png" in PREP_TJ
assert "mode=full" in PREP_TJ
assert "duration_ms" in PREP_TJ
assert "class SiegeThirdJusticeVideo" in VIDEO
assert "video.full()" in REEL
assert "third_justice_video/frame_" in REEL
assert "VIDEO DE PRUEBA" in REEL
assert "mode=full" in MANIFEST, "release build must contain the complete Third Justice reel"

manifest = dict(
    line.split("=", 1) for line in MANIFEST.splitlines()
    if line.strip() and "=" in line
)
frames = int(manifest.get("frames", "0"))
assert frames >= 300, f"Third Justice release reel is incomplete: only {frames} frames"
assert frames != 3, "Release build must not fall back to the old 3-frame reel"
assert int(manifest.get("duration_ms", "0")) >= 30_000
assert manifest.get("width") == "640" and manifest.get("height") == "360"

assert '"third-justice"' in GUIDE
assert "third_justice_tooltip.png" in GUIDE and "third_justice_field.png" in GUIDE

print("Durable SIEGE 5.30 DVN, full-color Third Justice and complete reel contracts passed")
