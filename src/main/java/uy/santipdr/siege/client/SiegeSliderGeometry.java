package uy.santipdr.siege.client;

/** Pointer mapping shared by the click and drag paths. */
public final class SiegeSliderGeometry {
    private SiegeSliderGeometry() { }

    public static int percent(double pointer, int left, int width) {
        return percent(pointer, left, width, 0.0D);
    }

    public static int percent(double pointer, int left, int width, double grabOffset) {
        if (!Double.isFinite(pointer) || !Double.isFinite(grabOffset)) return 0;
        double fraction = (pointer - grabOffset - trackLeft(left)) / trackLength(width);
        return (int)Math.round(Math.max(0, Math.min(1, fraction)) * 100);
    }

    public static double knobCenter(int left, int width, int percent) {
        int clamped = Math.max(0, Math.min(100, percent));
        return trackLeft(left) + trackLength(width) * clamped / 100.0D;
    }

    public static double grabOffset(double pointer, int left, int width, int percent, double radius) {
        if (!Double.isFinite(pointer) || !Double.isFinite(radius) || radius < 0) return 0.0D;
        double center = knobCenter(left, width, percent);
        return Math.abs(pointer - center) <= radius ? pointer - center : 0.0D;
    }

    private static double trackLeft(int left) {
        return left + 9.0D;
    }

    private static double trackLength(int width) {
        return Math.max(1.0D, width - 18.0D);
    }
}
