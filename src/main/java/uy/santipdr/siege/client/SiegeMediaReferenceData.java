package uy.santipdr.siege.client;

import java.util.List;

/** Curated Dummies vs Noobs media ideas used by SIEGE 4.0's Media Room. */
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
                    "Buen tono para navegación tranquila y pantallas de descanso.",
                    "Good fit for calm navigation and downtime screens."),
            new Track("Music Box", Use.LOBBY,
                    "Tono de lobby reconocible; combina especialmente bien con la portada y la galería.",
                    "Recognizable lobby tone; fits the title screen and gallery especially well."),
            new Track("New Store", Use.LOBBY,
                    "Funciona bien para Arsenal, galería o pantallas de preparación.",
                    "Fits Armory, gallery or preparation screens well."),
            new Track("Jazz Music", Use.LOBBY,
                    "Más relajada: ideal para Enciclopedia, lectura o Multimedia.",
                    "More relaxed: ideal for Encyclopedia, reading or Media."),
            new Track("From the Ashes", Use.VICTORY,
                    "Encaja con cierres victoriosos o momentos de operación completada.",
                    "Fits victorious endings or operation-complete moments."),
            new Track("Sad Choir", Use.LOSS,
                    "Encaja con derrota, archivo oscuro o transiciones de alto riesgo.",
                    "Fits defeat, dark archive or high-risk transitions.")
    );

    private static final List<Visual> VISUALS = List.of(
            new Visual("Última fortaleza", "Last stronghold",
                    "Defensa de fortaleza, escuadra táctica y armamento moderno/futurista.",
                    "Stronghold defense, tactical squad and modern/near-future weaponry."),
            new Visual("Operación urbana nocturna", "Night urban operation",
                    "Calles oscuras, focos, siluetas tácticas y señalética militar.",
                    "Dark streets, spotlights, tactical silhouettes and military signage."),
            new Visual("Hangar / briefing", "Hangar / briefing",
                    "Ideal para Despliegue, Briefing de Recluta y Sala Multimedia.",
                    "Ideal for Deployment, Recruit Briefing and Media Room."),
            new Visual("Defensa de oleada", "Wave defense",
                    "Escena amplia con presión de combate sin tapar las zonas de lectura del menú.",
                    "Wide combat-pressure scene that keeps menu reading areas clear."),
            new Visual("Escuadra en avance", "Squad advancing",
                    "Composición con varios soldados y profundidad de campo para reforzar la identidad cooperativa.",
                    "Multi-soldier composition with depth to reinforce the cooperative identity."),
            new Visual("Zona industrial devastada", "Devastated industrial zone",
                    "Ruinas, humo, luz fría y maquinaria para secciones de Intel o Amenazas.",
                    "Ruins, smoke, cold light and machinery for Intel or Threat sections.")
    );

    public static List<Track> dvnTracks() { return DVN_TRACKS; }
    public static List<Visual> visualReferences() { return VISUALS; }
}
