package uy.santipdr.siege.client;

import java.util.List;

/**
 * Current SIEGE Intel overlays. Entries backed by Dummies vs Noobs reference
 * material keep that provenance in their status text; entries without a reliable
 * source remain explicitly partial instead of being guessed.
 */
final class IntelCurrentData {
    private IntelCurrentData() { }

    static List<IntelEntry> supplemental() {
        return List.of(
                dossier("ADV-007", "ENGINEER", "ADVANCED", 0, "150", "N/D", "placeholder/classified",
                        "República de Nusia / referencia Dummies vs Noobs",
                        "Pistola + llave de construcción + Sentry + Teleporter",
                        "Advanced Noob / Unarmored Infantry / apoyo técnico",
                        "REFERENCIA DVN // SIN REGISTRO VISUAL LOCAL",
                        "Engineer es una unidad de apoyo documentada en Dummies vs Noobs. Avanza hasta un punto de construcción y levanta estructuras de apoyo, priorizando torretas y luego teleporters; usa una pistola para defensa propia. La referencia actual le asigna 150 HP. Su apariencia documentada corresponde a un ingeniero militar con equipo de construcción, llave y arma corta. SIEGE no dispone todavía de una imagen local verificada, por lo que mantiene el marcador documental.",
                        "Priorizar la destrucción de sus estructuras antes de que el nido se consolide. El sonido de la llave puede delatar una posición de construcción fuera de la línea de visión.",
                        "Republic of Nusia / Dummies vs Noobs reference",
                        "Pistol + construction wrench + Sentry + Teleporter",
                        "Advanced Noob / Unarmored Infantry / technical support",
                        "DVN REFERENCE // NO VERIFIED LOCAL IMAGE",
                        "Engineer is a documented Dummies vs Noobs support unit. It advances to a construction point and builds support structures, prioritising sentries and then teleporters, while carrying a pistol for self-defence. The current reference lists 150 HP. Its documented appearance is that of a military engineer carrying construction gear, a wrench and a sidearm. SIEGE does not yet have a verified local image asset, so the dossier keeps the document marker.",
                        "Destroy its structures before the nest is established. Wrench sounds can reveal a construction position outside direct line of sight."),
                dossier("ADV-008", "INFORMANT", "ADVANCED", 0, "155", "N/D", "placeholder/classified",
                        "República de Nusia / referencia Dummies vs Noobs",
                        "H94 Rifle + granadas de mano",
                        "Advanced Infantry / Sandbox-Holiday unit",
                        "REFERENCIA DVN // SIN REGISTRO VISUAL LOCAL",
                        "Informant está documentado como una forma avanzada de Infantry. Usa un H94 Rifle y granadas, tiene 155 HP, avanza hacia jugadores o edificios y puede priorizar construcciones. La referencia visual describe colores más pálidos que la Infantry estándar, gafas colocadas sobre el rostro y rodilleras negras. Mientras dispara puede desplazarse lateralmente y, si un jugador se acerca, retrocede mientras mantiene fuego.",
                        "No confundirlo a distancia con Infantry normal. Confirmar las trazas de sus disparos y evitar perseguirlo de forma lineal cuando empieza a retroceder.",
                        "Republic of Nusia / Dummies vs Noobs reference",
                        "H94 Rifle + hand grenades",
                        "Advanced Infantry / Sandbox-Holiday unit",
                        "DVN REFERENCE // NO VERIFIED LOCAL IMAGE",
                        "Informant is documented as a more advanced form of Infantry. It carries an H94 Rifle and hand grenades, has 155 HP, advances on players or buildings and can prioritise structures. The visual reference describes paler colours than standard Infantry, goggles worn over the face and black kneepads. While firing it can strafe, and when a player closes in it retreats while maintaining fire.",
                        "Do not mistake it for standard Infantry at range. Confirm its projectile trails and avoid chasing in a straight line once it begins retreating."),
                dossier("HU-010", "GRAPPLER", "UNKNOWN", 0, "N/D", "N/D", "placeholder/classified",
                        "Sin registro confirmado", "Información no recuperada", "Clasificación táctica no confirmada", "SIN REGISTRO VISUAL // ARCHIVO INCOMPLETO",
                        "No se encontró una referencia suficientemente fiable para fijar categoría, apariencia, armamento o estadísticas de Grappler sin inventar información. Permanece en Desconocido hasta recuperar una fuente verificable.",
                        "Registrar alcance, mecanismo de captura, equipo visible y comportamiento antes de reclasificar el expediente.",
                        "No confirmed record", "Information not recovered", "Tactical classification unconfirmed", "NO VISUAL RECORD // INCOMPLETE FILE",
                        "No sufficiently reliable reference was found to assign Grappler a category, appearance, weapons or statistics without inventing information. It remains Unknown until a verifiable source is recovered.",
                        "Record range, capture mechanism, visible equipment and behaviour before reclassifying the dossier."),
                dossier("ADV-009", "TRANQUILIZER", "ADVANCED", 0, "100", "N/D", "placeholder/classified",
                        "República de Nusia / referencia Dummies vs Noobs",
                        "Dart Rifle + compuesto químico",
                        "Advanced Noob / Epilogue-Hell unit",
                        "REFERENCIA DVN // SIN REGISTRO VISUAL LOCAL",
                        "Tranquilizer fue introducido oficialmente para Epilogue como reemplazo de Grenadier en ese modo. La referencia actual lo describe como Infantry sin armadura con máscara de gas, 100 HP y un Dart Rifle. Los dardos dejan un rastro químico dañino; un impacto directo aplica Tranquilised, reduciendo movimiento y aumentando el daño recibido durante un periodo corto. Evita disparar a quemarropa y tiende a separarse si un jugador se aproxima.",
                        "Cerrar distancia durante su enfriamiento o usar cobertura. Evitar atravesar el rastro químico y priorizarlo si su debuff deja expuesto al equipo.",
                        "Republic of Nusia / Dummies vs Noobs reference",
                        "Dart Rifle + chemical compound",
                        "Advanced Noob / Epilogue-Hell unit",
                        "DVN REFERENCE // NO VERIFIED LOCAL IMAGE",
                        "Tranquilizer was officially introduced for Epilogue as Grenadier's replacement in that mode. The current reference describes an unarmoured, gas-mask-wearing Infantry unit with 100 HP and a Dart Rifle. Its darts leave a harmful chemical trail; a direct hit applies Tranquilised, slowing movement and increasing incoming damage for a short period. It avoids firing at point-blank range and tends to create distance when approached.",
                        "Close distance during its cooldown or use cover. Avoid crossing the chemical trail and prioritise it when its debuff exposes the squad."),
                dossier("HU-012", "SKYDIVER", "UNKNOWN", 0, "N/D", "N/D", "placeholder/classified",
                        "Sin registro confirmado", "Información no recuperada", "Clasificación táctica no confirmada", "SIN REGISTRO VISUAL // ARCHIVO INCOMPLETO",
                        "No se recuperó una fuente fiable que confirme categoría, apariencia, armamento o estadísticas de Skydiver. La referencia aerotransportada por sí sola no basta para clasificarlo.",
                        "Vigilar cielo y zonas de descenso y documentar equipo, trayectoria y función antes de asignar categoría.",
                        "No confirmed record", "Information not recovered", "Tactical classification unconfirmed", "NO VISUAL RECORD // INCOMPLETE FILE",
                        "No reliable source was recovered that confirms Skydiver's category, appearance, weapons or statistics. An airborne designation alone is not enough to classify it.",
                        "Watch the sky and landing zones and document equipment, trajectory and role before assigning a category."),
                dossier("HU-013", "SKYLINER", "UNKNOWN", 0, "N/D", "N/D", "placeholder/classified",
                        "Sin registro confirmado", "Información no recuperada", "Clasificación táctica no confirmada", "SIN REGISTRO VISUAL // ARCHIVO INCOMPLETO",
                        "No se recuperó una fuente fiable que confirme categoría, apariencia, armamento o estadísticas de Skyliner. Permanece en Desconocido para evitar completar el archivo por semejanza con otras tropas aéreas.",
                        "Registrar altura, velocidad, carga útil, equipo y función táctica antes de reclasificar.",
                        "No confirmed record", "Information not recovered", "Tactical classification unconfirmed", "NO VISUAL RECORD // INCOMPLETE FILE",
                        "No reliable source was recovered that confirms Skyliner's category, appearance, weapons or statistics. It remains Unknown to avoid completing the file by analogy with other airborne troops.",
                        "Record altitude, speed, payload, equipment and tactical role before reclassifying."),
                dossier("TNK-006", "AGITATOR", "TANK", 0, "300", "Mecánico / escudo de ricochet", "placeholder/classified",
                        "República de Nusia / referencia Dummies vs Noobs",
                        "Shotgun + afterburn + tanque de combustible vulnerable",
                        "Armoured / Mechanical / advanced Shielder form",
                        "REFERENCIA DVN // SIN REGISTRO VISUAL LOCAL",
                        "Agitator está documentado como enemigo avanzado, blindado y mecánico. Tiene 300 HP en el cuerpo y un tanque de combustible de 200 HP. Se parece a un Shielder de colores más pálidos, con rodilleras negras, chaleco táctico más grande y un tanque rojo sujeto a la pierna derecha. Su escudo usa placas de ricochet; el cuerpo requiere explosivos, mientras que destruir el tanque de combustible provoca una explosión que elimina a la unidad.",
                        "Si no hay explosivos, buscar el tanque rojo de la pierna derecha. Evitar gastar fuego convencional contra el escudo frontal.",
                        "Republic of Nusia / Dummies vs Noobs reference",
                        "Shotgun + afterburn + vulnerable fuel tank",
                        "Armoured / Mechanical / advanced Shielder form",
                        "DVN REFERENCE // NO VERIFIED LOCAL IMAGE",
                        "Agitator is documented as an advanced armoured mechanical enemy. It has 300 body HP and a 200 HP fuel tank. It resembles a paler Shielder with black kneepads, a larger tactical vest and a red fuel tank attached to the right leg. Its shield uses ricochet plating; the body requires explosives, while destroying the fuel tank triggers an explosion that kills the unit.",
                        "If explosives are unavailable, target the red tank on the right leg. Do not waste conventional fire on the frontal shield."),
                dossier("ELT-004", "FAUNA", "ELITE", 0, "N/D", "N/D", "placeholder/classified",
                        "Sin registro de origen confirmado", "RPK-74 + visión tridimensional + Hallucinator", "Cloaker Infiltrator / señuelos", "ÉLITE HOSTIL // SIN REGISTRO VISUAL",
                        "Cazadora infiltradora equipada con una RPK-74, gafas de visión tridimensional y un exoesqueleto avanzado capaz de superar protecciones basadas en Compound V. Su grabadora Hallucinator puede generar hasta tres señuelos visuales y reproducir firmas sonoras de ataques falsos para desviar la atención. Puede ser convocada mediante Nexus Rally de Gaia o radios de mando de jefes. No se encontró una fuente principal suficientemente fiable para ampliar este expediente sin mezclar material de fangames.",
                        "Verificar contacto visual y procedencia del sonido antes de responder. Separar observadores para comparar ángulos y evitar perseguir una firma aislada sin confirmación física.",
                        "No confirmed origin record", "RPK-74 + three-dimensional vision + Hallucinator", "Cloaker Infiltrator / decoys", "HOSTILE ELITE // NO VISUAL RECORD",
                        "An infiltrator hunter equipped with an RPK-74, three-dimensional vision goggles and an advanced exoskeleton capable of bypassing Compound-V-based protection. Its Hallucinator recorder can create up to three visual decoys and reproduce false attack signatures to misdirect attention. It can be called through Gaia's Nexus Rally or boss command radios. No sufficiently reliable primary source was found to expand this dossier without mixing in fangame material.",
                        "Verify visual contact and sound origin before responding. Split observers to compare angles and avoid pursuing an isolated signature without physical confirmation."),
                dossier("ELT-005", "CERBERUS", "ELITE", 0, "N/D", "N/D", "placeholder/classified",
                        "Sin registro confirmado", "Información parcial", "Élite", "ÉLITE // SIN REGISTRO VISUAL",
                        "Cerberus permanece clasificado como Élite según el archivo SIEGE existente. No se encontró una fuente principal de Dummies vs Noobs suficientemente fiable para añadir apariencia, estadísticas o capacidades nuevas sin mezclar contenido de fans.",
                        "Tratarlo como amenaza de nivel Élite y registrar únicamente capacidades observadas directamente.",
                        "No confirmed record", "Partial information", "Elite", "ELITE // NO VISUAL RECORD",
                        "Cerberus remains classified as Elite according to the existing SIEGE file. No sufficiently reliable primary Dummies vs Noobs source was found to add appearance, statistics or new capabilities without mixing in fan content.",
                        "Treat it as an Elite-level threat and record only directly observed capabilities."),
                dossier("BOS-011", "PROTEUS", "BOSS", 0, "550", "N/D", "placeholder/classified",
                        "República de Nusia / referencia Dummies vs Noobs",
                        "Spectral Shotgun + Hivelink backpack",
                        "Cloning / Spectral Leap / boss en desarrollo",
                        "REFERENCIA DVN // DESARROLLO ACTIVO // SIN IMAGEN LOCAL",
                        "Proteus está documentado como Boss sin armadura actualmente en desarrollo. La referencia pública indica 550 HP base más 250 por jugador, una Spectral Shotgun de cinco perdigones y una mochila Hivelink. Puede usar Spectral Leap para reposicionarse y dividirse en grupos de Specters; cuando queda un único Specter, vuelve a materializarse como Proteus. Su diseño y balance siguen sujetos a cambios mientras la unidad continúa en desarrollo.",
                        "Reducir el número de Specters antes de que la clonación se descontrole y conservar distancia suficiente para evitar el golpe de escopeta y el bash. Tratar cifras y comportamiento como datos de desarrollo, no como valores inmutables.",
                        "Republic of Nusia / Dummies vs Noobs reference",
                        "Spectral Shotgun + Hivelink backpack",
                        "Cloning / Spectral Leap / boss in development",
                        "DVN REFERENCE // ACTIVE DEVELOPMENT // NO LOCAL IMAGE",
                        "Proteus is documented as an unarmoured Boss currently under development. The public reference lists 550 base HP plus 250 per player, a five-pellet Spectral Shotgun and a Hivelink backpack. It can use Spectral Leap to reposition and split into groups of Specters; when only one Specter remains it materialises back into Proteus. Its design and balance remain subject to change while the unit is still in development.",
                        "Reduce the Specter count before cloning snowballs and keep enough distance to avoid the shotgun and bash. Treat its numbers and behaviour as development data rather than immutable values."),
                dossier("SUP-002", "SAURON", "SUPER-UNIT", 5, "23,400,000", "N/D", "placeholder/classified",
                        "Sin registro confirmado", "Información no recuperada", "Operation Exodus", "SUPER UNIT // SIN REGISTRO VISUAL",
                        "Super Unit asociada a Operation Exodus con una resistencia confirmada de 23.400.000 HP. No se encontró una referencia externa fiable que permita completar armamento, defensa, capacidades o apariencia sin inventar información.",
                        "Evitar contacto aislado y conservar rutas de retirada. No inferir capacidades a partir de su clasificación o resistencia.",
                        "No confirmed record", "Information not recovered", "Operation Exodus", "SUPER UNIT // NO VISUAL RECORD",
                        "Super Unit associated with Operation Exodus with a confirmed durability of 23,400,000 HP. No reliable external reference was found that would allow weapons, defence, capabilities or appearance to be completed without invention.",
                        "Avoid isolated contact and preserve withdrawal routes. Do not infer capabilities from classification or durability."),
                dossier("SUP-003", "HEDALUS", "SUPER-UNIT", 5, "23,400,000", "N/D", "placeholder/classified",
                        "Sin registro confirmado", "Información no recuperada", "Operation Exodus", "SUPER UNIT // SIN REGISTRO VISUAL",
                        "Super Unit asociada a Operation Exodus con una resistencia confirmada de 23.400.000 HP. No se encontró una referencia externa fiable que permita completar armamento, defensa, capacidades o apariencia sin inventar información.",
                        "Evitar contacto aislado y conservar rutas de retirada. No inferir capacidades a partir de su clasificación o resistencia.",
                        "No confirmed record", "Information not recovered", "Operation Exodus", "SUPER UNIT // NO VISUAL RECORD",
                        "Super Unit associated with Operation Exodus with a confirmed durability of 23,400,000 HP. No reliable external reference was found that would allow weapons, defence, capabilities or appearance to be completed without invention.",
                        "Avoid isolated contact and preserve withdrawal routes. Do not infer capabilities from classification or durability."),
                dossier("BOS-010", "SPARTA", "BOSS", 0, "350", "N/D", "bosses/classified/frame_00",
                        "República de Nusia / referencia Dummies vs Noobs",
                        "Khanblades + Jetpack + Stomp",
                        "Hunter Boss / triple slash bajo 50% HP",
                        "REFERENCIA DVN // BOSS // SIN IMAGEN LOCAL",
                        "Sparta está documentado oficialmente como Boss de Dummies vs Noobs. La referencia actual lo describe como un cazador extremadamente rápido con dos espadas Khanblades, jetpack, placas/escudos en la espalda y tatuajes en los brazos y bajo el ojo izquierdo. Tiene 350 HP base más escalado por jugador. Persigue objetivos hasta entrar en rango de corte; sus ataques de espada son letales, recupera vida tras conseguir una baja y, por debajo de la mitad de su vida, puede ejecutar stomp, onda de choque y cadenas de tres cortes. El historial oficial de actualizaciones también documenta que su disparo antiguo fue reemplazado por una habilidad de salto para cerrar distancia.",
                        "Mantener movimiento constante y romper la trayectoria de sus cargas. A media vida asumir stomp y combos múltiples; no permanecer en líneas previsibles cuando activa el jetpack.",
                        "Republic of Nusia / Dummies vs Noobs reference",
                        "Khanblades + Jetpack + Stomp",
                        "Hunter Boss / triple slash below 50% HP",
                        "DVN REFERENCE // BOSS // NO LOCAL IMAGE",
                        "Sparta is officially documented as a Dummies vs Noobs Boss. The current reference describes an extremely fast hunter wielding twin Khanblades, a jetpack, plates/shields on his back and tattoos on his arms and below the left eye. He has 350 base HP plus player scaling. He chases targets into slash range; his sword attacks are lethal, he heals after securing a kill and, below half health, can use a stomp, shockwave and three-hit slash chains. The official update history also documents that his old gun behaviour was replaced by a leaping gap-closer.",
                        "Keep moving and break the path of his charges. Below half health, expect stomp and multi-hit chains; do not stay on predictable lines when the jetpack engages."));
    }

