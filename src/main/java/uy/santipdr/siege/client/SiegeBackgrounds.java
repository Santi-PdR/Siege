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
    private static final long SCENE_MS = 18_000L;
    private static final long FADE_MS = 1_600L;

    private SiegeBackgrounds() {}

    private static ResourceLocation scene(String id) {
        return new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/backgrounds/" + id + ".png");
    }

    public static void render(GuiGraphics graphics, int width, int height, long now) {
        long slot = now / SCENE_MS;
        float local = (now % SCENE_MS) / (float) SCENE_MS;
        int current = (int) (slot % SCENES.size());
        float drift = (float) Math.sin(local * Math.PI) * 0.012f;
        drawCover(graphics, SCENES.get(current), width, height, 1f, drift);
        if (local > 1f - FADE_MS / (float) SCENE_MS) {
            float alpha = (local - (1f - FADE_MS / (float) SCENE_MS)) / (FADE_MS / (float) SCENE_MS);
            drawCover(graphics, SCENES.get((current + 1) % SCENES.size()), width, height, smooth(alpha), -drift);
        }
        graphics.fill(0, 0, width, height, 0x3D000000);
        graphics.fill(0, 0, Math.max(300, width / 4), height, 0x72000000);
        for (int y = 0; y < height; y += 4) graphics.fill(0, y, width, y + 1, 0x12000000);
    }

    private static void drawCover(GuiGraphics g, ResourceLocation texture, int w, int h, float alpha, float drift) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        int overscan = 8;
        int x = -overscan + Math.round(drift * w);
        g.blit(texture, x, -overscan, w + overscan * 2, h + overscan * 2,
                0, 0, 960, 540, 960, 540);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    private static float smooth(float x) { return x * x * (3f - 2f * x); }
}
