# Eternal Craft - SIEGE

Forge 1.20.1 client menu for Eternal Craft: SIEGE.

## SIEGE 0.6.2

### Intel dossiers

- Intel keeps numbered `ALL`, `UNITS`, `ADVANCED`, `TANKS`, `BOSSES`, `ELITES` and `SUPER-UNITS` categories with live dossier counts.
- Arrow controls, mouse wheel and keyboard arrows move through troops in the active category. Number keys `0-6` switch categories directly.
- The wide `CATEGORIES` header no longer shares a full-width rule with its label. `FILES` keeps its separate darker divider because that treatment remains readable.
- GUI scale 4 now gets a dedicated compact layout: short category controls, reduced navigation chrome, condensed metadata and more height for the dossier reading area.
- The main menu exposes a reduced Intel feed at normal GUI scales. Scale 1-2 can show separate Unit and Advanced cards; scale 3 uses one alternating compact card; scale 4 intentionally hides the feed.
- All sixteen photographs still come from the owner's named ZIP folders. No troop model is replaced or redrawn.
- Nusia affiliation blocks are rebuilt across every dossier. Confirmed files use a worn printed flag/ledger treatment instead of a clean pasted badge; unknown origins use distressed censor ink rather than a generic digital `REDACTED` box.
- Common units retain the beige field dossier. Advanced units retain the colder technical/blue treatment.

### SIEGE Settings

- Settings remain divided into `Overview`, `Music`, `Interface`, `Accessibility` and `Graphics` sections.
- The Music section keeps the custom live 0-100 slider, current-track state and manual `Next Track` control.
- SIEGE controls use custom tactical widgets rather than vanilla button textures or vanilla click sounds.

### Soundtrack timing and source integrity

- Automatic transitions are duration-driven. A transient `SoundManager.isActive(false)` can no longer skip a track early.
- GitHub Actions converts the recovered owner soundtrack sources to Minecraft-safe Ogg Vorbis and measures the encoded duration with `ffprobe`; the exact durations are embedded in `music_durations.properties`.
- Natural fade-out begins exactly eight seconds before the encoded end. Manual `Next Track` uses its own shorter fade. Volume changes remain live and never restart the stream, including a 0% live mute.
- CI also audits the recovered Ogg pages before conversion. That audit proved the soundtrack binaries preserved in repository history are themselves truncated/corrupted after their valid audio pages. The currently recoverable audio lengths are `Tale of a Cruel World` 22.8s, `Darkest of Days` 25.0s, `Kaptain Music Box` 25.1s and `Heaven's Hell-Sent Gift` 24.0s.
- 0.6.2 therefore plays every recoverable encoded track through its verified end without the controller cutting it prematurely. Restoring the genuinely longer original songs requires clean full source masters from the owner; missing audio cannot be reconstructed from the damaged repository blobs.
- Music remains menu-only and stops immediately when a world/server is loaded.

### Background presentation

- Background scenes hold longer and use a smoother 4.8-second quintic crossfade.
- Cinematic/Balanced modes use safe overscan with deterministic slow pan so transitions do not expose black edges or jump camera direction.
- Reduced Motion keeps the crossfade but removes camera travel.

The full category palette, origin rules and client constraints are documented in `docs/RECONSTRUCTION.md`.

## Build and deploy

GitHub Actions is the only build environment. Windows only requires Git: `scripts/install-latest.ps1` clones the current `main` and copies the GitHub-validated JAR exclusively to SKLauncher instance `test-1`; it never invokes local Java or Gradle.
