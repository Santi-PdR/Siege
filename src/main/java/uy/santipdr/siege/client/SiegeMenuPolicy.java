package uy.santipdr.siege.client;

/** Exact allowlist: third-party screens and all in-world screens remain untouched. */
public final class SiegeMenuPolicy {
    private SiegeMenuPolicy() { }
    public static boolean nativeDialog(String name, boolean worldLoaded, boolean ownedConfirmation) {
        if (worldLoaded) return false;
        return switch (name) {
            case "net.minecraft.client.gui.screens.ConnectScreen",
                 "net.minecraft.client.gui.screens.DisconnectedScreen",
                 "net.minecraft.client.gui.screens.EditServerScreen",
                 "net.minecraft.client.gui.screens.DirectJoinServerScreen" -> true;
            case "net.minecraft.client.gui.screens.ConfirmScreen" -> ownedConfirmation;
            default -> false;
        };
    }
    public static int entryShade(long age, boolean effects, boolean reducedMotion) {
        if (!effects || reducedMotion || age < 0 || age >= 180) return 0;
        return (int)(42 * (180 - age) / 180);
    }
}
