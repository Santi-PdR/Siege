package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Current-first player knowledge registry for SIEGE 5.00. */
public final class SiegeKnowledgeRegistry {
    private static final Set<String> HIDDEN_PLAYER_IDS = Set.of(
            "source-policy", "source-audit", "research-open-questions",
            "progression-mobility-priority", "prompt-precision-framework",
            "assembling-planning", "revive-repeat-penalties", "deteriorer-re-overflow-history"
    );

    /** Complete maintenance history, including old/conflicting records. Never rendered directly. */
    private static final List<SiegeKnowledgeData.Entry> MAINTENANCE = java.util.stream.Stream.of(
            SiegeKnowledgeData.entries(), SiegeKnowledgeExpansion40.entries(),
            SiegeKnowledgeExpansion50.entries(), SiegeKnowledgeCorpus50.entries(),
            SiegeKnowledgeCorrections50.entries())
            .flatMap(List::stream).toList();

    /** One current record per ID. The last source wins, so late corrections replace stale recipes/rules. */
    private static final Map<String, SiegeKnowledgeData.Entry> CURRENT_BY_ID = buildCurrent();
    private static final List<SiegeKnowledgeData.Entry> PUBLIC = CURRENT_BY_ID.values().stream()
            .filter(SiegeKnowledgeRegistry::playerFacing)
            .toList();

    private SiegeKnowledgeRegistry() { }

    private static Map<String, SiegeKnowledgeData.Entry> buildCurrent() {
        LinkedHashMap<String, SiegeKnowledgeData.Entry> out = new LinkedHashMap<>();
        addCurrent(out, SiegeKnowledgeData.entries());
        addCurrent(out, SiegeKnowledgeExpansion40.entries());
        addCurrent(out, SiegeKnowledgeExpansion50.entries());
        addCurrent(out, SiegeKnowledgeCorpus50.entries());
        addCurrent(out, SiegeKnowledgeCorrections50.entries());
        return Collections.unmodifiableMap(out);
    }

    private static void addCurrent(Map<String, SiegeKnowledgeData.Entry> out,
                                   List<SiegeKnowledgeData.Entry> entries) {
        for (SiegeKnowledgeData.Entry entry : entries) {
            if (entry.zone() == SiegeKnowledgeData.Zone.SERVER) out.put(entry.id(), entry);
        }
    }

    private static boolean playerFacing(SiegeKnowledgeData.Entry entry) {
        if (entry == null || entry.zone() != SiegeKnowledgeData.Zone.SERVER) return false;
        if (entry.domain() == SiegeKnowledgeData.Domain.SOURCES
                || entry.domain() == SiegeKnowledgeData.Domain.CONTRADICTIONS) return false;
        return !HIDDEN_PLAYER_IDS.contains(entry.id());
    }

    public static List<SiegeKnowledgeData.Entry> entries() { return PUBLIC; }
    public static List<SiegeKnowledgeData.Entry> maintenanceEntries() { return MAINTENANCE; }

    /** Player-facing lookup. Editorial/history-only records deliberately resolve to null. */
    public static SiegeKnowledgeData.Entry get(String id) {
        if (id == null) return null;
        SiegeKnowledgeData.Entry entry = CURRENT_BY_ID.get(id);
        return playerFacing(entry) ? entry : null;
    }

    public static List<SiegeKnowledgeData.Entry> critical() {
        return PUBLIC.stream().filter(SiegeKnowledgeData.Entry::critical)
                .sorted(Comparator.comparing(SiegeKnowledgeData.Entry::id))
                .toList();
    }

    public static List<SiegeKnowledgeData.Entry> search(String query, boolean spanish, int limit) {
        int safeLimit = Math.max(1, Math.min(128, limit));
        String q = normalize(query);
        if (q.isBlank()) return PUBLIC.stream().limit(safeLimit).toList();

        record Ranked(SiegeKnowledgeData.Entry entry, int score) { }
        List<Ranked> ranked = new ArrayList<>();
        for (SiegeKnowledgeData.Entry entry : PUBLIC) {
            int score = score(entry, q, spanish);
            if (score > 0) ranked.add(new Ranked(entry, score));
        }
        ranked.sort(Comparator.comparingInt(Ranked::score).reversed()
                .thenComparing(r -> normalize(r.entry().title(spanish)))
                .thenComparing(r -> r.entry().id()));
        return ranked.stream().limit(safeLimit).map(Ranked::entry).toList();
    }

    public static String searchable(SiegeKnowledgeData.Entry entry, boolean spanish) {
        return SiegeKnowledgeData.searchable(entry, spanish);
    }

    private static int score(SiegeKnowledgeData.Entry entry, String query, boolean spanish) {
        String id = normalize(entry.id());
        String title = normalize(entry.title(spanish));
        String summary = normalize(entry.summary(spanish));
        String domain = normalize(entry.domain().label(spanish));
        String all = normalize(searchable(entry, spanish));
        if (id.equals(query) || title.equals(query)) return 160;
        if (id.startsWith(query)) return 145;
        if (title.startsWith(query)) return 135;
        if (title.contains(query)) return 115;
        if (summary.contains(query)) return 90;
        if (domain.contains(query)) return 80;
        if (all.contains(query)) return 55;
        String[] tokens = query.split("\\s+");
        int matched = 0;
        for (String token : tokens) if (!token.isBlank() && all.contains(token)) matched++;
        return matched == tokens.length && matched > 0 ? 30 + matched * 5 : 0;
    }

    private static String normalize(String value) {
        if (value == null) return "";
        String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return decomposed.toLowerCase(Locale.ROOT)
                .replace('·', ' ')
                .replaceAll("[^a-z0-9?_-]+", " ")
                .trim();
    }
}
