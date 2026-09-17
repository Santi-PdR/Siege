package uy.santipdr.siege.client;

/** Label and state occupy disjoint regions, including compact category buttons. */
public record SiegeControlLayout(int labelX, int labelWidth, int badgeX, int badgeWidth) {
    public static SiegeControlLayout of(int width, boolean icon, boolean selected, int stateTextWidth) {
        int w = Math.max(1, width);
        int left = w <= 40 ? 4 : icon ? 27 : 13;
        int badge = stateTextWidth <= 0 || w < 48 ? 0 : Math.min(w / 2, stateTextWidth + 12);
        int bx = w - badge - 4;
        int right = badge > 0 ? bx - 4 : w - (w <= 40 ? 4 : selected ? 17 : 9);
        return new SiegeControlLayout(Math.min(left, w), Math.max(0, right - left), bx, badge);
    }
}
