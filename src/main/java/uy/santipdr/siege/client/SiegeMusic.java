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
    private static final List<String> TRACK_NAMES = List.of(
            "Tale of a Cruel World",
            "Darkest of Days",
            "Kaptain Music Box",
            "Heaven's Hell-Sent Gift"
    );
    private static final List<Integer> queue = new ArrayList<>();
    private static SoundInstance active;
    private static int previous = -1;
    private static long lastStartAttempt;

    private SiegeMusic() { }

    /** Called every client tick so vanilla menu screens keep the SIEGE soundtrack too. */
    public static void tick() {
        if (shouldPlay()) ensurePlaying();
        else stop();
    }

    public static void ensurePlaying() {
        Minecraft minecraft = Minecraft.getInstance();
        if (!shouldPlay()) {
            stop();
            return;
        }

        // SIEGE owns music only while there is no loaded world. Gameplay music is never touched.
        minecraft.getMusicManager().stopPlaying();
        var manager = minecraft.getSoundManager();
        if (active != null && manager.isActive(active)) return;

        long now = System.currentTimeMillis();
        if (now - lastStartAttempt < 750L) return;
        lastStartAttempt = now;
        playNext(false);
    }

    public static void nextTrack() {
        if (!SiegeConfig.music) {
            SiegeConfig.music = true;
            SiegeConfig.save();
        }
        if (!shouldPlay()) return;
        playNext(true);
    }

    public static void refreshVolume() {
        if (!shouldPlay()) {
            stop();
            return;
        }
        if (previous < 0) {
            ensurePlaying();
            return;
        }
        playIndex(previous, true);
    }

    public static String currentTrackName() {
        return previous >= 0 && previous < TRACK_NAMES.size() ? TRACK_NAMES.get(previous) : "--";
    }

    private static boolean shouldPlay() {
        Minecraft minecraft = Minecraft.getInstance();
        return SiegeConfig.music
                && SiegeConfig.musicVolume > 0
                && minecraft.level == null
                && minecraft.screen != null;
    }

    private static void playNext(boolean stopCurrent) {
        if (queue.isEmpty()) refillQueue();
        int next = queue.remove(0);
        previous = next;
        playIndex(next, stopCurrent);
    }

    private static void playIndex(int index, boolean stopCurrent) {
        var manager = Minecraft.getInstance().getSoundManager();
        if (active != null && (stopCurrent || manager.isActive(active))) manager.stop(active);
        float volume = SiegeConfig.clampVolume(SiegeConfig.musicVolume) / 100.0F;
        active = SimpleSoundInstance.forUI(TRACKS.get(index).get(), 1.0F, volume);
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
