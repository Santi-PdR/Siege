# Eternal Craft - SIEGE

Forge 1.20.1 client menu for Eternal Craft: SIEGE.

## SIEGE 0.6.0

### Intel dossiers

- Intel now includes a numbered `ALL` category plus numbered `UNITS`, `ADVANCED`, `TANKS`, `BOSSES`, `ELITES` and `SUPER-UNITS` categories. Each category shows its current dossier count.
- Arrow controls, mouse wheel and keyboard arrows move through the troops in the active category. Number keys `0-6` switch categories directly.
- The dossier renderer is responsive at GUI scales 1-4. The body begins after both the portrait and the metadata block, eliminating text/image overlap when metadata wraps.
- Wide layouts keep a permanent file navigator. Compact layouts use a reduced two-row category strip and dedicated previous/next troop controls.
- All sixteen photographs come from the owner's named ZIP folders. No troop is redrawn, substituted or generated: only the dossier treatment is composited over the supplied Minecraft pixels.
- Common-unit photographs use the physical aged-paper dossier with distressed military/typewriter ink and a worn red `CLASSIFIED` treatment. Advanced units retain their separate colder technical treatment.
- Flags appear only when the supplied record confirms the unit's origin. Patriot remains explicitly incomplete instead of inventing missing capabilities.
- The main menu has a responsive mini Intel feed containing only Units and Advanced troops. It automatically disappears when the logical screen is too small, protecting high GUI scales and small windows.

### Menu UI and audio

- The main title has stronger hierarchy and a larger `SIEGE` treatment without replacing Minecraft gameplay screens.
- SIEGE controls use custom tactical widgets rather than vanilla button textures/sounds.
- Music volume is controlled by a custom 0-100 slider. Dragging it changes the currently playing stream live; it does not restart the track.
- Tracks are allowed to finish before automatic advance. Stream startup has a grace period so asynchronous OGG loading cannot be mistaken for a finished song.
- Manual `Next Track` performs a fade-out, then the next shuffled track fades in. Natural transitions also fade the incoming track.
- The shuffled queue completes a full cycle before repeating and avoids an immediate repeat at cycle boundaries.
- Menu music remains global across pre-game/menu screens but stops immediately whenever a world/server is loaded, including pause/gameplay screens. The normal Minecraft music-category volume is restored when SIEGE releases control.

The full category palette and verified unit-origin rules are documented in `docs/RECONSTRUCTION.md`.

## Build and deploy

GitHub Actions is the only build environment. Windows only requires Git: `scripts/install-latest.ps1` clones the current `main` and copies the GitHub-validated JAR exclusively to SKLauncher instance `test-1`; it never invokes local Java or Gradle.
