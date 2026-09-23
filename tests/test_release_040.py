#!/usr/bin/env python3
"""Durable SIEGE 4.00 contracts that must survive later 5.x redesigns."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
WORKFLOW = read(".github/workflows/build.yml")
OPS = read("src/main/java/uy/santipdr/siege/client/SiegeOperationsIndex.java")
HUB = read("src/main/java/uy/santipdr/siege/client/SiegeOperationsHubScreen.java")
REG = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeRegistry.java")
KNOWLEDGE_SCREEN = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeScreen.java")
ATLAS = read("src/main/java/uy/santipdr/siege/client/SiegeAtlasScreen.java")
BRIEF = read("src/main/java/uy/santipdr/siege/client/SiegeBriefingScreen.java")
DETAIL = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeFileScreen.java")
RACES = read("src/main/java/uy/santipdr/siege/client/SiegeRaceAtlasData.java")
RACE_SCREEN = read("src/main/java/uy/santipdr/siege/client/SiegeRaceAtlasScreen.java")
PROGRESSION = read("src/main/java/uy/santipdr/siege/client/SiegeProgressionData.java")
PROGRESSION_SCREEN = read("src/main/java/uy/santipdr/siege/client/SiegeProgressionMapScreen.java")
MEDIA_DATA = read("src/main/java/uy/santipdr/siege/client/SiegeMediaReferenceData.java")
MEDIA_ROOM = read("src/main/java/uy/santipdr/siege/client/SiegeMediaRoomScreen.java")
PROFILES = read("src/main/java/uy/santipdr/siege/client/SiegeClientProfile.java")
PROFILE_SPEC = read("src/main/java/uy/santipdr/siege/client/SiegeProfileSpec.java")
MENU_EVENTS = read("src/main/java/uy/santipdr/siege/client/Siege250MenuEvents.java")
TITLE = read("src/main/java/uy/santipdr/siege/client/SiegeTitleScreen.java")
MULTI = read("src/main/java/uy/santipdr/siege/client/SiegeMultiplayerScreen.java")
INTEL = read("src/main/java/uy/santipdr/siege/client/IntelScreenV3.java")
SCENES = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
EASTER = read("src/main/java/uy/santipdr/siege/client/SiegeEasterEggVault.java")

match = re.search(r"version\s*=\s*'([0-9]+)\.([0-9]+)\.([0-9]+)'", BUILD)
assert match, "Missing semantic version in build.gradle"
major, minor, patch = map(int, match.groups())
assert major >= 5, "Durable 4.00 contracts are only expected on SIEGE 5.x+ releases"

for cls in ("SiegeAtlasScreen", "SiegeBriefingScreen", "SiegeThreatBoardScreen", "SiegeKnowledgeFileScreen",
            "SiegeRaceAtlasScreen", "SiegeProgressionMapScreen", "SiegeMediaRoomScreen"):
    assert f"class {cls}" in read(f"src/main/java/uy/santipdr/siege/client/{cls}.java")

# The 4.00 information surfaces still exist, but later 5.x versions may simplify navigation and wording.
assert "SiegeKnowledgeExpansion40.entries" in REG
assert "SiegeKnowledgeExpansion50.entries" in REG
assert "SiegeKnowledgeRegistry.entries" in OPS
for constructor in ("new SiegeBriefingScreen(this)", "new SiegeAtlasScreen(this)", "new SiegeRaceAtlasScreen(this)",
                    "new SiegeProgressionMapScreen(this)", "new SiegeThreatBoardScreen(this)", "new SiegeMediaRoomScreen(this)"):
    assert constructor in HUB, f"Missing Operations route: {constructor}"
assert "new SiegeKnowledgeFileScreen" in HUB
assert "SiegeKnowledgeRegistry.search" in KNOWLEDGE_SCREEN

# Technical provenance remains out of normal player surfaces.
for technical in ("sourceLine(", "selected.sources()", 'label("REFERENCIA"', 'label("FUENTES"', "TAMBIÉN VER"):
    assert technical not in KNOWLEDGE_SCREEN
for technical in ("sourceLine(", "entry.sources()", "SERVER FILE · NO PLAYER PROFILE DATA", "TAMBIÉN VER"):
    assert technical not in DETAIL
assert "TAMBIÉN VER" not in ATLAS

# Race Atlas still keeps the known rarity ladder and per-race lookup.
for race_id in ("human", "hacker", "shark", "saiyan", "deteriorer", "pharaoh", "apotheosis", "death",
                "cyborg", "ghoul", "subhuman", "terrarian", "kaioshin", "dragon", "shinigami", "majin", "undertale-au"):
    assert f'"{race_id}"' in RACES
for rarity in ("COMMON", "UNCOMMON", "RARE", "ULTRA_RARE", "LEGENDARY", "OBSAINAN", "MYTHIC", "GODLY", "ETERNAL", "FABLED"):
    assert rarity in RACES
assert "UNKNOWN" in RACES and "HIDDEN" in RACES
assert "SiegeKnowledgeRegistry.get" in RACE_SCREEN
assert "rarityRows(" in RACE_SCREEN

# Progression is still conceptual and race-dependent rather than one fabricated universal recipe.
for track in ('"core"', '"v1v4"', '"special"', '"trials"'):
    assert track in PROGRESSION
assert "V1 → V4" in PROGRESSION
assert "DEPENDE DE LA RAZA" in PROGRESSION
assert "SiegeKnowledgeRegistry.get" in PROGRESSION_SCREEN
assert "trackButtonLabel(" in PROGRESSION_SCREEN

# Media, profiles and accessibility survive later redesigns.
for title in ("Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir"):
    assert title in MEDIA_DATA
assert "SiegeMusic.previousTrack" in MEDIA_ROOM and "SiegeMusic.nextTrack" in MEDIA_ROOM
assert "SiegeBackgrounds.rotationState" in MEDIA_ROOM
for profile in ("CINEMATIC", "TACTICAL", "PERFORMANCE", "CALM", "READING", "CLASSIC", "HIGH_CONTRAST", "IMMERSIVE"):
    assert profile in PROFILES and profile in PROFILE_SPEC

# Main-menu and server contracts remain intact.
assert '"siege.menu.deployment"' in MENU_EVENTS
assert "new SiegeBriefingScreen(screen)" in MENU_EVENTS
assert "new SiegeOperationsHubScreen(screen)" in MENU_EVENTS
assert "SiegeLacontinuacion.exaroton.me:18736" in MULTI
assert "GLFW_KEY_S && Screen.hasControlDown()" in TITLE
for forbidden in ("FAVORITES", "toggleFavoriteIntel", "favoriteButton", "addIndexButton", "copyText", "GUARDAR"):
    assert forbidden not in INTEL
assert '"tempest_jutcherson"' in EASTER
assert '"tempest_jutcherson"' not in SCENES

# Privacy remains a release contract.
PUBLIC_DATA = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeExpansion50.java") + RACES + PROGRESSION
for forbidden in ("mi inventario", "mi personaje", "mi partida actual", "player notebook", "private build"):
    assert forbidden.lower() not in PUBLIC_DATA.lower()

for test_name in ("AtlasRegressionTest", "RaceAtlasRegressionTest", "ProgressionMapRegressionTest", "MediaReferenceRegressionTest"):
    assert test_name in WORKFLOW
assert "Publish validated jar for installer" in WORKFLOW
print(f"Durable SIEGE 4.00 gameplay/UI contracts preserved under {major}.{minor}.{patch}")
