package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/** Local gallery: browsing never changes the configured background until Pin is pressed. */
public final class SiegeSceneScreen extends Screen {
    private final Screen parent;
    private int index;
    private boolean cleanView;
    private SiegeButton pin;

    public SiegeSceneScreen(Screen parent) {
        super(Component.literal("SIEGE"));
        this.parent = parent;
        index = Math.max(0, SiegeConfig.selectedScene);
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        int gap = 4;
        int buttonWidth = Math.min(128, (width - 28) / 4);
        int left = (width - buttonWidth * 4 - gap * 3) / 2;
        int y = height - 32;
        addRenderableWidget(new SiegeButton(left, y, buttonWidth, 22,
                text("← ANTERIOR", "← PREVIOUS"), b -> step(-1), 0xFF55BFD9));
        addRenderableWidget(new SiegeButton(left + buttonWidth + gap, y, buttonWidth, 22,
                text("SIGUIENTE →", "NEXT →"), b -> step(1), 0xFF55BFD9));
        pin = addRenderableWidget(new SiegeButton(left + (buttonWidth + gap) * 2, y, buttonWidth, 22,
                text("FIJAR FONDO", "PIN BACKGROUND"), b -> {
            SiegeUiSounds.click();
            SiegeConfig.selectedScene = index;
            SiegeConfig.save();
            refreshPin();
        }, 0xFFD6A94B));
        addRenderableWidget(new SiegeButton(left + (buttonWidth + gap) * 3, y, buttonWidth, 22,
                text("VOLVER", "BACK"), b -> onClose(), 0xFFD65A4B));
        for (var child : children()) if (child instanceof AbstractWidget widget) {
            widget.setTooltip(Tooltip.create(widget.getMessage()));
            widget.visible = !cleanView;
        }
        refreshPin();
    }

    private void refreshPin() {
        pin.setSelected(SiegeConfig.selectedScene == index);
        pin.setMessage(SiegeConfig.selectedScene == index ? text("FIJADO", "PINNED") : text("FIJAR FONDO", "PIN BACKGROUND"));
        pin.setTooltip(Tooltip.create(pin.getMessage()));
    }

    private void step(int direction) {
        index = Math.floorMod(index + direction, SiegeBackgrounds.count());
        SiegeUiSounds.click();
        refreshPin();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.renderPreview(g, width, height, index);
        if (cleanView) return;
        g.fill(0, 0, width, 45, 0xB0000000);
        g.fill(0, height - 41, width, height, 0xB0000000);
        String heading = (index + 1) + " / " + SiegeBackgrounds.count() + "  ·  " + SiegeBackgrounds.name(index);
        g.drawCenteredString(font, font.plainSubstrByWidth(heading, width - 20), width / 2, 10, 0xFFF0EEE8);
        g.drawCenteredString(font, text("F1: vista limpia · ESC: volver · ← →: cambiar", "F1: clean view · ESC: back · ← →: browse"), width / 2, 27, 0xFFBCC5CC);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (cleanView) toggleCleanView(); else onClose();
            return true;
        }
        if (key == GLFW.GLFW_KEY_F1) { toggleCleanView(); return true; }
        if (key == GLFW.GLFW_KEY_LEFT) { step(-1); return true; }
        if (key == GLFW.GLFW_KEY_RIGHT) { step(1); return true; }
        return cleanView || super.keyPressed(key, scanCode, modifiers);
    }

    private void toggleCleanView() {
        cleanView = !cleanView;
        setFocused(null);
        for (var child : children()) if (child instanceof AbstractWidget widget) widget.visible = !cleanView;
        SiegeUiSounds.resetHover();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        return cleanView || super.mouseClicked(x, y, button);
    }

    @Override
    public void onClose() {
        SiegeUiSounds.back();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    private Component text(String es, String en) {
        return Component.literal(minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_") ? es : en);
    }
}
