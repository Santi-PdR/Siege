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
 * 0.40 policy: dossiers expose confirmed/official material. When supplied SIEGE
 * changelogs conflict, newer announcements override older ones. Field reports,
 * player testimony and hypotheses stay in the Guide/Operations archive.
 */
final class IntelCatalog {
    private static final List<IntelEntry> FILES = IntelData.FILES.stream()
            .map(IntelCatalog::currentOfficialDossier)
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

        // Agreement remains the regression sentinel for the official-only dossier rule.
        IntelEntry agreement = BY_CODE.get("TNK-003");
        if (agreement == null || containsFieldReportMaterial(agreement.spanish()) || containsFieldReportMaterial(agreement.english()))
            throw new IllegalStateException("Agreement dossier leaked field-report material");

        // 0.40 current-announcement sentinels. These prevent an older archive entry
        // from silently replacing the newest supplied Trident/Fusilier behaviour.
        IntelEntry trident = BY_CODE.get("BOS-004");
        IntelEntry fusilier = BY_CODE.get("BOS-002");
        if (trident == null || !trident.spanish().variants().contains("VISOR PUESTO")
                || !trident.spanish().description().contains("40%") || !trident.spanish().description().contains("10%"))
            throw new IllegalStateException("Trident current visor dossier missing");
        if (fusilier == null || !fusilier.spanish().variants().contains("Modo Mortero")
                || !fusilier.spanish().description().contains("seis segundos") || !fusilier.spanish().description().contains("Blox Drink"))
            throw new IllegalStateException("Fusilier current dossier missing");
    }

    private IntelCatalog() {}

    private static IntelEntry currentOfficialDossier(IntelEntry entry) {
        return switch (entry.code()) {
            case "TNK-003" -> agreement(entry);
            case "BOS-002" -> fusilier(entry);
            case "BOS-004" -> trident(entry);
            default -> entry;
        };
    }

    private static IntelEntry agreement(IntelEntry entry) {
        return replaceText(entry,
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

    /** Newest supplied SIEGE changelog is authoritative over older Fusilier notes. */
    private static IntelEntry fusilier(IntelEntry entry) {
        return replaceText(entry,
                new IntelEntry.IntelText(
                        "Sin registro de origen confirmado",
                        "Lanzagranadas de seis disparos + granadas nucleares/incendiarias + pala",
                        "Modo Mortero / Blox Drink / strafing mejorado",
                        "JEFE HOSTIL // EXPEDIENTE ACTUALIZADO",
                        "Fusilier conserva su función de bombardeo pesado y ahora dispone de granadas nucleares con una ventana aproximada de seis segundos para reaccionar, granadas incendiarias de área y un patrón de esquive mejorado. En Modo Mortero dispara al cielo y controla las zonas de caída con distintos tipos de munición; su barra de jefe se vuelve blanca mientras el modo está activo. Blox Drink acelera recargas, disparo y recuperación de habilidades a cambio de visión borrosa durante su efecto.",
                        "Salir de la zona de impacto apenas aparezca una amenaza nuclear. Un explosivo correctamente sincronizado puede impedir que complete Blox Drink. Cuando la barra se vuelva blanca, asumir bombardeo indirecto y cambiar de posición antes de la caída de los proyectiles."),
                new IntelEntry.IntelText(
                        "No confirmed origin record",
                        "Six-shot grenade launcher + nuclear/incendiary grenades + shovel",
                        "Mortar Mode / Blox Drink / improved strafing",
                        "HOSTILE BOSS // UPDATED DOSSIER",
                        "Fusilier retains its heavy-bombardment role and now carries nuclear grenades with an approximately six-second reaction window, area incendiary grenades and improved evasive strafing. In Mortar Mode it fires skyward and controls impact zones with multiple ammunition types; its boss bar turns white while the mode is active. Blox Drink speeds up reloads, firing and ability recovery while blurring its vision during the effect.",
                        "Leave the impact zone as soon as a nuclear threat appears. A correctly timed explosive can interrupt Blox Drink. When the boss bar turns white, assume indirect bombardment and relocate before the projectiles land."));
    }

    /** Newest supplied SIEGE changelog is authoritative over older Trident notes. */
    private static IntelEntry trident(IntelEntry entry) {
        return replaceText(entry,
                new IntelEntry.IntelText(
                        "Sin registro de origen confirmado",
                        "Machete de alta frecuencia + gancho + visor con dos estados",
                        "VISOR PUESTO / VISOR REMOVIDO",
                        "JEFE HOSTIL // EXPEDIENTE ACTUALIZADO",
                        "Trident puede alternar su visor. Con VISOR PUESTO mantiene 40% de resistencia a todo daño, no recibe disparos a la cabeza y su gancho tiene más dificultad para atrapar objetivos. Con VISOR REMOVIDO baja a 10% de resistencia y queda expuesto a disparos a la cabeza y flashbangs, pero puede responder instantáneamente al primer ataque cuerpo a cuerpo con una mutilación y su gancho atrapa con mayor facilidad, menor resistencia de arrastre y mayor velocidad de tracción. Un aliado enganchado puede ser liberado disparándole al torso; la víctima también puede intentar «cortar cuerda» en el chat si dispone de un arma suficientemente afilada.",
                        "Identificar el estado del visor antes de entrar en rango. No abrir un intercambio cuerpo a cuerpo cuando el visor esté removido. Coordinar el rescate de aliados enganchados y aprovechar la menor resistencia defensiva del modo sin visor con fuego a distancia o flashbangs."),
                new IntelEntry.IntelText(
                        "No confirmed origin record",
                        "High-frequency machete + hook + two-state visor",
                        "VISOR ON / VISOR REMOVED",
                        "HOSTILE BOSS // UPDATED DOSSIER",
                        "Trident can switch visor states. With the VISOR ON it keeps 40% resistance to all damage, is immune to headshots and has a harder time hooking targets. With the VISOR REMOVED resistance drops to 10% and it becomes vulnerable to headshots and flashbangs, but it can instantly answer the first incoming melee attack with a mutilating parry and its hook becomes easier to land, has less pull resistance and reels targets faster. A hooked ally can be freed by shooting their torso; the victim may also attempt the 'cut rope' chat cast while carrying a sufficiently sharp weapon.",
                        "Identify the visor state before entering range. Do not open with melee while the visor is removed. Coordinate rescues for hooked allies and exploit the reduced defensive resistance of the visor-off state with ranged fire or flashbangs."));
    }

    private static IntelEntry replaceText(IntelEntry entry, IntelEntry.IntelText spanish, IntelEntry.IntelText english) {
        return new IntelEntry(entry.code(), entry.name(), entry.category(), entry.threat(), entry.hp(), entry.defense(), entry.image(), spanish, english);
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
