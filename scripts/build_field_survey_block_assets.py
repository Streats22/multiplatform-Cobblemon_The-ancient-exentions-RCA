"""Bake Create-inspired brass block textures for Field Survey Sensor & Monitor."""
from __future__ import annotations

from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError:
    raise SystemExit("Install Pillow: pip install pillow")

ROOT = Path(__file__).resolve().parents[1] / "common" / "src" / "main" / "resources" / "assets" / "ancient_extensions" / "textures"
BLOCK = ROOT / "block"
ITEM = ROOT / "item"

# Create-adjacent brass palette
BRASS_SHADOW = (58, 42, 24)
BRASS_DARK = (92, 68, 36)
BRASS = (158, 124, 62)
BRASS_LIGHT = (196, 164, 92)
BRASS_HI = (228, 208, 140)
COPPER = (168, 92, 58)
COPPER_DARK = (110, 58, 34)
IRON = (92, 96, 104)
IRON_LIGHT = (148, 152, 160)
IRON_DARK = (48, 50, 56)
SCREEN_BG = (18, 28, 36)
SCREEN_LINE = (32, 48, 58)
SCREEN_GLOW = (48, 92, 108)
LENS_CORE = (120, 220, 200)
LENS_MID = (48, 148, 128)
LENS_RING = (24, 72, 64)
LENS_OFF = (28, 44, 40)
Rivet = (72, 54, 30)


def save(img: Image.Image, path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)
    print(f"wrote {path.relative_to(ROOT.parents[4])}")


