package uy.santipdr.siege.client;

import java.util.List;

/** Small player-facing audiovisual presets used by the 5.50 Media Room. */
public final class SiegeMediaPresets {
    public record Preset(String id, String titleEs, String titleEn, String track, String sceneId) {
        public String title(boolean spanish) { return spanish ? titleEs : titleEn; }
    }

    private static final List<Preset> PRESETS = List.of(
            new Preset("stronghold", "STRONGHOLD", "STRONGHOLD",
                    "Stronghold 5-5 · Black Signal", "stronghold_red_alert"),
            new Preset("nucleus", "NÚCLEO", "NUCLEUS",
                    "Nucleus · Silent Carrier", "nucleus_interference"),
            new Preset("tesla", "TESLA", "TESLA",
                    "Tesla Breach", "tesla_breach")
    );

    private SiegeMediaPresets() { }

    public static List<Preset> presets() { return PRESETS; }

    public static boolean apply(String id) {
        if (id == null) return false;
        for (Preset preset : PRESETS) {
            if (!preset.id().equalsIgnoreCase(id)) continue;
            int scene = SiegeSceneCatalog.indexOfId(preset.sceneId());
            if (scene < 0) return false;
            SiegeConfig.selectedScene = scene;
            SiegeConfig.animatedBackgrounds = true;
            SiegeConfig.save();
            SiegeMusic.selectTrackByName(preset.track());
            return true;
        }
        return false;
    }
}
