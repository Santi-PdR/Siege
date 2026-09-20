#!/usr/bin/env python3
"""Durable release contracts from Atlas through SIEGE 1.25."""
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
UI_LAYOUT = read("src/main/java/uy/santipdr/siege/client/SiegeUiLayout.java")
UI_SOUNDS = read("src/main/java/uy/santipdr/siege/client/SiegeUiSounds.java")
BACKGROUND = read("src/main/java/uy/santipdr/siege/client/SiegeBackgrounds.java")
EVENTS = read("src/main/java/uy/santipdr/siege/client/SiegeMenuThemeEvents.java")
CHROME = read("src/main/java/uy/santipdr/siege/client/SiegeVanillaChrome.java")
BUILD = read("build.gradle")
RUNTIME_TEST = read("tests/RuntimeRegressionTest.java")


def numbered(changelog: str):
    return [int(v) for v in re.findall(r"(?m)^(\d+)\. ", changelog)]


# Historical milestones remain real releases rather than disappearing after bumps.
assert numbered(CHANGELOG_014) == list(range(1, 51))
assert numbered(CHANGELOG_050) == list(range(1, 51))
assert numbered(CHANGELOG_060) == list(range(1, 61))
assert numbered(CHANGELOG_070) == list(range(1, 71))
assert numbered(CHANGELOG_075) == list(range(1, 13))
assert numbered(CHANGELOG_125) == list(range(1, 81))
assert "version = '1.26.0'" in BUILD
assert "version = '1.20.0'" not in BUILD
assert "version = '0.75.0'" not in BUILD

# Atlas and earlier Intel contracts remain intact.
assert 'file("SUP-001", "ATLAS", "SUPER-UNIT", 5, "125,000,000", "atlas"' in INTEL_DATA
assert "No se recuperaron datos verificados" in INTEL_DATA
for forbidden in ("FAVORITES", "toggleFavoriteIntel", "favoriteButton", "addIndexButton", "copyText", "GUARDAR"):
    assert forbidden not in INTEL_SCREEN, f"Removed Intel control returned: {forbidden}"
assert 'label("AMPLIAR", "INSPECT")' in INTEL_SCREEN

# Profiles are now one authoritative specification instead of two drift-prone tables.
for profile in ("CINEMATIC", "TACTICAL", "PERFORMANCE", "CALM", "READING", "CUSTOM"):
    assert profile in PROFILE, f"Missing client profile {profile}"
assert "class SiegeProfileSpec" in PROFILE_SPEC
assert "public record Spec(" in PROFILE_SPEC
assert "return SiegeProfileSpec.distance(profile);" in METRICS
assert "SiegeProfileSpec.apply(profile);" in PROFILE
assert "private static final int FIELDS = 22;" in PROFILE_SPEC
for field in ("autoContrast", "scanlineIntensity", "interferenceIntensity"):
    assert field in PROFILE_SPEC, f"1.25 profile field missing: {field}"

# 1.25 shared navigation/chrome is explicit and covers all major owned surfaces.
assert "class SiegeNavigationModel" in NAVIGATION and "enum Section" in NAVIGATION
for section in ("HOME", "DEPLOYMENT", "INTEL", "GUIDE", "SETTINGS", "COMMAND", "DIAGNOSTICS", "BACKGROUNDS", "INSPECTOR"):
    assert section in NAVIGATION, f"Missing navigation identity {section}"
assert "class SiegeScreenChrome" in SCREEN_CHROME
assert "SiegeScreenChrome.renderOverlay(screen, g);" in EVENTS
assert "s instanceof SiegeDiagnosticsScreen" in EVENTS
assert "renderCleanNativeHeader" not in EVENTS
assert EVENTS.count("SiegeVanillaChrome.renderOverlay(screen, g);") == 1
assert CHROME.count('g.drawCenteredString(font, center, width / 2, 6, titleColor);') == 1

# Settings were rebuilt into the seven requested domains.
for section in ("APPEARANCE", "MOTION", "AUDIO", "INTEL", "ACCESSIBILITY", "BACKGROUNDS", "SYSTEM"):
    assert section in SETTINGS, f"Missing settings domain {section}"
assert "settingsSectionColumns" in UI_LAYOUT
assert "draggingScrollbar" in SETTINGS and "scrollTo(" in SETTINGS
assert "SiegeConfig.autoContrast" in SETTINGS
assert "SiegeConfig.scanlineIntensity" in SETTINGS
assert "SiegeConfig.interferenceIntensity" in SETTINGS

