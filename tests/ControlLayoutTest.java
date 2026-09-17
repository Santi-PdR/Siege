import uy.santipdr.siege.client.SiegeControlLayout;
public class ControlLayoutTest {
    public static void main(String[] args) {
        for (int w = 1; w <= 1200; w++) for (int state = 0; state <= 240; state += 6)
            for (boolean selected : new boolean[]{false, true}) {
                var p = SiegeControlLayout.of(w, w >= 96, selected, state);
                if (p.labelX() < 0 || p.labelWidth() < 0 || p.labelX() + p.labelWidth() > w)
                    throw new AssertionError("Label bounds " + w);
                if (p.badgeWidth() > 0 && (p.labelX() + p.labelWidth() > p.badgeX() || p.badgeX() + p.badgeWidth() > w))
                    throw new AssertionError("State overlap " + w);
            }
        System.out.println("98,400 control label/state layouts passed");
    }
}
