#!/usr/bin/env python3
"""Durable release contracts from Atlas through SIEGE 4.00."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
read = lambda path: (ROOT / path).read_text(encoding="utf-8")

CHANGELOGS = {
    "014": read("docs/CHANGELOG-0.14.0.md"),
    "050": read("docs/CHANGELOG-0.50.0.md"),
    "060": read("docs/CHANGELOG-0.60.0.md"),
    "070": read("docs/CHANGELOG-0.70.0.md"),
    "075": read("docs/CHANGELOG-0.75.0.md"),
    "125": read("docs/CHANGELOG-1.25.0.md"),
    "126": read("docs/CHANGELOG-1.26.0.md"),
    "150": read("docs/CHANGELOG-1.50.0.md"),
    "200": read("docs/CHANGELOG-2.0.0.md"),
    "225": read("docs/CHANGELOG-2.25.0.md"),
    "250": read("docs/CHANGELOG-2.50.0.md"),
    "300": read("docs/CHANGELOG-3.0.0.md"),
    "400": read("docs/CHANGELOG-4.0.0.md"),
}

def numbered(text):
    return [int(v) for v in re.findall(r"(?m)^(\d+)\. ", text)]

expected = {
    "014": 50, "050": 50, "060": 60, "070": 70, "075": 12,
    "125": 80, "126": 34, "150": 50, "200": 80, "225": 25,
    "250": 50, "300": 100, "400": 100,
}
for key, amount in expected.items():
    assert numbered(CHANGELOGS[key]) == list(range(1, amount + 1)), f"Broken changelog {key}"

BUILD = read("build.gradle")
INTEL_DATA = read("src/main/java/uy/santipdr/siege/client/IntelData.java")
INTEL_CURRENT = read("src/main/java/uy/santipdr/siege/client/IntelCurrentData.java")
INTEL_CATALOG = read("src/main/java/uy/santipdr/siege/client/IntelCatalog.java")
INTEL_SCREEN = read("src/main/java/uy/santipdr/siege/client/IntelScreenV3.java")
INTEL_PRESENTATION = read("src/main/java/uy/santipdr/siege/client/IntelPresentation.java")
DEPLOYMENT = read("src/main/java/uy/santipdr/siege/client/SiegeDeploymentStatus.java")
MULTIPLAYER = read("src/main/java/uy/santipdr/siege/client/SiegeMultiplayerScreen.java")
TITLE = read("src/main/java/uy/santipdr/siege/client/SiegeTitleScreen.java")
NAVIGATION = read("src/main/java/uy/santipdr/siege/client/SiegeNavigationModel.java")
SCREEN_CHROME = read("src/main/java/uy/santipdr/siege/client/SiegeScreenChrome.java")
GUIDE = read("src/main/java/uy/santipdr/siege/client/SiegeGuideScreen.java")
GUIDE_SUPPLEMENTAL = read("src/main/java/uy/santipdr/siege/client/SiegeGuideSupplemental.java")
ARCHIVE_DATA = read("src/main/java/uy/santipdr/siege/client/SiegeArchiveData.java")
SYSTEM = read("src/main/java/uy/santipdr/siege/client/SiegeSystemScreen.java")
SETTINGS = read("src/main/java/uy/santipdr/siege/client/SiegeSettingsScreen.java")
SCENE_CATALOG = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
SCENE_SCHEDULE = read("src/main/java/uy/santipdr/siege/client/SiegeSceneSchedule.java")
EASTER_EGGS = read("src/main/java/uy/santipdr/siege/client/SiegeEasterEggVault.java")
BACKGROUND_SCRIPT = read("scripts/prepare-backgrounds-hd.py")
OPS_INDEX = read("src/main/java/uy/santipdr/siege/client/SiegeOperationsIndex.java")
OPS_HUB = read("src/main/java/uy/santipdr/siege/client/SiegeOperationsHubScreen.java")
OPS_EVENTS = read("src/main/java/uy/santipdr/siege/client/Siege250MenuEvents.java")
ROUTE_HISTORY = read("src/main/java/uy/santipdr/siege/client/SiegeRouteHistory.java")
KNOWLEDGE = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeData.java")
KNOWLEDGE_SCREEN = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeScreen.java")
RECRUIT = read("src/main/java/uy/santipdr/siege/client/SiegeRecruitBriefingScreen.java")
RACES = read("src/main/java/uy/santipdr/siege/client/SiegeRaceAtlasData.java")
RACE_SCREEN = read("src/main/java/uy/santipdr/siege/client/SiegeRaceAtlasScreen.java")
PROGRESSION = read("src/main/java/uy/santipdr/siege/client/SiegeProgressionData.java")
PROGRESSION_SCREEN = read("src/main/java/uy/santipdr/siege/client/SiegeProgressionMapScreen.java")
THREATS = read("src/main/java/uy/santipdr/siege/client/SiegeThreatBoardScreen.java")
MEDIA = read("src/main/java/uy/santipdr/siege/client/SiegeMediaRoomScreen.java")
MEDIA_DATA = read("src/main/java/uy/santipdr/siege/client/SiegeMediaReferenceData.java")
WORKFLOW = read(".github/workflows/build.yml")

# Active build.
assert "version = '4.00.0'" in BUILD
for stale in ("version = '3.00.0'", "version = '2.50.0'", "version = '2.25.0'", "version = '2.0.0'"):
    assert stale not in BUILD

# Intel durability.
assert 'file("SUP-001", "ATLAS", "SUPER-UNIT", 5, "125,000,000", "atlas"' in INTEL_DATA
assert "No se recuperaron datos verificados" in INTEL_DATA
for forbidden in ("FAVORITES", "toggleFavoriteIntel", "favoriteButton", "addIndexButton", "copyText", "GUARDAR"):
    assert forbidden not in INTEL_SCREEN
assert 'label("AMPLIAR", "INSPECT")' in INTEL_SCREEN
assert '"UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT", "UNKNOWN"' in INTEL_SCREEN
assert 'case "UNKNOWN" -> "UNK";' in INTEL_PRESENTATION
assert 'Set.of("UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT", "UNKNOWN")' in INTEL_CATALOG
for fragment in (
        'dossier("ADV-007", "ENGINEER", "ADVANCED", 0, "150"',
        'dossier("ADV-008", "INFORMANT", "ADVANCED", 0, "155"',
        'dossier("ADV-009", "TRANQUILIZER", "ADVANCED", 0, "100"',
        'dossier("TNK-006", "AGITATOR", "TANK", 0, "300"',
        'dossier("BOS-010", "SPARTA", "BOSS", 0, "350"',
        'dossier("BOS-011", "PROTEUS", "BOSS", 0, "550"'):
    assert fragment in INTEL_CURRENT

# Architecture/invariants.
for section in ("HOME", "OPERATIONS", "KNOWLEDGE", "DEPLOYMENT", "INTEL", "REFERENCE", "FIELD_MANUAL",
                "SETTINGS", "COMMAND", "DIAGNOSTICS", "BACKGROUNDS", "INSPECTOR", "MEDIA", "NATIVE"):
    assert section in NAVIGATION
for code in ('"RCT"', '"ENC"', '"RCE"', '"PRG"', '"THR"', '"AV"'):
    assert code in NAVIGATION
assert "enum Mode { ARCHIVE, ARMORY }" in GUIDE
assert "new SiegeArchiveScreen(this)" in GUIDE
assert "new SiegeArchiveScreen(this)" not in SYSTEM
assert "SHELLSHOCK" in ARCHIVE_DATA and "MUTILATED" in ARCHIVE_DATA
for section in ("APPEARANCE", "MOTION", "AUDIO", "INTEL", "ACCESSIBILITY", "BACKGROUNDS", "SYSTEM"):
    assert section in SETTINGS
assert "readiness() + \"%\"" not in SCREEN_CHROME

# Server encyclopedia remains non-personal and player-facing.
assert "enum Zone { SERVER, HISTORY }" in KNOWLEDGE
for entry_id in ("server-overview", "rarity-order", "race-catalog", "progression-v1-v4", "trials-basics",
                 "executors-basics", "structures-basics", "bosses-basics", "dimensions-basics", "assembling-table",
                 "item-geography-table", "item-daemonium-kit", "relic-basics", "respawn-cards", "raids-basics",
                 "factions-basics", "economy-basics"):
    assert f'"{entry_id}"' in KNOWLEDGE
for forbidden in ("current-rust-guard", "current-deteriorer-snapshot", "CURRENT_CONFIRMED", "PLAYER_EXPERIENCE",
                  "Mi partida actual", "mi inventario", "mi personaje"):
    assert forbidden not in KNOWLEDGE
assert "Mode { START, RACES, PROGRESSION, SYSTEMS, HISTORY }" in KNOWLEDGE_SCREEN
assert "sourceLine(" not in KNOWLEDGE_SCREEN
assert "selected.sources()" not in KNOWLEDGE_SCREEN
assert 'label("FUENTES"' not in KNOWLEDGE_SCREEN

# 4.0 front door + specialized screens.
assert "new SiegeRecruitBriefingScreen(screen)" in OPS_EVENTS
for topic in ("SERVER", "RACES", "PROGRESSION", "SURVIVAL", "THREATS", "EQUIPMENT", "DEPLOYMENT", "MEDIA"):
    assert topic in RECRUIT
assert "new SiegeOperationsHubScreen(this)" in RECRUIT
assert "class SiegeRaceAtlasScreen" in RACE_SCREEN
assert "class SiegeProgressionMapScreen" in PROGRESSION_SCREEN
assert "class SiegeThreatBoardScreen" in THREATS
assert "class SiegeMediaRoomScreen" in MEDIA

# Races, progression, Arsenal and Media.
for rarity in ("COMMON", "UNCOMMON", "RARE", "ULTRA_RARE", "LEGENDARY", "OBSAINAN", "MYTHIC", "GODLY", "ETERNAL", "FABLED"):
    assert rarity in RACES
for race in ("Human", "Hacker", "Shark", "Saiyan", "Deteriorer", "Faraón", "Apotheosis", "Muerte", "Cyborg", "Ghoul", "Subhuman", "Terrariano", "Kaioshin", "Dragon", "Shinigami", "Majin", "Undertale AU"):
    assert f'"{race}"' in RACES
for track in ("RUTA GENERAL", "V1 → V4", "TRIALS", "RUTAS ESPECIALES", "SISTEMAS AVANZADOS"):
    assert track in PROGRESSION
assert "ABRIR FUENTE" not in PROGRESSION_SCREEN
assert "VER INFORMACIÓN" in PROGRESSION_SCREEN
assert "mouseScrolled" in THREATS and "offset" in THREATS
for item in ("Third Justice", "Geography Table", "Daemonium Kit", "Fallen Angel Halo", "Improbability Scroll", "Assembling Table"):
    assert f'"{item}"' in GUIDE_SUPPLEMENTAL
assert "120 wins" in GUIDE_SUPPLEMENTAL and "lifesteal" in GUIDE_SUPPLEMENTAL
for track in ("Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir"):
    assert track in MEDIA_DATA

# Search, server, easter egg and shortcuts survive.
assert "SiegeOperationsIndex.search" in OPS_HUB
assert "new IntelScreenV3(this, entry.intel())" in OPS_HUB
assert "new SiegeKnowledgeScreen(this, entry.knowledgeId())" in OPS_HUB
assert "SiegeRouteHistory.snapshot" in OPS_HUB
assert "class SiegeRouteHistory" in ROUTE_HISTORY and "RECENT.remove(route)" in ROUTE_HISTORY
assert "TEMPEST_JUTCHERSON" in EASTER_EGGS
assert '"tempest_jutcherson"' not in SCENE_CATALOG
assert "tempest_jutcherson" not in BACKGROUND_SCRIPT
assert "standardIndex(long slot)" in SCENE_SCHEDULE
assert "SiegeLacontinuacion.exaroton.me:18736" in MULTIPLAYER
assert "original.onPress()" in MULTIPLAYER
for state in ("QUERYING", "OFFLINE", "NO_RESPONSE", "INCOMPATIBLE", "ONLINE"):
    assert state in DEPLOYMENT
assert "GLFW_KEY_S && Screen.hasControlDown()" in TITLE
for forbidden_key in ("GLFW_KEY_I", "GLFW_KEY_P", "GLFW_KEY_G", "GLFW_KEY_M", "GLFW_KEY_R"):
    assert re.search(rf"(?<![A-Z0-9_]){re.escape(forbidden_key)}(?![A-Z0-9_])", TITLE) is None

for required in ("OperationsIndexTest", "KnowledgeDataRegressionTest", "GuiResourceRegressionTest", "SceneScheduleTest",
                 "NavigationIdentityTest", "test_release_400.py"):
    assert required in WORKFLOW
assert "version = '4.00.0'" in WORKFLOW
assert "tempest_jutcherson" in WORKFLOW

print("SIEGE durable release contracts through 4.00 Command Rebuild passed")
