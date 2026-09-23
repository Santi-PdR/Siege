# SIEGE 5.60 — Music candidates (approval required)

SIEGE no vuelve a generar música sintética propia para rellenar la playlist. Las pistas nuevas deben escucharse primero y sólo entran al mod después de aprobación explícita.

## Regla de integración

- `Stronghold 5-5 · Black Signal`, `Nucleus · Silent Carrier` y `Tesla Breach` fueron retiradas de la playlist activa y del pipeline de generación.
- Ningún candidato de esta lista está instalado todavía.
- Las pistas comerciales de juegos que inspiran SIEGE sirven para comparar dirección musical, pero no se empaquetan en el JAR salvo que exista permiso/licencia de redistribución.
- Para pistas realmente distribuibles se priorizan fuentes que indiquen una licencia clara y compatible con un mod público.

## Candidatos distribuibles para escuchar

Todos los siguientes temas de Scott Buckley están publicados por el compositor bajo **CC BY 4.0**: pueden usarse en proyectos, incluidos comerciales, con la atribución indicada por el autor.

### Stronghold / frente militar

1. **Legionnaire (2022 Remaster)** — https://www.scottbuckley.com.au/library/legionnaire-2022/
   - Orquestal militar, metales grandes y sensación de marcha/defensa.
   - Candidato principal para Stronghold 5-5.

2. **Catalyst** — https://www.scottbuckley.com.au/library/catalyst/
   - Synths oscuros + orquesta; empieza contenido y crece a un tema épico.
   - Alternativa menos marcial y más tecnológica.

### El Núcleo / tecnología

1. **Red** — https://www.scottbuckley.com.au/library/red/
   - Híbrido oscuro de electrónica y orquesta inspirado en futuros tipo Deus Ex/Cyberpunk.
   - Candidato principal para El Núcleo.

2. **Monomyth – The Encounter** — https://www.scottbuckley.com.au/library/the-encounter/
   - Synth y cuerdas amenazantes que construyen tensión hasta un drop dramático.
   - Alternativa más inquietante para eventos/reactividad del Núcleo.

### Tesla / Nusia / boss

1. **Simulacra** — https://www.scottbuckley.com.au/library/simulacra/
   - Acción híbrida de alta energía: synth arpegiado, guitarra, cuerdas y metales caóticos.
   - Candidato principal para amenazas Tesla y bosses.

2. **Venom** — https://www.scottbuckley.com.au/library/venom/
   - Trailer oscuro y agresivo con guitarras pesadas, synths sucios y batería.
   - Alternativa más industrial y hostil.

## Referencia directa de una inspiración de SIEGE

- **Helldivers 2 — A Cup of Liber-Tea**, compuesta por Wilbert Roget II, sirve como referencia clara para la escala militar/heroica de despliegue. La publicación oficial de Sony Soundtracks está disponible para escuchar, pero se trata como **referencia comercial**, no como asset redistribuible del mod.

## Próximo paso

Esperar la elección del jugador. Tras aprobar una pista se descarga desde su fuente/licencia original, se convierte a Ogg Vorbis 44.1 kHz estéreo con headroom, se acredita en el mod y recién entonces entra a `SiegeMusic`, `sounds.json` y CI.
