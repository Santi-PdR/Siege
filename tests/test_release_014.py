#!/usr/bin/env python3
"""Durable release contracts from Atlas through SIEGE 3.00."""
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
}

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
DIAGNOSTIC = read("src/main/java/uy/santipdr/siege/client/SiegeDiagnosticReport.java")
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
KNOWLEDGE_ARCH = read("docs/KNOWLEDGE-ARCHITECTURE-3.0.md")
QA_300 = read("docs/QA-3.0.0.md")
WORKFLOW = read(".github/workflows/build.yml")


def numbered(text: str):
    return [int(v) for v in re.findall(r"(?m)^(\d+)\. ", text)]


# Historical release docs remain intact and 3.00 is active.
assert numbered(CHANGELOGS["014"]) == list(range(1, 51))
assert numbered(CHANGELOGS["050"]) == list(range(1, 51))
assert numbered(CHANGELOGS["060"]) == list(range(1, 61))
assert numbered(CHANGELOGS["070"]) == list(range(1, 71))
assert numbered(CHANGELOGS["075"]) == list(range(1, 13))
assert numbered(CHANGELOGS["125"]) == list(range(1, 81))
assert numbered(CHANGELOGS["126"]) == list(range(1, 35))
assert numbered(CHANGELOGS["150"]) == list(range(1, 51))
assert numbered(CHANGELOGS["200"]) == list(range(1, 81))
assert numbered(CHANGELOGS["225"]) == list(range(1, 26))
assert numbered(CHANGELOGS["250"]) == list(range(1, 51))
assert numbered(CHANGELOGS["300"]) == list(range(1, 101))
assert "version = '3.00.0'" in BUILD
for stale in ("version = '2.50.0'", "version = '2.25.0'", "version = '2.0.0'", "version = '1.50.0'", "version = '1.26.0'"):
    assert stale not in BUILD, f"Stale active version returned: {stale}"

# Intel remains conservative and free of removed controls.
assert 'file("SUP-001", "ATLAS", "SUPER-UNIT", 5, "125,000,000", "atlas"' in INTEL_DATA
assert "No se recuperaron datos verificados" in INTEL_DATA
for forbidden in ("FAVORITES", "toggleFavoriteIntel", "favoriteButton", "addIndexButton", "copyText", "GUARDAR"):
    assert forbidden not in INTEL_SCREEN, f"Removed Intel control returned: {forbidden}"
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
    assert fragment in INTEL_CURRENT, f"Missing source-backed Intel record: {fragment}"
for fragment in (
        'dossier("HU-010", "GRAPPLER", "UNKNOWN"',
        'dossier("HU-012", "SKYDIVER", "UNKNOWN"',
        'dossier("HU-013", "SKYLINER", "UNKNOWN"'):
    assert fragment in INTEL_CURRENT, f"Missing unresolved UNKNOWN record: {fragment}"
assert "No se encontró una referencia suficientemente fiable" in INTEL_CURRENT

# Information domains remain separate.
for section in ("HOME", "OPERATIONS", "KNOWLEDGE", "DEPLOYMENT", "INTEL", "REFERENCE", "FIELD_MANUAL", "SETTINGS",
                "COMMAND", "DIAGNOSTICS", "BACKGROUNDS", "INSPECTOR", "MEDIA", "NATIVE"):
    assert section in NAVIGATION, f"Missing navigation section {section}"
assert '"OPERATIONS HUB"' in NAVIGATION and '"OPS"' in NAVIGATION
assert '"SERVER ENCYCLOPEDIA"' in NAVIGATION and '"ENC"' in NAVIGATION
assert "enum Mode { ARCHIVE, ARMORY }" in GUIDE
assert "new SiegeArchiveScreen(this)" in GUIDE
assert '"third-justice"' in GUIDE_SUPPLEMENTAL
assert "SHELLSHOCK" in ARCHIVE_DATA and "MUTILATED" in ARCHIVE_DATA
assert "new SiegeArchiveScreen(this)" not in SYSTEM
for section in ("APPEARANCE", "MOTION", "AUDIO", "INTEL", "ACCESSIBILITY", "BACKGROUNDS", "SYSTEM"):
    assert section in SETTINGS

