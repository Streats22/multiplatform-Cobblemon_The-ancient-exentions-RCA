# Project modules (`:common`, `:fabric`, `:neoforge`)

Architectury multiplatform layout for **The Ancient Extensions**.

```
ancient-extensions/
├── common/          ← shared game logic, assets, datapacks
├── neoforge/      ← NeoForge loader (Rubius target)
└── fabric/        ← Fabric loader
```

## `:common`

|                   |                                                                    |
|-------------------|--------------------------------------------------------------------|
| **Role**          | Shared code for both loaders                                       |
| **Sources**       | `src/main/java`, `src/client/java` (screens only — no loader APIs) |
| **Resources**     | All `assets/` and `data/` (loaders must not duplicate them)        |
| **Depends on**    | Cobblemon `mod`, Minecraft (Loom)                                  |
| **Optional APIs** | None in Gradle — no Create, SB, or JEI here                        |

Platform-specific integrations live only in `:neoforge` or `:fabric`.

## `:neoforge`

|                          |                                                                         |
|--------------------------|-------------------------------------------------------------------------|
| **Role**                 | Production jar for Rubius Cobblemon (NeoForge 1.21.1)                   |
| **Depends on**           | `:common` (`namedElements` + `transformProductionFabric` for shadow)    |
| **Optional compile**     | Create (Display Link), Sophisticated Backpacks (telemetry upgrade), JEI |
| **Integration packages** | `neoforge/integration/create/`, `…/sophisticated/`, `…/jei/`            |

Blocks (field sensor/monitor) always register on NeoForge; Create is only required for recipes + Display Link.

## `:fabric`

|                      |                                                                 |
|----------------------|-----------------------------------------------------------------|
| **Role**             | Fabric build of the same mod                                    |
| **Depends on**       | `:common`, Fabric API, Fabric Language Kotlin, Cobblemon Fabric |
| **Optional compile** | JEI only (`fabric/integration/jei/`)                            |
| **Not present**      | Create integration, SB telemetry upgrade (NeoForge-only code)   |

Field sensor/monitor blocks are **not** registered on Fabric (no Create on 1.21.1).

## Build commands

```bash
./gradlew :common:build          # common only
./gradlew :neoforge:build        # NeoForge release jar
./gradlew :fabric:build          # Fabric release jar
./gradlew build                  # everything
```

## IDE (all three modules)

```bash
./gradlew setupIde
```

Then **Java: Clean Java Language Server Workspace** → Reload. See [IDE.md](IDE.md).

Open the **repository root** in Cursor so Gradle imports `:common`, `:fabric`, and `:neoforge` together.
