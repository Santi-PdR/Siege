package uy.santipdr.siege.client;

import java.util.Comparator;
import java.util.List;

/**
 * 0.40 operational archive. Source order is significant: the supplied SIEGE
 * notices are newest-to-oldest, so a higher rank wins whenever two notices
 * describe the same subject differently.
 */
public final class SiegeArchiveData {
    private SiegeArchiveData() { }

    public enum Category {
        CURRENT("ACTUAL", "CURRENT"),
        MISSIONS("MISIONES", "MISSIONS"),
        UNITS("UNIDADES", "UNITS"),
        EQUIPMENT("EQUIPO", "EQUIPMENT"),
        CASUALTY("ESTADOS", "CASUALTY"),
        HISTORY("ARCHIVO", "ARCHIVE");
        private final String es, en;
        Category(String es, String en) { this.es = es; this.en = en; }
        public String title(boolean spanish) { return spanish ? es : en; }
    }

    public enum Source {
        CURRENT_SIEGE("SIEGE · ACTUAL", "SIEGE · CURRENT"),
        SIEGE_ARCHIVE("SIEGE · ARCHIVO", "SIEGE · ARCHIVE"),
        DVN_REFERENCE("DVN · REFERENCIA", "DVN · REFERENCE");
        private final String es, en;
        Source(String es, String en) { this.es = es; this.en = en; }
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    public record Entry(String id, Category category, int rank, Source source,
                        String titleEs, String titleEn, String bodyEs, String bodyEn) {
        public String title(boolean spanish) { return spanish ? titleEs : titleEn; }
        public String body(boolean spanish) { return spanish ? bodyEs : bodyEn; }
        public String searchable(boolean spanish) {
            return title(spanish) + " " + body(spanish) + " " + source.label(spanish);
        }
    }

    private static Entry e(String id, Category category, int rank, Source source,
                           String titleEs, String titleEn, String bodyEs, String bodyEn) {
        return new Entry(id, category, rank, source, titleEs, titleEn, bodyEs, bodyEn);
    }

