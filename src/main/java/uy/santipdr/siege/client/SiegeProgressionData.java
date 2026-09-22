package uy.santipdr.siege.client;

import java.util.List;

/** Non-personal progression map built only from source-aware server records. */
public final class SiegeProgressionData {
    public enum Status {
        CONFIRMED("CONFIRMADO", "CONFIRMED", 0xFF72C98B),
        PARTIAL("PARCIAL", "PARTIAL", 0xFFD6AE65),
        HISTORICAL("HISTÓRICO", "HISTORICAL", 0xFFE89B59),
        VARIABLE("VARIABLE", "VARIABLE", 0xFF68C6D8);
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
            new Track("core", "PROGRESIÓN GENERAL", "GENERAL PROGRESSION",
                    "Mapa conceptual: explorar, identificar sistema racial, alcanzar requisitos y preparar Trials/artefactos.",
                    "Concept map: explore, identify the race system, meet requirements and prepare Trials/artifacts.",
                    List.of(
                            n("explore", "Explorar con salida", "Explore with an exit",
                                    "Movilidad, retirada y conocimiento del entorno son parte del progreso.",
                                    "Mobility, retreat and environment knowledge are part of progression.", Status.CONFIRMED, "server-exploration"),
                            n("race", "Identificar ruta racial", "Identify race route",
                                    "No todas las razas usan el mismo camino V1→V4.",
                                    "Not every race uses the same V1→V4 path.", Status.CONFIRMED, "progression-v1-v4"),
                            n("trials", "Preparar Trials", "Prepare Trials",
                                    "Requisitos pueden incluir raza/versión, nivel, kills u objetos.",
                                    "Requirements may include race/version, level, kills or items.", Status.CONFIRMED, "trials-basics"),
                            n("research", "Investigar objetos", "Research items",
                                    "Geography Table y otras herramientas reducen decisiones a ciegas.",
                                    "Geography Table and other tools reduce blind decisions.", Status.CONFIRMED, "item-geography-table"),
                            n("advanced", "Sistemas avanzados", "Advanced systems",
                                    "Reliquias, Assembling, dimensiones y raids dependen del estado del servidor.",
                                    "Relics, Assembling, dimensions and raids depend on server state.", Status.VARIABLE, "relic-basics")
                    )),
            new Track("v1v4", "RUTA V1 → V4", "V1 → V4 ROUTE",
                    "Marco común para varias razas; los requisitos concretos cambian por raza.",
                    "Common framework for several races; exact requirements vary by race.",
                    List.of(
                            n("v1", "V1", "V1", "Base racial; pocos efectos notorios en varias razas.", "Race baseline; limited visible effects for several races.", Status.CONFIRMED, "progression-v1-v4"),
                            n("v2", "V2", "V2", "Suele volver efectos/atributos más visibles; requisitos no universales.", "Often makes effects/attributes more visible; requirements are not universal.", Status.CONFIRMED, "progression-v1-v4"),
                            n("v3", "V3", "V3", "Puede implicar varios pasos específicos de la raza.", "May involve several race-specific steps.", Status.PARTIAL, "race-human"),
                            n("v4", "V4", "V4", "Muchos Trials/artefactos la usan como requisito inicial, pero no todos.", "Many Trials/artifacts use it as an initial requirement, but not all.", Status.CONFIRMED, "progression-v1-v4")
                    )),
            new Track("special", "RUTAS ESPECIALES", "SPECIAL ROUTES",
                    "Razas que no encajan limpiamente en el esquema general.",
                    "Races that do not fit cleanly into the general scheme.",
                    List.of(
                            n("saiyan", "Saiyan", "Saiyan", "Transformaciones, stats, control y teleport.", "Transformations, stats, control and teleport.", Status.CONFIRMED, "race-saiyan"),
                            n("cyborg", "Cyborg", "Cyborg", "Implantes, chips y Assembling forman parte de su ruta.", "Implants, chips and Assembling are part of its route.", Status.CONFIRMED, "race-cyborg"),
                            n("hidden", "Razas ocultas", "Hidden races", "Algunas se obtienen mediante Trials/pasos, no por giros comunes.", "Some are obtained through Trials/steps rather than common rolls.", Status.PARTIAL, "race-undertale-au"),
                            n("fabled", "Fabled", "Fabled", "Usan pasos y spins especiales; no giros comunes.", "Use steps and special spins; not common rolls.", Status.CONFIRMED, "fabled-acquisition")
                    )),
            new Track("advanced", "PROGRESIÓN AVANZADA", "ADVANCED PROGRESSION",
                    "Sistemas que suelen importar cuando ya se domina la base del servidor.",
                    "Systems that usually matter after mastering the server basics.",
                    List.of(
                            n("assembling", "Assembling", "Assembling", "Trasplantes, chips y tecnología avanzada.", "Transplants, chips and advanced technology.", Status.CONFIRMED, "assembling-table"),
                            n("relics", "Reliquias", "Relics", "Investigar antes de equipar, vender o extraer.", "Research before equipping, selling or extracting.", Status.CONFIRMED, "relic-analysis-workflow"),
                            n("dimensions", "Dimensiones", "Dimensions", "Entrar con método de salida confirmado.", "Enter with a confirmed exit method.", Status.CONFIRMED, "dimensions-basics"),
                            n("raids", "Raids y eventos", "Raids and events", "Preparar daño, defensa, movilidad, apoyo y rescate.", "Prepare damage, defense, mobility, support and rescue.", Status.CONFIRMED, "raids-basics")
                    ))
    );

    public static List<Track> tracks() { return TRACKS; }
    public static Track get(String id) {
        for (Track track : TRACKS) if (track.id().equals(id)) return track;
        return TRACKS.get(0);
    }
}
