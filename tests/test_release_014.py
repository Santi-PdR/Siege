#!/usr/bin/env python3
"""Release contract for Atlas and the fifty user-facing improvements."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
CHANGELOG = (ROOT / "docs/CHANGELOG-0.14.0.md").read_text(encoding="utf-8")
INTEL = (ROOT / "src/main/java/uy/santipdr/siege/client/IntelData.java").read_text(encoding="utf-8")
SCREEN = (ROOT / "src/main/java/uy/santipdr/siege/client/IntelScreenV3.java").read_text(encoding="utf-8")

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
print("SIEGE 0.14.0 release contract and 50-item changelog passed")
