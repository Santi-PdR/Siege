# SIEGE 0.70.0 — Deployment & Intel Recovery

0.70 completa el segundo salto grande desde 0.50. La versión reconstruye la lectura operativa de Multiplayer/Despliegue y repara la nueva tanda de Intel que estaba mostrando texturas ausentes. La conexión y persistencia siguen en manos de Minecraft/Forge; SIEGE mejora la presentación, clasificación y control visual.

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
47. La categoría usa el código compacto `UNK`.
48. La categoría Desconocido usa un acento neutral propio.
49. El layout Intel calcula automáticamente su geometría con siete categorías.
50. Desconocido aparece tanto en interfaz ancha como compacta.
51. Engineer pasa a Desconocido mientras no exista clase confirmada.
52. Informant pasa a Desconocido mientras no exista clase confirmada.
53. Grappler pasa a Desconocido mientras no exista clase confirmada.
54. Tranquilizer pasa a Desconocido mientras no exista clase confirmada.
55. Skydiver pasa a Desconocido mientras no exista clase confirmada.
56. Skyliner pasa a Desconocido mientras no exista clase confirmada.
57. Agitator conserva su clasificación Tank.
58. Sparta conserva su clasificación Boss.
59. Fauna, Cerberus y Proteus conservan su clasificación Elite.
60. Sauron y Hedalus conservan su clasificación Super Unit.
61. Las tropas afectadas dejan de apuntar a la ruta inexistente `classified`.
62. Los expedientes no-Boss sin imagen usan `placeholder/classified.png`.
63. Sparta usa el placeholder Boss animado `bosses/classified/frame_00..05`.
64. Los dossiers sin imagen indican **SIN REGISTRO VISUAL**.
65. La interfaz inglesa indica **NO VISUAL RECORD**.
66. No se inventan fotografías, apariencias ni renders para completar archivos faltantes.
67. No se inventan categorías cuando el tipo de unidad no está confirmado.
68. El resumen global de Intel incluye contador de expedientes desconocidos.
69. Las pruebas de runtime verifican categoría, placeholder y archivo de imagen real para cada dossier.
70. CI protege simultáneamente 0.60, el overhaul de despliegue 0.70 y la reparación de Intel.

## Límite de alcance

0.70 no reemplaza la lógica de red de Minecraft, no modifica daño, IA, HP, raids ni reglas de servidor. Los cambios de Intel son de clasificación/presentación y preservan los datos confirmados existentes.
