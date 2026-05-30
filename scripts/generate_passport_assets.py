"""Generate regional passport item + GUI textures."""
from __future__ import annotations

from pathlib import Path

try:
    from PIL import Image, ImageDraw, ImageFont
except ImportError:
    raise SystemExit("Install Pillow: pip install pillow")

ROOT = Path(__file__).resolve().parents[1] / "common" / "src" / "main" / "resources" / "assets" / "ancient_extensions" / "textures"


def rgb(c: str) -> tuple[int, int, int]:
    c = c.lstrip("#")
    return tuple(int(c[i : i + 2], 16) for i in (0, 2, 4))


def darken(color: tuple[int, int, int], factor: float) -> tuple[int, int, int]:
    return tuple(max(0, min(255, int(v * factor))) for v in color)


def lighten(color: tuple[int, int, int], factor: float) -> tuple[int, int, int]:
    return tuple(max(0, min(255, int(v + (255 - v) * factor))) for v in color)


def draw_passport_item(path: Path) -> None:
    size = 32
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)

    leather = rgb("#4a2f18")
    leather_hi = rgb("#7a5030")
    leather_lo = rgb("#2d1a0c")
    gold = rgb("#d4af37")
    gold_hi = rgb("#f0d070")
    gold_lo = rgb("#9a7a1a")
    page = rgb("#f7eed8")
    page_edge = rgb("#c8b080")
    seal = rgb("#b83030")
    seal_hi = rgb("#e05050")

    # Soft shadow
    d.rectangle((6, 7, 28, 28), fill=(0, 0, 0, 50))

    # Page stack (right edge)
    d.rectangle((20, 6, 28, 27), fill=page_edge)
    d.rectangle((21, 7, 27, 26), fill=page)
    d.line([(21, 7), (21, 26)], fill=rgb("#e8dcc0"), width=1)

    # Leather cover
    d.rectangle((5, 5, 22, 28), fill=leather_lo)
    d.rectangle((6, 6, 21, 27), fill=leather)
    d.line([(6, 6), (21, 6)], fill=leather_hi, width=1)
    d.line([(6, 6), (6, 27)], fill=leather_hi, width=1)
    d.line([(21, 6), (21, 27)], fill=leather_lo, width=1)

    # Stitching dots
    for y in range(8, 26, 2):
        d.point((7, y), fill=gold_lo)
        d.point((20, y), fill=gold_lo)

    # Gold frame
    d.rectangle((8, 8, 19, 24), outline=gold, width=1)
    d.rectangle((9, 9, 18, 23), outline=gold_lo, width=1)

    # RSA emboss (pixel letters)
    letters = [
        "01110",
        "10001",
        "11110",
        "10001",
        "10001",
    ]
    lx, ly = 10, 11
    for row, pattern in enumerate(letters):
        for col, ch in enumerate(pattern):
            if ch == "1":
                d.point((lx + col, ly + row), fill=gold_hi)
                d.point((lx + col, ly + row + 6), fill=gold_hi)

    # Survey compass mark
    cx, cy = 13, 21
    d.ellipse((cx - 2, cy - 2, cx + 2, cy + 2), outline=gold_hi, width=1)
    d.point((cx, cy - 2), fill=gold_hi)
    d.point((cx, cy + 2), fill=gold_hi)
    d.point((cx - 2, cy), fill=gold_hi)
    d.point((cx + 2, cy), fill=gold_hi)

    # Wax seal
    d.ellipse((15, 20, 22, 27), fill=seal)
    d.ellipse((16, 21, 21, 26), fill=seal_hi)
    d.ellipse((17, 22, 20, 25), fill=seal)
    d.point((18, 23), fill=gold_hi)

    img.save(path)


def draw_passport_cover_texture(path: Path) -> None:
    """32x32 cover face for 3D item model."""
    draw_passport_item(path)


def draw_passport_page_texture(path: Path) -> None:
    img = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    page = rgb("#f7eed8")
    edge = rgb("#c8b888")
    d.rectangle((0, 0, 31, 31), fill=edge)
    d.rectangle((1, 1, 30, 30), fill=page)
    for y in range(4, 28, 4):
        d.line([(3, y), (28, y)], fill=rgb("#e8dcc0"), width=1)
    img.save(path)


def draw_passport_seal_texture(path: Path) -> None:
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    seal = rgb("#a83232")
    seal_hi = rgb("#d85050")
    d.ellipse((1, 1, 14, 14), fill=seal)
    d.ellipse((3, 2, 13, 12), fill=seal_hi)
    d.ellipse((5, 4, 11, 10), fill=seal)
    img.save(path)


