package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;
import uy.santipdr.siege.SiegeMod;

import java.util.ArrayList;
import java.util.List;

public final class IntelScreen extends Screen {
    private static final int BOSS_FRAME_COUNT = 6;
    private static final List<String> CATEGORIES = List.of("ALL", "UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT");
    private static final List<IntelEntry> FILES = List.of(
            file("HU-001", "INFANTRY", "UNIT", 1, "100", "infantry",
                    "Sin registro confirmado", "FN SCAR + armadura táctica", "Esqueleto / Pillager", "ACTIVO",
                    "La primera unidad que suele aparecer durante los primeros días de una operación. Aunque es la tropa más débil, su inteligencia artificial le permite compartir información, formar grupos y crear estrategias entre varios Infantries. Las apariencias registradas incluyen esqueletos y Pillagers, siempre equipados para combate directo.",
                    "No subestimar una formación numerosa. Separar al grupo, romper su coordinación y eliminar primero a las unidades que estén cubriendo el avance.",
                    "No confirmed record", "FN SCAR + tactical armour", "Skeleton / Pillager", "ACTIVE",
                    "Usually the first unit encountered during the opening days of an operation. Although it is the weakest troop, its artificial intelligence lets several Infantries share information, form groups and coordinate tactics. Recorded appearances include Skeleton and Pillager bodies, always prepared for direct combat.",
                    "Do not underestimate a large formation. Split the group, disrupt its coordination and eliminate the units covering the advance first."),
            file("HU-002", "SHIELDER", "UNIT", 2, "150", "shielder",
                    "Sin registro confirmado", "Escopeta + dos escudos", "Emboscador", "ACTIVO",
                    "Unidad semipeligrosa especializada en emboscadas y ataques por la espalda. Dos o más Shielders pueden planificar una encerrona coordinada. Su escopeta aumenta drásticamente el daño a corta distancia y los dos escudos le permiten bloquear prácticamente cualquier ataque frontal.",
                    "Evitar el cuerpo a cuerpo y no desperdiciar munición contra los escudos. Forzar un giro, atacar desde los flancos y mantener distancia de la escopeta.",
                    "No confirmed record", "Shotgun + two shields", "Ambusher", "ACTIVE",
                    "A semi-dangerous unit specialised in ambushes and attacks from behind. Two or more Shielders can coordinate a trap. Its shotgun becomes dramatically more dangerous at close range, while the two shields can block almost any frontal attack.",
                    "Avoid close combat and do not waste ammunition on the shields. Force it to turn, attack from the flanks and stay outside shotgun range."),
            file("SOP-001", "SABOTEUR", "UNIT", 4, "120", "saboteur",
                    "Sin registro confirmado", "Dos pistolas + C4 + sabotaje electrónico", "C4 normal / C4 nuclear", "HOSTIL",
                    "Una de las unidades más peligrosas para cualquier asentamiento. Puede infiltrarse en invisibilidad, aparecer cuando ya está demasiado cerca y colocar cargas C4 sobre estructuras o directamente sobre un jugador. Su dispositivo de sabotaje también neutraliza torretas Sentinel y otros aparatos electrónicos.",
                    "Tratar toda anomalía electrónica como una infiltración. Localizarlo antes de que coloque cargas y evacuar inmediatamente cualquier zona marcada con C4.",
                    "No confirmed record", "Dual pistols + C4 + electronic sabotage", "Standard C4 / nuclear C4", "HOSTILE",
                    "One of the most dangerous units for any settlement. It can infiltrate while invisible, reveal itself only at very close range and attach C4 to structures or directly to a player. Its sabotage device can also disable Sentinel turrets and other electronics.",
                    "Treat every electronic anomaly as an infiltration. Locate it before charges are planted and evacuate any area marked with C4 immediately."),
            file("SOP-002", "STALKER", "UNIT", 3, "100", "stalker",
                    "Sin registro confirmado", "Rifle subsónico + panel de camuflaje + GPS", "Escuadras de infiltración", "HOSTIL",
                    "Unidad espía que entra al área mediante una explosión de humo y desactiva su camuflaje al iniciar el ataque. Los Stalkers operan en grupos, observan bases para reunir información y pueden colocar discretamente un rastreador GPS en su objetivo sin que este lo note.",
                    "Vigilar cada nube de humo repentina. Después del contacto, revisar el equipo en busca de rastreadores y asumir que la posición de la base pudo quedar comprometida.",
                    "No confirmed record", "Subsonic rifle + camouflage panel + GPS", "Infiltration squads", "HOSTILE",
                    "A spy unit that enters the area through a smoke burst and disables its camouflage when the attack begins. Stalkers operate in groups, observe bases to gather intelligence and can discreetly plant a GPS tracker on a target without being noticed.",
                    "Watch every sudden smoke cloud. After contact, inspect equipment for trackers and assume the base position may have been compromised."),
            file("MECH-001", "NATZUKA", "UNIT", 3, "105", "natzuka",
                    "Sin registro confirmado", "Armas de fuego + carga suicida explosiva", "Cuadrúpedo de bajo costo", "ACTIVO",
                    "Unidad mecánica cuadrúpeda diseñada para atravesar terrenos difíciles. Su función principal es el combate directo con armas de fuego; si la amenaza persiste, puede ejecutar una carga suicida explosiva. Su bajo costo y fabricación sencilla permiten despliegues masivos.",
                    "Destruirla a distancia y evitar espacios cerrados. Una Natzuka aislada puede ser reemplazada rápidamente por una oleada completa.",
                    "No confirmed record", "Firearms + suicidal explosive charge", "Low-cost quadruped", "ACTIVE",
                    "A quadruped mechanical unit designed to cross difficult terrain. Its primary role is direct combat with firearms; if the threat persists, it can perform a suicidal explosive charge. Low cost and simple manufacturing allow mass deployment.",
                    "Destroy it at range and avoid confined spaces. A single Natzuka can quickly be replaced by an entire wave."),
            file("HU-003", "SNIPER", "UNIT", 4, "150", "sniper",
                    "República de Nusia", "Rifle de precisión + radar + radio", "Unidad de retaguardia", "HOSTIL",
                    "Francotirador nusiano equipado para combate de largo alcance. Busca lentamente un objetivo, se agacha y lo sigue durante varios segundos antes de disparar un proyectil rápido y potente. Un láser rojo advierte el disparo. Si su posición queda expuesta, cambia de lugar y también intenta cubrirse contra otros francotiradores.",
                    "Romper la línea de visión en cuanto aparezca el láser. Avanzar bajo cobertura y presionarlo para impedir que vuelva a establecer una posición estable.",
                    "Republic of Nusia", "Precision rifle + radar + radio", "Rear-line unit", "HOSTILE",
                    "A Nusian sniper equipped for long-range combat. It slowly searches for a target, crouches and tracks it for several seconds before firing a fast, powerful projectile. A red laser warns of the shot. If exposed, it relocates and seeks cover against opposing snipers.",
                    "Break line of sight as soon as the laser appears. Advance under cover and keep pressure on it so it cannot establish another stable position."),
            file("HU-004", "GRENADIER", "UNIT", 4, "350", "grenadier",
                    "República de Nusia", "Lanzagranadas multifunción + visión nocturna", "Gas / explosivo", "HOSTIL",
                    "Unidad nusiana de retaguardia que analiza la situación antes de decidir cuándo atacar. Dispara gas lacrimógeno para cegar y ralentizar, y cambia a granadas reales cuando necesita defenderse. Puede teletransportarse mediante bombas de humo y ejecutar disparos acrobáticos con Stylish Battle Ranks que triplican el daño.",
                    "Separarse para reducir el efecto del gas, abandonar inmediatamente la nube y evitar trayectorias previsibles que faciliten un disparo acrobático.",
                    "Republic of Nusia", "Multifunction launcher + night vision", "Gas / explosive", "HOSTILE",
                    "A Nusian rear-line unit that analyses the situation before choosing when to attack. It fires tear gas to blind and slow targets, switching to live grenades for defence. It can teleport through smoke bombs and perform Stylish Battle Rank trick shots that triple damage.",
                    "Spread out to limit the gas, leave the cloud immediately and avoid predictable movement that enables an acrobatic shot."),
            file("HU-005", "GUNNER", "UNIT", 5, "600", "gunner",
                    "República de Nusia", "LMG + mochila pesada de munición", "Tanque de unidad común", "HOSTIL",
                    "El mayor tanque entre las unidades comunes de Nusia. Avanza lentamente con casco, máscara de gas y una gran reserva de munición. Al localizar un objetivo se posiciona, permanece quieto y dispara ráfagas extremadamente rápidas durante doce a veinte segundos, suficientes para derribar a un jugador en menos de diez.",
                    "No intentar esquivar las balas en campo abierto. Usar cobertura sólida, esperar el final de la ráfaga y atacar durante su reposicionamiento.",
                    "Republic of Nusia", "LMG + heavy ammunition pack", "Common-unit tank", "HOSTILE",
                    "The heaviest tank among Nusia's common units. It advances slowly with a helmet, gas mask and a large ammunition reserve. After acquiring a target it anchors itself and fires extremely fast bursts for twelve to twenty seconds, enough to down a player in under ten.",
                    "Do not attempt to dodge in open ground. Use solid cover, wait for the burst to end and attack during relocation."),
            file("HU-006", "JETPACKER", "UNIT", 4, "150", "jetpacker",
                    "Sin registro confirmado", "Jetpack + RPG + cámara binocular", "Aéreo / terrestre", "HOSTIL",
                    "Unidad explosiva extremadamente rápida que usa un Jetpack para alcanzar velocidades sónicas, abrumar al objetivo y lanzar misiles desde una posición calculada. Sus proyectiles no son teledirigidos. Si falla, activa el modo terrestre del dispositivo, obteniendo supervelocidad y supersalto sobre tierra y montañas. Un rastro amarillo revela su desplazamiento.",
                    "Seguir el rastro amarillo, dispersarse antes de la descarga y cambiar de dirección después del lanzamiento. Aprovechar que los misiles no corrigen su trayectoria.",
                    "No confirmed record", "Jetpack + RPG + binocular camera", "Airborne / ground", "HOSTILE",
                    "An extremely fast explosive unit that uses a jetpack to reach sonic speed, overwhelm its target and rain missiles from a calculated position. Its projectiles are not guided. If it misses, the device enters ground mode, granting super-speed and super-jump across land and mountains. A yellow trail reveals its movement.",
                    "Track the yellow trail, spread out before the barrage and change direction after launch. Exploit the fact that its missiles cannot correct course."),
            file("HU-007", "PATRIOT", "UNIT", 4, "350", "patriot",
                    "República de Nusia", "Fusil militar — datos incompletos", "Información insuficiente", "ARCHIVO INCOMPLETO",
                    "Expediente parcialmente censurado. La bandera recuperada y el registro corregido confirman su pertenencia a la República de Nusia. No existe información verificada suficiente sobre sus capacidades especiales, patrón de despliegue o función exacta dentro de las fuerzas nusianas.",
                    "No completar los vacíos con suposiciones. Tratarlo como hostil hasta recuperar datos operativos confirmados.",
                    "Republic of Nusia", "Military rifle — incomplete data", "Insufficient information", "INCOMPLETE FILE",
                    "Partially redacted dossier. The recovered flag and corrected record confirm allegiance to the Republic of Nusia. There is not enough verified information about special capabilities, deployment pattern or exact role within Nusian forces.",
                    "Do not fill missing information with assumptions. Treat it as hostile until confirmed operational data is recovered."),
            file("ADV-001", "SPECIALIST", "ADVANCED", 5, "500", "specialist",
                    "República de Nusia", "C4 + bombas nucleares + teletransportación", "Planificador / líder de escuadra", "HOSTIL",
                    "Maestro de operaciones, planificación y estrategia. Se infiltra en invisibilidad y, si el jugador está dentro de una base, rodea la estructura con C4 antes de detonarla de una vez. Puede sellar pozos con capas de obsidiana y rodear la salida con explosivos nucleares. En grupos grandes asume el liderazgo, crea estrategias y distribuye órdenes. Posee teletransportación, autoconsciencia e IA cercana al nivel de jugadores PVP UHC.",
                    "No permanecer dentro de estructuras ni pozos después de detectarlo. Romper el grupo antes de que asuma el mando y cambiar constantemente de plan.",
                    "Republic of Nusia", "C4 + nuclear bombs + teleportation", "Planner / squad leader", "HOSTILE",
                    "Master of operations, planning and strategy. It infiltrates invisibly and, when a player is inside a base, surrounds the structure with C4 before detonating everything at once. It can seal pits with layered obsidian and ring the exit with nuclear explosives. In large groups it assumes command, creates strategies and issues orders. It possesses teleportation, self-awareness and AI approaching UHC PVP players.",
                    "Do not remain inside structures or pits after detection. Break the group before it assumes command and change tactics constantly."),
            file("ADV-002", "DEMOMAN", "ADVANCED", 4, "NAN", "demoman",
                    "República de Nusia", "Rifle de asalto + dinamita corporal", "Kamikaze avanzado", "HOSTIL",
                    "Maestro del suicidio definitivo y enemigo silencioso. Porta un rifle de asalto y una cantidad extrema de dinamita adherida al cuerpo. Tarda más en detonar que una unidad Kamikaze común, pero la explosión resultante es considerablemente mayor y más letal.",
                    "Identificar la carga roja y eliminarlo a máxima distancia. El tiempo adicional de detonación es la única ventana fiable de escape.",
                    "Republic of Nusia", "Assault rifle + body-mounted dynamite", "Advanced kamikaze", "HOSTILE",
                    "Master of ultimate suicide tactics and a silent enemy. It carries an assault rifle and an extreme quantity of dynamite attached to its body. Detonation takes longer than a common Kamikaze unit, but the resulting explosion is considerably larger and deadlier.",
                    "Identify the red payload and eliminate it at maximum range. The longer detonation time is the only reliable escape window."),
            file("ADV-003", "ARTILLER", "ADVANCED", 5, "500", "artiller",
                    "República de Nusia", "SMG + Radio Striker", "Flame Strike / misiles / nuclear", "HOSTIL",
                    "Especialista en bombardeo encubierto. En lugar de correr hacia el jugador, selecciona un punto seguro, permanece oculto y solicita ataques con su Radio Striker. Puede ordenar Flame Strikes, misiles pequeños, medianos o grandes y, en el extremo superior, bombas nucleares.",
                    "Localizar la transmisión y obligarlo a abandonar su posición antes de que complete la solicitud. No permanecer dentro de la zona marcada.",
                    "Republic of Nusia", "SMG + Striker Radio", "Flame Strike / missiles / nuclear", "HOSTILE",
                    "A covert bombardment specialist. Instead of rushing the player, it selects a protected location, remains hidden and calls attacks through a Striker Radio. Available strikes include Flame Strikes, small, medium and large missiles and, at the highest level, nuclear bombs.",
                    "Locate the transmission and force it from its position before the request completes. Never remain inside the marked zone."),
            file("ADV-004", "CLOAKER", "ADVANCED", 5, "150", "cloaker",
                    "República de Nusia", "Exoesqueleto + visión nocturna", "Cazador de alta velocidad", "HOSTIL",
                    "Maestro de caza absoluta. Aunque no porta un arma convencional, su exoesqueleto permite velocidades extremas y saltos muy altos. Emite pulsos de radar antes de correr hacia el objetivo con un chillido. Al alcanzarlo ejecuta un dropkick de 100.000 de daño que ignora completamente la armadura y mata en cualquier dificultad. Sus gafas nocturnas permiten cazar en oscuridad total.",
                    "El chillido confirma la carga. Separarse y cortar inmediatamente su trayectoria; no existe armadura capaz de resistir el impacto.",
                    "Republic of Nusia", "Exoskeleton + night vision", "High-speed hunter", "HOSTILE",
                    "Master of absolute hunting. Although it carries no conventional weapon, its exoskeleton enables extreme speed and very high jumps. It emits radar pulses before sprinting at the target with a screech. On contact it performs a 100,000-damage dropkick that completely ignores armour and kills on every difficulty. Night-vision goggles allow it to hunt in total darkness.",
                    "The screech confirms the charge. Split up and break its path immediately; no armour can survive the impact."),
            file("ADV-005", "APU", "ADVANCED", 5, "120,000", "apu",
                    "República de Nusia", "Mech de californita + lanzallamas", "Defensa reducida en agua", "HOSTIL",
                    "Maestro de la robótica hostil. Un piloto con casco de protección controla un exoesqueleto de californita armado con lanzallamas. Cuenta con 120.000 HP y 0,75 de defensa, ajustables por dificultad. Al acercarse quema continuamente, ignora los I-frames y vuelve inútiles los tótems. Su defensa cae dentro del agua.",
                    "Mantener distancia, atraerlo al agua y evitar por completo su arco frontal. No confiar en tótems ni en invulnerabilidad temporal.",
                    "Republic of Nusia", "Californite mech + flamethrower", "Reduced defence in water", "HOSTILE",
                    "Master of hostile robotics. A helmeted pilot controls a Californite exoskeleton armed with a flamethrower. It has 120,000 HP and 0.75 defence, adjusted by difficulty. At close range it burns continuously, ignores I-frames and renders totems useless. Its defence falls in water.",
                    "Keep it at range, lure it into water and avoid its frontal fire arc completely. Do not rely on totems or temporary invulnerability."),
            file("ADV-006", "MISSILER", "ADVANCED", 4, "250", "missiler",
                    "República de Nusia", "PVS-14 + Javelin + 8 DEF", "Francotirador de misiles guiados", "HOSTIL",
                    "Maestro de los teledirigidos y evolución avanzada del Sniper. Se desplaza en invisibilidad, elige una posición lejana y lanza misiles guiados. No proyecta un láser: aparece un destello amarillo que concede cinco segundos para escapar. Si detecta jugadores acercándose, desaparece nuevamente y selecciona otra posición.",
                    "Abandonar el área inmediatamente al ver el destello amarillo. Acercarse en grupo obliga al Missiler a interrumpir el ataque y reposicionarse.",
                    "Republic of Nusia", "PVS-14 + Javelin + 8 DEF", "Guided-missile sniper", "HOSTILE",
                    "Master of guided weaponry and an advanced evolution of the Sniper. It moves while invisible, selects a distant position and launches guided missiles. No laser is projected: a yellow flash grants five seconds to escape. If players approach, it vanishes again and selects another position.",
                    "Leave the area immediately when the yellow flash appears. A coordinated approach forces the Missiler to interrupt its attack and relocate."),
            tankFile("TNK-001", "ZAPPER", 4, "3,000", "100", "zapper",
                    "República de Nusia", "Bastón eléctrico + bobinas Tesla", "Melee / distancia / antitécnicas", "HOSTIL",
                    "Unidad tanque nusiana y minijefe poco frecuente. Su bastón controla corrientes, ondas y láseres letales, mientras las bobinas Tesla de la espalda recargan el sistema. Puede combatir sin apoyo y adaptarse a distintos entornos. Un impacto directo contra un objetivo sin armadura puede electrocutarlo y aturdirlo durante varios minutos; los disparos adicionales prolongan el efecto.",
                    "Usar aislamiento y cobertura sólida. Interrumpir la recarga de las bobinas, evitar encadenar impactos eléctricos y no enfrentarlo sin armadura.",
                    "Republic of Nusia", "Electric staff + Tesla coils", "Melee / ranged / anti-technique", "HOSTILE",
                    "A rare Nusian tank unit and field mini-boss. Its staff controls lethal currents, waves and lasers while the Tesla coils on its back recharge the system. It needs no common-unit support and can adapt to different environments. A direct hit on an unarmoured target can electrocute and stun for several minutes; additional shots extend the effect.",
                    "Use insulation and solid cover. Interrupt the coil recharge, avoid chained electric hits and never engage it without armour."),
            tankFile("TNK-002", "COMBATANT", 4, "3,000", "100", "combatant",
                    "República de Nusia", "M48 Tomahawk + armadura pesada", "Carga con control de trayectoria", "HOSTIL",
                    "Tanque de asalto pesado y deliberadamente ruidoso. Trota hasta localizar jugadores y se lanza al combate con una M48 Tomahawk capaz de proyectar ataques en varias direcciones. Su carga causa hasta 650 HP de daño y tiene un 75% de probabilidad de ignorar defensas, aunque puede esquivarse si el Combatant todavía no ha aprendido a corregir la trayectoria.",
                    "Mantener distancia y concentrar fuego a distancia. No pelear cuerpo a cuerpo; guardar movilidad para esquivar la carga y atacar durante su recuperación.",
                    "Republic of Nusia", "M48 Tomahawk + heavy armour", "Trajectory-controlled charge", "HOSTILE",
                    "A loud heavy-assault tank. It trots until players are found, then commits to close combat with an M48 Tomahawk capable of projecting attacks in several directions. Its charge deals up to 650 HP and has a 75% chance to ignore defences, although it can be dodged while the Combatant has not learned to correct its trajectory.",
                    "Keep your distance and focus ranged fire. Avoid close combat, preserve mobility for the charge and attack during its recovery."),
            tankFile("TNK-003", "AGREEMENT", 0, "3,000", "100", "agreement",
                    "Corporación Secure Contain Protect", "Información no recuperada", "Sin registro", "ARCHIVO INCOMPLETO",
                    "Solo se confirmó la designación Agreement, su resistencia estimada y la vinculación corporativa con Secure Contain Protect. No existen datos verificados sobre armamento, habilidades, comportamiento o función de combate.",
                    "No completar el expediente con suposiciones. Mantener observación y tratar la unidad como hostil hasta obtener evidencia operativa.",
                    "Secure Contain Protect Corporation", "Information not recovered", "No record", "INCOMPLETE FILE",
                    "Only the Agreement designation, estimated durability and corporate link to Secure Contain Protect have been confirmed. There is no verified data about weapons, abilities, behaviour or combat role.",
                    "Do not fill the dossier with assumptions. Maintain observation and treat the unit as hostile until operational evidence is recovered."),
            tankFile("TNK-004", "JAGANT", 0, "2,500", "100", "jagant",
                    "Sin registro confirmado", "Información no recuperada", "Sin registro", "ARCHIVO INCOMPLETO",
                    "El archivo únicamente conserva el nombre Jagant, una captura parcial y sus valores estimados de resistencia. Su origen, armamento, capacidades y patrón de despliegue siguen sin confirmar.",
                    "Evitar conclusiones basadas solo en la imagen. Registrar cada encuentro y mantener distancia hasta identificar su método de ataque.",
                    "No confirmed record", "Information not recovered", "No record", "INCOMPLETE FILE",
                    "The file only preserves the Jagant name, a partial capture and estimated durability values. Its origin, weapons, capabilities and deployment pattern remain unconfirmed.",
                    "Avoid conclusions based on the image alone. Record every encounter and keep your distance until its attack method is identified."),
            tankFile("TNK-005", "STRIDER", 0, "2,500", "100", "strider",
                    "Sin registro confirmado", "Información no recuperada", "Render recuperado", "ARCHIVO INCOMPLETO",
                    "Se recuperó un render de una estructura mecánica de patas largas identificada como Strider. No existe información verificada sobre su armamento, movilidad, autonomía, origen o comportamiento en combate.",
                    "No aproximarse basándose únicamente en su apariencia. Priorizar observación remota y registrar cualquier patrón de movimiento o emisión de energía.",
                    "No confirmed record", "Information not recovered", "Recovered render", "INCOMPLETE FILE",
                    "A render of a long-legged mechanical structure identified as Strider was recovered. There is no verified information about weapons, mobility, autonomy, origin or combat behaviour.",
                    "Do not approach based on appearance alone. Prioritise remote observation and record any movement pattern or energy emission."),
            bossFile("BOS-001", "TEMPEST", 5, "8,000", "tempest",
                    "Sin registro confirmado", "Pistola de rayos + descarga eléctrica + bobinas Tesla", "Eléctrico / semiacuático / sabotaje", "JEFE HOSTIL",
                    "Jefe especializado en controlar multitudes mediante armamento eléctrico y semiacuático. Su pistola de rayos daña a grupos completos y la descarga de corto alcance puede freír instantáneamente a las víctimas cercanas. Dos bobinas Tesla alimentan el sistema. El daño aumenta cuanto menor sea la distancia y Tempest también puede sabotear habilidades.",
                    "Reconocimiento Aéreo, Stronghold 5-5: Tempest se dirige hacia la zona. No amontonarse, mantener una separación amplia y evitar por completo su descarga de corto alcance.",
                    "No confirmed record", "Ray pistol + electric discharge + Tesla coils", "Electric / semi-aquatic / sabotage", "HOSTILE BOSS",
                    "A crowd-control boss equipped with electric and semi-aquatic weaponry. Its ray pistol damages entire groups and its short-range discharge can instantly fry nearby victims. Two Tesla coils power the system. Damage rises as distance closes, and Tempest can also sabotage abilities.",
                    "Aerial Recon, Stronghold 5-5: Tempest is moving towards the area. Do not cluster, keep wide spacing and avoid its short-range discharge completely."),
            bossFile("BOS-002", "FUSILIER", 5, "8,000", "fusilier",
                    "Sin registro confirmado", "Lanzagranadas de seis disparos + pala", "Bombardeo de largo alcance", "JEFE HOSTIL // LENTO",
                    "Jefe blindado de largo alcance que bombardea posiciones con un lanzagranadas de seis disparos. Si un jugador consigue acercarse, utiliza una pala como defensa. El registro visual confirma gorro, gafas protectoras, armadura pesada y movilidad reducida.",
                    "Reconocimiento Aéreo, sector 5-5: se detectó un Fusilier. Abandonar inmediatamente el área marcada por el proyectil y aprovechar su lentitud para cambiar de cobertura.",
                    "No confirmed record", "Six-shot grenade launcher + shovel", "Long-range bombardment", "HOSTILE BOSS // SLOW",
                    "A long-range armoured boss that bombards positions with a six-shot grenade launcher. If a player closes the distance, it uses a shovel for defence. Visual records confirm a cap, protective goggles, heavy armour and reduced mobility.",
                    "Aerial Recon, sector 5-5: a Fusilier has been detected. Leave the projectile impact area immediately and exploit its slow movement to change cover."),
            bossFile("BOS-003", "ACHILLES", 5, "15,000", "achilles",
                    "Sin registro confirmado", "Francotirador Armour Peeler de alta frecuencia", "Perforación total de armadura", "JEFE HOSTIL // AGONÍA // LENTO",
                    "Jefe francotirador equipado con un Armour Peeler de alta frecuencia. El proyectil ignora por completo la armadura y causa todavía más daño a objetivos protegidos. Su puntería es promedio y se desplaza lentamente; el registro muestra armadura ligera, auriculares y una radio de comunicaciones.",
                    "Reconocimiento Aéreo, Stronghold 5-5: una unidad Achilles apoya a las fuerzas enemigas. Romper las líneas de visión y atacar únicamente durante las ventanas en que sus defensas estén desactivadas.",
                    "No confirmed record", "High-frequency Armour Peeler sniper", "Complete armour penetration", "HOSTILE BOSS // AGONY // SLOW",
                    "A sniper boss equipped with a high-frequency Armour Peeler. Its projectile completely ignores armour and deals even more damage to protected targets. Its aim is average and movement is slow; records show light armour, headphones and a communications radio.",
                    "Aerial Recon, Stronghold 5-5: an Achilles unit is supporting enemy forces. Break dangerous sight lines and attack only during windows when its defences are disabled."),
            bossFile("BOS-004", "TRIDENT", 5, "38,000", "trident",
                    "Sin registro confirmado", "Machete de alta frecuencia + gancho de resistencia máxima", "Gancho / ejecución / carga", "JEFE HOSTIL",
                    "Agente fuertemente blindado que atrae jugadores con un gancho y desenvaina un machete de alta frecuencia al tenerlos cerca. El golpe próximo es una ejecución instantánea y puede rematar objetivos caídos. Si la víctima se aleja demasiado, Trident carga a gran velocidad. Porta visera opaca, pantalones hazmat mostaza, hombreras y guantes negros.",
                    "Reconocimiento Aéreo, Stronghold 5-5: tenemos un Trident. Mantener distancia, permanecer coordinados y cortar la trayectoria del gancho; acercarse permite su ejecución inmediata.",
                    "No confirmed record", "High-frequency machete + maximum-strength hook", "Hook / execution / charge", "HOSTILE BOSS",
                    "A heavily armoured agent that pulls players in with a hook and draws a high-frequency machete at close range. The nearby strike is an instant execution and can finish downed targets. If a victim moves too far away, Trident charges at high speed. It wears an opaque visor, mustard hazmat trousers, shoulder pads and black gloves.",
                    "Aerial Recon, Stronghold 5-5: a Trident is present. Keep distance, remain coordinated and break the hook trajectory; moving close enables its immediate execution."),
            bossFile("BOS-005", "PROMETHEUS", 5, "8,000", "prometheus",
                    "Sin registro confirmado", "FAHRENNEIT-3000 + dos tanques de combustible", "Incineración / dispositivo de alejamiento", "JEFE HOSTIL",
                    "Jefe incendiario equipado con un lanzallamas FAHRENNEIT-3000 y dos tanques de combustible. Su arma prende fuego a los jugadores y un dispositivo defensivo castiga a quienes se acercan demasiado, expulsándolos con gran fuerza y daño. El agente usa máscara de gas, frac, chaleco, hombreras y muñequeras tácticas.",
                    "Reconocimiento Aéreo, Stronghold 5-5: Prometheus fue desplegado. Mantenerse fuera del arco del lanzallamas y concentrar los ataques sobre sus tanques de combustible.",
                    "No confirmed record", "FAHRENNEIT-3000 + two fuel tanks", "Incineration / repulsion device", "HOSTILE BOSS",
                    "An incendiary boss equipped with a FAHRENNEIT-3000 flamethrower and two fuel tanks. Its weapon sets players ablaze, while a defensive device punishes anyone who moves too close by throwing them away with severe damage. The agent wears a gas mask, tailcoat, tactical vest, shoulder pads and wrist guards.",
                    "Aerial Recon, Stronghold 5-5: Prometheus has been deployed. Stay outside the flamethrower arc and concentrate attacks on its fuel tanks."),
            bossFile("BOS-006", "DAEDALUS", 5, "N/D", "daedalus",
                    "Sin registro confirmado", "Pico de combate de acero de improbabilidad", "Minería supersónica / emboscada subterránea", "JEFE HOSTIL",
                    "Jefe minero capaz de excavar bajo tierra a velocidad supersónica para emerger delante o detrás de su objetivo. Su pico parece común, pero está forjado con lingotes de acero de improbabilidad de frecuencia media. Puede dejar fuera de combate a un rival en tres a cinco golpes, o en uno solo si no lleva armadura pesada o moderna. También porta casco minero con rayos X, armadura naranja y vendas de curación pasiva.",
                    "Reconocimiento Aéreo, Stronghold 5-5: un Daedalus está en movimiento. Vigilar la retaguardia, no deambular solo y cambiar de posición cuando desaparezca bajo tierra.",
                    "No confirmed record", "Improbability-steel combat pickaxe", "Supersonic mining / underground ambush", "HOSTILE BOSS",
                    "A mining boss capable of tunnelling underground at supersonic speed before emerging ahead of or behind its target. Its pickaxe appears ordinary but is forged from medium-frequency improbability steel. It can incapacitate an opponent in three to five hits, or one hit without heavy or modern armour. It also carries an X-ray mining helmet, orange tactical armour and passive self-healing bandages.",
                    "Aerial Recon, Stronghold 5-5: a Daedalus is moving. Watch your rear, never wander alone and relocate whenever it disappears underground."),
            bossFile("BOS-007", "HERMES", 0, "45,000", "hermes",
                    "Sin registro confirmado", "Información no recuperada", "Video de archivo recuperado", "EXPEDIENTE INCOMPLETO",
                    "Existe metraje de archivo asociado a la designación Hermes y una resistencia estimada de 45.000 HP. No hay datos verificados sobre origen, armamento, capacidades, comportamiento o condiciones de despliegue.",
                    "No deducir habilidades únicamente a partir del video. Mantener observación remota, registrar cada movimiento y tratar a Hermes como un jefe hostil hasta completar el expediente.",
                    "No confirmed record", "Information not recovered", "Recovered archival video", "INCOMPLETE FILE",
                    "Archival footage linked to the Hermes designation exists, along with an estimated durability of 45,000 HP. There is no verified information about origin, weapons, capabilities, behaviour or deployment conditions.",
                    "Do not infer abilities from the video alone. Maintain remote observation, record every movement and treat Hermes as a hostile boss until the dossier is complete."),
            bossFile("BOS-008", "LELANTOS", 0, "8,000", "lelantos",
                    "Sin registro confirmado", "Información no recuperada", "Video de archivo recuperado", "EXPEDIENTE INCOMPLETO",
                    "Solo se recuperaron la designación Lelantos, un video de reconocimiento y una resistencia estimada de 8.000 HP. Su armamento, habilidades, origen y patrón táctico permanecen sin confirmar.",
                    "No completar la información con suposiciones. Analizar el metraje, evitar el contacto cercano y documentar cualquier capacidad observada en combate.",
                    "No confirmed record", "Information not recovered", "Recovered archival video", "INCOMPLETE FILE",
                    "Only the Lelantos designation, reconnaissance footage and an estimated durability of 8,000 HP were recovered. Its weapons, abilities, origin and tactical pattern remain unconfirmed.",
                    "Do not fill missing information with assumptions. Analyse the footage, avoid close contact and document any capability observed in combat."),
            bossFile("BOS-009", "GAIA", 0, "20,000", "gaia",
                    "Sin registro confirmado", "Información no recuperada", "Video de archivo recuperado", "EXPEDIENTE INCOMPLETO",
                    "El archivo de Gaia contiene metraje parcial y una resistencia estimada de 20.000 HP. No existen registros verificados sobre armamento, capacidades, afiliación o comportamiento de combate.",
                    "La presencia de otras figuras en el metraje no confirma aliados ni duplicados. Mantener distancia y registrar el primer encuentro operativo antes de clasificar a Gaia.",
                    "No confirmed record", "Information not recovered", "Recovered archival video", "INCOMPLETE FILE",
                    "The Gaia file contains partial footage and an estimated durability of 20,000 HP. There are no verified records about weapons, capabilities, affiliation or combat behaviour.",
                    "Other figures visible in the footage do not confirm allies or duplicates. Keep your distance and record the first operational encounter before classifying Gaia."));

