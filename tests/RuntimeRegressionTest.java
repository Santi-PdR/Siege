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

        var unknown = IntelCatalog.filtered("UNKNOWN");
        check(unknown.size() == 6, "Unknown Intel category must contain the six unclassified new troops");
        check(unknown.stream().map(IntelEntry::name).toList().equals(java.util.List.of(
                "ENGINEER", "INFORMANT", "GRAPPLER", "TRANQUILIZER", "SKYDIVER", "SKYLINER")),
                "Unknown troop order or membership changed");
        for (IntelEntry entry : unknown) {
            check(entry.image().equals("placeholder/classified"), "Unknown troop must use valid dossier placeholder: " + entry.name());
            check(entry.text(true).status().contains("SIN REGISTRO VISUAL"), "Spanish missing-visual marker absent: " + entry.name());
            check(entry.text(false).status().contains("NO VISUAL RECORD"), "English missing-visual marker absent: " + entry.name());
        }
        check(IntelPresentation.categoryCode("UNKNOWN").equals("UNK"), "Unknown short category code changed");

        var elites = IntelCatalog.filtered("ELITE");
        check(elites.size() == 6, "Current Elite catalog must include illustrated and partial current dossiers");
        check(elites.stream().map(IntelEntry::code).distinct().count() == elites.size(), "Duplicate Elite codes");
        check(elites.subList(0, 3).stream().map(IntelEntry::name).toList().equals(
                java.util.List.of("AGARES", "GHOST", "AURELIONIS")), "Existing illustrated Elite order changed");
        check(elites.stream().map(IntelEntry::name).toList().containsAll(java.util.List.of("FAUNA", "CERBERUS", "PROTEUS")),
                "Current Elite dossiers missing");
        for (String code : new String[]{"ELT-004", "ELT-005", "ELT-006"})
            check(IntelCatalog.byCode(code).image().equals("placeholder/classified"), "Elite placeholder repair missing: " + code);

        IntelEntry aurelionis = IntelCatalog.byCode("ELT-003");
        check(aurelionis != null && aurelionis.hp().equals("1") && aurelionis.threat() == 0,
                "Aurelionis unknown data was inferred");
        check(aurelionis.text(true).description().equals("???") && aurelionis.text(false).advisory().equals("???"),
                "Aurelionis lore must remain unknown");

        IntelEntry fauna = IntelCatalog.byCode("ELT-004");
        check(fauna != null && fauna.hp().equals("N/D") && fauna.defense().equals("N/D"),
                "Fauna received invented numeric stats");
        check(fauna.text(true).armament().contains("RPK-74") && fauna.text(true).description().contains("tres señuelos"),
                "Fauna current equipment incomplete");

        var superUnits = IntelCatalog.filtered("SUPER-UNIT");
        check(superUnits.size() == 3, "Operation Exodus Super Units missing from Intel");
        IntelEntry atlas = IntelCatalog.byCode("SUP-001");
        check(atlas != null && atlas.name().equals("ATLAS"), "Atlas identity changed");
        check(atlas.hp().equals("125,000,000") && IntelPresentation.hpValue(atlas.hp()).longValueExact() == 125_000_000L,
                "Atlas HP changed");
        check(IntelPresentation.compactHp(atlas.hp()).equals("125M"), "Atlas compact HP");
        check(atlas.text(true).armament().equals("Información no recuperada")
                && atlas.text(false).origin().equals("No confirmed record"), "Unconfirmed Atlas capabilities were invented");
        check(IntelPresentation.completeness(atlas, atlas.text(true)) == 50, "Atlas data completeness");
        check(IntelPresentation.coverageGrade(atlas, atlas.text(true)).equals("C"), "Atlas coverage grade");
        check(IntelPresentation.completeness(aurelionis, aurelionis.text(true)) == 17, "Aurelionis data completeness");
        check(IntelPresentation.coverageGrade(aurelionis, aurelionis.text(true)).equals("D"), "Aurelionis coverage grade");
        for (String code : new String[]{"SUP-002", "SUP-003"}) {
            IntelEntry exodus = IntelCatalog.byCode(code);
            check(exodus != null && exodus.hp().equals("23,400,000") && exodus.defense().equals("N/D"),
                    "Exodus Super Unit stats changed or invented: " + code);
            check(exodus.text(true).variants().equals("Operation Exodus"), "Exodus classification missing: " + code);
            check(exodus.image().equals("placeholder/classified"), "Exodus missing-image repair absent: " + code);
        }

        check(java.util.Set.of("UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT", "UNKNOWN").stream()
                .allMatch(category -> IntelCatalog.count(category) > 0), "Every visible category must contain a dossier");
        check(IntelCatalog.byCode("SUP-001") == atlas, "Code lookup must preserve Atlas identity");
        check(IntelCatalog.byCode("TNK-006").category().equals("TANK")
                && IntelCatalog.byCode("TNK-006").image().equals("placeholder/classified"), "Agitator classification/visual repair changed");
        check(IntelCatalog.byCode("BOS-010").category().equals("BOSS")
                && IntelCatalog.byCode("BOS-010").image().equals("bosses/classified/frame_00"), "Sparta Boss dossier placeholder changed");

        IntelEntry agreement = IntelCatalog.byCode("TNK-003");
        check(agreement.hp().equals("3,000") && agreement.defense().equals("100") && agreement.threat() == 0,
                "Agreement official stats changed");
        check(agreement.text(true).origin().equals("Corporación Secure Contain Protect")
                && agreement.text(false).origin().equals("Secure Contain Protect Corporation"), "Agreement affiliation changed");
        check(agreement.text(true).status().contains("OFICIAL") && agreement.text(false).status().contains("OFFICIAL"),
                "Agreement must be presented as an official partial dossier");
        check(IntelPresentation.completeness(agreement, agreement.text(true)) == 67, "Agreement official completeness");
        check(IntelPresentation.coverageGrade(agreement, agreement.text(true)).equals("B"), "Agreement coverage grade");
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

        IntelEntry trident = IntelCatalog.byCode("BOS-004");
        check(trident.hp().equals("38,000"), "Trident HP changed without a newer numeric announcement");
        check(trident.text(true).variants().equals("VISOR PUESTO / VISOR REMOVIDO"), "Trident visor states missing");
        check(trident.text(true).description().contains("40%") && trident.text(true).description().contains("10%"),
                "Trident visor resistances missing");
        check(trident.text(true).description().contains("cortar la cuerda") && trident.text(true).description().contains("torso"),
                "Trident hook rescue rules missing");
        check(trident.text(false).description().contains("headshots") && trident.text(false).description().contains("flashbangs"),
                "Trident visor-off vulnerabilities missing");

        IntelEntry fusilier = IntelCatalog.byCode("BOS-002");
        check(fusilier.hp().equals("8,000"), "Fusilier HP changed without a newer numeric announcement");
        check(fusilier.text(true).variants().contains("Modo Mortero") && fusilier.text(true).variants().contains("Blox Drink"),
                "Fusilier current modes missing");
        check(fusilier.text(true).description().contains("seis segundos") && fusilier.text(true).description().contains("barra de jefe se vuelve blanca"),
                "Fusilier nuclear/mortar cues missing");
        check(fusilier.text(false).advisory().contains("correctly timed explosive"), "Fusilier drink interrupt missing");

        check(IntelCatalog.byCode("SOP-002") != null, "Stalker dossier missing");
        check(IntelCatalog.byCode("HU-008") != null && IntelCatalog.byCode("HU-008").text(true).armament().contains("Sound Erradicator"),
                "Engineer current dossier missing");
        check(IntelCatalog.byCode("HU-008").category().equals("UNKNOWN"), "Engineer must remain unclassified until confirmed");
        check(IntelCatalog.byCode("ADV-001").text(true).variants().contains("Stop Time"), "Specialist Stop Time missing");
        check(IntelCatalog.byCode("ADV-004").text(true).description().contains("parry"), "Cloaker parry capability missing");

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
        System.out.println("Audio recovery, 0.70 unknown dossiers, official source policy and Intel resources passed");
    }
}
