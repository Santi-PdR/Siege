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
        check(IntelCatalog.previewable().stream().noneMatch(e -> e.code().equals("TNK-003")), "Agreement must not leak into main-menu preview pool");
        check(IntelCatalog.filtered("UNIT") == IntelCatalog.filtered("UNIT"), "Catalog allocation regression");
        check(IntelCatalog.filtered("ELITE").size() == 3, "Elite catalog must contain exactly three records");
        check(IntelCatalog.filtered("ELITE").stream().map(IntelEntry::code).distinct().count() == 3, "Duplicate Elite codes");
        check(IntelCatalog.filtered("ELITE").stream().map(IntelEntry::name).toList().equals(
                java.util.List.of("AGARES", "GHOST", "AURELIONIS")), "Elite order changed");

        IntelEntry aurelionis = IntelCatalog.filtered("ELITE").get(2);
        check(aurelionis.hp().equals("1") && aurelionis.threat() == 0, "Aurelionis unknown data was inferred");
        check(aurelionis.text(true).description().equals("???") && aurelionis.text(false).advisory().equals("???"),
                "Aurelionis lore must remain unknown");

        check(IntelCatalog.filtered("SUPER-UNIT").size() == 1, "Atlas must be the only Super Unit");
        IntelEntry atlas = IntelCatalog.filtered("SUPER-UNIT").get(0);
        check(atlas.code().equals("SUP-001") && atlas.name().equals("ATLAS"), "Atlas identity changed");
        check(atlas.hp().equals("125,000,000") && IntelPresentation.hpValue(atlas.hp()).longValueExact() == 125_000_000L,
                "Atlas HP changed");
        check(IntelPresentation.compactHp(atlas.hp()).equals("125M"), "Atlas compact HP");
        check(atlas.text(true).armament().equals("Información no recuperada")
                && atlas.text(false).origin().equals("No confirmed record"), "Unconfirmed Atlas capabilities were invented");
        check(IntelPresentation.completeness(atlas, atlas.text(true)) == 50, "Atlas data completeness");
        check(IntelPresentation.coverageGrade(atlas, atlas.text(true)).equals("C"), "Atlas coverage grade");
        check(IntelPresentation.completeness(aurelionis, aurelionis.text(true)) == 17, "Aurelionis data completeness");
        check(IntelPresentation.coverageGrade(aurelionis, aurelionis.text(true)).equals("D"), "Aurelionis coverage grade");

        check(java.util.Set.of("UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT").stream()
                .allMatch(category -> IntelCatalog.count(category) > 0), "Every visible category must contain a dossier");
        check(IntelCatalog.byCode("SUP-001") == atlas, "Code lookup must preserve Atlas identity");

        // 0.30 source policy: the Agreement dossier is official-only. Field reports
        // remain available separately but can never drive dossier content or styling.
        IntelEntry agreement = IntelCatalog.byCode("TNK-003");
        check(agreement.hp().equals("3,000") && agreement.defense().equals("100") && agreement.threat() == 0,
                "Agreement official stats changed");
        check(agreement.text(true).origin().equals("Corporación Secure Contain Protect")
                && agreement.text(false).origin().equals("Secure Contain Protect Corporation"), "Agreement affiliation changed");
        check(agreement.text(true).status().contains("OFICIAL") && agreement.text(false).status().contains("OFFICIAL"),
                "Agreement must be presented as an official partial dossier");
        check(IntelPresentation.completeness(agreement, agreement.text(true)) == 50, "Agreement official completeness");
        check(IntelPresentation.coverageGrade(agreement, agreement.text(true)).equals("C"), "Agreement coverage grade");
        check(!AgreementReport.applies(agreement), "Field report must never apply to an official dossier");
        for (boolean es : new boolean[]{true, false}) {
            var text = agreement.text(es);
            String dossier = (text.origin() + " " + text.armament() + " " + text.variants() + " " + text.status() + " "
                    + text.description() + " " + text.advisory()).toUpperCase(java.util.Locale.ROOT);
            for (String forbidden : new String[]{"GATE", "RIFT", "RICK SANCHEZ", "VISOR", "SABOTAJE", "SABOTAGE", "TESTIMONIO", "TESTIMONY"})
                check(!dossier.contains(forbidden), "Agreement dossier leaked field-report term " + forbidden);
        }
        check(AgreementReport.ES.contains("GATES") && AgreementReport.ES.contains("RIFTS") && AgreementReport.ES.contains("Rick Sanchez"),
                "Spanish field report lost its original claims");
        check(AgreementReport.EN.contains("GATES") && AgreementReport.EN.contains("RIFTS") && AgreementReport.EN.contains("Rick Sanchez"),
                "English field report lost its original claims");
        check(AgreementReport.ADVICE_ES.contains("SIN VERIFICAR") && AgreementReport.ADVICE_EN.contains("UNVERIFIED"),
                "Field advice lost provenance");

        check(IntelCatalog.total() == IntelCatalog.files().size(), "Catalog total changed");
        try {
            IntelCatalog.filtered("SUPER-UNIT").clear();
            throw new AssertionError("Category groups must be immutable");
        } catch (UnsupportedOperationException expected) { }

        for (IntelEntry e : IntelCatalog.files()) {
            Path image = Path.of("src/main/resources/assets/siege/textures/gui/intel/" + e.image() + ".png");
            check(Files.isRegularFile(image), "Missing image " + image);
            check(!e.text(true).advisory().isBlank() && !e.text(false).advisory().isBlank(), "Missing advisory " + e.code());
            check(IntelPresentation.hpValue(e.hp()).signum() >= 0, "Invalid HP value " + e.code());
            check(IntelPresentation.coverageGrade(e, e.text(true)).matches("[A-E]"), "Invalid coverage grade " + e.code());
        }
        System.out.println("Audio recovery, official Intel source policy, catalog identity and resource validation passed");
    }
}
