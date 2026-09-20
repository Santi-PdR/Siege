# SIEGE 0.23.0 — Vanilla UI Overhaul

This release pushes SIEGE styling well beyond its custom screens and into the vanilla title-menu flows that still looked out of place.

## Major visual changes

- Vanilla **Options** and its child screens now use the SIEGE background, tactical shell, custom button language, focus treatment and UI sounds.
- **Sound**, **Video**, **Controls**, **Language**, **Accessibility**, **Chat**, **Online**, **Skin Customization** and **Resource Packs** receive category-specific SIEGE accents.
- Hidden singleplayer flows opened from SIEGE now keep the same visual language through **Select World**, **Create World**, **Edit World**, experiments, world optimization, backup confirmation and flat-world setup screens.
- Connection flows now extend the existing treatment through receiving/downloading terrain screens when no world is loaded yet.
- Native vanilla buttons are replaced in-place by SIEGE controls while keeping the original Minecraft objects alive for validation and state updates.
- Native sliders remain Minecraft-controlled but receive SIEGE focus frames and accent rails rather than being replaced with fragile custom option logic.
- Vanilla selection lists receive tactical perimeter frames and family accents.
- Text fields now have stronger focus brackets and category-colored focus rails.
- Tooltips inherit the active screen family accent rather than always using the same red border.

## Tactical chrome

- New compact top command bar with screen title, SIEGE subsystem label and animated accent sweep.
- New family palette for network, system, audio, video, controls, language, packs and world operations.
- Added code-drawn icons for back, globe, keyboard, package, chat, user, world and warning states.
- Background registration marks and edge rails make vanilla screens visibly part of SIEGE without covering their usable content.

## Safety / compatibility

- The theme remains **title-menu only**: it is disabled whenever a Minecraft world is loaded.
- Gameplay screens such as Pause, Chat, Inventory and Death remain untouched.
- Third-party mod screens are still excluded by exact class allowlisting.
- Generic `ConfirmScreen` is themed only when reached from an already themed SIEGE/menu flow.
- No gameplay hooks, Intel data, soundtrack assets, server persistence or dossiers are modified by this release.
