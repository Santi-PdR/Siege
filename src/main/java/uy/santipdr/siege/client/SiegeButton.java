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
    private long pressedUntil;
    private long lastRenderNanos;

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
        long now = System.nanoTime();
        float elapsed = lastRenderNanos == 0 ? 1.0F / 60.0F : Math.min(0.1F, (now - lastRenderNanos) / 1_000_000_000.0F);
        lastRenderNanos = now;
        if (effects) hoverAmount += (target - hoverAmount) * (1.0F - (float)Math.exp(-16.0F * elapsed));
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
        boolean pressed = System.currentTimeMillis() < pressedUntil;
        int face = !active ? 0xFF56585A : pressed ? 0xFF555759 : hot || selected ? 0xFF858789 : 0xFF696B6D;
        int inset = !active ? 0xFF606264 : pressed ? 0xFF626466 : hot || selected ? 0xFF929496 : 0xFF747678;
        int rim = pressed ? accent : hot || selected ? 0xFFF0F0EC : 0xFF9A9C9E;

        // Old reference: solid grey plate, deep lower/right shadow and square double rim.
        g.fill(x + 3, y + 4, x + w + 4, y + h + 4, 0xA0000000);
        g.fill(x, y, x + w, y + h, 0xFF26282A);
        g.fill(x + 2, y + 2, x + w - 2, y + h - 2, face);
        g.fill(x + 4, y + 4, x + w - 4, y + h - 4, inset);
        g.fill(x + 2, y + 2, x + w - 2, y + 3, rim);
        g.fill(x + 2, y + h - 3, x + w - 2, y + h - 2, 0xFF4A4C4E);
        g.fill(x + 2, y + 2, x + 3, y + h - 2, 0xFF8B8D8F);

        if (hot || selected) {
            int cy = y + h / 2;
            int ax = x + 9;
            g.fill(ax, cy - 4, ax + 3, cy + 5, accent);
            g.fill(ax + 3, cy - 3, ax + 6, cy + 4, accent);
            g.fill(ax + 6, cy - 1, ax + 9, cy + 2, accent);
            g.fill(x + 2, y + 2, x + 4, y + h - 2, accent);
            if (effects && hoverAmount > 0.05F) {
                int shineX = x + 5 + (int) ((System.currentTimeMillis() / 13L) % Math.max(1, w - 12));
                g.enableScissor(x + 4, y + 4, x + w - 4, y + h - 4);
                g.fill(shineX, y + 4, shineX + 2, y + h - 4, 0x20FFFFFF);
                g.disableScissor();
            }
        }

        String text = fit(font, getMessage().getString(), w - 42);
        int textX = x + (w - font.width(text)) / 2;
        int textY = y + Math.max(1, (h - font.lineHeight) / 2);
        int textColor = !active ? 0xFFAAAAA7 : hot || selected ? 0xFFFFFFFF : 0xFFF0F0ED;
        int pressOffset = pressed ? 1 : 0;
        g.drawString(font, text, textX + 1 + pressOffset, textY + 1 + pressOffset, 0xB0303030, false);
        g.drawString(font, text, textX + pressOffset, textY + pressOffset, textColor, false);
    }

    @Override
    public void onPress() {
        pressedUntil = System.currentTimeMillis() + 120L;
        super.onPress();
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

