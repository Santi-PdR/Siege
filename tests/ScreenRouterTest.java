import uy.santipdr.siege.client.SiegeScreenRouter;

public class ScreenRouterTest {
    private static final String TITLE = "net.minecraft.client.gui.screens.TitleScreen";
    private static final String MULTIPLAYER = "net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen";

    private static void expect(SiegeScreenRouter.Destination actual, SiegeScreenRouter.Destination expected,
                               String scenario) {
        if (actual != expected) throw new AssertionError(scenario + ": " + actual + " != " + expected);
    }

    public static void main(String[] args) {
        expect(SiegeScreenRouter.destination(TITLE, false), SiegeScreenRouter.Destination.TITLE,
                "Startup and singleplayer logout");
        expect(SiegeScreenRouter.destination(MULTIPLAYER, false), SiegeScreenRouter.Destination.MULTIPLAYER,
                "Multiplayer logout and disconnected Back");
        expect(SiegeScreenRouter.destination("uy.santipdr.siege.client.SiegeTitleScreen", false),
                SiegeScreenRouter.Destination.KEEP, "SIEGE title must not recurse");
        expect(SiegeScreenRouter.destination("uy.santipdr.siege.client.SiegeMultiplayerScreen", false),
                SiegeScreenRouter.Destination.KEEP, "SIEGE multiplayer must not recurse");
        expect(SiegeScreenRouter.destination("example.mod.CustomTitleScreen", false),
                SiegeScreenRouter.Destination.KEEP, "Other mod subclass");
        expect(SiegeScreenRouter.destination(TITLE, true), SiegeScreenRouter.Destination.KEEP,
                "Never replace screens while a world is loaded");
        expect(SiegeScreenRouter.destination(MULTIPLAYER, true), SiegeScreenRouter.Destination.KEEP,
                "Never replace multiplayer while connected");
        expect(SiegeScreenRouter.destination(null, false), SiegeScreenRouter.Destination.KEEP,
                "Closing a screen");
        System.out.println("Eight menu return routes passed");
    }
}
