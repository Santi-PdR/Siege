# SIEGE 2.0.0 — reconstrucción generacional

SIEGE 2.0.0 reorganiza el cliente alrededor de una arquitectura de información explícita, convierte Arsenal y Archivo en destinos reales, integra evidencia multimedia de Third Justice, endurece la cadena de recursos y corrige la presentación de los fondos recientes. No cambia Minecraft 1.20.1, Forge, reglas de servidor ni gameplay por cuenta propia.

## 80 cambios concretos

### Portada y navegación
1. La versión del proyecto pasa a `2.0.0`.
2. El porcentaje de readiness deja de mostrarse en el pie normal de la portada.
3. La salud del cliente se comunica de forma cualitativa en el chrome ordinario.
4. La fila que antes llevaba a un Arsenal ambiguo se convierte en dos destinos: **ARCHIVO** y **ARSENAL**.
5. Archivo y Arsenal reutilizan la misma fila para no alargar artificialmente el menú.
6. La portada conserva Despliegue como primera acción.
7. Intel continúa siendo acceso directo a dossiers.
8. Ajustes continúa siendo una sección independiente de conocimiento/lore.
9. Singleplayer permanece oculto salvo Ctrl+S.
10. El chrome compartido reconoce las nuevas superficies de referencia de 2.0.
11. Archivo recibe identidad de navegación propia.
12. Arsenal recibe identidad de navegación propia.
13. Manual de campo recibe identidad distinta de Intel.
14. El visor multimedia recibe identidad MEDIA.
15. El inspector de dossiers continúa separado de los artículos generales.

### Arquitectura de información
16. Se documenta formalmente una única residencia para cada tipo de conocimiento.
17. **Intel** queda reservado para unidades y dossiers.
18. **Archivo** concentra qué es SIEGE, 2044, facciones, lore, El Núcleo, Gates/Rifts, dificultad, crónicas e inspiraciones.
19. **Arsenal** concentra objetos y equipo.
20. **Manual de campo** concentra misiones, estados corporales y protocolos.
21. **Despliegue** queda reservado a servidores, estado y conexión.
22. **Ajustes** queda reservado a configuración del cliente.
23. **Centro de comando** queda reservado a perfiles/prioridades técnicas.
24. **Diagnóstico** queda reservado a problemas, impacto, recomendaciones y recuperación.
25. **Fondos** queda reservado a galería, rotación y metadatos visuales.
26. Los estados Downed/Mangled/Mutilated/Dismembered/Disfigured dejan de competir conceptualmente con dossiers.
27. Bleeding, Burned y Erased tienen hogar explícito en el Manual de campo.
28. SHELLSHOCK y otros protocolos permanecen en el Manual de campo.
29. Los objetos dejan de presentarse como si fueran dossiers enemigos.
30. Sistema/Diagnóstico dejan de ser rutas de acceso a conocimiento del mundo.

### Archivo y Arsenal
31. `SiegeGuideScreen` incorpora modos explícitos `ARCHIVE` y `ARMORY`.
32. Arsenal filtra únicamente la categoría ITEMS.
33. Archivo excluye ITEMS de sus pestañas.
34. Archivo ofrece una ruta visible al Manual de campo.
35. Arsenal no muestra la ruta del Manual como si fuera equipamiento.
36. El encabezado cambia según el modo abierto.
37. La búsqueda conserva su alcance en la sección actual.
38. Las listas conservan selección/paginación con la nueva separación.
39. La presentación de imágenes sigue utilizando el visor completo existente.
40. La arquitectura queda protegida por contratos de CI para evitar que vuelva a mezclarse.

