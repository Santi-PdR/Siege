# SIEGE 4.00.0 — Completion QA

## Home
- BRIEFING y DESPLIEGUE comparten la antigua fila de Despliegue sin crear una fila extra.
- OPERACIONES y AJUSTES conservan su fila compartida.
- Ctrl+S sigue siendo la única ruta visible hacia Singleplayer.
- Tempest Jutcherson no aparece en fondo/galería normal.

## War Room
- Deben existir rutas BRIEFING, ATLAS, RAZAS, PROGRESIÓN, AMENAZAS, MULTIMEDIA, DESPLIEGUE, INTEL, ENCICLOPEDIA, ARCHIVO, ARSENAL, MANUAL, COMANDO, DIAGNÓSTICO, AJUSTES y FONDOS.
- En GUI compacta, las rutas deben reorganizarse sin tapar búsqueda ni chrome inferior.
- Buscar `Obsainan race` debe encontrar RAZAS.
- Buscar `V1 V4 progression` debe encontrar PROGRESIÓN.
- Buscar `soundtrack dummies noobs` debe encontrar MULTIMEDIA.
- Los deep-links de Intel/Enciclopedia deben seguir abriendo la ficha concreta.

## Atlas de Razas
- Mostrar al menos 17 fichas iniciales.
- La rareza debe seguir: Común → Poco común → Raro → Ultra raro → Legendario → Obsainan → Mítico → Godly → Eternal → Fabled.
- Saiyan y Deteriorer: Obsainan.
- Apotheosis: Eternal.
- Las rarezas desconocidas deben decir SIN CONFIRMAR.
- No completar stats/requisitos que la enciclopedia no tenga.
- Buscar por nombre, rareza y sistema debe funcionar.
- Cada ficha debe abrir su fuente de enciclopedia.

## Mapa de Progresión
- PROGRESIÓN GENERAL debe tener exploración, ruta racial, Trials, investigación y sistemas avanzados.
- V1→V4 debe mantener cuatro nodos conceptuales.
- RUTAS ESPECIALES debe separar Saiyan/Cyborg/ocultas/Fabled.
- PROGRESIÓN AVANZADA debe enlazar Assembling, reliquias, dimensiones y raids.
- Ninguna ruta se presenta como receta universal si la fuente no lo demuestra.

## Sala Multimedia
- ANTERIOR / REINICIAR / SIGUIENTE controlan únicamente el soundtrack incluido.
- Rotación automática de fondos vuelve a `selectedScene=-1`.
- Las pistas DVN externas aparecen como REFERENCIA / NO INCLUIDAS.
- No debe existir código que descargue automáticamente Convenience Store, New Store, Jazz Music, From the Ashes o Sad Choir.
- Las recomendaciones de nuevos fondos deben exigir 16:9 y preferentemente 1920×1080 o más.

## Perfiles
- CINEMÁTICO, TÁCTICO, RENDIMIENTO, TRANQUILO y LECTURA siguen existiendo.
- CLÁSICO debe ser visualmente sobrio y conservar fondos/Intel sin interferencia fuerte.
- ALTO CONTRASTE debe activar high contrast y reducir flashes/movimiento.
- INMERSIVO debe activar la presentación ambiental completa.
- Cambiar manualmente un valor debe permitir volver a CUSTOM si ya no coincide con un preset.

## Regresiones
- Servidor oficial intacto; Editar/Eliminar siguen bloqueados.
- UNKNOWN intacto en Intel.
- Sin Favoritos/Índice/Guardar/Copiar en Intel.
- Sin REC/STILL sobre boss frames.
- Sin `readiness %` críptico en navegación normal.
- Sin datos personales de jugadores en Atlas/Enciclopedia/Progresión.
- Sin música externa redistribuida por búsqueda web.

## Release
1. PR CI completamente verde.
2. `gradle clean build` verde.
3. Artifact del PR generado.
4. Squash merge a main.
5. Main CI verde.
6. `Publish validated jar for installer` = success.
7. Verificar manifest, commit, SHA-256 y JAR final en `dist/`.
