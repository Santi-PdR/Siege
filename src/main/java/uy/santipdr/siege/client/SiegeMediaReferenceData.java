package uy.santipdr.siege.client;

import java.util.List;

/**
 * Reference-only Dummies vs Noobs media catalog.
 * External tracks/visuals are recommendations, never silent downloads. The mod
 * only bundles resources already in the project or supplied with clear permission.
 */
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
                    "Referencia de lobby de Dummies vs Noobs; no incluida automáticamente en el JAR.",
                    "Dummies vs Noobs lobby reference; not automatically bundled in the JAR."),
            new Track("Music Box", Use.LOBBY,
                    "Referencia de lobby. SIEGE conserva el recorte Kaptain Music Box ya suministrado al proyecto.",
                    "Lobby reference. SIEGE keeps the project-supplied Kaptain Music Box cut."),
            new Track("New Store", Use.LOBBY,
                    "Referencia de lobby; requiere una fuente/licencia clara antes de distribuirse.",
                    "Lobby reference; requires a clear source/license before redistribution."),
            new Track("Jazz Music", Use.LOBBY,
                    "Referencia de lobby para una sección tranquila o multimedia; no incluida.",
                    "Lobby reference suitable for a calm/media section; not bundled."),
            new Track("From the Ashes", Use.VICTORY,
                    "Referencia asociada a victoria; no se redistribuye sin verificar derechos del audio.",
                    "Victory-associated reference; not redistributed without verifying audio rights."),
            new Track("Sad Choir", Use.LOSS,
                    "Referencia asociada a derrota; no se redistribuye sin verificar derechos del audio.",
                    "Loss-associated reference; not redistributed without verifying audio rights.")
    );

    private static final List<Visual> VISUALS = List.of(
            new Visual("Stronghold / última fortaleza", "Stronghold / last stronghold",
                    "Buscar capturas oficiales con defensa de fortaleza, escuadra y armamento moderno/futurista.",
                    "Look for official imagery showing stronghold defense, a squad and modern/future weaponry."),
            new Visual("Operación urbana nocturna", "Night urban operation",
                    "Escenas DVN con siluetas tácticas, calles oscuras, focos y señalética militar funcionan bien bajo el chrome SIEGE.",
                    "DVN scenes with tactical silhouettes, dark streets, spotlights and military signage fit the SIEGE chrome well."),
            new Visual("Hangar / briefing", "Hangar / briefing",
                    "Ideal para Briefing, Deployment o Media Room; priorizar imágenes 16:9 de alta resolución.",
                    "Ideal for Briefing, Deployment or Media Room; prioritize high-resolution 16:9 images."),
            new Visual("Defensa de oleada", "Wave defense",
                    "Debe comunicar escala y presión sin tapar la UI; usar contraste adaptativo y evitar texto incrustado importante.",
                    "Should communicate scale and pressure without fighting the UI; use adaptive contrast and avoid important baked-in text.")
    );

    public static List<Track> dvnTracks() { return DVN_TRACKS; }
    public static List<Visual> visualReferences() { return VISUALS; }
}
