import uy.santipdr.siege.client.SiegeMenuPolicy;

public class MenuThemePolicyTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }
    public static void main(String[] args) {
        String root = "net.minecraft.client.gui.screens.";
        for (String name : new String[] {"ConnectScreen", "DisconnectedScreen", "EditServerScreen", "DirectJoinServerScreen"}) {
            check(SiegeMenuPolicy.nativeDialog(root + name, false, false), "Missing native flow: " + name);
            check(!SiegeMenuPolicy.nativeDialog(root + name, true, true), "Theme leaked into world: " + name);
        }
        check(!SiegeMenuPolicy.nativeDialog(root + "ConfirmScreen", false, false), "Foreign confirmation touched");
        check(SiegeMenuPolicy.nativeDialog(root + "ConfirmScreen", false, true), "Menu confirmation missing");
        for (String name : new String[] {"OptionsScreen", "VideoSettingsScreen", "PauseScreen", "ChatScreen", "inventory.InventoryScreen"})
            check(!SiegeMenuPolicy.nativeDialog(root + name, false, true), "Excluded screen modified: " + name);
        check(!SiegeMenuPolicy.nativeDialog("other.mod.EditServerScreen", false, true), "Third-party class touched");
        check(!SiegeMenuPolicy.nativeDialog(root + "ConnectScreen$Custom", false, true), "Subclass touched");
        for (long t = -1; t <= 250; t++) {
            int alpha = SiegeMenuPolicy.entryShade(t, true, false);
            check(alpha >= 0 && alpha <= 42, "Fade obscures interface");
            check(SiegeMenuPolicy.entryShade(t, true, true) == 0, "Reduced-motion fade");
            check(SiegeMenuPolicy.entryShade(t, false, false) == 0, "Disabled-effects fade");
            if (t >= 180) check(alpha == 0, "Fade outlived 180ms");
        }
        System.out.println("Menu allowlist, gameplay exclusions, confirmation scope and transition bounds passed");
    }
}
