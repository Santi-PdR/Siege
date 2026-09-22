package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Public-facing SIEGE 4.0 reference model.
 *
 * It intentionally hides research/source plumbing from ordinary players. The
 * server encyclopedia remains source-aware internally, but UI surfaces consume
 * short, readable cards grouped by what a new player actually wants to learn.
 */
public final class Siege4ReferenceData {
    public record Card(String id, String titleEs, String titleEn,
                       String summaryEs, String summaryEn,
                       String bodyEs, String bodyEn,
                       String tagEs, String tagEn, int accent) {
        public String title(boolean es) { return es ? titleEs : titleEn; }
        public String summary(boolean es) { return es ? summaryEs : summaryEn; }
        public String body(boolean es) { return es ? bodyEs : bodyEn; }
        public String tag(boolean es) { return es ? tagEs : tagEn; }
    }

    public record Race(String id, String name, String rarity,
                       String roleEs, String roleEn,
                       String detailEs, String detailEn) {
        public String role(boolean es) { return es ? roleEs : roleEn; }
        public String detail(boolean es) { return es ? detailEs : detailEn; }
    }

    public record MusicIdea(String title, String useEs, String useEn, boolean bundled) {
        public String use(boolean es) { return es ? useEs : useEn; }
    }

    private Siege4ReferenceData() { }

    private static Card card(String id, String es, String en, String summaryEs, String summaryEn,
                             String bodyEs, String bodyEn, String tagEs, String tagEn, int accent) {
        return new Card(id, es, en, summaryEs, summaryEn, bodyEs, bodyEn, tagEs, tagEn, accent);
    }

