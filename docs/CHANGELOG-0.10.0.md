# SIEGE 0.10.0

36 mejoras nuevas respecto de 0.9.2. No se cuentan compilación, documentación ni pruebas como funciones nuevas. No se añaden atajos de teclado.

1. Guardar o quitar favoritos directamente desde cada fila del índice.
2. Filtro de solo favoritos dentro del índice.
3. Filtro de amenaza mínima: todas, 3, 4 o 5.
4. Invertir el orden de los resultados.
5. Conservar el criterio y sentido de orden después de reiniciar.
6. Buscar también en el perfil y advertencia táctica desde el índice.
7. Botón para ir a la primera página.
8. Botón para ir a la última página.
9. Conservar aproximadamente el primer resultado visible al redimensionar el índice.
10. Limpiar búsqueda y filtros en una sola acción.
11. Mostrar cantidad de resultados frente al total de la selección.
12. Recordar el modo Lectura entre sesiones.
13. Espaciado de lectura ampliado, configurable.
14. Papel oscuro opcional con texto y advertencias de mayor contraste.
15. Recordar la posición de lectura de cada expediente mientras Intel permanece abierto.
16. Botón para volver al inicio del texto.
17. Botón para ir al final del texto.
18. Copiar la información del expediente al portapapeles.
19. Confirmación visible «Copiado» después de copiar.
20. Abrir el visor pulsando directamente la imagen del expediente.
21. Mostrar los fondos completos en la galería, sin recortarlos, como alternativa al encuadre que llena la vista.
22. Previsualizar la oscuridad configurada del fondo y del panel lateral en la galería.
23. Deshacer la última fijación o reactivación de rotación de fondos durante la visita a la galería.
24. Abrir/recuperar en la galería el fondo que está usando actualmente el menú.
25. Conservar la página de miniaturas al redimensionar la galería.
26. Evitar dibujar dos fondos cuando la transición de la vista previa ya terminó.
27. Elegir fondo negro, gris o papel para inspeccionar imágenes.
28. Mostrar u ocultar el minimapa del visor y recordar la preferencia.
29. Botón de ampliación directa 2×/4×.
30. Centrar la imagen sin cambiar su ampliación.
31. Sección Intel propia en Configuración, separando lectura y dossiers de las opciones generales.
32. Escuchar muestras de los sonidos de interfaz desde Configuración.
33. Panel musical fijo con nombre, tiempo transcurrido, duración, tiempo restante y progreso, visible al desplazar los ajustes.
34. Perfil tranquilo que aplica movimiento reducido, desactiva interferencia/líneas/hover y usa gráficos equilibrados.
35. Restaurar ajustes conserva los favoritos guardados.
36. Acción separada para vaciar favoritos, con confirmación.

## Comprobaciones

- CI prueba la geometría de galería/Intel/índice y las combinaciones de búsqueda, favoritos, amenaza y orden.
- CI ejecuta el código real de SiegeConfig con adaptadores de ruta/log para comprobar persistencia, conservación de favoritos, perfil tranquilo y límites de opciones.
- Se conserva la revisión 801 de la migración antigua: añadir opciones no debe volver a activar por error la rotación que el usuario desactivó desde Configuración.
- La imagen original de los dossiers, sus banderas y el contenido del catálogo no se modifican.
- La verificación visual dentro de Minecraft sigue pendiente. Las pruebas automáticas no constituyen capturas ni una revisión del juego ejecutándose.
