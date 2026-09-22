#!/usr/bin/env python3
"""SIEGE 4.00 durable release contracts."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
read = lambda p: (ROOT / p).read_text(encoding="utf-8")

BUILD = read("build.gradle")
WORKFLOW = read(".github/workflows/build.yml")
OPS = read("src/main/java/uy/santipdr/siege/client/SiegeOperationsIndex.java")
HUB = read("src/main/java/uy/santipdr/siege/client/SiegeOperationsHubScreen.java")
NAV = read("src/main/java/uy/santipdr/siege/client/SiegeNavigationModel.java")
BASE = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeData.java")
EXP = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeExpansion40.java")
REG = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeRegistry.java")
ATLAS = read("src/main/java/uy/santipdr/siege/client/SiegeAtlasScreen.java")
BRIEF = read("src/main/java/uy/santipdr/siege/client/SiegeBriefingScreen.java")
THREATS = read("src/main/java/uy/santipdr/siege/client/SiegeThreatBoardScreen.java")
DETAIL = read("src/main/java/uy/santipdr/siege/client/SiegeKnowledgeFileScreen.java")
TITLE = read("src/main/java/uy/santipdr/siege/client/SiegeTitleScreen.java")
MULTI = read("src/main/java/uy/santipdr/siege/client/SiegeMultiplayerScreen.java")
INTEL = read("src/main/java/uy/santipdr/siege/client/IntelScreenV3.java")
SCENES = read("src/main/java/uy/santipdr/siege/client/SiegeSceneCatalog.java")
EASTER = read("src/main/java/uy/santipdr/siege/client/SiegeEasterEggVault.java")
CHANGELOG = read("docs/CHANGELOG-4.0.0.md")
MEDIA = read("docs/DVN-MEDIA-CANDIDATES-4.0.md")

assert "version = '4.00.0'" in BUILD
assert "SIEGE 4.00" in CHANGELOG
for cls in ("SiegeAtlasScreen", "SiegeBriefingScreen", "SiegeThreatBoardScreen", "SiegeKnowledgeFileScreen"):
    assert f"class {cls}" in read(f"src/main/java/uy/santipdr/siege/client/{cls}.java")
for route in ("BRIEFING", "ATLAS", "THREATS", "DEPLOYMENT", "INTEL", "KNOWLEDGE", "ARCHIVE", "ARMORY",
              "FIELD_MANUAL", "COMMAND", "DIAGNOSTICS", "SETTINGS", "BACKGROUNDS"):
    assert route in OPS
assert "SiegeKnowledgeExpansion40.entries" in REG
assert "SiegeKnowledgeRegistry.entries" in OPS
assert "new SiegeBriefingScreen(this)" in HUB
assert "new SiegeAtlasScreen(this)" in HUB
assert "new SiegeThreatBoardScreen(this)" in HUB
assert "new SiegeKnowledgeFileScreen" in HUB
for section in ("BRIEFING", "ATLAS", "THREATS", "KNOWLEDGE", "DEPLOYMENT", "INTEL"):
    assert section in NAV
for knowledge_id in ("progression-mobility-priority", "combat-adaptation", "prompt-precision-framework",
                     "raid-area-discipline", "relic-analysis-workflow", "assembling-planning",
                     "revive-repeat-penalties", "deteriorer-re-overflow-history", "research-open-questions",
                     "newcomer-operational-rule"):
    assert knowledge_id in EXP
# Privacy remains a release contract.
for forbidden in ("mi inventario", "mi personaje", "mi partida actual", "player notebook", "private build"):
    assert forbidden.lower() not in (BASE + EXP).lower()
# Existing high-value contracts survive the major release.
assert "SiegeLacontinuacion.exaroton.me:18736" in MULTI
assert "GLFW_KEY_S && Screen.hasControlDown()" in TITLE
for forbidden in ("FAVORITES", "toggleFavoriteIntel", "favoriteButton", "addIndexButton", "copyText", "GUARDAR"):
    assert forbidden not in INTEL
assert '"tempest_jutcherson"' in EASTER
assert '"tempest_jutcherson"' not in SCENES
# Media policy: discovered material can be recommended without silently reuploading it.
assert "768x432" in MEDIA
assert "no se incorpora" in MEDIA.lower() or "no se incluyen" in MEDIA.lower()
assert "permiso" in MEDIA.lower()
# CI must actually validate 4.00 and publish only after successful build.
assert "AtlasRegressionTest" in WORKFLOW
assert "version = '4.00.0'" in WORKFLOW
assert "Publish validated jar for installer" in WORKFLOW
print("SIEGE 4.00 release contracts passed")
