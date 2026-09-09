# Reconstruction reference

This repository is a clean-room rebuild from screenshots supplied by the project owner.

## Confirmed behavior

- Forge 1.20.1 client-only menu replacement.
- Cinematic rotating backgrounds with crossfades, restrained camera drift, scanlines and contrast overlays.
- Main commands remain in a compact left-side column so the active background stays visible.
- Intel opens a responsive intelligence database with numbered categories: `0 ALL`, `1 UNITS`, `2 ADVANCED`, `3 TANKS`, `4 BOSSES`, `5 ELITES`, `6 SUPER-UNITS`.
- Every category displays its live record count. The active category owns the current troop sequence.
- Troops can be changed with dedicated up/down controls, mouse wheel and keyboard arrows. Number keys `0-6` switch categories directly.
- Wide Intel layouts preserve a left category/file navigator. Compact layouts collapse the categories into two rows and use dedicated troop navigation controls.
- GUI scales 1-4 must remain usable. Layout is driven by logical width/height rather than assuming a fixed resolution. Main-menu Intel preview content may disappear automatically when the logical viewport is too small.
- Dossier body text starts after the greater of portrait height or wrapped metadata height. This is a hard layout rule to prevent the overlap seen when origin/status/armament text becomes taller than the image.
- Operational profiles use the complete supplied source material and an independently scrollable tactical reading area.
- The ten standard records are Units: Infantry, Shielder, Saboteur, Stalker, Natzuka, Sniper, Grenadier, Gunner, Jetpacker and Patriot.
- The six Advanced records are Specialist, Demoman, Artiller, Cloaker, APU and Missiler. All originate from the Republic of Nusia.
- Demoman intentionally reports `NAN HP`. APU reports 120,000 HP and 0.75 defence, adjusted by difficulty.
- Confirmed Nusia records are Sniper, Grenadier, Gunner, Patriot, Specialist, Demoman, Artiller, Cloaker, APU and Missiler. No flag or country is assigned to Infantry, Shielder, Saboteur, Stalker, Natzuka or Jetpacker because their supplied records do not confirm an origin.
- The authentic Nusia flag uses vertical yellow, blue and green fields with four yellow stars on the central blue field. Substitute flags and real-world national flags are forbidden.
- Dossier accents are category-owned rather than threat-owned: All cyan, Unit red, Advanced blue, Tank amber, Boss crimson, Elite violet and Super-Unit gold.
- Intel records and tactical text are available in Spanish and English, including their configured Minecraft locale variants.
- Supplied unit captures remain in Minecraft style, are normalized to 640x360 and receive their category-specific dossier treatment.
- Every common-unit image uses the Sniper's beige aged-paper archive style, desaturated Minecraft photography, distressed ink embedded into the paper, secondary metadata blocks and a worn red `CLASSIFIED` stamp. Advanced dossiers deliberately use a separate colder technical treatment.
- Patriot is a confirmed Nusia unit. Its file does not invent missing abilities or deployment details.
- Jetpacker and Stalker use their corrected owner-supplied Minecraft captures. Troop models retain Minecraft proportions and equipment.
- The named folders in the owner's definitive ZIP are the sole portrait source: `Unidades` supplies the ten common records and `Avanzados` supplies the six advanced records. The source pixels are never replaced with generated characters.
- Only records explicitly confirmed as Nusia display the supplied Nusia flag. Unconfirmed records display no flag and make no origin claim.
- The main menu may show a small rotating Intel summary containing only Unit and Advanced records. It must show reduced information and direct the player to Intel for the full file.
- Singleplayer has no visible button. Staff can open the vanilla world-selection screen with the undocumented Ctrl+S chord.

## Menu audio

- SIEGE owns music only while no world/server is loaded and a menu screen exists. It must stop immediately in gameplay and therefore never continue into pause, inventory or other world screens.
- The normal Minecraft music-category volume is restored whenever SIEGE releases control.
- The SIEGE settings screen uses a custom 0-100 music slider. Moving it changes the active stream live and must not restart the track.
- Automatic advance occurs only after the sound engine reports that the current streamed OGG has ended. A startup grace period prevents asynchronous stream initialization from being interpreted as completion.
- Manual `Next Track` fades the current music out and fades the next track in. Natural transitions fade the incoming track in.
- The shuffled queue completes a full cycle before refilling and avoids an immediate repeat at cycle boundaries.
- SIEGE buttons/sliders do not use vanilla button textures or vanilla click audio.

## Builds

- GitHub Actions is the only compilation environment. It uses Temurin Java 17 and Gradle 8.8, uploads the artifact and publishes the validated runtime JAR under `dist/`.
- Windows scripts never invoke local Java or Gradle. They install only the GitHub-built JAR in SKLauncher instance `test-1`.
- All menu music assets must be valid Ogg Vorbis streams; Opus-in-Ogg is not accepted because Minecraft 1.20.1 cannot decode it reliably.

## Design rule

Gameplay screens are not replaced. The reconstruction is scoped to menus and its dedicated Intel interface.
