# SIEGE 5.61.0 — Scene Intelligence

5.61 continúa el trabajo de 5.60 fuera del soundtrack. La prioridad es que los fondos expliquen claramente de dónde salen, qué está usando el menú y qué va a aparecer después, sin mezclar capturas oficiales con tratamientos propios.

## Procedencia de fondos

El catálogo de escenas ahora guarda una procedencia explícita para cada imagen:

- **DVN OFICIAL**: miniaturas oficiales de Dummies vs Noobs conservadas a `768×432`.
- **TRATAMIENTO SIEGE**: variantes visuales creadas por SIEGE a partir de una fuente DVN verificada, sin presentarlas como capturas oficiales nuevas.
- **ARCHIVO SIEGE**: escenas históricas/propias del catálogo anterior.

La procedencia no se deduce del nombre del archivo. Vive en `SiegeSceneCatalog`, la misma tabla usada por renderer, galería, estado y CI.

## Galería

La Galería de Fondos muestra la procedencia real en:

- el detalle de la escena seleccionada;
- el tooltip de cada miniatura;
- la ficha de estado junto a `FIJADO / EN USO / SIN APLICAR`.

La categoría visual (`ESCENA`, `DESTACADO`, `ANOMALÍA VISUAL`) sigue separada de la procedencia. Una escena puede ser normal y al mismo tiempo ser `DVN OFICIAL` o `TRATAMIENTO SIEGE`.

## Rotación y Sala Multimedia

`SiegeBackgrounds` expone ahora de forma autoritativa:

- escena actual;
- próxima escena;
- tiempo restante;
- progreso de la rotación;
- procedencia de la escena.

La Sala Multimedia usa esos datos para mostrar el fondo actual con su procedencia y, cuando la rotación automática está activa, el nombre de la siguiente escena junto al tiempo restante.

La pestaña visual dejó de llamarse sólo `FONDOS DVN`: contiene material DVN y SIEGE, por lo que ahora se presenta simplemente como **FONDOS / VISUALS**.

## Estado del cliente

El estado de fondos del Centro de Comando ya no se limita a nombre, duración y movimiento. También indica:

- procedencia de la escena activa;
- próxima escena cuando existe rotación automática;
- si el fondo está fijado, rotando o estático.

Esto evita que una escena tratada por SIEGE se confunda con una captura oficial.

## Protecciones

`test_release_561.py` obliga a conservar:

- la separación `DVN_OFFICIAL / SIEGE_TREATMENT / SIEGE_ARCHIVE`;
- los seis fondos DVN como oficiales;
- los tres tratamientos 5.50 como tratamientos SIEGE;
- la información de procedencia en Galería, Sala Multimedia y Runtime Status;
- el estado de próxima escena;
- el aislamiento de `Tempest Jutcherson` fuera del catálogo normal.

Los contratos 5.60 pasan a ser durables para versiones 5.x posteriores y siguen protegiendo música, accesibilidad, fondos adaptativos, Third Justice e Intel.
