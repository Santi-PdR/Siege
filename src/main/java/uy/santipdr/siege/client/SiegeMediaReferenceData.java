package uy.santipdr.siege.client;

import java.util.List;

/**
 * Dummies vs Noobs media recommendations. External media stays reference-only
 * unless the project receives a distributable resource or clear permission.
 */
public final class SiegeMediaReferenceData {
    public enum Use {
        LOBBY("LOBBY", "LOBBY"),
        VICTORY("VICTORIA", "VICTORY"),
        LOSS("DERROTA", "LOSS"),
        REFERENCE("AMBIENTE", "AMBIENCE");
        private final String es, en;
        Use(String es, String en) { this.es = es; this.en = en; }
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    public record Track(String title, Use use, String noteEs, String noteEn) {
        public String note(boolean spanish) { return spanish ? noteEs : noteEn; }
    }
    public record Visual(String titleEs, String titleEn, String noteEs, String noteEn) {
        public String title(boolean spanish) { return spanish ? titleEs : titleEn; }
        public String note(boolean spanish) { return spanish ? noteEs : noteEn; }
    }

    private SiegeMediaReferenceData() { }

    private static final List<Track> DVN_TRACKS = List.of(
            new Track("Convenience Store", Use.LOBBY,
                    "Buena para un menú tranquilo antes del despliegue.",
                    "Works well for a calm pre-deployment menu."),
            new Track("Music Box", Use.LOBBY,
                    "La identidad más cercana al ambiente de lobby que SIEGE ya usa.",
                    "Closest match to the lobby atmosphere SIEGE already uses."),
            new Track("New Store", Use.LOBBY,
                    "Alternativa para rotaciones de menú más relajadas.",
                    "Alternative for calmer menu rotations."),
            new Track("Jazz Music", Use.LOBBY,
                    "Encaja especialmente bien en Archivo, Enciclopedia o Multimedia.",
                    "Fits Archive, Encyclopedia or Media particularly well."),
            new Track("From the Ashes", Use.VICTORY,
                    "Referencia para cierres de operación o pantallas de victoria.",
                    "Reference for operation endings or victory screens."),
            new Track("Sad Choir", Use.LOSS,
                    "Referencia para desconexiones, derrotas o estados críticos.",
                    "Reference for disconnects, defeats or critical states.")
    );

    private static final List<Visual> VISUALS = List.of(
            new Visual("Stronghold / última fortaleza", "Stronghold / last stronghold",
                    "Defensa de fortaleza, escuadra y armamento moderno/futurista.",
                    "Stronghold defense, squad presence and modern/future weaponry."),
            new Visual("Operación urbana nocturna", "Night urban operation",
                    "Calles oscuras, focos, humo, señalética militar y siluetas tácticas.",
                    "Dark streets, spotlights, smoke, military signage and tactical silhouettes."),
            new Visual("Hangar / briefing", "Hangar / briefing",
                    "Ideal para Briefing o Despliegue: tropas, equipo y preparación antes de salir.",
                    "Ideal for Briefing or Deployment: troops, gear and pre-mission preparation."),
            new Visual("Defensa de oleada", "Wave defense",
                    "Una escena que muestre presión, escala y la sensación de mantener una posición.",
                    "A scene showing pressure, scale and the feeling of holding a position."),
            new Visual("Zona ártica / puesto avanzado", "Arctic zone / forward post",
                    "Varía la paleta del menú sin abandonar la estética militar de DVN.",
                    "Varies the menu palette without leaving DVN's military aesthetic."),
            new Visual("Ruinas industriales", "Industrial ruins",
                    "Fábricas, carreteras destruidas y zonas de combate con espacio limpio para la UI.",
                    "Factories, destroyed roads and combat zones with clean space for the UI.")
    );

    public static List<Track> dvnTracks() { return DVN_TRACKS; }
    public static List<Visual> visualReferences() { return VISUALS; }
}
