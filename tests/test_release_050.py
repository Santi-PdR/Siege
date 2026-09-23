#!/usr/bin/env python3
"""Durable SIEGE 5.00 generation contracts kept across later 5.x releases."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
WORKFLOW = read(".github/workflows/build.yml")
MEDIA_DATA = read("src/main/java/uy/santipdr/siege/client/SiegeMediaReferenceData.java")
MEDIA_ROOM = read("src/main/java/uy/santipdr/siege/client/SiegeMediaRoomScreen.java")
COMMAND = read("src/main/java/uy/santipdr/siege/client/SiegeCommandNetwork.java")
HUB = read("src/main/java/uy/santipdr/siege/client/SiegeOperationsHubScreen.java")
OPS = read("src/main/java/uy/santipdr/siege/client/SiegeOperationsIndex.java")
BRIEF = read("src/main/java/uy/santipdr/siege/client/SiegeBriefingScreen.java")
ATLAS_INDEX = read("src/main/java/uy/santipdr/siege/client/SiegeAtlasIndex.java")
ATLAS = read("src/main/java/uy/santipdr/siege/client/SiegeAtlasScreen.java")
ENC = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeScreen.java")
DETAIL = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeFileScreen.java")
REG = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeRegistry.java")
EXP50 = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeExpansion50.java")
CORPUS50 = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeCorpus50.java")
RACES = read("src/main/java/uy/santipdr/siege/client/SiegeRaceAtlasData.java")
RACE_VARIANTS = read("src/main/java/uy/santipdr/siege/client/SiegeRaceVariantsScreen.java")
PROGRESSION = read("src/main/java/uy/santipdr/siege/client/SiegeProgressionData.java")
PROFILES = read("src/main/java/uy/santipdr/siege/client/SiegeClientProfile.java")
PROFILE_SPEC = read("src/main/java/uy/santipdr/siege/client/SiegeProfileSpec.java")
UI_SOUNDS = read("src/main/java/uy/santipdr/siege/client/SiegeUiSounds.java")
MUSIC_PREP = read("scripts/prepare-music.sh")
TITLE = read("src/main/java/uy/santipdr/siege/client/SiegeTitleScreen.java")
MULTI = read("src/main/java/uy/santipdr/siege/client/SiegeMultiplayerScreen.java")
SCENES = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
EASTER = read("src/main/java/uy/santipdr/siege/client/SiegeEasterEggVault.java")
CHANGELOG = read("docs/CHANGELOG-5.0.0.md")
MEDIA_DOC = read("docs/DVN-MEDIA-CANDIDATES-5.0.md")

assert "version = '5." in BUILD
assert "SIEGE 5.00.0" in CHANGELOG

# Operations is intentionally smaller than 4.00.
assert "enum Lane { DEPLOYMENT, INTELLIGENCE, REFERENCE }" in COMMAND
assert "hasNoDuplicateVisibleRoutes" in COMMAND
for hidden in ("Route.ARCHIVE", "Route.ARMORY", "Route.DIAGNOSTICS", "Route.COMMAND", "Route.SETTINGS", "Route.BACKGROUNDS"):
    assert hidden not in COMMAND
for visible in ("Route.BRIEFING", "Route.DEPLOYMENT", "Route.THREATS", "Route.INTEL", "Route.RACES",
                "Route.PROGRESSION", "Route.ATLAS", "Route.KNOWLEDGE", "Route.FIELD_MANUAL", "Route.MEDIA"):
    assert visible in COMMAND
assert "SiegeCommandNetwork.isVisible(route)" in HUB
assert 'Component.literal("SIEGE // OPERATIONS")' in HUB
assert "CLIENTE " not in HUB and "profileFitLabel" not in HUB
assert "Kind.ARMORY" not in OPS
assert "confidence().label" not in OPS

# Briefing remains a newcomer path, not a second encyclopedia. The exact title may
# evolve across later 5.x releases; the durable contract is the guided sequence and
# a clearly identified briefing/first-steps surface.
for required in ("server-overview", "newcomer-operational-rule", "race-system", "abilities-experience",
                 "progression-v1-v4", "trials-basics", "executors-basics", "bosses-basics", "death-revive-current"):
    assert f'"{required}"' in BRIEF
for removed in ("progression-mobility-priority", "dimensions-basics", "economy-basics", "prompt-precision-framework"):
    assert removed not in BRIEF
assert "PRIMEROS PASOS" in BRIEF or "BRIEFING 5.40" in BRIEF
assert 'Component.literal("SIEGE // BRIEFING")' in BRIEF

# Atlas/Encyclopedia expose current information in natural categories; no historical/editorial tab or generic see-also.
assert "enum View { RACES, PROGRESSION, SYSTEMS, ITEMS }" in ATLAS_INDEX
assert "RESEARCH" not in ATLAS_INDEX and "BRIEFING" not in ATLAS_INDEX
assert "Mode { START, RACES, PROGRESSION, SYSTEMS, ITEMS }" in ENC
for surface in (ATLAS, ENC, DETAIL):
    assert "TAMBIÉN VER" not in surface and "SEE ALSO" not in surface
assert "SiegeKnowledgeExpansion50.entries" in REG
assert "SiegeKnowledgeCorpus50.entries" in REG
assert REG.index("SiegeKnowledgeExpansion50.entries") < REG.index("SiegeKnowledgeCorpus50.entries")
assert "maintenanceEntries" in REG
assert "Zone.SERVER" in REG

# Current rules/recipes introduced by 5.00 remain present.
assert '"death-revive-current"' in CORPUS50
assert "RCP dejó de ser" in CORPUS50
assert '"item-defibrillator"' in CORPUS50
assert "3 bloques de hierro + 1 bloque de oro" in CORPUS50
assert '"item-medkit"' in CORPUS50
assert "3 bloques de hierro + 1 mesa de encantamientos" in CORPUS50
assert "La receta anterior no se muestra" in CORPUS50
assert '"item-daemonium-kit"' in CORPUS50
assert '"assembling-table"' in CORPUS50
assert '"respawn-cards"' in CORPUS50
assert '"meditation-levels"' in CORPUS50
assert '"ability-stamina"' in CORPUS50
assert '"race-spins"' in CORPUS50
assert "120 wins" not in EXP50 and "220 wins" not in EXP50
assert "120 wins" not in CORPUS50 and "220 wins" not in CORPUS50

# Full-corpus pass expands the Race Atlas without making up missing mechanics.
for race in ("Mink", "Tsufurujin", "Otsutsuki", "Cold Demon", "Lunarian", "Diclonius",
             "Void Master", "SOBRINO", "Oni", "Iluminati", "Fullbringer", "Arrancar", "Hakaishin"):
    assert f'"{race}"' in RACES
assert RACES.count("unknown(") >= 20
assert '"hacker"' in RACES and '"subhuman"' in RACES and '"saiyan"' in RACES and '"ghoul"' in RACES
assert '"subhuman-rick-sanchez"' in CORPUS50
assert '"subhuman-rick-sanchez"' in RACE_VARIANTS
assert '"executor-maze"' in CORPUS50
assert '"executor-nearby-warning"' in CORPUS50
assert '"trial-shrine-global"' in CORPUS50
assert '"trial-third-justice"' in CORPUS50
assert '"ability-room"' in CORPUS50 and '"ability-gate"' in CORPUS50

# Race-specific progression remains explicit instead of pretending everything is V1→V4.
for key in ('"race-saiyan"', '"saiyan-transformations"', '"race-ghoul"', '"ghoul-progression"',
            '"race-subhuman"', '"subhuman-adamantium-human"', '"subhuman-sorcerer"', '"subhuman-evil-morty"'):
    assert key in EXP50
for track in ('"core"', '"v1v4"', '"special"', '"trials"'):
    assert track in PROGRESSION
assert "DEPENDE DE LA RAZA" in PROGRESSION
assert "Trial Spire" in PROGRESSION and "Witch Trials" in PROGRESSION

# Audio keeps clean headroom and UI samples stay at authored pitch.
assert 'HEADROOM_DB="-3dB"' in MUSIC_PREP
assert 'OUTPUT_RATE="44100"' in MUSIC_PREP
assert "insufficient decoded headroom" in MUSIC_PREP
assert "SimpleSoundInstance.forUI(event, 1.0F, volume)" in UI_SOUNDS
for shifted in ("0.72F, 0.82F", "0.84F, 0.76F", "1.08F, 0.58F", "0.92F, 0.64F"):
    assert shifted not in UI_SOUNDS

# Media Room remains a real audiovisual surface.
assert "Mode { BUNDLED, MOODS, DVN_AUDIO, VISUALS }" in MEDIA_ROOM
assert "renderMoods" in MEDIA_ROOM and "mouseScrolled" in MEDIA_ROOM
for mood in ('"stronghold"', '"deployment"', '"intel"', '"last-stand"'):
    assert mood in MEDIA_DATA
for visual in ('"stronghold-defense"', '"arctic-standoff"', '"urban-night"', '"city-siege"',
               '"dune-front"', '"industrial-zone"', '"hangar-briefing"', '"wave-defense"',
               '"boss-assault"', '"armory-deployment"'):
    assert visual in MEDIA_DATA
for track in ("Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir"):
    assert track in MEDIA_DATA
assert "768×432" in MEDIA_DOC and "no se hará upscale barato" in MEDIA_DOC.lower() and "1920×1080" in MEDIA_DOC

# Stronghold profile keeps the accessibility intent.
assert "STRONGHOLD" in PROFILES
assert "case STRONGHOLD" in PROFILE_SPEC
assert "SiegeClientProfile.Profile.STRONGHOLD" in PROFILE_SPEC
assert "38, 0, 34, 78" in PROFILE_SPEC

# Major-release invariants remain intact.
assert "SiegeLacontinuacion.exaroton.me:18736" in MULTI
assert "GLFW_KEY_S && Screen.hasControlDown()" in TITLE
assert '"tempest_jutcherson"' in EASTER
assert '"tempest_jutcherson"' not in SCENES
assert "Publish validated jar for installer" in WORKFLOW
assert "python3 tests/test_release_040.py" in WORKFLOW
assert "python3 tests/test_release_050.py" in WORKFLOW

print("SIEGE 5.00 generation contracts still pass on the current 5.x release")
