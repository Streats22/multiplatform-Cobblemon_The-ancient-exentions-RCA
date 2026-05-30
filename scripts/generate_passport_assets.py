"""Generate regional passport GUI atlas (item icon is a hand-authored PNG)."""
from __future__ import annotations

from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError:
    raise SystemExit("Install Pillow: pip install pillow")

ROOT = Path(__file__).resolve().parents[1] / "common" / "src" / "main" / "resources" / "assets" / "ancient_extensions" / "textures"


def rgb(c: str) -> tuple[int, int, int]:
    c = c.lstrip("#")
    return tuple(int(c[i : i + 2], 16) for i in (0, 2, 4))


def lighten(color: tuple[int, int, int], factor: float) -> tuple[int, int, int]:
    return tuple(max(0, min(255, int(v + (255 - v) * factor))) for v in color)


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
    gui_dir = ROOT / "gui"
    gui_dir.mkdir(parents=True, exist_ok=True)
    draw_gui_atlas(gui_dir / "regional_passport.png")
    print("Generated passport GUI texture.")


if __name__ == "__main__":
    main()
