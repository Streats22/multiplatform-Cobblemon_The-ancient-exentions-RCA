"""Build detailed 32x32 Poké Ball pouch item icons (one-time asset bake)."""
from __future__ import annotations

from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError:
    raise SystemExit("Install Pillow: pip install pillow")

ROOT = Path(__file__).resolve().parents[1] / "common" / "src" / "main" / "resources" / "assets" / "ancient_extensions" / "textures" / "item"
SIZE = 32

# Leather palette
OUTLINE = (42, 28, 16, 255)
SHADOW = (58, 38, 22, 255)
LEATHER = (92, 58, 32, 255)
LEATHER_MID = (118, 76, 42, 255)
LEATHER_LIGHT = (148, 102, 58, 255)
HIGHLIGHT = (186, 138, 82, 255)
STITCH = (64, 44, 26, 255)
STRING = (196, 188, 168, 255)
STRING_SHADOW = (120, 112, 96, 255)
TRIM_GOLD = (201, 162, 39, 255)
TRIM_GOLD_HI = (232, 200, 74, 255)
TRIM_MASTER = (120, 52, 168, 255)


def rgba(c: tuple[int, int, int]) -> tuple[int, int, int, int]:
    return (*c, 255)


def set_px(img: Image.Image, x: int, y: int, color: tuple[int, int, int, int]) -> None:
    if 0 <= x < SIZE and 0 <= y < SIZE:
        img.putpixel((x, y), color)


def fill_rect(img: Image.Image, x0: int, y0: int, x1: int, y1: int, color: tuple[int, int, int, int]) -> None:
    draw = ImageDraw.Draw(img)
    draw.rectangle((x0, y0, x1, y1), fill=color)


def draw_stitches(img: Image.Image, x: int, y: int, length: int, vertical: bool = True) -> None:
    for i in range(0, length, 2):
        px = x if vertical else x + i
        py = y + i if vertical else y
        set_px(img, px, py, STITCH)
        if not vertical:
            set_px(img, px, py + 1, STITCH)


def draw_string_loop(img: Image.Image) -> None:
    # Left strap
    for y in range(4, 11):
        set_px(img, 11, y, STRING_SHADOW)
        set_px(img, 12, y, STRING)
    # Right strap
    for y in range(4, 11):
        set_px(img, 19, y, STRING_SHADOW)
        set_px(img, 20, y, STRING)
    # Bow knot
    fill_rect(img, 13, 3, 18, 4, STRING_SHADOW)
    fill_rect(img, 14, 2, 17, 3, STRING)
    set_px(img, 15, 1, STRING)
    set_px(img, 16, 1, STRING)


def draw_pouch_body(img: Image.Image, trim: tuple[int, int, int, int] | None = None) -> None:
    # Outer silhouette with outline
    body = [
        "....oooooooooooo....",
        "...oLLLLLLLLLLLLLo...",
        "..oLLLLLLLLLLLLLLLo..",
        ".oLLLLLLLLLLLLLLLLLo.",
        "oLLLLLLLLLLLLLLLLLLLo",
        "oLLLLLLLLLLLLLLLLLLLo",
        "oLLLLLLLLLLLLLLLLLLLo",
        "oLLLLLLLLLLLLLLLLLLLo",
        "oLLLLLLLLLLLLLLLLLLLo",
        "oLLLLLLLLLLLLLLLLLLLo",
        "oLLLLLLLLLLLLLLLLLLLo",
        "oLLLLLLLLLLLLLLLLLLLo",
        "oLLLLLLLLLLLLLLLLLLLo",
        ".oLLLLLLLLLLLLLLLLLo.",
        "..oLLLLLLLLLLLLLLLo..",
        "...oSSSSSSSSSSSSSo...",
        "....oooooooooooo....",
    ]
    palette = {
        ".": (0, 0, 0, 0),
        "o": OUTLINE,
        "L": LEATHER_MID,
        "S": SHADOW,
    }
    ox, oy = 7, 10
    for row_idx, row in enumerate(body):
        for col_idx, ch in enumerate(row):
            set_px(img, ox + col_idx, oy + row_idx, palette[ch])

    # Shading — left highlight, right shadow, top flap
    for y in range(11, 14):
        for x in range(9, 13):
            set_px(img, x, y, LEATHER_LIGHT)
    for y in range(11, 25):
        set_px(img, 23, y, SHADOW)
        set_px(img, 24, y, OUTLINE)
    for x in range(9, 24):
        set_px(img, x, 11, HIGHLIGHT)

    # Flap seam
    for x in range(9, 24):
        set_px(img, x, 14, OUTLINE)

    # Inner pocket shading + leather grain
    for y in range(15, 24):
        for x in range(10, 23):
            tone = LEATHER if (x + y) % 3 == 0 else LEATHER_MID
            set_px(img, x, y, tone)
            if (x * y) % 7 == 0:
                set_px(img, x, y, SHADOW)

    # Small clasp on flap
    fill_rect(img, 15, 12, 17, 13, TRIM_GOLD_HI)
    set_px(img, 16, 12, TRIM_GOLD)

    # Corner highlights
    for d in range(3):
        set_px(img, 9 + d, 12 + d, HIGHLIGHT)
        set_px(img, 10 + d, 12, LEATHER_LIGHT)

    # Stitching
    draw_stitches(img, 9, 15, 10)
    draw_stitches(img, 23, 15, 10)
    for x in range(10, 23, 2):
        set_px(img, x, 24, STITCH)

    if trim is not None:
        for x in range(9, 24):
            set_px(img, x, 12, trim)
            set_px(img, x, 25, trim)
        for y in range(12, 25):
            set_px(img, 9, y, trim)
            set_px(img, 23, y, trim)


