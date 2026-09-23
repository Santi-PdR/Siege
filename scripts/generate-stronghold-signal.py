#!/usr/bin/env python3
"""Generate SIEGE's original menu track: Stronghold 5-5 // Black Signal.

The track is synthesized deterministically during CI. It intentionally avoids external
music dependencies: slow military-industrial drones, distant impacts, radio noise and a
small late-track alarm motif create a restrained 2044 command-room atmosphere without
copying an existing soundtrack.
"""
from __future__ import annotations

import sys
import wave
from pathlib import Path

import numpy as np

RATE = 44_100
DURATION = 132.0
SEED = 0x5349454745  # "SIEGE"


def add_burst(buffer: np.ndarray, start_s: float, duration_s: float, signal: np.ndarray) -> None:
    start = max(0, int(start_s * RATE))
    end = min(buffer.size, start + signal.size, start + int(duration_s * RATE))
    if end > start:
        buffer[start:end] += signal[: end - start]


def decaying_tone(freq: float, duration_s: float, decay: float, amplitude: float,
                   overtone: float = 0.0) -> np.ndarray:
    n = max(1, int(duration_s * RATE))
    x = np.arange(n, dtype=np.float32) / RATE
    tone = np.sin(2.0 * np.pi * freq * x)
    if overtone > 0.0:
        tone += 0.35 * np.sin(2.0 * np.pi * overtone * x + 0.7)
    return (tone * np.exp(-decay * x) * amplitude).astype(np.float32)


def main() -> None:
    if len(sys.argv) != 2:
        raise SystemExit("usage: generate-stronghold-signal.py OUTPUT.wav")

    target = Path(sys.argv[1])
    target.parent.mkdir(parents=True, exist_ok=True)

    samples = int(DURATION * RATE)
    t = np.arange(samples, dtype=np.float32) / RATE
    rng = np.random.default_rng(SEED)

    # Persistent low-frequency machinery. Modulation keeps the drone alive without
    # turning it into a melody that competes with menu reading.
    slow = 0.70 + 0.30 * np.sin(2.0 * np.pi * 0.021 * t + 0.3)
    drone = (
        0.145 * np.sin(2.0 * np.pi * 43.65 * t)
        + 0.070 * np.sin(2.0 * np.pi * 65.41 * t + 0.5)
        + 0.035 * np.sin(2.0 * np.pi * 87.31 * t + 1.2)
    ) * slow

    # Cold minor pad, introduced gradually after the command-room opening.
    pad_env = np.clip((t - 9.0) / 18.0, 0.0, 1.0)
    pad_env *= 0.72 + 0.28 * np.sin(2.0 * np.pi * 0.012 * t)
    pad = (
        0.030 * np.sin(2.0 * np.pi * 110.00 * t + 0.2)
        + 0.026 * np.sin(2.0 * np.pi * 130.81 * t + 1.0)
        + 0.020 * np.sin(2.0 * np.pi * 164.81 * t + 2.2)
    ) * pad_env

    events = np.zeros(samples, dtype=np.float32)

    # Distant industrial pulse: deliberate and heavy rather than dance-like.
    for index, when in enumerate(np.arange(15.0, DURATION - 8.0, 2.75)):
        amp = 0.115 if index % 4 == 0 else 0.074
        hit = decaying_tone(52.0, 1.25, 4.2, amp, 104.0)
        add_burst(events, float(when), 1.25, hit)

    # Sparse metallic relay / hangar impacts.
    for index, when in enumerate(np.arange(23.0, DURATION - 6.0, 13.0)):
        freq = 690.0 + (index % 3) * 115.0
        clang = decaying_tone(freq, 1.65, 3.1, 0.036, freq * 1.47)
        add_burst(events, float(when), 1.65, clang)

    # A small alarm-like motif only in the final third so the track develops without
    # becoming a constant siren. Notes form a descending, unresolved warning figure.
    motif_notes = (196.00, 174.61, 146.83, 130.81)
    for index, when in enumerate(np.arange(76.0, DURATION - 8.0, 4.0)):
        note = motif_notes[index % len(motif_notes)]
        motif = decaying_tone(note, 2.3, 1.45, 0.030, note * 2.0)
        add_burst(events, float(when), 2.3, motif)

    # Radio/static texture. Slow gates create short communication-like swells without
    # synthesizing speech or intelligible messages.
    noise = rng.normal(0.0, 1.0, samples).astype(np.float32)
    radio_gate = np.clip(
        0.18
        + 0.42 * np.sin(2.0 * np.pi * 0.047 * t + 1.7)
        + 0.32 * np.sin(2.0 * np.pi * 0.083 * t),
        0.0,
        1.0,
    )
    radio = noise * radio_gate * 0.010

    mono = (drone + pad + events + radio).astype(np.float32)

    # Slow stereo movement. Events remain mostly centered while atmosphere drifts.
    pan = 0.5 + 0.19 * np.sin(2.0 * np.pi * 0.0085 * t)
    left = mono * np.sqrt(1.0 - pan)
    right = mono * np.sqrt(pan)
    left += 0.010 * np.sin(2.0 * np.pi * 220.0 * t + 0.9) * pad_env
    right += 0.010 * np.sin(2.0 * np.pi * 220.0 * t + 1.4) * pad_env

    stereo = np.column_stack((left, right)).astype(np.float32)

    # Smooth beginning/end and conservative peak. prepare-music.sh applies another
    # -3 dB headroom stage before the final Vorbis asset.
    fade_in = np.clip(t / 6.0, 0.0, 1.0)
    fade_out = np.clip((DURATION - t) / 10.0, 0.0, 1.0)
    stereo *= np.minimum(fade_in, fade_out)[:, None]

    peak = float(np.max(np.abs(stereo)))
    if peak <= 0.0:
        raise SystemExit("generated soundtrack is silent")
    stereo *= 0.48 / peak

    pcm = np.clip(stereo * 32767.0, -32768, 32767).astype("<i2")
    with wave.open(str(target), "wb") as wav:
        wav.setnchannels(2)
        wav.setsampwidth(2)
        wav.setframerate(RATE)
        wav.writeframes(pcm.tobytes())

    print(f"SIEGE generated: {target} · {DURATION:.1f}s · {RATE} Hz stereo · peak-normalized")


if __name__ == "__main__":
    main()
