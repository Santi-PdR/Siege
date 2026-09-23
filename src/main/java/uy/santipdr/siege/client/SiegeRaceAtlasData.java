package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Current, non-personal race catalog for the SIEGE 5.00 Atlas.
 *
 * Names come from the full Discord export and later player/staff confirmations.
 * Unknown data stays unknown: the Atlas must never fill a race with another
 * race's rules just to make the card look complete.
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
                    "Usa la progresión V1→V4. La ficha individual debe mostrar sólo los pasos confirmados para Human.",
                    "Uses V1→V4. Its entry should only show confirmed Human-specific steps.",
                    "race-human", false, "v1", "v2", "v3", "v4"),
            r("subhuman", "Subhuman", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Subhuman es una familia de humanos mutados. Las variantes conocidas se consultan por separado.",
                    "Subhuman is a family of mutated humans. Known variants are browsed separately.",
                    "race-subhuman", false, "variants", "adamantium", "sorcerer", "morty", "rick"),
            r("mink", "Mink", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Mink aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Mink appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("tsufurujin", "Tsufurujin", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Tsufurujin aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Tsufurujin appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("shark", "Shark", Rarity.UNKNOWN, Progression.VERSIONED,
                    "Tiene ventajas relacionadas con el agua. Algunos pasos cambiaron con el tiempo, así que la ficha evita recetas antiguas.",
                    "Has water-related advantages. Some steps changed over time, so its entry avoids old recipes.",
                    "race-shark", true, "water", "v2"),
            r("angel", "Angel", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Angel aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Angel appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("ghoul", "Ghoul", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Puede mejorar comiendo carne y además tiene evoluciones propias. El chat reciente confirma V2 y la existencia de Super Ghoul.",
                    "Can improve by eating meat and also has its own evolutions. Recent records confirm V2 and the existence of Super Ghoul.",
                    "race-ghoul", false, "meat", "v2", "super-ghoul"),
            r("cyborg", "Cyborg", Rarity.UNKNOWN, Progression.ASSEMBLING,
                    "Su progreso se relaciona con implantes, chips, trasplantes y Assembling.",
                    "Its progression is tied to implants, chips, transplants and Assembling.",
                    "race-cyborg", false, "assembling", "implants", "chips"),
            r("deteriorer", "Deteriorer", Rarity.OBSAINAN, Progression.SPECIAL,
                    "Se centra en deterioro y oxidación progresiva. Sus habilidades y consumo de energía se explican en su propia ficha.",
                    "Built around progressive deterioration and oxidation. Its abilities and energy use belong in its own entry.",
                    "race-deteriorer", false, "oxidation", "rust", "re"),
            r("involver", "Involver", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Involver aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Involver appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("majin", "Majin", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "La raza está confirmada, pero sus requisitos, habilidades y etapas todavía no están completos.",
                    "The race is confirmed, but its requirements, abilities and stages are still incomplete.",
                    "race-majin", false, "confirmed"),
            r("dark-manor", "Dark Manor", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Dark Manor aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Dark Manor appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("hermes", "Hermes", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Hermes aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Hermes appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("saiyan", "Saiyan", Rarity.OBSAINAN, Progression.TRANSFORMATIONS,
                    "Progresa sobre todo mediante entrenamiento, zenkai y transformaciones; no se resume bien como una sola ruta V1→V4.",
                    "Progresses mainly through training, zenkai and transformations; it is not well described as one V1→V4 route.",
                    "race-saiyan", false, "transformations", "training", "zenkai", "xeno-saiyan"),
            r("otsutsuki", "Otsutsuki", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Otsutsuki aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Otsutsuki appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("ackerman", "Ackerman", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Ackerman aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Ackerman appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("titan", "Titan", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Titan aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Titan appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("exceed", "Exceed", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Exceed aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Exceed appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("cold-demon", "Cold Demon", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Cold Demon aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Cold Demon appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("kaioshin", "Kaioshin", Rarity.UNKNOWN, Progression.VERSIONED,
                    "Usa progresión por versiones, pero algunos requisitos antiguos cambiaron y no se muestran como actuales sin confirmar.",
                    "Uses versioned progression, but some old requirements changed and are not shown as current without confirmation.",
                    "race-kaioshin", true, "v2", "v3", "v4"),
            r("dragon", "Dragon", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Tiene varias variantes conocidas. La ficha separa variantes cuando hay información suficiente.",
                    "Has several known variants. Its entry separates variants when enough information exists.",
                    "race-dragon", false, "variants"),
            r("gas", "Gas", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Gas aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Gas appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("death", "Muerte", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Raza vinculada con almas de bosses. No debe confundirse con los estados de muerte del jugador.",
                    "Race tied to boss souls. It should not be confused with player death states.",
                    "race-death", false, "souls", "boss"),
            r("shinigami", "Shinigami", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "La raza está confirmada en el catálogo, pero todavía no hay información suficiente para explicar bien su ruta actual.",
                    "The race is confirmed in the catalog, but there is not enough information yet to explain its current path well.",
                    "race-shinigami", false, "confirmed"),
            r("fullbringer", "Fullbringer", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Fullbringer aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Fullbringer appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("arrancar", "Arrancar", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Arrancar aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Arrancar appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("hakaishin", "Hakaishin", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Hakaishin aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Hakaishin appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("zeno", "Zeno", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Zeno aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Zeno appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("quincy", "Quincy", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Quincy aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Quincy appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("divine", "Divine", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Divine aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Divine appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("namekian", "Namekian", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Namekian aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Namekian appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("angel-guia", "Angel Guía", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Angel Guía aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Angel Guía appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("aryano", "Aryano", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Aryano aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Aryano appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("narehate", "Narehate", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Narehate aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Narehate appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("arclighter", "Arclighter", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Arclighter aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Arclighter appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("pharaoh", "Faraón", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Está relacionada con una zona o dimensión desértica. No se muestran requisitos que no estén confirmados.",
                    "Linked to a desert area or dimension. Unconfirmed requirements are not shown.",
                    "race-pharaoh", true, "desert", "dimension"),
            r("sun", "Sun", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Sun aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Sun appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("almirante", "Almirante", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Almirante aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Almirante appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("archie", "Archie", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Archie aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Archie appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("emperador", "Emperador", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Emperador aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Emperador appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("glitch-core", "Glitch Core", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Glitch Core aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Glitch Core appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("jiren", "Jiren", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Jiren aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Jiren appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("winter-hunter", "Winter Hunter", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Winter Hunter aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Winter Hunter appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("apotheosis", "Apotheosis", Rarity.ETERNAL, Progression.SPECIAL,
                    "Raza Eternal relacionada con la fe. Hay partes de sus costes y progresión que todavía no están claras.",
                    "Eternal-rarity race tied to faith. Parts of its costs and progression are still unclear.",
                    "race-apotheosis", false, "faith", "eternal"),
            r("lunarian", "Lunarian", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Lunarian aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Lunarian appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("virtud", "Virtud", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Virtud aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Virtud appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("super-ghoul", "Super Ghoul", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Evolución o variante de Ghoul confirmada en el chat. Su obtención exacta no se fija si la información vigente no está clara.",
                    "Confirmed Ghoul evolution or variant. Its exact acquisition is not fixed when current information is unclear.",
                    "ghoul-progression", true, "ghoul", "variant"),
            r("terrarian", "Terrariano", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Existe como raza conocida, pero todavía falta una explicación completa y actual de su progresión.",
                    "Known race, but a complete current explanation of its progression is still missing.",
                    "race-terrarian", false, "confirmed"),
            r("fenix", "Fénix", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Fénix aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Fénix appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("diclonius", "Diclonius", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Diclonius aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Diclonius appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("windwhirl", "Windwhirl", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Windwhirl aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Windwhirl appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("void-master", "Void Master", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Void Master aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Void Master appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("sobrino", "SOBRINO", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "SOBRINO aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "SOBRINO appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("oni", "Oni", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Oni aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Oni appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("iluminati", "Iluminati", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Iluminati aparece en el catálogo de razas conocido. Todavía no hay información reciente suficiente para explicar sus habilidades o progresión sin inventar.",
                    "Iluminati appears in the known race catalog. There is not enough recent information yet to explain its abilities or progression without guessing.",
                    "", true, "confirmed", "needs-info"),
            r("undertale-au", "Undertale AU", Rarity.HIDDEN, Progression.STEPS_TRIALS,
                    "Familia oculta relacionada con Trials y pasos difíciles. Las rutas conocidas siguen incompletas.",
                    "Hidden family tied to Trials and difficult steps. Known routes are still incomplete.",
                    "race-undertale-au", true, "hidden", "trial")
    );

    private static Race r(String id, String name, Rarity rarity, Progression progression,
                          String es, String en, String knowledgeId, boolean mayHaveChanged, String... tags) {
        return new Race(id, name, rarity, progression, es, en, knowledgeId, mayHaveChanged, List.of(tags));
    }

    public static List<Race> all() { return RACES; }

    public static List<Race> search(String query, int limit) {
        int safeLimit = Math.max(1, Math.min(128, limit));
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
