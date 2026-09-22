import uy.santipdr.siege.client.SiegeCommandNetwork;
import uy.santipdr.siege.client.SiegeOperationsIndex;
import uy.santipdr.siege.client.SiegeRouteHistory;

import java.lang.reflect.Method;
import java.util.List;

public final class OperationsIndexTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) throws Exception {
        check(SiegeCommandNetwork.hasNoDuplicateVisibleRoutes(), "Visible Operations routes must not duplicate");
        check(!SiegeCommandNetwork.isVisible(SiegeOperationsIndex.Route.ARCHIVE), "Archive belongs only on main screen");
        check(!SiegeCommandNetwork.isVisible(SiegeOperationsIndex.Route.ARMORY), "Armory belongs only on main screen");
        check(!SiegeCommandNetwork.isVisible(SiegeOperationsIndex.Route.DIAGNOSTICS), "Diagnostics must not be visible in Operations");
        check(!SiegeCommandNetwork.isVisible(SiegeOperationsIndex.Route.COMMAND), "Command Center must not duplicate Settings");
        check(!SiegeCommandNetwork.isVisible(SiegeOperationsIndex.Route.SETTINGS), "Settings must not duplicate main/settings flow");
        check(!SiegeCommandNetwork.isVisible(SiegeOperationsIndex.Route.BACKGROUNDS), "Backgrounds must not duplicate Settings");

        var atlasDossier = SiegeOperationsIndex.search("atlas", false, 8);
        check(atlasDossier.stream().anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.INTEL && e.id().equals("SUP-001")),
                "ATLAS dossier must remain searchable directly");
        check(SiegeOperationsIndex.search("atlas races systems", false, 8).stream()
                        .anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.ROUTE && e.route() == SiegeOperationsIndex.Route.ATLAS),
                "Atlas route missing");
        check(SiegeOperationsIndex.search("first steps survival", false, 8).stream()
                        .anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.ROUTE && e.route() == SiegeOperationsIndex.Route.BRIEFING),
                "Briefing route missing");
        check(SiegeOperationsIndex.search("units executors bosses", false, 8).stream()
                        .anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.ROUTE && e.route() == SiegeOperationsIndex.Route.THREATS),
                "Threat route missing");
        check(SiegeOperationsIndex.search("Obsainan race", false, 12).stream()
                        .anyMatch(e -> e.route() == SiegeOperationsIndex.Route.RACES), "Race Atlas route missing");
        check(SiegeOperationsIndex.search("V1 V4 progression", false, 12).stream()
                        .anyMatch(e -> e.route() == SiegeOperationsIndex.Route.PROGRESSION), "Progression route missing");
        check(SiegeOperationsIndex.search("soundtrack dummies noobs", false, 12).stream()
                        .anyMatch(e -> e.route() == SiegeOperationsIndex.Route.MEDIA), "Media route missing");

        // Hidden main-screen tools must not leak back through Operations search.
        check(SiegeOperationsIndex.search("archive", false, 12).stream().noneMatch(e -> e.route() == SiegeOperationsIndex.Route.ARCHIVE),
                "Archive leaked into Operations search");
        check(SiegeOperationsIndex.search("armory", false, 12).stream().noneMatch(e -> e.route() == SiegeOperationsIndex.Route.ARMORY),
                "Armory leaked into Operations search");
        check(SiegeOperationsIndex.search("diagnostics", false, 12).stream().noneMatch(e -> e.route() == SiegeOperationsIndex.Route.DIAGNOSTICS),
                "Diagnostics leaked into Operations search");

        var geography = SiegeOperationsIndex.search("Geography Table", false, 8);
        check(geography.stream().anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.KNOWLEDGE
                        && e.knowledgeId().equals("item-geography-table")), "Geography Table deep-link failed");
        var defib = SiegeOperationsIndex.search("defibrillator revive", false, 12);
        check(defib.stream().anyMatch(e -> e.knowledgeId().equals("item-defibrillator")), "Current defibrillator knowledge missing");
        var rarity = SiegeOperationsIndex.search("Obsainan Fabled", true, 12);
        check(rarity.stream().anyMatch(e -> e.knowledgeId().equals("rarity-order")), "Race rarity search failed");
        var trials = SiegeOperationsIndex.search("Witch Trial", false, 12);
        check(trials.stream().anyMatch(e -> e.kind() == SiegeOperationsIndex.Kind.KNOWLEDGE), "Trial knowledge search failed");
        check(SiegeOperationsIndex.search("a", false, 2).size() <= 2, "Search limit contract");
        check(SiegeOperationsIndex.search("", false, 5).isEmpty(), "Blank search must stay empty");

        Method clear = SiegeRouteHistory.class.getDeclaredMethod("clearForTests");
        clear.setAccessible(true); clear.invoke(null);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.RACES);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.MEDIA);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.DEPLOYMENT);
        SiegeRouteHistory.record(SiegeOperationsIndex.Route.RACES);
        List<SiegeOperationsIndex.Route> recent = SiegeRouteHistory.snapshot();
        check(recent.size() == 3, "Recent route history must deduplicate");
        check(recent.get(0) == SiegeOperationsIndex.Route.RACES
                        && recent.get(1) == SiegeOperationsIndex.Route.DEPLOYMENT
                        && recent.get(2) == SiegeOperationsIndex.Route.MEDIA, "Recent route ordering");
        System.out.println("SIEGE 5.00 focused Operations search and hidden-main-screen route contracts passed");
    }
}
