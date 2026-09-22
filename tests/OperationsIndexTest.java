import uy.santipdr.siege.client.SiegeOperationsIndex;
import uy.santipdr.siege.client.SiegeRouteHistory;

import java.lang.reflect.Method;
import java.util.List;

public final class OperationsIndexTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) throws Exception {
        var atlasDossier = SiegeOperationsIndex.search("atlas", false, 8);
        check(atlasDossier.stream().anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.INTEL && e.id().equals("SUP-001")),
                "ATLAS dossier must remain searchable directly");
        check(SiegeOperationsIndex.search("tactical atlas", false, 8).stream()
                        .anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.ROUTE && e.route() == SiegeOperationsIndex.Route.ATLAS),
                "Tactical Atlas route missing");
        check(SiegeOperationsIndex.search("new player briefing", false, 8).stream()
                        .anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.ROUTE && e.route() == SiegeOperationsIndex.Route.BRIEFING),
                "Briefing route missing");
        check(SiegeOperationsIndex.search("threat board", false, 8).stream()
                        .anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.ROUTE && e.route() == SiegeOperationsIndex.Route.THREATS),
                "Threat Board route missing");

        var justice = SiegeOperationsIndex.search("third justice", false, 8);
        check(justice.stream().anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.ARMORY && e.id().equals("third-justice")),
                "Third Justice must still resolve through Armory search");
        var core = SiegeOperationsIndex.search("Núcleo", true, 5);
        check(core.stream().anyMatch(e -> e.route() == SiegeOperationsIndex.Route.ARCHIVE), "Archive search failed");
        var trauma = SiegeOperationsIndex.search("mutilated", false, 5);
        check(trauma.stream().anyMatch(e -> e.route() == SiegeOperationsIndex.Route.FIELD_MANUAL), "Field Manual search failed");
        var server = SiegeOperationsIndex.search("server ping", false, 5);
        check(server.stream().anyMatch(e -> e.route() == SiegeOperationsIndex.Route.DEPLOYMENT), "Deployment search failed");
        var geography = SiegeOperationsIndex.search("Geography Table", false, 8);
        check(geography.stream().anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.KNOWLEDGE
                        && e.knowledgeId().equals("item-geography-table")), "Geography Table deep-link failed");
        var adaptation = SiegeOperationsIndex.search("adaptation repeated techniques", false, 12);
        check(adaptation.stream().anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.KNOWLEDGE
                        && e.knowledgeId().equals("combat-adaptation")), "4.00 expansion not indexed");
        var rarity = SiegeOperationsIndex.search("Obsainan Fabled", true, 12);
        check(rarity.stream().anyMatch(e -> e.knowledgeId().equals("rarity-order")), "Race rarity search failed");
        var trials = SiegeOperationsIndex.search("V4 trials", false, 12);
        check(trials.stream().anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.KNOWLEDGE), "Trial knowledge search failed");
        check(SiegeOperationsIndex.search("a", false, 2).size() <= 2, "Search limit contract");
        check(SiegeOperationsIndex.search("", false, 5).isEmpty(), "Blank search must stay empty");

        Method clear = SiegeRouteHistory.class.getDeclaredMethod("clearForTests");
        clear.setAccessible(true); clear.invoke(null);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.ATLAS);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.THREATS);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.DEPLOYMENT);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.ATLAS);
        List<SiegeOperationsIndex.Route> recent = SiegeRouteHistory.snapshot();
        check(recent.size() == 3, "Recent route history must deduplicate");
        check(recent.get(0) == SiegeOperationsIndex.Route.ATLAS
                        && recent.get(1) == SiegeOperationsIndex.Route.DEPLOYMENT
                        && recent.get(2) == SiegeOperationsIndex.Route.THREATS, "Recent route ordering");
        System.out.println("SIEGE 4.00 War Room index, new routes, expansion knowledge and deep-links passed");
    }
}
