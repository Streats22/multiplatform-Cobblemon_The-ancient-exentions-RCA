# IDE setup (Cursor / VS Code)

This repo has three Gradle modules: **`:common`**, **`:fabric`**, **`:neoforge`**. See [MODULES.md](MODULES.md).

Red errors on `com.simibubi.create.*` or `net.p3pp3rf1y.*` in **`:neoforge` only** mean the Java language server is not using the same classpath as Gradle. Verify with:

```bash
./gradlew :common:compileJava :common:compileClientJava :fabric:compileJava :neoforge:compileJava
```

## Fix (do all steps)

1. Open the **repository root** as the workspace folder (`multiplatform_Cobblemon_The_ancient_exentions-RCA`).
2. In the project root, run:

```bash
./gradlew setupIde
```

This runs `setupIde` on **all three** Loom modules and generates Eclipse + VS Code metadata. For `:common` it also creates `build/classes/kotlin/client` and `build/resources/client` (Eclipse references them even when empty).

If you see **“Project 'common' is missing required library: …/build/classes/kotlin/client”**, run:

```bash
./gradlew :common:ensureIdeClientOutputs
```

Then **Java: Clean Java Language Server Workspace** → Reload.

Optional: open `ancient-extensions.code-workspace` instead of the folder.

3. Command Palette → **Java: Clean Java Language Server Workspace** → Reload.
4. Wait until **Gradle: Refresh Gradle project** finishes.

## What Gradle provides

| Code | Optional API | Configuration |
|------|----------------|-------------|
| `neoforge/integration/create/*` | Create (slim) | `modCompileOnly` + `compileOnly` + `modLocalRuntime` |
| `neoforge/integration/sophisticated/*` | Sophisticated Core + Backpacks | same |
| `neoforge/integration/jei/*` | JEI NeoForge + common API | `compileOnly` |

`.vscode/settings.json` also lists remapped JAR globs under `.gradle/loom-cache/` as a fallback when Gradle import misses `modCompileOnly`.

## Path with spaces

This repo lives under `Learning Java/`. If the IDE still fails after `setupIde`, try opening a symlink without spaces, e.g.:

```bash
ln -s "/Users/you/StreatsDesign/Learning Java/multiplatform_Cobblemon_The_ancient_exentions-RCA" ~/ancient-extensions
```

Then open `~/ancient-extensions` in Cursor.
