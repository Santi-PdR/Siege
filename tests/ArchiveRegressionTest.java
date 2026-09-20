import uy.santipdr.siege.client.SiegeArchiveData;

public final class ArchiveRegressionTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        var all = SiegeArchiveData.all();
        check(all.size() == 8, "0.40.2 field manual record count changed");

        int previous = Integer.MAX_VALUE;
        java.util.HashSet<String> ids = new java.util.HashSet<>();
        for (var entry : all) {
            check(ids.add(entry.id()), "duplicate field-manual id " + entry.id());
            check(entry.rank() < previous, "field manual priority order changed at " + entry.id());
            previous = entry.rank();
            check(!entry.title(true).isBlank() && !entry.title(false).isBlank(), "missing bilingual title " + entry.id());
            check(!entry.body(true).isBlank() && !entry.body(false).isBlank(), "missing bilingual body " + entry.id());
            String es = (entry.title(true) + " " + entry.body(true)).toLowerCase(java.util.Locale.ROOT);
            check(!es.contains("fue añadido") && !es.contains("fueron añadidos") && !es.contains("re-añad"),
                    "changelog language returned in current Intel: " + entry.id());
        }

        var missions = SiegeArchiveData.entries(SiegeArchiveData.Category.MISSIONS, "", true);
        check(missions.size() == 2, "mission count changed");
        check(missions.stream().anyMatch(e -> e.id().equals("operation-exodus") && e.body(true).contains("23.400.000")
                && e.body(true).contains("SAURON") && e.body(true).contains("HEDALUS")), "Operation Exodus record incomplete");
        check(missions.stream().anyMatch(e -> e.id().equals("endless-inferno") && e.body(true).contains("Great Pyramid of Giza")
                && e.body(true).contains("Bramblewick")), "Endless Inferno maps missing");

        var conditions = SiegeArchiveData.entries(SiegeArchiveData.Category.CONDITIONS, "", true);
        check(conditions.size() == 4, "condition count changed");
        var trauma = conditions.stream().filter(e -> e.id().equals("casualty-severity")).findFirst().orElseThrow();
        String traumaEs = trauma.body(true);
        check(traumaEs.contains("Downed") && traumaEs.contains("Mangled") && traumaEs.contains("Mutilated")
                && traumaEs.contains("Dismembered") && traumaEs.contains("Disfigured"), "trauma sequence incomplete");
        check(!traumaEs.toLowerCase(java.util.Locale.ROOT).contains("2 cargas")
                && !traumaEs.toLowerCase(java.util.Locale.ROOT).contains("dos cargas"), "invented Mutilated charge rule returned");
        check(traumaEs.contains("Estado transitorio") && traumaEs.contains("no admite corrección definitiva"),
                "Disfigured temporary-state rule missing");
        check(traumaEs.contains("pérdida o separación") && traumaEs.contains("hemorragia"),
                "Dismembered realistic response missing");

        var bleeding = conditions.stream().filter(e -> e.id().equals("bleeding")).findFirst().orElseThrow();
        String bleedingEs = bleeding.body(true).toLowerCase(java.util.Locale.ROOT);
        check(bleedingEs.contains("presión directa") && bleedingEs.contains("gasa") && bleedingEs.contains("torniquete")
                && bleedingEs.contains("sutur"), "Bleeding field response incomplete");

        var burned = conditions.stream().filter(e -> e.id().equals("burned")).findFirst().orElseThrow();
        String burnedEs = burned.body(true).toLowerCase(java.util.Locale.ROOT);
        check(burnedEs.contains("agua limpia y fresca") && burnedEs.contains("no aplicar hielo"),
                "Burned cooling protocol incomplete");

        var erased = conditions.stream().filter(e -> e.id().equals("erased")).findFirst().orElseThrow();
        check(erased.body(true).contains("integridad corporal") && erased.body(true).contains("reanimación convencional"),
                "Erased definition incomplete");

        var protocols = SiegeArchiveData.entries(SiegeArchiveData.Category.PROTOCOLS, "", true);
        check(protocols.size() == 2, "protocol count changed");
        check(protocols.stream().anyMatch(e -> e.id().equals("shellshock") && e.body(true).contains("150%")),
                "SHELLSHOCK 150% missing");
        check(protocols.stream().anyMatch(e -> e.id().equals("casualty-priority")
                && e.body(true).contains("vía aérea") && e.body(true).contains("desfibrilador")),
                "casualty priority protocol incomplete");

        check(SiegeArchiveData.entries(SiegeArchiveData.Category.CONDITIONS, "mutilated", false).stream()
                .anyMatch(e -> e.id().equals("casualty-severity")), "English condition search failed");
        check(SiegeArchiveData.entries(SiegeArchiveData.Category.CONDITIONS, "agua", true).stream()
                .anyMatch(e -> e.id().equals("burned")), "Spanish Burned search failed");
        check(SiegeArchiveData.entries(SiegeArchiveData.Category.MISSIONS, "HEDALUS", true).stream()
                .anyMatch(e -> e.id().equals("operation-exodus")), "mission search failed");

        System.out.println("0.40.2 Intel field manual, realistic casualty states, missions and no-changelog policy passed");
    }
}
