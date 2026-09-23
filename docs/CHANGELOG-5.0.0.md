# SIEGE 5.00.0 — Command Network

Estado: desarrollo activo en `chatgpt/siege-5.00.0-nextgen`.

5.00 no se plantea como una suma de parches sobre 4.00. La base es convertir SIEGE en una experiencia más coherente, fácil de entender y mucho más audiovisual, manteniendo Forge 1.20.1 / Java 17 y los contratos de privacidad, Intel, Deployment y accesibilidad existentes.

## Bloques ya implementados

### Operations simplificado
- Operations deja de mostrar una pared de accesos repetidos.
- Quedan tres frentes: **Entrar y prepararse**, **Amenazas y progreso** y **Consultar información**.
- Archivo y Arsenal permanecen en el menú principal y dejan de ser rutas de Operations.
- Comando, Diagnóstico, Ajustes y Fondos dejan de duplicarse dentro de Operations.
- La búsqueda de Operations sigue encontrando dossiers, razas, Trials, reliquias y temas, pero ya no vuelve a introducir las rutas que se quitaron.
- El encabezado deja de mostrar estado técnico del cliente, “profile fit” y otros datos que no ayudan a jugar.

### Briefing rehecho para nuevos
- El Briefing pasa a llamarse **Primeros pasos** dentro de la propia pantalla.
- Se reduce a nueve temas útiles: qué es SIEGE, cómo empezar, razas, habilidades, V1→V4, Trials, Executores, unidades/bosses y heridas/reanimación.
- El comienzo explica que se puede arrancar como un survival normal: comida, herramientas, armadura, refugio y recursos antes de meterse con sistemas complejos.
- Se eliminan frases de auditoría, “evidencia”, “revisión”, “corpus” y otras expresiones técnicas de las pantallas normales.
- Los enlaces entre fichas dejan de ser un “También ver” automático. Sólo aparecen siguientes pasos concretos cuando realmente ayudan.

### Atlas y Enciclopedia separados del Briefing
- Atlas queda para consulta detallada, no como una copia del Briefing.
- Cuatro áreas claras: **Razas**, **Progresión**, **Sistemas** y **Objetos**.
- La Enciclopedia usa categorías equivalentes y sólo muestra información actual destinada a jugadores.
- Los registros viejos o editoriales pueden seguir existiendo internamente para mantenimiento, pero no aparecen como fichas normales ni pueden abrirse mediante deep-link público.
- La información más nueva reemplaza a la vieja cuando comparten el mismo tema.

### Razas y progresión
- V1, V2, V3 y V4 ahora se explican como etapas de evolución para las razas que usan ese sistema; ya no se da a entender que todas las razas progresan igual.
- Saiyan tiene su propia ruta basada en entrenamiento y transformaciones.
- Ghoul separa progreso por carne, V2 y Super Ghoul para no mezclar mecanismos distintos.
- Subhuman pasa a explicarse como una familia de variantes. Existen fichas separadas para **Adamantium Human**, **Sorcerer**, **Evil Morty** y **Rick Sanchez**.
- Las fichas de Subhuman, Saiyan y Ghoul pueden abrir una pantalla de rutas/variantes propia.
- El Atlas de Razas se amplió con el catálogo grande recuperado del chat: Human, Mink, Tsufurujin, Shark, Angel, Ghoul, Cyborg, Deteriorer, Involver, Majin, Dark Manor, Hermes, Saiyan, Otsutsuki, Ackerman, Titan, Exceed, Cold Demon, Kaioshin, Dragon, Gas, Muerte, Shinigami, Fullbringer, Arrancar, Hakaishin, Zeno, Quincy, Divine, Namekian, Angel Guía, Aryano, Narehate, Arclighter, Faraón, Sun, Almirante, Archie, Emperador, Glitch Core, Jiren, Winter Hunter, Apotheosis, Lunarian, Virtud, Super Ghoul, Terrariano, Fénix, Diclonius, Windwhirl, Void Master, SOBRINO, Oni, Iluminati y Undertale AU.
- Una raza encontrada en el registro no recibe habilidades inventadas. Cuando sólo está confirmado el nombre, la ficha lo dice y queda preparada para completar más adelante.
- El mapa de progresión se organiza en: cómo progresar, V1→V4, rutas de raza y Trials.
- Trial Spire, Trials de meditación, Trials de raza, Trials de armas, Witch Trials y Third Justice quedan separados en lugar de mezclarse en una regla genérica.

### Información recuperada del export completo
- El export dividido enviado para 5.00 se reconstruyó e indexó: **251.065 mensajes** disponibles para búsquedas y revisión por tema.
- Se añadieron datos reutilizables que no dependan de la partida de una persona: variantes de raza, tipos de Executor, herramientas de reliquias y Trials.
- Maze Executor queda identificado como una variante de Executor; la guía ya no trata a todos los Executores como si fueran iguales.
- Se incorpora el aviso de proximidad de Executor como ayuda del cliente, aclarando que no reemplaza el dossier de Intel.
- Se añadió la variante Subhuman **Rick Sanchez** sin inventar rareza, habilidades o progresión que el chat no confirme.
- El Trial del santuario queda documentado como evento global, pero sus cifras antiguas no se fijan como requisitos permanentes porque pueden cambiar con balance.