    /**
     * Keep this list in descending announcement order. Do not sort alphabetically:
     * chronology is part of the data model and is regression-tested.
     */
    public static final List<Entry> ENTRIES = List.of(
        e("current-core", Category.CURRENT, 1000, Source.CURRENT_SIEGE,
            "Estado actual · herramientas, Stalkers, Engineer y SHELLSHOCK",
            "Current state · tools, Stalkers, Engineer and SHELLSHOCK",
            "HERRAMIENTAS ICÓNICAS\nSe anunciaron nuevos iconos para Wrench, F.A.S.T, Jetpack, Terminal Velocity y Aerorig+. Este registro sólo confirma la renovación visual de iconos; no inventa estadísticas nuevas."
            + "\n\nSTALKERS\nLos Stalkers fueron reañadidos al despliegue. El aviso no aporta aquí estadísticas suficientes para crear un dossier Intel completo."
            + "\n\nENGINEER · SOUND ERRADICATOR\nLos Engineers reciben Sound Erradicator: pueden lanzar un megáfono destruido o un teléfono Motorola ejecutando PUBG. Al impactar, el dispositivo estalla y aplica sordera total, náuseas y Lentitud VI–VIII."
            + "\n\nDIFICULTAD\nSHELLSHOCK queda registrada al 150%. Al ser el aviso más reciente, esta dificultad se considera vigente frente a listados anteriores que saltaban de Dynamic 100% a Masoquista 200%.",
            "ICONIC TOOLS\nNew icons were announced for Wrench, F.A.S.T, Jetpack, Terminal Velocity and Aerorig+. This record confirms the icon refresh only; it does not invent new statistics."
            + "\n\nSTALKERS\nStalkers were re-added to deployment. The notice does not provide enough statistics here for a complete Intel dossier."
            + "\n\nENGINEER · SOUND ERRADICATOR\nEngineers receive Sound Erradicator: they can throw a destroyed megaphone or a Motorola phone running PUBG. On impact the device bursts, causing total deafness, nausea and Slowness VI–VIII."
            + "\n\nDIFFICULTY\nSHELLSHOCK is recorded at 150%. Because this is the newest notice, it is current over older lists that jumped directly from Dynamic 100% to Masochist 200%."),

        e("current-trident", Category.CURRENT, 990, Source.CURRENT_SIEGE,
            "TRIDENT · revisión de visor y gancho", "TRIDENT · visor and hook revision",
            "RESCATE DEL GANCHO\nUn aliado atrapado puede ser liberado disparándole al torso. La víctima también puede intentar «cortar cuerda» en el chat si dispone de un arma lo suficientemente afilada."
            + "\n\nPARRY INSTANTÁNEO\nCon el visor removido, Trident responde al primer ataque cuerpo a cuerpo con un parry instantáneo que mutila al atacante. Esta conducta no se atribuye al estado con visor puesto."
            + "\n\nVISOR PUESTO\n40% de resistencia a todo daño, inmunidad a disparos a la cabeza y mayor dificultad para que el gancho atrape al objetivo."
            + "\n\nVISOR REMOVIDO\n10% de resistencia, vulnerabilidad a headshots y flashbangs, acceso al parry instantáneo y gancho más fácil de conectar, con menor resistencia de arrastre y mayor velocidad al traer a la víctima.",
            "HOOK RESCUE\nA hooked ally can be freed by shooting their torso. The victim may also attempt the 'cut rope' chat cast if carrying a sufficiently sharp weapon."
            + "\n\nINSTANT PARRY\nWith the visor removed, Trident answers the first incoming melee attack with an instant parry that mutilates the attacker. This behaviour is not assigned to the visor-on state."
            + "\n\nVISOR ON\n40% resistance to all damage, immunity to headshots and a harder hook acquisition."
            + "\n\nVISOR REMOVED\n10% resistance, vulnerability to headshots and flashbangs, instant-parry access and an easier hook with lower pull resistance and faster reel speed."),

        e("current-fusilier", Category.CURRENT, 980, Source.CURRENT_SIEGE,
            "FUSILIER · artillería expandida", "FUSILIER · expanded artillery",
            "GRANADA NUCLEAR\nNueva munición con una ventana aproximada de seis segundos para reaccionar. Recibir el impacto demasiado cerca produce una muerte de tipo mutilación."
            + "\n\nSTRAFING Y FUEGO\nSu esquive lateral fue mejorado y ahora dispone de granadas incendiarias de área."
            + "\n\nMODO MORTERO\nPuede disparar al cielo y controlar las zonas de caída de sus proyectiles, incluyendo varios tipos de munición y granadas nucleares. Su barra de jefe se torna blanca mientras este modo está activo."
            + "\n\nBLOX DRINK\nAl consumir la bebida su visión se vuelve borrosa, pero bajan los cooldowns y aumentan la velocidad de recarga y disparo del lanzagranadas. Un explosivo bien sincronizado puede impedir que termine de beberla.",
            "NUCLEAR GRENADE\nNew ammunition with an approximately six-second reaction window. Taking the impact too close results in a mutilation-type death."
            + "\n\nSTRAFING AND FIRE\nIts evasive strafing was improved and it now has area incendiary grenades."
            + "\n\nMORTAR MODE\nIt can fire skyward and control projectile landing zones, including multiple ammunition types and nuclear grenades. Its boss bar turns white while this mode is active."
            + "\n\nBLOX DRINK\nAfter drinking it, its vision becomes blurred, but cooldowns fall and grenade-launcher reload/fire speed increase. A correctly timed explosive can stop the drink from being completed."),

        e("current-sparta", Category.CURRENT, 970, Source.CURRENT_SIEGE,
            "SPARTA · Shockwave y combate aéreo", "SPARTA · Shockwave and aerial combat",
            "SHOCKWAVE\nSparta recibió Shockwave."
            + "\n\nSTEADFAST\nSu modo Steadfast ahora deja una ventana de endlag más larga. El aviso también aclara que su administración de habilidades hace difícil convertir esa ventana en una eliminación sencilla."
            + "\n\nRESPUESTA AÉREA\nSi un jugador lo ataca a distancia mientras planea, Sparta puede glidear y lanzar cortes en el aire. No se agregan aquí estadísticas que no estén presentes en el anuncio.",
            "SHOCKWAVE\nSparta received Shockwave."
            + "\n\nSTEADFAST\nSteadfast now leaves a longer endlag window. The notice also states that its ability management makes that opening difficult to convert into an easy kill."
            + "\n\nAERIAL RESPONSE\nIf a player attacks from range while gliding, Sparta can glide and throw aerial slashes. No statistics absent from the announcement are added here."),

        e("current-fauna", Category.CURRENT, 960, Source.CURRENT_SIEGE,
            "FAUNA · nueva unidad Élite", "FAUNA · new Elite unit",
            "CLASIFICACIÓN\nÉlite de especie Cloaker Infiltrator. Puede ser llamada mediante Nexus Rally de Gaia o radios de jefes."
            + "\n\nEQUIPO CONFIRMADO\nRPK-74, gafas de visión tridimensional, exoesqueleto avanzado capaz de hacer bypass a Compound V y una grabadora Hallucinator."
            + "\n\nHALLUCINATOR\nLa grabadora puede producir hasta tres clones señuelo y reproducir sonidos de ataques falsos para atraer o desviar a los jugadores."
            + "\n\nDOSSIER\nNo se crea todavía una ficha Intel ilustrada: el aviso no aporta HP, DEF ni una imagen identificada con suficiente certeza. La información oficial queda preservada aquí sin inventar campos faltantes.",
            "CLASSIFICATION\nElite Cloaker Infiltrator species. It can be called by Gaia's Nexus Rally or boss radios."
            + "\n\nCONFIRMED LOADOUT\nRPK-74, three-dimensional vision goggles, an advanced exoskeleton capable of bypassing Compound V and a Hallucinator recorder."
            + "\n\nHALLUCINATOR\nThe recorder can produce up to three decoy clones and play fake attack sounds to lure or misdirect players."
            + "\n\nDOSSIER\nAn illustrated Intel card is not created yet: the notice provides no HP, DEF or confidently identified portrait. Official information is preserved here without inventing missing fields."),

        e("operation-exodus", Category.MISSIONS, 900, Source.SIEGE_ARCHIVE,
            "OPERATION EXODUS", "OPERATION EXODUS",
            "EVENTO CATASTRÓFICO\nOperation Exodus fue anunciada como un evento de incidentes múltiples cuya probabilidad de aparición se habilita en dificultades superiores a Dynamic 100%."
            + "\n\nSUPER UNITS\nSAURON · 23.400.000 HP.\nHEDALUS · 23.400.000 HP."
            + "\n\nLÍMITE DEL REGISTRO\nNo se atribuyen armamento, DEF ni habilidades adicionales porque el anuncio conservado no las detalla.",
            "CATASTROPHIC EVENT\nOperation Exodus was announced as a multi-incident event whose spawn chance becomes available above Dynamic 100% difficulty."
            + "\n\nSUPER UNITS\nSAURON · 23,400,000 HP.\nHEDALUS · 23,400,000 HP."
            + "\n\nRECORD LIMIT\nNo weapons, DEF or additional abilities are assigned because the preserved announcement does not specify them."),

        e("endless-inferno", Category.MISSIONS, 890, Source.SIEGE_ARCHIVE,
            "ENDLESS INFERNO", "ENDLESS INFERNO",
            "MISIÓN\nModo de combate sin fin contra unidades en mapas completamente abiertos. Conserva la libertad de construcción y el inventario; el anuncio enfatiza el trabajo en equipo por el aumento de inteligencia de las unidades dentro del modo."
            + "\n\nMAPAS REGISTRADOS\nGreat Pyramid of Giza.\nBramblewick."
            + "\n\nARCHIVO VISUAL\nLas capturas conservadas muestran un entorno piramidal de gran escala y un mapa abierto de valles verdes. No se infieren oleadas, spawns ni objetivos adicionales a partir de las imágenes.",
            "MISSION\nEndless combat against units on fully open maps. Building freedom and inventory are retained; the announcement stresses teamwork because unit intelligence is increased in this mode."
            + "\n\nRECORDED MAPS\nGreat Pyramid of Giza.\nBramblewick."
            + "\n\nVISUAL ARCHIVE\nPreserved captures show a large pyramid environment and an open green-valley map. Waves, spawns and additional objectives are not inferred from the images."),

        e("third-justice", Category.EQUIPMENT, 880, Source.SIEGE_ARCHIVE,
            "THIRD JUSTICE · rareza Eternal", "THIRD JUSTICE · Eternal rarity",
            "CLASIFICACIÓN\nHerramienta de parry asociada a Daemonium Ore Metal Gear. Rareza Eternal."
            + "\n\nFUNCIÓN REGISTRADA\nUtilidad de parry a largo plazo y +2 perks mientras está equipada en el inventario."
            + "\n\nLÍMITE\nLa captura de tooltip se conserva como referencia visual, pero no se transcriben efectos que no puedan leerse con seguridad.",
            "CLASSIFICATION\nParry tool associated with Daemonium Ore Metal Gear. Eternal rarity."
            + "\n\nRECORDED FUNCTION\nLong-term parry utility and +2 perks while equipped in the inventory."
            + "\n\nLIMIT\nThe tooltip capture is retained as visual reference, but effects that cannot be read confidently are not transcribed."),

        e("equipment-wave", Category.EQUIPMENT, 870, Source.SIEGE_ARCHIVE,
            "Vanguard Sword, HOLO-Watch, Riflator y Aerorig", "Vanguard Sword, HOLO-Watch, Riflator and Aerorig",
            "MEDIUM FREQUENCY VANGUARD SWORD\nRecibió mejoras no especificadas en el aviso conservado."
            + "\n\nHOLO-WATCH\nSe añadió el HOLO-Watch, identificado en el anuncio con la referencia «Megamente». No se inventa su función."
            + "\n\nRIFLATOR\nFue añadido y su daño se modificó de 50 a 130."
            + "\n\nAERORIG\nSe añadió el dispositivo de vuelo Aerorig. En el mismo bloque histórico se redujo la velocidad de las balas de los Gunners.",
            "MEDIUM FREQUENCY VANGUARD SWORD\nReceived improvements not specified in the preserved notice."
            + "\n\nHOLO-WATCH\nThe HOLO-Watch was added, identified by the announcement with the 'Megamind' reference. Its function is not invented."
            + "\n\nRIFLATOR\nIt was added and its damage changed from 50 to 130."
            + "\n\nAERORIG\nThe Aerorig flight device was added. The same historical block reduced Gunner bullet speed."),

        e("death-states", Category.CASUALTY, 850, Source.DVN_REFERENCE,
            "Estados de caída · referencia Dummies vs Noobs", "Downed states · Dummies vs Noobs reference",
            "MODELO VERIFICADO\nLa referencia principal de Dummies vs Noobs documenta tres estados de caída: Downed, Mangled y Mutilated. Se separan de efectos visuales o etiquetas de causa de muerte."
            + "\n\nDOWNED / MUERTO\nEstado común. El Defibrillator estándar tarda aproximadamente 1,5 s en revivirlo. Una reanimación consume un uso del dispositivo; el Defibrillator estándar dispone de dos cargas por oleada."
            + "\n\nMANGLED\nEstado más severo. El Defibrillator estándar tarda aproximadamente 4 s, pero sigue siendo una reanimación: no se documenta un coste especial de dos cargas. Medical Bow no puede revivir Mangled; Pacemaker ignora la penalización de Mangled y tarda alrededor de 1,5 s."
            + "\n\nMUTILATED\nEl Defibrillator estándar no puede revivir este estado. En la referencia de DvN, la recuperación normal espera al siguiente intermedio/respawn; existen excepciones especiales que funcionan como respawn, no como la reanimación normal del desfibrilador."
            + "\n\nREGLA PARA SIEGE\nNo se asignan dos cargas a Mutilated porque esa regla no está respaldada por la referencia DvN consultada. Si SIEGE implementa una variante propia, debe registrarse como regla propia y no como dato de DvN.",
            "VERIFIED MODEL\nThe primary Dummies vs Noobs reference documents three downed states: Downed, Mangled and Mutilated. They are distinct from visual death effects or cause-of-death labels."
            + "\n\nDOWNED / DEAD\nCommon state. A standard Defibrillator takes about 1.5 s to revive it. A revive consumes one use; the standard Defibrillator has two charges per wave."
            + "\n\nMANGLED\nMore severe state. A standard Defibrillator takes about 4 s, but it remains one revive; no special two-charge cost is documented. Medical Bow cannot revive Mangled; Pacemaker ignores the Mangled penalty and takes about 1.5 s."
            + "\n\nMUTILATED\nA standard Defibrillator cannot revive this state. In the DvN reference, normal recovery waits for the next intermission/respawn; special exceptions behave like respawning rather than a normal defibrillator revive."
            + "\n\nSIEGE RULE\nNo two-charge cost is assigned to Mutilated because that rule is not supported by the DvN reference consulted. If SIEGE implements its own variant, it must be documented as a SIEGE rule rather than a DvN fact."),

        e("death-labels", Category.CASUALTY, 840, Source.DVN_REFERENCE,
            "Burnt, Disfigured y Erased · clasificación", "Burnt, Disfigured and Erased · classification",
            "NO SON LOS TRES ESTADOS PRINCIPALES VERIFICADOS\nBurnt, Disfigured y Erased pueden aparecer como descripciones, efectos de muerte o términos de contenido relacionado, pero la referencia principal de DvN consultada no los enumera junto a Downed/Mangled/Mutilated como estados estándar de reanimación."
            + "\n\nUSO EN SIEGE\nSe pueden conservar como etiquetas de condición o resultado cuando el servidor las confirme, pero no reciben automáticamente tiempo ni coste de desfibrilador. Esto evita inventar reglas de revive."
            + "\n\nERASED\nDebe tratarse especialmente como estado terminal/descriptivo sólo cuando SIEGE lo defina de forma explícita; no se equipara a Mutilated por inferencia."
            + "\n\nBURNT / DISFIGURED\nPueden describir el estado del cuerpo o el tipo de muerte. Tampoco sustituyen por sí solos la clasificación de reanimación.",
            "NOT PART OF THE THREE VERIFIED MAIN STATES\nBurnt, Disfigured and Erased can appear as descriptions, death effects or related-content terms, but the primary DvN reference consulted does not list them beside Downed/Mangled/Mutilated as standard revive states."
            + "\n\nUSE IN SIEGE\nThey can be retained as condition/outcome labels when the server confirms them, but they do not automatically receive a defibrillator time or charge cost. This avoids inventing revive rules."
            + "\n\nERASED\nTreat it as a terminal/descriptive state only when SIEGE explicitly defines it; do not infer that it equals Mutilated."
            + "\n\nBURNT / DISFIGURED\nThese can describe corpse condition or death type. They do not by themselves replace the revive classification."),

        e("proteus-shielders", Category.UNITS, 800, Source.SIEGE_ARCHIVE,
            "PROTEUS y Shielders", "PROTEUS and Shielders",
            "PROTEUS\nSe añadió Proteus como Élite W.I.P.; el aviso no aporta todavía estadísticas suficientes para una ficha oficial completa."
            + "\n\nSHIELDERS\nSe corrigió un comportamiento roto de los Shielders. No se atribuye aquí un cambio numérico no publicado.",
            "PROTEUS\nProteus was added as a W.I.P. Elite; the notice does not yet provide enough statistics for a complete official dossier."
            + "\n\nSHIELDERS\nBroken Shielder behaviour was fixed. No unpublished numeric change is assigned here."),

        e("cerberus", Category.UNITS, 790, Source.SIEGE_ARCHIVE,
            "CERBERUS · Boss → Élite", "CERBERUS · Boss → Elite",
            "RECLASIFICACIÓN\nCerberus pasó de Boss a unidad Élite."
            + "\n\nREWORK\nEl anuncio registra cambios masivos en ATK, DEF y habilidades, sin valores suficientes para reproducir números aquí."
            + "\n\nAUDIO\nSe introdujeron temas de underscore/highscore basados en ULTRAKILL · Tenebre Rosso Sangue. Este archivo sólo registra el anuncio; no añade música al soundtrack del menú sin el audio correspondiente.",
            "RECLASSIFICATION\nCerberus moved from Boss to Elite unit."
            + "\n\nREWORK\nThe announcement records major ATK, DEF and skill changes without enough values to reproduce numbers here."
            + "\n\nAUDIO\nUnderscore/highscore themes based on ULTRAKILL · Tenebre Rosso Sangue were introduced. This archive records the notice only; menu soundtrack audio is not added without the corresponding asset."),

        e("roster-expansion", Category.UNITS, 780, Source.SIEGE_ARCHIVE,
            "Expansión de roster · Agitator, Informant, Jagant y soporte", "Roster expansion · Agitator, Informant, Jagant and support",
            "REGISTROS\nSe anunciaron Agitator (Tank), Informant (Unidad), Jagant (Tank), Grappler (soporte), Tranquilizer (soporte), Skydiver y Skyliner."
            + "\n\nNPCS Y PATHFINDING\nUn bloque histórico también registra mejor pathfinding para NPCs jugadores, menos probabilidad de NPCs barderos y que esos NPCs dejan de escalar con la dificultad."
            + "\n\nOTROS AJUSTES\nLa visión de calor de Magispeller Siniestro aumentó de alcance; Paul Bear recibió self-heal pasivo; se corrigió música que se detenía en Cherry Blossoms; Fusilier recibió predicción de movimiento y sprint; se corrigieron Infantries que quedaban congelados."
            + "\n\nPRIORIDAD\nCuando este bloque contradiga un aviso posterior de Fusilier u otra unidad, prevalece el aviso más reciente de este archivo 0.40.",
            "RECORDS\nAgitator (Tank), Informant (Unit), Jagant (Tank), Grappler (support), Tranquilizer (support), Skydiver and Skyliner were announced."
            + "\n\nNPCS AND PATHFINDING\nA historical block also records improved player-NPC pathfinding, fewer disruptive NPCs and player NPCs no longer scaling with difficulty."
            + "\n\nOTHER ADJUSTMENTS\nSinister Magispeller heat-vision range increased; Paul Bear received passive self-heal; music stopping in Cherry Blossoms was fixed; Fusilier received movement prediction and sprint; frozen Infantry AI was fixed."
            + "\n\nPRIORITY\nWhenever this block conflicts with a later Fusilier or other-unit notice, the newer 0.40 archive notice wins."),

        e("old-unit-wave", Category.HISTORY, 700, Source.SIEGE_ARCHIVE,
            "Cambios históricos de unidades", "Historical unit changes",
            "REGISTROS EN ORDEN DESCENDENTE\nCloakers: probabilidad media de aparecer con Parry; proyectiles de Gunner se hicieron visibles.\nEspecialist: habilidad Stop Time.\nSe registró un rework completo de unidades especiales, Bosses, Elites y Tanks.\nGunner y Agreement fueron añadidos en un bloque anterior."
            + "\n\nUSO\nEstos avisos se conservan para contexto histórico. No sustituyen datos posteriores ni crean por sí solos estadísticas actuales.",
            "RECORDS IN DESCENDING ORDER\nCloakers: medium chance to spawn with Parry; Gunner projectiles became visible.\nSpecialist: Stop Time ability.\nA full rework of special units, Bosses, Elites and Tanks was recorded.\nGunner and Agreement were added in an earlier block."
            + "\n\nUSE\nThese notices are preserved for historical context. They do not replace later data or create current statistics by themselves."),

        e("twilight-grade2", Category.HISTORY, 690, Source.SIEGE_ARCHIVE,
            "Twilight Forest · Grado 2", "Twilight Forest · Grade 2",
            "REGISTRO\nSe anunció oficialmente el Grado 2 de Twilight Forest. No se acompañó en el material conservado de reglas, requisitos o recompensas suficientes para ampliarlo sin especular.",
            "RECORD\nTwilight Forest Grade 2 was officially announced. The preserved material does not include enough rules, requirements or rewards to expand it without speculation."),

        e("stylish-rank", Category.HISTORY, 680, Source.SIEGE_ARCHIVE,
            "Stylish Battle Rank · rework", "Stylish Battle Rank · rework",
            "REGISTRO\nSe anunció un rework del sistema Stylish Rank y, en otro bloque, su reimplementación para algunas armas. Las capturas conservadas prueban la existencia visual del rework, pero el material no define aquí una fórmula completa de puntuación."
            + "\n\nREGLA\nNo se inventan multiplicadores, letras, umbrales o recompensas que no estén expresados en el material de origen.",
            "RECORD\nA Stylish Rank system rework was announced and another block records its reimplementation for some weapons. Preserved captures establish the visual existence of the rework, but the material here does not define a complete scoring formula."
            + "\n\nRULE\nNo multipliers, letters, thresholds or rewards absent from the source material are invented.")
    );

    private static final List<Entry> SORTED = ENTRIES.stream()
            .sorted(Comparator.comparingInt(Entry::rank).reversed())
            .toList();

    static {
        int previous = Integer.MAX_VALUE;
        java.util.HashSet<String> ids = new java.util.HashSet<>();
        for (Entry entry : ENTRIES) {
            if (!ids.add(entry.id())) throw new IllegalStateException("Duplicate archive id: " + entry.id());
            if (entry.rank() >= previous) throw new IllegalStateException("Archive must remain newest-to-oldest: " + entry.id());
            previous = entry.rank();
        }
        if (!ENTRIES.get(0).id().equals("current-core") || !ENTRIES.get(1).id().equals("current-trident"))
            throw new IllegalStateException("Newest SIEGE announcement priority changed");
    }

    public static List<Entry> entries(Category category, String query, boolean spanish) {
        var matcher = IntelSearch.compile(query);
        return SORTED.stream()
                .filter(e -> e.category() == category)
                .filter(e -> matcher.test(e.searchable(spanish)))
                .toList();
    }

    public static List<Entry> all() { return SORTED; }
    public static int total() { return ENTRIES.size(); }
}
