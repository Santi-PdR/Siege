package uy.santipdr.siege.client;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class SiegeConfig {
    public enum Graphics { PERFORMANCE, BALANCED, CINEMATIC;
        public Graphics next() { return values()[(ordinal() + 1) % values().length]; }
    }

    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("siege-client.properties");
    public static boolean music = true;
    public static int musicVolume = 75;
    public static boolean uiSounds = true;
    public static boolean menuEffects = true;
    public static boolean animatedBackgrounds = true;
    public static boolean animatedIntel = true;
    public static boolean mainMenuIntel = true;
    public static boolean scanlines = true;
    public static boolean reducedMotion = false;
    public static Graphics graphics = Graphics.CINEMATIC;

    private SiegeConfig() {}

    public static void load() {
        Properties p = new Properties();
        if (Files.isRegularFile(FILE)) {
            try (InputStream in = Files.newInputStream(FILE)) { p.load(in); }
            catch (IOException ignored) { }
        }
        music = bool(p, "music", true);
        musicVolume = integer(p, "musicVolume", 75, 0, 100);
        uiSounds = bool(p, "uiSounds", true);
        menuEffects = bool(p, "menuEffects", true);
        animatedBackgrounds = bool(p, "animatedBackgrounds", true);
        animatedIntel = bool(p, "animatedIntel", true);
        mainMenuIntel = bool(p, "mainMenuIntel", true);
        scanlines = bool(p, "scanlines", true);
        reducedMotion = bool(p, "reducedMotion", false);
        try { graphics = Graphics.valueOf(p.getProperty("graphics", Graphics.CINEMATIC.name())); }
        catch (IllegalArgumentException ignored) { graphics = Graphics.CINEMATIC; }
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("music", Boolean.toString(music));
        p.setProperty("musicVolume", Integer.toString(musicVolume));
        p.setProperty("uiSounds", Boolean.toString(uiSounds));
        p.setProperty("menuEffects", Boolean.toString(menuEffects));
        p.setProperty("animatedBackgrounds", Boolean.toString(animatedBackgrounds));
        p.setProperty("animatedIntel", Boolean.toString(animatedIntel));
        p.setProperty("mainMenuIntel", Boolean.toString(mainMenuIntel));
        p.setProperty("scanlines", Boolean.toString(scanlines));
        p.setProperty("reducedMotion", Boolean.toString(reducedMotion));
        p.setProperty("graphics", graphics.name());
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream out = Files.newOutputStream(FILE)) { p.store(out, "Eternal Craft: SIEGE client settings"); }
        } catch (IOException ignored) { }
    }

    public static int clampVolume(int value) { return Math.max(0, Math.min(100, value)); }

    private static boolean bool(Properties p, String key, boolean fallback) {
        String value = p.getProperty(key);
        return value == null ? fallback : Boolean.parseBoolean(value);
    }

    private static int integer(Properties p, String key, int fallback, int min, int max) {
        try { return Math.max(min, Math.min(max, Integer.parseInt(p.getProperty(key, Integer.toString(fallback))))); }
        catch (NumberFormatException ignored) { return fallback; }
    }
}
