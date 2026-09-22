package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** SIEGE 4.0 soundtrack, gallery and DVN visual-direction room. */
public final class SiegeMediaRoomScreen extends Screen {
    private enum Mode { SOUNDTRACK, DVN_MUSIC, VISUALS }

    private final Screen parent;
    private Mode mode = Mode.SOUNDTRACK;
    private int panelX, panelY, panelW, panelH, contentX, contentY, contentW, contentH;
    private int scroll;
    private boolean compact;

    public SiegeMediaRoomScreen(Screen parent) {
        super(Component.literal("SIEGE // MEDIA ROOM"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        compact = width < 660 || height < 390;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 38;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 18 : 24));

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 88, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));
        int galleryW = compact ? 78 : 112;
        addRenderableWidget(new SiegeButton(Math.max(8, width - galleryW - 8), 7, galleryW, 19,
                Component.literal(label("GALERÍA", "GALLERY")),
                b -> minecraft.setScreen(new SiegeSceneScreen(this)), SiegeTheme.BLUE)
                .withIcon("image").setCompactCenter(true));

        int tabY = panelY + 42;
        int tabX = panelX + 10;
        int gap = 4;
        int tabW = panelW - 20;
        int cell = Math.max(60, (tabW - gap * 2) / 3);
        for (int i = 0; i < Mode.values().length; i++) {
            Mode value = Mode.values()[i];
            int x = tabX + i * (cell + gap);
            int right = i == 2 ? panelX + panelW - 10 : x + cell;
            addRenderableWidget(new SiegeButton(x, tabY, Math.max(40, right - x), 19,
                    Component.literal(modeLabel(value)), b -> switchMode(value), modeAccent(value))
                    .setCompactCenter(true).setSelected(value == mode));
        }

        contentX = panelX + 10;
        contentY = tabY + 29;
        contentW = panelW - 20;
        contentH = Math.max(80, panelY + panelH - contentY - 10);
        if (mode == Mode.SOUNDTRACK) initSoundtrackControls();
    }

    private void switchMode(Mode next) {
        if (mode == next) return;
        mode = next;
        scroll = 0;
        SiegeUiSounds.category();
        rebuildWidgets();
    }

    private void initSoundtrackControls() {
        int gap = 4;
        int y = contentY + 50;
        int w = Math.max(44, (contentW - gap * 2) / 3);
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

        int sceneY = y + 50;
        addRenderableWidget(new SiegeButton(contentX, sceneY, w, 19, Component.literal(label("◀ FONDO", "◀ SCENE")),
                b -> shiftScene(-1), SiegeTheme.BLUE).withIcon("image").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(contentX + w + gap, sceneY, w, 19, Component.literal(label("ROTACIÓN", "ROTATION")),
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
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mode != Mode.SOUNDTRACK && mouseX >= contentX && mouseX <= contentX + contentW
                && mouseY >= contentY && mouseY <= contentY + contentH) {
            int max = mode == Mode.DVN_MUSIC
                    ? Math.max(0, SiegeMediaReferenceData.dvnTracks().size() * 31 - contentH + 52)
                    : Math.max(0, SiegeMediaReferenceData.visualReferences().size() * 34 - contentH + 56);
            scroll = Math.max(0, Math.min(max, scroll - (int)Math.signum(delta) * 18));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xCA000000 : 0xA0000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, SiegeTheme.CYAN);
        g.drawString(font, fit(label("SALA MULTIMEDIA", "MEDIA ROOM") + " // " + SiegeRuntimeStatus.version(), panelW - 24),
                panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Soundtrack, fondos, galería y referencias visuales de la identidad SIEGE / Dummies vs Noobs.",
                "Soundtrack, backgrounds, gallery and SIEGE / Dummies vs Noobs visual references."), panelW - 24),
                panelX + 12, panelY + 22, SiegeTheme.MUTED, false);

        SiegeTheme.panel(g, contentX - 3, contentY - 3, contentW + 6, contentH + 6, modeAccent(mode));
        switch (mode) {
            case SOUNDTRACK -> renderSoundtrack(g);
            case DVN_MUSIC -> renderDvnMusic(g);
            case VISUALS -> renderVisuals(g);
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private void renderSoundtrack(GuiGraphics g) {
        int x = contentX + 9;
        int y = contentY + 8;
        int w = contentW - 18;
        g.drawString(font, fit(label("PISTA ACTUAL: ", "CURRENT TRACK: ") + SiegeMusic.currentTrackName(), w),
                x, y, SiegeTheme.GOLD, false);
        y += 12;
        g.drawString(font, fit(SiegeMusic.transitionLabel(spanish()) + " · "
                + SiegeMusic.currentTrackNumber() + "/" + SiegeMusic.trackNames().size(), w),
                x, y, SiegeTheme.MUTED, false);
        y += 12;
        g.fill(x, y, x + w, y + 3, 0xFF272C30);
        g.fill(x, y, x + Math.round(w * SiegeMusic.currentProgress()), y + 3, SiegeTheme.GOLD);

        int sceneY = contentY + 82;
        int scene = SiegeBackgrounds.currentIndex(System.currentTimeMillis());
        g.drawString(font, fit(label("FONDO: ", "BACKGROUND: ") + SiegeBackgrounds.name(scene, spanish())
                + " · " + SiegeBackgrounds.sceneTag(scene, spanish()), w), x, sceneY, SiegeTheme.CYAN, false);
        g.drawString(font, fit(SiegeBackgrounds.rotationState(spanish(), System.currentTimeMillis()), w),
                x, sceneY + 12, SiegeTheme.MUTED, false);

        int listY = contentY + 119;
        if (listY + 10 < contentY + contentH) {
            g.drawString(font, label("PLAYLIST SIEGE", "SIEGE PLAYLIST"), x, listY, SiegeTheme.INK, false);
            int yy = listY + 14;
            for (String name : SiegeMusic.trackNames()) {
                if (yy + font.lineHeight > contentY + contentH - 6) break;
                g.drawString(font, "• " + name, x, yy,
                        name.equals(SiegeMusic.currentTrackName()) ? SiegeTheme.GOLD : SiegeTheme.MUTED, false);
                yy += 11;
            }
        }
    }

    private void renderDvnMusic(GuiGraphics g) {
        int x = contentX + 9;
        int y = contentY + 8 - scroll;
        int w = contentW - 18;
        g.enableScissor(contentX, contentY, contentX + contentW, contentY + contentH);
        g.drawString(font, label("PISTAS QUE ENCAJAN CON LA IDENTIDAD DVN", "TRACKS THAT FIT THE DVN IDENTITY"),
                x, y, SiegeTheme.GOLD, false);
        y += 17;
        for (SiegeMediaReferenceData.Track track : SiegeMediaReferenceData.dvnTracks()) {
            g.drawString(font, fit(track.title() + "  //  " + track.use().label(spanish()), w), x, y, SiegeTheme.CYAN, false);
            y += 12;
            for (FormattedCharSequence line : font.split(Component.literal(track.note(spanish())), w - 8)) {
                g.drawString(font, line, x + 8, y, SiegeTheme.MUTED, false);
                y += font.lineHeight + 1;
            }
            y += 8;
        }
        g.disableScissor();
    }

    private void renderVisuals(GuiGraphics g) {
        int x = contentX + 9;
        int y = contentY + 8 - scroll;
        int w = contentW - 18;
        g.enableScissor(contentX, contentY, contentX + contentW, contentY + contentH);
        g.drawString(font, label("DIRECCIÓN VISUAL PARA NUEVOS FONDOS", "VISUAL DIRECTION FOR NEW BACKGROUNDS"),
                x, y, SiegeTheme.BLUE, false);
        y += 14;
        for (FormattedCharSequence line : font.split(Component.literal(label(
                "Objetivo: escenas 16:9, nítidas y amplias, con espacio para leer la interfaz sin tapar la acción.",
                "Goal: sharp 16:9 wide scenes with enough space to read the UI without covering the action.")), w)) {
            g.drawString(font, line, x, y, SiegeTheme.MUTED, false);
            y += font.lineHeight + 1;
        }
        y += 8;
        for (SiegeMediaReferenceData.Visual visual : SiegeMediaReferenceData.visualReferences()) {
            g.drawString(font, fit(visual.title(spanish()), w), x, y, SiegeTheme.GOLD, false);
            y += 12;
            for (FormattedCharSequence line : font.split(Component.literal(visual.note(spanish())), w - 8)) {
                g.drawString(font, line, x + 8, y, SiegeTheme.MUTED, false);
                y += font.lineHeight + 1;
            }
            y += 8;
        }
        g.disableScissor();
    }

    private String modeLabel(Mode value) {
        return switch (value) {
            case SOUNDTRACK -> label("SOUNDTRACK", "SOUNDTRACK");
            case DVN_MUSIC -> label("MÚSICA DVN", "DVN MUSIC");
            case VISUALS -> label("FONDOS DVN", "DVN VISUALS");
        };
    }

    private int modeAccent(Mode value) {
        return switch (value) {
            case SOUNDTRACK -> SiegeTheme.CYAN;
            case DVN_MUSIC -> SiegeTheme.GOLD;
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
    @Override public void onClose() { SiegeUiSounds.back(); if (minecraft != null) minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}
