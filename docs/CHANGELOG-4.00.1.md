# SIEGE 4.00.1 — Server Guide completion

1. Añadida una **Guía del Servidor** pensada para jugadores nuevos y consultas rápidas.
2. La Guía evita mostrar metadatos técnicos de mantenimiento en la interfaz normal.
3. La información se divide en siete categorías visibles y simples.
4. Nueva categoría **EMPEZAR**.
5. Nueva categoría **RAZAS**.
6. Nueva categoría **PROGRESIÓN**.
7. Nueva categoría **AMENAZAS**.
8. Nueva categoría **SISTEMAS**.
9. Nueva categoría **SUPERVIVENCIA**.
10. Nueva categoría **HISTÓRICO**.
11. La portada cambia el antiguo acceso corto a Briefing por **GUÍA**, sin añadir otra fila vertical.
12. El texto del botón GUÍA se mide según el ancho disponible para evitar solapamientos.
13. La Sala de Operaciones abre la Guía directamente desde su ruta de conocimiento.
14. Búsqueda global mantiene deep-links a fichas completas cuando se busca un tema concreto.
15. Los resultados de conocimiento ya no muestran nivel de confianza ni metadatos de mantenimiento en su subtítulo normal.
16. Añadido resumen **Primeros pasos**.
17. Añadido resumen **Cómo entender las razas**.
18. Añadido resumen **Cómo progresa el servidor**.
19. Añadido resumen **Antes de entrar a un Trial**.
20. Añadido resumen **Cómo reconocer a un Executor**.
21. Añadido resumen **Prepararse para bosses**.
22. Añadido resumen **Reliquias y objetos desconocidos**.
23. Añadido resumen **Assembling y Cyborgs**.
24. Añadido resumen **Entrar a una dimensión**.
25. Añadido resumen **Muerte y revive**.
26. Añadido resumen **Economía y precios**.
27. Añadido resumen **Acciones y habilidades complejas**.
28. Cada categoría coloca primero los resúmenes generales y después las fichas específicas.
29. La sección RAZAS conserva el orden de rarezas y el catálogo existente.
30. La sección PROGRESIÓN reúne V1→V4, Trials, rutas especiales, exploración y meditación.
31. La sección AMENAZAS reúne Executores, bosses, raids, estructuras y adaptación enemiga.
32. La sección SISTEMAS reúne reliquias, objetos, Assembling, habilidades, dimensiones y economía.
33. La sección SUPERVIVENCIA reúne revive, movilidad, preparación y errores de alto riesgo.
34. La sección HISTÓRICO separa reglas antiguas o contradictorias de la información general actual.
35. Se mantiene la regla de no incluir información personal, inventarios, builds o progreso privado.
36. Nueva prueba `ServerGuideRegressionTest` protege categorías, contenido y entradas esenciales.
37. CI compila `SiegeKnowledgeExpansion401` en Atlas, Guide y búsqueda global.
38. Navigation regression reconoce `GUI // SERVER GUIDE`.
39. Tempest Jutcherson continúa aislado del catálogo normal de fondos.
40. Intel conserva UNKNOWN y los controles eliminados siguen prohibidos.
41. Singleplayer sigue oculto salvo Ctrl+S.
42. Deployment conserva el servidor oficial y los callbacks vanilla.
43. Sala Multimedia conserva las recomendaciones de música DVN sin incorporar automáticamente archivos externos.
44. Forge 1.20.1 y Java 17 continúan como base del mod.
45. La versión publicada pasa a **4.00.1** tras CI y build Forge exitosos.
