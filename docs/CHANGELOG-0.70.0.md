# SIEGE 0.70.0 — Deployment & Intel Recovery

0.70 completa el segundo salto grande desde 0.50. La versión reconstruye la lectura operativa de Multiplayer/Despliegue y repara la nueva tanda de Intel que estaba mostrando texturas ausentes. La conexión y persistencia siguen en manos de Minecraft/Forge; SIEGE mejora la presentación, clasificación y control visual.

La revisión de Intel usa información pública de Dummies vs Noobs cuando existe una referencia suficientemente clara. Los datos documentados por los update logs del desarrollador tienen prioridad; la wiki actual de DVN se utiliza para complementar apariencia y comportamiento actuales cuando no hay un registro de desarrollo equivalente. SIEGE no copia imágenes externas al mod: mientras no exista un asset local verificado, el expediente usa un placeholder documental válido.

## 70 mejoras

1. La versión del mod avanza a **0.70.0**.
2. Multiplayer pasa a presentarse como consola **DESPLIEGUE 0.70**.
3. Se crea un modelo central `SiegeDeploymentStatus`.
4. El estado de un servidor se calcula una sola vez desde datos vanilla.
5. Se distinguen CONSULTANDO, APAGADO, SIN RESPUESTA, INCOMPATIBLE y EN LÍNEA.
6. Cada estado de despliegue recibe un acento visual coherente.
7. El encabezado muestra el estado del destino seleccionado.
8. Se añade `DESTINO DISPONIBLE` para servidores compatibles en línea.
9. Se añade `VERIFICANDO DESTINO` durante consulta de ping.
10. Se añade `CLIENTE INCOMPATIBLE` cuando los protocolos no coinciden.
11. Se añade `DESTINO NO DISPONIBLE` para offline/sin respuesta.
12. El botón CONECTAR recibe un badge corto según el estado.
13. El badge puede mostrar LISTO, PING, OFF, SIN RED o VERSIÓN.
14. Se añade una banda visual de latencia en el panel de detalle.
15. La latencia se clasifica en baja, media, alta o muy alta.
16. La banda de latencia se rellena según el ping observado.
17. La interfaz no convierte la latencia en una promesa de calidad de juego.
18. El panel derecho pasa de DETALLES a CONTROL DE DESPLIEGUE.
19. El nombre del destino sigue siendo la primera referencia visible.
20. El servidor oficial se marca como DESTINO OFICIAL FIJADO.
21. La IP oficial permanece `SiegeLacontinuacion.exaroton.me:18736`.
22. El servidor oficial continúa fijado en la primera posición.
23. Duplicados del servidor oficial continúan eliminándose de forma segura.
24. Editar permanece bloqueado para el servidor oficial.
25. Eliminar permanece bloqueado para el servidor oficial.
26. Los tooltips explican por qué esas dos acciones están bloqueadas.
27. Los demás servidores conservan Editar y Eliminar normales.
28. Conexión directa conserva el callback vanilla.
29. Agregar servidor conserva el callback vanilla.
30. Conectar conserva el callback vanilla.
31. F5 conserva únicamente su comportamiento vanilla de actualización.
32. No se añade ningún atajo SIEGE nuevo.
33. Actualizar conserva selección cuando es posible.
34. Actualizar conserva la posición de scroll.
35. Se mantiene el throttle de refresh para evitar consultas duplicadas.
36. La fila de escaneo LAN sigue siendo informativa y no un destino seleccionable.
37. El modo compacto recibe un resumen operativo más completo.
38. El resumen compacto incluye estado y latencia cuando corresponde.
39. El panel ancho muestra versión de servidor si es incompatible.
40. El panel ancho muestra también la versión del cliente para comparar.
41. Se añade la ruta visual `DESTINO → ESTADO → CONECTAR`.
42. La interfaz aclara que red y conexión siguen siendo responsabilidad de Minecraft/Forge.
43. Los textos largos continúan recortándose sin invadir otros paneles.
44. La selección activa usa el color de su estado operativo.
45. La lista de servidores mantiene el orden relativo de destinos no oficiales.
46. Intel incorpora una séptima categoría: **DESCONOCIDO / UNKNOWN**.
47. La categoría usa el código compacto `UNK` y un acento neutral propio.
48. El layout Intel calcula automáticamente su geometría con siete categorías.
49. Desconocido aparece tanto en interfaz ancha como compacta.
50. Grappler permanece en Desconocido porque no se encontró una referencia fiable para fijar su clase.
51. Skydiver permanece en Desconocido por la misma razón.
52. Skyliner permanece en Desconocido por la misma razón.
53. Engineer se reclasifica a **Advanced** usando la referencia de DVN; se documentan 150 HP, pistola, llave, Sentry y Teleporter.
54. Informant se reclasifica a **Advanced**; se documentan 155 HP, H94 Rifle, granadas y su comportamiento de Infantry avanzada.
55. Tranquilizer se reclasifica a **Advanced**; se documentan 100 HP, Dart Rifle, máscara de gas y su función de Epilogue/Hell.
56. Agitator conserva **Tank** en SIEGE y recibe sus datos DVN de unidad armoured/mechanical, 300 HP y tanque de combustible vulnerable.
57. Sparta conserva **Boss** y recibe un dossier basado en DVN: Khanblades, jetpack, persecución, stomp y cadenas de cortes.
58. El dossier de Sparta registra 350 HP base y deja explícito que existe escalado por jugador.
59. Proteus deja de figurar incorrectamente como Elite y pasa a **Boss** según la referencia actual de DVN.
60. Proteus documenta Spectral Shotgun, Hivelink, Cloning y Spectral Leap, dejando claro que sigue en desarrollo.
61. Fauna y Cerberus conservan su clasificación Elite existente sin mezclar material no fiable de fangames.
62. Sauron y Hedalus conservan Super Unit y sus datos SIEGE existentes; no se inventan capacidades externas.
63. Todas las tropas afectadas dejan de apuntar a la ruta inexistente `classified`.
64. Los expedientes no-Boss sin imagen usan `placeholder/classified.png`.
65. Sparta usa el placeholder Boss animado `bosses/classified/frame_00..05`.
66. Los dossiers sin asset local indican **SIN REGISTRO VISUAL** o una variante equivalente.
67. No se copian renders o screenshots externos al mod únicamente por existir en Internet.
68. El resumen global de Intel incluye contador de expedientes desconocidos.
69. Las pruebas de runtime verifican las nuevas categorías, los datos DVN documentados y la existencia física de cada recurso de imagen.
70. CI protege simultáneamente 0.60, el overhaul de despliegue 0.70 y la reparación/source-pass de Intel.

## Fuentes de referencia para esta pasada

- Dummies vs Noobs 1.8 update log: introducción y diseño original de Sparta.
- Dummies vs Noobs 1.9 update log: cambios de movilidad, curación y leap de Sparta; introducción de Engineer entre las nuevas unidades.
- Dummies vs Noobs 2.0 update log: Tranquilizer como unidad exclusiva de Epilogue y actualización visual de Sparta.
- Dummies vs Noobs Wiki / documentación pública actual: detalles actuales de Engineer, Informant, Tranquilizer, Agitator, Sparta y Proteus.

## Límite de alcance

0.70 no reemplaza la lógica de red de Minecraft, no modifica daño, IA, HP, raids ni reglas de servidor. Los cambios de Intel son de clasificación/presentación y preservan los datos confirmados existentes. Cuando la fuente pública indica que una unidad sigue en desarrollo, el dossier lo declara expresamente en lugar de presentarla como definitiva.
