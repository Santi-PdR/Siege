#!/usr/bin/env python3
"""SIEGE 5.60 release contracts.

5.60 removes the rejected synthetic/interference-like tracks and makes the new
background timing/motion settings real runtime controls. Replacement music is never
added by this release gate: every candidate must be previewed and approved first.
"""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
MOD = read("src/main/java/uy/santipdr/siege/SiegeMod.java")
MUSIC = read("src/main/java/uy/santipdr/siege/client/SiegeMusic.java")
SOUNDS = read("src/main/resources/assets/siege/sounds.json")
PREP = read("scripts/prepare-music.sh")
PRESETS = read("src/main/java/uy/santipdr/siege/client/SiegeMediaPresets.java")
CONFIG = read("src/main/java/uy/santipdr/siege/client/SiegeConfig.java")
BACKGROUNDS = read("src/main/java/uy/santipdr/siege/client/SiegeBackgrounds.java")
CANDIDATES = read("docs/MUSIC-CANDIDATES-5.60.md")

assert "version = '5.60.0'" in BUILD

# Rejected 5.40/5.50 generated audio must not be active, registered or rebuilt.
rejected_constants = ("STRONGHOLD_BLACK_SIGNAL", "NUCLEUS_SILENT_CARRIER", "TESLA_BREACH")
rejected_keys = ("stronghold_black_signal", "nucleus_silent_carrier", "tesla_breach")
for value in rejected_constants:
    assert value not in MOD, f"rejected sound event returned: {value}"
    assert f"SiegeMod.{value}" not in MUSIC, f"rejected track returned to playlist: {value}"
for value in rejected_keys:
    assert f'"music.{value}"' not in SOUNDS, f"rejected sounds.json entry returned: {value}"
    assert value not in PREP, f"rejected track returned to music preparation: {value}"

assert not (ROOT / "scripts/generate-stronghold-signal.py").exists()
assert not (ROOT / "scripts/generate-frontline-signal-550.py").exists()
assert "Only tracks explicitly approved" in MUSIC

# Exactly the five approved tracks remain in the current player-facing rotation.
for title in (
    "Tale of a Cruel World", "Darkest of Days", "Kaptain Music Box",
    "Heaven's Hell-Sent Gift", "Arc - Enemy · Potoe",
):
    assert title in MUSIC, title
for title in ("Stronghold 5-5 · Black Signal", "Nucleus · Silent Carrier", "Tesla Breach"):
    assert title not in MUSIC, title

# Scene presets remain useful while music approval is pending, but must not silently
# select a track behind the player's back.
assert "scene-only" in PRESETS.lower()
assert "SiegeMusic.selectTrack" not in PRESETS
assert "String track" not in PRESETS
for scene in ("stronghold_red_alert", "nucleus_interference", "tesla_breach"):
    assert f'"{scene}"' in PRESETS

# 5.60 background controls are persisted and actually consumed by the renderer.
for option in ("backgroundMotionIntensity", "backgroundSceneSeconds", "backgroundCrossfadeSeconds"):
    assert option in CONFIG
    assert f"SiegeConfig.{option}" in BACKGROUNDS
assert "private static long sceneMs()" in BACKGROUNDS
assert "private static long crossfadeMs()" in BACKGROUNDS
assert "fadeDuration > 0L" in BACKGROUNDS, "zero-crossfade mode must avoid division by zero"
assert "overscan" in BACKGROUNDS and "backgroundMotionIntensity / 100.0D" in BACKGROUNDS
assert "SiegeConfig.reducedMotion" in BACKGROUNDS
assert "SiegeConfig.reduceFlashes" in BACKGROUNDS
assert "SiegeConfig.Graphics.PERFORMANCE" in BACKGROUNDS
assert "Math.max(w / (double)sourceW, h / (double)sourceH)" in BACKGROUNDS

# Candidate policy is explicit: real music may be researched, but nothing gets bundled
# before the player has listened and approved it.
assert "Ningún candidato de esta lista está instalado todavía" in CANDIDATES
assert "CC BY 4.0" in CANDIDATES
assert "aprobación explícita" in CANDIDATES

print("SIEGE 5.60 rejected-music cleanup, approval gate and adaptive backgrounds passed")
