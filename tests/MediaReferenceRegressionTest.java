import java.util.HashSet;
import uy.santipdr.siege.client.SiegeMediaReferenceData;

public final class MediaReferenceRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        var tracks = SiegeMediaReferenceData.dvnTracks();
        check(tracks.size() >= 24, "DVN soundtrack list unexpectedly small");
        for (String title : new String[] {
                "Arc - Enemy", "Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir",
                "Powerplay", "Bewitched", "Dissonant", "Voltaic Dispatch", "Ablaze", "Dweller's Fury",
                "Dead Center", "Imperishable Valour", "Death Sentence"}) {
            check(tracks.stream().anyMatch(t -> t.title().equals(title)), "Missing DVN media entry: " + title);
        }
        check(tracks.stream().allMatch(t -> !t.title().isBlank() && !t.note(false).isBlank()),
                "Every soundtrack entry needs useful player-facing context");
        check(tracks.stream().filter(t -> t.use() == SiegeMediaReferenceData.Use.BOSS).count() >= 9,
                "Boss soundtrack direction should remain substantial");

        var moods = SiegeMediaReferenceData.moods();
        check(moods.size() >= 6, "5.10 operational mood catalog unexpectedly small");
        for (String id : new String[] {"stronghold", "deployment", "intel", "last-stand", "industrial-war", "boss-alert"})
            check(moods.stream().anyMatch(m -> m.id().equals(id)), "Missing mood: " + id);
        check(moods.stream().allMatch(m -> !m.bundledTrack().isBlank() && !m.referenceTracks().isEmpty()),
                "Every mood needs an installed-track anchor and references");
        check(new HashSet<>(moods.stream().map(SiegeMediaReferenceData.Mood::id).toList()).size() == moods.size(),
                "Mood IDs must remain unique");
        check(moods.stream().anyMatch(m -> m.bundledTrack().contains("Arc - Enemy")),
                "At least one 5.10 mood should anchor to the newly bundled DVN track");

        var visuals = SiegeMediaReferenceData.visualReferences();
        check(visuals.size() >= 12, "5.10 visual direction list unexpectedly small");
        for (String id : new String[] {"official-gallery-01", "official-gallery-02", "stronghold-defense",
                "portal-last-stand", "coastal-assault", "arctic-standoff", "industrial-zone", "boss-assault"}) {
            check(visuals.stream().anyMatch(v -> v.id().equals(id)), "Missing visual direction: " + id);
        }
        check(visuals.stream().allMatch(v -> !v.id().isBlank() && !v.title(false).isBlank() && !v.note(false).isBlank()),
                "Every visual direction needs readable metadata");

        var ready = visuals.stream().filter(SiegeMediaReferenceData.Visual::rotationReady).toList();
        // 5.10 introduced two sourced official thumbnails. Later releases may add more
        // official-gallery entries, so this legacy regression must not reject them.
        check(ready.size() >= 2, "The original sourced DVN thumbnails must remain promoted to rotation");
        check(ready.stream().anyMatch(v -> v.id().equals("official-gallery-01")),
                "Official DVN gallery scene 01 should be rotation-ready");
        check(ready.stream().anyMatch(v -> v.id().equals("official-gallery-02")),
                "Official DVN gallery scene 02 should be rotation-ready");
        check(ready.stream().allMatch(v -> v.id().startsWith("official-gallery-")),
                "Only sourced official-gallery visuals may be rotation-ready");
        check(visuals.stream().filter(v -> !v.id().startsWith("official-gallery-"))
                        .noneMatch(SiegeMediaReferenceData.Visual::rotationReady),
                "Reference-only visual ideas must remain outside rotation");
        check(new HashSet<>(visuals.stream().map(SiegeMediaReferenceData.Visual::id).toList()).size() == visuals.size(),
                "Visual IDs must remain unique");
        System.out.println("SIEGE 5.10 DVN soundtrack, promoted visuals, moods and references passed");
    }
}
