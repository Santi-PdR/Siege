package uy.santipdr.siege.client;

import java.util.List;

/** Natural-language progression map for SIEGE 5.00. */
public final class SiegeProgressionData {
    public enum Status {
        CONFIRMED("CONOCIDO", "KNOWN", 0xFF72C98B),
        PARTIAL("INCOMPLETO", "INCOMPLETE", 0xFFD6AE65),
        VARIABLE("DEPENDE DE LA RAZA", "RACE-DEPENDENT", 0xFF68C6D8);
        private final String es, en;
        private final int accent;
        Status(String es, String en, int accent) { this.es = es; this.en = en; this.accent = accent; }
        public String label(boolean spanish) { return spanish ? es : en; }
        public int accent() { return accent; }
    }

    public record Node(String id, String titleEs, String titleEn,
                       String summaryEs, String summaryEn,
                       Status status, String knowledgeId) {
        public String title(boolean spanish) { return spanish ? titleEs : titleEn; }
        public String summary(boolean spanish) { return spanish ? summaryEs : summaryEn; }
    }

    public record Track(String id, String titleEs, String titleEn,
                        String descriptionEs, String descriptionEn,
                        List<Node> nodes) {
        public String title(boolean spanish) { return spanish ? titleEs : titleEn; }
        public String description(boolean spanish) { return spanish ? descriptionEs : descriptionEn; }
    }

    private SiegeProgressionData() { }

    private static Node n(String id, String es, String en, String summaryEs, String summaryEn,
                          Status status, String knowledgeId) {
        return new Node(id, es, en, summaryEs, summaryEn, status, knowledgeId);
    }

