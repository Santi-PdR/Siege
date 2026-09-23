package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** SIEGE 5.40 audiovisual room: current media, operational moods and DVN direction. */
public final class SiegeMediaRoomScreen extends Screen {
    private enum Mode { BUNDLED, MOODS, DVN_AUDIO, VISUALS }

    private final Screen parent;
    private Mode mode = Mode.BUNDLED;
    private int panelX, panelY, panelW, panelH, contentX, contentY, contentW, contentH;
    private int listOffset;
    private boolean compact;

    public SiegeMediaRoomScreen(Screen parent) {
        super(Component.literal("SIEGE // MEDIA ROOM"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        compact = width < 640 || height < 370;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 38;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 18 : 24));

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 88, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(Math.max(8, width - (compact ? 78 : 112) - 8), 7,
                compact ? 78 : 112, 19, Component.literal(label("GALERÍA", "GALLERY")),
                b -> minecraft.setScreen(new SiegeSceneScreen(this)), SiegeTheme.BLUE)
                .withIcon("image").setCompactCenter(true));

        int tabY = panelY + 41;
        int tabX = panelX + 10;
        int gap = 4;
        int tabW = panelW - 20;
        int count = Mode.values().length;
        int cell = Math.max(34, (tabW - gap * (count - 1)) / count);
        for (int i = 0; i < count; i++) {
            Mode value = Mode.values()[i];
            int x = tabX + i * (cell + gap);
            int w = i == count - 1 ? panelX + panelW - 10 - x : cell;
            addRenderableWidget(new SiegeButton(x, tabY, w, 19,
                    Component.literal(tabLabel(value, w)), b -> switchMode(value), modeAccent(value))
                    .setCompactCenter(true).setSelected(value == mode));
        }

