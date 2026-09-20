package uy.santipdr.siege.client;

import java.util.Comparator;
import java.util.List;

/**
 * Intel field manual. This is current operational knowledge, not a changelog.
 * Unit dossiers live in IntelCatalog; this manual is reserved for missions,
 * casualty/medical states and operational protocols that do not belong to one unit.
 */
public final class SiegeArchiveData {
    private SiegeArchiveData() { }

    public enum Category {
        MISSIONS("MISIONES", "MISSIONS"),
        CONDITIONS("ESTADOS", "CONDITIONS"),
        PROTOCOLS("PROTOCOLOS", "PROTOCOLS");
        private final String es, en;
        Category(String es, String en) { this.es = es; this.en = en; }
        public String title(boolean spanish) { return spanish ? es : en; }
    }

    public enum Source {
        SIEGE("SIEGE · INTEL", "SIEGE · INTEL"),
        DVN_REFERENCE("REFERENCIA DVN", "DVN REFERENCE"),
        MEDICAL("PROTOCOLO MÉDICO", "MEDICAL PROTOCOL");
        private final String es, en;
        Source(String es, String en) { this.es = es; this.en = en; }
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    public record Entry(String id, Category category, int rank, Source source,
                        String titleEs, String titleEn, String bodyEs, String bodyEn) {
        public String title(boolean spanish) { return spanish ? titleEs : titleEn; }
        public String body(boolean spanish) { return spanish ? bodyEs : bodyEn; }
        public String searchable(boolean spanish) {
            return title(spanish) + " " + body(spanish) + " " + source.label(spanish);
        }
    }

    private static Entry e(String id, Category category, int rank, Source source,
                           String titleEs, String titleEn, String bodyEs, String bodyEn) {
        return new Entry(id, category, rank, source, titleEs, titleEn, bodyEs, bodyEn);
    }

