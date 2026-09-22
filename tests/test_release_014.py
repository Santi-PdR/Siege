#!/usr/bin/env python3
"""Durable release contracts from Atlas through SIEGE 2.50."""
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
WORKFLOW = read(".github/workflows/build.yml")


def numbered(text: str):
    return [int(v) for v in re.findall(r"(?m)^(\d+)\. ", text)]


# Historical release documentation remains intact and 2.50 is the active build.
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
assert "version = '2.50.0'" in BUILD
for stale in ("version = '2.25.0'", "version = '2.0.0'", "version = '1.50.0'", "version = '1.26.0'"):
    assert stale not in BUILD, f"Stale active version returned: {stale}"

# Intel remains conservative, categorized and free of removed controls.
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

# Information domains stay separate while Operations becomes a navigation layer.
for section in ("HOME", "OPERATIONS", "DEPLOYMENT", "INTEL", "REFERENCE", "FIELD_MANUAL", "SETTINGS",
                "COMMAND", "DIAGNOSTICS", "BACKGROUNDS", "INSPECTOR", "MEDIA", "NATIVE"):
    assert section in NAVIGATION, f"Missing navigation section {section}"
assert '"OPERATIONS HUB"' in NAVIGATION and '"OPS"' in NAVIGATION
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

# Operations Hub is additive: search + deep links + live state + session history.
assert "class SiegeOperationsIndex" in OPS_INDEX
for kind in ("ROUTE", "INTEL", "ARMORY"):
    assert kind in OPS_INDEX
for route in ("DEPLOYMENT", "INTEL", "ARCHIVE", "ARMORY", "FIELD_MANUAL", "COMMAND", "DIAGNOSTICS", "SETTINGS", "BACKGROUNDS"):
    assert route in OPS_INDEX
assert "Normalizer.normalize" in OPS_INDEX
assert "IntelCatalog.files()" in OPS_INDEX
assert "SiegeGuideSupplemental.entries" in OPS_INDEX
assert "class SiegeOperationsHubScreen" in OPS_HUB
assert "SiegeOperationsIndex.search" in OPS_HUB
assert "new IntelScreenV3(this, entry.intel())" in OPS_HUB
assert "SiegeRuntimeStatus.intelLabel" in OPS_HUB
assert "SiegeRuntimeStatus.audioLabel" in OPS_HUB
assert "SiegeRouteHistory.snapshot" in OPS_HUB
assert "siege.menu.settings" in OPS_EVENTS
assert "new SiegeOperationsHubScreen(screen)" in OPS_EVENTS
assert "class SiegeRouteHistory" in ROUTE_HISTORY and "RECENT.remove(route)" in ROUTE_HISTORY

# Tempest Jutcherson remains an easter egg and is not a normal menu photo.
assert "TEMPEST_JUTCHERSON" in EASTER_EGGS
assert '"tempest_jutcherson"' in EASTER_EGGS
assert '"tempest_jutcherson"' not in SCENE_CATALOG
assert '"tempest_jutcherson"' not in BACKGROUND_SCRIPT
assert "anomalyIndex()" in SCENE_CATALOG
assert "standardIndex(long slot)" in SCENE_SCHEDULE
assert "SiegeSceneCatalog.featuredIndex()" in SCENE_SCHEDULE
assert "SiegeSceneCatalog.anomalyIndex()" not in SCENE_SCHEDULE

# Deployment and hidden Singleplayer contracts are preserved.
assert "SiegeLacontinuacion.exaroton.me:18736" in MULTIPLAYER
assert "original.onPress()" in MULTIPLAYER
for state in ("QUERYING", "OFFLINE", "NO_RESPONSE", "INCOMPATIBLE", "ONLINE"):
    assert state in DEPLOYMENT
assert "GLFW_KEY_S && Screen.hasControlDown()" in TITLE
for forbidden_key in ("GLFW_KEY_I", "GLFW_KEY_P", "GLFW_KEY_G", "GLFW_KEY_M", "GLFW_KEY_R"):
    assert re.search(rf"(?<![A-Z0-9_]){re.escape(forbidden_key)}(?![A-Z0-9_])", TITLE) is None, \
        f"Unrequested title shortcut returned: {forbidden_key}"

# CI must validate the new systems rather than merely compiling them.
assert "OperationsIndexTest" in WORKFLOW
assert "GuiResourceRegressionTest" in WORKFLOW
assert "SceneScheduleTest" in WORKFLOW
assert "NavigationIdentityTest" in WORKFLOW
assert "version = '2.50.0'" in WORKFLOW
assert "tempest_jutcherson" in WORKFLOW
assert "SiegeEasterEggVault.java" in WORKFLOW

print("SIEGE durable release contracts through 2.50 advanced Operations overhaul passed")
