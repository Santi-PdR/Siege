# Eternal Craft - SIEGE

Forge 1.20.1 client menu for Eternal Craft: SIEGE.

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

The Fedora installer never invokes local Java or Gradle. It removes older `siege-menu-*.jar` files before copying the validated build.

### Legacy Windows environment

`scripts/install-latest.ps1` remains available only for the previous Windows setup.
