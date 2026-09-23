# SIEGE 5.30.0 — visual media overhaul

SIEGE 5.30 continúa 5.10 con una revisión completa de los fondos del menú y del material multimedia de Third Justice. El objetivo es mostrar fuentes reales con la mejor calidad que realmente tienen, no convertir archivos pequeños en falsos 1080p.

## Fondos

- La versión sube de `5.10.0` a `5.30.0`.
- Se elimina el contrato que obligaba a transformar todos los fondos existentes a 1920×1080.
- Las fuentes 960×540 se conservan a 960×540, sin reescalado destructivo.
- `night_operation` y `urban_rendezvous` se preparan a 720×405 mediante recorte central desde su captura casi 16:9; no se amplían.
- `rooftop_squad` mantiene la ilustración completa sobre una extensión 16:9 suavizada; el primer plano se reduce y nunca se amplía.
- Los fondos normales quedan protegidos por un piso de 640×360 y relación 16:9 exacta.
- Se añade una auditoría automática que revisa dimensiones, contraste, rango tonal, detalle de bordes y posterización básica para detectar exports rotos o casi negros.
- Las dimensiones declaradas en `SiegeSceneCatalog` vuelven a representar el archivo real que recibe Minecraft.

## Dummies vs Noobs

- La búsqueda de fondos se concentra en material oficial del juego de Roblox, no en imágenes generadas.
- El fetch oficial continúa usando el universo de DVN (`3293525400`) y la API de thumbnails de Roblox.
- La galería pasa de dos a tres thumbnails oficiales: `dvn_official_01`, `dvn_official_02` y `dvn_official_03`.
- Cada thumbnail se valida a 768×432 y 16:9 antes de entrar al JAR.
- Los tres se conservan a resolución nativa; no se les inventa detalle mediante upscale a Full HD.
- Se mantiene el nombre genérico de cada escena para no inventar una ubicación si Roblox cambia el orden de su galería oficial.
- Tempest Jutcherson continúa fuera de la rotación y la galería normal.

## Third Justice

- Las dos capturas y los tres fotogramas históricos se procesan para corregir el rango tonal aplastado que los hacía verse casi negros.
- La reparación expande la información luminosa existente; no coloca un filtro cinematográfico encima.
- El visor del reel deja de renderizar la capa oscura que dominaba la presentación visual.
- El video usa ahora un entorno neutro para que el material sea el protagonista.
- Se añade tiempo transcurrido, duración y barra de progreso.
- Reducir movimiento hace que el video empiece pausado, pero el jugador puede reproducirlo manualmente.
- Se añade `SiegeThirdJusticeVideo`, que lee un manifiesto de frames preparado durante el build.
- Si está disponible `assets-source/third-justice/third_justice_full.mp4`, el build convierte **toda la duración** del test a una secuencia Forge-native de 10 fps, 640×360, con primer y último tramo incluidos.
- Si el MP4 original no está presente, SIEGE lo declara explícitamente como `fallback` y usa los tres registros recuperados. No se finge que esos tres fotogramas sean el video completo.

## Estado del video original

El repositorio histórico nunca guardó el MP4 completo: SIEGE 2.0 había almacenado sólo tres fotogramas representativos. Por eso 5.30 deja terminada la reproducción completa y la cadena de extracción, pero el JAR sólo podrá contener el test entero cuando el archivo original vuelva a estar disponible para el build.

## Compatibilidad y contratos conservados

- Minecraft 1.20.1.
- Forge 47.4.x / Java 17.
- Servidor oficial sin cambios: `SiegeLacontinuacion.exaroton.me:18736`.
- Singleplayer sigue oculto salvo `Ctrl+S`.
- Tempest Jutcherson sigue siendo únicamente un easter egg.
- No regresan Favoritos/Índice/Guardar/Copiar a Intel.
- La información pública sigue sin mostrar datos privados de jugadores ni lenguaje interno de investigación.
