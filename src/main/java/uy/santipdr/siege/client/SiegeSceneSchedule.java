package uy.santipdr.siege.client;

/** Stable slot selection shared by the displayed scene and its incoming crossfade. */
public final class SiegeSceneSchedule {
    public static final int COUNT = 13;
    private SiegeSceneSchedule() { }
    public static int index(long slot, boolean surprises) {
        int phase = (int)Math.floorMod(slot, 100L);
        // Two isolated 24-second appearances per 100 slots. Calm mode excludes surprises.
        if (surprises && (phase == 17 || phase == 73)) return 12;
        if (phase == 8 || phase == 26 || phase == 44 || phase == 62 || phase == 80 || phase == 98) return 11;
        return (int)Math.floorMod(slot, 11L);
    }
}
