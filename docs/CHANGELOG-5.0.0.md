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
- Subhuman pasa a explicarse como una familia de variantes. Ya existen fichas separadas para **Adamantium Human**, **Sorcerer** y **Evil Morty**.
- Las fichas de Subhuman, Saiyan y Ghoul pueden abrir una pantalla de rutas/variantes propia.
- El mapa de progresión se organiza en: cómo progresar, V1→V4, rutas de raza y Trials.
- Trial Spire, Trials de meditación, Trials de raza, Trials de armas, Witch Trials y Third Justice quedan separados en lugar de mezclarse en una regla genérica.

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

### Media Room 5.0
- Nueva sección **Ambientes / Moods**.
- Los ambientes conectan una pista ya instalada con referencias temáticas para una situación concreta.
- Primeros ambientes: **Stronghold**, **Deployment**, **Intel / Archive** y **Last Stand**.
- Los catálogos DVN y de dirección visual tienen scroll.
- La dirección visual incluye Stronghold, Arctic Standoff, combate urbano nocturno, ciudad sitiada, frente desértico, zona industrial, hangar/briefing, wave defense, boss assault y armería de despliegue.
- Las referencias externas siguen siendo referencias. No se incluye audio de terceros sin una fuente válida para redistribuirlo.

### Perfil STRONGHOLD
- Perfil visual inspirado en la identidad militar de Dummies vs Noobs / Stronghold.
- Mantiene fondos e Intel animados y scanlines suaves.
- Reduce flashes y elimina la interferencia de título para no sacrificar legibilidad por estética.

## Fuente de contenido para el pase 5.00
La revisión de contenido está usando el export completo de Eternal Craft / SIEGE, con mensajes desde 2025 hasta septiembre de 2026. La regla para la interfaz normal es conservar el dato más reciente que pueda confirmarse y no mezclarlo con recetas, precios o requisitos anteriores. Cuando la información sigue incompleta, se indica de forma sencilla en vez de inventar el paso que falta.

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
- seguir extrayendo del export grande razas, sistemas, Trials, objetos y consejos que todavía no tengan ficha actual;
- completar las rutas de razas sólo cuando haya información suficientemente clara y reciente;
- continuar buscando masters de fondos de Dummies vs Noobs con calidad suficiente para entrar en la rotación normal;
- probar en juego el nuevo build, especialmente audio, GUI Scale, Intel, Atlas, Briefing y Operations;
- mantener el PR como draft hasta que el CI final y la prueba real del JAR estén verdes.
