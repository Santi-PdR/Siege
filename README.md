## SIEGE 0.9.1 — Inspección y navegación

- El zoom del visor conserva el punto bajo el cursor, salvo cuando los límites de la imagen requieren centrarlo.
- Minimap interactivo al ampliar en pantallas de al menos 500×300 píxeles lógicos: muestra el área visible y permite moverla con clic/arrastre.
- El arrastre debe comenzar dentro de la imagen o del minimapa; soltar fuera finaliza correctamente la interacción.
- Zoom entre 1× y 4×, sin sonidos repetidos al alcanzar un límite. El porcentaje tiene espacio propio y Ajustar centra el arte completo.
- La galería tiene botones para recorrer páginas de miniaturas sin cambiar la imagen seleccionada. La rueda sobre miniaturas pasa páginas; sobre la vista previa cambia la imagen.
- Los controles de página se desactivan en los extremos; el nombre del fondo tiene espacio reservado junto a ellos.
- Sonido de hover corregido: el widget bajo el mouse tiene prioridad sobre el foco del teclado.
- Flechas, +, − y otros botones pequeños conservan su símbolo centrado durante hover; los textos demasiado estrechos no dibujan puntos fuera del botón.
- CI prueba la cámara real del visor: punto de zoom, minimapa, límites, redimensionado y Ajustar, además de las pruebas de escala de 0.9.0.

Compilación y pruebas automáticas no equivalen a verificación visual dentro de Minecraft; esa comprobación continúa pendiente.

## SIEGE 0.9.0 — Galería visual y lectura Intel

La galería pasa a tener miniaturas seleccionables y una vista previa grande. Intel añade búsqueda, lectura completa y un visor de imágenes. Los controles son visibles; no se agregan atajos.

- **Galería:** miniaturas nativas, selección directa, paginación automática, marca del fondo fijado, transición de 260 ms y vista limpia que se cierra con clic o Escape. La selección no cambia el fondo guardado hasta pulsar Fijar.
- **Intel:** búsqueda literal por nombre, código, origen, armamento y texto; ignora tildes/mayúsculas y admite varias palabras. Botón para limpiar, estados vacíos y búsqueda compatible con Favoritos.
- **Lectura:** área de texto delimitada, metadatos dentro del scroll, progreso y barra arrastrable. El modo Lectura dedica el espacio al texto; Ampliar abre el arte original con zoom 1–4×, arrastre limitado y Ajustar.
- **Portada:** acceso visible a Fondos, título ajustado al ancho, interferencia breve, aviso musical separado de los botones y dossiers. La doble tarjeta requiere altura suficiente.
- **Configuración:** pestañas compactas con más espacio útil, controles de pista en una fila, barra arrastrable, scroll conservado al volver y valores correctos en segundos/porcentaje.
- **Correcciones:** Favoritos se actualiza al pasar de expediente; contador separado del botón; la rueda fuera del texto no cambia archivos por accidente; eliminadas ayudas antiguas de F/F1 y números de atajos.
- **Fuente Intel:** `src/main/java/uy/santipdr/siege/client/IntelScreenV3.java` es ahora la fuente canónica legible. CI ya no restaura una copia comprimida que pueda deshacer los cambios.

### Verificación 0.9.0

CI ejecuta `tests/UiRegressionTest.java` sobre la geometría usada por las pantallas: barrido de tamaños lógicos desde 320×240, resoluciones 720p/768p/1080p/1440p/ultrawide y escalas solicitadas 1–4, además de búsqueda con tildes, múltiples palabras y caracteres literales. Después ejecuta las protecciones de navegación/recursos y compila Forge.

Estas pruebas verifican geometría y lógica; no sustituyen una comprobación visual dentro de Minecraft con sus fuentes y paquetes de recursos. La lista de comprobación está en `docs/QA-0.9.0.md`.

Instalación actual: Fedora KDE, Konsole, SKLauncher `test-1`, `/home/Santipdr/.sklauncher/instances/test-1/mods`. Cerrar Minecraft y ejecutar:

