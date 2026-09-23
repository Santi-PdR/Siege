package uy.santipdr.siege.client;

/** Single authoritative contract for every SIEGE presentation profile. */
public final class SiegeProfileSpec {
    public record Spec(
            boolean reading,
            SiegeConfig.Graphics graphics,
            boolean reducedMotion,
            boolean reduceFlashes,
            boolean highContrast,
            boolean menuEffects,
            boolean animatedBackgrounds,
            boolean animatedIntel,
            boolean scanlines,
            boolean titleInterference,
            boolean hoverSounds,
            boolean mainMenuIntel,
            boolean autoRotateIntel,
            boolean showIntelProgress,
            boolean trackAnnouncements,
            boolean autoContrast,
            int scanlineIntensity,
            int interferenceIntensity,
            int backgroundDarkness,
            int panelDarkness,
            int backgroundMotionIntensity,
            int backgroundSceneSeconds,
            int backgroundCrossfadeSeconds) { }

    private static final int FIELDS = 25;
    private SiegeProfileSpec() { }

    public static Spec of(SiegeClientProfile.Profile profile) {
        return switch (profile) {
            case CINEMATIC -> new Spec(false, SiegeConfig.Graphics.CINEMATIC,
                    false, false, false, true, true, true, true, true, true,
                    true, true, true, true, true, 66, 64, 18, 58, 70, 28, 6);
            case TACTICAL -> new Spec(false, SiegeConfig.Graphics.BALANCED,
                    false, false, false, true, true, true, true, false, true,
                    true, true, true, true, true, 48, 24, 30, 72, 40, 30, 4);
            case STRONGHOLD -> new Spec(false, SiegeConfig.Graphics.BALANCED,
                    false, true, false, true, true, true, true, false, true,
                    true, true, true, true, true, 38, 0, 34, 78, 45, 32, 5);
            case PERFORMANCE -> new Spec(false, SiegeConfig.Graphics.PERFORMANCE,
                    true, true, false, false, false, false, false, false, false,
                    true, false, false, false, true, 0, 0, 38, 78, 0, 36, 0);
            case CALM -> new Spec(false, SiegeConfig.Graphics.BALANCED,
                    true, true, true, false, false, false, false, false, false,
                    true, false, false, false, true, 0, 0, 42, 84, 0, 36, 0);
            case READING -> new Spec(true, SiegeConfig.Graphics.BALANCED,
                    true, true, true, false, false, false, false, false, false,
                    false, false, false, false, true, 0, 0, 48, 88, 0, 36, 0);
            case CLASSIC -> new Spec(false, SiegeConfig.Graphics.BALANCED,
                    false, false, false, true, true, true, false, false, true,
                    true, true, true, true, true, 0, 0, 28, 70, 20, 28, 3);
            case HIGH_CONTRAST -> new Spec(false, SiegeConfig.Graphics.BALANCED,
                    true, true, true, false, false, false, false, false, false,
                    true, false, true, false, true, 0, 0, 52, 90, 0, 36, 0);
            case IMMERSIVE -> new Spec(false, SiegeConfig.Graphics.CINEMATIC,
                    false, false, false, true, true, true, true, true, true,
                    true, true, true, true, true, 58, 42, 20, 62, 85, 24, 6);
            case CUSTOM -> throw new IllegalArgumentException("CUSTOM has no fixed specification");
        };
    }

    public static SiegeClientProfile.Profile[] presets() {
        return new SiegeClientProfile.Profile[] {
                SiegeClientProfile.Profile.CINEMATIC,
                SiegeClientProfile.Profile.TACTICAL,
                SiegeClientProfile.Profile.STRONGHOLD,
                SiegeClientProfile.Profile.PERFORMANCE,
                SiegeClientProfile.Profile.CALM,
                SiegeClientProfile.Profile.READING,
                SiegeClientProfile.Profile.CLASSIC,
                SiegeClientProfile.Profile.HIGH_CONTRAST,
                SiegeClientProfile.Profile.IMMERSIVE
        };
    }

