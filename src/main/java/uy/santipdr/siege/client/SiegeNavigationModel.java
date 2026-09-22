package uy.santipdr.siege.client;

/** Minecraft-independent identity map for completed SIEGE 4.00 surfaces. */
public final class SiegeNavigationModel {
    public enum Section {
        HOME, OPERATIONS, BRIEFING, ATLAS, RACES, PROGRESSION, THREATS, KNOWLEDGE, DEPLOYMENT, INTEL,
        REFERENCE, FIELD_MANUAL, SETTINGS, COMMAND, DIAGNOSTICS, BACKGROUNDS, INSPECTOR, MEDIA, NATIVE
    }

    public record Descriptor(Section section, String es, String en, String code, int accent) {
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    private SiegeNavigationModel() { }

    public static Descriptor forClassName(String className) {
        String simple = className == null ? "" : className.substring(className.lastIndexOf('.') + 1);
        return switch (simple) {
            case "SiegeTitleScreen" -> d(Section.HOME, "PORTADA", "HOME", "HOME", 0xFFE54852);
            case "SiegeOperationsHubScreen" -> d(Section.OPERATIONS, "SALA DE OPERACIONES", "WAR ROOM", "OPS", 0xFF68C6D8);
            case "SiegeBriefingScreen" -> d(Section.BRIEFING, "BRIEFING DE INGRESO", "ENTRY BRIEFING", "BRF", 0xFFE89B59);
            case "SiegeAtlasScreen" -> d(Section.ATLAS, "ATLAS TÁCTICO", "TACTICAL ATLAS", "ATL", 0xFF72C98B);
            case "SiegeRaceAtlasScreen" -> d(Section.RACES, "ATLAS DE RAZAS", "RACE ATLAS", "RAC", 0xFFD6AE65);
            case "SiegeProgressionMapScreen" -> d(Section.PROGRESSION, "MAPA DE PROGRESIÓN", "PROGRESSION MAP", "PRG", 0xFFD6AE65);
            case "SiegeThreatBoardScreen" -> d(Section.THREATS, "TABLERO DE AMENAZAS", "THREAT BOARD", "THR", 0xFFE54852);
            case "SiegeServerGuideScreen" -> d(Section.KNOWLEDGE, "GUÍA DEL SERVIDOR", "SERVER GUIDE", "GUI", 0xFFE89B59);
            case "SiegeKnowledgeScreen", "SiegeKnowledgeFileScreen" ->
                    d(Section.KNOWLEDGE, "ENCICLOPEDIA DEL SERVIDOR", "SERVER ENCYCLOPEDIA", "ENC", 0xFF72C98B);
            case "SiegeMultiplayerScreen", "JoinMultiplayerScreen", "DirectJoinServerScreen", "EditServerScreen",
                 "ConnectScreen", "DisconnectedScreen" -> d(Section.DEPLOYMENT, "DESPLIEGUE", "DEPLOYMENT", "DEP", 0xFFE54852);
            case "IntelScreenV3" -> d(Section.INTEL, "DOSSIERS INTEL", "INTEL DOSSIERS", "INT", 0xFFD6AE65);
            case "SiegeArchiveScreen" -> d(Section.FIELD_MANUAL, "MANUAL DE CAMPO", "FIELD MANUAL", "FLD", 0xFFE89B59);
            case "SiegeGuideScreen", "SiegeGuideImageScreen" -> d(Section.REFERENCE, "ARCHIVO / ARSENAL", "ARCHIVE / ARMORY", "REF", 0xFFD6AE65);
            case "SiegeEvidenceReelScreen" -> d(Section.MEDIA, "EVIDENCIA", "EVIDENCE", "MED", 0xFF68C6D8);
            case "SiegeMediaRoomScreen" -> d(Section.MEDIA, "SALA MULTIMEDIA", "MEDIA ROOM", "MED", 0xFF68C6D8);
            case "SiegeSettingsScreen" -> d(Section.SETTINGS, "CONFIGURACIÓN", "SETTINGS", "CFG", 0xFFE54852);
            case "SiegeSystemScreen" -> d(Section.COMMAND, "CENTRO DE COMANDO", "COMMAND CENTER", "CMD", 0xFF68C6D8);
            case "SiegeDiagnosticsScreen" -> d(Section.DIAGNOSTICS, "DIAGNÓSTICO", "DIAGNOSTICS", "DIA", 0xFFE89B59);
            case "SiegeSceneScreen" -> d(Section.BACKGROUNDS, "FONDOS", "BACKGROUNDS", "BG", 0xFF789BFF);
            case "IntelPortraitScreen" -> d(Section.INSPECTOR, "INSPECTOR INTEL", "INTEL INSPECTOR", "VIEW", 0xFFD6AE65);
            case "SoundOptionsScreen" -> d(Section.NATIVE, "MEZCLA DE AUDIO", "AUDIO MIX", "AUD", 0xFFD6AE65);
            case "VideoSettingsScreen" -> d(Section.NATIVE, "VIDEO", "VIDEO", "VID", 0xFF68C6D8);
            case "ControlsScreen", "KeyBindsScreen" -> d(Section.NATIVE, "CONTROLES", "CONTROLS", "CTL", 0xFF789BFF);
            case "MouseSettingsScreen" -> d(Section.NATIVE, "MOUSE", "MOUSE", "MSE", 0xFF789BFF);
            case "AccessibilityOptionsScreen", "AccessibilityOnboardingScreen" -> d(Section.NATIVE, "ACCESIBILIDAD", "ACCESSIBILITY", "ACC", 0xFF72C58A);
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
