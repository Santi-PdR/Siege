package uy.santipdr.siege.client;

import java.io.InputStream;
import java.util.Properties;

/** Runtime metadata for the Forge-native Third Justice evidence video. */
final class SiegeThirdJusticeVideo {
    record Spec(boolean full, int frames, int fps, int durationMs, int width, int height) {
        Spec {
            frames = Math.max(0, frames);
            fps = Math.max(1, fps);
            durationMs = Math.max(1, durationMs);
            width = Math.max(1, width);
            height = Math.max(1, height);
        }
    }

    private static final Spec FALLBACK = new Spec(false, 3, 1, 3300, 640, 360);
    private static final Spec SPEC = load();

    private SiegeThirdJusticeVideo() { }

    static Spec spec() { return SPEC; }

    private static Spec load() {
        Properties properties = new Properties();
        try (InputStream input = SiegeThirdJusticeVideo.class.getResourceAsStream(
                "/assets/siege/third_justice_video.properties")) {
            if (input == null) return FALLBACK;
            properties.load(input);
            boolean full = "full".equalsIgnoreCase(properties.getProperty("mode", "fallback"));
            return new Spec(
                    full,
                    parse(properties, "frames", FALLBACK.frames()),
                    parse(properties, "fps", FALLBACK.fps()),
                    parse(properties, "duration_ms", FALLBACK.durationMs()),
                    parse(properties, "width", FALLBACK.width()),
                    parse(properties, "height", FALLBACK.height())
            );
        } catch (Exception ignored) {
            return FALLBACK;
        }
    }

    private static int parse(Properties properties, String key, int fallback) {
        try { return Integer.parseInt(properties.getProperty(key, Integer.toString(fallback)).trim()); }
        catch (Exception ignored) { return fallback; }
    }
}