    private static final List<Track> TRACKS = List.of(
            new Track("core", "CÓMO PROGRESAR", "HOW TO PROGRESS",
                    "Primero entendé tu raza. Después seguí la ruta que esa raza realmente usa.",
                    "Understand your race first. Then follow the path that race actually uses.",
                    List.of(
                            n("race", "Conocer tu raza", "Know your race",
                                    "Averiguá qué hace al empezar y si tiene V1→V4, transformaciones u otra evolución.",
                                    "Learn what it does at the start and whether it uses V1→V4, transformations or another evolution.", Status.CONFIRMED, "race-system"),
                            n("abilities", "Aprender habilidades", "Learn abilities",
                                    "No todas aparecen de golpe; algunas llegan con versiones, entrenamiento o sistemas aparte.",
                                    "They do not all appear at once; some come from stages, training or separate systems.", Status.CONFIRMED, "abilities-experience"),
                            n("requirements", "Revisar requisitos", "Check requirements",
                                    "Antes de gastar objetos, revisá qué pide exactamente tu siguiente paso.",
                                    "Before spending items, check exactly what your next step requires.", Status.VARIABLE, "progression-v1-v4"),
                            n("trials", "Entrar a Trials", "Enter Trials",
                                    "Cada Trial puede pedir cosas distintas; mirá su ficha antes de entrar.",
                                    "Each Trial can require different things; check its entry before entering.", Status.VARIABLE, "trials-basics")
                    )),
            new Track("v1v4", "V1 → V4", "V1 → V4",
                    "Una forma común de evolución racial. No todas las razas siguen esta ruta.",
                    "A common form of race evolution. Not every race follows this path.",
                    List.of(
                            n("v1", "V1", "V1",
                                    "Es la etapa inicial de una raza que usa este sistema. Suele tener menos efectos y habilidades visibles.",
                                    "The initial stage of a race using this system. It usually has fewer visible effects and abilities.", Status.CONFIRMED, "progression-v1-v4"),
                            n("v2", "V2", "V2",
                                    "Primera evolución importante. Suele volver más notorios los atributos o abrir capacidades nuevas.",
                                    "The first major evolution. It often makes attributes more noticeable or opens new capabilities.", Status.CONFIRMED, "progression-v1-v4"),
                            n("v3", "V3", "V3",
                                    "Una etapa superior cuyo método cambia según la raza. No existe una receta V3 universal.",
                                    "A higher stage whose method changes by race. There is no universal V3 recipe.", Status.VARIABLE, "progression-v1-v4"),
                            n("v4", "V4", "V4",
                                    "Etapa avanzada. Muchos Trials y artefactos pueden pedir V4, pero cada raza llega de forma distinta.",
                                    "An advanced stage. Many Trials and artifacts may require V4, but each race reaches it differently.", Status.VARIABLE, "progression-v1-v4")
                    )),
            new Track("special", "RUTAS DE RAZA", "RACE PATHS",
                    "Algunas razas tienen progresiones importantes además de, o en lugar de, V1→V4.",
                    "Some races have important progression paths in addition to, or instead of, V1→V4.",
                    List.of(
                            n("saiyan", "Saiyan", "Saiyan",
                                    "Crece mediante entrenamiento y transformaciones; no se entiende copiando una ruta V1→V4 genérica.",
                                    "Grows through training and transformations; a generic V1→V4 path does not explain it.", Status.CONFIRMED, "race-saiyan"),
                            n("ghoul", "Ghoul", "Ghoul",
                                    "Comer carne puede fortalecerlo; V2 y Super Ghoul aparecen como evoluciones separadas.",
                                    "Eating meat can strengthen it; V2 and Super Ghoul appear as separate evolutions.", Status.CONFIRMED, "race-ghoul"),
                            n("subhuman", "Subhuman", "Subhuman",
                                    "Es una familia de variantes como Adamantium Human, Sorcerer y Evil Morty, no una sola raza simple.",
                                    "A family of variants such as Adamantium Human, Sorcerer and Evil Morty rather than one simple race.", Status.PARTIAL, "race-subhuman"),
                            n("cyborg", "Cyborg", "Cyborg",
                                    "Implantes, chips y Assembling forman parte de su progreso conocido.",
                                    "Implants, chips and Assembling are part of its known progression.", Status.CONFIRMED, "race-cyborg")
                    )),
            new Track("trials", "TRIALS", "TRIALS",
                    "Los Trials son pruebas distintas entre sí. Abrí cada ficha para no mezclar requisitos.",
                    "Trials are distinct challenges. Open each entry so their requirements do not get mixed together.",
                    List.of(
                            n("spire", "Trial Spire", "Trial Spire",
                                    "Trial conocido; sus requisitos actuales completos todavía no están claros.",
                                    "Known Trial; its full current requirements are not yet clear.", Status.PARTIAL, "trial-spire"),
                            n("meditation", "Meditación", "Meditation",
                                    "Trials relacionados con el progreso de meditación; los números exactos han cambiado.",
                                    "Trials tied to meditation progression; exact numbers have changed over time.", Status.VARIABLE, "trial-meditation"),
                            n("race", "Trials de raza", "Race Trials",
                                    "Pueden formar parte de obtener o mejorar una raza concreta.",
                                    "They can be part of obtaining or improving a specific race.", Status.VARIABLE, "trial-race"),
                            n("weapons", "Trials de armas", "Weapon Trials",
                                    "Pruebas ligadas a armas u objetos concretos, con pasos propios.",
                                    "Challenges tied to specific weapons or items, with their own steps.", Status.VARIABLE, "trial-weapons"),
                            n("witch", "Witch Trials", "Witch Trials",
                                    "Familia de Trials conocida; la ruta exacta sigue incompleta en el registro actual.",
                                    "Known family of Trials; the exact route remains incomplete in current records.", Status.PARTIAL, "trial-witch")
                    ))
    );

    public static List<Track> tracks() { return TRACKS; }
    public static Track get(String id) {
        for (Track track : TRACKS) if (track.id().equals(id)) return track;
        return TRACKS.get(0);
    }
}
