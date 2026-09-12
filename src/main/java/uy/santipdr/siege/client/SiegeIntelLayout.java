package uy.santipdr.siege.client;

/** Layout is independent of GUI scale: inputs are Minecraft logical pixel dimensions. */
public record SiegeIntelLayout(boolean wide, boolean ultraCompact, int sidebarWidth,
                               int columns, int categoryHeight, int categoryTop,
                               int listTop, int listBottom, int contentTop) {
    public static int portraitWidth(int inner, int availableHeight) {
        return Math.max(1, Math.min(Math.min(292, inner * 43 / 100), Math.max(1, availableHeight - 80) * 16 / 9));
    }
    public static SiegeIntelLayout of(int width, int height, int categories) {
        boolean shortWindow = height < 330;
        int sidebar = Math.min(210, Math.max(112, width / 4));
        int h = shortWindow ? 13 : 18;
        int top = shortWindow ? 40 : 64;
        int list = top + categories * (h + (shortWindow ? 2 : 3)) + (shortWindow ? 18 : 30);
        return new SiegeIntelLayout(true, shortWindow, sidebar, 1, h, top, list, height - 37, 96);
    }
}
