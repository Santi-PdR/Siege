package uy.santipdr.siege.client;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Function;

/**
 * Immutable catalog prepared once; screens never own troop data.
 *
 * 0.30 policy: dossiers only expose confirmed/official material. Field reports,
 * player testimony and hypotheses belong to the Guide/Operations archive.
 */
final class IntelCatalog {
    private static final List<IntelEntry> FILES = IntelData.FILES.stream()
            .map(IntelCatalog::officialDossier)
            .toList();
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

        // Agreement is the regression sentinel for the official-only dossier rule.
        IntelEntry agreement = BY_CODE.get("TNK-003");
        if (agreement == null || containsFieldReportMaterial(agreement.spanish()) || containsFieldReportMaterial(agreement.english()))
            throw new IllegalStateException("Agreement dossier leaked field-report material");
    }

    private IntelCatalog() {}

    private static IntelEntry officialDossier(IntelEntry entry) {
        if (!"TNK-003".equals(entry.code())) return entry;
        return new IntelEntry(entry.code(), entry.name(), entry.category(), entry.threat(), entry.hp(), entry.defense(), entry.image(),
                new IntelEntry.IntelText(
                        "Corporación Secure Contain Protect",
                        "Sin información oficial confirmada",
                        "Sin información oficial confirmada",
                        "EXPEDIENTE OFICIAL // PARCIAL",
                        "El expediente oficial confirma únicamente la designación Agreement, una resistencia de 3.000 HP, 100 DEF y su vínculo con Secure Contain Protect. No hay datos oficiales confirmados sobre armamento, capacidades, variantes ni patrón táctico.",
                        "Sin protocolo táctico oficial confirmado. Consulte Guía SIEGE > Operaciones para archivos de campo separados."),
                new IntelEntry.IntelText(
                        "Secure Contain Protect Corporation",
                        "No official information confirmed",
                        "No official information confirmed",
                        "OFFICIAL DOSSIER // PARTIAL",
                        "The official dossier confirms only the Agreement designation, 3,000 HP, 100 DEF and its link to Secure Contain Protect. No official data is confirmed for armament, capabilities, variants or tactical pattern.",
                        "No official tactical protocol has been confirmed. See SIEGE Guide > Operations for separate field archives."));
    }

    private static boolean containsFieldReportMaterial(IntelEntry.IntelText text) {
        String combined = (text.origin() + " " + text.armament() + " " + text.variants() + " " + text.status() + " "
                + text.description() + " " + text.advisory()).toUpperCase(Locale.ROOT);
        return combined.contains("GATE") || combined.contains("RIFT") || combined.contains("RICK SANCHEZ")
                || combined.contains("VISOR") || combined.contains("SABOTAJE") || combined.contains("SABOTAGE")
                || combined.contains("TESTIMONIO") || combined.contains("TESTIMONY")
                || combined.contains("REPORTE SIN VERIFICAR") || combined.contains("UNVERIFIED REPORT");
    }

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
