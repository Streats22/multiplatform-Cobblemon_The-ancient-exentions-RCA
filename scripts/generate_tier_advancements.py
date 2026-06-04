"""Generate research tier advancement JSON and lang entries."""
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ADV_DIR = ROOT / "common/src/main/resources/data/ancient_extensions/advancement/research/tier"
LANG_PATH = ROOT / "common/src/main/resources/assets/ancient_extensions/lang/en_us.json"

TIERS = [
    "ROOKIE",
    "FIELD_AIDE",
    "SURVEYOR",
    "JUNIOR_TRACKER",
    "TRACKER",
    "NATURALIST",
    "FIELD_NATURALIST",
    "ECOLOGIST",
    "ROUTE_SCOUT",
    "MIGRATION_SPECIALIST",
    "LEAGUE_AIDE",
    "LEAGUE_RESEARCHER",
    "SENIOR_RESEARCHER",
    "REGIONAL_AUTHORITY",
    "BIOME_EXPERT",
    "REGIONAL_ANALYST",
    "CHIEF_RESEARCHER",
    "CONTINENTAL_SURVEYOR",
    "MASTER_SURVEYOR",
    "GRAND_AUTHORITY",
    "LEGENDARY_AUTHORITY",
]

MIN_POINTS = {
    "ROOKIE": 0,
    "FIELD_AIDE": 13,
    "SURVEYOR": 25,
    "JUNIOR_TRACKER": 40,
    "TRACKER": 55,
    "NATURALIST": 75,
    "FIELD_NATURALIST": 100,
    "ECOLOGIST": 120,
    "ROUTE_SCOUT": 150,
    "MIGRATION_SPECIALIST": 175,
    "LEAGUE_AIDE": 200,
    "LEAGUE_RESEARCHER": 230,
    "SENIOR_RESEARCHER": 270,
    "REGIONAL_AUTHORITY": 300,
    "BIOME_EXPERT": 340,
    "REGIONAL_ANALYST": 380,
    "CHIEF_RESEARCHER": 425,
    "CONTINENTAL_SURVEYOR": 475,
    "MASTER_SURVEYOR": 530,
    "GRAND_AUTHORITY": 600,
    "LEGENDARY_AUTHORITY": 700,
}

CHART_TIERS = {"ROUTE_SCOUT", "MIGRATION_SPECIALIST"}
PASSPORT_TIERS = {
    "REGIONAL_AUTHORITY",
    "BIOME_EXPERT",
    "REGIONAL_ANALYST",
    "CHIEF_RESEARCHER",
    "CONTINENTAL_SURVEYOR",
    "MASTER_SURVEYOR",
    "GRAND_AUTHORITY",
    "LEGENDARY_AUTHORITY",
}
COMPASS_TIERS = {"LEGENDARY_AUTHORITY"}


def tier_key(tier: str) -> str:
    return tier.lower()


def frame(ordinal: int) -> str:
    if ordinal <= 4:
        return "task"
    if ordinal <= 11:
        return "goal"
    return "challenge"


def icon(tier: str) -> str:
    if tier in COMPASS_TIERS:
        return "ancient_extensions:migration_route_compass"
    if tier in CHART_TIERS:
        return "ancient_extensions:migration_route_chart"
    if tier in PASSPORT_TIERS:
        return "ancient_extensions:regional_passport"
    return "ancient_extensions:regional_survey_journal"


def advancement_id(tier: str) -> str:
    return f"ancient_extensions:research/tier/{tier_key(tier)}"


def write_root() -> None:
    payload = {
        "parent": "ancient_extensions:survey/pitch_field_camp",
        "display": {
            "icon": {"id": "ancient_extensions:regional_survey_journal"},
            "title": {"translate": "ancient_extensions.advancement.research_root.title"},
            "description": {"translate": "ancient_extensions.advancement.research_root.description"},
            "background": "ancient_extensions:textures/gui/regional_survey_journal.png",
            "frame": "goal",
            "hidden": True,
            "show_toast": False,
            "announce_to_chat": False,
        },
        "criteria": {"research_started": {"trigger": "minecraft:impossible"}},
    }
    (ADV_DIR / "root.json").write_text(json.dumps(payload, indent=2) + "\n", encoding="utf-8")


def write_tier(tier: str, ordinal: int, parent: str | None) -> None:
    key = tier_key(tier)
    payload = {
        "parent": parent,
        "display": {
            "icon": {"id": icon(tier)},
            "title": {"translate": f"ancient_extensions.advancement.tier.{key}.title"},
            "description": {"translate": f"ancient_extensions.advancement.tier.{key}.description"},
            "frame": frame(ordinal),
            "show_toast": True,
            "announce_to_chat": ordinal >= 5,
        },
        "criteria": {"reached_tier": {"trigger": "minecraft:impossible"}},
    }
    (ADV_DIR / f"{key}.json").write_text(json.dumps(payload, indent=2) + "\n", encoding="utf-8")


def lang_entries() -> dict[str, str]:
    entries = {
        "ancient_extensions.advancement.research_root.title": "Regional Survey",
        "ancient_extensions.advancement.research_root.description": "Rise through the research ranks by catching Pokémon in the field.",
    }
    for tier in TIERS:
        key = tier_key(tier)
        tier_name_key = f"ancient_extensions.dex.tier.{key}"
        rp = MIN_POINTS[tier]
        entries[f"ancient_extensions.advancement.tier.{key}.title"] = f"{{{{tier_name}}}}"
        if tier == "ROOKIE":
            desc = "Begin the Regional Survey."
        else:
            desc = f"Reach this research rank ({rp} RP)."
        entries[f"ancient_extensions.advancement.tier.{key}.description"] = desc
    return entries


def merge_lang() -> None:
    lang = json.loads(LANG_PATH.read_text(encoding="utf-8"))
    generated = lang_entries()
    for tier in TIERS:
        key = tier_key(tier)
        title_key = f"ancient_extensions.advancement.tier.{key}.title"
        tier_display = lang.get(f"ancient_extensions.dex.tier.{key}", key.replace("_", " ").title())
        generated[title_key] = tier_display
    lang.update({k: v for k, v in generated.items() if not k.endswith(".title") or "research_root" in k})
    for tier in TIERS:
        key = tier_key(tier)
        title_key = f"ancient_extensions.advancement.tier.{key}.title"
        lang[title_key] = lang.get(f"ancient_extensions.dex.tier.{key}", generated[title_key])
    lang["ancient_extensions.advancement.research_root.title"] = generated[
        "ancient_extensions.advancement.research_root.title"
    ]
    lang["ancient_extensions.advancement.research_root.description"] = generated[
        "ancient_extensions.advancement.research_root.description"
    ]
    for tier in TIERS:
        key = tier_key(tier)
        desc_key = f"ancient_extensions.advancement.tier.{key}.description"
        lang[desc_key] = generated[desc_key]
    LANG_PATH.write_text(json.dumps(lang, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def main() -> None:
    ADV_DIR.mkdir(parents=True, exist_ok=True)
    write_root()
    parent = "ancient_extensions:research/tier/root"
    for ordinal, tier in enumerate(TIERS):
        write_tier(tier, ordinal, parent)
        parent = advancement_id(tier)
    merge_lang()
    print(f"wrote {len(TIERS) + 1} advancements to {ADV_DIR}")


if __name__ == "__main__":
    main()
