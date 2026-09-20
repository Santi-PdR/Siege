#!/usr/bin/env python3
"""Release contract for Atlas and durable SIEGE UI invariants."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
CHANGELOG = (ROOT / "docs/CHANGELOG-0.14.0.md").read_text(encoding="utf-8")
INTEL = (ROOT / "src/main/java/uy/santipdr/siege/client/IntelData.java").read_text(encoding="utf-8")
SCREEN = (ROOT / "src/main/java/uy/santipdr/siege/client/IntelScreenV3.java").read_text(encoding="utf-8")
EVENTS = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeMenuThemeEvents.java").read_text(encoding="utf-8")
CHROME = (ROOT / "src/main/java/uy/santipdr/siege/client/SiegeVanillaChrome.java").read_text(encoding="utf-8")
BUILD = (ROOT / "build.gradle").read_text(encoding="utf-8")

numbers = [int(value) for value in re.findall(r"(?m)^(\d+)\. ", CHANGELOG)]
assert numbers == list(range(1, 51)), f"Expected improvements 1..50, found {numbers}"
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

# Native screens must have one authoritative SIEGE header. The older cleanup
# pass redrew the full top bar after SiegeVanillaChrome had already rendered it.
assert "renderCleanNativeHeader" not in EVENTS, "Duplicate native-header pass returned"
assert EVENTS.count("SiegeVanillaChrome.renderOverlay(screen, g);") == 1
assert 'String familyText = "SIEGE // " + familyLabel(family);' not in CHROME
assert CHROME.count('g.drawCenteredString(font, center, width / 2, 6, titleColor);') == 1

# Version labels and Intel wording must follow the active build/current-knowledge
# model instead of freezing an old minor version or presenting a changelog history.
assert "version = '0.40.3'" in BUILD
assert 'String currentVersion = version();' in EVENTS
assert '"SISTEMA 0.40"' not in EVENTS and '"SYSTEM 0.40"' not in EVENTS
assert "historial SIEGE" not in EVENTS
assert "operational protocols" in EVENTS

print("SIEGE release contract, native header and current-knowledge invariants passed")
