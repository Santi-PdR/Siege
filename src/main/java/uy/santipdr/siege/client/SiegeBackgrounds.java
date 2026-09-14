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
            scene("canyon_engagement"), scene("night_battle")
    );

    private static final long SCENE_MS = 24_000L;
    private static final long CROSSFADE_MS = 4_800L;

    private SiegeBackgrounds() { }

    public static int currentIndex(long now) {
        if (SiegeConfig.selectedScene >= 0) return Math.floorMod(SiegeConfig.selectedScene, SCENES.size());
        return SiegeConfig.animatedBackgrounds ? (int)Math.floorMod(now / SCENE_MS, SCENES.size()) : 0;
    }
    public static void renderContainedRegion(GuiGraphics g, int x, int y, int w, int h, int index, float alpha) {
        double scale = Math.min(w / 960.0, h / 540.0);
        int drawW = Math.max(1, (int)Math.floor(960 * scale));
        int drawH = Math.max(1, (int)Math.floor(540 * scale));
        renderRegion(g, x + (w - drawW) / 2, y + (h - drawH) / 2, drawW, drawH, index, alpha);
    }

    public static int count() { return SCENES.size(); }

    public static String name(int index) {
        return name(index, false);
    }

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
            default -> spanish ? "Batalla nocturna" : "Night Battle";
        };
    }

    public static void renderPreview(GuiGraphics graphics, int width, int height, int index) {
        int safeIndex = Math.floorMod(index, SCENES.size());
        drawScene(graphics, SCENES.get(safeIndex), width, height, 1.0F, safeIndex, 0.5F, false);
    }

    /** Clipped cover rendering shared by the gallery preview and native thumbnail widgets. */
    public static void renderRegion(GuiGraphics g, int x, int y, int w, int h, int index, float alpha) {
        if (w <= 0 || h <= 0) return;
        g.enableScissor(x, y, x + w, y + h);
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        drawScene(g, SCENES.get(Math.floorMod(index, SCENES.size())), w, h, alpha, index, 0.5F, false);
        g.pose().popPose();
        g.disableScissor();
    }

    public static double panelFraction(int width, int height, double guiScale) {
        boolean compact = width < 520 || height < 290;
        boolean three = guiScale >= 2.75 && guiScale < 3.75 && !compact;
        int margin = compact ? 9 : Math.max(14, width / 55);
        int menu = three ? Math.min(218, Math.max(198, width / 5))
                : Math.min(compact ? 176 : 212, Math.max(138, width / (compact ? 2 : 5)));
        menu = Math.min(menu, width - margin * 2);
        return Math.min(width, margin + menu + (compact ? 12 : 18)) / (double)width;
    }
    public static void renderPanel(GuiGraphics g, int x, int y, int width, int height) {
        int alpha = Math.max(0, Math.min(255, SiegeConfig.panelDarkness * 255 / 100));
        g.fill(x, y, x + width, y + height, (alpha << 24) | 0x00050506);
    }

    private static ResourceLocation scene(String id) {
        return new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/backgrounds/" + id + ".png");
    }

    public static void render(GuiGraphics graphics, int width, int height, long now) {
        renderInternal(graphics, width, height, now, false);
    }

    /** Aspect-preserving fullscreen crop used by Multiplayer. */
    public static void renderCover(GuiGraphics graphics, int width, int height, long now) {
        renderInternal(graphics, width, height, now, true);
    }

    private static void renderInternal(GuiGraphics graphics, int width, int height, long now, boolean cover) {
        graphics.fill(0, 0, width, height, 0xFF080A0C);
        boolean animated = SiegeConfig.animatedBackgrounds && SiegeConfig.selectedScene < 0;
        long slot = animated ? now / SCENE_MS : 0L;
        long localMs = animated ? now % SCENE_MS : 0L;
        float local = animated ? localMs / (float) SCENE_MS : 0.0F;
        int current = SiegeConfig.selectedScene >= 0 ? Math.floorMod(SiegeConfig.selectedScene, SCENES.size()) : (int) (slot % SCENES.size());
        int next = (current + 1) % SCENES.size();

        boolean allowPan = !SiegeConfig.reducedMotion && SiegeConfig.graphics != SiegeConfig.Graphics.PERFORMANCE;
        float currentProgress = allowPan ? local : 0.5F;
        drawScene(graphics, SCENES.get(current), width, height, 1.0F, current, currentProgress, allowPan, cover);

        if (animated) {
            long fadeStart = SCENE_MS - CROSSFADE_MS;
            if (localMs >= fadeStart) {
                float raw = (localMs - fadeStart) / (float) CROSSFADE_MS;
                float alpha = smoother(raw);
                float incomingProgress = allowPan ? Math.min(0.18F, raw * 0.18F) : 0.5F;
                drawScene(graphics, SCENES.get(next), width, height, alpha, next, incomingProgress, allowPan, cover);

                // A very small midpoint veil masks large exposure differences between source images
                // without turning the transition into a visible black flash.
                int veilAlpha = Math.round((float) Math.sin(alpha * Math.PI) * 20.0F);
                if (veilAlpha > 0) graphics.fill(0, 0, width, height, veilAlpha << 24);
            }
        }

        int darkness = Math.max(0, Math.min(255, SiegeConfig.backgroundDarkness * 255 / 100));
        graphics.fill(0, 0, width, height, darkness << 24);

        if (SiegeConfig.scanlines && SiegeConfig.graphics != SiegeConfig.Graphics.PERFORMANCE) {
            int spacing = SiegeConfig.graphics == SiegeConfig.Graphics.CINEMATIC ? 4 : 7;
            for (int y = 0; y < height; y += spacing) {
                graphics.fill(0, y, width, y + 1, 0x10000000);
            }
        }
    }

    private static void drawScene(GuiGraphics g, ResourceLocation texture, int w, int h, float alpha,
                                  int sceneIndex, float progress, boolean allowPan) {
        drawScene(g, texture, w, h, alpha, sceneIndex, progress, allowPan, false);
    }

    private static void drawScene(GuiGraphics g, ResourceLocation texture, int w, int h, float alpha,
                                  int sceneIndex, float progress, boolean allowPan, boolean cover) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, Math.max(0.0F, Math.min(1.0F, alpha)));

        // Menus normally preserve the complete image. Multiplayer may request a
        // cover crop so no empty bands or stretched-looking inset remain.
        double scale = cover ? Math.max(w / 960.0D, h / 540.0D)
                : Math.min(w / 960.0D, h / 540.0D);
        int drawW = Math.max(1, (int)Math.floor(960 * scale));
        int drawH = Math.max(1, (int)Math.floor(540 * scale));
        int x = (w - drawW) / 2;
        int y = (h - drawH) / 2;
        g.blit(texture, x, y, drawW, drawH, 0, 0, 960, 540, 960, 540);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static float smoother(float x) {
        x = Math.max(0.0F, Math.min(1.0F, x));
        return x * x * x * (x * (x * 6.0F - 15.0F) + 10.0F);
    }
}
