import uy.santipdr.siege.client.SiegeGalleryLayout;
import uy.santipdr.siege.client.SiegeGalleryLayout.Rect;
import uy.santipdr.siege.client.SiegeIntelLayout;
import uy.santipdr.siege.client.IntelSearch;

/** Tests production geometry, including Minecraft's minimum logical viewport and odd sizes. */
public class UiRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }
    private static boolean overlaps(Rect a, Rect b) {
        return a.x() < b.right() && a.right() > b.x() && a.y() < b.bottom() && a.bottom() > b.y();
    }
    private static void inside(Rect r, int w, int h) {
        check(r.x() >= 0 && r.y() >= 0 && r.w() > 0 && r.h() > 0 && r.right() <= w && r.bottom() <= h,
                "Outside viewport " + w + "x" + h + ": " + r);
    }
    public static void main(String[] args) {
        int cases = 0;
        for (int w = 320; w <= 2560; w += 17) for (int h = 240; h <= 1440; h += 19) {
            var gallery = SiegeGalleryLayout.of(w, h);
            Rect[] regions = {gallery.heading(), gallery.preview(), gallery.thumbnails(), gallery.actions()};
            for (int i = 0; i < regions.length; i++) {
                inside(regions[i], w, h);
                for (int j = i + 1; j < regions.length; j++) check(!overlaps(regions[i], regions[j]), "Gallery overlap");
            }
            for (int i = 0; i < gallery.capacity(); i++) {
                Rect tile = gallery.tile(i);
                inside(tile, w, h);
                check(tile.h() > 20, "Thumbnail image has no height");
                check(tile.x() >= gallery.thumbnails().x() && tile.right() <= gallery.thumbnails().right()
                        && tile.y() >= gallery.thumbnails().y() && tile.bottom() <= gallery.thumbnails().bottom(), "Tile outside rail");
                for (int j = i + 1; j < gallery.capacity(); j++) check(!overlaps(tile, gallery.tile(j)), "Tile overlap");
            }
            for (int i = 0; i < 4; i++) { inside(gallery.action(i), w, h); check(gallery.action(i).w() >= 60, "Action too narrow"); }
            var intel = SiegeIntelLayout.of(w, h, 7);
            check(intel.contentTop() + 34 + 55 < h - 24, "Intel reading area lost at " + w + "x" + h);
            if (intel.wide()) check(intel.listTop() + 44 <= intel.listBottom(), "Wide list cannot fit one entry");
            else check(intel.listTop() - 24 >= intel.categoryTop() + ((7 + intel.columns() - 1) / intel.columns()) * (intel.categoryHeight() + 2), "Search overlaps categories");
            cases++;
        }
        int[][] resolutions = {{1280,720},{1366,768},{1920,1080},{2560,1440},{3440,1440}};
        for (int[] r : resolutions) for (int requested = 1; requested <= 4; requested++) {
            int scale = 1;
            while (scale < requested && r[0] / (scale + 1) >= 320 && r[1] / (scale + 1) >= 240) scale++;
            int w = (r[0] + scale - 1) / scale, h = (r[1] + scale - 1) / scale;
            var g = SiegeGalleryLayout.of(w, h);
            inside(g.preview(), w, h);
            System.out.println(r[0] + "x" + r[1] + " GUI " + requested + " -> " + w + "x" + h + " OK");
        }
        check(IntelSearch.matches("canON", "Combate en el cañón"), "Accent/case search");
        check(IntelSearch.matches("  misil   nusia ", "Nusia despliega un misil"), "Multi-token search");
        check(IntelSearch.matches("", "Patriot"), "Empty search");
        check(!IntelSearch.matches(".*", "Patriot"), "Query must be literal");
        check(!IntelSearch.matches("sniper", "Patriot"), "Unrelated search");
        System.out.println(cases + " viewport layouts and search regressions passed");
    }
}
