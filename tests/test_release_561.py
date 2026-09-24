#!/usr/bin/env python3
"""SIEGE 5.61 scene-intelligence release contracts."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
CATALOG = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
BACKGROUNDS = read("src/main/java/uy/santipdr/siege/client/SiegeBackgrounds.java")
MEDIA = read("src/main/java/uy/santipdr/siege/client/SiegeMediaRoomScreen.java")
GALLERY = read("src/main/java/uy/santipdr/siege/client/SiegeSceneScreen.java")
RUNTIME = read("src/main/java/uy/santipdr/siege/client/SiegeRuntimeStatus.java")
BRIEFING = read("src/main/java/uy/santipdr/siege/client/SiegeBriefingScreen.java")
EASTER = read("src/main/java/uy/santipdr/siege/client/SiegeEasterEggVault.java")
WORKFLOW = read(".github/workflows/build.yml")

assert "version = '5.61.0'" in BUILD

# Scene provenance is explicit and shared by every surface instead of inferred from names.
assert "enum Source { SIEGE_ARCHIVE, DVN_OFFICIAL, SIEGE_TREATMENT }" in CATALOG
assert "Source source" in CATALOG
assert "sourceLabel" in CATALOG
for label in ("DVN OFICIAL", "TRATAMIENTO SIEGE", "ARCHIVO SIEGE"):
    assert label in CATALOG
for scene in (
    "dvn_official_01", "dvn_official_02", "dvn_official_03",
    "dvn_official_04", "dvn_official_05", "dvn_official_06",
):
    assert f'dvn("{scene}"' in CATALOG
for scene in ("nucleus_interference", "tesla_breach", "stronghold_red_alert"):
    assert f'generated("{scene}"' in CATALOG

# Rotation exposes current/next state without changing the deterministic schedule.
for symbol in ("public static int nextIndex(long now)", "public static float rotationProgress(long now)",
               "public static String rotationDetail(boolean spanish, long now)",
               "public static String sourceTag(int index, boolean spanish)"):
    assert symbol in BACKGROUNDS, symbol
assert "rotationIndex(Math.floorDiv(now, sceneMs()) + 1L)" in BACKGROUNDS

# Gallery, Media Room and runtime status all use the same authoritative provenance.
assert "SiegeBackgrounds.sourceTag(tile.scene, spanish())" in GALLERY
assert "SiegeBackgrounds.sourceTag(index, spanish())" in GALLERY
assert "SiegeBackgrounds.sourceTag(scene, spanish())" in MEDIA
assert "SiegeBackgrounds.rotationDetail(spanish(), now)" in MEDIA
assert "SiegeBackgrounds.sourceTag(index, spanish)" in RUNTIME
assert "SiegeBackgrounds.nextIndex(now)" in RUNTIME
assert 'case VISUALS -> label("FONDOS", "VISUALS")' in MEDIA

# Player-facing version labels must never freeze at an older 5.x release.
assert '"BRIEFING // " + SiegeRuntimeStatus.version()' in BRIEFING
assert "BRIEFING 5.40" not in BRIEFING

# The normal catalog must still keep the Tempest easter egg isolated.
assert '"tempest_jutcherson"' in EASTER
assert '"tempest_jutcherson"' not in CATALOG

# CI executes this release gate and no later release is allowed to silently remove it.
assert "python3 tests/test_release_561.py" in WORKFLOW

print("SIEGE 5.61 scene provenance, next-scene state, dynamic Briefing version and gallery/media integration passed")
