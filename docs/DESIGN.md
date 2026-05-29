# Ancient Extensions — design decisions

| Topic | Decision | Implementation |
|-------|----------|----------------|
| **Kit** | Custom mod item | `ancient_professors_kit` on first world join (not craftable); creative for testing; `FieldCampPlacer` for camp |
| **Journal** | Progress book | `regional_survey_journal` granted on join; right-click opens live written book (goals, dex stats, migration route) |
| **Poké Ball pouch** | Filtered storage | `pokeball_pouch` — placeable 3D block + handheld; 18 slots, only `#cobblemon:poke_balls`; Cobblemon-style layered ball on front; craftable (leather + string) |
| **Dex** | Catch-only | `CobblemonEvents.POKEMON_CAPTURED` only; PC registration ignored |
| **Migration** | Repeatable every season, diminishing RP | Route resets when calendar season changes; per-season completion count; `0.65^n` multiplier on route reward |
| **Spawns** | Additive migratory pool | Low-weight `spawn_pool_world/ancient_extensions_migration_*.json` entries; does not overwrite vanilla/Cobblemon biome pools |

## Migration calendar (v0.2)

- Default: **7 in-game days** per season (`MigrationConfig.DAYS_PER_SEASON`).
- Planned: Serene Seasons Plus integration on NeoForge (`MigrationSeasonClock`).

## Tuning

- Routes: `MigrationRoutes.java` — each leg accepts **Terralith**, **Regions Unexplored**, or vanilla biomes (any one counts).
- Migratory species (quest + catch credit): `MigrationSpecies.java`
- Spawn weights: `data/cobblemon/spawn_pool_world/ancient_extensions_migration_*.json` (additive pools in both worldgen mods’ biomes)
