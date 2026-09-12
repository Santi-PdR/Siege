# Revisión integral de SIEGE 0.10.5

Base revisada: `27fcbb50c128b7f60d1d394fddf8f49a6be836aa` (main con JAR 0.10.5). Revisión estática del código, recursos declarados, instalador y CI. No se ejecutó Minecraft ni se midieron FPS, memoria o audio real. Las capturas del usuario sirven como antecedentes visuales, no como validación de propuestas nuevas.

## Diagnóstico

La prioridad es estabilizar lectura, estados e interacción. La distribución lateral de Intel debe conservarse. Añadir más controles antes de corregir duplicaciones, trabajo por frame y pruebas incompletas aumentaría el mantenimiento sin garantizar una mejora visible.

## Hallazgos y propuestas, por prioridad

“Código” indica una condición observable en la implementación; “Riesgo” requiere reproducir el escenario; “Propuesta” es una mejora de diseño, no un fallo confirmado.

| # | Prioridad / evidencia | Hallazgo y cambio recomendado | Fuente |
|---|---|---|---|
| 1 | Alta · Código | La cabecera del dossier de portada usa todo el ancho cuando no hay hover, aunque AUTO/FIJO se dibuja en esa misma línea. Reservar el ancho real del estado y un margen. | `SiegeTitleScreen.renderIntelCard` |
| 2 | Alta · Código | El nombre de tropa de portada se dibuja sin límite de ancho. Ajustarlo a la tarjeta y permitir consultar el nombre completo. | `SiegeTitleScreen.renderIntelCard` |
| 3 | Alta · Código | El slider compacto de 19 px coloca texto en y+4 y el tirador entre y+10 e y+18: ambas zonas se cruzan verticalmente. Separar etiqueta, valor y pista; probar con la fuente real. | `SiegeSlider.renderWidget`, `SiegeSettingsScreen.initSectionControls` |
| 4 | Alta · Riesgo | `isActuallyPlaying()` sólo comprueba que exista el objeto. Una pista descartada por el motor puede seguir figurando como reproduciéndose hasta agotar su duración. Incorporar detección de inactividad sostenida, gracia inicial y recuperación acotada; nunca reaccionar a un único frame inactivo. | `SiegeMusic.tick`, `ensurePlaying`, `isActuallyPlaying` |
| 5 | Alta · Código | El catálogo se obtiene por reflexión de una pantalla antigua. Si falla, se devuelve una lista vacía sin diagnóstico. Separar los datos del render y validar códigos, categorías y recursos. | `IntelCatalog.loadFiles`, `IntelScreen.FILES` |
| 6 | Alta · Código | Las pruebas de geometría no renderizan fuentes ni controles reales; no prueban la ficha táctica, los sliders ni las cabeceras de portada. Añadir casos de estas regiones y una matriz visual reproducible con Minecraft. | `tests/UiRegressionTest.java` |
| 7 | Alta · Código | La navegación, hover y protección de atajos dependen en parte de buscar cadenas en Java. Esto no prueba la reanudación tras 2 s ni el comportamiento del audio. Extraer modelos de estado y probar secuencias y tiempos. | `.github/workflows/build.yml` |
| 8 | Media · Código | Intel vuelve a crear y envolver el cuerpo completo del expediente en cada render. Cachear por código, idioma, anchura, modo y ajustes de lectura. Medir antes/después; no prometer una cifra de FPS. | `IntelScreenV3.renderFile` |
| 9 | Media · Código | La ficha táctica vuelve a envolver su contenido cada frame. Compartir una preparación de texto invalidable con el cuerpo principal. | `IntelScreenV3.renderTacticalSummary` |
| 10 | Media · Código | La portada recrea la lista de unidades/avanzados cada frame; los datos son estáticos. Prepararla una sola vez. | `IntelCatalog.previewable`, `SiegeTitleScreen.renderIntelPreview` |
| 11 | Media · Riesgo | La posición de lectura se conserva por línea, no por párrafo. Cambiar ancho, idioma o interlineado puede trasladar al lector a otra parte del texto. Conservar un ancla de contenido y probar redimensionado. | `IntelScreenV3.readingPositions` |
| 12 | Media · Código | La ficha táctica repite vida, defensa y advertencia presentes a la izquierda. Convertirla en un resumen jerarquizado y distinguir información resumida de la lectura completa, sin inventar datos de tropas. | `IntelScreenV3.renderTacticalSummary` |
| 13 | Media · Código | “VISTA LIMPIA” y “VER COMPLETO” ejecutan la misma acción. Dejar un único acceso. La ayuda de VER COMPLETO todavía habla de recortar aunque los fondos ya se ajustan completos. | `SiegeSceneScreen.init`, `refresh` |
| 14 | Media · Código | La vista de contraste de galería sólo aproxima el panel: usa un cuarto del ancho y omite la sombra fija adicional del render principal. Compartir las reglas de oscuridad para que la previsualización coincida. | `SiegeSceneScreen.renderContrast`, `SiegeBackgrounds.render`, `SiegeTitleScreen.render` |
| 15 | Media · Código | El fondo añade una sombra izquierda fija además del panel configurable. Unificar estas capas para que el ajuste tenga un resultado predecible. | `SiegeBackgrounds.render`, `SiegeTitleScreen.render` |
| 16 | Media · Código | Las bandas libres de las miniaturas muestran el color de selección del rectángulo exterior, porque el render de imagen contenida no pinta fondo. Dar a la imagen una base neutra y reservar el color al borde. | `SiegeSceneScreen.Thumbnail.renderWidget`, `SiegeBackgrounds.drawScene` |
| 17 | Media · Código | Configuración sobrescribe las ayudas de todos los controles con su propio nombre; los toggles repiten esa sustitución al cambiar. Mantener descripciones que expliquen el efecto y el estado. | `SiegeSettingsScreen.init`, `toggle`, `literalToggle` |
| 18 | Media · Código | La barra de configuración convierte la posición del cursor directamente a porcentaje, sin conservar el punto agarrado del tirador. Aplicar el comportamiento corregido en Intel. | `SiegeSettingsScreen.mouseClicked`, `dragScroll` |
| 19 | Media · Código | Música tiene título, tiempos/progreso arriba y otra representación de estado/pista/progreso abajo. Consolidar el reproductor en una región estable y dejar más espacio a sus opciones. | `SiegeSettingsScreen.renderSectionHeader`, `renderSectionInformation` |
| 20 | Media · Código | El aviso de pista empieza al solicitar el sonido, antes de confirmar activación. Iniciarlo al confirmar reproducción; definir el caso de fallo para no anunciar audio inexistente. | `SiegeMusic.playIndex`, `tick` |
| 21 | Media · Código | La transición musical llamada crossfade es realmente salida de una pista y posterior entrada de otra: sólo existe una instancia activa. Corregir el nombre/documentación o implementar solapamiento real cuidadosamente. | `SiegeMusic.tick`, `startNext` |
| 22 | Media · Código | Sobreviven pantallas V1/V2, Índice, funciones de Favoritos y pruebas de características retiradas. Migrar primero el catálogo y luego retirar el código inaccesible; no restaurar sus botones. | `IntelScreen*`, `IntelIndex*`, `SiegeConfig`, tests |
| 23 | Media · Código | No hay grupo de concurrencia de CI. Dos pushes seguidos pueden hacer que un build antiguo falle al publicar por no ser fast-forward. Serializar/cancelar ejecuciones superadas y comprobar la revisión antes de publicar. | `.github/workflows/build.yml` |
| 24 | Media · Código | Se regeneran música e imágenes en cada build, aunque cambie sólo una etiqueta. Cachear por hash de originales y scripts, conservando las comprobaciones de codec, duración y recursos. | workflow, `prepare-music.sh`, `prepare-intel-assets.py` |
| 25 | Media · Código | El instalador toma el primer JAR que encuentra. La copia se compara y existe respaldo, pero no se exige exactamente un candidato ni un manifiesto que relacione versión, commit y hash. Añadir esas garantías y verificar contenido ZIP antes de sustituir. | `scripts/install-latest.sh` |
| 26 | Media · Código | El instalador usa una ruta fija y clona el repositorio completo. Mantener test-1 como valor predeterminado, permitir otra ruta explícita y descargar sólo el artefacto validado con autenticación. | `scripts/install-latest.sh` |
| 27 | Baja · Código | ForgeGradle usa un rango de versiones. Fijar las herramientas verificadas para mejorar reproducibilidad; conservar Forge 1.20.1 actual salvo solicitud. | `build.gradle` |
| 28 | Media · Propuesta | Establecer una jerarquía visual única en Intel: identidad, datos, perfil y advertencia; menos abreviaturas donde quepan nombres completos, márgenes y contraste coherentes. Mantener imagen y ficha a la derecha. | Intel |
| 29 | Media · Propuesta | Sustituir barridos continuos por animaciones cortas al entrar, seleccionar o cambiar expediente. Respetar Movimiento reducido y evitar destellos sobre texto. Comparar en juego antes de decidir. | `SiegeButton`, Intel |
| 30 | Media · Propuesta | Crear una matriz visual de aceptación: escalas 1–4, español/inglés, lectura con/sin imagen, texto más largo, cero/uno/muchos resultados, perfiles gráficos, ventana pequeña y ultrapanorámica. Capturas y checklist vinculados a una versión exacta. | QA |

