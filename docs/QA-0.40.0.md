# QA — SIEGE 0.40.0

## CI gates

The release must not merge unless all of these pass:

1. responsive layout/search regression suite;
2. Guide regression suite;
3. new Operations Archive chronology/content suite;
4. config persistence suite;
5. native menu policy/drawing geometry suite;
6. dossier navigation checks;
7. clean boss-frame checks;
8. runtime Intel/audio/catalog suite;
9. complete Forge build;
10. post-merge build on the exact merged `main` commit;
11. validated JAR publication.

## Source chronology

The supplied SIEGE notices are interpreted as **newest → oldest**.

Verify:

- `Current state · tools, Stalkers, Engineer and SHELLSHOCK` is first;
- Trident is second;
- Fusilier is third;
- rank values strictly decrease throughout the archive;
- historical Fusilier/Trident notes never replace the newest dossier text;
- archive search does not reorder records alphabetically.

## Operations Archive layout

Test GUI scales 1, 2, 3, 4 and Auto where available.

Window matrix:

- 320×180 logical-equivalent minimum;
- narrow portrait-like window;
- 854×480;
- 1280×720;
- 1920×1080;
- 2560×1440;
- ultrawide.

Verify:

- six tabs never leave the viewport;
- narrow layouts use the responsive multi-row tab geometry inherited from Guide;
- search box and clear button never overlap;
- record list and article panel never intersect;
- previous/next list-page controls stay below the list;
- article up/down controls stay below the article;
- scrollbar remains inside article bounds;
- dragging scrollbar cannot create negative or out-of-range scroll values;
- current/source labels wrap rather than leaving the article;
- High Contrast changes readability without changing geometry.

## Current notice content

Verify exact current values:

- SHELLSHOCK = 150%;
- Engineer Sound Erradicator records deafness, nausea and Slowness VI–VIII;
- Fauna: RPK-74, 3D-vision goggles, Compound-V-bypassing exoskeleton, Hallucinator recorder, up to three decoy clones;
- no HP/DEF invented for Fauna;
- latest Trident visor state rules appear in Current and Intel;
- latest Fusilier Mortar/Blox Drink/nuclear rules appear in Current and Intel.

## Trident dossier

Check `BOS-004`:

- HP remains 38,000 unless a newer numeric notice is supplied;
- Visor On = 40% resistance + headshot immunity + harder hook;
- Visor Removed = 10% resistance + headshot/flashbang vulnerability + instant melee parry + easier/faster hook;
- hooked ally torso rescue is documented;
- `cortar cuerda` / cut-rope option is documented conditionally on a sufficiently sharp weapon;
- old simplified hook-only description is not displayed as the current dossier.

## Fusilier dossier

Check `BOS-002`:

- HP remains 8,000 unless a newer numeric notice is supplied;
- nuclear grenade reaction window is approximately six seconds;
- close nuclear impact is recorded as mutilation-type defeat;
- fire grenades and improved strafing are present;
- Mortar Mode is present;
- white boss bar cue is present;
- Blox Drink is present;
- explosive interruption of Blox Drink is present.

## Agreement source boundary

Check `TNK-003`:

- 3,000 HP;
- 100 DEF;
- Secure Contain Protect link;
- no Gates;
- no Rifts;
- no Rick Sanchez comparison;
- no visor theory;
- no sabotage field advice.

Then verify those field-report concepts remain available in Guide > Operations.

## DvN casualty reference

The Operations Archive must show the researched distinction:

### Downed
- ~1.5 s standard Defibrillator revive.

### Mangled
- ~4 s standard Defibrillator revive;
- no invented two-charge cost;
- Pacemaker ignores the Mangled timing penalty.

### Mutilated
- standard Defibrillator cannot revive;
- no invented two-charge cost.

### Additional labels

Burnt, Disfigured and Erased must remain in a separate explanatory record and must not be presented as the verified three-state DvN revive model.

## Missions

### Operation Exodus
- SAURON = 23,400,000 HP;
- HEDALUS = 23,400,000 HP;
- probability enabled above Dynamic 100%;
- no invented weapons/DEF/abilities.

### Endless Inferno
- endless combat;
- open maps;
- construction freedom;
- inventory retained;
- increased unit intelligence / teamwork emphasis;
- Great Pyramid of Giza;
- Bramblewick;
- no invented wave count or spawn table from screenshots.

## System Center

- title reads `0.40 SYSTEM CENTER`;
- archive count appears in Content card at non-compact widths;
- `OPERATIONS ARCHIVE 0.40` button is reachable;
- the fourth control row does not overlap the policy text or panel edge;
- Minecraft Options and SIEGE Guide still work;
- Back returns to SIEGE Settings;
- compact windows hide the graphics profile label if it would overlap config status.

## Regression sweep

Verify unchanged behavior:

- official Multiplayer server remains protected;
- Edit/Delete remain unavailable only where intended;
- LAN discovery, pings, Direct Connect, Add, Refresh and Back still function;
- music keeps playing through Archive/System/Guide transitions;
- background gallery works;
- Intel left/right dossier navigation works;
- dossier hover pause/resume works;
- no Favorites/Index/Save/Copy controls reappear;
- no unrequested keyboard shortcuts appear;
- Embeddium keeps its own video interface;
- hidden Singleplayer route still works;
- Forge 1.20.1 / Java 17 target remains unchanged.
