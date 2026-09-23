# SIEGE 5.40.0 — Stronghold / Black Signal

SIEGE 5.40 amplía 5.30 sin deshacer la corrección de Third Justice. La actualización se centra en que el menú represente mejor el servidor actual: más variedad visual, una pista propia de SIEGE, un briefing más útil y una capa de información actual que no borra el historial anterior.

## Fondos y galería

- La galería oficial de Dummies vs Noobs pasa de 3 a 6 escenas obtenidas desde la galería actual de Roblox.
- Las seis escenas oficiales se conservan a 768×432 nativos; no se inventa 1080p mediante upscale.
- El audit visual valida las 6 imágenes por formato 16:9, rango tonal, contraste, detalle y posterización.
- La rotación total del menú pasa de 15 a 18 escenas normales/especiales.
- Tempest Jutcherson continúa fuera de la rotación normal y de la galería.
- Se mantienen las reparaciones de sombras de Night Operation y Urban Rendezvous.
- Third Justice conserva las capturas RGB corregidas y el reel completo de ~31 s.

## Música

- Nueva pista instalada: **Stronghold 5-5 · Black Signal**.
- Es una pista original de SIEGE generada de forma determinista durante el build: drones industriales, maquinaria grave, impactos lejanos, estática de radio y un motivo de alarma moderado en la parte final.
- Dura aproximadamente 132 s, estéreo a 44.1 kHz y pasa por el mismo pipeline Vorbis y control de headroom que el resto del soundtrack.
- La playlist instalada pasa a 6 pistas sin eliminar las anteriores.
- El shuffle sigue evitando repetir inmediatamente la pista anterior.
- Se agregan los ambientes **STRONGHOLD** y **SEÑAL DEL NÚCLEO** alrededor de Black Signal en la Sala Multimedia.

## Información y briefing

- Nueva capa `SiegeKnowledgePlayer540`: los datos actuales pueden reemplazar textos viejos en la vista del jugador sin borrar la información histórica usada para mantenimiento.
- Se rehace el resumen general de SIEGE alrededor de 2044, Dominion of Pinzhao, Stronghold 5-5, Nusia y las demás fuerzas del frente.
- Nueva ficha dedicada a **El Núcleo**, explicándolo como IA reactiva y evitando presentarlo como un enemigo con comportamiento fijo.
- Nueva ficha de **Stronghold 5-5**.
- Nueva ficha de **Facciones del frente**.
- Nueva ficha **Qué hace distinto a SIEGE** para resumir vidas limitadas, raids, progresión, NPC, bosses, tecnología y eventos reactivos.
- Nueva guía **Cómo leer las amenazas**, con ejemplos de Tank, Boss, Élite y Super Unit y la regla de no inventar capacidades cuando Intel no tiene datos.
- Se aclara la terminología **Gates / Rifts / Agreements** sin convertir hipótesis de campo en datos oficiales.
- El Briefing ahora empieza por el contexto del frente, El Núcleo, Stronghold y facciones antes de llevar al jugador a razas, Trials, Executores, bosses y revive.

## Validación

- Nuevo release gate `test_release_540.py`.
- Los tests de 5.30 pasan a ser garantías duraderas y ya no bloquean una versión posterior sólo por agregar más contenido.
- CI valida que las seis escenas DVN existan y conserven su resolución real.
- CI genera y valida Black Signal, comprueba Ogg Vorbis, 44.1 kHz y headroom.
- CI compila la nueva capa 5.40 tanto en Atlas como en Operations.
- La publicación final continúa condicionada a un build verde de Forge y a que el JAR validado pueda publicarse sin superar los límites del repositorio.
