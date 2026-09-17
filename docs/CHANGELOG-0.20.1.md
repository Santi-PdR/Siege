# SIEGE 0.20.1

Correcciones del ciclo de entrada y salida de mundos y servidores:

- Al salir de un servidor, el destino vanilla `JoinMultiplayerScreen` se reemplaza por Multiplayer de SIEGE.
- El botón Volver de una desconexión o error tampoco puede devolver a la lista vanilla guardada como pantalla padre.
- Al salir de un mundo local, `TitleScreen` vuelve a convertirse en la portada de SIEGE.
- El enrutamiento se ejecuta antes que el tema de diálogos, por lo que la pantalla correcta recibe fondo, transición y sonidos.
- Sólo se reemplazan las dos clases vanilla exactas. Pantallas de otros mods, subclases y cualquier interfaz mientras hay un mundo cargado quedan intactas.
- Se evita la recursión: las propias pantallas de SIEGE nunca vuelven a reemplazarse.
- Multiplayer reconstruido recibe una portada SIEGE como padre, evitando que Escape o Volver conserven una portada vanilla oculta.
- El orden del servidor oficial ahora se guarda en `servers.dat` cuando hubo que moverlo al primer puesto.
- El servidor oficial recién creado o deduplicado también fuerza el guardado de la lista final.
- El orden relativo de todos los demás servidores continúa intacto.

Validación añadida para ocho rutas: inicio, salida de mundo, salida de servidor, Volver tras desconexión, cierre de pantalla, pantallas SIEGE, subclases externas y protección dentro de mundos.

Sin cambios de gameplay, posiciones, Intel, música, controles retirados ni dirección de los paneles. Revisión visual dentro de Minecraft pendiente.

Respaldo: `backup/pre-0.20.1`.
