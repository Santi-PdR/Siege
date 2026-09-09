# SIEGE soundtrack source masters

These files are the clean owner-supplied OGG uploads from the SIEGE chat on 2026-09-09. They replace the historically damaged ~786 KiB blobs that only contained about 22-25 seconds of valid audio.

Expected source files and SHA-256 fingerprints:

- `tale_cruel_world.ogg` — `96d66d0c0a645279764d42f787f4bb1244b25aa0dc595623a75b40c0c7573b35` — source duration ~261.540 s
- `darkest_of_days.ogg` — `b5797d634e6a25ce58469fca11a58b0fa3e015d167ed75c0646729283d2a7060` — source duration ~281.940 s
- `dvn_lobby_music.ogg` — `61a498de84b0052f0cf359eda84de624af91d18b0a0248cad3755006f52f2928` — source duration ~539.540 s
- `heavens_hell_sent_gift.ogg` — `24ffffa0537ae9a9483bf36596ec760d8f52305e2daf9d7140c2097ab4e62412` — source duration ~217.220 s

The DVN source is a compilation. Only `Kaptain - Music Box` is packaged: `179.599646s <= t < 319.568250s` (about 139.969 s). The other three sources must be encoded in full with no time trim.

`scripts/prepare-music.sh` validates source duration, performs the DVN extraction, converts to Ogg Vorbis for Minecraft 1.20.1, measures the final encoded durations and generates `music_durations.properties`. Natural playback fade begins 8 seconds before those measured final durations.
