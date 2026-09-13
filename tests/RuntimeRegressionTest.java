package uy.santipdr.siege.client;

import java.nio.file.Files;
import java.nio.file.Path;

public class RuntimeRegressionTest {
    private static void check(boolean value, String reason) { if (!value) throw new AssertionError(reason); }
    public static void main(String[] args) {
        SiegePreviewClock clock = new SiegePreviewClock();
        check(clock.update(3, 0, false, true, true) == 0, "Initial dossier");
        check(clock.update(3, 100, true, true, true) == 0, "Hover pause");
        check(clock.update(3, 30000, true, true, true) == 0, "Long reading pause");
        check(clock.update(3, 31000, false, true, true) == 0 && clock.deadline == 33000, "Fixed exit deadline");
        check(clock.update(3, 32999, false, true, true) == 0, "Wait two seconds");
        check(clock.update(3, 33000, false, true, true) == 1, "Resume after two seconds");
        check(clock.update(3, 41500, false, true, true) == 2, "Resume normal cycle");
        clock.step(3, 42000, 1);
        check(clock.update(3, 56999, false, true, true) == 0, "Manual circular selection and hold");
        check(clock.update(3, 57000, false, true, true) == 1, "Manual hold expires");
        check(clock.update(3, 90000, false, true, false) == 1, "Disabled rotation preserved");
        SiegeAudioHealth h = new SiegeAudioHealth();
        h.begin(0);
        check(!h.recover(4999, false), "Initial grace");
        check(!h.recover(5000, false), "Start debounce");
        check(!h.recover(7999, false), "Transient backend loss");
        check(h.recover(8000, false), "Recover sustained loss");
        check(!h.recover(9000, true), "Successful activation");
        check(!h.recover(14000, false), "Second debounce");
        check(h.recover(17000, false), "Second recovery");
        check(!h.recover(50000, false) && h.retries() == 2, "Bounded retries");
        h.begin(60000);
        check(!h.recover(65000, false) && !h.recover(65500, true) && !h.recover(68000, false), "Activity resets debounce");
        check(!IntelCatalog.files().isEmpty(), "Catalog missing");
        check(IntelCatalog.previewable().stream().allMatch(e -> e.category().equals("UNIT") || e.category().equals("ADVANCED")), "Preview categories");
        check(IntelCatalog.filtered("UNIT") == IntelCatalog.filtered("UNIT"), "Catalog allocation regression");
        check(IntelCatalog.filtered("ELITE").size() == 3, "Elite catalog must contain exactly three records");
        check(IntelCatalog.filtered("ELITE").stream().map(IntelEntry::code).distinct().count() == 3, "Duplicate Elite codes");
        check(IntelCatalog.filtered("ELITE").stream().map(IntelEntry::name).toList().equals(
                java.util.List.of("AGARES", "GHOST", "AURELIONIS")), "Elite order changed");
        IntelEntry aurelionis = IntelCatalog.filtered("ELITE").get(2);
        check(aurelionis.hp().equals("1") && aurelionis.threat() == 0, "Aurelionis unknown data was inferred");
        check(aurelionis.text(true).description().equals("???") && aurelionis.text(false).advisory().equals("???"),
                "Aurelionis lore must remain unknown");
        for (IntelEntry e : IntelCatalog.files()) {
            Path image = Path.of("src/main/resources/assets/siege/textures/gui/intel/" + e.image() + ".png");
            check(Files.isRegularFile(image), "Missing image " + image);
            check(!e.text(true).advisory().isBlank() && !e.text(false).advisory().isBlank(), "Missing advisory " + e.code());
        }
        System.out.println("Audio recovery, catalog identity and resource validation passed");
    }
}
