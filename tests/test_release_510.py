#!/usr/bin/env python3
"""Durable SIEGE 5.10 player-language and DVN media contracts."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
WORKFLOW = read(".github/workflows/build.yml")
REG = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeRegistry.java")
PLAYER = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgePlayer510.java")
RACES = read("src/main/java/uy/santipdr/siege/client/SiegeRaceAtlasData.java")
SCENES = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
MEDIA = read("src/main/java/uy/santipdr/siege/client/SiegeMediaReferenceData.java")
MUSIC = read("src/main/java/uy/santipdr/siege/client/SiegeMusic.java")
MOD = read("src/main/java/uy/santipdr/siege/SiegeMod.java")
SOUNDS = read("src/main/resources/assets/siege/sounds.json")
FETCH = read("scripts/fetch-dvn-media-510.sh")
PREP = read("scripts/prepare-music.sh")
LICENSE = read("src/main/resources/assets/siege/licenses/arc_enemy.txt")

match = re.search(r"version\s*=\s*'([0-9]+)\.([0-9]+)\.([0-9]+)'", BUILD)
assert match
major, minor, patch = map(int, match.groups())
assert (major, minor) >= (5, 10), "5.10 contracts require SIEGE 5.10+"

# A final player-language layer must override old research-oriented records.
assert "class SiegeKnowledgePlayer510" in PLAYER
assert "SiegeKnowledgePlayer510.entries()" in REG
assert REG.index("SiegeKnowledgeCorpus50.entries()") < REG.index("SiegeKnowledgePlayer510.entries()")
assert '"raid-area-discipline"' in REG
assert "cleanForPlayer" in REG
assert "entry.related(), List.of()" in REG

# Important rewritten/current topics live in the clean layer.
for entry in (
    "server-overview", "rarity-order", "race-catalog", "race-spins", "race-human",
    "race-shark", "race-saiyan", "race-xeno-saiyan", "race-cyborg", "race-ghoul",
    "race-subhuman", "progression-v1-v4", "trials-basics", "trial-third-justice",
    "executors-basics", "meditation-levels", "ability-stamina", "ability-room",
    "ability-gate", "death-revive-current", "item-defibrillator", "item-medkit",
    "respawn-cards", "assembling-table", "item-daemonium-kit", "dimensions-basics",
    "raids-basics",
):
    assert f'"{entry}"' in PLAYER, entry

assert "3 bloques de hierro + 1 bloque de oro" in PLAYER
assert "3 bloques de hierro + 1 mesa de encantamientos" in PLAYER
assert "20 discos de Nightdream" in PLAYER
assert "Adamantium Human" in PLAYER and "Rick Sanchez" in PLAYER

# Recovered race catalog gaps are represented without inventing full mechanics.
assert 'known("xeno-saiyan", "Xeno Saiyan"' in RACES
assert 'known("mink", "Mink"' in RACES
assert 'known("angel", "Angel"' in RACES

# Real searched DVN media stays native rather than generated/fake-HD.
for scene in ("dvn_official_01", "dvn_official_02"):
    assert f'"{scene}"' in SCENES
assert "DVN_W = 768" in SCENES and "DVN_H = 432" in SCENES
assert "fake-upscaled" in SCENES
assert "thumbnails.roblox.com/v1/games/multiget/thumbnails" in FETCH
assert "DVN_UNIVERSE_ID='3293525400'" in FETCH
assert "dvn_official_01.png" in FETCH
assert "dvn_official_02.png" in FETCH
assert "Keep the official source at native size" in FETCH

# Arc - Enemy remains a verified noncommercial DVN addition with attribution in the jar.
assert "soundcloud.com/potoe-50708490/arc-enemy" in FETCH
assert "arc_enemy" in PREP
assert "ARC_ENEMY" in MOD
assert '"music.arc_enemy"' in SOUNDS
assert "SiegeMod.ARC_ENEMY" in MUSIC
assert '"Arc - Enemy · Potoe"' in MUSIC
assert "CC BY-NC-SA" in LICENSE and "Potoe" in LICENSE
assert 'new Track("Arc - Enemy"' in MEDIA
assert 'new Visual("official-gallery-01"' in MEDIA
assert 'new Visual("official-gallery-02"' in MEDIA

# CI must still fetch and validate the DVN media before packaging.
assert "fetch-dvn-media-510.sh" in WORKFLOW
assert "test_release_510.py" in WORKFLOW
assert "SiegeKnowledgePlayer510.java" in WORKFLOW
assert "dvn_official_01" in WORKFLOW and "dvn_official_02" in WORKFLOW

print("Durable SIEGE 5.10 player language, race coverage and DVN media contracts passed")
