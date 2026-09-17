package uy.santipdr.siege.client;

/** Logical-pixel geometry shared with tests, independent of GUI scale. */
public record SiegeGuideLayout(int width, int height, int columns, int tabWidth, int searchY,
                               Rect list, Rect article, int footerY, int capacity) {
    public record Rect(int x, int y, int w, int h) {
        public int right() { return x + w; }
        public int bottom() { return y + h; }
        public boolean contains(double px, double py) { return px >= x && px < right() && py >= y && py < bottom(); }
    }
    public static SiegeGuideLayout of(int width, int height) {
        int columns = width >= 600 ? 5 : 3;
        int tabs = (width - 16 - (columns - 1) * 4) / columns;
        int searchY = 34 + ((5 + columns - 1) / columns) * 22 + 4;
        int top = searchY + 26;
        int bottom = height - 34;
        int nav = Math.min(190, Math.max(88, width / 4));
        return new SiegeGuideLayout(width, height, columns, tabs, searchY,
                new Rect(8, top, nav, Math.max(1, bottom - top)),
                new Rect(nav + 16, top, Math.max(1, width - nav - 24), Math.max(1, bottom - top)),
                height - 26, Math.max(1, (bottom - top) / 24));
    }
    public static Rect fit(Rect area, int sourceWidth, int sourceHeight) {
        if (sourceWidth <= 0 || sourceHeight <= 0) throw new IllegalArgumentException("Image dimensions");
        double scale = Math.min(area.w() / (double)sourceWidth, area.h() / (double)sourceHeight);
        int w = Math.max(1, Math.min(area.w(), (int)Math.floor(sourceWidth * scale)));
        int h = Math.max(1, Math.min(area.h(), (int)Math.floor(sourceHeight * scale)));
        return new Rect(area.x() + (area.w() - w) / 2, area.y() + (area.h() - h) / 2, w, h);
    }
}
