#!/usr/bin/env python3
"""Regenerate Cobblemon migration spawn pool JSON from season biome legs."""

from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "common/src/main/resources/data/cobblemon/spawn_pool_world"

# Biome legs mirror RegionsUnexploredBiomes.partitionForLegs(forSeason(...)).
SEASONS: dict[str, dict] = {
    "spring": {
        "legs": [
            [
                "regions_unexplored:flower_fields",
                "regions_unexplored:clover_plains",
                "regions_unexplored:temperate_grove",
                "regions_unexplored:marsh",
                "regions_unexplored:bayou",
                "regions_unexplored:fen",
                "regions_unexplored:magnolia_woodland",
            ],
            [
                "regions_unexplored:orchard",
                "regions_unexplored:eucalyptus_forest",
                "regions_unexplored:bamboo_forest",
                "regions_unexplored:alpha_grove",
                "regions_unexplored:fungal_fen",
                "regions_unexplored:hyacinth_deeps",
                "regions_unexplored:muddy_river",
            ],
            [
                "regions_unexplored:old_growth_bayou",
                "regions_unexplored:willow_forest",
                "regions_unexplored:poppy_fields",
                "regions_unexplored:pumpkin_fields",
                "regions_unexplored:ancient_delta",
                "regions_unexplored:bioshroom_caves",
            ],
        ],
        "spawns": [
            ("hoppip", 0, "8-22", 1.6, "grounded", "common"),
            ("skiploom", 0, "14-28", 1.2, "grounded", "uncommon"),
            ("oddish", 0, "6-20", 1.0, "grounded", "common"),
            ("burmy", 1, "6-20", 1.5, "grounded", "uncommon"),
            ("budew", 1, "8-22", 1.2, "grounded", "uncommon"),
            ("scatterbug", 1, "5-18", 1.0, "grounded", "common"),
            ("combee", 2, "10-24", 1.4, "grounded", "uncommon"),
            ("flabebe", 2, "8-20", 1.1, "grounded", "common"),
            ("wooper", 2, "10-24", 1.0, "submerged", "common"),
        ],
    },
    "summer": {
        "legs": [
            [
                "regions_unexplored:tropical_river",
                "regions_unexplored:grassy_beach",
                "regions_unexplored:rocky_reef",
                "regions_unexplored:outback",
                "regions_unexplored:arid_mountains",
                "regions_unexplored:dry_bushland",
                "regions_unexplored:baobab_savanna",
            ],
            [
                "regions_unexplored:joshua_desert",
                "regions_unexplored:sparse_rainforest",
                "regions_unexplored:tropics",
                "regions_unexplored:rainforest",
                "regions_unexplored:saguaro_desert",
                "regions_unexplored:shrubland",
                "regions_unexplored:steppe",
            ],
            [
                "regions_unexplored:rocky_meadow",
                "regions_unexplored:highland_fields",
                "regions_unexplored:mountains",
                "regions_unexplored:gravel_beach",
                "regions_unexplored:prismachasm",
            ],
        ],
        "spawns": [
            ("wingull", 0, "10-26", 1.6, "grounded", "common"),
            ("magikarp", 0, "5-20", 1.3, "submerged", "common"),
            ("surskit", 0, "8-22", 1.0, "grounded", "common"),
            ("corphish", 1, "12-28", 1.5, "grounded", "uncommon"),
            ("lotad", 1, "8-22", 1.2, "grounded", "common"),
            ("mudkip", 1, "10-24", 1.0, "grounded", "uncommon"),
            ("remoraid", 2, "8-24", 1.4, "grounded", "uncommon"),
            ("krabby", 2, "10-26", 1.1, "grounded", "common"),
            ("arrokuda", 2, "12-28", 1.0, "submerged", "common"),
        ],
    },
    "autumn": {
        "legs": [
            [
                "regions_unexplored:autumnal_maple_forest",
                "regions_unexplored:maple_forest",
                "regions_unexplored:deciduous_forest",
                "regions_unexplored:cold_deciduous_forest",
                "regions_unexplored:barley_fields",
            ],
            [
                "regions_unexplored:grassland",
                "regions_unexplored:prairie",
                "regions_unexplored:redwoods",
                "regions_unexplored:sparse_redwoods",
                "regions_unexplored:boreal_taiga",
            ],
            [
                "regions_unexplored:silver_birch_forest",
                "regions_unexplored:ashen_woodland",
                "regions_unexplored:towering_cliffs",
                "regions_unexplored:mauve_hills",
                "regions_unexplored:redstone_caves",
            ],
        ],
        "spawns": [
            ("seedot", 0, "8-22", 1.6, "grounded", "uncommon"),
            ("nuzleaf", 0, "16-30", 1.1, "grounded", "uncommon"),
            ("deerling", 0, "10-24", 1.0, "grounded", "common"),
            ("shroomish", 1, "10-24", 1.5, "grounded", "uncommon"),
            ("pineco", 1, "8-22", 1.2, "grounded", "common"),
            ("bouffalant", 1, "18-32", 0.9, "grounded", "uncommon"),
            ("patrat", 2, "6-18", 1.4, "grounded", "common"),
            ("pumpkaboo", 2, "12-26", 1.2, "grounded", "uncommon"),
            ("foongus", 2, "10-22", 1.0, "grounded", "common"),
        ],
    },
    "winter": {
        "legs": [
            [
                "regions_unexplored:frozen_tundra",
                "regions_unexplored:cold_boreal_taiga",
                "regions_unexplored:icy_heights",
                "regions_unexplored:frozen_pine_taiga",
                "regions_unexplored:spires",
                "regions_unexplored:cold_river",
            ],
            [
                "regions_unexplored:blackwood_taiga",
                "regions_unexplored:pine_taiga",
                "regions_unexplored:pine_slopes",
                "regions_unexplored:golden_boreal_taiga",
                "regions_unexplored:chalk_cliffs",
                "regions_unexplored:scorching_caves",
            ],
            [
                "regions_unexplored:blackstone_basin",
                "regions_unexplored:glistering_meadow",
                "regions_unexplored:infernal_holt",
                "regions_unexplored:mycotoxic_undergrowth",
                "regions_unexplored:redstone_abyss",
            ],
        ],
        "spawns": [
            ("snover", 0, "10-26", 1.6, "grounded", "uncommon"),
            ("swinub", 0, "8-22", 1.2, "grounded", "common"),
            ("delibird", 0, "12-28", 0.9, "grounded", "uncommon"),
            ("snom", 1, "8-22", 1.5, "grounded", "uncommon"),
            ("bergmite", 1, "10-24", 1.1, "grounded", "common"),
            ("snorunt", 1, "8-20", 1.0, "grounded", "common"),
            ("cubchoo", 2, "10-24", 1.4, "grounded", "uncommon"),
            ("spheal", 2, "10-26", 1.2, "grounded", "common"),
            ("cetoddle", 2, "12-28", 1.0, "grounded", "uncommon"),
        ],
    },
}

