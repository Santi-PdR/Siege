package uy.santipdr.siege.client;

/** Geometry shared by rendering, mouse targets and the scale regression test. */
public record SiegeGalleryLayout(Rect preview, Rect heading, Rect thumbnails, Rect actions,
                                 int columns, int rows, int tileWidth, int tileHeight) {
    public record Rect(int x, int y, int w, int h) {
        public int right() { return x + w; }
        public int bottom() { return y + h; }
        public boolean contains(double px, double py) {
            return px >= x && px < right() && py >= y && py < bottom();
        }
    }

    public static SiegeGalleryLayout of(int width, int height) {
        int margin = 8, gap = 4;
        boolean wide = width >= 700 && height >= 350;
        Rect actions = new Rect(margin, height - 30, width - margin * 2, 22);
        Rect heading = new Rect(margin, 36, width - margin * 2, 24);
        Rect preview;
        Rect thumbnails;
        int columns, rows;
        if (wide) {
            int rail = Math.min(310, width / 3);
            preview = new Rect(margin, 65, width - rail - margin * 3, height - 106);
            thumbnails = new Rect(width - rail - margin, 65, rail, height - 106);
            columns = 2;
            rows = Math.min(5, Math.max(2, thumbnails.h() / 74));
        } else {
            int stripHeight = height >= 300 ? 78 : 58;
            thumbnails = new Rect(margin, actions.y() - stripHeight - 8, width - margin * 2, stripHeight);
            preview = new Rect(margin, 65, width - margin * 2, thumbnails.y() - 73);
            columns = Math.max(3, Math.min(6, thumbnails.w() / 96));
            rows = 1;
        }
        return new SiegeGalleryLayout(preview, heading, thumbnails, actions, columns, rows,
                (thumbnails.w() - gap * (columns - 1)) / columns,
                (thumbnails.h() - gap * (rows - 1)) / rows);
    }

    public int capacity() { return columns * rows; }
    public Rect tile(int slot) {
        return new Rect(thumbnails.x() + slot % columns * (tileWidth + 4),
                thumbnails.y() + slot / columns * (tileHeight + 4), tileWidth, tileHeight);
    }
    public Rect action(int slot) {
        int w = (actions.w() - 12) / 4;
        return new Rect(actions.x() + slot * (w + 4), actions.y(), w, actions.h());
    }
}
