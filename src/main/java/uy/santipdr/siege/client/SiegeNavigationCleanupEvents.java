package uy.santipdr.siege.client;

import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uy.santipdr.siege.SiegeMod;

/**
 * Final navigation cleanup for 5.00.
 *
 * Archive and Armory belong to the main screen; Diagnostics is not a player-facing
 * destination. Running at LOWEST lets this clean legacy buttons added by older
 * screen hooks without rewriting the vanilla/native theming layer.
 */
@Mod.EventBusSubscriber(modid = SiegeMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SiegeNavigationCleanupEvents {
    private SiegeNavigationCleanupEvents() { }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void initialized(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof IntelScreenV3) {
            removeByLabel(event, "GUÍA", "GUIDE", "ARCHIVO", "ARCHIVE", "ARSENAL", "ARMORY");
        }
        if (event.getScreen() instanceof SiegeSettingsScreen || event.getScreen() instanceof SiegeSystemScreen) {
            removeContaining(event, "DIAGNÓSTICO", "DIAGNOSTIC", "RECUPERACIÓN", "RECOVERY");
        }
    }

    private static void removeByLabel(ScreenEvent.Init.Post event, String... labels) {
        for (var listener : List.copyOf(event.getListenersList())) {
            if (!(listener instanceof AbstractWidget widget)) continue;
            String text = normalize(widget.getMessage().getString());
            for (String label : labels) {
                if (text.equals(normalize(label))) {
                    event.removeListener(listener);
                    break;
                }
            }
        }
    }

    private static void removeContaining(ScreenEvent.Init.Post event, String... fragments) {
        for (var listener : List.copyOf(event.getListenersList())) {
            if (!(listener instanceof AbstractWidget widget)) continue;
            String text = normalize(widget.getMessage().getString());
            for (String fragment : fragments) {
                if (text.contains(normalize(fragment))) {
                    event.removeListener(listener);
                    break;
                }
            }
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }
}
