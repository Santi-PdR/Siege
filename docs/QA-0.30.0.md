# QA — SIEGE 0.30.0

## Required automated checks

1. `Build Forge mod` must pass completely on the 0.30 PR.
2. Runtime catalog tests must confirm Agreement remains 3,000 HP / 100 DEF / threat unknown while its official dossier contains no Gate/Rift/Rick/visor/sabotage testimony.
3. Agreement field-report constants must still preserve the separated unverified material and provenance.
4. New High Contrast and Reduce Flashes settings must survive save/reload and use independent defaults on old revision-801 configs.
5. Reduce Flashes must suppress title interference.
6. Calm and Reading presets must match their 0.30 behavior.
7. Existing catalog identity, image resources, audio recovery, dossier rotation and source-policy tests must remain green.

## Visual matrix

Test GUI scales 1, 2, 3, 4 and Auto where available. Repeat at narrow, normal and maximized window sizes.

### Settings and System Center

- Open SIEGE Settings: `SYSTEM 0.30` must fit in the top-right without touching Back or the settings title.
- On a narrow window the button must shorten rather than overlap.
- Open System Center and verify the displayed SIEGE, Minecraft/Forge and render-backend information matches the actual running client.
- Confirm Intel/background/music counts are non-negative and plausible for the loaded build.
- Open Minecraft Options from System Center, return, and confirm navigation returns to System Center and then to SIEGE Settings.

### Accessibility

- Toggle High Contrast and verify SIEGE-owned screens plus themed vanilla screens become easier to separate without widgets moving.
- Toggle Reduce Flashes and confirm title interference is disabled, short transition fades disappear, background pan stops and the long crossfade remains smooth.
- Apply Calm Preset and confirm reduced motion, reduced flashes, high contrast, no scanlines/title interference/hover sound, Balanced profile.
- Apply Reading Preset and confirm Intel reading mode, comfortable spacing, dark paper, high contrast, reduced motion/flashes and no scanlines/title interference.
- Restart Minecraft and verify the settings persist.

### Intel source separation

- Open `TNK-003 AGREEMENT`.
- Its dossier must show the confirmed designation, 3,000 HP, 100 DEF and Secure Contain Protect link only.
- The dossier must not mention Gates, Rifts, Rick Sanchez, visor overload or sabotage advice.
- No `REPORTE`/`UNVERIFIED REPORT` styling should replace the normal official dossier presentation.
- Open Guide > Operations and verify the Agreement field report and Gates/Rifts material remain there and clearly marked as unverified where appropriate.
- Check other dossiers to ensure their existing official content and images were not altered by the Agreement-specific sanitation rule.

### Native screens

- Mouse, Accessibility, Sound and vanilla Video must retain their dedicated SIEGE identities.
- Focus, slider and text-field decoration must remain inside native widget rectangles at every scale.
- High Contrast must strengthen, not displace, those decorations.
- Reduce Flashes must remove the animated top sweep.
- With Embeddium active, its own Video GUI must remain untouched by SIEGE.
- Telemetry Data and Credits/Attribution entry points must remain absent from vanilla Options.

### Regression sweep

- Multiplayer official server stays pinned and Edit/Delete remain blocked only for that entry.
- Multiplayer pings, LAN discovery, Direct Connection, Add, Refresh and Back still use vanilla behavior underneath the SIEGE presentation.
- Main-menu music, track switching, dossier hover pause/resume and background gallery still work.
- No Favorites/Index/Save/Copy Intel controls return.
- No unrequested keyboard shortcuts return.
- Hidden Singleplayer route and vanilla world screens remain functional.
