package uy.santipdr.siege.client;

import java.util.List;

/** Latest corrections that arrived after the Discord export used by the 5.00 knowledge pass. */
public final class SiegeKnowledgeCorrections50 {
    private SiegeKnowledgeCorrections50() { }

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
            e("item-defibrillator", SiegeKnowledgeData.Domain.ITEMS,
                    "Desfibrilador", "Defibrillator",
                    "Es la herramienta normal de reanimación del sistema actual.",
                    "It is the normal revival tool in the current system.",
                    "Después del cambio de septiembre, RCP dejó de ser el método general y el desfibrilador pasó a usarse para reanimar los estados de muerte actuales.\n\nCrafteo más reciente que aparece en el export: 3 bloques de hierro + 1 bloque de oro.\n\nNo se muestran recetas anteriores en la ficha normal. Si este crafteo vuelve a cambiar, sólo debe quedar el nuevo.",
                    "After the September change, CPR stopped being the general method and the defibrillator became the normal tool for reviving the current death states.\n\nNewest recipe present in the export: 3 iron blocks + 1 gold block.\n\nOlder recipes are not shown in the normal entry. If this recipe changes again, only the new one should remain.",
                    true, List.of("death-revive-current", "item-medkit"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "12/09/2026", "Desfibrilador",
                            "El export más reciente conserva 3 bloques de hierro + 1 bloque de oro para el desfibrilador.",
                            "The newest export record keeps 3 iron blocks + 1 gold block for the defibrillator.")),

            e("item-medkit", SiegeKnowledgeData.Domain.ITEMS,
                    "Medkit / Botiquín", "Medkit",
                    "Objeto médico para curación; su receta actual cambió después de las versiones guardadas en el export.",
                    "Medical item for healing; its current recipe changed after the versions preserved in the export.",
                    "El Medkit sirve como objeto médico y aparece repetidamente como forma de curarse. El sistema actual de reanimación general usa desfibriladores, así que no se presenta el Medkit como sustituto universal para revivir.\n\nCrafteo actual confirmado en la revisión del 22/09/2026: 3 bloques de hierro + 1 mesa de encantamientos.\n\nLa receta anterior no se muestra porque ya no es la vigente.",
                    "The Medkit is a medical item and repeatedly appears as a way to heal. The current general revival system uses defibrillators, so the Medkit is not presented as a universal revival replacement.\n\nCurrent recipe confirmed during the 2026-09-22 review: 3 iron blocks + 1 enchanting table.\n\nThe previous recipe is not shown because it is no longer current.",
                    false, List.of("death-revive-current", "item-defibrillator"),
                    src(SiegeKnowledgeData.Confidence.SYSTEM_OBSERVED, "22/09/2026", "Crafteos actuales",
                            "Corrección actual aportada durante la revisión 5.00.",
                            "Current correction supplied during the 5.00 review.")),

            e("death-revive-current", SiegeKnowledgeData.Domain.DEATH_REVIVE,
                    "Estados de heridas y reanimación", "Injury states and revival",
                    "Los estados de caída no son iguales; el sistema actual ya no usa RCP como método general.",
                    "Downed states are not all the same; the current system no longer uses CPR as the general method.",
                    "El servidor puede dejar estados como Injured, Incapacitated, Dead, Mangled, Mutilated o Disfigured según lo que haya ocurrido. No todos representan la misma gravedad.\n\nLa regla actual cambió en septiembre: RCP dejó de ser el método general y los desfibriladores pasaron a ser la herramienta normal para reanimar los estados de muerte. El Medkit sigue siendo un objeto médico, pero no se muestra como la regla universal de reanimación.\n\nSi un estado concreto necesita un tratamiento especial, se explica como una excepción en su propia ficha. No se reutiliza la vieja escala Dead→RCP, Mangled→botiquín, Mutilated→desfibrilador como si siguiera vigente.",
                    "The server can apply states such as Injured, Incapacitated, Dead, Mangled, Mutilated or Disfigured depending on what happened. They do not all represent the same severity.\n\nThe current rule changed in September: CPR stopped being the general method and defibrillators became the normal revival tool for death states. The Medkit remains a medical item, but it is not shown as the universal revival rule.\n\nIf a specific state needs special treatment, it is explained as an exception in its own entry. The old Dead→CPR, Mangled→medkit, Mutilated→defibrillator scale is not reused as if it were still current.",
                    true, List.of("item-defibrillator", "item-medkit"),
                    src(SiegeKnowledgeData.Confidence.STAFF_CONFIRMED, "12/09/2026", "Muerte / Revive",
                            "RCP retirado y desfibrilador como método general actual.",
                            "CPR removed and defibrillator used as the current general method."))
    );

    public static List<SiegeKnowledgeData.Entry> entries() { return ENTRIES; }
}
