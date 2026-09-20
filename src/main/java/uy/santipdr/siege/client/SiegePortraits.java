package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import uy.santipdr.siege.SiegeMod;

/** Resource-manager lookup honors pack reloads; no stale availability cache or disk reads. */
public final class SiegePortraits {
    private static final ResourceLocation CLASSIFIED = new ResourceLocation(SiegeMod.MOD_ID,
            "textures/gui/intel/placeholder/classified.png");
    private SiegePortraits() { }
    public static ResourceLocation resolve(String image) {
        if (image == null || image.isBlank()) return CLASSIFIED;
        ResourceLocation candidate = ResourceLocation.tryParse(SiegeMod.MOD_ID + ":textures/gui/intel/" + image + ".png");
        if (candidate == null) return CLASSIFIED;
        return Minecraft.getInstance().getResourceManager().getResource(candidate).isPresent() ? candidate : CLASSIFIED;
    }
}
