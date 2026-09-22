package uy.santipdr.siege.client;

import java.util.List;

/**
 * Server-wide additions recovered for the 4.00 tactical knowledge pass.
 *
 * This file is deliberately non-personal. It only stores reusable Eternal Craft
 * mechanics, dated staff guidance, uncertainty and historical warnings that are
 * useful to any player. Player-specific inventories, builds and progression are
 * forbidden here by regression tests.
 */
public final class SiegeKnowledgeExpansion40 {
    private SiegeKnowledgeExpansion40() { }

    private static SiegeKnowledgeData.Source src(SiegeKnowledgeData.Confidence confidence,
                                                 String date, String section,
                                                 String es, String en) {
        return new SiegeKnowledgeData.Source(confidence, date, section, es, en);
    }

    private static SiegeKnowledgeData.Entry e(String id,
                                              SiegeKnowledgeData.Zone zone,
                                              SiegeKnowledgeData.Domain domain,
                                              String titleEs, String titleEn,
                                              String summaryEs, String summaryEn,
                                              String bodyEs, String bodyEn,
                                              boolean critical,
                                              List<String> related,
                                              SiegeKnowledgeData.Source... sources) {
        return new SiegeKnowledgeData.Entry(id, zone, domain, titleEs, titleEn,
                summaryEs, summaryEn, bodyEs, bodyEn, critical, related, List.of(sources));
    }

    private static final List<SiegeKnowledgeData.Entry> ENTRIES = List.of(
            e("progression-mobility-priority", SiegeKnowledgeData.Zone.SERVER,
                    SiegeKnowledgeData.Domain.PROGRESSION,
                    "Movilidad antes que daño puro", "Mobility before pure damage",
                    "En progreso medio/alto, una vía de escape o movilidad puede ser más valiosa que otro aumento de daño.",
                    "In mid/high progression, an escape or mobility option can be more valuable than another damage increase.",
                    "El staff recomendó priorizar reliquias y herramientas de movilidad/utility en etapas avanzadas. La razón práctica es sobrevivir a raids, explorar y reposicionarse frente a amenazas que pueden adaptarse al daño repetido. Esto no convierte una reliquia concreta en requisito universal: la disponibilidad y los costes cambian con el tiempo.",
                    "Staff recommended prioritizing mobility/utility relics and tools in advanced progression. The practical reason is surviving raids, exploring and repositioning against threats that may adapt to repeated damage. This does not make any specific relic a universal requirement: availability and prices change over time.",
                    true, List.of("server-exploration", "relic-basics", "combat-adaptation"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "12/06/2026",
                            "Consejos de staff · progresión",
                            "Consejo general recuperado del análisis del Discord.",
                            "General guidance recovered from the Discord analysis.")),

            e("combat-adaptation", SiegeKnowledgeData.Zone.SERVER,
                    SiegeKnowledgeData.Domain.BOSSES,
                    "Adaptación a técnicas repetidas", "Adaptation to repeated techniques",
                    "Repetir siempre la misma técnica puede producir resistencias o counters; conviene variar herramientas y estilos.",
                    "Repeatedly using the same technique can produce resistances or counters; vary tools and styles.",
                    "El staff describió adaptación enemiga frente a ataques usados de forma repetitiva. La recomendación general es alternar daño físico, balístico, elemental o utilitario según el enemigo y observar cambios en su comportamiento. La adaptación no debe interpretarse como una fórmula fija para todos los bosses: cada encuentro conserva su dossier o fuente propia.",
                    "Staff described enemy adaptation against repeatedly used attacks. General advice is to alternate physical, ballistic, elemental or utility approaches depending on the enemy and watch for behavioral changes. Adaptation should not be treated as one fixed formula for every boss: each encounter keeps its own dossier or source.",
                    true, List.of("bosses-basics", "raids-basics", "progression-mobility-priority"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "03/06/2026",
                            "Consejos de staff · combate",
                            "La adaptación se conserva como regla general, sin inventar porcentajes ni umbrales.",
                            "Adaptation is preserved as a general rule without invented percentages or thresholds.")),

            e("prompt-precision-framework", SiegeKnowledgeData.Zone.SERVER,
                    SiegeKnowledgeData.Domain.PROMPTS,
                    "Marco de prompts seguros", "Safe prompt framework",
                    "Definir energía, alcance, duración, objetivos válidos, exclusiones y forma de terminar la acción reduce consecuencias inesperadas.",
                    "Define energy, range, duration, valid targets, exclusions and termination to reduce unintended consequences.",
                    "La revisión del corpus muestra una pauta repetida del staff: una acción compleja debe decir qué intenta hacer, cuánto recurso puede gastar, hasta dónde puede llegar, cuánto dura, qué puede afectar y qué no, y cómo se detiene o disipa. Una frase vaga no permite asumir éxito ni fracaso. Después de ejecutar una acción, el resultado observado tiene prioridad sobre la intención escrita.",
                    "Corpus review shows a repeated staff pattern: a complex action should state what it is trying to do, how much resource it may spend, how far it may reach, how long it lasts, what it may and may not affect, and how it stops or dissipates. Vague wording does not prove success or failure. After an action, observed results outrank written intent.",
                    true, List.of("prompt-design", "raid-area-discipline", "source-policy"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "2026",
                            "Consejos de staff · prompts",
                            "Regla reutilizable encontrada en múltiples conversaciones.",
                            "Reusable rule found across multiple conversations.")),

            e("raid-area-discipline", SiegeKnowledgeData.Zone.SERVER,
                    SiegeKnowledgeData.Domain.RAIDS_EVENTS,
                    "Control de área durante raids", "Area control during raids",
                    "Poderes amplios pueden perjudicar estructuras aliadas o atraer amenazas si no se limitan.",
                    "Wide-area powers can harm allied structures or attract threats when left unrestricted.",
                    "El ejemplo más claro recuperado fue una advertencia del staff sobre usar oxidación sin control durante raids. La enseñanza general es aplicable a cualquier acción de área: limitar radio, duración y blancos antes de activarla cerca de aliados, estructuras o posiciones que se quieren conservar. No se extrapolan daños exactos a otras habilidades.",
                    "The clearest recovered example was a staff warning about uncontrolled oxidation during raids. The reusable lesson applies to area actions in general: limit radius, duration and targets before using them near allies, structures or positions worth preserving. Exact damage is not extrapolated to unrelated abilities.",
                    true, List.of("raids-basics", "prompt-precision-framework"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "04/08/2026",
                            "Consejos de staff · raids",
                            "Advertencia de área convertida en principio general, sin datos personales.",
                            "Area warning converted into a general principle without personal data.")),

            e("relic-analysis-workflow", SiegeKnowledgeData.Zone.SERVER,
                    SiegeKnowledgeData.Domain.RELICS,
                    "Flujo de análisis de reliquias", "Relic analysis workflow",
                    "Geography Table identifica propiedades; Daemonium Kit sirve para extraer componentes al desarmar reliquias.",
                    "Geography Table identifies properties; Daemonium Kit is used to extract components when dismantling relics.",
                    "Para reliquias desconocidas, la ruta documentada separa dos funciones: investigar propiedades con Geography Table y extraer componentes con Daemonium Kit cuando corresponda. Los precios y requisitos económicos deben leerse siempre con fecha; esta ficha no fija un precio para evitar mezclar valores históricos con valores actuales.",
                    "For unknown relics, the documented route separates two functions: investigate properties with Geography Table and extract components with a Daemonium Kit when applicable. Prices and economic requirements must always be read with a date; this entry intentionally avoids fixing a price so historical and current values are not mixed.",
                    true, List.of("item-geography-table", "item-daemonium-kit", "relic-basics", "economy-basics"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "22/03/2026",
                            "Reliquias · Geography Table",
                            "Función de investigación documentada.",
                            "Documented research function."),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "19/09/2026",
                            "Reliquias · Daemonium",
                            "Función de extracción documentada.",
                            "Documented extraction function.")),

