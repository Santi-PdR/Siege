# SIEGE 3.00.0 — Server Encyclopedia Overhaul

SIEGE 3.00 convierte la investigación de Eternal Craft en una enciclopedia útil para cualquier jugador, sin perfiles, inventarios ni progreso personal.

1. Versión activa elevada de 2.50.0 a 3.00.0.
2. Nuevo dominio **ENCICLOPEDIA DEL SERVIDOR**.
3. Nueva pantalla `SiegeKnowledgeScreen` integrada al lenguaje visual de SIEGE.
4. La enciclopedia queda explícitamente prohibida para datos personales de jugadores.
5. No se guardan inventarios personales.
6. No se guardan builds privadas.
7. No se guardan estadísticas de una partida concreta.
8. No se guardan logros personales.
9. No se guardan perfiles de otros jugadores.
10. El contenido se limita a información general del servidor y sistemas documentados.
11. Nueva vista **EMPEZAR** para información básica de nuevos jugadores.
12. Nueva vista **RAZAS**.
13. Nueva vista **SISTEMAS**.
14. Nueva vista **HISTÓRICO**.
15. Nueva búsqueda interna por tema.
16. La búsqueda ignora mayúsculas y tildes.
17. Operations Hub incorpora la nueva ruta **ENCICLOPEDIA**.
18. Nueva identidad de navegación `ENC // SERVER ENCYCLOPEDIA`.
19. Operations Hub indexa entradas de la enciclopedia.
20. Los resultados de enciclopedia admiten deep-link directo a una ficha.
21. La portada de Operations muestra cantidad de temas de enciclopedia.
22. La portada de Operations muestra cantidad de temas críticos.
23. El buscador global puede resolver nombres de razas.
24. El buscador global puede resolver rarezas.
25. El buscador global puede resolver Trials.
26. El buscador global puede resolver Executores.
27. El buscador global puede resolver objetos y reliquias.
28. Nuevo modelo `SiegeKnowledgeData` independiente del gameplay.
29. Cada ficha tiene ID estable.
30. Cada ficha tiene dominio temático.
31. Cada ficha tiene resumen ES/EN.
32. Cada ficha tiene cuerpo ES/EN.
33. Cada ficha conserva referencias fechadas cuando existen.
34. Cada ficha conserva relaciones con otros temas.
35. Cada ficha puede marcarse como crítica para supervivencia/progresión.
36. Nuevo estado de confianza **STAFF CONFIRMADO**.
37. Nuevo estado de confianza **SISTEMA / OBSERVADO**.
38. Nuevo estado de confianza **HISTÓRICO**.
39. Nuevo estado de confianza **POR VERIFICAR**.
40. Nuevo estado de confianza **CONTRADICCIÓN**.
41. Los estados de confianza no se usan como rareza ni nivel de amenaza.
42. Nueva ficha general de Eternal Craft / SIEGE.
43. Nueva ficha de cobertura de la investigación.
44. La cobertura registra 488 archivos encontrados.
45. La cobertura registra 252/252 JSON procesados íntegramente.
46. La cobertura registra 251.065 mensajes únicos indexados.
47. La cobertura registra 0 duplicados.
48. Se documenta que el export disponible cubre sólo el canal General.
49. Se documenta que indexación completa no equivale a revisión semántica completa.
50. Nueva ficha de política de fuentes y huecos de información.
51. Nuevo catálogo de razas documentadas.
52. El catálogo incluye Human.
53. El catálogo incluye Hacker.
54. El catálogo incluye Shark.
55. El catálogo incluye Saiyan.
56. El catálogo incluye Deteriorer.
57. El catálogo incluye Faraón.
58. El catálogo incluye Apotheosis.
59. El catálogo incluye Muerte.
60. El catálogo incluye Cyborg.
61. El catálogo incluye Ghoul.
62. El catálogo incluye Subhuman.
63. El catálogo incluye Terrariano.
64. El catálogo incluye Kaioshin.
65. El catálogo incluye Dragon.
66. El catálogo incluye Shinigami.
67. El catálogo incluye Majin.
68. El catálogo registra razas ocultas de AUs de Undertale sin inventar nombres faltantes.
69. Nueva ficha del orden de rarezas de raza.
70. Rarezas: Común → Poco común → Raro → Ultra raro → Legendario → Obsainan → Mítico → Godly → Eternal → Fabled.
71. Obsainan queda correctamente situado por encima de Legendario.
72. Se conservan ejemplos fechados de rareza sin tratarlos como balance permanente.
73. Nueva ficha de slots de raza.
74. Nueva ficha de obtención general de Fabled mediante pasos/spins especiales.
75. Nueva ficha de progresión racial V1 → V4.
76. Se documenta que V1→V4 no es una ruta universal para todas las razas.
77. Saiyan queda separado del esquema V2/V3/V4 tradicional cuando corresponde.
78. Nueva ficha general de Trials.
79. Nueva ficha de Trials de meditación.
80. Nueva ficha de Trial Spire.
81. Nueva ficha general de Executores.
82. Se documenta terror radius de Executores.
83. Se conservan cambios históricos entre ediciones/contextos de Executores.
84. Nueva ficha general de estructuras.
85. Nueva ficha general de bosses.
86. Nueva ficha general de misiones/NPC.
87. Nueva ficha general de dimensiones.
88. Nueva ficha de Room.
89. Nueva ficha de Gate.
90. Umbrales contradictorios de meditación quedan en HISTÓRICO/CONTRADICCIÓN en vez de fingir una regla única.
91. Nueva ficha de Assembling Table.
92. Nueva ficha de Geography Table con referencia histórica de 120 wins correctamente asociada a la mesa.
93. Nueva ficha de Daemonium Kit.
94. Nueva ficha de Improbability Scroll.
95. Nueva ficha general de reliquias y ficha histórica de Third Justice.
96. Nuevo bloque de historia del sistema de muerte/revive y Respawn Cards.
97. Nuevas fichas generales de raids, facciones, economía y diseño seguro de prompts/acciones.
98. Nuevo test de regresión que bloquea nombres/datos personales dentro de la enciclopedia.
99. Nuevo documento `KNOWLEDGE-ARCHITECTURE-3.0.md` define separación, fuentes, historial y privacidad.
100. Nuevo QA 3.00 exige verificar navegación, búsqueda, razas, rarezas, temas históricos y ausencia de información personal antes de publicar.
