package uy.santipdr.siege.client;

/** Logical pixels; shared by Multiplayer rendering and the geometry regression. */
public record SiegeMultiplayerLayout(int x, int width, int top, int bottom,
                                     int listWidth, int detailX, int detailWidth,
                                     int firstRow, int secondRow) {
    public static SiegeMultiplayerLayout of(int width, int height) {
        int safeMargin = width < 360 ? 6 : width < 520 ? 8 : 10;
        int total = Math.min(700, Math.max(1, width - safeMargin * 2));
        int x = (width - total) / 2;
        int detail = width >= 640 ? total - 372 : 0;
        int list = detail > 0 ? 360 : Math.min(360, total);
        if (detail == 0) { total = list; x = (width - total) / 2; }
        int top = height < 300 ? 44 : 50;
        return new SiegeMultiplayerLayout(x, total, top, Math.max(top + 36, height - 86),
                list, x + list + 12, detail, height - 54, height - 28);
    }
    public int listCenterX() { return x + listWidth / 2; }
    public int buttonX(int index, int count) { return x + index * (width + 6) / count; }
    public int buttonWidth(int index, int count) {
        return buttonX(index + 1, count) - buttonX(index, count) - 6;
    }
}
