# Eternal Craft - SIEGE

Forge 1.20.1 client menu for Eternal Craft: SIEGE.

## SIEGE 0.6.4

### Intel dossiers

- Intel keeps numbered `ALL`, `UNITS`, `ADVANCED`, `TANKS`, `BOSSES`, `ELITES` and `SUPER-UNITS` categories with live dossier counts.
- Arrow controls, mouse wheel and keyboard arrows move through troops in the active category. Number keys `0-6` switch categories directly.
- The wide `CATEGORIES` header no longer shares a full-width rule with its label. `FILES` keeps its separate darker divider because that treatment remains readable.
- GUI scale 4 has a dedicated compact layout with reduced navigation chrome, condensed metadata and more height for dossier text.
- The main menu rotates reduced Unit, Advanced and Tank Intel cards at normal GUI scales and hides them automatically when the logical viewport is too small.
- Every main-menu Intel summary is a short complete sentence; previews no longer stop mid-phrase above the full-file prompt.
- Tank dossiers now include Zapper, Combatant, Agreement, Jagant and Strider with their supplied HP/DEF data and owner-provided renders.
- Patriot uses a visible hand-painted anonymous portrait instead of an invented Minecraft character; its unknown identity and incomplete record remain explicit.
- Intel now shows a proportional reading scrollbar, Page Up/Page Down support and real `0-6` category shortcuts. Metadata and body text no longer overlap at narrow sizes.
- Nusia affiliation blocks use worn printed/technical treatment; unknown origins use distressed censor ink instead of digital-looking `REDACTED` overlays.

### SIEGE Settings

- Settings remain divided into `Overview`, `Music`, `Interface`, `Accessibility` and `Graphics` sections.
- Music keeps the custom live 0-100 slider, current-track state and manual `Next Track` control.
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

GitHub Actions is the only build environment. Windows only requires Git: `scripts/install-latest.ps1` clones the current `main` and copies the GitHub-validated JAR exclusively to SKLauncher instance `test-1`; it never invokes local Java or Gradle.
