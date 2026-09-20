package uy.santipdr.siege.client;

/**
 * Exact vanilla-screen allowlist. SIEGE may reskin title-menu flows, but it must
 * never leak into gameplay screens or third-party GUI classes.
 */
public final class SiegeMenuPolicy {
    public enum NativeFamily {
        NONE,
        NETWORK,
        SYSTEM,
        AUDIO,
        VIDEO,
        CONTROLS,
        MOUSE,
        ACCESSIBILITY,
        LANGUAGE,
        PACKS,
        WORLD,
        CONFIRM
    }

    private static final String ROOT = "net.minecraft.client.gui.screens.";
    private static final String OPTIONS = ROOT + "OptionsScreen";
    private static final String CREDITS = ROOT + "CreditsAndAttributionScreen";

    private SiegeMenuPolicy() { }

    public static NativeFamily nativeFamily(String name) {
        if (name == null) return NativeFamily.NONE;
        return switch (name) {
            case "net.minecraft.client.gui.screens.ConnectScreen",
                 "net.minecraft.client.gui.screens.DisconnectedScreen",
                 "net.minecraft.client.gui.screens.EditServerScreen",
                 "net.minecraft.client.gui.screens.DirectJoinServerScreen",
                 "net.minecraft.client.gui.screens.ReceivingLevelScreen",
                 "net.minecraft.client.gui.screens.DownloadingTerrainScreen",
                 "net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen" -> NativeFamily.NETWORK;

            case "net.minecraft.client.gui.screens.OptionsScreen",
                 "net.minecraft.client.gui.screens.SkinCustomizationScreen",
                 "net.minecraft.client.gui.screens.OnlineOptionsScreen",
                 "net.minecraft.client.gui.screens.ChatOptionsScreen",
                 "net.minecraft.client.gui.screens.CreditsAndAttributionScreen",
                 "net.minecraft.client.gui.screens.GenericDirtMessageScreen",
                 "net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen" -> NativeFamily.SYSTEM;

            case "net.minecraft.client.gui.screens.SoundOptionsScreen" -> NativeFamily.AUDIO;
            case "net.minecraft.client.gui.screens.VideoSettingsScreen" -> NativeFamily.VIDEO;
            case "net.minecraft.client.gui.screens.controls.ControlsScreen",
                 "net.minecraft.client.gui.screens.controls.KeyBindsScreen" -> NativeFamily.CONTROLS;

            // Forge 1.20.1 places MouseSettingsScreen directly under gui.screens.
            // Keep the controls-package alias as a harmless compatibility guard for
            // mappings/ports so this screen can never silently fall back to vanilla.
            case "net.minecraft.client.gui.screens.MouseSettingsScreen",
                 "net.minecraft.client.gui.screens.controls.MouseSettingsScreen" -> NativeFamily.MOUSE;

            case "net.minecraft.client.gui.screens.AccessibilityOptionsScreen",
                 "net.minecraft.client.gui.screens.AccessibilityOnboardingScreen" -> NativeFamily.ACCESSIBILITY;
            case "net.minecraft.client.gui.screens.LanguageSelectScreen" -> NativeFamily.LANGUAGE;
            case "net.minecraft.client.gui.screens.packs.PackSelectionScreen" -> NativeFamily.PACKS;

            case "net.minecraft.client.gui.screens.worldselection.SelectWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.EditWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.ExperimentsScreen",
                 "net.minecraft.client.gui.screens.worldselection.OptimizeWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.BackupConfirmScreen",
                 "net.minecraft.client.gui.screens.worldselection.CreateFlatWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.PresetFlatWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.DatapackLoadFailureScreen" -> NativeFamily.WORLD;

            case "net.minecraft.client.gui.screens.ConfirmScreen" -> NativeFamily.CONFIRM;
            default -> NativeFamily.NONE;
        };
    }

    public static boolean nativeDialog(String name, boolean worldLoaded, boolean ownedConfirmation) {
        if (worldLoaded) return false;
        NativeFamily family = nativeFamily(name);
        if (family == NativeFamily.CONFIRM) return ownedConfirmation;
        return family != NativeFamily.NONE;
    }

    public static boolean hideNativeButton(String screenName, String translationKey) {
        if (screenName == null || translationKey == null) return false;
        if (OPTIONS.equals(screenName)) {
            return translationKey.equals("options.telemetry")
                    || translationKey.equals("options.credits_and_attribution");
        }
        if (CREDITS.equals(screenName)) {
            return translationKey.equals("credits_and_attribution.button.credits")
                    || translationKey.equals("credits_and_attribution.button.attribution");
        }
        return false;
    }

    public static int adjustedButtonY(String screenName, String translationKey, int y) {
        if (OPTIONS.equals(screenName) && "gui.done".equals(translationKey)) return Math.max(0, y - 24);
        return y;
    }

    public static boolean listRail(String screenName) {
        if (screenName == null) return false;
        return switch (screenName) {
            case "net.minecraft.client.gui.screens.LanguageSelectScreen",
                 "net.minecraft.client.gui.screens.packs.PackSelectionScreen",
                 "net.minecraft.client.gui.screens.controls.KeyBindsScreen",
                 "net.minecraft.client.gui.screens.worldselection.SelectWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.PresetFlatWorldScreen" -> true;
            default -> false;
        };
    }

    /**
     * Vanilla option screens draw their own title after their background. The
     * SIEGE overlay is later in the frame, so this returns the maximum safe band
     * that may be repainted before the custom header/context strip is drawn.
     */
    public static int vanillaTitleMaskBottom(int firstWidgetY, int screenHeight) {
        int safeHeight = Math.max(0, screenHeight);
        if (safeHeight == 0) return 0;
        int beforeWidgets = firstWidgetY <= 0 ? 36 : Math.max(22, firstWidgetY - 2);
        return Math.min(safeHeight, Math.min(40, beforeWidgets));
    }

    public static int entryShade(long age, boolean effects, boolean reducedMotion) {
        if (!effects || reducedMotion || age < 0 || age >= 180) return 0;
        return (int)(42 * (180 - age) / 180);
    }
}
