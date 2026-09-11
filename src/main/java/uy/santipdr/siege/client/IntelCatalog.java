package uy.santipdr.siege.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared read-only view of the troop catalog.
 *
 * The dossier data still lives in IntelScreen so 0.6.0 can focus on rebuilding the
 * navigation/rendering layer without duplicating the long lore records. Once
 * the legacy screen is removed in a later data migration this class can become the
 * canonical catalog directly.
 */
final class IntelCatalog {
    private static final List<IntelEntry> FILES = loadFiles();

    private IntelCatalog() { }

    static List<IntelEntry> files() {
        return FILES;
    }

    static List<IntelEntry> filtered(String category) {
        if ("ALL".equals(category)) return FILES;
        List<IntelEntry> result = new ArrayList<>();
        for (IntelEntry entry : FILES) {
            if ("FAVORITES".equals(category) ? SiegeConfig.isFavoriteIntel(entry.code())
                    : entry.category().equals(category)) result.add(entry);
        }
        return List.copyOf(result);
    }

    static List<IntelEntry> previewable() {
        List<IntelEntry> result = new ArrayList<>();
        for (IntelEntry entry : FILES) {
            if (entry.category().equals("UNIT") || entry.category().equals("ADVANCED")) {
                result.add(entry);
            }
        }
        return List.copyOf(result);
    }

    static int count(String category) {
        return filtered(category).size();
    }

    @SuppressWarnings("unchecked")
    private static List<IntelEntry> loadFiles() {
        try {
            Field field = IntelScreen.class.getDeclaredField("FILES");
            field.setAccessible(true);
            Object value = field.get(null);
            if (value instanceof List<?> list) {
                return List.copyOf((List<IntelEntry>) list);
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
        return List.of();
    }
}
