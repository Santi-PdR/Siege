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

        int body = !active ? 0xB90A0C0F : selected ? 0xE51A2026 : hot ? 0xE5181D22 : 0xD20B0F13;
        int edge = !active ? 0xFF41464C : (selected || hot) ? accent : 0xFF4C555E;

        g.fill(x + 2, y + 2, x + w + 2, y + h + 2, 0x55000000);
        g.fill(x, y, x + w, y + h, body);
        g.fill(x, y, x + 2, y + h, edge);
        g.fill(x + 2, y, x + w, y + 1, hot ? edge : 0xFF293038);
        g.fill(x + 2, y + h - 1, x + w, y + h, selected ? edge : 0xFF20262C);

        if (hot) {
            g.fill(x + 5, y + 3, x + 7, y + h - 3, accent);
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
