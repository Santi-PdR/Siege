# QA — SIEGE 0.24.0

## Automated

- Build Forge mod workflow must pass completely.
- Guide geometry must pass across the full tested viewport sweep.
- Six Guide categories must stay inside their tab region without touching Search.
- Guide archive must contain 30 bilingual records with unique IDs and the same ten validated PNG sources.
- Operations search must find El Núcleo, Agreement and Rifts without exposing Chronicle spoiler bodies.
- Slider geometry must preserve values when the knob is grabbed off-centre, while rail clicks still seek and endpoints remain clamped.

## Minecraft visual check

Test GUI scales 1, 2, 3, 4 and Auto when available, in Spanish and English:

1. Open Configuración → Resumen → Guía SIEGE.
2. Verify OPERACIONES / OPERATIONS is visible and does not overlap the search field.
3. At narrow window sizes verify the category buttons move to two rows and remain readable.
4. Open El Núcleo, Agreement and Gates y Rifts; scroll each article to the final paragraph.
5. Search `Núcleo`, `3.000 HP` / `3,000 HP` and `interdimensional`; verify the expected single Operations record appears.
6. Confirm Chronicle searches still match titles without revealing spoiler body text before SHOW SPOILERS.
7. In Settings, grab Music Volume, UI Effects Volume and both darkness sliders from the left and right side of their knobs. The value must not jump when drag begins.
8. Click an empty point on each slider rail. The value should still seek directly to that position.
9. Resize the window while the Guide is open and confirm list/article/footer regions stay separated.
10. Confirm Intel, Multiplayer, title-screen dossiers, Gallery and music playback behave as before.
