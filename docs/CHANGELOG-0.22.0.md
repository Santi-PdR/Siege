# SIEGE 0.22.0 — Pulido global del menú

Esta actualización es un pase de calidad transversal sobre la interfaz completa. No cambia mecánicas de juego ni sustituye pantallas vanilla fuera del alcance de SIEGE.

## Controles compartidos

- Los botones de SIEGE ahora usan una transición de hover más suave y una respuesta de pulsación animada en lugar de un cambio binario.
- El feedback de pulsación conserva un desplazamiento mínimo de un píxel y respeta Movimiento reducido y Efectos del menú.
- El foco de teclado se diferencia claramente del hover mediante esquinas doradas, sin modificar las zonas clicables.
- Los estados seleccionado, desactivado y enfocado tienen mayor separación visual.
- El barrido táctico de hover sigue siendo barato: no usa ruido aleatorio, texturas adicionales ni bucles de píxeles por botón.
- Los iconos, badges y etiquetas truncadas conservan su geometría previa para no romper GUI scales 1–4.

## Sliders

- Los sliders comparten ahora el mismo lenguaje visual de foco que los botones.
- El relleno de la barra y las marcas de 25% muestran mejor qué parte ya está aplicada.
- El knob gana una respuesta visual gradual al hover, foco y arrastre.
- El render tolera etiquetas vacías sin provocar errores.
- Arrastre, guardado al soltar y control con flechas permanecen intactos.

## Paneles y superficies

- Los paneles reciben profundidad mediante líneas internas y sombras de un píxel sin cambiar dimensiones ni hitboxes.
- Los acentos de esquina fueron refinados para Settings, Intel, Gallery, Guide, Multiplayer y diálogos nativos.
- Se añadió un sistema común de focus corners y divisores para evitar estilos distintos entre pantallas.

## Diálogos y campos de texto

- Los campos de texto de las pantallas tematizadas muestran un borde dorado consistente cuando tienen foco.
- Connect, Direct Join, Edit Server, confirmaciones y errores conservan su lógica vanilla pero se integran mejor con el resto de SIEGE.
- Tooltips y audio de interfaz siguen usando el tema y sonidos propios del mod.

## Versión visible

- El rótulo de build de la portada deja de depender del texto histórico `BUILD 0.13.0`.
- La versión visible se obtiene de los metadatos reales del mod, por lo que 0.22.0 y futuras versiones ya no necesitan corregir manualmente ese texto.
- El rótulo sigue siendo pequeño y sólo aparece cuando `showBuildLabel` está activado.

## Rendimiento y compatibilidad

- No se incorporó el ruido visual por píxel de ramas experimentales antiguas.
- Las nuevas animaciones son constantes en coste y se desactivan con Movimiento reducido.
- No se modifican IntelData, dossiers, música, fondos, Multiplayer persistence, servidor oficial ni rutas de navegación.
- Forge 1.20.1 / Java 17 y los tests existentes continúan siendo la referencia de compilación.

La validación final requiere que `Build Forge mod` termine en `success` antes de integrar esta versión en `main`.
