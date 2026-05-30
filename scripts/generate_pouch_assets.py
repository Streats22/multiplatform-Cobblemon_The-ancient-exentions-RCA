"""Generate Poké Ball Pouch container GUI texture (tileable panels + one slot-row template)."""
from __future__ import annotations

from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError:
    raise SystemExit("Install Pillow: pip install pillow")

GUI_ROOT = (
    Path(__file__).resolve().parents[1]
    / "common"
    / "src"
    / "main"
    / "resources"
    / "assets"
    / "ancient_extensions"
    / "textures"
    / "gui"
)

WIDTH = 176
HEIGHT = 166

# Must match PokeballPouchLayout UV constants.
HEADER_H = 17
PANEL_Y = 18
PANEL_H = 6
SLOT_ROW_Y = 26
SLOT_ROW_H = 18
FOOTER_Y = 160
FOOTER_H = 6


def rgb(hex_color: str) -> tuple[int, int, int, int]:
    hex_color = hex_color.lstrip("#")
    r, g, b = (int(hex_color[i : i + 2], 16) for i in (0, 2, 4))
    return r, g, b, 255


def draw_slot_row(d: ImageDraw.ImageDraw, y: int, slot, slot_hi, leather_lo) -> None:
    for col in range(9):
        x = 7 + col * 18
        d.rectangle((x, y, x + 16, y + 16), fill=slot)
        d.rectangle((x, y, x + 16, y + 1), fill=slot_hi)
        d.rectangle((x, y, x + 1, y + 16), fill=slot_hi)
        d.rectangle((x + 15, y, x + 16, y + 16), fill=leather_lo)
        d.rectangle((x, y + 15, x + 16, y + 16), fill=leather_lo)


def draw_pouch_gui(path: Path) -> None:
    """
    Atlas layout (no overlapping bakes):
      y=0..16   header + title band
      y=18..23  plain leather panel strip (stretched vertically in-game)
      y=26..43  single 9-slot row template (reused for pouch, player inv, hotbar)
      y=160..165 bottom frame cap
    """
    img = Image.new("RGBA", (WIDTH, HEIGHT), rgb("#00000000"))
    d = ImageDraw.Draw(img)

    border_dark = rgb("#2a1810")
    border_mid = rgb("#4a3020")
    leather = rgb("#b8925a")
    leather_hi = rgb("#d4b080")
    leather_lo = rgb("#8a6840")
    panel = rgb("#c9a56a")
    slot = rgb("#a08050")
    slot_hi = rgb("#c0a070")
    line = rgb("#5a3820")

    d.rectangle((0, 0, WIDTH - 1, HEIGHT - 1), fill=border_dark)
    d.rectangle((2, 2, WIDTH - 3, HEIGHT - 3), fill=border_mid)
    d.rectangle((4, 4, WIDTH - 5, HEIGHT - 5), fill=leather)

    d.rectangle((4, 4, WIDTH - 5, 16), fill=leather_hi)
    d.line([(4, 16), (WIDTH - 5, 16)], fill=line, width=1)

    # Plain panel only — never stretched over slot rows in-game.
    d.rectangle((6, PANEL_Y, WIDTH - 7, PANEL_Y + PANEL_H - 1), fill=panel)

    draw_slot_row(d, SLOT_ROW_Y, slot, slot_hi, leather_lo)

    d.line([(6, SLOT_ROW_Y + SLOT_ROW_H), (WIDTH - 7, SLOT_ROW_Y + SLOT_ROW_H)], fill=line, width=1)

    d.rectangle((4, FOOTER_Y, WIDTH - 5, HEIGHT - 5), fill=leather_lo)
    d.line([(4, FOOTER_Y), (WIDTH - 5, FOOTER_Y)], fill=line, width=1)

    for cx, cy in ((5, 5), (WIDTH - 6, 5), (5, HEIGHT - 6), (WIDTH - 6, HEIGHT - 6)):
        d.rectangle((cx - 1, cy - 1, cx + 1, cy + 1), fill=rgb("#f0d890"))

    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)
    print(f"Wrote {path}")


if __name__ == "__main__":
    draw_pouch_gui(GUI_ROOT / "pokeball_pouch.png")
