# SIEGE 0.40.1 — Intel Placement & Interface Cleanup

0.40.1 corrects the information architecture introduced by 0.40.0 and performs a focused interface cleanup.

## Intel now owns gameplay knowledge

- The chronological Operations Archive is no longer opened from Settings/System.
- Intel now exposes a dedicated `INTEL ARCHIVE` entry in its own top bar.
- The archive is presented as `INTEL // OPERATIONAL ARCHIVE`, not as a configuration feature.
- Death/downed states, missions, equipment, current notices, unit updates and historical records are therefore all reached through Intel.
- The Dummies vs Noobs reference for Downed, Mangled and Mutilated remains under the Intel archive `ESTADOS / CASUALTY` section.
- Burnt, Disfigured and Erased remain separate condition/result labels without invented defibrillator rules.
- Current-vs-historical source priority remains newest → oldest.

## Settings is configuration again

- System 0.40 no longer contains an Operations Archive button.
- The Guide shortcut was removed from the System screen.
- System cards now describe client/build/render/configuration state rather than catalog/archive content.
- The remaining System controls are High Contrast, Reduce Flashes, Calm Preset, Reading Preset and Minecraft Options.

## Vanilla/custom title collision fix

- The themed vanilla screens previously rendered Minecraft's own title first and then drew the SIEGE header over it. Depending on GUI scale and the screen's title position, both could remain visible.
- 0.40.1 masks only the proven-empty vanilla title strip after native rendering and before drawing SIEGE chrome.
- The mask stops before the first visible widget and is capped to 34 logical pixels, preventing it from covering option rows on dense GUI scales.
- Regression tests cover short viewports, close first-widget positions and the maximum title-mask height.

## Intel archive layout cleanup

- Archive title text now uses the free header area to the right of Back and clips itself before overlap.
- Narrow layouts shorten the heading to `INTEL // ARCHIVE` / `INTEL // ARCHIVO`.
- Search wording is now Intel-specific and explicitly includes states, missions, units and equipment.
- The screen is included in SIEGE-owned UI theming so focus, tooltips and transitions remain consistent with the rest of Intel.

No gameplay mechanics, server rules, unit statistics or source chronology were changed in this patch.
