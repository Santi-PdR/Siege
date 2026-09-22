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
        check(all.size() >= 20, "3.00 knowledge corpus unexpectedly small");

        Set<String> ids = new HashSet<>();
        for (SiegeKnowledgeData.Entry entry : all) {
            check(ids.add(entry.id()), "Duplicate knowledge id: " + entry.id());
            check(!entry.title(false).isBlank(), "Missing English title: " + entry.id());
            check(!entry.title(true).isBlank(), "Missing Spanish title: " + entry.id());
            check(!entry.sources().isEmpty(), "Every knowledge record must retain provenance: " + entry.id());
        }

        check(!SiegeKnowledgeData.current().isEmpty(), "Current SIEGE zone missing");
        check(!SiegeKnowledgeData.encyclopedia().isEmpty(), "Eternal Craft encyclopedia zone missing");
        check(SiegeKnowledgeData.current().stream().allMatch(e -> e.zone() == SiegeKnowledgeData.Zone.CURRENT),
                "Current zone leaked encyclopedia entries");
        check(SiegeKnowledgeData.encyclopedia().stream().allMatch(e -> e.zone() == SiegeKnowledgeData.Zone.ENCYCLOPEDIA),
                "Encyclopedia zone leaked current entries");

        var audit = SiegeKnowledgeData.get("audit-discord-export");
        check(audit != null && audit.body(true).contains("251.065") && audit.body(true).contains("72.384"),
                "Discord audit counts must remain visible");

        var rustGuard = SiegeKnowledgeData.get("current-rust-guard");
        check(rustGuard != null && rustGuard.zone() == SiegeKnowledgeData.Zone.CURRENT,
                "Rust Guard must stay in current player notes");
        check(rustGuard.body(true).contains("25 segundos") && rustGuard.body(true).contains("8 RE"),
                "Rust Guard snapshot lost its recorded duration/cost");

        var oldDuration = SiegeKnowledgeData.get("history-deteriorer-duration");
        check(oldDuration != null && oldDuration.confidence() == SiegeKnowledgeData.Confidence.HISTORICAL,
                "Old Deteriorer duration must remain historical");
        check(oldDuration.spoiler(), "Historical Discord mechanics should respect spoiler guard");

        var meditation = SiegeKnowledgeData.get("alex-meditation-overload");
        check(meditation != null && meditation.critical(), "Meditation overload must stay in survival warnings");
        check(meditation.sources().stream().anyMatch(s -> s.confidence() == SiegeKnowledgeData.Confidence.ALEX_CONFIRMED),
                "Meditation warning lost Alex provenance");
        check(meditation.sources().stream().anyMatch(s -> s.confidence() == SiegeKnowledgeData.Confidence.SYSTEM_OBSERVED),
                "Meditation warning lost observed-result provenance");

        var halo = SiegeKnowledgeData.search("120 wins", true, SiegeKnowledgeData.Zone.ENCYCLOPEDIA, 5);
        check(halo.stream().anyMatch(e -> e.id().equals("relic-fallen-angel-halo")),
                "Fallen Angel Halo price snapshot should be searchable");

        var geography = SiegeKnowledgeData.search("Geography Table", false, null, 5);
        check(geography.stream().anyMatch(e -> e.id().equals("item-geography-table")),
                "Geography Table search failed");

        var vague = SiegeKnowledgeData.search("medito fuerte", true, null, 5);
        check(vague.stream().anyMatch(e -> e.id().equals("prompt-vague-failure")),
                "Historical prompt failure search failed");

        check(SiegeKnowledgeData.survival().stream().allMatch(SiegeKnowledgeData.Entry::critical),
                "Survival feed may only contain critical records");
        check(SiegeKnowledgeData.survival().size() >= 8, "Survival feed too small for 3.00");

        System.out.println("SIEGE 3.00 knowledge zones, provenance, search, spoilers and survival feed passed");
    }
}
