package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Server-wide race atlas for SIEGE 4.0.
 *
 * The atlas intentionally stores only general Eternal Craft information. Unknown
 * rarity/progression fields stay UNKNOWN instead of being inferred from a player,
 * a one-off anecdote or an unrelated game.
 */
public final class SiegeRaceAtlasData {
    public enum Rarity {
        COMMON("COMÚN", "COMMON", 0xFFA7B0B6),
        UNCOMMON("POCO COMÚN", "UNCOMMON", 0xFF72C98B),
        RARE("RARO", "RARE", 0xFF68C6D8),
        ULTRA_RARE("ULTRA RARO", "ULTRA RARE", 0xFF789BFF),
        LEGENDARY("LEGENDARIO", "LEGENDARY", 0xFFD6AE65),
        OBSAINAN("OBSAINAN", "OBSAINAN", 0xFFE89B59),
        MYTHIC("MÍTICO", "MYTHIC", 0xFFD979D7),
        GODLY("GODLY", "GODLY", 0xFFFF6B6B),
        ETERNAL("ETERNAL", "ETERNAL", 0xFFF0D66B),
        FABLED("FABLED", "FABLED", 0xFFFFFFFF),
        HIDDEN("OCULTA", "HIDDEN", 0xFF9AA4AB),
        UNKNOWN("SIN CONFIRMAR", "UNCONFIRMED", 0xFF7D858A);

        private final String es, en;
        private final int accent;
        Rarity(String es, String en, int accent) { this.es = es; this.en = en; this.accent = accent; }
        public String label(boolean spanish) { return spanish ? es : en; }
        public int accent() { return accent; }
    }

    public enum Progression {
        VERSIONED("V1 → V4", "V1 → V4"),
        TRANSFORMATIONS("TRANSFORMACIONES", "TRANSFORMATIONS"),
        STEPS_TRIALS("PASOS / TRIALS", "STEPS / TRIALS"),
        ASSEMBLING("ASSEMBLING", "ASSEMBLING"),
        UNKNOWN("POR RECONSTRUIR", "TO RECONSTRUCT");

        private final String es, en;
        Progression(String es, String en) { this.es = es; this.en = en; }
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    public record Race(String id, String name, Rarity rarity, Progression progression,
                       String summaryEs, String summaryEn, String knowledgeId,
                       boolean historical, List<String> tags) {
        public Race {
            id = safe(id); name = safe(name);
            rarity = rarity == null ? Rarity.UNKNOWN : rarity;
            progression = progression == null ? Progression.UNKNOWN : progression;
            summaryEs = safe(summaryEs); summaryEn = safe(summaryEn); knowledgeId = safe(knowledgeId);
            tags = tags == null ? List.of() : List.copyOf(tags);
        }
        public String summary(boolean spanish) { return spanish ? summaryEs : summaryEn; }
    }

    private SiegeRaceAtlasData() { }

