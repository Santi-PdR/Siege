#!/usr/bin/env python3
"""Repair Ogg page CRCs without changing packet payloads.

Some recovered owner-supplied SIEGE masters contain intact Ogg pages whose stored
checksums were damaged by an earlier binary transfer. FFmpeg correctly rejects
those pages and therefore only sees the first ~20 seconds. This tool scans every
Ogg page, recomputes the standard Ogg CRC, preserves the original audio payload
byte-for-byte, and reports the physical stream coverage before conversion.
"""
from __future__ import annotations

import argparse
import struct
from pathlib import Path

POLY = 0x04C11DB7


def make_table() -> list[int]:
    table: list[int] = []
    for i in range(256):
        r = i << 24
        for _ in range(8):
            r = ((r << 1) ^ POLY) & 0xFFFFFFFF if (r & 0x80000000) else (r << 1) & 0xFFFFFFFF
        table.append(r)
    return table


TABLE = make_table()


def ogg_crc(page: bytes) -> int:
    crc = 0
    for b in page:
        crc = ((crc << 8) & 0xFFFFFFFF) ^ TABLE[((crc >> 24) & 0xFF) ^ b]
    return crc


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("source", type=Path)
    parser.add_argument("target", type=Path)
    args = parser.parse_args()

    data = bytearray(args.source.read_bytes())
    cursor = 0
    pages = 0
    mismatches = 0
    gaps = 0
    serials: set[int] = set()
    max_granule = 0
    last_end = 0

    while cursor < len(data):
        pos = data.find(b"OggS", cursor)
        if pos < 0:
            break
        if pos != cursor and cursor != 0:
            gaps += 1
        if pos + 27 > len(data):
            break
        version = data[pos + 4]
        if version != 0:
            cursor = pos + 4
            continue
        segments = data[pos + 26]
        header_end = pos + 27 + segments
        if header_end > len(data):
            break
        body_len = sum(data[pos + 27:header_end])
        page_end = header_end + body_len
        if page_end > len(data):
            break

        serial = struct.unpack_from("<I", data, pos + 14)[0]
        granule = struct.unpack_from("<Q", data, pos + 6)[0]
        stored = struct.unpack_from("<I", data, pos + 22)[0]
        serials.add(serial)
        if granule != 0xFFFFFFFFFFFFFFFF:
            max_granule = max(max_granule, granule)

        page = bytearray(data[pos:page_end])
        page[22:26] = b"\0\0\0\0"
        calculated = ogg_crc(page)
        if calculated != stored:
            mismatches += 1
            struct.pack_into("<I", data, pos + 22, calculated)

        pages += 1
        last_end = page_end
        cursor = page_end

    if pages == 0:
        raise SystemExit(f"No Ogg pages found in {args.source}")

    args.target.parent.mkdir(parents=True, exist_ok=True)
    args.target.write_bytes(data)
    approx_seconds = max_granule / 48000.0 if max_granule else 0.0
    trailing = len(data) - last_end
    print(
        f"SIEGE Ogg repair: {args.source.name}: pages={pages} crc_fixed={mismatches} "
        f"gaps={gaps} serials={len(serials)} max_granule={max_granule} "
        f"approx={approx_seconds:.3f}s trailing={trailing}B"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
