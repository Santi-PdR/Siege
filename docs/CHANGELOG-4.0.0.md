# SIEGE 4.00.0 — War Room / Tactical Atlas

Salto mayor desde 3.00.0 centrado en convertir el menú en una interfaz táctica útil para entrar, aprender, investigar y actuar sin mezclar información personal con conocimiento general del servidor.

1. La versión activa pasa a 4.00.0.
2. Operations Hub evoluciona a Sala de Operaciones / War Room.
3. Se añade una ruta BRIEFING para nuevos jugadores.
4. Se añade una ruta ATLAS para explorar conocimiento general.
5. Se añade una ruta AMENAZAS / THREAT BOARD.
6. Las tres rutas nuevas aparecen en la búsqueda global.
7. Se mantiene Despliegue como ruta independiente.
8. Se mantiene Intel como sistema de dossiers verificables.
9. Se mantiene Enciclopedia 3.00 como archivo detallado compatible.
10. Se mantiene Archivo/Lore separado de mecánicas del servidor.
11. Se mantiene Arsenal separado de Intel y Enciclopedia.
12. Se mantiene Manual de Campo separado de configuración técnica.
13. Se crea SiegeBriefingScreen.
14. El Briefing usa una secuencia corta de fichas esenciales.
15. El Briefing cubre qué es el servidor antes de entrar en sistemas avanzados.
16. El Briefing enlaza exploración y preparación.
17. El Briefing enlaza catálogo de razas.
18. El Briefing enlaza rarezas raciales.
19. El Briefing enlaza progresión V1–V4 cuando aplica.
20. El Briefing enlaza Trials.
21. El Briefing enlaza Executores.
22. El Briefing enlaza estructuras.
23. El Briefing enlaza bosses.
24. El Briefing enlaza revive/Respawn Cards.
25. El Briefing enlaza dimensiones.
26. El Briefing enlaza análisis de reliquias.
27. El Briefing enlaza economía.
28. El Briefing enlaza el marco de prompts seguros.
29. Cada paso abre una ficha con fuente y fecha.
30. Se crea SiegeAtlasScreen.
31. El Atlas tiene vista EMPEZAR.
32. El Atlas tiene vista RAZAS.
33. El Atlas tiene vista SISTEMAS.
34. El Atlas tiene vista INVESTIGACIÓN.
35. El Atlas incorpora búsqueda local.
36. El Atlas muestra resumen antes de abrir la ficha completa.
37. El Atlas conserva el nivel de confianza de cada registro.
38. El Atlas conserva la división SERVER/HISTORY.
39. Se crea SiegeKnowledgeFileScreen como visor de archivo completo.
40. El visor muestra cuerpo, relaciones y referencias.
41. Se crea SiegeThreatBoardScreen.
42. El Threat Board tiene vista DOSSIERS.
43. La vista DOSSIERS usa IntelCatalog y no duplica Intel.
44. El Threat Board tiene vista EJECUTORES.
45. El Threat Board tiene vista BOSSES.
46. El Threat Board tiene vista EVENTOS.
47. Intel y archivo del Discord siguen siendo fuentes distintas aunque se consulten juntos.
48. Se crea SiegeKnowledgeRegistry para unir corpus 3.00 y expansión 4.00 en lectura.
49. Se añade una expansión no personal del conocimiento recuperado.
50. Se documenta la prioridad general de movilidad/utility en progreso avanzado.
51. Se documenta la adaptación a técnicas repetidas sin inventar porcentajes.
52. Se documenta un marco reusable de prompts: objetivo, recurso, rango, duración, blancos, exclusiones y finalización.
53. Se documenta disciplina de poderes de área durante raids.
54. Se documenta el flujo Geography Table → análisis de reliquia.
55. Se documenta el papel de Daemonium Kit como extracción de componentes cuando corresponde.
56. Se documenta planificación de Assembling sin inventar recetas faltantes.
57. Las penalizaciones históricas de revive repetido quedan marcadas como no confirmadas/antiguas.
58. La sobrecarga histórica de RE/Deteriorer queda en HISTÓRICO y no como regla universal.
59. Se crea una ficha explícita de preguntas abiertas del archivo.
60. La indexación completa no se presenta como revisión semántica completa.
61. Se mantiene el contrato de privacidad: sin inventarios, builds o progreso de jugadores concretos.
62. Operations global search indexa la expansión 4.00.
63. Operations puede abrir archivos de la expansión directamente.
64. NavigationModel reconoce Briefing.
65. NavigationModel reconoce Tactical Atlas.
66. NavigationModel reconoce Threat Board.
67. NavigationModel reconoce el nuevo visor de fichas.
68. El historial de rutas reconoce las nuevas superficies.
69. El botón OPERACIONES del menú principal describe las capacidades 4.00.
70. Se mantienen los callbacks vanilla de conexión del servidor.
71. Se conserva el servidor oficial configurado.
72. Singleplayer continúa oculto salvo Ctrl+S.
73. Los controles Intel eliminados no regresan.
74. UNKNOWN sigue siendo categoría válida de Intel.
75. Tempest Jutcherson continúa fuera de la rotación normal de fondos.
76. Se conserva la música existente aprobada y sus transiciones.
77. No se incorporan canciones externas encontradas en Internet sin permiso/licencia suficiente.
78. Se documentan candidatos musicales de Dummies vs Noobs para pedir permiso o aportar archivo autorizado.
79. Se documentan miniaturas oficiales de Dummies vs Noobs como referencias visuales, no como masters HD.
80. No se fuerzan imágenes 768x432 dentro de la galería HD sólo por ser oficiales.
81. Se conserva el sistema de fondos HD existente mientras no haya nuevos masters adecuados.
82. Se mantienen contraste automático y high contrast.
83. Briefing adapta número de columnas a viewport compacto.
84. Atlas adapta lista y detalle a viewport compacto.
85. Threat Board adapta lista y detalle a viewport compacto.
86. Las pantallas nuevas conservan hover/click del sistema SiegeButton.
87. Las pantallas nuevas conservan música de menú.
88. Las pantallas nuevas conservan fondos de SIEGE y chrome común.
89. Se amplían pruebas del índice Operations para nuevas rutas.
90. Se añade AtlasRegressionTest.
91. AtlasRegressionTest comprueba separación de histórico.
92. AtlasRegressionTest comprueba que la expansión aparece en las vistas correctas.
93. AtlasRegressionTest vuelve a bloquear datos personales.
94. NavigationIdentityTest cubre BRF/ATL/THR.
95. Se añade test_release_040.py.
96. El release test conserva el servidor oficial, Ctrl+S e invariantes de Intel.
97. El release test conserva el aislamiento de Tempest Jutcherson.
98. CI compila y ejecuta las regresiones de conocimiento/Atlas/Operations.
99. CI sigue compilando Forge completo antes de publicar.
100. El instalador sólo recibe 4.00.0 después de que main supere todas las verificaciones.
