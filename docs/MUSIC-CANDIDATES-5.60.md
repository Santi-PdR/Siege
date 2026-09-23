# SIEGE 5.60 — música aprobada

La selección de música nueva de 5.60 quedó cerrada por decisión del jugador.

## Aprobadas

1. **A Stranger I Remain (Maniac Agenda Mix)** — *Metal Gear Rising: Revengeance*
   - Uso previsto: boss, alerta roja, combate intenso o amenaza individual importante.
   - Duración de referencia: ~2:25.
   - Archivo de entrada esperado: `assets-source/music-full/a_stranger_i_remain.<ext>`.

2. **Receive You The Hyperactive** — *Yakuza: Like a Dragon*
   - Uso previsto: combate especial, boss, personaje importante o escena de alta energía.
   - Duración de referencia: ~4:48.
   - Archivo de entrada esperado: `assets-source/music-full/receive_you_the_hyperactive.<ext>`.

## Rechazadas para 5.60

No deben añadirse como música nueva en esta versión:

- A Cup of Liber-Tea
- The Automaton Legion
- Legionnaire
- Catalyst
- Red
- Monomyth – The Encounter
- Simulacra
- Venom

También siguen retiradas las tres pistas sintéticas que no gustaron:

- Stronghold 5-5 · Black Signal
- Nucleus · Silent Carrier
- Tesla Breach

## Integración

Las dos canciones aprobadas son música comercial existente. El repositorio **no descarga ni ripea audio desde YouTube, Spotify u otros servicios**. El código de 5.60 ya reconoce únicamente esos dos nuevos slots y `scripts/prepare-music.sh` los convierte automáticamente cuando el propietario aporta un master local legítimo con el nombre esperado.

Si el master no está presente, el build continúa correctamente y la canción no aparece en la lista del juego. Esto evita entradas mudas, assets rotos y descargas no autorizadas.

Los masters aceptados se convierten a Ogg Vorbis estéreo, 44.1 kHz, con headroom, validación de duración y comprobación completa de decodificación antes de entrar al JAR.
