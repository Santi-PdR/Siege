import uy.santipdr.siege.client.SiegeSceneCatalog;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * SIEGE 2.0 release gate for visual media. Every GUI PNG/JPG must decode.
 * Backgrounds are shipped as prepared 1920x1080 masters so runtime rendering
 * never relies on a sub-HD source texture.
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
        check(SiegeSceneCatalog.count() >= 13, "Scene catalog unexpectedly shrank");
        for (int i = 0; i < SiegeSceneCatalog.count(); i++) {
            Path file = backgrounds.resolve(SiegeSceneCatalog.id(i) + ".png");
            int[] size = decode(file);
            check(size[0] == SiegeSceneCatalog.width(i) && size[1] == SiegeSceneCatalog.height(i),
                    "Scene metadata mismatch for " + SiegeSceneCatalog.id(i) + ": catalog="
                            + SiegeSceneCatalog.width(i) + "x" + SiegeSceneCatalog.height(i)
                            + " file=" + size[0] + "x" + size[1]);
            check(size[0] >= 1920 && size[1] >= 1080,
                    "SIEGE 2.0 background below Full HD: " + SiegeSceneCatalog.id(i) + " -> " + size[0] + "x" + size[1]);
            check(size[0] * 9 == size[1] * 16,
                    "Background must remain 16:9: " + SiegeSceneCatalog.id(i));
        }

        int anomaly = SiegeSceneCatalog.anomalyIndex();
        check(anomaly >= 0 && SiegeSceneCatalog.kind(anomaly) == SiegeSceneCatalog.Kind.ANOMALY,
                "Tempest anomaly metadata missing");
        check(!SiegeSceneCatalog.comfortEligible(anomaly), "Anomaly must stay excluded from comfort rotation");
        check(SiegeSceneCatalog.featuredIndex() >= 0, "Featured scene metadata missing");

        System.out.println("SIEGE GUI resources: " + images.size()
                + " PNG/JPG images decoded; Full-HD scene metadata verified");
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
