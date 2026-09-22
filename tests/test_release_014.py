#!/usr/bin/env python3
"""Durable release contracts from Atlas through SIEGE 2.25."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
read = lambda path: (ROOT / path).read_text(encoding="utf-8")

CHANGELOG_014 = read("docs/CHANGELOG-0.14.0.md")
CHANGELOG_050 = read("docs/CHANGELOG-0.50.0.md")
CHANGELOG_060 = read("docs/CHANGELOG-0.60.0.md")
CHANGELOG_070 = read("docs/CHANGELOG-0.70.0.md")
CHANGELOG_075 = read("docs/CHANGELOG-0.75.0.md")
CHANGELOG_125 = read("docs/CHANGELOG-1.25.0.md")
CHANGELOG_126 = read("docs/CHANGELOG-1.26.0.md")
CHANGELOG_150 = read("docs/CHANGELOG-1.50.0.md")
CHANGELOG_200 = read("docs/CHANGELOG-2.0.0.md")
CHANGELOG_225 = read("docs/CHANGELOG-2.25.0.md")
ARCHITECTURE_200 = read("docs/INFORMATION-ARCHITECTURE-2.0.md")
INTEL_DATA = read("src/main/java/uy/santipdr/siege/client/IntelData.java")
INTEL_CURRENT = read("src/main/java/uy/santipdr/siege/client/IntelCurrentData.java")
INTEL_CATALOG = read("src/main/java/uy/santipdr/siege/client/IntelCatalog.java")
INTEL_SCREEN = read("src/main/java/uy/santipdr/siege/client/IntelScreenV3.java")
INTEL_PRESENTATION = read("src/main/java/uy/santipdr/siege/client/IntelPresentation.java")
DEPLOYMENT = read("src/main/java/uy/santipdr/siege/client/SiegeDeploymentStatus.java")
MULTIPLAYER = read("src/main/java/uy/santipdr/siege/client/SiegeMultiplayerScreen.java")
SYSTEM = read("src/main/java/uy/santipdr/siege/client/SiegeSystemScreen.java")
SETTINGS = read("src/main/java/uy/santipdr/siege/client/SiegeSettingsScreen.java")
PROFILE = read("src/main/java/uy/santipdr/siege/client/SiegeClientProfile.java")
PROFILE_SPEC = read("src/main/java/uy/santipdr/siege/client/SiegeProfileSpec.java")
METRICS = read("src/main/java/uy/santipdr/siege/client/SiegeProfileMetrics.java")
DIAGNOSTIC = read("src/main/java/uy/santipdr/siege/client/SiegeDiagnosticReport.java")
DIAGNOSTICS_SCREEN = read("src/main/java/uy/santipdr/siege/client/SiegeDiagnosticsScreen.java")
NAVIGATION = read("src/main/java/uy/santipdr/siege/client/SiegeNavigationModel.java")
SCREEN_CHROME = read("src/main/java/uy/santipdr/siege/client/SiegeScreenChrome.java")
COMMAND_STRIP = read("src/main/java/uy/santipdr/siege/client/SiegeCommandStrip.java")
UI_LAYOUT = read("src/main/java/uy/santipdr/siege/client/SiegeUiLayout.java")
UI_SOUNDS = read("src/main/java/uy/santipdr/siege/client/SiegeUiSounds.java")
BACKGROUND = read("src/main/java/uy/santipdr/siege/client/SiegeBackgrounds.java")
SCENE_CATALOG = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
SCENE_SCHEDULE = read("src/main/java/uy/santipdr/siege/client/SiegeSceneSchedule.java")
EVENTS = read("src/main/java/uy/santipdr/siege/client/SiegeMenuThemeEvents.java")
CHROME = read("src/main/java/uy/santipdr/siege/client/SiegeVanillaChrome.java")
GUIDE = read("src/main/java/uy/santipdr/siege/client/SiegeGuideScreen.java")
GUIDE_SUPPLEMENTAL = read("src/main/java/uy/santipdr/siege/client/SiegeGuideSupplemental.java")
ARCHIVE = read("src/main/java/uy/santipdr/siege/client/SiegeArchiveScreen.java")
EVIDENCE = read("src/main/java/uy/santipdr/siege/client/SiegeEvidenceReelScreen.java")
TITLE = read("src/main/java/uy/santipdr/siege/client/SiegeTitleScreen.java")
BUILD = read("build.gradle")
WORKFLOW = read(".github/workflows/build.yml")
RUNTIME_TEST = read("tests/RuntimeRegressionTest.java")
GUI_RESOURCE_TEST = read("tests/GuiResourceRegressionTest.java")
NAV_TEST = read("tests/NavigationIdentityTest.java")
GUIDE_MEDIA_TEST = read("tests/GuideMediaRegressionTest.java")


def numbered(changelog: str):
    return [int(v) for v in re.findall(r"(?m)^(\d+)\. ", changelog)]


# Historical milestones stay real and the active build is exactly 2.25.0.
assert numbered(CHANGELOG_014) == list(range(1, 51))
assert numbered(CHANGELOG_050) == list(range(1, 51))
assert numbered(CHANGELOG_060) == list(range(1, 61))
assert numbered(CHANGELOG_070) == list(range(1, 71))
assert numbered(CHANGELOG_075) == list(range(1, 13))
assert numbered(CHANGELOG_125) == list(range(1, 81))
assert numbered(CHANGELOG_126) == list(range(1, 35))
assert numbered(CHANGELOG_150) == list(range(1, 51))
assert numbered(CHANGELOG_200) == list(range(1, 81))
assert numbered(CHANGELOG_225) == list(range(1, 26))
assert "version = '2.25.0'" in BUILD
for stale in ("version = '2.0.0'", "version = '1.50.0'", "version = '1.26.0'", "version = '1.20.0'", "version = '0.75.0'"):
    assert stale not in BUILD, f"Stale active version returned: {stale}"

# Atlas and Intel contracts remain intact.
assert 'file("SUP-001", "ATLAS", "SUPER-UNIT", 5, "125,000,000", "atlas"' in INTEL_DATA
assert "No se recuperaron datos verificados" in INTEL_DATA
for forbidden in ("FAVORITES", "toggleFavoriteIntel", "favoriteButton", "addIndexButton", "copyText", "GUARDAR"):
    assert forbidden not in INTEL_SCREEN, f"Removed Intel control returned: {forbidden}"
assert 'label("AMPLIAR", "INSPECT")' in INTEL_SCREEN
assert '"UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT", "UNKNOWN"' in INTEL_SCREEN
assert 'case "UNKNOWN" -> "UNK";' in INTEL_PRESENTATION
assert 'case "UNKNOWN" -> 0xFF9AA4AB;' in INTEL_PRESENTATION
assert 'Set.of("UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT", "UNKNOWN")' in INTEL_CATALOG

# Source-backed DVN records and unresolved UNKNOWN records survive the rebuild.
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

# Profiles still have one authoritative specification.
for profile in ("CINEMATIC", "TACTICAL", "PERFORMANCE", "CALM", "READING", "CUSTOM"):
    assert profile in PROFILE, f"Missing client profile {profile}"
assert "class SiegeProfileSpec" in PROFILE_SPEC and "public record Spec(" in PROFILE_SPEC
assert "return SiegeProfileSpec.distance(profile);" in METRICS
assert "SiegeProfileSpec.apply(profile);" in PROFILE
assert "private static final int FIELDS = 22;" in PROFILE_SPEC
for field in ("autoContrast", "scanlineIntensity", "interferenceIntensity"):
    assert field in PROFILE_SPEC, f"Profile field missing: {field}"

# SIEGE 2.0 navigation has distinct information domains instead of the old GUIDE bucket.
assert "class SiegeNavigationModel" in NAVIGATION and "enum Section" in NAVIGATION
for section in ("HOME", "DEPLOYMENT", "INTEL", "REFERENCE", "FIELD_MANUAL", "SETTINGS", "COMMAND",
                "DIAGNOSTICS", "BACKGROUNDS", "INSPECTOR", "MEDIA", "NATIVE"):
    assert section in NAVIGATION, f"Missing 2.0 navigation identity {section}"
assert "GUIDE," not in NAVIGATION
for code in ('"AUD"', '"VID"', '"CTL"', '"MSE"', '"ACC"', '"LNG"', '"PAK"', '"WRD"'):
    assert code in NAVIGATION, f"Missing native identity {code}"
assert '"DOSSIERS INTEL"' in NAVIGATION
assert '"MANUAL DE CAMPO"' in NAVIGATION
assert '"ARCHIVO / ARSENAL"' in NAVIGATION
assert '"EVIDENCIA"' in NAVIGATION

# The cryptic readiness percentage is intentionally gone from ordinary chrome.
assert "class SiegeScreenChrome" in SCREEN_CHROME
assert "SiegeRuntimeStatus.readiness()" not in SCREEN_CHROME
assert "readiness() + \"%\"" not in SCREEN_CHROME
assert "SiegeRuntimeStatus.healthLabel" in SCREEN_CHROME
assert "SiegeRuntimeStatus.version()" in SCREEN_CHROME
assert "renderSceneTag" in SCREEN_CHROME
assert "SiegeCommandStrip.render(screen, g);" in SCREEN_CHROME
assert "rotationRemainingMs" in SCREEN_CHROME
assert "screen.height - 33" in COMMAND_STRIP
assert "SiegeBackgrounds.sceneTag" in COMMAND_STRIP and "SiegeBackgrounds.name" in COMMAND_STRIP
# Readiness may remain an internal diagnostic metric, but it must not leak into normal navigation.
assert "public static int readiness()" in DIAGNOSTIC
assert "porcentajes crípticos" in ARCHITECTURE_200
assert "readiness" in CHANGELOG_200.lower()

# Archive, Armory, Field Manual and media are separate, explicit routes.
assert "enum Mode { ARCHIVE, ARMORY }" in GUIDE
assert "if (mode == Mode.ARMORY) return List.of(SiegeGuideData.Category.ITEMS);" in GUIDE
assert "SiegeGuideData.Category.LORE" in GUIDE and "SiegeGuideData.Category.INSPIRATIONS" in GUIDE
assert "new SiegeArchiveScreen(this)" in GUIDE
assert '"third-justice"' in GUIDE_SUPPLEMENTAL
assert "SiegeGuideData.Category.ITEMS" in GUIDE_SUPPLEMENTAL
assert "SiegeEvidenceReelScreen" in GUIDE and "class SiegeEvidenceReelScreen" in EVIDENCE
assert "third_justice_tooltip.png" in GUIDE_SUPPLEMENTAL
assert "third_justice_field.png" in GUIDE_SUPPLEMENTAL
for reel in ("third_justice_reel_01.png", "third_justice_reel_02.png", "third_justice_reel_03.png"):
    assert reel in GUIDE_SUPPLEMENTAL
assert "0,1 s" in GUIDE_SUPPLEMENTAL and "15%" in GUIDE_SUPPLEMENTAL and "doble" in GUIDE_SUPPLEMENTAL
assert "ImageIO.read" in GUIDE_MEDIA_TEST and "exactly three curated frames" in GUIDE_MEDIA_TEST
assert "Manual de campo" in ARCHITECTURE_200 and "Arsenal" in ARCHITECTURE_200 and "Intel · Dossiers" in ARCHITECTURE_200

# Settings keep the seven requested domains and shared chrome stays centralized.
for section in ("APPEARANCE", "MOTION", "AUDIO", "INTEL", "ACCESSIBILITY", "BACKGROUNDS", "SYSTEM"):
    assert section in SETTINGS, f"Missing settings domain {section}"
assert "settingsSectionColumns" in UI_LAYOUT
assert "draggingScrollbar" in SETTINGS and "scrollTo(" in SETTINGS
assert "SiegeConfig.autoContrast" in SETTINGS
assert "SiegeConfig.scanlineIntensity" in SETTINGS
assert "SiegeConfig.interferenceIntensity" in SETTINGS
assert "SiegeScreenChrome.renderOverlay(screen, g);" in EVENTS
assert "s instanceof SiegeDiagnosticsScreen" in EVENTS
assert EVENTS.count("SiegeVanillaChrome.renderOverlay(screen, g);") == 1
assert CHROME.count('g.drawCenteredString(font, center, width / 2, 6, titleColor);') == 1

# Command Center/Diagnostics retain explicit safe-repair semantics without becoming lore surfaces.
assert "CENTRO DE COMANDO" in NAVIGATION and "COMMAND CENTER" in NAVIGATION
assert "priorityCard" in SYSTEM and "SiegeDiagnosticReport.operational" in SYSTEM
assert "class SiegeDiagnosticReport" in DIAGNOSTIC
assert "enum Recovery" in DIAGNOSTIC and "public static boolean repair(Entry entry)" in DIAGNOSTIC
for priority in ("CRÍTICO", "ATENCIÓN", "OPERATIVO"):
    assert priority in DIAGNOSTIC or priority in SYSTEM
assert "impact" in DIAGNOSTIC and "recommendation" in DIAGNOSTIC
assert "REPAIR SELECTED" in DIAGNOSTICS_SCREEN and "renderSelectedDetail" in DIAGNOSTICS_SCREEN
assert "Conocimiento del mundo" in ARCHITECTURE_200

# Scene metadata, anomaly rarity, shuffled bags and resource QA remain authoritative.
assert "class SiegeSceneCatalog" in SCENE_CATALOG and "record Scene" in SCENE_CATALOG
assert "STANDARD, FEATURED, ANOMALY" in SCENE_CATALOG
assert '"tempest_jutcherson"' in SCENE_CATALOG and "comfortEligible" in SCENE_CATALOG
assert '"rooftop_squad"' in SCENE_CATALOG
assert "STANDARD_INDICES" in SCENE_CATALOG and "standardCount()" in SCENE_CATALOG and "standardIndex(int ordinal)" in SCENE_CATALOG
assert "SiegeSceneCatalog.count()" in SCENE_SCHEDULE
assert "SiegeSceneCatalog.anomalyIndex()" in SCENE_SCHEDULE
assert "SiegeSceneCatalog.featuredIndex()" in SCENE_SCHEDULE
assert "standardIndex(long slot)" in SCENE_SCHEDULE and "permutationStep" in SCENE_SCHEDULE
assert "SiegeSceneCatalog.darknessBias" in BACKGROUND
assert "SiegeSceneCatalog.label" in BACKGROUND
assert "sceneTag" in BACKGROUND and "isAnomaly" in BACKGROUND and "isFeatured" in BACKGROUND
assert "effectiveBackgroundDarkness(int sceneIndex)" in BACKGROUND
assert "Files.walk(gui)" in GUI_RESOURCE_TEST
assert "ImageIO.read" in GUI_RESOURCE_TEST
assert "Scene metadata mismatch" in GUI_RESOURCE_TEST
assert "Corrupt GUI image" in GUI_RESOURCE_TEST
assert "GuiResourceRegressionTest" in WORKFLOW
assert "SiegeSceneCatalog.java" in WORKFLOW
assert "NavigationIdentityTest" in WORKFLOW

# Adaptive visuals and semantic audio remain intact.
assert "effectiveBackgroundDarkness" in BACKGROUND and "effectivePanelDarkness" in BACKGROUND
assert "SiegeConfig.scanlineIntensity" in BACKGROUND
for sound in ("selection()", "dossier()", "category()", "warning()", "error()"):
    assert sound in UI_SOUNDS, f"Semantic UI sound missing: {sound}"

# Deployment remains vanilla-authoritative and keeps the official destination.
assert "class SiegeDeploymentStatus" in DEPLOYMENT
for state in ("QUERYING", "OFFLINE", "NO_RESPONSE", "INCOMPATIBLE", "ONLINE"):
    assert state in DEPLOYMENT, f"Missing deployment state {state}"
assert "routeStep" in DEPLOYMENT and "compatibilityLabel" in DEPLOYMENT and "latencyBand" in DEPLOYMENT
assert "SiegeDeploymentStatus.state" in MULTIPLAYER
assert "CONTROL DE DESPLIEGUE" in MULTIPLAYER and "DEPLOYMENT CONTROL" in MULTIPLAYER
assert "DESTINO → ESTADO → CONECTAR" in MULTIPLAYER
assert "DESPLIEGUE 0.70" not in MULTIPLAYER and "DEPLOYMENT 0.70" not in MULTIPLAYER
assert "SiegeRuntimeStatus.version()" in MULTIPLAYER
assert "SiegeLacontinuacion.exaroton.me:18736" in MULTIPLAYER
assert "original.onPress()" in MULTIPLAYER, "Vanilla callbacks must remain authoritative"
assert "setScrollAmount" in MULTIPLAYER and "restoreAddress" in MULTIPLAYER

# Older image-recovery gates are strengthened, never replaced.
assert "siete PNG de placeholder" in CHANGELOG_075
assert "ImageIO" in CHANGELOG_075
assert "ImageIO.read" in RUNTIME_TEST
assert "Corrupt image" in RUNTIME_TEST
assert 'entry.category().equals("BOSS")' in RUNTIME_TEST and 'frame < 6' in RUNTIME_TEST
assert "Image below Intel minimum resolution" in RUNTIME_TEST
assert "Intel image must remain 16:9" in RUNTIME_TEST
assert "Boss animation frame dimensions changed" in RUNTIME_TEST

# Singleplayer stays deliberately hidden and no broad shortcut layer is introduced.
assert "GLFW_KEY_S && Screen.hasControlDown()" in TITLE
for source_name, source in (("profile", PROFILE), ("profile_spec", PROFILE_SPEC), ("metrics", METRICS),
                            ("diagnostic", DIAGNOSTIC), ("diagnostics", DIAGNOSTICS_SCREEN),
                            ("settings", SETTINGS), ("navigation", NAVIGATION), ("scene_catalog", SCENE_CATALOG)):
    assert "GLFW_KEY_" not in source, f"Unrequested keyboard shortcut added in {source_name}"

print("SIEGE durable release contracts through 2.25 command and scene overhaul passed")
