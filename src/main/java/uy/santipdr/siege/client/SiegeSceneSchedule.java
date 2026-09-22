package uy.santipdr.siege.client;

/** Stable slot selection shared by the displayed scene and its incoming crossfade. */
public final class SiegeSceneSchedule {
    public static final int COUNT = SiegeSceneCatalog.count();
    private SiegeSceneSchedule() { }

    public static int index(long slot, boolean surprises) {
        int phase = (int)Math.floorMod(slot, 100L);
        int featured = SiegeSceneCatalog.featuredIndex();

        // 2.50 keeps the actual menu rotation clean. Easter-egg art is not part
        // of SiegeSceneCatalog, so the surprise flag no longer changes menu art.
        // The parameter remains for binary/source compatibility with callers.
        if (featured >= 0 && (phase == 8 || phase == 26 || phase == 44 || phase == 62 || phase == 80 || phase == 98))
            return featured;

        return standardIndex(slot);
    }

    /**
     * Deterministic shuffled-bag selection for normal scenes.
     * Every standard scene appears once before the permutation repeats, while
     * each cycle starts from a shifted position so the gallery does not fall
     * back into a simple numeric sequence. Consecutive repeats are impossible.
     */
    public static int standardIndex(long slot) {
        int count = Math.max(1, SiegeSceneCatalog.standardCount());
        long cycle = Math.floorDiv(slot, count);
        int position = Math.floorMod(slot, count);
        int step = permutationStep(count);
        int shift = step == count - 1 && count > 2 ? 2 : 1;
        int offset = (int)Math.floorMod(cycle * shift, count);
        int ordinal = Math.floorMod(offset + position * step, count);
        return SiegeSceneCatalog.standardIndex(ordinal);
    }

    private static int permutationStep(int count) {
        if (count <= 2) return 1;
        for (int candidate : new int[]{7, 5, 3, 2, 1}) {
            if (candidate < count && gcd(candidate, count) == 1) return candidate;
        }
        return 1;
    }

    private static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
