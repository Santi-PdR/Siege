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
    private float hoverAmount;
    private long lastRenderNanos;

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
        boolean handled = active && visible && button == 0 && isMouseOver(mouseX, mouseY);
        if (handled) {
            dragging = true;
            updateFromPointer(mouseX);
            SiegeUiSounds.click();
        }
        return handled;
    }

    private void updateFromPointer(double mouseX) {
        if (!Double.isFinite(mouseX)) return;
        value = SiegeSliderGeometry.percent(mouseX, getX(), getWidth()) / 100.0;
        applyValue();
        updateMessage();
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (button == 0 && dragging && active && visible) {
            updateFromPointer(x);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        boolean handled = button == 0 && dragging;
        if (button == 0) {
            dragging = false;
            if (handled) SiegeConfig.save();
        }
        return super.mouseReleased(x, y, button) || handled;
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int left = getX();
        int top = getY();
        int right = left + getWidth();
        int bottom = top + getHeight();
        boolean hot = active && (dragging || isHovered() || isFocused());
        boolean effects = SiegeConfig.menuEffects && !SiegeConfig.reducedMotion;

        long now = System.nanoTime();
        float elapsed = lastRenderNanos == 0 ? 1.0F / 60.0F
                : Math.min(0.1F, (now - lastRenderNanos) / 1_000_000_000.0F);
        lastRenderNanos = now;
        float target = hot ? 1.0F : 0.0F;
        if (!active) hoverAmount = 0.0F;
        else if (effects) hoverAmount = settle(hoverAmount, target, 18.0F, elapsed);
        else hoverAmount = target;

        int accent = !active ? 0xFF696064 : themeAccent;
        SiegeTheme.panel(g, left, top, getWidth(), getHeight(), accent);
        if (isFocused() && active) SiegeTheme.focusCorners(g, left, top, getWidth(), getHeight(), SiegeTheme.FOCUS);

        g.fill(left, top, right, Math.min(bottom, top + 1), hot ? accent : 0xFF343033);
        if (bottom > top) g.fill(left, bottom - 1, right, bottom, 0xFF252123);
        g.fill(left, top, Math.min(right, left + 2), bottom, accent);

        int trackLeft = Math.min(right, left + 9);
        int trackRight = Math.max(trackLeft, right - 9);
        int trackY = Math.max(top, bottom - 6);
        if (trackY + 2 <= bottom) g.fill(trackLeft, trackY, trackRight, trackY + 2, 0xFF343033);

        int fillRight = trackLeft + (int)Math.round((trackRight - trackLeft) * value);
        if (trackY + 2 <= bottom) g.fill(trackLeft, trackY, fillRight, trackY + 2, accent);
        if (hot && trackRight > trackLeft && trackY > top)
            g.fill(trackLeft, trackY - 1, fillRight, trackY, (Math.min(72, Math.round(72 * hoverAmount)) << 24) | (accent & 0x00FFFFFF));

        if (getHeight() >= 10) for (int i = 0; i <= 4; i++) {
            int tick = trackLeft + (trackRight - trackLeft) * i / 4;
            int tickColor = tick <= fillRight ? accent : 0xFF71696D;
            g.fill(tick, Math.max(top, trackY - 1), tick + 1, Math.min(bottom, trackY + 3), tickColor);
        }

        int knobX = fillRight - 2;
        if (active && hot && getHeight() >= 10) {
            int glowAlpha = Math.min(90, Math.max(20, Math.round(hoverAmount * 90.0F)));
            g.fill(Math.max(left, knobX - 2), Math.max(top, trackY - 5), Math.min(right, knobX + 7), Math.min(bottom, trackY + 7),
                    (glowAlpha << 24) | (accent & 0x00FFFFFF));
        }
        if (getHeight() >= 8) {
            g.fill(Math.max(left, knobX), Math.max(top, trackY - 3), Math.min(right, knobX + 5), Math.min(bottom, trackY + 5),
                    active ? SiegeTheme.INK : SiegeTheme.MUTED);
            g.fill(Math.max(left, knobX + 1), Math.max(top, trackY - 2), Math.min(right, knobX + 4), Math.min(bottom, trackY + 4), accent);
            g.fill(Math.max(left, knobX + 2), Math.max(top, trackY - 1), Math.min(right, knobX + 3), Math.min(bottom, trackY + 3),
                    active ? SiegeTheme.INK : SiegeTheme.MUTED);
        }

        var font = Minecraft.getInstance().font;
        String amount = valueText == null ? lastPercent + "%" : valueText.apply(lastPercent);
        int amountWidth = font.width(amount);
        if (amountWidth > Math.max(1, getWidth() - 18)) {
            amount = font.plainSubstrByWidth(amount, Math.max(1, getWidth() - 18));
            amountWidth = font.width(amount);
        }

        String labelText = label == null ? "" : label.getString();
        int textWidth = Math.max(0, getWidth() - amountWidth - 26);
        String clipped = font.plainSubstrByWidth(labelText, textWidth);
        if (!clipped.equals(labelText) && textWidth >= font.width("…"))
            clipped = font.plainSubstrByWidth(labelText, textWidth - font.width("…")) + "…";

        int textColor = !active ? SiegeTheme.MUTED : hot ? SiegeTheme.INK : 0xFFD8DDE1;
        int textY = Math.min(Math.max(top, bottom - font.lineHeight), top + 4);
        g.drawString(font, clipped, Math.min(right, left + 9), textY, textColor, false);
        g.drawString(font, amount, Math.max(left, right - amountWidth - 9), textY, accent, false);
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
            applyValue();
            updateMessage();
            if (before != lastPercent) {
                SiegeUiSounds.click();
                SiegeConfig.save();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private static float settle(float current, float target, float speed, float elapsed) {
        if (Math.abs(target - current) < 0.0015F) return target;
        return current + (target - current) * (1.0F - (float)Math.exp(-speed * elapsed));
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
