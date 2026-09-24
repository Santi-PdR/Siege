package uy.santipdr.siege.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import uy.santipdr.siege.SiegeMod;

import java.util.List;
import java.util.stream.IntStream;

public final class SiegeBackgrounds {
    private static final List<ResourceLocation> SCENES = IntStream.range(0, SiegeSceneCatalog.count())
            .mapToObj(index -> scene(SiegeSceneCatalog.id(index))).toList();

    private SiegeBackgrounds() { }

    /** Timing is read live so Settings/config changes do not require a restart. */
    private static long sceneMs() {
        return Math.max(12_000L, Math.min(60_000L, SiegeConfig.backgroundSceneSeconds * 1_000L));
    }

    private static long crossfadeMs() {
        long requested = Math.max(0L, Math.min(10_000L, SiegeConfig.backgroundCrossfadeSeconds * 1_000L));
        return Math.min(requested, sceneMs() / 2L);
    }

    public static int currentIndex(long now) {
        if (SiegeConfig.selectedScene >= 0) return Math.floorMod(SiegeConfig.selectedScene, SCENES.size());
        long sceneMs = sceneMs();
        return SiegeConfig.animatedBackgrounds ? rotationIndex(Math.floorDiv(now, sceneMs)) : 0;
    }

    public static int nextIndex(long now) {
        int current = currentIndex(now);
        if (SiegeConfig.selectedScene >= 0 || !SiegeConfig.animatedBackgrounds) return current;
        return rotationIndex(Math.floorDiv(now, sceneMs()) + 1L);
    }

    private static int rotationIndex(long slot) {
        return SiegeSceneSchedule.index(slot, !SiegeConfig.reducedMotion && !SiegeConfig.reduceFlashes);
    }

    private static int sourceWidth(int index) { return SiegeSceneCatalog.width(index); }
    private static int sourceHeight(int index) { return SiegeSceneCatalog.height(index); }

    public static long rotationRemainingMs(long now) {
        if (SiegeConfig.selectedScene >= 0 || !SiegeConfig.animatedBackgrounds) return -1L;
        long duration = sceneMs();
        long local = Math.floorMod(now, duration);
        return duration - local;
    }

    public static float rotationProgress(long now) {
        if (SiegeConfig.selectedScene >= 0 || !SiegeConfig.animatedBackgrounds) return 0.0F;
        long duration = sceneMs();
        return Math.max(0.0F, Math.min(1.0F, Math.floorMod(now, duration) / (float)duration));
    }

    public static String rotationState(boolean spanish, long now) {
        if (SiegeConfig.selectedScene >= 0) return spanish ? "Fondo fijado" : "Background pinned";
        if (!SiegeConfig.animatedBackgrounds) return spanish ? "Rotación desactivada" : "Rotation disabled";
        long remaining = rotationRemainingMs(now);
        long seconds = Math.max(0, (remaining + 999L) / 1000L);
        return (spanish ? "Siguiente escena en " : "Next scene in ") + seconds + " s";
    }

    public static String rotationDetail(boolean spanish, long now) {
        if (SiegeConfig.selectedScene >= 0 || !SiegeConfig.animatedBackgrounds) return rotationState(spanish, now);
        int next = nextIndex(now);
        long seconds = Math.max(0, (rotationRemainingMs(now) + 999L) / 1000L);
        return (spanish ? "SIGUIENTE: " : "NEXT: ") + name(next, spanish) + " · " + seconds + " s";
    }

    public static void renderContainedRegion(GuiGraphics g, int x, int y, int w, int h, int index, float alpha) {
        if (w <= 0 || h <= 0) return;
        double scale = Math.min(w / (double)sourceWidth(index), h / (double)sourceHeight(index));
        int drawW = Math.max(1, (int)Math.floor(sourceWidth(index) * scale));
        int drawH = Math.max(1, (int)Math.floor(sourceHeight(index) * scale));
        renderRegion(g, x + (w - drawW) / 2, y + (h - drawH) / 2, drawW, drawH, index, alpha);
    }

    public static int count() { return SCENES.size(); }
    public static String name(int index) { return name(index, false); }
    public static String name(int index, boolean spanish) { return SiegeSceneCatalog.label(index, spanish); }
    public static boolean isAnomaly(int index) { return SiegeSceneCatalog.kind(index) == SiegeSceneCatalog.Kind.ANOMALY; }
    public static boolean isFeatured(int index) { return SiegeSceneCatalog.kind(index) == SiegeSceneCatalog.Kind.FEATURED; }

