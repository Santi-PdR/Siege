# SIEGE 0.17.0 — 100 mejoras y correcciones

Base: 0.16.1, commit `e2bfccd40a7c285b1b2624e3784777d03b13b802`.

Esta entrega prioriza funcionamiento, accesibilidad y estabilidad. Incluye correcciones pequeñas y de casos límite: no presenta 100 funciones visuales nuevas ni cuenta traducciones o pruebas como mejoras adicionales.

Se conservan posiciones, imágenes completas, lore y músicas. No se añaden atajos, gameplay, telemetría, controles eliminados ni texto permanente a Multiplayer.


## Búsqueda

1. Frases sin comilla de cierre siguen buscando el último término.
2. Comillas tipográficas y angulares aceptadas al pegar texto.
3. Espacios no separables reconocidos entre palabras.
4. Texto de ancho completo y ligaduras normalizados.
5. Guiones Unicode equivalentes a los de los códigos Intel.
6. Marcas invisibles de pegado ignoradas.
7. Operador de exclusión vacío no elimina todos los resultados.
8. Términos repetidos se evalúan una sola vez.
9. Consulta analizada una vez por filtrado, no por expediente.
10. Patrones de normalización compilados una sola vez.

## Ajustes

11. Archivo malformado conserva los ajustes activos y el original.
12. Migración respeta rotación desactivada explícitamente.
13. Booleanos toleran espacios al editar el archivo.
14. Valores numéricos toleran espacios.
15. Perfil gráfico acepta minúsculas y espacios.
16. Guardado evita escrituras idénticas en cada clic.
17. Valores fuera de rango se corrigen antes de guardarse.
18. Carga y guardado serializados para evitar carreras.
19. Restaurar ajustes respalda un archivo corrupto antes de reemplazarlo.
20. Estado de fallo de guardado disponible para informar en pantalla.

## Visor

21. Cámara válida incluso antes del primer redimensionado.
22. Dimensiones inválidas no contaminan la cámara.
23. Zoom inválido se ignora sin perder el encuadre.
24. Arrastres inválidos no rompen la posición.
25. Minimapa rechaza coordenadas inválidas.
26. Zoom de rueda respeta la magnitud y los movimientos pequeños del touchpad.
27. Soltar un arrastre se consume sin propagarse a otros controles.
28. Área de imagen conserva altura positiva en ventanas mínimas.

## Controles

29. Botones desactivados no ejecutan callbacks por llamadas directas.
30. Botones rechazan dimensiones negativas.
31. Animaciones y pulsación no dependen de cambios del reloj del sistema.
32. Slider alinea su zona de clic con la pista dibujada.
33. Arrastre del slider conserva valores enteros exactos.
34. Soltar el slider guarda el último valor y termina el arrastre.
35. Flechas del slider avanzan exactamente un punto, independientemente del ancho.
36. Cambios de slider por teclado se guardan inmediatamente.
37. Slider tiene marco de foco distinguible del hover.
38. Valores largos del slider no invaden el margen izquierdo.

## Galería

39. Fijar fondo desactivado si ya está aplicado.
40. Rotación automática desactivada si ya está activa.
41. Ayudas explicativas conservadas tras cambiar de fondo.
42. Huecos de la última página no reciben foco ni tooltips.
43. Transición no produce alfa negativo si cambia el reloj.
44. Deshacer muestra el fondo que acaba de restaurar.
45. Ayuda de miniatura incluye posición y nombre completo.
46. Salir de vista limpia no reactiva huecos de miniaturas.

## Intel

47. Abrir dossier desde portada ignora filtros anteriores que lo ocultaban.
48. Redimensionar cancela arrastres y zonas de clic antiguas.
49. Limpiar búsqueda devuelve el foco al buscador y emite sonido SIEGE.
50. Navegar una categoría de un solo expediente no reinicia su animación.
51. Cambiar categoría reinicia la animación del expediente entrante.
52. Búsqueda sin resultados descarta cachés de lectura antiguas.
53. Posición de lectura independiente para español e inglés.
54. Soltar barras de lectura consume correctamente el evento.
55. Advertencia reserva espacio para su barra y conserva posición al cambiar ancho.
56. Texturas de expedientes reutilizan identificadores en vez de crearlos cada frame.

