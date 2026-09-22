package uy.santipdr.siege.client;

/**
 * Assets deliberately kept outside normal presentation catalogs.
 *
 * These identifiers may be used by a future hidden trigger, but they must never
 * be added to the ordinary background gallery, automatic scene rotation or the
 * home scene label. Keeping the reservation explicit prevents an easter egg from
 * silently becoming regular UI content again during refactors.
 */
public final class SiegeEasterEggVault {
    public static final String TEMPEST_JUTCHERSON = "tempest_jutcherson";

    private SiegeEasterEggVault() { }

    public static boolean reserved(String id) {
        return TEMPEST_JUTCHERSON.equals(id);
    }

    public static boolean allowedInMenuCatalog(String id) {
        return !reserved(id);
    }
}