### Información actual y recetas
- La interfaz normal conserva sólo la regla más nueva conocida cuando algo cambió.
- El desfibrilador reemplaza a RCP como método actual de reanimación general.
- Crafteo actual del desfibrilador en la guía: **3 bloques de hierro + 1 mesa de encantamientos**.
- Las recetas anteriores no se muestran en la ficha normal.
- Geography Table se explica por su función: investigar propiedades ocultas de objetos y reliquias.
- Daemonium Kit se separa de Geography Table: sirve en el proceso de desmontar reliquias y extraer componentes.
- Se eliminan precios, compras personales y anécdotas de jugadores de estas fichas.

### Navegación duplicada
- Intel deja de ser un acceso alternativo a Archivo/Arsenal mediante un pase final de limpieza de navegación.
- Diagnóstico deja de ser una ruta visible para el jugador.
- Comando permanece como opción dentro de Ajustes, sin duplicarse en Operations.

### Audio 5.0
- El soundtrack se vuelve a generar desde los masters limpios con **44.1 kHz** y **3 dB de headroom** antes de codificar a Vorbis.
- La razón es práctica: el artefacto 4.0 decodificaba varias pistas llegando o superando 0 dBFS, algo que puede producir crackling/interferencia en ciertas combinaciones OpenAL/dispositivo.
- CI decodifica completamente cada OGG y rechaza pistas corruptas, tasas de muestreo incorrectas o música sin margen suficiente.
- Los sonidos de interfaz también se verifican como Vorbis 44.1 kHz.
- Los clics, volver, avisos y cambios de categoría dejan de reutilizar el mismo sample con pitch artificial distinto: se reproducen al pitch original `1.0` para evitar que la interfaz suene deformada.

### Media Room 5.0
- Nueva sección **Ambientes / Moods**.
- Los ambientes conectan una pista ya instalada con referencias temáticas para una situación concreta.
- Ambientes actuales: **Stronghold**, **Deployment**, **Intel / Archive**, **Last Stand** y **Industrial War**.
- Los catálogos DVN y de dirección visual tienen scroll.
- La dirección visual incluye Stronghold, Arctic Standoff, combate urbano nocturno, ciudad sitiada, frente desértico, zona industrial, hangar/briefing, wave defense, boss assault y armería de despliegue.
- La búsqueda web del pase 5.00 amplió referencias musicales DVN con Convenience Store, Music Box, New Store, Jazz Music, From the Ashes, Sad Choir, Calm Before The Storm A, The Last Flame, Into The Storm, Grinder, Hell March (Remastered), Fight Through Adversity, Scanning Hostile Biodats y Full Force.
- Las referencias externas siguen siendo referencias. No se incluye audio de terceros sin una fuente válida para redistribuirlo.
- El material oficial localizado en Roblox sigue llegando a resolución de miniatura (por ejemplo 768×432), por lo que no se hace upscale barato para fingir un fondo HD.

### Perfil STRONGHOLD
- Perfil visual inspirado en la identidad militar de Dummies vs Noobs / Stronghold.
- Mantiene fondos e Intel animados y scanlines suaves.
- Reduce flashes y elimina la interferencia de título para no sacrificar legibilidad por estética.

## Fuente de contenido para el pase 5.00
La revisión de contenido usa el export completo de Eternal Craft / SIEGE, con mensajes desde 2025 hasta septiembre de 2026. El export se indexó completo para localizar sistemas, razas, objetos, Trials, recetas y correcciones recientes. Eso no significa copiar conversaciones a la interfaz: se extrae información reutilizable, se descartan datos personales y se compara la fecha cuando hay versiones distintas de una misma mecánica.

La regla para la interfaz normal es conservar el dato más reciente que pueda confirmarse y no mezclarlo con recetas, precios o requisitos anteriores. Cuando la información sigue incompleta, se indica de forma sencilla en vez de inventar el paso que falta.

## Contratos que siguen siendo obligatorios
- Forge 1.20.1 / Java 17.
- Singleplayer sólo mediante Ctrl+S.
- servidor oficial y callbacks vanilla autoritativos.
- UNKNOWN permanece en Intel cuando realmente no se sabe algo.
- sin Favoritos / Índice / Guardar / Copiar en Intel.
- dossier a la derecha.
- música aleatoria sin repetición.
- Tempest Jutcherson continúa aislado como easter egg, fuera de fondos y galería normales.
- pantallas de Embeddium/Sodium/otros mods no se tematizan accidentalmente.
- reduce flashes / reduce motion / high contrast siguen siendo contratos de accesibilidad.
- la información general nunca debe convertirse en perfil, inventario, build o progreso personal de un jugador.

## Todavía pendiente antes de considerar 5.00 terminado
- seguir revisando por tema los 251.065 mensajes para razas, sistemas, Trials, objetos y consejos que todavía no tengan ficha actual;
- completar las rutas de razas sólo cuando haya información suficientemente clara y reciente;
- continuar buscando masters de fondos de Dummies vs Noobs con calidad suficiente y procedencia adecuada para entrar en la rotación normal;
- probar en juego el nuevo build, especialmente audio, GUI Scale, Intel, Atlas, Briefing y Operations;
- mantener el PR como draft hasta que el CI final y la prueba real del JAR estén verdes.
