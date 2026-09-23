import uy.santipdr.siege.client.IntelSearch;
import uy.santipdr.siege.client.SiegeGuideData;
import uy.santipdr.siege.client.SiegeGuideSupplemental;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/** SIEGE 5.30 Third Justice visibility and complete-video preparation contract. */
public final class GuideMediaRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    private static BufferedImage decode(Path file) {
        try {
            BufferedImage decoded = ImageIO.read(file.toFile());
            check(decoded != null, "Unreadable Third Justice media: " + file);
            return decoded;
        } catch (Exception ex) {
            throw new AssertionError("Unreadable Third Justice media: " + file + " (" + ex.getMessage() + ")", ex);
        }
    }

    private static double meanLuma(BufferedImage image) {
        long total = 0L;
        long pixels = (long) image.getWidth() * image.getHeight();
        int step = Math.max(1, (int)Math.sqrt(Math.max(1L, pixels / 120_000L)));
        long samples = 0L;
        for (int y = 0; y < image.getHeight(); y += step) {
            for (int x = 0; x < image.getWidth(); x += step) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 255, g = (rgb >> 8) & 255, b = rgb & 255;
                total += Math.round(0.2126 * r + 0.7152 * g + 0.0722 * b);
                samples++;
            }
        }
        return samples == 0 ? 0.0 : total / (double)samples;
    }

    public static void main(String[] args) throws Exception {
        List<SiegeGuideData.Entry> items = SiegeGuideSupplemental.entries(
                SiegeGuideData.Category.ITEMS, "", true);
        var third = items.stream().filter(e -> e.id().equals("third-justice")).findFirst()
                .orElseThrow(() -> new AssertionError("Third Justice missing from Armory"));

        check(third.images().size() == 5, "Third Justice must retain 2 captures + 3 fallback reel frames");
        check(third.bodyEs().contains("0,1 s") && third.bodyEs().contains("1 s"),
                "Third Justice parry timing evidence missing");
        check(third.bodyEs().contains("15%") && third.bodyEs().contains("doble"),
                "Third Justice visible debuffs missing");
        check(!third.bodyEs().toLowerCase().contains("imagen aportada"),
                "External-source wording leaked into in-world Armory copy");

        Path guide = Path.of("src/main/resources/assets/siege/textures/gui/guide");
        int reels = 0;
        for (var art : third.images()) {
            Path file = guide.resolve(art.file());
            check(Files.isRegularFile(file), "Missing Third Justice media: " + file);
            BufferedImage decoded = decode(file);
            check(decoded.getWidth() == art.width() && decoded.getHeight() == art.height(),
                    "Declared Third Justice media dimensions changed: " + art.file()
                            + " expected=" + art.width() + "x" + art.height()
                            + " actual=" + decoded.getWidth() + "x" + decoded.getHeight());
            double mean = meanLuma(decoded);
            check(mean >= 42.0,
                    "Third Justice image is still hidden by crushed darkness: " + art.file() + " mean=" + mean);
            if (art.file().startsWith("third_justice_reel_")) {
                reels++;
                check(decoded.getWidth() >= 640 && decoded.getHeight() >= 360,
                        "Evidence reel frame below 640x360: " + art.file());
                check(decoded.getWidth() * 9 == decoded.getHeight() * 16,
                        "Evidence reel frame must remain 16:9: " + art.file());
            }
        }
        check(reels == 3, "Fallback evidence reel must retain exactly three repaired frames");

        Path manifestPath = Path.of("src/main/resources/assets/siege/third_justice_video.properties");
        check(Files.isRegularFile(manifestPath), "Missing Third Justice video manifest");
        Properties manifest = new Properties();
        try (var input = Files.newInputStream(manifestPath)) { manifest.load(input); }
        String mode = manifest.getProperty("mode", "fallback");
        int frames = Integer.parseInt(manifest.getProperty("frames", "0"));
        int width = Integer.parseInt(manifest.getProperty("width", "0"));
        int height = Integer.parseInt(manifest.getProperty("height", "0"));
        check(width == 640 && height == 360, "Third Justice prepared video must be 640x360");
        if (mode.equals("full")) {
            check(frames >= 20, "Complete Third Justice test must contain a real frame sequence");
            Path videoDir = guide.resolve("third_justice_video");
            long actual;
            try (var stream = Files.list(videoDir)) {
                actual = stream.filter(p -> p.getFileName().toString().matches("frame_[0-9]{5}\\.png")).count();
            }
            check(actual == frames, "Complete Third Justice manifest/frame count mismatch");
            decode(videoDir.resolve("frame_00001.png"));
            decode(videoDir.resolve(String.format("frame_%05d.png", frames)));
        } else {
            check(frames == 3, "Fallback manifest must describe the repaired three-frame reel");
        }

        System.out.println("Third Justice media is visible; video manifest mode=" + mode + ", frames=" + frames);
    }
}
