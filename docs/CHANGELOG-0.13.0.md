# SIEGE 0.13.0 — Primeras unidades Élite

## Nuevas tropas

- **ELT-001 / AGARES — 25.000 HP.** Expediente bilingüe sobre telequinesis, sabotaje eléctrico, peligro extremo para cyborgs, teletransportadores vinculados a su supervivencia y equipo Two Sides.
- **ELT-002 / GHOST — 25.000 HP.** Expediente bilingüe sobre invisibilidad sin recarga, ataques por la espalda, daga venenosa y dinamita Creeper como señuelo.
- **ELT-003 / AURELIONIS — 1 HP.** El archivo conserva `???` en todos los datos no proporcionados. No se inventaron capacidades, origen, armamento ni recomendación táctica.
- La categoría ÉLITES deja de estar vacía y muestra un contador real de tres expedientes.
- Cada unidad incorpora una imagen estática de 640×360 creada desde el video suministrado. El logotipo incrustado en la captura queda fuera del encuadre y los videos completos no se empaquetan en el mod.

## Bugs y protecciones

- Corregido un posible solapamiento en cabeceras estrechas: el nombre de la categoría sólo se dibuja cuando existe espacio real junto al código y al contador.
- El generador crea el directorio de salida cuando falta, evitando un fallo al preparar recursos desde un árbol limpio.
- El generador rechaza fuentes Élite inesperadas y detiene la compilación si falta alguna de las tres capturas requeridas.
- Las imágenes Élite se verifican como PNG válido de 640×360 antes de compilar.
- La prueba de catálogo exige exactamente tres ÉLITES, códigos únicos y el orden canónico Agares, Ghost, Aurelionis.
- La prueba de recursos comprueba que cada nuevo expediente tenga una textura existente.
- Una regresión específica impide convertir los datos desconocidos de Aurelionis en información inventada.

## Alcance de validación

La compilación automatizada valida catálogo, recursos, navegación y geometría. La apariencia final todavía debe comprobarse dentro de Minecraft con las escalas de interfaz reales; una prueba automática no sustituye esa inspección visual.
