package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;

/** SIEGE-styled slider with integer 0-100 output and no vanilla widget texture/sound. */
public final class SiegeSlider extends AbstractSliderButton {
    private final Component label;
    private final IntConsumer consumer;
    private int lastPercent;

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
        setMessage(label.copy().append(": " + percent + "%"));
    }

    @Override
    protected void applyValue() {
        int percent = clamp((int)Math.round(value * 100.0D));
        if (percent == lastPercent) return;
        lastPercent = percent;
        consumer.accept(percent);
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int left = getX();
        int top = getY();
        int right = left + getWidth();
        int bottom = top + getHeight();
        int accent = isHoveredOrFocused() ? 0xFF6BD8F2 : 0xFF55BFD9;

        g.fill(left, top, right, bottom, 0xED0A0F13);
        g.fill(left, top, right, top + 1, 0xFF2A343C);
        g.fill(left, bottom - 1, right, bottom, 0xFF1B252C);
        g.fill(left, top, left + 2, bottom, accent);

        int trackLeft = left + 9;
        int trackRight = right - 9;
        int trackY = bottom - 6;
        g.fill(trackLeft, trackY, trackRight, trackY + 2, 0xFF28343B);
        int fillRight = trackLeft + (int)Math.round((trackRight - trackLeft) * value);
        g.fill(trackLeft, trackY, fillRight, trackY + 2, accent);

        int knobX = Math.max(trackLeft, Math.min(trackRight - 3, fillRight - 2));
        g.fill(knobX, trackY - 3, knobX + 5, trackY + 5, 0xFFE8EEF1);
        g.fill(knobX + 1, trackY - 2, knobX + 4, trackY + 4, accent);

        int textWidth = Math.max(20, getWidth() - 18);
        Component clipped = Component.literal(Minecraft.getInstance().font.plainSubstrByWidth(getMessage().getString(), textWidth));
        g.drawString(Minecraft.getInstance().font, clipped, left + 9, top + 5, 0xFFE6ECEF, false);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // Custom UI audio is handled by SiegeUiSounds; never play the vanilla button click.
    }

    public int percent() {
        return lastPercent;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
