package uy.santipdr.siege.client;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Background/soundtrack command surface plus reference-only DVN media notes. */
public final class SiegeMediaRoomScreen extends Screen {
    private enum Mode { BUNDLED, DVN_AUDIO, VISUALS }

    private final Screen parent;
    private Mode mode = Mode.BUNDLED;
    private int panelX, panelY, panelW, panelH, contentX, contentY, contentW, contentH;
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
        int cell = (tabW - gap * 2) / 3;
        for (int i = 0; i < Mode.values().length; i++) {
            Mode value = Mode.values()[i];
            int x = tabX + i * (cell + gap);
            int w = i == 2 ? panelX + panelW - 10 - x : cell;
            addRenderableWidget(new SiegeButton(x, tabY, w, 19, Component.literal(modeLabel(value)),
                    b -> switchMode(value), modeAccent(value)).setCompactCenter(true).setSelected(value == mode));
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
        addRenderableWidget(new SiegeButton(contentX + w + gap, sceneY, w, 19, Component.literal(label("ROTACIÓN AUTO", "AUTO ROTATE")),
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
        g.drawString(font, label("SALA MULTIMEDIA", "MEDIA ROOM") + " // " + SiegeRuntimeStatus.version(),
                panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Controla recursos ya incluidos y separa recomendaciones externas de los archivos que realmente distribuye el mod.",
                "Control bundled resources while keeping external recommendations separate from files actually distributed by the mod."), panelW - 24),
                panelX + 12, panelY + 21, SiegeTheme.MUTED, false);

        SiegeTheme.panel(g, contentX - 3, contentY - 3, contentW + 6, contentH + 6, modeAccent(mode));
        switch (mode) {
            case BUNDLED -> renderBundled(g);
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
        String track = label("PISTA: ", "TRACK: ") + SiegeMusic.currentTrackName();
        g.drawString(font, fit(track, w), x, y, SiegeTheme.GOLD, false);
        y += 12;
        String progress = label("ESTADO: ", "STATE: ") + SiegeMusic.transitionLabel(spanish())
                + " · " + SiegeMusic.currentTrackNumber() + "/" + SiegeMusic.trackNames().size();
        g.drawString(font, fit(progress, w), x, y, SiegeTheme.MUTED, false);
        y += 12;
        int barW = Math.max(10, w);
        g.fill(x, y, x + barW, y + 3, 0xFF272C30);
        g.fill(x, y, x + Math.round(barW * SiegeMusic.currentProgress()), y + 3, SiegeTheme.GOLD);

        int sceneY = contentY + 86;
        int scene = SiegeBackgrounds.currentIndex(System.currentTimeMillis());
        g.drawString(font, fit(label("ESCENA: ", "SCENE: ") + SiegeBackgrounds.name(scene, spanish())
                + " · " + SiegeBackgrounds.sceneTag(scene, spanish()), w), x, sceneY, SiegeTheme.CYAN, false);
        g.drawString(font, fit(SiegeBackgrounds.rotationState(spanish(), System.currentTimeMillis()), w),
                x, sceneY + 12, SiegeTheme.MUTED, false);

        int listY = contentY + 127;
        if (listY + 10 < contentY + contentH) {
            g.drawString(font, label("PISTAS INCLUIDAS", "BUNDLED TRACKS"), x, listY, SiegeTheme.INK, false);
            int yy = listY + 13;
            for (String name : SiegeMusic.trackNames()) {
                if (yy + font.lineHeight > contentY + contentH - 6) break;
                g.drawString(font, "• " + name, x, yy, name.equals(SiegeMusic.currentTrackName()) ? SiegeTheme.GOLD : SiegeTheme.MUTED, false);
                yy += 11;
            }
        }
    }

    private void renderDvnAudio(GuiGraphics g) {
        int x = contentX + 9;
        int y = contentY + 8;
        int w = contentW - 18;
        g.drawString(font, label("REFERENCIAS DE SOUNDTRACK DVN — NO INCLUIDAS", "DVN SOUNDTRACK REFERENCES — NOT BUNDLED"),
                x, y, SiegeTheme.GOLD, false);
        y += 14;
        g.drawString(font, fit(label(
                "Se muestran como ideas para futuras importaciones. No se descargan ni redistribuyen automáticamente sin derechos claros.",
                "Shown as ideas for future imports. They are not automatically downloaded or redistributed without clear rights."), w),
                x, y, SiegeTheme.MUTED, false);
        y += 18;
        for (SiegeMediaReferenceData.Track track : SiegeMediaReferenceData.dvnTracks()) {
            if (y + 22 > contentY + contentH) break;
            g.drawString(font, fit(track.title() + "  //  " + track.use().label(spanish()), w), x, y, SiegeTheme.CYAN, false);
            y += 11;
            g.drawString(font, fit(track.note(spanish()), w), x + 8, y, SiegeTheme.MUTED, false);
            y += 14;
        }
    }

    private void renderVisuals(GuiGraphics g) {
        int x = contentX + 9;
        int y = contentY + 8;
        int w = contentW - 18;
        g.drawString(font, label("DIRECCIÓN VISUAL DVN PARA FUTUROS FONDOS", "DVN VISUAL DIRECTION FOR FUTURE BACKGROUNDS"),
                x, y, SiegeTheme.BLUE, false);
        y += 15;
        g.drawString(font, fit(label(
                "Regla 4.0: 16:9, preferentemente 1920×1080 o más, sin deformar, y con contraste comprobado contra la UI.",
                "4.0 rule: 16:9, preferably 1920×1080 or higher, no distortion, and contrast checked against the UI."), w),
                x, y, SiegeTheme.MUTED, false);
        y += 20;
        for (SiegeMediaReferenceData.Visual visual : SiegeMediaReferenceData.visualReferences()) {
            if (y + 24 > contentY + contentH) break;
            g.drawString(font, fit(visual.title(spanish()), w), x, y, SiegeTheme.GOLD, false);
            y += 11;
            g.drawString(font, fit(visual.note(spanish()), w), x + 8, y, SiegeTheme.MUTED, false);
            y += 16;
        }
    }

    private String modeLabel(Mode value) {
        return switch (value) {
            case BUNDLED -> label("INCLUIDO", "BUNDLED");
            case DVN_AUDIO -> label("MÚSICA DVN", "DVN MUSIC");
            case VISUALS -> label("FONDOS DVN", "DVN VISUALS");
        };
    }

    private int modeAccent(Mode value) {
        return switch (value) {
            case BUNDLED -> SiegeTheme.CYAN;
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
