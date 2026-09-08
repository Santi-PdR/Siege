package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import uy.santipdr.siege.SiegeMod;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class SiegeMusic {
    private static final List<RegistryObject<SoundEvent>> TRACKS = List.of(
            SiegeMod.TALE_CRUEL_WORLD, SiegeMod.DARKEST_OF_DAYS,
            SiegeMod.KAPTAIN_MUSIC_BOX, SiegeMod.HEAVENS_GIFT
    );
    private static SoundInstance active;
    private static int previous = -1;

    private SiegeMusic() {}

    public static void ensurePlaying() {
        var manager = Minecraft.getInstance().getSoundManager();
        if (active != null && manager.isActive(active)) return;
        int next;
        do next = ThreadLocalRandom.current().nextInt(TRACKS.size());
        while (TRACKS.size() > 1 && next == previous);
        previous = next;
        active = SimpleSoundInstance.forMusic(TRACKS.get(next).get());
        manager.play(active);
    }

    public static void stop() {
        if (active != null) Minecraft.getInstance().getSoundManager().stop(active);
        active = null;
    }
}
