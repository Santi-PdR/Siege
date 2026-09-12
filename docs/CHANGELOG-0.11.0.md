# SIEGE 0.11.0

Implementación de la revisión de 0.10.5, conservando la distribución aprobada y el arte/lore original.

- Advertencia bajo la imagen completa y desplazable; eliminados puntos suspensivos añadidos y estadísticas duplicadas. En alturas que no permiten la ficha independiente, el texto completo sigue en el lector principal.
- Caché del cuerpo de Intel y de la advertencia por expediente, ancho, idioma y estilo; ancla de párrafo para reenvolver el cuerpo al cambiar el ancho. No se prometen FPS sin medición.
- Catálogo independiente de las pantallas, acceso directo y validación de identidad/categoría/recursos. Listas de portada y categorías preparadas una sola vez.
- Retiradas pantallas antiguas V1/V2 e Índice, configuración obsoleta de Favoritos/Índice y sus pruebas. Datos originales migrados sin editar su contenido.
- Cabecera de portada reserva espacio real para AUTO/FIJO/ABRIR y limita el ancho del nombre.
- Audio confirma actividad del motor; recuperación de la misma pista tras inactividad sostenida, con gracia inicial y máximo de dos intentos por selección. Aviso musical al confirmar inicio.
- Modelo de rotación del dossier probado con hover prolongado, salida de cursor, espera de 2 s, ciclo normal y pausa manual de 15 s.
- Galería con un único acceso a vista limpia; miniaturas con márgenes neutros; panel de contraste comparte las reglas de la portada. Eliminada la sombra izquierda fija adicional.
- Configuración conserva ayudas descriptivas, corrige agarre de barra y retira el segundo reproductor informativo.
- Barrido de botones limitado a una entrada breve al señalar, sin repetirse continuamente. Respeta Movimiento reducido.
- CI con cancelación de builds superados y cachés de recursos por fuentes/scripts; pruebas del catálogo, audio e instalador real con GitHub simulado.
- Publicación con manifiesto de versión, commit y SHA-256. Instalador descarga sólo artefacto/manifiesto de una revisión fija, verifica hash y ZIP antes de reemplazar, mantiene respaldo y admite otra ruta como argumento.

## Estado de la revisión

Implementados los cambios de código de los puntos 1–2, 4–5, 8–10, 12–26, 28–29. Puntos 6–7: cobertura automática ampliada; render real pendiente. Punto 11: ancla del párrafo al cambiar anchura; el cambio entre idiomas no garantiza equivalencia exacta de posición. Punto 27: ForgeGradle fijado en 6.0.54, la versión resuelta y compilada por CI. Punto 30: matriz visual preparada en QA-0.11.0.md, pendiente dentro de Minecraft.

Corrección de la revisión anterior, punto 3: los sliders actualmente construidos por Configuración usan 25/31 px, no 19 px. No se cambia su geometría por el supuesto incorrecto; su apariencia con la fuente real queda en QA.

El audio usa salida y entrada secuenciales; no se anuncia como crossfade entre dos pistas simultáneas.

La altura de la imagen reserva espacio para la advertencia en ventanas bajas; prueba geométrica añadida. El primer build pasó 8448 tamaños, pruebas de audio/rotación/catálogo, recursos e instalador.
