package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Player-facing category map for the SIEGE server guide.
 *
 * This file deliberately contains no personal player state and no source/audit
 * vocabulary. It only decides which existing server-wide encyclopedia entries
 * should be grouped together for fast reading.
 */
public final class SiegeServerGuideData {
    public enum Category {
        START("EMPEZAR", "START HERE", "Lo esencial para entender el servidor antes de arriesgar recursos.",
                "The essentials for understanding the server before risking resources."),
        RACES("RAZAS", "RACES", "Razas conocidas, rarezas, slots y diferencias de progresión.",
                "Known races, rarities, slots and progression differences."),
        PROGRESSION("PROGRESIÓN", "PROGRESSION", "V1→V4, Trials, rutas especiales, meditación y exploración.",
                "V1→V4, Trials, special routes, meditation and exploration."),
        THREATS("AMENAZAS", "THREATS", "Executores, bosses, raids, estructuras peligrosas y adaptación enemiga.",
                "Executors, bosses, raids, dangerous structures and enemy adaptation."),
        SYSTEMS("SISTEMAS", "SYSTEMS", "Reliquias, objetos, Assembling, habilidades, dimensiones y economía.",
                "Relics, items, Assembling, abilities, dimensions and economy."),
        SURVIVAL("SUPERVIVENCIA", "SURVIVAL", "Revive, muerte, preparación, movilidad y errores que conviene evitar.",
                "Revival, death, preparation, mobility and mistakes worth avoiding."),
        HISTORY("HISTÓRICO", "HISTORY", "Mecánicas antiguas o que pueden haber cambiado; no asumirlas como actuales.",
                "Older mechanics or rules that may have changed; do not assume they are current.");

        private final String es, en, descEs, descEn;
        Category(String es, String en, String descEs, String descEn) {
            this.es = es; this.en = en; this.descEs = descEs; this.descEn = descEn;
        }
        public String label(boolean spanish) { return spanish ? es : en; }
        public String description(boolean spanish) { return spanish ? descEs : descEn; }
    }

    private SiegeServerGuideData() { }

    public static List<SiegeKnowledgeData.Entry> entries(Category category) {
        if (category == null) return List.of();
        Set<String> ids = switch (category) {
            case START -> ordered(
                    "server-overview", "newcomer-operational-rule", "server-exploration",
                    "rarity-order", "progression-v1-v4", "trials-basics", "executors-basics",
                    "structures-basics", "bosses-basics", "respawn-cards", "relic-basics",
                    "dimensions-basics", "economy-basics", "prompt-precision-framework");
            case RACES -> ordered(
                    "rarity-order", "race-catalog", "race-slots", "fabled-acquisition",
                    "race-human", "race-hacker", "race-shark", "race-saiyan", "race-deteriorer",
                    "race-pharaoh", "race-apotheosis", "race-death", "race-cyborg", "race-ghoul",
                    "race-subhuman", "race-terrarian", "race-kaioshin", "race-dragon",
                    "race-shinigami", "race-majin", "race-undertale-au");
            case PROGRESSION -> ordered(
                    "progression-v1-v4", "fabled-acquisition", "progression-mobility-priority",
                    "server-exploration", "trials-basics", "trial-meditation", "trial-spire",
                    "meditation-levels", "assembling-planning", "race-human", "race-saiyan");
            case THREATS -> ordered(
                    "executors-basics", "executors-history", "bosses-basics", "combat-adaptation",
                    "raids-basics", "raid-area-discipline", "factions-basics", "structures-basics",
                    "missions-npcs");
            case SYSTEMS -> ordered(
                    "relic-basics", "relic-analysis-workflow", "relic-third-justice",
                    "item-geography-table", "item-daemonium-kit", "item-improbability-scroll",
                    "assembling-table", "assembling-planning", "ability-room", "ability-gate",
                    "dimensions-basics", "economy-basics", "prompt-design", "prompt-precision-framework");
            case SURVIVAL -> ordered(
                    "newcomer-operational-rule", "progression-mobility-priority", "server-exploration",
                    "respawn-cards", "death-revive-history", "death-revive-contradictions",
                    "revive-repeat-penalties", "bosses-basics", "raid-area-discipline",
                    "structures-basics", "prompt-precision-framework");
            case HISTORY -> ordered(
                    "executors-history", "race-deteriorer-old-debuff", "race-shark", "race-pharaoh",
                    "race-kaioshin", "race-undertale-au", "relic-third-justice", "death-revive-history",
                    "death-revive-contradictions", "revive-repeat-penalties",
                    "deteriorer-re-overflow-history", "meditation-levels");
        };

        List<SiegeKnowledgeData.Entry> out = new ArrayList<>();
        for (String id : ids) {
            SiegeKnowledgeData.Entry entry = SiegeKnowledgeRegistry.get(id);
            if (entry != null) out.add(entry);
        }
        return List.copyOf(out);
    }

    public static int accent(Category category) {
        if (category == null) return 0xFF68C6D8;
        return switch (category) {
            case START -> 0xFF72C98B;
            case RACES, PROGRESSION -> 0xFFD6AE65;
            case THREATS -> 0xFFE54852;
            case SYSTEMS -> 0xFF68C6D8;
            case SURVIVAL, HISTORY -> 0xFFE89B59;
        };
    }

    private static Set<String> ordered(String... ids) {
        LinkedHashSet<String> out = new LinkedHashSet<>();
        if (ids != null) for (String id : ids) if (id != null && !id.isBlank()) out.add(id);
        return out;
    }
}
