package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import uy.santipdr.siege.SiegeMod;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/** Menu soundtrack controller. Never owns audio while a world/server is loaded. */
public final class SiegeMusic {
    /**
     * Only tracks explicitly approved for normal playback live in this list.
     * Experimental/generated tracks stay out until they have been previewed and accepted.
     */
    private static final List<RegistryObject<SoundEvent>> TRACKS = List.of(
            SiegeMod.TALE_CRUEL_WORLD,
            SiegeMod.DARKEST_OF_DAYS,
            SiegeMod.KAPTAIN_MUSIC_BOX,
            SiegeMod.HEAVENS_GIFT,
            SiegeMod.ARC_ENEMY
    );
    private static final List<String> TRACK_KEYS = List.of(
            "tale_cruel_world",
            "darkest_of_days",
            "kaptain_music_box",
            "heavens_hell_sent_gift",
            "arc_enemy"
    );
    private static final List<String> TRACK_NAMES = List.of(
            "Tale of a Cruel World",
            "Darkest of Days",
            "Kaptain Music Box",
            "Heaven's Hell-Sent Gift",
            "Arc - Enemy · Potoe"
    );

    private static final long[] FALLBACK_DURATIONS_MS = {
            261_534L, 281_934L, 139_969L, 217_214L, 180_000L
    };
    private static final long[] TRACK_DURATIONS_MS = loadDurations();
    private static final List<Integer> queue = new ArrayList<>();

    private static final long NATURAL_FADE_OUT_MS = 8_000L;
    private static final long MANUAL_FADE_OUT_MS = 1_250L;
    private static final long FADE_IN_MS = 2_200L;
    public static final long TRACK_ANNOUNCEMENT_MS = 8_500L;

    private static SiegeTrackSound active;
    private static final SiegeAudioHealth health = new SiegeAudioHealth();
    private static int previous = -1;
    private static int beforePrevious = -1;
    private static int requestedNext = -1;
    private static long startRequestedAt;
    private static long announcementStartedAt;
    private static long playbackAnchorAt;
    private static boolean clockAnchored;
    private static float fadeGain;
    private static float fadeFromGain;
    private static long fadeStartedAt;
    private static long fadeDurationMs;
    private static boolean naturalFadeOut;
    private static FadeState fadeState = FadeState.NONE;
    private static int maintenanceTicks;

    private SiegeMusic() { }

    public static void tick() {
        if (!shouldPlay()) {
            stop();
            return;
        }

        ensurePlaying();
        if (active == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        var manager = minecraft.getSoundManager();
        long now = (System.nanoTime() / 1_000_000L);

        boolean backendPlaying = manager.isActive(active);
        if (!clockAnchored && backendPlaying) {
            playbackAnchorAt = now; clockAnchored = true; announcementStartedAt = now;
        }
        if (fadeState != FadeState.OUT && health.recover(now, backendPlaying)) {
            manager.stop(active);
            active = new SiegeTrackSound(resolveTrack(previous));
            startRequestedAt = now; clockAnchored = false; announcementStartedAt = 0;
            fadeGain = 0; fadeState = FadeState.IN; fadeStartedAt = now; fadeDurationMs = FADE_IN_MS;
            manager.play(active);
            return;
        }

        maintenanceTicks++;
        if (maintenanceTicks >= 100) {
            minecraft.getMusicManager().stopPlaying();
            maintenanceTicks = 0;
        }

        long elapsed = clockAnchored ? Math.max(0L, now - playbackAnchorAt) : 0L;
        long duration = currentDurationMs();

        if (clockAnchored && fadeState != FadeState.OUT && duration > 0L
                && elapsed >= Math.max(0L, duration - NATURAL_FADE_OUT_MS)) {
            beginFadeOut(NATURAL_FADE_OUT_MS, true);
        }

        if (fadeState == FadeState.IN) {
            if (!clockAnchored) { fadeStartedAt = now; applyLiveVolume(); return; }
            float progress = progress(now, fadeStartedAt, fadeDurationMs);
            fadeGain = progress;
            applyLiveVolume();
            if (progress >= 1.0F) {
                fadeGain = 1.0F;
                fadeState = FadeState.NONE;
            }
        } else if (fadeState == FadeState.OUT) {
            float progress = progress(now, fadeStartedAt, fadeDurationMs);
            fadeGain = Math.max(0.0F, fadeFromGain * (1.0F - progress));
            applyLiveVolume();
            if (progress >= 1.0F || (naturalFadeOut && clockAnchored && elapsed >= duration)) {
                manager.stop(active);
                active = null;
                fadeGain = 0.0F;
                startNext(true);
            }
        } else {
            fadeGain = 1.0F;
            applyLiveVolume();
        }
    }

    public static void ensurePlaying() {
        if (!shouldPlay()) {
            stop();
            return;
        }

        if (active == null) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
            maintenanceTicks = 0;
            startNext(true);
        } else {
            applyLiveVolume();
        }
    }

