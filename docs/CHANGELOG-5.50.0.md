# SIEGE 5.50.0 — Immersive Front

SIEGE 5.50 continúa la expansión audiovisual de 5.40 sin convertir el menú en algo más cargado. El objetivo de esta versión es que los fondos y la música puedan acompañar mejor el estado del frente y que la Sala Multimedia tenga controles que realmente cambian la experiencia, manteniendo al mismo tiempo las reglas de calidad introducidas en 5.30 y 5.40.

## Tres nuevos fondos SIEGE

Se agregan tres tratamientos tácticos generados durante el build a partir de escenas oficiales de Dummies vs Noobs ya verificadas. No se presentan como capturas oficiales nuevas y no se reescalan artificialmente: conservan el lienzo original de 768×432.

- **SIEGE · Interferencia del Núcleo**: separación cromática leve, cortes de señal y telemetría concentrada principalmente a la derecha para no ensuciar la navegación.
- **SIEGE · Ruptura Tesla**: arcos eléctricos, brillo localizado y una lectura más fría para amenazas Tesla/Nusia.
- **SIEGE · Stronghold en alerta roja**: iluminación de emergencia roja, barras discretas y partículas contenidas para una situación de defensa o crisis.

La rotación normal pasa a tener 21 escenas. Las seis escenas oficiales DVN continúan separadas de estos tres tratamientos y Tempest Jutcherson sigue completamente fuera de la galería y de la rotación normal.

## Dos nuevas músicas originales

La playlist instalada pasa de seis a ocho pistas.

- **Nucleus · Silent Carrier** — ~116 s. Ambiente electrónico lento para El Núcleo, briefing e Intel: graves sostenidos, pulsos codificados, ruido de radio y una portadora que aparece y desaparece sin voces.
- **Tesla Breach** — ~104 s. Ambiente eléctrico-industrial para amenazas de Nusia, Tesla y bosses: pulso pesado, arcos sintetizados y resonancias metálicas.

Las dos pistas se generan de forma determinista durante CI, igual que Black Signal, y pasan por el mismo pipeline de Ogg Vorbis estéreo a 44.1 kHz, validación de duración, decodificación y headroom.

## Presets audiovisuales reales

La pestaña de ambientes de la Sala Multimedia ya no funciona sólo como referencia. 5.50 añade tres presets accionables:

- **STRONGHOLD** → `Stronghold 5-5 · Black Signal` + `SIEGE · Stronghold en alerta roja`.
- **NÚCLEO** → `Nucleus · Silent Carrier` + `SIEGE · Interferencia del Núcleo`.
- **TESLA** → `Tesla Breach` + `SIEGE · Ruptura Tesla`.

Al aplicar uno se selecciona realmente la pista instalada y se fija su escena correspondiente. La rotación automática puede volver a activarse desde la misma Sala Multimedia.

## Dirección audiovisual

La base de referencias distingue ahora tres grupos de forma explícita: material oficial DVN realmente cargado, tratamientos SIEGE que sí están disponibles en la rotación y conceptos de dirección que todavía no son assets del mod. Esto evita que una idea futura aparezca descrita como contenido ya instalado.

También se añade el ambiente **FRENTE TESLA** y se actualiza **SEÑAL DEL NÚCLEO** para usar las nuevas pistas originales en lugar de depender sólo de referencias externas.

## Información del servidor

La capa de conocimiento 5.40 continúa siendo la fuente pública actual de contexto, con El Núcleo, Stronghold 5-5, facciones del frente, Gates/Rifts/Agreements y lectura de amenazas. 5.50 no inventa nueva información sólo para llenar la Enciclopedia: cuando una capacidad o estadística no está confirmada, Intel sigue marcándola como desconocida o parcial.

Se preservan las reglas ya establecidas: la Enciclopedia explica sistemas generales, Intel se usa para unidades concretas y los dossiers no convierten suposiciones o material de fans en datos oficiales.

## Validación

- Nuevo release gate `test_release_550.py`.
- Los contratos 5.30 y 5.40 permanecen activos como garantías duraderas para versiones posteriores.
- El audit visual valida las 21 escenas del catálogo, incluidas las tres nuevas variantes 768×432.
- `GuiResourceRegressionTest` verifica de forma explícita las seis escenas oficiales y los tres tratamientos SIEGE.
- CI genera y valida las dos nuevas pistas, confirma Vorbis/44.1 kHz y comprueba que los archivos no estén truncados.
- El JAR sólo se publica en `dist/` después de que el build completo de Forge termine correctamente en `main`.
