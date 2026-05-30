#!/usr/bin/env python3
"""Regenerate Cobblemon migration spawn pool JSON from season biome legs."""

from __future__ import annotations

import json
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "common/src/main/resources/data/cobblemon/spawn_pool_world"

# Legendary / mythical / ultra-beast flyers — excluded from additive migration spawns only.
FLYING_SPAWN_EXCLUDE = {
    "articuno",
    "zapdos",
    "moltres",
    "mew",
    "lugia",
    "ho-oh",
    "hooh",
    "rayquaza",
    "latias",
    "latios",
    "kyogre",
    "groudon",
    "dialga",
    "palkia",
    "giratina",
    "reshiram",
    "zekrom",
    "kyurem",
    "xerneas",
    "yveltal",
    "zygarde",
    "solgaleo",
    "lunala",
    "necrozma",
    "zacian",
    "zamazenta",
    "eternatus",
    "calyrex",
    "koraidon",
    "miraidon",
    "terapagos",
    "celesteela",
    "enamorus",
    "landorus",
    "thundurus",
    "tornadus",
    "arceus",
    "darkrai",
    "cresselia",
    "manaphy",
    "phione",
    "jirachi",
    "deoxys",
    "volcanion",
    "magearna",
    "zarude",
    "pecharunt",
}

COBBLEMON_JAR_CANDIDATES = [
    Path.home()
    / ".gradle/caches/modules-2/files-2.1/com.cobblemon/mod/1.7.3+1.21.1/996437367d11e3008c48b19e0f1cc4934ee71451/mod-1.7.3+1.21.1.jar",
    Path.home()
    / ".gradle/caches/modules-2/files-2.1/com.cobblemon/mod/1.7.1+1.21.1/bad6fb7c93e9647aaba869bda13101b0349bbcfe/mod-1.7.1+1.21.1.jar",
]

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
        "ground_spawns": [
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
        "ground_spawns": [
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
        "ground_spawns": [
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
        "ground_spawns": [
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

SEASON_ORDER = ["spring", "summer", "autumn", "winter"]


def resolve_cobblemon_jar() -> Path:
    for candidate in COBBLEMON_JAR_CANDIDATES:
        if candidate.is_file():
            return candidate
    raise FileNotFoundError(
        "Cobblemon mod JAR not found in Gradle cache. Build the project once to download dependencies."
    )


def load_flying_species() -> list[str]:
    jar = resolve_cobblemon_jar()
    flying: list[str] = []
    with zipfile.ZipFile(jar) as archive:
        for name in archive.namelist():
            if not name.startswith("data/cobblemon/species/") or not name.endswith(".json"):
                continue
            payload = json.loads(archive.read(name))
            types = [payload.get("primaryType"), payload.get("secondaryType")]
            if "flying" not in [value for value in types if value]:
                continue
            species = name.split("/")[-1].replace(".json", "")
            if species in FLYING_SPAWN_EXCLUDE:
                continue
            flying.append(species)
    return sorted(set(flying))


def partition_flying_by_season(flying: list[str]) -> dict[str, list[str]]:
    by_season: dict[str, list[str]] = {season: [] for season in SEASON_ORDER}
    for index, species in enumerate(flying):
        by_season[SEASON_ORDER[index % len(SEASON_ORDER)]].append(species)
    return by_season


RARE_FLYING = {
    "aerodactyl",
    "archeops",
    "corviknight",
    "dragonite",
    "salamence",
    "talonflame",
    "staraptor",
    "pidgeot",
    "noivern",
}


def flying_spawn_entry(species: str, leg_index: int) -> tuple:
    # Low weight additive spawns; rotate across the three route legs.
    if species in RARE_FLYING:
        bucket = "rare"
    elif species.endswith(("otto", "loom", "drill", "knight", "aptor", "eon")):
        bucket = "uncommon"
    else:
        bucket = "common"
    return (species, leg_index % 3, "8-32", 0.55, "grounded", bucket)


def build_spawn(
    season: str,
    pokemon: str,
    leg_index: int,
    level: str,
    weight: float,
    position: str,
    bucket: str,
    biomes: list[str],
    *,
    aerial: bool = False,
) -> dict:
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
        entry["presets"] = ["natural", "treetop"] if aerial else ["natural"]
    return entry


def build_pool(season: str, data: dict, flying_for_season: list[str]) -> dict:
    spawns = []
    for row in data["ground_spawns"]:
        spawns.append(build_spawn(season, *row, data["legs"][row[1]]))
    for index, species in enumerate(flying_for_season):
        row = flying_spawn_entry(species, index)
        spawns.append(build_spawn(season, *row, data["legs"][row[1]], aerial=True))
    return {
        "enabled": True,
        "neededInstalledMods": [],
        "neededUninstalledMods": [],
        "spawns": spawns,
    }


def main() -> None:
    flying = load_flying_species()
    flying_by_season = partition_flying_by_season(flying)
    OUT.mkdir(parents=True, exist_ok=True)
    for season, data in SEASONS.items():
        path = OUT / f"ancient_extensions_migration_{season}.json"
        pool = build_pool(season, data, flying_by_season[season])
        path.write_text(json.dumps(pool, indent=2) + "\n", encoding="utf-8")
        ground = len(data["ground_spawns"])
        air = len(flying_by_season[season])
        print(f"Wrote {path} ({ground} survey + {air} flying = {ground + air} spawns)")
    print(f"\nTotal flying species in migration pools: {len(flying)} (excludes {len(FLYING_SPAWN_EXCLUDE)} legendaries)")


if __name__ == "__main__":
    main()