# Ordinary chrome keeps qualitative health instead of the old unexplained percentage.
assert "SiegeRuntimeStatus.readiness()" not in SCREEN_CHROME
assert "readiness() + \"%\"" not in SCREEN_CHROME
assert "SiegeRuntimeStatus.healthLabel" in SCREEN_CHROME
assert "public static int readiness()" in DIAGNOSTIC

# 3.00 Server Encyclopedia contracts.
assert "class SiegeKnowledgeData" in KNOWLEDGE
assert "enum Zone { SERVER, HISTORY }" in KNOWLEDGE
for confidence in ("STAFF_CONFIRMED", "SYSTEM_OBSERVED", "HISTORICAL", "UNCONFIRMED", "CONTRADICTION"):
    assert confidence in KNOWLEDGE, f"Missing encyclopedia confidence {confidence}"
for domain in ("PROGRESSION", "EXECUTORS", "TRIALS", "STRUCTURES", "BOSSES", "MISSIONS", "RACES",
               "ABILITIES", "MEDITATION", "ITEMS", "ASSEMBLING", "RELICS", "DIMENSIONS", "DEATH_REVIVE",
               "RAIDS_EVENTS", "FACTIONS", "ECONOMY", "PROMPTS", "CONTRADICTIONS", "SOURCES"):
    assert domain in KNOWLEDGE, f"Missing encyclopedia domain {domain}"
for audit_value in ("488 archivos", "252/252 JSON", "251.065", "0 duplicados"):
    assert audit_value in KNOWLEDGE, f"Encyclopedia audit lost: {audit_value}"
for entry_id in (
        "server-overview", "rarity-order", "race-catalog", "race-human", "race-hacker", "race-shark",
        "race-saiyan", "race-deteriorer", "race-pharaoh", "race-apotheosis", "race-death", "race-cyborg",
        "race-ghoul", "race-subhuman", "race-terrarian", "race-kaioshin", "race-dragon", "race-shinigami",
        "race-majin", "race-undertale-au", "race-slots", "fabled-acquisition", "progression-v1-v4",
        "trials-basics", "executors-basics", "structures-basics", "bosses-basics", "missions-npcs",
        "dimensions-basics", "assembling-table", "item-geography-table", "item-daemonium-kit",
        "item-improbability-scroll", "relic-basics", "relic-third-justice", "respawn-cards", "raids-basics",
        "factions-basics", "economy-basics", "prompt-design"):
    assert f'"{entry_id}"' in KNOWLEDGE, f"Missing server encyclopedia entry {entry_id}"
assert "Común → Poco común → Raro → Ultra raro → Legendario → Obsainan → Mítico → Godly → Eternal → Fabled" in KNOWLEDGE
assert '"item-geography-table"' in KNOWLEDGE and "120 wins" in KNOWLEDGE
assert '"race-deteriorer-old-debuff", Zone.HISTORY' in KNOWLEDGE
assert '"meditation-levels", Zone.HISTORY, Domain.CONTRADICTIONS' in KNOWLEDGE
assert "class SiegeKnowledgeScreen" in KNOWLEDGE_SCREEN
assert "Mode { START, RACES, SYSTEMS, HISTORY }" in KNOWLEDGE_SCREEN
assert "ENCICLOPEDIA DEL SERVIDOR" in KNOWLEDGE_SCREEN
assert "Guía general" in KNOWLEDGE_SCREEN
assert "perfiles de jugadores" in KNOWLEDGE_ARCH
assert "añadir únicamente información general" in KNOWLEDGE_ARCH
assert "KnowledgeDataRegressionTest" in WORKFLOW
assert "información personal" in QA_300

# No personal-player subsystem is allowed back into the encyclopedia.
for forbidden in (
        "current-rust-guard", "current-deteriorer-snapshot", "current-meditation", "CURRENT_CONFIRMED",
        "PLAYER_EXPERIENCE", "SIEGE current notebook", "player notebook", "Mi partida actual"):
    assert forbidden not in KNOWLEDGE, f"Personal/player-specific encyclopedia data returned: {forbidden}"

