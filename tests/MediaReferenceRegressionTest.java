import java.util.HashSet;
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

        var moods = SiegeMediaReferenceData.moods();
        check(moods.size() >= 4, "5.00 operational mood catalog unexpectedly small");
        check(moods.stream().anyMatch(m -> m.id().equals("stronghold")), "Stronghold mood missing");
        check(moods.stream().anyMatch(m -> m.id().equals("deployment")), "Deployment mood missing");
        check(moods.stream().anyMatch(m -> m.id().equals("intel")), "Intel mood missing");
        check(moods.stream().anyMatch(m -> m.id().equals("last-stand")), "Last Stand mood missing");
        check(moods.stream().allMatch(m -> !m.bundledTrack().isBlank() && !m.referenceTracks().isEmpty()),
                "Every mood needs an installed-track anchor and references");
        check(new HashSet<>(moods.stream().map(SiegeMediaReferenceData.Mood::id).toList()).size() == moods.size(),
                "Mood IDs must remain unique");

        // References are direction, not an excuse to silently inject third-party binaries.
        var visuals = SiegeMediaReferenceData.visualReferences();
        check(visuals.size() >= 10, "5.00 visual direction list unexpectedly small");
        check(visuals.stream().anyMatch(v -> v.id().equals("stronghold-defense")), "Stronghold visual direction missing");
        check(visuals.stream().anyMatch(v -> v.id().equals("arctic-standoff")), "Arctic direction missing");
        check(visuals.stream().anyMatch(v -> v.id().equals("industrial-zone")), "Industrial direction missing");
        check(visuals.stream().allMatch(v -> !v.id().isBlank() && !v.title(false).isBlank() && !v.note(false).isBlank()),
                "Every visual direction needs readable metadata");
        check(new HashSet<>(visuals.stream().map(SiegeMediaReferenceData.Visual::id).toList()).size() == visuals.size(),
                "Visual IDs must remain unique");
        System.out.println("SIEGE 5.00 media references, operational moods and visual direction passed");
    }
}
