import uy.santipdr.siege.client.SiegeEasterEggVault;
import uy.santipdr.siege.client.SiegeSceneCatalog;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * SIEGE 5.30 release gate for visual media. Every GUI PNG/JPG must decode.
 * Menu scenes use their real prepared source dimensions; no CI rule may demand
 * synthetic 1920x1080 enlargement from a smaller source.
 */
public final class GuiResourceRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) throws Exception {
        Path gui = Path.of("src/main/resources/assets/siege/textures/gui");
        check(Files.isDirectory(gui), "Missing GUI texture root");

        List<Path> images = new ArrayList<>();
        try (var files = Files.walk(gui)) {
            files.filter(Files::isRegularFile)
                    .filter(GuiResourceRegressionTest::supportedImage)
                    .forEach(images::add);
        }
        check(!images.isEmpty(), "No GUI image resources found");
        for (Path image : images) {
            int[] size = decode(image);
            check(size[0] >= 32 && size[1] >= 32,
                    "Suspiciously tiny GUI image " + image + " -> " + size[0] + "x" + size[1]);
        }

        Path backgrounds = gui.resolve("backgrounds");
        check(SiegeSceneCatalog.count() >= 15, "5.30 scene catalog unexpectedly shrank");
        for (int i = 0; i < SiegeSceneCatalog.count(); i++) {
            Path file = backgrounds.resolve(SiegeSceneCatalog.id(i) + ".png");
            int[] size = decode(file);
            check(size[0] == SiegeSceneCatalog.width(i) && size[1] == SiegeSceneCatalog.height(i),
                    "Scene metadata mismatch for " + SiegeSceneCatalog.id(i) + ": catalog="
                            + SiegeSceneCatalog.width(i) + "x" + SiegeSceneCatalog.height(i)
                            + " file=" + size[0] + "x" + size[1]);
            check(size[0] >= 640 && size[1] >= 360,
                    "Menu background below approved native source floor: " + SiegeSceneCatalog.id(i)
                            + " -> " + size[0] + "x" + size[1]);
            check(size[0] * 9 == size[1] * 16,
                    "Background must remain exact 16:9: " + SiegeSceneCatalog.id(i));
            check(!SiegeEasterEggVault.reserved(SiegeSceneCatalog.id(i)),
                    "Reserved easter egg leaked into normal menu catalog: " + SiegeSceneCatalog.id(i));
        }

        for (String id : new String[]{"dvn_official_01", "dvn_official_02", "dvn_official_03"}) {
            check(SiegeSceneCatalog.width(indexOf(id)) == 768 && SiegeSceneCatalog.height(indexOf(id)) == 432,
                    "Official DVN scene should stay at native 768x432, not fake-HD: " + id);
        }
        check(SiegeSceneCatalog.width(indexOf("dummies_assault")) == 960,
                "Legacy 960px source must not be fake-upscaled to 1920");
        check(SiegeSceneCatalog.width(indexOf("night_operation")) == 720,
                "Compact legacy scene should use its real prepared crop, not fake-HD");
        check(SiegeSceneCatalog.anomalyIndex() < 0, "Normal scene catalog must not expose an anomaly entry");
        check(!SiegeSceneCatalog.containsId(SiegeEasterEggVault.TEMPEST_JUTCHERSON),
                "Tempest easter egg must stay outside menu/gallery scenes");
        check(SiegeSceneCatalog.featuredIndex() >= 0, "Featured scene metadata missing");

        System.out.println("SIEGE GUI resources: " + images.size()
                + " images decoded; native-resolution 16:9 scenes and easter-egg isolation verified");
    }

    private static int indexOf(String id) {
        for (int i = 0; i < SiegeSceneCatalog.count(); i++) {
            if (SiegeSceneCatalog.id(i).equals(id)) return i;
        }
        throw new AssertionError("Missing scene: " + id);
    }

    private static boolean supportedImage(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
    }

    private static int[] decode(Path image) {
        check(Files.isRegularFile(image), "Missing GUI image " + image);
        try {
            var decoded = ImageIO.read(image.toFile());
            check(decoded != null, "Unreadable GUI image " + image);
            int width = decoded.getWidth(), height = decoded.getHeight();
            check(width > 0 && height > 0, "Invalid GUI image dimensions " + image);
            check(width <= 8192 && height <= 8192,
                    "Unsafe GUI image dimensions " + image + " -> " + width + "x" + height);
            return new int[]{width, height};
        } catch (IOException error) {
            throw new AssertionError("Corrupt GUI image " + image + ": " + error.getMessage(), error);
        }
    }
}