```bash
gh api repos/Santi-PdR/Siege/contents/scripts/install-latest.sh --jq .content | base64 -d | bash
```

Las secciones siguientes conservan el historial de versiones; los comportamientos actuales se describen arriba.

## SIEGE 0.8.1

- Removed the unrequested title-screen music, gallery, Intel and rotation keyboard shortcuts.
- Removed the unrequested gallery keyboard/mouse chords and Intel category/favorite shortcuts.
- The only special keyboard inputs retained are requested dossier `Left/Right` navigation and the established hidden staff `Ctrl+S` Singleplayer access.
- Gallery actions now use visible Previous, Next, Pin, Auto Rotation, Clean View and Back controls.
- Intel Favorites now use a visible Save/Saved button in both compact and wide layouts.
- The main-menu dossier hover fix remains explicit: hovering pauses it, leaving starts one two-second grace period, and expiry advances exactly once.
- A one-time 0.8.1 settings migration clears the persisted stopped state that the removed `P` shortcut could leave behind; the visible setting works normally afterward.
- CI rejects reintroduction of the removed shortcuts and verifies the visible replacements.

## SIEGE 0.8.0

1. Intel hover pause uses an explicit reading state instead of extending the rotation deadline every frame.
2. Leaving a hovered dossier starts one fixed two-second grace period.
3. Automatic rotation is forced to advance after that grace period and cannot remain suspended.
4. Disabling main-menu Intel clears stale hover state.
5. Dossiers expose `READING`, `FIXED` or a live `AUTO Ns` status.
6. A subtle progress line shows time until the next automatic dossier.
7. Hover pausing has its own persistent setting.
8. Dossier progress can be hidden independently.
9. Dossier state text can be hidden independently.
10. `P` toggles dossier auto-rotation from the title screen.
11. Home/End select the first/last title-screen dossier.
12. Quit now has an optional confirmation screen.
13. Quit confirmation can be disabled in Interface settings.
14. The build label has its own visibility setting.
15. Background darkness is adjustable without changing the graphics profile.
16. Left-panel darkness is independently adjustable.
17. Previous Track is available in Music settings.
18. Restart Track restarts the current complete stream with the normal short fade.
19. New-track notice duration is adjustable from 3 to 15 seconds.
20. Notice fade/progress calculations follow the configured duration.
21. Shift+M plays the previous track from the title screen.
22. Ctrl+M toggles menu music without opening settings.
23. `R` restarts the current track.
24. `G` opens the background gallery.
25. Intel has a persistent Favorites category.
26. `F` adds/removes the current dossier from Favorites.
27. Middle-click also toggles the current Intel favorite.
28. Favorite dossiers display a star in the file list.
29. Favorite dossiers display a star on the classified stamp.
30. Favorite codes persist safely in `siege-client.properties`.
31. `Q/E` cycle Intel categories in either direction.
32. Shift+wheel cycles Intel categories.
33. Number key 7 opens Favorites directly.
34. Intel Home/End select the first/last dossier.
35. Ctrl+Home/Ctrl+End jump to the top/bottom of dossier text.
36. Backspace returns from Intel like the visible Back control.
37. Intel remembers the last category during the current game session.
38. Intel remembers the last selected dossier during the current game session.
39. Empty Favorites explains how to add a dossier instead of showing a generic empty message.
40. Detail wheel scrolling moves one line at a time for more precise reading.
41. Wide Intel layouts show a compact shortcut guide.
42. Gallery `P` pins the current background.
43. Gallery `R` resumes automatic background rotation.
44. Number keys 1-9 select gallery backgrounds directly.
45. Gallery `X` jumps to a different random scene.
46. Space toggles gallery clean view.
47. Left-click exits clean view, middle-click pins and right-click returns.
48. Gallery header distinguishes Pinned, Preview and Rotation Active states.

## SIEGE 0.7.9

