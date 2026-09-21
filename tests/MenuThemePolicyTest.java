import uy.santipdr.siege.client.SiegeMenuPolicy;

public class MenuThemePolicyTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    private static void themed(String name, SiegeMenuPolicy.NativeFamily family) {
        check(SiegeMenuPolicy.nativeFamily(name) == family, "Wrong family for " + name);
        check(SiegeMenuPolicy.nativeDialog(name, false, false), "Missing themed vanilla flow: " + name);
        check(!SiegeMenuPolicy.nativeDialog(name, true, true), "Theme leaked into loaded world: " + name);
    }

    public static void main(String[] args) {
        String root = "net.minecraft.client.gui.screens.";
        for (String name : new String[] {"ConnectScreen", "DisconnectedScreen", "EditServerScreen", "DirectJoinServerScreen",
                "ReceivingLevelScreen", "DownloadingTerrainScreen"})
            themed(root + name, SiegeMenuPolicy.NativeFamily.NETWORK);
        themed(root + "multiplayer.JoinMultiplayerScreen", SiegeMenuPolicy.NativeFamily.NETWORK);

        for (String name : new String[] {"OptionsScreen", "SkinCustomizationScreen", "OnlineOptionsScreen", "ChatOptionsScreen",
                "CreditsAndAttributionScreen", "GenericDirtMessageScreen"})
            themed(root + name, SiegeMenuPolicy.NativeFamily.SYSTEM);
        themed(root + "telemetry.TelemetryInfoScreen", SiegeMenuPolicy.NativeFamily.SYSTEM);

        themed(root + "SoundOptionsScreen", SiegeMenuPolicy.NativeFamily.AUDIO);
        themed(root + "VideoSettingsScreen", SiegeMenuPolicy.NativeFamily.VIDEO);
        for (String name : new String[] {"ControlsScreen", "KeyBindsScreen"})
            themed(root + "controls." + name, SiegeMenuPolicy.NativeFamily.CONTROLS);
        // Forge 1.20.1 uses the root package; the controls alias remains a compatibility guard.
        themed(root + "MouseSettingsScreen", SiegeMenuPolicy.NativeFamily.MOUSE);
        themed(root + "controls.MouseSettingsScreen", SiegeMenuPolicy.NativeFamily.MOUSE);
        themed(root + "AccessibilityOptionsScreen", SiegeMenuPolicy.NativeFamily.ACCESSIBILITY);
        themed(root + "AccessibilityOnboardingScreen", SiegeMenuPolicy.NativeFamily.ACCESSIBILITY);
        themed(root + "LanguageSelectScreen", SiegeMenuPolicy.NativeFamily.LANGUAGE);
        themed(root + "packs.PackSelectionScreen", SiegeMenuPolicy.NativeFamily.PACKS);

        String world = root + "worldselection.";
        for (String name : new String[] {"SelectWorldScreen", "CreateWorldScreen", "EditWorldScreen", "ExperimentsScreen",
                "OptimizeWorldScreen", "BackupConfirmScreen", "CreateFlatWorldScreen", "PresetFlatWorldScreen",
                "DatapackLoadFailureScreen"})
            themed(world + name, SiegeMenuPolicy.NativeFamily.WORLD);

        check(SiegeMenuPolicy.nativeFamily(root + "ConfirmScreen") == SiegeMenuPolicy.NativeFamily.CONFIRM,
                "ConfirmScreen family missing");
        check(!SiegeMenuPolicy.nativeDialog(root + "ConfirmScreen", false, false), "Foreign confirmation touched");
        check(SiegeMenuPolicy.nativeDialog(root + "ConfirmScreen", false, true), "Menu confirmation missing");

        check(!SiegeMenuPolicy.nativeDialog("org.embeddedt.embeddium.gui.EmbeddiumVideoOptionsScreen", false, true),
                "Embeddium video screen must stay untouched");
        check(!SiegeMenuPolicy.nativeDialog("me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI", false, true),
                "Sodium/Embeddium legacy video screen must stay untouched");

        check(SiegeMenuPolicy.hideNativeButton(root + "OptionsScreen", "options.telemetry"), "Telemetry button still exposed");
        check(SiegeMenuPolicy.hideNativeButton(root + "OptionsScreen", "options.credits_and_attribution"), "Credits entry still exposed");
        check(SiegeMenuPolicy.hideNativeButton(root + "CreditsAndAttributionScreen", "credits_and_attribution.button.credits"),
                "Credits button still exposed");
        check(SiegeMenuPolicy.hideNativeButton(root + "CreditsAndAttributionScreen", "credits_and_attribution.button.attribution"),
                "Attribution button still exposed");
        check(!SiegeMenuPolicy.hideNativeButton(root + "OptionsScreen", "options.video"), "Useful option hidden");
        check(!SiegeMenuPolicy.hideNativeButton(root + "CreditsAndAttributionScreen", "credits_and_attribution.button.licenses"),
                "License button should not be removed defensively");
        check(SiegeMenuPolicy.adjustedButtonY(root + "OptionsScreen", "gui.done", 200) == 176,
                "Done row was not compacted by exactly one vanilla grid row");
        check(SiegeMenuPolicy.adjustedButtonY(root + "SoundOptionsScreen", "gui.done", 200) == 200,
                "Unrelated Done button moved");

        check(SiegeMenuPolicy.listRail(root + "controls.KeyBindsScreen"), "Keybind list rail missing");
        check(SiegeMenuPolicy.listRail(root + "LanguageSelectScreen"), "Language list rail missing");
        check(!SiegeMenuPolicy.listRail(root + "controls.ControlsScreen"), "Controls rail can overlap buttons");
        check(!SiegeMenuPolicy.listRail(root + "MouseSettingsScreen"), "Mouse rail can overlap sliders");
        check(!SiegeMenuPolicy.listRail(root + "AccessibilityOptionsScreen"), "Accessibility rail can overlap options");
        check(!SiegeMenuPolicy.listRail(root + "VideoSettingsScreen"), "Video rail can overlap options");
        check(!SiegeMenuPolicy.listRail(root + "SoundOptionsScreen"), "Audio rail can overlap options");

        // A custom title may safely erase the vanilla title strip, but the mask
        // must never cross the first active widget or grow indefinitely.
        check(SiegeMenuPolicy.vanillaTitleMaskBottom(40, 240) == 34, "Wide title mask cap changed");
        check(SiegeMenuPolicy.vanillaTitleMaskBottom(28, 240) == 26, "Title mask must stop before widgets");
        check(SiegeMenuPolicy.vanillaTitleMaskBottom(18, 240) == 20, "Header minimum changed");
        check(SiegeMenuPolicy.vanillaTitleMaskBottom(40, 16) == 16, "Mask escaped short viewport");
        check(SiegeMenuPolicy.vanillaTitleMaskBottom(0, 0) == 0, "Zero-height mask invalid");

        for (String name : new String[] {"PauseScreen", "ChatScreen", "inventory.InventoryScreen", "DeathScreen"})
            check(!SiegeMenuPolicy.nativeDialog(root + name, false, true), "Gameplay screen modified: " + name);
        check(!SiegeMenuPolicy.nativeDialog("other.mod.OptionsScreen", false, true), "Third-party class touched");
        check(!SiegeMenuPolicy.nativeDialog(root + "OptionsScreen$Custom", false, true), "Subclass touched");
        check(SiegeMenuPolicy.nativeFamily(null) == SiegeMenuPolicy.NativeFamily.NONE, "Null screen family");

        for (long t = -1; t <= 250; t++) {
            int alpha = SiegeMenuPolicy.entryShade(t, true, false);
            check(alpha >= 0 && alpha <= 42, "Fade obscures interface");
            check(SiegeMenuPolicy.entryShade(t, true, true) == 0, "Reduced-motion fade");
            check(SiegeMenuPolicy.entryShade(t, false, false) == 0, "Disabled-effects fade");
            if (t >= 180) check(alpha == 0, "Fade outlived 180ms");
        }
        System.out.println("Vanilla profiles, Mouse root mapping, title masking, dead-button removal, Embeddium exclusion and layout bounds passed");
    }
}
