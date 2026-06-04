package nl.streats1.ancientextensions.block;

import nl.streats1.ancientextensions.pouch.PouchTier;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class PokeballPouchBlockState {

    public static final EnumProperty<PouchTier> TIER = EnumProperty.create("tier", PouchTier.class);

    private PokeballPouchBlockState() {
    }
}
