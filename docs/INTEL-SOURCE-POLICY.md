# SIEGE Intel Source Policy — 0.30+

## Principle

The Intel dossier database is not a dumping ground for every statement known about a unit. A dossier may show official/confirmed information and may explicitly mark official fields as unknown. Player testimony, tactical guesses, comparisons, rumours and hypotheses belong to the Guide/Operations archive unless they are later promoted to official information.

This rule prevents an unverified tactic from becoming a unit capability simply because it appears beside HP, DEF or an official image.

## Agreement / TNK-003

Official dossier facts currently retained:

- designation: Agreement;
- category: Tank;
- 3,000 HP;
- 100 DEF;
- documented link to Secure Contain Protect.

Not official dossier facts:

- comparison to Rick Sanchez;
- Gates;
- Rifts;
- claims about portal fluid;
- claims about visor blocking/overload;
- repeated-sabotage tactics;
- any alleged unique weakness derived from those reports.

Those claims remain available as unverified field material under Guide > Operations. Keeping them there preserves useful context and attribution without presenting them as established specifications.

## Unknown fields

Unknown data must stay unknown. The UI uses explicit labels such as `Sin información oficial confirmada` / `No official information confirmed` rather than filling gaps from appearance, screenshots, other games or inference.

The dossier coverage grade (A–E) describes how many tracked fields are documented. It is not a probability, truth score, threat score or combat rating.

## Promotion rule

Field-report material should enter a dossier only after it becomes official SIEGE information. When that happens, update the canonical Intel record and its regression tests together. Do not silently copy text from the Guide into Intel.

## Regression guard

Agreement is the current sentinel record. Runtime validation rejects its official dossier if terminology associated with the separated field report leaks back into the catalog. This is intentional: future UI refactors must preserve the source boundary.
