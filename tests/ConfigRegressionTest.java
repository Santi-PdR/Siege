import java.nio.file.Files;
import java.nio.file.Path;
import uy.santipdr.siege.client.SiegeConfig;

public class ConfigRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }
    public static void main(String[] args) throws Exception {
        Path folder = Files.createTempDirectory("siege-config-test-");
        System.setProperty("siege.test.config", folder.toString());
        try {
            SiegeConfig.load();
            SiegeConfig.toggleFavoriteIntel("U01");
            SiegeConfig.autoRotateIntel = false;
            SiegeConfig.intelReadingMode = true;
            SiegeConfig.darkIntelPaper = true;
            SiegeConfig.comfortableReading = true;
            SiegeConfig.indexOrder = 2;
            SiegeConfig.indexReverse = true;
            SiegeConfig.inspectorMap = false;
            SiegeConfig.inspectorBackground = 2;
            SiegeConfig.save();
            SiegeConfig.load();
            check(!SiegeConfig.autoRotateIntel, "New settings must not rerun the legacy auto-rotate migration");
            check(SiegeConfig.intelReadingMode && SiegeConfig.darkIntelPaper && SiegeConfig.comfortableReading, "Reader settings round trip");
            check(SiegeConfig.indexOrder == 2 && SiegeConfig.indexReverse, "Index settings round trip");
            check(!SiegeConfig.inspectorMap && SiegeConfig.inspectorBackground == 2, "Inspector settings round trip");
            SiegeConfig.resetDefaults();
            check(SiegeConfig.isFavoriteIntel("U01"), "Reset must preserve favorites");
            SiegeConfig.load();
            check(SiegeConfig.isFavoriteIntel("U01"), "Preserved favorites must survive reload");
            SiegeConfig.applyCalmPreset();
            check(SiegeConfig.reducedMotion && !SiegeConfig.scanlines && !SiegeConfig.titleInterference && !SiegeConfig.hoverSounds, "Calm preset");
            check(SiegeConfig.isFavoriteIntel("U01"), "Calm preset must preserve favorites");
            SiegeConfig.clearFavoriteIntel(); SiegeConfig.load();
            check(!SiegeConfig.isFavoriteIntel("U01"), "Explicit favorite clearing");
            Files.writeString(folder.resolve("siege-client.properties"), "settingsRevision=801\nindexOrder=99\ninspectorBackground=-9\nautoRotateIntel=false\n");
            SiegeConfig.load();
            check(SiegeConfig.indexOrder == 2 && SiegeConfig.inspectorBackground == 0, "Invalid numeric settings clamped");
            check(!SiegeConfig.autoRotateIntel, "Existing rotation preference retained");
            System.out.println("Settings persistence, favorites preservation, presets and bounds passed");
        } finally {
            try (var files = Files.list(folder)) { for (Path file : files.toList()) Files.deleteIfExists(file); }
            Files.deleteIfExists(folder);
        }
    }
}
