"""Generate Minecraft-style pixel-art kit item textures (16x16)."""
from __future__ import annotations

from pathlib import Path

try:
    from pixel_art import render_grid
except ImportError:
    from scripts.pixel_art import render_grid  # type: ignore[no-redef]

ROOT = Path(__file__).resolve().parents[1] / "common" / "src" / "main" / "resources" / "assets" / "ancient_extensions" / "textures" / "item"

KIT_PALETTE = {
    ".": (0, 0, 0, 0),
    "o": (45, 30, 16, 255),
    "s": (58, 38, 20, 255),
    "d": (82, 54, 30, 255),
    "m": (118, 80, 46, 255),
    "l": (150, 106, 62, 255),
    "h": (184, 136, 82, 255),
    "c": (204, 186, 142, 255),
    "b": (176, 142, 48, 255),
    "k": (36, 28, 18, 255),
}

# Leather field satchel — flap, straps, front buckle, left highlight / right shadow
KIT_BASE = [
    "................",
    "....cc....cc....",
    "..oooooooooooo..",
    ".ossdhhhhhhddso.",
    ".ossdllllllddso.",
    "ossdmmllllmmdsso",
    "osdmmllllllmmdso",
    ".osdmmllbbllmds.",
    "osdmmlllllllmmdo",
    ".osdmmlllllmmddo",
    "..osdmmmmmmmddso",
    "...ossddddddsso.",
    "....oooooooo....",
    "................",
    "................",
    "................",
]

# Poké Ball emblem on the flap (layer 1) — three-row ball + button
KIT_OVERLAY = [
    "................",
    "................",
    "................",
    "................",
    "................",
    ".....ooooo......",
    "....oRRRRRo.....",
    "....oRkkkRo.....",
    "....oOWWWWo.....",
    ".....oOOOo......",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
]

OVERLAY_PALETTE = {
    ".": (0, 0, 0, 0),
    "o": (45, 30, 16, 255),
    "O": (36, 28, 18, 255),
    "R": (214, 46, 40, 255),
    "W": (244, 244, 244, 255),
    "k": (28, 28, 28, 255),
}

BRIEFING_PALETTE = {
    ".": (0, 0, 0, 0),
    "o": (48, 34, 20, 255),
    "s": (72, 52, 32, 255),
    "d": (96, 70, 44, 255),
    "p": (232, 220, 188, 255),
    "a": (214, 198, 162, 255),
    "l": (178, 158, 118, 255),
    "t": (88, 64, 38, 255),
    "k": (48, 40, 32, 255),
    "r": (196, 48, 40, 255),
    "w": (228, 92, 80, 255),
}

# Folded field briefing pamphlet with title band + wax seal
BRIEFING = [
    "................",
    "....oooooooo....",
    "...oapaaaaapo...",
    "...oaplktlapo...",
    "...oapalaapao...",
    "...oapaaaaapo...",
    "...oapaarwapo...",
    "...oapalaapao...",
    "...oapaaaaapo...",
    "...ossdddssso...",
    "....oooooooo....",
    "................",
    "................",
    "................",
    "................",
    "................",
]


def main() -> None:
    ROOT.mkdir(parents=True, exist_ok=True)
    render_grid(KIT_BASE, KIT_PALETTE).save(ROOT / "ancient_professors_kit.png")
    render_grid(KIT_OVERLAY, OVERLAY_PALETTE).save(ROOT / "ancient_professors_kit_overlay.png")
    render_grid(BRIEFING, BRIEFING_PALETTE).save(ROOT / "field_briefing.png")
    print("Generated kit item textures (16x16 pixel art).")


if __name__ == "__main__":
    main()
