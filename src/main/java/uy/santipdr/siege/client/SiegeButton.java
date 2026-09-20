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
public class SiegeButton extends Button {
    private final int accent;
    private String icon = "";
    private String badge = "";
    private String faceLabel;
    public SiegeButton withBadge(String value) { badge = value == null ? "" : value; return this; }
    public SiegeButton withFaceLabel(String value) { faceLabel = value; return this; }

    private boolean selected;
    private boolean mainMenuStyle;
    private float hoverAmount;
    private float pressAmount;
    private long pressedUntil;
    private long lastRenderNanos, hoverStartedAt;
    private boolean wasHot;
    private boolean compactCenter;
    private boolean fullHoverFrame;
    private int textOffsetY;

    public SiegeButton(int x, int y, int width, int height, Component message, OnPress onPress, int accent) {
        super(x, y, Math.max(1, width), Math.max(1, height), message, onPress, DEFAULT_NARRATION);
        this.accent = accent;
    }

    public SiegeButton withIcon(String icon) {
        this.icon = icon == null ? "" : icon;
        return this;
    }

    public SiegeButton setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    public SiegeButton setMainMenuStyle(boolean mainMenuStyle) {
        this.mainMenuStyle = mainMenuStyle;
        return this;
    }

    public SiegeButton setCompactCenter(boolean compactCenter) {
        this.compactCenter = compactCenter;
        return this;
    }

    public SiegeButton setFullHoverFrame(boolean fullHoverFrame) {
        this.fullHoverFrame = fullHoverFrame;
        return this;
    }