## Orden recomendado

1. **Correcciones y garantías:** cabeceras y sliders; audio con estados verificables; ayudas; duplicaciones de galería; pruebas del comportamiento real.
2. **Lectura y rendimiento:** caché de contenido, anclas de lectura, ficha táctica mejor jerarquizada, sombras consistentes y recursos del catálogo separados de pantallas antiguas.
3. **Acabado y entrega:** animaciones discretas verificadas en juego, documentación consolidada, CI reproducible y artefactos con manifiesto.

## Lo que conviene conservar

- Dossier a la derecha, navegación a la izquierda y arte original.
- Un único Ampliar; sin Favoritos, Índice, Guardar ni Copiar.
- Fondos completos y acceso desde Configuración.
- Flechas de dossiers y Ctrl+S de staff; sin nuevos atajos ocultos.
- Separación del gameplay: el audio tiene guardia `level == null` y el evento de pantalla sólo sustituye TitleScreen.
- Configuración con sustitución atómica y límites numéricos; instalador con copia previa y respaldo.
- Pruebas existentes de zoom, límites y configuración; validación de duración/codec musical y recursos Intel.

## Límites y estado de entrega

Esta revisión no publica una versión nueva del mod ni declara resueltos los hallazgos. El JAR vigente sigue siendo 0.10.5. Las observaciones de código son verificables mediante los métodos indicados; audio real, apariencia, fluidez y compatibilidad con el conjunto de mods del usuario requieren pruebas dentro de Minecraft. No se prometen mejoras cuantitativas sin medición.
