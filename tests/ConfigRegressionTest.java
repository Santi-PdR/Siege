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
            SiegeConfig.highContrast = true;
            SiegeConfig.reduceFlashes = true;
            SiegeConfig.titleInterference = true;
            SiegeConfig.autoContrast = false;
            SiegeConfig.scanlineIntensity = 37;
            SiegeConfig.interferenceIntensity = 91;
            SiegeConfig.save();
            SiegeConfig.load();
            check(!SiegeConfig.autoRotateIntel, "New settings must not rerun the legacy auto-rotate migration");
            check(SiegeConfig.intelReadingMode && SiegeConfig.darkIntelPaper && SiegeConfig.comfortableReading, "Reader settings round trip");
            check(!SiegeConfig.inspectorMap && SiegeConfig.inspectorBackground == 2, "Inspector settings round trip");
            check(SiegeConfig.highContrast && SiegeConfig.reduceFlashes, "Accessibility settings round trip");
            check(!SiegeConfig.titleInterference && SiegeConfig.interferenceIntensity == 0,
                    "Flash reduction must suppress title interference and its intensity");
            check(!SiegeConfig.autoContrast && SiegeConfig.scanlineIntensity == 37,
                    "1.25 visual controls round trip independently");

            SiegeConfig.resetDefaults();
            SiegeConfig.load();
            check(SiegeConfig.autoContrast && SiegeConfig.scanlineIntensity == 55 && SiegeConfig.interferenceIntensity == 55,
                    "1.25 visual defaults");
            SiegeConfig.applyCalmPreset();
            check(SiegeConfig.reducedMotion && SiegeConfig.reduceFlashes && SiegeConfig.highContrast
                    && !SiegeConfig.scanlines && SiegeConfig.scanlineIntensity == 0
                    && !SiegeConfig.titleInterference && SiegeConfig.interferenceIntensity == 0
                    && !SiegeConfig.hoverSounds && SiegeConfig.autoContrast, "Calm preset");

            SiegeConfig.resetDefaults();
            for (int scene = 9; scene < 13; scene++) {
                SiegeConfig.selectedScene = scene; SiegeConfig.save();
                SiegeConfig.selectedScene = -1; SiegeConfig.load();
                check(SiegeConfig.selectedScene == scene, "New gallery scene survives save/reload");
            }
            SiegeConfig.applyReadingPreset();
            check(SiegeConfig.intelReadingMode && SiegeConfig.comfortableReading && SiegeConfig.darkIntelPaper
                    && SiegeConfig.highContrast && SiegeConfig.reducedMotion && SiegeConfig.reduceFlashes
                    && !SiegeConfig.scanlines && !SiegeConfig.titleInterference && SiegeConfig.autoContrast,
                    "Reading preset");

            Files.writeString(folder.resolve("siege-client.properties"), "settingsRevision=801\nindexOrder=99\ninspectorBackground=-9\nautoRotateIntel=false\n");
            SiegeConfig.load();
            check(!SiegeConfig.autoRotateIntel, "Existing rotation preference retained");
            check(SiegeConfig.inspectorBackground == 0, "Invalid numeric value clamped");
            check(!SiegeConfig.highContrast && !SiegeConfig.reduceFlashes, "New options use independent defaults");
            check(SiegeConfig.autoContrast && SiegeConfig.scanlineIntensity == 55 && SiegeConfig.interferenceIntensity == 55,
                    "1.25 options load independent defaults without a migration revision");

            Path config = folder.resolve("siege-client.properties");
            Files.writeString(config, "settingsRevision=801\nuiVolume= 44 \nmusic= false \ngraphics= balanced \nhighContrast= true \nreduceFlashes= true \ntitleInterference=true\nscanlineIntensity=250\ninterferenceIntensity=-9\n");
            SiegeConfig.load();
            check(SiegeConfig.uiVolume == 44 && !SiegeConfig.music, "Trimmed settings");
            check(SiegeConfig.graphics == SiegeConfig.Graphics.BALANCED, "Case-insensitive graphics");
            check(SiegeConfig.highContrast && SiegeConfig.reduceFlashes && !SiegeConfig.titleInterference,
                    "Accessibility normalization on load");
            check(SiegeConfig.scanlineIntensity == 100 && SiegeConfig.interferenceIntensity == 0,
                    "1.25 intensity values clamp safely");
            SiegeConfig.save();
            var savedTime = Files.getLastModifiedTime(config);
            SiegeConfig.save();
            check(savedTime.equals(Files.getLastModifiedTime(config)), "Unchanged save must not write");
            SiegeConfig.uiVolume = 999; SiegeConfig.save();
            check(SiegeConfig.uiVolume == 100, "Save clamps public settings");

            Files.writeString(config, "music=\\uBROKEN");
            SiegeConfig.load(); SiegeConfig.save();
            check(!SiegeConfig.lastSaveSucceeded, "Corrupt file blocks implicit overwrite");
            check(Files.readString(config).equals("music=\\uBROKEN"), "Corrupt original retained");
            SiegeConfig.resetDefaults();
            check(SiegeConfig.lastSaveSucceeded, "Explicit reset can recover");
            try (var entries = Files.list(folder)) {
                check(entries.anyMatch(path -> path.getFileName().toString().startsWith("siege-client-corrupt-")), "Corrupt file backed up");
            }
        } finally {
            try (var files = Files.list(folder)) { for (Path file : files.toList()) Files.deleteIfExists(file); }
            Files.deleteIfExists(folder);
        }
    }
}
