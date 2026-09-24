#!/usr/bin/env python3
"""Durable SIEGE 5.60 release contracts kept across later 5.x releases.

5.60 removes the rejected generated music, accepts exactly two player-approved new
songs when legitimate source masters are supplied, and makes adaptive presentation
settings real player-facing runtime controls.
"""
from pathlib import Path
import re

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
SETTINGS = read("src/main/java/uy/santipdr/siege/client/SiegeSettingsScreen.java")
MEDIA_ROOM = read("src/main/java/uy/santipdr/siege/client/SiegeMediaRoomScreen.java")
RUNTIME = read("src/main/java/uy/santipdr/siege/client/SiegeRuntimeStatus.java")
DIAGNOSTIC = read("src/main/java/uy/santipdr/siege/client/SiegeDiagnosticReport.java")
TITLE = read("src/main/java/uy/santipdr/siege/client/SiegeTitleScreen.java")
SCENE_SCREEN = read("src/main/java/uy/santipdr/siege/client/SiegeSceneScreen.java")
APPROVED = read("docs/MUSIC-CANDIDATES-5.60.md")
WORKFLOW = read(".github/workflows/build.yml")

version = re.search(r"version = '5\.(\d+)\.0'", BUILD)
assert version and int(version.group(1)) >= 60

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

# Exactly two NEW music choices were approved by the player.
approved = (
    ("A_STRANGER_I_REMAIN", "a_stranger_i_remain", "A Stranger I Remain (Maniac Agenda Mix)"),
    ("RECEIVE_YOU_HYPERACTIVE", "receive_you_the_hyperactive", "Receive You The Hyperactive"),
)
for constant, key, title in approved:
    assert constant in MOD
    assert f"SiegeMod.{constant}" in MUSIC
    assert f'"music.{key}"' in SOUNDS
    assert key in PREP
    assert title in MUSIC
    assert title in APPROVED

# Optional commercial tracks must never become broken/silent entries. They are visible
# only when a prepared OGG exists in the built resources. A stale pin to an optional
# track from another build must self-heal to shuffle.
assert "hasPreparedAudio" in MUSIC
assert '.filter(track -> track.required() || hasPreparedAudio(track.key()))' in MUSIC
assert "private static void sanitizeSelection()" in MUSIC
assert "SiegeConfig.selectedTrack >= TRACKS.size()" in MUSIC
assert "SiegeConfig.selectedTrack = -1" in MUSIC
assert "public static boolean shuffleEnabled()" in MUSIC
assert "public static int pinnedTrackNumber()" in MUSIC
assert "public static long currentElapsedMs()" in MUSIC
assert "public static String currentTimeLabel()" in MUSIC
assert "source master not supplied yet" in PREP
assert "no descarga ni ripea audio" in APPROVED

# The rejected alternatives from the preview round must remain outside the integration.
for title in (
    "A Cup of Liber-Tea", "The Automaton Legion", "Legionnaire", "Catalyst",
    "Monomyth – The Encounter", "Simulacra", "Venom",
):
    assert title not in MUSIC
    assert title in APPROVED

# Existing five approved tracks stay intact.
for title in (
    "Tale of a Cruel World", "Darkest of Days", "Kaptain Music Box",
    "Heaven's Hell-Sent Gift", "Arc - Enemy · Potoe",
):
    assert title in MUSIC, title
for title in ("Stronghold 5-5 · Black Signal", "Nucleus · Silent Carrier", "Tesla Breach"):
    assert title not in MUSIC, title

# Scene presets remain useful but must not silently force music.
assert "change only the scene" in PRESETS.lower()
assert "SiegeMusic.selectTrack" not in PRESETS
assert "String track" not in PRESETS
for scene in ("stronghold_red_alert", "nucleus_interference", "tesla_breach"):
    assert f'"{scene}"' in PRESETS

# 5.60 background controls are persisted, consumed by the renderer and reachable from
# the actual Settings UI. A config field that cannot be changed in-game is not complete.
for option in ("backgroundMotionIntensity", "backgroundSceneSeconds", "backgroundCrossfadeSeconds"):
    assert option in CONFIG
    assert f"SiegeConfig.{option}" in BACKGROUNDS
    assert f"SiegeConfig.{option}" in SETTINGS
for label in ("BACKGROUND MOTION", "SCENE DURATION", "SCENE CROSSFADE"):
    assert label in SETTINGS
assert "private static long sceneMs()" in BACKGROUNDS
assert "private static long crossfadeMs()" in BACKGROUNDS
assert "fadeDuration > 0L" in BACKGROUNDS, "zero-crossfade mode must avoid division by zero"
assert "overscan" in BACKGROUNDS and "backgroundMotionIntensity / 100.0D" in BACKGROUNDS
assert "SiegeConfig.reducedMotion" in BACKGROUNDS
assert "SiegeConfig.reduceFlashes" in BACKGROUNDS
assert "SiegeConfig.Graphics.PERFORMANCE" in BACKGROUNDS
assert "Math.max(w / (double)sourceW, h / (double)sourceH)" in BACKGROUNDS
assert '" // SIEGE " + SiegeRuntimeStatus.version()' in SETTINGS
assert "SIEGE 1.25" not in SETTINGS