- Clicking a Unit or Advanced dossier on the main menu opens that exact record in the full Intel database.
- Dual-card layouts resolve each card independently; clicking the second preview never opens the first.
- Hovering a dossier body reveals a restrained `OPEN DOSSIER` action without adding another permanent button.
- The `I` key opens the primary dossier currently shown on the title screen.
- Main-menu cards now expose their position within the Unit/Advanced feed, such as `UNIT 04/16`.
- Dossier changes use a short directional slide that follows previous/next input and is disabled by Reduced Motion.
- The packaged Intel V3 source accepts an exact requested category and code while preserving normal navigation afterward.
- CI validates the direct-open constructor and the main-menu interaction path before Forge compilation.

## SIEGE 0.7.8

- Main-menu Intel pauses while the cursor is over a dossier and waits two seconds after leaving before rotation resumes.
- Both dossier footers work in the two-card layout; the first card no longer shows a dead navigation control.
- Hovering either half of a dossier footer now gives restrained directional feedback without changing the approved card design.
- Background Gallery supports the mouse wheel plus Home/End navigation, including clean view.
- Background names are curated and localized in Spanish and English instead of exposing lowercase resource IDs.
- Main-menu Intel interaction geometry is covered by CI checks for single- and dual-card layouts.
- Validation: Forge compilation in GitHub Actions; in-game visual/audio review remains required.

## SIEGE 0.7.7

- New Background Gallery in Graphics settings; F1 opens it directly from the main menu.
- Browse all nine supplied backgrounds with buttons or arrow keys. Pinning persists the selected scene; browsing alone changes no preference.
- Gallery F1 hides controls for a clean view. Escape restores controls first, then returns to the parent screen. Hidden buttons cannot be activated.
- Resume Background Rotation returns to the existing automatic sequence.
- Background rendering preserves 16:9 proportions with centered cover cropping, including narrow and ultrawide windows.
- Music settings can select any of the four complete tracks for persistent repeat, or resume shuffle without restarting the active track.
- Next Track in repeat mode advances the pinned selection. Changes preserve the short audio fade.
- A playback progress bar and repeat/shuffle status appear below music controls.
- New labels follow the existing Spanish/English locale selection.
- Validation: Forge compilation in GitHub Actions; geometry checks for gallery controls and cover bounds. In-game rendering/audio still needs runtime review.

## SIEGE 0.7.6

- Settings scroll within their own panel, with a scrollbar, mouse wheel and Page Up/Page Down. Tab/Shift+Tab brings each control into view.
- Help and playback text are placed after the actual controls, fixing Overview/Graphics overlap and high-scale overflow.
- Reset opens a confirmation; cancelling preserves preferences. Long control labels have full-text tooltips.
- Independent UI effects volume and hover/focus sound switch; hover easing uses elapsed time instead of frame count.
- Main-menu Intel auto-rotation can be disabled. Manual navigation still works and automatic rotation resumes sequentially after the 15-second reading pause.
- Hidden preview cards no longer retain clickable regions after resizing or disabling previews.
- Settings use staged atomic writes where supported; malformed booleans fall back to defaults and save errors are logged.
- Music volume updates live but disk persistence happens on release/close instead of each drag event.
- Fedora installer verifies its staged copy before replacement and keeps previous JARs outside mods for recovery.
- Validation: GitHub Actions compiles Forge; GUI layout calculations cover 320x240 through 1920x1080. In-game visual/audio review remains required.

# Eternal Craft - SIEGE

Forge 1.20.1 client menu for Eternal Craft: SIEGE.

## SIEGE 0.7.5

- Left and Right now move backward and forward through dossiers in the packaged Intel V3 screen.
- Dossier navigation remains circular at category boundaries.
- The main-menu Unit/Advanced preview supports Left/Right, mouse wheel and clickable footer arrows.
- A manually selected preview remains stable for 15 seconds before automatic rotation resumes.
- The dossier footer now communicates its navigation directly without adding large controls.
- CI patches and validates the generated Intel V3 source actually packaged into the JAR.

## SIEGE 0.7.4

- Title interference can now be disabled independently without removing button effects.
- New-track `REC` announcements have their own persistent toggle.
- The settings overview can safely restore only SIEGE client preferences.
- Main-menu keyboard navigation starts on Deployment instead of having no initial focus.
- Reference-style command plates now provide a short physical press response.
- Settings layout and help text were updated for the new controls.

