# 0.9.0 verification

## Automated in GitHub Actions

- Production gallery bounds: positive preview size; separate heading, preview, thumbnail region and action bar; no tile overlap; click targets inside their region.
- Production Intel bounds: categories/search/navigation separated; at least one row in the wide list; minimum reading height.
- 320×240 through 2560×1440 logical pixel sweep with odd dimensions; actual GUI-scale calculation for 1280×720, 1366×768, 1920×1080, 2560×1440 and 3440×1440 at requested scales 1–4.
- Literal search: case, accents, whitespace, multiple tokens, empty and unmatched queries.
- Existing CI guards: dossier arrows, circular navigation, hover release, forbidden unsolicited shortcuts, clean boss images and Forge build.

## In-game visual verification — still requires a Minecraft client

At GUI scales 1, 2, 3, 4 and Auto, in Spanish and English:

1. Resize between wide/compact while an Intel query is present and while scrolled.
2. Open a long dossier; read its last advisory line; drag its scrollbar; use Reading and Inspect; return to the same record.
3. Type accented text and move the caret with arrows; arrows must edit the query, not change dossiers.
4. Clear a no-results query; remove the last favorite; verify the empty state and recovery through categories.
5. Navigate favorites with dossier arrows and check that Save/Saved always matches the displayed record.
6. Inspect artwork: Fit, +, −, wheel, edge-limited drag and Back. Verify original flags/text are preserved.
7. Browse gallery thumbnails across pages, pin a scene, resume rotation, enter clean view and return with click/Escape.
8. Reduce motion: gallery changes immediately; no Intel reveal or title interference.
9. Verify title, music notice, gallery button, main buttons and one/two dossiers do not overlap. GUI 4 retains the previous policy of hiding the main feed.
10. Hover a main dossier, leave it and verify automatic rotation resumes after 2 seconds.
11. Test settings scroll/slider drag and the transport controls; notice duration is shown in seconds, darkness in actual percent.
12. Join a world/server and verify the menu soundtrack stops; return and verify it resumes.

No in-game screenshots or manual game verification are claimed by the geometry tests.
