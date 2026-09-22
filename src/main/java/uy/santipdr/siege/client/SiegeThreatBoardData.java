package uy.santipdr.siege.client;

import java.util.List;

/** High-level threat domains; no new gameplay entities are invented here. */
public final class SiegeThreatBoardData {
    public enum Severity {
        INFO("INFORMACIÓN", "INFO", SiegeTheme.CYAN),
        CAUTION("PRECAUCIÓN", "CAUTION", SiegeTheme.GOLD),
        HIGH("ALTO", "HIGH", SiegeTheme.ORANGE),
        CRITICAL("CRÍTICO", "CRITICAL", SiegeTheme.RED);
        private final String es, en;
        private final int accent;
        Severity(String es, String en, int accent) { this.es = es; this.en = en; this.accent = accent; }
        public String label(boolean spanish) { return spanish ? es : en; }
        public int accent() { return accent; }
    }

    public record Threat(String id, String titleEs, String titleEn,
                         String summaryEs, String summaryEn,
                         Severity severity, String knowledgeId, boolean opensIntel) {
        public String title(boolean spanish) { return spanish ? titleEs : titleEn; }
        public String summary(boolean spanish) { return spanish ? summaryEs : summaryEn; }
    }

    private SiegeThreatBoardData() { }

    private static final List<Threat> THREATS = List.of(
            new Threat("intel", "UNIDADES Y AMENAZAS", "UNITS AND THREATS",
                    "Dossiers de UNIT, ADVANCED, TANK, BOSS, ELITE, SUPER-UNIT y UNKNOWN. UNKNOWN se usa cuando todavía no hay información suficiente para clasificar sin inventar.",
                    "Dossiers for UNIT, ADVANCED, TANK, BOSS, ELITE, SUPER-UNIT and UNKNOWN. UNKNOWN is used when there is not enough information to classify without guessing.",
                    Severity.HIGH, "", true),
            new Threat("executors", "EJECUTORES", "EXECUTORS",
                    "Reconocibles por su terror radius. Hay esencias, llaves y sellos ligados a ellos; algunas reglas cambiaron con el tiempo.",
                    "Recognizable by their terror radius. Essences, keys and seals are tied to them; some rules changed over time.",
                    Severity.CRITICAL, "executors-basics", false),
            new Threat("bosses", "BOSSES", "BOSSES",
                    "Algunos ataques pueden ignorar defensas, matar de inmediato o destruir materia. Movilidad, observación y una salida preparada importan.",
                    "Some attacks may bypass defenses, kill instantly or destroy matter. Mobility, observation and a prepared exit matter.",
                    Severity.CRITICAL, "bosses-basics", false),
            new Threat("raids", "RAIDS / HORDAS", "RAIDS / HORDES",
                    "Entrá con daño, defensa, movilidad, apoyo, rescate y retirada preparados. Evitá técnicas destructivas sin límites claros.",
                    "Enter with damage, defense, mobility, support, rescue and retreat prepared. Avoid destructive techniques without clear limits.",
                    Severity.HIGH, "raids-basics", false),
            new Threat("structures", "ESTRUCTURAS", "STRUCTURES",
                    "NPC hostiles, shrines, puertas y paneles pueden exigir condiciones. No actives o fuerces algo sin saber cómo salir.",
                    "Hostile NPCs, shrines, doors and panels may have conditions. Do not activate or force something without knowing how to get out.",
                    Severity.CAUTION, "structures-basics", false),
            new Threat("factions", "FACCIONES / ENEMIGOS", "FACTIONS / ENEMIES",
                    "Pueden adaptarse, sabotear tecnología o atacar infraestructura. Protegé rutas de escape y no dependas siempre de una sola táctica.",
                    "They may adapt, sabotage technology or attack infrastructure. Protect escape routes and do not rely on a single tactic every time.",
                    Severity.HIGH, "factions-basics", false),
            new Threat("dimensions", "DIMENSIONES", "DIMENSIONS",
                    "Algunos portales exigen recursos o condiciones. Entrá con un método de vuelta conocido y equipo para retirarte si algo sale mal.",
                    "Some portals require resources or conditions. Enter with a known way back and equipment to retreat if something goes wrong.",
                    Severity.CAUTION, "dimensions-basics", false),
            new Threat("revive", "MUERTE / REVIVE", "DEATH / REVIVE",
                    "Las formas de revivir y los estados de herida cambiaron con el tiempo. Revisá el sistema actual antes de gastar recursos de revive.",
                    "Revival methods and injury states changed over time. Check the current system before spending revival resources.",
                    Severity.HIGH, "respawn-cards", false)
    );

    public static List<Threat> all() { return THREATS; }
}
