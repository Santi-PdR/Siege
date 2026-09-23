#!/usr/bin/env python3
"""SIEGE 5.30 visual-quality, DVN-background and Third Justice contracts."""
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

assert "version = '5.30.0'" in BUILD

# Three real official Roblox DVN thumbnails, always native 16:9.
for scene in ("dvn_official_01", "dvn_official_02", "dvn_official_03"):
    assert f'"{scene}"' in SCENES, scene
assert "urls[:3]" in FETCH
assert "rgb.size != (768, 432)" in FETCH
assert "Keep the official source at native size" in FETCH
assert "need 3" in FETCH
assert 'for index in 01 02 03' in FETCH
assert 'dvn_official_${index}.png' in FETCH
for visual in ("official-gallery-01", "official-gallery-02", "official-gallery-03"):
    assert f'"{visual}"' in MEDIA, visual

# 5.30 removes the old fake-Full-HD background contract.
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

# Third Justice images get a real visibility repair, not another dark overlay.
assert "remap_visible" in PREP_TJ
assert "still too dark after repair" in PREP_TJ
assert "third_justice_field.png" in PREP_TJ and "third_justice_tooltip.png" in PREP_TJ
assert "No darkness/filter overlay is composited over evidence" in REEL
assert "SiegeBackgrounds.render(g, width, height" not in REEL
assert "0xE20A0C0F" not in REEL

# 5.30 ships the complete supplied ~31 s Third Justice test, not the old 3-frame
# placeholder. CI has already run the preparation script before this contract test.
assert EMBEDDED_VIDEO.is_file() and EMBEDDED_VIDEO.stat().st_size > 20_000
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
assert "mode=full" in MANIFEST, "5.30 release build must contain the complete Third Justice reel"
assert "frames=3" not in MANIFEST, "5.30 release build must not fall back to the old 3-frame reel"

manifest = dict(
    line.split("=", 1) for line in MANIFEST.splitlines()
    if line.strip() and "=" in line
)
assert int(manifest.get("frames", "0")) >= 300
assert int(manifest.get("duration_ms", "0")) >= 30_000
assert manifest.get("width") == "640" and manifest.get("height") == "360"

# Armory still owns the item and its visible evidence.
assert '"third-justice"' in GUIDE
assert "third_justice_tooltip.png" in GUIDE and "third_justice_field.png" in GUIDE

print("SIEGE 5.30 DVN backgrounds, cover scaling, night visibility and complete Third Justice reel passed")
