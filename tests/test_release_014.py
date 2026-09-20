#!/usr/bin/env python3
"""Durable release contracts from Atlas through the SIEGE 0.60 diagnostics release."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
CHANGELOG = (ROOT / "docs/CHANGELOG-0.14.0.md").read_text(encoding="utf-8")
CHANGELOG_050 = (ROOT / "docs/CHANGELOG-0.50.0.md").read_text(encoding="utf-8")
CHANGELOG_060 = (ROOT / "docs/CHANGELOG-0.60.0.md").read_text(encoding="utf-8")
INTEL = (ROOT / "src/main/java/uy/santipdr/siege/client/IntelData.java").read_text(encoding="utf-8")
SCREEN = (ROOT / "src/main/java/uy/santipdr/siege/client/IntelScreenV3.java").read_text(encoding="utf-8")
EVENTS = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeMenuThemeEvents.java").read_text(encoding="utf-8")
CHROME = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeVanillaChrome.java").read_text(encoding="utf-8")
SYSTEM = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeSystemScreen.java").read_text(encoding="utf-8")
PROFILE = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeClientProfile.java").read_text(encoding="utf-8")
METRICS = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeProfileMetrics.java").read_text(encoding="utf-8")
RUNTIME = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeRuntimeStatus.java").read_text(encoding="utf-8")
DIAGNOSTIC = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeDiagnosticReport.java").read_text(encoding="utf-8")
DIAGNOSTICS_SCREEN = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeDiagnosticsScreen.java").read_text(encoding="utf-8")
STRIP = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeCommandStrip.java").read_text(encoding="utf-8")
CONFIG = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeConfig.java").read_text(encoding="utf-8")
BUILD = (ROOT / "build.gradle").read_text(encoding="utf-8")

numbers = [int(value) for value in re.findall(r"(?m)^(\d+)\. ", CHANGELOG)]
assert numbers == list(range(1, 51)), f"Expected 0.14 improvements 1..50, found {numbers}"
assert 'file("SUP-001", "ATLAS", "SUPER-UNIT", 5, "125,000,000", "atlas"' in INTEL
assert "Atlas está clasificado como Super Unit" in INTEL
assert "No se recuperaron datos verificados" in INTEL
assert "125.000.000 HP" in CHANGELOG
assert "private static final List<String> CATEGORIES" in SCREEN
assert '"UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT"' in SCREEN
for forbidden in ("FAVORITES", "toggleFavoriteIntel", "favoriteButton", "addIndexButton", "copyText", "GUARDAR"):
    assert forbidden not in SCREEN, f"Removed Intel control returned: {forbidden}"
assert 'label("AMPLIAR", "INSPECT")' in SCREEN
assert 'label("AMPLIAR ↗", "INSPECT ↗")' not in SCREEN

# Native screens retain one authoritative SIEGE header.
assert "renderCleanNativeHeader" not in EVENTS, "Duplicate native-header pass returned"
assert EVENTS.count("SiegeVanillaChrome.renderOverlay(screen, g);") == 1
assert 'String familyText = "SIEGE // " + familyLabel(family);' not in CHROME
assert CHROME.count('g.drawCenteredString(font, center, width / 2, 6, titleColor);') == 1

# 0.50 remains a real historical release contract even after advancing the build.
numbers_050 = [int(value) for value in re.findall(r"(?m)^(\d+)\. ", CHANGELOG_050)]
assert numbers_050 == list(range(1, 51)), f"Expected 0.50 improvements 1..50, found {numbers_050}"
for profile in ("CINEMATIC", "TACTICAL", "PERFORMANCE", "CALM", "READING", "CUSTOM"):
    assert profile in PROFILE, f"Missing client profile {profile}"
assert "public static Profile detect()" in PROFILE
assert "public static void apply(Profile profile)" in PROFILE
assert 'SiegeCommandStrip.render(screen, g);' in EVENTS
assert 'instanceof SiegeTitleScreen' in STRIP and 'instanceof SiegeSettingsScreen' in STRIP
assert 'screen.width < (title ? 650 : 520)' in STRIP
assert "CENTRO DE COMANDO" in SYSTEM and "COMMAND CENTER" in SYSTEM
assert "profileButtons" in SYSTEM and "applyProfile" in SYSTEM
assert "SiegeClientProfile.Profile.CALM" not in CONFIG, "SiegeConfig must remain standalone-test safe"
assert "SiegeClientProfile.Profile.READING" not in CONFIG, "SiegeConfig must remain standalone-test safe"

# 0.60 must be a complete diagnostics/recovery release, not a number-only bump.
assert "version = '0.60.0'" in BUILD
numbers_060 = [int(value) for value in re.findall(r"(?m)^(\d+)\. ", CHANGELOG_060)]
assert numbers_060 == list(range(1, 61)), f"Expected 0.60 improvements 1..60, found {numbers_060}"
assert "Diagnostics & Recovery" in CHANGELOG_060
assert "class SiegeDiagnosticReport" in DIAGNOSTIC
assert "enum Severity { OK, NOTICE, WARNING, ERROR }" in DIAGNOSTIC
for code in ('"CFG"', '"MUS"', '"SFX"', '"VIS"', '"GPU"', '"PRF"', '"INT"', '"BG"', '"ACC"'):
    assert code in DIAGNOSTIC, f"Missing diagnostic subsystem {code}"
assert "SiegeProfileMetrics.nearest()" in DIAGNOSTIC
assert "SiegeProfileMetrics.fitPercent(nearest)" in DIAGNOSTIC
assert "SiegeProfileMetrics.fieldCount()" in DIAGNOSTIC
assert "public static int readiness()" in DIAGNOSTIC
assert "ERROR -> 40" in DIAGNOSTIC and "WARNING -> 12" in DIAGNOSTIC

assert "class SiegeProfileMetrics" in METRICS
assert "private static final int FIELDS = 21" in METRICS
assert "public static SiegeClientProfile.Profile nearest()" in METRICS
assert "public static int distance(" in METRICS
assert "public static int fitPercent(" in METRICS
assert "public static int fieldCount()" in METRICS
for preset in ("CINEMATIC", "TACTICAL", "PERFORMANCE", "CALM", "READING"):
    assert f"Profile.{preset}" in METRICS, f"0.60 metrics missing {preset}"

assert "class SiegeDiagnosticsScreen" in DIAGNOSTICS_SCREEN
assert "DIAGNÓSTICO Y RECUPERACIÓN" in DIAGNOSTICS_SCREEN
assert "DIAGNOSTICS & RECOVERY" in DIAGNOSTICS_SCREEN
assert "alignNearest()" in DIAGNOSTICS_SCREEN
assert "SiegeClientProfile.apply(nearest)" in DIAGNOSTICS_SCREEN
assert "SiegeDiagnosticReport.entries(spanish())" in DIAGNOSTICS_SCREEN
assert "SiegeDiagnosticReport.readiness()" in DIAGNOSTICS_SCREEN

assert "new SiegeDiagnosticsScreen(this)" in SYSTEM
assert "DIAGNÓSTICO Y RECUPERACIÓN" in SYSTEM
assert "SiegeRuntimeStatus.profileFitLabel(spanish())" in SYSTEM
assert "SiegeDiagnosticReport.readiness()" in RUNTIME
assert "SiegeDiagnosticReport.entries(spanish)" in RUNTIME
assert "SiegeProfileMetrics.fitPercent(nearest)" in RUNTIME
assert "CUSTOM→" in STRIP and "SiegeProfileMetrics.fitPercent(nearest)" in STRIP

# Stale 0.40 wording and invented shortcuts remain prohibited.
assert "0.40 SYSTEM SETTINGS" not in SYSTEM
assert '"SISTEMA 0.40"' not in EVENTS and '"SYSTEM 0.40"' not in EVENTS
assert "historial SIEGE" not in EVENTS
assert "operational protocols" in EVENTS
for source_name, source in (
        ("profile", PROFILE), ("metrics", METRICS), ("runtime", RUNTIME),
        ("diagnostic", DIAGNOSTIC), ("diagnostics-screen", DIAGNOSTICS_SCREEN),
        ("strip", STRIP), ("system", SYSTEM)):
    assert "GLFW_KEY_" not in source, f"Unrequested keyboard shortcut added in {source_name}"

print("SIEGE release contracts through 0.60 diagnostics and recovery passed")
