package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Source-aware knowledge imported for SIEGE 3.00 from the audited Eternal Craft
 * Discord research and the living SIEGE notebook. The two zones deliberately
 * stay separate: CURRENT is the player's latest recorded state, while
 * ENCYCLOPEDIA contains server knowledge that can be historical or uncertain.
 *
 * This class is client-side reference data only. It does not modify races,
 * energy, inventories, server rules or gameplay.
 */
public final class SiegeKnowledgeData {
    public enum Zone { CURRENT, ENCYCLOPEDIA }

    public enum Domain {
        OVERVIEW("RESUMEN", "OVERVIEW"),
        PROGRESSION("PROGRESIÓN", "PROGRESSION"),
        EXECUTORS("EJECUTORES", "EXECUTORS"),
        TRIALS("TRIALS", "TRIALS"),
        STRUCTURES("ESTRUCTURAS", "STRUCTURES"),
        BOSSES("BOSSES", "BOSSES"),
        MISSIONS("MISIONES", "MISSIONS"),
        NPCS("NPCS", "NPCS"),
        RACES("RAZAS", "RACES"),
        PETS("PETS / SUMMONS", "PETS / SUMMONS"),
        ABILITIES("HABILIDADES", "ABILITIES"),
        ENERGIES("ENERGÍAS", "ENERGIES"),
        MEDITATION("MEDITACIÓN", "MEDITATION"),
        MAGIC("MAGIA", "MAGIC"),
        ITEMS("ITEMS", "ITEMS"),
        CRAFTING("CRAFTING", "CRAFTING"),
        ASSEMBLING("ASSEMBLING / CYBORGS", "ASSEMBLING / CYBORGS"),
        RELICS("RELIQUIAS", "RELICS"),
        DIMENSIONS("DIMENSIONES", "DIMENSIONS"),
        RITUALS("RITUALES", "RITUALS"),
        HACKING("SABOTAJE / HACKING", "SABOTAGE / HACKING"),
        DEATH_REVIVE("MUERTE / HERIDAS / REVIVE", "DEATH / INJURY / REVIVE"),
        RAIDS_EVENTS("RAIDS / EVENTOS", "RAIDS / EVENTS"),
        FACTIONS("FACCIONES", "FACTIONS"),
        ECONOMY("ECONOMÍA / WINS", "ECONOMY / WINS"),
        PROMPTS("PROMPTS / COMANDOS", "PROMPTS / COMMANDS"),
        CONSEQUENCES("ACCIÓN → CONSECUENCIA", "ACTION → CONSEQUENCE"),
        HIDDEN("MECÁNICAS OCULTAS", "HIDDEN MECHANICS"),
        MISTAKES("COSAS QUE HICIMOS MAL", "MISTAKES"),
        ALEX("CONSEJOS DE ALEX", "ALEX ADVICE"),
        HISTORY("HISTÓRICO", "HISTORY"),
        MYSTERIES("MISTERIOS", "MYSTERIES"),
        CONTRADICTIONS("CAMBIOS / CONTRADICCIONES", "CHANGES / CONTRADICTIONS"),
        SOURCES("FUENTES", "SOURCES");

