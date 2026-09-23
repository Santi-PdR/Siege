#!/usr/bin/env python3
"""Durable SIEGE 5.40 contracts kept across later 5.x releases."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
SCENES = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
FETCH = read("scripts/fetch-dvn-media-510.sh")
AUDIT = read("scripts/audit-backgrounds-530.py")
MEDIA = read("src/main/java/uy/santipdr/siege/client/SiegeMediaReferenceData.java")
MUSIC = read("src/main/java/uy/santipdr/siege/client/SiegeMusic.java")
MOD = read("src/main/java/uy/santipdr/siege/SiegeMod.java")
SOUNDS = read("src/main/resources/assets/siege/sounds.json")
PREP_MUSIC = read("scripts/prepare-music.sh")
GEN_MUSIC = read("scripts/generate-stronghold-signal.py")
KNOWLEDGE = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgePlayer540.java")
REGISTRY = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeRegistry.java")
BRIEFING = read("src/main/java/uy/santipdr/siege/client/SiegeBriefingScreen.java")
PREP_TJ = read("scripts/prepare-third-justice-media.py")

assert "version = '5.40.0'" in BUILD or "version = '5.50.0'" in BUILD

# Six official DVN scenes remain guaranteed at their real native 768x432 size.
for index in range(1, 7):
    scene = f"dvn_official_{index:02d}"
    visual = f"official-gallery-{index:02d}"
    assert f'"{scene}"' in SCENES, scene
    assert f'"{scene}": (768, 432)' in AUDIT, scene
    assert f'"{visual}"' in MEDIA, visual
assert "urls[:6]" in FETCH
assert "need 6" in FETCH
assert "for index in 01 02 03 04 05 06" in FETCH
assert 'rgb.size != (768, 432)' in FETCH
assert "Do not invent detail through upscaling" in FETCH

# Black Signal remains a SIEGE-original deterministic track.
assert "STRONGHOLD_BLACK_SIGNAL" in MOD
assert "SiegeMod.STRONGHOLD_BLACK_SIGNAL" in MUSIC
assert '"stronghold_black_signal"' in MUSIC
assert "Stronghold 5-5 · Black Signal" in MUSIC
assert '"music.stronghold_black_signal"' in SOUNDS
assert "generate-stronghold-signal.py" in PREP_MUSIC
assert 'encode_full "stronghold_black_signal"' in PREP_MUSIC
assert "SEED =" in GEN_MUSIC
assert "DURATION = 132.0" in GEN_MUSIC
assert "RATE = 44_100" in GEN_MUSIC
assert "np.random.default_rng" in GEN_MUSIC
assert "Stronghold 5-5 · Black Signal" in MEDIA
assert '"nucleus-signal"' in MEDIA

# The 5.40 current knowledge override remains active even when newer releases add
# additional media/features around it.
assert "class SiegeKnowledgePlayer540" in KNOWLEDGE
for knowledge_id in (
    "server-overview", "el-nucleo", "stronghold-55", "factions-current-front",
    "warfare-pillars", "threat-classes", "portal-terminology",
):
    assert f'"{knowledge_id}"' in KNOWLEDGE, knowledge_id
assert "SiegeKnowledgePlayer540.entries()" in REGISTRY
assert REGISTRY.find("SiegeKnowledgePlayer510.entries()") < REGISTRY.find("SiegeKnowledgePlayer540.entries()")
for briefing_id in ("el-nucleo", "stronghold-55", "factions-current-front", "warfare-pillars", "threat-classes"):
    assert f'"{briefing_id}"' in BRIEFING, briefing_id

# Do not regress known media safety/quality rules while expanding the release.
assert "TEMPEST_JUTCHERSON" not in SCENES
assert "tempest_jutcherson" not in read("scripts/prepare-backgrounds-hd.py")
assert "restore_static_captures" in PREP_TJ
assert '"-nostdin"' in PREP_TJ
assert "regenerating full-color reel previews" in PREP_TJ

for forbidden in ("mi inventario", "mi personaje", "mi partida actual", "player notebook", "private build"):
    assert forbidden.lower() not in KNOWLEDGE.lower(), forbidden

print("Durable SIEGE 5.40 knowledge, Black Signal and six official DVN scene guarantees passed")
