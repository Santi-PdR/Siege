# SIEGE 3.00.0 — QA

## Objetivo

Validar que 3.00 añade una Enciclopedia del Servidor grande y útil para nuevos jugadores sin introducir perfiles, inventarios, progreso individual o anécdotas personales.

## CI obligatorio

- `build.gradle` debe publicar `3.00.0`.
- Forge 1.20.1 + Java 17 debe compilar con `gradle clean build`.
- `KnowledgeDataRegressionTest` debe pasar.
- `OperationsIndexTest` debe pasar con deep-links de Enciclopedia.
- `NavigationIdentityTest` debe reconocer `ENC // SERVER ENCYCLOPEDIA`.
- Deben seguir pasando layouts, Guide, Archive, Config, escenas, instalador, theme scope, sliders, router, audio, Intel y recursos GUI.
- Tempest Jutcherson debe seguir fuera de `SiegeSceneCatalog` y `prepare-backgrounds-hd.py`.
- El JAR sólo se publica tras un push verde a `main`.

## Privacidad y alcance

Comprobar que `SiegeKnowledgeData` no contiene:

- nombres de jugadores usados como perfiles o ejemplos personales;
- “mi partida”, “mi inventario”, “mi personaje”;
- HP, energía o progreso de una persona concreta;
- inventarios personales;
- logros personales;
- builds privadas.

Las fuentes pueden resumirse como staff, sistema, histórico o sección temática sin necesidad de conservar identidades personales.

## Enciclopedia — navegación

### EMPEZAR

Debe priorizar temas que sirvan a un jugador nuevo:

- resumen del servidor;
- exploración;
- rarezas;
- catálogo de razas;
- progresión;
- Trials;
- Executores;
- estructuras;
- bosses;
- misiones/NPC;
- dimensiones;
- revive;
- reliquias;
- economía;
- política de fuentes.

### RAZAS

Verificar que aparezcan al menos:

- Human
- Hacker
- Shark
- Saiyan
- Deteriorer
- Faraón
- Apotheosis
- Muerte
- Cyborg
- Ghoul
- Subhuman
- Terrariano
- Kaioshin
- Dragon
- Shinigami
- Majin
- razas ocultas de AUs de Undertale

También deben aparecer:

- rarezas;
- slots;
- Fabled;
- progresión V1 → V4 cuando corresponde.

### SISTEMAS

Verificar navegación y búsqueda de:

- Executores;
- Trials;
- estructuras;
- bosses;
- misiones/NPC;
- Room/Gate;
- energía/meditación;
- Assembling;
- Geography Table;
- Daemonium Kit;
- Improbability Scroll;
- reliquias;
- dimensiones;
- muerte/revive;
- Respawn Cards;
- raids;
- facciones;
- economía;
- prompts/acciones.

### HISTÓRICO

Debe mostrar datos fechados/cambiados sin presentarlos como reglas actuales:

- debuffs antiguos de Deteriorer;
- Shark V2 histórica;
- Faraón y disponibilidad variable;
- cambios de edición/contexto de Executores;
- cambios de muerte/revive;
- contradicciones de Injured/Bleeding;
- umbrales contradictorios de meditación;
- política/cobertura de fuentes.

## Rarezas

Comprobar exactamente el orden:

`Común → Poco común → Raro → Ultra raro → Legendario → Obsainan → Mítico → Godly → Eternal → Fabled`

No cambiar `Obsainan` por otra ortografía inventada.

## Búsqueda interna

Buscar al menos:

- `Obsainan`
- `Fabled`
- `Human`
- `Hacker`
- `Deteriorer`
- `Executors`
- `terror radius`
- `V4 Trials`
- `Geography Table`
- `Daemonium Kit`
- `Respawn Cards`
- `Assembling`
- `Third Justice`

Verificar tildes y mayúsculas/minúsculas.

## Operations Hub

- Deben existir diez rutas de primer nivel.
- ENCICLOPEDIA debe tener identidad propia.
- Buscar `Geography Table` debe ofrecer un resultado KNOWLEDGE.
- Abrir ese resultado debe entrar directamente en su ficha.
- Buscar `Obsainan Fabled` debe encontrar la ficha de rarezas.
- Buscar `Human Hacker Saiyan` debe encontrar el catálogo de razas.
- Buscar `Executor terror radius` debe encontrar la ficha de Executores.
- Buscar `ATLAS` debe seguir llevando a Intel, no a la Enciclopedia.
- Buscar `Third Justice` debe seguir ofreciendo Arsenal y además poder mostrar su referencia histórica de servidor.
- El historial reciente debe aceptar ENC/KNOwledge sin duplicados consecutivos.

## Responsive / escalas

Verificar GUI Scale 1, 2, 3 y 4 cuando sean aplicables en:

- 1280×720
- 1366×768
- 1920×1080

Especial atención a:

- pestañas EMPEZAR / RAZAS / SISTEMAS / HISTÓRICO;
- cuadro de búsqueda;
- lista de temas;
- panel de detalle;
- diez rutas de Operations;
- líneas de fuente/confianza;
- textos de estado adicionales.

No debe haber solapes, botones fuera de pantalla ni texto encima del chrome inferior.

## Regresiones que NO pueden volver

- Tempest Jutcherson como fondo normal.
- Singleplayer visible sin Ctrl+S.
- favoritos/guardar/copiar/índice en Intel.
- REC/STILL sobre frames de bosses.
- porcentaje `readiness` críptico en navegación normal.
- gameplay/lore metido dentro de Settings/System.
- datos históricos mostrados como actuales.
- campos incompletos interpretados como “sin requisito”.
- información personal o perfiles dentro de la Enciclopedia.

## Verificación final antes de merge

Sólo mergear si:

1. PR CI = green.
2. `KnowledgeDataRegressionTest` = success.
3. `Compile and verify` = success.
4. Artifact del PR = success.
5. Tras squash a main, main CI = green.
6. `Publish validated jar for installer` = success.
7. `dist/manifest.json` indica `3.00.0`, commit final y SHA-256.
8. `dist/` contiene sólo el manifest y el JAR activo esperado.