## SIEGE 0.7.3

- The generated wordmark was removed; the centered title now uses Minecraft's own font.
- The title recreates the old interference effect with short animated slices instead of a malformed permanent shadow.
- Main-menu command plates return to the older solid-grey, double-rimmed style with a compact lower/right shadow.
- GUI scale 3 command sizing is rebalanced and the left shade remains neutral rather than blue.

## SIEGE 0.7.2

- Removed the runtime `REC / STILL` frame badge from the Intel V3 screen that is actually packaged.
- Boss dossier artwork no longer prints frame numbers; CI now checks the restored V3 source and the artwork generator.
- Main-menu Intel rotation is restricted to Unit and Advanced records.
- The title, GUI-scale-3 command column and command plates are smaller and better balanced.
- The left panel is now neutral black/grey without the cyan divider or blue scanline cast.
- Removed `MENU COMMAND LINK`; the footer is a smaller `BUILD 0.7.2` label without `SECURE CHANNEL`.
- `REC` and the current track name now appear together for 8.5 seconds whenever a track actually starts.
- Fedora's installer prefers the authenticated GitHub CLI, avoiding private-repository clone failures.

## SIEGE 0.7.1

- The main-screen title is now a dedicated transparent wordmark derived from the supplied visual reference instead of enlarged vanilla lettering.
- The command buttons use the reference's medium-grey industrial plates, centered labels and red selection arrow.
- GUI scale 3 receives wider 286 px command plates with 31 px height and more vertical breathing room.
- The existing lower-right Intel dossier is intentionally unchanged.

## SIEGE 0.7.0

- New persistent Tactical Effects toggle in Interface settings.
- Hover motion now eases smoothly instead of shifting text abruptly.
- Buttons gain a progressive accent underline, restrained sweep and clearer keyboard focus brackets.
- Reduced Motion automatically disables animated sweeps while preserving clear hover/focus feedback.
- The centered title divider gains a subtle tactical pulse and balanced cyan end markers.

## SIEGE 0.6.9

- The title is narrower and closer to the supplied tactical pixel lettering.
- The oversized black spikes are replaced by a short clean extrusion, fine red edge and restrained face highlight.
- GUI scale 3 gets wider, taller command buttons with increased spacing.
- Buttons now have eased hover response, a clipped light sweep and tactical corner brackets.

## SIEGE 0.6.8

- The main title is now a large centered tactical wordmark.
- `ETERNAL CRAFT` uses a pale pixel face, red keyline and deep black extrusion matching the supplied reference.
- `S I E G E` sits beneath it with responsive sizing for GUI scales 1-4.
- The existing lower-right Intel dossier is preserved.

## SIEGE 0.6.7

- The per-frame boss `REC / STILL` counter is forbidden by the build and cannot be reintroduced accidentally.
- The dossier keeps its six-frame animation without drawing frame numbers over the image.

## SIEGE 0.6.6

- Boss images no longer show the intrusive runtime `REC / STILL` badge.
- Main-menu Intel cards can now be hidden independently.
- Scanlines can now be disabled independently from the graphics profile.
- The new client preferences persist in `config/siege-client.properties`.

## SIEGE 0.6.5

### Intel dossiers