# Command Center and diagnostics 2.0 preserve old capabilities and add operational priorities.
assert "CENTRO DE COMANDO" in NAVIGATION and "COMMAND CENTER" in NAVIGATION
assert "priorityCard" in SYSTEM and "SiegeDiagnosticReport.operational" in SYSTEM
assert "class SiegeDiagnosticReport" in DIAGNOSTIC and "public static int readiness()" in DIAGNOSTIC
assert "enum Recovery" in DIAGNOSTIC and "public static boolean repair(Entry entry)" in DIAGNOSTIC
for priority in ("CRÍTICO", "ATENCIÓN", "OPERATIVO"):
    assert priority in DIAGNOSTIC or priority in SYSTEM
assert "impact" in DIAGNOSTIC and "recommendation" in DIAGNOSTIC
assert "REPAIR SELECTED" in DIAGNOSTICS_SCREEN and "renderSelectedDetail" in DIAGNOSTICS_SCREEN

# Adaptive visual layer and semantic audio are real 1.25 systems.
assert "effectiveBackgroundDarkness" in BACKGROUND and "effectivePanelDarkness" in BACKGROUND
assert "SiegeConfig.scanlineIntensity" in BACKGROUND
for sound in ("selection()", "dossier()", "category()", "warning()", "error()"):
    assert sound in UI_SOUNDS, f"Semantic UI sound missing: {sound}"

# Deployment overhaul remains vanilla-authoritative while growing its status model.
assert "class SiegeDeploymentStatus" in DEPLOYMENT
for state in ("QUERYING", "OFFLINE", "NO_RESPONSE", "INCOMPATIBLE", "ONLINE"):
    assert state in DEPLOYMENT, f"Missing deployment state {state}"
assert "routeStep" in DEPLOYMENT and "compatibilityLabel" in DEPLOYMENT and "latencyBand" in DEPLOYMENT
assert "SiegeDeploymentStatus.state" in MULTIPLAYER
assert "CONTROL DE DESPLIEGUE" in MULTIPLAYER and "DEPLOYMENT CONTROL" in MULTIPLAYER
assert "DESTINO → ESTADO → CONECTAR" in MULTIPLAYER
assert "SiegeLacontinuacion.exaroton.me:18736" in MULTIPLAYER
assert "original.onPress()" in MULTIPLAYER, "Vanilla callbacks must remain authoritative"
assert "setScrollAmount" in MULTIPLAYER and "restoreAddress" in MULTIPLAYER

# UNKNOWN and source-backed DVN unit classifications remain intact.
assert '"UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT", "UNKNOWN"' in INTEL_SCREEN
assert 'case "UNKNOWN" -> "UNK";' in INTEL_PRESENTATION
assert 'case "UNKNOWN" -> 0xFF9AA4AB;' in INTEL_PRESENTATION
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
assert "Khanblades" in INTEL_CURRENT and "Spectral Shotgun" in INTEL_CURRENT and "Hivelink" in INTEL_CURRENT
assert "Dart Rifle" in INTEL_CURRENT and "H94 Rifle" in INTEL_CURRENT
assert "placeholder/classified" in INTEL_CURRENT and "bosses/classified/frame_00" in INTEL_CURRENT
assert "SIN REGISTRO VISUAL" in INTEL_CURRENT and "NO VISUAL RECORD" in INTEL_CURRENT
assert "No se encontró una referencia suficientemente fiable" in INTEL_CURRENT

# Source policy remains conservative.
assert "referencia suficientemente clara" in CHANGELOG_070
assert "Proteus deja de figurar incorrectamente como Elite" in CHANGELOG_070
assert "sigue en desarrollo" in CHANGELOG_070
assert "No se copian renders o screenshots externos" in CHANGELOG_070

# 0.75 image recovery is strengthened, not replaced.
assert "siete PNG de placeholder" in CHANGELOG_075
assert "ImageIO" in CHANGELOG_075
assert "ImageIO.read" in RUNTIME_TEST
assert "Corrupt image" in RUNTIME_TEST
assert 'entry.category().equals("BOSS")' in RUNTIME_TEST and 'frame < 6' in RUNTIME_TEST
assert "Image below Intel minimum resolution" in RUNTIME_TEST
assert "Intel image must remain 16:9" in RUNTIME_TEST
assert "Boss animation frame dimensions changed" in RUNTIME_TEST

# Singleplayer remains deliberately hidden and no broad shortcut layer was added.
TITLE = read("src/main/java/uy/santipdr/siege/client/SiegeTitleScreen.java")
assert "GLFW_KEY_S && Screen.hasControlDown()" in TITLE
for source_name, source in (("profile", PROFILE), ("profile_spec", PROFILE_SPEC), ("metrics", METRICS),
                            ("diagnostic", DIAGNOSTIC), ("diagnostics", DIAGNOSTICS_SCREEN),
                            ("settings", SETTINGS), ("navigation", NAVIGATION)):
    assert "GLFW_KEY_" not in source, f"Unrequested keyboard shortcut added in {source_name}"

print("SIEGE durable release contracts through 1.25 generational overhaul passed")
