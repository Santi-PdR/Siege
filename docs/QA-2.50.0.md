# QA SIEGE 2.50.0

## Portada
- [ ] OPERACIONES y AJUSTES comparten la antigua fila de Ajustes sin solaparse en GUI Scale 1, 2, 3 y 4.
- [ ] Despliegue, Intel, Archivo/Arsenal, Operaciones/Ajustes y Salir siguen siendo utilizables.
- [ ] Ctrl+S continúa abriendo Singleplayer y no aparecen nuevos atajos globales inesperados.
- [ ] Tempest Jutcherson no aparece en la rotación, nombre de escena ni galería normal.

## Centro de Operaciones
- [ ] El hub abre desde portada y vuelve correctamente.
- [ ] Las nueve rutas abren su destino correcto.
- [ ] La búsqueda de `ATLAS` abre SUP-001.
- [ ] La búsqueda de `Third Justice` conduce al Arsenal.
- [ ] `Núcleo` y `Core` conducen al Archivo.
- [ ] `Mutilated`/estados equivalentes conducen al Manual de Campo.
- [ ] `server ping` conduce a Despliegue.
- [ ] Las búsquedas toleran mayúsculas y tildes.
- [ ] El historial de rutas no repite la misma ruta dos veces seguidas ni duplica entradas.
- [ ] Build, perfil, salud, Intel, escena y audio no se cortan en resoluciones normales.
- [ ] En resoluciones compactas la interfaz prioriza rutas/búsqueda y no solapa el pie.

## Fondos
- [ ] Las 12 escenas normales siguen siendo 1920×1080 y 16:9.
- [ ] Rooftop Squad permanece FEATURED.
- [ ] La bolsa estándar no repite una escena antes de completar el ciclo.
- [ ] La transición entre ciclos no repite consecutivamente la misma escena.
- [ ] La bandera histórica de `surprises` no modifica la selección de fondos normales.
- [ ] `tempest_jutcherson` no está en `SiegeSceneCatalog` ni en el listado de preparación HD.

## Intel / Archivo / Arsenal / Manual
- [ ] UNKNOWN sigue presente y conservador.
- [ ] No vuelven Favoritos, Índice, Guardar, Copiar ni un segundo Ampliar.
- [ ] Dossiers siguen en Intel; lore general sigue en Archivo.
- [ ] Estados de muerte/heridas y protocolos siguen en Manual de Campo.
- [ ] Objetos y Third Justice siguen en Arsenal.
- [ ] El buscador global no altera ni duplica esos dominios: solo navega hacia ellos.

## Despliegue
- [ ] `SiegeLacontinuacion.exaroton.me:18736` continúa como servidor oficial.
- [ ] Editar/Eliminar siguen bloqueados.
- [ ] Minecraft conserva callbacks de conexión autoritativos.
- [ ] QUERYING/OFFLINE/NO_RESPONSE/INCOMPATIBLE/ONLINE siguen representándose correctamente.

## Ajustes / Comando / Diagnóstico
- [ ] Perfil, apariencia, movimiento, audio, Intel, accesibilidad, fondos y sistema persisten.
- [ ] Centro de Comando y Diagnóstico permanecen técnicos, sin absorber lore.
- [ ] No reaparece un porcentaje de readiness críptico en navegación ordinaria.
- [ ] Pantallas de terceros como Embeddium no reciben tematización accidental.

## Recursos / CI / release
- [ ] Todos los PNG/JPG de GUI decodifican con ImageIO.
- [ ] Todos los fondos normales coinciden con `SiegeSceneCatalog`.
- [ ] El gate de easter eggs impide que Tempest vuelva al catálogo normal.
- [ ] `OperationsIndexTest` pasa búsquedas/ranking/historial.
- [ ] `SceneScheduleTest`, `NavigationIdentityTest`, `GuiResourceRegressionTest` y tests históricos pasan.
- [ ] `gradle clean build` termina correctamente con Java 17/Forge 1.20.1.
- [ ] El PR queda verde antes del squash merge.
- [ ] El build de `main` publica `dist/siege-menu-2.50.0.jar` y manifiesto SHA-256 válido.