    public static final List<Entry> ENTRIES = List.of(
        e("operation-exodus", Category.MISSIONS, 1000, Source.SIEGE,
            "OPERATION EXODUS", "OPERATION EXODUS",
            "CLASIFICACIÓN\nEvento catastrófico de incidentes múltiples. Su despliegue puede producirse cuando la intensidad de la operación supera Dynamic 100%."
            + "\n\nSUPER UNITS\nSAURON · 23.400.000 HP.\nHEDALUS · 23.400.000 HP."
            + "\n\nCONDUCTA OPERATIVA\nLa presencia simultánea de amenazas de esta escala obliga a abandonar posiciones aisladas, mantener rutas de retirada abiertas y concentrar recursos antes de entrar en contacto.",
            "CLASSIFICATION\nCatastrophic multi-incident operation. Deployment may occur when operational intensity exceeds Dynamic 100%."
            + "\n\nSUPER UNITS\nSAURON · 23,400,000 HP.\nHEDALUS · 23,400,000 HP."
            + "\n\nOPERATIONAL RESPONSE\nSimultaneous threats at this scale require abandoning isolated positions, keeping withdrawal routes open and consolidating resources before contact."),

        e("endless-inferno", Category.MISSIONS, 990, Source.SIEGE,
            "ENDLESS INFERNO", "ENDLESS INFERNO",
            "MISIÓN\nOperación de combate continuo sobre terreno abierto. La construcción permanece disponible y el inventario se conserva durante el despliegue. La inteligencia enemiga es mayor que en operaciones convencionales, por lo que una fuerza dispersa pierde capacidad de respuesta con rapidez."
            + "\n\nMAPAS CONFIRMADOS\nGreat Pyramid of Giza.\nBramblewick."
            + "\n\nRECOMENDACIÓN\nMantener sectores de apoyo mutuo, reservas de munición y una ruta de repliegue compartida. La libertad de movimiento no sustituye la coordinación.",
            "MISSION\nContinuous-combat operation across open terrain. Construction remains available and inventory is retained during deployment. Enemy intelligence is higher than in conventional operations, so a dispersed force rapidly loses response capability."
            + "\n\nCONFIRMED MAPS\nGreat Pyramid of Giza.\nBramblewick."
            + "\n\nRECOMMENDATION\nMaintain mutually supporting sectors, ammunition reserves and a shared withdrawal route. Freedom of movement does not replace coordination."),

        e("casualty-severity", Category.CONDITIONS, 900, Source.DVN_REFERENCE,
            "Clasificación de daño corporal", "Body-trauma classification",
            "SECUENCIA OPERATIVA\nDowned / Muerto reanimable → Mangled → Mutilated → Dismembered → Disfigured. La clasificación describe el estado físico del cuerpo y la posibilidad de recuperación; no es una simple etiqueta visual."
            + "\n\nDOWNED / MUERTO REANIMABLE\nAusencia reciente de respuesta o pulso sin destrucción corporal incompatible con la vida. La reanimación eléctrica sólo sirve cuando el ritmo cardíaco y el contexto lo permiten; debe acompañarse de control de vía aérea, respiración y hemorragias."
            + "\n\nMANGLED\nTrauma grave con daño extenso, pero todavía con anatomía suficiente para intentar estabilización y reanimación. La referencia de Dummies vs Noobs lo distingue de Downed y exige una intervención más prolongada."
            + "\n\nMUTILATED\nTrauma catastrófico. La referencia de Dummies vs Noobs indica que un desfibrilador de campaña normal no puede devolver a la víctima en este estado. La prioridad es confirmar viabilidad, controlar amenazas inmediatas y evacuar si existe posibilidad de supervivencia."
            + "\n\nDISMEMBERED\nExiste pérdida o separación de una extremidad o de una parte corporal importante. Se controla primero la hemorragia, se inmoviliza lo que permanezca unido y se evacua con urgencia. Un desfibrilador no repara la pérdida estructural."
            + "\n\nDISFIGURED\nEstado transitorio de daño corporal extremo. Mientras esta clasificación permanezca activa no admite corrección definitiva; sólo estabilización, protección de la vía aérea, control de hemorragias y vigilancia hasta que el estado cambie y pueda reevaluarse.",
            "OPERATIONAL SEQUENCE\nDowned / recoverable death → Mangled → Mutilated → Dismembered → Disfigured. The classification describes physical body condition and recovery potential; it is not merely a visual label."
            + "\n\nDOWNED / RECOVERABLE DEATH\nRecent unresponsiveness or loss of pulse without body destruction incompatible with life. Electrical defibrillation only applies when the cardiac rhythm and circumstances permit it and must be paired with airway, breathing and bleeding control."
            + "\n\nMANGLED\nSevere trauma with extensive damage but enough anatomical integrity to attempt stabilisation and resuscitation. The Dummies vs Noobs reference distinguishes it from Downed and requires a longer intervention."
            + "\n\nMUTILATED\nCatastrophic trauma. The Dummies vs Noobs reference states that a standard field defibrillator cannot restore a casualty in this condition. Priority shifts to viability assessment, immediate threat control and evacuation if survival remains possible."
            + "\n\nDISMEMBERED\nA limb or other major body segment has been lost or separated. Control haemorrhage first, immobilise what remains attached and evacuate urgently. A defibrillator cannot repair structural loss."
            + "\n\nDISFIGURED\nA temporary state of extreme bodily trauma. While this classification remains active, no definitive correction is possible; only stabilisation, airway protection, bleeding control and observation until the condition transitions and can be reassessed."),

        e("bleeding", Category.CONDITIONS, 890, Source.MEDICAL,
            "BLEEDING · hemorragia activa", "BLEEDING · active haemorrhage",
            "RIESGO\nLa pérdida continua de sangre reduce rápidamente la capacidad de mantener conciencia, presión y perfusión. La prioridad es detener la hemorragia antes de intentar desplazamientos innecesarios."
            + "\n\nRESPUESTA\nAplicar presión directa firme con material limpio y mantenerla. Si la herida es profunda, el empaquetado con gasa puede ser necesario; en una hemorragia grave de una extremidad puede requerirse un torniquete proximal. Una herida sólo debe suturarse después de controlar la hemorragia, limpiarla y evaluarla, y por personal con formación adecuada."
            + "\n\nDESPUÉS\nCubrir la lesión, limitar el movimiento y vigilar signos de shock mientras se organiza evacuación o tratamiento definitivo.",
            "RISK\nOngoing blood loss rapidly reduces consciousness, pressure and tissue perfusion. Stop the haemorrhage before unnecessary movement."
            + "\n\nRESPONSE\nApply firm direct pressure with clean material and keep it in place. Deep wounds may require gauze packing; severe limb haemorrhage may require a proximal tourniquet. Suturing should only occur after bleeding is controlled, the wound is cleaned and assessed, and by appropriately trained personnel."
            + "\n\nAFTERWARDS\nCover the injury, limit movement and watch for shock while arranging evacuation or definitive treatment."),

        e("burned", Category.CONDITIONS, 880, Source.MEDICAL,
            "BURNED · lesión térmica", "BURNED · thermal injury",
            "PRIORIDAD\nAlejar a la víctima de la fuente de calor y extinguir cualquier llama o material que continúe ardiendo. Si la ropa está adherida a la piel no se arranca."
            + "\n\nENFRIAMIENTO\nEnfriar la zona con agua limpia y fresca durante un período sostenido cuando sea posible. No aplicar hielo directamente ni sustancias que atrapen calor sobre la quemadura."
            + "\n\nPROTECCIÓN\nRetirar objetos que puedan comprimir al aparecer hinchazón, cubrir con material limpio no adherente y vigilar respiración, temperatura y signos de shock. Las quemaduras extensas, eléctricas, químicas o de la cara y vía aérea requieren evacuación prioritaria.",
            "PRIORITY\nMove the casualty away from the heat source and extinguish any flame or material that is still burning. Do not tear away clothing that has adhered to skin."
            + "\n\nCOOLING\nCool the area with clean cool water for a sustained period when possible. Do not place ice directly on the burn or apply substances that trap heat."
            + "\n\nPROTECTION\nRemove items that may constrict as swelling develops, cover with clean non-adherent material and monitor breathing, temperature and shock. Extensive, electrical, chemical, facial or airway burns require priority evacuation."),

        e("erased", Category.CONDITIONS, 870, Source.SIEGE,
            "ERASED · pérdida total", "ERASED · total loss",
            "DEFINICIÓN\nClasificación reservada para incidentes en los que ya no existe integridad corporal suficiente para aplicar reanimación convencional. No se equipara automáticamente a Mutilated ni a Dismembered."
            + "\n\nPROTOCOLO\nConfirmar que la zona sea segura, registrar el incidente y priorizar a las demás víctimas recuperables. No se inicia desfibrilación cuando no existe anatomía capaz de sostener una recuperación.",
            "DEFINITION\nReserved for incidents in which insufficient bodily integrity remains for conventional resuscitation. It is not automatically equivalent to Mutilated or Dismembered."
            + "\n\nPROTOCOL\nConfirm scene safety, record the incident and prioritise other recoverable casualties. Defibrillation is not initiated when no anatomy remains capable of supporting recovery."),

        e("shellshock", Category.PROTOCOLS, 800, Source.SIEGE,
            "SHELLSHOCK · 150%", "SHELLSHOCK · 150%",
            "INTENSIDAD\nSHELLSHOCK está fijada en 150%, entre Dynamic 100% y los niveles superiores de intensidad."
            + "\n\nPREPARACIÓN\nEntrar con redundancia médica, munición de reserva, rutas de retirada y funciones de escuadra definidas. El aumento de presión no debe compensarse separando al equipo ni agotando recursos al principio del operativo.",
            "INTENSITY\nSHELLSHOCK is fixed at 150%, between Dynamic 100% and the higher intensity levels."
            + "\n\nPREPARATION\nEnter with medical redundancy, reserve ammunition, withdrawal routes and defined squad roles. Increased pressure should not be answered by splitting the team or exhausting resources early in the operation."),

        e("casualty-priority", Category.PROTOCOLS, 790, Source.MEDICAL,
            "Prioridad de atención", "Casualty priority",
            "ORDEN\nPrimero se elimina el peligro inmediato. Después se comprueban vía aérea, respiración, circulación y hemorragias masivas. Las lesiones espectaculares no siempre son la amenaza más urgente."
            + "\n\nREANIMACIÓN\nUn desfibrilador corrige determinados ritmos cardíacos; no cierra heridas, no repone sangre y no reconstruye tejido. Toda reanimación debe ir acompañada de tratamiento de la causa que produjo el colapso."
            + "\n\nEVACUACIÓN\nLas víctimas con deterioro progresivo, pérdida importante de sangre, lesión de vía aérea, amputación, quemaduras extensas o alteración neurológica requieren evacuación prioritaria.",
            "ORDER\nRemove the immediate danger first. Then assess airway, breathing, circulation and massive haemorrhage. Visually dramatic injuries are not always the most immediate threat."
            + "\n\nRESUSCITATION\nA defibrillator corrects specific cardiac rhythms; it does not close wounds, replace blood or rebuild tissue. Resuscitation must be paired with treatment of the cause of collapse."
            + "\n\nEVACUATION\nCasualties with progressive deterioration, major blood loss, airway injury, amputation, extensive burns or neurological changes require priority evacuation.")
    );

    private static final List<Entry> SORTED = ENTRIES.stream()
            .sorted(Comparator.comparingInt(Entry::rank).reversed())
            .toList();

    static {
        java.util.HashSet<String> ids = new java.util.HashSet<>();
        for (Entry entry : ENTRIES) {
            if (!ids.add(entry.id())) throw new IllegalStateException("Duplicate field-manual id: " + entry.id());
            if (entry.titleEs().toLowerCase(java.util.Locale.ROOT).contains("añadid")
                    || entry.bodyEs().toLowerCase(java.util.Locale.ROOT).contains("fue añadido")
                    || entry.bodyEs().toLowerCase(java.util.Locale.ROOT).contains("fueron añadidos")
                    || entry.bodyEs().toLowerCase(java.util.Locale.ROOT).contains("re-añad"))
                throw new IllegalStateException("Field manual contains changelog language: " + entry.id());
        }
    }

    public static List<Entry> entries(Category category, String query, boolean spanish) {
        var matcher = IntelSearch.compile(query);
        return SORTED.stream()
                .filter(e -> e.category() == category)
                .filter(e -> matcher.test(e.searchable(spanish)))
                .toList();
    }

    public static List<Entry> all() { return SORTED; }
    public static int total() { return ENTRIES.size(); }
}