    public SiegeButton setTextOffsetY(int textOffsetY) {
        this.textOffsetY = textOffsetY;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        boolean pointerHot = active && isHovered();
        boolean focused = active && isFocused();
        boolean hot = pointerHot || focused;
        boolean effects = SiegeConfig.menuEffects && !SiegeConfig.reducedMotion;

        long nowNanos = System.nanoTime();
        long nowMs = nowNanos / 1_000_000L;
        float elapsed = lastRenderNanos == 0 ? 1.0F / 60.0F
                : Math.min(0.1F, (nowNanos - lastRenderNanos) / 1_000_000_000.0F);
        lastRenderNanos = nowNanos;

        if (hot && !wasHot) hoverStartedAt = nowMs;
        wasHot = hot;

        float hoverTarget = hot ? 1.0F : 0.0F;
        float pressTarget = active && nowMs < pressedUntil ? 1.0F : 0.0F;
        if (!active) {
            hoverAmount = 0.0F;
            pressAmount = 0.0F;
        } else if (effects) {
            hoverAmount += (hoverTarget - hoverAmount) * (1.0F - (float)Math.exp(-18.0F * elapsed));
            pressAmount += (pressTarget - pressAmount) * (1.0F - (float)Math.exp(-24.0F * elapsed));
        } else {
            hoverAmount = hoverTarget;
            pressAmount = pressTarget;
        }

        if (mainMenuStyle) {
            renderMainMenuWidget(g, font, x, y, w, h, hot, focused, effects, nowMs);
            return;
        }

        boolean pressed = pressAmount > 0.03F;
        int idleBody = selected ? 0xF02F2528 : blend(0xF01B1B1D, 0xFA302A2C, hoverAmount);
        int body = !active ? 0xE0151516 : blend(idleBody, 0xFF221D20, pressAmount);
        int edge = !active ? 0xFF41464C : focused ? SiegeTheme.FOCUS
                : selected || fullHoverFrame && hot ? accent : hot ? accent : 0xFF4C555E;

        // Soft depth stays inside a fixed four-fill budget regardless of button size.
        g.fill(x + 2, y + 2, x + w + 2, y + h + 2, 0x55000000);
        g.fill(x, y, x + w, y + h, body);
        SiegeTheme.frame(g, x, y, w, h, selected ? accent : 0xFF454044);
        if (h >= 18 && w >= 48) {
            int sheen = blend(0x185D5859, 0x2A8A8185, hoverAmount);
            g.fill(x + 3, y + 2, x + w - 3, y + 3, sheen);
        }
        g.fill(x, y, x + 2, y + h, edge);
        g.fill(x + 2, y, x + w, y + 1, hot ? edge : 0xFF293038);
        g.fill(x + 2, y + h - 1, x + w, y + h, selected ? edge : 0xFF20262C);

        if (hoverAmount > 0.02F) {
            int alpha = Math.min(164, Math.max(0, Math.round(hoverAmount * 164.0F)));
            if (icon.isEmpty() && w > 40 && h >= 14)
                g.fill(x + 5, y + 3, x + 7, y + h - 3, (alpha << 24) | (accent & 0x00FFFFFF));

            if (effects && nowMs - hoverStartedAt < 480L) {
                // One clipped sweep gives feedback without texture sampling or noise loops.
                int sweepRange = Math.max(1, w + 48);
                int sweepX = x - 24 + (int) ((nowMs - hoverStartedAt) * sweepRange / 480L);
                g.enableScissor(x + 2, y + 1, x + w - 1, y + h - 1);
                g.fill(sweepX, y + 2, sweepX + 1, y + h - 2,
                        (Math.min(72, alpha) << 24) | 0x00FFFFFF);
                g.fill(sweepX + 1, y + 2, sweepX + 5, y + h - 2,
                        (Math.min(26, alpha / 2) << 24) | (accent & 0x00FFFFFF));
                g.disableScissor();
            }

            int bracket = Math.max(4, Math.min(9, h / 3));
            g.fill(x + w - bracket, y, x + w, y + 1, edge);
            g.fill(x + w - 1, y, x + w, y + bracket, edge);
            g.fill(x + w - bracket, y + h - 1, x + w, y + h, edge);
            g.fill(x + w - 1, y + h - bracket, x + w, y + h, edge);
        }

        if (selected && badge.isEmpty() && w > 40 && h >= 13)
            SiegeTheme.icon(g, x + w - 13, y + (h - 9) / 2, "check", active ? accent : SiegeTheme.MUTED);

        if (focused) SiegeTheme.focusCorners(g, x, y, w, h, SiegeTheme.FOCUS);
        else if (fullHoverFrame && hot) SiegeTheme.focusCorners(g, x, y, w, h, edge);

        int underlineWidth = Math.round((w - 4) * hoverAmount);
        if (underlineWidth > 0) g.fill(x + 2, y + h - 2, x + 2 + underlineWidth, y + h - 1, accent);

        boolean showIcon = !icon.isEmpty() && w >= 96;
        SiegeControlLayout positions = SiegeControlLayout.of(w, showIcon, selected, badge.isEmpty() ? 0 : font.width(badge));
        int left = positions.labelX();
        int badgeWidth = positions.badgeWidth();
        int usable = positions.labelWidth();
        String text = fit(font, faceLabel == null ? getMessage().getString() : faceLabel, usable);
        if (badgeWidth > 0) {
            int bx = x + positions.badgeX();
            int badgeEdge = active && selected ? accent : hot ? 0xFF8A8387 : 0xFF777174;
            SiegeTheme.frame(g, bx, y + 3, badgeWidth, h - 6, badgeEdge);
            String state = fit(font, badge, Math.max(1, badgeWidth - 6));
            g.drawString(font, state, bx + (badgeWidth - font.width(state)) / 2, y + (h - font.lineHeight) / 2,
                    active && selected ? SiegeTheme.INK : SiegeTheme.MUTED, false);
        }
        if (w <= 40 || compactCenter)
            left = Math.max(positions.labelX(), positions.labelX() + (positions.labelWidth() - font.width(text)) / 2);

        int textColor = !active ? 0xFF6F767D : hot || selected ? 0xFFF5F3EC : 0xFFD8DDE1;
        if (pressed) {
            int pressedEdge = blend(edge, 0xFFFFFFFF, Math.min(0.35F, pressAmount * 0.35F));
            g.fill(x + 2, y + 1, x + w - 1, y + 2, pressedEdge);
            g.fill(x + 2, y + h - 2, x + w - 1, y + h - 1, 0xFF171417);
        }

        int pressOffset = pressAmount >= 0.45F ? 1 : 0;
        int textY = y + Math.max(1, (h - font.lineHeight) / 2) + textOffsetY;
        if (showIcon)
            SiegeTheme.icon(g, x + 11, y + (h - 9) / 2 + pressOffset, icon,
                    !active ? 0xFF777174 : hot ? blend(accent, 0xFFFFFFFF, 0.18F) : accent);
        g.drawString(font, text, x + left, textY + pressOffset, textColor, false);
    }

