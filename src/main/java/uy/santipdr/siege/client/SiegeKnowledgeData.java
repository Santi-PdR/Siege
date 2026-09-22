package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Non-personal Eternal Craft / SIEGE server encyclopedia for SIEGE 3.00.
 *
 * This data intentionally excludes player profiles, inventories, private notes,
 * personal progress and named-player anecdotes. It only keeps server-wide
 * mechanics, race catalogs, progression concepts, systems and dated history.
 */
public final class SiegeKnowledgeData {
    public enum Zone { SERVER, HISTORY }

    public enum Domain {
        OVERVIEW("SERVIDOR", "SERVER"),
        PROGRESSION("PROGRESIÓN", "PROGRESSION"),
        EXECUTORS("EJECUTORES", "EXECUTORS"),
        TRIALS("TRIALS", "TRIALS"),
        STRUCTURES("ESTRUCTURAS", "STRUCTURES"),
        BOSSES("BOSSES", "BOSSES"),
        MISSIONS("MISIONES / NPC", "MISSIONS / NPCS"),
        RACES("RAZAS", "RACES"),
        ABILITIES("HABILIDADES", "ABILITIES"),
        MEDITATION("ENERGÍA / MEDITACIÓN", "ENERGY / MEDITATION"),
        ITEMS("OBJETOS", "ITEMS"),
        ASSEMBLING("ASSEMBLING / CYBORGS", "ASSEMBLING / CYBORGS"),
        RELICS("RELIQUIAS", "RELICS"),
        DIMENSIONS("DIMENSIONES", "DIMENSIONS"),
        DEATH_REVIVE("MUERTE / REVIVE", "DEATH / REVIVE"),
        RAIDS_EVENTS("RAIDS / EVENTOS", "RAIDS / EVENTS"),
        FACTIONS("FACCIONES", "FACTIONS"),
        ECONOMY("ECONOMÍA", "ECONOMY"),
        PROMPTS("PROMPTS / ACCIONES", "PROMPTS / ACTIONS"),
        CONTRADICTIONS("CAMBIOS / CONTRADICCIONES", "CHANGES / CONTRADICTIONS"),
        SOURCES("FUENTES", "SOURCES");

