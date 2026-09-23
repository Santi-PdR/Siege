package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import uy.santipdr.siege.SiegeMod;

import java.util.List;

/** One semantic UI-audio layer with cooldowns so navigation never becomes noisy. */
public final class SiegeUiSounds {
    private static AbstractWidget hovered;
    private static long lastHover;
    private static long lastSemantic;
    private SiegeUiSounds() {}

    public static void updateHover(List<? extends GuiEventListener> children) {
        AbstractWidget next = null;
        AbstractWidget focused = null;
        for (GuiEventListener child : children) {
            if (child instanceof AbstractWidget widget && widget.visible && widget.active) {
                if (widget.isHovered()) { next = widget; break; }
                if (widget.isFocused()) focused = widget;
            }
        }
        if (next == null) next = focused;
        if (next != hovered) {
            hovered = next;
            long now = System.nanoTime() / 1_000_000L;
            if (SiegeConfig.hoverSounds && next != null && now - lastHover > 90L) {
                play(SiegeMod.UI_HOVER, 0.42F);
                lastHover = now;
            }
        }
    }

    public static void preview(int sample) {
        switch (Math.floorMod(sample, 3)) {
            case 0 -> play(SiegeMod.UI_HOVER, 0.42F);
            case 1 -> click();
            default -> nextTrack();
        }
    }

    public static void click() { play(SiegeMod.UI_CLICK, 0.82F); }
    public static void confirm() { semantic(SiegeMod.UI_TRACK, 0.70F); }
    public static void back() { semantic(SiegeMod.UI_BACK, 0.64F); }
    public static void nextTrack() { semantic(SiegeMod.UI_TRACK, 0.70F); }
    public static void selection() { semantic(SiegeMod.UI_CLICK, 0.62F); }
    public static void dossier() { semantic(SiegeMod.UI_TRACK, 0.52F); }
    public static void category() { semantic(SiegeMod.UI_CLICK, 0.56F); }
    public static void warning() { semantic(SiegeMod.UI_BACK, 0.68F); }
    public static void error() { semantic(SiegeMod.UI_BACK, 0.72F); }
    public static void resetHover() { hovered = null; }

    private static void semantic(RegistryObject<SoundEvent> sound, float gain) {
        long now = System.nanoTime() / 1_000_000L;
        if (now - lastSemantic < 55L) return;
        lastSemantic = now;
        play(sound, gain);
    }

    private static void play(RegistryObject<SoundEvent> sound, float gain) {
        if (!SiegeConfig.uiSounds || SiegeConfig.uiVolume <= 0) return;
        SoundEvent event;
        if (sound.isPresent()) event = sound.get();
        else {
            var id = sound.getId();
            if (id == null) return;
            event = SoundEvent.createVariableRangeEvent(id);
        }
        float volume = SiegeConfig.clampVolume(SiegeConfig.uiVolume) / 100.0F * Math.max(0.0F, Math.min(1.0F, gain));
        // Keep UI samples at their authored pitch. 4.00 pitch-shifted the same sample for
        // different actions, which made clicks/back/warnings sound distorted on some setups.
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event, 1.0F, volume));
    }
}