            e("assembling-planning", SiegeKnowledgeData.Zone.SERVER,
                    SiegeKnowledgeData.Domain.ASSEMBLING,
                    "Planificación de Assembling", "Assembling planning",
                    "Assembling combina tecnología, partes y mejoras; gastar componentes sin plan puede cerrar opciones posteriores.",
                    "Assembling combines technology, parts and upgrades; spending components without a plan can close later options.",
                    "El corpus describe Assembling como un sistema para unir máquinas, chips, implantes y partes biológicas/cibernéticas. El staff aconsejó pensar dónde invertir partes del cuerpo o recursos antes de comprometerlos. Varias recetas concretas siguen incompletas, por lo que esta ficha conserva la regla de planificación sin inventar inputs u outputs que no estén confirmados.",
                    "The corpus describes Assembling as a system for combining machines, chips, implants and biological/cybernetic parts. Staff advised planning where body parts or resources are invested before committing them. Several concrete recipes remain incomplete, so this entry preserves the planning rule without inventing unconfirmed inputs or outputs.",
                    true, List.of("assembling-table", "source-policy", "research-open-questions"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "20–22/05/2026",
                            "Assembling / Cyborgs",
                            "Sistema general y recomendación de planificación.",
                            "General system and planning guidance.")),

            e("revive-repeat-penalties", SiegeKnowledgeData.Zone.HISTORY,
                    SiegeKnowledgeData.Domain.DEATH_REVIVE,
                    "Revive repetido · penalizaciones por verificar", "Repeated revival · penalties to verify",
                    "Hay referencias históricas a castigos acumulativos por reanimaciones repetidas, pero la versión actual no está reconstruida con suficiente certeza.",
                    "Historical references mention stacking penalties after repeated revivals, but the current version is not reconstructed with enough certainty.",
                    "Mensajes de enero de 2026 describieron aumento de esperas o debuffs después de reanimaciones repetidas. La evidencia disponible no permite fijar valores, número exacto de revives ni vigencia actual. Debe consultarse junto a la ficha actual de Respawn Cards y el Manual de Campo antes de tratarlo como regla vigente.",
                    "January 2026 messages described increased waits or debuffs after repeated revivals. Available evidence does not support fixed values, an exact revival count or current validity. Read this alongside the current Respawn Card entry and Field Manual before treating it as a live rule.",
                    true, List.of("respawn-cards", "source-policy"),
                    src(SiegeKnowledgeData.Confidence.UNCONFIRMED, "10/01/2026",
                            "Muerte / revive · histórico",
                            "Referencia antigua conservada para evitar convertirla en regla actual.",
                            "Old reference preserved so it is not mistaken for a current rule.")),

