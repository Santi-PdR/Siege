# SIEGE 0.15.0 — Multiplayer

Multiplayer adopta la presentación de SIEGE; el único cambio en portada es que Despliegue abre la nueva pantalla.

- Fondo fotográfico existente, oscurecido para lectura; respeta configuración de fondos y movimiento.
- Cabecera SIEGE / DESPLIEGUE y contador real de servidores guardados.
- Lista centrada, placas alternadas y acento rojo de selección.
- Conectar, Conexión directa y Agregar en una fila; Editar, Eliminar, Actualizar y Volver en otra.
- Botones SIEGE, sonidos de hover/click/volver y foco de teclado.
- Estados de consulta, sin respuesta, versión incompatible y online con latencia; no afirman compatibilidad de mods.
- Panel del destino en anchos de 640 píxeles lógicos o más: nombre, estado, versión y mensaje del servidor. Ayuda al señalarlo con el texto completo.
- En tamaños menores, resumen bajo la lista con ayuda para nombres largos.
- Acciones de selección sincronizadas inmediatamente; editar/eliminar siguen desactivadas sin un servidor guardado seleccionado.
- Actualizar y F5 conservan la pantalla SIEGE; Volver/Escape regresan directamente al padre.
- Vanilla conserva servidores, iconos, ping, número de jugadores, protocolos, LAN, edición, confirmación de borrado y conexión. No se fija ningún servidor ni se altera servers.dat fuera de las acciones normales del usuario.
- Idiomas españoles e ingleses siguen la selección del cliente. No se muestran direcciones adicionales.

## Validación

CI compila la pantalla real con Forge y ejecuta MultiplayerLayoutTest sobre 21.306 tamaños lógicos, además de la suite existente. Las pruebas geométricas no validan fuentes, audio ni conexión real.

Pendiente en Minecraft: escalas 1–4 a 1366×768 y 1920×1080; lista vacía/larga; nombre y MOTD largos; sin respuesta y protocolo incompatible; LAN; Tab/Enter; añadir/editar/borrar/cancelar; F5; cancelar conexión y volver de un fallo; cambiar tamaño; volver con un clic/Escape; comprobar tooltip de ping/Forge y ausencia de música al entrar al mundo.

Base preservada en la rama backup/pre-multiplayer-0.14.0. Intel, su dossier derecho, tropas, galería, configuración y lógica de audio no se modifican.
