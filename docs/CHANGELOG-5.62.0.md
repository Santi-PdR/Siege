# SIEGE 5.62.0 — Rotation Fairness

5.62 continúa la mejora del cliente fuera de la música. Esta revisión corrige un problema poco visible pero real en la rotación de fondos: las escenas destacadas se insertaban en seis posiciones fijas de cada 100, pero al hacerlo consumían también una posición del ciclo normal. El resultado era que algunos fondos estándar podían ser salteados antes de completar la bolsa de rotación.

## Rotación sin fondos perdidos

La línea de tiempo de fondos ahora separa dos conceptos:

- **slot global**: incluye las seis apariciones destacadas de cada 100 posiciones;
- **slot estándar**: avanza únicamente cuando realmente se muestra un fondo estándar.

Cuando aparece la escena destacada en las fases `8, 26, 44, 62, 80 y 98`, la bolsa estándar queda pausada durante ese slot y continúa exactamente donde estaba en el siguiente. De esta forma, la escena destacada funciona como una inserción y no como un reemplazo.

Esto conserva:

- seis slots destacados por cada 100;
- selección determinista;
- ausencia de repeticiones consecutivas;
- bolsa barajada de fondos estándar;
- todos los fondos estándar antes de repetir una bolsa completa;
- comportamiento correcto también con índices negativos, usado por las pruebas de continuidad.

## Operaciones conserva el contexto

El Centro de Operaciones ahora se comporta como un espacio de trabajo continuo durante la sesión. La búsqueda que estés usando queda guardada en la propia pantalla y se restaura cuando la interfaz reconstruye sus controles.

Esto evita perder el texto al:

- cambiar entre las áreas de Despliegue, Inteligencia y Referencia;
- redimensionar la ventana o cambiar una escala que obligue a reconstruir la pantalla;
- abrir un dossier Intel o una ficha de conocimiento y volver a Operaciones.

La consulta no se guarda permanentemente en disco: es contexto de navegación de esa sesión/pantalla, por lo que tampoco contamina las preferencias del cliente.

## Pruebas

`SceneScheduleTest` ahora recorre múltiples bloques positivos, negativos y muy alejados del origen. Además de comprobar los seis slots destacados, junta grupos completos de escenas no destacadas y exige que cada grupo contenga todos los fondos estándar exactamente una vez.

`test_release_562.py` protege la compresión de slots, la continuidad de Scene Intelligence 5.61, el aislamiento de `Tempest Jutcherson` y la persistencia de búsqueda de Operaciones.

El contrato durable de 5.61 pasa a aceptar versiones posteriores y, durante la transición del workflow, ejecuta automáticamente el gate 5.62 cuando detecta una versión `5.62.0` o superior.

## Lo que no cambia

- Third Justice sigue usando capturas full-color y su reel completo.
- Las seis escenas oficiales DVN siguen diferenciadas de los tratamientos SIEGE.
- `Tempest Jutcherson` continúa fuera de la rotación y galería normales.
- Las pistas sintéticas rechazadas no regresan.
- `A Stranger I Remain` y `Receive You The Hyperactive` siguen siendo slots aprobados pero opcionales: no se presentan como instalados si sus archivos legítimos no están disponibles.
