# SIEGE 1.25.0 — Generational Overhaul

1. La versión de producción avanza de 0.75.0 a 1.25.0.
2. Los perfiles del cliente pasan a tener una única especificación autoritativa en `SiegeProfileSpec`.
3. Aplicar perfiles y detectar el perfil activo dejan de mantener definiciones duplicadas.
4. El cálculo de cercanía de un perfil personalizado usa exactamente la misma especificación que aplica el preset.
5. La coherencia de perfiles se amplía para incluir 22 campos de presentación.
6. Se añade contraste automático como preferencia persistente del cliente.
7. Se añade intensidad configurable de scanlines de 0 a 100%.
8. Se añade intensidad configurable de interferencia de 0 a 100%.
9. Los tres controles nuevos se guardan y cargan sin alterar la revisión histórica 801.
10. Los valores de intensidad se limitan de forma segura al cargar y guardar.
11. Reducir destellos ahora fuerza la interferencia a 0 además de desactivarla.
12. Los perfiles Cinemático, Táctico, Rendimiento, Tranquilo y Lectura incluyen valores explícitos para los nuevos controles.
13. El render de fondos incorpora mínimos de contraste automáticos.
14. El panel lateral incorpora mínimos de oscuridad automáticos independientes del fondo elegido.
15. Las scanlines dejan de ser solo un interruptor y usan intensidad real en su alpha.
16. Rendimiento conserva el objetivo de evitar scanlines e interferencia.
17. Se crea una clasificación responsive común: ultra compacta, compacta, estándar y amplia.
18. Se centralizan márgenes seguros según el viewport.
19. Se centralizan alturas de control según la densidad disponible.
20. Configuración se reorganiza en Apariencia, Movimiento, Audio, Intel, Accesibilidad, Fondos y Sistema.
21. Apariencia concentra perfil de render, etiqueta de build, oscuridad del panel y perfiles visuales.
22. Movimiento concentra efectos, movimiento reducido, destellos e interferencia.
23. Audio conserva volumen de música, pista anterior, reinicio, siguiente pista y aviso de canción.
24. Audio conserva el modo aleatorio sin repetición y la selección manual de las cuatro pistas.
25. Audio concentra también sonidos UI, volumen UI y hover sonoro.
26. Intel concentra lectura, papel, portada, animaciones, rotación, pausa y progreso del dossier.
27. Accesibilidad agrupa contraste automático, alto contraste, destellos, movimiento y perfiles de confort.
28. Fondos agrupa rotación, oscuridad, scanlines, intensidad, galería y reanudación automática.
29. Sistema agrupa Centro de Comando, Diagnóstico, opciones nativas, Guía, confirmación de salida y reset.
30. La navegación de Configuración adapta sus columnas al ancho disponible.
31. Los controles de Configuración se desplazan con scroll independiente del resto de la pantalla.
32. Se añade scrollbar arrastrable a las secciones que superan el espacio visible.
33. La selección de sección conserva su posición de scroll durante la sesión.
34. Se crea `SiegeNavigationModel` como identidad compartida de las superficies SIEGE.
35. La identidad compartida define sección, código y acento para Portada, Despliegue, Intel, Guía, Configuración, Comando, Diagnóstico, Fondos e Inspector.
36. Se crea `SiegeScreenChrome` como capa operacional común de versión, perfil, sección y salud.
37. La Portada recibe una placa dinámica de build, perfil y estado que cubre la etiqueta histórica antigua.
38. Despliegue recibe un encabezado dinámico 1.25 que sustituye visualmente la etiqueta 0.70 heredada.
39. Las demás superficies reciben una identidad de sección compacta cuando existe espacio seguro.
40. Diagnóstico pasa a priorizar CRÍTICO, ATENCIÓN y OPERATIVO.
41. Cada entrada de diagnóstico incorpora impacto además del estado técnico.
42. Cada entrada de diagnóstico incorpora una recomendación concreta.
43. Las reparaciones nunca se ejecutan automáticamente: requieren una acción explícita.
44. Diagnóstico puede restaurar un volumen de música audible cuando el canal está activo a 0%.
45. Diagnóstico puede restaurar efectos UI audibles cuando el canal está activo a 0%.
46. Diagnóstico puede resolver el conflicto entre reducción de destellos e interferencia.
47. Diagnóstico puede convertir una configuración de Rendimiento incoherente en una presentación visual ligera.
48. Diagnóstico puede alinear un perfil personalizado al preset más cercano solamente cuando se solicita.
49. Diagnóstico puede activar contraste automático como reparación segura de legibilidad.
50. La pantalla de Diagnóstico incorpora selección de subsistema y detalle separado en layouts amplios.
51. El detalle de Diagnóstico muestra estado, impacto y recomendación sin saturar la lista principal.
52. Diagnóstico conserva un layout compacto de una sola columna en pantallas estrechas.
53. El Centro de Comando se reconstruye alrededor de conteos de prioridad y preparación.
54. El Centro de Comando conserva los cinco perfiles completos y muestra la deriva de CUSTOM.
55. El Centro de Comando conecta directamente Configuración y Diagnóstico sin mezclar lore con opciones.
56. La salud global considera también avisos de atención, no solo errores y warnings severos.
57. Los estados globales pasan a usar las mismas palabras CRÍTICO, ATENCIÓN y OPERATIVO.
58. Se añade una capa semántica de audio UI para selección, dossier, categoría, warning y error.
59. Los nuevos sonidos semánticos reutilizan recursos existentes y aplican cooldown para evitar spam.
60. El estado de Despliegue incorpora una ruta operacional distinta para online, consulta, incompatibilidad y falta de respuesta.
61. Despliegue incorpora una etiqueta reutilizable de compatibilidad cliente-servidor.
62. Despliegue centraliza las bandas de latencia BAJA, MEDIA, ALTA y MUY ALTA.
63. Se mantiene el servidor oficial `SiegeLacontinuacion.exaroton.me:18736` y su protección Editar/Eliminar.
64. Se preservan los callbacks vanilla como autoridad de conexión y persistencia.
65. Se preservan las categorías UNIT, ADVANCED, TANK, BOSS, ELITE, SUPER-UNIT y UNKNOWN.
66. Se preservan Engineer, Informant y Tranquilizer como Advanced; Agitator como Tank; Sparta y Proteus como Boss.
67. Se preservan Fauna y Cerberus como Elite; Sauron y Hedalus como Super Unit.
68. Grappler, Skydiver y Skyliner continúan en UNKNOWN mientras no exista clasificación suficientemente fiable.
69. La validación de imágenes Intel sigue decodificando físicamente cada PNG mediante `ImageIO`.
70. La validación 1.25 exige una resolución mínima de 320×180 para imágenes Intel.
71. La validación 1.25 rechaza recursos Intel por encima de 4096×4096.
72. La validación 1.25 exige proporción 16:9 con una tolerancia estrecha.
73. Los seis frames de cada Boss deben compartir exactamente las mismas dimensiones.
74. Un PNG ausente, ilegible, corrupto o con dimensiones no válidas sigue bloqueando la release.
75. Se preserva la prohibición de Favoritos, Índice, Guardar y Copiar dentro de Intel.
76. Se preserva un único control Ampliar por dossier.
77. Se preserva Singleplayer oculto detrás de Ctrl+S.
78. No se añaden nuevos atajos de teclado a perfiles, diagnóstico ni configuración.
79. Se preserva la reproducción completa de las pistas y el shuffle sin repetición existente.
80. La release sigue publicándose únicamente después de las regresiones y del build Forge completo.
