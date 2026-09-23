#!/usr/bin/env python3
"""SIEGE 5.50 release contracts: tactical visuals, original tracks and real media presets."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
SCENES = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
PREP_BG = read("scripts/prepare-backgrounds-hd.py")
GEN_BG = read("scripts/generate-siege-backgrounds-550.py")
AUDIT = read("scripts/audit-backgrounds-530.py")
MOD = read("src/main/java/uy/santipdr/siege/SiegeMod.java")
MUSIC = read("src/main/java/uy/santipdr/siege/client/SiegeMusic.java")
SOUNDS = read("src/main/resources/assets/siege/sounds.json")
PREP_MUSIC = read("scripts/prepare-music.sh")
GEN_MUSIC = read("scripts/generate-frontline-signal-550.py")
MEDIA = read("src/main/java/uy/santipdr/siege/client/SiegeMediaReferenceData.java")
PRESETS = read("src/main/java/uy/santipdr/siege/client/SiegeMediaPresets.java")
ROOM = read("src/main/java/uy/santipdr/siege/client/SiegeMediaRoomScreen.java")
WORKFLOW = read(".github/workflows/build.yml")

assert "version = '5.50.0'" in BUILD

# Three new visual treatments are built from verified DVN sources and remain native 768x432.
for scene, source in (
    ("nucleus_interference", "dvn_official_02"),
    ("tesla_breach", "dvn_official_05"),
    ("stronghold_red_alert", "dvn_official_01"),
):
    assert f'"{scene}"' in SCENES, scene
    assert f'"{scene}": (768, 432)' in AUDIT, scene
    assert f'save("{scene}"' in GEN_BG, scene
    assert f'load("{source}")' in GEN_BG, source
assert "generate-siege-backgrounds-550.py" in PREP_BG
assert "SIZE = (768, 432)" in GEN_BG
assert "fake new source art" in GEN_BG
assert "Image.blend" in GEN_BG
assert "indexOfId" in SCENES

# Two additional SIEGE-original tracks join Black Signal in the real installed playlist.
for constant in ("NUCLEUS_SILENT_CARRIER", "TESLA_BREACH"):
    assert constant in MOD and f"SiegeMod.{constant}" in MUSIC, constant
for key in ("nucleus_silent_carrier", "tesla_breach"):
    assert f'"{key}"' in MUSIC, key
    assert f'"music.{key}"' in SOUNDS, key
    assert f'encode_full "{key}"' in PREP_MUSIC, key
assert "Nucleus · Silent Carrier" in MUSIC
assert "Tesla Breach" in MUSIC
assert "generate-frontline-signal-550.py" in PREP_MUSIC
assert "duration = 116.0" in GEN_MUSIC
assert "duration = 104.0" in GEN_MUSIC
assert "RATE = 44_100" in GEN_MUSIC
assert "np.random.default_rng" in GEN_MUSIC
assert "selectTrackByName" in MUSIC

# Media presets are functional rather than decorative: each one chooses an installed track
# and a matching generated scene, then the Media Room exposes the controls.
for preset_id, track, scene in (
    ("stronghold", "Stronghold 5-5 · Black Signal", "stronghold_red_alert"),
    ("nucleus", "Nucleus · Silent Carrier", "nucleus_interference"),
    ("tesla", "Tesla Breach", "tesla_breach"),
):
    assert f'"{preset_id}"' in PRESETS
    assert f'"{track}"' in PRESETS
    assert f'"{scene}"' in PRESETS
assert "SiegeSceneCatalog.indexOfId" in PRESETS
assert "SiegeMusic.selectTrackByName" in PRESETS
assert "initMoodControls" in ROOM
assert "SiegeMediaPresets.apply" in ROOM
assert "SALA MULTIMEDIA 5.50" in ROOM

# Current media catalog differentiates official scenes from generated SIEGE treatments.
for visual in ("siege-nucleus-interference", "siege-tesla-breach", "siege-stronghold-red-alert"):
    assert f'"{visual}"' in MEDIA
assert "Nucleus · Silent Carrier" in MEDIA
assert "Tesla Breach" in MEDIA
assert '"tesla-front"' in MEDIA

# Build pipeline must validate the new release and generated tracks.
assert "python3 tests/test_release_550.py" in WORKFLOW
assert "scripts/generate-frontline-signal-550.py" in WORKFLOW
assert "nucleus_silent_carrier.ogg" in WORKFLOW
assert "tesla_breach.ogg" in WORKFLOW

# Existing isolation rule remains non-negotiable.
assert "tempest_jutcherson" not in SCENES
assert "tempest_jutcherson" not in PREP_BG

print("SIEGE 5.50 tactical visuals, two original tracks and real audiovisual presets passed")
