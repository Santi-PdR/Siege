package uy.santipdr.siege.client;

import java.util.List;

/** Curated audiovisual direction for the SIEGE 5.00 Media Room. */
public final class SiegeMediaReferenceData {
    public enum Use {
        LOBBY("LOBBY", "LOBBY"),
        BRIEFING("BRIEFING", "BRIEFING"),
        DEPLOYMENT("DESPLIEGUE", "DEPLOYMENT"),
        COMBAT("COMBATE", "COMBAT"),
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

    /** A player-facing audiovisual preset. referenceTracks are suggestions, not bundled assets. */
    public record Mood(String id, String titleEs, String titleEn,
                       String purposeEs, String purposeEn,
                       String bundledTrack, List<String> referenceTracks) {
        public Mood {
            referenceTracks = referenceTracks == null ? List.of() : List.copyOf(referenceTracks);
        }
        public String title(boolean spanish) { return spanish ? titleEs : titleEn; }
        public String purpose(boolean spanish) { return spanish ? purposeEs : purposeEn; }
    }

    public record Visual(String id, String titleEs, String titleEn,
                         String noteEs, String noteEn, boolean rotationReady) {
        public String title(boolean spanish) { return spanish ? titleEs : titleEn; }
        public String note(boolean spanish) { return spanish ? noteEs : noteEn; }
    }

    private SiegeMediaReferenceData() { }

    /**
     * DVN soundtrack references verified against current soundtrack indexes.
     * They are not automatically bundled: binary audio only enters SIEGE when a
     * redistributable source is available.
     */
    private static final List<Track> DVN_TRACKS = List.of(
            new Track("Convenience Store", Use.LOBBY,
                    "Entrada tranquila antes del briefing o despliegue.",
                    "Calm entry before briefing or deployment."),
            new Track("Music Box", Use.LOBBY,
                    "Calma inquietante de lobby; SIEGE ya conserva un recorte preparado dentro de su soundtrack actual.",
                    "Uneasy lobby calm; SIEGE already keeps a prepared cut inside its current soundtrack."),
            new Track("New Store", Use.BRIEFING,
                    "Navegación tranquila para Atlas, Arsenal o preparación.",
                    "Calm navigation for Atlas, Armory or preparation."),
            new Track("Jazz Music", Use.REFERENCE,
                    "Consulta prolongada en Multimedia, Enciclopedia o pantallas de archivo.",
                    "Long-form browsing in Media, Encyclopedia or archive screens."),
            new Track("From the Ashes", Use.VICTORY,
                    "Cierre de operación, victoria o confirmación importante.",
                    "Operation completion, victory or major confirmation."),
            new Track("Sad Choir", Use.LOSS,
                    "Estados críticos, derrota o cierre fallido.",
                    "Critical states, defeat or failed outcomes."),
            new Track("Calm Before The Storm A", Use.DEPLOYMENT,
                    "Buen candidato para la transición entre preparación y entrada al frente.",
                    "Good candidate for the transition between preparation and deployment."),
            new Track("The Last Flame", Use.DEPLOYMENT,
                    "Tensión de epílogo sin entrar todavía en combate abierto.",
                    "Epilogue tension without moving into full combat yet."),
            new Track("Into The Storm", Use.COMBAT,
                    "Escalada fuerte para una operación o frente que ya entró en combate.",
                    "Strong escalation for an operation or front already in combat."),
            new Track("Grinder", Use.COMBAT,
                    "Combate industrial y agresivo; encaja con escenas de maquinaria y oleadas.",
                    "Aggressive industrial combat; fits machinery and wave-defense scenes."),
            new Track("Hell March (Remastered)", Use.COMBAT,
                    "Marcha pesada para guerra abierta; usar sólo si existe una fuente redistribuible válida.",
                    "Heavy march for open warfare; bundle only if a valid redistributable source exists."),
            new Track("Fight Through Adversity", Use.COMBAT,
                    "Combate sostenido con un tono menos caótico que una pista de boss.",
                    "Sustained combat with a less chaotic tone than a boss track."),
            new Track("Scanning Hostile Biodats", Use.COMBAT,
                    "Buen encaje para amenazas tecnológicas, Intel activo o encuentros de alta presión.",
                    "Fits technological threats, active Intel or high-pressure encounters."),
            new Track("Full Force", Use.COMBAT,
                    "Candidato para situaciones de última línea o cierre de una operación grande.",
                    "Candidate for last-line situations or the end of a major operation.")
    );

