# Ancient Extensions — design decisions

| Topic | Decision | Implementation |
|-------|----------|----------------|
| **Kit** | Custom mod item | `ancient_professors_kit` on first join; pitches a comfort camp (campfire, small tent, bedroll, chest, lectern) + player starter supplies |
| **Journal** | Progress book | `regional_survey_journal` granted on join; right-click opens live written book (goals, dex stats, migration route) |
| **Field Survey Tablet** | Pocket dashboard | `field_survey_tablet` granted on join; right-click opens live status + shortcuts to journal, passport, and route chart |
| **Passport / origin** | One-time region stamp | `regional_passport` on join; right-click opens custom passport GUI (stamp page or region grid); locked permanently; journal shows origin goal |
| **MCA Reborn** | Optional intro order | When `mca` is installed and `deferOriginPickerForMca` is true, passport stamp opens after MCA's destiny intro closes (not during join) |
| **Serene Seasons** | Real migration calendar | When `sereneseasons` is installed and `useSereneSeasonsWhenPresent` is true, `MigrationSeasonClock` reads Serene Seasons instead of 7-day internal rotation |
| **Multiplayer origin tag** | Colored badge + name | Scoreboard team per region (`ae_kanto`, …); `[KT]` prefix + tinted player name in tab list and name tags; server announce on first stamp |
| **Poké Ball pouch** | Filtered storage | `pokeball_pouch` — craft with any `#cobblemon:poke_balls` ball; tier sets 2D/3D look and slots (18 / 27 / 36 / 54); placeable 3D block |
| **Dex** | Catch-only | `CobblemonEvents.POKEMON_CAPTURED` only; PC registration ignored |
| **Migration** | Repeatable every season, diminishing RP | Route resets when calendar season changes; per-season completion count; `0.65^n` multiplier on route reward |
| **World-gen biomes** | Optional RU + BOP | `MigrationBiomeCatalog` merges Regions Unexplored and Biomes O' Plenty when each mod is loaded; vanilla-only fallback otherwise; RU: Expansion still counts via `minecraft:*` |
| **Rank rewards** | One-time claim per tier | 21 research ranks; journal lists status; claim via journal button or `/ancientextensions rewards claim` |
| **Field Survey Calendar** | Wall spawn estimate | Hang on a wall; right-click shows species likely more common here (spawn-pool weights by biome). Estimate only — sensor is authoritative |
| **Migration Route Compass** | Biome finder | Points to nearest biome on active migration leg; sneak+use offers JourneyMap waypoint link |
| **Field Survey Sensor** | Placeable block + Create Display Link source | Always registered as a **block** (`BlockItem`); crafting + Display Link need Create (NeoForge) |
| **Field Survey Monitor** | Placeable block + Create Display Link target | Always registered; four-line board when linked via Create Display Link |
| **Field Survey Telemetry upgrade** | Official SB on NeoForge | [Unofficial Fabric port](https://modrinth.com/mod/sophisticated-backpacks-(unoffical-fabric-port)) uses the same mod IDs; backpacks work, but **no telemetry upgrade** in our Fabric jar yet |
| **Create field sensor / monitor** | Create (NeoForge only) | Blocks always on NeoForge; Display Link + recipes need Create; **not on Fabric 1.21.1** |
| **Spawns** | Additive migratory pool | Low-weight `spawn_pool_world/ancient_extensions_migration_*.json` entries; does not overwrite vanilla/Cobblemon biome pools |
| **Migration chart trade** | Cartographer (journeyman) | 7 emeralds + compass → `migration_route_chart` (crafting with journal still works) |

## Migration calendar (v0.2)

- **With Serene Seasons** (`sereneseasons`, optional): migration season = world's current Serene Seasons season (Spring/Summer/Autumn/Winter).
- **Without Serene Seasons**: **7 in-game days** per season (`MigrationConfig.DAYS_PER_SEASON`), then internal rotation.
- Tablet and `/ancientextensions migration` show which calendar is active.

## Tuning

- Routes: `MigrationRoutes.java` — each leg accepts **Regions Unexplored** biomes assigned to that season, plus any vanilla `minecraft:*` biome (covers **Regions Unexplored: Expansion** remodels).
- Migratory species (quest + catch credit): `MigrationSpecies.java` — nine species per season; regenerate spawn JSON with `scripts/generate_migration_spawns.py`
