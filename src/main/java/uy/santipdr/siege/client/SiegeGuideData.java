package uy.santipdr.siege.client;

import java.util.List;

/** Client-side reference only. No inventory items, race rolls or server rules are changed. */
public final class SiegeGuideData {
    private SiegeGuideData() { }
    public enum Category {
        ITEMS("OBJETOS", "ITEMS"), LORE("LORE", "LORE"), DIFFICULTY("DIFICULTAD", "DIFFICULTY"),
        CHRONICLES("CRÓNICAS", "CHRONICLES"), INSPIRATIONS("INSPIRACIÓN", "INSPIRATION");
        private final String es, en;
        Category(String es, String en) { this.es = es; this.en = en; }
        public String title(boolean spanish) { return spanish ? es : en; }
    }
    public record Art(String file, int width, int height, String es, String en) {
        public String caption(boolean spanish) { return spanish ? es : en; }
    }
    public record Entry(String id, Category category, String titleEs, String titleEn,
                        String bodyEs, String bodyEn, List<Art> images, boolean spoiler) {
        public String title(boolean spanish) { return spanish ? titleEs : titleEn; }
        public String body(boolean spanish) { return spanish ? bodyEs : bodyEn; }
    }
    private static Entry text(String id, Category c, String es, String en, String bodyEs, String bodyEn) {
        return new Entry(id, c, es, en, bodyEs, bodyEn, List.of(), false);
    }
    private static Entry chronicle(String id, String name, String es, String en) {
        return new Entry(id, Category.CHRONICLES, name, name,
                "REGISTRO NARRATIVO\n" + es + "\n\nFUENTE\nCanal Summary aportado por el usuario. Es un desenlace del relato, no el estado de una entidad en tu partida.",
                "NARRATIVE RECORD\n" + en + "\n\nSOURCE\nSummary channel supplied by the user. This is a story outcome, not the state of an entity in your session.", List.of(), true);
    }
    private static Entry inspiration(String id, String title, String file, int w, int h, boolean main) {
        return new Entry(id, Category.INSPIRATIONS, title, title,
                (main ? "INSPIRACIÓN PRINCIPAL" : "INSPIRACIÓN DECLARADA")
                + "\n" + title + " figura entre las referencias de SIEGE indicadas en su presentación."
                + "\n\nCRÉDITOS\nImagen aportada por el usuario. Las obras, nombres y material visual pertenecen a sus respectivos titulares. Su inclusión como referencia no implica afiliación oficial ni que todas sus mecánicas estén presentes en SIEGE.",
                (main ? "PRIMARY INSPIRATION" : "DECLARED INSPIRATION")
                + "\n" + title + " is listed among SIEGE's references in its introduction."
                + "\n\nCREDITS\nImage supplied by the user. Works, names and artwork belong to their respective owners. Inclusion as a reference does not imply official affiliation or that all their mechanics are present in SIEGE.",
                List.of(new Art(file, w, h, title, title)), false);
    }

