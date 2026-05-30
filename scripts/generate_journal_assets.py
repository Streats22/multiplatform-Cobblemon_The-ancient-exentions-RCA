"""Generate Regional Survey Journal GUI + aged book item icon."""
from __future__ import annotations

from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError:
    raise SystemExit("Install Pillow: pip install pillow")

try:
    from pixel_art import render_grid
except ImportError:
    from scripts.pixel_art import render_grid  # type: ignore[no-redef]

ASSETS = Path(__file__).resolve().parents[1] / "common" / "src" / "main" / "resources" / "assets" / "ancient_extensions" / "textures"
GUI_W = 220
GUI_H = 220

JOURNAL_ITEM_PALETTE = {
    ".": (0, 0, 0, 0),
    "o": (32, 20, 12, 255),
    "s": (48, 30, 18, 255),
    "d": (68, 42, 24, 255),
    "l": (96, 58, 32, 255),
    "h": (118, 72, 40, 255),
    "p": (224, 206, 168, 255),
    "a": (204, 184, 144, 255),
    "e": (176, 154, 114, 255),
    "g": (188, 152, 44, 255),
    "k": (56, 40, 24, 255),
}

# Closed field log — leather cover, page edge, gold strap, spine shadow
JOURNAL_ITEM = [
    "................",
    "....pppppppp....",
    "...oppppppppo...",
    "..odllllllhpo...",
    "..odllllllhpo...",
    "..odllggllhpo...",
    "..odllllllhpo...",
    "..odllllllhpo...",
    "..odllllllhpo...",
    "...oppppppppo...",
    "...osssssssso...",
    "....oooooooo....",
    "................",
    "................",
    "................",
    "................",
]


def rgb(hex_color: str) -> tuple[int, int, int, int]:
    hex_color = hex_color.lstrip("#")
    r, g, b = (int(hex_color[i : i + 2], 16) for i in (0, 2, 4))
    return r, g, b, 255


def draw_journal_gui(path: Path) -> None:
    """Open aged field log — dark leather frame, warm paper, subtle rules."""
    img = Image.new("RGBA", (GUI_W, GUI_H), rgb("#00000000"))
    d = ImageDraw.Draw(img)

    leather_dark = rgb("#2a160c")
    leather = rgb("#4a2818")
    leather_hi = rgb("#5c3420")
    paper_aged = rgb("#e8d4b8")
    paper_light = rgb("#f0e2cc")
    paper_shadow = rgb("#d4bc98")
    rule = rgb("#c4a880")
    ink_band = rgb("#3d2818")

    d.rectangle((0, 0, GUI_W - 1, GUI_H - 1), fill=leather_dark)
    d.rectangle((3, 3, GUI_W - 4, GUI_H - 4), fill=leather)
    d.rectangle((5, 5, GUI_W - 6, GUI_H - 6), fill=leather_hi)

    d.rectangle((10, 10, GUI_W - 11, GUI_H - 11), fill=paper_shadow)
    d.rectangle((12, 12, GUI_W - 13, GUI_H - 13), fill=paper_aged)
    d.rectangle((14, 14, GUI_W - 15, GUI_H - 15), fill=paper_light)

    d.rectangle((14, 14, GUI_W - 15, 28), fill=ink_band)
    d.line([(14, 28), (GUI_W - 15, 28)], fill=leather_dark, width=1)

    for x in range(14, 19):
        alpha = 30 + (x - 14) * 8
        d.line([(x, 29), (x, GUI_H - 16)], fill=(100, 60, 30, alpha), width=1)

    for y in range(36, GUI_H - 32, 11):
        d.line([(18, y), (GUI_W - 18, y)], fill=rule, width=1)

    d.line([(18, GUI_H - 30), (GUI_W - 18, GUI_H - 30)], fill=rule, width=1)

    for ox, oy in ((14, 14), (GUI_W - 18, 14), (14, GUI_H - 18), (GUI_W - 18, GUI_H - 18)):
        d.point((ox, oy), fill=paper_shadow)
        d.point((ox + 1, oy), fill=paper_shadow)

    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)
    print(f"Wrote {path}")


def draw_journal_item(path: Path) -> None:
    render_grid(JOURNAL_ITEM, JOURNAL_ITEM_PALETTE).save(path)
    print(f"Wrote {path}")


if __name__ == "__main__":
    draw_journal_gui(ASSETS / "gui" / "regional_survey_journal.png")
    draw_journal_item(ASSETS / "item" / "regional_survey_journal.png")
