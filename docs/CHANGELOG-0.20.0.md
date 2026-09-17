# SIEGE 0.20.0

## Agreement: información aportada por el usuario

El expediente TNK-003 incorpora el texto y la captura facilitados por el usuario. Está disponible en español e inglés. Se conservan 3.000 HP, 100 DEF, amenaza desconocida y su vínculo con Secure Contain Protect.

El perfil separa Gates, Rifts y visores. Gates se describen como teletransportadores caseros y caros; Rifts, como viajes interdimensionales comparados con portales de Blox Fruits y fluido de portales. La comparación con Rick Sanchez se conserva como analogía informal, no como equivalencia de poderes.

La advertencia táctica recoge el sabotaje repetido y más rápido que la respuesta de Agreement, y la posible saturación de sus visores mediante portales ajenos. No presenta esa táctica como una debilidad única demostrada. El reporte está identificado como testimonio sin verificar; no se inventan alcance, duración, coste o recarga. En la ficha derecha, REPORTE y una línea discontinua sustituyen al porcentaje de datos para no sugerir certeza.

Código: `AgreementReport.java`, `IntelData.java`, `IntelScreenV3.java`. La advertencia sigue únicamente en la ficha derecha cuando esa ficha se muestra.

## 50 mejoras adicionales de interfaz

Son cambios nuevos respecto de 0.19.0. La información de Agreement y las pruebas no se cuentan dentro de estos 50 puntos. Incluyen cambios de presentación, interacción y ayudas contextuales; no son 50 pantallas nuevas.

