import net.minecraft.client.gui.GuiGraphics;
import uy.santipdr.siege.client.SiegeTheme;
public class MenuThemeDrawingTest {
    private static void inside(GuiGraphics g, int x, int y, int w, int h) {
        for (var fill : g.fills) if (fill.x() < x || fill.y() < y || fill.right() > x + w || fill.bottom() > y + h)
            throw new AssertionError("Drawing outside its assigned bounds: " + fill);
    }
    public static void main(String[] args) {
        for (int w = 1; w < 800; w += 7) for (int h = 1; h < 600; h += 11) {
            GuiGraphics g = new GuiGraphics();
            SiegeTheme.panel(g, 9, 17, w, h, SiegeTheme.RED); inside(g, 9, 17, w, h);
            g.fills.clear(); SiegeTheme.paper(g, 9, 17, w, h, false); inside(g, 9, 17, w, h);
            for (var fill : g.fills) if (fill.y() >= 21 && fill.x() > 13 && fill.right() < 9 + w - 4)
                throw new AssertionError("Paper ornament invaded readable area");
        }
        for (String icon : new String[] {"connect", "intel", "settings", "music", "pin", "check", "play", "pause", "eye", "image", "shield", "overview", "lock", "search",
                "back", "globe", "keyboard", "package", "chat", "user", "world", "warning"}) {
            GuiGraphics g = new GuiGraphics(); SiegeTheme.icon(g, 0, 0, icon, SiegeTheme.RED); inside(g, 0, 0, 9, 9);
            if (g.fills.isEmpty()) throw new AssertionError("Empty icon " + icon);
        }
        System.out.println("Production panel, paper margins and tactical 9x9 icons stay inside their bounds");
    }
}
