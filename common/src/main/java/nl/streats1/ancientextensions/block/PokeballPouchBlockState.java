package nl.streats1.ancientextensions.block;

import net.minecraft.world.level.block.state.properties.EnumProperty;

import nl.streats1.ancientextensions.pouch.PouchTier;

public final class PokeballPouchBlockState {

    public static final EnumProperty<PouchTier> TIER = EnumProperty.create("tier", PouchTier.class);

    private PokeballPouchBlockState() {
    }
}
