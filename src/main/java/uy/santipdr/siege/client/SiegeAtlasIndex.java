package uy.santipdr.siege.client;

import java.util.List;
import java.util.Set;

/** Pure classification layer used by the SIEGE 4.00 Tactical Atlas. */
public final class SiegeAtlasIndex {
    public enum View { BRIEFING, RACES, SYSTEMS, RESEARCH }

    private static final Set<String> BRIEFING_IDS = Set.of(
            "server-overview", "newcomer-operational-rule", "server-exploration",
            "rarity-order", "race-catalog", "progression-v1-v4", "progression-mobility-priority",
            "trials-basics", "executors-basics", "structures-basics", "bosses-basics",
            "dimensions-basics", "respawn-cards", "relic-basics", "relic-analysis-workflow",
            "economy-basics", "prompt-design", "prompt-precision-framework", "source-policy"
    );

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
            case BRIEFING -> BRIEFING_IDS.contains(entry.id());
            case RACES -> entry.domain() == SiegeKnowledgeData.Domain.RACES
                    || entry.id().equals("rarity-order")
                    || entry.id().equals("progression-v1-v4")
                    || entry.id().equals("fabled-acquisition")
                    || entry.id().equals("deteriorer-re-overflow-history");
            case SYSTEMS -> entry.zone() == SiegeKnowledgeData.Zone.SERVER
                    && entry.domain() != SiegeKnowledgeData.Domain.RACES
                    && entry.domain() != SiegeKnowledgeData.Domain.SOURCES
                    && entry.domain() != SiegeKnowledgeData.Domain.CONTRADICTIONS
                    && entry.domain() != SiegeKnowledgeData.Domain.OVERVIEW;
            case RESEARCH -> entry.zone() == SiegeKnowledgeData.Zone.HISTORY
                    || entry.domain() == SiegeKnowledgeData.Domain.SOURCES
                    || entry.domain() == SiegeKnowledgeData.Domain.CONTRADICTIONS
                    || entry.confidence() == SiegeKnowledgeData.Confidence.UNCONFIRMED
                    || entry.confidence() == SiegeKnowledgeData.Confidence.CONTRADICTION
                    || entry.confidence() == SiegeKnowledgeData.Confidence.HISTORICAL;
        };
    }

    public static String label(View view, boolean spanish) {
        return switch (view) {
            case BRIEFING -> spanish ? "EMPEZAR" : "START HERE";
            case RACES -> spanish ? "REGISTRO DE RAZAS" : "RACE REGISTRY";
            case SYSTEMS -> spanish ? "SISTEMAS" : "SYSTEMS";
            case RESEARCH -> spanish ? "INVESTIGACIÓN" : "RESEARCH";
        };
    }

    public static String description(View view, boolean spanish) {
        return switch (view) {
            case BRIEFING -> spanish
                    ? "Lo mínimo que conviene entender antes de arriesgar recursos: progreso, revive, amenazas, movilidad y límites."
                    : "The minimum to understand before risking resources: progression, revival, threats, mobility and limits.";
            case RACES -> spanish
                    ? "Rarezas, razas documentadas, progresión y diferencias conocidas sin mezclar datos personales."
                    : "Rarities, documented races, progression and known differences without personal data.";
            case SYSTEMS -> spanish
                    ? "Trials, Executores, estructuras, bosses, dimensiones, Assembling, reliquias, economía, prompts y eventos."
                    : "Trials, Executors, structures, bosses, dimensions, Assembling, relics, economy, prompts and events.";
            case RESEARCH -> spanish
                    ? "Histórico, contradicciones, fuentes y preguntas todavía abiertas del archivo."
                    : "History, contradictions, sources and questions still open in the archive.";
        };
    }
}
