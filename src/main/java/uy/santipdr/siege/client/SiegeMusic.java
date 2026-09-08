package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import uy.santipdr.siege.SiegeMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SiegeMusic {
    private static final List<RegistryObject<SoundEvent>> TRACKS = List.of(
            SiegeMod.TALE_CRUEL_WORLD, SiegeMod.DARKEST_OF_DAYS,
            SiegeMod.KAPTAIN_MUSIC_BOX, SiegeMod.HEAVENS_GIFT
    );
    private static final List<Integer> queue = new ArrayList<>();
    private static SoundInstance active;
    private static int previous = -1;

    private SiegeMusic() {}

    public static void ensurePlaying() {
        if (!SiegeConfig.music) { stop(); return; }
        var manager = Minecraft.getInstance().getSoundManager();
        if (active != null && manager.isActive(active)) return;
        playNext(false);
    }

    public static void nextTrack() {
        if (!SiegeConfig.music) return;
        playNext(true);
    }

    private static void playNext(boolean stopCurrent) {
        var manager = Minecraft.getInstance().getSoundManager();
        if (stopCurrent && active != null) manager.stop(active);
        if (queue.isEmpty()) refillQueue();
        int next = queue.remove(0);
        previous = next;
        active = SimpleSoundInstance.forMusic(TRACKS.get(next).get());
        manager.play(active);
    }

    private static void refillQueue() {
        for (int i = 0; i < TRACKS.size(); i++) queue.add(i);
        Collections.shuffle(queue);
        if (queue.size() > 1 && queue.get(0) == previous) Collections.swap(queue, 0, 1);
    }

    public static void stop() {
        if (active != null) Minecraft.getInstance().getSoundManager().stop(active);
        active = null;
    }
}
