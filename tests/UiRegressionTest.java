import uy.santipdr.siege.client.SiegeGalleryLayout;
import uy.santipdr.siege.client.SiegeGalleryLayout.Rect;
import uy.santipdr.siege.client.SiegeIntelLayout;
import uy.santipdr.siege.client.IntelSearch;
import uy.santipdr.siege.client.IntelEntry;
import java.util.List;
import uy.santipdr.siege.client.SiegeImageViewport;
import uy.santipdr.siege.client.SiegeUiLayout;
import uy.santipdr.siege.client.IntelPresentation;

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
            var intel = SiegeIntelLayout.of(w, h, 6);
            int readingHeight = h - 32 - intel.contentTop() - 34;
            if (readingHeight >= 150) {
                int imageW = SiegeIntelLayout.portraitWidth(w - intel.sidebarWidth() - 54, readingHeight);
                check(imageW * 9 / 16 + 3 + 8 + 52 <= readingHeight, "Portrait must leave readable tactical brief space");
            }
            check(intel.wide(), "Dossier must remain on the right at every GUI scale");
            check(w - intel.sidebarWidth() - 30 >= 178, "Dossier too narrow");
            check(intel.categoryTop() + 6 * (intel.categoryHeight() + (intel.ultraCompact() ? 2 : 3)) <= intel.listTop() - 18, "Categories overlap navigator");
            check(intel.contentTop() >= 50 + 22 + 18 + 6, "Tools overlap paper");
            check(intel.contentTop() + 34 + 55 < h - 24, "Intel reading area lost at " + w + "x" + h);
            if (intel.wide()) check(intel.listTop() + 44 <= intel.listBottom(), "Wide list cannot fit one entry");
            else check(intel.listTop() - 24 >= intel.categoryTop() + ((6 + intel.columns() - 1) / intel.columns()) * (intel.categoryHeight() + 2), "Search overlaps categories");
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
        check(IntelSearch.matches("\"alerta maxima\"", "Estado: ALERTA MÁXIMA"), "Quoted phrase search");
        check(IntelSearch.matches("atlas !nusia", "Atlas Super Unit"), "Excluded search token");
        check(!IntelSearch.matches("atlas !nusia", "Atlas de Nusia"), "Excluded search match");
        check(IntelSearch.matches("125000000", "HP 125,000,000"), "Numeric separator search");
        check(IntelSearch.matches("super unit", "SUPER-UNIT"), "Hyphen-insensitive search");
        check(IntelSearch.matches("  atlas\t super  ", "Atlas Super Unit"), "Whitespace-normalized search");
        check(IntelSearch.matches(null, "Atlas"), "Null query must be empty");
        check(!IntelSearch.matches("\"missing phrase", "Atlas"), "Unclosed quote must not discard query");
        check(IntelSearch.matches("“alerta máxima”", "Estado: alerta maxima"), "Smart quotes");
        check(IntelSearch.matches("atlas\u00a0unit", "Atlas Super Unit"), "NBSP tokens");
        check(IntelSearch.matches("ＡＴＬＡＳ", "Atlas"), "Compatibility normalization");
        check(IntelSearch.matches("SUP—001", "SUP-001"), "Unicode code dash");
        check(IntelSearch.matches("at\u200blas", "Atlas"), "Invisible pasted marker");
        check(IntelSearch.matches("!", "Atlas"), "Empty exclusion ignored");
        check(IntelSearch.matches("atlas atlas", "Atlas"), "Duplicate tokens");
        check(!IntelSearch.matches("!\"super unit", "Atlas super unit"), "Unclosed excluded phrase");
        SiegeImageViewport uninitialized = new SiegeImageViewport();
        check(Double.isFinite(uninitialized.visibleLeft()), "Camera valid before resize");
        uninitialized.resize(Double.NaN, Double.POSITIVE_INFINITY);
        check(Double.isFinite(uninitialized.imageWidth()), "Invalid dimensions repaired");
        check(!uninitialized.zoomAt(Double.NaN, 0, 0), "Invalid zoom ignored");
        uninitialized.drag(Double.NaN, 1);
        uninitialized.centerOn(Double.POSITIVE_INFINITY, 0);
        check(Double.isFinite(uninitialized.x()), "Invalid pan inputs ignored");
        check(IntelPresentation.compactHp("1,250").equals("1.2K"), "Compact thousands");
        check(IntelPresentation.compactHp("125,000,000").equals("125M"), "Compact millions");
        check(IntelPresentation.hpValue("N/D").signum() == 0, "Unknown HP parsing");
        check(java.util.stream.Stream.of("UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT")
                .map(IntelPresentation::accent).distinct().count() == 6, "Category accents must be distinct");
        SiegeImageViewport camera = new SiegeImageViewport();
        camera.resize(640, 360);
        double beforeU = (400 - camera.x()) / camera.imageWidth();
        double beforeV = (200 - camera.y()) / camera.imageHeight();
        camera.zoomAt(2, 400, 200);
        check(Math.abs(beforeU - (400 - camera.x()) / camera.imageWidth()) < 1e-9, "Zoom lost pointer X");
        check(Math.abs(beforeV - (200 - camera.y()) / camera.imageHeight()) < 1e-9, "Zoom lost pointer Y");
        camera.centerOn(1, 1);
        check(Math.abs(camera.visibleRight() - 1) < 1e-9, "Minimap right edge");
        check(Math.abs(camera.visibleBottom() - 1) < 1e-9, "Minimap bottom edge");
        camera.drag(1_000_000, 1_000_000);
        check(Math.abs(camera.visibleLeft()) < 1e-9 && Math.abs(camera.visibleTop()) < 1e-9, "Pan bounds");
        camera.zoomAt(99, 320, 180);
        check(camera.zoom() == 4, "Maximum zoom");
        check(!camera.zoomAt(99, 320, 180), "Zoom at limit should not trigger audio");
        for (int[] size : new int[][] {{304,165}, {464,195}, {624,285}, {1280,720}, {300,900}}) {
            camera.resize(size[0], size[1]);
            for (int direction : new int[] {-1, 1}) {
                camera.drag(direction * 1_000_000, direction * 1_000_000);
                check(camera.visibleLeft() >= 0 && camera.visibleRight() <= 1 && camera.visibleLeft() < camera.visibleRight(), "Visible X bounds after resize");
                check(camera.visibleTop() >= 0 && camera.visibleBottom() <= 1 && camera.visibleTop() < camera.visibleBottom(), "Visible Y bounds after resize");
            }
        }
        camera.reset();
        check(camera.zoom() == 1 && camera.visibleLeft() == 0 && camera.visibleRight() == 1
                && camera.visibleTop() == 0 && camera.visibleBottom() == 1, "Fit must show full artwork");
        for (int w = 320; w <= 2560; w += 13) for (int h = 240; h <= 1440; h += 17) {
            boolean compact = SiegeUiLayout.compactTitle(w, h);
            int menu = Math.min(compact ? 176 : 212, Math.max(138, w / (compact ? 2 : 5)));
            int title = SiegeUiLayout.centeredTitleWidth(w, compact, menu);
            check(title > 0 && title <= w, "Centered title outside viewport");
            int musicY = SiegeUiLayout.musicButtonY(h, compact);
            check(musicY >= 4 && musicY + 20 <= h, "Music control outside viewport");
            int notice = SiegeUiLayout.trackNoticeWidth(w, compact ? 9 : 14, menu, compact);
            check(notice == 0 || notice >= 96, "Unreadable track notice");
            check(SiegeUiLayout.settingsColumns(Math.max(230, w - 28)) >= 2, "Settings columns");
            int viewport = Math.max(1, h - 100);
            int thumb = SiegeUiLayout.scrollThumb(viewport, viewport + 400);
            check(thumb >= 10 && thumb <= viewport, "Invalid scrollbar thumb");
            check(SiegeUiLayout.clampScroll(-20, 100) == 0 && SiegeUiLayout.clampScroll(120, 100) == 100,
                    "Scroll clamp");
        }
        System.out.println("Pointer zoom, minimap, pan limits and resize passed");
        System.out.println(cases + " viewport layouts and search regressions passed");
    }
}

