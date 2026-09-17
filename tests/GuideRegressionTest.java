import uy.santipdr.siege.client.SiegeGuideData;
import uy.santipdr.siege.client.SiegeGuideLayout;
import java.nio.file.Path;
import java.util.HashSet;
import javax.imageio.ImageIO;

public final class GuideRegressionTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }
    public static void main(String[] args) throws Exception {
        int layouts = 0;
        for (int w = 320; w <= 1920; w += 7) for (int h = 180; h <= 1080; h += 7) {
            var l = SiegeGuideLayout.of(w, h);
            check(l.tabWidth() > 0 && l.capacity() > 0, "positive controls");
            check(l.list().right() < l.article().x(), "columns separated");
            check(l.article().right() <= w - 8, "right margin");
            check(l.article().bottom() < l.footerY(), "footer separated");
            check(l.list().y() >= l.searchY() + 22, "search separated");
            check(l.list().y() + (l.capacity() - 1) * 24 + 20 <= l.list().bottom(), "list rows contained");
            check(l.article().y() + Math.min(21, l.article().h() - 20) + 18 <= l.article().bottom() - 2, "spoiler button contained");
            for (int i = 0; i < 5; i++) {
                int x = 8 + i % l.columns() * (l.tabWidth() + 4);
                int y = 34 + i / l.columns() * 22;
                check(x + l.tabWidth() <= w - 8 && y + 18 < l.searchY(), "tab bounds");
            }
            for (int[] image : new int[][] {{1528,918},{353,158},{1280,600},{1360,768}}) {
                var area = l.article();
                var box = SiegeGuideLayout.fit(area, image[0], image[1]);
                check(box.x() >= area.x() && box.y() >= area.y() && box.right() <= area.right() && box.bottom() <= area.bottom(), "image contained");
                double scale = Math.min(area.w() / (double)image[0], area.h() / (double)image[1]);
                check(Math.abs(box.w() - scale * image[0]) < 1.01 && Math.abs(box.h() - scale * image[1]) < 1.01, "aspect fit, no crop");
            }
            layouts++;
        }
        var ids = new HashSet<String>();
        var files = new HashSet<String>();
        check(SiegeGuideData.ENTRIES.size() == 27, "27 reference entries");
        int[] expected = {1,4,2,13,7};
        for (var category : SiegeGuideData.Category.values())
            check(SiegeGuideData.entries(category, "", true).size() == expected[category.ordinal()], "category count " + category);
        for (var e : SiegeGuideData.ENTRIES) {
            check(ids.add(e.id()), "unique ID " + e.id());
            check(!e.title(true).isBlank() && !e.title(false).isBlank() && !e.body(true).isBlank() && !e.body(false).isBlank(), "bilingual " + e.id());
            check(e.spoiler() == (e.category() == SiegeGuideData.Category.CHRONICLES), "all chronicles gated");
            for (var art : e.images()) {
                check(files.add(art.file()), "unique source " + art.file());
                var png = ImageIO.read(Path.of("src/main/resources/assets/siege/textures/gui/guide", art.file()).toFile());
                check(png != null && png.getWidth() == art.width() && png.getHeight() == art.height(), "PNG dimensions " + art.file());
            }
        }
        check(files.size() == 10, "ten original assets");
        check(SiegeGuideData.entries(SiegeGuideData.Category.CHRONICLES, "Luna", true).isEmpty(), "spoiler body hidden from search");
        check(SiegeGuideData.entries(SiegeGuideData.Category.CHRONICLES, "AURELIONIS", true).size() == 1, "search chronicle title");
        check(SiegeGuideData.entries(SiegeGuideData.Category.ITEMS, "Shadow", true).size() == 1, "search item body");
        String race = SiegeGuideData.ENTRIES.get(0).body(true);
        for (String word : new String[] {"Shadow", "INDEPENDIENTE", "Bloodluck", "@romax141403", "sin asumir que 2"}) check(race.contains(word), "race rule " + word);
        check(SiegeGuideData.ENTRIES.stream().filter(e -> e.id().equals("ending-aurelionis")).findFirst().orElseThrow().body(true).contains("escapar con vida"), "Aurelionis alive");
        check(SiegeGuideData.ENTRIES.stream().filter(e -> e.id().equals("ending-hermes")).findFirst().orElseThrow().body(true).contains("No se encontró su cuerpo"), "Hermes uncertainty");
        System.out.println("Guide: " + layouts + " layouts, 27 bilingual records, 10 original PNGs, spoiler and source rules passed");
    }
}
