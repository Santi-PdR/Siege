# Eternal Craft - SIEGE

Forge 1.20.1 client menu for Eternal Craft: SIEGE.

## Intel dossiers

- Intel has no global `ALL` filter: categories and their own file lists remain permanently visible in the left sidebar.
- All sixteen photographs come from the owner's named ZIP folders. No troop is redrawn, substituted or generated: only the dossier treatment is composited over the supplied Minecraft pixels.
- Common-unit photographs reproduce the Sniper's physical aged-paper dossier: distressed military/typewriter ink, embedded archival metadata and a worn red `CLASSIFIED` stamp. Advanced units keep a separate charcoal dossier with electric-blue accents and blue `CLASSIFIED` stamp.
- Flags are shown only when the supplied record confirms the unit's origin; no unknown allegiance is invented.
- SIEGE menu music is loudness-normalized, controlled by its own toggle and plays through the audible master/UI mix instead of Minecraft's ambient-music slider. `Next Track` also re-enables music if an older saved configuration had disabled it.
- Common units use one consistent aged grayscale record treatment with red accents.
- Advanced units use aged grayscale records with blue accents.
- Each file contains the complete supplied operational profile and tactical advisory, with a scrollable reading area at every GUI scale.
- Nusia flags appear only on records whose supplied source explicitly confirms the Republic of Nusia.
- Every portrait is stored at 640x360 and preserves the source game's block-based visual style.
- Patriot is confirmed as a Republic of Nusia unit. Its missing operational details remain marked as incomplete instead of being invented.

The full category palette and verified unit-origin rules are documented in `docs/RECONSTRUCTION.md`.

## Build and deploy

GitHub Actions is the only build environment. Windows only requires Git: `scripts/install-latest.ps1` clones the current `main` and copies the GitHub-validated JAR exclusively to SKLauncher instance `test-1`; it never invokes local Java or Gradle.
