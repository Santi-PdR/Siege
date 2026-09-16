package uy.santipdr.siege.client;

/** Pointer mapping shared by the click and drag paths. */
public final class SiegeSliderGeometry {
    private SiegeSliderGeometry() { }
    public static int percent(double pointer, int left, int width) {
        if (!Double.isFinite(pointer)) return 0;
        double fraction = (pointer - left - 9.0) / Math.max(1, width - 18);
        return (int)Math.round(Math.max(0, Math.min(1, fraction)) * 100);
    }
}
