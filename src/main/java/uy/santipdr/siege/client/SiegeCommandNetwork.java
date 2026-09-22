package uy.santipdr.siege.client;

import java.util.List;

/** SIEGE 5.00 navigation hierarchy used by the War Room to avoid a flat wall of buttons. */
public final class SiegeCommandNetwork {
    public enum Lane { DEPLOYMENT, INTELLIGENCE, KNOWLEDGE, SYSTEMS }

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
            case KNOWLEDGE -> List.of(
                    SiegeOperationsIndex.Route.KNOWLEDGE,
                    SiegeOperationsIndex.Route.ARCHIVE,
                    SiegeOperationsIndex.Route.ARMORY,
                    SiegeOperationsIndex.Route.FIELD_MANUAL,
                    SiegeOperationsIndex.Route.MEDIA);
            case SYSTEMS -> List.of(
                    SiegeOperationsIndex.Route.COMMAND,
                    SiegeOperationsIndex.Route.DIAGNOSTICS,
                    SiegeOperationsIndex.Route.SETTINGS,
                    SiegeOperationsIndex.Route.BACKGROUNDS);
        };
    }

    public static Lane laneFor(SiegeOperationsIndex.Route route) {
        if (route == null) return Lane.DEPLOYMENT;
        for (Lane lane : Lane.values()) {
            if (routes(lane).contains(route)) return lane;
        }
        return Lane.DEPLOYMENT;
    }

    public static boolean coversEveryRouteExactlyOnce() {
        var flattened = java.util.Arrays.stream(Lane.values()).flatMap(lane -> routes(lane).stream()).toList();
        return flattened.size() == SiegeOperationsIndex.Route.values().length
                && flattened.stream().distinct().count() == flattened.size();
    }
}
