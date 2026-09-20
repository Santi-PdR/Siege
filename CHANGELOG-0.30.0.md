# SIEGE 0.30.0 — SYSTEM / INTEL RECONSTRUCTION

0.30.0 is a deliberate major jump from 0.25.0. It changes how SIEGE separates official intelligence from field reports, adds a real client system center, expands accessibility into rendering behavior, and hardens the themed vanilla interfaces instead of treating this as a cosmetic version bump.

## Intel: official dossiers only

- The Intel catalog now has an explicit source boundary: dossier records are presentation data for **official/confirmed information only**.
- `AGREEMENT / TNK-003` is the first enforced source-policy record. Its dossier contains only the confirmed designation, 3,000 HP, 100 DEF and the link to Secure Contain Protect.
- Gates, Rifts, the Rick Sanchez comparison, visor-overload ideas, sabotage tactics and similar player testimony are no longer injected into the Agreement dossier.
- Those claims remain available as **unverified field intelligence** in the Guide/Operations material, where they can retain their provenance without being mistaken for canonical unit specifications.
- A startup regression guard rejects Agreement dossier data if field-report vocabulary leaks back into the official catalog.
- Intel coverage now has an A–E coverage grade derived only from actually documented fields. It is a coverage indicator, not a certainty score or threat rating.
- Main-menu Intel preview remains limited to Unit and Advanced categories, preventing field-report-only tank material from surfacing there.

## New SIEGE System Center

A new `SYSTEM 0.30` entry is injected into the SIEGE Settings title bar without expanding or overcrowding the existing settings category list.

The System Center shows live, real client state:

- SIEGE build/version;
- Minecraft and Forge versions;
- detected render backend (Embeddium, Rubidium, Sodium or vanilla fallback);
- current graphics profile;
- config save health;
- Intel/background/music catalog counts;
- active background and rotation state;
- current soundtrack state and SIEGE music volume;
- the 0.30 official-Intel source policy.

It also links directly to Minecraft Options and the SIEGE Guide.

## Accessibility is now functional rendering behavior

New persistent client options:

- **High Contrast** — strengthens separation between surfaces, text and native menu chrome and raises the effective darkness behind reading surfaces.
- **Reduce Flashes** — disables title interference, disables the transient midpoint veil between backgrounds and suppresses short SIEGE entry fades.

Updated presets:

- **Calm Preset** now enables reduced motion, reduced flashes and high contrast, disables scanlines/title interference/hover sounds and uses the Balanced profile.
- **Reading Preset** enables Intel reading mode, comfortable line spacing, dark dossier paper, high contrast, reduced motion and reduced flashes, while disabling scanlines and title interference.

Both options persist in `config/siege-client.properties`. The historical settings revision remains 801 so adding 0.30 options cannot re-run the old auto-rotation migration.

## Background system 0.30

- Background rendering now obeys High Contrast and Reduce Flashes instead of those options being labels only.
- High Contrast darkens the image behind menu content and strengthens panel separation.
- Reduce Flashes disables pan/drift and the transient crossfade veil while preserving the long crossfade itself.
- Scanlines are quieter under High Contrast.
- Background state now exposes the current scene and time until the next automatic rotation to the System Center.

## Native settings chrome 0.30

Mouse, Accessibility, Sound and vanilla Video retain their dedicated 0.25 identities and receive the new accessibility behavior:

- high-contrast top chrome, context strips, fields and list rails;
- no animated sweep when Reduce Flashes is active;
- stronger focused/unfocused distinction without painting outside native widget bounds;
- Embeddium/Sodium-derived video GUIs remain outside the exact vanilla allowlist and keep their own interface.

The 0.25 removal of Telemetry Data and Credits/Attribution menu entry points is preserved.

## Intel and menu presentation

- Intel screens display a restrained `OFFICIAL DOSSIERS` source badge on sufficiently wide layouts.
- Compact layouts omit that badge instead of sacrificing usable space.
- SIEGE Settings receives a responsive System 0.30 button; narrow screens shorten it to `0.30` rather than overlapping the Back control or title.
- High Contrast also affects SIEGE tooltips, focused text fields and the build label.

## Safety and regression policy

0.30 adds/updates tests for:

- official Agreement data and the field-report separation;
- forbidden field-report terms in the Agreement dossier;
- Intel coverage grades;
- new accessibility settings, persistence and both presets;
- old settings files continuing to load without migration regressions;
- catalog immutability, identity and resources;
- existing audio recovery and Intel rotation behavior.

No gameplay hooks, server combat rules, mob AI or world logic are introduced by this release.
