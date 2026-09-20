import uy.santipdr.siege.client.SiegeMultiplayerLayout;
public final class MultiplayerLayoutTest {
    private static void validate(int w, int h) {
        var l = SiegeMultiplayerLayout.of(w, h);
        check(l.x() >= 6 && l.x() + l.width() <= w - 6, "horizontal safe bounds " + w + "x" + h);
        if (w >= 520 && l.width() < 700) check(l.x() >= 10 && l.x() + l.width() <= w - 10,
                "wide-screen safe margin");
        check(l.bottom() - l.top() >= 72, "two visible server rows");
        check(l.bottom() + 14 < l.firstRow(), "status separate from controls");
        check(l.firstRow() + 24 <= l.secondRow() && l.secondRow() + 24 <= h, "button shadows");
        check(l.listWidth() >= 308, "native entry width");
        check(l.listCenterX() >= l.x() && l.listCenterX() < l.x() + l.listWidth(), "LAN center in list");
        if (l.detailWidth() > 0) check(l.listCenterX() != w / 2, "LAN center independent from screen");
        if (l.detailWidth() > 0) {
            check(l.detailX() >= l.x() + l.listWidth() + 12, "panel separation");
            check(l.detailX() + l.detailWidth() <= w, "details bounds");
        }
        for (int count : new int[]{3,4}) for (int i = 0; i < count; i++) {
            check(l.buttonWidth(i, count) >= 65, "minimum button width");
            check(l.buttonX(i,count) + l.buttonWidth(i,count) <= l.x() + l.width(), "button bound");
            if (i + 1 < count) check(l.buttonX(i,count) + l.buttonWidth(i,count) + 4
                    < l.buttonX(i+1,count), "button separation");
        }
    }

    public static void main(String[] args) {
        int cases = 0;
        for (int w = 320; w <= 2560; w += 8) for (int h = 240; h <= 1440; h += 8) {
            validate(w, h);
            cases++;
        }
        int[] widths = {320,359,360,519,520,639,640,641,699,700,701,1280,1920,2560,3440,5120};
        int[] heights = {240,289,290,299,300,329,330,349,350,354,355,480,720,1080,1440,2160};
        for (int w : widths) for (int h : heights) {
            validate(w, h);
            cases++;
        }
        System.out.println("Multiplayer layout passed " + cases + " logical sizes and responsive boundaries; in-game verification still required.");
    }
    static void check(boolean ok, String label) { if (!ok) throw new AssertionError(label); }
}
