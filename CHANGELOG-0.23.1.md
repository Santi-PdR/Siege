# SIEGE 0.23.1 — Stabilization & Scale Polish

This release deliberately adds no new menu features. It tightens the existing 0.23.0 interface, fixes edge cases and reduces avoidable per-frame work.

## Layout and overlap fixes

- Button depth/shadow treatment stays inside each widget's own rectangle so tightly packed controls cannot paint over their neighbours at high GUI scales.
- Very small controls no longer attempt invalid hover-scissor geometry.
- Native button wrappers now mirror source height as well as position and width, preventing stale hit/render geometry when vanilla relayouts a screen.
- Native text-field and slider focus frames are clamped to the current viewport so decoration cannot bleed beyond screen edges.
- Existing list-family rails remain inside safe margins on short and narrow windows.

## Optimization

- Button and slider hover animation settles without continuing expensive exponential interpolation once the visual state has converged.
- Native EditBox text colors are initialized once instead of being rewritten for every field on every rendered frame.
- Native button icons are recalculated only when the source button label actually changes.
- Scanline density is capped adaptively on very tall logical viewports, preserving the existing effect while reducing draw calls.
- Crossfade rendering skips effectively invisible incoming frames.

## Robustness

- Background helpers now reject zero-sized regions safely and avoid width division edge cases.
- Responsive regression coverage now includes exact layout breakpoints, 4K/ultrawide resolutions and every valid requested GUI scale while preserving Minecraft's minimum logical viewport.
- Existing gameplay exclusions, Intel content, audio catalogue, dossiers, server persistence and navigation behaviour are unchanged.
