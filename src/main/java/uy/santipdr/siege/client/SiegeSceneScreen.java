package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

/** Contact sheet and large preview; selection is committed only by Pin. */
public final class SiegeSceneScreen extends Screen {
    private final Screen parent;
    private int index, previousIndex, page;
    private long changedAt;
    private boolean cleanView;
    private SiegeButton pin, auto, clean;
    private SiegeGalleryLayout layout;
    private final List<Thumbnail> thumbnails = new ArrayList<>();

    public SiegeSceneScreen(Screen parent) {
        super(Component.literal("SIEGE"));
        this.parent = parent;
        index = Math.floorMod(Math.max(0, SiegeConfig.selectedScene), SiegeBackgrounds.count());
        previousIndex = index;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        thumbnails.clear();
        layout = SiegeGalleryLayout.of(width, height);
        addAction(0, text("← ANTERIOR", "← PREVIOUS"), b -> step(-1), 0xFF55BFD9);
        addAction(1, text("SIGUIENTE →", "NEXT →"), b -> step(1), 0xFF55BFD9);
        pin = addAction(2, text("FIJAR FONDO", "PIN BACKGROUND"), b -> pinCurrent(), 0xFFD6A94B);
        addAction(3, text("VOLVER", "BACK"), b -> onClose(), 0xFFD65A4B);
        int topWidth = Math.min(128, (width - 24) / 2);
        auto = addRenderableWidget(new SiegeButton(8, 8, topWidth, 20,
                text("ROTACIÓN AUTO", "AUTO ROTATION"), b -> resumeRotation(), 0xFFD6A94B));
        clean = addRenderableWidget(new SiegeButton(width - topWidth - 8, 8, topWidth, 20,
                text("VISTA LIMPIA", "CLEAN VIEW"), b -> toggleCleanView(), 0xFF55BFD9));
        for (int slot = 0; slot < layout.capacity(); slot++) {
            Thumbnail tile = new Thumbnail(layout.tile(slot));
            thumbnails.add(addRenderableWidget(tile));
        }
        page = index / layout.capacity();
        refresh();
        applyVisibility();
    }

    private SiegeButton addAction(int slot, Component label, Button.OnPress action, int color) {
        var r = layout.action(slot);
        return addRenderableWidget(new SiegeButton(r.x(), r.y(), r.w(), r.h(), label, action, color));
    }

    private void refresh() {
        pin.setSelected(SiegeConfig.selectedScene == index);
        pin.setMessage(SiegeConfig.selectedScene == index ? text("FIJADO", "PINNED") : text("FIJAR FONDO", "PIN BACKGROUND"));
        auto.setSelected(SiegeConfig.selectedScene < 0 && SiegeConfig.animatedBackgrounds);
        for (int i = 0; i < thumbnails.size(); i++) {
            Thumbnail tile = thumbnails.get(i);
            tile.scene = page * layout.capacity() + i;
            tile.active = tile.scene < SiegeBackgrounds.count();
            tile.setMessage(tile.active ? Component.literal(SiegeBackgrounds.name(tile.scene, spanish())) : Component.empty());
        }
        for (var child : children()) if (child instanceof AbstractWidget widget)
            widget.setTooltip(Tooltip.create(widget.getMessage()));
        clean.setTooltip(Tooltip.create(text("Ver sin interfaz. Clic o Escape para volver.", "Hide the interface. Click or Escape to return.")));
    }

