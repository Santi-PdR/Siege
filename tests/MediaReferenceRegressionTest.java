import java.util.HashSet;
import uy.santipdr.siege.client.SiegeMediaReferenceData;

public final class MediaReferenceRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        var tracks = SiegeMediaReferenceData.dvnTracks();
        check(tracks.size() >= 25, "5.40 soundtrack/reference list unexpectedly small");
        for (String title : new String[] {
                "Stronghold 5-5 · Black Signal", "Arc - Enemy", "Convenience Store", "Music Box", "New Store",
                "Jazz Music", "From the Ashes", "Sad Choir", "Powerplay", "Bewitched", "Dissonant",
                "Voltaic Dispatch", "Ablaze", "Dweller's Fury", "Dead Center", "Imperishable Valour", "Death Sentence"}) {
            check(tracks.stream().anyMatch(t -> t.title().equals(title)), "Missing media entry: " + title);
        }
        check(tracks.stream().allMatch(t -> !t.title().isBlank() && !t.note(false).isBlank()),
                "Every soundtrack entry needs useful player-facing context");
        check(tracks.stream().filter(t -> t.use() == SiegeMediaReferenceData.Use.BOSS).count() >= 9,
                "Boss soundtrack direction should remain substantial");
        check(tracks.stream().anyMatch(t -> t.title().equals("Stronghold 5-5 · Black Signal")
                        && t.use() == SiegeMediaReferenceData.Use.BRIEFING),
                "The original 5.40 Stronghold track must be documented as real briefing media");

        var moods = SiegeMediaReferenceData.moods();
        check(moods.size() >= 7, "5.40 operational mood catalog unexpectedly small");
        for (String id : new String[] {"stronghold", "nucleus-signal", "deployment", "intel", "last-stand",
                "industrial-war", "boss-alert"})
            check(moods.stream().anyMatch(m -> m.id().equals(id)), "Missing mood: " + id);
        check(moods.stream().allMatch(m -> !m.bundledTrack().isBlank() && !m.referenceTracks().isEmpty()),
                "Every mood needs an installed-track anchor and references");
        check(new HashSet<>(moods.stream().map(SiegeMediaReferenceData.Mood::id).toList()).size() == moods.size(),
                "Mood IDs must remain unique");
        check(moods.stream().anyMatch(m -> m.bundledTrack().contains("Arc - Enemy")),
                "At least one mood should remain anchored to the bundled DVN track");
        check(moods.stream().filter(m -> m.id().equals("stronghold") || m.id().equals("nucleus-signal"))
                        .allMatch(m -> m.bundledTrack().equals("Stronghold 5-5 · Black Signal")),
                "Stronghold/Nucleus moods must anchor to the new original 5.40 track");

        var visuals = SiegeMediaReferenceData.visualReferences();
        check(visuals.size() >= 20, "5.40 visual direction list unexpectedly small");
        for (String id : new String[] {"official-gallery-01", "official-gallery-02", "official-gallery-03",
                "official-gallery-04", "official-gallery-05", "official-gallery-06", "stronghold-defense",
                "portal-last-stand", "coastal-assault", "arctic-standoff", "industrial-zone", "boss-assault",
                "nucleus-command", "tesla-breach"}) {
            check(visuals.stream().anyMatch(v -> v.id().equals(id)), "Missing visual direction: " + id);
        }
        check(visuals.stream().allMatch(v -> !v.id().isBlank() && !v.title(false).isBlank() && !v.note(false).isBlank()),
                "Every visual direction needs readable metadata");

        var ready = visuals.stream().filter(SiegeMediaReferenceData.Visual::rotationReady).toList();
        check(ready.size() == 6, "5.40 must expose exactly six sourced official DVN scenes as rotation-ready");
        for (int i = 1; i <= 6; i++) {
            String id = String.format("official-gallery-%02d", i);
            check(ready.stream().anyMatch(v -> v.id().equals(id)), "Official DVN scene missing from rotation: " + id);
        }
        check(ready.stream().allMatch(v -> v.id().startsWith("official-gallery-")),
                "Only sourced official-gallery visuals may be rotation-ready");
        check(visuals.stream().filter(v -> !v.id().startsWith("official-gallery-"))
                        .noneMatch(SiegeMediaReferenceData.Visual::rotationReady),
                "Reference-only visual ideas must remain outside rotation");
        check(new HashSet<>(visuals.stream().map(SiegeMediaReferenceData.Visual::id).toList()).size() == visuals.size(),
                "Visual IDs must remain unique");
        System.out.println("SIEGE 5.40 soundtrack, six-scene DVN gallery, moods and references passed");
    }
}