- Intel keeps numbered `ALL`, `UNITS`, `ADVANCED`, `TANKS`, `BOSSES`, `ELITES` and `SUPER-UNITS` categories with live dossier counts.
- Arrow controls, mouse wheel and keyboard arrows move through troops in the active category. Number keys `0-6` switch categories directly.
- The wide `CATEGORIES` header no longer shares a full-width rule with its label. `FILES` keeps its separate darker divider because that treatment remains readable.
- GUI scale 4 has a dedicated compact layout with reduced navigation chrome, condensed metadata and more height for dossier text.
- The main menu rotates reduced Unit and Advanced Intel cards at normal GUI scales and hides them automatically when the logical viewport is too small.
- Main-menu Intel cards are taller and use curated two-sentence summaries where space permits. Narrow layouts remove whole sentences instead of cutting a phrase in half.
- Tank dossiers now include Zapper, Combatant, Agreement, Jagant and Strider with their supplied HP/DEF data and owner-provided renders.
- Boss dossiers now include Tempest, Fusilier, Achilles, Trident, Prometheus, Daedalus, Hermes, Lelantos and Gaia. Each uses the same six-frame classified-video treatment derived from its owner-supplied footage.
- Hermes receives exactly the same dossier-video treatment as every other boss. Hermes, Lelantos and Gaia keep their unknown abilities explicit rather than inventing information from their appearance.
- Patriot uses a visible paper-rooted sketch preserving the silhouette of its supplied Roblox form, helmet, rifle and Nusia flag; the unrelated Minecraft background and stand-in presentation are gone.
- Intel now shows a proportional reading scrollbar, Page Up/Page Down support and real `0-6` category shortcuts. Metadata and body text no longer overlap at narrow sizes.
- Nusia affiliation blocks use worn printed/technical treatment; unknown origins use distressed censor ink instead of digital-looking `REDACTED` overlays.
- Boss footage advances as a lightweight archival contact sequence and can be disabled independently. Reduced Motion also freezes every record on its first frame.

### SIEGE Settings

- Settings remain divided into `Overview`, `Music`, `Interface`, `Accessibility` and `Graphics` sections.
- Music keeps the custom live 0-100 slider, current-track state and manual `Next Track` control.
- The main-screen track control is the compact `> MÚSICA` / `> MUSIC`, so its complete label fits. The `M` key triggers the same short manual fade.
- Interface settings now include an independent Intel-animation toggle alongside UI sounds and rotating backgrounds.
- SIEGE controls use custom tactical widgets rather than vanilla button textures or vanilla click sounds.

### Complete soundtrack masters

The authoritative soundtrack sources are the clean files supplied by the owner in chat on 2026-09-09. CI converts them to Minecraft-safe Ogg Vorbis and measures the encoded duration with `ffprobe` before compiling.

- `The Tale of a Cruel World`: complete source, approximately 261.54 s.
- `The Darkest of Days`: complete source, approximately 281.94 s.
- `Heaven's Hell-Sent Gift`: complete source, approximately 217.22 s.
- `DVN lobby music`: this 539.54 s compilation is **not** packaged whole. Only `Kaptain - Music Box` is used. Silence analysis of the supplied file places the Music Box section from 179.599646 s to 319.568250 s, approximately 139.97 s.

`Tale`, `Darkest` and `Heaven` are always encoded from first packet to final packet: the build script deliberately uses no seek/time-cut filter for them. `DVN` is the only source intentionally split.

Automatic transitions are duration-driven. `SoundManager.isActive(false)` is never used as the automatic-next signal. A natural fade-out starts exactly eight seconds before the measured encoded end, then the next shuffled track fades in. Manual `Next Track` uses its own shorter fade. Changing SIEGE volume never restarts the active stream.

CI rejects the old damaged 22-25 second repository blobs by enforcing minimum source durations before compilation, so a truncated soundtrack cannot silently ship again.

### Background presentation

- Background scenes hold longer and use a 4.8-second quintic crossfade.
- Cinematic/Balanced modes use safe overscan with deterministic slow pan so transitions do not expose black edges or jump camera direction.
- Reduced Motion keeps the crossfade but removes camera travel.

The full category palette, origin rules and client constraints are documented in `docs/RECONSTRUCTION.md`.

## Build and deploy

GitHub Actions is the only build environment.

### Current environment: Fedora KDE

The active workstation now uses Fedora with KDE Plasma and Konsole. Run `bash scripts/install-latest.sh` from Konsole. It clones the current `main` and installs only the GitHub-validated JAR into:

`/home/Santipdr/.sklauncher/instances/test-1/mods`

The Fedora installer never invokes local Java or Gradle. It verifies a staged copy, backs up previous `siege-menu-*.jar` files outside `mods`, then replaces the installed build.

### Legacy Windows environment

`scripts/install-latest.ps1` remains available only for the previous Windows setup.
