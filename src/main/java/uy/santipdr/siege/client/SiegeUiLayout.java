package uy.santipdr.siege.client;

/** Shared, Minecraft-independent responsive geometry helpers. */
public final class SiegeUiLayout {
    public enum Density { ULTRA_COMPACT, COMPACT, STANDARD, WIDE }

    private SiegeUiLayout() { }

    public static Density density(int width, int height) {
        if (width < 380 || height < 270) return Density.ULTRA_COMPACT;
        if (width < 600 || height < 350) return Density.COMPACT;
        if (width < 960 || height < 540) return Density.STANDARD;
        return Density.WIDE;
    }

    public static boolean compactTitle(int width, int height) {
        return width < 520 || height < 290;
    }

    public static int safeMargin(int width, int height) {
        return switch (density(width, height)) {
            case ULTRA_COMPACT -> 6;
            case COMPACT -> 8;
            case STANDARD -> 12;
            case WIDE -> Math.min(18, Math.max(14, width / 80));
        };
    }

    public static int controlHeight(int width, int height) {
        return switch (density(width, height)) {
            case ULTRA_COMPACT -> 17;
            case COMPACT -> 19;
            case STANDARD -> 22;
            case WIDE -> 24;
        };
    }

    public static int centeredPanelWidth(int viewportWidth, int compactMax, int wideMax) {
        int margin = viewportWidth < 600 ? 8 : 14;
        int cap = viewportWidth < 700 ? compactMax : wideMax;
        return Math.max(1, Math.min(cap, viewportWidth - margin * 2));
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

    /** Historical boundary retained for older compact callers. */
    public static int settingsColumns(int panelWidth) {
        return panelWidth < 360 ? 2 : 3;
    }

    /** 1.25 settings navigation may use four columns when labels have room. */
    public static int settingsSectionColumns(int panelWidth) {
        if (panelWidth < 360) return 2;
        if (panelWidth < 620) return 3;
        return 4;
    }

    public static int clampScroll(int value, int maximum) {
        return Math.max(0, Math.min(Math.max(0, maximum), value));
    }

    public static int scrollThumb(int viewport, int content) {
        if (viewport <= 0 || content <= 0) return 0;
        return Math.min(viewport, Math.max(10, viewport * viewport / Math.max(viewport, content)));
    }
}
