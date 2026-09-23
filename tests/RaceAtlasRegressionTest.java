import uy.santipdr.siege.client.SiegeRaceAtlasData;

import java.util.HashSet;
import java.util.Set;

public final class RaceAtlasRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        check(SiegeRaceAtlasData.all().size() >= 50, "Race atlas unexpectedly small after full-corpus pass");
        Set<String> ids = new HashSet<>();
        int unresolved = 0;
        for (var race : SiegeRaceAtlasData.all()) {
            check(ids.add(race.id()), "Duplicate race id: " + race.id());
            check(!race.name().isBlank(), "Race missing name: " + race.id());
            String haystack = (race.summaryEs() + " " + race.summaryEn()).toLowerCase();
            for (String forbidden : new String[] {"santi", "inventario personal", "mi partida", "my run", "my inventory"})
                check(!haystack.contains(forbidden), "Personal data leaked into race atlas: " + race.id());

            if (race.knowledgeId().isBlank()) {
                unresolved++;
                check(race.progression() == SiegeRaceAtlasData.Progression.UNKNOWN,
                        "Race without a deep-link must not claim a detailed progression: " + race.id());
                check(haystack.contains("no hay información") || haystack.contains("not enough recent information"),
                        "Race without a deep-link must explain that current information is incomplete: " + race.id());
            }
        }
        check(unresolved >= 20, "Expected unresolved corpus races to remain explicitly unknown rather than invented");

        var order = SiegeRaceAtlasData.rarityOrder();
        check(order.size() == 10, "Rarity ladder must have ten documented ranks");
        check(order.get(0) == SiegeRaceAtlasData.Rarity.COMMON, "Rarity order must start at Common");
        check(order.get(5) == SiegeRaceAtlasData.Rarity.OBSAINAN, "Obsainan position regression");
        check(order.get(8) == SiegeRaceAtlasData.Rarity.ETERNAL, "Eternal position regression");
        check(order.get(9) == SiegeRaceAtlasData.Rarity.FABLED, "Fabled must remain highest documented rank");

        check(SiegeRaceAtlasData.search("Obsainan", 30).stream().anyMatch(r -> r.id().equals("saiyan")),
                "Obsainan search should surface Saiyan");
        check(SiegeRaceAtlasData.search("deteriorer", 5).stream().anyMatch(r -> r.id().equals("deteriorer")),
                "Deteriorer search failed");
        check(SiegeRaceAtlasData.search("assembling", 8).stream().anyMatch(r -> r.id().equals("cyborg")),
                "Assembling search should surface Cyborg");
        check(SiegeRaceAtlasData.search("Mink", 5).stream().anyMatch(r -> r.id().equals("mink")),
                "Full-corpus race search should surface Mink");
        check(SiegeRaceAtlasData.search("Void Master", 5).stream().anyMatch(r -> r.id().equals("void-master")),
                "Full-corpus race search should surface Void Master");
        check(SiegeRaceAtlasData.search("", 5).size() == 5, "Blank search limit contract");

        System.out.println("SIEGE 5.00 race atlas: expanded catalog, explicit unknowns, rarity ladder, search and privacy passed");
    }
}
