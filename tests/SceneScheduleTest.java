import uy.santipdr.siege.client.SiegeSceneSchedule;
public final class SceneScheduleTest {
    private static void check(boolean b, String message) { if (!b) throw new AssertionError(message); }
    public static void main(String[] args) {
        for (long start : new long[]{-10000, 0, 10000, 74000000}) {
            int rare = 0, special = 0;
            for (long slot = start; slot < start + 100; slot++) {
                int current = SiegeSceneSchedule.index(slot, true);
                check(current >= 0 && current < SiegeSceneSchedule.COUNT, "Bounds");
                check(current == SiegeSceneSchedule.index(slot, true), "Stable render selection");
                check(current != SiegeSceneSchedule.index(slot + 1, true), "No consecutive repeats");
                check(SiegeSceneSchedule.index(slot, false) != 12, "Calm mode excludes meme");
                if (current == 12) rare++;
                if (current == 11) special++;
            }
            check(rare == 2, "Two rare slots per 100");
            check(special == 6, "Six featured slots per 100");
        }
        System.out.println("Scene schedule: rarity, bounds, continuity and calm mode passed");
    }
}
