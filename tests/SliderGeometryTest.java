import uy.santipdr.siege.client.SiegeSliderGeometry;
public class SliderGeometryTest {
    public static void main(String[] args) {
        for (int width = 19; width <= 1000; width++) {
            int last = -1;
            for (int step = 0; step <= 100; step++) {
                double x = 37 + 9 + (width - 18) * step / 100.0;
                int value = SiegeSliderGeometry.percent(x, 37, width);
                if (value != step || value < last) throw new AssertionError(width + ": " + step);
                last = value;
            }
            if (SiegeSliderGeometry.percent(-1000, 37, width) != 0 ||
                SiegeSliderGeometry.percent(10000, 37, width) != 100) throw new AssertionError("clamp");
        }
        System.out.println("Slider mapping: 99,182 positions and endpoint clamps passed");
    }
}
