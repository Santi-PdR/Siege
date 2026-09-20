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

        for (String name : new String[] {"OptionsScreen", "SkinCustomizationScreen", "AccessibilityOptionsScreen",
                "AccessibilityOnboardingScreen", "OnlineOptionsScreen", "ChatOptionsScreen", "CreditsAndAttributionScreen",
                "GenericDirtMessageScreen"})
            themed(root + name, SiegeMenuPolicy.NativeFamily.SYSTEM);
        themed(root + "telemetry.TelemetryInfoScreen", SiegeMenuPolicy.NativeFamily.SYSTEM);

        themed(root + "SoundOptionsScreen", SiegeMenuPolicy.NativeFamily.AUDIO);
        themed(root + "VideoSettingsScreen", SiegeMenuPolicy.NativeFamily.VIDEO);
        for (String name : new String[] {"ControlsScreen", "KeyBindsScreen", "MouseSettingsScreen"})
            themed(root + "controls." + name, SiegeMenuPolicy.NativeFamily.CONTROLS);
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
        System.out.println("Vanilla title-menu allowlist, families, gameplay exclusions and transition bounds passed");
    }
}
