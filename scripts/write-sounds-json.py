#!/usr/bin/env python3
"""Write assets/siege/sounds.json from the audio files present in this build.

Required menu tracks and UI effects are always registered. Optional commercial tracks
are registered only when their prepared OGG exists, so a normal public build never
advertises a Minecraft sound resource that is not actually packaged in the JAR.
"""
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSET_ROOT = ROOT / "src/main/resources/assets/siege"
MUSIC_DIR = ASSET_ROOT / "sounds/music"
TARGET = ASSET_ROOT / "sounds.json"

REQUIRED_MUSIC = (
    "tale_cruel_world",
    "darkest_of_days",
    "kaptain_music_box",
    "heavens_hell_sent_gift",
    "arc_enemy",
)
OPTIONAL_MUSIC = (
    "a_stranger_i_remain",
    "receive_you_the_hyperactive",
)
UI_SOUNDS = ("hover", "click", "back", "track")


def music_entry(key: str) -> dict:
    return {"sounds": [{"name": f"siege:music/{key}", "stream": True}]}


def ui_entry(key: str) -> dict:
    return {"sounds": [{"name": f"siege:ui/{key}"}]}


def main() -> None:
    missing_required = [key for key in REQUIRED_MUSIC if not (MUSIC_DIR / f"{key}.ogg").is_file()]
    if missing_required:
        raise SystemExit("Missing required prepared music: " + ", ".join(missing_required))

    payload: dict[str, dict] = {}
    for key in REQUIRED_MUSIC:
        payload[f"music.{key}"] = music_entry(key)

    installed_optional: list[str] = []
    for key in OPTIONAL_MUSIC:
        if (MUSIC_DIR / f"{key}.ogg").is_file():
            payload[f"music.{key}"] = music_entry(key)
            installed_optional.append(key)

    for key in UI_SOUNDS:
        payload[f"ui.{key}"] = ui_entry(key)

    TARGET.write_text(json.dumps(payload, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    optional_text = ", ".join(installed_optional) if installed_optional else "none"
    print(f"SIEGE sounds.json: {len(REQUIRED_MUSIC)} required music tracks; optional installed: {optional_text}")


if __name__ == "__main__":
    main()
