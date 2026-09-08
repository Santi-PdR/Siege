package uy.santipdr.siege;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import uy.santipdr.siege.client.SiegeTitleScreen;

@Mod(SiegeMod.MOD_ID)
public final class SiegeMod {
    public static final String MOD_ID = "siege";

    public SiegeMod() {
        MinecraftForge.EVENT_BUS.addListener(this::onScreenOpening);
    }

    private void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof net.minecraft.client.gui.screens.TitleScreen
                && !(event.getScreen() instanceof SiegeTitleScreen)) {
            event.setNewScreen(new SiegeTitleScreen());
        }
    }
}

