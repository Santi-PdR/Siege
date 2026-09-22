import uy.santipdr.siege.client.SiegeMediaReferenceData;

public final class MediaReferenceRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        var tracks = SiegeMediaReferenceData.dvnTracks();
        check(tracks.size() >= 6, "DVN soundtrack reference list unexpectedly small");
        for (String title : new String[] {"Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir"})
            check(tracks.stream().anyMatch(t -> t.title().equals(title)), "Missing DVN media reference: " + title);
        check(tracks.stream().allMatch(t -> t.note(false).toLowerCase().contains("not")
                        || t.note(false).toLowerCase().contains("requires")),
                "External audio references must state non-bundled/licensing boundary");
        check(SiegeMediaReferenceData.visualReferences().size() >= 4, "Visual direction list unexpectedly small");
        check(SiegeMediaReferenceData.visualReferences().stream().anyMatch(v -> v.note(false).contains("16:9")),
                "Visual references must retain HD/aspect-ratio direction");
        System.out.println("SIEGE 4.00 media references: DVN soundtrack names and no-auto-bundle policy passed");
    }
}
