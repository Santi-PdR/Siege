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
    private static final List<IntelEntry> SUPPLEMENTAL = List.of(
            dossier("HU-008", "ENGINEER", "UNKNOWN", 0, "N/D", "N/D", "placeholder/classified",
                    "Sin registro de origen confirmado", "Equipo técnico + Sound Erradicator", "Clasificación táctica no confirmada", "SIN REGISTRO VISUAL // ARCHIVO PARCIAL",
                    "La designación Engineer está confirmada, pero su categoría operativa todavía no. Sound Erradicator utiliza dispositivos acústicos improvisados —entre ellos megáfonos dañados y terminales portátiles modificados— lanzados como cargas de impacto. No existe una fotografía o captura visual verificada de esta unidad; el expediente muestra un marcador documental en lugar de una textura inexistente.",
                    "Mantener protección auditiva cuando sea posible y dispersarse al identificar un dispositivo arrojado. Registrar cualquier evidencia visual fiable antes de reclasificar la unidad.",
                    "No confirmed origin record", "Technical equipment + Sound Erradicator", "Tactical classification unconfirmed", "NO VISUAL RECORD // PARTIAL FILE",
                    "The Engineer designation is confirmed, but its operational category is not. Sound Erradicator uses improvised acoustic devices —including damaged megaphones and modified portable terminals— thrown as impact charges. No verified photograph or visual capture exists for this unit; the dossier uses a document placeholder instead of a missing texture.",
                    "Use hearing protection when available and spread out when a thrown device is identified. Record reliable visual evidence before reclassifying the unit."),
            dossier("HU-009", "INFORMANT", "UNKNOWN", 0, "N/D", "N/D", "placeholder/classified",
                    "Sin registro confirmado", "Información no recuperada", "Clasificación táctica no confirmada", "SIN REGISTRO VISUAL // ARCHIVO INCOMPLETO",
                    "La designación Informant está confirmada, pero no existe información suficiente para determinar si pertenece a unidades, avanzados u otra clase. Tampoco existe un registro visual verificado; se muestra un marcador documental para evitar una textura ausente.",
                    "Tratar cualquier encuentro como fuente de inteligencia potencial y documentar comunicaciones, equipo, rutas y evidencia visual antes de definir una clasificación.",
                    "No confirmed record", "Information not recovered", "Tactical classification unconfirmed", "NO VISUAL RECORD // INCOMPLETE FILE",
                    "The Informant designation is confirmed, but there is not enough information to determine whether it belongs to units, advanced units or another class. No verified visual record exists either; a document placeholder is shown instead of a missing texture.",
                    "Treat any encounter as a potential intelligence source and document communications, equipment, routes and visual evidence before assigning a classification."),
            dossier("HU-010", "GRAPPLER", "UNKNOWN", 0, "N/D", "N/D", "placeholder/classified",
                    "Sin registro confirmado", "Información no recuperada", "Clasificación táctica no confirmada", "SIN REGISTRO VISUAL // ARCHIVO INCOMPLETO",
                    "Grappler permanece sin categoría operativa confirmada. El expediente actual tampoco contiene un registro visual fiable ni datos suficientes sobre el mecanismo de agarre, alcance, daño o resistencia; el marcador del dossier sustituye la textura inexistente.",
                    "Mantener espacio libre alrededor de obstáculos y compañeros hasta conocer su método de captura. Registrar alcance, preparación y evidencia visual durante el primer contacto.",
                    "No confirmed record", "Information not recovered", "Tactical classification unconfirmed", "NO VISUAL RECORD // INCOMPLETE FILE",
                    "Grappler remains without a confirmed operational category. The current file also lacks a reliable visual record and sufficient data on its grappling mechanism, range, damage or durability; the dossier marker replaces the missing texture.",
                    "Keep clear space around obstacles and teammates until its capture method is known. Record range, preparation and visual evidence during first contact."),
            dossier("HU-011", "TRANQUILIZER", "UNKNOWN", 0, "N/D", "N/D", "placeholder/classified",
                    "Sin registro confirmado", "Información no recuperada", "Clasificación táctica no confirmada", "SIN REGISTRO VISUAL // ARCHIVO INCOMPLETO",
                    "Tranquilizer permanece sin categoría operativa confirmada. No se atribuyen sedantes, armas ni efectos concretos porque el expediente disponible no los confirma. Tampoco existe un registro visual verificado; el dossier muestra un marcador documental.",
                    "Evitar exposición innecesaria y registrar cualquier síntoma fisiológico o evidencia visual sólo después de un contacto observado.",
                    "No confirmed record", "Information not recovered", "Tactical classification unconfirmed", "NO VISUAL RECORD // INCOMPLETE FILE",
                    "Tranquilizer remains without a confirmed operational category. No sedative, weapon or specific effect is assigned because the available file does not confirm them. There is no verified visual record either; the dossier displays a document placeholder.",
                    "Avoid unnecessary exposure and record physiological symptoms or visual evidence only after an observed contact."),
            dossier("HU-012", "SKYDIVER", "UNKNOWN", 0, "N/D", "N/D", "placeholder/classified",
                    "Sin registro confirmado", "Información no recuperada", "Clasificación táctica no confirmada", "SIN REGISTRO VISUAL // ARCHIVO INCOMPLETO",
                    "Skydiver conserva una referencia aerotransportada, pero su categoría oficial dentro de Intel no está confirmada. Altura de inserción, armamento, patrón de aterrizaje e imagen permanecen sin verificar; el dossier usa un marcador documental.",
                    "Vigilar cielo y zonas de descenso abiertas; no asumir un patrón de entrada ni una categoría hasta observarlos directamente.",
                    "No confirmed record", "Information not recovered", "Tactical classification unconfirmed", "NO VISUAL RECORD // INCOMPLETE FILE",
                    "Skydiver retains an airborne reference, but its official Intel category is unconfirmed. Insertion altitude, weapons, landing pattern and imagery remain unverified; the dossier uses a document placeholder.",
                    "Watch the sky and open landing zones; do not assume an entry pattern or category until directly observed."),
            dossier("HU-013", "SKYLINER", "UNKNOWN", 0, "N/D", "N/D", "placeholder/classified",
                    "Sin registro confirmado", "Información no recuperada", "Clasificación táctica no confirmada", "SIN REGISTRO VISUAL // ARCHIVO INCOMPLETO",
                    "Skyliner conserva una referencia aerotransportada, pero su categoría oficial dentro de Intel no está confirmada. No hay datos suficientes para establecer velocidad, carga útil, función táctica exacta ni una imagen verificada; el dossier usa un marcador documental.",
                    "Mantener observación de altura y registrar trayectoria, función y evidencia visual antes de comprometer una reclasificación.",
                    "No confirmed record", "Information not recovered", "Tactical classification unconfirmed", "NO VISUAL RECORD // INCOMPLETE FILE",
                    "Skyliner retains an airborne reference, but its official Intel category is unconfirmed. There is not enough data to establish speed, payload, exact tactical role or verified imagery; the dossier uses a document placeholder.",
                    "Maintain high-angle observation and record trajectory, role and visual evidence before committing to a reclassification."),
            dossier("TNK-006", "AGITATOR", "TANK", 0, "N/D", "N/D", "placeholder/classified",
                    "Sin registro confirmado", "Información no recuperada", "Tank", "SIN REGISTRO VISUAL // ARCHIVO INCOMPLETO",
                    "Agitator está clasificado como Tank. El expediente aún no permite confirmar armamento, defensa, movilidad ni función específica y no dispone de un registro visual verificado; se muestra el marcador documental de Intel.",
                    "Mantener distancia y documentar su primer patrón de ataque antes de fijar un protocolo de combate.",
                    "No confirmed record", "Information not recovered", "Tank", "NO VISUAL RECORD // INCOMPLETE FILE",
                    "Agitator is classified as a Tank. The file does not yet confirm weapons, defence, mobility or a specific battlefield role and has no verified visual record; Intel displays its document placeholder.",
                    "Keep distance and document its first attack pattern before establishing a combat protocol."),
            dossier("ELT-004", "FAUNA", "ELITE", 0, "N/D", "N/D", "placeholder/classified",
                    "Sin registro de origen confirmado", "RPK-74 + visión tridimensional + Hallucinator", "Cloaker Infiltrator / señuelos", "ÉLITE HOSTIL // SIN REGISTRO VISUAL",
                    "Cazadora infiltradora equipada con una RPK-74, gafas de visión tridimensional y un exoesqueleto avanzado capaz de superar protecciones basadas en Compound V. Su grabadora Hallucinator puede generar hasta tres señuelos visuales y reproducir firmas sonoras de ataques falsos para desviar la atención. Puede ser convocada mediante Nexus Rally de Gaia o radios de mando de jefes. No existe una imagen verificada en el archivo actual.",
                    "Verificar contacto visual y procedencia del sonido antes de responder. Separar observadores para comparar ángulos y evitar perseguir una firma aislada sin confirmación física.",
                    "No confirmed origin record", "RPK-74 + three-dimensional vision + Hallucinator", "Cloaker Infiltrator / decoys", "HOSTILE ELITE // NO VISUAL RECORD",
                    "An infiltrator hunter equipped with an RPK-74, three-dimensional vision goggles and an advanced exoskeleton capable of bypassing Compound-V-based protection. Its Hallucinator recorder can create up to three visual decoys and reproduce false attack signatures to misdirect attention. It can be called through Gaia's Nexus Rally or boss command radios. No verified image is available in the current file.",
                    "Verify visual contact and sound origin before responding. Split observers to compare angles and avoid pursuing an isolated signature without physical confirmation."),
            dossier("ELT-005", "CERBERUS", "ELITE", 0, "N/D", "N/D", "placeholder/classified",
                    "Sin registro confirmado", "Información parcial", "Élite", "ÉLITE // SIN REGISTRO VISUAL",
                    "Cerberus está clasificado actualmente como unidad Élite. El expediente confirma una revisión extensa de ataque, defensa y habilidades, pero no contiene valores suficientemente fiables para reproducirlos sin inventar cifras ni una imagen visual verificada.",
                    "Tratarlo como amenaza de nivel Élite y registrar capacidades observadas antes de actualizar el expediente con valores concretos.",
                    "No confirmed record", "Partial information", "Elite", "ELITE // NO VISUAL RECORD",
                    "Cerberus is currently classified as an Elite unit. The file confirms a major revision to attack, defence and abilities, but does not contain sufficiently reliable values to reproduce them without inventing numbers or a verified visual image.",
                    "Treat it as an Elite-level threat and record observed capabilities before updating the dossier with specific values."),
            dossier("ELT-006", "PROTEUS", "ELITE", 0, "N/D", "N/D", "placeholder/classified",
                    "Sin registro confirmado", "Información no recuperada", "Élite", "ÉLITE // SIN REGISTRO VISUAL",
                    "Proteus figura como unidad Élite en expediente preliminar. No se han recuperado datos suficientes para confirmar resistencia, armamento, capacidades actuales ni una imagen visual verificada.",
                    "No completar el expediente por semejanza con otras unidades. Registrar únicamente capacidades observadas de forma directa.",
                    "No confirmed record", "Information not recovered", "Elite", "ELITE // NO VISUAL RECORD",
                    "Proteus appears as an Elite unit in a preliminary dossier. Insufficient data has been recovered to confirm current durability, weapons, capabilities or a verified visual image.",
                    "Do not complete the file by analogy with other units. Record only directly observed capabilities."),
            dossier("SUP-002", "SAURON", "SUPER-UNIT", 5, "23,400,000", "N/D", "placeholder/classified",
                    "Sin registro confirmado", "Información no recuperada", "Operation Exodus", "SUPER UNIT // SIN REGISTRO VISUAL",
                    "Super Unit asociada a Operation Exodus con una resistencia confirmada de 23.400.000 HP. Armamento, defensa, capacidades e imagen visual permanecen sin confirmar.",
                    "Evitar contacto aislado y conservar rutas de retirada. No inferir capacidades a partir de su clasificación o resistencia.",
                    "No confirmed record", "Information not recovered", "Operation Exodus", "SUPER UNIT // NO VISUAL RECORD",
                    "Super Unit associated with Operation Exodus with a confirmed durability of 23,400,000 HP. Weapons, defence, capabilities and visual imagery remain unconfirmed.",
                    "Avoid isolated contact and preserve withdrawal routes. Do not infer capabilities from classification or durability."),
            dossier("SUP-003", "HEDALUS", "SUPER-UNIT", 5, "23,400,000", "N/D", "placeholder/classified",
                    "Sin registro confirmado", "Información no recuperada", "Operation Exodus", "SUPER UNIT // SIN REGISTRO VISUAL",
                    "Super Unit asociada a Operation Exodus con una resistencia confirmada de 23.400.000 HP. Armamento, defensa, capacidades e imagen visual permanecen sin confirmar.",
                    "Evitar contacto aislado y conservar rutas de retirada. No inferir capacidades a partir de su clasificación o resistencia.",
                    "No confirmed record", "Information not recovered", "Operation Exodus", "SUPER UNIT // NO VISUAL RECORD",
                    "Super Unit associated with Operation Exodus with a confirmed durability of 23,400,000 HP. Weapons, defence, capabilities and visual imagery remain unconfirmed.",
                    "Avoid isolated contact and preserve withdrawal routes. Do not infer capabilities from classification or durability."),
            dossier("BOS-010", "SPARTA", "BOSS", 0, "N/D", "N/D", "bosses/classified/frame_00",
                    "Sin registro de origen confirmado", "Shockwave + combate aéreo", "Steadfast / glide", "JEFE HOSTIL // SIN REGISTRO VISUAL",
                    "Sparta dispone de Shockwave y puede responder a ataques a distancia mientras planea mediante desplazamiento aéreo y cortes proyectados. Steadfast deja una ventana de recuperación más larga después de su ejecución, aunque su gestión de habilidades reduce la facilidad de aprovecharla. El archivo no dispone de una imagen verificada y utiliza el dossier clasificado de Boss en su lugar.",
                    "No asumir que una apertura tras Steadfast permanecerá disponible. Mantener separación vertical y lateral cuando entre en planeo y evitar líneas de tiro previsibles.",
                    "No confirmed origin record", "Shockwave + aerial combat", "Steadfast / glide", "HOSTILE BOSS // NO VISUAL RECORD",
                    "Sparta uses Shockwave and can answer ranged attacks while gliding through aerial movement and projected slashes. Steadfast leaves a longer recovery window after execution, although its ability management makes that opening difficult to exploit. The file has no verified image and uses the classified Boss dossier instead.",
                    "Do not assume a Steadfast opening will remain available. Maintain vertical and lateral separation during glide and avoid predictable firing lanes."));

    private static final List<IntelEntry> FILES = Stream.concat(
            IntelData.FILES.stream().map(IntelCatalog::currentOfficialDossier),
            SUPPLEMENTAL.stream()).toList();
    private static final Map<String, List<IntelEntry>> GROUPS = Map.copyOf(FILES.stream().collect(Collectors.groupingBy(
            IntelEntry::category, Collectors.collectingAndThen(Collectors.toList(), List::copyOf))));
    private static final Map<String, IntelEntry> BY_CODE = Map.copyOf(FILES.stream().collect(Collectors.toMap(
            IntelEntry::code, Function.identity())));
    private static final List<IntelEntry> PREVIEW = FILES.stream().filter(e -> e.category().equals("UNIT") || e.category().equals("ADVANCED")).toList();

    static {
        Set<String> codes = new java.util.HashSet<>();
        Set<String> categories = Set.of("UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT", "UNKNOWN");
        for (IntelEntry e : FILES) {
            if (!codes.add(e.code()) || !categories.contains(e.category()) || e.image().isBlank()
                    || e.name().isBlank() || e.hp().isBlank() || e.threat() < 0 || e.threat() > 5
                    || !validPrefix(e.category(), e.code()))
                throw new IllegalStateException("Invalid Intel record: " + e.code());
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
        if (BY_CODE.get("SOP-002") == null || BY_CODE.get("HU-008") == null || BY_CODE.get("ELT-004") == null)
            throw new IllegalStateException("Current unit roster is incomplete");
    }

    private IntelCatalog() {}

    private static IntelEntry currentOfficialDossier(IntelEntry entry) {
        return switch (entry.code()) {
            case "TNK-003" -> agreement(entry);
            case "BOS-002" -> fusilier(entry);
            case "BOS-004" -> trident(entry);
            case "ADV-001" -> specialist(entry);
            case "ADV-004" -> cloaker(entry);
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

    private static IntelEntry fusilier(IntelEntry entry) {
        return replaceText(entry,
                new IntelEntry.IntelText(
                        "Sin registro de origen confirmado",
                        "Lanzagranadas de seis disparos + granadas nucleares/incendiarias + pala",
                        "Modo Mortero / Blox Drink / strafing mejorado",
                        "JEFE HOSTIL // EXPEDIENTE ACTUAL",
                        "Fusilier mantiene una función de bombardeo pesado. Dispone de granadas nucleares con una ventana aproximada de seis segundos para abandonar la zona, granadas incendiarias de área y un patrón de esquive lateral mejorado. En Modo Mortero dispara al cielo y controla las zonas de caída con distintos tipos de munición; su barra de jefe se vuelve blanca mientras el modo está activo. Blox Drink acelera recarga, cadencia y recuperación de habilidades mientras deteriora su propia visión.",
                        "Salir de la zona de impacto apenas aparezca una amenaza nuclear. Un explosivo correctamente sincronizado puede impedir que complete Blox Drink. Con la barra blanca, asumir bombardeo indirecto y cambiar de posición antes de la caída de los proyectiles."),
                new IntelEntry.IntelText(
                        "No confirmed origin record",
                        "Six-shot grenade launcher + nuclear/incendiary grenades + shovel",
                        "Mortar Mode / Blox Drink / improved strafing",
                        "HOSTILE BOSS // CURRENT DOSSIER",
                        "Fusilier retains a heavy-bombardment role. It carries nuclear grenades with an approximately six-second window to clear the area, area incendiary grenades and improved evasive strafing. In Mortar Mode it fires skyward and controls impact zones with multiple ammunition types; its boss bar turns white while the mode is active. Blox Drink speeds reload, fire rate and ability recovery while degrading its own vision.",
                        "Leave the impact zone as soon as a nuclear threat appears. A correctly timed explosive can interrupt Blox Drink. With the bar white, assume indirect bombardment and relocate before the projectiles land."));
    }

    private static IntelEntry trident(IntelEntry entry) {
        return replaceText(entry,
                new IntelEntry.IntelText(
                        "Sin registro de origen confirmado",
                        "Machete de alta frecuencia + gancho + visor con dos estados",
                        "VISOR PUESTO / VISOR REMOVIDO",
                        "JEFE HOSTIL // EXPEDIENTE ACTUAL",
                        "Trident alterna dos configuraciones de visor. Con VISOR PUESTO mantiene 40% de resistencia general, protege completamente la cabeza frente a disparos y su gancho tiene más dificultad para fijar un objetivo. Con VISOR REMOVIDO baja a 10% de resistencia y queda expuesto a disparos a la cabeza y flashbangs, pero puede interceptar de inmediato el primer ataque cuerpo a cuerpo y su gancho conecta con mayor facilidad, tira con menos resistencia y recoge a la víctima con más velocidad. Un aliado enganchado puede ser liberado disparando al torso del cautivo sin alcanzar zonas vitales; la víctima también puede cortar la cuerda si dispone de una hoja suficientemente eficaz.",
                        "Identificar el estado del visor antes de entrar en rango. No iniciar combate cuerpo a cuerpo con el visor removido. Coordinar el rescate de una víctima enganchada y aprovechar la menor resistencia defensiva del modo sin visor con fuego a distancia o granadas cegadoras."),
                new IntelEntry.IntelText(
                        "No confirmed origin record",
                        "High-frequency machete + hook + two-state visor",
                        "VISOR ON / VISOR REMOVED",
                        "HOSTILE BOSS // CURRENT DOSSIER",
                        "Trident switches between two visor configurations. With the VISOR ON it keeps 40% general damage resistance, fully protects the head from gunfire and has greater difficulty securing a hook. With the VISOR REMOVED resistance drops to 10% and it becomes vulnerable to headshots and flashbangs, but can immediately intercept the first incoming melee strike and the hook becomes easier to land, pulls against less resistance and reels the casualty faster. A hooked ally can be freed by shooting the captive's torso without striking vital areas; the victim can also cut the rope with a sufficiently effective blade.",
                        "Identify the visor state before entering range. Do not initiate melee while the visor is removed. Coordinate rescue of a hooked casualty and exploit the reduced visor-off resistance with ranged fire or flashbangs."));
    }

    private static IntelEntry specialist(IntelEntry entry) {
        var es = entry.spanish();
        var en = entry.english();
        return replaceText(entry,
                new IntelEntry.IntelText(es.origin(), es.armament(), "Planificador / líder / Stop Time", es.status(),
                        es.description() + " También dispone de Stop Time como capacidad especial confirmada.", es.advisory()),
                new IntelEntry.IntelText(en.origin(), en.armament(), "Planner / leader / Stop Time", en.status(),
                        en.description() + " Stop Time is also confirmed as one of its special capabilities.", en.advisory()));
    }

    private static IntelEntry cloaker(IntelEntry entry) {
        var es = entry.spanish();
        var en = entry.english();
        return replaceText(entry,
                new IntelEntry.IntelText(es.origin(), es.armament(), es.variants(), es.status(),
                        es.description() + " Algunos Cloakers presentan capacidad de parry; no debe asumirse que todos la poseen.", es.advisory()),
                new IntelEntry.IntelText(en.origin(), en.armament(), en.variants(), en.status(),
                        en.description() + " Some Cloakers display a parry capability; it must not be assumed on every individual.", en.advisory()));
    }

    private static IntelEntry dossier(String code, String name, String category, int threat, String hp, String defense, String image,
                                      String esOrigin, String esArmament, String esVariants, String esStatus, String esDescription, String esAdvisory,
                                      String enOrigin, String enArmament, String enVariants, String enStatus, String enDescription, String enAdvisory) {
        return new IntelEntry(code, name, category, threat, hp, defense, image,
                new IntelEntry.IntelText(esOrigin, esArmament, esVariants, esStatus, esDescription, esAdvisory),
                new IntelEntry.IntelText(enOrigin, enArmament, enVariants, enStatus, enDescription, enAdvisory));
    }

    private static IntelEntry replaceText(IntelEntry entry, IntelEntry.IntelText spanish, IntelEntry.IntelText english) {
        return new IntelEntry(entry.code(), entry.name(), entry.category(), entry.threat(), entry.hp(), entry.defense(), entry.image(), spanish, english);
    }

    private static boolean containsFieldReportMaterial(IntelEntry.IntelText text) {
        String combined = (text.origin() + " " + text.armament() + " " + text.variants() + " " + text.status() + " "
                + text.description() + " " + text.advisory()).toUpperCase(Locale.ROOT);
        return combined.contains("GATE") || combined.contains("RIFT") || combined.contains("RICK SANCHEZ")
                || combined.contains("SABOTAJE") || combined.contains("SABOTAGE")
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
            case "UNKNOWN" -> code.startsWith("HU-") || code.startsWith("UNK-");
            default -> false;
        };
    }
}