    public static List<Card> recruit() {
        return List.of(
            card("welcome", "QUÉ ES SIEGE", "WHAT SIEGE IS",
                    "Guerra y supervivencia en 2044, con progresión, facciones, IA, raids, bosses y sistemas propios.",
                    "War and survival in 2044, with progression, factions, AI, raids, bosses and custom systems.",
                    "SIEGE forma parte de Eternal Craft y está planteado como una guerra persistente. El mundo combina armamento moderno y futurista, unidades especiales, facciones, bosses, dimensiones, razas, Trials y sistemas de supervivencia. No conviene tratarlo como un survival vanilla: antes de explorar, entendé revive, progresión, amenazas y rutas de escape.",
                    "SIEGE is part of Eternal Craft and is built around persistent war. The world combines modern and near-future weapons, special units, factions, bosses, dimensions, races, Trials and survival systems. Treating it like vanilla survival is risky: learn revival, progression, threats and escape routes before exploring.",
                    "INTRO", "INTRO", SiegeTheme.CYAN),
            card("first-hour", "PRIMERA HORA", "FIRST HOUR",
                    "Aprendé las rutas principales antes de invertir recursos.",
                    "Learn the main routes before spending resources.",
                    "Prioridad inicial: 1) entender tu raza y cómo progresa; 2) identificar cómo funciona el revive actual; 3) conseguir movilidad y una ruta de retirada; 4) revisar Trials y estructuras antes de activarlas; 5) conservar objetos raros hasta saber si sirven para reliquias, crafting o progresión; 6) usar Intel para reconocer amenazas antes de pelear.",
                    "Initial priorities: 1) understand your race and progression; 2) learn the current revival system; 3) secure mobility and an escape route; 4) inspect Trials and structures before activating them; 5) keep unusual items until you know whether they matter for relics, crafting or progression; 6) use Intel to identify threats before fighting.",
                    "RECLUTA", "RECRUIT", SiegeTheme.GREEN),
            card("races", "RAZAS Y RAREZA", "RACES AND RARITY",
                    "Las razas cambian progresión, habilidades y acceso a sistemas.",
                    "Races change progression, abilities and access to systems.",
                    "La escala documentada es: Común → Poco común → Raro → Ultra raro → Legendario → Obsainan → Mítico → Godly → Eternal → Fabled. No todas las razas usan la misma progresión. Human, Hacker, Saiyan, Deteriorer, Cyborg y otras tienen rutas o excepciones propias.",
                    "The documented scale is: Common → Uncommon → Rare → Ultra Rare → Legendary → Obsainan → Mythic → Godly → Eternal → Fabled. Not every race uses the same progression. Human, Hacker, Saiyan, Deteriorer, Cyborg and others have their own routes or exceptions.",
                    "RAZAS", "RACES", SiegeTheme.GOLD),
            card("progression", "PROGRESIÓN", "PROGRESSION",
                    "V1→V4 es importante, pero no es una receta universal.",
                    "V1→V4 matters, but it is not one universal recipe.",
                    "V1 suele tener pocos efectos y V2 vuelve varias capacidades más notorias. Muchos Trials y artefactos usan V4 como requisito, pero cada raza puede pedir pasos distintos. Saiyan es una excepción importante porque su progreso se describe mediante transformaciones y stats en lugar de seguir exactamente el esquema V1–V4.",
                    "V1 tends to have few effects and V2 makes several capabilities more noticeable. Many Trials and artifacts use V4 as a requirement, but each race may need different steps. Saiyan is an important exception because its progression is described through transformations and stats instead of strictly following V1–V4.",
                    "PROGRESIÓN", "PROGRESSION", SiegeTheme.GOLD),
            card("trials", "TRIALS", "TRIALS",
                    "Pueden pedir raza, versión, nivel, kills, objetos o condiciones especiales.",
                    "May require race, version, level, kills, items or special conditions.",
                    "No fuerces una Trial o estructura si no responde. Primero identificá el requisito, qué pasa si fallás, si puede repetirse y qué recompensa entrega. Hay Trials vinculadas a progresión de raza, armas, meditación y razas ocultas.",
                    "Do not force a Trial or structure if it does not respond. First identify the requirement, failure condition, repeatability and reward. Trials can be tied to race progression, weapons, meditation and hidden races.",
                    "TRIAL", "TRIAL", SiegeTheme.ORANGE),
            card("revive", "MUERTE Y REVIVE", "DEATH AND REVIVAL",
                    "El sistema cambió varias veces; usá el Manual de Campo como referencia operativa.",
                    "The system changed several times; use the Field Manual as the operational reference.",
                    "No memorices una escala antigua como si fuera permanente. SIEGE tuvo cambios en estados como Injured, Bleeding, Dead y revive. Para una partida actual, el Manual de Campo debe ser la referencia práctica; la Enciclopedia conserva cambios históricos sólo para evitar confusiones.",
                    "Do not memorize an old state ladder as permanent. SIEGE has changed states such as Injured, Bleeding, Dead and revival. For current play, use the Field Manual as the practical reference; the Encyclopedia keeps historical changes only to prevent confusion.",
                    "SUPERVIVENCIA", "SURVIVAL", SiegeTheme.RED),
            card("loot", "NO TIRES LO RARO", "KEEP UNUSUAL LOOT",
                    "Objetos extraños pueden conectar con Trials, reliquias o sistemas posteriores.",
                    "Unusual items may connect to Trials, relics or later systems.",
                    "Discos, fragmentos, llaves, materiales de boss y objetos sin explicación pueden tener usos posteriores. Geography Table sirve para investigar información oculta de ciertos objetos y reliquias; Daemonium Kit está vinculado a extracción de materiales de reliquias.",
                    "Discs, fragments, keys, boss materials and unexplained items may have later uses. Geography Table can reveal hidden information on some items and relics; Daemonium Kit is tied to relic material extraction.",
                    "OBJETOS", "ITEMS", SiegeTheme.BLUE),
            card("mobility", "MOVILIDAD Y RETIRADA", "MOBILITY AND RETREAT",
                    "No todo combate debería terminar en matar al objetivo.",
                    "Not every encounter should end with killing the target.",
                    "Explorar sin una salida clara aumenta muchísimo el riesgo. Tener movilidad, una ruta de vuelta y una opción de retirada es parte de la progresión. En dimensiones, raids y encuentros con bosses, conocer cómo escapar puede ser más importante que tener más daño.",
                    "Exploring without a clear exit greatly increases risk. Mobility, a return route and a retreat option are part of progression. In dimensions, raids and boss encounters, knowing how to leave can matter more than having more damage.",
                    "MOVILIDAD", "MOBILITY", SiegeTheme.CYAN)
        );
    }