        contentX = panelX + 10;
        contentY = tabY + 28;
        contentW = panelW - 20;
        contentH = Math.max(80, panelY + panelH - contentY - 10);
        if (mode == Mode.BUNDLED) initBundledControls();
    }

    private void switchMode(Mode next) {
        if (mode == next) return;
        mode = next;
        listOffset = 0;
        SiegeUiSounds.category();
        rebuildWidgets();
    }

    private void initBundledControls() {
        int gap = 4;
        int y = contentY + 52;
        int w = Math.max(46, (contentW - gap * 2) / 3);
        addRenderableWidget(new SiegeButton(contentX, y, w, 19, Component.literal(label("ANTERIOR", "PREVIOUS")),
                b -> { SiegeMusic.previousTrack(); SiegeUiSounds.click(); }, SiegeTheme.CYAN)
                .withIcon("back").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(contentX + w + gap, y, w, 19, Component.literal(label("REINICIAR", "RESTART")),
                b -> { SiegeMusic.restartTrack(); SiegeUiSounds.click(); }, SiegeTheme.GOLD)
                .withIcon("music").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(contentX + (w + gap) * 2, y,
                contentX + contentW - (contentX + (w + gap) * 2), 19,
                Component.literal(label("SIGUIENTE", "NEXT")),
                b -> { SiegeMusic.nextTrack(); SiegeUiSounds.click(); }, SiegeTheme.CYAN)
                .withIcon("music").setCompactCenter(true));

        int sceneY = y + 58;
        addRenderableWidget(new SiegeButton(contentX, sceneY, w, 19, Component.literal(label("◀ FONDO", "◀ SCENE")),
                b -> shiftScene(-1), SiegeTheme.BLUE).withIcon("image").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(contentX + w + gap, sceneY, w, 19,
                Component.literal(compact ? label("AUTO", "AUTO") : label("ROTACIÓN AUTO", "AUTO ROTATE")),
                b -> { SiegeConfig.selectedScene = -1; SiegeConfig.animatedBackgrounds = true; SiegeConfig.save(); SiegeUiSounds.confirm(); },
                SiegeTheme.GREEN).withIcon("image").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(contentX + (w + gap) * 2, sceneY,
                contentX + contentW - (contentX + (w + gap) * 2), 19,
                Component.literal(label("FONDO ▶", "SCENE ▶")), b -> shiftScene(1), SiegeTheme.BLUE)
                .withIcon("image").setCompactCenter(true));
    }

    private void shiftScene(int direction) {
        int current = SiegeBackgrounds.currentIndex(System.currentTimeMillis());
        SiegeConfig.selectedScene = Math.floorMod(current + direction, SiegeBackgrounds.count());
        SiegeConfig.save();
        SiegeUiSounds.click();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC9000000 : 0xA0000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, SiegeTheme.CYAN);
        g.drawString(font, fit(label("SALA MULTIMEDIA 5.40", "MEDIA ROOM 5.40") + " // " + SiegeRuntimeStatus.version(), panelW - 24),
                panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Fondos, música y ambientes del frente actual de SIEGE / Dummies vs Noobs.",
                "Backgrounds, music and operational moods for the current SIEGE / Dummies vs Noobs front."), panelW - 24),
                panelX + 12, panelY + 21, SiegeTheme.MUTED, false);

        SiegeTheme.panel(g, contentX - 3, contentY - 3, contentW + 6, contentH + 6, modeAccent(mode));
        switch (mode) {
            case BUNDLED -> renderBundled(g);
            case MOODS -> renderMoods(g);
            case DVN_AUDIO -> renderDvnAudio(g);
            case VISUALS -> renderVisuals(g);
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private void renderBundled(GuiGraphics g) {
        int x = contentX + 9;
        int y = contentY + 8;
        int w = contentW - 18;
        g.drawString(font, fit(label("PISTA: ", "TRACK: ") + SiegeMusic.currentTrackName(), w), x, y, SiegeTheme.GOLD, false);
        y += 12;
        String progress = label("ESTADO: ", "STATE: ") + SiegeMusic.transitionLabel(spanish())
                + " · " + SiegeMusic.currentTrackNumber() + "/" + SiegeMusic.trackNames().size();
        g.drawString(font, fit(progress, w), x, y, SiegeTheme.MUTED, false);
        y += 12;
        g.fill(x, y, x + w, y + 3, 0xFF272C30);
        g.fill(x, y, x + Math.round(w * SiegeMusic.currentProgress()), y + 3, SiegeTheme.GOLD);

        int sceneY = contentY + 86;
        int scene = SiegeBackgrounds.currentIndex(System.currentTimeMillis());
        g.drawString(font, fit(label("ESCENA: ", "SCENE: ") + SiegeBackgrounds.name(scene, spanish())
                + " · " + SiegeBackgrounds.sceneTag(scene, spanish()), w), x, sceneY, SiegeTheme.CYAN, false);
        g.drawString(font, fit(SiegeBackgrounds.rotationState(spanish(), System.currentTimeMillis()), w),
                x, sceneY + 12, SiegeTheme.MUTED, false);

        int listY = contentY + 127;
        if (listY + 10 < contentY + contentH) {
            g.drawString(font, label("PLAYLIST ACTUAL", "CURRENT PLAYLIST"), x, listY, SiegeTheme.INK, false);
            int yy = listY + 13;
            for (String name : SiegeMusic.trackNames()) {
                if (yy + font.lineHeight > contentY + contentH - 6) break;
                g.drawString(font, "• " + name, x, yy,
                        name.equals(SiegeMusic.currentTrackName()) ? SiegeTheme.GOLD : SiegeTheme.MUTED, false);
                yy += 11;
            }
        }
    }

    private void renderMoods(GuiGraphics g) {
        int x = contentX + 9;
        int y = contentY + 8;
        int w = contentW - 18;
        g.drawString(font, fit(label("AMBIENTES OPERACIONALES", "OPERATIONAL MOODS"), w), x, y, SiegeTheme.GOLD, false);
        y += 14;
        g.drawString(font, fit(label(
                "Cada ambiente combina el soundtrack actual con referencias DVN según la pantalla o situación.",
                "Each mood combines the current soundtrack with DVN references for a screen or situation."), w),
                x, y, SiegeTheme.MUTED, false);
        y += 20;
        var moods = SiegeMediaReferenceData.moods();
        int visible = Math.max(1, (contentY + contentH - y - 12) / 39);
        int start = Math.min(listOffset, Math.max(0, moods.size() - visible));
        for (int i = start; i < moods.size() && i < start + visible; i++) {
            var mood = moods.get(i);
            g.drawString(font, fit(mood.title(spanish()) + "  //  " + mood.bundledTrack(), w), x, y, SiegeTheme.CYAN, false);
            y += 11;
            g.drawString(font, fit(mood.purpose(spanish()), w), x + 8, y, SiegeTheme.MUTED, false);
            y += 11;
            g.drawString(font, fit(label("REFERENCIAS: ", "REFERENCES: ") + String.join(" · ", mood.referenceTracks()), w),
                    x + 8, y, SiegeTheme.GOLD, false);
            y += 17;
        }
        renderScrollState(g, moods.size(), visible);
    }

    private void renderDvnAudio(GuiGraphics g) {
        int x = contentX + 9;
        int y = contentY + 8;
        int w = contentW - 18;
        g.drawString(font, fit(label("MÚSICA DE DUMMIES VS NOOBS QUE ENCAJA CON SIEGE", "DUMMIES VS NOOBS MUSIC THAT FITS SIEGE"), w),
                x, y, SiegeTheme.GOLD, false);
        y += 14;
        g.drawString(font, fit(label(
                "Catálogo de referencia: no cambia la playlist instalada por sí solo.",
                "Reference catalog: it does not change the installed playlist by itself."), w),
                x, y, SiegeTheme.MUTED, false);
        y += 18;
        var tracks = SiegeMediaReferenceData.dvnTracks();
        int visible = Math.max(1, (contentY + contentH - y - 12) / 25);
        int start = Math.min(listOffset, Math.max(0, tracks.size() - visible));
        for (int i = start; i < tracks.size() && i < start + visible; i++) {
            var track = tracks.get(i);
            g.drawString(font, fit(track.title() + "  //  " + track.use().label(spanish()), w), x, y, SiegeTheme.CYAN, false);
            y += 11;
            g.drawString(font, fit(track.note(spanish()), w), x + 8, y, SiegeTheme.MUTED, false);
            y += 14;
        }
        renderScrollState(g, tracks.size(), visible);
    }

    private void renderVisuals(GuiGraphics g) {
        int x = contentX + 9;
        int y = contentY + 8;
        int w = contentW - 18;
        g.drawString(font, fit(label("DIRECCIÓN DE FONDOS DVN", "DVN BACKGROUND DIRECTION"), w), x, y, SiegeTheme.BLUE, false);
        y += 15;
        g.drawString(font, fit(label(
                "Escenas tácticas 16:9, oscuras y legibles; la galería sólo recibe masters de calidad suficiente.",
                "Tactical, dark and readable 16:9 scenes; only sufficiently good masters enter the gallery."), w),
                x, y, SiegeTheme.MUTED, false);
        y += 20;
        var visuals = SiegeMediaReferenceData.visualReferences();
        int visible = Math.max(1, (contentY + contentH - y - 12) / 27);
        int start = Math.min(listOffset, Math.max(0, visuals.size() - visible));
        for (int i = start; i < visuals.size() && i < start + visible; i++) {
            var visual = visuals.get(i);
            g.drawString(font, fit(visual.title(spanish()), w), x, y, SiegeTheme.GOLD, false);
            y += 11;
            g.drawString(font, fit(visual.note(spanish()), w), x + 8, y, SiegeTheme.MUTED, false);
            y += 16;
        }
        renderScrollState(g, visuals.size(), visible);
    }

    private void renderScrollState(GuiGraphics g, int total, int visible) {
        if (total <= visible) return;
        int max = Math.max(0, total - visible);
        int current = Math.min(listOffset, max);
        String text = label("RUEDA: ", "WHEEL: ") + (current + 1) + "–" + Math.min(total, current + visible) + " / " + total;
        g.drawString(font, text, contentX + 9, contentY + contentH - 11, SiegeTheme.MUTED, false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mode == Mode.BUNDLED || mouseX < contentX || mouseX > contentX + contentW
                || mouseY < contentY || mouseY > contentY + contentH) {
            return super.mouseScrolled(mouseX, mouseY, delta);
        }
        int total = switch (mode) {
            case MOODS -> SiegeMediaReferenceData.moods().size();
            case DVN_AUDIO -> SiegeMediaReferenceData.dvnTracks().size();
            case VISUALS -> SiegeMediaReferenceData.visualReferences().size();
            default -> 0;
        };
        listOffset = Math.max(0, Math.min(Math.max(0, total - 1), listOffset + (delta < 0 ? 1 : -1)));
        return true;
    }

    private String tabLabel(Mode value, int width) {
        String full = modeLabel(value);
        if (font.width(full) <= Math.max(8, width - 12)) return full;
        return switch (value) {
            case BUNDLED -> label("ACTUAL", "CURRENT");
            case MOODS -> label("AMBI.", "MOODS");
            case DVN_AUDIO -> "DVN OST";
            case VISUALS -> "DVN BG";
        };
    }
    private String modeLabel(Mode value) {
        return switch (value) {
            case BUNDLED -> label("ACTUAL", "CURRENT");
            case MOODS -> label("AMBIENTES", "MOODS");
            case DVN_AUDIO -> label("MÚSICA DVN", "DVN MUSIC");
            case VISUALS -> label("FONDOS DVN", "DVN VISUALS");
        };
    }
    private int modeAccent(Mode value) {
        return switch (value) {
            case BUNDLED -> SiegeTheme.CYAN;
            case MOODS -> SiegeTheme.GREEN;
            case DVN_AUDIO -> SiegeTheme.GOLD;
            case VISUALS -> SiegeTheme.BLUE;
        };
    }
    private String fit(String text, int px) {
        if (text == null || px <= 0) return "";
        if (font.width(text) <= px) return text;
        return font.plainSubstrByWidth(text, Math.max(1, px - font.width("…"))) + "…";
    }
    private boolean spanish() { return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private String label(String es, String en) { return spanish() ? es : en; }
    @Override public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}