    public static final List<Entry> ENTRIES = List.of(
        new Entry("race-reroll", Category.ITEMS, "Giro de Raza", "Race Reroll",
            "FUNCIÓN\nSustituye la raza actual del personaje por otra elegida al azar. El resultado se revela al finalizar el giro; no puede elegirse previamente."
            + "\n\nOBTENCIÓN\nArtículo únicamente obtenible mediante Shadow [NPC], según la información aportada. No se especificaron precio, ubicación ni requisitos adicionales."
            + "\n\nCADA GIRO ES INDEPENDIENTE\nPuede volver a salir una raza que ya obtuviste. Fallar varias veces no garantiza conseguir la raza buscada en el próximo intento. No hay secuencias que permitan predecir con certeza el resultado."
            + "\n\nSUERTE Y BLOODLUCK\nLos giros no se ven afectados por reliquias que otorguen Suerte o Bloodluck. Las posibilidades dependen de la rareza y configuración de cada raza."
            + "\n\nPOSIBILIDADES VARIABLES\nEl conjunto de razas puede cambiar con actualizaciones, configuración o condiciones especiales. No se publicaron aquí una lista completa ni porcentajes por raza."
            + "\n\nREGLAMENTO\nEl sistema se describe como completamente RNG, sin resultados específicos garantizados. Esta guía no realiza giros ni altera sus probabilidades."
            + "\n\nCAPTURA DEL OBJETO\nLa captura muestra Original Cost: 2, Spell Type: Geomancy y uso en mano o bastón. Se conservan como texto observado, sin asumir que 2 sea el precio de Shadow, una probabilidad o una estadística actual."
            + "\n\nAUTORÍA\nTexto y reglas atribuidos a @romax141403.",
            "FUNCTION\nReplaces the character's current race with a randomly selected race. The outcome is revealed when the roll ends and cannot be chosen beforehand."
            + "\n\nACQUISITION\nOnly obtainable through Shadow [NPC], according to the supplied information. Price, location and additional requirements were not specified."
            + "\n\nEACH ROLL IS INDEPENDENT\nA previously obtained race can appear again. Repeated failures do not guarantee the desired race on the next attempt. No sequence can predict the outcome with certainty."
            + "\n\nLUCK AND BLOODLUCK\nRelics granting Luck or Bloodluck do not affect race rolls. Chances depend on each race's rarity and configuration."
            + "\n\nCHANGING POSSIBILITIES\nAvailable races may change with updates, configuration or special conditions. No complete race list or individual percentages were supplied here."
            + "\n\nRULES\nThe system is described as fully RNG, without guaranteed specific outcomes. This guide does not perform rolls or change their probabilities."
            + "\n\nITEM SCREENSHOT\nThe screenshot shows Original Cost: 2, Spell Type: Geomancy and casting in hand or staff. These are preserved as observed text, not assumed to be Shadow's price, a probability or a current statistic."
            + "\n\nAUTHORSHIP\nText and rules attributed to @romax141403.",
            List.of(new Art("race_reroll.png", 1360, 768, "Giro de Raza · captura aportada", "Race Reroll · supplied screenshot"),
                    new Art("race_reroll_tooltip.png", 353, 158, "Ficha del objeto · captura aportada", "Item tooltip · supplied screenshot")), false),
        text("world-2044", Category.LORE, "2044 · Guerra como negocio", "2044 · War as a business",
            "CONTEXTO\nEn el año 2044, los gobiernos han convertido las guerras en un negocio. Corporaciones privadas controlan ejércitos de soldados y robots, mientras la vida humana queda en segundo plano. Terrorismo, secuestros de niños y ocupación militar se extienden por el mundo."
            + "\n\nENEMIGOS\nNusia, Desperado Enforcement LLC, World Marshal Inc y SCP Foundation persiguen al jugador y a quienes se oponen a sus planes. Hay más organizaciones hostiles."
            + "\n\nRESISTENCIA\nDominia, el país al que pertenece el jugador al comenzar este relato, es presentado como el único que continúa en pie. Sobrevivir exige adaptar las armas, perfeccionar las tácticas y proteger al equipo.",
            "CONTEXT\nIn 2044, governments have turned warfare into a business. Private corporations control armies of soldiers and robots while human life becomes secondary. Terrorism, child abductions and military occupation spread across the world."
            + "\n\nENEMIES\nNusia, Desperado Enforcement LLC, World Marshal Inc and SCP Foundation pursue the player and those opposing their plans. Other hostile organisations also exist."
            + "\n\nRESISTANCE\nDominia, the player's country at the beginning of this account, is presented as the only one still standing. Survival requires adapting weapons, refining tactics and protecting the team."),
        text("dominians", Category.LORE, "Dominia y los Dominianos", "Dominia and the Dominians",
            "TERRITORIO\nNativos del sureste de Erikase, habitan las montañas frías y las tierras bajas húmedas del sur. El aislamiento geográfico los separó del resto del continente."
            + "\n\nSOCIEDAD\nLa presentación describe una sociedad predominantemente militarista y una población de apariencia mayormente homogénea. El idioma dominiano deriva de una lengua antigua de Erikase; compartir palabras y estructuras semejantes tiene relevancia en la carrera militar."
            + "\n\nTECNOLOGÍA\nCiviles con alto potencial de armamento, fabricantes de munición, impresoras de armas y torretas PDC automatizadas. Aun así, sus enemigos los superan en inteligencia artificial, tácticas, infraestructura espacial y población militar."
            + "\n\nLÍMITE NAVAL\nAunque tienen acceso a los mares del sur, no poseen una fuerza naval significativa: permanecen congelados la mayor parte del año y sólo están disponibles durante los meses cálidos del verano.",
            "TERRITORY\nNative to southeastern Erikase, they inhabit cold mountains and damp southern lowlands. Geographic isolation separated them from the rest of the continent."
            + "\n\nSOCIETY\nThe introduction describes a predominantly militarist society and a largely homogeneous population in appearance. The Dominian language descends from an ancient Erikase language; shared words and similar sentence structures have relevance in military careers."
            + "\n\nTECHNOLOGY\nCivilians with considerable weapons potential, ammunition fabricators, weapon printers and automated PDC turrets. Despite this technology, enemies surpass them in artificial intelligence, tactics, space infrastructure and military population."
            + "\n\nNAVAL LIMITATION\nDespite access to the southern seas, they lack a significant navy: these waters stay frozen for most of the year and are available only in the warm summer months."),
        text("stronghold", Category.LORE, "Stronghold 5-5", "Stronghold 5-5",
            "TU EQUIPO\nStronghold 5-5 se encuentra varado entre islas, ciudades, salares y zonas metropolitanas. Debe defenderse de hordas con armamento moderno en un apocalipsis de guerra, bandos, raids y caos."
            + "\n\nSUPERVIVENCIA\nAdaptá tus armas, perfeccioná tus tácticas y protegé a tus compañeros. La presentación indica que contarás con recursos y podrás revivir al equipo si cae. No especifica aquí tiempos, teclas ni límites del rescate. No esperes nada fácil.",
            "YOUR TEAM\nStronghold 5-5 is stranded across islands, cities, salt flats and metropolitan areas. It must face hordes using modern weapons in an apocalypse of war, factions, raids and chaos."
            + "\n\nSURVIVAL\nAdapt your weapons, refine your tactics and protect your teammates. The introduction says resources will be available and fallen teammates can be revived. It does not specify rescue timings, keys or limits here. Do not expect an easy fight."),
        text("server-rules", Category.LORE, "Vidas y condiciones del servidor", "Lives and server conditions",
            "VIDAS\nContás con vidas: no las malgastes. La cantidad no se especificó en esta fuente."
            + "\n\nCIVILIZACIONES\nPodés crear tu propio país y civilización. Dominia sigue siendo el país de origen descrito por la introducción."
            + "\n\nVERSIÓN\nMinecraft Forge 1.20.1. End y Nether deshabilitados según la presentación del servidor."
            + "\n\nALCANCE DE ESTA GUÍA\nEstas son reglas e información del servidor aportadas por el usuario. El mod de menú no bloquea dimensiones, no administra vidas y no impone estas reglas en mundos locales.",
            "LIVES\nYou have limited lives: do not waste them. This source does not specify their number."
            + "\n\nCIVILISATIONS\nYou can create your own country and civilisation. Dominia remains the origin country described in the introduction."
            + "\n\nVERSION\nMinecraft Forge 1.20.1. The End and Nether are disabled according to the server introduction."
            + "\n\nGUIDE SCOPE\nThese are server rules and information supplied by the user. This menu mod does not block dimensions, manage lives or impose these rules on local worlds."),
        text("difficulty-levels", Category.DIFFICULTY, "Dificultades del servidor", "Server difficulties",
            "CASUAL · 50%\nRecomendada para nuevos jugadores."
            + "\n\nCONSCRIPTO · 75%\nRecomendada para quienes prefieren lanzarse directamente a la acción."
            + "\n\nDINÁMICO / DYNAMIC · 100%\nEl desafío de referencia. Algunos enemigos ya pueden eliminarte de un solo golpe."
            + "\n\nMASOQUISTA / MASOCHIST · 200%\nReto superior a Dynamic, con cambios importantes en jefes y unidades."
            + "\n\nHELL · 300%\nEn desarrollo según la información recibida."
            + "\n\nLECTURA DE PORCENTAJES\nSon los valores de dificultad publicados. No se indicó que multipliquen de forma idéntica la vida, el daño u otras estadísticas. Esta pantalla es informativa: no cambia la dificultad del servidor.",
            "CASUAL · 50%\nRecommended for new players."
            + "\n\nCONSCRIPTO / CONSCRIPT · 75%\nRecommended for players who prefer to jump straight into the action."
            + "\n\nDINÁMICO / DYNAMIC · 100%\nThe reference challenge. Some enemies can already eliminate you in one hit."
            + "\n\nMASOQUISTA / MASOCHIST · 200%\nA challenge above Dynamic, with major changes to bosses and units."
            + "\n\nHELL · 300%\nUnder development according to the supplied information."
            + "\n\nREADING THE PERCENTAGES\nThese are the published difficulty values. The source does not say they multiply health, damage or other statistics identically. This screen is informational and does not change server difficulty."),
        new Entry("skull-order", Category.DIFFICULTY, "Orden de las calaveras", "Skull order",
            "ESCALA PUBLICADA\nEl orden indicado es: skullsitox → littleskull → natural → twoskulls → skullclosedmouth → skullopen. Los nombres corresponden a los emojis personalizados de Discord, no a seis niveles de dificultad del servidor."
            + "\n\nCAPTURA ORIGINAL\nLa imagen también muestra una calavera dorada adicional, pero este texto no define su equivalencia. No se le asigna automáticamente un porcentaje ni se recalifican las tropas existentes."
            + "\n\nDOS ESCALAS DISTINTAS\nLa clasificación de unidades por calaveras y las dificultades Casual–HELL se presentan por separado: la fuente no proporciona una conversión entre ellas.",
            "PUBLISHED SCALE\nThe stated order is: skullsitox → littleskull → natural → twoskulls → skullclosedmouth → skullopen. These are custom Discord emoji names, not six server difficulty levels."
            + "\n\nORIGINAL SCREENSHOT\nThe image also shows an additional golden skull, but this text does not define its equivalent. No percentage is automatically assigned and existing troops are not re-rated."
            + "\n\nTWO DIFFERENT SCALES\nUnit skull rankings and Casual–HELL difficulty levels are presented separately: the source provides no conversion between them.",
            List.of(new Art("skull_order.png", 1280, 600, "Orden de calaveras · fuente aportada", "Skull order · supplied source")), false),
        chronicle("ending-tempest", "TEMPEST", "29 de septiembre de 2044. Fue encontrado sin vida en aguas poco profundas. El agua destruyó gran parte de la evidencia. El relato considera probable que muriera por heridas de rifle y pérdida de sangre, sin una conclusión forense definitiva.", "29 September 2044. Found dead in shallow water. Water destroyed much of the evidence. The account considers rifle wounds and blood loss a likely cause, without a definitive forensic conclusion."),
        chronicle("ending-fusilier", "FUSILIER", "2 de junio de 2044. Murió en una explosión. Según los peritos del relato, su propio lanzagranadas explotó después de recibir numerosos impactos de bala.", "2 June 2044. Killed in an explosion. The account's forensic examiners attributed it to his own grenade launcher detonating after numerous bullet hits."),
        chronicle("ending-achilles", "ACHILLES", "15 de octubre de 2044. Murió por un impacto devastador en la cabeza, atribuido de forma presunta a una bala de tungsteno de un rifle antimaterial. El relato sospecha que el tirador rival actuó antes que él.", "15 October 2044. Died from a devastating head impact, presumed to be a tungsten round from an anti-materiel rifle. The account suspects the opposing shooter acted first."),
        chronicle("ending-trident", "TRIDENT", "7 de marzo de 2044. Fue encontrado muerto con lesiones atribuidas a una Muramasa de alta frecuencia. El relato vincula al atacante con Desperado Enforcement y señala que Trident perdió la visión al inicio del encuentro. Los restos fueron vendidos a terceros.", "7 March 2044. Found dead with injuries attributed to a high-frequency Muramasa. The account links the attacker to Desperado Enforcement and says Trident lost his sight early in the encounter. His remains were sold to third parties."),
        chronicle("ending-gaia", "GAIA", "2 de mayo de 2044. Fue encontrado muerto junto a varios nusianos no identificados. El reporte describe violencia extrema, abuso y lesiones por armas blancas y fuego. Se conserva aquí un resumen no gráfico del desenlace.", "2 May 2044. Found dead alongside several unidentified Nusians. The report describes extreme violence, abuse, and injuries involving blades and fire. This entry preserves a non-graphic summary of the outcome."),
        chronicle("ending-hermes", "HERMES", "18 de agosto de 2044. Se encontraron restos del helicóptero con quemaduras y un ala dañada por una caída descontrolada. Se cree que el piloto perdió el control tras sufrir daños graves. No se encontró su cuerpo; esa ausencia no confirma por sí sola su muerte.", "18 August 2044. Helicopter wreckage showed burns and wing damage from an uncontrolled descent. The pilot is believed to have lost control after severe damage. The pilot's body was not found; that absence alone does not confirm death."),
        chronicle("ending-twins", "THE TWINS", "Sin fecha indicada. Las ventanas blindadas de los helicópteros habrían sido perforadas por un armamento no identificado antes de caer. Sólo se encontraron piezas mecánicas dispersas. Este registro no añade una tropa jugable ni inventa estadísticas.", "No date supplied. The helicopters' armoured windows were reportedly pierced by unidentified weaponry before they fell. Only scattered mechanical parts were found. This record does not add a combat unit or invent statistics."),
        chronicle("ending-agares", "AGARES", "15 de abril de 2044. Fue encontrado muerto. El relato concluye que su dispositivo defensivo fue saboteado y atribuye su derrota a un grupo de Dominianos blindados con armas blancas.", "15 April 2044. Found dead. The account concludes that his defensive device was sabotaged and attributes his defeat to a group of armoured Dominians using bladed weapons."),
        chronicle("ending-ghost", "GHOST", "1 de julio de 2044. Fue encontrado muerto con lesiones de armas de alto calibre y de animales. El relato indica que, tras quedar indefenso, fue abandonado vivo ante animales de la selva como ofrenda.", "1 July 2044. Found dead with injuries from high-calibre weapons and animals. The account says that after being left defenceless, he was offered alive to jungle animals."),
        chronicle("ending-prometheus", "PROMETHEUS", "22 de agosto de 2044. Fue encontrado muerto tras un incendio y una explosión. Los tanques de gas de su lanzallamas estaban destruidos; el reporte concluye que explotaron al recibir disparos.", "22 August 2044. Found dead after fire and an explosion. His flamethrower's gas tanks were destroyed; the report concludes that gunfire caused them to explode."),
        chronicle("ending-sparta", "SPARTA", "31 de octubre de 2044. Fue encontrado muerto, presuntamente por un sable de alta frecuencia. Se sospecha la intervención de un grupo de Dominianos. La fuente no proporciona estadísticas de combate.", "31 October 2044. Found dead, presumably killed with a high-frequency blade. A group of Dominians is suspected. The source provides no combat statistics."),
        chronicle("ending-aurelionis", "AURELIONIS", "28 de mayo de 2044. Fue derrotado por un grupo de Dominianos y logró escapar con vida, con heridas graves en los brazos y lesiones por armas blancas. El relato menciona un trauma psicológico permanente. Sus habilidades siguen sin describirse.", "28 May 2044. Defeated by a group of Dominians, he escaped alive with severe arm injuries and blade wounds. The account mentions lasting psychological trauma. His abilities remain undescribed."),
        chronicle("ending-daedalus", "DAEDALUS", "17 de marzo de 2044. El reporte indica que fue encontrado muerto, flotando cerca de la Luna. No se especifica cómo llegó allí ni una causa de muerte.", "17 March 2044. The report says he was found dead, floating near the Moon. It does not explain how he got there or give a cause of death."),
        inspiration("inspiration-dvn", "Dummies vs Noobs", "dummies_vs_noobs.png", 768, 432, true),
        inspiration("inspiration-forsaken", "Forsaken", "forsaken.png", 768, 432, false),
        inspiration("inspiration-dmc", "Devil May Cry", "devil_may_cry.png", 1024, 576, false),
        inspiration("inspiration-mgr", "Metal Gear Rising: Revengeance", "metal_gear.png", 1528, 918, false),
        inspiration("inspiration-limbus", "Limbus Company", "limbus_company.png", 807, 399, false),
        inspiration("inspiration-helldivers", "Helldivers II", "helldivers_ii.png", 1024, 576, false),
        inspiration("inspiration-frostpunk", "Frostpunk", "frostpunk.png", 950, 510, false)
    );
    public static List<Entry> entries(Category category, String query, boolean es) {
        var matcher = IntelSearch.compile(query);
        return ENTRIES.stream().filter(e -> e.category() == category)
            .filter(e -> matcher.test(e.title(es) + " " + (e.spoiler() ? "" : e.body(es)))).toList();
    }
}
