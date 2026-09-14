# SIEGE 0.16.0 — Multiplayer completo

Actualización limitada a la interfaz Multiplayer y a sus componentes visuales reutilizables.

## Correcciones solicitadas

1. Los paneles conservan márgenes seguros y ya no parecen cortados contra los bordes.
2. La búsqueda LAN permanece dentro de la columna izquierda.
3. Eliminado el marco blanco seleccionable de la fila LAN.
4. El fondo usa un recorte proporcional tipo cover: llena la pantalla sin deformarse.
5. El estado señalado de cada botón dibuja un marco rojo completo.
6. El destino seleccionado también queda rodeado por un marco rojo completo.
7. Las etiquetas de los botones se ajustaron un píxel hacia arriba para centrarlas visualmente.
8. “Agregar servidor” pasa a “AGREGAR” en español y “ADD” en inglés.
9. Se fija “Servidor oficial de SIEGE / SIEGE Official Server”.
10. Dirección oficial: `SiegeLacontinuacion.exaroton.me:18736`.

## Mejoras adicionales

11. El servidor oficial se detecta por dirección sin distinguir mayúsculas.
12. No se crean duplicados al volver a abrir o actualizar Multiplayer.
13. Si ya existía, se reutiliza y actualiza su nombre.
14. El destino oficial se mantiene primero en la lista.
15. Editar y Borrar se desactivan al seleccionarlo.
16. El panel derecho lo identifica como “DESTINO OFICIAL · FIJO”.
17. La cabecera muestra el estado de destino oficial cuando hay espacio.
18. Todas las acciones usan etiquetas breves y consistentes en mayúsculas.
19. Los servidores normales siguen siendo editables, borrables y reordenables.
20. La detección LAN, ping, iconos, MOTD, conexión directa y ciclo de conexión continúan bajo la lógica vanilla.
21. Las placas desplazadas siguen la posición real del scroll.
22. El estado vacío conserva ayuda legible si no hubiera ningún destino disponible.
23. El cambio de fondo sigue respetando animación, movimiento reducido, gráficos y oscurecimiento.
24. No se modificaron Intel, portada, música, Galería, Configuración ni gameplay.

## Validación

La prueba de geometría recorre 21.306 tamaños lógicos y ahora exige márgenes seguros, separación de paneles, botones dentro de pantalla y centro LAN independiente. Forge compila después las clases reales de Minecraft 1.20.1.
