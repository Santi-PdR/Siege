package uy.santipdr.siege.client;

/**
 * High-level presentation profiles. 1.25 delegates every preset value to
 * {@link SiegeProfileSpec}, so apply/detect/diagnostics can no longer drift apart.
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
        for (Profile profile : SiegeProfileSpec.presets()) {
            if (SiegeProfileSpec.matches(profile)) return profile;
        }
        return Profile.CUSTOM;
    }

    public static void apply(Profile profile) {
        if (profile == null || profile == Profile.CUSTOM) return;
        SiegeProfileSpec.apply(profile);
        SiegeConfig.save();
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
            case CINEMATIC -> "CINE";
            case TACTICAL -> spanish ? "TÁCT" : "TACT";
            case PERFORMANCE -> spanish ? "REND" : "PERF";
            case CALM -> spanish ? "CALMA" : "CALM";
            case READING -> spanish ? "LEER" : "READ";
            case CUSTOM -> "CUSTOM";
        };
    }

    public static String description(Profile profile, boolean spanish) {
        return switch (profile) {
            case CINEMATIC -> spanish
                    ? "Identidad visual máxima: fondos y dossiers animados, scanlines e interferencia controlable."
                    : "Maximum visual identity: animated backgrounds and dossiers, scanlines and controllable interference.";
            case TACTICAL -> spanish
                    ? "Equilibrio operativo: información visible, animación moderada y contraste automático."
                    : "Operational balance: visible information, moderate animation and automatic contrast.";
            case PERFORMANCE -> spanish
                    ? "Reduce trabajo visual por frame: sin animaciones ambientales, scanlines ni interferencia."
                    : "Reduces per-frame visual work: no ambient animation, scanlines or interference.";
            case CALM -> spanish
                    ? "Minimiza movimiento, destellos, sonidos de hover y rotaciones automáticas con alto contraste."
                    : "Minimizes motion, flashes, hover sounds and automatic rotations with high contrast.";
            case READING -> spanish
                    ? "Prioriza Intel: papel oscuro, espaciado cómodo, alto contraste y sin movimiento automático."
                    : "Prioritizes Intel: dark paper, comfortable spacing, high contrast and no automatic motion.";
            case CUSTOM -> spanish
                    ? "Mezcla manual. El Centro de Comando indica qué perfil está más cerca y cuánto difiere."
                    : "Manual mix. Command Center shows the nearest profile and how far the settings drift.";
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
