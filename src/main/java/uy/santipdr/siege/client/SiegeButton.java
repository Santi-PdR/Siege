package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

/**
 * SIEGE-owned button rendering. This intentionally bypasses Minecraft's vanilla
 * widget texture and vanilla click sound so the menu keeps one visual/audio
 * language at every GUI scale.
 */
public final class SiegeButton extends Button {
    private final int accent;
    private boolean selected;
    private float hoverAmount;

    public SiegeButton(int x, int y, int width, int height, Component message, OnPress onPress, int accent) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.accent = accent;
    }

    public SiegeButton setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        boolean hot = active && isHoveredOrFocused();
        float target = hot ? 1.0F : 0.0F;
        hoverAmount += (target - hoverAmount) * Math.min(1.0F, 0.22F + partialTick * 0.08F);

        int body = !active ? 0xB90A0C0F : selected ? 0xE51A2026 : hot ? 0xE5181D22 : 0xD20B0F13;
        int edge = !active ? 0xFF41464C : (selected || hot) ? accent : 0xFF4C555E;

        g.fill(x + 2, y + 2, x + w + 2, y + h + 2, 0x55000000);
        g.fill(x, y, x + w, y + h, body);
        g.fill(x, y, x + 2, y + h, edge);
        g.fill(x + 2, y, x + w, y + 1, hot ? edge : 0xFF293038);
        g.fill(x + 2, y + h - 1, x + w, y + h, selected ? edge : 0xFF20262C);

        if (hoverAmount > 0.02F) {
            int alpha = Math.min(150, Math.max(0, Math.round(hoverAmount * 150.0F)));
            g.fill(x + 5, y + 3, x + 7, y + h - 3, (alpha << 24) | (accent & 0x00FFFFFF));

            // Thin tactical sweep, clipped to the button instead of washing out the text.
            int sweepRange = Math.max(1, w + 32);
            int sweepX = x - 16 + (int) ((System.currentTimeMillis() / 8L) % sweepRange);
            g.enableScissor(x + 2, y + 1, x + w - 1, y + h - 1);
            g.fill(sweepX, y + 2, sweepX + 1, y + h - 2, (Math.min(74, alpha) << 24) | 0x00FFFFFF);
            g.fill(sweepX + 1, y + 2, sweepX + 4, y + h - 2, (Math.min(28, alpha / 2) << 24) | (accent & 0x00FFFFFF));
            g.disableScissor();

            int bracket = Math.max(4, Math.min(9, h / 3));
            g.fill(x + w - bracket, y, x + w, y + 1, edge);
            g.fill(x + w - 1, y, x + w, y + bracket, edge);
            g.fill(x + w - bracket, y + h - 1, x + w, y + h, edge);
            g.fill(x + w - 1, y + h - bracket, x + w, y + h, edge);
        }
        if (selected) {
            g.fill(x + w - 12, y + 4, x + w - 5, y + 5, accent);
            g.fill(x + w - 9, y + 7, x + w - 5, y + 8, accent);
        }

        int left = hot ? 13 : 9;
        int usable = Math.max(8, w - left - 9);
        String text = fit(font, getMessage().getString(), usable);
        int textColor = !active ? 0xFF6F767D : hot || selected ? 0xFFF5F3EC : 0xFFD8DDE1;
        g.drawString(font, text, x + left, y + Math.max(1, (h - font.lineHeight) / 2), textColor, false);
    }

    private static String fit(Font font, String value, int width) {
        if (font.width(value) <= width) return value;
        String ellipsis = "...";
        int target = Math.max(0, width - font.width(ellipsis));
        String clipped = font.plainSubstrByWidth(value, target);
        return clipped + ellipsis;
    }

    /** Vanilla's Button would add its own click sound; SIEGE callbacks own audio. */
    @Override
    public void playDownSound(SoundManager soundManager) { }
}