    public static void nextTrack() {
        requestedNext = -1;
        if (SiegeConfig.selectedTrack >= 0) {
            SiegeConfig.selectedTrack = (SiegeConfig.selectedTrack + 1) % TRACKS.size();
            SiegeConfig.save();
        }
        if (!SiegeConfig.music) {
            SiegeConfig.music = true;
            SiegeConfig.save();
        }
        if (!shouldPlay()) return;
        if (active == null) {
            startNext(true);
            return;
        }
        beginFadeOut(MANUAL_FADE_OUT_MS, false);
    }

    public static void previousTrack() {
        int fallback = previous < 0 ? 0 : Math.floorMod(previous - 1, TRACKS.size());
        requestedNext = beforePrevious >= 0 ? beforePrevious : fallback;
        if (SiegeConfig.selectedTrack >= 0) { SiegeConfig.selectedTrack = requestedNext; SiegeConfig.save(); }
        enableMusicAndTransition();
    }

    public static void restartTrack() {
        if (previous < 0) {
            SiegeConfig.music = true; SiegeConfig.save();
            ensurePlaying();
            return;
        }
        requestedNext = previous;
        enableMusicAndTransition();
    }

    private static void enableMusicAndTransition() {
        if (!SiegeConfig.music) {
            SiegeConfig.music = true;
            SiegeConfig.save();
        }
        if (!shouldPlay()) return;
        if (active == null) startNext(true);
        else beginFadeOut(MANUAL_FADE_OUT_MS, false);
    }

    public static List<String> trackNames() { return TRACK_NAMES; }
    public static int currentTrackNumber() { return previous < 0 ? 0 : previous + 1; }
    public static float currentProgress() {
        long total = currentDurationMs();
        return total <= 0L ? 0.0F : Math.max(0.0F, Math.min(1.0F, (total - currentRemainingMs()) / (float) total));
    }

    public static void selectTrack(int index) {
        if (index < -1 || index >= TRACKS.size()) return;
        requestedNext = -1;
        SiegeConfig.selectedTrack = index;
        if (index >= 0) queue.clear();
        if (index >= 0) SiegeConfig.music = true;
        SiegeConfig.save();
        if (!shouldPlay()) return;
        if (active == null) startNext(true);
        else if (index >= 0 && (previous != index || fadeState == FadeState.OUT))
            beginFadeOut(MANUAL_FADE_OUT_MS, false);
    }

    public static boolean selectTrackByName(String name) {
        if (name == null) return false;
        for (int i = 0; i < TRACK_NAMES.size(); i++) {
            if (TRACK_NAMES.get(i).equalsIgnoreCase(name.trim())) {
                selectTrack(i);
                return true;
            }
        }
        return false;
    }

    public static void setVolumeLive(int percent) {
        SiegeConfig.musicVolume = SiegeConfig.clampVolume(percent);
        applyLiveVolume();
    }

    public static void refreshVolume() { applyLiveVolume(); }

    public static String currentTrackName() {
        return previous >= 0 && previous < TRACK_NAMES.size() ? TRACK_NAMES.get(previous) : "--";
    }

    public static long trackAnnouncementAgeMs() {
        if (active == null || announcementStartedAt <= 0L) return -1L;
        long age = Math.max(0L, (System.nanoTime() / 1_000_000L) - announcementStartedAt);
        return age <= trackAnnouncementDurationMs() ? age : -1L;
    }

    public static long trackAnnouncementDurationMs() {
        return Math.max(3_000L, SiegeConfig.trackNoticeSeconds * 1_000L);
    }

    public static boolean isActuallyPlaying() {
        return active != null && Minecraft.getInstance().getSoundManager().isActive(active);
    }

    public static long currentDurationMs() {
        if (previous < 0 || previous >= TRACK_DURATIONS_MS.length) return 0L;
        return TRACK_DURATIONS_MS[previous];
    }

    public static long currentRemainingMs() {
        if (!clockAnchored || active == null) return currentDurationMs();
        return Math.max(0L, currentDurationMs() - ((System.nanoTime() / 1_000_000L) - playbackAnchorAt));
    }

    public static String transitionLabel(boolean spanish) {
        if (!SiegeConfig.music) return spanish ? "MÚSICA DESACTIVADA" : "MUSIC OFF";
        if (SiegeConfig.musicVolume == 0) return spanish ? "SILENCIADA" : "MUTED";
        if (!isActuallyPlaying()) return spanish ? "ESPERANDO AUDIO" : "WAITING FOR AUDIO";
        return switch (fadeState) {
            case IN -> spanish ? "ENTRADA SUAVE" : "FADING IN";
            case OUT -> naturalFadeOut
                    ? (spanish ? "FADE FINAL · 8S" : "FINAL FADE · 8S")
                    : (spanish ? "CAMBIO SUAVE" : "FADING OUT");
            case NONE -> spanish ? "PISTA COMPLETA" : "FULL TRACK";
        };
    }

