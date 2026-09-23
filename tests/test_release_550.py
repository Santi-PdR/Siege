#!/usr/bin/env python3
"""Durable SIEGE 5.50 contracts retained by later 5.x releases.

5.50 introduced three useful tactical background treatments. Its experimental generated
music was later rejected by the player, so later releases must preserve the visual work
without being forced to keep unwanted audio.
"""
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
SCENES = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
PREP_BG = read("scripts/prepare-backgrounds-hd.py")
GEN_BG = read("scripts/generate-siege-backgrounds-550.py")
AUDIT = read("scripts/audit-backgrounds-530.py")
PRESETS = read("src/main/java/uy/santipdr/siege/client/SiegeMediaPresets.java")

match = re.search(r"version\s*=\s*'([0-9]+)\.([0-9]+)\.([0-9]+)'", BUILD)
assert match
major, minor, patch = map(int, match.groups())
assert (major, minor) >= (5, 50)

# Three SIEGE visual treatments remain generated from verified DVN sources and stay
# on the real native 768x432 canvas instead of becoming fake-HD assets.
for scene, source in (
    ("nucleus_interference", "dvn_official_02"),
    ("tesla_breach", "dvn_official_05"),
    ("stronghold_red_alert", "dvn_official_01"),
):
    assert f'"{scene}"' in SCENES, scene
    assert f'"{scene}": (768, 432)' in AUDIT, scene
    assert f'save("{scene}"' in GEN_BG, scene
    assert f'load("{source}")' in GEN_BG, source
    assert f'"{scene}"' in PRESETS, scene

assert "generate-siege-backgrounds-550.py" in PREP_BG
assert "SIZE = (768, 432)" in GEN_BG
assert "fake new source art" in GEN_BG
assert "Image.blend" in GEN_BG
assert "indexOfId" in SCENES

# Later releases are allowed to change what a preset does with music, but the three
# scene identities introduced in 5.50 remain functional and selectable.
for preset_id in ("stronghold", "nucleus", "tesla"):
    assert f'"{preset_id}"' in PRESETS
assert "SiegeSceneCatalog.indexOfId" in PRESETS

# Existing visual isolation rule remains non-negotiable.
assert "tempest_jutcherson" not in SCENES
assert "tempest_jutcherson" not in PREP_BG

print("Durable SIEGE 5.50 tactical-background and preset-scene contracts passed")
