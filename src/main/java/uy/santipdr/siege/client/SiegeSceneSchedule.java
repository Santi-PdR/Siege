package uy.santipdr.siege.client;

/** Stable slot selection shared by the displayed scene and its incoming crossfade. */
public final class SiegeSceneSchedule {
    public static final int COUNT = SiegeSceneCatalog.count();
    private SiegeSceneSchedule() { }

    public static int index(long slot, boolean surprises) {
        int phase = (int)Math.floorMod(slot, 100L);
        int anomaly = SiegeSceneCatalog.anomalyIndex();
        int featured = SiegeSceneCatalog.featuredIndex();

        // Two isolated 24-second anomaly appearances per 100 slots.
        // Comfort modes exclude the anomaly without disturbing the deterministic sequence.
        if (surprises && anomaly >= 0 && (phase == 17 || phase == 73)) return anomaly;
        if (featured >= 0 && (phase == 8 || phase == 26 || phase == 44 || phase == 62 || phase == 80 || phase == 98))
            return featured;

        int standardCount = Math.max(1, COUNT - (anomaly >= 0 ? 1 : 0) - (featured >= 0 ? 1 : 0));
        return (int)Math.floorMod(slot, standardCount);
    }
}
