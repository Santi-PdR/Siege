# SIEGE 5.60.0 — Adaptive Command

5.60 corrige la dirección de audio de 5.40/5.50 y continúa mejorando la presentación del menú sin meter música nueva sin revisión del jugador.

## Música: aprobación antes de integración

Las tres pistas sintéticas creadas durante 5.40/5.50 fueron retiradas del sistema activo:

- `Stronghold 5-5 · Black Signal`
- `Nucleus · Silent Carrier`
- `Tesla Breach`

Ya no forman parte de `SiegeMusic`, no tienen eventos en `sounds.json` y el pipeline de build dejó de generarlas. Los dos scripts usados para sintetizarlas también fueron eliminados.

La playlist vuelve temporalmente a las cinco pistas previamente aceptadas. Las siguientes incorporaciones se investigan entre música real existente y sólo se empaquetan después de que el jugador pueda escucharlas y las apruebe. Para música pública se priorizan licencias claras de redistribución; las bandas sonoras comerciales de juegos de inspiración pueden usarse como referencia de dirección, no como archivos del JAR sin permiso.

## Fondos adaptativos

Los tres controles de fondo creados para 5.60 ahora afectan de verdad al renderer:

- duración de cada escena: 12–60 s;
- duración del crossfade: 0–10 s, limitada automáticamente a la mitad de la escena;
- intensidad de movimiento cinematográfico: 0–100%.

El renderer dejó de usar los antiguos 24 s / 4.8 s fijos. El movimiento usa un overscan pequeño y conserva la relación de aspecto, por lo que no aparecen barras ni estiramiento. Cada escena usa una dirección de desplazamiento determinista para que la rotación tenga variedad sin temblores aleatorios.

`Reduced Motion`, `Reduce Flashes` y el modo `Performance` siguen siendo límites estrictos: desactivan el desplazamiento cinematográfico. Un crossfade configurado en cero también es un modo real y seguro, sin división por cero.

## Presets audiovisuales

Los presets Stronghold / Núcleo / Tesla se mantienen porque sus fondos sí funcionan bien, pero mientras no haya música aprobada pasan a ser **presets de escena solamente**. No fuerzan una pista experimental a escondidas.

## Control de calidad

Se agrega `test_release_560.py` para impedir que vuelvan silenciosamente las pistas rechazadas o sus generadores y para exigir que la configuración adaptativa de fondos esté realmente conectada al renderer.

La corrección de Third Justice, la separación de Tempest Jutcherson, la resolución nativa de fondos DVN y los controles Intel retirados siguen siendo contratos de versiones anteriores y no deben retroceder.
