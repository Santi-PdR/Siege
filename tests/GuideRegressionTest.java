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
            int tabCount = SiegeGuideData.Category.values().length;
            for (int i = 0; i < tabCount; i++) {
                int x = 8 + i % l.columns() * (l.tabWidth() + 4);
                int y = 34 + i / l.columns() * 22;
                check(x + l.tabWidth() <= w - 8 && y + 18 < l.searchY(), "tab bounds");
            }
            if (w >= 560) check(l.columns() == tabCount, "wide guide keeps one category row");
            else check(l.columns() <= 3, "narrow guide limits category columns");
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
        check(SiegeGuideData.ENTRIES.size() == 30, "30 reference entries");
        int[] expected = {1,4,3,2,13,7};
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
        check(SiegeGuideData.entries(SiegeGuideData.Category.OPERATIONS, "Núcleo", true).size() == 1, "search reactive AI briefing");
        check(SiegeGuideData.entries(SiegeGuideData.Category.OPERATIONS, "interdimensional", true).size() == 1, "search Rift briefing");
        check(SiegeGuideData.entries(SiegeGuideData.Category.OPERATIONS, "3.000 HP", true).size() == 1, "search Agreement briefing");

        // 0.30 source boundary: unverified Agreement/Gate/Rift material must stay
        // in Operations, where it can keep provenance without entering Intel.
        var agreementReport = SiegeGuideData.ENTRIES.stream().filter(e -> e.id().equals("agreement-field-report")).findFirst().orElseThrow();
        var gatesRifts = SiegeGuideData.ENTRIES.stream().filter(e -> e.id().equals("gates-rifts")).findFirst().orElseThrow();
        check(agreementReport.category() == SiegeGuideData.Category.OPERATIONS && gatesRifts.category() == SiegeGuideData.Category.OPERATIONS,
                "Agreement field intelligence moved out of Operations");
        check(agreementReport.body(true).contains("REPORTE SIN VERIFICAR") && agreementReport.body(false).contains("UNVERIFIED REPORT"),
                "Agreement field report lost provenance");
        check(agreementReport.body(true).contains("Rick Sanchez") && agreementReport.body(true).contains("3.000 HP"),
                "Agreement field report lost retained context");
        check(gatesRifts.body(true).contains("GATES") && gatesRifts.body(true).contains("RIFTS") && gatesRifts.body(true).contains("VISORES"),
                "Gate/Rift field briefing incomplete");
        check(gatesRifts.body(false).contains("GATES") && gatesRifts.body(false).contains("RIFTS") && gatesRifts.body(false).contains("VISORS"),
                "English Gate/Rift field briefing incomplete");

        String race = SiegeGuideData.ENTRIES.get(0).body(true);
        for (String word : new String[] {"Shadow", "INDEPENDIENTE", "Bloodluck", "@romax141403"}) check(race.contains(word), "race rule " + word);
        check(!race.contains("Original Cost") && !race.contains("CAPTURA DEL OBJETO"), "no technical screenshot transcription");
        String allSpanish = SiegeGuideData.ENTRIES.stream().map(e -> e.body(true)).reduce("", (a, b) -> a + " " + b);
        for (String forbidden : new String[] {"aportad", "Canal Summary", "skullsitox", "littleskull", "emoji", "Discord"})
            check(!allSpanish.contains(forbidden), "no out-of-world label " + forbidden);
        check(SiegeGuideData.entries(SiegeGuideData.Category.INSPIRATIONS, "economía de guerra", true).size() == 1, "inspiration lore searchable");
        check(SiegeGuideData.ENTRIES.stream().filter(e -> e.id().equals("ending-aurelionis")).findFirst().orElseThrow().body(true).contains("escapar con vida"), "Aurelionis alive");
        check(SiegeGuideData.ENTRIES.stream().filter(e -> e.id().equals("ending-hermes")).findFirst().orElseThrow().body(true).contains("No se encontró su cuerpo"), "Hermes uncertainty");
        System.out.println("Guide: " + layouts + " layouts, 30 bilingual records, 10 original PNGs, operations source boundary, spoiler and source rules passed");
    }
}
