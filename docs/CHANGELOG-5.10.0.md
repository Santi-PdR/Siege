# SIEGE 5.10.0

## Enciclopedia y lenguaje del jugador

- Nueva capa final de conocimiento pensada para jugadores, no para investigación interna.
- La interfaz normal deja de mostrar lenguaje como `staff confirmado`, `confidence`, `Discord`, `JSON`, `corpus`, `export`, cantidades de mensajes o metodología de revisión.
- Las referencias internas siguen pudiendo existir para mantenimiento, pero no forman parte de las fichas visibles.
- `raid-area-discipline` queda fuera de las superficies normales para no convertir una experiencia personal con oxidación durante una raid en una regla general del servidor.
- Intel reemplaza el tooltip de `testimonio / no verificado por staff` por mensajes naturales de `Información incompleta` / `Resumen del expediente disponible`.

## Información ampliada

- Catálogo de razas ampliado y ordenado con más de cincuenta nombres recuperados.
- Xeno Saiyan obtiene entrada separada y deja de quedar perdido dentro de Saiyan.
- Mink se describe como variante inspirada en One Piece centrada principalmente en movilidad/velocidad.
- Angel se identifica como la raza inspirada en Angel de Blox Fruits, evitando interpretarla como un ángel genérico del lore.
- Subhuman agrupa de forma clara Adamantium Human, Sorcerer, Evil Morty y Rick Sanchez sin copiar capacidades entre variantes.
- Se mejoran fichas de Human, Shark, Saiyan, Cyborg, Ghoul, progresión V1→V4, Trials, Executores, meditación, stamina, Room, Gate, dojos, revive, Assembling y dimensiones.
- Third Justice conserva como último requisito concreto conocido los 20 discos de Nightdream, dejando claro que un Trial puede cambiar con balance futuro.

## Revive y objetos actuales

- RCP deja de presentarse como método general de revive.
- Desfibrilador: herramienta normal de reanimación.
- Receta del desfibrilador: `3 bloques de hierro + 1 bloque de oro`.
- Medkit: objeto médico/curación, separado del desfibrilador.
- Receta del Medkit: `3 bloques de hierro + 1 mesa de encantamientos`.
- Respawn Cards permanecen como una vía aparte; activación con lingote de cobre y variantes Silver/Diamond descritas como revive gratis.
- Daemonium Kit se explica como herramienta para recuperar material de reliquias y no se mezcla con Geography Table.

## Fondos DVN reales

- Se añaden dos imágenes reales de Dummies vs Noobs desde la página oficial de Roblox:
  - `DVN · Arctic Standoff`
  - `DVN · Asalto costero`
- Las dos entran en la rotación normal y en la galería.
- Se conservan a `768×432`, su resolución nativa, en vez de aplicar un upscale falso a Full HD.
- El renderer y las pruebas aceptan distintas resoluciones de origen siempre que sigan siendo 16:9 y tengan tamaño suficiente.
- Tempest Jutcherson sigue totalmente fuera de la rotación normal.

## Música DVN

- Nueva pista integrada: `Arc - Enemy · Potoe`.
- La pista se obtiene desde la página individual del autor en SoundCloud, donde aparece marcada como CC BY-NC-SA.
- El JAR incluye un archivo de atribución.
- El build la convierte a Ogg Vorbis estéreo 44.1 kHz con -3 dB de headroom y verifica que decodifique correctamente.
- Se mantiene el shuffle sin repetición y los fades actuales.
- Otras pistas DVN/Boss OST siguen como referencias mientras su pista individual no tenga una fuente adecuada para incluirla.

## Build y pruebas

- Versión: `5.10.0`.
- CI descarga y valida los dos fondos DVN y la nueva pista antes de compilar.
- Nueva regresión `test_release_510.py` para bloquear lenguaje técnico/personal, recetas actuales, catálogo de razas y medios DVN.
- `AtlasRegressionTest` comprueba que las fichas públicas no expongan metadatos de fuentes.
- `MediaReferenceRegressionTest` distingue entre los dos fondos realmente instalados y las ideas que siguen siendo sólo referencias.
