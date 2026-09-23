#!/usr/bin/env python3
"""Durable SIEGE 5.40 knowledge and official-DVN contracts for later 5.x releases."""
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
SCENES = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
FETCH = read("scripts/fetch-dvn-media-510.sh")
AUDIT = read("scripts/audit-backgrounds-530.py")
MEDIA = read("src/main/java/uy/santipdr/siege/client/SiegeMediaReferenceData.java")
KNOWLEDGE = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgePlayer540.java")
REGISTRY = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeRegistry.java")
BRIEFING = read("src/main/java/uy/santipdr/siege/client/SiegeBriefingScreen.java")
PREP_TJ = read("scripts/prepare-third-justice-media.py")

match = re.search(r"version\s*=\s*'([0-9]+)\.([0-9]+)\.([0-9]+)'", BUILD)
assert match
major, minor, patch = map(int, match.groups())
assert (major, minor) >= (5, 40)

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

# The 5.40 current knowledge override remains active even as later releases change
# audiovisual material. A historical experiment in music must never be a permanent
# requirement if the player rejects it.
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

# Known visual/media safety rules remain mandatory.
assert "TEMPEST_JUTCHERSON" not in SCENES
assert "tempest_jutcherson" not in read("scripts/prepare-backgrounds-hd.py")
assert "restore_static_captures" in PREP_TJ
assert '"-nostdin"' in PREP_TJ
assert "regenerating full-color reel previews" in PREP_TJ

for forbidden in ("mi inventario", "mi personaje", "mi partida actual", "player notebook", "private build"):
    assert forbidden.lower() not in KNOWLEDGE.lower(), forbidden

print("Durable SIEGE 5.40 knowledge, six official DVN scenes and media-safety guarantees passed")
