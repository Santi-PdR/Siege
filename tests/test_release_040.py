#!/usr/bin/env python3
"""SIEGE 4.00 durable release contracts."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
WORKFLOW = read(".github/workflows/build.yml")
OPS = read("src/main/java/uy/santipdr/siege/client/SiegeOperationsIndex.java")
HUB = read("src/main/java/uy/santipdr/siege/client/SiegeOperationsHubScreen.java")
NAV = read("src/main/java/uy/santipdr/siege/client/SiegeNavigationModel.java")
BASE = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeData.java")
EXP = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeExpansion40.java")
REG = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeRegistry.java")
KNOWLEDGE_SCREEN = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeScreen.java")
ATLAS = read("src/main/java/uy/santipdr/siege/client/SiegeAtlasScreen.java")
BRIEF = read("src/main/java/uy/santipdr/siege/client/SiegeBriefingScreen.java")
THREATS = read("src/main/java/uy/santipdr/siege/client/SiegeThreatBoardScreen.java")
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
CHANGELOG = read("docs/CHANGELOG-4.0.0.md")
MEDIA = read("docs/DVN-MEDIA-CANDIDATES-4.0.md")

assert "version = '4.00.0'" in BUILD
assert "SIEGE 4.00" in CHANGELOG
for cls in ("SiegeAtlasScreen", "SiegeBriefingScreen", "SiegeThreatBoardScreen", "SiegeKnowledgeFileScreen",
            "SiegeRaceAtlasScreen", "SiegeProgressionMapScreen", "SiegeMediaRoomScreen"):
    assert f"class {cls}" in read(f"src/main/java/uy/santipdr/siege/client/{cls}.java")
for route in ("BRIEFING", "ATLAS", "RACES", "PROGRESSION", "THREATS", "MEDIA", "DEPLOYMENT", "INTEL", "KNOWLEDGE", "ARCHIVE", "ARMORY",
              "FIELD_MANUAL", "COMMAND", "DIAGNOSTICS", "SETTINGS", "BACKGROUNDS"):
    assert route in OPS
assert "SiegeKnowledgeExpansion40.entries" in REG
assert "SiegeKnowledgeRegistry.entries" in OPS
for constructor in ("new SiegeBriefingScreen(this)", "new SiegeAtlasScreen(this)", "new SiegeRaceAtlasScreen(this)",
                    "new SiegeProgressionMapScreen(this)", "new SiegeThreatBoardScreen(this)", "new SiegeMediaRoomScreen(this)"):
    assert constructor in HUB, f"Missing War Room route: {constructor}"
assert "new SiegeKnowledgeFileScreen" in HUB
for section in ("BRIEFING", "ATLAS", "RACES", "PROGRESSION", "THREATS", "MEDIA", "KNOWLEDGE", "DEPLOYMENT", "INTEL"):
    assert section in NAV
for knowledge_id in ("progression-mobility-priority", "combat-adaptation", "prompt-precision-framework",
                     "raid-area-discipline", "relic-analysis-workflow", "assembling-planning",
                     "revive-repeat-penalties", "deteriorer-re-overflow-history", "research-open-questions",
                     "newcomer-operational-rule"):
    assert knowledge_id in EXP

# Encyclopedia 4.00 must expose the complete unified registry rather than only the old 3.00 base list.
assert "SiegeKnowledgeRegistry.search" in KNOWLEDGE_SCREEN
assert "Mode { START, RACES, PROGRESSION, SYSTEMS, HISTORY }" in KNOWLEDGE_SCREEN
for category in ("EMPEZAR", "RAZAS", "PROGRESIÓN", "SISTEMAS", "HISTÓRICO"):
    assert category in KNOWLEDGE_SCREEN
# Source/provenance stays internal; normal player surfaces should show the information itself.
for technical in ("sourceLine(", "selected.sources()", 'label("REFERENCIA"', 'label("FUENTES"'):
    assert technical not in KNOWLEDGE_SCREEN
for technical in ("sourceLine(", "entry.sources()", "SERVER FILE · NO PLAYER PROFILE DATA"):
    assert technical not in DETAIL
assert "TAMBIÉN VER" in KNOWLEDGE_SCREEN and "TAMBIÉN VER" in DETAIL

# Dedicated Race Atlas: general server knowledge only, unknowns stay unknown.
for race_id in ("human", "hacker", "shark", "saiyan", "deteriorer", "pharaoh", "apotheosis", "death",
                "cyborg", "ghoul", "subhuman", "terrarian", "kaioshin", "dragon", "shinigami", "majin", "undertale-au"):
    assert f'"{race_id}"' in RACES
for rarity in ("COMMON", "UNCOMMON", "RARE", "ULTRA_RARE", "LEGENDARY", "OBSAINAN", "MYTHIC", "GODLY", "ETERNAL", "FABLED"):
    assert rarity in RACES
assert "UNKNOWN" in RACES and "HIDDEN" in RACES
assert "SiegeKnowledgeRegistry.get" in RACE_SCREEN
assert "rarityRows(" in RACE_SCREEN
assert "knowledge.body(spanish())" in RACE_SCREEN
assert "TAGS:" not in RACE_SCREEN
assert "ABRIR FUENTE" not in RACE_SCREEN
assert "VER INFORMACIÓN COMPLETA" in RACE_SCREEN

# Progression remains conceptual instead of fabricating one universal recipe, and its tabs fit narrow screens.
for track in ('"core"', '"v1v4"', '"special"', '"advanced"'):
    assert track in PROGRESSION
assert "V1 → V4" in PROGRESSION
assert "SiegeKnowledgeRegistry.get" in PROGRESSION_SCREEN
assert "trackButtonLabel(" in PROGRESSION_SCREEN
assert "ABRIR FUENTE" not in PROGRESSION_SCREEN
assert "VER INFORMACIÓN" in PROGRESSION_SCREEN

# Threat Board remains categorized and player-facing.
assert "ABRIR ARCHIVO" not in THREATS
assert "VER INFORMACIÓN" in THREATS
assert "without merging their sources" not in THREATS
assert "sin mezclar sus fuentes" not in THREATS

# Main-menu split buttons measure actual rendered labels to avoid GUI-scale overlap.
assert "buttonLabel(" in MENU_EVENTS
assert "minecraft.font.width(full)" in MENU_EVENTS
assert '"BRF"' in MENU_EVENTS and '"OPS"' in MENU_EVENTS

# Media Room provides recommendations without exposing development/licensing copy in the normal UI.
for title in ("Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir"):
    assert title in MEDIA_DATA
assert "SiegeMusic.previousTrack" in MEDIA_ROOM and "SiegeMusic.nextTrack" in MEDIA_ROOM
assert "SiegeBackgrounds.rotationState" in MEDIA_ROOM
assert "selectedScene = -1" in MEDIA_ROOM
for technical in ("NO INCLUIDAS", "NOT BUNDLED", "redistribuyen", "redistributed", "derechos claros", "clear rights"):
    assert technical.lower() not in MEDIA_ROOM.lower()

# 4.00 adds presentation profiles without removing accessibility/reading presets.
for profile in ("CINEMATIC", "TACTICAL", "PERFORMANCE", "CALM", "READING", "CLASSIC", "HIGH_CONTRAST", "IMMERSIVE"):
    assert profile in PROFILES and profile in PROFILE_SPEC
assert "SiegeClientProfile.Profile.HIGH_CONTRAST" in PROFILE_SPEC
assert "SiegeClientProfile.Profile.IMMERSIVE" in PROFILE_SPEC

# Briefing is directly accessible from the main menu without adding another vertical row.
assert '"siege.menu.deployment"' in MENU_EVENTS
assert "new SiegeBriefingScreen(screen)" in MENU_EVENTS
assert "new SiegeOperationsHubScreen(screen)" in MENU_EVENTS
assert "explicación simple y completa" in BRIEF

# Privacy remains a release contract.
for forbidden in ("mi inventario", "mi personaje", "mi partida actual", "player notebook", "private build"):
    assert forbidden.lower() not in (BASE + EXP + RACES + PROGRESSION).lower()

# Existing high-value contracts survive the major release.
assert "SiegeLacontinuacion.exaroton.me:18736" in MULTI
assert "GLFW_KEY_S && Screen.hasControlDown()" in TITLE
for forbidden in ("FAVORITES", "toggleFavoriteIntel", "favoriteButton", "addIndexButton", "copyText", "GUARDAR"):
    assert forbidden not in INTEL
assert '"tempest_jutcherson"' in EASTER
assert '"tempest_jutcherson"' not in SCENES

# External media policy remains documented for development; it does not need to clutter the player UI.
assert "768x432" in MEDIA
assert "no se incorpora" in MEDIA.lower() or "no se incluyen" in MEDIA.lower()
assert "permiso" in MEDIA.lower()

# CI must validate the completed 4.00 systems and publish only after successful build.
for test_name in ("AtlasRegressionTest", "RaceAtlasRegressionTest", "ProgressionMapRegressionTest", "MediaReferenceRegressionTest"):
    assert test_name in WORKFLOW
assert "version = '4.00.0'" in WORKFLOW
assert "Publish validated jar for installer" in WORKFLOW
print("SIEGE 4.00 completed readability/content contracts passed")
