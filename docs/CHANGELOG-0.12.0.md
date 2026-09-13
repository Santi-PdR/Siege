# SIEGE 0.12.0 — 50 mejoras de interfaz y estabilidad

Estas son mejoras nuevas sobre el estado real posterior a 0.11.1. No se recuperan Favoritos, Índice, Guardar o Copiar y no se añaden atajos.

## Controles generales

1. Foco de teclado con marco dorado distinto del hover del mouse.
2. El foco permanece legible sin simular que el puntero está sobre el control.
3. Los controles inactivos no ejecutan efectos de hover.
4. Opción de centrado real para botones compactos.
5. Los textos recortados usan el carácter tipográfico `…`.
6. El recorte sigue siendo seguro cuando ni siquiera cabe la elipsis.
7. Los botones seleccionados conservan una marca separada del foco.
8. Los bordes de foco cubren los cuatro lados del botón.
9. Los sliders distinguen visualmente el estado inactivo.
10. Los sliders muestran marcas en 0, 25, 50, 75 y 100 %.
11. Las etiquetas recortadas de sliders terminan con elipsis visible.
12. Las flechas del slider reproducen sonido sólo cuando cambia el valor.

## Portada

13. El ancho del título se calcula desde la ventana completa y no desde la columna de botones.
14. El título permanece centrado al cambiar de resolución.
15. El ancho máximo evita que se vuelva desproporcionado en ultrawide.
16. El margen compacto evita que el título salga de la pantalla.
17. Nuevo divisor táctico bajo el subtítulo.
18. El divisor combina línea cian y núcleo rojo sin tapar el texto.
19. En ventanas compactas, Música pasa a la esquina inferior derecha para no chocar con el título.
20. El botón Música muestra el número de pista y el total.
21. El número se actualiza también cuando la pista cambia automáticamente.
22. Los avisos de pista se ocultan en el diseño compacto sin espacio suficiente.
23. Un aviso nunca se dibuja con ancho negativo.
24. Los avisos menores a 96 px se omiten por ilegibles.
25. El ancho máximo del aviso sigue adaptándose al espacio libre.
26. La versión visible de la portada se actualizó a 0.12.0.

## Intel

27. Las categorías sin expedientes quedan inactivas.
28. Las flechas compactas explican expediente anterior y siguiente.
29. Ambas flechas se desactivan si sólo existe un resultado.
30. El botón para limpiar búsqueda desaparece cuando la consulta está vacía.
31. La rueda avanza dos líneas por paso en el lector principal.
32. La rueda avanza dos líneas por paso en la advertencia táctica.
33. La cabecera del papel muestra la categoría completa del expediente.
34. La categoría se alinea a la derecha y respeta el código de la izquierda.
35. El ancho mínimo del texto impide cálculos inválidos en ventanas extremas.
36. Señalar la imagen abre una banda de acción oscura.
37. La banda indica claramente `AMPLIAR ↗`/`INSPECT ↗`.
38. El borde de la imagen adopta el color del expediente al señalarla.
39. El lector muestra porcentaje de avance cuando dispone de espacio.
40. La advertencia táctica incorpora una barra de desplazamiento visible.
41. El tamaño del tirador representa la proporción de texto visible.
42. Se puede pulsar la pista de la barra táctica para saltar de posición.
43. La barra táctica puede arrastrarse sin saltar al tomar el tirador.
44. El arrastre táctico y el arrastre del lector tienen estados separados.
45. Soltar el mouse cancela ambos arrastres de forma segura.

## Configuración, Galería y validación

46. Configuración recuerda la última sección visitada durante la sesión.
47. Cada sección recuerda su propia posición de desplazamiento.
48. La navegación compacta usa dos columnas cuando tres cortarían las etiquetas.
49. El título de Configuración queda centrado y reserva lugar para Volver.
50. La barra de Configuración gana mayor zona visible, resaltado al señalar/arrastrar, porcentaje de avance, tamaño proporcional compartido y límites verificados; la Galería añade ayudas claras para fijar, deshacer y reactivar, además de una salida temporal en Vista limpia y una marca de fondo fijado que ya no parece un favorito.

## Alcance de validación

- `SiegeUiLayout` concentra cálculos de portada, avisos, columnas y scroll para que producción y pruebas usen la misma geometría.
- La matriz automática recorre tamaños lógicos desde 320×240 hasta 2560×1440.
- Se mantienen las protecciones que impiden recuperar controles y atajos descartados.
- La compilación automática no sustituye la revisión visual dentro de Minecraft.

