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


def draw_wax_seal_sprite(d: ImageDraw.ImageDraw, ox: int, oy: int, gold, gold_hi) -> None:
    """56×56 wax seal — shadow, wax body, embossed gold rim, inner dish (badge drawn in-game)."""
    size = 56
    cx = ox + size // 2
    cy = oy + size // 2

    wax_dark = (120, 28, 32, 255)
    wax_mid = (168, 42, 46, 255)
    wax_hi = (210, 72, 76, 255)
    wax_edge = (90, 18, 22, 255)

    # Drop shadow on parchment
    d.ellipse((cx - 20, cy - 16, cx + 22, cy + 24), fill=(40, 24, 16, 70))

    # Irregular wax pool (slightly oval, hand-stamped feel)
    d.ellipse((cx - 23, cy - 21, cx + 23, cy + 21), fill=wax_edge)
    d.ellipse((cx - 22, cy - 20, cx + 22, cy + 20), fill=wax_dark)
    d.ellipse((cx - 20, cy - 18, cx + 20, cy + 18), fill=wax_mid)

    # Wax highlights — light catch top-left
    d.pieslice((cx - 18, cy - 20, cx + 10, cy + 8), start=200, end=320, fill=wax_hi)
    d.ellipse((cx - 10, cy - 14, cx - 2, cy - 6), fill=(230, 110, 110, 200))

    # Small drip accents
    d.ellipse((cx + 16, cy + 14, cx + 21, cy + 19), fill=wax_mid)
    d.ellipse((cx - 19, cy + 10, cx - 14, cy + 15), fill=(140, 34, 38, 220))

    # Embossed gold rope ring
    for ring_r, col, width in (
        (19, gold_hi, 3),
        (18, gold, 2),
        (17, (140, 100, 28, 255), 1),
    ):
        d.ellipse(
            (cx - ring_r, cy - ring_r, cx + ring_r, cy + ring_r),
            outline=col,
            width=width,
        )

    # Inner pressed dish (where region code sits)
    d.ellipse((cx - 14, cy - 13, cx + 14, cy + 13), fill=(145, 36, 40, 255))
    d.ellipse((cx - 12, cy - 11, cx + 12, cy + 11), fill=(175, 50, 52, 255))

    # Subtle star / official mark hint in wax
    star = (220, 170, 90, 180)
    d.line([(cx, cy - 6), (cx, cy + 6)], fill=star, width=1)
    d.line([(cx - 5, cy - 3), (cx + 5, cy + 3)], fill=star, width=1)
    d.line([(cx - 5, cy + 3), (cx + 5, cy - 3)], fill=star, width=1)


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

    draw_wax_seal_sprite(d, 0, 200, gold, gold_hi)

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
