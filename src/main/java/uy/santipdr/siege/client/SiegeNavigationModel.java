package uy.santipdr.siege.client;

/**
 * Minecraft-independent map of SIEGE surfaces. It gives owned and approved
 * title-menu screens one section identity, accent family and responsive chrome reservation.
 */
public final class SiegeNavigationModel {
    public enum Section {
        HOME, DEPLOYMENT, INTEL, GUIDE, SETTINGS, COMMAND, DIAGNOSTICS, BACKGROUNDS, INSPECTOR, NATIVE
    }

    public record Descriptor(Section section, String es, String en, String code, int accent) {
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    private SiegeNavigationModel() { }

    public static Descriptor forClassName(String className) {
        String simple = className == null ? "" : className.substring(className.lastIndexOf('.') + 1);
        return switch (simple) {
            case "SiegeTitleScreen" -> d(Section.HOME, "PORTADA", "HOME", "HOME", 0xFFE54852);
            case "SiegeMultiplayerScreen", "JoinMultiplayerScreen", "DirectJoinServerScreen", "EditServerScreen",
                 "ConnectScreen", "DisconnectedScreen" ->
                    d(Section.DEPLOYMENT, "DESPLIEGUE", "DEPLOYMENT", "DEP", 0xFFE54852);
            case "IntelScreenV3", "SiegeArchiveScreen" -> d(Section.INTEL, "INTEL", "INTEL", "INT", 0xFFD6AE65);
            case "SiegeGuideScreen", "SiegeGuideImageScreen" -> d(Section.GUIDE, "GUÍA", "GUIDE", "GDE", 0xFFD6AE65);
            case "SiegeSettingsScreen" -> d(Section.SETTINGS, "CONFIGURACIÓN", "SETTINGS", "CFG", 0xFFE54852);
            case "SiegeSystemScreen" -> d(Section.COMMAND, "CENTRO DE COMANDO", "COMMAND CENTER", "CMD", 0xFF68C6D8);
            case "SiegeDiagnosticsScreen" -> d(Section.DIAGNOSTICS, "DIAGNÓSTICO", "DIAGNOSTICS", "DIA", 0xFFE89B59);
            case "SiegeSceneScreen" -> d(Section.BACKGROUNDS, "FONDOS", "BACKGROUNDS", "BG", 0xFF789BFF);
            case "IntelPortraitScreen" -> d(Section.INSPECTOR, "INSPECTOR INTEL", "INTEL INSPECTOR", "VIEW", 0xFFD6AE65);

            // Native title-menu surfaces keep vanilla state/logic but no longer collapse
            // into one generic SYS identity in the shared SIEGE chrome.
            case "SoundOptionsScreen" -> d(Section.NATIVE, "MEZCLA DE AUDIO", "AUDIO MIX", "AUD", 0xFFD6AE65);
            case "VideoSettingsScreen" -> d(Section.NATIVE, "VIDEO", "VIDEO", "VID", 0xFF68C6D8);
            case "ControlsScreen", "KeyBindsScreen" -> d(Section.NATIVE, "CONTROLES", "CONTROLS", "CTL", 0xFF789BFF);
            case "MouseSettingsScreen" -> d(Section.NATIVE, "MOUSE", "MOUSE", "MSE", 0xFF789BFF);
            case "AccessibilityOptionsScreen", "AccessibilityOnboardingScreen" ->
                    d(Section.NATIVE, "ACCESIBILIDAD", "ACCESSIBILITY", "ACC", 0xFF72C58A);
            case "LanguageSelectScreen" -> d(Section.NATIVE, "IDIOMA", "LANGUAGE", "LNG", 0xFF68C6D8);
            case "PackSelectionScreen" -> d(Section.NATIVE, "RECURSOS", "RESOURCES", "PAK", 0xFFD6AE65);
            case "SelectWorldScreen", "CreateWorldScreen", "EditWorldScreen", "ExperimentsScreen",
                 "OptimizeWorldScreen", "BackupConfirmScreen", "CreateFlatWorldScreen", "PresetFlatWorldScreen" ->
                    d(Section.NATIVE, "ARCHIVOS DE MUNDO", "WORLD FILES", "WRD", 0xFF72C58A);
            case "OptionsScreen", "SkinCustomizationScreen", "OnlineOptionsScreen", "ChatOptionsScreen" ->
                    d(Section.NATIVE, "SISTEMA", "SYSTEM", "SYS", 0xFFE89B59);
            default -> d(Section.NATIVE, "SISTEMA", "SYSTEM", "SYS", 0xFF68C6D8);
        };
    }

    public static int topInset(int width, int height) {
        return switch (SiegeUiLayout.density(width, height)) {
            case ULTRA_COMPACT -> 13;
            case COMPACT -> 15;
            case STANDARD, WIDE -> 17;
        };
    }

    public static int statusWidth(int width) {
        if (width < 420) return Math.max(80, width / 3);
        return Math.min(310, Math.max(180, width / 4));
    }

    private static Descriptor d(Section section, String es, String en, String code, int accent) {
        return new Descriptor(section, es, en, code, accent);
    }
}
