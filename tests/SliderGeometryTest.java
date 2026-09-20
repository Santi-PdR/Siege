import uy.santipdr.siege.client.SiegeSliderGeometry;
public class SliderGeometryTest {
    public static void main(String[] args) {
        int cases = 0;
        for (int width = 19; width <= 1000; width++) {
            int last = -1;
            for (int step = 0; step <= 100; step++) {
                double x = SiegeSliderGeometry.knobCenter(37, width, step);
                int value = SiegeSliderGeometry.percent(x, 37, width);
                if (value != step || value < last) throw new AssertionError(width + ": " + step);
                last = value;
                for (double offset : new double[] {-5.5, -2.0, 0.0, 2.0, 5.5}) {
                    double pointer = x + offset;
                    double grab = SiegeSliderGeometry.grabOffset(pointer, 37, width, step, 6.0);
                    int held = SiegeSliderGeometry.percent(pointer, 37, width, grab);
                    if (held != step) throw new AssertionError("grab jump " + width + ":" + step + ":" + offset + " -> " + held);
                    cases++;
                }
            }
            if (SiegeSliderGeometry.percent(-1000, 37, width) != 0 ||
                SiegeSliderGeometry.percent(10000, 37, width) != 100) throw new AssertionError("clamp");
            double far = SiegeSliderGeometry.knobCenter(37, width, 50) + 20.0;
            if (SiegeSliderGeometry.grabOffset(far, 37, width, 50, 6.0) != 0.0D)
                throw new AssertionError("far click must not preserve grab offset");
        }
        if (SiegeSliderGeometry.percent(Double.NaN, 0, 100) != 0) throw new AssertionError("NaN pointer");
        if (SiegeSliderGeometry.percent(20, 0, 100, Double.NaN) != 0) throw new AssertionError("NaN grab");
        System.out.println("Slider mapping and stable grab: " + cases + " held-pointer cases plus endpoint clamps passed");
    }
}