    private void select(int next) {
        next = Math.floorMod(next, SiegeBackgrounds.count());
        if (next == index) return;
        previousIndex = index;
        index = next;
        changedAt = System.currentTimeMillis();
        page = index / layout.capacity();
        SiegeUiSounds.click();
        refresh();
    }
    private void step(int direction) { select(index + direction); }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        if (cleanView) {
            SiegeBackgrounds.renderPreview(g, width, height, index);
            return;
        }
        g.fill(0, 0, width, height, 0xFF0B0D10);
        var p = layout.preview();
        g.fill(p.x() - 1, p.y() - 1, p.right() + 1, p.bottom() + 1, 0xFF56616A);
        float progress = Math.min(1F, (System.currentTimeMillis() - changedAt) / 260F);
        if (SiegeConfig.reducedMotion || !SiegeConfig.menuEffects) progress = 1F;
        SiegeBackgrounds.renderRegion(g, p.x(), p.y(), p.w(), p.h(), previousIndex, 1F);
        SiegeBackgrounds.renderRegion(g, p.x(), p.y(), p.w(), p.h(), index, progress * progress * (3 - 2 * progress));
        String state = SiegeConfig.selectedScene == index ? label("FIJADO", "PINNED") : label("VISTA PREVIA", "PREVIEW");
        String heading = String.format("%02d / %02d  ·  %s", index + 1, SiegeBackgrounds.count(), SiegeBackgrounds.name(index, spanish()));
        g.drawString(font, font.plainSubstrByWidth(heading, width - 16), 8, 36, 0xFFF0EEE8, false);
        String detail = state + "  ·  " + label("GALERÍA", "GALLERY") + " " + (page + 1) + "/"
                + ((SiegeBackgrounds.count() + layout.capacity() - 1) / layout.capacity());
        g.drawString(font, detail, 8, 49, 0xFFBCA56D, false);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (cleanView) toggleCleanView(); else onClose();
            return true;
        }
        return cleanView || super.keyPressed(key, scanCode, modifiers);
    }
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (delta != 0 && !cleanView && (layout.thumbnails().contains(x, y) || layout.preview().contains(x, y))) {
            step(delta > 0 ? -1 : 1);
            return true;
        }
        return super.mouseScrolled(x, y, delta);
    }
    private void pinCurrent() {
        SiegeUiSounds.click();
        SiegeConfig.selectedScene = index;
        SiegeConfig.save();
        refresh();
    }
    private void resumeRotation() {
        SiegeUiSounds.click();
        SiegeConfig.selectedScene = -1;
        SiegeConfig.animatedBackgrounds = true;
        SiegeConfig.save();
        refresh();
    }
    private void toggleCleanView() {
        cleanView = !cleanView;
        setFocused(null);
        applyVisibility();
        SiegeUiSounds.resetHover();
    }
    private void applyVisibility() {
        for (var child : children()) if (child instanceof AbstractWidget widget) widget.visible = !cleanView;
    }
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (cleanView) {
            if (button == 0) toggleCleanView();
            return true;
        }
        return super.mouseClicked(x, y, button);
    }
    @Override
    public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override
    public boolean isPauseScreen() { return false; }
    private Component text(String es, String en) { return Component.literal(label(es, en)); }
    private String label(String es, String en) { return spanish() ? es : en; }
    private boolean spanish() { return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_"); }

    private final class Thumbnail extends Button {
        private int scene;
        Thumbnail(SiegeGalleryLayout.Rect r) {
            super(r.x(), r.y(), r.w(), r.h(), Component.empty(), b -> {}, DEFAULT_NARRATION);
        }
        @Override
        public void onPress() { select(scene); }
        @Override
        public void playDownSound(SoundManager sounds) { }
        @Override
        protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            if (!active) return;
            int x = getX(), y = getY(), w = getWidth(), h = getHeight();
            int edge = scene == index ? 0xFFF0CE74 : isHoveredOrFocused() ? 0xFFF4EEE0 : 0xFF39434C;
            g.fill(x, y, x + w, y + h, edge);
            SiegeBackgrounds.renderRegion(g, x + 2, y + 2, w - 4, h - 17, scene, 1F);
            g.fill(x + 1, y + h - 15, x + w - 1, y + h - 1, 0xFF14191E);
            g.drawString(font, font.plainSubstrByWidth(getMessage().getString(), w - 8), x + 4, y + h - 12, edge, false);
            if (SiegeConfig.selectedScene == scene) {
                g.fill(x + 3, y + 3, x + 14, y + 14, 0xDD14191E);
                g.drawString(font, "★", x + 4, y + 4, 0xFFF0CE74, false);
            }
        }
    }
}
