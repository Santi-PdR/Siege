package uy.santipdr.siege.client;

/** Stable slot selection shared by the displayed scene and its incoming crossfade. */
public final class SiegeSceneSchedule {
    public static final int COUNT = SiegeSceneCatalog.count();
    private static final int[] FEATURED_PHASES = {8, 26, 44, 62, 80, 98};

    private SiegeSceneSchedule() { }

    public static int index(long slot, boolean surprises) {
        int phase = (int)Math.floorMod(slot, 100L);
        int featured = SiegeSceneCatalog.featuredIndex();

        // Easter-egg art remains outside SiegeSceneCatalog. Featured art is a normal,
        // explicitly catalogued scene inserted at six stable phases per 100 slots.
        // The surprise flag remains for binary/source compatibility with callers.
        if (featured >= 0 && isFeaturedPhase(phase)) return featured;

        // Featured inserts must not consume a standard-scene position. Compress the
        // global slot timeline into a contiguous standard-only ordinal before feeding
        // the shuffled bag, otherwise every insert silently skips one normal scene.
        return standardIndex(standardSlotOrdinal(slot));
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

    /** Number of standard-scene positions reached before/at a non-featured slot. */
    public static long standardSlotOrdinal(long slot) {
        long hundredBlock = Math.floorDiv(slot, 100L);
        int phase = (int)Math.floorMod(slot, 100L);
        long featuredBefore = hundredBlock * FEATURED_PHASES.length;
        for (int featuredPhase : FEATURED_PHASES) {
            if (featuredPhase >= phase) break;
            featuredBefore++;
        }
        return slot - featuredBefore;
    }

    public static boolean isFeaturedSlot(long slot) {
        return SiegeSceneCatalog.featuredIndex() >= 0 && isFeaturedPhase((int)Math.floorMod(slot, 100L));
    }

    private static boolean isFeaturedPhase(int phase) {
        for (int featuredPhase : FEATURED_PHASES) {
            if (phase == featuredPhase) return true;
        }
        return false;
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
