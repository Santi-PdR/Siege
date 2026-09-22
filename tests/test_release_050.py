#!/usr/bin/env python3
"""SIEGE 5.00 major-jump contracts introduced after the completed 4.00 release."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
WORKFLOW = read(".github/workflows/build.yml")
MEDIA_DATA = read("src/main/java/uy/santipdr/siege/client/SiegeMediaReferenceData.java")
MEDIA_ROOM = read("src/main/java/uy/santipdr/siege/client/SiegeMediaRoomScreen.java")
PROFILES = read("src/main/java/uy/santipdr/siege/client/SiegeClientProfile.java")
PROFILE_SPEC = read("src/main/java/uy/santipdr/siege/client/SiegeProfileSpec.java")
TITLE = read("src/main/java/uy/santipdr/siege/client/SiegeTitleScreen.java")
MULTI = read("src/main/java/uy/santipdr/siege/client/SiegeMultiplayerScreen.java")
SCENES = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
EASTER = read("src/main/java/uy/santipdr/siege/client/SiegeEasterEggVault.java")
CHANGELOG = read("docs/CHANGELOG-5.0.0.md")
MEDIA_DOC = read("docs/DVN-MEDIA-CANDIDATES-5.0.md")

assert "version = '5.00.0'" in BUILD
assert "SIEGE 5.00.0" in CHANGELOG
assert "Command Network" in CHANGELOG

# Media Room 5.0 is a real surface, not only a longer list of references.
assert "Mode { BUNDLED, MOODS, DVN_AUDIO, VISUALS }" in MEDIA_ROOM
assert "renderMoods" in MEDIA_ROOM
assert "mouseScrolled" in MEDIA_ROOM
assert "renderScrollState" in MEDIA_ROOM
assert "SiegeMediaReferenceData.moods()" in MEDIA_ROOM
for mood in ('"stronghold"', '"deployment"', '"intel"', '"last-stand"'):
    assert mood in MEDIA_DATA
for visual in ('"stronghold-defense"', '"arctic-standoff"', '"urban-night"', '"city-siege"',
               '"dune-front"', '"industrial-zone"', '"hangar-briefing"', '"wave-defense"',
               '"boss-assault"', '"armory-deployment"'):
    assert visual in MEDIA_DATA
for track in ("Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir"):
    assert track in MEDIA_DATA

# External references remain references. The quality gate explicitly rejects fake HD promotion.
assert "768×432" in MEDIA_DOC
assert "no se hará upscale barato" in MEDIA_DOC.lower()
assert "1920×1080" in MEDIA_DOC
assert "Tempest Jutcherson queda fuera siempre" in MEDIA_DOC

# 5.00 adds an identity preset while preserving accessibility instead of adding more flashing effects.
assert "STRONGHOLD" in PROFILES
assert "case STRONGHOLD" in PROFILE_SPEC
assert "SiegeClientProfile.Profile.STRONGHOLD" in PROFILE_SPEC
assert "false, true, false, true, true, true, true, false, true" in PROFILE_SPEC
assert "38, 0, 34, 78" in PROFILE_SPEC

# Major-release invariants remain intact.
assert "SiegeLacontinuacion.exaroton.me:18736" in MULTI
assert "GLFW_KEY_S && Screen.hasControlDown()" in TITLE
assert '"tempest_jutcherson"' in EASTER
assert '"tempest_jutcherson"' not in SCENES
assert "Publish validated jar for installer" in WORKFLOW
assert "version = '5.00.0'" in WORKFLOW
assert "python3 tests/test_release_040.py" in WORKFLOW
assert "python3 tests/test_release_050.py" in WORKFLOW

print("SIEGE 5.00 foundation contracts passed")
