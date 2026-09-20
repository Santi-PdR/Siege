# SIEGE 0.24.0 — Operations Archive & Interaction Polish

This update expands existing SIEGE material and continues the interface cleanup without changing gameplay rules or replacing the established menu structure.

## New guide content

- Added a sixth Guide family: **OPERATIONS / OPERACIONES**.
- Added a bilingual briefing for **El Núcleo / The Core**, documenting its reactive role in the 2044 war theatre and separating confirmed behaviour from broader assumptions.
- Added an **Agreement field report** that keeps retained stats and testimony-based tactics visibly separated.
- Added a dedicated **Gates and Rifts** briefing so constructed teleporters are no longer confused with interdimensional openings.
- Guide search indexes the new Operations records while Chronicle spoiler bodies remain excluded from search.

## Guide layout

- Category tab rows now derive from the actual number of Guide families instead of the old hard-coded five-tab assumption.
- Normal-width menus keep all six categories on one row.
- Narrow logical viewports use two readable rows instead of shrinking labels until they collide with Search.
- Regression coverage checks every Guide tab against Search, list, article and footer bounds across thousands of logical viewport sizes.

## Slider interaction

- Grabbing the slider knob no longer makes the value jump to the cursor centre.
- Dragging now preserves the exact point where the knob was grabbed.
- Clicking elsewhere on the rail still seeks immediately, preserving the fast existing interaction.
- Invalid pointer/grab values remain clamped safely.
- Geometry tests cover held-pointer offsets, endpoints and narrow slider widths.

## Unchanged

- No new gameplay hooks.
- No changes to Intel unit statistics or dossier artwork.
- No soundtrack replacement.
- No changes to official-server persistence or Multiplayer connection behaviour.
- Existing SIEGE navigation, backgrounds, title screen layout and menu identities remain intact.