    private static final List<Race> RACES = List.of(
            r("human", "Human", Rarity.UNKNOWN, Progression.VERSIONED,
                    "Ruta relativamente rápida hacia V4; varios Trials/artefactos usan V4 como requisito inicial.",
                    "Relatively fast route toward V4; several Trials/artifacts use V4 as an initial requirement.",
                    "race-human", false, "v4", "starter", "trials"),
            r("hacker", "Hacker", Rarity.UNKNOWN, Progression.VERSIONED,
                    "Raza tecnológica centrada en energía, Room, Gate, sabotaje y entrenamiento dirigido.",
                    "Technology-focused race centered on energy, Room, Gate, sabotage and focused training.",
                    "race-hacker", false, "room", "gate", "energy", "tech"),
            r("shark", "Shark", Rarity.UNKNOWN, Progression.VERSIONED,
                    "Raza con ventajas acuáticas documentadas; parte de su progresión V2 está conservada sólo como histórica.",
                    "Race with documented aquatic advantages; part of its V2 progression is preserved only as historical.",
                    "race-shark", true, "water", "v2"),
            r("saiyan", "Saiyan", Rarity.OBSAINAN, Progression.TRANSFORMATIONS,
                    "Progresa mediante transformaciones y stats, no con el mismo esquema V2/V3/V4 de otras razas.",
                    "Progresses through transformations and stats rather than the same V2/V3/V4 scheme as other races.",
                    "race-saiyan", false, "transformations", "teleport", "dojo"),
            r("deteriorer", "Deteriorer", Rarity.OBSAINAN, Progression.UNKNOWN,
                    "Raza basada en deterioro/oxidación progresiva y degradación de capacidades.",
                    "Race based on progressive deterioration/oxidation and capability degradation.",
                    "race-deteriorer", false, "oxidation", "wear", "debuff"),
            r("pharaoh", "Faraón", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Documentada con una dimensión desértica propia; disponibilidad cambió entre etapas del servidor.",
                    "Documented with its own desert dimension; availability changed across server eras.",
                    "race-pharaoh", true, "dimension", "desert"),
            r("apotheosis", "Apotheosis", Rarity.ETERNAL, Progression.UNKNOWN,
                    "Raza Eternal vinculada a la fe; reglas, costes y límites completos siguen incompletos.",
                    "Eternal-rarity race linked to faith; complete rules, costs and limits remain incomplete.",
                    "race-apotheosis", false, "faith", "eternal"),
            r("death", "Muerte", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Raza relacionada con absorber almas de bosses; no confundir con estados/eventos llamados Muerte.",
                    "Race related to absorbing boss souls; do not confuse it with states/events also named Death.",
                    "race-death", false, "souls", "boss"),
            r("cyborg", "Cyborg", Rarity.UNKNOWN, Progression.ASSEMBLING,
                    "Se relaciona con implantes, trasplantes y chips mediante Assembling.",
                    "Linked to implants, transplants and chips through Assembling.",
                    "race-cyborg", false, "assembling", "implants", "chips"),
            r("ghoul", "Ghoul", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Raza confirmada; el nombre también aparece en enemigos, por lo que el contexto importa.",
                    "Confirmed race; the name also appears for enemies, so context matters.",
                    "race-ghoul", false, "ghoul"),
            r("subhuman", "Subhuman", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Familia de variantes Human mutadas, con menciones como Adamantium Human y Sorcerer.",
                    "Family of mutated Human variants, with mentions such as Adamantium Human and Sorcerer.",
                    "race-subhuman", false, "human", "variants"),
            r("terrarian", "Terrariano", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Raza recomendada para enfrentamientos contra bosses; la progresión completa no está reconstruida.",
                    "Race recommended for boss encounters; full progression is not reconstructed.",
                    "race-terrarian", false, "boss"),
            r("kaioshin", "Kaioshin", Rarity.UNKNOWN, Progression.VERSIONED,
                    "Raza documentada; un caso histórico V2 estuvo ligado a Solaris sin convertirse en requisito universal.",
                    "Documented race; one historical V2 case was linked to Solaris without becoming a universal requirement.",
                    "race-kaioshin", true, "v2", "solaris"),
            r("dragon", "Dragon", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Se documentaron tres variantes; una relacionada con Blox Fruits y dos todavía sin reconstruir.",
                    "Three variants were documented; one related to Blox Fruits and two not yet reconstructed.",
                    "race-dragon", false, "variants"),
            r("shinigami", "Shinigami", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Existencia confirmada; habilidades y progresión siguen sin evidencia suficiente para una ficha completa.",
                    "Existence confirmed; abilities and progression still lack enough evidence for a complete profile.",
                    "race-shinigami", false, "confirmed"),
            r("majin", "Majin", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Existencia confirmada; stats, requisitos y habilidades completos permanecen abiertos.",
                    "Existence confirmed; full stats, requirements and abilities remain open.",
                    "race-majin", false, "confirmed"),
            r("undertale-au", "Undertale AU", Rarity.HIDDEN, Progression.STEPS_TRIALS,
                    "Familia de razas ocultas obtenibles mediante Trials y otros pasos difíciles; nombres/rutas no están completos.",
                    "Family of hidden races obtainable through Trials and other difficult steps; names/routes are incomplete.",
                    "race-undertale-au", true, "hidden", "trial", "steps")
    );

    private static Race r(String id, String name, Rarity rarity, Progression progression,
                          String es, String en, String knowledgeId, boolean historical, String... tags) {
        return new Race(id, name, rarity, progression, es, en, knowledgeId, historical, List.of(tags));
    }

    public static List<Race> all() { return RACES; }

    public static List<Race> search(String query, int limit) {
        int safeLimit = Math.max(1, Math.min(64, limit));
        String q = normalize(query);
        if (q.isBlank()) return RACES.stream().limit(safeLimit).toList();
        record Ranked(Race race, int score) { }
        List<Ranked> ranked = new ArrayList<>();
        for (Race race : RACES) {
            String haystack = normalize(race.id() + " " + race.name() + " " + race.rarity().name() + " "
                    + race.progression().name() + " " + race.summaryEs() + " " + race.summaryEn() + " "
                    + String.join(" ", race.tags()));
            int score = race.id().equals(q) || normalize(race.name()).equals(q) ? 120
                    : normalize(race.name()).startsWith(q) ? 100
                    : haystack.contains(q) ? 60 : 0;
            if (score > 0) ranked.add(new Ranked(race, score));
        }
        ranked.sort(Comparator.comparingInt(Ranked::score).reversed().thenComparing(r -> r.race().name()));
        return ranked.stream().limit(safeLimit).map(Ranked::race).toList();
    }

    public static List<Rarity> rarityOrder() {
        return List.of(Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.ULTRA_RARE,
                Rarity.LEGENDARY, Rarity.OBSAINAN, Rarity.MYTHIC, Rarity.GODLY,
                Rarity.ETERNAL, Rarity.FABLED);
    }

    public static long knownRarityCount() {
        return RACES.stream().filter(r -> r.rarity() != Rarity.UNKNOWN && r.rarity() != Rarity.HIDDEN).count();
    }

    public static long historicalCount() { return RACES.stream().filter(Race::historical).count(); }

    static String normalize(String value) {
        if (value == null) return "";
        String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return decomposed.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]+", " ").trim();
    }

    private static String safe(String value) { return value == null ? "" : value; }
}
