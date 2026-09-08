# Eternal Craft - SIEGE

Forge 1.20.1 client menu for Eternal Craft: SIEGE.

## Intel dossiers

- Common units use aged grayscale records with red accents.
- Advanced units use aged grayscale records with blue accents.
- Nusia flags appear only on records whose supplied source explicitly confirms the Republic of Nusia.
- Every portrait is stored at 640x360 and preserves the source game's block-based visual style.

The full category palette and verified unit-origin rules are documented in `docs/RECONSTRUCTION.md`.

## Build and deploy

GitHub Actions is the only build environment. It compiles and verifies the mod with Temurin Java 17 and Gradle 8.8, then publishes the validated JAR in `dist/`.

Windows only requires Git. Run `scripts/install-latest.ps1` from PowerShell to clone the current `main` and copy the GitHub-built JAR exclusively to the SKLauncher instance `test-1`. The installer does not invoke local Java or Gradle, so the system Java version is irrelevant.
