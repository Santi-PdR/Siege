import java.util.HashSet;
import uy.santipdr.siege.client.SiegeMediaReferenceData;

public final class MediaReferenceRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        var tracks = SiegeMediaReferenceData.dvnTracks();
        check(tracks.size() >= 23, "DVN soundtrack reference list unexpectedly small");
        for (String title : new String[] {
                "Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir",
                "Powerplay", "Bewitched", "Dissonant", "Voltaic Dispatch", "Ablaze", "Dweller's Fury",
                "Dead Center", "Imperishable Valour", "Death Sentence"}) {
            check(tracks.stream().anyMatch(t -> t.title().equals(title)), "Missing DVN media reference: " + title);
        }
        check(tracks.stream().allMatch(t -> !t.title().isBlank() && !t.note(false).isBlank()),
                "Every soundtrack recommendation needs useful player-facing context");
        check(tracks.stream().filter(t -> t.use() == SiegeMediaReferenceData.Use.BOSS).count() >= 9,
                "Boss soundtrack direction should remain substantial");

        var moods = SiegeMediaReferenceData.moods();
        check(moods.size() >= 6, "5.00 operational mood catalog unexpectedly small");
        for (String id : new String[] {"stronghold", "deployment", "intel", "last-stand", "industrial-war", "boss-alert"})
            check(moods.stream().anyMatch(m -> m.id().equals(id)), "Missing mood: " + id);
        check(moods.stream().allMatch(m -> !m.bundledTrack().isBlank() && !m.referenceTracks().isEmpty()),
                "Every mood needs an installed-track anchor and references");
        check(new HashSet<>(moods.stream().map(SiegeMediaReferenceData.Mood::id).toList()).size() == moods.size(),
                "Mood IDs must remain unique");

        // References are direction, not an excuse to silently inject third-party binaries.
        var visuals = SiegeMediaReferenceData.visualReferences();
        check(visuals.size() >= 12, "5.00 visual direction list unexpectedly small");
        for (String id : new String[] {"stronghold-defense", "portal-last-stand", "coastal-assault",
                "arctic-standoff", "industrial-zone", "boss-assault"}) {
            check(visuals.stream().anyMatch(v -> v.id().equals(id)), "Missing visual direction: " + id);
        }
        check(visuals.stream().allMatch(v -> !v.id().isBlank() && !v.title(false).isBlank() && !v.note(false).isBlank()),
                "Every visual direction needs readable metadata");
        check(visuals.stream().noneMatch(SiegeMediaReferenceData.Visual::rotationReady),
                "Reference-only visuals must not enter rotation without an actual approved master");
        check(new HashSet<>(visuals.stream().map(SiegeMediaReferenceData.Visual::id).toList()).size() == visuals.size(),
                "Visual IDs must remain unique");
        System.out.println("SIEGE 5.00 DVN media, boss soundtrack direction, moods and visual references passed");
    }
}
