package nl.streats1.ancientextensions.kit;

import net.minecraft.core.BlockPos;

public record CampPlacement(
        BlockPos campfirePos,
        BlockPos chestPos,
        BlockPos lecternPos,
        BlockPos bedrollHeadPos,
        BlockPos safeStandPos
) {
}
