package uy.santipdr.siege.client;

import java.util.List;

/**
 * SIEGE 4.00.1 newcomer-oriented server summaries.
 * These records are non-personal and intentionally avoid player-specific history.
 */
public final class SiegeKnowledgeExpansion401 {
    private SiegeKnowledgeExpansion401() { }

    private static SiegeKnowledgeData.Source src(String section) {
        return new SiegeKnowledgeData.Source(SiegeKnowledgeData.Confidence.SYSTEM_OBSERVED,
                "22/09/2026", section,
                "Resumen general del servidor.", "General server summary.");
    }

    private static SiegeKnowledgeData.Entry e(String id, SiegeKnowledgeData.Domain domain,
                                              String es, String en,
                                              String summaryEs, String summaryEn,
                                              String bodyEs, String bodyEn,
                                              boolean critical, List<String> related) {
        return new SiegeKnowledgeData.Entry(id, SiegeKnowledgeData.Zone.SERVER, domain,
                es, en, summaryEs, summaryEn, bodyEs, bodyEn,
                critical, related, List.of(src(domain.name())));
    }

    private static final List<SiegeKnowledgeData.Entry> ENTRIES = List.of(
            e("guide-first-hour", SiegeKnowledgeData.Domain.OVERVIEW,
                    "Primeros pasos", "First steps",
                    "Antes de buscar poder, entendé cómo salir, revivir, moverte y reconocer una amenaza.",
                    "Before chasing power, understand how to leave, revive, move and recognize a threat.",
                    "Una entrada segura al servidor empieza por cuatro preguntas: cómo vuelvo si entro a una zona desconocida, qué método de revive tengo disponible, qué amenaza puede aparecer y qué recurso estoy arriesgando. Después conviene reconocer la progresión de tu raza, aprender qué son los Trials y guardar objetos desconocidos hasta saber para qué sirven. Explorar forma parte del progreso, pero hacerlo sin salida o sin movilidad convierte un descubrimiento pequeño en una pérdida grande.",
                    "A safe start begins with four questions: how do I get back from an unknown area, what revival method is available, what threat may appear and what resource am I risking. Then learn your race progression, understand what Trials are and keep unknown items until their purpose is clear. Exploration is part of progression, but exploring without an exit or mobility can turn a small discovery into a major loss.",
                    true, List.of("server-overview", "server-exploration", "respawn-cards", "trials-basics")),

            e("guide-races", SiegeKnowledgeData.Domain.RACES,
                    "Cómo entender las razas", "Understanding races",
                    "Rareza no significa que todas las razas progresen igual: algunas usan V1→V4 y otras tienen rutas especiales.",
                    "Rarity does not mean every race progresses the same way: some use V1→V4 while others have special routes.",
                    "Primero mirá la rareza y después la ruta de progresión. El orden documentado de rarezas va de Común hasta Fabled. Varias razas usan versiones V1, V2, V3 y V4; otras, como Saiyan, fueron descritas mediante transformaciones y estadísticas. Algunas razas se obtienen con pasos, Trials o giros especiales en lugar de giros comunes. Los slots sirven para guardar una raza adicional, no para asumir cambios libres durante combate.",
                    "Check rarity first, then progression route. The documented rarity order runs from Common through Fabled. Several races use V1, V2, V3 and V4; others, such as Saiyan, were described through transformations and stats. Some races are obtained through steps, Trials or special spins rather than common rolls. Race slots store an additional race; they should not be treated as free mid-combat swapping.",
                    false, List.of("rarity-order", "race-catalog", "race-slots", "progression-v1-v4", "fabled-acquisition")),

            e("guide-progression", SiegeKnowledgeData.Domain.PROGRESSION,
                    "Cómo progresa el servidor", "How progression works",
                    "La progresión combina raza, Trials, exploración, objetos, movilidad y sistemas avanzados.",
                    "Progression combines race, Trials, exploration, items, mobility and advanced systems.",
                    "No existe una única barra que resuma todo el progreso. Para muchas razas importa avanzar versiones; para otras importa desbloquear transformaciones o sistemas propios. Los Trials pueden pedir raza, versión, nivel, kills u objetos. A medida que el riesgo sube, movilidad, revive y conocimiento del entorno suelen importar tanto como daño. Objetos, reliquias, Assembling y dimensiones forman rutas paralelas que se conectan con la progresión principal.",
                    "There is no single bar that represents all progression. For many races, version advancement matters; others rely on transformations or race-specific systems. Trials may ask for race, version, level, kills or items. As risk increases, mobility, revival and environment knowledge can matter as much as damage. Items, relics, Assembling and dimensions create parallel routes that connect to the main progression.",
                    true, List.of("progression-v1-v4", "trials-basics", "progression-mobility-priority", "assembling-table", "dimensions-basics")),

            e("guide-trials", SiegeKnowledgeData.Domain.TRIALS,
                    "Antes de entrar a un Trial", "Before entering a Trial",
                    "Identificá requisito, recompensa, condición de fracaso y cómo salir antes de gastar recursos.",
                    "Identify the requirement, reward, failure condition and exit before spending resources.",
                    "Un Trial puede depender de raza, versión, nivel, kills, objeto o una combinación. Que una puerta no responda no significa que esté rota: puede faltar un requisito. También conviene distinguir el lugar del Trial de la prueba concreta; Trial Spire, por ejemplo, se relaciona con NPCs y acceso a pruebas, pero no representa una sola prueba universal. Si falta un dato, la guía lo deja incompleto en vez de asumirlo.",
                    "A Trial may depend on race, version, level, kills, an item or a combination. If a door does not respond it may be missing a requirement rather than being broken. Also distinguish the Trial location from the specific Trial; Trial Spire, for example, relates to NPCs and access to Trials but is not one universal Trial. When a field is unknown, the guide leaves it incomplete rather than guessing.",
                    true, List.of("trials-basics", "trial-spire", "trial-meditation", "structures-basics")),

            e("guide-executors", SiegeKnowledgeData.Domain.EXECUTORS,
                    "Cómo reconocer a un Executor", "Recognizing an Executor",
                    "El terror radius es una señal importante; sellos, llaves o esencias pueden formar parte del sistema según la etapa.",
                    "The terror radius is an important signal; seals, keys or essences may be part of the system depending on the era.",
                    "Los Executores pertenecen a una capa de amenaza distinta de las unidades normales. El audio de terror radius fue documentado como señal de proximidad. También existen registros de esencias, llaves y sellos relacionados con su aparición o control. Las reglas cambiaron entre ediciones: no conviene asumir que todos pueden derrotarse, sellarse o manipularse de la misma forma. Si un dato aparece como histórico, tratá esa parte como referencia antigua.",
                    "Executors belong to a different threat layer from normal units. Terror-radius audio was documented as a proximity warning. Records also mention essences, keys and seals related to their appearance or control. Rules changed between editions: do not assume every Executor can be defeated, sealed or manipulated in the same way. When a detail is marked historical, treat it as old reference material.",
                    true, List.of("executors-basics", "executors-history", "bosses-basics")),

            e("guide-bosses", SiegeKnowledgeData.Domain.BOSSES,
                    "Prepararse para bosses", "Preparing for bosses",
                    "Movilidad, revive y variar técnicas importan; un boss conocido no representa a todos los demás.",
                    "Mobility, revival and varying techniques matter; one known boss does not represent every encounter.",
                    "Algunos bosses pueden atravesar defensas, castigar cercanía, destruir materia o adaptarse a ataques repetidos. Entrar con una sola técnica fuerte no garantiza seguridad. Prepará una salida, un método de revive y más de una forma de responder al enemigo. Drops desconocidos conviene conservarlos hasta saber si se conectan con recetas, Trials, reliquias o progresión.",
                    "Some bosses may bypass defenses, punish close range, destroy matter or adapt to repeated attacks. Entering with one strong technique is not guaranteed safety. Prepare an exit, a revival method and more than one way to respond. Unknown drops are worth keeping until it is known whether they connect to recipes, Trials, relics or progression.",
                    true, List.of("bosses-basics", "combat-adaptation", "raids-basics", "progression-mobility-priority")),

            e("guide-relics", SiegeKnowledgeData.Domain.RELICS,
                    "Reliquias y objetos desconocidos", "Relics and unknown items",
                    "Primero identificar, después equipar, vender o desmontar.",
                    "Identify first, then equip, sell or dismantle.",
                    "Geography Table y Daemonium Kit cubren funciones diferentes: la primera se usa para investigar información oculta de ciertos objetos/reliquias; el segundo aparece ligado a extracción de componentes. Desmontar de forma convencional puede destruir una reliquia. Una descripción inicial tampoco garantiza mostrar todos sus efectos. Si un objeto no tiene uso claro, conservarlo hasta reconocer su sistema suele ser más seguro que gastarlo a ciegas.",
                    "Geography Table and Daemonium Kit serve different roles: the former is used to investigate hidden information on certain items/relics, while the latter is linked to component extraction. Conventional dismantling may destroy a relic. An initial description also does not guarantee every effect is shown. If an item has no clear use, keeping it until its system is understood is safer than spending it blindly.",
                    true, List.of("relic-basics", "relic-analysis-workflow", "item-geography-table", "item-daemonium-kit")),

            e("guide-assembling", SiegeKnowledgeData.Domain.ASSEMBLING,
                    "Assembling y Cyborgs", "Assembling and Cyborgs",
                    "Assembling instala o combina tecnología, chips, implantes y partes; planificar antes evita cerrar opciones.",
                    "Assembling installs or combines technology, chips, implants and parts; planning first avoids closing options.",
                    "La Assembling Table está relacionada con trasplantes, chips e implantes y con la condición/categoría Cyborg. Varias compatibilidades y recetas siguen incompletas, así que no conviene gastar partes raras suponiendo un resultado. Improbability Scroll fue descrito como una forma de vincular mesas y usarlas sin colocarlas. Cada mejora debería evaluarse por utilidad, coste y si compromete materiales difíciles de reemplazar.",
                    "The Assembling Table is related to transplants, chips and implants and to the Cyborg condition/category. Several compatibilities and recipes remain incomplete, so rare parts should not be spent while assuming an outcome. Improbability Scroll was described as a way to link tables and use them without placing them. Evaluate each upgrade by utility, cost and whether it commits hard-to-replace materials.",
                    false, List.of("assembling-table", "assembling-planning", "item-improbability-scroll", "race-cyborg")),

            e("guide-dimensions", SiegeKnowledgeData.Domain.DIMENSIONS,
                    "Entrar a una dimensión", "Entering a dimension",
                    "Confirmá método de regreso, hazards y recursos necesarios antes de alejarte del punto de entrada.",
                    "Confirm return method, hazards and required resources before moving far from the entry point.",
                    "Los accesos dimensionales pueden depender de objetos, energía, vehículos, Trials u otras condiciones. La regla práctica es entrar sabiendo cómo volver. Al llegar, identificá ambiente, hazards, mobs, estructuras y método de salida antes de explorar lejos. Una dimensión puede formar parte de otra ruta de progresión, por lo que salir con vida y conservar información puede ser más valioso que completar toda la exploración de una vez.",
                    "Dimensional access may depend on items, energy, vehicles, Trials or other conditions. The practical rule is to enter while knowing how to return. On arrival, identify environment, hazards, mobs, structures and the exit method before exploring far. A dimension may be part of another progression route, so leaving alive with information may be more valuable than completing the whole exploration at once.",
                    true, List.of("dimensions-basics", "server-exploration", "structures-basics")),

            e("guide-revive", SiegeKnowledgeData.Domain.DEATH_REVIVE,
                    "Muerte y revive", "Death and revival",
                    "El sistema cambió varias veces: separá siempre la regla actual de los métodos históricos.",
                    "The system changed several times: always separate current rules from historical methods.",
                    "Respawn Cards aparecen como un método de revive en información más reciente del archivo. En etapas anteriores existieron escalas de RCP, botiquín y desfibrilador, además de cambios en Injured y Bleeding. Esas versiones antiguas se conservan como histórico y no deben usarse automáticamente como procedimiento actual. Antes de una actividad peligrosa, verificá qué método de revive está disponible en esa etapa del servidor.",
                    "Respawn Cards appear as a revival method in newer archive information. Earlier eras used CPR, medkit and defibrillator scales, alongside changes to Injured and Bleeding. Those older versions remain as history and should not automatically be treated as current procedure. Before a dangerous activity, verify which revival method is available in the current server era.",
                    true, List.of("respawn-cards", "death-revive-history", "death-revive-contradictions", "revive-repeat-penalties")),

            e("guide-economy", SiegeKnowledgeData.Domain.ECONOMY,
                    "Economía y precios", "Economy and prices",
                    "Un precio sin fecha puede quedar viejo; comparar utilidad, riesgo y coste antes de comprar.",
                    "A price without a date can become stale; compare utility, risk and cost before buying.",
                    "Wins, dinero, trades y precios cambian. Algunas mercancías y monedas fueron descritas como objetos físicos que pueden perderse o robarse. Trabajos, exploración, minerales, fabricación y servicios tuvieron rentabilidades distintas según la etapa. La guía usa precios antiguos sólo como referencia y no como valor garantizado. En progresión temprana suele ser más importante asegurar movilidad, revive y herramientas útiles que perseguir una mejora marginal muy cara.",
                    "Wins, money, trades and prices change. Some goods and currencies were described as physical items that can be lost or stolen. Jobs, exploration, minerals, crafting and services had different profitability across eras. The guide uses old prices only as references, never guaranteed values. Early in progression, securing mobility, revival and useful tools is often more important than chasing one expensive marginal upgrade.",
                    false, List.of("economy-basics", "progression-mobility-priority", "item-geography-table")),

            e("guide-actions", SiegeKnowledgeData.Domain.PROMPTS,
                    "Acciones y habilidades complejas", "Complex actions and abilities",
                    "Definí objetivo, alcance, duración, gasto, blancos válidos y cómo termina la acción.",
                    "Define objective, range, duration, cost, valid targets and how the action ends.",
                    "Las acciones complejas funcionan mejor cuando los límites están claros. Si una habilidad puede afectar área, especificá qué debe protegerse. Si consume energía, fijá cuánto puede gastar o cuándo debe detenerse. Si una orden es vaga, el resultado no puede deducirse sólo por la intención escrita. Después de ejecutar, observá qué ocurrió realmente antes de asumir que la mecánica funciona como esperabas.",
                    "Complex actions work better when their limits are clear. If an ability affects an area, specify what must be protected. If it consumes energy, define how much it may spend or when it should stop. If an instruction is vague, its result cannot be inferred from intent alone. After execution, observe what actually happened before assuming the mechanic works as expected.",
                    true, List.of("prompt-design", "prompt-precision-framework", "raid-area-discipline"))
    );

    public static List<SiegeKnowledgeData.Entry> entries() { return ENTRIES; }
}
