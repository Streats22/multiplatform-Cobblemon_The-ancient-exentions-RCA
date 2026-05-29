# Rubius Cobblemon — starter quest hook (Ancient Professor's Kit)

## How players get the kit

The mod **automatically gives** `ancient_extensions:ancient_professors_kit` the **first time** a player joins a world (once per save, stored on player data).

- **Not craftable** (no recipe).
- **Creative mode:** Ancient Extensions tab (for testing).
- **Ops:** `/ancientextensions givekit`

You do **not** need to reward the kit item in FTB Quests unless you want a redundant backup—the mod handles delivery.

## Mod dependency

Add to the modpack:

`ancient-extensions-neoforge-0.1.0-SNAPSHOT.jar` (or current build from `neoforge/build/libs/`)

## Quest flow (recommended)

| Step | Task type | Details |
|------|-----------|---------|
| 1 | **Checklist / advancement** | "Pitch your survey camp" — advancement `ancient_extensions:survey/pitch_field_camp` |
| 2 | **Catch task** | Catch 3 unique species (Cobblemon quest integration or manual) |
| 3 | **Optional** | Hint: `/ancientextensions survey` |

## FTB Quests (snippet)

- **Do not** require giving the kit item (mod grants on login).
- **Task:** Advancement completion — `ancient_extensions:survey/pitch_field_camp`
- **Subtitle:** *Right-click the Field Kit on open ground in front of you. Read the Regional Survey Briefing.*

## What the kit does (for quest text)

- Places a **survey camp** (spruce floor, wool tent, campfire, lectern, chest)
- Gives **balls, medicine, berries**, and **Regional Survey Briefing**
- **One deploy per player** per world (tracked separately from the login grant)

## Testing

1. New world → join → kit in inventory.
2. Rejoin same world → no second kit.
3. New save/world → kit granted again.
4. `/ancientextensions givekit` for manual testing.
