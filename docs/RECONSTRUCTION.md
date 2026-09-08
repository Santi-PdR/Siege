# Reconstruction reference

This repository is a clean-room rebuild from screenshots supplied by the project owner.

## Confirmed behavior

- Forge 1.20.1 client-only menu replacement.
- Cinematic rotating backgrounds with crossfades, restrained camera drift, scanlines and contrast overlays.
- Main commands: Deployment, Intel, Armory, Command Terminal and Quit.
- Intel opens an intelligence database with real category filters, file list, threat, armament, variants, status, description and tactical advisory.
- Intel categories are Units, Advanced, Tanks, Bosses, Elites and Super-Unit. Empty categories remain available for future records.
- The ten standard records are Units: Infantry, Shielder, Saboteur, Stalker, Natzuka, Sniper, Grenadier, Gunner, Jetpacker and Patriot.
- The six Advanced records are Specialist, Demoman, Artiller, Cloaker, APU and Missiler. All originate from the Republic of Nusia.
- Demoman intentionally reports `NAN HP`. APU reports 120,000 HP and 0.75 defence, adjusted by difficulty.
- Intel records and tactical text are available in Spanish and English, including their configured Minecraft locale variants.
- Desktop layouts expose clickable category filters and a scrollable file list. Compact layouts used by GUI scales 3-4 cycle categories from the top control and navigate only inside the active category.
- Supplied unit captures are preserved, resized to 640x360 and given restrained military grading; dossier framing is rendered non-destructively by the Intel interface.
- Patriot intentionally uses an incomplete classified record and provisional lore until the complete SIEGE canon is supplied.
- Singleplayer has no visible button. Staff can open the vanilla world-selection screen with the undocumented Ctrl+S chord.
- Menu music is randomized without immediate repeats and stops outside SIEGE menu screens.
- `DVN lobby music.ogg` contributes only Kaptain - Music Box (02:58-05:19).

## Builds

- GitHub Actions compiles every main-branch push using Temurin Java 17 and Gradle 8.8, then uploads the jar as a workflow artifact.
- `scripts/build-and-deploy.ps1` downloads the same Gradle version, builds locally and deploys only to SKLauncher instance `test-1`.

## Design rule

Gameplay screens are not replaced. The reconstruction is scoped to menus and its dedicated Intel interface.