    private void renderMainMenuWidget(GuiGraphics g, Font font, int x, int y, int w, int h,
                                      boolean hot, boolean focused, boolean effects, long nowMs) {
        boolean pressed = pressAmount > 0.03F;
        int baseFace = selected ? 0xFF686367 : blend(0xFF555357, 0xFF70696D, hoverAmount);
        int face = !active ? 0xFF49484A : blend(baseFace, 0xFF403B3E, pressAmount);
        int baseInset = selected ? 0xFF757074 : blend(0xFF5E5B60, 0xFF777174, hoverAmount);
        int inset = !active ? 0xFF504D50 : blend(baseInset, 0xFF494246, pressAmount);
        int rim = focused && active ? SiegeTheme.FOCUS : pressed ? accent : hot || selected ? 0xFFF0F0EC : 0xFF9A9C9E;

        // Solid grey plate, deep lower/right shadow and square double rim.
        g.fill(x + 3, y + 4, x + w + 4, y + h + 4, 0xA0000000);
        g.fill(x, y, x + w, y + h, 0xFF26282A);
        g.fill(x + 2, y + 2, x + w - 2, y + h - 2, face);
        g.fill(x + 4, y + 4, x + w - 4, y + h - 4, inset);
        g.fill(x + 2, y + 2, x + w - 2, y + 3, rim);
        g.fill(x + 2, y + h - 3, x + w - 2, y + h - 2,
                blend(0xFF4A4C4E, 0xFF393638, pressAmount));
        g.fill(x + 2, y + 2, x + 3, y + h - 2,
                blend(0xFF8B8D8F, 0xFFA5A1A3, hoverAmount * 0.25F));

        if (focused && active) SiegeTheme.focusCorners(g, x, y, w, h, SiegeTheme.FOCUS);
        if (hot || selected) {
            int cy = y + h / 2;
            int ax = x + 9;
            if (icon.isEmpty()) {
                g.fill(ax, cy - 4, ax + 3, cy + 5, accent);
                g.fill(ax + 3, cy - 3, ax + 6, cy + 4, accent);
                g.fill(ax + 6, cy - 1, ax + 9, cy + 2, accent);
            }
            g.fill(x + 2, y + 2, x + 4, y + h - 2, accent);
            if (effects && hoverAmount > 0.05F && nowMs - hoverStartedAt < 480L) {
                int shineX = x + 5 + (int) ((nowMs - hoverStartedAt) * Math.max(1, w - 12) / 480L);
                g.enableScissor(x + 4, y + 4, x + w - 4, y + h - 4);
                g.fill(shineX, y + 4, shineX + 2, y + h - 4, 0x24FFFFFF);
                g.disableScissor();
            }
        }

        int pressOffset = pressAmount >= 0.45F ? 1 : 0;
        if (!icon.isEmpty())
            SiegeTheme.icon(g, x + 10, y + (h - 9) / 2 + pressOffset, icon,
                    !active ? 0xFF8B8587 : hot ? accent : 0xFFD4CBCD);

        String label = getMessage().getString();
        int labelWidth = Math.max(1, w - 42);
        float labelScale = Math.max(0.85F, Math.min(1.0F,
                labelWidth / (float)Math.max(1, font.width(label))));
        String text = fit(font, label, (int)(labelWidth / labelScale));
        int textX = x + (w - Math.round(font.width(text) * labelScale)) / 2;
        int textY = y + Math.max(1, (h - Math.round(font.lineHeight * labelScale)) / 2);
        int textColor = !active ? 0xFFAAAAA7 : hot || selected ? 0xFFFFFFFF : 0xFFF0F0ED;

        g.pose().pushPose();
        g.pose().translate(textX + pressOffset, textY + pressOffset, 0);
        g.pose().scale(labelScale, labelScale, 1);
        g.drawString(font, text, 1, 1, 0xB0303030, false);
        g.drawString(font, text, 0, 0, textColor, false);
        g.pose().popPose();
    }

    @Override
    public void onPress() {
        if (!active || !visible) return;
        pressedUntil = System.nanoTime() / 1_000_000L + 145L;
        super.onPress();
    }

    private static int blend(int from, int to, float amount) {
        float t = Math.max(0, Math.min(1, amount));
        int result = 0;
        for (int shift = 0; shift <= 24; shift += 8) {
            int start = (from >>> shift) & 255;
            int end = (to >>> shift) & 255;
            result |= Math.round(start + (end - start) * t) << shift;
        }
        return result;
    }

    private static String fit(Font font, String value, int width) {
        if (font.width(value) <= width) return value;
        String ellipsis = "…";
        if (width < font.width(ellipsis)) return font.plainSubstrByWidth(value, Math.max(0, width));
        int target = Math.max(0, width - font.width(ellipsis));
        String clipped = font.plainSubstrByWidth(value, target);
        return clipped + ellipsis;
    }

    /** Vanilla's Button would add its own click sound; SIEGE callbacks own audio. */
    @Override
    public void playDownSound(SoundManager soundManager) { }
}
