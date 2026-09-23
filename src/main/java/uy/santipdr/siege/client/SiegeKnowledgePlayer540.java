package uy.santipdr.siege.client;

import java.util.List;

/**
 * Current player-facing knowledge refinements for SIEGE 5.40.
 *
 * This layer sits after the older encyclopedia files. Reusing an existing ID replaces
 * that older public wording without deleting historical maintenance data. Everything
 * here is server-wide: no player inventories, private progress, names or personal notes.
 */
public final class SiegeKnowledgePlayer540 {
    private SiegeKnowledgePlayer540() { }

    private static SiegeKnowledgeData.Entry e(String id, SiegeKnowledgeData.Domain domain,
                                               String titleEs, String titleEn,
                                               String summaryEs, String summaryEn,
                                               String bodyEs, String bodyEn,
                                               boolean critical, List<String> related) {
        return new SiegeKnowledgeData.Entry(id, SiegeKnowledgeData.Zone.SERVER, domain,
                titleEs, titleEn, summaryEs, summaryEn, bodyEs, bodyEn,
                critical, related, List.of());
    }

    private static final List<SiegeKnowledgeData.Entry> ENTRIES = List.of(
            e("server-overview", SiegeKnowledgeData.Domain.OVERVIEW,
                    "Eternal Craft — SIEGE", "Eternal Craft — SIEGE",
                    "Guerra y supervivencia en 2044: Stronghold 5-5, El Núcleo, facciones, razas, progresión, Trials, Executores, bosses y sistemas propios.",
                    "War and survival in 2044: Stronghold 5-5, the Nucleus, factions, races, progression, Trials, Executors, bosses and custom systems.",
                    "SIEGE transcurre en 2044, después de una Tercera Guerra Mundial que dejó un frente dominado por naciones, corporaciones armadas, tecnología extrema y amenazas que no siguen las reglas de un survival normal. Dominion of Pinzhao y Stronghold 5-5 forman parte central de la resistencia, mientras Nusia, Desperado LLC, World Marshal Inc., SCP Foundation y otras fuerzas aparecen como amenazas del conflicto.\n\nEl servidor combina la supervivencia de Minecraft con razas, habilidades, progresión, Trials, reliquias, dimensiones, tecnología, raids, bosses, NPC aliados y hostiles, vidas limitadas y sistemas de combate propios. El Núcleo añade otra capa: es una IA reactiva capaz de observar lo que ocurre y hacer que determinadas situaciones cambien según el desarrollo del servidor.\n\nNo existe una única ruta correcta. Antes de arriesgar equipo importante conviene revisar la Enciclopedia para sistemas generales y la base Intel para unidades concretas y amenazas actuales.",
                    "SIEGE takes place in 2044 after a Third World War left a front dominated by nations, armed corporations, extreme technology and threats that do not follow normal survival rules. Dominion of Pinzhao and Stronghold 5-5 are central to the resistance, while Nusia, Desperado LLC, World Marshal Inc., the SCP Foundation and other forces appear as threats in the conflict.\n\nThe server combines Minecraft survival with races, abilities, progression, Trials, relics, dimensions, technology, raids, bosses, allied and hostile NPCs, limited lives and custom combat systems. The Nucleus adds another layer: it is a reactive AI capable of observing what happens and causing certain situations to change as the server develops.\n\nThere is no single correct route. Before risking important equipment, use the Encyclopedia for general systems and the Intel database for specific units and current threats.",
                    true, List.of("el-nucleo", "stronghold-55", "factions-current-front", "warfare-pillars", "race-catalog", "death-revive-current")),

            e("el-nucleo", SiegeKnowledgeData.Domain.OVERVIEW,
                    "El Núcleo", "The Nucleus",
                    "IA reactiva del servidor: observa el desarrollo de la partida y puede hacer que eventos, oportunidades o amenazas cambien.",
                    "Reactive server AI: it observes how the game develops and can cause events, opportunities or threats to change.",
                    "El Núcleo no es simplemente un texto de ambientación ni un enemigo con una lista fija de ataques. Funciona como una presencia reactiva dentro de SIEGE: puede observar comportamiento, progreso y decisiones, y responder modificando situaciones del mundo. Dependiendo del contexto, esa respuesta puede abrir una oportunidad, complicar una operación, alterar un evento o convertir algo rutinario en un riesgo.\n\nTambién puede relacionarse con sistemas del servidor como estructuras, intercambios, rituales u otras interacciones cuando el evento correspondiente lo permite. Eso no significa que controle absolutamente todo ni que cada acción provoque una respuesta. La regla práctica es no asumir que dos situaciones iguales van a terminar siempre de la misma forma.\n\nSi aparece una intervención del Núcleo, tratala como información nueva del evento actual: observá qué cambió antes de repetir una estrategia vieja.",
                    "The Nucleus is not merely atmosphere text or an enemy with a fixed attack list. It acts as a reactive presence inside SIEGE: it can observe behaviour, progress and decisions, then respond by changing situations in the world. Depending on context, that response can create an opportunity, complicate an operation, alter an event or turn something routine into a risk.\n\nIt can also interact with server systems such as structures, exchanges, rituals or other mechanics when the relevant event allows it. That does not mean it controls everything or that every action triggers a response. The practical rule is not to assume two similar situations will always end the same way.\n\nWhen a Nucleus intervention appears, treat it as new information about the current event: identify what changed before repeating an old strategy.",
                    true, List.of("server-overview", "warfare-pillars", "raids-basics")),

            e("stronghold-55", SiegeKnowledgeData.Domain.STRUCTURES,
                    "Stronghold 5-5", "Stronghold 5-5",
                    "Punto central del frente de Dominion of Pinzhao y referencia operativa recurrente de SIEGE.",
                    "A central Dominion of Pinzhao front position and a recurring operational reference in SIEGE.",
                    "Stronghold 5-5 funciona como una referencia central para la identidad militar de SIEGE: defensa, despliegue, Intel y respuesta a amenazas se presentan alrededor de esta idea de una fortaleza que todavía mantiene el frente.\n\nNo debe confundirse una referencia narrativa a Stronghold 5-5 con una garantía de seguridad. Raids, unidades especiales, sabotaje, teletransportación y amenazas de alto nivel pueden volver peligrosa una zona fortificada. En especial, varias unidades enemigas están diseñadas para castigar grupos inmóviles, posiciones cerradas o planes demasiado repetidos.\n\nUsá la base Intel antes de una defensa importante: el tipo de amenaza cambia completamente qué tan útil es una fortificación.",
                    "Stronghold 5-5 is a central reference for SIEGE's military identity: defence, deployment, Intel and threat response are framed around the idea of a fortress still holding the front.\n\nA narrative reference to Stronghold 5-5 is not a guarantee of safety. Raids, special units, sabotage, teleportation and high-level threats can make a fortified area dangerous. Several enemy units are specifically designed to punish stationary groups, enclosed positions or overly repetitive plans.\n\nCheck Intel before a major defence: the type of threat completely changes how useful a fortification is.",
                    true, List.of("server-overview", "factions-current-front", "threat-classes", "raids-basics")),

            e("factions-current-front", SiegeKnowledgeData.Domain.FACTIONS,
                    "Facciones del frente", "Front factions",
                    "Dominion of Pinzhao sostiene el frente frente a Nusia, Desperado, World Marshal, SCP y otras amenazas.",
                    "Dominion of Pinzhao holds the front against Nusia, Desperado, World Marshal, SCP and other threats.",
                    "Dominion of Pinzhao es la nación central del lado jugable en el conflicto actual. La República de Nusia es una de las amenazas militares más visibles y concentra muchas unidades registradas en Intel. Desperado LLC y World Marshal Inc. representan fuerzas corporativas armadas, mientras SCP Foundation añade amenazas que no siempre encajan en una guerra convencional.\n\nQue dos enemigos pertenezcan al mismo bando o aparezcan en la misma zona no significa que compartan tácticas. Nusia, por ejemplo, incluye desde tropas comunes hasta unidades avanzadas, Tanks, bosses y otras categorías con respuestas muy diferentes.\n\nLa Enciclopedia explica el contexto general; Intel es la referencia para identificar una unidad concreta, sus capacidades conocidas y la respuesta recomendada.",
                    "Dominion of Pinzhao is the central nation on the playable side of the current conflict. The Republic of Nusia is one of the most visible military threats and accounts for many units recorded in Intel. Desperado LLC and World Marshal Inc. represent armed corporate forces, while the SCP Foundation introduces threats that do not always fit conventional warfare.\n\nTwo enemies sharing a side or an area does not mean they use the same tactics. Nusia, for example, includes everything from common troops to advanced units, Tanks, bosses and other categories that require very different responses.\n\nThe Encyclopedia explains the general context; Intel is the reference for identifying a specific unit, its known capabilities and the recommended response.",
                    true, List.of("server-overview", "stronghold-55", "threat-classes")),

            e("warfare-pillars", SiegeKnowledgeData.Domain.OVERVIEW,
                    "Qué hace distinto a SIEGE", "What makes SIEGE different",
                    "Más de cien mods alrededor de guerra, progresión, vidas limitadas, raids, NPC, bosses y combate de alta dificultad.",
                    "More than one hundred mods built around warfare, progression, limited lives, raids, NPCs, bosses and high-difficulty combat.",
                    "SIEGE no es sólo un menú temático sobre un modpack grande. La experiencia gira alrededor de sistemas que se cruzan entre sí: razas y evoluciones, habilidades, equipo difícil de recuperar, Trials, Executores, bosses, raids grandes, países y facciones, NPC aliados y hostiles, dimensiones, tecnología y eventos reactivos.\n\nEl combate puede pasar rápidamente de una pelea normal a una situación donde importan posicionamiento, movilidad, lectura de señales y coordinación. Los rangos de batalla y la presentación Hack & Slash refuerzan ese ritmo, pero las reglas reales siguen dependiendo de la unidad o sistema que tengas enfrente.\n\nLas vidas limitadas hacen que información y preparación tengan valor real. Si un enemigo aparece en Intel, revisá la ficha antes de probar una táctica cara sólo por intuición.",
                    "SIEGE is not just a themed menu placed over a large modpack. The experience revolves around systems that overlap: races and evolutions, abilities, hard-to-replace equipment, Trials, Executors, bosses, large raids, countries and factions, allied and hostile NPCs, dimensions, technology and reactive events.\n\nCombat can quickly shift from a normal fight into a situation where positioning, mobility, signal reading and coordination matter. Battle ranks and Hack & Slash presentation reinforce that rhythm, but actual rules still depend on the unit or system in front of you.\n\nLimited lives make information and preparation genuinely valuable. If an enemy appears in Intel, read its file before testing an expensive tactic on intuition alone.",
                    true, List.of("death-revive-current", "executors-basics", "bosses-basics", "el-nucleo", "threat-classes")),

            e("threat-classes", SiegeKnowledgeData.Domain.BOSSES,
                    "Cómo leer las amenazas", "How to read threats",
                    "Intel separa unidades comunes, avanzadas, Tanks, bosses, élites y Super Units porque cada categoría puede exigir una respuesta distinta.",
                    "Intel separates common, advanced, Tank, boss, Elite and Super Unit threats because each category can demand a different response.",
                    "No uses sólo los HP para decidir si algo es peligroso. La base Intel registra función, equipo, comportamiento conocido y consejo táctico cuando existe información suficiente. Ejemplos actuales muestran por qué importa la categoría: Zapper aparece como Tank, Tempest como Boss, Agares como Élite y Atlas como Super Unit.\n\nAtlas tiene 125.000.000 HP confirmados, pero la falta de datos sobre otras capacidades se mantiene explícita en vez de rellenarse con habilidades inventadas. Esa misma regla se aplica al resto de Intel: un campo desconocido significa que falta información, no que la unidad no tenga esa capacidad.\n\nAntes de entrar en rango, identificá la unidad y leé su ficha. Las señales previas a un ataque, la necesidad de separación, el uso de cobertura o la prioridad de objetivo pueden importar más que el daño bruto.",
                    "Do not use HP alone to decide whether something is dangerous. Intel records role, equipment, known behaviour and tactical advice when enough information exists. Current examples show why classification matters: Zapper is a Tank, Tempest a Boss, Agares an Elite and Atlas a Super Unit.\n\nAtlas has 125,000,000 confirmed HP, but missing information about its other capabilities remains explicit instead of being filled with invented abilities. The same rule applies across Intel: an unknown field means information is missing, not that the unit lacks that capability.\n\nBefore entering range, identify the unit and read its file. Attack warnings, spacing requirements, cover usage or target priority can matter more than raw damage.",
                    true, List.of("bosses-basics", "factions-current-front", "stronghold-55")),

            e("portal-terminology", SiegeKnowledgeData.Domain.DIMENSIONS,
                    "Gates, Rifts y Agreements", "Gates, Rifts and Agreements",
                    "Gates y Rifts no son lo mismo: Gates se usan como teletransportadores construidos; Rifts se describen como portales interdimensionales.",
                    "Gates and Rifts are not the same: Gates are used as constructed teleporters; Rifts are described as interdimensional portals.",
                    "En la terminología actual de SIEGE, un Gate es un teletransportador construido o casero. Un Rift se trata como una apertura de viaje interdimensional. Mezclar los dos términos puede llevar a interpretar mal una mecánica o una táctica.\n\nAgreement se relaciona con tecnología de portales y suele compararse de forma informal con una versión menos avanzada de Rick Sanchez. Esa comparación sirve para entender el concepto general, no para copiar automáticamente todas las capacidades de ese personaje.\n\nLos detalles específicos sobre alcance, coste, recuperación o interacciones especiales sólo deben darse por seguros cuando estén confirmados en la información actual; los reportes de campo no verificados permanecen separados de los dossiers oficiales.",
                    "In current SIEGE terminology, a Gate is a constructed or homemade teleporter. A Rift is treated as an opening for interdimensional travel. Mixing the terms can lead to misunderstanding a mechanic or tactic.\n\nAgreement is associated with portal technology and is often informally compared with a less advanced version of Rick Sanchez. That comparison explains the general idea; it does not automatically grant every capability of that character.\n\nSpecific details about range, cost, recovery or special interactions should only be treated as reliable when confirmed by current information; unverified field reports remain separate from official dossiers.",
                    false, List.of("server-overview", "factions-current-front"))
    );

    public static List<SiegeKnowledgeData.Entry> entries() { return ENTRIES; }
}
