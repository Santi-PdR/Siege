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
            SiegeMod.TALE_CRUEL_WORLD,
            SiegeMod.DARKEST_OF_DAYS,
            SiegeMod.KAPTAIN_MUSIC_BOX,
            SiegeMod.HEAVENS_GIFT
    );
    private static final List<Integer> queue = new ArrayList<>();
    private static SoundInstance active;
    private static int previous = -1;
    private static long lastStartAttempt;

    private SiegeMusic() { }

    public static void ensurePlaying() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getMusicManager().stopPlaying();
        if (!SiegeConfig.music) {
            stop();
            return;
        }
        var manager = minecraft.getSoundManager();
        if (active != null && manager.isActive(active)) return;
        long now = System.currentTimeMillis();
        if (now - lastStartAttempt < 750L) return;
        lastStartAttempt = now;
        playNext(false);
    }

    public static void nextTrack() {
        if (!SiegeConfig.music) return;
        playNext(true);
    }

    private static void playNext(boolean stopCurrent) {
        var manager = Minecraft.getInstance().getSoundManager();
        if (active != null && (stopCurrent || manager.isActive(active))) manager.stop(active);
        if (queue.isEmpty()) refillQueue();
        int next = queue.remove(0);
        previous = next;
        // SIEGE owns its menu soundtrack toggle.  Using the UI/master channel keeps the
        // soundtrack audible even when Minecraft's unrelated ambient-music slider is at 0.
        active = SimpleSoundInstance.forUI(TRACKS.get(next).get(), 1.0F, 0.72F);
        manager.play(active);
        lastStartAttempt = System.currentTimeMillis();
    }

    private static void refillQueue() {
        queue.clear();
        for (int i = 0; i < TRACKS.size(); i++) queue.add(i);
        Collections.shuffle(queue);
        if (queue.size() > 1 && queue.get(0) == previous) Collections.swap(queue, 0, 1);
    }

    public static void stop() {
        if (active != null) Minecraft.getInstance().getSoundManager().stop(active);
        active = null;
    }
}
