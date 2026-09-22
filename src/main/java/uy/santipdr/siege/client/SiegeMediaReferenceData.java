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
                    "Ideal para una entrada tranquila antes de la guía o el despliegue.",
                    "Fits a calm entry before the guide or deployment."),
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
            new Visual("Última fortaleza de Dummykind", "Dummykind's last stronghold",
                    "La referencia más directa para portada: fortaleza, defensa, humo y espacio oscuro para los botones.",
                    "The most direct main-menu reference: stronghold, defense, smoke and dark space for buttons."),
            new Visual("Escuadra de 8 contra horda Noob", "8-player squad vs Noob horde",
                    "Una composición de escuadra defendiendo una línea frente a una horda comunica DVN de inmediato.",
                    "A squad defending a line against a horde communicates DVN immediately."),
            new Visual("Armamento moderno / casi futurista", "Modern / near-future weaponry",
                    "Soldados, rifles, ópticas, equipo técnico y vehículos; buena base para Intel, Arsenal y Operaciones.",
                    "Soldiers, rifles, optics, technical gear and vehicles; strong for Intel, Armory and Operations."),
            new Visual("Operación ártica", "Arctic operation",
                    "Nieve, equipamiento invernal y visibilidad reducida; da variedad sin salir de la identidad militar DVN.",
                    "Snow, winter gear and reduced visibility; adds variety while staying inside DVN's military identity."),
            new Visual("Operación urbana nocturna", "Night urban operation",
                    "Siluetas tácticas, calles oscuras, focos y señalética militar. Ideal para Operaciones o Despliegue.",
                    "Tactical silhouettes, dark streets, spotlights and military signage. Ideal for Operations or Deployment."),
            new Visual("Hangar / briefing", "Hangar / briefing",
                    "Escuadra preparando equipo, interiores militares y luz controlada. Ideal para Guía y Arsenal.",
                    "Squad preparing equipment, military interiors and controlled lighting. Ideal for Guide and Armory."),
            new Visual("Defensa de oleada", "Wave defense",
                    "Presión, escala y enemigos al fondo, con espacio limpio suficiente para que la interfaz siga siendo legible.",
                    "Pressure, scale and distant enemies, with enough clean space for the interface to remain readable.")
    );

    public static List<Track> dvnTracks() { return DVN_TRACKS; }
    public static List<Visual> visualReferences() { return VISUALS; }
}
