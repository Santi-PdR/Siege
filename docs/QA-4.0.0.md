# SIEGE 4.00.0 — QA

## Release gate

- `build.gradle` debe declarar `4.00.0`.
- Forge 1.20.1 + Java 17 debe completar `gradle clean build`.
- Todas las regresiones heredadas de 3.00 deben seguir verdes.
- `tests/test_release_400.py` debe pasar.
- Artifact del PR debe generarse correctamente.
- Sólo después del merge, el workflow de `main` puede publicar `dist/siege-menu-4.00.0.jar`.

## Portada

- OPERACIONES abre **Briefing de Recluta**, no la pantalla densa de búsqueda.
- AJUSTES sigue abriendo SIEGE Settings.
- Singleplayer continúa oculto salvo Ctrl+S.
- No aparece readiness `%` críptico.
- Tempest Jutcherson no puede aparecer como fondo normal.

## Briefing de Recluta

Probar GUI Scale 1, 2, 3 y 4 cuando sea aplicable en 1280×720, 1366×768 y 1920×1080.

- No debe haber texto solapado con botones.
- Deben aparecer ocho bloques: Qué es SIEGE, Razas, Progresión, Supervivencia, Amenazas, Arsenal, Despliegue y Multimedia.
- El detalle debe hacer wrap en vez de cortarse sobre otros componentes.
- El botón BUSCAR abre Operations Hub.
- Cada bloque abre su pantalla especializada.

## Enciclopedia

- Debe mostrar EMPEZAR / RAZAS / PROGRESIÓN / SISTEMAS / HISTÓRICO.
- En pantallas estrechas las categorías deben dividirse en varias filas.
- El buscador siempre debe quedar debajo de la última fila de categorías.
- No deben aparecer `FUENTE`, canal, autor, path técnico, cantidad de mensajes o detalles del procesamiento del ZIP en la interfaz visible.
- No deben aparecer perfiles, inventarios, builds o progreso de jugadores.
- HISTÓRICO no debe mezclarse con el conocimiento general.
- Buscar `Obsainan`, `Executor`, `Trial`, `Geography Table`, `Respawn Card` y `Assembling` debe devolver resultados útiles.

## Atlas de Razas

- Mostrar la escala de rareza completa en orden.
- Mostrar Human, Hacker, Shark, Saiyan, Deteriorer, Faraón, Apotheosis, Muerte, Cyborg, Ghoul, Subhuman, Terrariano, Kaioshin, Dragon, Shinigami, Majin y Undertale AU.
- Saiyan y Deteriorer deben aparecer como Obsainan.
- Apotheosis debe aparecer como Eternal.
- Raza sin rareza fiable = SIN CONFIRMAR; nunca inferir.
- Buscar por raza/rareza/tag debe funcionar.
- El detalle debe poder abrir Enciclopedia.

## Progresión

- Tracks: Ruta General, V1→V4, Trials, Rutas Especiales, Sistemas Avanzados.
- En compact, tabs pueden ocupar más de una fila sin solaparse.
- No mostrar `ABRIR FUENTE`; usar `VER INFORMACIÓN`.
- Perish Staff V2 debe marcarse Histórico.
- Saiyan no debe presentarse como V1→V4 normal.
- Cyborg debe conectar con Assembling.
- Fabled debe indicar pasos/spins especiales, no giros comunes.

## Threat Board

- Debe mostrar Intel, Executores, Bosses, Raids/Hordas, Estructuras, Facciones, Dimensiones y Muerte/Revive.
- En compact debe poder hacerse scroll por todas las entradas.
- Intel sigue siendo dueño de dossiers; Threat Board no inventa unidades.
- UNKNOWN continúa disponible en Intel.
- `VER INFORMACIÓN` abre Enciclopedia para amenazas generales.

## Arsenal

Comprobar búsqueda y apertura de:

- Third Justice
- Geography Table
- Daemonium Kit
- Fallen Angel Halo
- Improbability Scroll
- Assembling Table
- Aerorig
- Riflator
- HOLO-Watch

Third Justice:
- 0,1 s parry.
- 1 s cooldown.
- stun.
- −15% movimiento mientras se sostiene.
- hambre ×2 mientras está en inventario.
- REEL accesible cuando los recursos multimedia válidos estén presentes.

Geography Table:
- investigar información oculta.
- 120 wins sólo como referencia variable.

Fallen Angel Halo:
- lifesteal conocido.
- no inventar porcentaje.

## Multimedia

- SOUNDTRACK muestra pista actual, progreso, playlist y controles.
- MÚSICA DVN muestra Convenience Store, Music Box, New Store, Jazz Music, From the Ashes y Sad Choir como recomendaciones de uso.
- FONDOS DVN muestra las seis direcciones visuales.
- Las listas deben hacer scroll.
- No debe aparecer texto técnico de licencias/redistribución en la pantalla del jugador.
- Galería continúa accesible.
- Los fondos normales deben seguir pasando validación 16:9/HD existente.

## Operations Hub

- Debe adaptarse a 2, 3 o 5 columnas según ancho.
- Search no se superpone con rutas.
- Puede buscar Intel, Enciclopedia y Arsenal.
- `Third Justice` debe llegar a Arsenal.
- `ATLAS` debe llegar a Intel.
- `Deteriorer`, `Trial`, `Executor`, `Geography Table` deben llegar a Enciclopedia/Arsenal según corresponda.

## Regresiones históricas

No pueden volver:

- Favoritos / Índice / Guardar / Copiar en Intel.
- categoría All.
- `DESPLIEGUE 0.70` hardcodeado.
- readiness 87% en navegación normal.
- REC/STILL sobre frames de Boss.
- Tempest Jutcherson como fondo normal.
- Singleplayer visible sin Ctrl+S.
- pantallas de Embeddium tematizadas accidentalmente.
- gameplay/lore dentro de Settings/System.
- datos personales del Discord dentro de la Enciclopedia.

## Publicación

Antes de declarar 4.00.0 terminada:

1. PR CI = success.
2. Forge `Compile and verify` = success.
3. Artifact = success.
4. Squash merge a `main`.
5. Main CI = success.
6. `Publish validated jar for installer` = success.
7. `dist/manifest.json` debe indicar `siege-menu-4.00.0.jar`, commit final y SHA-256.
8. `dist/` debe contener únicamente manifest + JAR activo esperado.
