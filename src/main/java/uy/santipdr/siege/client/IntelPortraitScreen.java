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
    private SiegeButton minus, plus, fitButton, backgroundButton, mapButton, zoomPresetButton, centerButton;
    private final int top = 64;
    private int bottom;
    private boolean draggingImage, draggingMap;

    public IntelPortraitScreen(Screen parent, IntelEntry entry) {
        super(Component.literal(entry.name()));
        this.parent = parent;
        this.entry = entry;
        texture = SiegePortraits.resolve(entry.image());
    }
    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        draggingImage = draggingMap = false;
        bottom = Math.max(top + 1, height - 35);
        camera.resize(width - 16, bottom - top);
        int w = Math.min(110, (width - 28) / 4);
        int x = (width - w * 4 - 12) / 2;
        minus = addRenderableWidget(new SiegeButton(x, height - 27, w, 19, Component.literal("−"), b -> changeZoom(camera.zoom() / 1.25, width / 2.0, (top + bottom) / 2.0), SiegeTheme.RED));
        plus = addRenderableWidget(new SiegeButton(x + w + 4, height - 27, w, 19, Component.literal("+"), b -> changeZoom(camera.zoom() * 1.25, width / 2.0, (top + bottom) / 2.0), SiegeTheme.RED));
        fitButton = addRenderableWidget(new SiegeButton(x + (w + 4) * 2, height - 27, w, 19,
                text("AJUSTAR", "FIT"), b -> { camera.reset(); refresh(); SiegeUiSounds.click(); }, SiegeTheme.GOLD));
        addRenderableWidget(new SiegeButton(x + (w + 4) * 3, height - 27, w, 19,
                text("VOLVER", "BACK"), b -> onClose(), 0xFFD65A4B));
        int optionW = (width - 28) / 4;
        backgroundButton = addRenderableWidget(new SiegeButton(8, 39, optionW, 18, text(backgroundLabelEs(), backgroundLabelEn()), b -> {
            SiegeConfig.inspectorBackground = (SiegeConfig.inspectorBackground + 1) % 3; SiegeConfig.save(); SiegeUiSounds.click(); refresh();
        }, SiegeTheme.GOLD));
        backgroundButton.setTooltip(Tooltip.create(text("Alternar fondo negro, gris o papel", "Cycle black, gray or paper background")));
        mapButton = addRenderableWidget(new SiegeButton(12 + optionW, 39, optionW, 18, text("MINIMAPA", "MINIMAP"), b -> {
            SiegeConfig.inspectorMap = !SiegeConfig.inspectorMap; SiegeConfig.save();
            ((SiegeButton)b).setSelected(SiegeConfig.inspectorMap); refresh(); SiegeUiSounds.click();
        }, SiegeTheme.RED).setSelected(SiegeConfig.inspectorMap));
        mapButton.withIcon("overview"); backgroundButton.withIcon("image");
        zoomPresetButton = addRenderableWidget(new SiegeButton(16 + optionW * 2, 39, optionW, 18, text("ZOOM 2×", "ZOOM 2×"), b -> {
            changeZoom(camera.zoom() < 1.99 || camera.zoom() >= 3.99 ? 2 : 4, width / 2.0, (top + bottom) / 2.0);
        }, SiegeTheme.RED));
        zoomPresetButton.setTooltip(Tooltip.create(text("Alternar ampliación precisa entre 2× y 4×", "Toggle precise magnification between 2× and 4×")));
        centerButton = addRenderableWidget(new SiegeButton(20 + optionW * 3, 39, optionW, 18, text("CENTRAR", "CENTER"), b -> {
            camera.centerOn(0.5, 0.5); refresh(); SiegeUiSounds.click();
        }, SiegeTheme.RED));
        centerButton.setTooltip(Tooltip.create(text("Centrar sin cambiar el zoom", "Center without changing zoom")));
        fitButton.withIcon("image"); centerButton.withIcon("overview");
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
        centerButton.active = camera.zoom() > 1.001 && (Math.abs(camera.visibleLeft() + camera.visibleRight() - 1) > 0.001
                || Math.abs(camera.visibleTop() + camera.visibleBottom() - 1) > 0.001);
        mapButton.active = width >= 500 && height >= 300;
        mapButton.setTooltip(Tooltip.create(!mapButton.active ? text("El minimapa necesita una ventana más amplia", "The minimap needs a larger window")
                : camera.zoom() <= 1.001 ? text("Aparecerá al ampliar la imagen", "Appears when the image is zoomed")
                : text("Clic o arrastre en el minimapa para desplazarte", "Click or drag the minimap to move")));
        mapButton.withBadge(SiegeConfig.inspectorMap ? label("SÍ", "ON") : label("NO", "OFF"));
        minus.setTooltip(Tooltip.create(minus.active ? text("Alejar la imagen", "Zoom out") : text("La imagen ya está ajustada completa", "The full image already fits")));
        plus.setTooltip(Tooltip.create(plus.active ? text("Ampliar la imagen", "Zoom in") : text("Ampliación máxima: 400%", "Maximum magnification: 400%")));
        zoomPresetButton.setMessage(text(camera.zoom() < 1.99 || camera.zoom() >= 3.99 ? "ZOOM 2×" : "ZOOM 4×",
                camera.zoom() < 1.99 || camera.zoom() >= 3.99 ? "ZOOM 2×" : "ZOOM 4×"));
        backgroundButton.setMessage(text(backgroundLabelEs(), backgroundLabelEn()));
    }
    private boolean hasMap() { return SiegeConfig.inspectorMap && camera.zoom() > 1.001 && width >= 500 && height >= 300; }
    private SiegeGalleryLayout.Rect map() { return new SiegeGalleryLayout.Rect(width - 120, top + 8, 104, 59); }
    private boolean inImage(double x, double y) { return x >= 8 && x < width - 8 && y >= top && y < bottom; }
    private void moveMap(double x, double y) {
        var map = map();
        camera.centerOn((x - map.x()) / map.w(), (y - map.y()) / map.h());
        updateCenterState();
    }
    private void updateCenterState() {
        centerButton.active = camera.zoom() > 1.001 && (Math.abs(camera.visibleLeft() + camera.visibleRight() - 1) > 0.001
                || Math.abs(camera.visibleTop() + camera.visibleBottom() - 1) > 0.001);
    }
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        g.fill(0, 0, width, height, 0xFF0B0D10);
        int accent = IntelPresentation.accent(entry.category());
        g.drawCenteredString(font, font.plainSubstrByWidth(entry.code() + " · " + entry.name(), width - 16), width / 2, 7, 0xFFE7DFC9);
        String hint = camera.zoom() <= 1.001 ? label("IMAGEN COMPLETA · Rueda para ampliar", "FULL IMAGE · Scroll to zoom")
                : draggingImage || draggingMap ? label("MOVIENDO IMAGEN", "PANNING IMAGE")
                : label("ARRASTRÁ PARA EXPLORAR · Rueda para ampliar", "DRAG TO EXPLORE · Scroll to zoom");
        String amount = Math.round(camera.zoom() * 100) + "%";
        g.drawString(font, font.plainSubstrByWidth(hint, width - font.width(amount) - 34), 8, 23, 0xFF9CA7AE, false);
        g.drawString(font, amount, width - font.width(amount) - 8, 23, 0xFFF0CE74, false);
        g.fill(8, 34, width - 8, 35, accent);
        int backdrop = switch (SiegeConfig.inspectorBackground) {
            case 1 -> 0xFF777777; case 2 -> 0xFFE7DFC9; default -> 0xFF08090A;
        };
        g.fill(8, top, width - 8, bottom, backdrop);
        g.fill(8, top, width - 8, top + 1, accent);
        g.fill(8, bottom - 1, width - 8, bottom, accent);
        SiegeTheme.frame(g, 7, top - 1, width - 14, bottom - top + 2,
                draggingImage ? SiegeTheme.GOLD : accent);
        g.enableScissor(8, top, width - 8, bottom);
        g.blit(texture, 8 + (int)Math.round(camera.x()), top + (int)Math.round(camera.y()),
                (int)Math.round(camera.imageWidth()), (int)Math.round(camera.imageHeight()), 0, 0, 640, 360, 640, 360);
        g.disableScissor();
        if (hasMap()) {
            var m = map();
            g.fill(m.x() - 3, m.y() - 3, m.right() + 3, m.bottom() + 3, 0xEE0B0D10);
            g.fill(m.x() - 2, m.y() - 2, m.right() + 2, m.y() - 1, accent);
            g.blit(texture, m.x(), m.y(), m.w(), m.h(), 0, 0, 640, 360, 640, 360);
            int x1 = m.x() + (int)(camera.visibleLeft() * m.w());
            int x2 = m.x() + (int)(camera.visibleRight() * m.w());
            int y1 = m.y() + (int)(camera.visibleTop() * m.h());
            int y2 = m.y() + (int)(camera.visibleBottom() * m.h());
            g.fill(m.x(), m.y(), m.right(), y1, 0x88000000);
            g.fill(m.x(), y2, m.right(), m.bottom(), 0x88000000);
            g.fill(m.x(), y1, x1, y2, 0x88000000);
            g.fill(x2, y1, m.right(), y2, 0x88000000);
            SiegeTheme.frame(g, m.x() - 2, m.y() - 2, m.w() + 4, m.h() + 4,
                    draggingMap || m.contains(mouseX, mouseY) ? SiegeTheme.GOLD : accent);
            g.fill(x1, y1, x2, y1 + 1, 0xFFFFD978);
            g.fill(x1, y2 - 1, x2, y2, 0xFFFFD978);
            g.fill(x1, y1, x1 + 1, y2, 0xFFFFD978);
            g.fill(x2 - 1, y1, x2, y2, 0xFFFFD978);
            int cx = (x1 + x2) / 2;
            int cy = (y1 + y2) / 2;
            g.fill(cx - 2, cy, cx + 3, cy + 1, accent);
            g.fill(cx, cy - 2, cx + 1, cy + 3, accent);
        }
        int zoomBar = Math.max(1, Math.round((float)((camera.zoom() - 1) / 3.0) * (width - 16)));
        g.fill(8, bottom + 2, width - 8, bottom + 3, 0xFF494044);
        if (camera.zoom() > 1.001) g.fill(8, bottom + 2, 8 + zoomBar, bottom + 3, accent);
        for (int mark = 0; mark < 4; mark++) {
            int mx = 8 + (width - 17) * mark / 3;
            g.fill(mx, bottom + 1, mx + 1, bottom + 4, SiegeTheme.MUTED);
        }
        super.render(g, mouseX, mouseY, partialTick);
        if (mouseY >= 5 && mouseY < 19 && font.width(entry.code() + " · " + entry.name()) > width - 16)
            g.renderTooltip(font, Component.literal(entry.code() + " · " + entry.name()), mouseX, mouseY);
        else if (mouseY >= 20 && mouseY < 34 && mouseX >= width - font.width(amount) - 12)
            g.renderTooltip(font, text("Ampliación respecto del ajuste completo (100–400%)", "Magnification relative to full fit (100–400%)"), mouseX, mouseY);
        SiegeUiSounds.updateHover(children());
    }
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (inImage(x, y) && Double.isFinite(delta) && delta != 0) {
            // The minimap is an overview, not the point in the artwork beneath it.
            boolean overMap = hasMap() && map().contains(x, y);
            changeZoom(camera.zoom() * Math.pow(1.25, Math.max(-4, Math.min(4, delta))), overMap ? width / 2.0 : x, overMap ? (top + bottom) / 2.0 : y);
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
        if (button == 0 && draggingImage) { camera.drag(dx, dy); updateCenterState(); return true; }
        return super.mouseDragged(x, y, button, dx, dy);
    }
    @Override
    public boolean mouseReleased(double x, double y, int button) {
        boolean handled = button == 0 && (draggingImage || draggingMap);
        if (button == 0) { draggingImage = draggingMap = false; }
        return super.mouseReleased(x, y, button) || handled;
    }
    @Override
    public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override
    public boolean isPauseScreen() { return false; }
    private Component text(String es, String en) { return Component.literal(label(es, en)); }
    private String label(String es, String en) { return minecraft.getLanguageManager().getSelected().startsWith("es_") ? es : en; }
    private String backgroundLabelEs() {
        return switch (SiegeConfig.inspectorBackground) { case 1 -> "FONDO GRIS"; case 2 -> "FONDO PAPEL"; default -> "FONDO NEGRO"; };
    }
    private String backgroundLabelEn() {
        return switch (SiegeConfig.inspectorBackground) { case 1 -> "GRAY BACKGROUND"; case 2 -> "PAPER BACKGROUND"; default -> "BLACK BACKGROUND"; };
    }
}

