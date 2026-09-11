package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import uy.santipdr.siege.SiegeMod;

/** Original-art inspector with pointer-centered zoom and an interactive overview map. */
public final class IntelPortraitScreen extends Screen {
    private final Screen parent;
    private final IntelEntry entry;
    private final SiegeImageViewport camera = new SiegeImageViewport();
    private final ResourceLocation texture;
    private SiegeButton minus, plus, fitButton;
    private final int top = 40;
    private int bottom;
    private boolean draggingImage, draggingMap;

    public IntelPortraitScreen(Screen parent, IntelEntry entry) {
        super(Component.literal(entry.name()));
        this.parent = parent;
        this.entry = entry;
        texture = new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/intel/" + entry.image() + ".png");
    }
    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        draggingImage = draggingMap = false;
        bottom = height - 35;
        camera.resize(width - 16, bottom - top);
        int w = Math.min(110, (width - 28) / 4);
        int x = (width - w * 4 - 12) / 2;
        minus = addRenderableWidget(new SiegeButton(x, height - 27, w, 19, Component.literal("−"), b -> changeZoom(camera.zoom() / 1.25, width / 2.0, (top + bottom) / 2.0), 0xFF55BFD9));
        plus = addRenderableWidget(new SiegeButton(x + w + 4, height - 27, w, 19, Component.literal("+"), b -> changeZoom(camera.zoom() * 1.25, width / 2.0, (top + bottom) / 2.0), 0xFF55BFD9));
        fitButton = addRenderableWidget(new SiegeButton(x + (w + 4) * 2, height - 27, w, 19,
                text("AJUSTAR", "FIT"), b -> { camera.reset(); refresh(); SiegeUiSounds.click(); }, 0xFFD6A94B));
        addRenderableWidget(new SiegeButton(x + (w + 4) * 3, height - 27, w, 19,
                text("VOLVER", "BACK"), b -> onClose(), 0xFFD65A4B));
        minus.setTooltip(Tooltip.create(text("Alejar la imagen", "Zoom out")));
        plus.setTooltip(Tooltip.create(text("Ampliar la imagen", "Zoom in")));
        fitButton.setTooltip(Tooltip.create(text("Centrar y mostrar el expediente completo", "Center and show the complete artwork")));
        refresh();
    }
    private void changeZoom(double value, double x, double y) {
        if (camera.zoomAt(value, x - 8, y - top)) SiegeUiSounds.click();
        refresh();
    }
    private void refresh() {
        minus.active = camera.zoom() > 1.001;
        plus.active = camera.zoom() < 3.999;
        fitButton.active = camera.zoom() > 1.001;
    }
    private boolean hasMap() { return camera.zoom() > 1.001 && width >= 500 && height >= 300; }
    private SiegeGalleryLayout.Rect map() { return new SiegeGalleryLayout.Rect(width - 120, top + 8, 104, 59); }
    private boolean inImage(double x, double y) { return x >= 8 && x < width - 8 && y >= top && y < bottom; }
    private void moveMap(double x, double y) {
        var map = map();
        camera.centerOn((x - map.x()) / map.w(), (y - map.y()) / map.h());
    }
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        g.fill(0, 0, width, height, 0xFF0B0D10);
        g.drawCenteredString(font, font.plainSubstrByWidth(entry.code() + " · " + entry.name(), width - 16), width / 2, 8, 0xFFE7DFC9);
        String hint = label("Rueda: ampliar · Arrastrar: mover", "Wheel: zoom · Drag: pan");
        String amount = Math.round(camera.zoom() * 100) + "%";
        g.drawString(font, font.plainSubstrByWidth(hint, width - font.width(amount) - 34), 8, 23, 0xFF9CA7AE, false);
        g.drawString(font, amount, width - font.width(amount) - 8, 23, 0xFFF0CE74, false);
        g.enableScissor(8, top, width - 8, bottom);
        g.blit(texture, 8 + (int)Math.round(camera.x()), top + (int)Math.round(camera.y()),
                (int)Math.round(camera.imageWidth()), (int)Math.round(camera.imageHeight()), 0, 0, 640, 360, 640, 360);
        g.disableScissor();
        if (hasMap()) {
            var m = map();
            g.fill(m.x() - 3, m.y() - 3, m.right() + 3, m.bottom() + 3, 0xEE0B0D10);
            g.blit(texture, m.x(), m.y(), m.w(), m.h(), 0, 0, 640, 360, 640, 360);
            int x1 = m.x() + (int)(camera.visibleLeft() * m.w());
            int x2 = m.x() + (int)(camera.visibleRight() * m.w());
            int y1 = m.y() + (int)(camera.visibleTop() * m.h());
            int y2 = m.y() + (int)(camera.visibleBottom() * m.h());
            g.fill(x1, y1, x2, y1 + 1, 0xFFFFD978);
            g.fill(x1, y2 - 1, x2, y2, 0xFFFFD978);
            g.fill(x1, y1, x1 + 1, y2, 0xFFFFD978);
            g.fill(x2 - 1, y1, x2, y2, 0xFFFFD978);
        }
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (inImage(x, y) && delta != 0) {
            // The minimap is an overview, not the point in the artwork beneath it.
            boolean overMap = hasMap() && map().contains(x, y);
            changeZoom(camera.zoom() * (delta > 0 ? 1.25 : 0.8), overMap ? width / 2.0 : x, overMap ? (top + bottom) / 2.0 : y);
            return true;
        }
        return super.mouseScrolled(x, y, delta);
    }
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0 && hasMap() && map().contains(x, y)) {
            draggingMap = true;
            moveMap(x, y);
            return true;
        }
        if (button == 0 && inImage(x, y) && camera.zoom() > 1) {
            draggingImage = true;
            return true;
        }
        return super.mouseClicked(x, y, button);
    }
    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (button == 0 && draggingMap) { moveMap(x, y); return true; }
        if (button == 0 && draggingImage) { camera.drag(dx, dy); return true; }
        return super.mouseDragged(x, y, button, dx, dy);
    }
    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (button == 0) { draggingImage = draggingMap = false; }
        return super.mouseReleased(x, y, button);
    }
    @Override
    public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override
    public boolean isPauseScreen() { return false; }
    private Component text(String es, String en) { return Component.literal(label(es, en)); }
    private String label(String es, String en) { return minecraft.getLanguageManager().getSelected().startsWith("es_") ? es : en; }
}
