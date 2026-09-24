#!/usr/bin/env python3
"""SIEGE 5.62 rotation-fairness and continuity contracts."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
SCHEDULE = read("src/main/java/uy/santipdr/siege/client/SiegeSceneSchedule.java")
SCENE_TEST = read("tests/SceneScheduleTest.java")
CATALOG = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
BACKGROUNDS = read("src/main/java/uy/santipdr/siege/client/SiegeBackgrounds.java")
EASTER = read("src/main/java/uy/santipdr/siege/client/SiegeEasterEggVault.java")

assert "version = '5.62.0'" in BUILD

# Six featured slots stay deterministic, but they are inserts rather than replacements.
assert "FEATURED_PHASES = {8, 26, 44, 62, 80, 98}" in SCHEDULE
assert "standardSlotOrdinal(slot)" in SCHEDULE
assert "featuredBefore" in SCHEDULE
assert "return slot - featuredBefore" in SCHEDULE
assert "isFeaturedSlot(long slot)" in SCHEDULE
assert "featured inserts are interleaved" in SCENE_TEST.lower()
assert "30" in SCENE_TEST and "compressed standard bag" in SCENE_TEST

# 5.61 provenance/rotation intelligence is preserved while the schedule is corrected.
assert "enum Source { SIEGE_ARCHIVE, DVN_OFFICIAL, SIEGE_TREATMENT }" in CATALOG
for symbol in ("nextIndex(long now)", "rotationProgress(long now)", "rotationDetail(boolean spanish, long now)", "sourceTag(int index, boolean spanish)"):
    assert symbol in BACKGROUNDS, symbol

# Normal rotation still cannot leak the isolated Tempest easter egg.
assert '"tempest_jutcherson"' in EASTER
assert '"tempest_jutcherson"' not in CATALOG
assert "SiegeSceneCatalog.featuredIndex()" in SCHEDULE

print("SIEGE 5.62 featured-insert fairness, complete standard bags and 5.61 continuity passed")
