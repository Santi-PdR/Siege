package uy.santipdr.siege.client;

/** Deterministic hover/manual/automatic rotation state; time is supplied by the screen. */
public final class SiegePreviewClock {
    private int index = -1;
    private boolean wasHovered;
    public boolean reading;
    public long started, deadline;
    public int update(int count, long now, boolean hovered, boolean pauseOnHover, boolean automatic) {
        if (count <= 0) return 0;
        if (index < 0) { index = 0; started = now; deadline = now + 8500; }
        reading = pauseOnHover && hovered;
        if (!automatic) { wasHovered = false; started = now; deadline = now + 8500; }
        else if (reading) wasHovered = true;
        else if (wasHovered) { wasHovered = false; started = now; deadline = now + 2000; }
        if (automatic && !reading && now >= deadline) {
            index = Math.floorMod(index + 1, count); started = now; deadline = now + 8500;
        }
        return Math.floorMod(index, count);
    }
    public void step(int count, long now, int direction) {
        if (count <= 0) return;
        index = Math.floorMod(Math.max(0, index) + direction, count);
        started = now; deadline = now + 15000;
    }
}
