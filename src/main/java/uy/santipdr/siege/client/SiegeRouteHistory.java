package uy.santipdr.siege.client;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Small in-memory session trail used by the 2.50 Operations Hub. */
public final class SiegeRouteHistory {
    private static final int LIMIT = 6;
    private static final Deque<SiegeOperationsIndex.Route> RECENT = new ArrayDeque<>();

    private SiegeRouteHistory() { }

    public static synchronized void record(SiegeOperationsIndex.Route route) {
        if (route == null) return;
        RECENT.remove(route);
        RECENT.addFirst(route);
        while (RECENT.size() > LIMIT) RECENT.removeLast();
    }

    public static synchronized List<SiegeOperationsIndex.Route> snapshot() {
        return List.copyOf(new ArrayList<>(RECENT));
    }

    static synchronized void clearForTests() {
        RECENT.clear();
    }
}