        private final String es;
        private final String en;
        Domain(String es, String en) { this.es = es; this.en = en; }
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    public enum Confidence {
        STAFF_CONFIRMED("STAFF CONFIRMADO", "STAFF CONFIRMED"),
        SYSTEM_OBSERVED("SISTEMA / OBSERVADO", "SYSTEM / OBSERVED"),
        HISTORICAL("HISTÓRICO", "HISTORICAL"),
        UNCONFIRMED("POR VERIFICAR", "UNCONFIRMED"),
        CONTRADICTION("CONTRADICCIÓN", "CONTRADICTION");

        private final String es;
        private final String en;
        Confidence(String es, String en) { this.es = es; this.en = en; }
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    public record Source(Confidence confidence, String date, String section, String noteEs, String noteEn) {
        public Source {
            confidence = confidence == null ? Confidence.UNCONFIRMED : confidence;
            date = safe(date);
            section = safe(section);
            noteEs = safe(noteEs);
            noteEn = safe(noteEn);
        }
        public String note(boolean spanish) { return spanish ? noteEs : noteEn; }
    }

    public record Entry(String id, Zone zone, Domain domain,
                        String titleEs, String titleEn,
                        String summaryEs, String summaryEn,
                        String bodyEs, String bodyEn,
                        boolean critical,
                        List<String> related,
                        List<Source> sources) {
        public Entry {
            id = safe(id);
            zone = zone == null ? Zone.SERVER : zone;
            domain = domain == null ? Domain.OVERVIEW : domain;
            titleEs = safe(titleEs);
            titleEn = safe(titleEn);
            summaryEs = safe(summaryEs);
            summaryEn = safe(summaryEn);
            bodyEs = safe(bodyEs);
            bodyEn = safe(bodyEn);
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

    private static Source src(Confidence confidence, String date, String section, String es, String en) {
        return new Source(confidence, date, section, es, en);
    }

    private static Entry e(String id, Zone zone, Domain domain,
                           String es, String en,
                           String summaryEs, String summaryEn,
                           String bodyEs, String bodyEn,
                           boolean critical,
                           List<String> related,
                           Source... sources) {
        return new Entry(id, zone, domain, es, en, summaryEs, summaryEn, bodyEs, bodyEn,
                critical, related, List.of(sources));
    }

    private static final List<Entry> ENTRIES = List.of(
        e("server-overview", Zone.SERVER, Domain.OVERVIEW,
            "Eternal Craft — SIEGE", "Eternal Craft — SIEGE",
            "Servidor moddeado de guerra/supervivencia con razas, progresión, Trials, reliquias, Executores, bosses, dimensiones y sistemas propios.",
            "Modded war/survival server with races, progression, Trials, relics, Executors, bosses, dimensions and custom systems.",
            "La enciclopedia 3.00 reúne únicamente información general del servidor. No guarda perfiles, inventarios, progreso privado ni notas personales. Las mecánicas cambian con el tiempo, por eso cada entrada conserva fecha y nivel de confianza cuando es necesario.",
            "The 3.00 encyclopedia stores only general server information. It does not keep profiles, inventories, private progress or personal notes. Mechanics change over time, so each entry keeps dates and confidence when needed.",
            false, List.of("race-catalog", "rarity-order", "progression-v1-v4", "trials-basics"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "Índice general", "Resumen no personal del servidor.", "Non-personal server summary.")),

        e("source-audit", Zone.SERVER, Domain.SOURCES,
            "Cobertura de la investigación", "Research coverage",
            "252 JSON y 251.065 mensajes únicos indexados; la revisión semántica sigue incompleta.",
            "252 JSON files and 251,065 unique messages indexed; semantic review remains incomplete.",
            "La auditoría encontró 488 archivos, abrió íntegramente 252/252 JSON y verificó 251.065 mensajes únicos sin duplicados. El export disponible cubre 02/11/2025 → 20/09/2026 y sólo el canal General. Indexar todo el texto no significa que cada mecánica ya haya sido revisada o siga vigente.",
            "The audit found 488 files, fully opened 252/252 JSON files and verified 251,065 unique messages with no duplicates. The available export spans 02/11/2025 → 20/09/2026 and only the General channel. Full text indexing does not mean every mechanic has been reviewed or remains current.",
            false, List.of("source-policy"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "00 - Auditoría del export", "Alcance técnico del corpus.", "Technical corpus scope.")),

        e("source-policy", Zone.SERVER, Domain.SOURCES,
            "Cómo leer esta enciclopedia", "How to read this encyclopedia",
            "Staff/resultado observado > histórico > dato incompleto. Un campo faltante nunca significa 'no hace falta'.",
            "Staff/observed result > historical > incomplete data. A missing field never means 'not required'.",
            "Las fichas separan información confirmada por staff, resultados observados, datos históricos, contradicciones y datos por verificar. Cuando una versión antigua contradice otra más nueva, ambas se conservan con fecha. No se completan requisitos, recetas, stats ni probabilidades por suposición.",
            "Entries separate staff-confirmed information, observed results, historical data, contradictions and unconfirmed data. When an older version conflicts with a newer one, both remain dated. Requirements, recipes, stats and probabilities are never filled in by assumption.",
            false, List.of("source-audit"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "Política de fuentes", "Regla de validación de la enciclopedia.", "Encyclopedia validation rule.")),

        e("rarity-order", Zone.SERVER, Domain.RACES,
            "Rarezas de raza", "Race rarities",
            "Común → Poco común → Raro → Ultra raro → Legendario → Obsainan → Mítico → Godly → Eternal → Fabled.",
            "Common → Uncommon → Rare → Ultra Rare → Legendary → Obsainan → Mythic → Godly → Eternal → Fabled.",
            "Ese orden fue repetido por staff en agosto y septiembre de 2026. Obsainan está por encima de Legendario. Ejemplos fechados del corpus: Apotheosis = Eternal; Saiyan = Obsainan; Deteriorer = Obsainan; Super Ghoul = Raro. Las rarezas pueden cambiar con balance futuro.",
            "That order was repeated by staff in August and September 2026. Obsainan ranks above Legendary. Dated examples from the corpus: Apotheosis = Eternal; Saiyan = Obsainan; Deteriorer = Obsainan; Super Ghoul = Rare. Rarities may change with future balancing.",
            false, List.of("race-catalog", "fabled-acquisition"),
            src(Confidence.STAFF_CONFIRMED, "23/08–20/09/2026", "08 - Razas", "Orden y ejemplos fechados.", "Dated order and examples.")),

        e("race-catalog", Zone.SERVER, Domain.RACES,
            "Razas documentadas", "Documented races",
            "Human, Hacker, Shark, Saiyan, Deteriorer, Faraón, Apotheosis, Muerte, Cyborg, Ghoul, Subhuman, Terrariano, Kaioshin, Dragon, Shinigami, Majin y razas ocultas de AUs de Undertale.",
            "Human, Hacker, Shark, Saiyan, Deteriorer, Pharaoh, Apotheosis, Death, Cyborg, Ghoul, Subhuman, Terrarian, Kaioshin, Dragon, Shinigami, Majin and hidden Undertale AU races.",
            "Este catálogo reúne nombres con evidencia concreta en la revisión. No significa que todas estén disponibles en la temporada actual ni que la lista sea exhaustiva. Algunas tienen variantes internas y varias se obtienen mediante pasos o Trials en vez de giros comunes.",
            "This catalog groups names with concrete evidence in the review. It does not mean every race is available in the current season or that the list is exhaustive. Some have internal variants and several are obtained through steps or Trials rather than common rolls.",
            false, List.of("race-human", "race-hacker", "race-saiyan", "race-deteriorer", "race-slots"),
            src(Confidence.STAFF_CONFIRMED, "2026", "08 - Razas", "Catálogo parcial confirmado por el corpus revisado.", "Partial catalog confirmed by reviewed corpus.")),

        e("race-human", Zone.SERVER, Domain.RACES,
            "Human", "Human",
            "Ruta rápida hacia V4; útil porque muchos Trials y artefactos usan V4 como requisito inicial.",
            "Fast route toward V4; useful because many Trials and artifacts use V4 as an initial requirement.",
            "Staff la describió como una de las rutas más rápidas para alcanzar V4. Eso no elimina los demás requisitos de un Trial o artefacto. V3 tiene varios pasos y no deben copiarse misiones de otros juegos como receta de Eternal Craft.",
            "Staff described it as one of the fastest routes to V4. That does not remove other Trial or artifact requirements. V3 has multiple steps and quests from other games must not be copied as Eternal Craft recipes.",
            false, List.of("progression-v1-v4", "trials-basics"),
            src(Confidence.STAFF_CONFIRMED, "08/09/2026", "08 - Razas", "Utilidad general de progresión.", "General progression utility.")),

        e("race-hacker", Zone.SERVER, Domain.RACES,
            "Hacker", "Hacker",
            "Raza centrada en energía, Room, Gate, sabotaje y progresión propia; los dones pueden cambiar costes/capacidades.",
            "Race centered on energy, Room, Gate, sabotage and its own progression; gifts can alter costs/capabilities.",
            "La meditación Hacker puede concentrarse en una capacidad concreta, como Room, en vez de repartir mejora entre varios aspectos. Hay referencias a V1–V4. Gate fue descrito con alcance espacial en V2, pero distancia/coste completos siguen sin reconstruirse. No asumir que todos los usuarios de Hacker tienen los mismos costes.",
            "Hacker meditation can focus on one capability, such as Room, instead of spreading improvement across several aspects. V1–V4 are referenced. Gate was described as reaching space at V2, but full distance/cost remain unreconstructed. Do not assume every Hacker user has identical costs.",
            false, List.of("ability-room", "ability-gate", "meditation-levels"),
            src(Confidence.STAFF_CONFIRMED, "2026", "08 - Razas / 10 - Habilidades", "Mecánicas generales de Hacker.", "General Hacker mechanics.")),

        e("race-shark", Zone.HISTORY, Domain.RACES,
            "Shark", "Shark",
            "V2 histórica: tres flores en inventario; ventajas acuáticas no resuelven todos los enfrentamientos.",
            "Historical V2: three flowers in inventory; aquatic advantages do not solve every matchup.",
            "Una explicación de mayo de 2026 vinculó Shark V2 con tener tres flores, sin identificar cuáles ni confirmar vigencia. También se aclaró que pelear contra ciertos cyborgs seguía siendo difícil incluso en agua.",
            "A May 2026 explanation linked Shark V2 with holding three flowers, without identifying which or confirming current validity. It was also clarified that fighting certain cyborgs remained difficult even in water.",
            false, List.of("progression-v1-v4"),
            src(Confidence.HISTORICAL, "02/05–15/05/2026", "08 - Razas", "Ruta/ventaja histórica.", "Historical route/advantage.")),

        e("race-saiyan", Zone.SERVER, Domain.RACES,
            "Saiyan / Saiyajin", "Saiyan",
            "Progresa con transformaciones y stats en vez del esquema V2/V3/V4 descrito para otras razas.",
            "Progresses through transformations and stats rather than the V2/V3/V4 scheme described for other races.",
            "El entrenamiento recomendado incluye control de transformación y teleport. Un teleport defectuoso puede llevar a un destino incorrecto. Dojos y máquinas multiplicadoras aparecen ligados a su progresión. Variantes documentadas incluyen Flame, Evil, radiactivo, DB AF y Xeno Saiyan.",
            "Recommended training includes transformation control and teleport. A faulty teleport may send the user to the wrong destination. Dojos and multiplier machines appear tied to progression. Documented variants include Flame, Evil, radioactive, DB AF and Xeno Saiyan.",
            true, List.of("rarity-order", "bosses-basics"),
            src(Confidence.STAFF_CONFIRMED, "05–08/2026", "08 - Razas", "Progresión y variantes documentadas.", "Documented progression and variants.")),

        e("race-deteriorer", Zone.SERVER, Domain.RACES,
            "Deteriorer", "Deteriorer",
            "Raza Obsainan basada en deterioro/oxidación progresiva y bloqueo o degradación de capacidades.",
            "Obsainan race based on progressive deterioration/oxidation and capability degradation or blocking.",
            "La identidad mecánica documentada es desgaste progresivo, no daño instantáneo. En versiones antiguas hubo efectos de bloqueo extremadamente largos; esa duración se considera histórica y no debe usarse como valor actual. Los requisitos actuales de V2/V3/V4 no están reconstruidos en la enciclopedia.",
            "The documented mechanical identity is progressive wear rather than instant damage. Old versions included extremely long disable effects; that duration is historical and must not be used as a current value. Current V2/V3/V4 requirements are not reconstructed in the encyclopedia.",
            true, List.of("rarity-order", "race-deteriorer-old-debuff"),
            src(Confidence.STAFF_CONFIRMED, "2026", "08 - Razas", "Identidad general de la raza.", "General race identity.")),

        e("race-deteriorer-old-debuff", Zone.HISTORY, Domain.RACES,
            "Deteriorer · debuff antiguo", "Deteriorer · old debuff",
            "Versiones antiguas podían inutilizar capacidades durante periodos de hasta varios días reales.",
            "Old versions could disable capabilities for periods of up to several real days.",
            "Se conserva exclusivamente como historia del balance. La propia revisión aclara que esas duraciones fueron modificadas y no representan la versión actual.",
            "Preserved exclusively as balance history. The review itself states those durations were changed and do not represent the current version.",
            false, List.of("race-deteriorer"),
            src(Confidence.HISTORICAL, "2025–2026", "08 - Razas", "Mecánica antigua, no actual.", "Old mechanic, not current.")),

        e("race-pharaoh", Zone.HISTORY, Domain.RACES,
            "Faraón", "Pharaoh",
            "Puede controlar una dimensión desértica propia; la disponibilidad ha cambiado según la etapa del servidor.",
            "Can control its own desert dimension; availability has changed across server eras.",
            "Fue descrita como una raza capaz de llevar enemigos a una dimensión desértica y usarla como refugio/base. También se indicó en esa etapa que no estaba disponible 'por ahora'. Poder y disponibilidad deben tratarse por separado.",
            "It was described as a race capable of bringing enemies into a desert dimension and using it as a refuge/base. At that time it was also said to be unavailable 'for now'. Power and availability must be treated separately.",
            false, List.of("dimensions-basics"),
            src(Confidence.HISTORICAL, "06/08/2026", "08 - Razas", "Capacidad histórica; disponibilidad variable.", "Historical capability; variable availability.")),

        e("race-apotheosis", Zone.SERVER, Domain.RACES,
            "Apotheosis", "Apotheosis",
            "Raza Eternal vinculada a la fe; faltan reglas completas, costes y límites.",
            "Eternal-rarity race linked to faith; complete rules, costs and limits are missing.",
            "La descripción disponible vincula sus efectos con la fe del personaje en una deidad. No existe en el material revisado una fórmula universal, lista completa de dioses ni garantía de que cualquier deseo o expectativa se cumpla.",
            "Available descriptions link its effects to the character's faith in a deity. The reviewed material does not provide a universal formula, complete god list or guarantee that any wish/expectation is fulfilled.",
            false, List.of("rarity-order"),
            src(Confidence.STAFF_CONFIRMED, "06/08–23/08/2026", "08 - Razas", "Rareza y concepto general.", "Rarity and general concept.")),

        e("race-death", Zone.SERVER, Domain.RACES,
            "Muerte", "Death",
            "Raza relacionada con absorber almas de bosses.",
            "Race related to absorbing boss souls.",
            "La revisión confirma la función general de absorber almas de bosses. 'Muerte' también aparece como nombre de estados, eventos y otras entidades; hay que desambiguar el contexto antes de atribuirle una mecánica racial.",
            "The review confirms the general function of absorbing boss souls. 'Death' also appears as the name of states, events and other entities; context must be disambiguated before assigning a racial mechanic.",
            false, List.of("bosses-basics"),
            src(Confidence.STAFF_CONFIRMED, "28/07/2026", "08 - Razas", "Función racial general.", "General racial function.")),

        e("race-cyborg", Zone.SERVER, Domain.RACES,
            "Cyborg", "Cyborg",
            "Se relaciona con instalar trasplantes y chips mediante Assembling; portar tecnología no basta para tener todas sus capacidades.",
            "Linked to installing transplants and chips through Assembling; carrying technology alone does not grant all racial capabilities.",
            "La transformación/condición cyborg se explicó mediante Assembling. No se debe asumir que cualquier implante es reversible ni que usar un arma tecnológica convierte automáticamente al usuario en Cyborg.",
            "The cyborg transformation/condition was explained through Assembling. Do not assume every implant is reversible or that using a technological weapon automatically makes the user a Cyborg.",
            false, List.of("assembling-table"),
            src(Confidence.STAFF_CONFIRMED, "08/09/2026", "08 - Razas / 14 - Assembling", "Relación entre raza e implantes.", "Link between race and implants.")),

        e("race-ghoul", Zone.SERVER, Domain.RACES,
            "Ghoul", "Ghoul",
            "Raza documentada; no confundirla con enemigos ghoul ni con otros contextos que usan el mismo nombre.",
            "Documented race; do not confuse it with ghoul enemies or other contexts using the same name.",
            "Existe como raza, pero el corpus también usa 'ghouls' para enemigos. La ruta de evolución mediante antimateria que apareció en una conversación quedó invalidada por el propio contexto y no se conserva como receta.",
            "It exists as a race, but the corpus also uses 'ghouls' for enemies. An antimatter evolution route from one conversation was invalidated by its own context and is not stored as a recipe.",
            false, List.of("rarity-order"),
            src(Confidence.STAFF_CONFIRMED, "2026", "08 - Razas", "Existencia de la raza; receta dudosa descartada.", "Race existence; dubious recipe discarded.")),

        e("race-subhuman", Zone.SERVER, Domain.RACES,
            "Subhuman", "Subhuman",
            "Familia de variantes Human mutadas; incluye menciones a Adamantium Human y Sorcerer.",
            "Family of mutated Human variants; includes references to Adamantium Human and Sorcerer.",
            "Adamantium Human fue descrita con piel muy dura y resistencia a radiación, sin cifras. Sorcerer fue relacionada con hechicería/energía maldita y rolls de raza, sin probabilidad exacta. No se extrapolan capacidades entre variantes.",
            "Adamantium Human was described with very hard skin and radiation resistance, without numbers. Sorcerer was linked to sorcery/cursed energy and race rolls, without an exact probability. Capabilities are not extrapolated between variants.",
            false, List.of("race-human", "race-catalog"),
            src(Confidence.STAFF_CONFIRMED, "15/07–22/07/2026", "08 - Razas", "Variantes parciales documentadas.", "Partially documented variants.")),

        e("race-terrarian", Zone.SERVER, Domain.RACES,
            "Terrariano", "Terrarian",
            "Raza recomendada para enfrentamientos contra bosses; stats/progresión completa no reconstruidos.",
            "Race recommended for boss encounters; complete stats/progression not reconstructed.",
            "La utilidad contra bosses está documentada, pero la enciclopedia todavía no tiene requisitos, stats ni árbol de progresión suficientes para una ficha más detallada.",
            "Its usefulness against bosses is documented, but the encyclopedia still lacks enough requirements, stats and progression data for a fuller entry.",
            false, List.of("bosses-basics"),
            src(Confidence.STAFF_CONFIRMED, "06/08/2026", "08 - Razas", "Utilidad general confirmada.", "General utility confirmed.")),

        e("race-kaioshin", Zone.HISTORY, Domain.RACES,
            "Kaioshin", "Kaioshin",
            "Raza documentada; existe un caso histórico V2 ligado a derrotar a Solaris, sin convertirlo en requisito universal.",
            "Documented race; one historical V2 case was linked to defeating Solaris, without making it a universal requirement.",
            "La revisión confirma la existencia de Kaioshin. El ejemplo de V2 proviene de un caso histórico concreto, por lo que no se presenta como receta general para cualquier etapa del servidor.",
            "The review confirms Kaioshin exists. The V2 example comes from one historical case, so it is not presented as a general recipe for every server era.",
            false, List.of("progression-v1-v4"),
            src(Confidence.HISTORICAL, "06/08/2026", "08 - Razas", "Ejemplo de progresión histórico.", "Historical progression example.")),

        e("race-dragon", Zone.SERVER, Domain.RACES,
            "Dragon", "Dragon",
            "Se documentaron tres variantes; una de ellas relacionada con Blox Fruits. Las otras dos siguen sin nombre en la ficha revisada.",
            "Three variants were documented; one relates to Blox Fruits. The other two remain unnamed in the reviewed entry.",
            "La existencia de varias variantes está confirmada, pero no hay datos suficientes para asignarles stats o progresiones sin inventar.",
            "The existence of multiple variants is confirmed, but there is not enough data to assign stats or progression without inventing them.",
            false, List.of("race-catalog"),
            src(Confidence.STAFF_CONFIRMED, "06/08/2026", "08 - Razas", "Tres variantes mencionadas.", "Three variants mentioned.")),

        e("race-shinigami", Zone.SERVER, Domain.RACES,
            "Shinigami", "Shinigami",
            "Raza confirmada por mención directa; habilidades y progresión todavía no reconstruidas.",
            "Race confirmed by direct mention; abilities and progression not yet reconstructed.",
            "La enciclopedia la incluye porque su existencia aparece confirmada, pero no completa una ficha mecánica sin evidencia adicional.",
            "The encyclopedia includes it because its existence is confirmed, but does not create a mechanical profile without additional evidence.",
            false, List.of("race-catalog"),
            src(Confidence.STAFF_CONFIRMED, "06/08/2026", "08 - Razas", "Existencia confirmada.", "Existence confirmed.")),

        e("race-majin", Zone.SERVER, Domain.RACES,
            "Majin", "Majin",
            "Raza confirmada por mención directa; no hay una ficha mecánica completa en la revisión.",
            "Race confirmed by direct mention; the review does not yet contain a complete mechanical profile.",
            "Se conserva el nombre como raza existente y se dejan vacíos stats, requisitos y habilidades que no han sido reconstruidos con evidencia suficiente.",
            "The name is preserved as an existing race while stats, requirements and abilities not reconstructed with enough evidence remain unstated.",
            false, List.of("race-catalog"),
            src(Confidence.STAFF_CONFIRMED, "06/08/2026", "08 - Razas", "Existencia confirmada.", "Existence confirmed.")),

        e("race-undertale-au", Zone.HISTORY, Domain.RACES,
            "Razas ocultas de AUs de Undertale", "Hidden Undertale AU races",
            "Se mencionaron varias razas ocultas obtenidas mediante Trials y otros pasos difíciles.",
            "Several hidden races were mentioned as obtainable through Trials and other difficult steps.",
            "La revisión no conecta aún nombres individuales, Trials, requisitos y recompensas. No se inventan AUs ni rutas concretas para rellenar la ficha.",
            "The review does not yet connect individual names, Trials, requirements and rewards. No AUs or routes are invented to fill the entry.",
            false, List.of("trials-basics", "race-catalog"),
            src(Confidence.HISTORICAL, "06/08/2026", "08 - Razas / 04 - Trials", "Familia de razas oculta, detalles pendientes.", "Hidden race family, details pending.")),

        e("race-slots", Zone.SERVER, Domain.RACES,
            "Slots de raza", "Race slots",
            "Permiten guardar una raza extra; no se plantean como cambio libre de raza durante combate.",
            "Allow storing an extra race; not described as free mid-combat race switching.",
            "La revisión distingue guardar una raza en slot de poder equipar/desequipar razas libremente en combate. También documenta que varias razas se consiguen mediante pasos y no sólo mediante giros.",
            "The review distinguishes storing a race in a slot from freely equipping/unequipping races during combat. It also documents that several races are obtained through steps rather than only rolls.",
            false, List.of("fabled-acquisition", "race-catalog"),
            src(Confidence.STAFF_CONFIRMED, "06/08–21/08/2026", "08 - Razas", "Uso general de slots y obtención.", "General slot use and acquisition.")),

        e("fabled-acquisition", Zone.SERVER, Domain.PROGRESSION,
            "Fabled · obtención", "Fabled · acquisition",
            "Las razas Fabled usan pasos y spins especiales, no giros comunes.",
            "Fabled races use steps and special spins, not common rolls.",
            "Consejo práctico: no gastar giros comunes esperando una Fabled. Los pasos exactos dependen de la raza y todavía no están reconstruidos como catálogo completo.",
            "Practical rule: do not spend common rolls expecting a Fabled. Exact steps depend on the race and are not yet reconstructed as a complete catalog.",
            true, List.of("rarity-order", "race-slots"),
            src(Confidence.STAFF_CONFIRMED, "23/08/2026", "08 - Razas", "Método general de obtención.", "General acquisition method.")),

        e("progression-v1-v4", Zone.SERVER, Domain.PROGRESSION,
            "Progresión racial V1 → V4", "Race progression V1 → V4",
            "V1 suele tener pocos efectos; V2 vuelve efectos/atributos más notorios. Muchos Trials y artefactos piden V4.",
            "V1 tends to have few effects; V2 makes effects/attributes more noticeable. Many Trials and artifacts ask for V4.",
            "No existe una ruta universal V1→V4 válida para todas las razas. Los requisitos deben consultarse por raza. En algunas razas el esquema cambia por completo, como Saiyan, que fue descrita con transformaciones y stats en lugar de V2/V3/V4.",
            "There is no universal V1→V4 route valid for every race. Requirements must be checked per race. Some races use a different scheme entirely, such as Saiyan, described through transformations and stats instead of V2/V3/V4.",
            false, List.of("race-human", "race-saiyan", "trials-basics"),
            src(Confidence.STAFF_CONFIRMED, "02/09–08/09/2026", "02 - Progresión", "Marco general, no receta universal.", "General framework, not a universal recipe.")),

        e("trials-basics", Zone.SERVER, Domain.TRIALS,
            "Trials · conceptos básicos", "Trials · basics",
            "Pueden exigir raza/versión, nivel, kills u objetos; siempre conviene identificar requisito, recompensa, fracaso y repetibilidad.",
            "May require race/version, level, kills or items; identify requirement, reward, failure and repeatability first.",
            "Muchos Trials o artefactos fueron descritos con V4 como primer requisito, pero no todos. Un dato faltante no significa que no exista requisito o castigo. Hay Trials ligados a meditación, progresión racial, armas y razas ocultas.",
            "Many Trials or artifacts were described with V4 as a first requirement, but not all. Missing data does not mean there is no requirement or penalty. Trials are linked to meditation, race progression, weapons and hidden races.",
            true, List.of("trial-meditation", "trial-spire", "progression-v1-v4"),
            src(Confidence.STAFF_CONFIRMED, "2026", "04 - Pruebas y Trials", "Reglas generales de lectura.", "General interpretation rules.")),

        e("trial-meditation", Zone.SERVER, Domain.TRIALS,
            "Trials de meditación", "Meditation Trials",
            "Aparecen en estructuras, pueden pedir niveles de experiencia y recompensar potenciadores de meditación.",
            "Appear in structures, may require experience levels and reward meditation boosters.",
            "La revisión todavía no reconstruye activación, umbral exacto de cada prueba, fracaso, cooldown ni repetibilidad. Esos campos permanecen por verificar.",
            "The review still does not reconstruct activation, exact threshold for each Trial, failure, cooldown or repeatability. Those fields remain unconfirmed.",
            false, List.of("trials-basics", "meditation-levels"),
            src(Confidence.STAFF_CONFIRMED, "06/08/2026", "04 - Pruebas y Trials", "Tipo de Trial confirmado, detalles incompletos.", "Trial type confirmed, details incomplete.")),

        e("trial-spire", Zone.SERVER, Domain.TRIALS,
            "Trial Spire", "Trial Spire",
            "Lugar relacionado con NPCs y solicitud/socialización de Trials; acceso y lista de pruebas siguen incompletos.",
            "Location related to NPCs and Trial requests/socialization; access and Trial list remain incomplete.",
            "El nombre del lugar no identifica una única prueba. No se asumen recompensas o requisitos sin conexión concreta con un Trial específico.",
            "The location name does not identify a single Trial. Rewards or requirements are not assumed without a concrete link to a specific Trial.",
            false, List.of("trials-basics", "missions-npcs"),
            src(Confidence.STAFF_CONFIRMED, "17/05/2026", "04 - Pruebas y Trials", "Función general del lugar.", "General location role.")),

        e("executors-basics", Zone.SERVER, Domain.EXECUTORS,
            "Executores · conceptos básicos", "Executors · basics",
            "Tienen terror radius; se documentaron llaves/esencias y un sistema de sellos. Ediciones/contextos distintos pueden cambiar si son derrotables.",
            "They have a terror radius; keys/essences and a seal system were documented. Different editions/contexts may change whether they are defeatable.",
            "El audio de clips de Executores fue identificado como terror radius. También se documentó que una esencia podía robarse de la espalda sin ser detectado y que quitar sellos por niveles permitía apariciones libres en mundos. Una cifra histórica de 60 Executores y descripciones de inmortalidad pertenecen a etapas/contextos concretos, no a un recuento o regla universal actual.",
            "Executor clip audio was identified as their terror radius. An essence was also documented as stealable from the back without detection, and tiered seal removal allowed free spawning in worlds. A historical count of 60 Executors and descriptions of immortality belong to specific eras/contexts, not a universal current count or rule.",
            true, List.of("executors-history", "raids-basics"),
            src(Confidence.STAFF_CONFIRMED, "2026", "03 - Ejecutores", "Mecánicas generales recuperadas.", "Recovered general mechanics.")),

        e("executors-history", Zone.HISTORY, Domain.EXECUTORS,
            "Executores · cambios de edición", "Executors · edition changes",
            "Primera edición fue descrita como derrotable; otra etapa/contexto describió Executores modernos como inmortales.",
            "First edition was described as defeatable; another era/context described modern Executors as immortal.",
            "La contradicción aparente se conserva como cambio de edición/contexto. No se usa una de esas descripciones para todos los servidores, temporadas o Executores.",
            "The apparent contradiction is preserved as an edition/context change. Neither description is applied to every server, season or Executor.",
            false, List.of("executors-basics"),
            src(Confidence.HISTORICAL, "23/07/2026", "03 - Ejecutores", "Comparación entre ediciones/contextos.", "Edition/context comparison.")),

        e("structures-basics", Zone.SERVER, Domain.STRUCTURES,
            "Estructuras · reglas básicas", "Structures · basic rules",
            "Saquear NPCs puede volverlos hostiles; shrines, puertas y paneles pueden exigir condiciones específicas.",
            "Looting NPCs may make them hostile; shrines, doors and panels may require specific conditions.",
            "Las estructuras de Trials suelen poder pedir raza, nivel, kills u objetos. Si una entrada no responde, primero identificar requisito en vez de forzarla. Antes de activar un shrine conviene conocer condición de activación y forma de salida.",
            "Trial structures may ask for race, level, kills or items. If an entrance does not respond, identify the requirement rather than forcing it. Before activating a shrine, know its trigger condition and exit method.",
            true, List.of("trials-basics", "missions-npcs", "dimensions-basics"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "05 - Estructuras", "Síntesis general no personal.", "Non-personal general synthesis.")),

        e("bosses-basics", Zone.SERVER, Domain.BOSSES,
            "Bosses · reglas básicas", "Bosses · basic rules",
            "Algunos ataques pueden ignorar defensas, matar de inmediato o destruir materia; movilidad y observación importan.",
            "Some attacks may bypass defenses, kill instantly or destroy matter; mobility and observation matter.",
            "Derrotar un boss conocido no implica estar preparado para todos. Twilight fue descrito como una zona con apariciones frecuentes de bosses. Los drops desconocidos conviene conservarlos hasta identificar si se conectan con recetas, Trials o evoluciones.",
            "Defeating one known boss does not imply readiness for all of them. Twilight was described as an area with frequent boss appearances. Unknown drops are worth keeping until it is known whether they connect to recipes, Trials or evolutions.",
            true, List.of("raids-basics", "race-terrarian"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "06 - Jefes", "Resumen general de riesgos.", "General risk summary.")),

        e("missions-npcs", Zone.SERVER, Domain.MISSIONS,
            "Misiones y NPC", "Missions and NPCs",
            "Algunos NPC actúan de forma competitiva: pueden engañar, fingir cooperación o atacar.",
            "Some NPCs act competitively: they may deceive, fake cooperation or attack.",
            "Una misión puede exigir abandonar base, explorar o cumplir condiciones que no aparecen de inmediato. No entregar objetos raros sin comprobar recompensa y siguiente paso. Algunas funciones de NPC históricos pueden cambiar y deben verificarse antes de usarlas como guía actual.",
            "A mission may require leaving base, exploring or meeting conditions that do not appear immediately. Do not hand over rare items without checking reward and next step. Some historical NPC functions may change and should be verified before being used as current guidance.",
            true, List.of("structures-basics", "trials-basics"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "07 - Misiones y NPC", "Reglas generales de interacción.", "General interaction rules.")),

        e("dimensions-basics", Zone.SERVER, Domain.DIMENSIONS,
            "Dimensiones · preparación", "Dimensions · preparation",
            "Entrar con método confirmado de vuelta/escape; portales pueden pedir objetos, energía o condiciones especiales.",
            "Enter with a confirmed return/escape method; portals may require items, energy or special conditions.",
            "Algunas rutas dimensionales requieren vehículos, Trials o recursos de otros sistemas. Históricamente se advirtió que no había freecam en dimensiones nuevas. Conviene registrar ambiente, hazards, mobs y método de salida antes de explorar lejos.",
            "Some dimensional routes require vehicles, Trials or resources from other systems. Historically, freecam was said to be unavailable in new dimensions. Record environment, hazards, mobs and exit method before exploring far.",
            true, List.of("structures-basics", "race-pharaoh"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "16 - Dimensiones", "Preparación general.", "General preparation.")),

        e("meditation-levels", Zone.HISTORY, Domain.CONTRADICTIONS,
            "Niveles de meditación y creación de habilidades", "Meditation levels and ability creation",
            "Hay umbrales históricos distintos: nivel 1, >10, 100 para elementos y una mención a 200 para habilidades 'tipo god'.",
            "Different historical thresholds exist: level 1, >10, 100 for elements and a mention of 200 for 'god-like' abilities.",
            "Las cifras pertenecen a conversaciones/etapas distintas y pueden describir tipos de habilidad diferentes. Nivel 100 fue ligado a crear elementos como viento, agua, fuego, magma o químicos. No usar 1, 10, 100 o 200 como regla universal sin verificar contexto actual.",
            "The numbers belong to different conversations/eras and may describe different ability types. Level 100 was linked to creating elements such as wind, water, fire, magma or chemicals. Do not use 1, 10, 100 or 200 as a universal rule without current context.",
            true, List.of("ability-room", "prompt-design"),
            src(Confidence.CONTRADICTION, "15/08–30/08/2026", "10 - Habilidades / 11 - Energía", "Umbrales incompatibles sin contexto completo.", "Conflicting thresholds without full context.")),

        e("ability-room", Zone.SERVER, Domain.ABILITIES,
            "Room", "Room",
            "Habilidad Hacker usada para control/defensa de zona; puede entrenarse mediante meditación enfocada.",
            "Hacker ability used for area control/defense; can be trained through focused meditation.",
            "La revisión confirma su utilidad para controlar acceso a una base y que Hacker puede concentrar meditación en Room. Un coste de 3000 energía/minuto aparece como experiencia individual en el corpus, por lo que no se conserva como coste universal.",
            "The review confirms its utility for controlling base access and that Hacker can focus meditation on Room. A 3000-energy/minute cost appears as an individual experience in the corpus, so it is not kept as a universal cost.",
            false, List.of("race-hacker", "ability-gate"),
            src(Confidence.STAFF_CONFIRMED, "30/07/2026", "10 - Habilidades", "Función/entrenamiento general.", "General function/training.")),

        e("ability-gate", Zone.SERVER, Domain.ABILITIES,
            "Gate", "Gate",
            "Habilidad Hacker de transporte; en V2 fue descrita con alcance hasta el espacio.",
            "Hacker transport ability; at V2 it was described as reaching space.",
            "Faltan límites completos de distancia, coste y seguridad. Una variante mejorada llamada Rate aparece en experiencias del corpus, pero sus riesgos no se convierten en regla general de Gate.",
            "Full distance, cost and safety limits are missing. An upgraded variant called Rate appears in corpus experiences, but its risks are not converted into a general Gate rule.",
            true, List.of("race-hacker", "ability-room"),
            src(Confidence.STAFF_CONFIRMED, "18/08/2026", "10 - Habilidades", "Capacidad general fechada.", "Dated general capability.")),

        e("assembling-table", Zone.SERVER, Domain.ASSEMBLING,
            "Assembling Table", "Assembling Table",
            "Sirve para instalar trasplantes y chips; se relaciona con la transformación/categoría Cyborg.",
            "Used to install transplants and chips; linked to the Cyborg transformation/category.",
            "Se documentó también una referencia de compra histórica de 4 millones y una acción escrita para colocar/reconocer la mesa. Slots, compatibilidades, materiales por instalación, extracción de implantes y reversibilidad siguen incompletos.",
            "A historical purchase reference of 4 million and a written action to place/recognize the table were also documented. Slots, compatibility, installation materials, implant extraction and reversibility remain incomplete.",
            false, List.of("race-cyborg", "item-improbability-scroll"),
            src(Confidence.STAFF_CONFIRMED, "2026", "14 - Fabricación y Assembling", "Función general; precio histórico separado.", "General function; historical price separate.")),

        e("item-geography-table", Zone.SERVER, Domain.ITEMS,
            "Geography Table", "Geography Table",
            "Investiga información oculta de ciertos objetos/reliquias; referencia histórica de 120 wins.",
            "Researches hidden information on certain items/relics; historical reference price of 120 wins.",
            "El precio de 120 wins fue mencionado el 19/09/2026 y debe conservarse sólo como referencia fechada. La enciclopedia no confirma receta u obtención actual. Su función principal es descubrir información no visible de ciertos objetos o reliquias.",
            "The 120-win price was mentioned on 19/09/2026 and is preserved only as a dated reference. The encyclopedia does not confirm a current recipe or acquisition method. Its main function is discovering hidden information on certain items or relics.",
            false, List.of("relic-basics", "item-daemonium-kit"),
            src(Confidence.STAFF_CONFIRMED, "19/09/2026", "13 - Objetos", "Función confirmada; precio fechado.", "Confirmed function; dated price.")),

        e("item-daemonium-kit", Zone.SERVER, Domain.ITEMS,
            "Daemonium Kit", "Daemonium Kit",
            "Requisito para extraer materiales de reliquias; el desmontaje convencional puede destruirlas.",
            "Required for extracting relic materials; conventional dismantling may destroy them.",
            "Tener el kit no significa conocer todos los pasos de cada reliquia. No hay una receta universal completa de extracción reconstruida en la revisión.",
            "Owning the kit does not mean every relic's steps are known. The review does not reconstruct one universal extraction recipe.",
            true, List.of("relic-basics", "item-geography-table"),
            src(Confidence.STAFF_CONFIRMED, "19/09/2026", "13 - Objetos / 15 - Reliquias", "Requisito general de extracción.", "General extraction requirement.")),

        e("item-improbability-scroll", Zone.SERVER, Domain.ITEMS,
            "Improbability Scroll", "Improbability Scroll",
            "Puede vincular Assembling u otras mesas de Steel y permitir usarlas sin colocarlas.",
            "Can link Assembling or other Steel tables and allow using them without placing them.",
            "La explicación disponible habla de guardar mesas vinculadas en una mochila telepática. Obtención, coste, consumo y límites siguen por verificar.",
            "The available explanation mentions storing linked tables in a telepathic backpack. Acquisition, cost, consumption and limits remain unconfirmed.",
            false, List.of("assembling-table"),
            src(Confidence.STAFF_CONFIRMED, "05/08/2026", "13 - Objetos / 14 - Assembling", "Función general; procedimiento incompleto.", "General function; incomplete procedure.")),

        e("relic-basics", Zone.SERVER, Domain.RELICS,
            "Reliquias · conceptos básicos", "Relics · basics",
            "Investigar efectos ocultos antes de equipar, vender o extraer; Geography Table y Daemonium Kit cubren funciones distintas.",
            "Research hidden effects before equipping, selling or extracting; Geography Table and Daemonium Kit serve different roles.",
            "Geography Table sirve para descubrir información oculta; Daemonium Kit para extracción de materiales. También se documentaron reliquias con counters, parry, rutas mediante Blood Moon/Trials y otros efectos. No se asume que una reliquia muestre todo lo que hace en su descripción inicial.",
            "Geography Table is used to discover hidden information; Daemonium Kit for material extraction. Relics with counters, parry, Blood Moon/Trial routes and other effects were also documented. Do not assume a relic's initial description shows everything it does.",
            true, List.of("item-geography-table", "item-daemonium-kit", "relic-third-justice"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "15 - Reliquias", "Resumen general no personal.", "Non-personal general summary.")),

        e("relic-third-justice", Zone.HISTORY, Domain.RELICS,
            "Third Justice", "Third Justice",
            "Parry ofensivo contra raiders sin bloqueo al milisegundo y efecto de stun; obtención histórica ligada a discos/Purgatorio.",
            "Offensive parry against raiders without millisecond timing plus stun; historical acquisition linked to discs/Purgatory.",
            "La obtención documentada era limitada y usaba discos encontrados en cofres, especialmente ocultos. No existía para esta reliquia el efecto de ojos verdes que indicaba ubicación. Cantidad de discos, pasos completos y vigencia actual siguen sin confirmar.",
            "Documented acquisition was limited and used discs found in chests, especially hidden ones. This relic did not have the green-eye location effect. Disc count, complete steps and current validity remain unconfirmed.",
            false, List.of("relic-basics", "raids-basics"),
            src(Confidence.HISTORICAL, "30/08/2026", "15 - Reliquias", "Función y obtención fechadas.", "Dated function and acquisition.")),

        e("death-revive-history", Zone.HISTORY, Domain.DEATH_REVIVE,
            "Muerte y revive · cambios de sistema", "Death and revive · system changes",
            "Dead→RCP, Mangled→botiquín y Mutilated→desfibrilador fueron reglas históricas y luego se anunció la eliminación de RCP.",
            "Dead→CPR, Mangled→medkit and Mutilated→defibrillator were historical rules, then CPR removal was announced.",
            "La revisión conserva versiones distintas en julio/septiembre de 2026. Por eso la enciclopedia no presenta esa escala antigua como procedimiento vigente. También hubo umbrales contradictorios para Injured y cambios en Bleeding.",
            "The review preserves different July/September 2026 versions. Therefore the encyclopedia does not present the old scale as a current procedure. Conflicting Injured thresholds and Bleeding changes were also documented.",
            true, List.of("respawn-cards", "death-revive-contradictions"),
            src(Confidence.HISTORICAL, "07–09/2026", "19 - Muerte, heridas y reanimación", "Sistema antiguo reemplazado/cambiado.", "Old system replaced/changed.")),

        e("respawn-cards", Zone.SERVER, Domain.DEATH_REVIVE,
            "Respawn Cards", "Respawn Cards",
            "Silver/Diamond Respawn Card fueron descritas como un revive gratis; en SIEGE se indicó activación con cobre.",
            "Silver/Diamond Respawn Cards were described as granting one free revive; SIEGE activation was described with copper.",
            "Este dato es posterior a parte de la escala histórica de RCP/medkit/desfibrilador, pero los costes y condiciones exactas del sistema actual deben seguir verificándose antes de gastar recursos.",
            "This data is later than part of the historical CPR/medkit/defibrillator scale, but exact current costs and conditions should still be verified before spending resources.",
            true, List.of("death-revive-history"),
            src(Confidence.STAFF_CONFIRMED, "17/09/2026", "19 - Muerte, heridas y reanimación", "Mecánica más reciente del export revisado.", "Newer mechanic in the reviewed export.")),

        e("death-revive-contradictions", Zone.HISTORY, Domain.CONTRADICTIONS,
            "Injured y Bleeding · datos contradictorios", "Injured and Bleeding · conflicting data",
            "Los umbrales de Injured cambiaron y Bleeding aparece tanto como removido como separado en dos conceptos.",
            "Injured thresholds changed and Bleeding appears both as removed and split into two different concepts.",
            "Para Injured aparecen valores 4–5, 5 y 6 antes de Dead. Para Bleeding se registró una eliminación y también la distinción entre un bleeding hiperrealista y el estado de un mod. No elegir una versión arbitrariamente sin contexto.",
            "Injured appears with 4–5, 5 and 6 stacks before Dead. Bleeding is recorded both as removed and as a distinction between hyper-realistic bleeding and a mod state. Do not arbitrarily choose one version without context.",
            false, List.of("death-revive-history"),
            src(Confidence.CONTRADICTION, "12/07–09/09/2026", "19 - Muerte, heridas y reanimación", "Cambios/contextos no reconciliados.", "Unreconciled changes/contexts.")),

        e("raids-basics", Zone.SERVER, Domain.RAIDS_EVENTS,
            "Raids y hordas · preparación", "Raids and hordes · preparation",
            "Definir daño, defensa, movilidad, apoyo y rescate; habilidades destructivas necesitan límites claros.",
            "Define damage, defense, mobility, support and rescue; destructive abilities need clear limits.",
            "Hordas y bosses pueden escalar, adaptarse o castigar técnicas repetidas. Conviene entrar con revive, retirada y recuperación de loot preparadas. Si el entorno puede dañarse, fijar radio, objetivos protegidos y condición de cancelación.",
            "Hordes and bosses may scale, adapt or punish repeated techniques. Enter with revive, retreat and loot recovery prepared. If the environment can be damaged, define radius, protected targets and a cancellation condition.",
            true, List.of("bosses-basics", "factions-basics", "prompt-design"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "20 - Raids, hordas y eventos", "Síntesis general no personal.", "Non-personal general synthesis.")),

        e("factions-basics", Zone.SERVER, Domain.FACTIONS,
            "Facciones y enemigos · seguridad operacional", "Factions and enemies · operational security",
            "Pueden adaptarse, sabotear tecnología y atacar bases/recursos; variar métodos y proteger infraestructura.",
            "May adapt, sabotage technology and attack bases/resources; vary methods and protect infrastructure.",
            "No revelar una estrategia completa a entidades desconocidas. Variar armas, tiempos y métodos ayuda frente a counters. Proteger energía, inventarios, infraestructura y rutas de escape. El lore sólo se trata como regla jugable cuando hay explicación directa o resultado observado.",
            "Do not reveal a full strategy to unknown entities. Varying weapons, timing and methods helps against counters. Protect energy, inventories, infrastructure and escape routes. Lore becomes gameplay guidance only when backed by direct explanation or observed result.",
            true, List.of("raids-basics", "bosses-basics"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "21 - Facciones y enemigos", "Síntesis operacional.", "Operational synthesis.")),

        e("economy-basics", Zone.SERVER, Domain.ECONOMY,
            "Economía · conceptos básicos", "Economy · basics",
            "Precios, wins y trades cambian; guardar siempre fecha de una referencia económica.",
            "Prices, wins and trades change; always keep the date of an economic reference.",
            "Los trabajos iniciales de ladrillos fueron descritos como poco rentables; exploración, minerales, fabricación y servicios especializados aparecen como mejores fuentes en distintas etapas. Dinero, carga y mercancías pueden ser físicos y robables. Priorizar supervivencia/movilidad antes de compras marginales.",
            "Early brick jobs were described as low-profit; exploration, minerals, crafting and specialized services appear as better sources across different eras. Money, cargo and goods may be physical and stealable. Prioritize survival/mobility before marginal purchases.",
            true, List.of("item-geography-table", "server-exploration"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "22 - Economía y precios", "Reglas generales no ligadas a cuentas personales.", "General rules not tied to personal accounts.")),

        e("prompt-design", Zone.SERVER, Domain.PROMPTS,
            "Prompts / acciones · cómo limitar riesgos", "Prompts / actions · limiting risk",
            "Definir objetivo, gasto, radio, duración, blancos válidos, protección del entorno y apagado.",
            "Define objective, cost, radius, duration, valid targets, environmental protection and shutdown.",
            "La revisión de acciones escritas muestra que una frase vaga no permite inferir resultado y que ausencia de respuesta tampoco equivale a fallo. Para acciones complejas conviene especificar intención, límites y condición de finalización y después observar el resultado real.",
            "Review of written actions shows that vague wording does not establish an outcome and lack of a response does not equal failure. For complex actions, specify intent, limits and termination condition, then observe the actual result.",
            true, List.of("meditation-levels", "raids-basics"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "23/24 - Prompts", "Principio general derivado de la revisión contextual.", "General principle from contextual review.")),

        e("server-exploration", Zone.SERVER, Domain.PROGRESSION,
            "Exploración como progresión", "Exploration as progression",
            "Explorar es una parte importante del progreso, pero hacerlo sin equipo o sin conocer unidades aumenta mucho el riesgo.",
            "Exploration is an important part of progression, but doing it without gear or unit knowledge greatly increases risk.",
            "La revisión de supervivencia prioriza exploración con objetivo, salida preparada, movilidad y conocimiento del entorno. Técnica/estrategia importan tanto como armadura. Una opción de movimiento/escape fue descrita como muy importante para progresión avanzada.",
            "The survival review prioritizes exploration with an objective, prepared exit, mobility and environment knowledge. Technique/strategy matter as much as armor. A movement/escape option was described as very important for advanced progression.",
            true, List.of("dimensions-basics", "bosses-basics", "economy-basics"),
            src(Confidence.SYSTEM_OBSERVED, "22/09/2026", "01 - Cómo empezar y sobrevivir", "Consejo general de supervivencia/progresión.", "General survival/progression guidance."))
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

    public static List<Entry> server() { return byZone(Zone.SERVER); }
    public static List<Entry> history() { return byZone(Zone.HISTORY); }

    public static List<Entry> critical() {
        return ENTRIES.stream().filter(Entry::critical)
                .sorted(Comparator.comparing((Entry e) -> e.zone() == Zone.SERVER ? 0 : 1)
                        .thenComparing(Entry::id))
                .toList();
    }

    public static List<Entry> search(String query, boolean spanish, Zone zone, int limit) {
        int safeLimit = Math.max(1, Math.min(128, limit));
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
        out.append(entry.id()).append(' ')
                .append(entry.title(spanish)).append(' ')
                .append(entry.summary(spanish)).append(' ')
                .append(entry.body(spanish)).append(' ')
                .append(entry.domain().label(spanish));
        for (String related : entry.related()) out.append(' ').append(related);
        for (Source source : entry.sources()) {
            out.append(' ').append(source.section()).append(' ')
                    .append(source.date()).append(' ')
                    .append(source.confidence().label(spanish)).append(' ')
                    .append(source.note(spanish));
        }
        return out.toString();
    }

    public static String sourceLine(Entry entry, boolean spanish) {
        if (entry == null || entry.sources().isEmpty()) return spanish ? "FUENTE —" : "SOURCE —";
        Source source = entry.sources().get(0);
        String when = source.date().isBlank() ? "" : " · " + source.date();
        String where = source.section().isBlank() ? "" : " · " + source.section();
        return source.confidence().label(spanish) + when + where;
    }

    public static int confidenceAccent(Confidence confidence) {
        if (confidence == null) return 0xFF9AA4AB;
        return switch (confidence) {
            case STAFF_CONFIRMED -> 0xFF68C6D8;
            case SYSTEM_OBSERVED -> 0xFF72C98B;
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
        String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return decomposed.toLowerCase(Locale.ROOT)
                .replace('·', ' ')
                .replaceAll("[^a-z0-9?_-]+", " ")
                .trim();
    }

    private static String safe(String value) { return value == null ? "" : value; }
}
