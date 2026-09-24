# Eternal Craft — SIEGE

Cliente Forge **1.20.1** para Eternal Craft: SIEGE. Reemplaza y amplía la experiencia de menús de Minecraft con una interfaz táctica propia, Intel, multimedia, fondos operacionales, navegación del servidor y herramientas de diagnóstico.

## SIEGE 5.61 — Scene Intelligence

La versión actual en desarrollo es **5.61.0**. Esta revisión continúa el trabajo de 5.60 fuera del soundtrack y se centra en que los fondos indiquen con precisión qué son, de dónde salen y qué escena viene después.

### Presentación y fondos

- 21 fondos normales auditados, incluyendo seis escenas oficiales de Dummies vs Noobs a su resolución nativa `768×432` y tres tratamientos SIEGE construidos sobre fuentes verificadas.
- Cada fondo tiene procedencia explícita: **DVN OFICIAL**, **TRATAMIENTO SIEGE** o **ARCHIVO SIEGE**.
- La Galería, la Sala Multimedia y el estado del cliente leen esa procedencia desde el mismo catálogo; no la deducen por el nombre del archivo.
- Rotación automática sin depender de un orden fijo repetitivo.
- La interfaz puede mostrar la escena actual, la próxima escena y el tiempo restante de la rotación.
- Duración de escena configurable entre **12 y 60 segundos**.
- Crossfade configurable entre **0 y 10 segundos**.
- Movimiento cinematográfico configurable entre **0 y 100%**, con overscan seguro y sin deformar la imagen.
- `Reduced Motion`, `Reduce Flashes` y `Performance` tienen prioridad y pueden anular el movimiento visual.
- La galería permite navegar, fijar una escena, reactivar la rotación, deshacer la última selección y usar vista limpia.
- El modo Contraste de la galería usa la misma oscuridad efectiva del menú, incluyendo sesgo por escena y Auto/High Contrast.

### Portada

- Título centrado `ETERNAL CRAFT / SIEGE` usando la tipografía de Minecraft.
- Interferencia opcional cuyo slider de intensidad controla realmente frecuencia, duración, desplazamiento y visibilidad del efecto.
- Columna de navegación compacta a la izquierda.
- Intel de portada limitado a Unidades y Avanzados cuando el espacio lo permite.
- `Ctrl+S` conserva el acceso oculto a Singleplayer; no se agregan atajos nuevos sin una necesidad explícita.
- El aviso `REC` aparece únicamente durante el aviso inicial de una pista y junto a su nombre.

### Intel y conocimiento

- Expedientes navegables para Unidades, Avanzados, Tanques, Bosses, Élites y Super Units.
- Búsqueda, lectura, visor de imágenes y navegación anterior/siguiente.
- Atlas, progresión, razas y guía operativa separados de los dossiers de combate.
- La capa de conocimiento actual cubre el frente de 2044, El Núcleo, Stronghold 5-5, facciones, clases de amenaza y la diferencia entre Gates, Rifts y Agreements.
- Los campos desconocidos permanecen explícitamente desconocidos: el cliente no rellena capacidades o estadísticas sin información confirmada.
- Los controles Intel retirados en revisiones anteriores no se recuperan accidentalmente mediante actualizaciones nuevas.

### Third Justice

Las capturas de Third Justice se restauran desde fuentes canónicas **full-color** y el reel completo se reconstruye durante el build. CI rechaza imágenes posterizadas/de pocos colores y protege el video completo de **313 frames / ~31.33 s**.

### Audio y Sala Multimedia

La playlist normal utiliza únicamente archivos que existen realmente dentro del build. No se crean entradas silenciosas para una canción ausente.

Pistas actualmente obligatorias del pipeline:

1. `Tale of a Cruel World`
2. `Darkest of Days`
3. `Kaptain Music Box`
4. `Heaven's Hell-Sent Gift`
5. `Arc - Enemy · Potoe`

Dos incorporaciones fueron aprobadas para 5.60:

- `A Stranger I Remain (Maniac Agenda Mix)`
- `Receive You The Hyperactive`

Son slots **opcionales**: sólo aparecen como pistas jugables cuando el build contiene un master preparado legítimamente. El repositorio y CI no descargan ni ripean esas bandas sonoras comerciales.

La Sala Multimedia ofrece anterior, reiniciar, siguiente, aleatorio sin repetir / pista fijada, progreso y reloj real de reproducción. Los presets Stronghold, Núcleo y Tesla cambian sólo la escena; no fuerzan música.

En 5.61 la parte visual de la Sala Multimedia también enseña la procedencia de la escena activa y la siguiente escena de la rotación. La pestaña de fondos ya no se etiqueta únicamente como DVN porque contiene material DVN y SIEGE.

Las pistas sintéticas experimentales `Black Signal`, `Silent Carrier` y `Tesla Breach` fueron retiradas y sus generadores ya no forman parte del proyecto.

### Centro de Comando y configuración

- Perfiles completos para distintos objetivos visuales y de rendimiento.
- Estado real de música, modo aleatorio/fijado y número de pistas disponibles.
- Estado de fondo, procedencia, escena actual, próxima escena, tiempos y movimiento efectivo.
- Diagnóstico y recuperación de preferencias del cliente.
- Configuración dividida en Apariencia, Movimiento, Audio, Intel, Accesibilidad, Fondos y Sistema.
- Los encabezados muestran la versión real instalada en vez de números escritos a mano.

## Principios de medios

SIEGE conserva algunas reglas de calidad que el build comprueba automáticamente:

- no inventar detalle mediante fake-HD;
- no introducir fondos normales desde fuentes no verificadas;
- distinguir explícitamente una captura oficial DVN de un tratamiento visual SIEGE;
- `Tempest Jutcherson` permanece aislado como easter egg y no entra en la rotación normal;
- Third Justice debe permanecer full-color y completo;
- ninguna pista nueva se trata como instalada si el archivo preparado no existe;
- la información de gameplay no se inventa para completar expedientes.

## Instalación — Fedora KDE / SKLauncher

Con Minecraft cerrado, desde Konsole:

```bash
bash <(gh api repos/Santi-PdR/Siege/contents/scripts/install-latest.sh --jq .content | base64 -d)
```

El instalador usa el JAR validado y publicado desde `main`, verifica la descarga y reemplaza versiones anteriores dentro de la instancia configurada. El entorno de uso actual es Forge 1.20.1 / Java 17.

## Build y validación

GitHub Actions es el entorno autoritativo de build. El workflow prepara los recursos, audita fondos, reconstruye medios derivados, valida audio Ogg Vorbis, ejecuta contratos de regresión y finalmente compila el mod Forge.

Un PR no se considera listo sólo porque el código compile: también deben pasar las protecciones de Intel, navegación, fondos, Third Justice, audio y recursos GUI.

## Documentación actual

- [Cambios de SIEGE 5.61](docs/CHANGELOG-5.61.0.md)
- [Cambios de SIEGE 5.60](docs/CHANGELOG-5.60.0.md)
- [Política/candidatos musicales de 5.60](docs/MUSIC-CANDIDATES-5.60.md)
- [Reconstrucción y restricciones históricas](docs/RECONSTRUCTION.md)

Los changelogs anteriores permanecen en `docs/` como historial. El README describe el comportamiento actual y no pretende repetir todas las funciones transitorias de versiones antiguas.
