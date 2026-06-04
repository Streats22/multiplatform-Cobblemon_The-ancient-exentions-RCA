"""Bake journal/chart footer widget sprites (claim + page nav)."""
from __future__ import annotations

from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError:
    raise SystemExit("Install Pillow: pip install pillow")

OUT = (
    Path(__file__).resolve().parents[1]
    / "common"
    / "src"
    / "main"
    / "resources"
    / "assets"
    / "ancient_extensions"
    / "textures"
    / "gui"
    / "regional_survey_journal_widgets.png"
)

W, H = 256, 32
CLAIM_W = 128
NAV_W = 22
BTN_H = 16

# Journal leather / gold palette (matches regional_survey_journal.png)
FRAME = (58, 38, 22)
LEATHER = (118, 82, 48)
LEATHER_LIGHT = (168, 124, 78)
LEATHER_HI = (214, 178, 120)
GOLD = (196, 152, 58)
GOLD_LIGHT = (236, 204, 110)
GOLD_SHADOW = (108, 72, 28)
INK = (42, 28, 18)
ARROW = (240, 224, 196)


def rounded_panel(draw: ImageDraw.ImageDraw, box: tuple[int, int, int, int], fill, outline, radius: int = 3) -> None:
    draw.rounded_rectangle(box, radius=radius, fill=fill, outline=outline, width=1)


def draw_claim_strip(img: Image.Image, y: int, hovered: bool) -> None:
    draw = ImageDraw.Draw(img)
    base = GOLD_LIGHT if hovered else GOLD
    inner = LEATHER_HI if hovered else LEATHER_LIGHT
    rounded_panel(draw, (0, y, CLAIM_W - 1, y + BTN_H - 1), inner, FRAME)
    rounded_panel(draw, (2, y + 2, CLAIM_W - 3, y + BTN_H - 3), base, GOLD_SHADOW)
    # embossed center band
    draw.rectangle((6, y + 5, CLAIM_W - 7, y + BTN_H - 6), fill=GOLD_LIGHT if hovered else GOLD)
    draw.line((8, y + 6, CLAIM_W - 9, y + 6), fill=GOLD_LIGHT, width=1)
    draw.line((8, y + BTN_H - 7, CLAIM_W - 9, y + BTN_H - 7), fill=GOLD_SHADOW, width=1)


def draw_nav_button(img: Image.Image, x: int, y: int, next_button: bool, hovered: bool) -> None:
    draw = ImageDraw.Draw(img)
    fill = LEATHER_LIGHT if hovered else LEATHER
    rounded_panel(draw, (x, y, x + NAV_W - 1, y + BTN_H - 1), fill, FRAME)
    rounded_panel(draw, (x + 2, y + 2, x + NAV_W - 3, y + BTN_H - 3), LEATHER_HI if hovered else LEATHER_LIGHT, GOLD_SHADOW)

    cx = x + NAV_W // 2
    cy = y + BTN_H // 2
    if next_button:
        points = [(cx - 3, cy - 5), (cx + 4, cy), (cx - 3, cy + 5)]
    else:
        points = [(cx + 3, cy - 5), (cx - 4, cy), (cx + 3, cy + 5)]
    draw.polygon(points, fill=ARROW, outline=INK)


def main() -> None:
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw_claim_strip(img, 0, hovered=False)
    draw_claim_strip(img, BTN_H, hovered=True)
    draw_nav_button(img, 112, 0, next_button=False, hovered=False)
    draw_nav_button(img, 112, BTN_H, next_button=False, hovered=True)
    draw_nav_button(img, 134, 0, next_button=True, hovered=False)
    draw_nav_button(img, 134, BTN_H, next_button=True, hovered=True)

    OUT.parent.mkdir(parents=True, exist_ok=True)
    img.save(OUT)
    print(f"wrote {OUT}")


if __name__ == "__main__":
    main()
