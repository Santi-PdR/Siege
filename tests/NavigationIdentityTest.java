import uy.santipdr.siege.client.SiegeNavigationModel;
import uy.santipdr.siege.client.SiegeUiLayout;

/** SIEGE 3.00 information architecture and native identity contract. */
public final class NavigationIdentityTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        expect("uy.santipdr.siege.client.SiegeTitleScreen", SiegeNavigationModel.Section.HOME, "HOME", "HOME");
        expect("uy.santipdr.siege.client.SiegeOperationsHubScreen", SiegeNavigationModel.Section.OPERATIONS, "OPS", "OPERATIONS HUB");
        expect("uy.santipdr.siege.client.SiegeKnowledgeScreen", SiegeNavigationModel.Section.KNOWLEDGE, "KNW", "KNOWLEDGE VAULT");
        expect("uy.santipdr.siege.client.SiegeMultiplayerScreen", SiegeNavigationModel.Section.DEPLOYMENT, "DEP", "DEPLOYMENT");
        expect("uy.santipdr.siege.client.IntelScreenV3", SiegeNavigationModel.Section.INTEL, "INT", "INTEL DOSSIERS");
        expect("uy.santipdr.siege.client.SiegeGuideScreen", SiegeNavigationModel.Section.REFERENCE, "REF", "ARCHIVE / ARMORY");
        expect("uy.santipdr.siege.client.SiegeArchiveScreen", SiegeNavigationModel.Section.FIELD_MANUAL, "FLD", "FIELD MANUAL");
        expect("uy.santipdr.siege.client.SiegeEvidenceReelScreen", SiegeNavigationModel.Section.MEDIA, "MED", "EVIDENCE");
        expect("uy.santipdr.siege.client.SiegeSettingsScreen", SiegeNavigationModel.Section.SETTINGS, "CFG", "SETTINGS");
        expect("uy.santipdr.siege.client.SiegeSystemScreen", SiegeNavigationModel.Section.COMMAND, "CMD", "COMMAND CENTER");
        expect("uy.santipdr.siege.client.SiegeDiagnosticsScreen", SiegeNavigationModel.Section.DIAGNOSTICS, "DIA", "DIAGNOSTICS");
        expect("uy.santipdr.siege.client.SiegeSceneScreen", SiegeNavigationModel.Section.BACKGROUNDS, "BG", "BACKGROUNDS");
        expect("uy.santipdr.siege.client.IntelPortraitScreen", SiegeNavigationModel.Section.INSPECTOR, "VIEW", "INTEL INSPECTOR");

        expect("net.minecraft.client.gui.screens.SoundOptionsScreen", SiegeNavigationModel.Section.NATIVE, "AUD", "AUDIO MIX");
        expect("net.minecraft.client.gui.screens.VideoSettingsScreen", SiegeNavigationModel.Section.NATIVE, "VID", "VIDEO");
        expect("net.minecraft.client.gui.screens.controls.ControlsScreen", SiegeNavigationModel.Section.NATIVE, "CTL", "CONTROLS");
        expect("net.minecraft.client.gui.screens.MouseSettingsScreen", SiegeNavigationModel.Section.NATIVE, "MSE", "MOUSE");
        expect("net.minecraft.client.gui.screens.AccessibilityOptionsScreen", SiegeNavigationModel.Section.NATIVE, "ACC", "ACCESSIBILITY");
        expect("net.minecraft.client.gui.screens.LanguageSelectScreen", SiegeNavigationModel.Section.NATIVE, "LNG", "LANGUAGE");
        expect("net.minecraft.client.gui.screens.packs.PackSelectionScreen", SiegeNavigationModel.Section.NATIVE, "PAK", "RESOURCES");
        expect("net.minecraft.client.gui.screens.worldselection.SelectWorldScreen", SiegeNavigationModel.Section.NATIVE, "WRD", "WORLD FILES");

        check(SiegeNavigationModel.topInset(320, 240) < SiegeNavigationModel.topInset(1200, 700),
                "Compact view must reserve a smaller chrome inset");
        check(SiegeNavigationModel.statusWidth(300) < SiegeNavigationModel.statusWidth(1200),
                "Status width should adapt to viewport width");
        check(SiegeUiLayout.density(320, 240) == SiegeUiLayout.Density.ULTRA_COMPACT, "Ultra compact identity");
        check(SiegeUiLayout.density(1280, 720) == SiegeUiLayout.Density.WIDE, "Wide identity");
        System.out.println("SIEGE 3.00 Operations/Knowledge/dossiers/reference/manual/media and native identities passed");
    }

    private static void expect(String className, SiegeNavigationModel.Section section, String code, String english) {
        var descriptor = SiegeNavigationModel.forClassName(className);
        check(descriptor.section() == section, className + " section -> " + descriptor.section());
        check(descriptor.code().equals(code), className + " code -> " + descriptor.code());
        check(descriptor.label(false).equals(english), className + " label -> " + descriptor.label(false));
    }
}
