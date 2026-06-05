"""Bake a Sophisticated Backpacks-style upgrade icon for Field Survey Telemetry."""
from __future__ import annotations

from pathlib import Path

try:
    from PIL import Image
except ImportError:
    raise SystemExit("Install Pillow: pip install pillow")

ROOT = Path(__file__).resolve().parents[1]
OUT = (
    ROOT
    / "common/src/main/resources/assets/ancient_extensions/textures/item/field_survey_telemetry_upgrade.png"
)

# Sophisticated Backpacks upgrade palette (pickup_upgrade reference)
TRANSPARENT = (0, 0, 0, 0)
BORDER = (84, 39, 22, 255)
RIVET = (193, 193, 193, 255)
PANEL = (158, 73, 42, 255)
TRIM = (198, 92, 53, 255)
ICON = (61, 28, 16, 255)
SCREEN = (72, 156, 172, 255)
SCREEN_HI = (118, 210, 196, 255)
NEEDLE = (196, 86, 48, 255)


def build_frame() -> Image.Image:
    """Standard SB upgrade card with a tablet + compass telemetry icon."""
    grid = [
        "................",
        ".BBBBRRBBRRBBRR.",
        ".BPPPPPPPPPPPPP.",
        ".RPTTTTTTTTTTTR.",
        ".BPTTDDDDDDTTPB.",
        ".BPTTDEEEEETTPB.",
        ".RPTTDECCCETTPR.",
        ".BPTTDECNCETTPB.",
        ".BPTTDECCCETTPB.",
        ".RPTTDEEEEETTPR.",
        ".BPTTDDDDDDTTPB.",
        ".BPTTTswsswTTTP.",
        ".RPTTTTTTTTTTTR.",
        ".BPPPPPPPPPPPPP.",
        ".BBBBRRBBRRBBRR.",
        "................",
    ]
    color_map = {
        ".": TRANSPARENT,
        "B": BORDER,
        "R": RIVET,
        "P": PANEL,
        "T": TRIM,
        "D": ICON,
        "E": SCREEN,
        "C": SCREEN_HI,
        "N": NEEDLE,
        "s": SCREEN,
        "w": SCREEN_HI,
    }
    img = Image.new("RGBA", (16, 16), TRANSPARENT)
    for y, row in enumerate(grid):
        if len(row) != 16:
            raise ValueError(f"row {y} has length {len(row)}, expected 16")
        for x, ch in enumerate(row):
            img.putpixel((x, y), color_map[ch])
    return img


def main() -> None:
    img = build_frame()
    OUT.parent.mkdir(parents=True, exist_ok=True)
    img.save(OUT)
    print(f"wrote {OUT.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