SPECIES_BY_SEASON = {
    season: sorted({entry[0] for entry in data["spawns"]})
    for season, data in SEASONS.items()
}


def build_spawn(season: str, pokemon: str, leg_index: int, level: str, weight: float, position: str, bucket: str, biomes: list[str]) -> dict:
    entry = {
        "id": f"ancient_extensions-{season}-{pokemon}",
        "pokemon": pokemon,
        "type": "pokemon",
        "spawnablePositionType": position,
        "bucket": bucket,
        "level": level,
        "weight": weight,
        "condition": {"biomes": biomes},
    }
    if position == "grounded":
        entry["presets"] = ["natural"]
    return entry


def build_pool(season: str, data: dict) -> dict:
    spawns = []
    for pokemon, leg_index, level, weight, position, bucket in data["spawns"]:
        spawns.append(
            build_spawn(season, pokemon, leg_index, level, weight, position, bucket, data["legs"][leg_index])
        )
    return {
        "enabled": True,
        "neededInstalledMods": [],
        "neededUninstalledMods": [],
        "spawns": spawns,
    }


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    for season, data in SEASONS.items():
        path = OUT / f"ancient_extensions_migration_{season}.json"
        path.write_text(json.dumps(build_pool(season, data), indent=2) + "\n", encoding="utf-8")
        print(f"Wrote {path} ({len(data['spawns'])} spawns)")
    print("\nSpecies per season (for MigrationSpecies.java):")
    for season, species in SPECIES_BY_SEASON.items():
        print(f"  {season.upper()}: {', '.join(species)}")


if __name__ == "__main__":
    main()
