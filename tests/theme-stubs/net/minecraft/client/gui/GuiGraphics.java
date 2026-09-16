package net.minecraft.client.gui;
import java.util.ArrayList;
import java.util.List;
public class GuiGraphics {
    public record Fill(int x, int y, int right, int bottom, int color) { }
    public final List<Fill> fills = new ArrayList<>();
    public void fill(int x, int y, int right, int bottom, int color) {
        if (right < x || bottom < y) throw new AssertionError("Inverted fill");
        fills.add(new Fill(x, y, right, bottom, color));
    }
}
