import uy.santipdr.siege.client.SiegeMediaReferenceData;

public final class MediaReferenceRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        var tracks = SiegeMediaReferenceData.dvnTracks();
        check(tracks.size() >= 6, "DVN soundtrack reference list unexpectedly small");
        for (String title : new String[] {"Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir"})
            check(tracks.stream().anyMatch(t -> t.title().equals(title)), "Missing DVN media reference: " + title);
        check(tracks.stream().allMatch(t -> !t.title().isBlank() && !t.note(false).isBlank()),
                "Every external soundtrack reference needs a title and policy note");

        String policy = tracks.stream().map(t -> t.note(false).toLowerCase()).reduce("", (a, b) -> a + " " + b);
        check(policy.contains("not automatically bundled") || policy.contains("not bundled"),
                "Reference catalog must explicitly say external audio is not bundled automatically");
        check(policy.contains("requires a clear source/license") || policy.contains("without verifying audio rights"),
                "Reference catalog must preserve the licensing/rights boundary");

        check(SiegeMediaReferenceData.visualReferences().size() >= 4, "Visual direction list unexpectedly small");
        check(SiegeMediaReferenceData.visualReferences().stream().anyMatch(v -> v.note(false).contains("16:9")),
                "Visual references must retain HD/aspect-ratio direction");
        System.out.println("SIEGE 4.00 media references: DVN soundtrack names and no-auto-bundle policy passed");
    }
}