            e("deteriorer-re-overflow-history", SiegeKnowledgeData.Zone.HISTORY,
                    SiegeKnowledgeData.Domain.MEDITATION,
                    "Deteriorer · sobrecarga de RE histórica", "Deteriorer · historical RE overload",
                    "Una versión histórica advertía que meditar con RE llena podía provocar una sobrecarga grave.",
                    "A historical version warned that meditating with full RE could cause a severe overload.",
                    "La advertencia recuperada de enero de 2026 se refería a RE/Deteriorer: antes de meditar se debía revisar la barra de energía y evitar hacerlo estando llena. Como balance, costes y meditación han cambiado con el tiempo, esta ficha permanece en HISTÓRICO y no se presenta como una regla universal para todas las energías o razas.",
                    "The recovered January 2026 warning concerned RE/Deteriorer: players were told to check the energy bar and avoid meditating while it was full. Because balancing, costs and meditation changed over time, this entry remains HISTORICAL and is not presented as a universal rule for every energy or race.",
                    true, List.of("race-deteriorer", "meditation-levels", "source-policy"),
                    src(SiegeKnowledgeData.Confidence.HISTORICAL, "05–06/01/2026",
                            "Meditación / RE",
                            "Advertencia racial histórica; vigencia actual no asumida.",
                            "Historical race-specific warning; current validity is not assumed.")),

            e("research-open-questions", SiegeKnowledgeData.Zone.SERVER,
                    SiegeKnowledgeData.Domain.SOURCES,
                    "Preguntas abiertas del archivo", "Open archive questions",
                    "El índice completo permite encontrar temas, pero varias recetas, rituales, probabilidades y requisitos ocultos siguen sin reconstruirse con certeza.",
                    "The complete index can locate topics, but several recipes, rituals, probabilities and hidden requirements still lack a reliable reconstruction.",
                    "La auditoría del ZIP indexó todo el texto disponible, pero la revisión semántica no está terminada. Siguen requiriendo evidencia fuerte: recetas fragmentadas, pasos exactos de rituales, requisitos invisibles de algunas Trials, probabilidades de drops, costes actuales y mecánicas mencionadas una sola vez. El Research Desk de 4.00 muestra estas zonas sin rellenarlas por suposición.",
                    "The ZIP audit indexed all available text, but semantic review is not complete. Strong evidence is still needed for fragmented recipes, exact ritual steps, hidden requirements for some Trials, drop probabilities, current prices and mechanics mentioned only once. The 4.00 Research Desk exposes these gaps without filling them by assumption.",
                    false, List.of("source-audit", "source-policy"),
                    src(SiegeKnowledgeData.Confidence.SYSTEM_OBSERVED, "22/09/2026",
                            "Auditoría 4.00",
                            "Lista de huecos mantenida explícitamente.",
                            "Explicit list of remaining evidence gaps.")),

            e("newcomer-operational-rule", SiegeKnowledgeData.Zone.SERVER,
                    SiegeKnowledgeData.Domain.OVERVIEW,
                    "Regla operacional para nuevos", "Newcomer operational rule",
                    "Primero entender salida, revive, amenaza y progreso; después experimentar con sistemas costosos o desconocidos.",
                    "Understand exit, revival, threats and progression first; experiment with expensive or unknown systems later.",
                    "El patrón común de los consejos recuperados favorece preparación antes que improvisación: conocer cómo salir de una zona, qué método de revive existe, qué amenaza puede aparecer y qué recurso se arriesga. La versión 4.00 usa esta regla para ordenar el Briefing inicial sin convertir experiencias personales en instrucciones universales.",
                    "The common pattern in recovered guidance favors preparation over improvisation: know how to leave an area, what revival method exists, what threat may appear and what resource is at risk. Version 4.00 uses this rule to order the initial Briefing without turning personal experiences into universal instructions.",
                    true, List.of("server-overview", "server-exploration", "respawn-cards", "executors-basics"),
                    src(SiegeKnowledgeData.Confidence.SYSTEM_OBSERVED, "22/09/2026",
                            "Síntesis de supervivencia",
                            "Principio general construido sólo con información reutilizable del servidor.",
                            "General principle built only from reusable server information."))
    );

    public static List<SiegeKnowledgeData.Entry> entries() {
        return ENTRIES;
    }
}
