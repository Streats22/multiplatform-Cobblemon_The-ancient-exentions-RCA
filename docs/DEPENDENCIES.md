# Mod dependencies

What **The Ancient Extensions** connects to, how it is declared, and where to find upstream MDKs/sources.

Target pack: [Rubius Cobblemon](https://modrinth.com/modpack/rubius-cobblemon) (NeoForge 1.21.1 + Cobblemon 1.7.3).

## Summary

| Mod | Mod ID | Required? | Used for |
|-----|--------|-----------|----------|
| Minecraft | `minecraft` | **Yes** | Base game |
| NeoForge | `neoforge` | **Yes** (NeoForge jar) | Production loader |
| Fabric Loader + API | `fabricloader`, `fabric-api` | **Yes** (Fabric jar) | Fabric loader |
| Cobblemon | `cobblemon` | **Yes** | Capture events, items, spawn pools, `#cobblemon:poke_balls` |
| Fabric Language Kotlin | `fabric-language-kotlin` | **Yes** (Fabric only) | Cobblemon is Kotlin; required at runtime on Fabric |
| Kotlin for Forge | *(library, not a mod)* | **Yes** (NeoForge only) | Cobblemon runtime on NeoForge (`kotlinforforge-neoforge`) |
| Regions Unexplored | `regions_unexplored` | **Recommended** | Migration route biomes + additive migratory spawn pools |
| Serene Seasons Plus | TBD | **Planned** | Real in-game calendar for `MigrationSeasonClock` |

Regions Unexplored is **not** a compile dependency — only biome IDs in JSON/Java. The mod still loads without it; migration legs also accept any vanilla `minecraft:*` biome.

## Version pins (`gradle.properties`)

These match the versions used in this repo’s Gradle build and pack target:

```properties
minecraft_version=1.21.1
neoforge_version=21.1.182
cobblemon_version=1.7.3+1.21.1
fabric_loader_version=0.17.2
fabric_api_version=0.116.6+1.21.1
fabric_kotlin=1.13.6+kotlin.2.2.20
kotlin_for_forge_version=5.10.0
regions_unexplored_version=0.5.9
```

## Cobblemon (required)

| | |
|---|---|
| **Mod ID** | `cobblemon` |
| **Version** | `1.7.3+1.21.1` (compatible `[1.7.3, 1.8.0)`) |
| **Gradle (common)** | `com.cobblemon:mod:1.7.3+1.21.1` |
| **Gradle (NeoForge)** | `com.cobblemon:neoforge:1.7.3+1.21.1` |
| **Gradle (Fabric)** | `com.cobblemon:fabric:1.7.3+1.21.1` |
| **Maven** | `https://artefacts.cobblemon.com/releases/` |
| **Source / MDK** | [gitlab.com/cable-mc/cobblemon](https://gitlab.com/cable-mc/cobblemon) |
| **Modrinth** | [modrinth.com/mod/cobblemon](https://modrinth.com/mod/cobblemon) |

**Code touchpoints:** `CobblemonEvents.POKEMON_CAPTURED`, Cobblemon item IDs in kit rewards, `data/cobblemon/spawn_pool_world/*.json`, `#cobblemon:is_overworld`, `#cobblemon:poke_balls`.

This project is based on the Cobblemon **Architectury multiplatform MDK** pattern (common + NeoForge + Fabric).

## Regions Unexplored (recommended)

| | |
|---|---|
| **Mod ID** | `regions_unexplored` |
| **Tested / targeted** | `0.5.9` (NeoForge 1.21.1) |
| **Version range** | `[0.5.6,)` |
| **Modrinth** | [modrinth.com/mod/regions-unexplored](https://modrinth.com/mod/regions-unexplored) |
| **NeoForge source** | [github.com/UHQ-GAMES-MODS/REGIONS_UNEXPLORED_FORGE](https://github.com/UHQ-GAMES-MODS/REGIONS_UNEXPLORED_FORGE) (branch `1.21`) |

**Content touchpoints:** `MigrationRoutes.java`, `ancient_extensions_migration_*.json` spawn pools.

> **Note:** RU `0.6+` renames/removes several biomes referenced in v0.1 routes (e.g. `frozen_tundra`, `temperate_grove`, `arid_mountains`). Pin **0.5.x** for Rubius until routes are updated, or refresh biome IDs after upgrading RU.

RU 0.6+ also pulls **Lithostitched** / **Biolith** as its own dependencies — you do **not** need to declare those for Ancient Extensions.

## Platform runtimes

### NeoForge

| | |
|---|---|
| **Loader** | NeoForge `21.1.182+` |
| **Kotlin library** | `thedarkcolour:kotlinforforge-neoforge:5.10.0` (Cobblemon runtime) |
| **Maven** | `https://maven.neoforged.net/releases/` |
| **KotlinForForge** | [github.com/thedarkcolour/KotlinForForge](https://github.com/thedarkcolour/KotlinForForge) |

Declared in `neoforge/src/main/resources/META-INF/neoforge.mods.toml`.

### Fabric

| | |
|---|---|
| **Loader** | Fabric Loader `0.17.2+` |
| **API** | Fabric API `0.116.6+1.21.1` |
| **Kotlin** | Fabric Language Kotlin `1.13.6+kotlin.2.2.20` |

Declared in `fabric/src/main/resources/fabric.mod.json`.

## Planned integrations

| Mod | Purpose | Status |
|-----|---------|--------|
| Serene Seasons Plus | Drive `MigrationSeasonClock` from real seasons | Roadmap (`docs/DESIGN.md`) |

## Local dev runtime (optional)

To test migration spawns with Regions Unexplored in a NeoForge dev client, add the jar manually or via Modrinth Maven in `neoforge/build.gradle.kts`:

```kotlin
repositories {
    maven("https://api.modrinth.com/maven")
}

dependencies {
    // Regions Unexplored 0.5.9 — NeoForge 1.21.1 (Modrinth version id KrbwbPuJ)
    modLocalRuntime("maven.modrinth:regions-unexplored:KrbwbPuJ")
}
```

Not enabled by default — keeps CI/offline builds simple.
