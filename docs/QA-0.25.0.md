# QA — SIEGE 0.25.0

## Automated

- Build Forge mod must pass completely on the PR and again on merged `main`.
- Native-family tests must distinguish Mouse and Accessibility from generic Controls/System.
- Embeddium and Sodium-derived video screen class names must remain outside the SIEGE native allowlist.
- Telemetry Data and Credits & Attribution must be hidden only from the exact vanilla Options flow.
- The Options Done button must move up exactly 24 logical pixels after that two-column row is removed.
- List rails must not be enabled for Mouse, Controls, Accessibility, Video or Sound.
- Every tactical 9×9 icon, including the new mouse icon, must remain inside its assigned bounds.

## Minecraft visual check

Test GUI scales 1, 2, 3, 4 and Auto where available, plus narrow and maximized windows:

1. Open vanilla Options and confirm Telemetry Data and Credits & Attribution are absent and there is no empty bottom row before Done.
2. Open Mouse Settings. Confirm the blue SIEGE mouse identity is visible and no large frame crosses sliders or labels.
3. Open Accessibility. Confirm the green accessibility identity is visible and no header/context text overlaps the first option.
4. Open Sound. Confirm the gold Audio Mixer identity remains clear at every scale and native volume controls stay usable.
5. Open Video without a replacement GUI and confirm the cyan Video / Render identity remains inside the viewport.
6. With Embeddium active, open Video and confirm Embeddium's own screen is not reskinned by SIEGE.
7. Use keyboard focus and Tab navigation on all four screens; focus brackets must remain inside the focused control.
8. Drag native sliders at their edges and through the centre. Decoration must not obscure adjacent rows.
9. Resize each screen while open and revisit it after resizing; no tactical rail should cross ordinary option grids.
10. Confirm Language, Resource Packs, Key Binds and Select World still retain the inner list rail where it is useful.
11. Confirm Pause, inventory, chat, death screen and third-party GUIs remain untouched.
12. Confirm Intel, Guide, Gallery, Multiplayer and music behaviour are unchanged.
