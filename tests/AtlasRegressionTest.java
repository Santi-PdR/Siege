import uy.santipdr.siege.client.SiegeAtlasIndex;
import uy.santipdr.siege.client.SiegeKnowledgeData;
import uy.santipdr.siege.client.SiegeKnowledgeRegistry;

public final class AtlasRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        check(SiegeKnowledgeRegistry.entries().size() >= 30, "5.00 current knowledge registry unexpectedly small");
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
                "Normal 5.00 Atlas must not expose historical records");
        check(SiegeKnowledgeRegistry.entries().stream().noneMatch(e -> e.domain() == SiegeKnowledgeData.Domain.SOURCES
                        || e.domain() == SiegeKnowledgeData.Domain.CONTRADICTIONS),
                "Editorial/source records must stay out of normal player interfaces");
        check(SiegeKnowledgeRegistry.get("research-open-questions") == null,
                "Research/editorial entry must not be player-facing");

        String corpus = SiegeKnowledgeRegistry.entries().stream()
                .map(e -> e.title(true) + " " + e.summary(true) + " " + e.body(true))
                .reduce("", (a, b) -> a + " " + b).toLowerCase();
        for (String forbidden : java.util.List.of("mi inventario", "mi personaje", "mi partida", "current player",
                "private build", "player notebook", "también ver:", "staff confirmado", "corpus", "fuente primaria")) {
            check(!corpus.contains(forbidden), "Player-facing knowledge leaked technical/personal wording: " + forbidden);
        }
        System.out.println("SIEGE 5.00 Atlas categories, current-only boundary and natural-language contracts passed");
    }
}
