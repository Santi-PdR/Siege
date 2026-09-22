package uy.santipdr.siege.client;

import java.util.List;

/** Detailed SIEGE 5.00 Atlas classification. Briefing is intentionally separate. */
public final class SiegeAtlasIndex {
    public enum View { RACES, PROGRESSION, SYSTEMS, ITEMS }

    private SiegeAtlasIndex() { }

    public static List<SiegeKnowledgeData.Entry> entries(View view, String query, boolean spanish, int limit) {
        int safeLimit = Math.max(1, Math.min(128, limit));
        List<SiegeKnowledgeData.Entry> pool = query == null || query.isBlank()
                ? SiegeKnowledgeRegistry.entries()
                : SiegeKnowledgeRegistry.search(query, spanish, 128);
        return pool.stream().filter(entry -> belongs(view, entry)).limit(safeLimit).toList();
    }

    public static int count(View view) {
        return (int) SiegeKnowledgeRegistry.entries().stream().filter(entry -> belongs(view, entry)).count();
    }

    public static boolean belongs(View view, SiegeKnowledgeData.Entry entry) {
        if (view == null || entry == null) return false;
        return switch (view) {
            case RACES -> entry.domain() == SiegeKnowledgeData.Domain.RACES;
            case PROGRESSION -> entry.domain() == SiegeKnowledgeData.Domain.PROGRESSION
                    || entry.domain() == SiegeKnowledgeData.Domain.TRIALS
                    || entry.domain() == SiegeKnowledgeData.Domain.ABILITIES
                    || entry.domain() == SiegeKnowledgeData.Domain.MEDITATION;
            case SYSTEMS -> entry.domain() == SiegeKnowledgeData.Domain.EXECUTORS
                    || entry.domain() == SiegeKnowledgeData.Domain.BOSSES
                    || entry.domain() == SiegeKnowledgeData.Domain.DEATH_REVIVE
                    || entry.domain() == SiegeKnowledgeData.Domain.STRUCTURES
                    || entry.domain() == SiegeKnowledgeData.Domain.MISSIONS
                    || entry.domain() == SiegeKnowledgeData.Domain.RAIDS_EVENTS
                    || entry.domain() == SiegeKnowledgeData.Domain.FACTIONS
                    || entry.domain() == SiegeKnowledgeData.Domain.DIMENSIONS;
            case ITEMS -> entry.domain() == SiegeKnowledgeData.Domain.ITEMS
                    || entry.domain() == SiegeKnowledgeData.Domain.RELICS
                    || entry.domain() == SiegeKnowledgeData.Domain.ASSEMBLING
                    || entry.domain() == SiegeKnowledgeData.Domain.ECONOMY;
        };
    }

    public static String label(View view, boolean spanish) {
        return switch (view) {
            case RACES -> spanish ? "RAZAS" : "RACES";
            case PROGRESSION -> spanish ? "PROGRESIÓN" : "PROGRESSION";
            case SYSTEMS -> spanish ? "SISTEMAS" : "SYSTEMS";
            case ITEMS -> spanish ? "OBJETOS" : "ITEMS";
        };
    }

    public static String description(View view, boolean spanish) {
        return switch (view) {
            case RACES -> spanish
                    ? "Razas y variantes conocidas. Para rarezas y búsqueda rápida también podés abrir el Atlas de Razas."
                    : "Known races and variants. For rarities and quick lookup you can also open the Race Atlas.";
            case PROGRESSION -> spanish
                    ? "Cómo avanzan las razas, qué son V1–V4, rutas especiales, habilidades, meditación y Trials."
                    : "How races advance, what V1–V4 mean, special paths, abilities, meditation and Trials.";
            case SYSTEMS -> spanish
                    ? "Executores, unidades, heridas y reanimación, estructuras, eventos y otros sistemas del servidor."
                    : "Executors, units, injuries and revival, structures, events and other server systems.";
            case ITEMS -> spanish
                    ? "Objetos, reliquias, Geography Table, Daemonium Kit, Assembling y otras herramientas."
                    : "Items, relics, Geography Table, Daemonium Kit, Assembling and other tools.";
        };
    }
}
