import uy.santipdr.siege.client.SiegeAtlasIndex;
import uy.santipdr.siege.client.SiegeKnowledgeData;
import uy.santipdr.siege.client.SiegeKnowledgeRegistry;

public final class AtlasRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        check(SiegeKnowledgeRegistry.entries().size() >= 40, "4.00 unified knowledge corpus unexpectedly small");
        check(SiegeAtlasIndex.count(SiegeAtlasIndex.View.BRIEFING) >= 16, "Briefing too small");
        check(SiegeAtlasIndex.count(SiegeAtlasIndex.View.RACES) >= 10, "Race registry too small");
        check(SiegeAtlasIndex.count(SiegeAtlasIndex.View.SYSTEMS) >= 10, "Systems view too small");
        check(SiegeAtlasIndex.count(SiegeAtlasIndex.View.RESEARCH) >= 5, "Research view too small");

        var mobility = SiegeKnowledgeRegistry.get("progression-mobility-priority");
        check(mobility != null && SiegeAtlasIndex.belongs(SiegeAtlasIndex.View.BRIEFING, mobility),
                "Mobility priority must appear in newcomer briefing");
        var adaptation = SiegeKnowledgeRegistry.get("combat-adaptation");
        check(adaptation != null && SiegeAtlasIndex.belongs(SiegeAtlasIndex.View.SYSTEMS, adaptation),
                "Combat adaptation must appear in systems");
        var overflow = SiegeKnowledgeRegistry.get("deteriorer-re-overflow-history");
        check(overflow != null && overflow.zone() == SiegeKnowledgeData.Zone.HISTORY
                        && SiegeAtlasIndex.belongs(SiegeAtlasIndex.View.RESEARCH, overflow),
                "Historical RE warning must stay in Research/history");
        var open = SiegeKnowledgeRegistry.get("research-open-questions");
        check(open != null && open.domain() == SiegeKnowledgeData.Domain.SOURCES,
                "Open research gaps must be explicit rather than invented");

        String corpus = SiegeKnowledgeRegistry.entries().stream()
                .map(e -> e.title(true) + " " + e.summary(true) + " " + e.body(true))
                .reduce("", (a, b) -> a + " " + b).toLowerCase();
        for (String forbidden : java.util.List.of("mi inventario", "mi personaje", "mi partida", "current player",
                "private build", "player notebook")) {
            check(!corpus.contains(forbidden), "Personal state leaked into 4.00 Atlas: " + forbidden);
        }
        System.out.println("SIEGE 4.00 Tactical Atlas classification, history boundary and privacy contracts passed");
    }
}
