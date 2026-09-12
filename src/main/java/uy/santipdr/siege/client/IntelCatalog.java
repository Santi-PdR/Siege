package uy.santipdr.siege.client;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Immutable catalog prepared once; screens never own troop data. */
final class IntelCatalog {
    private static final List<IntelEntry> FILES = IntelData.FILES;
    private static final Map<String, List<IntelEntry>> GROUPS = FILES.stream().collect(Collectors.groupingBy(IntelEntry::category));
    private static final List<IntelEntry> PREVIEW = FILES.stream().filter(e -> e.category().equals("UNIT") || e.category().equals("ADVANCED")).toList();
    static {
        Set<String> codes = new java.util.HashSet<>();
        Set<String> categories = Set.of("UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT");
        for (IntelEntry e : FILES) {
            if (!codes.add(e.code()) || !categories.contains(e.category()) || e.image().isBlank()
                    || e.threat() < 0 || e.threat() > 5)
                throw new IllegalStateException("Invalid Intel record: " + e.code());
        }
    }
    private IntelCatalog() {}
    static List<IntelEntry> files() { return FILES; }
    static List<IntelEntry> filtered(String category) { return GROUPS.getOrDefault(category, List.of()); }
    static List<IntelEntry> previewable() { return PREVIEW; }
    static int count(String category) { return filtered(category).size(); }
}
