#!/usr/bin/env python3
"""Durable release contracts from Atlas through the SIEGE 0.50 command-center jump."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
CHANGELOG = (ROOT / "docs/CHANGELOG-0.14.0.md").read_text(encoding="utf-8")
CHANGELOG_050 = (ROOT / "docs/CHANGELOG-0.50.0.md").read_text(encoding="utf-8")
INTEL = (ROOT / "src/main/java/uy/santipdr/siege/client/IntelData.java").read_text(encoding="utf-8")
SCREEN = (ROOT / "src/main/java/uy/santipdr/siege/client/IntelScreenV3.java").read_text(encoding="utf-8")
EVENTS = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeMenuThemeEvents.java").read_text(encoding="utf-8")
CHROME = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeVanillaChrome.java").read_text(encoding="utf-8")
SYSTEM = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeSystemScreen.java").read_text(encoding="utf-8")
PROFILE = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeClientProfile.java").read_text(encoding="utf-8")
RUNTIME = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeRuntimeStatus.java").read_text(encoding="utf-8")
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

# Native screens must have one authoritative SIEGE header.
assert "renderCleanNativeHeader" not in EVENTS, "Duplicate native-header pass returned"
assert EVENTS.count("SiegeVanillaChrome.renderOverlay(screen, g);") == 1
assert 'String familyText = "SIEGE // " + familyLabel(family);' not in CHROME
assert CHROME.count('g.drawCenteredString(font, center, width / 2, 6, titleColor);') == 1

# 0.50 is a major client-control release, not a version-only bump.
assert "version = '0.50.0'" in BUILD
numbers_050 = [int(value) for value in re.findall(r"(?m)^(\d+)\. ", CHANGELOG_050)]
assert numbers_050 == list(range(1, 51)), f"Expected 0.50 improvements 1..50, found {numbers_050}"
for profile in ("CINEMATIC", "TACTICAL", "PERFORMANCE", "CALM", "READING", "CUSTOM"):
    assert profile in PROFILE, f"Missing 0.50 profile {profile}"
for setting in ("animatedBackgrounds", "animatedIntel", "titleInterference", "autoRotateIntel", "backgroundDarkness", "panelDarkness"):
    assert setting in PROFILE, f"Profiles do not coordinate {setting}"
assert "public static Profile detect()" in PROFILE
assert "public static void apply(Profile profile)" in PROFILE
assert "readiness()" in RUNTIME and "warnings(boolean spanish)" in RUNTIME
assert 'IntelCatalog.count("SUPER-UNIT")' in RUNTIME
assert 'SiegeCommandStrip.render(screen, g);' in EVENTS
assert 'instanceof SiegeTitleScreen' in STRIP and 'instanceof SiegeSettingsScreen' in STRIP
assert 'screen.width < (title ? 650 : 520)' in STRIP, "Command strip must stay out of cramped layouts"
assert "CENTRO DE COMANDO" in SYSTEM and "COMMAND CENTER" in SYSTEM
assert "profileButtons" in SYSTEM and "applyProfile" in SYSTEM
assert "SiegeRuntimeStatus.readiness()" in SYSTEM
assert "SiegeRuntimeStatus.warnings(spanish())" in SYSTEM
assert "0.40 SYSTEM SETTINGS" not in SYSTEM
assert '"SISTEMA 0.40"' not in EVENTS and '"SYSTEM 0.40"' not in EVENTS
assert "historial SIEGE" not in EVENTS
assert "operational protocols" in EVENTS
assert "SiegeClientProfile.apply(SiegeClientProfile.Profile.CALM)" in CONFIG
assert "SiegeClientProfile.apply(SiegeClientProfile.Profile.READING)" in CONFIG

# The release must remain free of newly invented menu hotkeys.
for source_name, source in (("profile", PROFILE), ("runtime", RUNTIME), ("strip", STRIP), ("system", SYSTEM)):
    assert "GLFW_KEY_" not in source, f"Unrequested keyboard shortcut added in {source_name}"

print("SIEGE release contracts through 0.50 command center passed")
