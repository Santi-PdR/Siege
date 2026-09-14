# SIEGE 0.14.0 — Atlas y 50 mejoras nuevas

Esta entrega añade a **Atlas** como primera Super Unit y aplica exactamente 50 cambios nuevos sobre 0.13.0. Se mantienen las decisiones de interfaz ya aprobadas: dossier a la derecha, advertencia táctica únicamente bajo la imagen derecha, Fondos dentro de Configuración, imágenes completas, flechas visibles y ausencia de Favoritos, Índice, Guardar, Copiar o atajos nuevos.

## Atlas y su expediente

1. Se añadió `SUP-001 / ATLAS` a la categoría Super Unit.
2. El expediente registra exactamente `125.000.000 HP`.
3. Super Unit deja de ser una categoría vacía y su botón queda habilitado.
4. La imagen de Atlas procede del video entregado.
5. La silueta completa se encuadra sin deformación mediante ajuste contenido.
6. El bloque promocional del video queda fuera del documento.
7. Atlas estrena una identidad documental dorada exclusiva.
8. El arte incorpora doble marco de alerta Omega.
9. La captura muestra una lectura vital de 125.000.000 HP.
10. El estado visual de Atlas se clasifica como alerta máxima.
11. El expediente está disponible en español e inglés.
12. Origen, armas y capacidades permanecen como datos no recuperados.
13. La descripción separa hechos confirmados de información desconocida.
14. El generador rechaza fuentes Super Unit inesperadas o ausentes.
15. La compilación verifica que `atlas.png` sea un PNG real de 640×360.

## Búsqueda y navegación Intel

16. La búsqueda encuentra cifras aunque el usuario omita comas o puntos.
17. Los guiones se interpretan como separación, por lo que `super unit` encuentra `SUPER-UNIT`.
18. Varias clases de espacios se normalizan antes de buscar.
19. Las búsquedas siguen ignorando mayúsculas y tildes.
20. Las frases entre comillas se buscan como una unidad completa.
21. `!palabra` excluye resultados que contengan ese término.
22. Una consulta nula se trata de forma segura como búsqueda vacía.
23. La categoría técnica forma parte del contenido buscable.
24. El nombre traducido de la categoría también es buscable.
25. Las abreviaturas de HP —como `125M`— encuentran el expediente correspondiente.
26. La consulta se conserva al cerrar y volver a abrir Intel.
27. La caja de búsqueda explica frases y exclusiones mediante una ayuda visible.
28. Cada categoría recuerda el último expediente seleccionado entre aperturas.
29. Cada expediente recuerda su posición de lectura entre aperturas.
30. Los botones compactos de categoría muestran cuántos expedientes contienen.
31. La banda «Archivos» muestra el rango visible y el total cuando la lista es larga.
32. El estado sin resultados muestra la consulta exacta que produjo el vacío.
33. La rueda sobre la lista se detiene en los extremos en vez de saltar circularmente.
34. Llegar a un extremo con la rueda no reproduce un clic falso.
35. Las flechas izquierda/derecha conservan la navegación circular solicitada.

## Lectura y presentación

36. La cabecera compacta reserva espacio al botón Volver y evita cruzarlo.
37. Las filas laterales muestran HP abreviado sin ocultar el valor exacto del dossier.
38. La ayuda de cada fila incluye código, nombre, HP exactos y estado.
39. Unidades avanzadas, tanques, jefes, élites y superunidades tienen papeles diferenciados.
40. Jefes, élites y superunidades reciben tintas de acento propias y legibles.
41. Los expedientes Super Unit muestran una segunda regla dorada de seguridad.
42. Las imágenes del dossier reciben un borde del color de su categoría.
43. Se eliminó el segundo texto «Ampliar» que reaparecía sobre la imagen.
44. Los HP enormes muestran simultáneamente valor exacto y forma compacta (`125M`).
45. La barra del cuerpo se ensancha al señalarla o arrastrarla.
46. El pie del texto incorpora una línea visual de progreso de lectura.
47. La ficha táctica muestra el porcentaje de información confirmada.
48. Ese porcentaje también se representa con una barra de integridad de datos.
49. La barra de la advertencia táctica mejora su contraste al señalarla.
50. La advertencia continúa únicamente en el panel derecho y conserva todo su texto mediante desplazamiento propio.

## Refuerzos adicionales de calidad

- El visor indica el fondo actualmente seleccionado, el siguiente nivel de zoom y la categoría/HP del expediente.
- El visor añade bordes por categoría, progreso de zoom, retícula y borde coloreado en el minimapa.
- El catálogo usa grupos inmutables, búsqueda directa por código y validación de prefijos, nombres y HP.
- Las regresiones comprueban Atlas, sus 125.000.000 HP, la ausencia de capacidades inventadas, el buscador ampliado y los seis colores de categoría.

Las comprobaciones automáticas no sustituyen una inspección visual dentro de Minecraft. Deben revisarse las escalas de GUI 1–4 después de compilar el JAR.
