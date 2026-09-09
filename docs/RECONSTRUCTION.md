# Reconstruction reference

This repository is a clean-room rebuild from screenshots supplied by the project owner.

## Confirmed behavior

- Forge 1.20.1 client-only menu replacement.
- Cinematic rotating backgrounds with crossfades, restrained camera drift, scanlines and contrast overlays.
- Main commands are kept in a compact left-side column so the active background remains visible.
- Intel opens an intelligence database with categories and a category-owned file list permanently visible on the left. There is no `ALL` filter.
- Operational profiles use the complete supplied source material, larger presentation space and an independently scrollable tactical reading area.
- Intel categories are Units, Advanced, Tanks, Bosses, Elites and Super-Unit. Empty categories remain available for future records.
- The ten standard records are Units: Infantry, Shielder, Saboteur, Stalker, Natzuka, Sniper, Grenadier, Gunner, Jetpacker and Patriot. Their dossier portraits use an aged grayscale archive treatment with restrained red category accents.
- The six Advanced records are Specialist, Demoman, Artiller, Cloaker, APU and Missiler. All originate from the Republic of Nusia.
- Demoman intentionally reports `NAN HP`. APU reports 120,000 HP and 0.75 defence, adjusted by difficulty.
- Confirmed Nusia records are Sniper, Grenadier, Gunner, Patriot, Specialist, Demoman, Artiller, Cloaker, APU and Missiler. No flag or country is assigned to Infantry, Shielder, Saboteur, Stalker, Natzuka or Jetpacker because their supplied records do not confirm an origin.
- The authentic Nusia flag uses vertical yellow, blue and green fields with four yellow stars on the central blue field. Substitute flags and real-world national flags are forbidden.
- Dossier accents are category-owned rather than threat-owned: Unit red, Advanced blue, Tank amber, Boss crimson, Elite violet and Super-Unit gold.
- Intel records and tactical text are available in Spanish and English, including their configured Minecraft locale variants.
- GUI scales 1-4 retain the same left-side category and file navigation; the sidebar and record pane resize independently.
- Supplied unit captures remain in Minecraft style, are normalized to 640x360 and receive aged paper, photographic wear and a large category-colored `CLASSIFIED` stamp that overlaps only the lower part of the subject.
- Patriot is a confirmed Nusia unit. Its image uses the authentic supplied Nusia flag and its file does not invent missing abilities or affiliation.
- Every common-unit image uses the Sniper's beige aged-paper archive style, desaturated Minecraft photography, distressed ink embedded into the paper, secondary metadata blocks and a worn red `CLASSIFIED` stamp crossing the lower part of the troop. Clean digital overlay typography is not used. Advanced dossiers deliberately use a separate dark charcoal and blue treatment.
- Jetpacker and Stalker use their corrected owner-supplied Minecraft captures. Troop models retain Minecraft proportions and equipment.
- The named folders in the owner's definitive ZIP are the sole portrait source: `Unidades` supplies the ten common records and `Avanzados` supplies the six advanced records. The source pixels are never replaced with generated characters.
- Only records explicitly confirmed as Nusia display the supplied Nusia flag. Unconfirmed records display no flag and make no origin claim.
- Singleplayer has no visible button. Staff can open the vanilla world-selection screen with the undocumented Ctrl+S chord.
- Menu music is randomized without immediate repeats, loudness-normalized, stops outside SIEGE menu screens, and uses the UI/master mix at full track volume so it remains audible when Minecraft's ambient-music volume is disabled. Pressing `Next Track` re-enables a soundtrack disabled by an older saved configuration.

## Builds

- GitHub Actions is the only compilation environment. It uses Temurin Java 17 and Gradle 8.8, uploads the artifact and publishes the validated runtime JAR under `dist/`.
- Windows scripts never invoke local Java or Gradle. They install only the GitHub-built JAR in SKLauncher instance `test-1`.
- All menu music assets must be valid Ogg Vorbis streams; Opus-in-Ogg is not accepted because Minecraft 1.20.1 cannot decode it reliably.

## Design rule

Gameplay screens are not replaced. The reconstruction is scoped to menus and its dedicated Intel interface.