    private final Screen parent;
    private final List<SiegeButton> categoryButtons = new ArrayList<>();
    private final List<SiegeButton> fileButtons = new ArrayList<>();
    private String category = "UNIT";
    private int selected;
    private int listOffset;
    private int detailScroll;
    private int maxDetailScroll;
    private int sidebarWidth;
    private int listTop;
    private int contentTop;
    private boolean compact;

    public IntelScreen(Screen parent) {
        super(Component.translatable("siege.intel.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        categoryButtons.clear();
        fileButtons.clear();
        compact = width < 560 || height < 295;

        if (compact) initCompact();
        else initWide();

        refreshCategoryLabels();
        rebuildFileButtons();
    }

    private void initWide() {
        sidebarWidth = Math.min(232, Math.max(158, width / 5));
        int buttonHeight = height < 360 ? 18 : 20;
        int gap = height < 360 ? 2 : 3;
        addRenderableWidget(new SiegeButton(10, 10, sidebarWidth - 20, 21,
                Component.literal("< ").append(Component.translatable("siege.intel.return")), b -> onClose(), 0xFFD64B4B));

        int categoryY = 48;
        for (String value : CATEGORIES) {
            SiegeButton button = new SiegeButton(10, categoryY, sidebarWidth - 20, buttonHeight,
                    Component.literal(categoryLabel(value)), b -> setCategory(value), categoryAccent(value));
            categoryButtons.add(button);
            addRenderableWidget(button);
            categoryY += buttonHeight + gap;
        }
        listTop = categoryY + 21;
        contentTop = 55;
    }

    private void initCompact() {
        sidebarWidth = 0;
        addRenderableWidget(new SiegeButton(8, 7, Math.min(76, Math.max(60, width / 5)), 18,
                Component.literal("< ").append(Component.translatable("siege.intel.return")), b -> onClose(), 0xFFD64B4B));

        int margin = 8;
        int gap = 3;
        int buttonHeight = 18;
        int columns = width < 420 ? 3 : 4;
        int rows = (CATEGORIES.size() + columns - 1) / columns;
        int categoryWidth = Math.max(46, (width - margin * 2 - gap * (columns - 1)) / columns);
        int startY = 31;
        for (int i = 0; i < CATEGORIES.size(); i++) {
            String value = CATEGORIES.get(i);
            int col = i % columns;
            int row = i / columns;
            int x = margin + col * (categoryWidth + gap);
            int y = startY + row * (buttonHeight + 3);
            SiegeButton button = new SiegeButton(x, y, categoryWidth, buttonHeight,
                    Component.literal(categoryLabel(value)), b -> setCategory(value), categoryAccent(value));
            categoryButtons.add(button);
            addRenderableWidget(button);
        }
        listTop = startY + rows * (buttonHeight + 3) + 1;
        contentTop = listTop + 23;
    }

    private void setCategory(String value) {
        if (category.equals(value)) return;
        SiegeUiSounds.click();
        category = value;
        selected = 0;
        listOffset = 0;
        detailScroll = 0;
        refreshCategoryLabels();
        rebuildFileButtons();
    }

    private void refreshCategoryLabels() {
        for (int i = 0; i < categoryButtons.size(); i++) {
            String value = CATEGORIES.get(i);
            boolean active = value.equals(category);
            categoryButtons.get(i).setMessage(Component.literal(categoryLabel(value)));
            categoryButtons.get(i).setSelected(active);
        }
    }

    private void rebuildFileButtons() {
        for (SiegeButton button : fileButtons) removeWidget(button);
        fileButtons.clear();
        List<IntelEntry> files = filtered();

        if (compact) {
            int navY = listTop;
            int arrowWidth = Math.min(44, Math.max(32, width / 10));
            SiegeButton previous = new SiegeButton(8, navY, arrowWidth, 18, Component.literal("<"), b -> stepFile(-1), categoryAccent(category));
            SiegeButton next = new SiegeButton(width - arrowWidth - 8, navY, arrowWidth, 18, Component.literal(">"), b -> stepFile(1), categoryAccent(category));
            previous.active = files.size() > 1;
            next.active = files.size() > 1;
            fileButtons.add(previous);
            fileButtons.add(next);
            addRenderableWidget(previous);
            addRenderableWidget(next);
            contentTop = navY + 23;
            return;
        }

        int visible = visibleFiles();
        listOffset = Math.max(0, Math.min(listOffset, Math.max(0, files.size() - visible)));
        int y = listTop;
        for (int i = listOffset; i < files.size() && i < listOffset + visible; i++) {
            int index = i;
            IntelEntry entry = files.get(i);
            SiegeButton button = new SiegeButton(10, y, sidebarWidth - 20, 20,
                    Component.literal(entry.code() + "  " + entry.name()), b -> {
                SiegeUiSounds.click();
                selected = index;
                detailScroll = 0;
                refreshFileSelection();
            }, categoryAccent(category));
            button.setSelected(i == selected);
            fileButtons.add(button);
            addRenderableWidget(button);
            y += 23;
        }
    }

    private void refreshFileSelection() {
        if (compact) return;
        for (int i = 0; i < fileButtons.size(); i++) {
            fileButtons.get(i).setSelected(listOffset + i == selected);
        }
    }

    private void stepFile(int direction) {
        List<IntelEntry> files = filtered();
        if (files.isEmpty()) return;
        SiegeUiSounds.click();
        selected = Math.floorMod(selected + direction, files.size());
        detailScroll = 0;
        if (!compact) {
            int visible = visibleFiles();
            if (selected < listOffset) listOffset = selected;
            if (selected >= listOffset + visible) listOffset = selected - visible + 1;
            rebuildFileButtons();
        }
    }

    private int visibleFiles() { return Math.max(1, (height - listTop - 16) / 23); }

    private List<IntelEntry> filtered() {
        if ("ALL".equals(category)) return FILES;
        List<IntelEntry> result = new ArrayList<>();
        for (IntelEntry entry : FILES) if (entry.category().equals(category)) result.add(entry);
        return result;
    }

    private boolean spanish() { return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_"); }

    private String categoryLabel(String value) {
        if (compact) {
            if (spanish()) return switch (value) {
                case "ALL" -> "TODOS";
                case "UNIT" -> "UNIDADES";
                case "ADVANCED" -> "AVANZ.";
                case "TANK" -> "TANQUES";
                case "BOSS" -> "JEFES";
                case "ELITE" -> "ÉLITES";
                case "SUPER-UNIT" -> "SUPER";
                default -> value;
            };
            return switch (value) {
                case "ALL" -> "ALL";
                case "UNIT" -> "UNITS";
                case "ADVANCED" -> "ADVANCED";
                case "TANK" -> "TANKS";
                case "BOSS" -> "BOSSES";
                case "ELITE" -> "ELITES";
                case "SUPER-UNIT" -> "SUPER";
                default -> value;
            };
        }
        if (!spanish()) return switch (value) {
            case "ALL" -> "ALL FILES";
            case "UNIT" -> "COMMON UNITS";
            case "ADVANCED" -> "ADVANCED UNITS";
            case "TANK" -> "TANKS";
            case "BOSS" -> "BOSSES";
            case "ELITE" -> "ELITES";
            case "SUPER-UNIT" -> "SUPER-UNITS";
            default -> value;
        };
        return switch (value) {
            case "ALL" -> "TODOS LOS ARCHIVOS";
            case "UNIT" -> "TROPAS COMUNES";
            case "ADVANCED" -> "TROPAS AVANZADAS";
            case "TANK" -> "TANQUES";
            case "BOSS" -> "JEFES";
            case "ELITE" -> "ÉLITES";
            case "SUPER-UNIT" -> "SUPERUNIDADES";
            default -> value;
        };
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!compact && mouseX < sidebarWidth && mouseY >= listTop) {
            int max = Math.max(0, filtered().size() - visibleFiles());
            int next = Math.max(0, Math.min(max, listOffset + (delta < 0 ? 1 : -1)));
            if (next != listOffset) {
                listOffset = next;
                rebuildFileButtons();
            }
            return true;
        }
        if (compact || mouseX >= sidebarWidth) {
            detailScroll = Math.max(0, Math.min(maxDetailScroll, detailScroll + (delta < 0 ? 2 : -2)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_6) {
            int index = keyCode - GLFW.GLFW_KEY_0;
            if (index < CATEGORIES.size()) setCategory(CATEGORIES.get(index));
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_LEFT) {
            stepFile(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_RIGHT) {
            stepFile(1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            detailScroll = Math.max(0, detailScroll - 5);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            detailScroll = Math.min(maxDetailScroll, detailScroll + 5);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        int accent = categoryAccent(category);

        g.fill(0, 0, width, height, 0x9506090B);
        if (compact) renderCompactChrome(g, accent);
        else renderWideChrome(g, accent);

        List<IntelEntry> files = filtered();
        if (files.isEmpty()) {
            g.drawCenteredString(font, label("SIN EXPEDIENTES EN ESTA CATEGORÍA", "NO FILES IN THIS CATEGORY"),
                    compact ? width / 2 : (sidebarWidth + width) / 2, height / 2, 0xFF8A9298);
        } else {
            selected = Math.max(0, Math.min(selected, files.size() - 1));
            if (compact) {
                IntelEntry entry = files.get(selected);
                String nav = String.format("%02d/%02d  //  %s  %s", selected + 1, files.size(), entry.code(), entry.name());
                int available = width - 112;
                g.drawCenteredString(font, font.plainSubstrByWidth(nav, Math.max(80, available)), width / 2, listTop + 5, 0xFFDDE2E5);
                renderFile(g, entry, 8, contentTop, width - 16, true);
            } else {
                renderFile(g, files.get(selected), sidebarWidth + 16, contentTop, width - sidebarWidth - 32, false);
                g.drawString(font, String.format("%02d/%02d", selected + 1, files.size()), width - 48, 17, 0xFF8B939A, false);
            }
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    private void renderWideChrome(GuiGraphics g, int accent) {
        g.fill(0, 0, width, 42, 0xF207090B);
        g.fill(0, 42, sidebarWidth, height, 0xF00B1014);
        g.fill(sidebarWidth, 42, width, height, 0x76080A0C);
        g.fill(sidebarWidth - 2, 42, sidebarWidth, height, accent);
        g.fill(0, 40, width, 42, accent);
        for (int y = 46; y < height; y += 28) g.fill(0, y, sidebarWidth, y + 1, 0x1519A5BC);
        g.drawCenteredString(font, title, (sidebarWidth + width) / 2, 16, 0xFFE9E9E4);
        g.drawString(font, "// " + label("CATEGORÍAS", "CATEGORIES"), 12, 37, 0xFF79838B, false);
        g.drawString(font, "// " + label("EXPEDIENTES", "FILES") + " [" + filtered().size() + "]", 12, listTop - 15, 0xFF79838B, false);
    }

    private void renderCompactChrome(GuiGraphics g, int accent) {
        g.fill(0, 0, width, 28, 0xF207090B);
        g.fill(0, 27, width, 29, accent);
        String compactTitle = label("INTEL CLASIFICADO", "CLASSIFIED INTEL");
        g.drawCenteredString(font, compactTitle, width / 2, 10, 0xFFEDEBE5);
        g.fill(0, contentTop - 3, width, contentTop - 2, 0x66454E55);
    }

    private void renderFile(GuiGraphics g, IntelEntry entry, int x, int y, int availableWidth, boolean compactMode) {
        IntelEntry.IntelText text = entry.text(spanish());
        int accent = categoryAccent(entry.category());
        boolean advanced = entry.category().equals("ADVANCED");
        int paper = advanced ? 0xFFE0E7EB : 0xFFE6DEC7;
        int paperDark = advanced ? 0xFFBBC7CE : 0xFFC9BEA3;
        int ink = advanced ? 0xFF13232D : 0xFF2B281F;
        int muted = advanced ? 0xFF53646E : 0xFF6D6553;
        int warning = advanced ? 0xFF8C2532 : 0xFF8B2E25;
        int bottom = height - 8;
        if (bottom <= y + 24 || availableWidth < 80) return;

        g.fill(x + 3, y + 3, x + availableWidth + 3, bottom + 3, 0x65000000);
        g.fill(x, y, x + availableWidth, bottom, paper);
        g.fill(x, y, x + availableWidth, y + 3, accent);
        g.fill(x, y, x + 1, bottom, paperDark);
        g.fill(x + availableWidth - 1, y, x + availableWidth, bottom, paperDark);

        int pad = compactMode ? 7 : 10;
        int headerX = x + pad;
        int headerY = y + 7;
        String ref = label("EXPEDIENTE", "FILE") + " " + entry.code();
        g.drawString(font, ref, headerX, headerY, muted, false);
        String stamp = label("CLASIFICADO", "CLASSIFIED");
        if (availableWidth > 240) {
            g.drawString(font, stamp, x + availableWidth - pad - font.width(stamp), headerY, accent, false);
        }
        g.drawString(font, entry.name(), headerX, headerY + 12, ink, false);
        String threatLevel = entry.threat() > 0 ? stars(entry.threat()) : label("SIN DATOS", "NO DATA");
        String threat = label("AMENAZA", "THREAT") + " " + threatLevel + "   HP " + entry.hp();
        if (!"N/D".equals(entry.defense())) threat += "   DEF " + entry.defense();
        g.drawString(font, font.plainSubstrByWidth(threat, availableWidth - pad * 2), headerX, headerY + 24, warning, false);

        int imageY = headerY + 39;
        int imageWidth;
        if (compactMode) imageWidth = Math.min(118, Math.max(88, availableWidth * 34 / 100));
        else imageWidth = Math.max(126, Math.min(318, availableWidth * 42 / 100));
        int imageHeight = imageWidth * 9 / 16;
        int imageX = headerX;
        int mediaFrame = bossFrame(entry);
        ResourceLocation portrait = portraitTexture(entry, mediaFrame);
        g.fill(imageX - 3, imageY - 3, imageX + imageWidth + 3, imageY + imageHeight + 3, paperDark);
        g.blit(portrait, imageX, imageY, imageWidth, imageHeight, 0, 0, 640, 360, 640, 360);
        g.fill(imageX, imageY, imageX + imageWidth, imageY + 2, accent);
        if (entry.category().equals("BOSS") && imageWidth >= 90) {
            String record = (SiegeConfig.animatedIntel && !SiegeConfig.reducedMotion ? "REC " : "STILL ")
                    + String.format("%02d/%02d", mediaFrame + 1, BOSS_FRAME_COUNT);
            int recordWidth = font.width(record) + 6;
            g.fill(imageX + 3, imageY + 5, imageX + 3 + recordWidth, imageY + 17, 0xB2080A0C);
            g.drawString(font, record, imageX + 6, imageY + 7, 0xFFFF6B66, false);
        }

        int metaX = imageX + imageWidth + (compactMode ? 8 : 13);
        int metaWidth = Math.max(54, x + availableWidth - pad - metaX);
        int metaY = imageY;
        metaY = drawMeta(g, label("ORIGEN", "ORIGIN"), text.origin(), metaX, metaY, metaWidth, muted, ink);
        metaY = drawMeta(g, label("ESTADO", "STATUS"), text.status(), metaX, metaY + 4, metaWidth, muted, warning);
        if (!compactMode) {
            metaY = drawMeta(g, label("ARMAMENTO", "ARMAMENT"), text.armament(), metaX, metaY + 4, metaWidth, muted, ink);
            metaY = drawMeta(g, label("VARIANTES", "VARIANTS"), text.variants(), metaX, metaY + 4, metaWidth, muted, ink);
        }

        int bodyTop = Math.max(imageY + imageHeight, metaY) + 9;
        int bodyBottom = bottom - 12;
        int bodyWidth = availableWidth - pad * 2;
        List<DetailLine> lines = new ArrayList<>();
        if (compactMode) {
            appendWrapped(lines, label("ARMAMENTO", "ARMAMENT") + ": " + text.armament(), bodyWidth, muted);
            appendWrapped(lines, label("VARIANTES", "VARIANTS") + ": " + text.variants(), bodyWidth, muted);
            lines.add(blankLine());
        }
        appendWrapped(lines, label("PERFIL OPERATIVO", "OPERATIONAL PROFILE"), bodyWidth, accentInk(accent, advanced));
        appendWrapped(lines, text.description(), bodyWidth, ink);
        lines.add(blankLine());
        appendWrapped(lines, label("ADVERTENCIA TÁCTICA", "TACTICAL ADVISORY"), bodyWidth, warning);
        appendWrapped(lines, text.advisory(), bodyWidth, warning);

        int visibleLines = Math.max(1, (bodyBottom - bodyTop) / 11);
        maxDetailScroll = Math.max(0, lines.size() - visibleLines);
        detailScroll = Math.min(detailScroll, maxDetailScroll);
        if (bodyBottom > bodyTop) {
            g.fill(headerX, bodyTop - 4, x + availableWidth - pad, bodyTop - 3, paperDark);
            g.enableScissor(headerX, bodyTop, x + availableWidth - pad, bodyBottom);
            int lineY = bodyTop - detailScroll * 11;
            for (DetailLine line : lines) {
                if (lineY >= bodyTop - 11 && lineY < bodyBottom) g.drawString(font, line.value(), headerX, lineY, line.color(), false);
                lineY += 11;
            }
            g.disableScissor();
        }
        if (maxDetailScroll > 0) {
            String hint = label("RUEDA: LEER MÁS", "WHEEL: READ MORE");
            g.drawString(font, hint, x + availableWidth - pad - font.width(hint), bottom - 10, muted, false);
            int trackX = x + availableWidth - 4;
            int trackTop = bodyTop;
            int trackHeight = Math.max(8, bodyBottom - bodyTop);
            int thumbHeight = Math.max(8, trackHeight * visibleLines / Math.max(visibleLines, lines.size()));
            int thumbY = trackTop + (trackHeight - thumbHeight) * detailScroll / maxDetailScroll;
            g.fill(trackX, trackTop, trackX + 2, trackTop + trackHeight, 0x44384143);
            g.fill(trackX, thumbY, trackX + 2, thumbY + thumbHeight, accent);
        }
    }

    private DetailLine blankLine() {
        return new DetailLine(FormattedCharSequence.forward(" ", net.minecraft.network.chat.Style.EMPTY), 0x00000000);
    }

    private int drawMeta(GuiGraphics g, String label, String value, int x, int y, int width, int labelColor, int valueColor) {
        if (width <= 8) return y;
        g.drawString(font, label + ":", x, y, labelColor, false);
        int nextY = y + 10;
        for (FormattedCharSequence line : font.split(Component.literal(value), width)) {
            g.drawString(font, line, x, nextY, valueColor, false);
            nextY += 10;
        }
        return nextY;
    }

    private void appendWrapped(List<DetailLine> target, String value, int width, int color) {
        for (FormattedCharSequence line : font.split(Component.literal(value), Math.max(20, width))) {
            target.add(new DetailLine(line, color));
        }
    }

    private int accentInk(int accent, boolean advanced) {
        return advanced ? 0xFF245F86 : 0xFF8C302B;
    }

    private int bossFrame(IntelEntry entry) {
        if (!entry.category().equals("BOSS") || !SiegeConfig.animatedIntel || SiegeConfig.reducedMotion) return 0;
        return Math.floorMod((int) (System.currentTimeMillis() / 450L), BOSS_FRAME_COUNT);
    }

    private ResourceLocation portraitTexture(IntelEntry entry, int frame) {
        String image = entry.image();
        if (entry.category().equals("BOSS")) {
            image = image.substring(0, image.length() - 2) + String.format("%02d", frame);
        }
        return new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/intel/" + image + ".png");
    }

    private String label(String spanishValue, String englishValue) { return spanish() ? spanishValue : englishValue; }

    private int categoryAccent(String value) {
        return switch (value) {
            case "ALL" -> 0xFF55BFD9;
            case "UNIT" -> 0xFFD94A4A;
            case "ADVANCED" -> 0xFF2F80FF;
            case "TANK" -> 0xFFD98A2B;
            case "BOSS" -> 0xFFB5162D;
            case "ELITE" -> 0xFF9B59D0;
            case "SUPER-UNIT" -> 0xFFE0B93F;
            default -> 0xFFB8C0C8;
        };
    }

    private String stars(int count) { return "★".repeat(Math.max(0, count)) + "☆".repeat(Math.max(0, 5 - count)); }

    private static IntelEntry file(String code, String name, String category, int threat, String hp, String image,
                                   String esOrigin, String esArmament, String esVariants, String esStatus, String esDescription, String esAdvisory,
                                   String enOrigin, String enArmament, String enVariants, String enStatus, String enDescription, String enAdvisory) {
        return new IntelEntry(code, name, category, threat, hp, "N/D", image,
                new IntelEntry.IntelText(esOrigin, esArmament, esVariants, esStatus, esDescription, esAdvisory),
                new IntelEntry.IntelText(enOrigin, enArmament, enVariants, enStatus, enDescription, enAdvisory));
    }

    private static IntelEntry tankFile(String code, String name, int threat, String hp, String defense, String image,
                                       String esOrigin, String esArmament, String esVariants, String esStatus, String esDescription, String esAdvisory,
                                       String enOrigin, String enArmament, String enVariants, String enStatus, String enDescription, String enAdvisory) {
        return new IntelEntry(code, name, "TANK", threat, hp, defense, image,
                new IntelEntry.IntelText(esOrigin, esArmament, esVariants, esStatus, esDescription, esAdvisory),
                new IntelEntry.IntelText(enOrigin, enArmament, enVariants, enStatus, enDescription, enAdvisory));
    }

    private static IntelEntry bossFile(String code, String name, int threat, String hp, String image,
                                       String esOrigin, String esArmament, String esVariants, String esStatus, String esDescription, String esAdvisory,
                                       String enOrigin, String enArmament, String enVariants, String enStatus, String enDescription, String enAdvisory) {
        return new IntelEntry(code, name, "BOSS", threat, hp, "N/D", "bosses/" + image + "/frame_00",
                new IntelEntry.IntelText(esOrigin, esArmament, esVariants, esStatus, esDescription, esAdvisory),
                new IntelEntry.IntelText(enOrigin, enArmament, enVariants, enStatus, enDescription, enAdvisory));
    }

    @Override
    public void onClose() {
        SiegeUiSounds.back();
        minecraft.setScreen(parent);
    }

    @Override public boolean isPauseScreen() { return false; }
    private record DetailLine(FormattedCharSequence value, int color) { }
}
