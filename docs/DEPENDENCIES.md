# Mod dependencies

What **The Ancient Extensions** connects to, how it is declared, and where to find upstream MDKs/sources.

Target pack: [Rubius Cobblemon](https://modrinth.com/modpack/rubius-cobblemon) (NeoForge 1.21.1 + Cobblemon 1.7.3).

## Loader matrix (Minecraft 1.21.1)

Use this table before adding a pack mod or integration. **Exists** = official release for that loader on 1.21.1 (
community ports noted separately).

| Mod                         | Mod ID                          |  NeoForge  |    Fabric     | Exists on loader?                                                                                                                                                                                          | Ancient Extensions                                                                                   |
|-----------------------------|---------------------------------|:----------:|:-------------:|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| Minecraft                   | `minecraft`                     |     ✓      |       ✓       | Both                                                                                                                                                                                                       | Required                                                                                             |
| NeoForge                    | `neoforge`                      |     ✓      |       —       | NeoForge jar only                                                                                                                                                                                          | Required (NeoForge build)                                                                            |
| Fabric Loader + API         | `fabricloader`, `fabric-api`    |     —      |       ✓       | Fabric jar only                                                                                                                                                                                            | Required (Fabric build)                                                                              |
| Cobblemon                   | `cobblemon`                     |     ✓      |       ✓       | Both                                                                                                                                                                                                       | Required                                                                                             |
| Kotlin for Forge            | *(library)*                     |     ✓      |       —       | NeoForge runtime lib                                                                                                                                                                                       | Required (NeoForge)                                                                                  |
| Fabric Language Kotlin      | `fabric-language-kotlin`        |     —      |       ✓       | Fabric only                                                                                                                                                                                                | Required (Fabric)                                                                                    |
| **Create**                  | `create`                        |     ✓      |     **✗**     | **NeoForge only** (no official 1.21.1 Fabric; community port stops at 1.20.1)                                                                                                                              | Display Link + sensor/monitor recipes — `OptionalIntegrationMods.hasCreate()` is **false on Fabric** |
| **Sophisticated Backpacks** | `sophisticatedbackpacks`        | ✓ official | ✓ unofficial† | Official: [NeoForge](https://modrinth.com/mod/sophisticated-backpacks). Fabric: [unofficial port](https://modrinth.com/mod/sophisticated-backpacks-(unoffical-fabric-port)) — **same mod IDs** as official | Telemetry upgrade only on **official NeoForge** build (see below)                                    |
| Sophisticated Core          | `sophisticatedcore`             | ✓ official | ✓ unofficial† | Pair with SB; Fabric needs [unofficial Core port](https://modrinth.com/mod/sophisticated-core-(unofficial-fabric-port))                                                                                    | NeoForge `modCompileOnly` only                                                                       |
| Regions Unexplored          | `regions_unexplored`            |     ✓      |       ✓       | Both                                                                                                                                                                                                       | Optional biomes + spawn filter                                                                       |
| Biomes O' Plenty            | `biomesoplenty`                 |     ✓      |       ✓       | Both                                                                                                                                                                                                       | Optional biomes + spawn filter                                                                       |
| Serene Seasons              | `sereneseasons`                 |     ✓      |       ✓       | Both (Plus uses same API)                                                                                                                                                                                  | Optional migration calendar                                                                          |
| MCA Reborn                  | `mca`                           |     ✓      |       ✓       | Both                                                                                                                                                                                                       | Optional passport timing                                                                             |
| Comforts                    | `comforts`                      |     ✓      |       ✓       | Both                                                                                                                                                                                                       | Optional camp sleeping bag                                                                           |
| Lootr                       | `lootr`                         |     ✓      |       ✓       | Both                                                                                                                                                                                                       | Optional per-player camp chest (config)                                                              |
| JEI                         | `jei`                           |     ✓      |       ✓       | Both                                                                                                                                                                                                       | Optional recipe viewer (NeoForge plugin)                                                             |
| JourneyMap                  | `journeymap`                    |     ✓      |       ✓       | Both                                                                                                                                                                                                       | Optional compass waypoint click                                                                      |
| Xaero's Minimap / World Map | `xaerominimap`, `xaeroworldmap` |     ✓      |       ✓       | Both                                                                                                                                                                                                       | Optional coordinate hints                                                                            |
| CobbleDollars               | *(TBD)*                         |     —      |       —       | Roadmap                                                                                                                                                                                                    | Not integrated                                                                                       |

**Runtime checks:** `ModLoaderRuntime` (which jar is running) + `ModPresence.isLoaded(modId)` (is the mod in the pack).
Loader-specific APIs also use `OptionalIntegrationMods.hasCreate()` / `hasSophisticatedBackpacks()` so Fabric never
treats Create/SB integrations as active.

**Declared in metadata:** `neoforge.mods.toml` optional entries = NeoForge pack hints. `fabric.mod.json` `recommends` =
Fabric pack hints (no Create — it does not exist on Fabric 1.21.1).

## Summary

| Mod                    | Mod ID                       | Required?               | Used for                                                                                    |
|------------------------|------------------------------|-------------------------|---------------------------------------------------------------------------------------------|
| Minecraft              | `minecraft`                  | **Yes**                 | Base game                                                                                   |
| NeoForge               | `neoforge`                   | **Yes** (NeoForge jar)  | Production loader                                                                           |
| Fabric Loader + API    | `fabricloader`, `fabric-api` | **Yes** (Fabric jar)    | Fabric loader                                                                               |
| Cobblemon              | `cobblemon`                  | **Yes**                 | Capture events, items, spawn pools, `#cobblemon:poke_balls`                                 |
| Fabric Language Kotlin | `fabric-language-kotlin`     | **Yes** (Fabric only)   | Cobblemon is Kotlin; required at runtime on Fabric                                          |
| Kotlin for Forge       | *(library, not a mod)*       | **Yes** (NeoForge only) | Cobblemon runtime on NeoForge (`kotlinforforge-neoforge`)                                   |
| Regions Unexplored     | `regions_unexplored`         | **Optional**            | Migration routes + spawn estimates when mod is loaded                                       |
| Biomes O' Plenty       | `biomesoplenty`              | **Optional**            | Extra route biomes + spawn estimates when mod is loaded                                     |
| Serene Seasons         | `sereneseasons`              | **Optional**            | Real in-game calendar for `MigrationSeasonClock` (Serene Seasons Plus extends the same API) |

Regions Unexplored and Biomes O' Plenty are **not** compile dependencies — only biome IDs in JSON/Java. At runtime,
`MigrationBiomeCatalog` only includes each mod's biomes when that mod is present; without either, routes use
`VanillaSeasonBiomes`. Migration legs always accept any vanilla `minecraft:*` biome for catch credit.

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

|                       |                                                                        |
|-----------------------|------------------------------------------------------------------------|
| **Mod ID**            | `cobblemon`                                                            |
| **Version**           | `1.7.3+1.21.1` (compatible `[1.7.3, 1.8.0)`)                           |
| **Gradle (common)**   | `com.cobblemon:mod:1.7.3+1.21.1`                                       |
| **Gradle (NeoForge)** | `com.cobblemon:neoforge:1.7.3+1.21.1`                                  |
| **Gradle (Fabric)**   | `com.cobblemon:fabric:1.7.3+1.21.1`                                    |
| **Maven**             | `https://artefacts.cobblemon.com/releases/`                            |
| **Source / MDK**      | [gitlab.com/cable-mc/cobblemon](https://gitlab.com/cable-mc/cobblemon) |
| **Modrinth**          | [modrinth.com/mod/cobblemon](https://modrinth.com/mod/cobblemon)       |

**Code touchpoints:** `CobblemonEvents.POKEMON_CAPTURED`, Cobblemon item IDs in kit rewards,
`data/cobblemon/spawn_pool_world/*.json`, `#cobblemon:is_overworld`, `#cobblemon:poke_balls`.

This project is based on the Cobblemon **Architectury multiplatform MDK** pattern (common + NeoForge + Fabric).

## Regions Unexplored (optional)

|                       |                                                                                                                                  |
|-----------------------|----------------------------------------------------------------------------------------------------------------------------------|
| **Mod ID**            | `regions_unexplored`                                                                                                             |
| **Tested / targeted** | `0.5.9` (NeoForge 1.21.1)                                                                                                        |
| **Version range**     | `[0.5.6,)`                                                                                                                       |
| **Modrinth**          | [modrinth.com/mod/regions-unexplored](https://modrinth.com/mod/regions-unexplored)                                               |
| **NeoForge source**   | [github.com/UHQ-GAMES-MODS/REGIONS_UNEXPLORED_FORGE](https://github.com/UHQ-GAMES-MODS/REGIONS_UNEXPLORED_FORGE) (branch `1.21`) |

**Runtime:** `MigrationBiomeCatalog` + `RegionsUnexploredBiomes` when loaded. Spawn JSON lists RU biomes;
`MigrationSpawnPoolIndex` drops them if the mod is absent.

> **Note:** RU `0.6+` renames/removes several biomes referenced in v0.1 routes (e.g. `frozen_tundra`, `temperate_grove`,
`arid_mountains`). Pin **0.5.x** for Rubius until routes are updated, or refresh biome IDs after upgrading RU.

## Biomes O' Plenty (optional)

|                       |                                                                              |
|-----------------------|------------------------------------------------------------------------------|
| **Mod ID**            | `biomesoplenty`                                                              |
| **Tested / targeted** | `21.1.0.7` (NeoForge / Fabric 1.21.1)                                        |
| **Version range**     | `[21.1.0,)`                                                                  |
| **Modrinth**          | [modrinth.com/mod/biomes-o-plenty](https://modrinth.com/mod/biomes-o-plenty) |

**Runtime:** `BiomesOPlentyBiomes` merged into routes when loaded. Extend spawn JSON with `biomesoplenty:*` via
`scripts/generate_migration_spawns.py` when you want migratory spawns in BOP biomes (not yet in default pools).

## Platform runtimes

### NeoForge

|                    |                                                                                            |
|--------------------|--------------------------------------------------------------------------------------------|
| **Loader**         | NeoForge `21.1.182+`                                                                       |
| **Kotlin library** | `thedarkcolour:kotlinforforge-neoforge:5.10.0` (Cobblemon runtime)                         |
| **Maven**          | `https://maven.neoforged.net/releases/`                                                    |
| **KotlinForForge** | [github.com/thedarkcolour/KotlinForForge](https://github.com/thedarkcolour/KotlinForForge) |

Declared in `neoforge/src/main/resources/META-INF/neoforge.mods.toml`.

### Fabric

|            |                                               |
|------------|-----------------------------------------------|
| **Loader** | Fabric Loader `0.17.2+`                       |
| **API**    | Fabric API `0.116.6+1.21.1`                   |
| **Kotlin** | Fabric Language Kotlin `1.13.6+kotlin.2.2.20` |

Declared in `fabric/src/main/resources/fabric.mod.json`.

## Minecraft Comes Alive Reborn (optional)

|                 |                                                                                                                     |
|-----------------|---------------------------------------------------------------------------------------------------------------------|
| **Mod ID**      | `mca`                                                                                                               |
| **Targeted**    | `7.7.x` on Minecraft 1.21.1 (NeoForge / Fabric)                                                                     |
| **CurseForge**  | [minecraft-comes-alive-reborn](https://www.curseforge.com/minecraft/mc-mods/minecraft-comes-alive-reborn)           |
| **Integration** | Passport origin picker deferred until MCA destiny intro closes (`passport.deferOriginPickerForMca`, default `true`) |

MCA has no public callback API; Ancient Extensions waits until MCA removes the intro `Invisibility` effect (after the
destiny screen closes), then opens the passport picker.

## Optional integrations (not required for the mod to load)

Ancient Extensions never hard-depends on content mods below. Use `OptionalIntegrationMods` + `ModLoaderRuntime` (
`common`) and loader metadata (`neoforge.mods.toml` / `fabric.mod.json`).

| Pattern      | What happens without the mod (or on unsupported loader)                                                                                                 |
|--------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Registry** | Loader-specific: Fabric skips Create field blocks entirely; NeoForge always registers sensor/monitor blocks; SB upgrade only on NeoForge when SB loaded |
| **Datapack** | Recipes use `neoforge:mod_loaded` / `fabric:all_mods_loaded` so they never appear in JEI                                                                |
| **Java API** | NeoForge-only code under `neoforge/integration/*` with **compileOnly** jars; `init()` gated by `hasCreate()` / `hasSophisticatedBackpacks()`            |

### Create (`create`) — NeoForge only

|                     |                                                                                                                            |
|---------------------|----------------------------------------------------------------------------------------------------------------------------|
| **Mod ID**          | `create`                                                                                                                   |
| **Official 1.21.1** | **NeoForge only** — no Fabric release                                                                                      |
| **Gradle**          | `modCompileOnly` on NeoForge module only                                                                                   |
| **Content**         | `field_survey_sensor`, `field_survey_monitor` (NeoForge: always registered; Fabric: only if `hasCreate()`, normally never) |
| **Integration**     | `neoforge/integration/create/CreateCompat` — Display Link sources/targets                                                  |
| **Status**          | Implemented (NeoForge)                                                                                                     |

### World-gen mods (`regions_unexplored`, `biomesoplenty`) — both loaders

|                     |                                                                                              |
|---------------------|----------------------------------------------------------------------------------------------|
| **Detection**       | `OptionalIntegrationMods.hasRegionsUnexplored()` / `hasBiomesOPlenty()`                      |
| **Routes**          | `MigrationBiomeCatalog.biomesForSeason` — merges loaded mods; `VanillaSeasonBiomes` if none  |
| **Spawn estimates** | `MigrationSpawnPoolIndex` filters pool biomes to active namespaces at first use              |
| **Catch credit**    | `MigrationLeg.matchesBiome` — always `minecraft:*`; mod biomes only when mod + listed on leg |
| **Status**          | Implemented                                                                                  |

### Sophisticated Backpacks (`sophisticatedbackpacks`)

#### Official (NeoForge / Forge)

|                     |                                                                                                                                                 |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| **Modrinth**        | [Sophisticated Backpacks](https://modrinth.com/mod/sophisticated-backpacks) + [Sophisticated Core](https://modrinth.com/mod/sophisticated-core) |
| **Mod ID**          | `sophisticatedbackpacks`, `sophisticatedcore`                                                                                                   |
| **Our integration** | Field Survey Telemetry upgrade — `neoforge/integration/sophisticated/`, `modCompileOnly` official jars                                          |
| **Active when**     | `OptionalIntegrationMods.hasSophisticatedBackpacks()` → NeoForge **and** mod loaded                                                             |

#### Unofficial Fabric port (community)

|                        |                                                                                                                                                                                                                                                                                           |
|------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Modrinth**           | [SB (Unofficial Fabric port)](https://modrinth.com/mod/sophisticated-backpacks-(unoffical-fabric-port)) + [Core (Unofficial Fabric port)](https://modrinth.com/mod/sophisticated-core-(unofficial-fabric-port))                                                                           |
| **Mod ID**             | Still `sophisticatedbackpacks` / `sophisticatedcore` — `ModPresence.isLoaded("sophisticatedbackpacks")` is **true** on Fabric packs that use the port                                                                                                                                     |
| **Backpacks in pack**  | Work normally (community port)                                                                                                                                                                                                                                                            |
| **Ancient Extensions** | **Does not** register `field_survey_telemetry_upgrade` on Fabric: upgrade code lives in the NeoForge module and targets the **official** SB/Core API. `hasSophisticatedBackpacks()` intentionally requires `ModLoaderRuntime.isNeoForge()` so we never half-enable the feature on Fabric. |
| **Future**             | A Fabric-side port would need `fabric/integration/sophisticated/` + `modCompileOnly` against the unofficial Fabric jars (separate from the NeoForge integration).                                                                                                                         |

| Content                                                                          | Status                                                                |
|----------------------------------------------------------------------------------|-----------------------------------------------------------------------|
| Field Survey Telemetry upgrade (tooltip readout from `FieldSurveyWorldSnapshot`) | Implemented on **NeoForge + official SB**                             |
| Recipe `field_survey_telemetry_upgrade`                                          | NeoForge: loads when SB present; Fabric: disabled via load conditions |

### Map mods — both loaders (presence check only)

| Mod               | Mod ID          | NeoForge | Fabric | Purpose                                           |
|-------------------|-----------------|:--------:|:------:|---------------------------------------------------|
| JourneyMap        | `journeymap`    |    ✓     |   ✓    | Clickable waypoint line (Migration Route Compass) |
| Xaero's Minimap   | `xaerominimap`  |    ✓     |   ✓    | Coordinate hints                                  |
| Xaero's World Map | `xaeroworldmap` |    ✓     |   ✓    | Coordinate hints                                  |

### Quality-of-life — both loaders

| Mod            | Mod ID          | Integration                                                          |
|----------------|-----------------|----------------------------------------------------------------------|
| Serene Seasons | `sereneseasons` | `SereneSeasonsIntegration` — optional real-world season calendar     |
| MCA Reborn     | `mca`           | `McaIntegration` — defer passport stamp until destiny intro ends     |
| Comforts       | `comforts`      | `ComfortsCampCompat` — sleeping bag at survey camp                   |
| Lootr          | `lootr`         | `LootrCampChestCompat` — per-player camp chest when config enabled   |
| JEI            | `jei`           | NeoForge `AncientExtensionsJeiPlugin` (compileOnly + runtime in dev) |

## Planned integrations

| Mod           | Purpose                                | Status  |
|---------------|----------------------------------------|---------|
| CobbleDollars | Optional economy hook for tier rewards | Roadmap |

## Local dev runtime (optional)

To test migration spawns with Regions Unexplored in a NeoForge dev client, add the jar manually or via Modrinth Maven in
`neoforge/build.gradle.kts`:

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
