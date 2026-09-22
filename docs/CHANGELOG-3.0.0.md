# SIEGE 3.00.0 — Knowledge Overhaul

Este salto convierte la investigación masiva de Eternal Craft en un sistema navegable dentro del mod sin mezclar el estado actual del jugador con conocimiento histórico del Discord.

1. Versión activa elevada de 2.50.0 a 3.00.0.
2. Nuevo **Archivo de Conocimiento** (`KNW`) como dominio propio de la interfaz.
3. Nueva pantalla `SiegeKnowledgeScreen` integrada al lenguaje visual SIEGE.
4. Separación explícita entre **SIEGE ACTUAL** y **ETERNAL CRAFT — ENCICLOPEDIA**.
5. El estado actual deja de mezclarse silenciosamente con datos antiguos del Discord.
6. Nuevo modelo `SiegeKnowledgeData` independiente del gameplay del servidor.
7. Cada registro tiene identificador estable.
8. Cada registro tiene zona: CURRENT o ENCYCLOPEDIA.
9. Cada registro tiene dominio temático.
10. Cada registro tiene resumen corto y cuerpo detallado bilingüe.
11. Cada registro mantiene fecha de fuente cuando está disponible.
12. Cada registro mantiene canal/contexto de fuente cuando está disponible.
13. Cada registro mantiene nivel explícito de confianza.
14. Nueva confianza **ACTUAL CONFIRMADO**.
15. Nueva confianza **CONFIRMADO POR ALEX**.
16. Nueva confianza **SISTEMA / OBSERVADO**.
17. Nueva confianza **JUGADOR / EXPERIENCIA**.
18. Nueva confianza **HISTÓRICO**.
19. Nueva confianza **NO CONFIRMADO**.
20. Nueva confianza **CONTRADICCIÓN** preparada para futuros registros.
21. Nueva auditoría interna del corpus de Discord: 251.065 mensajes procesados.
22. La auditoría conserva 72.384 mensajes atribuidos a Alex.
23. La auditoría conserva 29.605 mensajes atribuidos a Santi.
24. La auditoría conserva 37.435 replies reconstruidas.
25. La auditoría conserva la referencia a 153 prompts clasificados.
26. La auditoría conserva la referencia a 35 documentos temáticos.
27. Nueva ficha de jerarquía de fuentes para evitar convertir rumores en reglas.
28. Nuevo snapshot de Deteriorer separado de la enciclopedia histórica.
29. Snapshot actual conserva rareza registrada como Obsainan.
30. Snapshot actual conserva 200 HP totales registrados.
31. Snapshot actual conserva RE / Rust Energy como energía racial.
32. Oxidación Recta aparece como habilidad actual registrada, no como dato genérico de otra raza.
33. Rust Guard aparece como habilidad actual registrada.
34. Rust Guard conserva duración de 25 segundos.
35. Rust Guard conserva radio de 2 bloques.
36. Rust Guard conserva coste de 8 RE por segundo.
37. Rust Guard conserva el snapshot de 1000/1000 RE del momento de registro.
38. La meditación actual queda registrada como recuperación de RE y entrenamiento del máximo.
39. La advertencia histórica de sobrecarga al meditar con RE llena queda separada y fechada.
40. Nueva vista **SUPERVIVENCIA** con registros críticos de varias áreas.
41. Nueva vista **FUENTES** para auditar el origen y la confianza de la información.
42. Nueva búsqueda local/accent-insensitive dentro del Archivo de Conocimiento.
43. La búsqueda distingue títulos, resúmenes, dominios, cuerpos y fuentes.
44. El Archivo de Conocimiento usa spoiler guard por sesión.
45. Los registros del Discord que pueden adelantar mecánicas ocultan el cuerpo completo hasta revelarlo.
46. Los resúmenes de supervivencia siguen visibles aunque el detalle esté oculto.
47. Nuevo registro histórico de Deteriorer como desgaste progresivo.
48. La antigua duración extrema de debuffs se conserva como **HISTÓRICO**, nunca como duración actual.
49. Nuevo consejo de raid: evitar oxidación descontrolada.
50. Nueva ficha sobre adaptación/counters de bosses ante técnicas repetidas.
51. Nueva ficha de sobrecarga de RE durante meditación con doble procedencia: Alex + resultado observado.
52. El valor de +6% RE/5 turnos queda marcado como **NO CONFIRMADO**, no como regla viva.
53. Filter Rod queda en **MISTERIOS** por evidencia insuficiente.
54. Geography Table entra como ficha de objeto/investigación.
55. Fallen Angel Halo entra como ficha de reliquia con precio fechado, no eterno.
56. Daemonium Kit entra como requisito documentado para extracción de componentes de reliquias.
57. La progresión V1 → V4 entra como marco histórico respaldado por fecha.
58. El posible reset posterior a V4 queda separado como **NO CONFIRMADO**.
59. Assembling de chips entra como sistema de referencia.
60. La recomendación de planificar partes del cuerpo queda etiquetada como posible/incompleta.
61. Heridas persistentes y tratamiento de hemorragias entran como referencia fechada.
62. Respawn Cards entran como referencia de revive con límites no inventados.
63. Penalizaciones por reanimación repetida quedan marcadas como posibles.
64. Nueva ficha de diseño seguro de prompts: energía, rango, duración, exclusiones y disipación.
65. Nueva ficha **Cosas que hicimos mal** para prompts vagos con consecuencias registradas.
66. `Ver barra` entra como referencia operacional vinculada a meditación/RE.
67. El posible riesgo de cruce no calibrado con Fallen Angel Halo queda en **MISTERIOS**.
68. Operations Hub añade nueva ruta **CONOCIMIENTO**.
69. Operations Hub pasa de 9 a 10 rutas de primer nivel.
70. Búsqueda global de Operations ahora indexa el Archivo de Conocimiento.
71. Buscar `Geography Table` puede abrir directamente su ficha.
72. Buscar `Rust Guard` puede abrir directamente la nota actual.
73. Buscar términos de meditación/RE puede llevar a advertencias de supervivencia.
74. Nuevo tipo de resultado `KNOWLEDGE` en el índice global.
75. Nuevo deep-link por `knowledgeId` desde búsqueda global.
76. Historial de rutas reconoce `KNOWLEDGE` como destino separado.
77. Barra de estado de Operations muestra cantidad de conocimiento y alertas de supervivencia.
78. Layout de Operations gana más espacio vertical para evitar solape entre estado y divisor de rutas.
79. En pantallas compactas Operations puede pasar a dos columnas de rutas.
80. El tooltip de OPERACIONES en portada ahora menciona el Archivo de Conocimiento.
81. Nueva identidad de navegación `KNW // KNOWLEDGE VAULT`.
82. Nueva regresión `KnowledgeDataRegressionTest` para separación, fuentes, búsqueda y spoilers.
83. Regression test exige IDs de conocimiento únicos.
84. Regression test exige al menos una fuente por registro.
85. Regression test protege el snapshot de Rust Guard.
86. Regression test protege la clasificación histórica de datos antiguos.
87. Regression test protege la doble procedencia de la advertencia de meditación.
88. Regression test protege búsqueda de Fallen Angel Halo y Geography Table.
89. OperationsIndexTest ahora valida deep-links de conocimiento.
90. NavigationIdentityTest ahora valida la identidad KNW.
91. CI compila y ejecuta el corpus de conocimiento sin depender de Minecraft.
92. CI mantiene las regresiones visuales, Intel, audio, fondos y easter eggs de 2.50.
93. Tempest Jutcherson sigue fuera del catálogo/galería/rotación normal.
94. Ningún registro de conocimiento modifica inventario, raza, energía o reglas del servidor.
95. El sistema evita presentar precios antiguos como valores permanentes.
96. El sistema evita presentar datos posibles como confirmados.
97. El sistema conserva relaciones entre entradas mediante IDs estables.
98. La arquitectura queda preparada para Ejecutores, Trials, Estructuras, Bosses, Misiones, NPCs, Pets, Magia, Rituales y más sin inventar contenido aún no recuperado.
99. Nuevo documento de arquitectura explica la frontera entre Intel, Archivo, Manual, Arsenal y Knowledge.
100. Nuevo plan QA de 3.00 exige verificación in-game además del CI automatizado.