    public static List<Race> races() {
        return List.of(
            new Race("human", "Human", "Común / variable", "Progresión rápida hacia V4", "Fast route toward V4",
                    "Buena referencia para entender el sistema V1–V4. Fue descrita como una de las rutas más rápidas hacia V4, pero eso no elimina requisitos de Trials o artefactos.",
                    "A useful reference for understanding V1–V4. It was described as one of the fastest routes toward V4, but that does not remove Trial or artifact requirements."),
            new Race("hacker", "Hacker", "Variable", "Energía, Room, Gate y sabotaje", "Energy, Room, Gate and sabotage",
                    "Tiene progresión propia y puede concentrar meditación en capacidades específicas. Room se relaciona con control de zona y Gate con transporte. Costes y límites pueden variar según progreso o dones.",
                    "Has its own progression and can focus meditation on specific capabilities. Room is tied to area control and Gate to transport. Costs and limits can vary with progression or gifts."),
            new Race("shark", "Shark", "Variable", "Ventajas acuáticas", "Aquatic advantages",
                    "Tiene ventajas relacionadas con agua, pero eso no garantiza superioridad contra todas las amenazas. Parte de su progresión documentada es histórica y puede haber cambiado.",
                    "Has water-related advantages, but that does not guarantee superiority against every threat. Part of its documented progression is historical and may have changed."),
            new Race("saiyan", "Saiyan", "Obsainan", "Transformaciones y entrenamiento", "Transformations and training",
                    "Su progreso se describe mediante transformaciones y stats, no simplemente V1–V4. Entrenamiento de transformación y teleport son conceptos importantes. Hay variantes documentadas como Flame, Evil, radiactivo, DB AF y Xeno Saiyan.",
                    "Progression is described through transformations and stats rather than simply V1–V4. Transformation control and teleport training are important concepts. Documented variants include Flame, Evil, radioactive, DB AF and Xeno Saiyan."),
            new Race("deteriorer", "Deteriorer", "Obsainan", "Desgaste y deterioro progresivo", "Progressive wear and deterioration",
                    "Su identidad gira alrededor del deterioro progresivo, no del daño instantáneo. Versiones antiguas tuvieron efectos de bloqueo mucho más largos; esos valores no deberían asumirse como actuales.",
                    "Its identity revolves around progressive deterioration rather than instant damage. Older versions had much longer disable effects; those values should not be assumed current."),
            new Race("pharaoh", "Faraón", "Variable", "Dimensión desértica", "Desert dimension",
                    "Fue descrita con acceso/control sobre una dimensión desértica propia. Su disponibilidad cambió entre etapas del servidor, por lo que poder y disponibilidad deben tratarse por separado.",
                    "Was described with access/control over its own desert dimension. Availability changed between server eras, so power and availability should be treated separately."),
            new Race("apotheosis", "Apotheosis", "Eternal", "Fe y efectos ligados a deidades", "Faith and deity-linked effects",
                    "Su concepto está ligado a la fe del personaje. No existe una fórmula universal documentada para convertir cualquier deseo o expectativa en un efecto garantizado.",
                    "Its concept is tied to character faith. There is no documented universal formula that turns any wish or expectation into a guaranteed effect."),
            new Race("death", "Muerte", "Variable", "Absorción de almas de bosses", "Boss soul absorption",
                    "Está relacionada con absorber almas de bosses. El nombre también aparece en otros contextos, así que no conviene atribuir toda mención a la raza.",
                    "Related to absorbing boss souls. The name also appears in other contexts, so not every mention should be attributed to the race."),
            new Race("cyborg", "Cyborg", "Variable", "Implantes, chips y Assembling", "Implants, chips and Assembling",
                    "Se conecta con Assembling Table, trasplantes y chips. Portar tecnología no significa automáticamente poseer todas las capacidades de la raza.",
                    "Connects to Assembling Table, transplants and chips. Carrying technology does not automatically grant every race capability."),
            new Race("ghoul", "Ghoul", "Variable", "Raza distinta de enemigos ghoul", "Race distinct from ghoul enemies",
                    "Existe como raza, pero el mismo nombre también se usa para enemigos. No mezcles rutas o drops de criaturas con progresión racial sin confirmación.",
                    "Exists as a race, but the same name is also used for enemies. Do not mix creature routes or drops with race progression without confirmation."),
            new Race("subhuman", "Subhuman", "Variable", "Familia de variantes humanas", "Family of human variants",
                    "Incluye variantes documentadas como Adamantium Human y Sorcerer. Cada variante tiene identidad propia y no se deben transferir habilidades entre ellas por suposición.",
                    "Includes documented variants such as Adamantium Human and Sorcerer. Each variant has its own identity and abilities should not be transferred between them by assumption."),
            new Race("terrarian", "Terrariano", "Variable", "Enfoque útil contra bosses", "Useful boss-oriented route",
                    "Fue recomendada para enfrentamientos contra bosses. No hay un árbol completo de stats o requisitos reconstruido todavía.",
                    "Was recommended for boss encounters. A complete stats or requirements tree has not yet been reconstructed."),
            new Race("kaioshin", "Kaioshin", "Variable", "Progresión propia", "Own progression route",
                    "Su existencia está confirmada. Existen ejemplos históricos de progresión, pero no deben usarse como receta universal para la versión actual.",
                    "Its existence is confirmed. Historical progression examples exist, but should not be used as a universal recipe for the current version."),
            new Race("dragon", "Dragon", "Variable", "Múltiples variantes", "Multiple variants",
                    "Se documentaron al menos tres variantes, una relacionada con Blox Fruits. Faltan datos suficientes para asignar stats o progresión a todas.",
                    "At least three variants were documented, one related to Blox Fruits. There is not enough data to assign stats or progression to all of them."),
            new Race("shinigami", "Shinigami", "Variable", "Raza confirmada", "Confirmed race",
                    "Su existencia está confirmada, pero habilidades y progresión completa siguen incompletas en la información disponible.",
                    "Its existence is confirmed, but complete abilities and progression remain incomplete in available information."),
            new Race("majin", "Majin", "Variable", "Raza confirmada", "Confirmed race",
                    "Su existencia está confirmada, pero no se completan stats, requisitos o habilidades sin información suficiente.",
                    "Its existence is confirmed, but stats, requirements or abilities are not filled in without enough information."),
            new Race("undertale-au", "Razas ocultas de AUs", "Fabled / especial", "Rutas ocultas y Trials", "Hidden routes and Trials",
                    "Se mencionaron razas ocultas vinculadas a AUs de Undertale y obtenidas mediante Trials u otros pasos difíciles. Los nombres y rutas completas no están reconstruidos.",
                    "Hidden races linked to Undertale AUs were mentioned as obtainable through Trials or other difficult steps. Complete names and routes have not been reconstructed.")
        );
    }