    static IntelEntry applyOfficialOverlay(IntelEntry entry) {
        return switch (entry.code()) {
            case "TNK-003" -> agreement(entry);
            case "BOS-002" -> fusilier(entry);
            case "BOS-004" -> trident(entry);
            case "ADV-001" -> specialist(entry);
            case "ADV-004" -> cloaker(entry);
            default -> entry;
        };
    }

    private static IntelEntry agreement(IntelEntry entry) {
        return replaceText(entry,
                new IntelEntry.IntelText(
                        "Corporación Secure Contain Protect", "Sin información oficial confirmada", "Sin información oficial confirmada",
                        "EXPEDIENTE OFICIAL // PARCIAL",
                        "El expediente oficial confirma únicamente la designación Agreement, una resistencia de 3.000 HP, 100 DEF y su vínculo con Secure Contain Protect. No hay datos oficiales confirmados sobre armamento, capacidades, variantes ni patrón táctico.",
                        "Sin protocolo táctico oficial confirmado. Consulte Guía SIEGE > Operaciones para archivos de campo separados."),
                new IntelEntry.IntelText(
                        "Secure Contain Protect Corporation", "No official information confirmed", "No official information confirmed",
                        "OFFICIAL DOSSIER // PARTIAL",
                        "The official dossier confirms only the Agreement designation, 3,000 HP, 100 DEF and its link to Secure Contain Protect. No official data is confirmed for armament, capabilities, variants or tactical pattern.",
                        "No official tactical protocol has been confirmed. See SIEGE Guide > Operations for separate field archives."));
    }

