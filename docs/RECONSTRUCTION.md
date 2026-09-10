# Reconstruction reference

This repository is a clean-room rebuild from screenshots supplied by the project owner.

## Confirmed behavior

- Forge 1.20.1 client-only menu replacement.
- Cinematic rotating backgrounds with long crossfades, restrained camera drift, scanlines and contrast overlays.
- Main commands remain in a compact neutral left-side column so the active background stays visible. The main screen has no cyan panel divider and no `MENU COMMAND LINK` footer.
- The custom wordmark and GUI-scale-3 command plates stay deliberately smaller than the dossier area; the lower-right dossier remains untouched by main-screen chrome changes.
- Music identification is event-driven: `REC` and the track name appear together briefly whenever a new track begins, rather than remaining as a faint static label.
- Intel opens a responsive intelligence database with numbered categories: `0 ALL`, `1 UNITS`, `2 ADVANCED`, `3 TANKS`, `4 BOSSES`, `5 ELITES`, `6 SUPER-UNITS`.
- Every category displays its live record count. The active category owns the current troop sequence.
- Troops can be changed with dedicated up/down controls, mouse wheel and keyboard arrows. Number keys `0-6` switch categories directly.
- Wide Intel layouts preserve a left category/file navigator. Compact layouts collapse categories and use dedicated troop navigation controls.
- The `CATEGORIES` heading must remain visually detached from decorative telemetry rules. `FILES` may retain its darker divider because that treatment remains legible.
- GUI scales 1-4 must remain usable. Scale 4 has a dedicated compact Intel treatment with short category controls, condensed metadata and more vertical room for dossier text.
- Dossier body text starts after the greater of portrait height or wrapped metadata height. This is a hard layout rule to prevent overlap when origin/status/armament text becomes taller than the image.
- Operational profiles use the complete supplied source material and an independently scrollable tactical reading area.
- The ten standard records are Units: Infantry, Shielder, Saboteur, Stalker, Natzuka, Sniper, Grenadier, Gunner, Jetpacker and Patriot.
- The six Advanced records are Specialist, Demoman, Artiller, Cloaker, APU and Missiler. All originate from the Republic of Nusia.
- The five Tank records are Zapper, Combatant, Agreement, Jagant and Strider. Zapper and Combatant are confirmed Nusia units; Agreement is linked to Secure Contain Protect; Jagant and Strider retain unknown origins.
- Zapper, Combatant and Agreement report 3,000 HP / 100 DEF. Jagant and Strider report 2,500 HP / 100 DEF. Missing threat, armament or role data is displayed as unknown instead of being invented.
- The nine Boss records are Tempest, Fusilier, Achilles, Trident, Prometheus, Daedalus, Hermes, Lelantos and Gaia. Known HP values are preserved exactly; Daedalus remains `N/D` because no HP value was supplied.
- Tempest, Fusilier, Achilles, Trident, Prometheus and Daedalus use the supplied operational descriptions and warnings. Hermes, Lelantos and Gaia contain only their supplied HP and recovered video status; appearance is never used to invent abilities.
- Demoman intentionally reports `NAN HP`. APU reports 120,000 HP and 0.75 defence, adjusted by difficulty.
- Confirmed Nusia records are Sniper, Grenadier, Gunner, Patriot, Specialist, Demoman, Artiller, Cloaker, APU and Missiler. No flag or country is assigned to Infantry, Shielder, Saboteur, Stalker, Natzuka or Jetpacker because their supplied records do not confirm an origin.
- The authentic Nusia flag uses vertical yellow, blue and green fields with four yellow stars on the central blue field. Substitute flags and real-world national flags are forbidden.
- Nusia affiliation graphics must look physically printed/worn into common dossiers or technically integrated into advanced dossiers, never like a clean pasted badge. Unknown-origin records use distressed censor ink rather than a generic digital `REDACTED` overlay.
- Dossier accents are category-owned rather than threat-owned: All cyan, Unit red, Advanced blue, Tank amber, Boss crimson, Elite violet and Super-Unit gold.
- Intel records and tactical text are available in Spanish and English, including their configured Minecraft locale variants.
- Supplied unit captures remain in Minecraft style, are normalized to 640x360 and receive their category-specific dossier treatment. Strider retains its recovered non-Minecraft render because that is the only supplied visual record.
- Patriot is a confirmed Nusia unit. Its file does not invent missing abilities or deployment details.
- Patriot is the deliberate exception to the supplied-pixel rule: the incorrect Minecraft-style scene is replaced by an owner-requested, paper-rooted sketch that preserves the supplied Roblox silhouette, helmet, rifle and Nusia flag. It must look drawn into the physical file rather than pasted onto a game background.
- Jetpacker and Stalker use their corrected owner-supplied Minecraft captures. Troop models retain Minecraft proportions and equipment.
- The named folders in the owner's definitive ZIP remain the portrait source for common and advanced records. `assets-source/intel-raw/tanks` preserves the five owner-supplied Tank captures, while `assets-source/intel-raw/bosses` preserves six representative frames per owner-supplied Boss video.
- Full MP4 files are not packaged into Minecraft. CI deterministically turns the 54 source frames into compact 640x360 dossier PNGs, giving every boss—including Hermes—the same classified motion-record presentation.
- Boss motion frames never display `REC`, `STILL` or a changing frame counter. CI scans the restored Intel V3 source and the image generator so this cannot silently regress.
- The main menu shows reduced Intel summaries containing only Unit and Advanced records when logical space permits. GUI scales 1-2 may show two rotating records simultaneously, scale 3 uses one rotating card, and scale 4 hides the preview to protect readability.
- Main-menu Intel cards use longer curated summaries and prefer two complete sentences. When the line budget is smaller, the renderer removes complete trailing sentences; it never slices a description in the middle of a phrase, and `INTEL: EXPEDIENTE COMPLETO` remains complete.
- Singleplayer has no visible button. Staff can open the vanilla world-selection screen with the undocumented Ctrl+S chord.

