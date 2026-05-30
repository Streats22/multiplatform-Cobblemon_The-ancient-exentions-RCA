"""Generate Poké Ball Pouch container GUI texture only (models use Cobblemon + vanilla textures)."""
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


def rgb(hex_color: str) -> tuple[int, int, int, int]:
    hex_color = hex_color.lstrip("#")
    r, g, b = (int(hex_color[i : i + 2], 16) for i in (0, 2, 4))
    return r, g, b, 255


def draw_pouch_gui(path: Path) -> None:
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

    d.rectangle((6, 17, WIDTH - 7, 65), fill=panel)
    for row in range(2):
        for col in range(9):
            x = 7 + col * 18
            y = 30 + row * 18
            d.rectangle((x, y, x + 16, y + 16), fill=slot)
            d.rectangle((x, y, x + 16, y + 1), fill=slot_hi)
            d.rectangle((x, y, x + 1, y + 16), fill=slot_hi)
            d.rectangle((x + 15, y, x + 16, y + 16), fill=leather_lo)
            d.rectangle((x, y + 15, x + 16, y + 16), fill=leather_lo)

    d.line([(6, 66), (WIDTH - 7, 66)], fill=line, width=1)

    d.rectangle((6, 67, WIDTH - 7, 140), fill=panel)
    for row in range(3):
        for col in range(9):
            x = 7 + col * 18
            y = 84 + row * 18
            d.rectangle((x, y, x + 16, y + 16), fill=slot)
            d.rectangle((x, y, x + 16, y + 1), fill=slot_hi)
            d.rectangle((x, y, x + 1, y + 16), fill=slot_hi)
            d.rectangle((x + 15, y, x + 16, y + 16), fill=leather_lo)
            d.rectangle((x, y + 15, x + 16, y + 16), fill=leather_lo)

    d.line([(6, 141), (WIDTH - 7, 141)], fill=line, width=1)

    d.rectangle((6, 142, WIDTH - 7, HEIGHT - 5), fill=leather_lo)
    for col in range(9):
        x = 7 + col * 18
        y = 142
        d.rectangle((x, y, x + 16, y + 16), fill=slot)
        d.rectangle((x, y, x + 16, y + 1), fill=slot_hi)
        d.rectangle((x, y, x + 1, y + 16), fill=slot_hi)
        d.rectangle((x + 15, y, x + 16, y + 16), fill=leather_lo)
        d.rectangle((x, y + 15, x + 16, y + 16), fill=leather_lo)

    for cx, cy in ((5, 5), (WIDTH - 6, 5), (5, HEIGHT - 6), (WIDTH - 6, HEIGHT - 6)):
        d.rectangle((cx - 1, cy - 1, cx + 1, cy + 1), fill=rgb("#f0d890"))

    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)
    print(f"Wrote {path}")


if __name__ == "__main__":
    draw_pouch_gui(GUI_ROOT / "pokeball_pouch.png")
