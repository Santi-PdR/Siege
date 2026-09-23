# SIEGE 5.30.0 — visual media overhaul

SIEGE 5.30 continúa 5.10 con una revisión completa de los fondos del menú y del material multimedia de Third Justice. El objetivo es mostrar fuentes reales con la mejor calidad que realmente tienen, no convertir archivos pequeños en falsos 1080p.

## Fondos

- La versión sube de `5.10.0` a `5.30.0`.
- Se elimina el contrato que obligaba a transformar todos los fondos existentes a 1920×1080.
- Las fuentes 960×540 se conservan a 960×540, sin reescalado destructivo.
- `night_operation` y `urban_rendezvous` se preparan a 720×405 mediante recorte central desde su captura casi 16:9; no se amplían.
- Esos dos fondos nocturnos reciben únicamente una recuperación suave de sombras para que no desaparezcan detrás de la interfaz; no se les aplica un filtro oscuro nuevo.
- `rooftop_squad` mantiene la ilustración completa sobre una extensión 16:9 suavizada; el primer plano se reduce y nunca se amplía.
- Los fondos normales quedan protegidos por un piso de 640×360 y relación 16:9 exacta.
- El render de pantalla completa usa escalado `cover`: mantiene la proporción, llena la pantalla y recorta el sobrante cuando la ventana no es 16:9, sin deformar la imagen.
- Se añade una auditoría automática que revisa dimensiones, contraste, rango tonal, detalle de bordes y posterización básica para detectar exports rotos o casi negros.
- Las dimensiones declaradas en `SiegeSceneCatalog` vuelven a representar el archivo real que recibe Minecraft.

## Dummies vs Noobs

- La búsqueda de fondos se concentra en material oficial del juego de Roblox, no en imágenes generadas.
- El fetch oficial usa el universo de DVN (`3293525400`) y la API de thumbnails de Roblox.
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
- El test completo entregado para 5.30 queda incluido como fuente compacta en `assets-source/third-justice/third_justice_full.b64`.
- Durante el build esa fuente se decodifica y se valida como video completo antes de generar la secuencia Forge-native.
- El reel se prepara a 10 fps y 640×360, conservando toda la duración del test (aprox. 31 s) y generando al menos 300 fotogramas.
- El build rechaza un archivo recortado de menos de 30 segundos para evitar que vuelva a publicarse accidentalmente una muestra parcial.
- El modo `fallback` de tres fotogramas queda sólo como seguridad para checkouts de desarrollo que no tengan la fuente; el contrato de release 5.30 exige `mode=full`.

## Validación 5.30

- Los 15 fondos normales pasan la auditoría de formato y calidad básica.
- Los tres fondos DVN se comprueban a 768×432 16:9 nativo.
- Las cinco imágenes de Third Justice deben superar el control de visibilidad después de la reparación.
- El release test comprueba que el reel de Third Justice sea completo, tenga al menos 300 frames, dure al menos 30 s y se renderice a 640×360.
- Los contratos antiguos de 5.10 ya no bloquean la tercera escena oficial de DVN añadida por 5.30.

## Compatibilidad y contratos conservados

- Minecraft 1.20.1.
- Forge 47.4.x / Java 17.
- Servidor oficial sin cambios: `SiegeLacontinuacion.exaroton.me:18736`.
- Singleplayer sigue oculto salvo `Ctrl+S`.
- Tempest Jutcherson sigue siendo únicamente un easter egg.
- No regresan Favoritos/Índice/Guardar/Copiar a Intel.
- La información pública sigue sin mostrar datos privados de jugadores ni lenguaje interno de investigación.