def draw_ball(
    img: Image.Image,
    cx: int,
    cy: int,
    top: tuple[int, int, int],
    bottom: tuple[int, int, int],
    button: tuple[int, int, int] = (240, 240, 240),
    ring: tuple[int, int, int] = (28, 28, 28),
    accents: list[tuple[int, int, tuple[int, int, int]]] | None = None,
) -> None:
    r = 6
    top_rgba = rgba(top)
    bottom_rgba = rgba(bottom)
    ring_rgba = rgba(ring)
    button_rgba = rgba(button)
    outline = OUTLINE

    for dy in range(-r - 1, r + 2):
        for dx in range(-r - 1, r + 2):
            dist = dx * dx + dy * dy
            x, y = cx + dx, cy + dy
            if dist > (r + 1) * (r + 1):
                continue
            if dist > r * r:
                set_px(img, x, y, outline)
            elif abs(dy) <= 0:
                set_px(img, x, y, ring_rgba)
            elif dy < 0:
                set_px(img, x, y, top_rgba)
            else:
                set_px(img, x, y, bottom_rgba)

    # Highlight on top hemisphere
    set_px(img, cx - 2, cy - 3, rgba((min(255, top[0] + 40), min(255, top[1] + 40), min(255, top[2] + 40))))
    set_px(img, cx - 1, cy - 4, rgba((min(255, top[0] + 60), min(255, top[1] + 60), min(255, top[2] + 60))))
    set_px(img, cx - 3, cy - 2, rgba((min(255, top[0] + 20), min(255, top[1] + 20), min(255, top[2] + 20))))

    # Button ring + center
    for dy in range(-2, 3):
        for dx in range(-2, 3):
            if dx * dx + dy * dy <= 4:
                set_px(img, cx + dx, cy + dy, ring_rgba)
    for dy in range(-1, 2):
        for dx in range(-1, 2):
            if dx * dx + dy * dy <= 2:
                set_px(img, cx + dx, cy + dy, button_rgba)

    if accents:
        for ax, ay, color in accents:
            set_px(img, cx + ax, cy + ay, rgba(color))
            set_px(img, cx + ax + 1, cy + ay, rgba((min(255, color[0] + 30), min(255, color[1] + 30), min(255, color[2] + 30))))


TIERS = {
    "pokeball_pouch": {
        "ball": ((204, 44, 44), (244, 244, 244)),
        "trim": None,
        "accents": None,
    },
    "pokeball_pouch_great": {
        "ball": ((44, 92, 204), (244, 244, 244)),
        "trim": None,
        "accents": None,
    },
    "pokeball_pouch_ultra": {
        "ball": ((24, 24, 24), (248, 208, 32)),
        "trim": TRIM_GOLD,
        "accents": [(-2, -3, (255, 236, 96)), (2, -3, (255, 236, 96))],
    },
    "pokeball_pouch_master": {
        "ball": ((112, 48, 176), (248, 96, 176)),
        "trim": TRIM_MASTER,
        "accents": [(-3, -3, (255, 120, 200)), (3, -3, (255, 120, 200))],
    },
}


def build_icon(name: str, top: tuple[int, int, int], bottom: tuple[int, int, int], trim, accents) -> None:
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    draw_string_loop(img)
    draw_pouch_body(img, trim=trim)
    draw_ball(img, 16, 19, top, bottom, accents=accents)
    img.save(ROOT / f"{name}.png")


def main() -> None:
    ROOT.mkdir(parents=True, exist_ok=True)
    for name, spec in TIERS.items():
        top, bottom = spec["ball"]
        build_icon(name, top, bottom, spec["trim"], spec["accents"])
        print(f"wrote {ROOT / (name + '.png')}")


if __name__ == "__main__":
    main()