    private static boolean shouldPlay() {
        Minecraft minecraft = Minecraft.getInstance();
        return SiegeConfig.music && minecraft.level == null && minecraft.screen != null
                && !(minecraft.screen instanceof net.minecraft.client.gui.screens.ConnectScreen)
                && !(minecraft.screen instanceof net.minecraft.client.gui.screens.ReceivingLevelScreen);
    }

    private static void startNext(boolean fadeIn) {
        if (!shouldPlay()) return;
        int next;
        if (requestedNext >= 0 && requestedNext < TRACKS.size()) {
            next = requestedNext;
            requestedNext = -1;
        } else if (SiegeConfig.selectedTrack >= 0 && SiegeConfig.selectedTrack < TRACKS.size()) {
            next = SiegeConfig.selectedTrack;
        } else {
            if (queue.isEmpty()) refillQueue();
            next = queue.remove(0);
        }
        if (previous != next) beforePrevious = previous;
        previous = next;
        playIndex(next, fadeIn);
    }

    private static void playIndex(int index, boolean fadeIn) {
        Minecraft minecraft = Minecraft.getInstance();
        var manager = minecraft.getSoundManager();
        if (active != null) manager.stop(active);

        active = new SiegeTrackSound(resolveTrack(index));
        startRequestedAt = (System.nanoTime() / 1_000_000L);
        announcementStartedAt = 0L;
        health.begin(startRequestedAt);
        playbackAnchorAt = startRequestedAt;
        clockAnchored = false;
        naturalFadeOut = false;
        fadeFromGain = 1.0F;
        fadeGain = fadeIn ? 0.0F : 1.0F;
        fadeState = fadeIn ? FadeState.IN : FadeState.NONE;
        fadeStartedAt = startRequestedAt;
        fadeDurationMs = fadeIn ? FADE_IN_MS : 1L;
        applyLiveVolume();
        manager.play(active);
    }

    private static SoundEvent resolveTrack(int index) {
        RegistryObject<SoundEvent> sound = TRACKS.get(index);
        if (sound.isPresent()) return sound.get();
        return SoundEvent.createVariableRangeEvent(sound.getId());
    }

    private static void beginFadeOut(long durationMs, boolean natural) {
        if (active == null) return;
        if (fadeState == FadeState.OUT && naturalFadeOut == natural) return;
        fadeFromGain = Math.max(0.0F, Math.min(1.0F, fadeGain));
        if (fadeState == FadeState.NONE) fadeFromGain = 1.0F;
        fadeState = FadeState.OUT;
        fadeStartedAt = (System.nanoTime() / 1_000_000L);
        fadeDurationMs = Math.max(1L, durationMs);
        naturalFadeOut = natural;
    }

    private static float progress(long now, long start, long duration) {
        if (duration <= 0L) return 1.0F;
        return Math.max(0.0F, Math.min(1.0F, (now - start) / (float) duration));
    }

    private static void applyLiveVolume() {
        if (active == null) return;
        float configured = SiegeConfig.clampVolume(SiegeConfig.musicVolume) / 100.0F;
        active.setGain(configured * Math.max(0.0F, Math.min(1.0F, fadeGain)));
    }

    private static void refillQueue() {
        queue.clear();
        for (int i = 0; i < TRACKS.size(); i++) queue.add(i);
        Collections.shuffle(queue);
        if (queue.size() > 1 && queue.get(0) == previous) Collections.swap(queue, 0, 1);
    }

    private static long[] loadDurations() {
        long[] values = FALLBACK_DURATIONS_MS.clone();
        Properties props = new Properties();
        try (InputStream in = SiegeMusic.class.getClassLoader()
                .getResourceAsStream("assets/siege/music_durations.properties")) {
            if (in == null) return values;
            props.load(in);
            for (int i = 0; i < TRACK_KEYS.size(); i++) {
                String raw = props.getProperty(TRACK_KEYS.get(i));
                if (raw == null) continue;
                try {
                    long parsed = Long.parseLong(raw.trim());
                    if (parsed > 1_000L && parsed <= 3_600_000L) values[i] = parsed;
                } catch (NumberFormatException ignored) { }
            }
        } catch (Exception ignored) { }
        return values;
    }

    public static void stop() {
        if (active != null) Minecraft.getInstance().getSoundManager().stop(active);
        active = null;
        fadeGain = 0.0F;
        fadeFromGain = 0.0F;
        fadeState = FadeState.NONE;
        naturalFadeOut = false;
        maintenanceTicks = 0;
        announcementStartedAt = 0L;
        clockAnchored = false;
        requestedNext = -1;
    }

    private enum FadeState { NONE, IN, OUT }
}
