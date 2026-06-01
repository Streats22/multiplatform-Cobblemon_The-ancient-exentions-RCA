"""Generate Regional Survey Journal GUI texture (item icons are hand-authored PNGs)."""
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


def draw_journal_gui(path: Path) -> None:
    """Open aged field log — dark leather frame, warm paper, subtle rules (full 256×256)."""
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
    footer_band = rgb("#d8c4a8")

    d.rectangle((0, 0, GUI_W - 1, GUI_H - 1), fill=leather_dark)
    d.rectangle((3, 3, GUI_W - 4, GUI_H - 4), fill=leather)
    d.rectangle((5, 5, GUI_W - 6, GUI_H - 6), fill=leather_hi)

    d.rectangle((10, 10, GUI_W - 11, GUI_H - 11), fill=paper_shadow)
    d.rectangle((12, 12, GUI_W - 13, GUI_H - 13), fill=paper_aged)
    d.rectangle((14, 14, GUI_W - 15, GUI_H - 15), fill=paper_light)

    d.rectangle((14, 14, GUI_W - 15, 30), fill=ink_band)
    d.line([(14, 30), (GUI_W - 15, 30)], fill=leather_dark, width=1)

    for x in range(14, 20):
        alpha = 30 + (x - 14) * 8
        d.line([(x, 31), (x, GUI_H - 38)], fill=(100, 60, 30, alpha), width=1)

    for y in range(38, GUI_H - 36, 11):
        d.line([(18, y), (GUI_W - 18, y)], fill=rule, width=1)

    d.rectangle((14, GUI_H - 34, GUI_W - 15, GUI_H - 16), fill=footer_band)
    d.line([(14, GUI_H - 34), (GUI_W - 15, GUI_H - 34)], fill=rule, width=1)

    for ox, oy in ((14, 14), (GUI_W - 18, 14), (14, GUI_H - 18), (GUI_W - 18, GUI_H - 18)):
        d.point((ox, oy), fill=paper_shadow)
        d.point((ox + 1, oy), fill=paper_shadow)

    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)
    print(f"Wrote {path}")

    draw_journal_widgets(ASSETS / "gui" / "regional_survey_journal_widgets.png", rule)


def draw_journal_widgets(path: Path, rule) -> None:
    """Footer button sprites — separate file so the main GUI blit does not duplicate them."""
    img = Image.new("RGBA", (GUI_W, 32), rgb("#00000000"))
    d = ImageDraw.Draw(img)
    draw_journal_sprites(d, rule)
    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)
    print(f"Wrote {path}")


def draw_journal_sprites(d: ImageDraw.ImageDraw, rule) -> None:
    gold = rgb("#c9a227")
    gold_hi = rgb("#e8c84a")
    gold_shadow = rgb("#8a6820")
    reward_band = rgb("#efe3c4")

    # Reward strip (14, 238) — sits between page body and footer
    rx, ry, rw, rh = 14, 238, 228, 18
    d.rectangle((rx, ry, rx + rw - 1, ry + rh - 1), fill=reward_band)
    d.line([(rx, ry), (rx + rw - 1, ry)], fill=gold, width=1)
    d.line([(rx, ry + rh - 1), (rx + rw - 1, ry + rh - 1)], fill=rule, width=1)

    leather_btn = rgb("#5c3420")
    leather_hi = rgb("#7a4a28")
    leather_lo = rgb("#3a2010")

    def draw_nav_tile(x0: int, y0: int, pointing_right: bool, hovered: bool) -> None:
        face = leather_hi if hovered else leather_btn
        d.rectangle((x0, y0, x0 + 21, y0 + 15), fill=face)
        d.rectangle((x0, y0, x0 + 21, y0 + 15), outline=gold if hovered else leather_lo, width=1)
        cx = x0 + 11
        cy = y0 + 8
        if pointing_right:
            pts = [(cx - 4, cy - 4), (cx + 3, cy), (cx - 4, cy + 4)]
        else:
            pts = [(cx + 4, cy - 4), (cx - 3, cy), (cx + 4, cy + 4)]
        d.polygon(pts, fill=gold_hi if hovered else gold)

    # Claim button sprites (0, 220) — 108×16 normal + hover
    bx, by, bw, bh = 0, 220, 108, 16
    for row, (face, hi, lo) in enumerate(
        (
            (gold_hi, rgb("#fff0a8"), gold),
            (gold, gold_hi, gold_shadow),
        )
    ):
        y0 = by + row * bh
        d.rectangle((bx, y0, bx + bw - 1, y0 + bh - 1), fill=face)
        d.rectangle((bx, y0, bx + bw - 1, y0 + bh - 1), outline=lo, width=1)
        d.line([(bx + 1, y0 + 1), (bx + bw - 2, y0 + 1)], fill=hi, width=1)
        d.line([(bx + 1, y0 + bh - 2), (bx + bw - 2, y0 + bh - 2)], fill=lo, width=1)

    # Page nav — prev (112, 220), next (134, 220); 22×16 normal + hover rows
    nav_y = 220
    draw_nav_tile(112, nav_y, False, False)
    draw_nav_tile(112, nav_y + 16, False, True)
    draw_nav_tile(134, nav_y, True, False)
    draw_nav_tile(134, nav_y + 16, True, True)


if __name__ == "__main__":
    draw_journal_gui(ASSETS / "gui" / "regional_survey_journal.png")
