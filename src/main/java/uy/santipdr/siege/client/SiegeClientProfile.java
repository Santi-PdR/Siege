package uy.santipdr.siege.client;

/** High-level presentation profiles backed by one authoritative specification. */
public final class SiegeClientProfile {
    public enum Profile {
        CINEMATIC,
        TACTICAL,
        STRONGHOLD,
        PERFORMANCE,
        CALM,
        READING,
        CLASSIC,
        HIGH_CONTRAST,
        IMMERSIVE,
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
            case STRONGHOLD -> "STRONGHOLD";
            case PERFORMANCE -> spanish ? "RENDIMIENTO" : "PERFORMANCE";
            case CALM -> spanish ? "TRANQUILO" : "CALM";
            case READING -> spanish ? "LECTURA" : "READING";
            case CLASSIC -> spanish ? "CLÁSICO" : "CLASSIC";
            case HIGH_CONTRAST -> spanish ? "ALTO CONTRASTE" : "HIGH CONTRAST";
            case IMMERSIVE -> spanish ? "INMERSIVO" : "IMMERSIVE";
            case CUSTOM -> spanish ? "PERSONALIZADO" : "CUSTOM";
        };
    }

    public static String shortLabel(Profile profile, boolean spanish) {
        return switch (profile) {
            case CINEMATIC -> "CINE";
            case TACTICAL -> spanish ? "TÁCT" : "TACT";
            case STRONGHOLD -> "SH-5";
            case PERFORMANCE -> spanish ? "REND" : "PERF";
            case CALM -> spanish ? "CALMA" : "CALM";
            case READING -> spanish ? "LEER" : "READ";
            case CLASSIC -> "CLASSIC";
            case HIGH_CONTRAST -> spanish ? "ALTO" : "HIGH";
            case IMMERSIVE -> spanish ? "INM" : "IMM";
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
            case STRONGHOLD -> spanish
                    ? "Perfil 5.0 inspirado en Stronghold/DVN: fondos activos, lectura oscura, scanlines suaves y destellos reducidos."
                    : "5.0 Stronghold/DVN-inspired profile: active scenes, dark readability, soft scanlines and reduced flashes.";
            case PERFORMANCE -> spanish
                    ? "Reduce trabajo visual por frame: sin animaciones ambientales, scanlines ni interferencia."
                    : "Reduces per-frame visual work: no ambient animation, scanlines or interference.";
            case CALM -> spanish
                    ? "Minimiza movimiento, destellos, sonidos de hover y rotaciones automáticas con alto contraste."
                    : "Minimizes motion, flashes, hover sounds and automatic rotations with high contrast.";
            case READING -> spanish
                    ? "Prioriza Intel: papel oscuro, espaciado cómodo, alto contraste y sin movimiento automático."
                    : "Prioritizes Intel: dark paper, comfortable spacing, high contrast and no automatic motion.";
            case CLASSIC -> spanish
                    ? "SIEGE sobrio: UI táctica limpia, fondos activos y casi sin interferencia visual."
                    : "Restrained SIEGE: clean tactical UI, active backgrounds and almost no visual interference.";
            case HIGH_CONTRAST -> spanish
                    ? "Legibilidad máxima: contraste fuerte, paneles oscuros, sin flashes ni movimiento automático."
                    : "Maximum legibility: strong contrast, dark panels, no flashes or automatic motion.";
            case IMMERSIVE -> spanish
                    ? "Sala de guerra completa: fondos, Intel animado, scanlines e interferencia más marcada."
                    : "Full war-room presentation: backgrounds, animated Intel, scanlines and stronger interference.";
            case CUSTOM -> spanish
                    ? "Mezcla manual. El Centro de Comando indica qué perfil está más cerca y cuánto difiere."
                    : "Manual mix. Command Center shows the nearest profile and how far the settings drift.";
        };
    }

    public static int accent(Profile profile) {
        return switch (profile) {
            case CINEMATIC, CLASSIC -> SiegeTheme.GOLD;
            case TACTICAL -> SiegeTheme.RED;
            case STRONGHOLD, READING, IMMERSIVE -> SiegeTheme.CYAN;
            case PERFORMANCE, HIGH_CONTRAST -> SiegeTheme.GREEN;
            case CALM -> SiegeTheme.BLUE;
            case CUSTOM -> SiegeTheme.ORANGE;
        };
    }

    public static String icon(Profile profile) {
        return switch (profile) {
            case CINEMATIC, IMMERSIVE -> "image";
            case TACTICAL, STRONGHOLD, HIGH_CONTRAST -> "shield";
            case PERFORMANCE, CLASSIC, CUSTOM -> "settings";
            case CALM -> "eye";
            case READING -> "intel";
        };
    }
}
