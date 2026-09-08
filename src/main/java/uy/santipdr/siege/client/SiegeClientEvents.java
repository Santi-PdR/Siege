package uy.santipdr.siege.client;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uy.santipdr.siege.SiegeMod;

@Mod.EventBusSubscriber(modid = SiegeMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SiegeClientEvents {
    private SiegeClientEvents() {}
    @SubscribeEvent public static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof TitleScreen) event.setNewScreen(new SiegeTitleScreen());
        else if (!(event.getScreen() instanceof SiegeTitleScreen)
                && !(event.getScreen() instanceof IntelScreen)
                && !(event.getScreen() instanceof SiegeSettingsScreen)) SiegeMusic.stop();
    }
}