    public static List<Card> progression() {
        return List.of(
            card("v1-v4", "V1 → V4", "V1 → V4", "Marco común de progresión racial.", "Common race-progression framework.",
                    "V1 suele mostrar pocos efectos. V2 hace más visibles atributos/capacidades. V3 y V4 dependen mucho más de la raza. Muchos Trials y artefactos usan V4 como requisito, pero no existe una sola receta válida para todas las razas.",
                    "V1 usually shows few effects. V2 makes attributes/capabilities more noticeable. V3 and V4 depend much more on the race. Many Trials and artifacts use V4 as a requirement, but there is no single recipe valid for every race.",
                    "RAZAS", "RACES", SiegeTheme.GOLD),
            card("trials", "TRIALS", "TRIALS", "Progresión condicionada por requisitos.", "Progression gated by requirements.",
                    "Una Trial puede pedir raza, versión, nivel, kills, objetos o condiciones especiales. Antes de empezar, identificá requisito, fallo, recompensa y repetibilidad. Algunas Trials desbloquean armas, razas ocultas o mejoras.",
                    "A Trial may require race, version, level, kills, items or special conditions. Before starting, identify requirement, failure, reward and repeatability. Some Trials unlock weapons, hidden races or upgrades.",
                    "TRIAL", "TRIAL", SiegeTheme.ORANGE),
            card("explore", "EXPLORACIÓN", "EXPLORATION", "Explorar también es progresar.", "Exploration is progression too.",
                    "NPCs, Trials, objetos, estructuras y rutas dimensionales aparecen fuera de la base. Salir sin movilidad, plan de retorno o conocimiento del entorno convierte progresión en pérdida de recursos.",
                    "NPCs, Trials, items, structures and dimensional routes appear outside the base. Leaving without mobility, a return plan or environment knowledge turns progression into resource loss.",
                    "CAMPO", "FIELD", SiegeTheme.CYAN),
            card("relics", "RELIQUIAS", "RELICS", "Investigar antes de vender, equipar o desmontar.", "Research before selling, equipping or dismantling.",
                    "Geography Table ayuda a revelar información oculta. Daemonium Kit está ligado a extracción de materiales. Algunas reliquias se conectan con Trials, Blood Moon, discos u otros sistemas.",
                    "Geography Table helps reveal hidden information. Daemonium Kit is tied to material extraction. Some relics connect to Trials, Blood Moon, discs or other systems.",
                    "RELIQUIAS", "RELICS", SiegeTheme.BLUE),
            card("assembling", "ASSEMBLING", "ASSEMBLING", "Ruta tecnológica para chips, trasplantes y Cyborg.", "Technology route for chips, transplants and Cyborg.",
                    "Assembling Table se usa para instalar mejoras avanzadas. Improbability Scroll puede vincular mesas de Steel para utilizarlas sin colocarlas. Slots, extracción y compatibilidad exacta dependen de la pieza/sistema.",
                    "Assembling Table is used for advanced upgrades. Improbability Scroll can link Steel tables for use without placing them. Exact slots, extraction and compatibility depend on the component/system.",
                    "TECNOLOGÍA", "TECH", SiegeTheme.CYAN),
            card("dimensions", "DIMENSIONES", "DIMENSIONS", "Entrar con método de vuelta confirmado.", "Enter with a confirmed return method.",
                    "Portales y teletransportadores pueden pedir objetos, energía o condiciones. Antes de alejarte, identificá salida, hazards, mobs y recursos necesarios. Algunas rutas dimensionales se conectan con Trials o progresión avanzada.",
                    "Portals and teleporters may require items, energy or conditions. Before going far, identify the exit, hazards, mobs and required resources. Some dimensional routes connect to Trials or advanced progression.",
                    "DIMENSIÓN", "DIMENSION", SiegeTheme.BLUE),
            card("economy", "ECONOMÍA", "ECONOMY", "Invertir en supervivencia antes que en compras marginales.", "Invest in survival before marginal purchases.",
                    "Precios y trades cambian. Algunas referencias históricas sirven para comparar, no para asumir valores actuales. Exploración, minerales, fabricación y servicios especializados aparecen como rutas de dinero mejores que trabajos iniciales poco rentables.",
                    "Prices and trades change. Historical references are useful for comparison, not as permanent current values. Exploration, minerals, crafting and specialized services appear as better money routes than low-profit starter jobs.",
                    "ECONOMÍA", "ECONOMY", SiegeTheme.GREEN)
        );
    }

