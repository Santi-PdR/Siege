import uy.santipdr.siege.client.SiegeOperationsIndex;
import uy.santipdr.siege.client.SiegeRouteHistory;

import java.lang.reflect.Method;
import java.util.List;

public final class OperationsIndexTest {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        var atlas = SiegeOperationsIndex.search("atlas", false, 5);
        check(atlas.stream().anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.INTEL && e.id().equals("SUP-001")),
                "Atlas dossier must be searchable directly");

        var justice = SiegeOperationsIndex.search("third justice", false, 5);
        check(justice.stream().anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.ARMORY && e.id().equals("third-justice")),
                "Third Justice must resolve through Armory search");

        var core = SiegeOperationsIndex.search("Núcleo", true, 5);
        check(core.stream().anyMatch(e -> e.route() == SiegeOperationsIndex.Route.ARCHIVE),
                "Accent-insensitive Archive search failed");

        var trauma = SiegeOperationsIndex.search("mutilated", false, 5);
        check(trauma.stream().anyMatch(e -> e.route() == SiegeOperationsIndex.Route.FIELD_MANUAL),
                "Field Manual route must surface death/injury state queries");

        var server = SiegeOperationsIndex.search("server ping", false, 5);
        check(server.stream().anyMatch(e -> e.route() == SiegeOperationsIndex.Route.DEPLOYMENT),
                "Deployment route must surface server/ping queries");

        check(SiegeOperationsIndex.search("a", false, 2).size() <= 2, "Search limit contract");
        check(SiegeOperationsIndex.search("", false, 5).isEmpty(), "Blank search must stay empty");

        // Session history is deduplicated and most-recent-first.
        Method clear = SiegeRouteHistory.class.getDeclaredMethod("clearForTests");
        clear.setAccessible(true);
        clear.invoke(null);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.INTEL);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.DEPLOYMENT);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.INTEL);
        List<SiegeOperationsIndex.Route> recent = SiegeRouteHistory.snapshot();
        check(recent.size() == 2, "Recent route history must deduplicate");
        check(recent.get(0) == SiegeOperationsIndex.Route.INTEL
                        && recent.get(1) == SiegeOperationsIndex.Route.DEPLOYMENT,
                "Recent route ordering");

        System.out.println("SIEGE 2.50 Operations index: routes, Intel, Armory, ranking and history passed");
    }
}