    public static void apply(SiegeClientProfile.Profile profile) {
        if (profile == null || profile == SiegeClientProfile.Profile.CUSTOM) return;
        Spec s = of(profile);
        SiegeConfig.intelReadingMode = s.reading();
        SiegeConfig.comfortableReading = s.reading();
        SiegeConfig.darkIntelPaper = s.reading();
        SiegeConfig.graphics = s.graphics();
        SiegeConfig.reducedMotion = s.reducedMotion();
        SiegeConfig.reduceFlashes = s.reduceFlashes();
        SiegeConfig.highContrast = s.highContrast();
        SiegeConfig.menuEffects = s.menuEffects();
        SiegeConfig.animatedBackgrounds = s.animatedBackgrounds();
        SiegeConfig.animatedIntel = s.animatedIntel();
        SiegeConfig.scanlines = s.scanlines();
        SiegeConfig.titleInterference = s.titleInterference();
        SiegeConfig.hoverSounds = s.hoverSounds();
        SiegeConfig.mainMenuIntel = s.mainMenuIntel();
        SiegeConfig.autoRotateIntel = s.autoRotateIntel();
        SiegeConfig.pauseIntelOnHover = true;
        SiegeConfig.showIntelProgress = s.showIntelProgress();
        SiegeConfig.showIntelState = true;
        SiegeConfig.trackAnnouncements = s.trackAnnouncements();
        SiegeConfig.autoContrast = s.autoContrast();
        SiegeConfig.scanlineIntensity = s.scanlineIntensity();
        SiegeConfig.interferenceIntensity = s.interferenceIntensity();
        SiegeConfig.backgroundDarkness = s.backgroundDarkness();
        SiegeConfig.panelDarkness = s.panelDarkness();
        SiegeConfig.backgroundMotionIntensity = s.backgroundMotionIntensity();
        SiegeConfig.backgroundSceneSeconds = s.backgroundSceneSeconds();
        SiegeConfig.backgroundCrossfadeSeconds = s.backgroundCrossfadeSeconds();
    }

    public static boolean matches(SiegeClientProfile.Profile profile) { return distance(profile) == 0; }

    public static int distance(SiegeClientProfile.Profile profile) {
        if (profile == null || profile == SiegeClientProfile.Profile.CUSTOM) return FIELDS;
        Spec s = of(profile);
        int distance = 0;
        distance += diff(SiegeConfig.intelReadingMode, s.reading());
        distance += diff(SiegeConfig.comfortableReading, s.reading());
        distance += diff(SiegeConfig.darkIntelPaper, s.reading());
        distance += diff(SiegeConfig.graphics, s.graphics());
        distance += diff(SiegeConfig.reducedMotion, s.reducedMotion());
        distance += diff(SiegeConfig.reduceFlashes, s.reduceFlashes());
        distance += diff(SiegeConfig.highContrast, s.highContrast());
        distance += diff(SiegeConfig.menuEffects, s.menuEffects());
        distance += diff(SiegeConfig.animatedBackgrounds, s.animatedBackgrounds());
        distance += diff(SiegeConfig.animatedIntel, s.animatedIntel());
        distance += diff(SiegeConfig.scanlines, s.scanlines());
        distance += diff(SiegeConfig.titleInterference, s.titleInterference());
        distance += diff(SiegeConfig.hoverSounds, s.hoverSounds());
        distance += diff(SiegeConfig.mainMenuIntel, s.mainMenuIntel());
        distance += diff(SiegeConfig.autoRotateIntel, s.autoRotateIntel());
        distance += diff(SiegeConfig.showIntelProgress, s.showIntelProgress());
        distance += diff(SiegeConfig.trackAnnouncements, s.trackAnnouncements());
        distance += diff(SiegeConfig.autoContrast, s.autoContrast());
        distance += diff(SiegeConfig.scanlineIntensity, s.scanlineIntensity());
        distance += diff(SiegeConfig.interferenceIntensity, s.interferenceIntensity());
        distance += diff(SiegeConfig.backgroundDarkness, s.backgroundDarkness());
        distance += diff(SiegeConfig.panelDarkness, s.panelDarkness());
        distance += diff(SiegeConfig.backgroundMotionIntensity, s.backgroundMotionIntensity());
        distance += diff(SiegeConfig.backgroundSceneSeconds, s.backgroundSceneSeconds());
        distance += diff(SiegeConfig.backgroundCrossfadeSeconds, s.backgroundCrossfadeSeconds());
        return distance;
    }

    public static int fieldCount() { return FIELDS; }
    private static int diff(boolean current, boolean expected) { return current == expected ? 0 : 1; }
    private static int diff(int current, int expected) { return current == expected ? 0 : 1; }
    private static int diff(Object current, Object expected) { return current == expected ? 0 : 1; }
}
