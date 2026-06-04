# Assets (Fabric + NeoForge)

All mod resources live in **one place**:

```
common/src/main/resources/
├── assets/ancient_extensions/   # textures, models, lang, item definitions
├── data/ancient_extensions/     # recipes, advancements
├── data/cobblemon/              # migration spawn pools
└── pack.mcmeta
```

Fabric and NeoForge **do not** ship their own `assets/` or `data/` trees. Both platform JARs embed `:common` via Architectury shadow/remap, so every texture and JSON is identical on both loaders.

## Code references

- GUI textures: `AncientExtensionsAssets` (used by all client screens)
- Mod icon: `assets/ancient_extensions/icon.png` — referenced in `fabric.mod.json` and `neoforge.mods.toml` as `AncientExtensionsAssets.MOD_ICON_PATH`

## Adding or changing art

Edit or add files under `common/src/main/resources/assets/ancient_extensions/` only. Do not copy assets into `fabric/` or `neoforge/`.
