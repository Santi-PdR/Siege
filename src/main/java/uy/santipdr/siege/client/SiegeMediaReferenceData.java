package uy.santipdr.siege.client;

import java.util.List;

/** Curated audiovisual direction for the SIEGE 5.10 Media Room. */
public final class SiegeMediaReferenceData {
    public enum Use {
        LOBBY("LOBBY", "LOBBY"),
        BRIEFING("BRIEFING", "BRIEFING"),
        DEPLOYMENT("DESPLIEGUE", "DEPLOYMENT"),
        COMBAT("COMBATE", "COMBAT"),
        BOSS("BOSS", "BOSS"),
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

    /** A player-facing audiovisual preset. referenceTracks may include bundled and reference-only songs. */
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

    private static final List<Track> DVN_TRACKS = List.of(
            new Track("Arc - Enemy", Use.COMBAT,
                    "Pista de Potoe dedicada a DVN. SIEGE 5.10 la integra en la rotación del menú con su atribución incluida.",
                    "Potoe track dedicated to DVN. SIEGE 5.10 includes it in menu rotation with bundled attribution."),
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
                    "Marcha pesada para guerra abierta.",
                    "Heavy march for open warfare."),
            new Track("Fight Through Adversity", Use.COMBAT,
                    "Combate sostenido con un tono menos caótico que una pista de boss.",
                    "Sustained combat with a less chaotic tone than a boss track."),
            new Track("Scanning Hostile Biodats", Use.COMBAT,
                    "Buen encaje para amenazas tecnológicas, Intel activo o encuentros de alta presión.",
                    "Fits technological threats, active Intel or high-pressure encounters."),
            new Track("Full Force", Use.COMBAT,
                    "Candidato para situaciones de última línea o cierre de una operación grande.",
                    "Candidate for last-line situations or the end of a major operation."),
            new Track("Powerplay", Use.BOSS,
                    "Boss de presión frontal o combate con ritmo muy marcado.",
                    "Boss encounter with direct pressure and a strongly marked rhythm."),
            new Track("Bewitched", Use.BOSS,
                    "Encuentros extraños o con una amenaza menos militar y más anómala.",
                    "Unusual encounters with a less military and more anomalous threat."),
            new Track("Dissonant", Use.BOSS,
                    "Bosses impredecibles, fallas, anomalías o combates que necesitan incomodidad constante.",
                    "Unpredictable bosses, failures, anomalies or fights that need constant unease."),
            new Track("Voltaic Dispatch", Use.BOSS,
                    "Encaja especialmente con amenazas eléctricas, Tesla o tecnológicas.",
                    "Especially suitable for electrical, Tesla or technological threats."),
            new Track("Ablaze", Use.BOSS,
                    "Combate de alta intensidad, incendios o una fase que ya está fuera de control.",
                    "High-intensity combat, fire or a phase that has gone out of control."),
            new Track("Dweller's Fury", Use.BOSS,
                    "Amenaza pesada, criatura grande o pelea cerrada con sensación de persecución.",
                    "Heavy threat, large creature or enclosed fight with a pursuit feel."),
            new Track("Dead Center", Use.BOSS,
                    "Boss directo y limpio para una pelea donde el objetivo principal domina toda la escena.",
                    "Direct boss track for a fight where the main target dominates the whole scene."),
            new Track("Imperishable Valour", Use.BOSS,
                    "Última fase, defensa desesperada o pelea donde el equipo aguanta contra una amenaza superior.",
                    "Final phase, desperate defense or a fight where the squad holds against a superior threat."),
            new Track("Death Sentence", Use.BOSS,
                    "Para encuentros de máximo riesgo, Executores o situaciones donde un error puede cerrar la pelea.",
                    "For maximum-risk encounters, Executors or situations where one mistake can end the fight.")
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
                    "Heaven's Hell-Sent Gift", List.of("Arc - Enemy", "Sad Choir", "From the Ashes", "Into The Storm", "Full Force")),
            new Mood("industrial-war", "GUERRA INDUSTRIAL", "INDUSTRIAL WAR",
                    "Oleadas, hangares, artillería y zonas industriales con ritmo más agresivo.",
                    "Waves, hangars, artillery and industrial zones with a more aggressive rhythm.",
                    "Arc - Enemy · Potoe", List.of("Grinder", "Hell March (Remastered)", "Fight Through Adversity")),
            new Mood("boss-alert", "BOSS / ALERTA ROJA", "BOSS / RED ALERT",
                    "Peleas donde una sola unidad domina el frente y la interfaz debe sentirse más urgente.",
                    "Fights where one unit dominates the front and the interface should feel more urgent.",
                    "Heaven's Hell-Sent Gift", List.of("Powerplay", "Voltaic Dispatch", "Ablaze", "Imperishable Valour", "Death Sentence"))
    );

    private static final List<Visual> VISUALS = List.of(
            new Visual("official-gallery-01", "DVN · Escena oficial 01", "DVN · Official Scene 01",
                    "Miniatura oficial de Dummies vs Noobs cargada en la rotación 5.10 a su resolución nativa 768×432.",
                    "Official Dummies vs Noobs thumbnail loaded into the 5.10 rotation at its native 768×432 resolution.", true),
            new Visual("official-gallery-02", "DVN · Escena oficial 02", "DVN · Official Scene 02",
                    "Segunda miniatura oficial de Dummies vs Noobs cargada en la rotación sin inventar detalle mediante upscale.",
                    "Second official Dummies vs Noobs thumbnail loaded into rotation without inventing detail through upscaling.", true),
            new Visual("stronghold-defense", "Stronghold / última fortaleza", "Stronghold / last stronghold",
                    "Defensa de fortaleza, escuadra y armamento moderno o futurista. Prioridad alta para portada.",
                    "Stronghold defense, squad and modern or future weaponry. High priority for the main menu.", false),
            new Visual("portal-last-stand", "Última defensa del portal", "Last stand at the portal",
                    "Escena DVN de defensa final alrededor de un portal, con el foco lejos de la zona donde se dibuja la navegación.",
                    "DVN final-defense scene around a portal, keeping the focal point away from navigation text.", false),
            new Visual("arctic-standoff", "Frente ártico", "Arctic front",
                    "Referencia visual para una escena DVN fría y abierta; todavía no forma parte de la rotación.",
                    "Visual reference for a cold, open DVN scene; not part of rotation yet.", false),
            new Visual("coastal-assault", "Asalto costero", "Coastal assault",
                    "Referencia para una escena costera de despliegue; todavía no forma parte de la rotación.",
                    "Reference for a coastal deployment scene; not part of rotation yet.", false),
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
