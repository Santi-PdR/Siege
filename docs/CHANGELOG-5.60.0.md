# SIEGE 5.60.0 — Adaptive Command

5.60 corrige la dirección de audio de 5.40/5.50 y mejora el comportamiento real del menú, los fondos y los diagnósticos.

## Música: sólo las dos aprobadas

Las tres pistas sintéticas creadas durante 5.40/5.50 fueron retiradas del sistema activo:

- `Stronghold 5-5 · Black Signal`
- `Nucleus · Silent Carrier`
- `Tesla Breach`

Ya no forman parte de `SiegeMusic`, no tienen generadores y el pipeline dejó de reconstruirlas.

Después de escuchar los candidatos, el jugador aprobó únicamente:

- **A Stranger I Remain (Maniac Agenda Mix)** — Metal Gear Rising: Revengeance.
- **Receive You The Hyperactive** — Yakuza: Like a Dragon.

Las demás propuestas fueron rechazadas para esta versión y no se integran.

Las dos aprobadas tienen slots reales de audio, nombres, eventos y preparación en 5.60, pero al ser música comercial el repositorio no la descarga desde servicios externos. Cuando se aporta un master local legítimo, `prepare-music.sh` lo valida, convierte a Ogg Vorbis 44.1 kHz estéreo, mide su duración y lo incluye automáticamente. Si el archivo no está, la canción no aparece en la playlist: no hay entradas mudas ni placeholders rotos.

## Playlist más robusta

`SiegeMusic` dejó de depender de varias listas paralelas que podían desincronizar nombre, evento, duración y archivo. 5.60 usa una definición única por pista y filtra los dos temas opcionales según los recursos realmente presentes en el JAR.

La cola aleatoria sigue sin repetir inmediatamente la pista anterior, el fade natural continúa empezando 8 segundos antes del final medido y las selecciones antiguas fuera del rango válido ya no pueden dejar el controlador en un índice imposible.

La Sala Multimedia ahora permite cambiar directamente entre **aleatorio sin repetir** y **pista fijada**, sin tener que ir hasta Configuración. El botón refleja el modo real en uso y puede fijar la pista que está sonando o devolver la reproducción a la rotación aleatoria.

## Fondos adaptativos

Los tres controles de fondo creados para 5.60 ahora afectan de verdad al renderer:

- duración de cada escena: 12–60 s;
- duración del crossfade: 0–10 s, limitada automáticamente a la mitad de la escena;
- intensidad de movimiento cinematográfico: 0–100%.

El renderer dejó de usar los antiguos 24 s / 4.8 s fijos. El movimiento usa un overscan pequeño y conserva la relación de aspecto, por lo que no aparecen barras ni estiramiento. Cada escena usa una dirección de desplazamiento determinista para que la rotación tenga variedad sin temblores aleatorios.

`Reduced Motion`, `Reduce Flashes` y el modo `Performance` siguen siendo límites estrictos: desactivan el desplazamiento cinematográfico. Un crossfade configurado en cero también es un modo real y seguro, sin división por cero.

### Controles visibles en Configuración

Los tres parámetros adaptativos dejaron de ser opciones internas: ahora aparecen en **Configuración → Fondos** con sliders propios para movimiento, duración de escena y fundido. La misma sección conserva oscuridad, scanlines, galería y reanudación de la rotación.

Los títulos de Configuración ya no enseñan el antiguo `SIEGE 1.25`; toman la versión real del build mediante `SiegeRuntimeStatus.version()`, evitando que la interfaz vuelva a quedarse atrasada cuando cambie la versión.

## Estado y diagnósticos

El centro de comando ahora muestra mejor el estado real del cliente:

- cantidad de pistas disponibles en el build actual;
- pista/volumen activos;
- duración configurada de escena;
- duración del fundido;
- intensidad de movimiento de fondos.

Esto permite distinguir un build de cinco pistas de uno donde ya están disponibles las dos incorporaciones aprobadas sin asumir que un asset existe cuando todavía no fue suministrado.

## Sala Multimedia

La cabecera ya no tiene un número de versión escrito a mano: muestra siempre la versión real del mod. La pestaña de música también aclara que las dos elecciones aprobadas sólo se incorporan a la playlist cuando existe un master preparado; no presenta una pista opcional como si ya viniera instalada.

El bloque de reproducción mantiene anterior, reiniciar y siguiente, y suma el control de modo aleatorio/fijado. Los presets Stronghold / Núcleo / Tesla siguen siendo **presets de escena solamente**: no fuerzan una canción ni cambian la música sin que el jugador lo pida.

## CI y control de calidad

`test_release_560.py` y el workflow fueron actualizados para:

- impedir que vuelvan las tres pistas generadas rechazadas;
- permitir únicamente los dos nuevos slots aprobados;
- validar que los masters opcionales no generen entradas silenciosas;
- comprobar que los controles adaptativos de fondos estén conectados al renderer **y visibles en Configuración**;
- proteger el control aleatorio/fijado y la versión dinámica de Sala Multimedia;
- eliminar referencias del CI a scripts de música que ya no existen;
- conservar los contratos de versiones anteriores sin obligar a mantener experimentos de audio descartados.

La corrección de Third Justice, la separación de Tempest Jutcherson, la resolución nativa de fondos DVN y los controles Intel retirados siguen siendo contratos de versiones anteriores y no deben retroceder.
