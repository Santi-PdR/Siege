# SIEGE 0.25.0 — Native Settings Refit

This release focuses on the vanilla settings interfaces that were still visually cramped or too generic after 0.23.x, while preserving Minecraft's real option state and third-party rendering GUIs.

## Overlap and scale fixes

- Focus frames, EditBox rails and slider accents now render **inside** each native widget rectangle instead of borrowing pixels from neighbouring controls.
- The broad selection-list rail is no longer drawn over ordinary Controls, Mouse, Video, Sound or Accessibility screens; it is restricted to actual list screens.
- The Options grid is compacted after removing its unused bottom row, so `Done` moves up by exactly one vanilla 24-pixel row instead of leaving dead space near the lower chrome.
- Screen-specific information strips are only drawn when a real gap exists above the first visible widget; narrow or dense layouts skip the strip rather than overlap controls.
- Existing viewport clamping remains active for all native decoration.

## Mouse, Accessibility, Sound and Video

- **Mouse** is now its own SIEGE family with a blue input identity and dedicated mouse pictogram instead of sharing the generic Controls header.
- **Accessibility** is now its own green SIEGE family with an eye identity and accessibility-specific header.
- **Sound** uses a dedicated `AUDIO MIXER` identity, gold perimeter motif and factual native-mix context.
- **Video** uses a dedicated `VIDEO / RENDER` identity, cyan framing and explicit compatibility wording.
- The exact-class policy continues to theme the vanilla `VideoSettingsScreen` only. Embeddium/Sodium-derived video screens remain third-party and are deliberately untouched.

## Removed vanilla menu entries

- Removed `Telemetry Data` from the vanilla Options screen.
- Removed the `Credits & Attribution` entry from the vanilla Options screen.
- If the credits screen is reached through another route, its Credits and Attribution buttons are also suppressed defensively; licensing information is not deleted from the game files.
- No Minecraft telemetry implementation, attribution files or licenses are modified; this is a menu-visibility change only.

## Real information, not invented settings

- The new context labels describe settings that actually exist on the current native screens: master/category audio mixing, mouse sensitivity/input, accessibility features and vanilla video/render options.
- Embeddium compatibility is stated as an interface-scope rule enforced by SIEGE: its GUI classes are explicitly tested as outside the SIEGE native allowlist.
- No fake graphics toggles, invented performance claims or fabricated gameplay data are added.

## Unchanged

- No gameplay hooks, combat statistics, server rules or world logic are changed.
- Intel dossiers, soundtrack assets, Multiplayer connection handling and official-server persistence are preserved.