    private static IntelEntry fusilier(IntelEntry entry) {
        return replaceText(entry,
                new IntelEntry.IntelText(
                        "Sin registro de origen confirmado", "Lanzagranadas de seis disparos + granadas nucleares/incendiarias + pala",
                        "Modo Mortero / Blox Drink / strafing mejorado", "JEFE HOSTIL // EXPEDIENTE ACTUAL",
                        "Fusilier mantiene una función de bombardeo pesado. Dispone de granadas nucleares con una ventana aproximada de seis segundos para abandonar la zona, granadas incendiarias de área y un patrón de esquive lateral mejorado. En Modo Mortero dispara al cielo y controla las zonas de caída con distintos tipos de munición; su barra de jefe se vuelve blanca mientras el modo está activo. Blox Drink acelera recarga, cadencia y recuperación de habilidades mientras deteriora su propia visión.",
                        "Salir de la zona de impacto apenas aparezca una amenaza nuclear. Un explosivo correctamente sincronizado puede impedir que complete Blox Drink. Con la barra blanca, asumir bombardeo indirecto y cambiar de posición antes de la caída de los proyectiles."),
                new IntelEntry.IntelText(
                        "No confirmed origin record", "Six-shot grenade launcher + nuclear/incendiary grenades + shovel",
                        "Mortar Mode / Blox Drink / improved strafing", "HOSTILE BOSS // CURRENT DOSSIER",
                        "Fusilier retains a heavy-bombardment role. It carries nuclear grenades with an approximately six-second window to clear the area, area incendiary grenades and improved evasive strafing. In Mortar Mode it fires skyward and controls impact zones with multiple ammunition types; its boss bar turns white while the mode is active. Blox Drink speeds reload, fire rate and ability recovery while degrading its own vision.",
                        "Leave the impact zone as soon as a nuclear threat appears. A correctly timed explosive can interrupt Blox Drink. With the bar white, assume indirect bombardment and relocate before the projectiles land."));
    }

