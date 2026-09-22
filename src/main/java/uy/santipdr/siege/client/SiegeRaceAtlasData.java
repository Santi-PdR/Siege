package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Structured, non-personal race catalog for the SIEGE 5.00 Atlas. */
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
        UNKNOWN("DESCONOCIDA", "UNKNOWN", 0xFF7D858A);

        private final String es, en;
        private final int accent;
        Rarity(String es, String en, int accent) { this.es = es; this.en = en; this.accent = accent; }
        public String label(boolean spanish) { return spanish ? es : en; }
        public int accent() { return accent; }
    }

    public enum Progression {
        VERSIONED("V1 → V4", "V1 → V4"),
        TRANSFORMATIONS("TRANSFORMACIONES", "TRANSFORMATIONS"),
        SPECIAL("PROGRESIÓN PROPIA", "OWN PROGRESSION"),
        STEPS_TRIALS("PASOS / TRIALS", "STEPS / TRIALS"),
        ASSEMBLING("ASSEMBLING", "ASSEMBLING"),
        UNKNOWN("DESCONOCIDA", "UNKNOWN");
        private final String es, en;
        Progression(String es, String en) { this.es = es; this.en = en; }
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    public record Race(String id, String name, Rarity rarity, Progression progression,
                       String summaryEs, String summaryEn, String knowledgeId,
                       boolean mayHaveChanged, List<String> tags) {
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
                    "Usa V1→V4 y suele ser una de las rutas más rápidas de llevar hasta V4.",
                    "Uses V1→V4 and is usually one of the faster routes to reach V4.",
                    "race-human", false, "v4", "starter", "trials"),
            r("hacker", "Hacker", Rarity.UNKNOWN, Progression.VERSIONED,
                    "Raza tecnológica relacionada con energía, Room, Gate, sabotaje y entrenamiento dirigido.",
                    "Technology-focused race tied to energy, Room, Gate, sabotage and focused training.",
                    "race-hacker", false, "room", "gate", "energy", "tech"),
            r("shark", "Shark", Rarity.UNKNOWN, Progression.VERSIONED,
                    "Tiene ventajas acuáticas. Parte de sus pasos de progresión cambió con el tiempo, así que conviene revisar la ficha antes de intentar V2.",
                    "Has aquatic advantages. Some progression steps changed over time, so check its entry before attempting V2.",
                    "race-shark", true, "water", "v2"),
            r("saiyan", "Saiyan", Rarity.OBSAINAN, Progression.TRANSFORMATIONS,
                    "Crece con entrenamiento y transformaciones. Un Saiyan recién obtenido y uno muy entrenado pueden ser muy distintos.",
                    "Grows through training and transformations. A newly obtained Saiyan and a heavily trained one can be very different.",
                    "race-saiyan", false, "transformations", "training", "dojo"),
            r("deteriorer", "Deteriorer", Rarity.OBSAINAN, Progression.SPECIAL,
                    "Gira alrededor del deterioro y la oxidación progresiva, debilitando capacidades con el tiempo.",
                    "Built around progressive deterioration and oxidation, weakening capabilities over time.",
                    "race-deteriorer", false, "oxidation", "wear", "debuff"),
            r("pharaoh", "Faraón", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Está relacionada con una zona o dimensión desértica propia. Su disponibilidad no siempre fue la misma.",
                    "Linked to its own desert area or dimension. Its availability has not always been the same.",
                    "race-pharaoh", true, "dimension", "desert"),
            r("apotheosis", "Apotheosis", Rarity.ETERNAL, Progression.SPECIAL,
                    "Raza Eternal relacionada con la fe. Todavía faltan partes claras de sus costes, límites y progreso.",
                    "Eternal-rarity race tied to faith. Parts of its costs, limits and progression are still unclear.",
                    "race-apotheosis", false, "faith", "eternal"),
            r("death", "Muerte", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Está relacionada con absorber almas de bosses. No hay que confundirla con los estados de muerte del jugador.",
                    "Related to absorbing boss souls. It should not be confused with player death states.",
                    "race-death", false, "souls", "boss"),
            r("cyborg", "Cyborg", Rarity.UNKNOWN, Progression.ASSEMBLING,
                    "Su progreso se relaciona con implantes, trasplantes, chips y Assembling.",
                    "Its progression is tied to implants, transplants, chips and Assembling.",
                    "race-cyborg", false, "assembling", "implants", "chips"),
            r("ghoul", "Ghoul", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Puede fortalecerse comiendo carne y además tiene evoluciones separadas como V2 y Super Ghoul.",
                    "Can grow stronger by eating meat and also has separate evolutions such as V2 and Super Ghoul.",
                    "race-ghoul", false, "meat", "v2", "super-ghoul"),
            r("subhuman", "Subhuman", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Es una familia de humanos mutados. Variantes conocidas incluyen Adamantium Human, Sorcerer y Evil Morty.",
                    "A family of mutated humans. Known variants include Adamantium Human, Sorcerer and Evil Morty.",
                    "race-subhuman", false, "human", "variants", "adamantium", "sorcerer", "evil-morty"),
            r("terrarian", "Terrariano", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Puede ser útil contra bosses; todavía no está clara su progresión completa.",
                    "Can be useful against bosses; its full progression is still unclear.",
                    "race-terrarian", false, "boss"),
            r("kaioshin", "Kaioshin", Rarity.UNKNOWN, Progression.VERSIONED,
                    "Usa una progresión por versiones. Algunos pasos antiguos estuvieron relacionados con Solaris, pero no se toman como regla actual sin confirmar.",
                    "Uses versioned progression. Some older steps involved Solaris, but they are not treated as current rules without confirmation.",
                    "race-kaioshin", true, "v2", "solaris"),
            r("dragon", "Dragon", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Tiene varias variantes conocidas. Una está relacionada con Blox Fruits y todavía faltan datos de otras rutas.",
                    "Has several known variants. One is tied to Blox Fruits and other routes still lack details.",
                    "race-dragon", false, "variants"),
            r("shinigami", "Shinigami", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "La raza existe, pero todavía falta información suficiente para explicar bien sus habilidades y progresión.",
                    "The race exists, but there is not enough information yet to explain its abilities and progression well.",
                    "race-shinigami", false, "confirmed"),
            r("majin", "Majin", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "La raza existe, pero sus requisitos, habilidades y etapas todavía no están completos.",
                    "The race exists, but its requirements, abilities and stages are still incomplete.",
                    "race-majin", false, "confirmed"),
            r("undertale-au", "Undertale AU", Rarity.HIDDEN, Progression.STEPS_TRIALS,
                    "Familia de razas ocultas que puede depender de Trials y pasos difíciles. Las rutas conocidas todavía están incompletas.",
                    "Family of hidden races that can depend on Trials and difficult steps. Known routes are still incomplete.",
                    "race-undertale-au", true, "hidden", "trial", "steps")
    );

    private static Race r(String id, String name, Rarity rarity, Progression progression,
                          String es, String en, String knowledgeId, boolean mayHaveChanged, String... tags) {
        return new Race(id, name, rarity, progression, es, en, knowledgeId, mayHaveChanged, List.of(tags));
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
    public static long historicalCount() { return RACES.stream().filter(Race::mayHaveChanged).count(); }

    static String normalize(String value) {
        if (value == null) return "";
        String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return decomposed.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]+", " ").trim();
    }
    private static String safe(String value) { return value == null ? "" : value; }
}
