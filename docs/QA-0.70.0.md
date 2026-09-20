# QA visual — SIEGE 0.70.0

## Intel
- Abrir Intel en 1280×720 GUI 2 y 1920×1080 GUI 3.
- Confirmar categoría **DESCONOCIDO / UNKNOWN** visible sin solapes.
- Confirmar que contiene Engineer, Informant, Grappler, Tranquilizer, Skydiver y Skyliner.
- Confirmar que Agitator sigue en Tank.
- Confirmar que Sparta sigue en Boss.
- Confirmar Fauna, Cerberus y Proteus en Elite.
- Confirmar Sauron y Hedalus en Super Unit.
- Abrir cada una de las tropas anteriores y comprobar que nunca aparece la textura morada/negra de Minecraft.
- Confirmar placeholder de dossier y texto **SIN REGISTRO VISUAL / NO VISUAL RECORD**.
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
