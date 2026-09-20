package uy.santipdr.siege.client;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Immutable current Intel catalog. Unit history never appears as a changelog here. */
final class IntelCatalog {
    private static final List<IntelEntry> FILES = Stream.concat(
            IntelData.FILES.stream().map(IntelCurrentData::applyOfficialOverlay),
            IntelCurrentData.supplemental().stream()).toList();

    private static final Map<String, List<IntelEntry>> GROUPS = Map.copyOf(FILES.stream().collect(Collectors.groupingBy(
            IntelEntry::category, Collectors.collectingAndThen(Collectors.toList(), List::copyOf))));
    private static final Map<String, IntelEntry> BY_CODE = Map.copyOf(FILES.stream().collect(Collectors.toMap(
            IntelEntry::code, Function.identity())));
    private static final List<IntelEntry> PREVIEW = FILES.stream()
            .filter(e -> e.category().equals("UNIT") || e.category().equals("ADVANCED"))
            .toList();

    static {
        Set<String> codes = new java.util.HashSet<>();
        Set<String> categories = Set.of("UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT", "UNKNOWN");
        for (IntelEntry entry : FILES) {
            if (!codes.add(entry.code()) || !categories.contains(entry.category()) || entry.image().isBlank()
                    || entry.name().isBlank() || entry.hp().isBlank() || entry.threat() < 0 || entry.threat() > 5
                    || !validPrefix(entry.category(), entry.code())) {
                throw new IllegalStateException("Invalid Intel record: " + entry.code());
            }
        }

        IntelEntry agreement = BY_CODE.get("TNK-003");
        if (agreement == null || containsFieldReportMaterial(agreement.spanish()) || containsFieldReportMaterial(agreement.english()))
            throw new IllegalStateException("Agreement dossier leaked field-report material");

        IntelEntry trident = BY_CODE.get("BOS-004");
        IntelEntry fusilier = BY_CODE.get("BOS-002");
        if (trident == null || !trident.spanish().variants().contains("VISOR PUESTO")
                || !trident.spanish().description().contains("40%") || !trident.spanish().description().contains("10%"))
            throw new IllegalStateException("Trident current visor dossier missing");
        if (fusilier == null || !fusilier.spanish().variants().contains("Modo Mortero")
                || !fusilier.spanish().description().contains("seis segundos") || !fusilier.spanish().description().contains("Blox Drink"))
            throw new IllegalStateException("Fusilier current dossier missing");

        if (BY_CODE.get("SOP-002") == null || BY_CODE.get("ADV-007") == null || BY_CODE.get("ELT-004") == null)
            throw new IllegalStateException("Current unit roster is incomplete");
        if (BY_CODE.get("ADV-008") == null || BY_CODE.get("ADV-009") == null || BY_CODE.get("BOS-010") == null || BY_CODE.get("BOS-011") == null)
            throw new IllegalStateException("DVN reference roster is incomplete");
    }

    private IntelCatalog() { }

    static List<IntelEntry> files() { return FILES; }
    static List<IntelEntry> filtered(String category) { return GROUPS.getOrDefault(category, List.of()); }
    static List<IntelEntry> previewable() { return PREVIEW; }
    static int count(String category) { return filtered(category).size(); }
    static IntelEntry byCode(String code) { return BY_CODE.get(code); }
    static int total() { return FILES.size(); }

    private static boolean containsFieldReportMaterial(IntelEntry.IntelText text) {
        String combined = (text.origin() + " " + text.armament() + " " + text.variants() + " " + text.status() + " "
                + text.description() + " " + text.advisory()).toUpperCase(Locale.ROOT);
        return combined.contains("GATE") || combined.contains("RIFT") || combined.contains("RICK SANCHEZ")
                || combined.contains("SABOTAJE") || combined.contains("SABOTAGE")
                || combined.contains("TESTIMONIO") || combined.contains("TESTIMONY")
                || combined.contains("REPORTE SIN VERIFICAR") || combined.contains("UNVERIFIED REPORT");
    }

    private static boolean validPrefix(String category, String code) {
        return switch (category) {
            case "UNIT" -> code.startsWith("HU-") || code.startsWith("SOP-") || code.startsWith("MECH-");
            case "ADVANCED" -> code.startsWith("ADV-");
            case "TANK" -> code.startsWith("TNK-");
            case "BOSS" -> code.startsWith("BOS-");
            case "ELITE" -> code.startsWith("ELT-");
            case "SUPER-UNIT" -> code.startsWith("SUP-");
            case "UNKNOWN" -> code.startsWith("HU-") || code.startsWith("UNK-");
            default -> false;
        };
    }
}
