import uy.santipdr.siege.client.SiegeKnowledgeData;
import uy.santipdr.siege.client.SiegeKnowledgeRegistry;
import uy.santipdr.siege.client.SiegeServerGuideData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ServerGuideRegressionTest {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        check(SiegeServerGuideData.Category.values().length == 7, "Server guide category count");

        for (SiegeServerGuideData.Category category : SiegeServerGuideData.Category.values()) {
            List<SiegeKnowledgeData.Entry> entries = SiegeServerGuideData.entries(category);
            check(!entries.isEmpty(), "Empty server guide category: " + category);
            Set<String> ids = new HashSet<>();
            for (SiegeKnowledgeData.Entry entry : entries) {
                check(ids.add(entry.id()), "Duplicate entry in " + category + ": " + entry.id());
                check(!entry.title(true).isBlank(), "Missing Spanish title: " + entry.id());
                check(!entry.summary(true).isBlank(), "Missing Spanish summary: " + entry.id());
                check(!entry.body(true).isBlank(), "Missing Spanish body: " + entry.id());
            }
        }

        assertContains(SiegeServerGuideData.Category.START, "server-overview", "guide-first-hour", "trials-basics");
        assertContains(SiegeServerGuideData.Category.RACES, "rarity-order", "race-catalog", "race-deteriorer", "race-saiyan");
        assertContains(SiegeServerGuideData.Category.PROGRESSION, "progression-v1-v4", "trials-basics", "server-exploration");
        assertContains(SiegeServerGuideData.Category.THREATS, "executors-basics", "bosses-basics", "raids-basics");
        assertContains(SiegeServerGuideData.Category.SYSTEMS, "relic-basics", "assembling-table", "dimensions-basics");
        assertContains(SiegeServerGuideData.Category.SURVIVAL, "respawn-cards", "guide-revive", "guide-first-hour");

        check(SiegeServerGuideData.entries(SiegeServerGuideData.Category.HISTORY).stream()
                        .anyMatch(e -> e.zone() == SiegeKnowledgeData.Zone.HISTORY),
                "History category must contain historical entries");

        for (String id : List.of("guide-first-hour", "guide-races", "guide-progression", "guide-trials",
                "guide-executors", "guide-bosses", "guide-relics", "guide-assembling", "guide-dimensions",
                "guide-revive", "guide-economy", "guide-actions")) {
            check(SiegeKnowledgeRegistry.get(id) != null, "Missing 4.00.1 newcomer entry: " + id);
        }

        System.out.println("SIEGE 4.00.1 simple server guide categories and newcomer content passed");
    }

    private static void assertContains(SiegeServerGuideData.Category category, String... ids) {
        Set<String> found = new HashSet<>();
        for (SiegeKnowledgeData.Entry entry : SiegeServerGuideData.entries(category)) found.add(entry.id());
        for (String id : ids) check(found.contains(id), category + " missing " + id);
    }
}
