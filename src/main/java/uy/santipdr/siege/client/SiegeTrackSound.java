package uy.santipdr.siege.client;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/**
 * Dedicated SIEGE menu track instance.
 *
 * It deliberately uses MASTER rather than Minecraft's MUSIC category so the
 * SIEGE soundtrack is controlled only by its own slider (while still obeying
 * the user's master volume). Because it is tickable, its volume can change
 * every client tick without stopping/restarting the OGG stream.
 */
public final class SiegeTrackSound extends AbstractTickableSoundInstance {
    public SiegeTrackSound(SoundEvent sound) {
        super(sound, SoundSource.MASTER, RandomSource.create());
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.looping = false;
        this.delay = 0;
        this.pitch = 1.0F;
        this.volume = 0.0F;
    }

    public void setGain(float gain) {
        this.volume = Float.isFinite(gain) ? Math.max(0.0F, Math.min(1.0F, gain)) : 0;
    }

    @Override
    public void tick() {
        // Playback is streamed by Minecraft; SiegeMusic only changes gain/state.
    }

    @Override
    public boolean canStartSilent() {
        // Fade-in starts at 0.0. Without this, the sound engine may discard the
        // stream before the first fade tick and the track appears to never play.
        return true;
    }
}

