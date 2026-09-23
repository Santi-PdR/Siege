#!/usr/bin/env python3
"""Generate two original SIEGE 5.50 menu tracks.

Nucleus // Silent Carrier is restrained electronic ambience for reading/briefing.
Tesla Breach is a heavier electrical-industrial cue for Nusia/Tesla threat screens.
Both are deterministic, speech-free and generated from synthesis/noise only.
"""
from __future__ import annotations

import sys
import wave
from pathlib import Path

import numpy as np

RATE = 44_100
SEED = 0x550534745


def write_stereo(path: Path, stereo: np.ndarray, duration: float) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    peak = float(np.max(np.abs(stereo)))
    if peak <= 0.0:
        raise SystemExit(f"generated soundtrack is silent: {path}")
    stereo = stereo * (0.48 / peak)
    pcm = np.clip(stereo * 32767.0, -32768, 32767).astype("<i2")
    with wave.open(str(path), "wb") as wav:
        wav.setnchannels(2)
        wav.setsampwidth(2)
        wav.setframerate(RATE)
        wav.writeframes(pcm.tobytes())
    print(f"SIEGE generated: {path} · {duration:.1f}s · {RATE} Hz stereo")


def fade(stereo: np.ndarray, duration: float, fade_in: float, fade_out: float) -> np.ndarray:
    t = np.arange(stereo.shape[0], dtype=np.float32) / RATE
    env_in = np.clip(t / fade_in, 0.0, 1.0)
    env_out = np.clip((duration - t) / fade_out, 0.0, 1.0)
    return stereo * np.minimum(env_in, env_out)[:, None]


def add(buffer: np.ndarray, start_s: float, signal: np.ndarray) -> None:
    start = max(0, int(start_s * RATE))
    end = min(buffer.size, start + signal.size)
    if end > start:
        buffer[start:end] += signal[: end - start]


def tone(freq: float, seconds: float, amp: float, decay: float = 0.0, phase: float = 0.0) -> np.ndarray:
    n = max(1, int(seconds * RATE))
    t = np.arange(n, dtype=np.float32) / RATE
    env = np.exp(-decay * t) if decay > 0.0 else 1.0
    return (np.sin(2.0 * np.pi * freq * t + phase) * env * amp).astype(np.float32)


def silent_carrier() -> tuple[np.ndarray, float]:
    duration = 116.0
    samples = int(duration * RATE)
    t = np.arange(samples, dtype=np.float32) / RATE
    rng = np.random.default_rng(SEED ^ 0xA1)

    drift = 0.68 + 0.32 * np.sin(2.0 * np.pi * 0.014 * t + 0.4)
    low = (
        0.120 * np.sin(2.0 * np.pi * 36.71 * t)
        + 0.062 * np.sin(2.0 * np.pi * 55.00 * t + 0.8)
        + 0.028 * np.sin(2.0 * np.pi * 73.42 * t + 1.7)
    ) * drift

    carrier_gate = 0.5 + 0.5 * np.sin(2.0 * np.pi * 0.009 * t - 1.0)
    carrier = (
        0.014 * np.sin(2.0 * np.pi * 293.66 * t + 0.3)
        + 0.010 * np.sin(2.0 * np.pi * 440.00 * t + 1.2)
    ) * carrier_gate

    events = np.zeros(samples, dtype=np.float32)

    pattern = (0.0, 0.16, 0.42, 0.58, 1.02)
    for group, when in enumerate(np.arange(12.0, duration - 8.0, 11.5)):
        base = 880.0 + (group % 3) * 110.0
        for pulse_index, offset in enumerate(pattern):
            freq = base + (pulse_index % 2) * 190.0
            add(events, float(when + offset), tone(freq, 0.11, 0.022, decay=17.0, phase=0.4))

    # Relay thumps use separate event layers so different decay lengths never rely
    # on NumPy broadcasting between unlike arrays.
    for index, when in enumerate(np.arange(19.0, duration - 7.0, 8.0)):
        add(events, float(when), tone(48.0 + (index % 2) * 5.0, 1.1, 0.055, decay=4.6))
        add(events, float(when), tone(96.0, 0.8, 0.018, decay=6.0, phase=0.8))

    control = rng.normal(0.0, 1.0, int(duration * 6) + 3).astype(np.float32)
    slow_noise = np.interp(
        np.arange(samples, dtype=np.float32),
        np.linspace(0, samples - 1, control.size, dtype=np.float32),
        control,
    ).astype(np.float32)
    grain = rng.normal(0.0, 1.0, samples).astype(np.float32)
    atmosphere = slow_noise * 0.007 + grain * (0.0025 + 0.0025 * carrier_gate)

    mono = (low + carrier + events + atmosphere).astype(np.float32)
    pan = 0.5 + 0.16 * np.sin(2.0 * np.pi * 0.0067 * t)
    left = mono * np.sqrt(1.0 - pan)
    right = mono * np.sqrt(pan)
    stereo = np.column_stack((left, right)).astype(np.float32)
    return fade(stereo, duration, 7.0, 9.0), duration


