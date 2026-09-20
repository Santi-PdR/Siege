package uy.santipdr.siege.client;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class RuntimeRegressionTest {
    private static void check(boolean value, String reason) { if (!value) throw new AssertionError(reason); }

    public static void main(String[] args) {
        previewClockContract();
        audioRecoveryContract();
        intelCatalogContract();
        sourceBackedDvnContract();
        existingIntelContract();
        resourceContract();
        System.out.println("Audio recovery, SIEGE 1.25 Intel, DVN references and decodable 16:9 image resources passed");
    }

    private static void previewClockContract() {
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
    }

    private static void audioRecoveryContract() {
        SiegeAudioHealth health = new SiegeAudioHealth();
        health.begin(0);
        check(!health.recover(4999, false), "Initial grace");
        check(!health.recover(5000, false), "Start debounce");
        check(!health.recover(7999, false), "Transient backend loss");
        check(health.recover(8000, false), "Recover sustained loss");
        check(!health.recover(9000, true), "Successful activation");
        check(!health.recover(14000, false), "Second debounce");
        check(health.recover(17000, false), "Second recovery");
        check(!health.recover(50000, false) && health.retries() == 2, "Bounded retries");
        health.begin(60000);
        check(!health.recover(65000, false) && !health.recover(65500, true) && !health.recover(68000, false), "Activity resets debounce");
    }

    private static void intelCatalogContract() {
        check(!IntelCatalog.files().isEmpty(), "Catalog missing");
        check(IntelCatalog.total() == IntelCatalog.files().size(), "Catalog total changed");
        check(IntelCatalog.previewable().stream().allMatch(e -> e.category().equals("UNIT") || e.category().equals("ADVANCED")),
                "Preview categories");
        check(IntelCatalog.filtered("UNIT") == IntelCatalog.filtered("UNIT"), "Catalog allocation regression");

        Set<String> visible = Set.of("UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT", "UNKNOWN");
        check(visible.stream().allMatch(category -> IntelCatalog.count(category) > 0), "Every visible category must contain a dossier");
        check(IntelPresentation.categoryCode("UNKNOWN").equals("UNK"), "Unknown short category code changed");

        var unknown = IntelCatalog.filtered("UNKNOWN");
        check(unknown.stream().map(IntelEntry::name).toList().equals(List.of("GRAPPLER", "SKYDIVER", "SKYLINER")),
                "Only troops without a reliable source should remain UNKNOWN");
        for (IntelEntry entry : unknown) {
            check(entry.image().equals("placeholder/classified"), "Unknown dossier must use classified placeholder: " + entry.name());
            check(entry.text(true).status().contains("SIN REGISTRO VISUAL"), "Spanish visual marker missing: " + entry.name());
            check(entry.text(false).status().contains("NO VISUAL RECORD"), "English visual marker missing: " + entry.name());
        }
    }

    private static void sourceBackedDvnContract() {
        IntelEntry engineer = IntelCatalog.byCode("ADV-007");
        check(engineer != null && engineer.category().equals("ADVANCED") && engineer.hp().equals("150"), "Engineer DVN classification/stats missing");
        check(engineer.text(true).armament().contains("Sentry") && engineer.text(true).armament().contains("Teleporter"),
                "Engineer construction role missing");

        IntelEntry informant = IntelCatalog.byCode("ADV-008");
        check(informant != null && informant.category().equals("ADVANCED") && informant.hp().equals("155"), "Informant DVN classification/stats missing");
        check(informant.text(true).armament().contains("H94"), "Informant H94 reference missing");

        IntelEntry tranquilizer = IntelCatalog.byCode("ADV-009");
        check(tranquilizer != null && tranquilizer.category().equals("ADVANCED") && tranquilizer.hp().equals("100"),
                "Tranquilizer DVN classification/stats missing");
        check(tranquilizer.text(true).armament().contains("Dart Rifle"), "Tranquilizer Dart Rifle missing");
        check(tranquilizer.text(true).description().contains("Epilogue"), "Tranquilizer Epilogue source context missing");

        IntelEntry agitator = IntelCatalog.byCode("TNK-006");
        check(agitator != null && agitator.category().equals("TANK") && agitator.hp().equals("300"), "Agitator DVN data missing");
        check(agitator.text(true).description().contains("tanque de combustible de 200 HP"), "Agitator fuel-tank data missing");

        IntelEntry sparta = IntelCatalog.byCode("BOS-010");
        check(sparta != null && sparta.category().equals("BOSS") && sparta.hp().equals("350"), "Sparta Boss data missing");
        check(sparta.text(true).armament().contains("Khanblades") && sparta.text(true).description().contains("jetpack"),
                "Sparta current DVN equipment/behaviour missing");
        check(sparta.image().equals("bosses/classified/frame_00"), "Sparta must retain the valid animated Boss placeholder");

        IntelEntry proteus = IntelCatalog.byCode("BOS-011");
        check(proteus != null && proteus.category().equals("BOSS") && proteus.hp().equals("550"), "Proteus must be a DVN Boss dossier");
        check(proteus.text(true).armament().contains("Hivelink") && proteus.text(true).variants().contains("Spectral Leap"),
                "Proteus public development data missing");
        check(proteus.text(true).status().contains("DESARROLLO"), "Proteus development-state warning missing");
    }

    private static void existingIntelContract() {
        var elites = IntelCatalog.filtered("ELITE");
        check(elites.stream().map(IntelEntry::name).toList().containsAll(List.of("AGARES", "GHOST", "AURELIONIS", "FAUNA", "CERBERUS")),
                "Current Elite dossiers missing");
        check(elites.stream().noneMatch(e -> e.name().equals("PROTEUS")), "Proteus must no longer remain incorrectly classified as Elite");

        IntelEntry atlas = IntelCatalog.byCode("SUP-001");
        check(atlas != null && atlas.hp().equals("125,000,000") && IntelPresentation.compactHp(atlas.hp()).equals("125M"), "Atlas contract changed");
        for (String code : new String[]{"SUP-002", "SUP-003"}) {
            IntelEntry exodus = IntelCatalog.byCode(code);
            check(exodus != null && exodus.hp().equals("23,400,000") && exodus.image().equals("placeholder/classified"),
                    "Operation Exodus dossier changed: " + code);
        }

        IntelEntry agreement = IntelCatalog.byCode("TNK-003");
        check(agreement != null && agreement.hp().equals("3,000") && agreement.defense().equals("100"), "Agreement official stats changed");
        check(agreement.text(true).status().contains("OFICIAL") && !AgreementReport.applies(agreement), "Agreement source policy changed");

        IntelEntry trident = IntelCatalog.byCode("BOS-004");
        check(trident != null && trident.text(true).variants().contains("VISOR PUESTO") && trident.text(true).description().contains("40%"),
                "Trident current visor dossier missing");
        IntelEntry fusilier = IntelCatalog.byCode("BOS-002");
        check(fusilier != null && fusilier.text(true).variants().contains("Modo Mortero") && fusilier.text(true).description().contains("Blox Drink"),
                "Fusilier current dossier missing");
    }

    private static void resourceContract() {
        for (IntelEntry entry : IntelCatalog.files()) {
            Path image = Path.of("src/main/resources/assets/siege/textures/gui/intel/" + entry.image() + ".png");
            decodeImage(image);
            if (entry.category().equals("BOSS") && entry.image().endsWith("frame_00")) {
                String framePrefix = entry.image().substring(0, entry.image().length() - 2);
                int expectedWidth = -1, expectedHeight = -1;
                for (int frame = 0; frame < 6; frame++) {
                    int[] size = decodeImage(Path.of("src/main/resources/assets/siege/textures/gui/intel/" + framePrefix + String.format("%02d", frame) + ".png"));
                    if (frame == 0) { expectedWidth = size[0]; expectedHeight = size[1]; }
                    else check(size[0] == expectedWidth && size[1] == expectedHeight,
                            "Boss animation frame dimensions changed inside one dossier: " + entry.code());
                }
            }
            check(!entry.text(true).advisory().isBlank() && !entry.text(false).advisory().isBlank(), "Missing advisory " + entry.code());
            check(IntelPresentation.hpValue(entry.hp()).signum() >= 0, "Invalid HP value " + entry.code());
            check(IntelPresentation.coverageGrade(entry, entry.text(true)).matches("[A-E]"), "Invalid coverage grade " + entry.code());
        }
        try {
            IntelCatalog.filtered("SUPER-UNIT").clear();
            throw new AssertionError("Category groups must be immutable");
        } catch (UnsupportedOperationException expected) { }
    }

    private static int[] decodeImage(Path image) {
        check(Files.isRegularFile(image), "Missing image " + image);
        try {
            var decoded = ImageIO.read(image.toFile());
            check(decoded != null, "Unreadable image " + image);
            int width = decoded.getWidth(), height = decoded.getHeight();
            check(width >= 320 && height >= 180, "Image below Intel minimum resolution " + image + " -> " + width + "x" + height);
            check(width <= 4096 && height <= 4096, "Image exceeds safe Intel bounds " + image + " -> " + width + "x" + height);
            double ratio = width / (double)height;
            check(Math.abs(ratio - (16.0 / 9.0)) < 0.025,
                    "Intel image must remain 16:9 " + image + " -> " + width + "x" + height);
            return new int[]{width, height};
        } catch (IOException error) {
            throw new AssertionError("Corrupt image " + image + ": " + error.getMessage(), error);
        }
    }
}
