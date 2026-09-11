# Reconstruction reference

This repository is a clean-room rebuild from screenshots supplied by the project owner.

## Confirmed behavior

- Forge 1.20.1 client-only menu replacement.
- Cinematic rotating backgrounds with long crossfades, restrained camera drift, scanlines and contrast overlays.
- Main commands remain in a compact neutral left-side column so the active background stays visible. The main screen has no cyan panel divider and no `MENU COMMAND LINK` footer.
- The Minecraft-font title and GUI-scale-3 command plates stay deliberately smaller than the dossier area; the lower-right dossier remains untouched by main-screen chrome changes.
- Music identification is event-driven: `REC` and the track name appear together briefly whenever a new track begins, rather than remaining as a faint static label.
- Intel opens a responsive intelligence database with numbered categories: `0 ALL`, `1 UNITS`, `2 ADVANCED`, `3 TANKS`, `4 BOSSES`, `5 ELITES`, `6 SUPER-UNITS`.
- Every category displays its live record count. The active category owns the current troop sequence.
- Troops can be changed with dedicated left/right controls, mouse wheel and keyboard arrows. Number keys `0-6` switch categories directly.
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


## 0.7.6 usability and persistence

- Settings controls have a bounded scroll area; navigation stays fixed. Wheel, Page Up/Down and Tab/Shift+Tab reach every setting, including at 320x240 logical resolution.
- Information begins below measured controls, never at hard-coded overlapping offsets.
- Interface exposes independent effects volume, hover/focus audio and automatic preview rotation.
- Preview rotation resumes from the manually selected record; it never jumps back to a wall-clock index. Hidden preview hitboxes are cleared each render.
- Configuration writes stage a complete properties file before replacing the previous one. Reset requires an in-menu confirmation.
- Keep Fedora KDE/Konsole and the established test-1 path as the installation target. The installer retains previous JARs in a sibling backup folder.
- Runtime visual verification is not implied by successful compilation or layout arithmetic.

## 0.8.0 interaction and configuration expansion

- The main-menu Intel pause is a small state machine: hover enters reading, pointer exit creates one fixed two-second deadline, and expiry advances exactly once before returning to the normal 8.5-second cycle.
- The dossier header and progress edge communicate Reading, Fixed and automatic countdown states; each can be configured independently.
- Music gains previous/restart controls, configurable announcement duration and direct title-screen shortcuts.
- Intel adds persistent code-based Favorites, session selection memory, category cycling and precise document navigation.
- Gallery input now covers mouse buttons, wheel, direct number selection, random selection, pinning, rotation resume and clean view.
- Graphics exposes background and command-panel darkness separately from the Performance/Balanced/Cinematic profile.
- Interface exposes quit confirmation and build-label visibility without modifying vanilla gameplay screens.

## 0.8.1 visible controls and input cleanup

- Unrequested title-screen, Intel and gallery shortcut chords are removed instead of becoming hidden behavior users must memorize.
- Requested Left/Right dossier navigation and the established hidden staff Ctrl+S world-selection entry remain unchanged.
- Gallery operations are represented by visible Previous, Next, Pin, Auto Rotation, Clean View and Back buttons.
- Intel Favorites use a visible Save/Saved control in compact and wide layouts; wheel scrolling remains the normal pointer interaction.
- Main-menu hover pause is transition-based rather than frame-based: pointer exit creates one two-second deadline and automatic rotation advances once when it expires.
- Settings revision 801 re-enables Intel rotation once to clear the persistent stopped state formerly created by the removed `P` shortcut; subsequent visible-setting changes remain persistent.
- CI rejects the removed shortcut keys and validates the visible controls plus the hover state transition.

## 0.7.9 direct Intel handoff

- A main-menu preview is now an actionable dossier: its body opens the matching full Intel record.
- The handoff passes the record category and stable dossier code, so duplicate display names cannot open the wrong file.
- Each card in the dual preview resolves independently, while `I` opens the primary visible record.
- Position counters make the Unit/Advanced feed legible without adding another panel.
- A 220 ms directional slide acknowledges manual and automatic changes. Reduced Motion and disabled tactical effects remove it.
- The generated Intel V3 source remains authoritative and its compressed build input must include the targeted-entry constructor.

## 0.7.8 Intel reading behavior and gallery navigation

- Hovering a main-menu dossier suspends automatic rotation and leaves a short reading grace period after the pointer exits.
- In the dual-card layout, both visible footers are real click targets and retain circular previous/next navigation.
- Footer hover feedback is limited to the selected half and does not alter the approved dossier composition.
- The background gallery accepts the wheel, Home/End and arrow keys even in clean view.
- Gallery scene labels are curated for Spanish and English instead of being generated from asset paths.

## 0.7.7 gallery and soundtrack selection

- selectedScene=-1 retains automatic background behavior; 0..8 pins one of the existing supplied scenes. Selecting a gallery preview alone never persists it.
- F1 on the main screen opens the gallery. In the gallery it toggles clean view; Escape always restores controls before returning. No gameplay screen is replaced.
- Gallery and menu backgrounds use centered cover cropping instead of stretching source art to the window ratio.
- selectedTrack=-1 uses the existing shuffled queue. 0..3 repeats a chosen track after its complete duration/fade. Next Track advances that pinned track; choosing shuffle retains current playback and resets the upcoming queue.
- Settings reset restores both selections to -1. Invalid saved values are bounded on load.
- Verify live audio transitions, gallery keyboard navigation, narrow-screen labels and resource-pack font sizing in Minecraft before claiming visual QA.
