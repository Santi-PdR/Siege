package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.function.IntConsumer;

/** SIEGE-styled slider with integer 0-100 output and no vanilla widget texture/sound. */
public final class SiegeSlider extends AbstractSliderButton {
    private final Component label;
    private final IntConsumer consumer;
    private int lastPercent;
    private boolean dragging;
    private int themeAccent = SiegeTheme.RED;
    private java.util.function.IntFunction<String> valueText;

    public SiegeSlider(int x, int y, int width, int height, Component label, int initialPercent, IntConsumer consumer) {
        super(x, y, width, height, Component.empty(), clamp(initialPercent) / 100.0D);
        this.label = label;
        this.consumer = consumer;
        this.lastPercent = clamp(initialPercent);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        int percent = (int)Math.round(value * 100.0D);
        if (label == null) {
            setMessage(Component.literal(percent + "%"));
            return;
        }
        setMessage(label.copy().append(": " + (valueText == null ? percent + "%" : valueText.apply(percent))));
    }

    public SiegeSlider withAccent(int accent) { themeAccent = accent; return this; }

    public SiegeSlider withValueText(java.util.function.IntFunction<String> formatter) {
        valueText = formatter;
        updateMessage();
        return this;
    }

    @Override
    protected void applyValue() {
        int percent = clamp((int)Math.round(value * 100.0D));
        if (percent == lastPercent) return;
        lastPercent = percent;
        if (consumer != null) consumer.accept(percent);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        if (handled) { dragging = true; updateFromPointer(mouseX); SiegeUiSounds.click(); }
        return handled;
    }

    private void updateFromPointer(double mouseX) {
        if (!Double.isFinite(mouseX)) return;
        value = Math.round(Math.max(0, Math.min(1, (mouseX - getX() - 9) / Math.max(1, getWidth() - 18))) * 100) / 100.0;
        applyValue(); updateMessage();
    }
    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (button == 0 && dragging && active && visible) { updateFromPointer(x); return true; }
        return false;
    }
    @Override
    public boolean mouseReleased(double x, double y, int button) {
        boolean handled = button == 0 && dragging;
        if (button == 0) { dragging = false; if (handled) SiegeConfig.save(); }
        return super.mouseReleased(x, y, button) || handled;
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int left = getX();
        int top = getY();
        int right = left + getWidth();
        int bottom = top + getHeight();
        if (isFocused() && active) {
            g.fill(left - 1, top - 1, right + 1, bottom + 1, 0xFFF2D36F);
        }
        int accent = !active ? 0xFF696064 : themeAccent;

        SiegeTheme.panel(g, left, top, getWidth(), getHeight(), active ? themeAccent : 0xFF696064);
        g.fill(left, top, right, top + 1, 0xFF2A343C);
        g.fill(left, bottom - 1, right, bottom, 0xFF1B252C);
        g.fill(left, top, left + 2, bottom, accent);

        int trackLeft = left + 9;
        int trackRight = Math.max(trackLeft, right - 9);
        int trackY = bottom - 6;
        g.fill(trackLeft, trackY, trackRight, trackY + 2, 0xFF28343B);
        for (int i = 0; i <= 4; i++) {
            int tick = trackLeft + (trackRight - trackLeft) * i / 4;
            g.fill(tick, trackY - 1, tick + 1, trackY + 3, 0xFF52616A);
        }
        int fillRight = trackLeft + (int)Math.round((trackRight - trackLeft) * value);
        g.fill(trackLeft, trackY, fillRight, trackY + 2, accent);

        int knobX = Math.max(trackLeft, Math.min(trackRight - 3, fillRight - 2));
        g.fill(knobX, trackY - 3, knobX + 5, trackY + 5, 0xFFE8EEF1);
        g.fill(knobX + 1, trackY - 2, knobX + 4, trackY + 4, accent);

        var font = Minecraft.getInstance().font;
        String amount = valueText == null ? lastPercent + "%" : valueText.apply(lastPercent);
        int amountWidth = font.width(amount);
        if (amountWidth > Math.max(1, getWidth() - 18)) {
            amount = font.plainSubstrByWidth(amount, Math.max(1, getWidth() - 18)); amountWidth = font.width(amount);
        }
        int textWidth = Math.max(0, getWidth() - amountWidth - 26);
        String clipped = font.plainSubstrByWidth(label.getString(), textWidth);
        if (!clipped.equals(label.getString()) && textWidth >= font.width("…"))
            clipped = font.plainSubstrByWidth(label.getString(), textWidth - font.width("…")) + "…";
        g.drawString(font, clipped, left + 9, top + 4, 0xFFE6ECEF, false);
        g.drawString(font, amount, right - amountWidth - 9, top + 4, accent, false);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // Custom UI audio is handled by SiegeUiSounds; never play the vanilla slider/button click.
    }

    public int percent() {
        return lastPercent;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!active || !visible || !isFocused()) return false;
        int before = lastPercent;
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT) {
            value = clamp(lastPercent + (keyCode == GLFW.GLFW_KEY_LEFT ? -1 : 1)) / 100.0;
            applyValue(); updateMessage();
            if (before != lastPercent) { SiegeUiSounds.click(); SiegeConfig.save(); }
            return true;
        }
        boolean handled = super.keyPressed(keyCode, scanCode, modifiers);
        if (handled && before != lastPercent && (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT))
            SiegeUiSounds.click();
        return handled;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}

