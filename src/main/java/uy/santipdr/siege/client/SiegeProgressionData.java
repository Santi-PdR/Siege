package uy.santipdr.siege.client;

import java.util.List;

/** Non-personal progression map for SIEGE 4.0. */
public final class SiegeProgressionData {
    public enum Status {
        CONFIRMED("CONFIRMADO", "CONFIRMED", SiegeTheme.GREEN),
        PARTIAL("INCOMPLETO", "INCOMPLETE", SiegeTheme.GOLD),
        HISTORICAL("HISTÓRICO", "HISTORICAL", SiegeTheme.ORANGE),
        VARIABLE("VARIABLE", "VARIABLE", SiegeTheme.CYAN);

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
            new Track("core", "RUTA GENERAL", "GENERAL ROUTE",
                    "La progresión no es sólo subir una raza: explorar, investigar y sobrevivir abren sistemas posteriores.",
                    "Progression is more than raising a race: exploration, research and survival open later systems.",
                    List.of(
                            n("explore", "Explorar con salida", "Explore with an exit",
                                    "Movilidad, retirada y conocimiento del entorno forman parte del progreso.",
                                    "Mobility, retreat and environment knowledge are part of progression.", Status.CONFIRMED, "server-exploration"),
                            n("race", "Entender tu raza", "Understand your race",
                                    "No todas usan V1→V4 ni tienen los mismos pasos.",
                                    "Not all of them use V1→V4 or the same steps.", Status.CONFIRMED, "progression-v1-v4"),
                            n("trials", "Preparar Trials", "Prepare Trials",
                                    "Pueden pedir raza/versión, nivel, kills, objetos o condiciones especiales.",
                                    "May require race/version, level, kills, items or special conditions.", Status.CONFIRMED, "trials-basics"),
                            n("research", "Investigar loot", "Research loot",
                                    "Objetos raros pueden estar ligados a reliquias, Trials o crafting.",
                                    "Unusual loot may connect to relics, Trials or crafting.", Status.CONFIRMED, "item-geography-table"),
                            n("advanced", "Abrir sistemas avanzados", "Open advanced systems",
                                    "Reliquias, Assembling, dimensiones y raids aparecen como ramas posteriores.",
                                    "Relics, Assembling, dimensions and raids appear as later branches.", Status.VARIABLE, "relic-basics")
                    )),
            new Track("v1v4", "V1 → V4", "V1 → V4",
                    "Marco común para varias razas. Los requisitos concretos siguen siendo propios de cada una.",
                    "Common framework for several races. Exact requirements remain race-specific.",
                    List.of(
                            n("v1", "V1", "V1",
                                    "Estado base: en varias razas los efectos todavía son poco notorios.",
                                    "Base state: in several races effects are still subtle.", Status.CONFIRMED, "progression-v1-v4"),
                            n("v2", "V2", "V2",
                                    "Suele volver atributos o capacidades más evidentes; no hay un requisito universal.",
                                    "Often makes attributes or capabilities more evident; there is no universal requirement.", Status.CONFIRMED, "progression-v1-v4"),
                            n("v3", "V3", "V3",
                                    "Puede exigir cadenas de pasos específicas de la raza.",
                                    "May require race-specific chains of steps.", Status.PARTIAL, "race-human"),
                            n("v4", "V4", "V4",
                                    "Muchos Trials y artefactos la usan como requisito inicial. Una etapa histórica también exigió Trials con otros jugadores reales.",
                                    "Many Trials and artifacts use it as an initial requirement. One historical stage also required Trials with other real players.", Status.VARIABLE, "progression-v1-v4")
                    )),
            new Track("trials", "TRIALS", "TRIALS",
                    "No existe una Trial universal: cada una debe entenderse por requisito, pasos, recompensa, fallo y repetibilidad.",
                    "There is no universal Trial: each one must be understood through requirements, steps, reward, failure and repeatability.",
                    List.of(
                            n("meditation", "Trials de meditación", "Meditation Trials",
                                    "Aparecen en estructuras, pueden pedir niveles de experiencia y dar potenciadores de meditación. Varios detalles siguen abiertos.",
                                    "Appear in structures, may require experience levels and grant meditation boosts. Several details remain open.", Status.PARTIAL, "trial-meditation"),
                            n("spire", "Trial Spire", "Trial Spire",
                                    "Lugar relacionado con NPC y solicitud/socialización de Trials; no representa una única prueba.",
                                    "Location tied to NPCs and Trial requests/social activity; it is not one single Trial.", Status.PARTIAL, "trial-spire"),
                            n("perish", "Perish Staff V2", "Perish Staff V2",
                                    "Ruta histórica: Perish Staff + 3 Angel Feathers + 1 Antiprisma, imbuir y ganar un Trial usando sólo el bastón.",
                                    "Historical route: Perish Staff + 3 Angel Feathers + 1 Antiprisma, imbue it and win a Trial using only the staff.", Status.HISTORICAL, "trials-basics"),
                            n("hidden", "Razas ocultas", "Hidden races",
                                    "Algunas razas de AUs de Undertale fueron relacionadas con Trials y otros pasos difíciles.",
                                    "Some Undertale AU races were linked to Trials and other difficult steps.", Status.PARTIAL, "race-undertale-au")
                    )),
            new Track("special", "RUTAS ESPECIALES", "SPECIAL ROUTES",
                    "Razas y sistemas que no encajan limpiamente en el esquema V1→V4.",
                    "Races and systems that do not fit cleanly into the V1→V4 scheme.",
                    List.of(
                            n("saiyan", "Saiyan", "Saiyan",
                                    "Transformaciones, estadísticas, control de forma y teleport.",
                                    "Transformations, stats, form control and teleport.", Status.CONFIRMED, "race-saiyan"),
                            n("cyborg", "Cyborg", "Cyborg",
                                    "Implantes, trasplantes y chips mediante Assembling.",
                                    "Implants, transplants and chips through Assembling.", Status.CONFIRMED, "race-cyborg"),
                            n("hacker", "Hacker", "Hacker",
                                    "Versiones propias, entrenamiento enfocado, Room, Gate y otras capacidades de energía.",
                                    "Own version progression, focused training, Room, Gate and other energy capabilities.", Status.VARIABLE, "race-hacker"),
                            n("fabled", "Fabled", "Fabled",
                                    "Se describieron con pasos y spins especiales en vez de giros comunes.",
                                    "Described through steps and special spins rather than common rolls.", Status.CONFIRMED, "fabled-acquisition")
                    )),
            new Track("advanced", "SISTEMAS AVANZADOS", "ADVANCED SYSTEMS",
                    "Ramas que ganan importancia cuando ya entendés raza, Trials y supervivencia básica.",
                    "Branches that matter more once race, Trials and basic survival are understood.",
                    List.of(
                            n("assembling", "Assembling", "Assembling",
                                    "Instalación de chips y trasplantes; también se conecta con Cyborg.",
                                    "Installation of chips and transplants; also connects to Cyborg.", Status.CONFIRMED, "assembling-table"),
                            n("relics", "Reliquias", "Relics",
                                    "Investigar antes de equipar, vender o extraer; no muestran necesariamente todo al primer vistazo.",
                                    "Research before equipping, selling or extracting; they may not reveal everything at first glance.", Status.CONFIRMED, "relic-basics"),
                            n("dimensions", "Dimensiones", "Dimensions",
                                    "Portales pueden pedir recursos/condiciones; entrar con método de salida conocido.",
                                    "Portals may require resources/conditions; enter with a known exit method.", Status.CONFIRMED, "dimensions-basics"),
                            n("raids", "Raids y eventos", "Raids and events",
                                    "Preparar daño, defensa, movilidad, apoyo, rescate y retirada.",
                                    "Prepare damage, defense, mobility, support, rescue and retreat.", Status.CONFIRMED, "raids-basics")
                    ))
    );

    public static List<Track> tracks() { return TRACKS; }
    public static Track get(String id) {
        for (Track track : TRACKS) if (track.id().equals(id)) return track;
        return TRACKS.get(0);
    }
}