## Audio

57. Reloj monotónico evita saltos de pista al ajustar la hora del sistema.
58. Recuperación de audio no reinicia una pista mientras se está abandonando.
59. Fade de entrada empieza cuando el motor realmente reproduce audio.
60. Anterior actualiza la pista fija para no volver luego a la equivocada.
61. Volver a aleatorio conserva la cola pendiente si ya estaba en aleatorio.
62. Reiniciar permite arrancar música desactivada aun sin pista anterior.
63. Música del menú se detiene durante conexión y carga del mundo.
64. Estado distingue música desactivada, silenciada y esperando motor.
65. Un dato de duración corrupto no invalida las otras pistas; duración absurda se rechaza.
66. Ganancia no finita se convierte a silencio para proteger el motor de audio.

## Multiplayer

67. Elimina duplicados del servidor oficial conservando la primera entrada y sus preferencias.
68. Fijar el servidor preserva el orden relativo de los demás.
69. Abrir Multiplayer sin cambios deja de reescribir servers.dat.
70. Protección de edición y eliminación también se comprueba dentro del callback; la ayuda usa el área del botón aunque esté desactivado.
71. Ayudas de Editar y Eliminar sólo se recrean cuando cambia la selección.
72. Actualizar restaura el servidor seleccionado por su dirección.
73. Actualizar conserva la posición de desplazamiento.
74. Actualizar tiene límite de frecuencia para evitar múltiples consultas simultáneas.
75. El servidor oficial vuelve al primer lugar si se intenta reordenar.
76. Fila LAN recortada al área de lista durante el scroll.
77. Modo movimiento reducido detiene los puntos animados de LAN.
78. Panel derecho recorta contenido para no invadir botones en ventanas bajas.
79. IP truncada puede leerse completa al señalarla.
80. Una consulta pendiente o fallida no muestra una versión incompatible sin confirmar.

## Configuración

81. Panel no fuerza ancho mayor que la ventana disponible.
82. Ayuda de pausa Intel explica correctamente la reanudación a los dos segundos.
83. Ayuda de lectura espaciada describe el efecto real.
84. Shift+Tab sin foco comienza por el último control, sin saltarse uno.
85. Soltar barra de ajustes no filtra clic a otro widget.
86. Cerrar ajustes cancela cualquier arrastre activo.
87. Fallo al guardar se muestra dentro de la pantalla.
88. Traducción de perfil gráfico independiente de la configuración regional del sistema.
89. Touchpad acumula desplazamientos fraccionarios sin perderlos.
90. Scroll en el límite deja de consumir movimientos sin efecto.

## Instalación

91. Archivos temporales con permisos privados desde el inicio.
92. Bloqueo por carpeta impide dos instalaciones simultáneas.
93. Dependencias de copia y comparación comprobadas antes de descargar.
94. Revisión Git validada antes de construir URLs.
95. Versión del manifiesto debe coincidir con el nombre del JAR.
96. Archivo descargado debe declarar modId siege y la versión exacta.
97. JAR con entradas duplicadas o rutas inseguras rechazado.
98. Destino simbólico o no regular rechazado sin reemplazarlo.
99. Reinstalar el mismo JAR intacto no crea respaldos redundantes.
100. Cada respaldo se compara antes de reemplazar; funciona con carpetas personalizadas.

## Validación

Pruebas de búsqueda, cámara, configuración e instalador ampliadas para los casos corregidos. La compilación Forge y las pruebas Java se ejecutan en GitHub Actions. La validación visual en Minecraft con escalas 1–4 queda pendiente; las pruebas de geometría no la sustituyen.
