package uy.santipdr.siege.client;

/** Profile-fit diagnostics backed by the same specification that applies presets. */
public final class SiegeProfileMetrics {
    private SiegeProfileMetrics() { }

    public static SiegeClientProfile.Profile nearest() {
        SiegeClientProfile.Profile best = SiegeClientProfile.Profile.TACTICAL;
        int bestDistance = Integer.MAX_VALUE;
        for (SiegeClientProfile.Profile profile : SiegeProfileSpec.presets()) {
            int distance = distance(profile);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = profile;
            }
        }
        return best;
    }

    public static int distance(SiegeClientProfile.Profile profile) {
        return SiegeProfileSpec.distance(profile);
    }

    public static int fitPercent(SiegeClientProfile.Profile profile) {
        int fields = fieldCount();
        int distance = Math.max(0, Math.min(fields, distance(profile)));
        return Math.round((fields - distance) * 100.0F / fields);
    }

    public static int fieldCount() { return SiegeProfileSpec.fieldCount(); }

    public static boolean exact(SiegeClientProfile.Profile profile) {
        return SiegeProfileSpec.matches(profile);
    }
}
