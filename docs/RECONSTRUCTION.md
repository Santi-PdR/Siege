# Reconstruction reference

This repository is a clean-room rebuild from screenshots supplied by the project owner.

## Confirmed behavior

- Forge 1.20.1 client-only menu replacement.
- Cinematic rotating backgrounds with crossfades, restrained camera drift, scanlines and contrast overlays.
- Main commands: Deployment, Intel, Armory, Command Terminal and Quit.
- Intel opens an intelligence database with category filters, file list, threat, armament, variants, status, description and tactical advisory.
- Intel contains nine illustrated records reconstructed from the supplied field manual: Infantry, Shielder, Saboteur, Stalker, Natzuka, Sniper, Grenadier, Gunner and Jetpacker.
- Patriot remains excluded because the supplied manual contains only its heading, without enough canonical information for a record.
- Singleplayer has no visible button. Staff can open the vanilla world-selection screen with the undocumented Ctrl+S chord.
- Menu music is randomized without immediate repeats and stops outside SIEGE menu screens.
- `DVN lobby music.ogg` contributes only Kaptain - Music Box (02:58–05:19).

## Builds

- GitHub Actions compiles every main-branch push using Temurin Java 17 and Gradle 8.8, then uploads the jar as a workflow artifact.
- `scripts/build-and-deploy.ps1` downloads the same Gradle version, builds locally and deploys only to SKLauncher instance `test-1`.

## Design rule

Gameplay screens are not replaced. The reconstruction is scoped to menus and its dedicated Intel interface.
