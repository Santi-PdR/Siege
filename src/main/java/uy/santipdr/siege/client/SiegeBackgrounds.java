package uy.santipdr.siege.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import uy.santipdr.siege.SiegeMod;

import java.util.List;

public final class SiegeBackgrounds {
    private static final List<ResourceLocation> SCENES = List.of(
            scene("dummies_assault"), scene("anniversary"), scene("frontline_19"),
            scene("cyborg"), scene("last_stand"), scene("vought_siege"), scene("earth_orbit"),
            scene("canyon_engagement"), scene("night_battle"),
            scene("night_operation"), scene("urban_rendezvous"), scene("rooftop_squad"), scene("tempest_jutcherson")
    );

    private static final long SCENE_MS = 24_000L;
    private static final long CROSSFADE_MS = 4_800L;

    private SiegeBackgrounds() { }

    public static int currentIndex(long now) {
        if (SiegeConfig.selectedScene >= 0) return Math.floorMod(SiegeConfig.selectedScene, SCENES.size());
        return SiegeConfig.animatedBackgrounds ? rotationIndex(Math.floorDiv(now, SCENE_MS)) : 0;
    }

    private static int rotationIndex(long slot) {
        return SiegeSceneSchedule.index(slot, !SiegeConfig.reducedMotion && !SiegeConfig.reduceFlashes);
    }

    private static int sourceWidth(int index) {
        return switch (Math.floorMod(index, count())) { case 9, 10 -> 735; case 11 -> 680; case 12 -> 720; default -> 960; };
    }

    private static int sourceHeight(int index) {
        return switch (Math.floorMod(index, count())) { case 9 -> 490; case 10 -> 414; case 11 -> 510; case 12 -> 405; default -> 540; };
    }

    public static long rotationRemainingMs(long now) {
        if (SiegeConfig.selectedScene >= 0 || !SiegeConfig.animatedBackgrounds) return -1L;
        long local = Math.floorMod(now, SCENE_MS);
        return SCENE_MS - local;
    }

    public static String rotationState(boolean spanish, long now) {
        if (SiegeConfig.selectedScene >= 0) return spanish ? "Fondo fijado" : "Background pinned";
        if (!SiegeConfig.animatedBackgrounds) return spanish ? "Rotación desactivada" : "Rotation disabled";
        long remaining = rotationRemainingMs(now);
        long seconds = Math.max(0, (remaining + 999L) / 1000L);
        return (spanish ? "Siguiente escena en " : "Next scene in ") + seconds + " s";
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

    public static String name(int index, boolean spanish) {
        return switch (Math.floorMod(index, SCENES.size())) {
            case 0 -> spanish ? "Asalto de Dummies" : "Dummies Assault";
            case 1 -> spanish ? "Aniversario" : "Anniversary";
            case 2 -> spanish ? "Frente 19" : "Frontline 19";
            case 3 -> spanish ? "Cíborg" : "Cyborg";
            case 4 -> spanish ? "Última resistencia" : "Last Stand";
            case 5 -> spanish ? "Asedio Vought" : "Vought Siege";
            case 6 -> spanish ? "Órbita terrestre" : "Earth Orbit";
            case 7 -> spanish ? "Combate en el cañón" : "Canyon Engagement";
            case 8 -> spanish ? "Batalla nocturna" : "Night Battle";
            case 9 -> spanish ? "Operación nocturna" : "Night Operation";
            case 10 -> spanish ? "Encuentro urbano" : "Urban Rendezvous";
            case 11 -> spanish ? "Escuadrón en azotea · Especial" : "Rooftop Squad · Special";
            default -> "TEMPEST JUTCHERSON";
        };
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
        int value = SiegeConfig.backgroundDarkness;
        if (SiegeConfig.autoContrast) value = Math.max(value, SiegeConfig.highContrast ? 40 : 24);
        return Math.max(0, Math.min(70, value));
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

    public static void render(GuiGraphics graphics, int width, int height, long now) {
        renderInternal(graphics, width, height, now, false);
    }

    public static void renderCover(GuiGraphics graphics, int width, int height, long now) {
        renderInternal(graphics, width, height, now, true);
    }

    private static void renderInternal(GuiGraphics graphics, int width, int height, long now, boolean cover) {
        if (width <= 0 || height <= 0) return;
        graphics.fill(0, 0, width, height, 0xFF080A0C);
        boolean animated = SiegeConfig.animatedBackgrounds && SiegeConfig.selectedScene < 0;
        long slot = animated ? Math.floorDiv(now, SCENE_MS) : 0L;
        long localMs = animated ? Math.floorMod(now, SCENE_MS) : 0L;
        float local = animated ? localMs / (float)SCENE_MS : 0.0F;
        int current = currentIndex(now);
        int next = rotationIndex(slot + 1);

        boolean allowPan = !SiegeConfig.reducedMotion && !SiegeConfig.reduceFlashes
                && SiegeConfig.graphics != SiegeConfig.Graphics.PERFORMANCE;
        float currentProgress = allowPan ? local : 0.5F;
        drawScene(graphics, SCENES.get(current), width, height, 1.0F, current, currentProgress, allowPan, cover);

        if (animated) {
            long fadeStart = SCENE_MS - CROSSFADE_MS;
            if (localMs >= fadeStart) {
                float raw = (localMs - fadeStart) / (float)CROSSFADE_MS;
                float alpha = smoother(raw);
                if (alpha > 0.01F) {
                    float incomingProgress = allowPan ? Math.min(0.18F, raw * 0.18F) : 0.5F;
                    drawScene(graphics, SCENES.get(next), width, height, alpha, next, incomingProgress, allowPan, cover);
                }
                if (!SiegeConfig.reduceFlashes) {
                    int veilAlpha = Math.round((float)Math.sin(alpha * Math.PI) * 20.0F);
                    if (veilAlpha > 0) graphics.fill(0, 0, width, height, veilAlpha << 24);
                }
            }
        }

        int darkness = effectiveBackgroundDarkness() * 255 / 100;
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
        int drawW = Math.max(1, (int)Math.floor(sourceW * scale));
        int drawH = Math.max(1, (int)Math.floor(sourceH * scale));
        int x = (w - drawW) / 2;
        int y = (h - drawH) / 2;
        g.blit(texture, x, y, drawW, drawH, 0, 0, sourceW, sourceH, sourceW, sourceH);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static float smoother(float x) {
        x = Math.max(0.0F, Math.min(1.0F, x));
        return x * x * x * (x * (x * 6.0F - 15.0F) + 10.0F);
    }
}
