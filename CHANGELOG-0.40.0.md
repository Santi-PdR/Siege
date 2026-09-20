# SIEGE 0.40.0 — OPERATIONS ARCHIVE / CURRENT INTEL

0.40.0 is a major jump from 0.30.0. Its central rule is simple: **newer SIEGE notices override older ones when they conflict**, while old notices remain searchable as history instead of silently rewriting current dossiers.

## 1. New Operations Archive 0.40

A new full-screen archive is available from `Settings → System 0.40 → Operations Archive 0.40`.

It includes six responsive sections:

- Current
- Missions
- Units
- Equipment
- Casualty
- Archive

The screen supports:

- chronological newest-first priority;
- bilingual Spanish/English records;
- literal search using the same normalized search engine as Intel;
- responsive six-tab layout shared with the Guide geometry;
- paged record list;
- independent article scrolling;
- draggable article scrollbar;
- high-contrast support;
- source colors distinguishing current SIEGE, historical SIEGE and DvN reference material;
- no new gameplay packets, commands or server-side behavior.

## 2. Current SIEGE notice is authoritative

The newest supplied notice is stored as the current baseline, including:

- refreshed icons for Wrench, F.A.S.T, Jetpack, Terminal Velocity and Aerorig+;
- Stalkers re-added;
- Engineer `Sound Erradicator`;
- `SHELLSHOCK · 150%` difficulty;
- latest Trident visor/hook/parry rules;
- latest Fusilier nuclear, fire, Mortar Mode, strafing and Blox Drink rules;
- latest Sparta Shockwave / Steadfast / aerial response;
- new Elite Fauna and its confirmed equipment.

Older announcements remain available in the archive but may not overwrite these current records.

## 3. Trident official dossier updated

`BOS-004 / TRIDENT` now reflects the newest supplied SIEGE announcement:

### Visor on

- 40% resistance to all damage;
- immune to headshots;
- hook acquisition is more difficult.

### Visor removed

- 10% resistance;
- vulnerable to headshots and flashbangs;
- instant first-melee parry that causes a mutilation-type defeat;
- hook becomes easier to land and reels faster with less pull resistance.

### Hook rescue

- allies caught by the hook can be freed by shooting their torso;
- a hooked player may attempt the `cut rope` / `cortar cuerda` chat cast while carrying a sufficiently sharp weapon.

The previous HP value remains unchanged because the newer notice supplied no replacement HP value.

## 4. Fusilier official dossier updated

`BOS-002 / FUSILIER` now reflects the newest supplied notice:

- nuclear grenade with an approximately six-second reaction window;
- close nuclear impact produces a mutilation-type defeat;
- improved strafing;
- incendiary AoE grenades;
- Mortar Mode with skyward fire, controlled impact zones and multiple ammunition types;
- white boss bar as the Mortar Mode cue;
- Blox Drink reduces ability cooldowns and accelerates grenade-launcher reload/fire speed while blurring vision;
- a correctly timed explosive can interrupt the drink.

Older Fusilier notes such as predictive fire and sprint remain historical context rather than overriding the latest dossier.

## 5. Agreement source separation remains enforced

The 0.30 official-only Agreement rule remains active:

- dossier: confirmed designation, 3,000 HP, 100 DEF and Secure Contain Protect link;
- Guide/Operations: Gates, Rifts, Rick Sanchez comparison, visor theories and sabotage field advice.

0.40 does not regress this separation.

## 6. New mission archive

### Operation Exodus

- catastrophic multi-incident event;
- spawn probability enabled above Dynamic 100%;
- SAURON — 23,400,000 HP;
- HEDALUS — 23,400,000 HP;
- no invented DEF, weapons or abilities beyond the supplied notice.

### Endless Inferno

- endless combat mission;
- fully open maps;
- building freedom;
- inventory retention;
- teamwork emphasized because unit intelligence is increased in this mode;
- registered maps: Great Pyramid of Giza and Bramblewick.

The supplied map captures are treated as visual context only; wave/spawn rules are not inferred from scenery.

