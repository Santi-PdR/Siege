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
            new Threat("intel", "UNIDADES DOCUMENTADAS", "DOCUMENTED UNITS",
                    "Dossiers de UNIT, ADVANCED, TANK, BOSS, ELITE, SUPER-UNIT y UNKNOWN. UNKNOWN permanece cuando falta fuente fiable.",
                    "Dossiers for UNIT, ADVANCED, TANK, BOSS, ELITE, SUPER-UNIT and UNKNOWN. UNKNOWN remains when reliable sourcing is missing.",
                    Severity.HIGH, "", true),
            new Threat("executors", "EJECUTORES", "EXECUTORS",
                    "Terror radius, esencias/llaves y sistemas de sellos aparecen documentados; ediciones distintas cambiaron algunas reglas.",
                    "Terror radius, essences/keys and seal systems are documented; different editions changed some rules.",
                    Severity.CRITICAL, "executors-basics", false),
            new Threat("bosses", "BOSSES", "BOSSES",
                    "Algunos ataques pueden ignorar defensas, matar de inmediato o destruir materia. Preparación y movilidad importan.",
                    "Some attacks may bypass defenses, kill instantly or destroy matter. Preparation and mobility matter.",
                    Severity.CRITICAL, "bosses-basics", false),
            new Threat("raids", "RAIDS / HORDAS", "RAIDS / HORDES",
                    "Entrar con daño, defensa, movilidad, apoyo, rescate y retirada preparados; evitar técnicas destructivas sin límites.",
                    "Enter with damage, defense, mobility, support, rescue and retreat prepared; avoid unbounded destructive techniques.",
                    Severity.HIGH, "raids-basics", false),
            new Threat("structures", "ESTRUCTURAS", "STRUCTURES",
                    "NPC hostiles, shrines, puertas y paneles pueden exigir requisitos o castigar activaciones a ciegas.",
                    "Hostile NPCs, shrines, doors and panels may require conditions or punish blind activation.",
                    Severity.CAUTION, "structures-basics", false),
            new Threat("factions", "FACCIONES / ENEMIGOS", "FACTIONS / ENEMIES",
                    "Pueden adaptar, sabotear tecnología o atacar infraestructura; proteger rutas de escape y variar métodos.",
                    "May adapt, sabotage technology or attack infrastructure; protect escape routes and vary methods.",
                    Severity.HIGH, "factions-basics", false),
            new Threat("dimensions", "DIMENSIONES", "DIMENSIONS",
                    "Portales pueden exigir recursos o condiciones; entrar sólo con un método de salida conocido.",
                    "Portals may require resources or conditions; enter only with a known exit method.",
                    Severity.CAUTION, "dimensions-basics", false),
            new Threat("revive", "MUERTE / REVIVE", "DEATH / REVIVE",
                    "El sistema cambió varias veces. Diferenciar reglas actuales de escalas históricas antes de gastar recursos de revive.",
                    "The system changed several times. Distinguish current rules from historical scales before spending revive resources.",
                    Severity.HIGH, "respawn-cards", false)
    );

    public static List<Threat> all() { return THREATS; }
}
