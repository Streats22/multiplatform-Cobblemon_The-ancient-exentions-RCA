"""Bake Shiny Charm item sprite (32x32)."""
from __future__ import annotations

from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError:
    raise SystemExit("Install Pillow: pip install pillow")

ROOT = Path(__file__).resolve().parents[1]
OUT = (
    ROOT
    / "common"
    / "src"
    / "main"
    / "resources"
    / "assets"
    / "ancient_extensions"
    / "textures"
    / "item"
    / "shiny_charm.png"
)

CYAN = (120, 220, 255)
CYAN_HI = (210, 245, 255)
CORD = (36, 44, 120)
BEAD = (96, 220, 72)
BEAD_HI = (180, 255, 140)
INK = (18, 18, 28)


def star(draw: ImageDraw.ImageDraw, cx: int, cy: int, r: int, fill, outline=INK) -> None:
    points = []
    for i in range(8):
        angle = i * 45 - 90
        import math

        rad = math.radians(angle)
        dist = r if i % 2 == 0 else r // 2
        points.append((cx + int(math.cos(rad) * dist), cy + int(math.sin(rad) * dist)))
    draw.polygon(points, fill=fill, outline=outline)


def main() -> None:
    img = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # cord loop
    draw.arc((12, 0, 20, 8), 200, 340, fill=CORD, width=2)
    draw.line((16, 6, 16, 9), fill=CORD, width=2)

    # top bead
    draw.ellipse((14, 9, 18, 13), fill=BEAD, outline=INK)
    draw.point((15, 10), fill=BEAD_HI)

    # star body
    star(draw, 16, 18, 8, CYAN)
    draw.polygon([(16, 12), (18, 16), (16, 20), (14, 16)], fill=CYAN_HI, outline=None)

    # bottom bead
    draw.ellipse((14, 23, 18, 27), fill=BEAD, outline=INK)
    draw.point((15, 24), fill=BEAD_HI)

    # tassel
    draw.line((16, 27, 13, 31), fill=CORD, width=2)
    draw.line((16, 27, 16, 31), fill=CORD, width=2)
    draw.line((16, 27, 19, 31), fill=CORD, width=2)

    OUT.parent.mkdir(parents=True, exist_ok=True)
    img.save(OUT)
    print(f"wrote {OUT}")


if __name__ == "__main__":
    main()