1. Interruptores con estado SÍ/NO separado del nombre para que un rótulo largo no lo oculte. — `SiegeButton / SiegeSettingsScreen`.
2. Marca de selección reconocible como un check, en lugar de dos rayas decorativas. — `SiegeButton`.
3. Al desactivar un botón desaparece inmediatamente el brillo de interacción residual. — `SiegeButton`.
4. El barrido lateral ya no invade los pictogramas de los botones. — `SiegeButton`.
5. Sliders con rótulo atenuado cuando están desactivados y tirador con una marca de agarre. — `SiegeSlider`.
6. Secciones de Configuración con pictogramas propios: resumen, música, interfaz, Intel, accesibilidad y gráficos. — `SiegeSettingsScreen / SiegeTheme`.
7. Interruptor de música dorado, coherente con el resto de su sección. — `SiegeSettingsScreen`.
8. Volumen y controles de transporte indican visualmente que la música está desactivada. — `SiegeSettingsScreen`.
9. Duración del aviso desactivada cuando los avisos de pista están apagados. — `SiegeSettingsScreen`.
10. Probar sonidos muestra MUDO y explica cómo habilitarlo cuando los efectos no pueden oírse. — `SiegeSettingsScreen`.
11. La canción que realmente está reproduciéndose tiene icono de reproducción y distintivo SUENA, independiente de la pista fijada. — `SiegeSettingsScreen`.
12. La cabecera de música distingue volumen cero de música desactivada. — `SiegeSettingsScreen`.
13. Progreso de canción con marcas de cuartos para estimar visualmente cuánto queda. — `SiegeSettingsScreen`.
14. Resumen de Configuración con tarjetas compactas de música y fondo en lugar de cadenas de estados. — `SiegeSettingsScreen`.
15. Confirmación temporal al aplicar el perfil tranquilo, conservando el estado actualizado de los controles. — `SiegeSettingsScreen`.
16. Aviso de ajustes sin guardar visible en la cabecera, sin tener que desplazarse al final. — `SiegeSettingsScreen`.
17. Barra de Configuración con marca de agarre; se retira el porcentaje que podía superponerse a los controles. — `SiegeSettingsScreen`.
18. Ayuda de sonido al señalar explica cuándo se reproduce y que no se repite continuamente. — `SiegeSettingsScreen.settingHelp`.
19. Ayuda de efectos tácticos explica su relación con movimiento reducido. — `SiegeSettingsScreen.settingHelp`.
20. Ayuda de Mostrar build explica cómo identificar la versión instalada. — `SiegeSettingsScreen.settingHelp`.
21. Ayuda de confirmación al salir aclara que actúa al cerrar Minecraft desde portada. — `SiegeSettingsScreen.settingHelp`.
22. Ayuda del modo lectura explica el espacio de texto y la disponibilidad del visor. — `SiegeSettingsScreen.settingHelp`.
23. Ayuda de Intel de portada identifica las categorías mostradas y su posición. — `SiegeSettingsScreen.settingHelp`.
24. Ayuda de animaciones de Intel distingue retratos animados de rotación de expedientes. — `SiegeSettingsScreen.settingHelp`.
25. Ayuda de rotación automática aclara que las flechas siguen funcionando al desactivarla. — `SiegeSettingsScreen.settingHelp`.
26. Ayuda de líneas de escaneo permite decidir si desactivarlas para mejorar la lectura. — `SiegeSettingsScreen.settingHelp`.
27. Ayuda de interferencia explica por qué no aparece con movimiento reducido. — `SiegeSettingsScreen.settingHelp`.
28. Miniaturas de Galería numeradas para localizar una imagen con facilidad. — `SiegeSceneScreen`.
29. Miniatura seleccionada con doble marco y check, distinta del fondo fijado. — `SiegeSceneScreen`.
30. Marca de reproducción sobre el fondo que está en uso durante la rotación. — `SiegeSceneScreen`.
31. Estado SIN APLICAR distingue una vista previa de una elección ya aplicada. — `SiegeSceneScreen`.
32. Nombre completo de un fondo disponible al señalar la cabecera si no cabe. — `SiegeSceneScreen`.
33. Títulos de miniaturas con puntos suspensivos cuando se recortan y nombre completo en su ayuda. — `SiegeSceneScreen`.
34. ACTUAL se desactiva cuando ya estás viendo el fondo que usa el menú. — `SiegeSceneScreen`.
35. DESHACER explica cuándo estará disponible y qué selección restaurará. — `SiegeSceneScreen`.
36. FIJAR y ROTACIÓN explican que la acción ya está aplicada cuando están desactivados. — `SiegeSceneScreen`.
37. CONTRASTE muestra explícitamente SÍ/NO en lugar de depender sólo del color. — `SiegeSceneScreen`.
38. Gestos pequeños de touchpad se acumulan antes de cambiar imagen o página, evitando saltos por cada fracción. — `SiegeSceneScreen`.
39. La Galería muestra el fallo de guardado en lugar de presentar la elección como guardada. — `SiegeSceneScreen`.
40. Minimapa del visor con zona exterior oscurecida y marco sensible al cursor para localizar el área visible. — `IntelPortraitScreen`.
41. La ayuda del visor cambia entre imagen completa, exploración y arrastre activo. — `IntelPortraitScreen`.
42. Escala visual de zoom con referencias 1×–4× situada fuera de la imagen. — `IntelPortraitScreen`.
43. Ayudas del visor muestran el título completo si se recorta y explican que el porcentaje es relativo al ajuste completo. — `IntelPortraitScreen`.
44. Minimapa explica por qué no aparece: ventana pequeña o imagen todavía sin ampliar. — `IntelPortraitScreen`.
45. CENTRAR sólo está disponible cuando la imagen ampliada está desplazada. — `IntelPortraitScreen`.
46. Los botones de zoom explican cuándo se alcanzó el ajuste completo o el máximo de 400%. — `IntelPortraitScreen`.
47. Marco dorado durante el arrastre de la imagen para identificar la interacción activa. — `IntelPortraitScreen`.
48. Búsqueda de Intel con coincidencias por categoría; el estado vacío orienta hacia esos contadores sin añadir Todas. — `IntelScreenV3`.
49. Papel oscuro de Intel en carbón cálido y acentos propios de cada categoría. — `IntelScreenV3`.
50. Lectura de Intel con separadores discretos entre bloques y FIN DEL TEXTO al alcanzar el final. — `IntelScreenV3`.

## Restricciones conservadas

Dossier a la derecha, posiciones de portada, advertencia fuera de la columna izquierda, imágenes originales completas, Fondos dentro de Configuración, categorías sin Todas, servidor oficial y sus bloqueos. Sin Favoritos/Índice/Guardar/Copiar ni nuevos atajos. Sin cambios de gameplay, inventario, chat, Video Settings, unidades adicionales o ventajas ocultas.

## Validación

La compilación y la suite completa se ejecutan en GitHub Actions antes de publicar. Se amplían las comprobaciones de límites de iconos, 98.400 distribuciones de etiqueta/estado y procedencia del reporte; se conserva la suite de geometría, catálogo, música e instalación. Estas pruebas no sustituyen ver la interfaz en Minecraft. Revisión visual en escalas 1–4 pendiente; ver `QA-0.20.0.md`.

Respaldo anterior: `backup/pre-0.20.0`.