    public static String sceneTag(int index, boolean spanish) {
        return switch (SiegeSceneCatalog.kind(index)) {
            case STANDARD -> spanish ? "ESCENA" : "SCENE";
            case FEATURED -> spanish ? "DESTACADO" : "FEATURED";
            case ANOMALY -> spanish ? "ANOMALÍA VISUAL" : "VISUAL ANOMALY";
        };
    }

    public static String sourceTag(int index, boolean spanish) {
        return SiegeSceneCatalog.sourceLabel(index, spanish);
    }

    public static void renderPreview(GuiGraphics graphics, int width, int height, int index) {
        if (width <= 0 || height <= 0) return;
        int safeIndex = Math.floorMod(index, SCENES.size());
        drawScene(graphics, SCENES.get(safeIndex), width, height, 1.0F, safeIndex, 0.5F, false);
    }

    public static void renderRegion(GuiGraphics g, int x, int y, int w, int h, int index, float alpha) {
        if (w <= 0 || h <= 0 || alpha <= 0.0F) return;
        g.enableScissor(x, y, x + w, y + h);
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        drawScene(g, SCENES.get(Math.floorMod(index, SCENES.size())), w, h, alpha, index, 0.5F, false);
        g.pose().popPose();
        g.disableScissor();
    }

    public static double panelFraction(int width, int height, double guiScale) {
        if (width <= 0) return 1.0D;
        boolean compact = width < 520 || height < 290;
        boolean three = guiScale >= 2.75 && guiScale < 3.75 && !compact;
        int margin = compact ? 9 : Math.max(14, width / 55);
        int menu = three ? Math.min(218, Math.max(198, width / 5))
                : Math.min(compact ? 176 : 212, Math.max(138, width / (compact ? 2 : 5)));
        menu = Math.max(0, Math.min(menu, width - margin * 2));
        return Math.max(0.0D, Math.min(1.0D, Math.min(width, margin + menu + (compact ? 12 : 18)) / (double)width));
    }

    public static int effectiveBackgroundDarkness() {
        return effectiveBackgroundDarkness(currentIndex(System.currentTimeMillis()));
    }

    public static int effectiveBackgroundDarkness(int sceneIndex) {
        int value = SiegeConfig.backgroundDarkness + SiegeSceneCatalog.darknessBias(sceneIndex);
        if (SiegeConfig.autoContrast) value = Math.max(value, SiegeConfig.highContrast ? 40 : 24);
        return Math.max(0, Math.min(76, value));
    }

    public static int effectivePanelDarkness() {
        int value = SiegeConfig.panelDarkness;
        if (SiegeConfig.autoContrast) value = Math.max(value, SiegeConfig.highContrast ? 78 : 66);
        return Math.max(20, Math.min(90, value));
    }