## 7. Equipment archive

Added preserved records for:

- Third Justice — Eternal rarity, long-term parry utility, +2 perks while equipped in inventory;
- Medium Frequency Vanguard Sword improvements;
- HOLO-Watch;
- Riflator, including the recorded 50 → 130 damage change;
- Aerorig flight device;
- historical Gunner projectile-speed adjustment;
- Stylish Battle Rank rework notice without inventing an unpublished scoring formula.

## 8. Unit / roster archive

The archive now preserves records for:

- Fauna — current Elite Cloaker Infiltrator;
- Proteus — Elite W.I.P.;
- Cerberus reclassification from Boss to Elite and its recorded large rework;
- Agitator;
- Informant;
- Jagant;
- Grappler;
- Tranquilizer;
- Skydiver;
- Skyliner;
- historical Cloaker Parry chance;
- Gunner projectile visibility;
- Specialist Stop Time;
- historical full rework of specials, Bosses, Elites and Tanks.

Fauna/Proteus and other entries are not forced into illustrated Intel dossiers when a trustworthy portrait or required dossier fields are missing. Missing information stays missing.

## 9. Dummies vs Noobs casualty-state research

0.40 adds a researched casualty reference under the new `Casualty` archive section.

The checked DvN references document three core downed/revive states:

### Downed

- normal state;
- standard Defibrillator: about 1.5 seconds;
- a normal revive uses one device charge.

### Mangled

- standard Defibrillator: about 4 seconds;
- still a single revive use, not a documented two-charge revive;
- Medical Bow cannot normally revive it;
- Pacemaker ignores the Mangled time penalty and revives in about 1.5 seconds.

### Mutilated

- standard Defibrillator cannot revive it;
- normal recovery waits for intermission/respawn;
- special respawn-like exceptions are not treated as ordinary Defibrillator rules.

The standard Defibrillator itself is documented with two charges per wave. This is a device capacity, not a rule that Mutilated costs two charges.

### Burnt / Disfigured / Erased

These are retained as possible SIEGE casualty/result labels, but 0.40 does **not** misrepresent them as part of the verified three-state DvN revive model or assign invented revive costs/timings.

Research rationale is stored in `docs/DVN-DEATH-STATES-REFERENCE.md`.

## 10. System Center upgraded to 0.40

System Center now:

- identifies itself as `0.40 SYSTEM CENTER`;
- counts Operations Archive records alongside Intel/background/music content;
- links directly to the new Operations Archive;
- explains newest-first source priority;
- keeps Minecraft Options and SIEGE Guide access;
- keeps High Contrast, Reduce Flashes, Calm and Reading presets;
- avoids drawing the graphics-profile label when there is insufficient horizontal room.

The controls block was expanded carefully rather than stacking a new button over existing content.

## 11. Automated protection

New regression coverage verifies:

- archive IDs are unique;
- announcement ranks remain strictly newest-to-oldest;
- the newest notice stays first;
- SHELLSHOCK remains 150%;
- current Fauna / Trident / Fusilier details remain intact;
- Operation Exodus HP values remain exact;
- Endless Inferno map names remain exact;
- DvN Downed/Mangled/Mutilated states remain present;
- Mutilated does not receive an invented two-charge revive rule;
- Burnt/Disfigured/Erased remain separate labels;
- current Trident and Fusilier official dossiers keep newest supplied behavior;
- existing Agreement source boundaries, Intel assets, soundtrack recovery and layout tests continue to run.

## 12. What 0.40 deliberately does not do

- no fake HP/DEF for newly announced units;
- no reused portrait assigned to the wrong unit;
- no gameplay implementation of server changelog mechanics inside this client-menu mod;
- no automatic claim that historical notices are still current when a newer notice supersedes them;
- no invented DvN revive charge rules;
- no reintroduction of Intel Favorites/Index/Save/Copy controls;
- no unrequested keyboard shortcuts;
- no modification to the official server lock/persistence behavior.
