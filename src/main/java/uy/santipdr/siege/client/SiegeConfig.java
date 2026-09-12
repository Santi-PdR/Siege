package uy.santipdr.siege.client;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import com.mojang.logging.LogUtils;
import java.util.Properties;
import java.util.LinkedHashSet;
import java.util.Set;

public final class SiegeConfig {
    private static final int SETTINGS_REVISION = 801;
    public enum Graphics { PERFORMANCE, BALANCED, CINEMATIC;
        public Graphics next() { return values()[(ordinal() + 1) % values().length]; }
    }

    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("siege-client.properties");
    public static boolean intelReadingMode = false;
    public static boolean comfortableReading = false;
    public static boolean darkIntelPaper = false;
    public static boolean inspectorMap = true;
    public static int inspectorBackground = 0;
    public static int selectedTrack = -1;
    public static int selectedScene = -1;
    public static int uiVolume = 100;
    public static boolean hoverSounds = true;
    public static boolean autoRotateIntel = true;
    public static boolean music = true;
    public static int musicVolume = 75;
    public static boolean uiSounds = true;
    public static boolean menuEffects = true;
    public static boolean animatedBackgrounds = true;
    public static boolean animatedIntel = true;
    public static boolean mainMenuIntel = true;
    public static boolean scanlines = true;
    public static boolean reducedMotion = false;
    public static boolean titleInterference = true;
    public static boolean trackAnnouncements = true;
    public static boolean pauseIntelOnHover = true;
    public static boolean showIntelProgress = true;
    public static boolean showIntelState = true;
    public static boolean showBuildLabel = true;
    public static boolean confirmQuit = true;
    public static int backgroundDarkness = 23;
    public static int panelDarkness = 63;
    public static int trackNoticeSeconds = 8;
    public static Graphics graphics = Graphics.CINEMATIC;

    private SiegeConfig() {}

