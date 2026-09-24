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
    private record TrackDef(RegistryObject<SoundEvent> sound, String key, String name,
                            long fallbackDurationMs, boolean required) { }

    /**
     * SIEGE 5.60 keeps the five existing approved tracks and accepts exactly two new
     * player-approved songs. The commercial additions only become visible when a
     * prepared OGG master is actually present in the JAR, so incomplete builds never
     * offer a silent/broken menu entry.
     */
    private static final List<TrackDef> ALL_TRACKS = List.of(
            new TrackDef(SiegeMod.TALE_CRUEL_WORLD, "tale_cruel_world", "Tale of a Cruel World", 261_534L, true),
            new TrackDef(SiegeMod.DARKEST_OF_DAYS, "darkest_of_days", "Darkest of Days", 281_934L, true),
            new TrackDef(SiegeMod.KAPTAIN_MUSIC_BOX, "kaptain_music_box", "Kaptain Music Box", 139_969L, true),
            new TrackDef(SiegeMod.HEAVENS_GIFT, "heavens_hell_sent_gift", "Heaven's Hell-Sent Gift", 217_214L, true),
            new TrackDef(SiegeMod.ARC_ENEMY, "arc_enemy", "Arc - Enemy · Potoe", 180_000L, true),
            new TrackDef(SiegeMod.A_STRANGER_I_REMAIN, "a_stranger_i_remain",
                    "A Stranger I Remain (Maniac Agenda Mix)", 145_000L, false),
            new TrackDef(SiegeMod.RECEIVE_YOU_HYPERACTIVE, "receive_you_the_hyperactive",
                    "Receive You The Hyperactive", 288_000L, false)
    );

    private static final List<TrackDef> TRACKS = ALL_TRACKS.stream()
            .filter(track -> track.required() || hasPreparedAudio(track.key()))
            .toList();
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

    private static boolean hasPreparedAudio(String key) {
        return SiegeMusic.class.getClassLoader()
                .getResource("assets/siege/sounds/music/" + key + ".ogg") != null;
    }

    /**
     * A pinned optional track may disappear between builds when its commercial master
     * is not supplied. Heal that persisted selection back to shuffle instead of leaving
     * the UI claiming that a non-existent track is pinned.
     */
    private static void sanitizeSelection() {
        if (SiegeConfig.selectedTrack >= TRACKS.size()) {
            SiegeConfig.selectedTrack = -1;
            SiegeConfig.save();
        }
    }

    public static boolean shuffleEnabled() {
        return SiegeConfig.selectedTrack < 0 || SiegeConfig.selectedTrack >= TRACKS.size();
    }

    public static int pinnedTrackNumber() {
        return shuffleEnabled() ? 0 : SiegeConfig.selectedTrack + 1;
    }

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
        sanitizeSelection();
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
        sanitizeSelection();
        requestedNext = -1;
        if (SiegeConfig.selectedTrack >= 0 && !TRACKS.isEmpty()) {
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
        sanitizeSelection();
        if (TRACKS.isEmpty()) return;
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

    public static List<String> trackNames() {
        return TRACKS.stream().map(TrackDef::name).toList();
    }

    public static int currentTrackNumber() { return previous < 0 ? 0 : previous + 1; }

    public static float currentProgress() {
        long total = currentDurationMs();
        return total <= 0L ? 0.0F : Math.max(0.0F, Math.min(1.0F, currentElapsedMs() / (float) total));
    }

    public static long currentElapsedMs() {
        if (!clockAnchored || active == null) return 0L;
        return Math.max(0L, Math.min(currentDurationMs(), (System.nanoTime() / 1_000_000L) - playbackAnchorAt));
    }

    public static String currentTimeLabel() {
        return formatTime(currentElapsedMs()) + " / " + formatTime(currentDurationMs());
    }

    private static String formatTime(long millis) {
        long totalSeconds = Math.max(0L, millis) / 1_000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format(java.util.Locale.ROOT, "%d:%02d", minutes, seconds);
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
        String wanted = name.trim();
        for (int i = 0; i < TRACKS.size(); i++) {
            if (TRACKS.get(i).name().equalsIgnoreCase(wanted)) {
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
        return previous >= 0 && previous < TRACKS.size() ? TRACKS.get(previous).name() : "--";
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
        return Math.max(0L, currentDurationMs() - currentElapsedMs());
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
        sanitizeSelection();
        if (!shouldPlay() || TRACKS.isEmpty()) return;
        int next;
        if (requestedNext >= 0 && requestedNext < TRACKS.size()) {
            next = requestedNext;
            requestedNext = -1;
        } else if (SiegeConfig.selectedTrack >= 0 && SiegeConfig.selectedTrack < TRACKS.size()) {
            next = SiegeConfig.selectedTrack;
        } else {
            if (queue.isEmpty()) refillQueue();
            if (queue.isEmpty()) return;
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
        RegistryObject<SoundEvent> sound = TRACKS.get(index).sound();
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
        long[] values = new long[TRACKS.size()];
        for (int i = 0; i < TRACKS.size(); i++) values[i] = TRACKS.get(i).fallbackDurationMs();

        Properties props = new Properties();
        try (InputStream in = SiegeMusic.class.getClassLoader()
                .getResourceAsStream("assets/siege/music_durations.properties")) {
            if (in == null) return values;
            props.load(in);
            for (int i = 0; i < TRACKS.size(); i++) {
                String raw = props.getProperty(TRACKS.get(i).key());
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
