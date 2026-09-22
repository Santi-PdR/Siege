package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Unified read-only knowledge registry for SIEGE 4.00.1. */
public final class SiegeKnowledgeRegistry {
    private static final List<SiegeKnowledgeData.Entry> ALL = java.util.stream.Stream
            .concat(
                    java.util.stream.Stream.concat(
                            SiegeKnowledgeData.entries().stream(),
                            SiegeKnowledgeExpansion40.entries().stream()),
                    SiegeKnowledgeExpansion401.entries().stream())
            .toList();

    private SiegeKnowledgeRegistry() { }

    public static List<SiegeKnowledgeData.Entry> entries() { return ALL; }

    public static SiegeKnowledgeData.Entry get(String id) {
        if (id == null) return null;
        for (SiegeKnowledgeData.Entry entry : ALL) if (entry.id().equals(id)) return entry;
        return null;
    }

    public static List<SiegeKnowledgeData.Entry> critical() {
        return ALL.stream().filter(SiegeKnowledgeData.Entry::critical)
                .sorted(Comparator.comparing((SiegeKnowledgeData.Entry e) ->
                                e.zone() == SiegeKnowledgeData.Zone.SERVER ? 0 : 1)
                        .thenComparing(SiegeKnowledgeData.Entry::id))
                .toList();
    }

    public static List<SiegeKnowledgeData.Entry> search(String query, boolean spanish, int limit) {
        int safeLimit = Math.max(1, Math.min(128, limit));
        String q = normalize(query);
        if (q.isBlank()) return ALL.stream().limit(safeLimit).toList();

        record Ranked(SiegeKnowledgeData.Entry entry, int score) { }
        List<Ranked> ranked = new ArrayList<>();
        for (SiegeKnowledgeData.Entry entry : ALL) {
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
