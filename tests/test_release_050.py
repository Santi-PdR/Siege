#!/usr/bin/env python3
"""SIEGE 5.00 major-jump contracts."""
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
PROGRESSION = read("src/main/java/uy/santipdr/siege/client/SiegeProgressionData.java")
PROFILES = read("src/main/java/uy/santipdr/siege/client/SiegeClientProfile.java")
PROFILE_SPEC = read("src/main/java/uy/santipdr/siege/client/SiegeProfileSpec.java")
TITLE = read("src/main/java/uy/santipdr/siege/client/SiegeTitleScreen.java")
MULTI = read("src/main/java/uy/santipdr/siege/client/SiegeMultiplayerScreen.java")
SCENES = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
EASTER = read("src/main/java/uy/santipdr/siege/client/SiegeEasterEggVault.java")
CHANGELOG = read("docs/CHANGELOG-5.0.0.md")
MEDIA_DOC = read("docs/DVN-MEDIA-CANDIDATES-5.0.md")

assert "version = '5.00.0'" in BUILD
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

# Briefing is a newcomer path, not a second encyclopedia.
for required in ("server-overview", "newcomer-operational-rule", "race-system", "abilities-experience",
                 "progression-v1-v4", "trials-basics", "executors-basics", "bosses-basics", "death-revive-current"):
    assert f'"{required}"' in BRIEF
for removed in ("progression-mobility-priority", "dimensions-basics", "economy-basics", "prompt-precision-framework"):
    assert removed not in BRIEF
assert "PRIMEROS PASOS" in BRIEF

# Atlas/Encyclopedia expose current information in natural categories; no historical/editorial tab or generic see-also.
assert "enum View { RACES, PROGRESSION, SYSTEMS, ITEMS }" in ATLAS_INDEX
assert "RESEARCH" not in ATLAS_INDEX and "BRIEFING" not in ATLAS_INDEX
assert "Mode { START, RACES, PROGRESSION, SYSTEMS, ITEMS }" in ENC
for surface in (ATLAS, ENC, DETAIL):
    assert "TAMBIÉN VER" not in surface and "SEE ALSO" not in surface
assert "SiegeKnowledgeExpansion50.entries" in REG
assert "maintenanceEntries" in REG
assert "Zone.SERVER" in REG

# Current recipes/rules override stale information in normal UI.
assert '"item-defibrillator"' in EXP50
assert "3 bloques de hierro + 1 mesa de encantamientos" in EXP50
assert "No se muestran recetas anteriores" in EXP50
assert '"death-revive-current"' in EXP50
assert "RCP dejó de ser" in EXP50
assert '"item-geography-table"' in EXP50
assert "investigar sus propiedades" in EXP50
assert "120 wins" not in EXP50 and "220 wins" not in EXP50

# Race-specific progression is explicit instead of pretending everything is V1→V4.
for key in ('"race-saiyan"', '"saiyan-transformations"', '"race-ghoul"', '"ghoul-progression"',
            '"race-subhuman"', '"subhuman-adamantium-human"', '"subhuman-sorcerer"', '"subhuman-evil-morty"'):
    assert key in EXP50
for track in ('"core"', '"v1v4"', '"special"', '"trials"'):
    assert track in PROGRESSION
assert "DEPENDE DE LA RAZA" in PROGRESSION
assert "Trial Spire" in PROGRESSION and "Witch Trials" in PROGRESSION

# Media Room 5.0 remains a real audiovisual surface.
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

print("SIEGE 5.00 navigation, natural-language knowledge, freshness and audiovisual contracts passed")
