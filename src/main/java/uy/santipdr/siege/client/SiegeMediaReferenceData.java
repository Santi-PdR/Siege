package uy.santipdr.siege.client;

import java.util.List;

/** Curated Dummies vs Noobs media references for the player-facing Media Room. */
public final class SiegeMediaReferenceData {
    public enum Use {
        LOBBY("LOBBY", "LOBBY"),
        VICTORY("VICTORIA", "VICTORY"),
        LOSS("DERROTA", "LOSS"),
        REFERENCE("REFERENCIA", "REFERENCE");
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
                    "Ideal para una entrada tranquila antes del briefing o despliegue.",
                    "Fits a calm entry before briefing or deployment."),
            new Track("Music Box", Use.LOBBY,
                    "El estilo más cercano a la calma inquietante del lobby; SIEGE ya usa su recorte preparado.",
                    "Closest to the lobby's uneasy calm; SIEGE already uses its prepared cut."),
            new Track("New Store", Use.LOBBY,
                    "Funciona para navegación tranquila, Atlas o Arsenal.",
                    "Works for calm navigation, Atlas or Armory."),
            new Track("Jazz Music", Use.LOBBY,
                    "Buena opción para Multimedia, Enciclopedia o pantallas de consulta.",
                    "Good fit for Media, Encyclopedia or reference screens."),
            new Track("From the Ashes", Use.VICTORY,
                    "Tema asociado a victoria; encaja con confirmaciones importantes o cierre de operación.",
                    "Victory-associated track; fits major confirmations or operation completion."),
            new Track("Sad Choir", Use.LOSS,
                    "Tema asociado a derrota; encaja con estados críticos o cierres fallidos.",
                    "Loss-associated track; fits critical states or failed outcomes.")
    );

    private static final List<Visual> VISUALS = List.of(
            new Visual("Stronghold / última fortaleza", "Stronghold / last stronghold",
                    "Defensa de fortaleza, escuadra y armamento moderno/futurista. Muy buen fondo de portada.",
                    "Stronghold defense, squad and modern/future weaponry. Strong fit for the main menu."),
            new Visual("Operación urbana nocturna", "Night urban operation",
                    "Siluetas tácticas, calles oscuras, focos y señalética militar. Ideal para Operaciones o Despliegue.",
                    "Tactical silhouettes, dark streets, spotlights and military signage. Ideal for Operations or Deployment."),
            new Visual("Hangar / briefing", "Hangar / briefing",
                    "Escuadra preparando equipo, interiores militares y luz controlada. Ideal para Briefing y Arsenal.",
                    "Squad preparing equipment, military interiors and controlled lighting. Ideal for Briefing and Armory."),
            new Visual("Defensa de oleada", "Wave defense",
                    "Una escena con presión y escala, pero con espacio oscuro suficiente para que el menú siga siendo legible.",
                    "A scene with pressure and scale while leaving enough dark space for the menu to remain readable.")
    );

    public static List<Track> dvnTracks() { return DVN_TRACKS; }
    public static List<Visual> visualReferences() { return VISUALS; }
}
