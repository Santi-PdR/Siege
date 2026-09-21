import uy.santipdr.siege.client.SiegeNavigationModel;
import uy.santipdr.siege.client.SiegeUiLayout;

public final class NavigationIdentityTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        expect("net.minecraft.client.gui.screens.SoundOptionsScreen", "AUD", "AUDIO MIX");
        expect("net.minecraft.client.gui.screens.VideoSettingsScreen", "VID", "VIDEO");
        expect("net.minecraft.client.gui.screens.controls.ControlsScreen", "CTL", "CONTROLS");
        expect("net.minecraft.client.gui.screens.MouseSettingsScreen", "MSE", "MOUSE");
        expect("net.minecraft.client.gui.screens.AccessibilityOptionsScreen", "ACC", "ACCESSIBILITY");
        expect("net.minecraft.client.gui.screens.LanguageSelectScreen", "LNG", "LANGUAGE");
        expect("net.minecraft.client.gui.screens.packs.PackSelectionScreen", "PAK", "RESOURCES");
        expect("net.minecraft.client.gui.screens.worldselection.SelectWorldScreen", "WRD", "WORLD FILES");
        expect("uy.santipdr.siege.client.SiegeDiagnosticsScreen", "DIA", "DIAGNOSTICS");
        expect("uy.santipdr.siege.client.SiegeSceneScreen", "BG", "BACKGROUNDS");

        check(SiegeNavigationModel.topInset(320, 240) < SiegeNavigationModel.topInset(1200, 700),
                "Compact view must reserve a smaller chrome inset");
        check(SiegeNavigationModel.statusWidth(300) < SiegeNavigationModel.statusWidth(1200),
                "Status width should adapt to viewport width");
        check(SiegeUiLayout.density(320, 240) == SiegeUiLayout.Density.ULTRA_COMPACT, "Ultra compact identity");
        check(SiegeUiLayout.density(1280, 720) == SiegeUiLayout.Density.WIDE, "Wide identity");
        System.out.println("SIEGE navigation identities and responsive chrome contracts passed");
    }

    private static void expect(String className, String code, String english) {
        var descriptor = SiegeNavigationModel.forClassName(className);
        check(descriptor.code().equals(code), className + " code -> " + descriptor.code());
        check(descriptor.label(false).equals(english), className + " label -> " + descriptor.label(false));
    }
}
