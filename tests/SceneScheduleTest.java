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
        System.out.println("Scene schedule: shuffled bags, no easter-egg leakage, bounds and continuity passed");
    }
}
