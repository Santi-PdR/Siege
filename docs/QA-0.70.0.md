# QA visual — SIEGE 0.70.0

## Intel
- Abrir Intel en 1280×720 GUI 2 y 1920×1080 GUI 3.
- Confirmar categoría **DESCONOCIDO / UNKNOWN** visible sin solapes.
- Confirmar que Desconocido contiene únicamente Grappler, Skydiver y Skyliner.
- Confirmar Engineer, Informant y Tranquilizer dentro de **Advanced**.
- Confirmar Agitator dentro de Tank.
- Confirmar Sparta y Proteus dentro de Boss.
- Confirmar Fauna y Cerberus dentro de Elite.
- Confirmar Sauron y Hedalus dentro de Super Unit.
- Revisar Engineer: 150 HP, pistola/llave, Sentry y Teleporter.
- Revisar Informant: 155 HP, H94 Rifle, granadas y descripción visual DVN.
- Revisar Tranquilizer: 100 HP, Dart Rifle, máscara de gas y contexto Epilogue/Hell.
- Revisar Agitator: 300 HP, unidad mecánica/blindada y tanque de combustible vulnerable.
- Revisar Sparta: Khanblades, jetpack, stomp y cadenas de cortes.
- Revisar Proteus: Spectral Shotgun, Hivelink, Cloning/Spectral Leap y aviso explícito de desarrollo activo.
- Abrir todas las tropas nuevas y comprobar que nunca aparece la textura morada/negra de Minecraft.
- Confirmar placeholder documental válido cuando no exista asset local verificado.
- Probar AMPLIAR sobre un placeholder y comprobar que sigue siendo un documento válido.
- Probar búsqueda dentro de Desconocido y contadores `UNK`.

## Despliegue / Multiplayer
- Confirmar servidor oficial en primera posición y sin duplicados.
- Confirmar Editar/Eliminar bloqueados sólo para el servidor oficial.
- Confirmar Conectar, Conexión directa y Agregar con callbacks vanilla.
- Confirmar estados CONSULTANDO, APAGADO/SIN RESPUESTA, INCOMPATIBLE y EN LÍNEA cuando puedan reproducirse.
- Confirmar badge del botón CONECTAR al cambiar selección.
- Confirmar banda y etiqueta de latencia en un servidor online.
- Confirmar comparación CLIENTE/SERVIDOR cuando el protocolo sea incompatible.
- Confirmar `DESTINO → ESTADO → CONECTAR` en el panel ancho.
- Confirmar resumen compacto sin solapes en anchos menores a 640.
- Confirmar Actualizar y F5 preservando selección/scroll.
- Confirmar LAN como fila informativa no seleccionable.
- Confirmar MOTD largo recortado/ajustado dentro del panel.

## Regresión
- Centro de Comando 0.60 sigue abriendo Diagnóstico y Recuperación.
- No reaparecen Favoritos, Índice, Guardar ni Copiar en Intel.
- No aparecen nuevos atajos SIEGE.
- Música, fondos y navegación principal siguen funcionando.
- Ningún dato de una wiki/fangame se presenta como definitivo cuando la fuente indica que sigue en desarrollo.
