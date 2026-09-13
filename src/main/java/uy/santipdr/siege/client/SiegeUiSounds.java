package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import uy.santipdr.siege.SiegeMod;

import java.util.List;

public final class SiegeUiSounds {
    private static AbstractWidget hovered;
    private static long lastHover;
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
            long now = System.currentTimeMillis();
            if (SiegeConfig.hoverSounds && next != null && now - lastHover > 80L) {
                play(SiegeMod.UI_HOVER, 1.0f);
                lastHover = now;
            }
        }
    }

    public static void preview(int sample) {
        switch (Math.floorMod(sample, 3)) {
            case 0 -> play(SiegeMod.UI_HOVER, 1.0F);
            case 1 -> click();
            default -> nextTrack();
        }
    }
    public static void click() { play(SiegeMod.UI_CLICK, 1.0f); }
    public static void back() { play(SiegeMod.UI_BACK, 0.96f); }
    public static void nextTrack() { play(SiegeMod.UI_TRACK, 1.04f); }
    public static void resetHover() { hovered = null; }

    private static void play(RegistryObject<SoundEvent> sound, float pitch) {
        if (!SiegeConfig.uiSounds || SiegeConfig.uiVolume <= 0) return;
        // A missing Forge registry value must never be able to close the menu.
        SoundEvent event;
        if (sound.isPresent()) {
            event = sound.get();
        } else {
            var id = sound.getId();
            if (id == null) return;
            event = SoundEvent.createVariableRangeEvent(id);
        }
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(
                event, pitch, SiegeConfig.clampVolume(SiegeConfig.uiVolume) / 100.0F));
    }
}
