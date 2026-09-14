import uy.santipdr.siege.client.SiegeMultiplayerLayout;
public final class MultiplayerLayoutTest {
    public static void main(String[] args) {
        int cases = 0;
        for (int w = 320; w <= 1920; w += 8) for (int h = 240; h <= 1080; h += 8) {
            var l = SiegeMultiplayerLayout.of(w, h);
            check(l.x() >= 0 && l.x() + l.width() <= w, "horizontal bounds");
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
            cases++;
        }
        System.out.println("Multiplayer layout passed " + cases + " logical sizes; in-game verification still required.");
    }
    static void check(boolean ok, String label) { if (!ok) throw new AssertionError(label); }
}
