package uy.santipdr.siege.client;

/**
 * High-level client profiles introduced in SIEGE 0.50.0. Profiles only touch
 * presentation/client preferences; they never change gameplay or server rules.
 */
public final class SiegeClientProfile {
    public enum Profile {
        CINEMATIC,
        TACTICAL,
        PERFORMANCE,
        CALM,
        READING,
        CUSTOM
    }

    private SiegeClientProfile() { }

    public static Profile detect() {
        if (matchesReading()) return Profile.READING;
        if (matchesCalm()) return Profile.CALM;
        if (matchesPerformance()) return Profile.PERFORMANCE;
        if (matchesTactical()) return Profile.TACTICAL;
        if (matchesCinematic()) return Profile.CINEMATIC;
        return Profile.CUSTOM;
    }

    public static void apply(Profile profile) {
        if (profile == null || profile == Profile.CUSTOM) return;

        switch (profile) {
            case CINEMATIC -> {
                commonReading(false);
                SiegeConfig.graphics = SiegeConfig.Graphics.CINEMATIC;
                SiegeConfig.reducedMotion = false;
                SiegeConfig.reduceFlashes = false;
                SiegeConfig.highContrast = false;
                SiegeConfig.menuEffects = true;
                SiegeConfig.animatedBackgrounds = true;
                SiegeConfig.animatedIntel = true;
                SiegeConfig.scanlines = true;
                SiegeConfig.titleInterference = true;
                SiegeConfig.hoverSounds = true;
                SiegeConfig.mainMenuIntel = true;
                SiegeConfig.autoRotateIntel = true;
                SiegeConfig.pauseIntelOnHover = true;
                SiegeConfig.showIntelProgress = true;
                SiegeConfig.showIntelState = true;
                SiegeConfig.trackAnnouncements = true;
                SiegeConfig.backgroundDarkness = 18;
                SiegeConfig.panelDarkness = 58;
            }
            case TACTICAL -> {
                commonReading(false);
                SiegeConfig.graphics = SiegeConfig.Graphics.BALANCED;
                SiegeConfig.reducedMotion = false;
                SiegeConfig.reduceFlashes = false;
                SiegeConfig.highContrast = false;
                SiegeConfig.menuEffects = true;
                SiegeConfig.animatedBackgrounds = true;
                SiegeConfig.animatedIntel = true;
                SiegeConfig.scanlines = true;
                SiegeConfig.titleInterference = false;
                SiegeConfig.hoverSounds = true;
                SiegeConfig.mainMenuIntel = true;
                SiegeConfig.autoRotateIntel = true;
                SiegeConfig.pauseIntelOnHover = true;
                SiegeConfig.showIntelProgress = true;
                SiegeConfig.showIntelState = true;
                SiegeConfig.trackAnnouncements = true;
                SiegeConfig.backgroundDarkness = 30;
                SiegeConfig.panelDarkness = 72;
            }
            case PERFORMANCE -> {
                commonReading(false);
                SiegeConfig.graphics = SiegeConfig.Graphics.PERFORMANCE;
                SiegeConfig.reducedMotion = true;
                SiegeConfig.reduceFlashes = true;
                SiegeConfig.highContrast = false;
                SiegeConfig.menuEffects = false;
                SiegeConfig.animatedBackgrounds = false;
                SiegeConfig.animatedIntel = false;
                SiegeConfig.scanlines = false;
                SiegeConfig.titleInterference = false;
                SiegeConfig.hoverSounds = false;
                SiegeConfig.mainMenuIntel = true;
                SiegeConfig.autoRotateIntel = false;
                SiegeConfig.pauseIntelOnHover = true;
                SiegeConfig.showIntelProgress = false;
                SiegeConfig.showIntelState = true;
                SiegeConfig.trackAnnouncements = false;
                SiegeConfig.backgroundDarkness = 38;
                SiegeConfig.panelDarkness = 78;
            }
            case CALM -> {
                commonReading(false);
                SiegeConfig.graphics = SiegeConfig.Graphics.BALANCED;
                SiegeConfig.reducedMotion = true;
                SiegeConfig.reduceFlashes = true;
                SiegeConfig.highContrast = true;
                SiegeConfig.menuEffects = false;
                SiegeConfig.animatedBackgrounds = false;
                SiegeConfig.animatedIntel = false;
                SiegeConfig.scanlines = false;
                SiegeConfig.titleInterference = false;
                SiegeConfig.hoverSounds = false;
                SiegeConfig.mainMenuIntel = true;
                SiegeConfig.autoRotateIntel = false;
                SiegeConfig.pauseIntelOnHover = true;
                SiegeConfig.showIntelProgress = false;
                SiegeConfig.showIntelState = true;
                SiegeConfig.trackAnnouncements = false;
                SiegeConfig.backgroundDarkness = 42;
                SiegeConfig.panelDarkness = 84;
            }
            case READING -> {
                commonReading(true);
                SiegeConfig.graphics = SiegeConfig.Graphics.BALANCED;
                SiegeConfig.reducedMotion = true;
                SiegeConfig.reduceFlashes = true;
                SiegeConfig.highContrast = true;
                SiegeConfig.menuEffects = false;
                SiegeConfig.animatedBackgrounds = false;
                SiegeConfig.animatedIntel = false;
                SiegeConfig.scanlines = false;
                SiegeConfig.titleInterference = false;
                SiegeConfig.hoverSounds = false;
                SiegeConfig.mainMenuIntel = false;
                SiegeConfig.autoRotateIntel = false;
                SiegeConfig.pauseIntelOnHover = true;
                SiegeConfig.showIntelProgress = false;
                SiegeConfig.showIntelState = true;
                SiegeConfig.trackAnnouncements = false;
                SiegeConfig.backgroundDarkness = 48;
                SiegeConfig.panelDarkness = 88;
            }
            default -> { }
        }
        SiegeConfig.save();
    }

