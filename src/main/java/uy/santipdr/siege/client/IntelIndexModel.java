package uy.santipdr.siege.client;

import java.util.Comparator;
import java.util.List;

/** Immutable index snapshot; selecting a row always returns the stable dossier code. */
public final class IntelIndexModel {
    public enum Order { CODE, NAME, THREAT;
        public Order next() { return values()[(ordinal() + 1) % values().length]; }
    }
    private final List<IntelEntry> entries;
    public IntelIndexModel(List<IntelEntry> entries) { this.entries = List.copyOf(entries); }
    public List<IntelEntry> results(String query, Order order, boolean spanish) {
        Comparator<IntelEntry> comparator = switch (order) {
            case CODE -> Comparator.comparing(IntelEntry::code);
            case NAME -> Comparator.comparing(IntelEntry::name, String.CASE_INSENSITIVE_ORDER);
            case THREAT -> Comparator.comparingInt(IntelEntry::threat).reversed();
        };
        return entries.stream().filter(e -> IntelSearch.matches(query,
                e.code() + " " + e.name() + " " + e.text(spanish).armament() + " " + e.text(spanish).origin()))
                .sorted(comparator.thenComparing(IntelEntry::code)).toList();
    }
    public static int rowsPerPage(int height) { return Math.max(1, (height - 102) / 34); }
    public static int lastPage(int count, int capacity) { return Math.max(0, (count - 1) / Math.max(1, capacity)); }
    public static int clampPage(int page, int count, int capacity) { return Math.max(0, Math.min(lastPage(count, capacity), page)); }
}
