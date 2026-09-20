import uy.santipdr.siege.client.SiegeArchiveData;

public final class ArchiveRegressionTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        var all = SiegeArchiveData.all();
        check(all.size() >= 15, "0.40 archive is unexpectedly small");
        check(all.get(0).id().equals("current-core"), "Newest announcement must remain first");
        check(all.get(1).id().equals("current-trident"), "Trident must follow the newest core notice");
        check(all.get(2).id().equals("current-fusilier"), "Fusilier current notice order changed");

        int previous = Integer.MAX_VALUE;
        java.util.HashSet<String> ids = new java.util.HashSet<>();
        for (var entry : all) {
            check(ids.add(entry.id()), "duplicate archive id " + entry.id());
            check(entry.rank() < previous, "archive no longer newest-to-oldest at " + entry.id());
            previous = entry.rank();
            check(!entry.title(true).isBlank() && !entry.title(false).isBlank(), "missing bilingual title " + entry.id());
            check(!entry.body(true).isBlank() && !entry.body(false).isBlank(), "missing bilingual body " + entry.id());
        }

        var current = SiegeArchiveData.entries(SiegeArchiveData.Category.CURRENT, "", true);
        check(current.size() == 5, "current notice count changed");
        check(current.get(0).body(true).contains("SHELLSHOCK") && current.get(0).body(true).contains("150%"),
                "SHELLSHOCK 150% missing from newest notice");
        check(current.stream().anyMatch(e -> e.id().equals("current-fauna") && e.body(true).contains("RPK-74")
                && e.body(true).contains("tres clones")), "Fauna current record incomplete");
        check(current.stream().anyMatch(e -> e.id().equals("current-trident") && e.body(true).contains("40%")
                && e.body(true).contains("10%") && e.body(true).contains("cortar cuerda")), "Trident current record incomplete");
        check(current.stream().anyMatch(e -> e.id().equals("current-fusilier") && e.body(true).contains("seis segundos")
                && e.body(true).contains("Modo Mortero") && e.body(true).contains("BLOX DRINK")), "Fusilier current record incomplete");

        var missions = SiegeArchiveData.entries(SiegeArchiveData.Category.MISSIONS, "", true);
        check(missions.stream().anyMatch(e -> e.id().equals("operation-exodus") && e.body(true).contains("23.400.000")
                && e.body(true).contains("SAURON") && e.body(true).contains("HEDALUS")), "Operation Exodus record incomplete");
        check(missions.stream().anyMatch(e -> e.id().equals("endless-inferno") && e.body(true).contains("Great Pyramid of Giza")
                && e.body(true).contains("Bramblewick")), "Endless Inferno maps missing");

        var casualty = SiegeArchiveData.entries(SiegeArchiveData.Category.CASUALTY, "", true);
        var states = casualty.stream().filter(e -> e.id().equals("death-states")).findFirst().orElseThrow();
        check(states.body(true).contains("DOWNED") && states.body(true).contains("MANGLED") && states.body(true).contains("MUTILATED"),
                "verified DVN states missing");
        check(states.body(true).contains("1,5 s") && states.body(true).contains("4 s"), "DVN revive timing missing");
        check(states.body(true).contains("no puede revivir") && !states.body(true).contains("MUTILATED · 2"),
                "invented two-charge Mutilated rule returned");
        var labels = casualty.stream().filter(e -> e.id().equals("death-labels")).findFirst().orElseThrow();
        check(labels.body(true).contains("Burnt") && labels.body(true).contains("Disfigured") && labels.body(true).contains("Erased"),
                "additional casualty labels missing");
        check(labels.body(true).contains("NO SON LOS TRES ESTADOS PRINCIPALES"), "extra labels were promoted to DVN revive states");

        check(SiegeArchiveData.entries(SiegeArchiveData.Category.CURRENT, "visor 40%", true).stream()
                .anyMatch(e -> e.id().equals("current-trident")), "archive search failed for Trident");
        check(SiegeArchiveData.entries(SiegeArchiveData.Category.CASUALTY, "mutilated", false).stream()
                .anyMatch(e -> e.id().equals("death-states")), "English casualty search failed");

        System.out.println("0.40 archive chronology, current SIEGE notices, missions and DVN casualty reference passed");
    }
}
