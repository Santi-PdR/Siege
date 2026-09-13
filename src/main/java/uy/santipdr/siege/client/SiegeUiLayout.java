package uy.santipdr.siege.client;

/** Shared, Minecraft-independent responsive geometry helpers. */
public final class SiegeUiLayout {
    private SiegeUiLayout() { }

    public static boolean compactTitle(int width, int height) {
        return width < 520 || height < 290;
    }

    public static int centeredTitleWidth(int width, boolean compact, int menuWidth) {
        return Math.max(1, Math.min(compact ? width - 24 : 520, Math.max(menuWidth, width - 32)));
    }

    public static int musicButtonY(int height, boolean compact) {
        return compact ? Math.max(4, height - 27) : 9;
    }

    /** Returns zero when a notice would collide with the left command rail. */
    public static int trackNoticeWidth(int width, int menuX, int menuWidth, boolean compact) {
        int available = width - menuX - menuWidth - 38;
        return available < 96 ? 0 : Math.max(96, Math.min(compact ? 170 : 220, available));
    }

    public static int settingsColumns(int panelWidth) {
        return panelWidth < 360 ? 2 : 3;
    }

    public static int clampScroll(int value, int maximum) {
        return Math.max(0, Math.min(Math.max(0, maximum), value));
    }

    public static int scrollThumb(int viewport, int content) {
        if (viewport <= 0 || content <= 0) return 0;
        return Math.min(viewport, Math.max(10, viewport * viewport / Math.max(viewport, content)));
    }
}