# Operations Hub remains additive: search + deep links + live state + history.
assert "class SiegeOperationsIndex" in OPS_INDEX
for kind in ("ROUTE", "INTEL", "ARMORY", "KNOWLEDGE"):
    assert kind in OPS_INDEX
for route in ("DEPLOYMENT", "INTEL", "KNOWLEDGE", "ARCHIVE", "ARMORY", "FIELD_MANUAL", "COMMAND", "DIAGNOSTICS", "SETTINGS", "BACKGROUNDS"):
    assert route in OPS_INDEX
assert "Normalizer.normalize" in OPS_INDEX
assert "IntelCatalog.files()" in OPS_INDEX
assert "SiegeGuideSupplemental.entries" in OPS_INDEX
assert "SiegeKnowledgeData.entries()" in OPS_INDEX
assert "knowledgeId" in OPS_INDEX
assert "class SiegeOperationsHubScreen" in OPS_HUB
assert "SiegeOperationsIndex.search" in OPS_HUB
assert "new IntelScreenV3(this, entry.intel())" in OPS_HUB
assert "new SiegeKnowledgeScreen(this, entry.knowledgeId())" in OPS_HUB
assert "new SiegeKnowledgeScreen(this)" in OPS_HUB
assert "SiegeRuntimeStatus.intelLabel" in OPS_HUB
assert "SiegeRuntimeStatus.audioLabel" in OPS_HUB
assert "SiegeKnowledgeData.critical" in OPS_HUB
assert "SiegeRouteHistory.snapshot" in OPS_HUB
assert "siege.menu.settings" in OPS_EVENTS
assert "new SiegeOperationsHubScreen(screen)" in OPS_EVENTS
assert 'case "SiegeKnowledgeScreen"' in OPS_EVENTS
assert "class SiegeRouteHistory" in ROUTE_HISTORY and "RECENT.remove(route)" in ROUTE_HISTORY

# Tempest Jutcherson stays an easter egg and not a normal menu image.
assert "TEMPEST_JUTCHERSON" in EASTER_EGGS
assert '"tempest_jutcherson"' in EASTER_EGGS
assert '"tempest_jutcherson"' not in SCENE_CATALOG
assert '"tempest_jutcherson"' not in BACKGROUND_SCRIPT
assert "anomalyIndex()" in SCENE_CATALOG
assert "standardIndex(long slot)" in SCENE_SCHEDULE
assert "SiegeSceneCatalog.featuredIndex()" in SCENE_SCHEDULE
assert "SiegeSceneCatalog.anomalyIndex()" not in SCENE_SCHEDULE

# Deployment and hidden Singleplayer contracts remain.
assert "SiegeLacontinuacion.exaroton.me:18736" in MULTIPLAYER
assert "original.onPress()" in MULTIPLAYER
for state in ("QUERYING", "OFFLINE", "NO_RESPONSE", "INCOMPATIBLE", "ONLINE"):
    assert state in DEPLOYMENT
assert "GLFW_KEY_S && Screen.hasControlDown()" in TITLE
for forbidden_key in ("GLFW_KEY_I", "GLFW_KEY_P", "GLFW_KEY_G", "GLFW_KEY_M", "GLFW_KEY_R"):
    assert re.search(rf"(?<![A-Z0-9_]){re.escape(forbidden_key)}(?![A-Z0-9_])", TITLE) is None, \
        f"Unrequested title shortcut returned: {forbidden_key}"

# CI validates the new systems rather than merely compiling them.
for required in ("OperationsIndexTest", "KnowledgeDataRegressionTest", "GuiResourceRegressionTest", "SceneScheduleTest", "NavigationIdentityTest"):
    assert required in WORKFLOW, f"CI lost {required}"
assert "version = '3.00.0'" in WORKFLOW
assert "tempest_jutcherson" in WORKFLOW
assert "SiegeEasterEggVault.java" in WORKFLOW
assert "SiegeKnowledgeData.java" in WORKFLOW
assert "SiegeKnowledgeScreen" in WORKFLOW

print("SIEGE durable release contracts through 3.00 Server Encyclopedia overhaul passed")
