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
    private boolean cleanView, menuPreview, undoAvailable;
    private boolean containPreview = true;
    private int undoScene;
    private boolean undoAnimated;
    private SiegeButton framing, contrast, undo, current;
    private SiegeButton pin, auto, clean, previousPage, nextPage;
    private SiegeGalleryLayout layout;
    private final List<Thumbnail> thumbnails = new ArrayList<>();

    public SiegeSceneScreen(Screen parent) {
        super(Component.literal("SIEGE"));
        this.parent = parent;
        index = SiegeBackgrounds.currentIndex(System.currentTimeMillis());
        previousIndex = index;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        thumbnails.clear();
        int firstVisible = layout == null ? index : page * layout.capacity();
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
        previousPage = addRenderableWidget(new SiegeButton(width - 60, 36, 24, 20,
                Component.literal("←"), b -> changePage(-1), 0xFF55BFD9));
        nextPage = addRenderableWidget(new SiegeButton(width - 32, 36, 24, 20,
                Component.literal("→"), b -> changePage(1), 0xFF55BFD9));
        int optionWidth = (width - 28) / 4;
        framing = addRenderableWidget(new SiegeButton(8, 65, optionWidth, 18, text("VER COMPLETO", "FULL VIEW"), b -> {
            toggleCleanView();
        }, 0xFF55BFD9));
        contrast = addRenderableWidget(new SiegeButton(12 + optionWidth, 65, optionWidth, 18, text("CONTRASTE", "CONTRAST"), b -> {
            menuPreview = !menuPreview; refresh(); SiegeUiSounds.click();
        }, 0xFF55BFD9));
        undo = addRenderableWidget(new SiegeButton(16 + optionWidth * 2, 65, optionWidth, 18, text("DESHACER", "UNDO"), b -> {
            if (undoAvailable) {
                SiegeConfig.selectedScene = undoScene; SiegeConfig.animatedBackgrounds = undoAnimated;
                SiegeConfig.save(); undoAvailable = false; refresh(); SiegeUiSounds.click();
            }
        }, 0xFFD6A94B));
        current = addRenderableWidget(new SiegeButton(20 + optionWidth * 3, 65, optionWidth, 18, text("ACTUAL", "CURRENT"),
                b -> select(SiegeBackgrounds.currentIndex(System.currentTimeMillis())), 0xFFD6A94B));
        current.setTooltip(Tooltip.create(text("Ver el fondo que está usando el menú", "Show the background currently used by the menu")));
        for (int slot = 0; slot < layout.capacity(); slot++) {
            Thumbnail tile = new Thumbnail(layout.tile(slot));
            thumbnails.add(addRenderableWidget(tile));
        }
        page = Math.min((SiegeBackgrounds.count() - 1) / layout.capacity(), firstVisible / layout.capacity());
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
        current.setTooltip(Tooltip.create(text("Ver el fondo que está usando el menú", "Show the background currently used by the menu")));
        framing.setSelected(containPreview);
        framing.setTooltip(Tooltip.create(text("Mostrar la imagen completa en vez de recortarla", "Show the full image instead of cropping it")));
        contrast.setSelected(menuPreview);
        contrast.setTooltip(Tooltip.create(text("Previsualizar la oscuridad de fondo y panel del menú", "Preview the menu background and panel darkness")));
        undo.active = undoAvailable;
        previousPage.active = page > 0;
        nextPage.active = (page + 1) * layout.capacity() < SiegeBackgrounds.count();
        previousPage.setTooltip(Tooltip.create(text("Página anterior de miniaturas", "Previous thumbnail page")));
        nextPage.setTooltip(Tooltip.create(text("Página siguiente de miniaturas", "Next thumbnail page")));
        clean.setTooltip(Tooltip.create(text("Ver sin interfaz. Clic o Escape para volver.", "Hide the interface. Click or Escape to return.")));
    }

    private void select(int next) {
        next = Math.floorMod(next, SiegeBackgrounds.count());
        if (next == index) { page = index / layout.capacity(); refresh(); return; }
        previousIndex = index;
        index = next;
        changedAt = System.currentTimeMillis();
        page = index / layout.capacity();
        SiegeUiSounds.click();
        refresh();
    }
    private void step(int direction) { select(index + direction); }
    private void changePage(int direction) {
        int lastPage = (SiegeBackgrounds.count() - 1) / layout.capacity();
        int next = Math.max(0, Math.min(lastPage, page + direction));
        if (next == page) return;
        page = next;
        refresh();
        SiegeUiSounds.click();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        if (cleanView) {
            g.fill(0, 0, width, height, 0xFF0B0D10);
            renderPreviewImage(g, 0, 0, width, height, index, 1F);
            renderContrast(g, 0, 0, width, height);
            return;
        }
        g.fill(0, 0, width, height, 0xFF0B0D10);
        var p = layout.preview();
        g.fill(p.x() - 1, p.y() - 1, p.right() + 1, p.bottom() + 1, 0xFF56616A);
        float progress = Math.min(1F, (System.currentTimeMillis() - changedAt) / 260F);
        if (SiegeConfig.reducedMotion || !SiegeConfig.menuEffects) progress = 1F;
        g.fill(p.x(), p.y(), p.right(), p.bottom(), 0xFF0B0D10);
        if (progress < 1F) renderPreviewImage(g, p.x(), p.y(), p.w(), p.h(), previousIndex, 1F);
        renderPreviewImage(g, p.x(), p.y(), p.w(), p.h(), index, progress * progress * (3 - 2 * progress));
        renderContrast(g, p.x(), p.y(), p.w(), p.h());
        String state = SiegeConfig.selectedScene == index ? label("FIJADO", "PINNED") : label("VISTA PREVIA", "PREVIEW");
        String heading = String.format("%02d / %02d  ·  %s", index + 1, SiegeBackgrounds.count(), SiegeBackgrounds.name(index, spanish()));
        g.drawString(font, font.plainSubstrByWidth(heading, width - 82), 8, 36, 0xFFF0EEE8, false);
        String detail = state + "  ·  " + label("GALERÍA", "GALLERY") + " " + (page + 1) + "/"
                + ((SiegeBackgrounds.count() + layout.capacity() - 1) / layout.capacity());
        g.drawString(font, font.plainSubstrByWidth(detail, width - 82), 8, 49, 0xFFBCA56D, false);
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
        if (delta != 0 && !cleanView && layout.thumbnails().contains(x, y)) {
            changePage(delta > 0 ? -1 : 1);
            return true;
        }
        if (delta != 0 && !cleanView && layout.preview().contains(x, y)) {
            step(delta > 0 ? -1 : 1);
            return true;
        }
        return super.mouseScrolled(x, y, delta);
    }
    private void renderPreviewImage(GuiGraphics g, int x, int y, int w, int h, int scene, float alpha) {
        if (containPreview) SiegeBackgrounds.renderContainedRegion(g, x, y, w, h, scene, alpha);
        else SiegeBackgrounds.renderRegion(g, x, y, w, h, scene, alpha);
    }
    private void renderContrast(GuiGraphics g, int x, int y, int w, int h) {
        if (!menuPreview) return;
        g.fill(x, y, x + w, y + h, (SiegeConfig.backgroundDarkness * 255 / 100) << 24);
        g.fill(x, y, x + w / 4, y + h, (SiegeConfig.panelDarkness * 255 / 100) << 24);
    }
    private void rememberBackground() {
        undoScene = SiegeConfig.selectedScene; undoAnimated = SiegeConfig.animatedBackgrounds; undoAvailable = true;
    }
    private void pinCurrent() {
        SiegeUiSounds.click();
        if (SiegeConfig.selectedScene == index) return;
        rememberBackground();
        SiegeConfig.selectedScene = index;
        SiegeConfig.save();
        refresh();
    }
    private void resumeRotation() {
        SiegeUiSounds.click();
        if (SiegeConfig.selectedScene < 0 && SiegeConfig.animatedBackgrounds) return;
        rememberBackground();
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
