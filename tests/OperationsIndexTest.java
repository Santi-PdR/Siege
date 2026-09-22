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

        var geography = SiegeOperationsIndex.search("Geography Table", false, 8);
        check(geography.stream().anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.KNOWLEDGE
                        && e.knowledgeId().equals("item-geography-table")),
                "Knowledge search must deep-link Geography Table");

        var rust = SiegeOperationsIndex.search("Rust Guard", false, 8);
        check(rust.stream().anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.KNOWLEDGE
                        && e.knowledgeId().equals("current-rust-guard")),
                "Current SIEGE notes must be searchable from Operations");

        var meditation = SiegeOperationsIndex.search("meditar 100 RE", true, 12);
        check(meditation.stream().anyMatch(e -> e.route() == SiegeOperationsIndex.Route.KNOWLEDGE),
                "Survival knowledge must surface from global search");

        check(SiegeOperationsIndex.search("a", false, 2).size() <= 2, "Search limit contract");
        check(SiegeOperationsIndex.search("", false, 5).isEmpty(), "Blank search must stay empty");

        // Session history is deduplicated and most-recent-first.
        Method clear = SiegeRouteHistory.class.getDeclaredMethod("clearForTests");
        clear.setAccessible(true);
        clear.invoke(null);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.INTEL);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.KNOWLEDGE);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.DEPLOYMENT);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.KNOWLEDGE);
        List<SiegeOperationsIndex.Route> recent = SiegeRouteHistory.snapshot();
        check(recent.size() == 3, "Recent route history must deduplicate");
        check(recent.get(0) == SiegeOperationsIndex.Route.KNOWLEDGE
                        && recent.get(1) == SiegeOperationsIndex.Route.DEPLOYMENT
                        && recent.get(2) == SiegeOperationsIndex.Route.INTEL,
                "Recent route ordering");

        System.out.println("SIEGE 3.00 Operations index: routes, Intel, Armory, Knowledge deep-links and history passed");
    }
}
