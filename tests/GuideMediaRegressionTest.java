import uy.santipdr.siege.client.IntelSearch;
import uy.santipdr.siege.client.SiegeGuideData;
import uy.santipdr.siege.client.SiegeGuideSupplemental;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** SIEGE 2.0 equipment/reference media contract. */
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

    public static void main(String[] args) {
        List<SiegeGuideData.Entry> items = SiegeGuideSupplemental.entries(
                SiegeGuideData.Category.ITEMS, "", true);
        var third = items.stream().filter(e -> e.id().equals("third-justice")).findFirst()
                .orElseThrow(() -> new AssertionError("Third Justice missing from Armory"));

        check(third.images().size() == 5, "Third Justice must retain 2 captures + 3 reel frames");
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
            if (art.file().startsWith("third_justice_reel_")) {
                reels++;
                check(decoded.getWidth() >= 640 && decoded.getHeight() >= 360,
                        "Evidence reel frame below 640x360: " + art.file());
                check(decoded.getWidth() * 9 == decoded.getHeight() * 16,
                        "Evidence reel frame must remain 16:9: " + art.file());
            }
        }
        check(reels == 3, "Evidence reel must contain exactly three curated frames");
        System.out.println("Third Justice Armory media and evidence reel contracts passed");
    }
}