## Settings architecture

- SIEGE Settings is a section-based control center rather than one page containing every option.
- Required sections are Overview, Music, Interface, Accessibility and Graphics.
- Overview is informational and explains the responsibility of the other sections while showing a reduced client-status summary.
- Wide layouts use a SIEGE tactical navigation rail. Compact/high-GUI-scale layouts use a responsive section grid.
- Music contains menu-music enable/disable, live volume, current playback state and manual track advance.
- Interface contains UI feedback sounds, rotating-background behavior and an independent Boss Intel animation switch.
- Accessibility contains reduced motion, which also freezes Boss motion records on their first frame.
- Graphics owns the menu visual profile.
- Section navigation and controls use SIEGE-owned buttons/sliders rather than vanilla button textures or vanilla click audio.

## Menu audio

- SIEGE owns music only while no world/server is loaded and a menu screen exists. It stops immediately in gameplay and therefore never continues into pause, inventory or other world screens.
- SIEGE soundtrack playback is independent of Minecraft's ambient Music slider. The custom stream uses the master mix, so it still respects the user's global Master volume.
- The track implementation supports starting at zero gain during fade-in without being discarded by Minecraft's sound engine.
- The SIEGE settings screen uses a custom 0-100 music slider. Moving it changes the active stream live and never restarts the track; 0% is a live mute rather than a restart/stop operation.
- Automatic advance is based on CI-generated per-track duration metadata, not transient `SoundManager.isActive` state.
- Natural fade-out begins 8 seconds before the verified encoded end. Manual `Next Track` uses its own shorter transition.
- The shuffled queue completes a full cycle before refilling and avoids an immediate repeat at cycle boundaries.
- Vanilla menu music is periodically suppressed while SIEGE owns menu audio so two soundtracks cannot overlap.
- GitHub Actions converts owner soundtrack sources to Ogg Vorbis and measures their actual encoded duration with `ffprobe` before compiling the JAR.
- CI rejects the earlier damaged 22-25 second soundtrack blobs. The current authoritative owner-supplied masters are converted and measured during the build, so truncated audio cannot silently ship again.

## Background presentation

- Animated scenes hold for 24 seconds and use a 4.8-second smooth quintic crossfade.
- Cinematic and Balanced profiles use safe overscan with slow deterministic pan; a transition must never expose a black edge or abruptly reverse camera direction.
- Reduced Motion retains the crossfade while removing camera travel.

## Builds

- GitHub Actions is the only compilation environment. It uses Temurin Java 17 and Gradle 8.8, uploads the artifact and publishes the validated runtime JAR under `dist/`.
- CI restores the generated Intel V3 source, applies dossier overlays, generates and validates all Boss motion-record frames, prepares/validates soundtrack assets and only then compiles Forge.
- Windows scripts never invoke local Java or Gradle. They install only the GitHub-built JAR in SKLauncher instance `test-1`.
- Packaged menu music must be valid Ogg Vorbis; Opus-in-Ogg is not used directly by Minecraft 1.20.1.

## Design rule

Gameplay screens are not replaced. The reconstruction is scoped to menus and its dedicated Intel interface.


## Current development and test environment

- Operating system: Fedora Linux with KDE Plasma.
- Terminal: Konsole.
- Launcher: SKLauncher.
- Active test instance: `test-1`.
- Mod directory: `/home/Santipdr/.sklauncher/instances/test-1/mods`.
- Official local installer: `scripts/install-latest.sh`.
- Builds remain GitHub Actions-only; Fedora downloads and installs the validated JAR from `dist`.
- PowerShell documentation is legacy material for the former Windows installation.
