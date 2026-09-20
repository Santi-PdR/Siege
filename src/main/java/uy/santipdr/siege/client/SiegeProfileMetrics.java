package uy.santipdr.siege.client;

/** Profile-fit metrics kept separate so diagnostics can evolve without changing preset behavior. */
public final class SiegeProfileMetrics {
    private static final int FIELDS = 19;

    private SiegeProfileMetrics() { }

    public static SiegeClientProfile.Profile nearest() {
        SiegeClientProfile.Profile best = SiegeClientProfile.Profile.TACTICAL;
        int bestDistance = Integer.MAX_VALUE;
        for (SiegeClientProfile.Profile profile : presets()) {
            int distance = distance(profile);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = profile;
            }
        }
        return best;
    }

    public static int distance(SiegeClientProfile.Profile profile) {
        if (profile == null || profile == SiegeClientProfile.Profile.CUSTOM) return FIELDS;
        Expected expected = expected(profile);
        int distance = 0;
        distance += diff(SiegeConfig.intelReadingMode, expected.reading());
        distance += diff(SiegeConfig.comfortableReading, expected.reading());
        distance += diff(SiegeConfig.darkIntelPaper, expected.reading());
        distance += diff(SiegeConfig.graphics, expected.graphics());
        distance += diff(SiegeConfig.reducedMotion, expected.reducedMotion());
        distance += diff(SiegeConfig.reduceFlashes, expected.reduceFlashes());
        distance += diff(SiegeConfig.highContrast, expected.highContrast());
        distance += diff(SiegeConfig.menuEffects, expected.menuEffects());
        distance += diff(SiegeConfig.animatedBackgrounds, expected.animatedBackgrounds());
        distance += diff(SiegeConfig.animatedIntel, expected.animatedIntel());
        distance += diff(SiegeConfig.scanlines, expected.scanlines());
        distance += diff(SiegeConfig.titleInterference, expected.titleInterference());
        distance += diff(SiegeConfig.hoverSounds, expected.hoverSounds());
        distance += diff(SiegeConfig.mainMenuIntel, expected.mainMenuIntel());
        distance += diff(SiegeConfig.autoRotateIntel, expected.autoRotateIntel());
        distance += diff(SiegeConfig.showIntelProgress, expected.showIntelProgress());
        distance += diff(SiegeConfig.trackAnnouncements, expected.trackAnnouncements());
        distance += diff(SiegeConfig.backgroundDarkness, expected.backgroundDarkness());
        distance += diff(SiegeConfig.panelDarkness, expected.panelDarkness());
        return distance;
    }

    public static int fitPercent(SiegeClientProfile.Profile profile) {
        int distance = Math.max(0, Math.min(FIELDS, distance(profile)));
        return Math.round((FIELDS - distance) * 100.0F / FIELDS);
    }

    public static int fieldCount() { return FIELDS; }

    public static boolean exact(SiegeClientProfile.Profile profile) { return distance(profile) == 0; }

    private static SiegeClientProfile.Profile[] presets() {
        return new SiegeClientProfile.Profile[] {
                SiegeClientProfile.Profile.CINEMATIC,
                SiegeClientProfile.Profile.TACTICAL,
                SiegeClientProfile.Profile.PERFORMANCE,
                SiegeClientProfile.Profile.CALM,
                SiegeClientProfile.Profile.READING
        };
    }

    private static Expected expected(SiegeClientProfile.Profile profile) {
        return switch (profile) {
            case CINEMATIC -> new Expected(false, SiegeConfig.Graphics.CINEMATIC, false, false, false,
                    true, true, true, true, true, true, true, true, true, true, 18, 58);
            case TACTICAL -> new Expected(false, SiegeConfig.Graphics.BALANCED, false, false, false,
                    true, true, true, true, false, true, true, true, true, true, 30, 72);
            case PERFORMANCE -> new Expected(false, SiegeConfig.Graphics.PERFORMANCE, true, true, false,
                    false, false, false, false, false, false, true, false, false, false, 38, 78);
            case CALM -> new Expected(false, SiegeConfig.Graphics.BALANCED, true, true, true,
                    false, false, false, false, false, false, true, false, false, false, 42, 84);
            case READING -> new Expected(true, SiegeConfig.Graphics.BALANCED, true, true, true,
                    false, false, false, false, false, false, false, false, false, false, 48, 88);
            default -> new Expected(false, SiegeConfig.Graphics.BALANCED, false, false, false,
                    true, true, true, true, false, true, true, true, true, true, 30, 72);
        };
    }

    private static int diff(boolean current, boolean expected) { return current == expected ? 0 : 1; }
    private static int diff(int current, int expected) { return current == expected ? 0 : 1; }
    private static int diff(Object current, Object expected) { return current == expected ? 0 : 1; }

    private record Expected(boolean reading, SiegeConfig.Graphics graphics,
                            boolean reducedMotion, boolean reduceFlashes, boolean highContrast,
                            boolean menuEffects, boolean animatedBackgrounds, boolean animatedIntel,
                            boolean scanlines, boolean titleInterference, boolean hoverSounds,
                            boolean mainMenuIntel, boolean autoRotateIntel, boolean showIntelProgress,
                            boolean trackAnnouncements, int backgroundDarkness, int panelDarkness) { }
}
