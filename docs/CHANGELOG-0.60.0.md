# SIEGE 0.60.0 — Diagnostics & Recovery

0.60 convierte el Centro de Comando de 0.50 en una herramienta que no sólo muestra estado: ahora explica qué está desalineado, mide la coherencia del cliente y permite recuperar un perfil completo sin tocar gameplay.

## 60 mejoras

1. La versión del mod avanza a **0.60.0**.
2. Se añade una pantalla completa de **Diagnóstico y Recuperación**.
3. El diagnóstico se abre desde el Centro de Comando.
4. La pantalla usa la versión real del mod en su cabecera.
5. La pantalla es bilingüe español/inglés.
6. El diagnóstico separa los problemas por subsistema.
7. Configuración tiene una fila de estado propia.
8. Música tiene una fila de estado propia.
9. Sonidos de interfaz tienen una fila de estado propia.
10. Efectos visuales tienen una fila de estado propia.
11. Perfil de render tiene una fila de estado propia.
12. Coherencia de perfil tiene una fila de estado propia.
13. Intel tiene una fila de estado propia.
14. Fondo tiene una fila de estado propia.
15. Accesibilidad tiene una fila de estado propia.
16. Cada fila tiene un código corto estable.
17. Cada fila muestra un estado OK/INFO/REVISAR/ERROR.
18. Cada severidad usa un acento visual distinto.
19. Los errores de guardado pasan a severidad ERROR.
20. Música activa a 0% pasa a REVISAR.
21. SFX activos a 0% pasan a REVISAR.
22. Reducir destellos + interferencia activa se marca como contradicción.
23. Rendimiento con efectos incompatibles se marca como contradicción.
24. El diagnóstico muestra el estado actual de Intel.
25. El diagnóstico muestra el fondo actual y su modo.
26. El diagnóstico resume accesibilidad.
27. Se añade un contador de errores.
28. Se añade un contador de advertencias.
29. Se añade un contador de avisos informativos.
30. La preparación general se calcula desde severidades reales.
31. Un error resta más preparación que una advertencia.
32. Una mezcla personalizada genera INFO, no un falso error.
33. Se añade análisis de distancia entre ajustes y perfiles.
34. El análisis compara 21 campos de configuración.
35. Se calcula el perfil predefinido más cercano.
36. Se calcula un porcentaje de coincidencia con ese perfil.
37. La pantalla muestra ese porcentaje cuando el perfil es personalizado.
38. Se añade el botón **Alinear al perfil más cercano**.
39. La alineación usa los perfiles oficiales de SIEGE.
40. La alineación queda desactivada cuando ya existe coincidencia exacta.
41. El botón muestra `EXACTO/EXACT` cuando no hay drift.
42. La recuperación guarda la configuración inmediatamente.
43. Tras recuperar se recalcula el diagnóstico completo.
44. La lista de diagnóstico tiene scroll propio.
45. El scroll se limita al área de subsistemas.
46. Se añade scrollbar visual cuando hace falta.
47. El layout reduce altura de filas en ventanas compactas.
48. El panel se adapta desde ventanas pequeñas hasta 820 px lógicos.
49. Los textos largos se recortan dentro de su fila y no invaden estados.
50. El hover de filas mejora la lectura sin añadir acciones ocultas.
51. El Centro de Comando añade acceso directo a Diagnóstico.
52. Ajustes nativos y Diagnóstico comparten una fila de acciones clara.
53. El resumen del Centro de Comando usa el diagnóstico 0.60 como fuente de preparación.
54. Las advertencias del Centro de Comando se obtienen del mismo modelo de diagnóstico.
55. La barra operacional muestra ajuste al perfil cuando el modo es Custom.
56. Las métricas de perfil quedan aisladas de `SiegeConfig` para conservar sus pruebas standalone.
57. No se altera la persistencia histórica de `siege-client.properties`.
58. No se añaden atajos de teclado nuevos.
59. No vuelven Favoritos, Índice, Guardar ni Copiar a Intel.
60. CI protege versión, pantalla, métricas, recuperación y ausencia de atajos inventados.

## Límite de alcance

0.60 sigue siendo una actualización de cliente/interfaz. No modifica daño, IA, unidades, servidor, raids ni reglas de gameplay.
