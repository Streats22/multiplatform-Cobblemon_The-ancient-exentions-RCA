"""Shared 16x16 pixel-art helpers for Minecraft item textures."""
from __future__ import annotations

from PIL import Image


def render_grid(grid: list[str], palette: dict[str, tuple[int, int, int, int]], size: int = 16) -> Image.Image:
    if len(grid) != size:
        raise ValueError(f"Grid must have {size} rows, got {len(grid)}")
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    px = img.load()
    for y, row in enumerate(grid):
        if len(row) != size:
            raise ValueError(f"Row {y} must have {size} columns, got {len(row)!r}")
        for x, ch in enumerate(row):
            if ch not in palette:
                raise KeyError(f"Unknown palette key {ch!r} at ({x}, {y})")
            px[x, y] = palette[ch]
    return img
