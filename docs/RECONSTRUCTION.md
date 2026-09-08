# Reconstruction reference

This repository is a clean-room rebuild from screenshots supplied by the project owner.

## Confirmed behavior

- Forge 1.20.1 client-only menu replacement.
- Cinematic rotating backgrounds with crossfades, restrained camera drift, scanlines and contrast overlays.
- Main commands: Deployment, Intel, Armory, Command Terminal and Quit.
- Intel opens an intelligence database with real category filters, file list, threat, armament, variants, status, description and tactical advisory.
- Intel categories are Units, Advanced, Tanks, Bosses, Elites and Super-Unit. Empty categories remain available for future records.
- The ten standard records are Units: Infantry, Shielder, Saboteur, Stalker, Natzuka, Sniper, Grenadier, Gunner, Jetpacker and Patriot. Their dossier portraits use an aged grayscale archive treatment with restrained red category accents.
- The six Advanced records are Specialist, Demoman, Artiller, Cloaker, APU and Missiler. All originate from the Republic of Nusia.
- Demoman intentionally reports `NAN HP`. APU reports 120,000 HP and 0.75 defence, adjusted by difficulty.
- Confirmed Nusia records are Sniper, Grenadier, Gunner, Specialist, Demoman, Artiller, Cloaker, APU and Missiler. No flag or country is assigned to Infantry, Shielder, Saboteur, Stalker, Natzuka, Jetpacker or Patriot because their supplied records do not confirm an origin.
- The authentic Nusia flag uses vertical yellow, blue and green fields with four yellow stars on the central blue field. Substitute flags and real-world national flags are forbidden.
- Dossier accents are category-owned rather than threat-owned: Unit red, Advanced blue, Tank amber, Boss crimson, Elite violet and Super-Unit gold.
- Intel records and tactical text are available in Spanish and English, including their configured Minecraft locale variants.
- Desktop layouts expose clickable category filters and a scrollable file list. Compact layouts used by GUI scales 3-4 cycle categories from the top control and navigate only inside the active category.
- Supplied unit captures remain in Minecraft style, are normalized to 640x360 and receive aged paper, photographic wear and a large category-colored `CLASSIFIED` stamp that overlaps only the lower part of the subject.
- Patriot intentionally uses an incomplete classified record and provisional lore until the complete SIEGE canon is supplied.
- Singleplayer has no visible button. Staff can open the vanilla world-selection screen with the undocumented Ctrl+S chord.
- Menu music is randomized without immediate repeats and stops outside SIEGE menu screens.
- `DVN lobby music.ogg` contributes only Kaptain - Music Box (02:58-05:19).

## Builds

- GitHub Actions is the only compilation environment. Every pull request and main-branch source push is compiled and verified with Temurin Java 17 and Gradle 8.8.
- After a successful main build, the workflow publishes the validated runtime JAR in `dist/` and uploads the normal workflow artifact.
- `scripts/build-and-deploy.ps1` performs deployment only: it copies the GitHub-built JAR to SKLauncher instance `test-1`. It never invokes Java or Gradle.
- `scripts/install-latest.ps1` is the standalone Windows entry point: it clones a clean copy of `main` and installs the validated JAR, replacing only previous `siege-menu-*.jar` files in `test-1`.
- The Java version installed on the player's computer does not affect deployment.

## Design rule

Gameplay screens are not replaced. The reconstruction is scoped to menus and its dedicated Intel interface.
