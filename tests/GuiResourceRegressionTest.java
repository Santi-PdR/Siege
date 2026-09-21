import uy.santipdr.siege.client.SiegeSceneCatalog;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** 1.50 release gate: every GUI PNG must decode, and scene metadata must match the bytes shipped in the JAR. */
public final class GuiResourceRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) throws Exception {
        Path gui = Path.of("src/main/resources/assets/siege/textures/gui");
        check(Files.isDirectory(gui), "Missing GUI texture root");

        List<Path> pngs = new ArrayList<>();
        try (var files = Files.walk(gui)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".png"))
                    .forEach(pngs::add);
        }
        check(!pngs.isEmpty(), "No GUI PNG resources found");
        for (Path png : pngs) decode(png);

        Path backgrounds = gui.resolve("backgrounds");
        check(SiegeSceneCatalog.count() >= 13, "Scene catalog unexpectedly shrank");
        for (int i = 0; i < SiegeSceneCatalog.count(); i++) {
            Path file = backgrounds.resolve(SiegeSceneCatalog.id(i) + ".png");
            int[] size = decode(file);
            check(size[0] == SiegeSceneCatalog.width(i) && size[1] == SiegeSceneCatalog.height(i),
                    "Scene metadata mismatch for " + SiegeSceneCatalog.id(i) + ": catalog="
                            + SiegeSceneCatalog.width(i) + "x" + SiegeSceneCatalog.height(i)
                            + " file=" + size[0] + "x" + size[1]);
        }

        int anomaly = SiegeSceneCatalog.anomalyIndex();
        check(anomaly >= 0 && SiegeSceneCatalog.kind(anomaly) == SiegeSceneCatalog.Kind.ANOMALY,
                "Tempest anomaly metadata missing");
        check(!SiegeSceneCatalog.comfortEligible(anomaly), "Anomaly must stay excluded from comfort rotation");
        check(SiegeSceneCatalog.featuredIndex() >= 0, "Featured scene metadata missing");

        System.out.println("SIEGE GUI resources: " + pngs.size() + " PNGs decoded; scene metadata verified");
    }

    private static int[] decode(Path image) {
        check(Files.isRegularFile(image), "Missing GUI image " + image);
        try {
            var decoded = ImageIO.read(image.toFile());
            check(decoded != null, "Unreadable GUI image " + image);
            int width = decoded.getWidth(), height = decoded.getHeight();
            check(width > 0 && height > 0, "Invalid GUI image dimensions " + image);
            check(width <= 8192 && height <= 8192, "Unsafe GUI image dimensions " + image + " -> " + width + "x" + height);
            return new int[]{width, height};
        } catch (IOException error) {
            throw new AssertionError("Corrupt GUI image " + image + ": " + error.getMessage(), error);
        }
    }
}
