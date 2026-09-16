# SIEGE 0.18.0 — coherencia visual de los menús

Base: 0.17.0. Respaldo: `backup/pre-0.18.0`.

## Pantallas de conexión y formularios

- Conexión y desconexión: fondo SIEGE, superficie carbón y bordes militares discretos.
- Agregar, editar y conexión directa: controles tematizados, campos con texto claro y marco de foco rojo.
- Confirmaciones abiertas desde SIEGE: mismo acabado para salir, eliminar servidor o restaurar preferencias.
- Se mantienen las pantallas nativas y sus callbacks: dirección, validación, motivo real de error, estado de conexión y cancelación no se simulan.
- Los controles reflejan el estado activo, visible, mensaje y ayuda del control original.
- Lista explícita de pantallas: no se aplica al mundo, chat, inventario, pausa ni ajustes de vídeo. Confirmaciones ajenas tampoco se tematizan.

## Acabado común

- Carbón, gris cálido y rojo como base; música en dorado y categorías Intel conservan sus colores.
- Botones con relieve tenue, borde completo y desplazamiento de texto al pulsar.
- Iconos propios de 9×9 dibujados mediante código: conexión, expediente, ajustes, música y pin.
- Iconos junto a los nombres, sin reemplazar etiquetas ni mover los botones de portada.
- Sliders con paneles del tema y acento dorado en música.
- Ayudas emergentes oscuras con borde rojo apagado, conservando sus textos.
- Fundido de entrada de 180 ms con opacidad máxima baja; no bloquea entrada ni se aplica dentro del gameplay. Se desactiva con movimiento reducido o efectos desactivados.
- Sonidos propios en controles de los diálogos y mezcla más suave para hover y volver. Confirmación diferenciada.

## Intel, Multiplayer y Galería

- Grano tenue y grapas pequeñas únicamente en los márgenes vacíos del papel. Arte, fuentes y sellos existentes se conservan.
- Dossier y advertencia siguen a la derecha; no vuelve la advertencia izquierda ni controles eliminados.
- Multiplayer mantiene sus dos zonas y usa selección roja más sobria.
- Insignia pequeña de pin acompaña el rótulo OFICIAL; se conserva la dirección protegida.
- Galería con marco común, miniaturas enmarcadas y pin coherente para el fondo aplicado.
- Fondo e imágenes completos; Fondos continúa en Configuración.
- Se retira el pie técnico permanente de Configuración para reducir texto innecesario.

## Validación

Pruebas nuevas sobre exclusión de gameplay/pantallas ajenas, límites del fundido, límites de paneles e iconos y márgenes del papel. Se mantienen las pruebas anteriores de geometría, configuración, búsqueda, catálogo e instalador.

La compilación Forge se verifica en GitHub Actions. La inspección visual dentro de Minecraft en escalas 1–4 sigue pendiente; las pruebas de dibujo no equivalen a una captura del cliente real.
