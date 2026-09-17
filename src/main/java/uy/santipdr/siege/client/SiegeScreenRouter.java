package uy.santipdr.siege.client;

/**
 * Routes only exact vanilla menu destinations. Subclasses from other mods and
 * every in-world screen remain untouched.
 */
public final class SiegeScreenRouter {
    private static final String TITLE = "net.minecraft.client.gui.screens.TitleScreen";
    private static final String MULTIPLAYER = "net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen";

    private SiegeScreenRouter() { }

    public static Destination destination(String className, boolean worldLoaded) {
        if (worldLoaded || className == null) return Destination.KEEP;
        if (TITLE.equals(className)) return Destination.TITLE;
        if (MULTIPLAYER.equals(className)) return Destination.MULTIPLAYER;
        return Destination.KEEP;
    }

    public enum Destination { KEEP, TITLE, MULTIPLAYER }
}
