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
            SiegeConfig.autoRotateIntel = false;
            SiegeConfig.intelReadingMode = true;
            SiegeConfig.darkIntelPaper = true;
            SiegeConfig.comfortableReading = true;
            SiegeConfig.inspectorMap = false;
            SiegeConfig.inspectorBackground = 2;
            SiegeConfig.save();
            SiegeConfig.load();
            check(!SiegeConfig.autoRotateIntel, "New settings must not rerun the legacy auto-rotate migration");
            check(SiegeConfig.intelReadingMode && SiegeConfig.darkIntelPaper && SiegeConfig.comfortableReading, "Reader settings round trip");
            check(!SiegeConfig.inspectorMap && SiegeConfig.inspectorBackground == 2, "Inspector settings round trip");
            SiegeConfig.resetDefaults();
            SiegeConfig.load();
            SiegeConfig.applyCalmPreset();
            check(SiegeConfig.reducedMotion && !SiegeConfig.scanlines && !SiegeConfig.titleInterference && !SiegeConfig.hoverSounds, "Calm preset");
            Files.writeString(folder.resolve("siege-client.properties"), "settingsRevision=801\nindexOrder=99\ninspectorBackground=-9\nautoRotateIntel=false\n");
            SiegeConfig.load();
            check(!SiegeConfig.autoRotateIntel, "Existing rotation preference retained");
            check(SiegeConfig.inspectorBackground == 0, "Invalid numeric value clamped");
        } finally {
            try (var files = Files.list(folder)) { for (Path file : files.toList()) Files.deleteIfExists(file); }
            Files.deleteIfExists(folder);
        }
    }
}
