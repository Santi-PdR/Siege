package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.registries.RegistryObject;
import uy.santipdr.siege.SiegeMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Menu soundtrack controller. Never owns audio while a world/server is loaded. */
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

    private static final int FADE_TICKS = 28;
    private static final long STARTUP_GRACE_MS = 3000L;

    private static SoundInstance active;
    private static int previous = -1;
    private static long activeStartedAt;
    private static float fadeGain;
    private static FadeState fadeState = FadeState.NONE;
    private static boolean ownsMusicSource;

    private SiegeMusic() { }

    /** Called every client tick so vanilla menu screens keep the SIEGE soundtrack too. */
    public static void tick() {
        if (!shouldPlay()) {
            stop();
            return;
        }

        ensurePlaying();
        if (active == null) return;

        var manager = Minecraft.getInstance().getSoundManager();
        long aliveFor = System.currentTimeMillis() - activeStartedAt;

        // A track advances only when the sound engine reports that the stream really ended.
        // The startup grace prevents asynchronous streamed OGGs from being mistaken for a finished track.
        if (fadeState != FadeState.OUT && aliveFor > STARTUP_GRACE_MS && !manager.isActive(active)) {
            active = null;
            fadeGain = 0.0F;
            startNext(true);
            return;
        }

        if (fadeState == FadeState.IN) {
            fadeGain = Math.min(1.0F, fadeGain + 1.0F / FADE_TICKS);
            if (fadeGain >= 0.999F) {
                fadeGain = 1.0F;
                fadeState = FadeState.NONE;
            }
            applyLiveVolume();
        } else if (fadeState == FadeState.OUT) {
            fadeGain = Math.max(0.0F, fadeGain - 1.0F / FADE_TICKS);
            applyLiveVolume();
            if (fadeGain <= 0.001F) {
                manager.stop(active);
                active = null;
                fadeGain = 0.0F;
                startNext(true);
            }
        }
    }

    public static void ensurePlaying() {
        Minecraft minecraft = Minecraft.getInstance();
        if (!shouldPlay()) {
            stop();
            return;
        }

        // SIEGE owns only the no-world menu music channel. Gameplay music is never touched.
        minecraft.getMusicManager().stopPlaying();
        if (active == null) startNext(true);
        else applyLiveVolume();
    }

    /** Manual skip uses a real fade-out before starting the next shuffled track. */
    public static void nextTrack() {
        if (!SiegeConfig.music) {
            SiegeConfig.music = true;
            SiegeConfig.save();
        }
        if (!shouldPlay()) return;
        if (active == null) {
            startNext(true);
            return;
        }
        if (fadeState != FadeState.OUT) fadeState = FadeState.OUT;
    }

    /** Applies the custom SIEGE volume to the active stream without restarting it. */
    public static void setVolumeLive(int percent) {
        SiegeConfig.musicVolume = SiegeConfig.clampVolume(percent);
        applyLiveVolume();
    }

    /** Backwards-compatible alias for older callers. No restart occurs anymore. */
    public static void refreshVolume() {
        applyLiveVolume();
    }

    public static String currentTrackName() {
        return previous >= 0 && previous < TRACK_NAMES.size() ? TRACK_NAMES.get(previous) : "--";
    }

    public static String transitionLabel(boolean spanish) {
        return switch (fadeState) {
            case IN -> spanish ? "ENTRADA SUAVE" : "FADING IN";
            case OUT -> spanish ? "CAMBIO SUAVE" : "FADING OUT";
            case NONE -> spanish ? "REPRODUCCIÓN COMPLETA" : "FULL TRACK";
        };
    }

    private static boolean shouldPlay() {
        Minecraft minecraft = Minecraft.getInstance();
        return SiegeConfig.music
                && SiegeConfig.musicVolume > 0
                && minecraft.level == null
                && minecraft.screen != null;
    }

    private static void startNext(boolean fadeIn) {
        if (!shouldPlay()) return;
        if (queue.isEmpty()) refillQueue();
        int next = queue.remove(0);
        previous = next;
        playIndex(next, fadeIn);
    }

    private static void playIndex(int index, boolean fadeIn) {
        Minecraft minecraft = Minecraft.getInstance();
        var manager = minecraft.getSoundManager();
        if (active != null) manager.stop(active);

        active = SimpleSoundInstance.forMusic(TRACKS.get(index).get());
        fadeGain = fadeIn ? 0.0F : 1.0F;
        fadeState = fadeIn ? FadeState.IN : FadeState.NONE;
        ownsMusicSource = true;
        applyLiveVolume();
        manager.play(active);
        activeStartedAt = System.currentTimeMillis();
    }

    private static void applyLiveVolume() {
        if (!ownsMusicSource) return;
        Minecraft minecraft = Minecraft.getInstance();
        float configured = SiegeConfig.clampVolume(SiegeConfig.musicVolume) / 100.0F;
        minecraft.getSoundManager().updateSourceVolume(SoundSource.MUSIC, configured * Math.max(0.0F, Math.min(1.0F, fadeGain)));
    }

    private static void restoreVanillaMusicVolume() {
        if (!ownsMusicSource) return;
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().updateSourceVolume(SoundSource.MUSIC, minecraft.options.getSoundSourceVolume(SoundSource.MUSIC));
        ownsMusicSource = false;
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
        fadeGain = 0.0F;
        fadeState = FadeState.NONE;
        restoreVanillaMusicVolume();
    }

    private enum FadeState { NONE, IN, OUT }
}
