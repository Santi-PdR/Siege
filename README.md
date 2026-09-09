# Eternal Craft - SIEGE

Forge 1.20.1 client menu for Eternal Craft: SIEGE.

## SIEGE 0.6.3

### Intel dossiers

- Intel keeps numbered `ALL`, `UNITS`, `ADVANCED`, `TANKS`, `BOSSES`, `ELITES` and `SUPER-UNITS` categories with live dossier counts.
- Arrow controls, mouse wheel and keyboard arrows move through troops in the active category. Number keys `0-6` switch categories directly.
- GUI scale 4 has its own compact layout with short category controls, condensed metadata and more height for the dossier reading area.
- The main menu exposes reduced Unit/Advanced Intel cards at normal GUI scales and hides them automatically when scale 4 needs the space.
- All sixteen photographs come from the owner's named ZIP folders. No troop model is replaced or redrawn.
- Confirmed Nusia files use worn printed/technical affiliation elements; unknown origins use distressed censor ink. Common and Advanced records keep distinct visual treatments.

### SIEGE Settings

- Settings remain divided into `Overview`, `Music`, `Interface`, `Accessibility` and `Graphics` sections.
- The Music section keeps the custom live 0-100 slider, current-track state and manual `Next Track` control.
- SIEGE controls use custom tactical widgets rather than vanilla button textures or vanilla click sounds.

### Clean full soundtrack masters

- 0.6.3 replaces the damaged historical soundtrack copies with the four clean OGG files re-uploaded by the owner on 2026-09-09.
- CI verifies the exact SHA-256 of every source before it is allowed into the build, then converts Opus-in-Ogg to Minecraft-safe Ogg Vorbis and measures the real encoded duration with `ffprobe`.
- `Tale of a Cruel World` is the complete ~4:21.54 source.
- `Darkest of Days` is the complete ~4:41.94 source. It is **not** the file that needs a middle section extracted: fingerprint comparison against the old 0.6.2 JAR proves its previous clip came from the beginning of the full master.
- `Heaven's Hell-Sent Gift` is the complete ~3:37.22 source.
- The uploaded `DVN lobby music` file is a complete ~8:59.54 three-song lobby compilation. Fingerprint comparison against the old SIEGE build proves the intended SIEGE track begins at exactly **178.500 s**, inside the second lobby-song section. CI therefore preserves that second song in full, from 178.500 s through 320.781792 s, stopping in the silent gap before song three. The legacy internal key `kaptain_music_box` is retained for compatibility.
- Natural fade-out begins eight seconds before the generated track duration. Manual `Next Track` uses a shorter fade, and 0-100 volume changes remain live without restarting playback.
- Music remains menu-only and stops immediately when a world/server is loaded.

### Background presentation

- Background scenes hold longer and use a 4.8-second quintic crossfade.
- Cinematic/Balanced modes use safe overscan with deterministic slow pan so transitions do not expose black edges or jump camera direction.
- Reduced Motion keeps the crossfade but removes camera travel.

The full category palette, origin rules and client constraints are documented in `docs/RECONSTRUCTION.md`.

## Build and deploy

GitHub Actions is the only build environment. Windows only requires Git: `scripts/install-latest.ps1` clones the current `main` and copies the GitHub-validated JAR exclusively to SKLauncher instance `test-1`; it never invokes local Java or Gradle.
