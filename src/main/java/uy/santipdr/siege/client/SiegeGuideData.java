package uy.santipdr.siege.client;

import java.util.List;

/** Client-side reference only. No inventory items, race rolls or server rules are changed. */
public final class SiegeGuideData {
    private SiegeGuideData() { }
    public enum Category {
        ITEMS("OBJETOS", "ITEMS"), LORE("LORE", "LORE"), OPERATIONS("OPERACIONES", "OPERATIONS"),
        DIFFICULTY("DIFICULTAD", "DIFFICULTY"), CHRONICLES("CRÓNICAS", "CHRONICLES"),
        INSPIRATIONS("INSPIRACIÓN", "INSPIRATION");
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
                "ARCHIVO HISTÓRICO\n" + es + "\n\nCLASIFICACIÓN\nDesenlace registrado en los archivos de campaña de 2044.",
                "HISTORICAL ARCHIVE\n" + en + "\n\nCLASSIFICATION\nOutcome recorded in the 2044 campaign archives.", List.of(), true);
    }
    private static Entry inspiration(String id, String title, String file, int w, int h, boolean main,
                                     String loreEs, String loreEn, String influenceEs, String influenceEn) {
        return new Entry(id, Category.INSPIRATIONS, title, title,
                (main ? "INSPIRACIÓN PRINCIPAL" : "INSPIRACIÓN DECLARADA")
                + "\n" + loreEs + "\n\nHUELLA EN SIEGE\n" + influenceEs,
                (main ? "PRIMARY INSPIRATION" : "DECLARED INSPIRATION")
                + "\n" + loreEn + "\n\nMARK ON SIEGE\n" + influenceEn,
                List.of(new Art(file, w, h, title, title)), false);
    }

    public static final List<Entry> ENTRIES = List.of(
        new Entry("race-reroll", Category.ITEMS, "Giro de Raza", "Race Reroll",
            "FUNCIÓN\nSustituye la raza actual del personaje por otra elegida al azar. El resultado se revela al finalizar el giro; no puede elegirse previamente."
            + "\n\nOBTENCIÓN\nArtículo únicamente obtenible mediante Shadow [NPC]. Su precio, ubicación y requisitos adicionales permanecen sin registrar."
            + "\n\nCADA GIRO ES INDEPENDIENTE\nPuede volver a salir una raza que ya obtuviste. Fallar varias veces no garantiza conseguir la raza buscada en el próximo intento. No hay secuencias que permitan predecir con certeza el resultado."
            + "\n\nSUERTE Y BLOODLUCK\nLos giros no se ven afectados por reliquias que otorguen Suerte o Bloodluck. Las posibilidades dependen de la rareza y configuración de cada raza."
            + "\n\nPOSIBILIDADES VARIABLES\nEl conjunto de razas puede cambiar con actualizaciones, configuración o condiciones especiales. No se publicaron aquí una lista completa ni porcentajes por raza."
            + "\n\nREGLAMENTO\nEl procedimiento se rige por azar genuino y no garantiza ningún resultado específico. Todo solicitante acepta esas condiciones antes de autorizar el giro."
            + "\n\nAUTORÍA\nTexto y reglas atribuidos a @romax141403.",
            "FUNCTION\nReplaces the character's current race with a randomly selected race. The outcome is revealed when the roll ends and cannot be chosen beforehand."
            + "\n\nACQUISITION\nOnly obtainable through Shadow [NPC]. Its price, location and additional requirements remain unrecorded."
            + "\n\nEACH ROLL IS INDEPENDENT\nA previously obtained race can appear again. Repeated failures do not guarantee the desired race on the next attempt. No sequence can predict the outcome with certainty."
            + "\n\nLUCK AND BLOODLUCK\nRelics granting Luck or Bloodluck do not affect race rolls. Chances depend on each race's rarity and configuration."
            + "\n\nCHANGING POSSIBILITIES\nAvailable races may change with updates, configuration or special conditions. The complete race list and individual percentages are not public."
            + "\n\nRULES\nThe procedure follows genuine chance and guarantees no specific outcome. Every applicant accepts those conditions before authorising the reroll."
            + "\n\nAUTHORSHIP\nText and rules attributed to @romax141403.",
            List.of(new Art("race_reroll.png", 1360, 768, "Manifestación del Giro de Raza", "Race Reroll manifestation"),
                    new Art("race_reroll_tooltip.png", 353, 158, "Identificación arcana del objeto", "Arcane item identification")), false),
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
            + "\n\nSUPERVIVENCIA\nAdaptá tus armas, perfeccioná tus tácticas y protegé a tus compañeros. El protocolo de Stronghold contempla suministros de supervivencia y recuperación de aliados abatidos. No esperes nada fácil.",
            "YOUR TEAM\nStronghold 5-5 is stranded across islands, cities, salt flats and metropolitan areas. It must face hordes using modern weapons in an apocalypse of war, factions, raids and chaos."
            + "\n\nSURVIVAL\nAdapt your weapons, refine your tactics and protect your teammates. Stronghold protocol provides survival supplies and recovery of fallen allies. Do not expect an easy fight."),
        text("server-rules", Category.LORE, "Vidas y condiciones del servidor", "Lives and server conditions",
            "VIDAS\nContás con vidas: no las malgastes. La cantidad asignada depende del operativo."
            + "\n\nCIVILIZACIONES\nPodés crear tu propio país y civilización. Dominia sigue siendo el país de origen descrito por la introducción."
            + "\n\nPROTOCOLO DIMENSIONAL\nEl End y el Nether permanecen clausurados por orden operativa. Forge 1.20.1 es la plataforma confirmada para el despliegue.",
            "LIVES\nYou have limited lives: do not waste them. The assigned count depends on the operation."
            + "\n\nCIVILISATIONS\nYou can create your own country and civilisation. Dominia remains the origin country described in the introduction."
            + "\n\nDIMENSIONAL PROTOCOL\nThe End and Nether remain sealed by operational order. Forge 1.20.1 is the confirmed deployment platform."),
        text("core-ai", Category.OPERATIONS, "El Núcleo", "The Core",
            "ESTADO\nEl Núcleo es la inteligencia artificial reactiva que volvió a intervenir en el teatro de guerra de 2044. No funciona como un simple registro: observa el desarrollo de las operaciones y puede activar eventos que cambian las condiciones del campo de batalla."
            + "\n\nINTERVENCIÓN\nSus eventos pueden reforzar amenazas, alterar la fuerza o el daño recibido y modificar la presión de una zona. No todos los eventos tienen el mismo alcance, duración ni objetivo."
            + "\n\nLECTURA OPERATIVA\nUna intervención del Núcleo debe tratarse como un cambio real de condiciones. Reevalúa cobertura, recursos, rutas y capacidad del equipo antes de continuar una ofensiva."
            + "\n\nLÍMITES DEL ARCHIVO\nEl expediente confirma su papel reactivo, pero no afirma que controle cada unidad ni que pueda alterar cualquier sistema sin restricciones.",
            "STATUS\nThe Core is the reactive artificial intelligence that returned to the 2044 war theatre. It is not merely an archive: it observes how operations develop and can trigger events that change battlefield conditions."
            + "\n\nINTERVENTION\nIts events can reinforce threats, alter incoming damage or enemy strength, and change pressure across an area. Not every event has the same scope, duration or purpose."
            + "\n\nOPERATIONAL READING\nA Core intervention must be treated as a real change in conditions. Reassess cover, resources, routes and squad capability before continuing an offensive."
            + "\n\nARCHIVE LIMITS\nThe file confirms its reactive role, but does not claim that it controls every unit or can alter every system without restriction."),
        text("agreement-field-report", Category.OPERATIONS, "Agreement · Reporte de campo", "Agreement · Field report",
            "DATOS CONSERVADOS\nAgreement figura con 3.000 HP, 100 DEF y un vínculo registrado con Secure Contain Protect."
            + "\n\nREPORTE SIN VERIFICAR\nUn testimonio lo compara con una versión menos avanzada de Rick Sanchez por su tecnología de portales. La comparación es informal y no implica equivalencia de poderes."
            + "\n\nRESPUESTA TÁCTICA\nEl mismo reporte propone que sabotajes repetidos, ejecutados más rápido de lo que Agreement puede responder, podrían saturarlo. No se ha demostrado que sea su única debilidad ni que garantice derrotarlo."
            + "\n\nCRITERIO\nLos datos confirmados y las hipótesis del testimonio se mantienen separados para evitar convertir una teoría de campo en una capacidad establecida.",
            "RETAINED DATA\nAgreement is recorded with 3,000 HP, 100 DEF and a documented link to Secure Contain Protect."
            + "\n\nUNVERIFIED REPORT\nA testimony compares it to a less advanced version of Rick Sanchez because of its portal technology. The comparison is informal and does not imply equivalent powers."
            + "\n\nTACTICAL RESPONSE\nThe same report proposes that repeated sabotage, performed faster than Agreement can respond, may overload it. This has not been shown to be its only weakness or a guaranteed way to defeat it."
            + "\n\nSTANDARD\nConfirmed data and testimony-based hypotheses remain separated so a field theory is not presented as an established capability."),
        text("gates-rifts", Category.OPERATIONS, "Gates y Rifts", "Gates and Rifts",
            "GATES\nEl reporte disponible los describe como teletransportadores caseros y costosos. Sirven para traslado, pero no deben confundirse con la tecnología interdimensional de los Rifts."
            + "\n\nRIFTS\nSe describen como portales de viaje interdimensional asociados a Agreement. Alcance, coste y tiempo de recarga permanecen sin confirmar."
            + "\n\nVISORES\nExiste un testimonio sin verificar según el cual portales ajenos pueden bloquear temporalmente los visores de Agreement. La duración y las condiciones exactas no están demostradas."
            + "\n\nDISTINCIÓN OPERATIVA\nGate significa teletransportador construido; Rift significa apertura interdimensional. Usar ambos términos como sinónimos produce análisis tácticos incorrectos.",
            "GATES\nThe available report describes them as costly, homemade teleporters. They provide transport, but must not be confused with the interdimensional technology of Rifts."
            + "\n\nRIFTS\nThey are described as interdimensional travel portals associated with Agreement. Range, cost and cooldown remain unconfirmed."
            + "\n\nVISORS\nOne unverified testimony claims that foreign portals can temporarily lock Agreement's visors. Duration and exact conditions have not been demonstrated."
            + "\n\nOPERATIONAL DISTINCTION\nGate means a constructed teleporter; Rift means an interdimensional opening. Treating both terms as synonyms leads to incorrect tactical analysis."),
        text("difficulty-levels", Category.DIFFICULTY, "Dificultades del servidor", "Server difficulties",
            "CASUAL · 50%\nRecomendada para nuevos jugadores."
            + "\n\nCONSCRIPTO · 75%\nRecomendada para quienes prefieren lanzarse directamente a la acción."
            + "\n\nDINÁMICO / DYNAMIC · 100%\nEl desafío de referencia. Algunos enemigos ya pueden eliminarte de un solo golpe."
            + "\n\nMASOQUISTA / MASOCHIST · 200%\nReto superior a Dynamic, con cambios importantes en jefes y unidades."
            + "\n\nHELL · 300%\nDoctrina todavía en desarrollo."
            + "\n\nLECTURA DE PORCENTAJES\nEl porcentaje expresa la presión operativa general. La resistencia, el daño, el número y las tácticas del enemigo pueden no aumentar en proporciones idénticas.",
            "CASUAL · 50%\nRecommended for new players."
            + "\n\nCONSCRIPTO / CONSCRIPT · 75%\nRecommended for players who prefer to jump straight into the action."
            + "\n\nDINÁMICO / DYNAMIC · 100%\nThe reference challenge. Some enemies can already eliminate you in one hit."
            + "\n\nMASOQUISTA / MASOCHIST · 200%\nA challenge above Dynamic, with major changes to bosses and units."
            + "\n\nHELL · 300%\nDoctrine still under development."
            + "\n\nREADING THE PERCENTAGES\nThe percentage expresses overall operational pressure. Enemy resilience, damage, numbers and tactics may not increase in identical proportions."),
        new Entry("skull-order", Category.DIFFICULTY, "Escala de amenaza", "Threat scale",
            "CLASIFICACIÓN DE CAMPO\nI · Vigilancia: presencia anómala o combatiente de riesgo limitado.\nII · Hostil: amenaza armada capaz de abatir personal aislado.\nIII · Letal: exige una escuadra preparada y coordinación.\nIV · Severa: puede destruir equipos completos y posiciones fortificadas.\nV · Crítica: compromete sectores enteros y requiere respuesta especializada.\nVI · Catastrófica: amenaza de bajas masivas, retirada o movilización total."
            + "\n\nCLASIFICACIÓN DORADA\nReservada para entidades cuya capacidad supera la estimación convencional. Su aparición activa alerta máxima y autorización de emergencia."
            + "\n\nLECTURA OPERATIVA\nLa escala mide el peligro de una unidad. Casual–HELL describe las condiciones generales de una operación; ambas evaluaciones cumplen funciones distintas.",
            "FIELD CLASSIFICATION\nI · Watch: anomalous presence or combatant of limited risk.\nII · Hostile: armed threat capable of killing isolated personnel.\nIII · Lethal: requires a prepared and coordinated squad.\nIV · Severe: can destroy full teams and fortified positions.\nV · Critical: endangers entire sectors and requires a specialist response.\nVI · Catastrophic: mass-casualty threat requiring retreat or full mobilisation."
            + "\n\nGOLD CLASSIFICATION\nReserved for entities beyond conventional assessment. Their appearance triggers maximum alert and emergency authorisation."
            + "\n\nOPERATIONAL READING\nThis scale measures the danger posed by a unit. Casual–HELL describes the overall conditions of an operation; the two assessments serve different purposes.",
            List.of(new Art("skull_order.png", 1280, 600, "Insignias de la escala de amenaza", "Threat-scale insignia")), false),
        chronicle("ending-tempest", "TEMPEST", "29 de septiembre de 2044. Fue encontrado sin vida en aguas poco profundas. El agua destruyó gran parte de la evidencia. El relato considera probable que muriera por heridas de rifle y pérdida de sangre, sin una conclusión forense definitiva.", "29 September 2044. Found dead in shallow water. Water destroyed much of the evidence. The account considers rifle wounds and blood loss a likely cause, without a definitive forensic conclusion."),
        chronicle("ending-fusilier", "FUSILIER", "2 de junio de 2044. Murió en una explosión. Según los peritos del relato, su propio lanzagranadas explotó después de recibir numerosos impactos de bala.", "2 June 2044. Killed in an explosion. The account's forensic examiners attributed it to his own grenade launcher detonating after numerous bullet hits."),
        chronicle("ending-achilles", "ACHILLES", "15 de octubre de 2044. Murió por un impacto devastador en la cabeza, atribuido de forma presunta a una bala de tungsteno de un rifle antimaterial. El relato sospecha que el tirador rival actuó antes que él.", "15 October 2044. Died from a devastating head impact, presumed to be a tungsten round from an anti-materiel rifle. The account suspects the opposing shooter acted first."),
        chronicle("ending-trident", "TRIDENT", "7 de marzo de 2044. Fue encontrado muerto con lesiones atribuidas a una Muramasa de alta frecuencia. El relato vincula al atacante con Desperado Enforcement y señala que Trident perdió la visión al inicio del encuentro. Los restos fueron vendidos a terceros.", "7 March 2044. Found dead with injuries attributed to a high-frequency Muramasa. The account links the attacker to Desperado Enforcement and says Trident lost his sight early in the encounter. His remains were sold to third parties."),
        chronicle("ending-gaia", "GAIA", "2 de mayo de 2044. Fue encontrado muerto junto a varios nusianos no identificados. El reporte describe violencia extrema, abuso y lesiones por armas blancas y fuego. Se conserva aquí un resumen no gráfico del desenlace.", "2 May 2044. Found dead alongside several unidentified Nusians. The report describes extreme violence, abuse, and injuries involving blades and fire. This entry preserves a non-graphic summary of the outcome."),
        chronicle("ending-hermes", "HERMES", "18 de agosto de 2044. Se encontraron restos del helicóptero con quemaduras y un ala dañada por una caída descontrolada. Se cree que el piloto perdió el control tras sufrir daños graves. No se encontró su cuerpo; esa ausencia no confirma por sí sola su muerte.", "18 August 2044. Helicopter wreckage showed burns and wing damage from an uncontrolled descent. The pilot is believed to have lost control after severe damage. The pilot's body was not found; that absence alone does not confirm death."),
        chronicle("ending-twins", "THE TWINS", "Sin fecha indicada. Las ventanas blindadas de los helicópteros habrían sido perforadas por un armamento no identificado antes de caer. Sólo se encontraron piezas mecánicas dispersas en el campo de batalla.", "No date recorded. The helicopters' armoured windows were reportedly pierced by unidentified weaponry before they fell. Only scattered mechanical parts were found across the battlefield."),
        chronicle("ending-agares", "AGARES", "15 de abril de 2044. Fue encontrado muerto. El relato concluye que su dispositivo defensivo fue saboteado y atribuye su derrota a un grupo de Dominianos blindados con armas blancas.", "15 April 2044. Found dead. The account concludes that his defensive device was sabotaged and attributes his defeat to a group of armoured Dominians using bladed weapons."),
        chronicle("ending-ghost", "GHOST", "1 de julio de 2044. Fue encontrado muerto con lesiones de armas de alto calibre y de animales. El relato indica que, tras quedar indefenso, fue abandonado vivo ante animales de la selva como ofrenda.", "1 July 2044. Found dead with injuries from high-calibre weapons and animals. The account says that after being left defenceless, he was offered alive to jungle animals."),
        chronicle("ending-prometheus", "PROMETHEUS", "22 de agosto de 2044. Fue encontrado muerto tras un incendio y una explosión. Los tanques de gas de su lanzallamas estaban destruidos; el reporte concluye que explotaron al recibir disparos.", "22 August 2044. Found dead after fire and an explosion. His flamethrower's gas tanks were destroyed; the report concludes that gunfire caused them to explode."),
        chronicle("ending-sparta", "SPARTA", "31 de octubre de 2044. Fue encontrado muerto, presuntamente por un sable de alta frecuencia. Se sospecha la intervención de un grupo de Dominianos.", "31 October 2044. Found dead, presumably killed with a high-frequency blade. A group of Dominians is suspected."),
        chronicle("ending-aurelionis", "AURELIONIS", "28 de mayo de 2044. Fue derrotado por un grupo de Dominianos y logró escapar con vida, con heridas graves en los brazos y lesiones por armas blancas. El relato menciona un trauma psicológico permanente. Sus habilidades siguen sin describirse.", "28 May 2044. Defeated by a group of Dominians, he escaped alive with severe arm injuries and blade wounds. The account mentions lasting psychological trauma. His abilities remain undescribed."),
        chronicle("ending-daedalus", "DAEDALUS", "17 de marzo de 2044. El reporte indica que fue encontrado muerto, flotando cerca de la Luna. No se especifica cómo llegó allí ni una causa de muerte.", "17 March 2044. The report says he was found dead, floating near the Moon. It does not explain how he got there or give a cause of death."),
        inspiration("inspiration-dvn", "Dummies vs Noobs", "dummies_vs_noobs.png", 768, 432, true,
            "Una guerra prolongada enfrenta a los Noobs con fuerzas Dummy cada vez más especializadas. Infantería, vehículos, unidades avanzadas y jefes convierten cada operación en una lucha de desgaste.",
            "A prolonged war pits the Noobs against increasingly specialised Dummy forces. Infantry, vehicles, advanced units and bosses turn every operation into a battle of attrition.",
            "Es la raíz principal de las clases enemigas, los despliegues por oleadas y la identidad militar de los expedientes.",
            "It is the main root of enemy classes, wave deployments and the military identity of the dossiers."),
        inspiration("inspiration-forsaken", "Forsaken", "forsaken.png", 768, 432, false,
            "Supervivientes atrapados en un dominio hostil repiten ciclos de persecución contra asesinos vinculados a sus propios pasados. Escapar exige cooperación, sacrificio y aprovechar cada segundo.",
            "Survivors trapped in a hostile realm endure repeated hunts by killers tied to their own pasts. Escape demands cooperation, sacrifice and careful use of every second.",
            "Aporta tensión de persecución, identidades marcadas por el trauma y la sensación de que cada vida cuenta.",
            "It contributes pursuit tension, trauma-shaped identities and the sense that every life matters."),
        inspiration("inspiration-dmc", "Devil May Cry", "devil_may_cry.png", 1024, 576, false,
            "Cazadores descendientes de poderes humanos y demoníacos enfrentan invasiones del inframundo. Sus conflictos mezclan rivalidades familiares, armas imposibles y enemigos que desafían la escala humana.",
            "Hunters descended from human and demonic powers face invasions from the underworld. Their conflicts mix family rivalries, impossible weapons and enemies beyond human scale.",
            "Inspira combatientes excepcionales, poderes extremos y enfrentamientos contra entidades muy superiores a un soldado común.",
            "It inspires exceptional fighters, extreme powers and confrontations with entities far beyond an ordinary soldier."),
        inspiration("inspiration-mgr", "Metal Gear Rising: Revengeance", "metal_gear.png", 1528, 918, false,
            "En un mundo dominado por contratistas militares, ciborgs y conflictos fabricados, la economía de guerra necesita enemigos permanentes. La tecnología convierte cuerpos y gobiernos en herramientas del negocio bélico.",
            "In a world ruled by military contractors, cyborgs and manufactured conflicts, the war economy needs permanent enemies. Technology turns bodies and governments into tools of the war business.",
            "Define gran parte del 2044 de SIEGE: corporaciones armadas, sabotaje, soldados aumentados y guerra convertida en industria.",
            "It defines much of SIEGE's 2044: armed corporations, sabotage, augmented soldiers and war turned into industry."),
        inspiration("inspiration-limbus", "Limbus Company", "limbus_company.png", 807, 399, false,
            "En una Ciudad gobernada por corporaciones conocidas como Alas, un grupo de pecadores recorre distritos devastados para recuperar artefactos llamados Ramas Doradas. Cada zona esconde tecnología, tragedias y reglas propias.",
            "In a City ruled by corporations known as Wings, a group of Sinners crosses ruined districts to recover artefacts called Golden Boughs. Every zone hides its own technology, tragedies and rules.",
            "Aporta expediciones por territorios corporativos, archivos fragmentados y personajes definidos por culpas y contratos.",
            "It contributes expeditions through corporate territories, fragmented records and characters defined by guilt and contracts."),
        inspiration("inspiration-helldivers", "Helldivers II", "helldivers_ii.png", 1024, 576, false,
            "Escuadras de élite son lanzadas sobre frentes planetarios para defender la Super Tierra de Terminids y Automatons. La propaganda glorifica una guerra galáctica sostenida por refuerzos, potencia de fuego y enormes bajas.",
            "Elite squads deploy across planetary fronts to defend Super Earth from Terminids and Automatons. Propaganda glorifies a galactic war sustained by reinforcements, firepower and enormous casualties.",
            "Inspira despliegues de escuadra, objetivos militares, apoyo pesado y la escala de una guerra que consume soldados constantemente.",
            "It inspires squad deployments, military objectives, heavy support and the scale of a war that constantly consumes soldiers."),
        inspiration("inspiration-frostpunk", "Frostpunk", "frostpunk.png", 950, 510, false,
            "Tras un invierno apocalíptico, las últimas ciudades sobreviven alrededor de generadores de calor. El liderazgo debe administrar hambre, frío, trabajo y esperanza mientras cada ley puede salvar a la comunidad o destruirla desde dentro.",
            "After an apocalyptic winter, the last cities survive around heat generators. Leadership must manage hunger, cold, labour and hope while every law can save the community or destroy it from within.",
            "Da forma al clima de Dominia, la supervivencia bajo frío extremo y las decisiones sociales tomadas durante una crisis interminable.",
            "It shapes Dominia's climate, survival under extreme cold and social decisions made during an endless crisis.")
    );
    public static List<Entry> entries(Category category, String query, boolean es) {
        var matcher = IntelSearch.compile(query);
        return ENTRIES.stream().filter(e -> e.category() == category)
            .filter(e -> matcher.test(e.title(es) + " " + (e.spoiler() ? "" : e.body(es)))).toList();
    }
}
