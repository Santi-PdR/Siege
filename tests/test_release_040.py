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
ATLAS = read("src/main/java/uy/santipdr/siege/client/SiegeAtlasScreen.java")
ATLAS_INDEX = read("src/main/java/uy/santipdr/siege/client/SiegeAtlasIndex.java")
KNOWLEDGE = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeScreen.java")
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

# Dedicated Race Atlas: general server knowledge only, unknowns stay unknown.
for race_id in ("human", "hacker", "shark", "saiyan", "deteriorer", "pharaoh", "apotheosis", "death",
                "cyborg", "ghoul", "subhuman", "terrarian", "kaioshin", "dragon", "shinigami", "majin", "undertale-au"):
    assert f'"{race_id}"' in RACES
for rarity in ("COMMON", "UNCOMMON", "RARE", "ULTRA_RARE", "LEGENDARY", "OBSAINAN", "MYTHIC", "GODLY", "ETERNAL", "FABLED"):
    assert rarity in RACES
assert "UNKNOWN" in RACES and "HIDDEN" in RACES
assert "SiegeKnowledgeRegistry.get" in RACE_SCREEN

# Progression remains conceptual instead of fabricating one universal recipe.
for track in ('"core"', '"v1v4"', '"special"', '"advanced"'):
    assert track in PROGRESSION
assert "V1 → V4" in PROGRESSION
assert "SiegeKnowledgeRegistry.get" in PROGRESSION_SCREEN

# Media Room controls bundled assets and can recommend DVN material without silently importing it.
for title in ("Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir"):
    assert title in MEDIA_DATA
assert "clear permission" in MEDIA_DATA.lower() or "distributable resource" in MEDIA_DATA.lower()
assert "SiegeMusic.previousTrack" in MEDIA_ROOM and "SiegeMusic.nextTrack" in MEDIA_ROOM
assert "SiegeBackgrounds.rotationState" in MEDIA_ROOM
assert "selectedScene = -1" in MEDIA_ROOM

# 4.00 adds presentation profiles without removing the accessibility/reading presets.
for profile in ("CINEMATIC", "TACTICAL", "PERFORMANCE", "CALM", "READING", "CLASSIC", "HIGH_CONTRAST", "IMMERSIVE"):
    assert profile in PROFILES and profile in PROFILE_SPEC
assert "SiegeClientProfile.Profile.HIGH_CONTRAST" in PROFILE_SPEC
assert "SiegeClientProfile.Profile.IMMERSIVE" in PROFILE_SPEC

# Briefing is directly accessible from the main menu without adding another vertical row.
assert '"siege.menu.deployment"' in MENU_EVENTS
assert "new SiegeBriefingScreen(screen)" in MENU_EVENTS
assert "new SiegeOperationsHubScreen(screen)" in MENU_EVENTS
assert "briefingW < 96" in MENU_EVENTS and "deploymentW < 118" in MENU_EVENTS
assert "opsW < 118" in MENU_EVENTS and "settingsW < 92" in MENU_EVENTS

# Player-facing encyclopedia must stay simple: validation metadata remains internal, not UI chrome.
for screen in (KNOWLEDGE, ATLAS, DETAIL):
    assert "sourceLine(" not in screen
assert "CORPUS " not in ATLAS
assert "Domain.SOURCES" not in ATLAS_INDEX.split("case RESEARCH ->", 1)[1]
assert "ABRIR FUENTE" not in RACE_SCREEN
assert "OPEN SOURCE" not in RACE_SCREEN
assert "Cada paso abre una ficha con fuente" not in BRIEF
assert "REFERENCIAS" not in DETAIL and "REFERENCES" not in DETAIL
assert "TAMBIÉN PODÉS VER" in DETAIL and "SEE ALSO" in DETAIL
assert "PROGRESIÓN" in KNOWLEDGE and "Mode { START, RACES, PROGRESSION, SYSTEMS, HISTORY }" in KNOWLEDGE

# Search results avoid long metadata in buttons; detail remains available as tooltip.
assert 'button.setMessage(Component.literal(prefix + " · " + entry.title()))' in HUB
assert "button.setTooltip(Tooltip.create(Component.literal(entry.subtitle())))" in HUB
assert "Sin coincidencias" in HUB

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

# Discovered media may be recommended without silently reuploading low-res/copyrighted assets.
assert "768x432" in MEDIA
assert "no se incorpora" in MEDIA.lower() or "no se incluyen" in MEDIA.lower()
assert "permiso" in MEDIA.lower()

# CI must validate the completed 4.00 systems and publish only after successful build.
for test_name in ("AtlasRegressionTest", "RaceAtlasRegressionTest", "ProgressionMapRegressionTest", "MediaReferenceRegressionTest"):
    assert test_name in WORKFLOW
assert "test_release_040.py" in WORKFLOW
assert "version = '4.00.0'" in WORKFLOW
assert "Publish validated jar for installer" in WORKFLOW
print("SIEGE 4.00 completed release contracts passed")
