"""Generate Field Survey Tablet GUI and item icon."""
from __future__ import annotations

from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError:
    raise SystemExit("Install Pillow: pip install pillow")

ASSETS = Path(__file__).resolve().parents[1] / "common" / "src" / "main" / "resources" / "assets" / "ancient_extensions" / "textures"
GUI_W = 256
GUI_H = 256


def rgb(hex_color: str) -> tuple[int, int, int, int]:
    hex_color = hex_color.lstrip("#")
    r, g, b = (int(hex_color[i : i + 2], 16) for i in (0, 2, 4))
    return r, g, b, 255


def draw_tablet_gui(path: Path) -> None:
    img = Image.new("RGBA", (GUI_W, GUI_H), rgb("#00000000"))
    d = ImageDraw.Draw(img)

    frame_dark = rgb("#142028")
    frame = rgb("#1e3440")
    frame_hi = rgb("#2a4a58")
    screen = rgb("#c8e4f0")
    screen_hi = rgb("#dceef8")
    screen_shadow = rgb("#9ec0d0")
    ink = rgb("#1a3040")
    footer = rgb("#b8d4e4")
    rule = rgb("#7aa8bc")

    d.rectangle((0, 0, GUI_W - 1, GUI_H - 1), fill=frame_dark)
    d.rectangle((4, 4, GUI_W - 5, GUI_H - 5), fill=frame)
    d.rectangle((8, 8, GUI_W - 9, GUI_H - 9), fill=frame_hi)

    d.rectangle((14, 14, GUI_W - 15, GUI_H - 15), fill=screen_shadow)
    d.rectangle((16, 16, GUI_W - 17, GUI_H - 17), fill=screen)
    d.rectangle((18, 18, GUI_W - 19, GUI_H - 19), fill=screen_hi)

    d.rectangle((18, 18, GUI_W - 19, 34), fill=ink)
    d.line([(18, 34), (GUI_W - 19, 34)], fill=frame_dark, width=1)

    for y in range(42, GUI_H - 52, 11):
        d.line([(22, y), (GUI_W - 22, y)], fill=rule, width=1)

    d.rectangle((18, GUI_H - 48, GUI_W - 19, GUI_H - 18), fill=footer)
    d.line([(18, GUI_H - 48), (GUI_W - 19, GUI_H - 48)], fill=rule, width=1)

    for ox, oy in ((18, 18), (GUI_W - 22, 18), (18, GUI_H - 22), (GUI_W - 22, GUI_H - 22)):
        d.point((ox, oy), fill=frame_hi)

    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)
    print(f"Wrote {path}")


def draw_item_icon(path: Path) -> None:
    size = 16
    img = Image.new("RGBA", (size, size), rgb("#00000000"))
    d = ImageDraw.Draw(img)
    d.rectangle((2, 1, 13, 14), fill=rgb("#2a4a58"), outline=rgb("#142028"))
    d.rectangle((3, 2, 12, 10), fill=rgb("#b8e0f0"))
    d.rectangle((5, 4, 10, 6), fill=rgb("#1a3040"))
    d.point((11, 12), fill=rgb("#4ad8ff"))
    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)
    print(f"Wrote {path}")


if __name__ == "__main__":
    draw_tablet_gui(ASSETS / "gui" / "field_survey_tablet.png")
    draw_item_icon(ASSETS / "item" / "field_survey_tablet.png")
