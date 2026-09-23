import uy.santipdr.siege.client.SiegeAtlasIndex;
import uy.santipdr.siege.client.SiegeKnowledgeData;
import uy.santipdr.siege.client.SiegeKnowledgeRegistry;

public final class AtlasRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        check(SiegeKnowledgeRegistry.entries().size() >= 30, "5.10 current knowledge registry unexpectedly small");
        check(SiegeAtlasIndex.count(SiegeAtlasIndex.View.RACES) >= 10, "Race view too small");
        check(SiegeAtlasIndex.count(SiegeAtlasIndex.View.PROGRESSION) >= 8, "Progression view too small");
        check(SiegeAtlasIndex.count(SiegeAtlasIndex.View.SYSTEMS) >= 8, "Systems view too small");
        check(SiegeAtlasIndex.count(SiegeAtlasIndex.View.ITEMS) >= 5, "Items view too small");

        var raceSystem = SiegeKnowledgeRegistry.get("race-system");
        check(raceSystem != null && SiegeAtlasIndex.belongs(SiegeAtlasIndex.View.RACES, raceSystem),
                "Race system must appear under Races");
        var v1v4 = SiegeKnowledgeRegistry.get("progression-v1-v4");
        check(v1v4 != null && SiegeAtlasIndex.belongs(SiegeAtlasIndex.View.PROGRESSION, v1v4),
                "V1-V4 must appear under Progression");
        var death = SiegeKnowledgeRegistry.get("death-revive-current");
        check(death != null && SiegeAtlasIndex.belongs(SiegeAtlasIndex.View.SYSTEMS, death),
                "Current death/revive information must appear under Systems");
        var geography = SiegeKnowledgeRegistry.get("item-geography-table");
        check(geography != null && SiegeAtlasIndex.belongs(SiegeAtlasIndex.View.ITEMS, geography),
                "Geography Table must appear under Items");

        check(SiegeKnowledgeRegistry.entries().stream().allMatch(e -> e.zone() == SiegeKnowledgeData.Zone.SERVER),
                "Normal 5.10 Atlas must not expose historical records");
        check(SiegeKnowledgeRegistry.entries().stream().noneMatch(e -> e.domain() == SiegeKnowledgeData.Domain.SOURCES
                        || e.domain() == SiegeKnowledgeData.Domain.CONTRADICTIONS),
                "Editorial/source records must stay out of normal player interfaces");
        check(SiegeKnowledgeRegistry.entries().stream().allMatch(e -> e.sources().isEmpty()),
                "Player-facing entries must not expose provenance metadata");
        check(SiegeKnowledgeRegistry.get("research-open-questions") == null,
                "Research/editorial entry must not be player-facing");
        check(SiegeKnowledgeRegistry.get("raid-area-discipline") == null,
                "One-player raid anecdotes must not become general player guidance");

        var catalog = SiegeKnowledgeRegistry.get("race-catalog");
        check(catalog != null && catalog.body(true).contains("Xeno Saiyan"),
                "5.10 race catalog must include Xeno Saiyan");
        check(catalog.body(true).contains("Adamantium Human"),
                "5.10 race catalog must explain Adamantium Human as a Subhuman variant");

        var defib = SiegeKnowledgeRegistry.get("item-defibrillator");
        check(defib != null && defib.body(true).contains("3 bloques de hierro + 1 bloque de oro"),
                "Current defibrillator recipe missing");
        var medkit = SiegeKnowledgeRegistry.get("item-medkit");
        check(medkit != null && medkit.body(true).contains("3 bloques de hierro + 1 mesa de encantamientos"),
                "Current medkit recipe missing");

        String text = SiegeKnowledgeRegistry.entries().stream()
                .map(e -> e.title(true) + " " + e.summary(true) + " " + e.body(true)
                        + " " + e.title(false) + " " + e.summary(false) + " " + e.body(false))
                .reduce("", (a, b) -> a + " " + b).toLowerCase();
        for (String forbidden : java.util.List.of(
                "mi inventario", "mi personaje", "mi partida", "current player",
                "private build", "player notebook", "también ver:",
                "staff confirmado", "staff confirmed", "el staff", "by staff",
                "discord", "251.065", "251,065", "json", "confidence",
                "fuente primaria", "primary source", "corpus", "export")) {
            check(!text.contains(forbidden), "Player-facing knowledge leaked technical/personal wording: " + forbidden);
        }
        System.out.println("SIEGE 5.10 Atlas, privacy and natural-language contracts passed");
    }
}