def brass_tile(size: int = 16) -> Image.Image:
    img = Image.new("RGBA", (size, size), BRASS)
    draw = ImageDraw.Draw(img)
    for y in range(size):
        for x in range(size):
            edge = min(x, y, size - 1 - x, size - 1 - y)
            if edge == 0:
                img.putpixel((x, y), BRASS_SHADOW)
            elif edge == 1:
                img.putpixel((x, y), BRASS_DARK)
            elif x < 2 or y < 2:
                img.putpixel((x, y), BRASS_LIGHT if (x + y) % 2 == 0 else BRASS)
            elif x > size - 3 or y > size - 3:
                img.putpixel((x, y), BRASS_DARK)
    # panel seam
    draw.line((0, size // 2, size - 1, size // 2), fill=BRASS_DARK, width=1)
    draw.line((size // 2, 0, size // 2, size - 1), fill=BRASS_DARK, width=1)
    # rivets
    for px, py in ((3, 3), (size - 4, 3), (3, size - 4), (size - 4, size - 4)):
        draw.rectangle((px, py, px + 1, py + 1), fill=BRASS_HI)
        draw.point((px, py), Rivet)
    return img


def brass_side(size: int = 16) -> Image.Image:
    img = brass_tile(size)
    draw = ImageDraw.Draw(img)
    for x in range(2, size - 2, 3):
        draw.line((x, 2, x, size - 3), fill=BRASS_DARK, width=1)
    draw.rectangle((1, size - 4, size - 2, size - 2), fill=COPPER_DARK)
    draw.rectangle((2, size - 3, size - 3, size - 2), fill=COPPER)
    return img


def brass_top(size: int = 16) -> Image.Image:
    img = brass_tile(size)
    draw = ImageDraw.Draw(img)
    draw.rectangle((3, 3, size - 4, size - 4), outline=BRASS_DARK, width=1)
    draw.line((3, 3, size - 4, size - 4), fill=BRASS_HI, width=1)
    return img


def sensor_lens(size: int = 16) -> Image.Image:
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy, r = size // 2, size // 2, size // 2 - 2
    draw.ellipse((cx - r, cy - r, cx + r, cy + r), fill=LENS_RING)
    draw.ellipse((cx - r + 2, cy - r + 2, cx + r - 2, cy + r - 2), fill=LENS_MID)
    draw.ellipse((cx - 3, cy - 3, cx + 2, cy + 2), fill=LENS_CORE)
    draw.rectangle((cx - 1, cy - r + 1, cx, cy - r + 3), fill=(220, 255, 245, 180))
    return img


def sensor_details(size: int = 16) -> Image.Image:
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    draw.rectangle((0, 12, 15, 15), fill=IRON_DARK)
    draw.rectangle((1, 13, 14, 14), fill=IRON)
    draw.rectangle((6, 0, 9, 3), fill=IRON)
    draw.rectangle((6, 1, 8, 2), fill=IRON_LIGHT)
    draw.rectangle((0, 6, 2, 9), fill=COPPER_DARK)
    draw.rectangle((14, 6, 15, 9), fill=COPPER_DARK)
    return img


def monitor_front(size: int = 16) -> Image.Image:
    img = Image.new("RGBA", (size, size), SCREEN_BG)
    draw = ImageDraw.Draw(img)
    draw.rectangle((0, 0, size - 1, size - 1), outline=BRASS_DARK, width=1)
    for row in range(4):
        y0 = 2 + row * 3
        draw.rectangle((2, y0, size - 3, y0 + 1), fill=SCREEN_LINE)
    draw.line((2, 2, 4, 4), fill=SCREEN_GLOW, width=1)
    return img


def monitor_side(size: int = 16) -> Image.Image:
    img = brass_side(size)
    draw = ImageDraw.Draw(img)
    draw.rectangle((0, 1, 1, size - 2), fill=BRASS_SHADOW)
    draw.rectangle((size - 2, 1, size - 1, size - 2), fill=BRASS_HI)
    return img


def monitor_top(size: int = 16) -> Image.Image:
    return brass_top(size)


def item_icon_sensor(size: int = 32) -> Image.Image:
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # base
    draw.polygon([(6, 22), (26, 22), (28, 28), (4, 28)], fill=BRASS_DARK)
    draw.polygon([(8, 14), (24, 14), (26, 22), (6, 22)], fill=BRASS)
    draw.polygon([(10, 10), (22, 10), (24, 14), (8, 14)], fill=BRASS_LIGHT)
    # lens
    draw.ellipse((10, 6, 22, 16), fill=LENS_RING)
    draw.ellipse((12, 8, 20, 14), fill=LENS_MID)
    draw.ellipse((14, 9, 18, 13), fill=LENS_CORE)
    # antenna
    draw.rectangle((15, 2, 17, 8), fill=IRON)
    draw.polygon([(14, 2), (18, 2), (16, 0)], fill=IRON_LIGHT)
    # pipes
    draw.rectangle((4, 16, 6, 20), fill=COPPER)
    draw.rectangle((26, 16, 28, 20), fill=COPPER)
    return img


def item_icon_monitor(size: int = 32) -> Image.Image:
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # stand
    draw.rectangle((12, 24, 20, 28), fill=BRASS_DARK)
    draw.rectangle((10, 22, 22, 24), fill=BRASS)
    # frame
    draw.rectangle((6, 6, 26, 22), fill=BRASS)
    draw.rectangle((8, 8, 24, 20), fill=SCREEN_BG)
    for row in range(4):
        y = 10 + row * 3
        draw.rectangle((10, y, 22, y + 1), fill=SCREEN_LINE)
    draw.line((10, 8, 13, 11), fill=SCREEN_GLOW, width=1)
    draw.rectangle((6, 6, 26, 7), fill=BRASS_LIGHT)
    draw.rectangle((6, 21, 26, 22), fill=BRASS_DARK)
    return img


def main() -> None:
    save(brass_tile(), BLOCK / "survey_brass_casing.png")
    save(brass_side(), BLOCK / "survey_brass_side.png")
    save(brass_top(), BLOCK / "survey_brass_top.png")
    save(sensor_lens(), BLOCK / "field_survey_sensor_lens.png")
    save(sensor_details(), BLOCK / "field_survey_sensor_details.png")
    save(monitor_front(), BLOCK / "field_survey_monitor_front.png")
    save(monitor_side(), BLOCK / "field_survey_monitor_side.png")
    save(monitor_top(), BLOCK / "field_survey_monitor_top.png")
    # legacy names still referenced until models updated — overwrite with new art
    save(item_icon_sensor(), ITEM / "field_survey_sensor.png")
    save(item_icon_monitor(), ITEM / "field_survey_monitor.png")
    # block particle fallback textures
    save(brass_top(), BLOCK / "field_survey_sensor.png")
    save(monitor_front(), BLOCK / "field_survey_monitor.png")


if __name__ == "__main__":
    main()
