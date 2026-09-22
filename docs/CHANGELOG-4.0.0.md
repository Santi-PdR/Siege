# SIEGE 4.00.0 — Command Rebuild

1. Versión activa elevada de 3.00.0 a 4.00.0.
2. OPERACIONES pasa a abrir un Briefing de Recluta antes del centro denso de búsqueda.
3. Nuevo `SiegeRecruitBriefingScreen` como puerta de entrada para jugadores nuevos y veteranos que buscan un tema concreto.
4. El Briefing divide la información en Qué es SIEGE, Razas, Progresión, Supervivencia, Amenazas, Arsenal, Despliegue y Multimedia.
5. El Briefing incluye acceso directo a búsqueda global sin convertirla en la primera pantalla.
6. El Briefing usa 2 o 4 columnas según espacio disponible.
7. El detalle del Briefing usa wrapping real para evitar textos solapados.
8. Los botones del Briefing calculan su ancho desde el viewport y no desde valores rígidos.
9. Nuevo Atlas de Razas independiente de la Enciclopedia general.
10. El Atlas reúne Human, Hacker, Shark, Saiyan, Deteriorer, Faraón, Apotheosis, Muerte, Cyborg, Ghoul, Subhuman, Terrariano, Kaioshin, Dragon, Shinigami, Majin y razas ocultas de AUs.
11. El Atlas muestra rareza sin inventarla cuando no está confirmada.
12. El Atlas distingue progresión V1→V4, transformaciones, pasos/Trials, Assembling y rutas todavía incompletas.
13. El Atlas permite buscar por nombre, rareza, progresión y etiquetas.
14. El Atlas muestra la escala Común → Poco común → Raro → Ultra raro → Legendario → Obsainan → Mítico → Godly → Eternal → Fabled.
15. Saiyan conserva rareza Obsainan.
16. Deteriorer conserva rareza Obsainan.
17. Apotheosis conserva rareza Eternal.
18. Las razas sin rareza reconstruida muestran SIN CONFIRMAR.
19. El Atlas no mezcla datos de inventario, builds ni progreso de jugadores.
20. El Atlas puede abrir la ficha correspondiente de la Enciclopedia.
21. Nuevo Mapa de Progresión independiente.
22. El Mapa añade una ruta general de exploración → raza → Trials → investigación → sistemas avanzados.
23. El Mapa conserva el marco V1→V4 como ruta común, no universal.
24. V1 explica el estado base de varias razas.
25. V2 explica el aumento habitual de efectos/atributos sin inventar un requisito universal.
26. V3 se mantiene como etapa dependiente de cada raza.
27. V4 se relaciona con muchos Trials/artefactos sin afirmar que todos lo exigen.
28. Nuevo track específico de Trials.
29. Trials de meditación aparecen como ruta incompleta pero conocida.
30. Trial Spire aparece como lugar relacionado con Trials y NPCs, no como una única prueba.
31. Perish Staff V2 se conserva como ruta histórica separada.
32. Las razas ocultas de AUs se relacionan con Trials sin inventar nombres o requisitos.
33. Nuevo track de rutas especiales.
34. Saiyan muestra progresión por transformaciones/stats.
35. Cyborg muestra progresión por implantes/chips/Assembling.
36. Hacker muestra versiones, entrenamiento enfocado, Room y Gate como ruta propia.
37. Fabled queda descrita mediante pasos/spins especiales en vez de giros comunes.
38. Nuevo track de sistemas avanzados.
39. Assembling, Reliquias, Dimensiones y Raids aparecen como ramas avanzadas diferenciadas.
40. El Mapa de Progresión adapta pestañas a varias filas en viewports compactos.
41. Se elimina del Mapa el texto técnico `ABRIR FUENTE`.
42. El acceso de detalle pasa a `VER INFORMACIÓN`.
43. Nuevo Tablero de Amenazas independiente de Intel.
44. El Tablero resume Intel, Executores, Bosses, Raids/Hordas, Estructuras, Facciones, Dimensiones y Muerte/Revive.
45. El Tablero no inventa unidades nuevas.
46. El Tablero conserva Intel como dueño de dossiers UNIT/ADVANCED/TANK/BOSS/ELITE/SUPER-UNIT/UNKNOWN.
47. El Tablero muestra contadores Intel por categoría.
48. El Tablero permite abrir Intel directamente.
49. Las amenazas generales abren información temática en vez de dossiers falsos.
50. El Tablero añade scroll real en modo compacto.
51. Se corrige el problema por el que una pantalla compacta podía mostrar sólo los primeros riesgos.
52. Se elimina del Tablero el lenguaje técnico sobre generación de stats/fuentes.
53. Nuevo `Siege4CardScreen` reutilizable para fichas simples y responsive.
54. Nuevo modelo `Siege4ReferenceData` para textos de orientación fáciles de leer.
55. La Enciclopedia del Servidor pasa de 4 a 5 categorías principales.
56. Las categorías son EMPEZAR / RAZAS / PROGRESIÓN / SISTEMAS / HISTÓRICO.
57. PROGRESIÓN deja de mezclarse dentro de SISTEMAS.
58. Los tabs de Enciclopedia se reparten en 2, 3 o 5 columnas según ancho.
59. Se corrige el solapamiento de textos/botones en la nueva interfaz de Enciclopedia.
60. La caja de búsqueda siempre se coloca debajo de todas las filas de categorías.
61. La Enciclopedia compacta separa lista y detalle verticalmente.
62. La Enciclopedia amplia mantiene lista izquierda + detalle derecho.
63. La búsqueda sugiere raza, rareza, Trial, Executor, reliquia, revive y dimensión.
64. EMPEZAR prioriza los temas que debería conocer alguien nuevo.
65. RAZAS muestra sólo fichas del dominio de razas.
66. PROGRESIÓN reúne progresión, Trials, meditación y dimensiones.
67. SISTEMAS reúne Executores, bosses, estructuras, objetos, reliquias, Assembling, raids, economía y sistemas relacionados.
68. HISTÓRICO mantiene datos antiguos/contradictorios apartados del conocimiento general.
69. Se eliminan de la UI normal las líneas técnicas de procedencia de cada ficha.
70. Se eliminan de la UI normal las listas de fuentes/fechas/canales.
71. La trazabilidad interna puede seguir existiendo para mantenimiento sin ensuciar la experiencia del jugador.
72. El detalle de Enciclopedia muestra sólo título, categoría/estado y explicación útil.
73. El Centro de Operaciones se mantiene como buscador avanzado detrás del Briefing.
74. El Centro de Operaciones pasa a 2, 3 o 5 columnas según el ancho disponible.
75. Se reduce la densidad de texto del header del Centro de Operaciones.
76. La búsqueda global sigue abriendo dossiers Intel concretos.
77. La búsqueda global sigue abriendo entradas de Arsenal concretas.
78. La búsqueda global sigue abriendo entradas de Enciclopedia concretas.
79. La ruta BACKGROUNDS del centro pasa a abrir la Sala Multimedia, que incluye acceso a Galería.
80. Nuevo `SiegeMediaRoomScreen`.
81. Sala Multimedia controla pista anterior, reinicio y pista siguiente.
82. Sala Multimedia controla fondo anterior, rotación automática y fondo siguiente.
83. Sala Multimedia muestra pista actual, progreso y estado.
84. Sala Multimedia muestra escena actual y estado de rotación.
85. Sala Multimedia añade una sección de música asociada a la identidad Dummies vs Noobs.
86. Se incluyen como referencias Convenience Store, Music Box, New Store, Jazz Music, From the Ashes y Sad Choir.
87. Las recomendaciones musicales indican dónde encajan dentro del menú.
88. La interfaz no muestra mensajes técnicos de redistribución/licencia al jugador.
89. Nuevo apartado de dirección visual DVN para futuras escenas.
90. Se agregan conceptos de Última fortaleza, operación urbana nocturna, hangar/briefing, defensa de oleada, escuadra en avance y zona industrial devastada.
91. Sala Multimedia permite scroll para listas largas de referencias.
92. Tempest Jutcherson continúa fuera de la galería y rotación normal.
93. Arsenal se expande más allá de Third Justice/Aerorig/Riflator/HOLO-Watch.
94. Geography Table entra al Arsenal con su función de investigación y referencia de 120 wins marcada como no permanente.
95. Daemonium Kit entra al Arsenal como herramienta de extracción de materiales de reliquias.
96. Fallen Angel Halo entra al Arsenal con lifesteal conocido y efectos restantes abiertos.
97. Improbability Scroll entra al Arsenal como herramienta para vincular mesas de Steel/Assembling.
98. Assembling Table entra al Arsenal con chips, trasplantes y relación con Cyborg.
99. Third Justice conserva parry de 0,1 s, cooldown de 1 s, stun, costes físicos y REEL multimedia sin texto técnico innecesario.
100. SIEGE 4.00 mantiene Forge 1.20.1, Java 17, servidor oficial, Ctrl+S para Singleplayer, UNKNOWN, callbacks vanilla, audio sin repetición y las restricciones históricas de Intel.
