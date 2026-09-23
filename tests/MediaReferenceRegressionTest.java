import java.util.HashSet;
import uy.santipdr.siege.client.SiegeMediaReferenceData;

public final class MediaReferenceRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        var tracks = SiegeMediaReferenceData.dvnTracks();
        check(tracks.size() >= 27, "5.50 soundtrack/reference list unexpectedly small");
        for (String title : new String[] {
                "Stronghold 5-5 · Black Signal", "Nucleus · Silent Carrier", "Tesla Breach", "Arc - Enemy",
                "Convenience Store", "Music Box", "New Store", "Jazz Music", "From the Ashes", "Sad Choir",
                "Powerplay", "Bewitched", "Dissonant", "Voltaic Dispatch", "Ablaze", "Dweller's Fury",
                "Dead Center", "Imperishable Valour", "Death Sentence"}) {
            check(tracks.stream().anyMatch(t -> t.title().equals(title)), "Missing media entry: " + title);
        }
        check(tracks.stream().allMatch(t -> !t.title().isBlank() && !t.note(false).isBlank()),
                "Every soundtrack entry needs useful player-facing context");
        check(tracks.stream().filter(t -> t.use() == SiegeMediaReferenceData.Use.BOSS).count() >= 10,
                "Boss soundtrack direction should remain substantial");
        check(tracks.stream().anyMatch(t -> t.title().equals("Stronghold 5-5 · Black Signal")
                        && t.use() == SiegeMediaReferenceData.Use.BRIEFING),
                "Stronghold Black Signal must remain real briefing media");
        check(tracks.stream().anyMatch(t -> t.title().equals("Nucleus · Silent Carrier")
                        && t.use() == SiegeMediaReferenceData.Use.BRIEFING),
                "Silent Carrier must be documented as installed Nucleus briefing media");
        check(tracks.stream().anyMatch(t -> t.title().equals("Tesla Breach")
                        && t.use() == SiegeMediaReferenceData.Use.BOSS),
                "Tesla Breach must be documented as installed boss/threat media");

        var moods = SiegeMediaReferenceData.moods();
        check(moods.size() >= 8, "5.50 operational mood catalog unexpectedly small");
        for (String id : new String[] {"stronghold", "nucleus-signal", "deployment", "intel", "last-stand",
                "industrial-war", "tesla-front", "boss-alert"})
            check(moods.stream().anyMatch(m -> m.id().equals(id)), "Missing mood: " + id);
        check(moods.stream().allMatch(m -> !m.bundledTrack().isBlank() && !m.referenceTracks().isEmpty()),
                "Every mood needs an installed-track anchor and references");
        check(new HashSet<>(moods.stream().map(SiegeMediaReferenceData.Mood::id).toList()).size() == moods.size(),
                "Mood IDs must remain unique");
        check(moods.stream().anyMatch(m -> m.bundledTrack().contains("Arc - Enemy")),
                "At least one mood should remain anchored to the bundled DVN track");
        check(moods.stream().anyMatch(m -> m.id().equals("stronghold")
                        && m.bundledTrack().equals("Stronghold 5-5 · Black Signal")),
                "Stronghold mood must use Black Signal");
        check(moods.stream().anyMatch(m -> m.id().equals("nucleus-signal")
                        && m.bundledTrack().equals("Nucleus · Silent Carrier")),
                "Nucleus mood must use Silent Carrier");
        check(moods.stream().filter(m -> m.id().equals("tesla-front") || m.id().equals("boss-alert"))
                        .allMatch(m -> m.bundledTrack().equals("Tesla Breach")),
                "Tesla/boss alert moods must use Tesla Breach");

        var visuals = SiegeMediaReferenceData.visualReferences();
        check(visuals.size() >= 23, "5.50 visual direction list unexpectedly small");
        for (String id : new String[] {"official-gallery-01", "official-gallery-02", "official-gallery-03",
                "official-gallery-04", "official-gallery-05", "official-gallery-06",
                "siege-nucleus-interference", "siege-tesla-breach", "siege-stronghold-red-alert",
                "stronghold-defense", "portal-last-stand", "coastal-assault", "arctic-standoff",
                "industrial-zone", "boss-assault", "nucleus-command", "tesla-breach-concept"}) {
            check(visuals.stream().anyMatch(v -> v.id().equals(id)), "Missing visual direction: " + id);
        }
        check(visuals.stream().allMatch(v -> !v.id().isBlank() && !v.title(false).isBlank() && !v.note(false).isBlank()),
                "Every visual direction needs readable metadata");

        var ready = visuals.stream().filter(SiegeMediaReferenceData.Visual::rotationReady).toList();
        check(ready.size() == 9, "5.50 must expose six official scenes plus three SIEGE treatments as rotation-ready");
        for (int i = 1; i <= 6; i++) {
            String id = String.format("official-gallery-%02d", i);
            check(ready.stream().anyMatch(v -> v.id().equals(id)), "Official DVN scene missing from rotation: " + id);
        }
        for (String id : new String[]{"siege-nucleus-interference", "siege-tesla-breach", "siege-stronghold-red-alert"})
            check(ready.stream().anyMatch(v -> v.id().equals(id)), "Generated SIEGE treatment missing from rotation: " + id);
        check(visuals.stream().filter(v -> !v.rotationReady())
                        .noneMatch(v -> v.id().startsWith("official-gallery-")),
                "All six sourced official-gallery visuals must remain rotation-ready");
        check(new HashSet<>(visuals.stream().map(SiegeMediaReferenceData.Visual::id).toList()).size() == visuals.size(),
                "Visual IDs must remain unique");
        System.out.println("SIEGE 5.50 soundtrack, presets, nine rotation visuals and references passed");
    }
}
