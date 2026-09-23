package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Current, non-personal race catalog used by the SIEGE 5.10 Race Atlas. */
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
            known("human", "Human", Rarity.UNKNOWN, Progression.VERSIONED,
                    "Usa la progresión V1→V4 y es una de las rutas más rápidas para llegar a V4.",
                    "Uses V1→V4 and is one of the faster routes to V4.", "race-human", "v1", "v2", "v3", "v4", "blox-fruits"),
            known("hacker", "Hacker", Rarity.UNKNOWN, Progression.VERSIONED,
                    "Raza ligada a energía, Room, Gate, sabotaje y una progresión propia por versiones.",
                    "Race tied to energy, Room, Gate, sabotage and its own versioned progression.", "race-hacker", "room", "gate", "v1", "v2", "v3", "v4"),
            known("subhuman", "Subhuman", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Familia de Human alterados. Incluye Adamantium Human, Sorcerer, Evil Morty y Rick Sanchez.",
                    "Family of altered Humans. Includes Adamantium Human, Sorcerer, Evil Morty and Rick Sanchez.", "race-subhuman", "variants", "adamantium", "sorcerer", "morty", "rick"),
            known("mink", "Mink", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Variante inspirada en Mink de One Piece; destaca principalmente por movilidad y velocidad para recorrer terreno.",
                    "Variant inspired by One Piece Mink; mainly associated with movement and speed across terrain.", "", "speed", "mobility", "one-piece"),
            unknown("tsufurujin", "Tsufurujin"),
            historical("shark", "Shark", Progression.VERSIONED,
                    "Inspirada en Shark de Blox Fruits y relacionada con ventajas acuáticas. Algunos pasos cambiaron con el tiempo.",
                    "Inspired by Blox Fruits Shark and tied to aquatic advantages. Some steps changed over time.", "race-shark", "water", "v2", "blox-fruits"),
            known("angel", "Angel", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "La Angel de SIEGE está basada en la raza Angel de Blox Fruits; no se trata como un ángel genérico del lore.",
                    "SIEGE Angel is based on the Angel race from Blox Fruits; it is not treated as a generic lore angel.", "", "blox-fruits"),
            known("ghoul", "Ghoul", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Puede mejorar mediante su propia progresión y tiene evoluciones conocidas, incluida Super Ghoul.",
                    "Uses its own progression and has known evolutions, including Super Ghoul.", "race-ghoul", "meat", "v2", "super-ghoul"),
            known("cyborg", "Cyborg", Rarity.UNKNOWN, Progression.ASSEMBLING,
                    "Su progreso se relaciona con implantes, chips, trasplantes y Assembling.",
                    "Its progression is tied to implants, chips, transplants and Assembling.", "race-cyborg", "assembling", "implants", "chips"),
            known("deteriorer", "Deteriorer", Rarity.OBSAINAN, Progression.SPECIAL,
                    "Se centra en deterioro y oxidación progresiva. Sus habilidades y energía se explican en su propia ficha.",
                    "Built around progressive deterioration and oxidation. Its abilities and energy are explained in its own entry.", "race-deteriorer", "oxidation", "rust", "re"),
            unknown("involver", "Involver"),
            known("majin", "Majin", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "La raza existe dentro del catálogo, pero sus requisitos, habilidades y etapas todavía están incompletos.",
                    "The race exists in the catalog, but its requirements, abilities and stages are still incomplete.", "race-majin", "needs-info"),
            unknown("dark-manor", "Dark Manor"),
            unknown("hermes", "Hermes"),
            known("saiyan", "Saiyan", Rarity.OBSAINAN, Progression.TRANSFORMATIONS,
                    "Progresa sobre todo mediante entrenamiento, zenkai y transformaciones; no se resume como una sola ruta V1→V4.",
                    "Progresses mainly through training, zenkai and transformations; it is not one simple V1→V4 route.", "race-saiyan", "transformations", "training", "zenkai", "flame-saiyan"),
            known("xeno-saiyan", "Xeno Saiyan", Rarity.UNKNOWN, Progression.TRANSFORMATIONS,
                    "Rama Saiyan diferenciada. Se mantiene separada para no copiarle automáticamente requisitos o transformaciones de Saiyan normal.",
                    "Distinct Saiyan branch. It stays separate so normal Saiyan requirements and transformations are not automatically copied onto it.", "race-xeno-saiyan", "saiyan", "transformations", "variant"),
            unknown("otsutsuki", "Otsutsuki"),
            unknown("ackerman", "Ackerman"),
            unknown("titan", "Titan"),
            unknown("exceed", "Exceed"),
            unknown("cold-demon", "Cold Demon"),
            historical("kaioshin", "Kaioshin", Progression.VERSIONED,
                    "Usa progresión por versiones, pero algunos requisitos antiguos cambiaron y no se muestran como actuales sin confirmar.",
                    "Uses versioned progression, but some old requirements changed and are not shown as current without confirmation.", "race-kaioshin", "v2", "v3", "v4"),
            known("dragon", "Dragon", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Tiene varias variantes conocidas. Se separan cuando hay información suficiente.",
                    "Has several known variants. They are separated when enough information exists.", "race-dragon", "variants"),
            unknown("gas", "Gas"),
            known("death", "Muerte", Rarity.UNKNOWN, Progression.SPECIAL,
                    "Raza vinculada con almas de bosses. No debe confundirse con los estados de muerte del jugador.",
                    "Race tied to boss souls. It should not be confused with player death states.", "race-death", "souls", "boss"),
            known("shinigami", "Shinigami", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Existe como raza conocida, pero todavía falta información suficiente para explicar bien su ruta actual.",
                    "Known race, but there is not enough information yet to explain its current path well.", "race-shinigami", "needs-info"),
            unknown("fullbringer", "Fullbringer"),
            unknown("arrancar", "Arrancar"),
            unknown("hakaishin", "Hakaishin"),
            unknown("zeno", "Zeno"),
            unknown("quincy", "Quincy"),
            unknown("divine", "Divine"),
            unknown("namekian", "Namekian"),
            unknown("angel-guia", "Angel Guía"),
            unknown("aryano", "Aryano"),
            unknown("narehate", "Narehate"),
            unknown("arclighter", "Arclighter"),
            historical("pharaoh", "Faraón", Progression.UNKNOWN,
                    "Está relacionada con una zona o dimensión desértica. No se muestran requisitos que no estén claros.",
                    "Linked to a desert area or dimension. Unclear requirements are not shown.", "race-pharaoh", "desert", "dimension"),
            unknown("sun", "Sun"),
            unknown("almirante", "Almirante"),
            unknown("archie", "Archie"),
            unknown("emperador", "Emperador"),
            unknown("glitch-core", "Glitch Core"),
            unknown("jiren", "Jiren"),
            unknown("winter-hunter", "Winter Hunter"),
            known("apotheosis", "Apotheosis", Rarity.ETERNAL, Progression.SPECIAL,
                    "Raza Eternal relacionada con la fe. Parte de sus costes y progresión todavía no está clara.",
                    "Eternal-rarity race tied to faith. Parts of its costs and progression are still unclear.", "race-apotheosis", "faith", "eternal"),
            unknown("lunarian", "Lunarian"),
            unknown("virtud", "Virtud"),
            historical("super-ghoul", "Super Ghoul", Progression.SPECIAL,
                    "Evolución o variante de Ghoul. Su obtención exacta no se fija mientras la información vigente no esté clara.",
                    "Ghoul evolution or variant. Its exact acquisition is not fixed while current information remains unclear.", "ghoul-progression", "ghoul", "variant"),
            known("terrarian", "Terrariano", Rarity.UNKNOWN, Progression.UNKNOWN,
                    "Existe como raza conocida, pero todavía falta una explicación completa y actual de su progresión.",
                    "Known race, but a complete current explanation of its progression is still missing.", "race-terrarian", "needs-info"),
            unknown("fenix", "Fénix"),
            unknown("diclonius", "Diclonius"),
            unknown("windwhirl", "Windwhirl"),
            unknown("void-master", "Void Master"),
            unknown("sobrino", "SOBRINO"),
            unknown("oni", "Oni"),
            unknown("iluminati", "Iluminati"),
            known("undertale-au", "Undertale AU", Rarity.HIDDEN, Progression.STEPS_TRIALS,
                    "Familia oculta relacionada con Trials y pasos difíciles. Las rutas conocidas siguen incompletas.",
                    "Hidden family tied to Trials and difficult steps. Known routes remain incomplete.", "race-undertale-au", "hidden", "trial")
    );

    private static Race known(String id, String name, Rarity rarity, Progression progression,
                              String es, String en, String knowledgeId, String... tags) {
        return new Race(id, name, rarity, progression, es, en, knowledgeId, false, List.of(tags));
    }

    private static Race historical(String id, String name, Progression progression,
                                   String es, String en, String knowledgeId, String... tags) {
        return new Race(id, name, Rarity.UNKNOWN, progression, es, en, knowledgeId, true, List.of(tags));
    }

    private static Race unknown(String id, String name) {
        return new Race(id, name, Rarity.UNKNOWN, Progression.UNKNOWN,
                "Aparece en el catálogo de razas de SIEGE, pero todavía no hay información suficiente para explicar sus habilidades o progresión sin inventar.",
                "Appears in the SIEGE race catalog, but there is not enough information yet to explain its abilities or progression without guessing.",
                "", true, List.of("needs-info"));
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
