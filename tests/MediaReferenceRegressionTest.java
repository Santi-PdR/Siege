import uy.santipdr.siege.client.SiegeMediaReferenceData;

public final class MediaReferenceRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        var tracks = SiegeMediaReferenceData.dvnTracks();
        check(tracks.size() >= 6, "DVN soundtrack reference list unexpectedly small");
        for (String title : new String[] {"Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir"})
            check(tracks.stream().anyMatch(t -> t.title().equals(title)), "Missing DVN media reference: " + title);
        check(tracks.stream().allMatch(t -> !t.title().isBlank() && !t.note(false).isBlank()),
                "Every soundtrack recommendation needs a title and player-facing description");

        check(SiegeMediaReferenceData.externalBundlingRequiresPermission(),
                "External media must remain blocked from automatic redistribution");
        check(SiegeMediaReferenceData.preferredBackgroundWidth() >= 1920
                        && SiegeMediaReferenceData.preferredBackgroundHeight() >= 1080,
                "DVN background target must remain Full HD or better");

        check(SiegeMediaReferenceData.visualReferences().size() >= 6,
                "Visual direction list unexpectedly small");
        System.out.println("SIEGE 4.00 media recommendations and internal distribution policy passed");
    }
}