### Third Justice
41. Third Justice queda oficialmente alojada en Arsenal.
42. La ficha se reescribe alrededor de la evidencia suministrada.
43. Se documenta la ventana de parry de 0,1 s mostrada en la captura.
44. Se documenta el cooldown de 1 s mostrado en la captura.
45. Se documenta la reducción de movimiento del 15% mientras se sostiene.
46. Se documenta el consumo de hambre ×2 mientras permanece en inventario.
47. El comportamiento observado en el video se distingue de estadísticas no confirmadas.
48. No se inventan daño, durabilidad ni alcance ausentes del material.
49. Se añade la captura original del tooltip de Third Justice.
50. Se añade la captura de Third Justice equipada durante la prueba de campo.
51. Se extraen tres fotogramas representativos del video suministrado.
52. Los fotogramas se almacenan como recursos GUI ligeros en lugar de empaquetar el MP4 completo.
53. Se crea `SiegeEvidenceReelScreen` para recorrer la evidencia.
54. El reel incluye anterior, reproducción/pausa y siguiente.
55. El reel respeta Reducir movimiento e inicia pausado cuando corresponde.
56. Arsenal muestra un control MEDIA sólo cuando la ficha dispone del reel correspondiente.
57. `GuideMediaRegressionTest` comprueba que la ficha y los cinco recursos existan.
58. El test multimedia comprueba dimensiones declaradas y decodificación ImageIO.

### Fondos y presentación visual
59. `SiegeSceneCatalog` normaliza la salida preparada de las 13 escenas a 1920×1080.
60. La salida preparada de fondos usa una relación 16:9 consistente.
61. Se añade `prepare-backgrounds-hd.py` como pipeline único de preparación visual.
62. El escalado controlado utiliza LANCZOS en lugar de depender sólo de ampliación runtime.
63. Se aplica un enfoque de sharpening moderado después del único resample controlado.
64. Operación nocturna elimina barras de captura antes de preparar su master 16:9.
65. Escuadrón en azotea conserva su ilustración 4:3 completa mediante extensión desenfocada, sin estirar personajes.
66. Las escenas estándar conservan sus etiquetas y sesgo de oscuridad existentes.
67. Rooftop Squad continúa marcado como FEATURED.
68. Tempest Jutcherson continúa marcado como ANOMALY.
69. Tempest Jutcherson continúa excluido de la rotación de confort.
70. El pipeline no afirma recuperar detalle inexistente: los originales de baja resolución siguen limitando el detalle real disponible.

### Recursos, CI y regresiones
71. El workflow prepara los masters visuales antes de ejecutar los gates de recursos.
72. CI decodifica PNG y JPG GUI, no sólo Intel.
73. Todos los recursos GUI deben tener dimensiones válidas y seguras.
74. Los 13 fondos del artefacto deben coincidir exactamente con el catálogo 1920×1080.
75. Los fondos deben cumplir 16:9 antes de permitir la compilación final.
76. Continúan los checks estrictos de Intel y los seis frames por Boss.
77. Los controles retirados de Intel (Favoritos, Índice, Guardar y Copiar) tienen gate para impedir su regreso.
78. CI verifica que el chrome ordinario no vuelva a invocar readiness numérico.
79. CI verifica las rutas Archivo/Arsenal/Manual/Media y que conocimiento del mundo no vuelva a System.
80. La publicación final mantiene el flujo rama → PR → Forge CI → merge → CI de `main` → JAR validado + manifest SHA-256.

## Calidad de los fondos

Los recursos recientes originales tienen menos resolución que 1920×1080. 2.0 prepara masters 1080p consistentes para evitar que cada pantalla haga ampliaciones diferentes y corrige relación de aspecto/composición, pero no etiqueta un upscale como detalle nativo inexistente. Si en el futuro se consiguen originales 1080p/4K reales, el mismo pipeline puede reemplazarlos sin cambiar la UI.

## Invariantes preservadas

- Minecraft 1.20.1 / Java 17 / Forge 47.4.x.
- Servidor oficial `SiegeLacontinuacion.exaroton.me:18736` primero, con Editar/Eliminar bloqueados.
- Callbacks vanilla autoritativos para red/conexión.
- Categorías Intel UNIT, ADVANCED, TANK, BOSS, ELITE, SUPER-UNIT y UNKNOWN.
- UNKNOWN continúa conservador y no se rellenan capacidades por intuición.
- Un único control de inspección/ampliación por dossier.
- Sin Favoritos/Índice/Guardar/Copiar.
- Música aleatoria sin repetición y reglas actuales de anuncio REC.
- Pantallas de terceros como Embeddium no se tematizan accidentalmente.
- Instalador raw + `dist/manifest.json` + SHA-256; no vuelve el método binario por `gh api`/base64.