        private final String es, en;
        Domain(String es, String en) { this.es = es; this.en = en; }
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    public enum Confidence {
        CURRENT_CONFIRMED("ACTUAL CONFIRMADO", "CURRENT CONFIRMED"),
        ALEX_CONFIRMED("CONFIRMADO POR ALEX", "ALEX CONFIRMED"),
        SYSTEM_OBSERVED("SISTEMA / OBSERVADO", "SYSTEM / OBSERVED"),
        PLAYER_EXPERIENCE("JUGADOR / EXPERIENCIA", "PLAYER / EXPERIENCE"),
        HISTORICAL("HISTÓRICO", "HISTORICAL"),
        UNCONFIRMED("NO CONFIRMADO", "UNCONFIRMED"),
        CONTRADICTION("CONTRADICCIÓN", "CONTRADICTION");

        private final String es, en;
        Confidence(String es, String en) { this.es = es; this.en = en; }
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    public record Source(Confidence confidence, String date, String channel, String noteEs, String noteEn) {
        public Source {
            confidence = confidence == null ? Confidence.UNCONFIRMED : confidence;
            date = safe(date);
            channel = safe(channel);
            noteEs = safe(noteEs);
            noteEn = safe(noteEn);
        }
        public String note(boolean spanish) { return spanish ? noteEs : noteEn; }
    }

    public record Entry(String id, Zone zone, Domain domain, String titleEs, String titleEn,
                        String summaryEs, String summaryEn, String bodyEs, String bodyEn,
                        boolean spoiler, boolean critical, List<String> related, List<Source> sources) {
        public Entry {
            id = safe(id);
            zone = zone == null ? Zone.ENCYCLOPEDIA : zone;
            domain = domain == null ? Domain.OVERVIEW : domain;
            titleEs = safe(titleEs); titleEn = safe(titleEn);
            summaryEs = safe(summaryEs); summaryEn = safe(summaryEn);
            bodyEs = safe(bodyEs); bodyEn = safe(bodyEn);
            related = related == null ? List.of() : List.copyOf(related);
            sources = sources == null ? List.of() : List.copyOf(sources);
            if (id.isBlank()) throw new IllegalArgumentException("knowledge id");
        }
        public String title(boolean spanish) { return spanish ? titleEs : titleEn; }
        public String summary(boolean spanish) { return spanish ? summaryEs : summaryEn; }
        public String body(boolean spanish) { return spanish ? bodyEs : bodyEn; }
        public Confidence confidence() {
            return sources.isEmpty() ? Confidence.UNCONFIRMED : sources.get(0).confidence();
        }
    }

    private SiegeKnowledgeData() { }

    private static Source src(Confidence confidence, String date, String channel, String es, String en) {
        return new Source(confidence, date, channel, es, en);
    }

    private static Entry e(String id, Zone zone, Domain domain, String es, String en,
                           String summaryEs, String summaryEn, String bodyEs, String bodyEn,
                           boolean spoiler, boolean critical, List<String> related, Source... sources) {
        return new Entry(id, zone, domain, es, en, summaryEs, summaryEn, bodyEs, bodyEn,
                spoiler, critical, related, List.of(sources));
    }

    /**
     * Records intentionally preserve uncertainty and dates from the research.
     * "Current" entries are snapshots from the living player notebook and must
     * not be mistaken for server-authoritative mechanics.
     */
    private static final List<Entry> ENTRIES = List.of(
        e("audit-discord-export", Zone.ENCYCLOPEDIA, Domain.SOURCES,
            "Auditoría del archivo de Discord", "Discord archive audit",
            "251.065 mensajes procesados; 72.384 de Alex; 29.605 de Santi; 37.435 replies reconstruidas.",
            "251,065 messages processed; 72,384 from Alex; 29,605 from Santi; 37,435 reconstructed replies.",
            "La investigación recuperada declara 488 archivos procesados, 251.065 mensajes, 72.384 mensajes de Alex, 29.605 de Santi, 37.435 respuestas reconstruidas, 153 prompts clasificados, 35 documentos temáticos, transcripciones y un índice SQLite/FTS. Esta auditoría describe el corpus usado para la enciclopedia; no convierte automáticamente cada comentario de Discord en una regla oficial.",
            "The recovered research reports 488 processed files, 251,065 messages, 72,384 messages from Alex, 29,605 from Santi, 37,435 reconstructed replies, 153 classified prompts, 35 thematic documents, transcripts and a SQLite/FTS index. This audit describes the corpus behind the encyclopedia; it does not automatically turn every Discord comment into an official rule.",
            false, false, List.of("source-priority"),
            src(Confidence.SYSTEM_OBSERVED, "21/09/2026", "Research audit", "Conteos del procesamiento del export.", "Export processing counts.")),

        e("source-priority", Zone.ENCYCLOPEDIA, Domain.SOURCES,
            "Jerarquía de confianza", "Source confidence hierarchy",
            "Staff y resultados observados pesan más que comentarios, especulación o recuerdos aislados.",
            "Staff and observed results outweigh comments, speculation or isolated recollections.",
            "Orden de trabajo de la investigación: 1) Alex/staff explicando directamente; 2) resultado mostrado por el sistema; 3) experimentos repetibles; 4) experiencia/comentarios de jugadores; 5) especulación o bromas. Cuando una mecánica cambia, la versión vieja se conserva como histórica en vez de sobrescribirse silenciosamente.",
            "Research working order: 1) Alex/staff explaining directly; 2) system-shown result; 3) repeatable experiments; 4) player experience/comments; 5) speculation or jokes. When a mechanic changes, the old version is preserved as historical rather than silently overwritten.",
            false, false, List.of("audit-discord-export"),
            src(Confidence.SYSTEM_OBSERVED, "21/09/2026", "Research method", "Política de fuentes del proyecto.", "Project source policy.")),

        e("current-deteriorer-snapshot", Zone.CURRENT, Domain.RACES,
            "Deteriorer · último registro", "Deteriorer · latest record",
            "Snapshot del bloc: Deteriorer, rareza Obsainan, 200 HP y energía RE / Rust Energy.",
            "Notebook snapshot: Deteriorer, Obsainan rarity, 200 HP and RE / Rust Energy.",
            "Último estado registrado en el bloc de SIEGE: raza Deteriorer; rareza anotada como Obsainan; la raza añadió +100 HP sobre la base y dejó 200 HP totales. La energía racial se registra como RE / Rust Energy. Este bloque es un snapshot del jugador, no una lectura en vivo del servidor.",
            "Latest state recorded in the SIEGE notebook: Deteriorer race; rarity recorded as Obsainan; the race added +100 HP over base and left 200 total HP. Racial energy is recorded as RE / Rust Energy. This is a player notebook snapshot, not a live server reading.",
            false, false, List.of("current-oxidation", "current-rust-guard", "current-meditation"),
            src(Confidence.CURRENT_CONFIRMED, "20/09/2026", "SIEGE current notebook", "Registro actual del jugador.", "Current player record.")),

        e("current-oxidation", Zone.CURRENT, Domain.ABILITIES,
            "Oxidación Recta", "Straight Oxidation",
            "Habilidad inicial registrada de Deteriorer; requiere control para no propagar desgaste de forma indeseada.",
            "Recorded starting Deteriorer ability; requires control to avoid unwanted spread.",
            "Oxidación Recta aparece como habilidad inicial de Deteriorer en el registro actual. El uso práctico debe mantenerse controlado: definir dirección, alcance y qué puede afectar antes de experimentar cerca de estructuras o aliados.",
            "Straight Oxidation appears as the starting Deteriorer ability in the current record. Practical use should stay controlled: define direction, range and what it may affect before experimenting near structures or allies.",
            false, true, List.of("alex-raid-oxidation", "prompt-precision"),
            src(Confidence.CURRENT_CONFIRMED, "20/09/2026", "SIEGE current notebook", "Habilidad poseída registrada.", "Recorded owned ability.")),

        e("current-rust-guard", Zone.CURRENT, Domain.ABILITIES,
            "Rust Guard", "Rust Guard",
            "25 s; área de 2 bloques; 8 RE/s; oxida hostiles y proyectiles registrados como balas.",
            "25 s; 2-block area; 8 RE/s; oxidizes hostiles and projectiles recorded as bullets.",
            "Rust Guard fue creada y registrada en el bloc actual. Dura 25 segundos, mantiene un área de oxidación de 2 bloques alrededor del cuerpo, afecta entidades hostiles y proyectiles descritos como balas, y consume 8 RE por segundo. El valor de RE anotado durante ese registro era 1000/1000.",
            "Rust Guard was created and recorded in the current notebook. It lasts 25 seconds, keeps a 2-block oxidation area around the body, affects hostile entities and projectiles described as bullets, and consumes 8 RE per second. The RE value written in that snapshot was 1000/1000.",
            false, true, List.of("current-deteriorer-snapshot", "alex-raid-oxidation", "alex-adaptation"),
            src(Confidence.CURRENT_CONFIRMED, "20/09/2026", "SIEGE current notebook", "Resultado registrado por el jugador.", "Player-recorded result.")),

        e("current-meditation", Zone.CURRENT, Domain.MEDITATION,
            "Meditación · uso actual", "Meditation · current use",
            "En el bloc actual sirve para recuperar RE y también se registró que puede aumentar el máximo disponible.",
            "In the current notebook it restores RE and was also recorded as able to raise the available maximum.",
            "La función actual documentada de la meditación es recuperar RE y aumentar el máximo disponible con entrenamiento. Antes de usarla conviene revisar la barra: existe una advertencia de Alex fechada en enero de 2026 sobre sobrecarga al meditar con RE completa. Esa advertencia histórica se muestra por separado para no confundirla con una lectura viva.",
            "The currently documented role of meditation is to restore RE and increase the available maximum through training. Before using it, check the bar: an Alex warning dated January 2026 describes overload when meditating at full RE. That historical warning is shown separately so it is not mistaken for live state.",
            false, true, List.of("alex-meditation-overload", "alex-meditation-rate", "command-ver-barra"),
            src(Confidence.CURRENT_CONFIRMED, "20/09/2026", "SIEGE current notebook", "Uso actual anotado.", "Current recorded use.")),

        e("current-next-actions", Zone.CURRENT, Domain.PROGRESSION,
            "Prioridades de campo", "Field priorities",
            "Revisar RE, mantener movilidad, investigar antes de desmontar y registrar cada prueba.",
            "Check RE, keep mobility, research before dismantling and log every experiment.",
            "Prioridades prácticas derivadas del bloc y del informe: usar *Ver barra* antes de acciones de energía; no probar oxidación abierta durante una raid; mantener una opción de movilidad/escape; investigar reliquias antes de modificarlas; no desmontar una reliquia sin comprobar el requisito del Daemonium Kit; variar tácticas ante enemigos adaptativos; registrar prompt, coste, resultado y consecuencia.",
            "Practical priorities derived from the notebook and report: use *View bar* before energy actions; do not test unrestricted oxidation during a raid; keep a mobility/escape option; research relics before modifying them; do not dismantle a relic without checking the Daemonium Kit requirement; vary tactics against adaptive enemies; record prompt, cost, result and consequence.",
            false, true, List.of("command-ver-barra", "item-geography-table", "item-daemonium-kit", "alex-adaptation"),
            src(Confidence.CURRENT_CONFIRMED, "21/09/2026", "SIEGE current notebook", "Lista operativa para la partida actual.", "Operational list for the current run.")),

        e("alex-deteriorer-wear", Zone.ENCYCLOPEDIA, Domain.RACES,
            "Deteriorer · desgaste progresivo", "Deteriorer · progressive wear",
            "Alex lo describió como desgaste progresivo, no como daño instantáneo.",
            "Alex described it as progressive wear rather than instant damage.",
            "Registro del 12/11/2025: Deteriorer se explicó como un efecto de deterioro progresivo. La investigación también conserva una nota histórica: en versiones antiguas ciertos debuffs podían durar hasta 5 días reales; el propio informe indica que esa duración fue reducida posteriormente.",
            "Record dated 12/11/2025: Deteriorer was explained as progressive deterioration. The research also preserves a historical note: in older versions some debuffs could last up to 5 real days; the report itself says that duration was later reduced.",
            true, false, List.of("current-deteriorer-snapshot", "history-deteriorer-duration"),
            src(Confidence.ALEX_CONFIRMED, "12/11/2025", "Discord General", "Explicación de Alex recuperada por la investigación.", "Alex explanation recovered by the research.")),

        e("history-deteriorer-duration", Zone.ENCYCLOPEDIA, Domain.HISTORY,
            "Deteriorer · duración antigua", "Deteriorer · old duration",
            "Histórico: el informe menciona debuffs de hasta 5 días reales en versiones anteriores.",
            "Historical: the report mentions debuffs lasting up to 5 real days in older versions.",
            "Este dato se conserva únicamente como histórico. No debe usarse como duración actual. El informe indica explícitamente que el comportamiento fue reducido/cambiado después.",
            "This record is preserved only as history. It must not be used as the current duration. The report explicitly says the behavior was later reduced/changed.",
            true, false, List.of("alex-deteriorer-wear"),
            src(Confidence.HISTORICAL, "12/11/2025", "Discord General", "Versión antigua según el informe.", "Older version according to the report.")),

        e("alex-raid-oxidation", Zone.ENCYCLOPEDIA, Domain.RAIDS_EVENTS,
            "Oxidación en raids", "Oxidation in raids",
            "No usar oxidación descontrolada durante raids; puede afectar entorno aliado o alertar enemigos.",
            "Do not use uncontrolled oxidation during raids; it may affect allied surroundings or alert enemies.",
            "Consejo atribuido a Alex el 04/08/2026: evitar oxidación descontrolada en raids. La mitigación propuesta por el informe es definir radio, duración y exclusiones antes de usar un prompt de área.",
            "Advice attributed to Alex on 04/08/2026: avoid uncontrolled oxidation in raids. The report's mitigation is to define radius, duration and exclusions before using an area prompt.",
            false, true, List.of("current-oxidation", "prompt-precision"),
            src(Confidence.ALEX_CONFIRMED, "04/08/2026", "Discord General", "Consejo de raid recuperado.", "Recovered raid advice.")),

        e("alex-adaptation", Zone.ENCYCLOPEDIA, Domain.BOSSES,
            "Adaptación a técnicas repetidas", "Adaptation to repeated techniques",
            "El informe atribuye a Alex que algunos bosses desarrollan counters ante técnicas abusadas repetidamente.",
            "The report attributes to Alex that some bosses develop counters to repeatedly abused techniques.",
            "Registro del 03/06/2026: repetir siempre la misma técnica puede provocar contramedidas/adaptación. Recomendación: alternar enfoques balísticos, físicos, elementales u otras herramientas disponibles en vez de depender de un único patrón.",
            "Record dated 03/06/2026: repeatedly using the same technique can provoke counters/adaptation. Recommendation: alternate ballistic, physical, elemental or other available approaches instead of relying on one pattern.",
            true, true, List.of("current-next-actions"),
            src(Confidence.ALEX_CONFIRMED, "03/06/2026", "Discord Avanzados", "Mecánica de adaptación reportada.", "Reported adaptation mechanic.")),

        e("alex-meditation-overload", Zone.ENCYCLOPEDIA, Domain.MEDITATION,
            "Sobrecarga de RE al meditar", "RE overload while meditating",
            "Advertencia: revisar RE antes de meditar; el informe documenta consecuencias catastróficas con la barra llena.",
            "Warning: check RE before meditating; the report documents catastrophic consequences at full bar.",
            "El 05/01/2026 Alex habría advertido que meditar con RE al 100% puede provocar sobrecarga grave; el 06/01/2026 se registra un caso observado de explosión/desintegración. Como el servidor cambia, este dato se mantiene fechado y no se presenta como comprobación en vivo de septiembre.",
            "On 05/01/2026 Alex reportedly warned that meditating at 100% RE can cause severe overload; on 06/01/2026 the research records an observed explosion/disintegration case. Because the server changes, this remains dated rather than presented as a live September verification.",
            true, true, List.of("current-meditation", "command-ver-barra"),
            src(Confidence.ALEX_CONFIRMED, "05/01/2026", "Discord Meditación", "Advertencia de Alex.", "Alex warning."),
            src(Confidence.SYSTEM_OBSERVED, "06/01/2026", "Discord General", "Caso observado recuperado por el informe.", "Observed case recovered by the report.")),

        e("alex-meditation-rate", Zone.ENCYCLOPEDIA, Domain.ENERGIES,
            "Recarga de meditación · dato por verificar", "Meditation recovery · verify",
            "El informe menciona +6% de RE en 5 turnos y mejoras leves con focus chips, pero lo marca como posible.",
            "The report mentions +6% RE over 5 turns and small improvements with focus chips, but marks it as possible.",
            "Dato del 11/07/2026 marcado como posible, no como regla actual confirmada. Debe verificarse antes de usarlo para calcular una build o un tiempo exacto de recuperación.",
            "Data dated 11/07/2026 is marked possible, not as a confirmed current rule. Verify it before using it to calculate a build or exact recovery timing.",
            true, false, List.of("current-meditation", "mystery-filter-rod"),
            src(Confidence.UNCONFIRMED, "11/07/2026", "Discord Tech", "Valor aproximado/posible según el informe.", "Approximate/possible value according to the report.")),

        e("mystery-filter-rod", Zone.ENCYCLOPEDIA, Domain.MYSTERIES,
            "Filter Rod y meditación", "Filter Rod and meditation",
            "Mención no confirmada: podría filtrar energía residual durante meditación/conjuro.",
            "Unconfirmed mention: may filter residual energy during meditation/spell work.",
            "La investigación menciona una 'filter rod' como posible apoyo para filtrar energía residual. No hay en el material recuperado una receta, valor exacto ni verificación suficiente; queda en Misterios en lugar de presentarse como mecánica establecida.",
            "The research mentions a 'filter rod' as a possible aid for filtering residual energy. The recovered material does not provide a recipe, exact value or sufficient verification; it stays under Mysteries rather than being presented as established mechanics.",
            true, false, List.of("alex-meditation-rate"),
            src(Confidence.UNCONFIRMED, "2026", "Discord / research", "Mención incompleta.", "Incomplete mention.")),

        e("item-geography-table", Zone.ENCYCLOPEDIA, Domain.ITEMS,
            "Geography Table", "Geography Table",
            "Mesa de investigación para estudiar efectos de reliquias desconocidas; el informe la considera costosa pero importante en mid/high game.",
            "Research table for studying unknown relic effects; the report considers it costly but important in mid/high game.",
            "Registro atribuido a Alex del 22/03/2026. Su valor práctico es reducir decisiones a ciegas antes de modificar o planificar una build alrededor de una reliquia. El informe no conserva aquí una receta exacta ni un coste actual, por lo que esos campos permanecen abiertos.",
            "Record attributed to Alex dated 22/03/2026. Its practical value is reducing blind decisions before modifying or planning a build around a relic. The report does not preserve an exact recipe or current cost here, so those fields remain open.",
            true, true, List.of("item-daemonium-kit", "relic-fallen-angel-halo"),
            src(Confidence.ALEX_CONFIRMED, "22/03/2026", "Discord General", "Función recuperada por el informe.", "Function recovered by the report.")),

        e("relic-fallen-angel-halo", Zone.ENCYCLOPEDIA, Domain.RELICS,
            "Fallen Angel Halo", "Fallen Angel Halo",
            "Reliquia de movilidad/escape; ~120 wins el 19/09/2026 según el informe. Precio sujeto a cambios.",
            "Mobility/escape relic; ~120 wins on 19/09/2026 according to the report. Price may change.",
            "El informe la registra como opción de movilidad/escape y anota un precio aproximado de 120 wins el 19/09/2026. No asumir que ese precio sigue vigente. El mismo material relaciona la extracción de componentes de reliquias con Daemonium Kit.",
            "The report records it as a mobility/escape option and notes an approximate price of 120 wins on 19/09/2026. Do not assume that price remains current. The same material links relic component extraction to Daemonium Kit.",
            true, true, List.of("item-daemonium-kit", "item-geography-table", "mystery-halo-risk"),
            src(Confidence.ALEX_CONFIRMED, "19/09/2026", "Discord General", "Función/precio aproximado según el informe.", "Function/approximate price according to the report.")),

        e("item-daemonium-kit", Zone.ENCYCLOPEDIA, Domain.ITEMS,
            "Daemonium Kit", "Daemonium Kit",
            "El informe lo marca como necesario para extraer componentes al desmontar reliquias.",
            "The report marks it as required to extract components when dismantling relics.",
            "Consejo operativo: no desmontar una reliquia esperando materiales sin comprobar que se dispone del Daemonium Kit y que la regla sigue vigente. El registro usado está fechado el 19/09/2026.",
            "Operational advice: do not dismantle a relic expecting materials without checking that a Daemonium Kit is available and the rule is still current. The source record is dated 19/09/2026.",
            true, true, List.of("relic-fallen-angel-halo", "item-geography-table"),
            src(Confidence.ALEX_CONFIRMED, "19/09/2026", "Discord General", "Requisito de extracción registrado.", "Recorded extraction requirement.")),

        e("progression-v1-v4", Zone.ENCYCLOPEDIA, Domain.PROGRESSION,
            "Progresión V1 → V4", "V1 → V4 progression",
            "La investigación conserva una progresión racial por versiones V1, V2, V3 y V4.",
            "The research preserves a racial progression through V1, V2, V3 and V4.",
            "Registro atribuido a Alex del 02/11/2025: avanzar de V1 a V4 desbloquea habilidades y aumentos de atributos. El informe no contiene aquí requisitos universales exactos para cada raza, por lo que no se inventa un árbol único. Cualquier requisito específico debe vincularse sólo cuando exista evidencia propia de esa raza.",
            "Record attributed to Alex dated 02/11/2025: progressing from V1 to V4 unlocks abilities and attribute increases. The report does not contain universal exact requirements for every race here, so no single tree is invented. Specific requirements should only be linked when there is evidence for that race.",
            true, false, List.of("progression-reset-v4"),
            src(Confidence.ALEX_CONFIRMED, "02/11/2025", "Discord General", "Marco de progresión recuperado.", "Recovered progression framework.")),

        e("progression-reset-v4", Zone.ENCYCLOPEDIA, Domain.PROGRESSION,
            "Reset tras V4 · posible", "Post-V4 reset · possible",
            "El informe sugiere un reset tras V4 para especialización, pero lo marca como posible.",
            "The report suggests a reset after V4 for specialization, but marks it as possible.",
            "Dato fechado el 15/07/2026 y clasificado como posible. No debe mostrarse como requisito obligatorio ni promesa de conservar todos los objetos hasta verificar la mecánica actual.",
            "Data dated 15/07/2026 and classified as possible. It must not be shown as mandatory or as a promise that all items persist without verifying current mechanics.",
            true, false, List.of("progression-v1-v4"),
            src(Confidence.UNCONFIRMED, "15/07/2026", "Discord General", "Mecánica posible, pendiente de revalidación.", "Possible mechanic pending revalidation.")),

        e("assembling-chips", Zone.ENCYCLOPEDIA, Domain.ASSEMBLING,
            "Assembling de chips", "Chip assembling",
            "Assembling combina máquinas/bio-partes para producir chips o implantes.",
            "Assembling combines machines/bio-parts to produce chips or implants.",
            "Registro del 20/05/2026: Assembling se describe como proceso para unir componentes tecnológicos y biológicos. El informe pone como ejemplo Nano Robots + Ghost Core para chips cibernéticos de daño; antes de reproducir una receta concreta conviene verificarla contra la versión actual.",
            "Record dated 20/05/2026: Assembling is described as a process for joining technological and biological components. The report gives Nano Robots + Ghost Core as an example for cybernetic damage chips; verify a concrete recipe against the current version before reproducing it.",
            true, false, List.of("assembling-body-planning"),
            src(Confidence.ALEX_CONFIRMED, "20/05/2026", "Discord Tech", "Sistema de assembling recuperado.", "Recovered assembling system.")),

        e("assembling-body-planning", Zone.ENCYCLOPEDIA, Domain.ASSEMBLING,
            "Planificar partes del cuerpo", "Plan body-part investment",
            "Consejo posible: no consumir todas las opciones corporales en una sola mejora.",
            "Possible advice: do not consume all body options in one upgrade.",
            "El informe del 22/05/2026 aconseja pensar en qué invertir huesos/órganos/partes antes de comprometerlas. Se conserva como consejo posible porque los beneficios y costes concretos no están suficientemente detallados en el material recuperado.",
            "The 22/05/2026 report advises planning how bones/organs/body parts are invested before committing them. It remains possible advice because concrete benefits and costs are not sufficiently detailed in the recovered material.",
            true, false, List.of("assembling-chips"),
            src(Confidence.UNCONFIRMED, "22/05/2026", "Discord Tech", "Consejo atribuido al staff, detalle incompleto.", "Staff-attributed advice with incomplete detail.")),

        e("death-wounds", Zone.ENCYCLOPEDIA, Domain.DEATH_REVIVE,
            "Heridas persistentes", "Persistent injuries",
            "El combate puede dejar sangrado y otras heridas persistentes; Red Blood/hemostáticos aparecen vinculados al control de hemorragias.",
            "Combat can leave bleeding and other persistent injuries; Red Blood/hemostatics are linked to hemorrhage control.",
            "El informe atribuye a Alex que Red Blood y hemostáticos sirven para tratar hemorragias. No se infieren aquí curas para todos los estados ni porcentajes de recuperación que el corpus resumido no confirme.",
            "The report attributes to Alex that Red Blood and hemostatics are used for hemorrhage treatment. This entry does not infer cures for every state or recovery percentages not confirmed by the summarized corpus.",
            true, true, List.of("respawn-cards", "repeat-revive-penalty"),
            src(Confidence.ALEX_CONFIRMED, "2026", "Discord / report", "Tratamiento de hemorragias recuperado.", "Recovered hemorrhage treatment.")),

        e("respawn-cards", Zone.ENCYCLOPEDIA, Domain.DEATH_REVIVE,
            "Respawn Cards", "Respawn Cards",
            "Método de resurrección documentado por la investigación de enero de 2026.",
            "Resurrection method documented by the January 2026 research.",
            "La investigación las describe como método primario de resurrección, con cooldown y recuperación de parte del equipamiento. Los valores exactos y condiciones actuales no aparecen suficientemente detallados en el resumen recuperado, por lo que deben verificarse antes de una operación de alto riesgo.",
            "The research describes them as a primary resurrection method, with cooldown and recovery of part of the equipment. Exact values and current conditions are not sufficiently detailed in the recovered summary, so verify them before a high-risk operation.",
            true, true, List.of("repeat-revive-penalty", "death-wounds"),
            src(Confidence.ALEX_CONFIRMED, "01/2026", "Discord General", "Mecánica de revive recuperada.", "Recovered revive mechanic.")),

        e("repeat-revive-penalty", Zone.ENCYCLOPEDIA, Domain.DEATH_REVIVE,
            "Penalización por reanimaciones repetidas", "Repeated revival penalty",
            "Posible aumento de espera/debuffs al reanimarse repetidamente.",
            "Possible increased wait/debuffs after repeated revival.",
            "El informe menciona un registro del 10/01/2026 sobre penalizaciones crecientes tras reanimaciones repetidas, pero lo clasifica como posible. No se fijan stacks, tiempos ni cantidad de revives sin una fuente actual más precisa.",
            "The report mentions a 10/01/2026 record about growing penalties after repeated revivals, but classifies it as possible. No stacks, times or revival count are asserted without a more precise current source.",
            true, true, List.of("respawn-cards"),
            src(Confidence.UNCONFIRMED, "10/01/2026", "Discord Avanzados", "Penalización posible según el informe.", "Possible penalty according to the report.")),

        e("prompt-precision", Zone.ENCYCLOPEDIA, Domain.PROMPTS,
            "Prompts: especificar límites", "Prompts: specify limits",
            "Cantidad de energía, rango, duración, objetivos/exclusiones y disipación deben quedar explícitos.",
            "Energy amount, range, duration, targets/exclusions and dissipation should be explicit.",
            "Patrón atribuido repetidamente a Alex: un prompt seguro no se limita a pedir un efecto; debe delimitar coste/energía, radio o alcance, duración, qué puede afectar, qué debe excluir y cómo termina o se disipa. Esta regla explica varios fallos registrados con instrucciones vagas.",
            "Pattern repeatedly attributed to Alex: a safe prompt does not merely ask for an effect; it should define cost/energy, radius or range, duration, what it may affect, what it must exclude and how it ends or dissipates. This explains several recorded failures from vague instructions.",
            false, true, List.of("prompt-vague-failure", "alex-raid-oxidation"),
            src(Confidence.ALEX_CONFIRMED, "varias fechas", "Discord General", "Patrón reiterado en el informe.", "Pattern repeated in the report.")),

        e("prompt-vague-failure", Zone.ENCYCLOPEDIA, Domain.MISTAKES,
            "Prompts vagos · consecuencias", "Vague prompts · consequences",
            "Órdenes sin límites se relacionaron con overflow de RE y daño no deseado al entorno.",
            "Unbounded instructions were linked to RE overflow and unintended environmental damage.",
            "Ejemplos conservados por el informe: 'medito fuerte' produjo overflow excesivo de RE; una orden genérica de 'usar Deteriorer' terminó dañando estructuras. Estos casos se guardan para no repetir el error, no como garantía de que cualquier frase idéntica siempre produzca el mismo resultado.",
            "Examples preserved by the report: 'meditate hard' produced excessive RE overflow; a generic 'use Deteriorer' instruction ended up damaging structures. These cases are kept to avoid repeating the mistake, not as a guarantee that identical wording always produces the same result.",
            true, true, List.of("prompt-precision", "alex-meditation-overload"),
            src(Confidence.ALEX_CONFIRMED, "05/01/2026; 15/08/2026", "Discord Meditación / General", "Fallos registrados.", "Recorded failures.")),

        e("command-ver-barra", Zone.ENCYCLOPEDIA, Domain.PROMPTS,
            "Comando: Ver barra", "Command: View bar",
            "Comprobar la barra de energía antes de acciones de RE.",
            "Check the energy bar before RE actions.",
            "La investigación recupera *Ver barra* como comprobación previa recomendada antes de acciones de energía. También aparecen menciones a 'info [reliquia]' o 'examinar [objeto]', pero sus sintaxis exactas deben tratarse como referencia del Discord y revalidarse si el servidor cambia.",
            "The research recovers *View bar* as a recommended pre-check before energy actions. Mentions of 'info [relic]' or 'examine [item]' also appear, but their exact syntax should be treated as Discord reference and revalidated if the server changes.",
            false, true, List.of("alex-meditation-overload", "current-next-actions"),
            src(Confidence.ALEX_CONFIRMED, "04/01/2026", "Discord General", "Comprobación de energía recuperada.", "Recovered energy check.")),

        e("mystery-halo-risk", Zone.ENCYCLOPEDIA, Domain.MYSTERIES,
            "Riesgo de cruce con Halo · no confirmado", "Halo traversal risk · unconfirmed",
            "El informe menciona riesgo de pérdida/daño al usar un cruce no calibrado; requiere fuente primaria antes de asumirlo.",
            "The report mentions loss/damage risk from an uncalibrated traversal; it needs primary-source verification before being assumed.",
            "Este punto fue marcado como posible en el informe del 21/09/2026. Se conserva en Misterios y no en Reglas: no hay detalle suficiente sobre condiciones, probabilidad ni si la mecánica sigue activa.",
            "This point was marked possible in the 21/09/2026 report. It remains under Mysteries rather than Rules: there is not enough detail on conditions, probability or whether the mechanic is still active.",
            true, false, List.of("relic-fallen-angel-halo"),
            src(Confidence.UNCONFIRMED, "21/09/2026", "Discord Relics / report", "Riesgo posible pendiente de fuente primaria.", "Possible risk pending primary source."))
    );

    public static List<Entry> entries() { return ENTRIES; }

    public static Entry get(String id) {
        if (id == null) return null;
        for (Entry entry : ENTRIES) if (entry.id().equals(id)) return entry;
        return null;
    }

    public static List<Entry> byZone(Zone zone) {
        if (zone == null) return List.of();
        return ENTRIES.stream().filter(e -> e.zone() == zone).toList();
    }

    public static List<Entry> survival() {
        return ENTRIES.stream().filter(Entry::critical)
                .sorted(Comparator.comparing((Entry e) -> e.zone() == Zone.CURRENT ? 0 : 1)
                        .thenComparing(Entry::id))
                .toList();
    }

    public static List<Entry> encyclopedia() {
        return byZone(Zone.ENCYCLOPEDIA);
    }

    public static List<Entry> current() {
        return byZone(Zone.CURRENT);
    }

    public static List<Entry> search(String query, boolean spanish, Zone zone, int limit) {
        int safeLimit = Math.max(1, Math.min(64, limit));
        String q = normalize(query);
        List<Entry> pool = zone == null ? ENTRIES : byZone(zone);
        if (q.isBlank()) return pool.stream().limit(safeLimit).toList();

        record Ranked(Entry entry, int score) { }
        List<Ranked> ranked = new ArrayList<>();
        for (Entry entry : pool) {
            int score = score(entry, q, spanish);
            if (score > 0) ranked.add(new Ranked(entry, score));
        }
        ranked.sort(Comparator.comparingInt(Ranked::score).reversed()
                .thenComparing(r -> normalize(r.entry().title(spanish)))
                .thenComparing(r -> r.entry().id()));
        return ranked.stream().limit(safeLimit).map(Ranked::entry).toList();
    }

    public static String searchable(Entry entry, boolean spanish) {
        if (entry == null) return "";
        StringBuilder out = new StringBuilder();
        out.append(entry.id()).append(' ').append(entry.title(spanish)).append(' ')
                .append(entry.summary(spanish)).append(' ').append(entry.body(spanish)).append(' ')
                .append(entry.domain().label(spanish));
        for (String related : entry.related()) out.append(' ').append(related);
        for (Source source : entry.sources()) out.append(' ').append(source.channel()).append(' ')
                .append(source.date()).append(' ').append(source.confidence().label(spanish)).append(' ')
                .append(source.note(spanish));
        return out.toString();
    }

    public static String sourceLine(Entry entry, boolean spanish) {
        if (entry == null || entry.sources().isEmpty()) return spanish ? "FUENTE —" : "SOURCE —";
        Source source = entry.sources().get(0);
        String when = source.date().isBlank() ? "" : " · " + source.date();
        String where = source.channel().isBlank() ? "" : " · " + source.channel();
        return source.confidence().label(spanish) + when + where;
    }

    public static int confidenceAccent(Confidence confidence) {
        if (confidence == null) return 0xFF9AA4AB;
        return switch (confidence) {
            case CURRENT_CONFIRMED -> 0xFF72C98B;
            case ALEX_CONFIRMED -> 0xFF68C6D8;
            case SYSTEM_OBSERVED -> 0xFF8DD6A5;
            case PLAYER_EXPERIENCE -> 0xFFD6AE65;
            case HISTORICAL -> 0xFFE89B59;
            case UNCONFIRMED -> 0xFF9AA4AB;
            case CONTRADICTION -> 0xFFE54852;
        };
    }

    private static int score(Entry entry, String query, boolean spanish) {
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

    static String normalize(String value) {
        if (value == null) return "";
        String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return decomposed.toLowerCase(Locale.ROOT).replace('·', ' ').replaceAll("[^a-z0-9?_-]+", " ").trim();
    }

    private static String safe(String value) { return value == null ? "" : value; }
}
