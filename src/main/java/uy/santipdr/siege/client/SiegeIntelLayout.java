package uy.santipdr.siege.client;

/** Layout is independent of GUI scale: inputs are Minecraft logical pixel dimensions. */
public record SiegeIntelLayout(boolean wide, boolean ultraCompact, int sidebarWidth,
                               int columns, int categoryHeight, int categoryTop,
                               int listTop, int listBottom, int contentTop) {
    public static SiegeIntelLayout of(int width, int height, int categories) {
        boolean wide = width >= 620 && height >= 400;
        boolean ultra = !wide && height < 285;
        int sidebar = wide ? Math.min(238, Math.max(174, width / 5)) : 0;
        int columns = wide ? 1 : width >= 560 ? categories : 4;
        int h = wide ? height < 430 ? 18 : 20 : ultra ? 15 : 16;
        int top = wide ? 64 : 29;
        int rows = (categories + columns - 1) / columns;
        int list = top + rows * (h + (wide ? 3 : 2)) + (wide ? 30 : 26);
        int bottom = wide ? height - 59 : list + (ultra ? 16 : 18);
        return new SiegeIntelLayout(wide, ultra, sidebar, columns, h, top, list, bottom, wide ? 91 : bottom + 4);
    }
}