    public static void renderPanel(GuiGraphics g, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) return;
        int alpha = effectivePanelDarkness() * 255 / 100;
        g.fill(x, y, x + width, y + height, (alpha << 24) | 0x00050506);
    }

    private static ResourceLocation scene(String id) {
        return new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/backgrounds/" + id + ".png");
    }

    /** Normal full-screen menu rendering uses cover: no bars, no stretching. */
    public static void render(GuiGraphics graphics, int width, int height, long now) {
        renderInternal(graphics, width, height, now, true);
    }

    public static void renderCover(GuiGraphics graphics, int width, int height, long now) {
        renderInternal(graphics, width, height, now, true);
    }

    private static void renderInternal(GuiGraphics graphics, int width, int height, long now, boolean cover) {
        if (width <= 0 || height <= 0) return;
        graphics.fill(0, 0, width, height, 0xFF080A0C);

        long duration = sceneMs();
        long fadeDuration = crossfadeMs();
        boolean animated = SiegeConfig.animatedBackgrounds && SiegeConfig.selectedScene < 0;
        long slot = animated ? Math.floorDiv(now, duration) : 0L;
        long localMs = animated ? Math.floorMod(now, duration) : 0L;
        float local = animated ? localMs / (float)duration : 0.5F;
        int current = currentIndex(now);
        int next = rotationIndex(slot + 1);

        boolean allowPan = !SiegeConfig.reducedMotion && !SiegeConfig.reduceFlashes
                && SiegeConfig.graphics != SiegeConfig.Graphics.PERFORMANCE
                && SiegeConfig.backgroundMotionIntensity > 0;
        float currentProgress = allowPan ? local : 0.5F;
        drawScene(graphics, SCENES.get(current), width, height, 1.0F, current, currentProgress, allowPan, cover);

        int darknessBias = SiegeSceneCatalog.darknessBias(current);
        if (animated && fadeDuration > 0L) {
            long fadeStart = duration - fadeDuration;
            if (localMs >= fadeStart) {
                float raw = Math.max(0.0F, Math.min(1.0F, (localMs - fadeStart) / (float)fadeDuration));
                float alpha = smoother(raw);
                if (alpha > 0.01F) {
                    float incomingProgress = allowPan ? Math.min(0.18F, raw * 0.18F) : 0.5F;
                    drawScene(graphics, SCENES.get(next), width, height, alpha, next, incomingProgress, allowPan, cover);
                    darknessBias = Math.max(darknessBias, Math.round(SiegeSceneCatalog.darknessBias(next) * alpha));
                }
                if (!SiegeConfig.reduceFlashes) {
                    int veilAlpha = Math.round((float)Math.sin(alpha * Math.PI) * 16.0F);
                    if (veilAlpha > 0) graphics.fill(0, 0, width, height, veilAlpha << 24);
                }
            }
        }

        int base = SiegeConfig.backgroundDarkness + darknessBias;
        if (SiegeConfig.autoContrast) base = Math.max(base, SiegeConfig.highContrast ? 40 : 24);
        int darkness = Math.max(0, Math.min(76, base)) * 255 / 100;
        graphics.fill(0, 0, width, height, darkness << 24);

        if (SiegeConfig.scanlines && SiegeConfig.scanlineIntensity > 0
                && SiegeConfig.graphics != SiegeConfig.Graphics.PERFORMANCE) {
            int baseSpacing = SiegeConfig.graphics == SiegeConfig.Graphics.CINEMATIC ? 4 : 7;
            int spacing = Math.max(baseSpacing, (height + 299) / 300);
            int maxAlpha = SiegeConfig.highContrast ? 10 : 24;
            int alpha = Math.max(1, Math.round(maxAlpha * SiegeConfig.scanlineIntensity / 100.0F));
            int lineColor = alpha << 24;
            for (int y = 0; y < height; y += spacing) graphics.fill(0, y, width, Math.min(height, y + 1), lineColor);
        }
    }

    private static void drawScene(GuiGraphics g, ResourceLocation texture, int w, int h, float alpha,
                                  int sceneIndex, float progress, boolean allowPan) {
        drawScene(g, texture, w, h, alpha, sceneIndex, progress, allowPan, false);
    }

    private static void drawScene(GuiGraphics g, ResourceLocation texture, int w, int h, float alpha,
                                  int sceneIndex, float progress, boolean allowPan, boolean cover) {
        if (w <= 0 || h <= 0 || alpha <= 0.0F) return;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, Math.max(0.0F, Math.min(1.0F, alpha)));

        int sourceW = sourceWidth(sceneIndex), sourceH = sourceHeight(sceneIndex);
        double scale = cover ? Math.max(w / (double)sourceW, h / (double)sourceH)
                : Math.min(w / (double)sourceW, h / (double)sourceH);

        // A tiny safe overscan creates real cinematic drift while preserving aspect ratio.
        // It is disabled completely by reduced-motion, flash-reduction and Performance mode.
        double motion = allowPan ? SiegeConfig.backgroundMotionIntensity / 100.0D : 0.0D;
        if (SiegeConfig.graphics == SiegeConfig.Graphics.BALANCED) motion *= 0.65D;
        double overscan = cover && allowPan ? 1.0D + 0.045D * motion : 1.0D;

        int drawW = Math.max(1, (int)Math.ceil(sourceW * scale * overscan));
        int drawH = Math.max(1, (int)Math.ceil(sourceH * scale * overscan));
        int centerX = (w - drawW) / 2;
        int centerY = (h - drawH) / 2;
        int x = centerX;
        int y = centerY;

        if (cover && allowPan) {
            float eased = smoother(progress);
            float travel = (eased - 0.5F) * 2.0F;
            int availableX = Math.max(0, (drawW - w) / 2);
            int availableY = Math.max(0, (drawH - h) / 2);
            int dirX = ((sceneIndex * 37) % 3) - 1;
            int dirY = ((sceneIndex * 53 + 1) % 3) - 1;
            if (dirX == 0 && dirY == 0) dirX = 1;
            x += Math.round(travel * availableX * dirX * 0.72F);
            y += Math.round(travel * availableY * dirY * 0.58F);
        }

        g.blit(texture, x, y, drawW, drawH, 0, 0, sourceW, sourceH, sourceW, sourceH);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static float smoother(float x) {
        x = Math.max(0.0F, Math.min(1.0F, x));
        return x * x * x * (x * (x * 6.0F - 15.0F) + 10.0F);
    }
}
