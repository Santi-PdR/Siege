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
    private boolean mainMenuStyle;
    private float hoverAmount;

    public SiegeButton(int x, int y, int width, int height, Component message, OnPress onPress, int accent) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.accent = accent;
    }

    public SiegeButton setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    public SiegeButton setMainMenuStyle(boolean mainMenuStyle) {
        this.mainMenuStyle = mainMenuStyle;
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
        boolean effects = SiegeConfig.menuEffects && !SiegeConfig.reducedMotion;
        float target = hot ? 1.0F : 0.0F;
        if (effects) hoverAmount += (target - hoverAmount) * Math.min(1.0F, 0.22F + partialTick * 0.08F);
        else hoverAmount = target;

        if (mainMenuStyle) {
            renderMainMenuWidget(g, font, x, y, w, h, hot, effects);
            return;
        }

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

            if (effects) {
                // Thin tactical sweep, clipped to the button instead of washing out the text.
                int sweepRange = Math.max(1, w + 48);
                int sweepX = x - 24 + (int) ((System.currentTimeMillis() / 10L) % sweepRange);
                g.enableScissor(x + 2, y + 1, x + w - 1, y + h - 1);
                g.fill(sweepX, y + 2, sweepX + 1, y + h - 2, (Math.min(66, alpha) << 24) | 0x00FFFFFF);
                g.fill(sweepX + 1, y + 2, sweepX + 5, y + h - 2, (Math.min(24, alpha / 2) << 24) | (accent & 0x00FFFFFF));
                g.disableScissor();
            }

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

        int underlineWidth = Math.round((w - 4) * hoverAmount);
        if (underlineWidth > 0) g.fill(x + 2, y + h - 2, x + 2 + underlineWidth, y + h - 1, accent);

        int left = 9 + Math.round(4.0F * hoverAmount);
        int usable = Math.max(8, w - left - 9);
        String text = fit(font, getMessage().getString(), usable);
        int textColor = !active ? 0xFF6F767D : hot || selected ? 0xFFF5F3EC : 0xFFD8DDE1;
        g.drawString(font, text, x + left, y + Math.max(1, (h - font.lineHeight) / 2), textColor, false);
    }

    private void renderMainMenuWidget(GuiGraphics g, Font font, int x, int y, int w, int h,
                                      boolean hot, boolean effects) {
        int body = !active ? 0xC54B4D50 : hot || selected ? 0xE18B8C8E : 0xD6757779;
        int inner = !active ? 0xAA55575A : hot || selected ? 0xD9929395 : 0xC87B7D7F;
        int border = hot || selected ? 0xFFE54852 : 0xFF9A9C9E;

        // Compact offset shadow, hard steel border and restrained horizontal grain:
        // this follows the reference's industrial grey plates instead of glassy black cards.
        g.fill(x + 2, y + 3, x + w + 3, y + h + 3, 0x66000000);
        g.fill(x, y, x + w, y + h, 0xFF303236);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, body);
        g.fill(x + 3, y + 3, x + w - 3, y + h - 3, inner);
        g.fill(x + 2, y + 1, x + w - 2, y + 2, border);
        g.fill(x + 2, y + h - 2, x + w - 2, y + h - 1, 0xFF4A4C4F);

        for (int lineY = y + 4; lineY < y + h - 3; lineY += 4) {
            g.fill(x + 4, lineY, x + w - 4, lineY + 1, hot ? 0x0EFFFFFF : 0x0AFFFFFF);
        }

        if (hot || selected) {
            int cy = y + h / 2;
            int arrowX = x + Math.max(9, h / 2);
            g.fill(arrowX, cy - 4, arrowX + 3, cy + 5, accent);
            g.fill(arrowX + 3, cy - 3, arrowX + 6, cy + 4, accent);
            g.fill(arrowX + 6, cy - 1, arrowX + 9, cy + 2, accent);
            g.fill(x + 1, y + 1, x + 3, y + h - 1, accent);

            if (effects && hoverAmount > 0.05F) {
                int sweep = Math.round((w - 12) * hoverAmount);
                g.fill(x + 5, y + h - 3, x + 5 + sweep, y + h - 2, 0x88E54852);
            }
        }

        String text = fit(font, getMessage().getString(), w - 40);
        int textX = x + (w - font.width(text)) / 2;
        int textColor = !active ? 0xFF9B9DA0 : 0xFFF1F0ED;
        g.drawString(font, text, textX, y + Math.max(1, (h - font.lineHeight) / 2), textColor, true);
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