    private static IntelEntry trident(IntelEntry entry) {
        return replaceText(entry,
                new IntelEntry.IntelText(
                        "Sin registro de origen confirmado", "Machete de alta frecuencia + gancho + visor con dos estados",
                        "VISOR PUESTO / VISOR REMOVIDO", "JEFE HOSTIL // EXPEDIENTE ACTUAL",
                        "Trident alterna dos configuraciones de visor. Con VISOR PUESTO mantiene 40% de resistencia general, protege completamente la cabeza frente a disparos y su gancho tiene más dificultad para fijar un objetivo. Con VISOR REMOVIDO baja a 10% de resistencia y queda expuesto a disparos a la cabeza y flashbangs, pero puede interceptar de inmediato el primer ataque cuerpo a cuerpo y su gancho conecta con mayor facilidad, tira con menos resistencia y recoge a la víctima con más velocidad. Un aliado enganchado puede ser liberado disparando al torso del cautivo sin alcanzar zonas vitales; la víctima también puede cortar la cuerda si dispone de una hoja suficientemente eficaz.",
                        "Identificar el estado del visor antes de entrar en rango. No iniciar combate cuerpo a cuerpo con el visor removido. Coordinar el rescate de una víctima enganchada y aprovechar la menor resistencia defensiva del modo sin visor con fuego a distancia o granadas cegadoras."),
                new IntelEntry.IntelText(
                        "No confirmed origin record", "High-frequency machete + hook + two-state visor",
                        "VISOR ON / VISOR REMOVED", "HOSTILE BOSS // CURRENT DOSSIER",
                        "Trident switches between two visor configurations. With the VISOR ON it keeps 40% general damage resistance, fully protects the head from gunfire and has greater difficulty securing a hook. With the VISOR REMOVED resistance drops to 10% and it becomes vulnerable to headshots and flashbangs, but can immediately intercept the first incoming melee strike and the hook becomes easier to land, pulls against less resistance and reels the casualty faster. A hooked ally can be freed by shooting the captive's torso without striking vital areas; the victim can also cut the rope with a sufficiently effective blade.",
                        "Identify the visor state before entering range. Do not initiate melee while the visor is removed. Coordinate rescue of a hooked casualty and exploit the reduced visor-off resistance with ranged fire or flashbangs."));
    }