# Gallery "contrast" is a real menu preview: it must use scene bias + auto/high
# contrast through the same effective-darkness path used by the main renderer.
assert "SiegeBackgrounds.effectiveBackgroundDarkness(index)" in SCENE_SCREEN
assert "SiegeBackgrounds.panelFraction" in SCENE_SCREEN
assert "SiegeBackgrounds.sceneTag(tile.scene, spanish())" in SCENE_SCREEN
assert "SiegeBackgrounds.sceneTag(index, spanish())" in SCENE_SCREEN

# The title interference slider now controls the actual effect rather than being a dead
# setting. Accessibility still has hard priority over any visual signal effect.
assert "SiegeConfig.interferenceIntensity" in TITLE
assert "interference > 0" in TITLE
assert "58 - Math.round(interference * 45.0F / 100.0F)" in TITLE
assert "1 + Math.round(interference * 3.0F / 100.0F)" in TITLE
assert "SiegeConfig.reducedMotion" in TITLE
assert "SiegeConfig.reduceFlashes" in TITLE

# Reduce Flashes is a successful runtime override, not an error condition. Diagnostics
# must report suppression truthfully and never lower readiness merely because a guarded
# decorative effect remains configured underneath the safeguard.
assert "INTERFERENCIA SUPRIMIDA POR ACCESIBILIDAD" in DIAGNOSTIC
assert "INTERFERENCE SUPPRESSED BY ACCESSIBILITY" in DIAGNOSTIC
assert "boolean interferenceConfigured" in DIAGNOSTIC
assert "SiegeConfig.reduceFlashes && interferenceConfigured" in DIAGNOSTIC
assert "Severity.OK, Recovery.NONE" in DIAGNOSTIC
assert "Reducción de destellos y la interferencia están en conflicto" not in DIAGNOSTIC

# Media Room exposes shuffle/pin state directly, a measured playback clock and the real
# runtime version without claiming optional masters are already installed.
assert "togglePlaybackMode" in MEDIA_ROOM
assert "PIN CURRENT TRACK" in MEDIA_ROOM
assert "RETURN TO SHUFFLE" in MEDIA_ROOM
assert "SiegeMusic.selectTrack(-1)" in MEDIA_ROOM
assert "SiegeMusic.shuffleEnabled()" in MEDIA_ROOM
assert "SiegeMusic.currentTimeLabel()" in MEDIA_ROOM
assert 'label("SALA MULTIMEDIA", "MEDIA ROOM") + " // " + SiegeRuntimeStatus.version()' in MEDIA_ROOM
assert "prepared master exists" in MEDIA_ROOM
assert "MEDIA ROOM 5.60" not in MEDIA_ROOM

# Command/diagnostic status must describe effective runtime state rather than only raw
# config values: sanitized pin/shuffle audio and motion overrides are visible.
assert "SiegeMusic.shuffleEnabled()" in RUNTIME
assert "SiegeMusic.pinnedTrackNumber()" in RUNTIME
assert 'spanish ? "ALEATORIO" : "SHUFFLE"' in RUNTIME
assert 'spanish ? "FIJA " : "PINNED "' in RUNTIME
assert "motionSuppressed" in RUNTIME
assert "SiegeConfig.reducedMotion || SiegeConfig.reduceFlashes" in RUNTIME
assert "SiegeConfig.Graphics.PERFORMANCE" in RUNTIME
assert 'spanish ? "MOV OFF" : "MOTION OFF"' in RUNTIME

# CI itself must execute the 5.60 contract, avoid deleted generators and run on the
# current Node-24-compatible major releases instead of deprecated v4 action majors.
assert "python3 tests/test_release_560.py" in WORKFLOW
assert "generate-stronghold-signal.py" not in WORKFLOW
assert "generate-frontline-signal-550.py" not in WORKFLOW
for action in (
    "actions/checkout@v7",
    "actions/setup-java@v6",
    "gradle/actions/setup-gradle@v6",
    "actions/cache@v6",
    "actions/upload-artifact@v7",
):
    assert action in WORKFLOW, action
for old in (
    "actions/checkout@v4",
    "actions/setup-java@v4",
    "gradle/actions/setup-gradle@v4",
    "actions/cache@v4",
    "actions/upload-artifact@v4",
):
    assert old not in WORKFLOW, old

print("SIEGE 5.60 durable approved-music, adaptive-presentation and diagnostics contracts passed")
