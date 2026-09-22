import uy.santipdr.siege.client.SiegeMediaReferenceData;

public final class MediaReferenceRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        var tracks = SiegeMediaReferenceData.dvnTracks();
        check(tracks.size() >= 6, "DVN soundtrack reference list unexpectedly small");
        for (String title : new String[] {"Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir"})
            check(tracks.stream().anyMatch(t -> t.title().equals(title)), "Missing DVN media reference: " + title);
        check(tracks.stream().allMatch(t -> !t.title().isBlank() && !t.note(false).isBlank()),
                "Every soundtrack recommendation needs useful player-facing context");

        // The project does not fetch or bundle these external references by this data class.
        // Rights/provenance policy belongs in development docs/tests, not in normal player UI copy.
        check(SiegeMediaReferenceData.visualReferences().size() >= 4, "Visual direction list unexpectedly small");
        check(SiegeMediaReferenceData.visualReferences().stream().anyMatch(v ->
                        v.title(false).toLowerCase().contains("stronghold")
                                || v.note(false).toLowerCase().contains("main menu")),
                "Visual references must retain a stronghold/main-menu direction");
        System.out.println("SIEGE 4.00 media references: DVN soundtrack and visual recommendations passed");
    }
}