    private static IntelEntry specialist(IntelEntry entry) {
        var es = entry.spanish();
        var en = entry.english();
        return replaceText(entry,
                new IntelEntry.IntelText(es.origin(), es.armament(), "Planificador / líder / Stop Time", es.status(),
                        es.description() + " También dispone de Stop Time como capacidad especial confirmada.", es.advisory()),
                new IntelEntry.IntelText(en.origin(), en.armament(), "Planner / leader / Stop Time", en.status(),
                        en.description() + " Stop Time is also confirmed as one of its special capabilities.", en.advisory()));
    }

    private static IntelEntry cloaker(IntelEntry entry) {
        var es = entry.spanish();
        var en = entry.english();
        return replaceText(entry,
                new IntelEntry.IntelText(es.origin(), es.armament(), es.variants(), es.status(),
                        es.description() + " Algunos Cloakers presentan capacidad de parry; no debe asumirse que todos la poseen.", es.advisory()),
                new IntelEntry.IntelText(en.origin(), en.armament(), en.variants(), en.status(),
                        en.description() + " Some Cloakers display a parry capability; it must not be assumed on every individual.", en.advisory()));
    }

    private static IntelEntry dossier(String code, String name, String category, int threat, String hp, String defense, String image,
                                      String esOrigin, String esArmament, String esVariants, String esStatus, String esDescription, String esAdvisory,
                                      String enOrigin, String enArmament, String enVariants, String enStatus, String enDescription, String enAdvisory) {
        return new IntelEntry(code, name, category, threat, hp, defense, image,
                new IntelEntry.IntelText(esOrigin, esArmament, esVariants, esStatus, esDescription, esAdvisory),
                new IntelEntry.IntelText(enOrigin, enArmament, enVariants, enStatus, enDescription, enAdvisory));
    }

    private static IntelEntry replaceText(IntelEntry entry, IntelEntry.IntelText spanish, IntelEntry.IntelText english) {
        return new IntelEntry(entry.code(), entry.name(), entry.category(), entry.threat(), entry.hp(), entry.defense(), entry.image(), spanish, english);
    }
}