    public static void load() {
        Properties p = new Properties();
        if (Files.isRegularFile(FILE)) {
            try (InputStream in = Files.newInputStream(FILE)) { p.load(in); }
            catch (IOException | IllegalArgumentException error) { LogUtils.getLogger().warn("Could not load SIEGE settings", error); }
        }
        intelReadingMode = bool(p, "intelReadingMode", false);
        comfortableReading = bool(p, "comfortableReading", false);
        darkIntelPaper = bool(p, "darkIntelPaper", false);
        inspectorMap = bool(p, "inspectorMap", true);
        inspectorBackground = integer(p, "inspectorBackground", 0, 0, 2);
        selectedTrack = integer(p, "selectedTrack", -1, -1, 3);
        selectedScene = integer(p, "selectedScene", -1, -1, 8);
        uiVolume = integer(p, "uiVolume", 100, 0, 100);
        hoverSounds = bool(p, "hoverSounds", true);
        int loadedRevision = integer(p, "settingsRevision", 0, 0, SETTINGS_REVISION);
        autoRotateIntel = loadedRevision < SETTINGS_REVISION || bool(p, "autoRotateIntel", true);
        music = bool(p, "music", true);
        musicVolume = integer(p, "musicVolume", 75, 0, 100);
        uiSounds = bool(p, "uiSounds", true);
        menuEffects = bool(p, "menuEffects", true);
        animatedBackgrounds = bool(p, "animatedBackgrounds", true);
        animatedIntel = bool(p, "animatedIntel", true);
        mainMenuIntel = bool(p, "mainMenuIntel", true);
        scanlines = bool(p, "scanlines", true);
        reducedMotion = bool(p, "reducedMotion", false);
        titleInterference = bool(p, "titleInterference", true);
        trackAnnouncements = bool(p, "trackAnnouncements", true);
        pauseIntelOnHover = bool(p, "pauseIntelOnHover", true);
        showIntelProgress = bool(p, "showIntelProgress", true);
        showIntelState = bool(p, "showIntelState", true);
        showBuildLabel = bool(p, "showBuildLabel", true);
        confirmQuit = bool(p, "confirmQuit", true);
        backgroundDarkness = integer(p, "backgroundDarkness", 23, 0, 70);
        panelDarkness = integer(p, "panelDarkness", 63, 20, 90);
        trackNoticeSeconds = integer(p, "trackNoticeSeconds", 8, 3, 15);
        try { graphics = Graphics.valueOf(p.getProperty("graphics", Graphics.CINEMATIC.name())); }
        catch (IllegalArgumentException ignored) { graphics = Graphics.CINEMATIC; }
        if (loadedRevision < SETTINGS_REVISION) save();
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("intelReadingMode", Boolean.toString(intelReadingMode));
        p.setProperty("comfortableReading", Boolean.toString(comfortableReading));
        p.setProperty("darkIntelPaper", Boolean.toString(darkIntelPaper));
        p.setProperty("inspectorMap", Boolean.toString(inspectorMap));
        p.setProperty("inspectorBackground", Integer.toString(inspectorBackground));
        p.setProperty("settingsRevision", Integer.toString(SETTINGS_REVISION));
        p.setProperty("selectedTrack", Integer.toString(selectedTrack));
        p.setProperty("selectedScene", Integer.toString(selectedScene));
        p.setProperty("uiVolume", Integer.toString(uiVolume));
        p.setProperty("hoverSounds", Boolean.toString(hoverSounds));
        p.setProperty("autoRotateIntel", Boolean.toString(autoRotateIntel));
        p.setProperty("music", Boolean.toString(music));
        p.setProperty("musicVolume", Integer.toString(musicVolume));
        p.setProperty("uiSounds", Boolean.toString(uiSounds));
        p.setProperty("menuEffects", Boolean.toString(menuEffects));
        p.setProperty("animatedBackgrounds", Boolean.toString(animatedBackgrounds));
        p.setProperty("animatedIntel", Boolean.toString(animatedIntel));
        p.setProperty("mainMenuIntel", Boolean.toString(mainMenuIntel));
        p.setProperty("scanlines", Boolean.toString(scanlines));
        p.setProperty("reducedMotion", Boolean.toString(reducedMotion));
        p.setProperty("titleInterference", Boolean.toString(titleInterference));
        p.setProperty("trackAnnouncements", Boolean.toString(trackAnnouncements));
        p.setProperty("pauseIntelOnHover", Boolean.toString(pauseIntelOnHover));
        p.setProperty("showIntelProgress", Boolean.toString(showIntelProgress));
        p.setProperty("showIntelState", Boolean.toString(showIntelState));
        p.setProperty("showBuildLabel", Boolean.toString(showBuildLabel));
        p.setProperty("confirmQuit", Boolean.toString(confirmQuit));
        p.setProperty("backgroundDarkness", Integer.toString(backgroundDarkness));
        p.setProperty("panelDarkness", Integer.toString(panelDarkness));
        p.setProperty("trackNoticeSeconds", Integer.toString(trackNoticeSeconds));
        p.setProperty("graphics", graphics.name());
        Path temporary = null;
        try {
            Files.createDirectories(FILE.getParent());
            temporary = Files.createTempFile(FILE.getParent(), "siege-client-", ".tmp");
            try (OutputStream out = Files.newOutputStream(temporary)) {
                p.store(out, "Eternal Craft: SIEGE client settings");
            }
            try {
                Files.move(temporary, FILE, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException error) {
            LogUtils.getLogger().warn("Could not save SIEGE settings; previous file retained", error);
        } finally {
            if (temporary != null) try { Files.deleteIfExists(temporary); }
            catch (IOException error) { LogUtils.getLogger().debug("Could not remove settings temporary file", error); }
        }
    }

    public static void resetDefaults() {
        intelReadingMode = false;
        comfortableReading = false;
        darkIntelPaper = false;
        inspectorMap = true;
        inspectorBackground = 0;

        selectedTrack = -1;
        selectedScene = -1;
        uiVolume = 100;
        hoverSounds = true;
        autoRotateIntel = true;
        music = true;
        musicVolume = 75;
        uiSounds = true;
        menuEffects = true;
        animatedBackgrounds = true;
        animatedIntel = true;
        mainMenuIntel = true;
        scanlines = true;
        reducedMotion = false;
        titleInterference = true;
        trackAnnouncements = true;
        pauseIntelOnHover = true;
        showIntelProgress = true;
        showIntelState = true;
        showBuildLabel = true;
        confirmQuit = true;
        backgroundDarkness = 23;
        panelDarkness = 63;
        trackNoticeSeconds = 8;
        graphics = Graphics.CINEMATIC;
        save();
    }

    public static void applyCalmPreset() {
        reducedMotion = true;
        titleInterference = false;
        scanlines = false;
        hoverSounds = false;
        graphics = Graphics.BALANCED;
        save();
    }

    public static int clampVolume(int value) { return Math.max(0, Math.min(100, value)); }

    private static boolean bool(Properties p, String key, boolean fallback) {
        String value = p.getProperty(key);
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        return fallback;
    }

    private static int integer(Properties p, String key, int fallback, int min, int max) {
        try { return Math.max(min, Math.min(max, Integer.parseInt(p.getProperty(key, Integer.toString(fallback))))); }
        catch (NumberFormatException ignored) { return fallback; }
    }
}