    private static void commonReading(boolean enabled) {
        SiegeConfig.intelReadingMode = enabled;
        SiegeConfig.comfortableReading = enabled;
        SiegeConfig.darkIntelPaper = enabled;
    }

    private static boolean matchesCinematic() {
        return !readingEnabled()
                && SiegeConfig.graphics == SiegeConfig.Graphics.CINEMATIC
                && !SiegeConfig.reducedMotion
                && !SiegeConfig.reduceFlashes
                && !SiegeConfig.highContrast
                && SiegeConfig.menuEffects
                && SiegeConfig.animatedBackgrounds
                && SiegeConfig.animatedIntel
                && SiegeConfig.scanlines
                && SiegeConfig.titleInterference
                && SiegeConfig.hoverSounds
                && SiegeConfig.mainMenuIntel
                && SiegeConfig.autoRotateIntel
                && SiegeConfig.showIntelProgress
                && SiegeConfig.trackAnnouncements
                && SiegeConfig.backgroundDarkness == 18
                && SiegeConfig.panelDarkness == 58;
    }

    private static boolean matchesTactical() {
        return !readingEnabled()
                && SiegeConfig.graphics == SiegeConfig.Graphics.BALANCED
                && !SiegeConfig.reducedMotion
                && !SiegeConfig.reduceFlashes
                && !SiegeConfig.highContrast
                && SiegeConfig.menuEffects
                && SiegeConfig.animatedBackgrounds
                && SiegeConfig.animatedIntel
                && SiegeConfig.scanlines
                && !SiegeConfig.titleInterference
                && SiegeConfig.hoverSounds
                && SiegeConfig.mainMenuIntel
                && SiegeConfig.autoRotateIntel
                && SiegeConfig.showIntelProgress
                && SiegeConfig.trackAnnouncements
                && SiegeConfig.backgroundDarkness == 30
                && SiegeConfig.panelDarkness == 72;
    }

    private static boolean matchesPerformance() {
        return !readingEnabled()
                && SiegeConfig.graphics == SiegeConfig.Graphics.PERFORMANCE
                && SiegeConfig.reducedMotion
                && SiegeConfig.reduceFlashes
                && !SiegeConfig.highContrast
                && !SiegeConfig.menuEffects
                && !SiegeConfig.animatedBackgrounds
                && !SiegeConfig.animatedIntel
                && !SiegeConfig.scanlines
                && !SiegeConfig.titleInterference
                && !SiegeConfig.hoverSounds
                && SiegeConfig.mainMenuIntel
                && !SiegeConfig.autoRotateIntel
                && !SiegeConfig.showIntelProgress
                && !SiegeConfig.trackAnnouncements
                && SiegeConfig.backgroundDarkness == 38
                && SiegeConfig.panelDarkness == 78;
    }

    private static boolean matchesCalm() {
        return !readingEnabled()
                && SiegeConfig.graphics == SiegeConfig.Graphics.BALANCED
                && SiegeConfig.reducedMotion
                && SiegeConfig.reduceFlashes
                && SiegeConfig.highContrast
                && !SiegeConfig.menuEffects
                && !SiegeConfig.animatedBackgrounds
                && !SiegeConfig.animatedIntel
                && !SiegeConfig.scanlines
                && !SiegeConfig.titleInterference
                && !SiegeConfig.hoverSounds
                && SiegeConfig.mainMenuIntel
                && !SiegeConfig.autoRotateIntel
                && !SiegeConfig.showIntelProgress
                && !SiegeConfig.trackAnnouncements
                && SiegeConfig.backgroundDarkness == 42
                && SiegeConfig.panelDarkness == 84;
    }

