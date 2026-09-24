package uy.santipdr.siege.client;

import java.util.List;

/** Small player-facing visual presets used by the Media Room. */
public final class SiegeMediaPresets {
    public record Preset(String id, String titleEs, String titleEn, String sceneId) {
        public String title(boolean spanish) { return spanish ? titleEs : titleEn; }
    }

    /**
     * These presets intentionally change only the scene. Music is not attached here
     * until the corresponding candidate has been previewed and explicitly approved.
     */
    private static final List<Preset> PRESETS = List.of(
            new Preset("stronghold", "STRONGHOLD", "STRONGHOLD", "stronghold_red_alert"),
            new Preset("nucleus", "NÚCLEO", "NUCLEUS", "nucleus_interference"),
            new Preset("tesla", "TESLA", "TESLA", "tesla_breach")
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
            return true;
        }
        return false;
    }
}