def tesla_breach() -> tuple[np.ndarray, float]:
    duration = 104.0
    samples = int(duration * RATE)
    t = np.arange(samples, dtype=np.float32) / RATE
    rng = np.random.default_rng(SEED ^ 0xB2)

    movement = 0.75 + 0.25 * np.sin(2.0 * np.pi * 0.026 * t)
    low = (
        0.118 * np.sin(2.0 * np.pi * 49.00 * t)
        + 0.050 * np.sin(2.0 * np.pi * 73.50 * t + 0.9)
        + 0.024 * np.sin(2.0 * np.pi * 98.00 * t + 1.5)
    ) * movement

    events = np.zeros(samples, dtype=np.float32)

    for index, when in enumerate(np.arange(9.0, duration - 6.0, 2.15)):
        amp = 0.075 if index % 4 else 0.115
        add(events, float(when), tone(58.0, 0.9, amp, decay=5.2))
        add(events, float(when), tone(174.0, 0.55, amp * 0.26, decay=8.0, phase=0.5))

    for index, when in enumerate(np.arange(15.0, duration - 5.0, 5.6)):
        seconds = 0.44 + 0.08 * (index % 3)
        n = int(seconds * RATE)
        local_t = np.arange(n, dtype=np.float32) / RATE
        start_f = 1100.0 + rng.uniform(-160.0, 220.0)
        end_f = 260.0 + rng.uniform(-50.0, 120.0)
        sweep = start_f + (end_f - start_f) * (local_t / max(seconds, 0.001))
        phase = 2.0 * np.pi * np.cumsum(sweep) / RATE
        env = np.exp(-7.5 * local_t)
        chirp = np.sin(phase).astype(np.float32) * env * 0.036
        chirp += rng.normal(0.0, 1.0, n).astype(np.float32) * env * 0.013
        add(events, float(when + rng.uniform(-0.35, 0.35)), chirp.astype(np.float32))

    for index, when in enumerate(np.arange(55.0, duration - 7.0, 9.3)):
        freq = 520.0 + (index % 4) * 87.0
        add(events, float(when), tone(freq, 1.9, 0.026, decay=2.8))
        add(events, float(when), tone(freq * 1.51, 1.6, 0.016, decay=3.2, phase=0.7))

    grain = rng.normal(0.0, 1.0, samples).astype(np.float32)
    static_gate = np.clip(0.25 + 0.35 * np.sin(2.0 * np.pi * 0.071 * t + 0.3), 0.0, 1.0)
    mono = (low + events + grain * static_gate * 0.0042).astype(np.float32)

    pan = 0.5 + 0.21 * np.sin(2.0 * np.pi * 0.0105 * t + 0.8)
    left = mono * np.sqrt(1.0 - pan)
    right = mono * np.sqrt(pan)

    coil_env = np.clip((t - 18.0) / 15.0, 0.0, 1.0) * 0.011
    left += np.sin(2.0 * np.pi * 235.0 * t + 0.3) * coil_env
    right += np.sin(2.0 * np.pi * 242.0 * t + 1.2) * coil_env
    stereo = np.column_stack((left, right)).astype(np.float32)
    return fade(stereo, duration, 5.0, 8.0), duration


def main() -> None:
    if len(sys.argv) != 3:
        raise SystemExit("usage: generate-frontline-signal-550.py NUCLEUS.wav TESLA.wav")
    nucleus_path = Path(sys.argv[1])
    tesla_path = Path(sys.argv[2])
    nucleus, nucleus_duration = silent_carrier()
    tesla, tesla_duration = tesla_breach()
    write_stereo(nucleus_path, nucleus, nucleus_duration)
    write_stereo(tesla_path, tesla, tesla_duration)


if __name__ == "__main__":
    main()