    private static boolean matchesReading() {
        return readingEnabled()
                && SiegeConfig.graphics == SiegeConfig.Graphics.BALANCED
                && SiegeConfig.reducedMotion
                && SiegeConfig.reduceFlashes
                && SiegeConfig.highContrast
                && !SiegeConfig.menuEffects
                && !SiegeConfig.animatedBackgrounds
                && !SiegeConfig.animatedIntel
                && !SiegeConfig.scanlines
                && !SiegeConfig.titleInterference
                && !SiegeConfig.hoverSounds
                && !SiegeConfig.mainMenuIntel
                && !SiegeConfig.autoRotateIntel
                && !SiegeConfig.showIntelProgress
                && !SiegeConfig.trackAnnouncements
                && SiegeConfig.backgroundDarkness == 48
                && SiegeConfig.panelDarkness == 88;
    }

    private static boolean readingEnabled() {
        return SiegeConfig.intelReadingMode && SiegeConfig.comfortableReading && SiegeConfig.darkIntelPaper;
    }

    public static String label(Profile profile, boolean spanish) {
        return switch (profile) {
            case CINEMATIC -> spanish ? "CINEMÁTICO" : "CINEMATIC";
            case TACTICAL -> spanish ? "TÁCTICO" : "TACTICAL";
            case PERFORMANCE -> spanish ? "RENDIMIENTO" : "PERFORMANCE";
            case CALM -> spanish ? "TRANQUILO" : "CALM";
            case READING -> spanish ? "LECTURA" : "READING";
            case CUSTOM -> spanish ? "PERSONALIZADO" : "CUSTOM";
        };
    }

    public static String shortLabel(Profile profile, boolean spanish) {
        return switch (profile) {
            case CINEMATIC -> spanish ? "CINE" : "CINE";
            case TACTICAL -> spanish ? "TÁCT" : "TACT";
            case PERFORMANCE -> spanish ? "REND" : "PERF";
            case CALM -> spanish ? "CALMA" : "CALM";
            case READING -> spanish ? "LEER" : "READ";
            case CUSTOM -> spanish ? "CUSTOM" : "CUSTOM";
        };
    }

    public static String description(Profile profile, boolean spanish) {
        return switch (profile) {
            case CINEMATIC -> spanish
                    ? "Máxima identidad visual: fondos y dossiers animados, interferencia y perfil gráfico cinematográfico."
                    : "Maximum visual identity: animated backgrounds and dossiers, interference and cinematic graphics.";
            case TACTICAL -> spanish
                    ? "Equilibrio recomendado: información visible, animación moderada y contraste operativo sin exceso."
                    : "Recommended balance: visible information, moderate animation and operational contrast without excess.";
            case PERFORMANCE -> spanish
                    ? "Reduce animaciones y efectos para priorizar fluidez del cliente y navegación rápida."
                    : "Reduces animations and effects to prioritize client responsiveness and fast navigation.";
            case CALM -> spanish
                    ? "Minimiza movimiento, destellos, sonidos de hover y rotaciones automáticas."
                    : "Minimizes motion, flashes, hover sounds and automatic rotations.";
            case READING -> spanish
                    ? "Prioriza Intel: papel oscuro, espaciado cómodo, alto contraste y sin rotación automática."
                    : "Prioritizes Intel: dark paper, comfortable spacing, high contrast and no automatic rotation.";
            case CUSTOM -> spanish
                    ? "Mezcla manual de ajustes. No coincide exactamente con un perfil predefinido."
                    : "Manual settings mix. It does not exactly match a predefined profile.";
        };
    }

    public static int accent(Profile profile) {
        return switch (profile) {
            case CINEMATIC -> SiegeTheme.GOLD;
            case TACTICAL -> SiegeTheme.RED;
            case PERFORMANCE -> SiegeTheme.GREEN;
            case CALM -> SiegeTheme.BLUE;
            case READING -> SiegeTheme.CYAN;
            case CUSTOM -> SiegeTheme.ORANGE;
        };
    }

    public static String icon(Profile profile) {
        return switch (profile) {
            case CINEMATIC -> "image";
            case TACTICAL -> "shield";
            case PERFORMANCE -> "settings";
            case CALM -> "eye";
            case READING -> "intel";
            case CUSTOM -> "settings";
        };
    }
}
