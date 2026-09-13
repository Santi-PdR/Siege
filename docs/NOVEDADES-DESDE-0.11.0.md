# Cambios incorporados después de SIEGE 0.11.0

Revisión del estado real de `main` realizada antes de iniciar 0.12.0.

1. El instalador dejó de enviar el JAR binario por la salida textual de `gh`; descarga a archivo y mantiene verificación SHA-256 y respaldo.
2. SIEGE 0.11.1 eliminó la advertencia táctica del lector izquierdo.
3. La advertencia completa quedó únicamente en la ficha derecha, con su desplazamiento independiente.
4. El título principal pasó a estar centrado en la parte superior de la ventana.
5. Los efectos de interfaz ya no cierran el menú si Forge aún no expuso el valor de un registro de sonido.
6. La música aplica el mismo reemplazo seguro cuando falta temporalmente un valor del registro.

Commits revisados: `2a05146`, `894b854`, `f69f3bd`, `c815456` y `01a8ca2`.

