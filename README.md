# The Ancient Extensions

Glue mod for [Rubius Cobblemon](https://modrinth.com/modpack/rubius-cobblemon), built on the Cobblemon multiplatform MDK (NeoForge 1.21.1 + Cobblemon 1.7.3).

See [docs/DESIGN.md](docs/DESIGN.md) for locked design decisions and [docs/DEPENDENCIES.md](docs/DEPENDENCIES.md) for required/recommended mods, version pins, and upstream MDK links.

## Features

| System | Behavior |
|--------|----------|
| **Ancient Professor's Field Kit** | Granted on **first join** to a world (not craftable); right-click to pitch camp + starter supplies; available in **creative** for testing |
| **Regional Survey** | Catch-only dex: Research Points and tiers from `POKEMON_CAPTURED` |
| **Field Survey Tablet** | Pocket dashboard on join; shortcuts to journal, passport, route chart, and rank claims |
| **Seasonal migration** | 3-leg routes per season; migratory species in route biomes; repeatable each season with diminishing RP; additive spawn pools |

## Commands

- `/ancientextensions survey`
- `/ancientextensions migration`
- `/ancientextensions deploykit`
- `/ancientextensions givekit` (op, testing)

## Development

```bash
./gradlew :neoforge:runClient
./gradlew :neoforge:build
```

Test: join a **new world** (kit in inventory), creative tab **Ancient Extensions**, or `/ancientextensions givekit`.

## Pack integration

Add `ancient-extensions-neoforge-*.jar` to Rubius Cobblemon. Kit auto-grants on first join — see [pack_integration/RUBIUS_STARTER_QUEST.md](pack_integration/RUBIUS_STARTER_QUEST.md).

**Pack mod context** (see [docs/DEPENDENCIES.md](docs/DEPENDENCIES.md)):

| Mod | Role |
|-----|------|
| [Cobblemon](https://modrinth.com/mod/cobblemon) 1.7.3 | **Required** — capture API, items, spawn pools |
| [Regions Unexplored](https://modrinth.com/mod/regions-unexplored) 0.5.x | **Recommended** — migration route biomes |

## Roadmap

- Serene Seasons Plus calendar hook
- CobbleDollars tier rewards (optional economy hook)