    public static List<MusicIdea> musicIdeas() {
        return List.of(
            new MusicIdea("Music Box", "Lobby / navegación tranquila", "Lobby / calm navigation", false),
            new MusicIdea("Convenience Store", "Lobby secundario / descanso", "Secondary lobby / downtime", false),
            new MusicIdea("New Store", "Galería / Arsenal", "Gallery / Armory", false),
            new MusicIdea("Jazz Music", "Enciclopedia / lectura", "Encyclopedia / reading", false),
            new MusicIdea("From the Ashes", "Cierre victorioso / momento importante", "Victory / important completion", false),
            new MusicIdea("Sad Choir", "Derrota / archivo oscuro", "Defeat / dark archive", false),
            new MusicIdea("Kaptain Music Box", "Pista DVN ya preparada en SIEGE", "DVN track already prepared in SIEGE", true)
        );
    }

    public static List<SiegeKnowledgeData.Entry> knowledgeByDomains(SiegeKnowledgeData.Domain... domains) {
        List<SiegeKnowledgeData.Entry> out = new ArrayList<>();
        for (SiegeKnowledgeData.Entry entry : SiegeKnowledgeData.entries()) {
            for (SiegeKnowledgeData.Domain domain : domains) {
                if (entry.domain() == domain) { out.add(entry); break; }
            }
        }
        return List.copyOf(out);
    }
}
