package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import uy.santipdr.siege.SiegeMod;

/** Full dossier artwork inspection, with bounded zoom and drag; never modifies assets. */
public final class IntelPortraitScreen extends Screen {
    private final Screen parent;
    private final IntelEntry entry;
    private double zoom = 1, panX, panY;
    private SiegeButton minus, plus;
    private int top = 40, bottom;

    public IntelPortraitScreen(Screen parent, IntelEntry entry) {
        super(Component.literal(entry.name()));
        this.parent = parent;
        this.entry = entry;
    }
    @Override
    protected void init() {
        bottom = height - 35;
        int w = Math.min(110, (width - 28) / 4);
        int x = (width - w * 4 - 12) / 2;
        minus = addRenderableWidget(new SiegeButton(x, height - 27, w, 19, Component.literal("−"), b -> changeZoom(zoom / 1.25), 0xFF55BFD9));
        plus = addRenderableWidget(new SiegeButton(x + w + 4, height - 27, w, 19, Component.literal("+"), b -> changeZoom(zoom * 1.25), 0xFF55BFD9));
        addRenderableWidget(new SiegeButton(x + (w + 4) * 2, height - 27, w, 19,
                text("AJUSTAR", "FIT"), b -> { panX = panY = 0; changeZoom(1); }, 0xFFD6A94B));
        addRenderableWidget(new SiegeButton(x + (w + 4) * 3, height - 27, w, 19,
                text("VOLVER", "BACK"), b -> onClose(), 0xFFD65A4B));
        clampPan();
        refresh();
    }
    private double fit() { return Math.min((width - 16) / 640.0, (bottom - top) / 360.0); }
    private void clampPan() {
        double maxX = Math.max(0, (640 * fit() * zoom - (width - 16)) / 2);
        double maxY = Math.max(0, (360 * fit() * zoom - (bottom - top)) / 2);
        panX = Math.max(-maxX, Math.min(maxX, panX));
        panY = Math.max(-maxY, Math.min(maxY, panY));
    }
    private void changeZoom(double value) {
        zoom = Math.max(1, Math.min(4, value));
        clampPan();
        refresh();
        SiegeUiSounds.click();
    }
    private void refresh() { minus.active = zoom > 1.001; plus.active = zoom < 3.999; }
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        g.fill(0, 0, width, height, 0xFF0B0D10);
        g.drawCenteredString(font, font.plainSubstrByWidth(entry.code() + " · " + entry.name(), width - 16), width / 2, 8, 0xFFE7DFC9);
        String hint = label("Rueda: ampliar · Arrastrar: mover", "Wheel: zoom · Drag: pan");
        g.drawCenteredString(font, font.plainSubstrByWidth(hint + "  ·  " + Math.round(zoom * 100) + "%", width - 16), width / 2, 23, 0xFF9CA7AE);
        String image = entry.image();
        // Inspect a stable frame; animation belongs to the dossier overview.
        ResourceLocation texture = new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/intel/" + image + ".png");
        int w = (int)Math.round(640 * fit() * zoom), h = (int)Math.round(360 * fit() * zoom);
        int x = (width - w) / 2 + (int)panX, y = top + (bottom - top - h) / 2 + (int)panY;
        g.enableScissor(8, top, width - 8, bottom);
        g.blit(texture, x, y, w, h, 0, 0, 640, 360, 640, 360);
        g.disableScissor();
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (y >= top && y < bottom && delta != 0) { changeZoom(zoom * (delta > 0 ? 1.25 : 0.8)); return true; }
        return super.mouseScrolled(x, y, delta);
    }
    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (button == 0 && y >= top && y < bottom && zoom > 1) {
            panX += dx; panY += dy; clampPan(); return true;
        }
        return super.mouseDragged(x, y, button, dx, dy);
    }
    @Override
    public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override
    public boolean isPauseScreen() { return false; }
    private Component text(String es, String en) { return Component.literal(label(es, en)); }
    private String label(String es, String en) { return minecraft.getLanguageManager().getSelected().startsWith("es_") ? es : en; }
}
