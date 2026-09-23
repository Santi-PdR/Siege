package uy.santipdr.siege.client;

import java.util.List;

/**
 * Extra current player-facing knowledge recovered from the full SIEGE Discord export.
 * Personal inventories, one-player prices/progress and old superseded recipes are intentionally excluded.
 */
public final class SiegeKnowledgeCorpus50 {
    private SiegeKnowledgeCorpus50() { }

    private static SiegeKnowledgeData.Source src(SiegeKnowledgeData.Confidence confidence,
                                                  String date, String section,
                                                  String es, String en) {
        return new SiegeKnowledgeData.Source(confidence, date, section, es, en);
    }

    private static SiegeKnowledgeData.Entry e(String id, SiegeKnowledgeData.Domain domain,
                                               String titleEs, String titleEn,
                                               String summaryEs, String summaryEn,
                                               String bodyEs, String bodyEn,
                                               boolean critical, List<String> related,
                                               SiegeKnowledgeData.Source... sources) {
        return new SiegeKnowledgeData.Entry(id, SiegeKnowledgeData.Zone.SERVER, domain,
                titleEs, titleEn, summaryEs, summaryEn, bodyEs, bodyEn,
                critical, related, List.of(sources));
    }

    private static final List<SiegeKnowledgeData.Entry> ENTRIES = List.of(
            e("subhuman-rick-sanchez", SiegeKnowledgeData.Domain.RACES,
                    "Rick Sanchez · Subhuman", "Rick Sanchez · Subhuman",
                    "Una variante Subhuman mencionada de forma directa; se trata como una rama de Subhuman y no como Human normal.",
                    "A directly mentioned Subhuman variant; it is treated as a Subhuman branch rather than normal Human.",
                    "Rick Sanchez aparece como una variante de Subhuman. Eso confirma su lugar dentro de esa familia, pero todavía no alcanza para definir con seguridad sus habilidades, rareza o una ruta de evolución completa. Por eso tiene su propia ficha y no hereda reglas de Human, Sorcerer, Evil Morty o Adamantium Human.",
                    "Rick Sanchez appears as a Subhuman variant. That confirms its place in the family, but there is not enough information yet to safely define its abilities, rarity or full evolution path. It therefore has its own entry and does not inherit rules from Human, Sorcerer, Evil Morty or Adamantium Human.",
                    false, List.of("race-subhuman"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "30/08/2026", "Subhuman",
                            "Variante Subhuman mencionada directamente.", "Subhuman variant directly mentioned.")),

            e("ability-room", SiegeKnowledgeData.Domain.ABILITIES,
                    "Room", "Room",
                    "Habilidad de Hacker para controlar una zona y limitar el movimiento dentro de ella.",
                    "Hacker ability used to control an area and limit movement within it.",
                    "Room forma parte de las habilidades conocidas de Hacker y puede entrenarse de manera enfocada mediante meditación. Se ha usado para defender zonas y controlar accesos, pero su gasto de energía puede variar según la progresión, el entrenamiento y otros factores. No se muestra un coste fijo porque no hay un valor reciente que pueda tratarse como universal para todos los usuarios de Hacker.",
                    "Room is one of Hacker's known abilities and can be trained specifically through meditation. It has been used to defend areas and control access, but its energy cost may vary with progression, training and other factors. No fixed cost is shown because there is no recent value that can safely be treated as universal for every Hacker user.",
                    false, List.of("race-hacker", "ability-gate", "meditation-levels"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "30/07/2026", "Hacker · Room",
                            "Función y entrenamiento de Room descritos directamente.", "Room function and training described directly.")),

            e("ability-gate", SiegeKnowledgeData.Domain.ABILITIES,
                    "Gate", "Gate",
                    "Habilidad de transporte de Hacker; versiones superiores pueden alcanzar distancias mucho mayores.",
                    "Hacker transportation ability; higher versions can reach much greater distances.",
                    "Gate permite desplazarse entre puntos y forma parte de la progresión de Hacker. En V2 fue descrito con alcance suficiente para llegar al espacio, pero todavía no hay una tabla completa y actual de distancia, coste y seguridad para cada versión. También existe una variante mejorada llamada Rate. Mientras esos límites no estén claros, la guía explica lo que hace sin inventar cifras ni tratar experiencias de un jugador como una regla general.",
                    "Gate allows travel between points and is part of Hacker progression. At V2 it was described as having enough range to reach space, but there is still no complete current table for distance, cost and safety at every stage. An improved variant called Rate also exists. Until those limits are clear, the guide explains the ability without inventing numbers or treating one player's experience as a universal rule.",
                    true, List.of("race-hacker", "ability-room"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "18/08/2026", "Hacker · Gate",
                            "Alcance general de Gate V2 y existencia de Rate mencionados.", "General V2 Gate range and the existence of Rate were mentioned.")),

            e("executor-maze", SiegeKnowledgeData.Domain.EXECUTORS,
                    "Maze Executor", "Maze Executor",
                    "Uno de los tipos de Executor conocidos; no todos los Executores funcionan igual.",
                    "One known Executor type; Executors do not all work the same way.",
                    "Maze Executor aparece como un tipo distinto dentro de los Executores. Eso significa que no conviene asumir que todos comparten las mismas capacidades, resistencia o forma de perseguirte. Si una variante tiene un dossier propio, ese dossier tiene prioridad sobre la explicación general de Executores.",
                    "Maze Executor appears as a distinct Executor type. This means players should not assume every Executor shares the same abilities, durability or pursuit behavior. When a variant has its own dossier, that dossier takes priority over the general Executor explanation.",
                    false, List.of("executors-basics"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "2026", "Executores",
                            "Maze Executor fue mencionado como variante diferenciada.", "Maze Executor was mentioned as a distinct variant.")),

            e("executor-nearby-warning", SiegeKnowledgeData.Domain.EXECUTORS,
                    "Aviso de Executor cercano", "Nearby Executor warning",
                    "El cliente puede mostrar un aviso cuando hay un Executor cerca.",
                    "The client can show a warning when an Executor is nearby.",
                    "En las versiones recientes se añadió un aviso visual para indicar que un Executor está cerca. Es una señal para prepararte, reagruparte o retirarte; no significa que ya sepas qué tipo de Executor es ni qué ataques tiene. Usalo como advertencia, no como sustituto del Intel.",
                    "Recent versions added a visual warning when an Executor is nearby. It is a cue to prepare, regroup or retreat; it does not identify the exact Executor type or its attacks. Treat it as a warning rather than a replacement for Intel.",
                    true, List.of("executors-basics", "executor-maze"),
                    src(SiegeKnowledgeData.Confidence.SYSTEM_OBSERVED, "09/2026", "Executores",
                            "Aviso de proximidad mencionado durante las pruebas recientes.", "Proximity warning mentioned during recent testing.")),

            e("item-daemonium-kit", SiegeKnowledgeData.Domain.ITEMS,
                    "Daemonium Kit", "Daemonium Kit",
                    "Herramienta usada para recuperar componentes al desarmar ciertas reliquias.",
                    "Tool used to recover components when dismantling certain relics.",
                    "El Daemonium Kit cumple una función distinta a la Geography Table. La Geography Table sirve para investigar propiedades o información oculta; el Daemonium Kit se usa cuando querés extraer componentes de una reliquia al desarmarla. No se fija un precio ni una receta mientras no haya una versión reciente suficientemente clara.",
                    "The Daemonium Kit has a different purpose from the Geography Table. The Geography Table is used to investigate properties or hidden information; the Daemonium Kit is used when extracting components from a relic during dismantling. No price or recipe is fixed unless a sufficiently recent version is clear.",
                    false, List.of("item-geography-table", "relic-basics"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "19/09/2026", "Reliquias",
                            "Uso de extracción confirmado recientemente.", "Extraction use recently confirmed.")),

            e("trial-shrine-global", SiegeKnowledgeData.Domain.TRIALS,
                    "Trial del santuario", "Shrine Trial",
                    "Un Trial global activado desde un santuario; algunos requisitos y recompensas fueron descritos en agosto.",
                    "A global Trial activated from a shrine; some requirements and rewards were described in August.",
                    "Este Trial fue descrito como un evento global iniciado desde un santuario. En agosto se hablaron de bajas, niveles de experiencia y coleccionables verdes repartidos por el Overworld, además de una duración limitada del santuario. Como esos números pueden cambiar con el balance, la guía no los presenta como requisitos permanentes. Si vuelve a activarse, los valores mostrados por el servidor o una confirmación reciente tienen prioridad.",
                    "This Trial was described as a global event started from a shrine. August discussions mentioned kills, experience levels and green collectibles spread around the Overworld, plus a limited shrine duration. Because those numbers can change with balance, the guide does not present them as permanent requirements. If it becomes active again, values shown by the server or a recent confirmation take priority.",
                    false, List.of("trials-basics"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "12/08/2026", "Trials",
                            "Evento de santuario descrito; sus cifras se consideran variables.", "Shrine event described; its numeric requirements are treated as changeable."))
    );

    public static List<SiegeKnowledgeData.Entry> entries() { return ENTRIES; }
}
