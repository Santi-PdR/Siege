package uy.santipdr.siege.client;

import java.util.Arrays;
import java.util.List;

/** SIEGE 5.00 Operations only exposes routes useful during play/preparation. */
public final class SiegeCommandNetwork {
    public enum Lane { DEPLOYMENT, INTELLIGENCE, REFERENCE }

    private SiegeCommandNetwork() { }

    public static List<SiegeOperationsIndex.Route> routes(Lane lane) {
        return switch (lane) {
            case DEPLOYMENT -> List.of(
                    SiegeOperationsIndex.Route.BRIEFING,
                    SiegeOperationsIndex.Route.DEPLOYMENT,
                    SiegeOperationsIndex.Route.THREATS);
            case INTELLIGENCE -> List.of(
                    SiegeOperationsIndex.Route.INTEL,
                    SiegeOperationsIndex.Route.RACES,
                    SiegeOperationsIndex.Route.PROGRESSION,
                    SiegeOperationsIndex.Route.ATLAS);
            case REFERENCE -> List.of(
                    SiegeOperationsIndex.Route.KNOWLEDGE,
                    SiegeOperationsIndex.Route.FIELD_MANUAL,
                    SiegeOperationsIndex.Route.MEDIA);
        };
    }

    public static Lane laneFor(SiegeOperationsIndex.Route route) {
        if (route == null) return Lane.DEPLOYMENT;
        for (Lane lane : Lane.values()) if (routes(lane).contains(route)) return lane;
        return Lane.DEPLOYMENT;
    }

    public static boolean isVisible(SiegeOperationsIndex.Route route) {
        return route != null && Arrays.stream(Lane.values()).anyMatch(lane -> routes(lane).contains(route));
    }

    public static boolean hasNoDuplicateVisibleRoutes() {
        var flattened = Arrays.stream(Lane.values()).flatMap(lane -> routes(lane).stream()).toList();
        return flattened.stream().distinct().count() == flattened.size();
    }
}
