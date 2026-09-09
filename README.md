# Eternal Craft - SIEGE

Forge 1.20.1 client menu for Eternal Craft: SIEGE.

## SIEGE 0.6.2

### Intel dossiers

- Intel keeps numbered `ALL`, `UNITS`, `ADVANCED`, `TANKS`, `BOSSES`, `ELITES` and `SUPER-UNITS` categories with live dossier counts.
- Arrow controls, mouse wheel and keyboard arrows move through troops in the active category. Number keys `0-6` switch categories directly.
- The wide `CATEGORIES` header no longer shares a full-width rule with its label. `FILES` keeps its separate darker divider because that treatment remains readable.
- GUI scale 4 now gets a dedicated compact layout: seven short category chips fit on one row whenever logical width allows, navigation consumes less vertical space, metadata is condensed and the dossier reading area receives more height.
- The main menu now exposes the reduced Intel feed at normal GUI scales instead of hiding it behind an overly large logical-width threshold. Scale 1-2 can show separate Unit and Advanced cards; scale 3 uses one alternating compact card; scale 4 intentionally hides the feed.
- All sixteen photographs still come from the owner's named ZIP folders. No troop model is replaced or redrawn.
- Nusia affiliation blocks were rebuilt across every dossier. Confirmed files use a worn printed flag/ledger treatment instead of a clean pasted badge; unknown origins use distressed censor ink rather than a generic digital `REDACTED` box.
- Common units retain the beige field dossier. Advanced units retain the colder technical/blue treatment.

### SIEGE Settings

- Settings remain divided into `Overview`, `Music`, `Interface`, `Accessibility` and `Graphics` sections.
- The Music section keeps the custom live 0-100 slider, current-track state and manual `Next Track` control.
- SIEGE controls use custom tactical widgets rather than vanilla button textures or vanilla click sounds.

### Full soundtrack timing

- The four original owner-supplied soundtrack masters are preserved under `assets-source/music-full/`.
- Those masters are Opus-in-Ogg, which Minecraft 1.20.1 cannot decode reliably. GitHub Actions converts the complete masters to Ogg Vorbis before every build; no time-cut filter is used.
- CI measures the encoded duration of every converted track with `ffprobe` and embeds those exact durations in `music_durations.properties`.
- Automatic transitions no longer use a transient `SoundManager.isActive(false)` as a signal to skip tracks. The encoded duration is authoritative.
- Natural fade-out begins exactly eight seconds before the real end of each track. The next shuffled track starts only when that final fade completes.
- Manual `Next Track` uses its own short fade. Volume changes remain live and never restart the stream, including a 0% live mute.
- Music remains menu-only and stops immediately when a world/server is loaded.

### Background presentation

- Background scenes hold longer and use a smoother 4.8-second quintic crossfade.
- Cinematic/Balanced modes use safe overscan with deterministic slow pan so transitions do not expose black edges or jump camera direction.
- Reduced Motion keeps the crossfade but removes camera travel.

The full category palette, origin rules and client constraints are documented in `docs/RECONSTRUCTION.md`.

## Build and deploy

GitHub Actions is the only build environment. Windows only requires Git: `scripts/install-latest.ps1` clones the current `main` and copies the GitHub-validated JAR exclusively to SKLauncher instance `test-1`; it never invokes local Java or Gradle.
