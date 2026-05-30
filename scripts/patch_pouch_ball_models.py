#!/usr/bin/env python3
"""Patch pouch block models — reduce z-fighting on embedded Poké Ball geometry."""

from __future__ import annotations

import json
from pathlib import Path

MODELS = Path(__file__).resolve().parents[1] / (
    "common/src/main/resources/assets/ancient_extensions/models/block"
)

# Slightly separated hemispheres; no coplanar equator faces between inner/outer shells.
BALL_ELEMENTS = [
    {
        "name": "ball_bottom_inner",
        "from": [6.72, 2.98, 6.52],
        "to": [9.28, 4.24, 9.08],
        "faces": {
            "north": {"uv": [10, 4, 12, 6], "texture": "#ball", "shade": False},
            "east": {"uv": [8, 4, 10, 6], "texture": "#ball", "shade": False},
            "south": {"uv": [14, 4, 16, 6], "texture": "#ball", "shade": False},
            "west": {"uv": [12, 4, 14, 6], "texture": "#ball", "shade": False},
            "down": {"uv": [14, 0, 12, 4], "texture": "#ball", "shade": False},
        },
    },
    {
        "name": "ball_bottom_outer",
        "from": [6.48, 2.74, 6.28],
        "to": [9.52, 4.32, 9.32],
        "faces": {
            "north": {"uv": [10, 14, 12, 16], "texture": "#ball", "shade": False},
            "east": {"uv": [8, 14, 10, 16], "texture": "#ball", "shade": False},
            "south": {"uv": [14, 14, 16, 16], "texture": "#ball", "shade": False},
            "west": {"uv": [12, 14, 14, 16], "texture": "#ball", "shade": False},
            "down": {"uv": [14, 10, 12, 14], "texture": "#ball", "shade": False},
        },
    },
    {
        "name": "ball_top_inner",
        "from": [6.72, 4.28, 6.52],
        "to": [9.28, 5.58, 9.08],
        "faces": {
            "north": {"uv": [2, 4, 4, 6], "texture": "#ball", "shade": False},
            "east": {"uv": [0, 4, 2, 6], "texture": "#ball", "shade": False},
            "south": {"uv": [6, 4, 8, 6], "texture": "#ball", "shade": False},
            "west": {"uv": [4, 4, 6, 6], "texture": "#ball", "shade": False},
            "up": {"uv": [4, 4, 2, 0], "texture": "#ball", "shade": False},
        },
    },
    {
        "name": "ball_top_outer",
        "from": [6.48, 4.14, 6.28],
        "to": [9.52, 5.72, 9.32],
        "faces": {
            "north": {"uv": [2, 14, 4, 16], "texture": "#ball", "shade": False},
            "east": {"uv": [0, 14, 2, 16], "texture": "#ball", "shade": False},
            "south": {"uv": [6, 14, 8, 16], "texture": "#ball", "shade": False},
            "west": {"uv": [4, 14, 6, 16], "texture": "#ball", "shade": False},
            "up": {"uv": [4, 14, 2, 10], "texture": "#ball", "shade": False},
            "down": {"uv": [6, 10, 4, 14], "texture": "#ball", "shade": False},
        },
    },
    {
        "name": "ball_button",
        "from": [7.66, 3.92, 9.30],
        "to": [8.34, 4.6, 9.38],
        "faces": {
            "north": {"uv": [0, 0, 0.5, 1], "texture": "#ball", "shade": False},
            "east": {"uv": [0, 0, 0, 1], "texture": "#ball", "shade": False},
            "south": {"uv": [0.5, 0, 1, 1], "texture": "#ball", "shade": False},
            "west": {"uv": [0.5, 0, 0.5, 1], "texture": "#ball", "shade": False},
            "up": {"uv": [0.5, 0, 0, 0], "texture": "#ball", "shade": False},
            "down": {"uv": [1, 0, 0.5, 0], "texture": "#ball", "shade": False},
        },
    },
]

BALL_NAMES = {element["name"] for element in BALL_ELEMENTS}


def patch_model(path: Path) -> None:
    data = json.loads(path.read_text(encoding="utf-8"))
    kept = [element for element in data["elements"] if element.get("name") not in BALL_NAMES]
    data["elements"] = kept + BALL_ELEMENTS
    path.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
    print(f"Patched {path.name}")


def main() -> None:
    for name in (
        "pokeball_pouch.json",
        "pokeball_pouch_great.json",
        "pokeball_pouch_ultra.json",
        "pokeball_pouch_master.json",
    ):
        patch_model(MODELS / name)


if __name__ == "__main__":
    main()
