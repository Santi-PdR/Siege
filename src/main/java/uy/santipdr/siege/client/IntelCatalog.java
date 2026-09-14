package uy.santipdr.siege.client;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Function;

/** Immutable catalog prepared once; screens never own troop data. */
final class IntelCatalog {
    private static final List<IntelEntry> FILES = IntelData.FILES;
    private static final Map<String, List<IntelEntry>> GROUPS = Map.copyOf(FILES.stream().collect(Collectors.groupingBy(
            IntelEntry::category, Collectors.collectingAndThen(Collectors.toList(), List::copyOf))));
    private static final Map<String, IntelEntry> BY_CODE = Map.copyOf(FILES.stream().collect(Collectors.toMap(
            IntelEntry::code, Function.identity())));
    private static final List<IntelEntry> PREVIEW = FILES.stream().filter(e -> e.category().equals("UNIT") || e.category().equals("ADVANCED")).toList();
    static {
        Set<String> codes = new java.util.HashSet<>();
        Set<String> categories = Set.of("UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT");
        for (IntelEntry e : FILES) {
            if (!codes.add(e.code()) || !categories.contains(e.category()) || e.image().isBlank()
                    || e.name().isBlank() || e.hp().isBlank() || e.threat() < 0 || e.threat() > 5
                    || !validPrefix(e.category(), e.code()))
                throw new IllegalStateException("Invalid Intel record: " + e.code());
        }
    }
    private IntelCatalog() {}
    static List<IntelEntry> files() { return FILES; }
    static List<IntelEntry> filtered(String category) { return GROUPS.getOrDefault(category, List.of()); }
    static List<IntelEntry> previewable() { return PREVIEW; }
    static int count(String category) { return filtered(category).size(); }
    static IntelEntry byCode(String code) { return BY_CODE.get(code); }
    static int total() { return FILES.size(); }

    private static boolean validPrefix(String category, String code) {
        return switch (category) {
            case "UNIT" -> code.startsWith("HU-") || code.startsWith("SOP-") || code.startsWith("MECH-");
            case "ADVANCED" -> code.startsWith("ADV-");
            case "TANK" -> code.startsWith("TNK-");
            case "BOSS" -> code.startsWith("BOS-");
            case "ELITE" -> code.startsWith("ELT-");
            case "SUPER-UNIT" -> code.startsWith("SUP-");
            default -> false;
        };
    }
}
