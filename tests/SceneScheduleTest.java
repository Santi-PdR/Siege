import uy.santipdr.siege.client.SiegeSceneCatalog;
import uy.santipdr.siege.client.SiegeSceneSchedule;

import java.util.HashSet;
import java.util.Set;

public final class SceneScheduleTest {
    private static void check(boolean b, String message) { if (!b) throw new AssertionError(message); }

    public static void main(String[] args) {
        int anomalyIndex = SiegeSceneCatalog.anomalyIndex();
        int featuredIndex = SiegeSceneCatalog.featuredIndex();
        int standardCount = SiegeSceneCatalog.standardCount();
        check(SiegeSceneSchedule.COUNT == SiegeSceneCatalog.count(), "Schedule/catalog count drift");
        check(anomalyIndex < 0, "Easter-egg art must not be a normal scene");
        check(featuredIndex >= 0, "Featured scene missing");
        check(standardCount == SiegeSceneSchedule.COUNT - 1, "Standard scene count drift");
        check(!SiegeSceneCatalog.containsId("tempest_jutcherson"), "Tempest leaked into normal menu catalog");

        for (long cycle = -12; cycle <= 12; cycle++) {
            Set<Integer> bag = new HashSet<>();
            long start = cycle * standardCount;
            for (int i = 0; i < standardCount; i++) {
                int current = SiegeSceneSchedule.standardIndex(start + i);
                check(current >= 0 && current < SiegeSceneSchedule.COUNT, "Standard bounds");
                check(current != featuredIndex, "Featured scene leaked into standard bag");
                check(bag.add(current), "Standard scene repeated before shuffled bag was exhausted");
                check(current == SiegeSceneSchedule.standardIndex(start + i), "Stable standard selection");
            }
            check(bag.size() == standardCount, "Shuffled bag did not cover all standard scenes");
            check(SiegeSceneSchedule.standardIndex(start + standardCount - 1)
                            != SiegeSceneSchedule.standardIndex(start + standardCount),
                    "Cycle boundary repeated a scene");
        }

        for (long start : new long[]{-10000, 0, 10000, 74000000}) {
            int featured = 0;
            for (long slot = start; slot < start + 100; slot++) {
                int current = SiegeSceneSchedule.index(slot, true);
                check(current >= 0 && current < SiegeSceneSchedule.COUNT, "Bounds");
                check(current == SiegeSceneSchedule.index(slot, true), "Stable render selection");
                check(current == SiegeSceneSchedule.index(slot, false),
                        "Easter-egg surprise flag must not alter normal menu art");
                check(current != SiegeSceneSchedule.index(slot + 1, true), "No consecutive repeats");
                if (current == featuredIndex) featured++;
            }
            check(featured == 6, "Six featured slots per 100");
        }

        // 5.62: featured inserts are interleaved, not substituted for standard scenes.
        // Each start below maps to an exact shuffled-bag boundary in the compressed
        // standard-only timeline. Thirty consecutive bags must remain complete even
        // while featured scenes are inserted into the visible global schedule.
        for (long globalStart : new long[]{-2596L, 0L, 2404L, 74000000L}) {
            check(Math.floorMod(SiegeSceneSchedule.standardSlotOrdinal(globalStart), standardCount) == 0,
                    "Test start must align to a compressed standard-bag boundary");
            long slot = globalStart;
            for (int bagNumber = 0; bagNumber < 30; bagNumber++) {
                Set<Integer> bag = new HashSet<>();
                int standardSeen = 0;
                while (standardSeen < standardCount) {
                    if (!SiegeSceneSchedule.isFeaturedSlot(slot)) {
                        int current = SiegeSceneSchedule.index(slot, true);
                        check(current != featuredIndex, "Featured scene leaked into compressed standard bag");
                        check(bag.add(current), "Featured insertion skipped/repeated a standard scene");
                        standardSeen++;
                    }
                    slot++;
                }
                check(bag.size() == standardCount, "Compressed standard bag did not cover every scene");
            }
        }

        // Ordinals remain contiguous across the featured phase itself, including
        // negative slots where floorDiv/floorMod behavior is easy to get wrong.
        check(SiegeSceneSchedule.standardSlotOrdinal(7) == 7, "Pre-feature ordinal drift");
        check(SiegeSceneSchedule.standardSlotOrdinal(9) == 8, "Post-feature ordinal must compress phase 8");
        check(SiegeSceneSchedule.isFeaturedSlot(8), "Phase 8 should be featured");
        check(SiegeSceneSchedule.isFeaturedSlot(-2), "Negative phase 98 should be featured");
        check(SiegeSceneSchedule.standardSlotOrdinal(-3) == -2, "Negative pre-feature compression drift");
        check(SiegeSceneSchedule.standardSlotOrdinal(-1) == -1, "Negative post-feature compression drift");

        System.out.println("Scene schedule: complete shuffled bags survive featured inserts; bounds and continuity passed");
    }
}
