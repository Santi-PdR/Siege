# Dummies vs Noobs casualty-state reference used by SIEGE 0.40

This note records why SIEGE 0.40 does **not** invent a two-charge revive rule for Mutilated.

Checked reference material:

- Dummies vs Noobs Wiki — Downed: https://dummies-vs-noobs.fandom.com/wiki/Downed
- Dummies vs Noobs Wiki — Defibrillator: https://dummies-vs-noobs.fandom.com/wiki/Defibrillator
- Dummies vs Noobs Wiki — Defibrillator/Pacemaker: https://dummies-vs-noobs.fandom.com/wiki/Defibrillator/Pacemaker
- Dummies vs Noobs 2.0 Update Log — Roblox Developer Forum: https://devforum.roblox.com/t/dummies-vs-noobs-20-update-log/2395600

## Verified core revive states

### Downed

- Common incapacitated state.
- Standard Defibrillator revive time: about 1.5 seconds.
- The normal Defibrillator has two charges per wave; a revive is one use.
- Medical Bow can revive ordinary Downed targets under its normal conditions.

### Mangled

- More severe revive state.
- Standard Defibrillator / Resuscitator revive time: about 4 seconds.
- It is still one revive use; the checked references do not document a special two-charge cost.
- Medical Bow cannot normally revive a Mangled target.
- Pacemaker ignores the Mangled penalty and revives in about 1.5 seconds.

### Mutilated

- Standard Defibrillator cannot revive this state.
- The main reference describes the player as effectively out for the rest of the wave until intermission/respawn.
- Special exceptions may bypass this through respawn-like behavior, but that is not the normal Defibrillator rule.

## Burnt / Disfigured / Erased

These terms are **not promoted to the core DvN revive-state model** in SIEGE 0.40.

The checked primary revive reference explicitly defines Downed, Mangled and Mutilated as its three downed states. Other terms may appear as corpse descriptions, visual death effects, related/fan-content labels, or narrative outcomes, but SIEGE must not assign them Defibrillator charge costs or revive timings without a server-specific rule.

If SIEGE later defines custom states called Burnt, Disfigured or Erased, those rules belong to SIEGE documentation and must be labelled as such rather than attributed to DvN.

## Implementation rule

`SiegeArchiveData` carries this distinction into the in-game Operations Archive. Automated tests ensure the three verified states remain present and that Mutilated is not silently given an invented two-charge revive rule.