    private static final List<Mood> MOODS = List.of(
            new Mood("stronghold", "STRONGHOLD", "STRONGHOLD",
                    "Sala de operaciones bajo presión: oscura, militar y contenida.",
                    "Operations room under pressure: dark, military and restrained.",
                    "The Darkest of Days", List.of("Music Box", "From the Ashes", "The Last Flame")),
            new Mood("deployment", "DESPLIEGUE", "DEPLOYMENT",
                    "Preparación antes de entrar al servidor: tensión baja y sensación de partida inminente.",
                    "Pre-server preparation: low tension and a sense of imminent deployment.",
                    "Dummies vs Noobs Lobby Music", List.of("Convenience Store", "New Store", "Calm Before The Storm A")),
            new Mood("intel", "INTEL / ARCHIVO", "INTEL / ARCHIVE",
                    "Lectura de dossiers y archivos sin competir con el texto.",
                    "Dossier and archive reading without competing with text.",
                    "Tale of a Cruel World", List.of("Jazz Music", "Music Box", "Scanning Hostile Biodats")),
            new Mood("last-stand", "ÚLTIMA LÍNEA", "LAST STAND",
                    "Escenas de alto riesgo, bosses y amenazas mayores.",
                    "High-risk scenes, bosses and major threats.",
                    "Heaven's Hell-Sent Gift", List.of("Sad Choir", "From the Ashes", "Into The Storm", "Full Force")),
            new Mood("industrial-war", "GUERRA INDUSTRIAL", "INDUSTRIAL WAR",
                    "Oleadas, hangares, artillería y zonas industriales con ritmo más agresivo.",
                    "Waves, hangars, artillery and industrial zones with a more aggressive rhythm.",
                    "The Darkest of Days", List.of("Grinder", "Hell March (Remastered)", "Fight Through Adversity"))
    );

    private static final List<Visual> VISUALS = List.of(
            new Visual("stronghold-defense", "Stronghold / última fortaleza", "Stronghold / last stronghold",
                    "Defensa de fortaleza, escuadra y armamento moderno o futurista. Prioridad alta para portada.",
                    "Stronghold defense, squad and modern or future weaponry. High priority for the main menu.", false),
            new Visual("arctic-standoff", "Arctic Standoff", "Arctic Standoff",
                    "Frente helado, siluetas claras y contraste frío para navegación táctica.",
                    "Frozen front, clear silhouettes and cold contrast for tactical navigation.", false),
            new Visual("urban-night", "Operación urbana nocturna", "Night urban operation",
                    "Calles oscuras, focos, humo y señalética militar para Operaciones o Despliegue.",
                    "Dark streets, spotlights, smoke and military signage for Operations or Deployment.", false),
            new Visual("city-siege", "Ciudad bajo asedio", "City under siege",
                    "Combate urbano amplio con espacio negativo suficiente para la interfaz.",
                    "Wide urban combat with enough negative space for the interface.", false),
            new Visual("dune-front", "Frente desértico", "Desert front",
                    "Terreno abierto, convoyes y líneas de fuego para una escena de despliegue distinta.",
                    "Open terrain, convoys and firing lines for a different deployment scene.", false),
            new Visual("industrial-zone", "Zona industrial", "Industrial zone",
                    "Tuberías, hangares, hormigón y maquinaria pesada para reforzar el tono de guerra industrial.",
                    "Pipes, hangars, concrete and heavy machinery to reinforce the industrial-war tone.", false),
            new Visual("hangar-briefing", "Hangar / briefing", "Hangar / briefing",
                    "Escuadra preparando equipo e interiores militares con iluminación controlada.",
                    "Squad preparing equipment in military interiors with controlled lighting.", false),
            new Visual("wave-defense", "Defensa de oleada", "Wave defense",
                    "Presión y escala sin llenar toda la imagen de ruido visual.",
                    "Pressure and scale without filling the image with visual noise.", false),
            new Visual("boss-assault", "Asalto a boss", "Boss assault",
                    "Una amenaza dominante al fondo y una escuadra pequeña en primer plano para vender escala.",
                    "A dominant threat in the distance and a small squad in front to sell scale.", false),
            new Visual("armory-deployment", "Armería de despliegue", "Deployment armory",
                    "Armas, cajas, luces de emergencia y preparación previa a una misión.",
                    "Weapons, crates, emergency lights and pre-mission preparation.", false)
    );

    public static List<Track> dvnTracks() { return DVN_TRACKS; }
    public static List<Mood> moods() { return MOODS; }
    public static List<Visual> visualReferences() { return VISUALS; }
}
