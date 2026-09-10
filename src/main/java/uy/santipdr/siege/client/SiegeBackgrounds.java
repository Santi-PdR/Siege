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

    private static ResourceLocation scene(String id) {
        return new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/backgrounds/" + id + ".png");
    }

    public static void render(GuiGraphics graphics, int width, int height, long now) {
        boolean animated = SiegeConfig.animatedBackgrounds;
        long slot = animated ? now / SCENE_MS : 0L;
        long localMs = animated ? now % SCENE_MS : 0L;
        float local = animated ? localMs / (float) SCENE_MS : 0.0F;
        int current = (int) (slot % SCENES.size());
        int next = (current + 1) % SCENES.size();

        boolean allowPan = !SiegeConfig.reducedMotion && SiegeConfig.graphics != SiegeConfig.Graphics.PERFORMANCE;
        float currentProgress = allowPan ? local : 0.5F;
        drawScene(graphics, SCENES.get(current), width, height, 1.0F, current, currentProgress, allowPan);

        if (animated) {
            long fadeStart = SCENE_MS - CROSSFADE_MS;
            if (localMs >= fadeStart) {
                float raw = (localMs - fadeStart) / (float) CROSSFADE_MS;
                float alpha = smoother(raw);
                float incomingProgress = allowPan ? Math.min(0.18F, raw * 0.18F) : 0.5F;
                drawScene(graphics, SCENES.get(next), width, height, alpha, next, incomingProgress, allowPan);

                // A very small midpoint veil masks large exposure differences between source images
                // without turning the transition into a visible black flash.
                int veilAlpha = Math.round((float) Math.sin(alpha * Math.PI) * 20.0F);
                if (veilAlpha > 0) graphics.fill(0, 0, width, height, veilAlpha << 24);
            }
        }

        graphics.fill(0, 0, width, height, 0x3A000000);
        graphics.fill(0, 0, Math.min(width, Math.max(220, width / 4)), height, 0x70000000);

        if (SiegeConfig.scanlines && SiegeConfig.graphics != SiegeConfig.Graphics.PERFORMANCE) {
            int spacing = SiegeConfig.graphics == SiegeConfig.Graphics.CINEMATIC ? 4 : 7;
            for (int y = 0; y < height; y += spacing) {
                graphics.fill(0, y, width, y + 1, 0x10000000);
            }
        }
    }

    private static void drawScene(GuiGraphics g, ResourceLocation texture, int w, int h, float alpha,
                                  int sceneIndex, float progress, boolean allowPan) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, Math.max(0.0F, Math.min(1.0F, alpha)));

        int overscan;
        if (!allowPan) overscan = 0;
        else if (SiegeConfig.graphics == SiegeConfig.Graphics.CINEMATIC) overscan = 26;
        else overscan = 16;

        int directionX = ((sceneIndex * 31) & 1) == 0 ? 1 : -1;
        int directionY = ((sceneIndex * 17) & 2) == 0 ? 1 : -1;
        float travel = (progress - 0.5F) * 2.0F;
        int panX = allowPan ? Math.round(directionX * travel * overscan * 0.58F) : 0;
        int panY = allowPan ? Math.round(directionY * travel * overscan * 0.24F) : 0;

        int x = -overscan + panX;
        int y = -overscan + panY;
        int drawW = w + overscan * 2;
        int drawH = h + overscan * 2;
        g.blit(texture, x, y, drawW, drawH, 0, 0, 960, 540, 960, 540);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static float smoother(float x) {
        x = Math.max(0.0F, Math.min(1.0F, x));
        return x * x * x * (x * (x * 6.0F - 15.0F) + 10.0F);
    }
}
