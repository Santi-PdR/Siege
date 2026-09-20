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
        LANGUAGE,
        PACKS,
        WORLD,
        CONFIRM
    }

    private SiegeMenuPolicy() { }

    public static NativeFamily nativeFamily(String name) {
        if (name == null) return NativeFamily.NONE;
        return switch (name) {
            // Multiplayer / connection flow.
            case "net.minecraft.client.gui.screens.ConnectScreen",
                 "net.minecraft.client.gui.screens.DisconnectedScreen",
                 "net.minecraft.client.gui.screens.EditServerScreen",
                 "net.minecraft.client.gui.screens.DirectJoinServerScreen",
                 "net.minecraft.client.gui.screens.ReceivingLevelScreen",
                 "net.minecraft.client.gui.screens.DownloadingTerrainScreen" -> NativeFamily.NETWORK;

            // Main vanilla settings shell and small supporting screens.
            case "net.minecraft.client.gui.screens.OptionsScreen",
                 "net.minecraft.client.gui.screens.SkinCustomizationScreen",
                 "net.minecraft.client.gui.screens.AccessibilityOptionsScreen",
                 "net.minecraft.client.gui.screens.OnlineOptionsScreen",
                 "net.minecraft.client.gui.screens.ChatOptionsScreen",
                 "net.minecraft.client.gui.screens.GenericDirtMessageScreen" -> NativeFamily.SYSTEM;

            case "net.minecraft.client.gui.screens.SoundOptionsScreen" -> NativeFamily.AUDIO;
            case "net.minecraft.client.gui.screens.VideoSettingsScreen" -> NativeFamily.VIDEO;
            case "net.minecraft.client.gui.screens.controls.ControlsScreen" -> NativeFamily.CONTROLS;
            case "net.minecraft.client.gui.screens.LanguageSelectScreen" -> NativeFamily.LANGUAGE;
            case "net.minecraft.client.gui.screens.packs.PackSelectionScreen" -> NativeFamily.PACKS;

            // Hidden singleplayer route and every vanilla world-setup screen it opens.
            case "net.minecraft.client.gui.screens.worldselection.SelectWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.EditWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.ExperimentsScreen",
                 "net.minecraft.client.gui.screens.worldselection.OptimizeWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.BackupConfirmScreen",
                 "net.minecraft.client.gui.screens.worldselection.CreateFlatWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.PresetFlatWorldScreen",
                 "net.minecraft.client.gui.screens.worldselection.DatapackLoadFailureScreen" -> NativeFamily.WORLD;

            // Confirmation is deliberately scoped to a transition that originated
            // from an already themed screen. Mods use ConfirmScreen too.
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

    public static int entryShade(long age, boolean effects, boolean reducedMotion) {
        if (!effects || reducedMotion || age < 0 || age >= 180) return 0;
        return (int)(42 * (180 - age) / 180);
    }
}
