package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;

/** Small code-drawn surfaces; no new textures, shaders or gameplay hooks. */
public final class SiegeTheme {
    public static final int RED = 0xFFE54852;
    public static final int GOLD = 0xFFD6AE65;
    public static final int INK = 0xFFF0EDEA;
    public static final int MUTED = 0xFFA4A3A1;
    public static final int SURFACE = 0xF018181A;
    public static final int FOCUS = 0xFFF2D36F;
    private SiegeTheme() { }

    public static void frame(GuiGraphics g, int x, int y, int w, int h, int color) {
        if (w < 2 || h < 2) return;
        g.fill(x, y, x + w, y + 1, color);
        g.fill(x, y + h - 1, x + w, y + h, color);
        g.fill(x, y, x + 1, y + h, color);
        g.fill(x + w - 1, y, x + w, y + h, color);
    }

    public static void panel(GuiGraphics g, int x, int y, int w, int h, int accent) {
        if (w < 6 || h < 6) return;
        g.fill(x, y, x + w, y + h, SURFACE);
        frame(g, x, y, w, h, 0xFF424044);

        // Layered one-pixel highlights add depth without introducing textures or
        // expensive per-frame effects. Geometry stays identical for every screen.
        g.fill(x + 1, y + 1, x + w - 1, y + 2, 0xFF666166);
        g.fill(x + 2, y + 2, x + w - 2, y + 3, 0x185F5B5F);
        g.fill(x + 1, y + h - 3, x + w - 1, y + h - 2, 0x26000000);
        g.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, 0xFF0B0B0C);
        g.fill(x + 1, y + 3, x + 2, y + h - 3, 0x205F5B5F);
        g.fill(x + w - 2, y + 3, x + w - 1, y + h - 3, 0x24000000);

        int corner = Math.min(18, Math.min(w, h) / 4);
        g.fill(x, y, x + corner, y + 2, accent);
        g.fill(x + w - corner, y + h - 2, x + w, y + h, accent);
    }

    /** Small focus brackets used by controls without changing their hit boxes. */
    public static void focusCorners(GuiGraphics g, int x, int y, int w, int h, int color) {
        if (w < 4 || h < 4) return;
        int arm = Math.min(6, Math.max(3, Math.min(w, h) / 3));
        g.fill(x, y, x + arm, y + 1, color);
        g.fill(x, y, x + 1, y + arm, color);
        g.fill(x + w - arm, y, x + w, y + 1, color);
        g.fill(x + w - 1, y, x + w, y + arm, color);
        g.fill(x, y + h - 1, x + arm, y + h, color);
        g.fill(x, y + h - arm, x + 1, y + h, color);
        g.fill(x + w - arm, y + h - 1, x + w, y + h, color);
        g.fill(x + w - 1, y + h - arm, x + w, y + h, color);
    }

    /** Compact section divider shared by screens that need a quiet tactical accent. */
    public static void divider(GuiGraphics g, int x, int y, int w, int accent) {
        if (w <= 0) return;
        g.fill(x, y, x + w, y + 1, 0xFF2A2D30);
        g.fill(x, y, x + Math.min(56, w), y + 2, accent);
    }

    /** Texture stays in the empty perimeter, never across the reading columns or portrait. */
    public static void paper(GuiGraphics g, int x, int y, int w, int h, boolean dark) {
        if (w < 40 || h < 40) return;
        int grain = dark ? 0x224B4843 : 0x224C4130;
        for (int offset = 11; offset < h - 8; offset += 13) {
            g.fill(x + 2, y + offset, x + 4, y + offset + 1, grain);
            g.fill(x + w - 4, y + offset + 4, x + w - 2, y + offset + 5, grain);
        }
        g.fill(x + 3, y + 3, x + w - 3, y + 4, dark ? 0x334E4E48 : 0x44FFFFFF);
        if (w >= 260) {
            for (int staple : new int[] {x + w / 2 - 16, x + w / 2 + 10}) {
                g.fill(staple, y + 2, staple + 6, y + 4, 0x88585651);
                g.fill(staple + 1, y + 2, staple + 5, y + 3, 0x99C8C5BB);
            }
        }
    }

    /** 9x9 pictograms, rendered as exact pixels at every GUI scale. */
    public static void icon(GuiGraphics g, int x, int y, String kind, int color) {
        switch (kind) {
            case "connect" -> {
                frame(g, x, y + 1, 9, 6, color);
                g.fill(x + 4, y + 7, x + 5, y + 9, color);
                g.fill(x + 2, y + 8, x + 7, y + 9, color);
            }
            case "intel" -> {
                frame(g, x + 1, y, 7, 9, color);
                g.fill(x + 3, y + 2, x + 6, y + 3, color);
                g.fill(x + 3, y + 5, x + 6, y + 6, color);
            }
            case "settings" -> {
                for (int i = 1; i <= 7; i += 3) g.fill(x, y + i, x + 9, y + i + 1, color);
                g.fill(x + 2, y, x + 3, y + 3, color);
                g.fill(x + 6, y + 3, x + 7, y + 6, color);
                g.fill(x + 3, y + 6, x + 4, y + 9, color);
            }
            case "music" -> {
                g.fill(x + 3, y, x + 4, y + 7, color);
                g.fill(x + 3, y, x + 8, y + 1, color);
                g.fill(x + 7, y, x + 8, y + 7, color);
                g.fill(x + 1, y + 6, x + 4, y + 8, color);
                g.fill(x + 5, y + 6, x + 8, y + 8, color);
            }
            case "pin" -> {
                frame(g, x + 2, y, 5, 4, color);
                g.fill(x + 1, y + 4, x + 8, y + 5, color);
                g.fill(x + 4, y + 5, x + 5, y + 9, color);
            }
            case "check" -> { g.fill(x + 1, y + 4, x + 3, y + 6, color); g.fill(x + 3, y + 6, x + 5, y + 8, color); g.fill(x + 5, y + 4, x + 7, y + 6, color); g.fill(x + 7, y + 2, x + 9, y + 4, color); }
            case "play" -> { for (int i = 0; i < 4; i++) g.fill(x + 2 + i, y + 1 + i, x + 3 + i, y + 8 - i, color); }
            case "pause" -> { g.fill(x + 1, y + 1, x + 3, y + 8, color); g.fill(x + 6, y + 1, x + 8, y + 8, color); }
            case "eye" -> { frame(g, x, y + 2, 9, 5, color); g.fill(x + 4, y + 3, x + 5, y + 6, color); }
            case "image" -> { frame(g, x, y, 9, 9, color); g.fill(x + 2, y + 2, x + 4, y + 4, color); g.fill(x + 4, y + 5, x + 7, y + 7, color); }
            case "shield" -> { frame(g, x + 1, y, 7, 6, color); g.fill(x + 2, y + 6, x + 7, y + 7, color); g.fill(x + 3, y + 7, x + 6, y + 8, color); g.fill(x + 4, y + 8, x + 5, y + 9, color); }
            case "overview" -> { frame(g, x, y, 4, 4, color); frame(g, x + 5, y, 4, 4, color); frame(g, x, y + 5, 4, 4, color); frame(g, x + 5, y + 5, 4, 4, color); }
            case "lock" -> { frame(g, x + 2, y, 5, 5, color); frame(g, x + 1, y + 4, 7, 5, color); g.fill(x + 4, y + 6, x + 5, y + 8, color); }
            case "search" -> { frame(g, x, y, 6, 6, color); g.fill(x + 5, y + 5, x + 7, y + 7, color); g.fill(x + 7, y + 7, x + 9, y + 9, color); }
            default -> { }
        }
    }
}
