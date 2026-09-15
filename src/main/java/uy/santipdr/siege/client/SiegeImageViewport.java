package uy.santipdr.siege.client;

/** Bounded image camera. Zoom preserves the image point under the pointer when edges allow it. */
public final class SiegeImageViewport {
    private double width = 1, height = 1, zoom = 1, panX, panY;
    public void resize(double width, double height) {
        double oldFit = fit();
        this.width = Double.isFinite(width) ? Math.max(1, width) : 1;
        this.height = Double.isFinite(height) ? Math.max(1, height) : 1;
        if (oldFit > 0) { panX *= fit() / oldFit; panY *= fit() / oldFit; }
        clamp();
    }
    public double fit() { return Math.min(width / 640.0, height / 360.0); }
    public double zoom() { return zoom; }
    public double imageWidth() { return 640 * fit() * zoom; }
    public double imageHeight() { return 360 * fit() * zoom; }
    public double x() { return (width - imageWidth()) / 2 + panX; }
    public double y() { return (height - imageHeight()) / 2 + panY; }
    public void reset() { zoom = 1; panX = panY = 0; }
    public boolean zoomAt(double value, double pointerX, double pointerY) {
        if (!Double.isFinite(value) || !Double.isFinite(pointerX) || !Double.isFinite(pointerY)) return false;
        double next = Math.max(1, Math.min(4, value));
        if (Math.abs(next - zoom) < 0.0001) return false;
        double ratio = next / zoom;
        panX = pointerX - width / 2 - (pointerX - width / 2 - panX) * ratio;
        panY = pointerY - height / 2 - (pointerY - height / 2 - panY) * ratio;
        zoom = next;
        clamp();
        return true;
    }
    public void drag(double dx, double dy) { if (!Double.isFinite(dx) || !Double.isFinite(dy)) return; panX += dx; panY += dy; clamp(); }
    public void centerOn(double u, double v) {
        if (!Double.isFinite(u) || !Double.isFinite(v)) return;
        panX = (0.5 - Math.max(0, Math.min(1, u))) * imageWidth();
        panY = (0.5 - Math.max(0, Math.min(1, v))) * imageHeight();
        clamp();
    }
    public double visibleLeft() { return Math.max(0, -x() / imageWidth()); }
    public double visibleTop() { return Math.max(0, -y() / imageHeight()); }
    public double visibleRight() { return Math.min(1, (width - x()) / imageWidth()); }
    public double visibleBottom() { return Math.min(1, (height - y()) / imageHeight()); }
    private void clamp() {
        double maxX = Math.max(0, (imageWidth() - width) / 2);
        double maxY = Math.max(0, (imageHeight() - height) / 2);
        panX = Math.max(-maxX, Math.min(maxX, panX));
        panY = Math.max(-maxY, Math.min(maxY, panY));
    }
}

