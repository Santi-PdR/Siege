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
                    "Rick Sanchez aparece en el registro como una variante de Subhuman. Eso confirma su lugar dentro de esa familia, pero no alcanza para inventar habilidades, rareza o una ruta de evolución completa. La ficha se mantiene separada para que, cuando aparezcan datos nuevos, puedan agregarse sin mezclar reglas de Human, Sorcerer, Evil Morty o Adamantium Human.",
                    "Rick Sanchez appears in the records as a Subhuman variant. That confirms its place in the family, but it is not enough to invent abilities, rarity or a full evolution path. This entry stays separate so new information can be added later without mixing rules from Human, Sorcerer, Evil Morty or Adamantium Human.",
                    false, List.of("race-subhuman"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "30/08/2026", "Subhuman",
                            "Variante Subhuman mencionada directamente.", "Subhuman variant directly mentioned.")),

            e("executor-maze", SiegeKnowledgeData.Domain.EXECUTORS,
                    "Maze Executor", "Maze Executor",
                    "Uno de los tipos de Executor conocidos; no todos los Executores funcionan igual.",
                    "One known Executor type; Executors do not all work the same way.",
                    "Maze Executor aparece como un tipo distinto dentro de los Executores. La información disponible confirma que existen variantes y que no conviene asumir que todos comparten las mismas capacidades, resistencia o forma de perseguirte. Si una variante tiene un dossier propio, ese dossier tiene prioridad sobre la descripción general de Executores.",
                    "Maze Executor appears as a distinct Executor type. Available information confirms that variants exist, so players should not assume every Executor shares the same abilities, durability or pursuit behavior. When a variant has its own dossier, that dossier takes priority over the general Executor description.",
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
                    "Este Trial fue descrito como un evento global iniciado desde un santuario. El registro de agosto menciona requisitos de bajas, niveles de experiencia y coleccionables verdes repartidos por el Overworld, además de una duración limitada del santuario. Como esos números pueden cambiar con balance, la guía actual no los convierte en requisitos permanentes. Si se vuelve a activar, usá los valores mostrados por el servidor o una confirmación reciente.",
                    "This Trial was described as a global event started from a shrine. August records mention kill requirements, experience levels and green collectibles spread around the Overworld, plus a limited shrine duration. Because those values can change with balance, the current guide does not turn them into permanent requirements. If it becomes active again, use the values shown by the server or a recent confirmation.",
                    false, List.of("trials-basics"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "12/08/2026", "Trials",
                            "Evento de santuario documentado; números tratados como variables.", "Shrine event documented; numeric requirements treated as changeable."))
    );

    public static List<SiegeKnowledgeData.Entry> entries() { return ENTRIES; }
}
