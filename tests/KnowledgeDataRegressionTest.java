import uy.santipdr.siege.client.SiegeKnowledgeData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class KnowledgeDataRegressionTest {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        List<SiegeKnowledgeData.Entry> all = SiegeKnowledgeData.entries();
        check(all.size() >= 30, "3.00 server encyclopedia unexpectedly small");

        Set<String> ids = new HashSet<>();
        for (SiegeKnowledgeData.Entry entry : all) {
            check(ids.add(entry.id()), "Duplicate knowledge id: " + entry.id());
            check(!entry.title(false).isBlank(), "Missing English title: " + entry.id());
            check(!entry.title(true).isBlank(), "Missing Spanish title: " + entry.id());
            check(!entry.sources().isEmpty(), "Every encyclopedia entry must retain provenance: " + entry.id());
        }

        check(!SiegeKnowledgeData.server().isEmpty(), "Server knowledge zone missing");
        check(!SiegeKnowledgeData.history().isEmpty(), "Historical knowledge zone missing");
        check(SiegeKnowledgeData.server().stream().allMatch(e -> e.zone() == SiegeKnowledgeData.Zone.SERVER),
                "Server zone leaked historical entries");
        check(SiegeKnowledgeData.history().stream().allMatch(e -> e.zone() == SiegeKnowledgeData.Zone.HISTORY),
                "History zone leaked server entries");

        var overview = SiegeKnowledgeData.get("server-overview");
        check(overview != null && overview.body(true).contains("información general del servidor"),
                "Server overview must explain the non-personal scope");

        var audit = SiegeKnowledgeData.get("source-audit");
        check(audit != null && audit.body(true).contains("251.065") && audit.body(true).contains("252/252"),
                "Research audit coverage must remain visible");

        var rarity = SiegeKnowledgeData.get("rarity-order");
        check(rarity != null && rarity.body(true).contains("Obsainan") && rarity.summary(true).contains("Fabled"),
                "Race rarity ladder lost required tiers");

        var catalog = SiegeKnowledgeData.get("race-catalog");
        check(catalog != null && catalog.summary(true).contains("Human")
                        && catalog.summary(true).contains("Deteriorer")
                        && catalog.summary(true).contains("Cyborg")
                        && catalog.summary(true).contains("Majin"),
                "Documented race catalog lost known races");

        var deteriorerOld = SiegeKnowledgeData.get("race-deteriorer-old-debuff");
        check(deteriorerOld != null && deteriorerOld.confidence() == SiegeKnowledgeData.Confidence.HISTORICAL,
                "Old Deteriorer balance must remain historical");

        var thresholds = SiegeKnowledgeData.get("meditation-levels");
        check(thresholds != null && thresholds.confidence() == SiegeKnowledgeData.Confidence.CONTRADICTION,
                "Conflicting meditation thresholds must remain a contradiction");

        var geography = SiegeKnowledgeData.search("Geography Table", false, null, 5);
        check(geography.stream().anyMatch(e -> e.id().equals("item-geography-table")),
                "Geography Table search failed");

        var raritySearch = SiegeKnowledgeData.search("Fabled", true, null, 10);
        check(raritySearch.stream().anyMatch(e -> e.id().equals("rarity-order")),
                "Race rarity search failed");

        var executors = SiegeKnowledgeData.search("terror radius", false, null, 10);
        check(executors.stream().anyMatch(e -> e.id().equals("executors-basics")),
                "Executor basics search failed");

        var trials = SiegeKnowledgeData.search("V4 Trials", true, null, 10);
        check(trials.stream().anyMatch(e -> e.domain() == SiegeKnowledgeData.Domain.TRIALS
                        || e.id().equals("progression-v1-v4")),
                "Trial/progression search failed");

        check(SiegeKnowledgeData.critical().stream().allMatch(SiegeKnowledgeData.Entry::critical),
                "Critical feed may only contain critical records");
        check(SiegeKnowledgeData.critical().size() >= 10, "Critical server topics too small for 3.00");

        // User request: this encyclopedia must remain server-wide and non-personal.
        String corpus = all.stream()
                .map(e -> e.title(true) + " " + e.summary(true) + " " + e.body(true) + " "
                        + e.sources().stream().map(s -> s.note(true)).reduce("", (a, b) -> a + " " + b))
                .reduce("", (a, b) -> a + " " + b)
                .toLowerCase();
        for (String forbidden : List.of("santi", "walter", "mell", "mathu", "agustin", "mi partida",
                "mi inventario", "mi personaje", "current player", "player notebook")) {
            check(!corpus.contains(forbidden), "Personal/player-specific data leaked into encyclopedia: " + forbidden);
        }

        System.out.println("SIEGE 3.00 server encyclopedia: races, rarities, systems, history and non-personal scope passed");
    }
}
