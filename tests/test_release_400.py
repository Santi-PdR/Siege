#!/usr/bin/env python3
"""SIEGE 4.00 product/UI contracts."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

build = read("build.gradle")
knowledge = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeScreen.java")
knowledge_data = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeData.java")
recruit = read("src/main/java/uy/santipdr/siege/client/SiegeRecruitBriefingScreen.java")
races = read("src/main/java/uy/santipdr/siege/client/SiegeRaceAtlasData.java")
race_screen = read("src/main/java/uy/santipdr/siege/client/SiegeRaceAtlasScreen.java")
progression = read("src/main/java/uy/santipdr/siege/client/SiegeProgressionData.java")
progression_screen = read("src/main/java/uy/santipdr/siege/client/SiegeProgressionMapScreen.java")
threat = read("src/main/java/uy/santipdr/siege/client/SiegeThreatBoardScreen.java")
media = read("src/main/java/uy/santipdr/siege/client/SiegeMediaRoomScreen.java")
media_data = read("src/main/java/uy/santipdr/siege/client/SiegeMediaReferenceData.java")
menu_events = read("src/main/java/uy/santipdr/siege/client/Siege250MenuEvents.java")
nav = read("src/main/java/uy/santipdr/siege/client/SiegeNavigationModel.java")
armory = read("src/main/java/uy/santipdr/siege/client/SiegeGuideSupplemental.java")
eggs = read("src/main/java/uy/santipdr/siege/client/SiegeEasterEggVault.java")
scene_catalog = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
bg_script = read("scripts/prepare-backgrounds-hd.py")

assert "version = '4.00.0'" in build
for stale in ("version = '3.00.0'", "version = '2.50.0'", "version = '2.25.0'"):
    assert stale not in build

# 4.0 must be a real multi-surface release.
for cls in (
    "SiegeRecruitBriefingScreen", "SiegeRaceAtlasScreen", "SiegeProgressionMapScreen",
    "SiegeThreatBoardScreen", "SiegeMediaRoomScreen", "SiegeKnowledgeScreen"):
    assert f"class {cls}" in read(f"src/main/java/uy/santipdr/siege/client/{cls}.java")

# Main menu enters the simple briefing, not the dense search/control screen.
assert "new SiegeRecruitBriefingScreen(screen)" in menu_events
assert "Briefing, razas, progresión, amenazas" in menu_events

# Player-facing encyclopedia is category-driven and hides provenance plumbing.
assert "Mode { START, RACES, PROGRESSION, SYSTEMS, HISTORY }" in knowledge
for label in ("EMPEZAR", "RAZAS", "PROGRESIÓN", "SISTEMAS", "HISTÓRICO"):
    assert label in knowledge
assert "sourceLine(" not in knowledge
assert "selected.sources()" not in knowledge
assert 'label("FUENTES"' not in knowledge
assert "ABRIR FUENTE" not in knowledge
assert "Raza, rareza, Trial, Executor, reliquia, revive, dimensión" in knowledge

# Encyclopedia remains general/server-wide.
for forbidden in (
    "current-rust-guard", "current-deteriorer-snapshot", "CURRENT_CONFIRMED",
    "PLAYER_EXPERIENCE", "Mi partida actual", "mi inventario", "mi personaje"):
    assert forbidden not in knowledge_data

# Race atlas: complete documented rarity ladder and broad race catalog.
for rarity in ("COMMON", "UNCOMMON", "RARE", "ULTRA_RARE", "LEGENDARY", "OBSAINAN",
               "MYTHIC", "GODLY", "ETERNAL", "FABLED"):
    assert rarity in races
for race in ("Human", "Hacker", "Shark", "Saiyan", "Deteriorer", "Faraón", "Apotheosis",
             "Muerte", "Cyborg", "Ghoul", "Subhuman", "Terrariano", "Kaioshin", "Dragon",
             "Shinigami", "Majin", "Undertale AU"):
    assert f'"{race}"' in races
assert "rarityOrder()" in races
assert "ATLAS DE RAZAS" in race_screen
assert "ABRIR FICHA DE ENCICLOPEDIA" in race_screen

# Progression is richer than a single V1→V4 line.
for track in ("RUTA GENERAL", "V1 → V4", "TRIALS", "RUTAS ESPECIALES", "SISTEMAS AVANZADOS"):
    assert track in progression
for topic in ("Perish Staff V2", "Trial Spire", "Saiyan", "Cyborg", "Hacker", "Assembling", "Reliquias", "Dimensiones"):
    assert topic in progression
assert "ABRIR FUENTE" not in progression_screen
assert "VER INFORMACIÓN" in progression_screen

# Threat board supports compact scrolling rather than silently dropping entries.
assert "mouseScrolled" in threat and "offset" in threat
assert "VER INFORMACIÓN" in threat
assert "ABRIR FUENTE" not in threat

# Media Room is player-facing, with soundtrack and DVN visual direction.
for track in ("Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir"):
    assert track in media_data
for visual in ("Última fortaleza", "Operación urbana nocturna", "Hangar / briefing", "Defensa de oleada"):
    assert visual in media_data
for forbidden in ("NO INCLUIDAS", "no se descargan", "redistribuyen", "licencia", "derechos claros"):
    assert forbidden.lower() not in media.lower()
assert "Tempest Jutcherson" not in media_data

# Arsenal is expanded with important server tools, while Third Justice remains available.
for item in ("Third Justice", "Geography Table", "Daemonium Kit", "Fallen Angel Halo",
             "Improbability Scroll", "Assembling Table", "Aerorig", "Riflator", "HOLO-Watch"):
    assert f'"{item}"' in armory
assert "120 wins" in armory
assert "lifesteal" in armory

# Navigation identities for every new 4.0 surface.
for code in ('"RCT"', '"RCE"', '"PRG"', '"THR"', '"AV"'):
    assert code in nav

# Easter egg isolation survives the large release.
assert "TEMPEST_JUTCHERSON" in eggs
assert '"tempest_jutcherson"' not in scene_catalog
assert "tempest_jutcherson" not in bg_script

print("SIEGE 4.00 recruit, encyclopedia, races, progression, threats, armory and media contracts passed")
