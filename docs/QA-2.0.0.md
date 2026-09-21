# QA visual y operacional — SIEGE 2.0.0

Esta lista complementa CI. Un build verde no sustituye abrir el mod en Minecraft y revisar la composición con la fuente, escala y recursos reales.

## Portada
- Probar GUI Scale 1, 2, 3 y 4 en 1280×720, 1920×1080, 2560×1440 y una ventana compacta.
- Confirmar que el pie ya **no muestra `87%`, readiness ni otro porcentaje de salud técnica**.
- Confirmar que `BUILD 2.0.0` y el perfil activo no chocan con botones ni dossiers automáticos.
- Confirmar que la fila de conocimiento muestra **ARCHIVO | ARSENAL** y que ambas rutas funcionan.
- Confirmar que Singleplayer permanece oculto y sólo aparece mediante Ctrl+S.
- Revisar la tarjeta Intel automática: una unidad debe poder abrir su dossier y el fondo debe seguir siendo legible detrás.

## Arquitectura de información
- Archivo: comprobar Lore, Operaciones, Dificultad, Crónicas e Inspiraciones.
- Arsenal: comprobar que sólo contiene objetos/equipo y no dossiers enemigos.
- Manual de campo: comprobar Misiones, Estados y Protocolos.
- Intel: comprobar sólo dossiers de unidades y UNKNOWN.
- Ajustes/System/Diagnostics: comprobar que no aparezcan lore, estados de muerte ni equipo.
- Deployment: comprobar que no aparezca documentación general.

## Third Justice
- Abrir Arsenal → Third Justice.
- Confirmar ventana de parry 0,1 s, cooldown 1 s, −15% movimiento y consumo de hambre ×2 según el registro visual aportado.
- Confirmar que no se inventan daño, durabilidad, alcance ni estadísticas ausentes.
- Confirmar las dos capturas del objeto.
- Abrir REEL y comprobar anterior/siguiente, reproducción/pausa y cierre.
- Con Reducir movimiento activo, el reel debe iniciar pausado y no avanzar solo.
- Confirmar que el JAR no incluye el MP4 original de ~78 MB: sólo material visual preparado.

## Fondos 2.0
- Revisar las 13 escenas a pantalla completa y en Galería.
- Confirmar que los recursos empaquetados son 1920×1080 y 16:9 después del paso de preparación.
- Revisar especialmente Operación nocturna, Encuentro urbano, Escuadrón en azotea y Tempest Jutcherson.
- En Escuadrón en azotea comprobar que la composición 4:3 no queda estirada; el fondo extendido debe conservar el arte principal.
- Confirmar que Tempest Jutcherson sigue siendo anomalía rara y no entra en rotación de confort.
- Revisar Auto Contraste, oscuridad y scanlines sobre fondos claros/oscuros.
- Confirmar que la transición entre escenas no produce flashes con Reducir destellos.

## Intel / dossiers
- Categorías exactas: UNIT, ADVANCED, TANK, BOSS, ELITE, SUPER-UNIT y UNKNOWN. Sin All.
- No deben reaparecer Favoritos, Índice, Guardar ni Copiar.
- Un único control de Inspección/Ampliar por dossier.
- Dossier a la derecha; categorías/archivos a la izquierda en todas las escalas.
- Scroll, flechas y búsqueda deben conservar selección cuando corresponda.
- Dossier sin imagen: mostrar archivo visual no recuperado, nunca checker magenta/negro.
- Bosses: seis frames válidos, sin REC/STILL/frame counters sobre el arte.
- Hover de dossier animado: pausar y reanudar según la política existente.

## Despliegue
- El servidor oficial `SiegeLacontinuacion.exaroton.me:18736` debe estar primero y existir una sola vez.
- Editar y Eliminar deben estar desactivados para el servidor oficial con explicación visible.
- Estados: consultando, online, offline, sin respuesta e incompatible.
- Flujo visible: DESTINO → ESTADO → CONECTAR.
- Selección y scroll sobreviven a Actualizar.
- F5 sigue usando refresh vanilla; no añadir atajos nuevos SIEGE.
- Comprobar LAN y callbacks vanilla de conexión.

## Ajustes y pantallas nativas
- Revisar Apariencia, Movimiento, Audio, Intel, Accesibilidad, Fondos y Sistema.
- Mouse, Audio, Video, Controls, Keybinds, Accessibility, Language y Resource Packs deben recibir identidad SIEGE sin romper sus controles vanilla.
- Embeddium/Sodium y pantallas de terceros deben quedar intactas.
- No debe haber doble título, botones tapados ni máscaras que crucen el primer widget.

## Audio
- Shuffle sin repetición hasta agotar la lista.
- Siguiente pista funciona sin crear atajo no solicitado.
- REC aparece sólo en el anuncio inicial de pista, no sobre dossiers/boss frames.
- UI hover/click/confirm/warning/error respetan volumen UI.
- Música desactivada/volumen 0 no debe disparar loops de recuperación ni spam de audio.

## Diagnóstico y accesibilidad
- Diagnóstico muestra estado, impacto, recomendación y sólo reparaciones explícitamente seguras.
- La interfaz ordinaria no muestra readiness numérico.
- Alto contraste mantiene texto legible.
- Reducir movimiento pausa/reduce animaciones, reel y paneo de fondos según contrato.
- Reducir destellos tiene prioridad sobre interferencia.
- Rendimiento elimina costes visuales innecesarios sin romper navegación.

## Recursos y release
- `GuiResourceRegressionTest`: todos los PNG/JPG GUI decodifican.
- Fondos preparados: 1920×1080 exactos, 16:9.
- `GuideMediaRegressionTest`: material Third Justice existe y coincide con metadatos.
- Pruebas de Intel existentes continúan verdes.
- `gradle clean build` en Java 17 / Forge 1.20.1.
- PR verde antes de merge.
- Build de `main` verde y paso `Publish validated jar for installer` exitoso.
- `dist/manifest.json` debe indicar `2.0.0`, `siege-menu-2.0.0.jar`, commit y SHA-256 correctos.
