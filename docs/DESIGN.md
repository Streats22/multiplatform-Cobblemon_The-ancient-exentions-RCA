# Ancient Extensions — design decisions

| Topic | Decision | Implementation |
|-------|----------|----------------|
| **Kit** | Custom mod item | `ancient_professors_kit` on first join; pitches a comfort camp (campfire, small tent, bedroll, chest, lectern) + player starter supplies |
| **Journal** | Progress book | `regional_survey_journal` granted on join; right-click opens live written book (goals, dex stats, migration route) |
| **Passport / origin** | One-time region stamp | `regional_passport` on join; right-click opens custom passport GUI (stamp page or region grid); locked permanently; journal shows origin goal |
| **Multiplayer origin tag** | Colored badge + name | Scoreboard team per region (`ae_kanto`, …); `[KT]` prefix + tinted player name in tab list and name tags; server announce on first stamp |
| **Poké Ball pouch** | Filtered storage | `pokeball_pouch` — craft with any `#cobblemon:poke_balls` ball; tier sets 2D/3D look and slots (18 / 27 / 36 / 54); placeable 3D block |
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
