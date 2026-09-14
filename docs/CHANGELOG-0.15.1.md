# SIEGE 0.15.1 — Corrección visual de Multiplayer

Corrección basada en la comprobación dentro de Minecraft a escala 3.

- La búsqueda LAN ya no se centra respecto de toda la pantalla ni queda recortada bajo el panel derecho.
- La fila LAN se presenta como un estado SIEGE compacto dentro de la columna de servidores.
- La fila de búsqueda no puede quedar seleccionada ni mostrar el marco blanco de destino.
- Los puntos de actividad permanecen dentro de la fila y no aparecen separados.
- Las placas usan la posición real de cada fila al desplazarse.
- Cuando no hay servidores guardados ni encontrados, la lista muestra una explicación breve y útil en vez de quedar completamente vacía.
- Se conservan la cabecera, el panel de destino derecho, los botones, los sonidos y todo el comportamiento vanilla de descubrimiento y conexión.
- No se modifican Intel, portada, música, Galería ni Configuración.

## Validación

La prueba geométrica verifica que el centro del indicador LAN pertenece siempre a la lista y, en el diseño de dos columnas, es independiente del centro de la pantalla. La comprobación visual final dentro de Minecraft sigue siendo necesaria.
