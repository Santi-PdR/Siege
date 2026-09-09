# Eternal Craft - SIEGE

Forge 1.20.1 client menu for Eternal Craft: SIEGE.

## SIEGE 0.6.1

### Intel dossiers

- Intel includes numbered `ALL`, `UNITS`, `ADVANCED`, `TANKS`, `BOSSES`, `ELITES` and `SUPER-UNITS` categories with live dossier counts.
- Arrow controls, mouse wheel and keyboard arrows move through troops in the active category. Number keys `0-6` switch categories directly.
- Wide Intel layouts now reserve solid header bands for `CATEGORIES` and `FILES`, so decorative cyan/grey telemetry rules never cross the labels.
- The dossier renderer remains responsive at GUI scales 1-4. Body text begins after both the portrait and wrapped metadata block, preventing text/image overlap.
- Wide layouts keep a permanent file navigator. Compact layouts use a reduced category strip and dedicated troop navigation controls.
- All sixteen photographs come from the owner's named ZIP folders. No troop is redrawn, substituted or generated: only the dossier treatment is composited over the supplied Minecraft pixels.
- Common-unit photographs use the physical aged-paper dossier with distressed military/typewriter ink and a worn red `CLASSIFIED` treatment. Advanced units retain their separate colder technical treatment.
- Flags appear only when the supplied record confirms the unit's origin. Patriot remains explicitly incomplete instead of inventing missing capabilities.
- The main menu has a responsive mini Intel feed containing only Units and Advanced troops. It automatically disappears when the logical screen is too small.

### SIEGE Settings

- Settings are now divided into dedicated `Overview`, `Music`, `Interface`, `Accessibility` and `Graphics` sections instead of presenting every option at once.
- Wide screens use a tactical section rail. Compact/high-GUI-scale layouts replace it with a responsive section grid while keeping the same controls.
- Overview explains what each section controls and shows a reduced client-status summary.
- Music contains the soundtrack toggle, custom live 0-100 volume slider, current playback state and `Next Track` control.
- Interface contains UI sounds and rotating-background controls. Accessibility contains reduced motion. Graphics owns the menu render profile.
- All section controls continue using SIEGE-owned widgets rather than vanilla button textures/sounds.

### Menu audio

- SIEGE menu music uses a dedicated tickable stream on Minecraft's master mix, so it is independent of the vanilla ambient Music slider while still respecting the user's Master volume.
- Fade-in is allowed to start silently without Minecraft discarding the streamed sound instance.
- Dragging the SIEGE volume slider changes the active stream live and never restarts the track.
- Tracks are allowed to finish before automatic advance. Stream startup has a grace period so asynchronous OGG loading cannot be mistaken for a finished song.
- Manual `Next Track` performs a fade-out, then the next shuffled track fades in. Natural transitions fade the incoming track.
- The shuffled queue completes a full cycle before repeating and avoids an immediate repeat at cycle boundaries.
- Menu music remains global across pre-game/menu screens but stops immediately whenever a world/server is loaded, including pause/gameplay screens.

The full category palette, verified unit-origin rules and UI/audio constraints are documented in `docs/RECONSTRUCTION.md`.

## Build and deploy

GitHub Actions is the only build environment. Windows only requires Git: `scripts/install-latest.ps1` clones the current `main` and copies the GitHub-validated JAR exclusively to SKLauncher instance `test-1`; it never invokes local Java or Gradle.
