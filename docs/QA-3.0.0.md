# SIEGE 3.00.0 — QA

## Objetivo

Validar que el salto 2.50 → 3.00 añade un Archivo de Conocimiento grande sin romper los sistemas ya publicados ni convertir información histórica/incierta del Discord en reglas actuales.

## CI obligatorio

- `build.gradle` debe publicar `3.00.0`.
- Forge 1.20.1 + Java 17 debe compilar con `gradle clean build`.
- `KnowledgeDataRegressionTest` debe pasar.
- `OperationsIndexTest` debe pasar con deep-links de Knowledge.
- `NavigationIdentityTest` debe reconocer `KNW`.
- Deben seguir pasando layouts, Guide, Archive, Config, escenas, instalador, theme scope, sliders, router, audio, Intel y recursos GUI.
- Tempest Jutcherson debe seguir fuera de `SiegeSceneCatalog` y `prepare-backgrounds-hd.py`.
- El JAR sólo se publica tras un push verde a `main`.

## Knowledge Vault

### Separación

1. Abrir OPERACIONES → CONOCIMIENTO.
2. Debe abrir inicialmente **SIEGE ACTUAL**.
3. Verificar que aparecen únicamente registros `CURRENT`.
4. Cambiar a **ENCICLOPEDIA**.
5. Verificar que los registros históricos no aparecen mezclados dentro del snapshot actual.
6. Cambiar entre pestañas varias veces: un deep-link inicial no debe obligar a volver automáticamente a la ficha original.

### Fuentes y confianza

Para varios registros verificar visualmente:

- Deteriorer actual → ACTUAL CONFIRMADO.
- consejo de oxidación en raid → CONFIRMADO POR ALEX.
- duración antigua extrema → HISTÓRICO.
- +6% RE/5 turnos → NO CONFIRMADO.
- Filter Rod → NO CONFIRMADO / Misterios.
- advertencia de meditación → conserva fuentes Alex + resultado observado al revelar.

### Spoilers

1. Abrir una entrada histórica con `spoiler=true`.
2. El título, resumen y fuente deben verse.
3. El cuerpo completo no debe verse antes de pulsar **REVELAR ARCHIVO**.
4. Tras revelar, debe aparecer detalle, relaciones y fuentes.
5. Cerrar la pantalla y volver: el reveal no debe convertirse en una preferencia permanente.

### Supervivencia

La vista SUPERVIVENCIA debe priorizar registros críticos, por ejemplo:

- revisar RE antes de meditar;
- oxidación descontrolada en raid;
- adaptación a técnicas repetidas;
- uso prudente de reliquias/desmontaje;
- prompts vagos con consecuencias;
- heridas/revive cuando corresponda.

No debe mostrar entradas no críticas simplemente para rellenar espacio.

### Búsqueda interna

Buscar al menos:

- `Rust Guard`
- `Geography Table`
- `Daemonium Kit`
- `120 wins`
- `medito fuerte`
- `Deteriorer`
- `revive`

Verificar tildes y mayúsculas/minúsculas.

## Operations Hub

- Deben existir diez rutas de primer nivel.
- CONOCIMIENTO debe tener identidad propia.
- Buscar `Geography Table` debe ofrecer un resultado KNOWLEDGE.
- Abrir ese resultado debe entrar directamente en su ficha.
- Buscar `Rust Guard` debe llevar a la zona CURRENT.
- Buscar Intel como `ATLAS` debe seguir llevando al dossier Intel, no a Knowledge.
- Buscar `Third Justice` debe seguir funcionando con Arsenal.
- El historial reciente debe aceptar KNW sin duplicados consecutivos.

## Responsive / escalas

Verificar GUI scale 1, 2, 3 y 4 cuando sean aplicables en:

- 1280×720
- 1366×768
- 1920×1080

Especial atención a:

- pestañas CURRENT/ENCYCLOPEDIA/SURVIVAL/SOURCES;
- cuadro de búsqueda;
- lista de registros;
- panel de detalle;
- botón REVELAR ARCHIVO;
- diez rutas de Operations;
- textos de estado adicionales.

No debe haber solapes, botones fuera de pantalla ni texto encima del chrome inferior.

## Regresiones que NO pueden volver

- Tempest Jutcherson como fondo normal.
- Singleplayer visible sin Ctrl+S.
- favoritos/guardar/copiar/índice en Intel.
- REC/STILL sobre frames de bosses.
- porcentaje `readiness` críptico en navegación normal.
- gameplay/lore metido dentro de Settings/System.
- datos históricos reemplazando silenciosamente el estado actual.
- datos posibles mostrados como confirmados.
- un comentario de jugador promovido automáticamente a regla del staff.

## Verificación final antes de merge

Sólo mergear si:

1. PR CI = green.
2. `Compile and verify` = success.
3. Artifact del PR = success.
4. Tras squash a main, main CI = green.
5. `Publish validated jar for installer` = success.
6. `dist/manifest.json` indica `3.00.0`, commit final y SHA-256.
7. `dist/` contiene sólo el manifest y el JAR activo esperado.
