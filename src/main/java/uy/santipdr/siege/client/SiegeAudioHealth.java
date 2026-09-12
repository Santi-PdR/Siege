package uy.santipdr.siege.client;

/** Debounces backend inactivity and bounds recovery attempts without advancing shuffle. */
public final class SiegeAudioHealth {
    private long requestedAt, missingAt = -1;
    private int retries;
    public void begin(long now) { requestedAt = now; missingAt = -1; retries = 0; }
    public boolean recover(long now, boolean playing) {
        if (playing) { missingAt = -1; return false; }
        if (now - requestedAt < 5000 || retries >= 2) return false;
        if (missingAt < 0) { missingAt = now; return false; }
        if (now - missingAt < 3000) return false;
        retries++; requestedAt = now; missingAt = -1;
        return true;
    }
    public int retries() { return retries; }
}