def draw_gui_atlas(path: Path) -> None:
    size = 256
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)

    leather = rgb("#3d2914")
    leather_mid = rgb("#5c3d1e")
    gold = rgb("#c9a227")
    gold_hi = rgb("#e8c84a")
    parchment = rgb("#f4e8c8")
    parchment_dark = rgb("#e8d4a8")
    parchment_line = rgb("#d4c098")
    ink = rgb("#3d2e24")

    pw, ph = 220, 200

    # Drop shadow
    d.rectangle((3, 3, pw + 5, ph + 5), fill=(0, 0, 0, 60))

    # Outer leather cover
    d.rectangle((0, 0, pw, ph), fill=leather)
    d.rectangle((2, 2, pw - 2, ph - 2), fill=leather_mid)

    # Gold trim
    d.rectangle((4, 4, pw - 4, ph - 4), outline=gold, width=1)
    d.rectangle((5, 5, pw - 5, ph - 5), outline=gold_hi, width=1)

    # Inner parchment
    d.rectangle((8, 8, pw - 8, ph - 8), fill=parchment)

    # Header band
    d.rectangle((10, 10, pw - 10, 28), fill=parchment_dark)
    d.line([(10, 28), (pw - 10, 28)], fill=gold, width=1)

    # Photo well (top-left)
    d.rectangle((16, 34, 58, 76), fill=rgb("#d8c8a0"))
    d.rectangle((17, 35, 57, 75), fill=rgb("#ebe0c8"))
    d.rectangle((18, 36, 56, 74), outline=gold, width=1)

    # Corner flourishes
    for ox, oy in ((10, 10), (pw - 18, 10), (10, ph - 18), (pw - 18, ph - 18)):
        d.line([(ox, oy), (ox + 6, oy)], fill=gold, width=1)
        d.line([(ox, oy), (ox, oy + 6)], fill=gold, width=1)

    # Stats panel bottom — light inset, not dark block
    d.rectangle((12, 128, pw - 12, ph - 12), fill=rgb("#efe3c4"))
    d.rectangle((12, 128, pw - 12, ph - 12), outline=parchment_line, width=1)
    d.line([(14, 140), (pw - 14, 140)], fill=gold, width=1)

    # Center divider (passport only — subtle)
    d.line([(pw // 2, 32), (pw // 2, 118)], fill=parchment_line, width=1)

    # Decorative subtitle lines (stats panel area only)
    for y in (108, 112, 116):
        d.line([(14, y), (pw - 14, y)], fill=parchment_line, width=1)

    # --- Sprites below main panel (y=200) ---

    # Wax blob 0,200 48x48
    wx, wy = 0, 200
    for r in range(24, 0, -1):
        t = r / 24.0
        col = (
            int(168 * t + 80 * (1 - t)),
            int(50 * t + 20 * (1 - t)),
            int(50 * t + 20 * (1 - t)),
            255,
        )
        d.ellipse((wx + 24 - r, wy + 24 - r, wx + 24 + r, wy + 24 + r), fill=col)
    d.ellipse((wx + 14, wy + 12, wx + 34, wy + 32), fill=(220, 90, 90, 180))

    # Ring 48,200 56x56
    rx, ry = 48, 200
    for i in range(3):
        rad = 26 - i * 4
        c = lighten(gold, 0.15 * i)
        d.ellipse((rx + 28 - rad, ry + 28 - rad, rx + 28 + rad, ry + 28 + rad), outline=c, width=2)

    # Photo frame sprite 104,200 40x44
    fx, fy = 104, 200
    d.rectangle((fx, fy, fx + 39, fy + 43), fill=leather_mid)
    d.rectangle((fx + 2, fy + 2, fx + 37, fy + 41), fill=gold)
    d.rectangle((fx + 4, fy + 4, fx + 35, fy + 39), fill=parchment_dark)
    d.rectangle((fx + 6, fy + 6, fx + 33, fy + 37), fill=rgb("#ebe0c8"))
    d.rectangle((fx + 6, fy + 6, fx + 33, fy + 37), outline=gold, width=1)
    # Corner ticks
    for px, py in ((fx + 6, fy + 6), (fx + 30, fy + 6), (fx + 6, fy + 34), (fx + 30, fy + 34)):
        d.point((px, py), fill=gold_hi)

    img.save(path)


def main() -> None:
    item_dir = ROOT / "item"
    gui_dir = ROOT / "gui"
    item_dir.mkdir(parents=True, exist_ok=True)
    gui_dir.mkdir(parents=True, exist_ok=True)

    draw_passport_item(item_dir / "regional_passport.png")
    draw_passport_cover_texture(item_dir / "regional_passport_cover.png")
    draw_passport_page_texture(item_dir / "regional_passport_page.png")
    draw_passport_seal_texture(item_dir / "regional_passport_seal.png")
    draw_gui_atlas(gui_dir / "regional_passport.png")
    print("Generated passport textures.")


if __name__ == "__main__":
    main()
