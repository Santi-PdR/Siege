# QA visual y operacional — SIEGE 1.50.0

## Portada
- Probar GUI Scale 1, 2, 3 y 4 en 1280×720 y 1920×1080.
- Confirmar que título, columna de comandos, música, Intel preview y chrome no se solapan.
- Confirmar que la placa BUILD usa 1.50.0 y muestra perfil + preparación.
- Confirmar que la placa de escena aparece solo cuando existe espacio seguro.
- Confirmar que Tempest Jutcherson muestra ANOMALÍA VISUAL y no aparece en rotación automática con Movimiento reducido o Reducir destellos.

## Fondos
- Recorrer manualmente las 13 escenas desde la galería.
- Confirmar que ninguna imagen muestra checker negro/magenta.
- Confirmar que Operación nocturna, Encuentro urbano, Escuadrón en azotea y Tempest Jutcherson conservan sus proporciones originales.
- Verificar que escenas claras reciben suficiente oscurecimiento con Auto contraste.
- Verificar crossfade sin salto de escena y sin cambio de dimensiones inesperado.

## Intel
- Abrir las siete categorías: UNIT, ADVANCED, TANK, BOSS, ELITE, SUPER-UNIT y UNKNOWN.
- Confirmar que UNKNOWN contiene Grappler, Skydiver y Skyliner mientras no haya una clasificación fiable posterior.
- Confirmar que los placeholders clasificados se renderizan y nunca caen en missing texture.
- Revisar los seis frames de cada Boss animado.
- Confirmar que no regresan Favoritos, Índice, Guardar ni Copiar.
- Confirmar un único botón Ampliar por dossier.

## Despliegue
- Confirmar que el header interno ya no muestra `DESPLIEGUE 0.70`.
- Confirmar que aparece la versión dinámica 1.50.0.
- Confirmar servidor oficial fijado: `SiegeLacontinuacion.exaroton.me:18736`.
- Confirmar Editar/Eliminar deshabilitados únicamente sobre el servidor oficial y con explicación.
- Probar estados online, consultando, sin respuesta, offline e incompatible.
- Probar F5 vanilla, selección preservada y scroll preservado.

## Pantallas vanilla tematizadas
- Mouse: verificar header MSE // MOUSE, sliders, foco, hover y retorno.
- Audio: verificar AUD // AUDIO MIX y que la lógica de volumen siga siendo vanilla.
- Video: verificar VID // VIDEO sin invadir la interfaz propia de Embeddium/Sodium.
- Controls/Key Binds: verificar CTL // CONTROLS, lista y foco.
- Accessibility: verificar ACC // ACCESSIBILITY.
- Language: verificar LNG // LANGUAGE.
- Resource Packs: verificar PAK // RESOURCES.
- World selection/create/edit: verificar WRD // WORLD FILES.
- Confirmar que el tema no entra a Pause, Chat, Inventory, Death ni pantallas de mods de terceros.

## Diagnóstico / Command Center
- Confirmar CRÍTICO / ATENCIÓN / OPERATIVO y porcentaje de preparación.
- Fijar Tempest Jutcherson manualmente con Reducir destellos y comprobar el aviso informativo de fondos.
- Confirmar que ninguna reparación se ejecuta sin acción explícita.
- Probar recuperación de música 0%, UI 0%, interferencia y perfil Rendimiento incoherente.

## CI obligatorio
- `SceneScheduleTest` verde.
- `ConfigRegressionTest` verde recorriendo todo `SiegeSceneCatalog`.
- `NavigationIdentityTest` verde.
- `GuiResourceRegressionTest` verde para todos los PNG de GUI.
- `RuntimeRegressionTest` verde para Intel/ImageIO/Boss frames.
- `test_release_014.py` verde con contrato 1.50.
- Gradle/Forge build verde.
- PR mergeable antes de fusionar.
- Tras merge, `Publish validated jar for installer` verde y `dist/manifest.json` apuntando a 1.50.0.
