package nl.streats1.ancientextensions.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;

/**
 * Optional Comforts integration — places a white or brown sleeping bag when Comforts is installed.
 */
public final class ComfortsCampCompat {

    private static final ResourceLocation WHITE_BAG = ResourceLocation.fromNamespaceAndPath("comforts", "sleeping_bag_white");
    private static final ResourceLocation BROWN_BAG = ResourceLocation.fromNamespaceAndPath("comforts", "sleeping_bag_brown");

    private ComfortsCampCompat() {
    }

    public static boolean isComfortsInstalled() {
        return resolveSleepingBag(WHITE_BAG) != null || resolveSleepingBag(BROWN_BAG) != null;
    }

    /** Places a Comforts sleeping bag (foot + head) when the mod blocks are registered. */
    public static void placeSleepingBag(
            ServerLevel level,
            BlockPos footPos,
            BlockPos headPos,
            Direction forward,
            RandomSource random,
            ServerPlayer builder
    ) {
        Block bagBlock = pickSleepingBagBlock(random);
        if (!(bagBlock instanceof BedBlock bedBlock)) {
            return;
        }

        BlockState foot = bedBlock.defaultBlockState()
                .setValue(BedBlock.FACING, forward)
                .setValue(BedBlock.PART, BedPart.FOOT);
        BlockState head = bedBlock.defaultBlockState()
                .setValue(BedBlock.FACING, forward)
                .setValue(BedBlock.PART, BedPart.HEAD);

        placeIfClear(level, footPos, foot, builder);
        placeIfClear(level, headPos, head, builder);
    }

    private static Block pickSleepingBagBlock(RandomSource random) {
        Block white = resolveSleepingBag(WHITE_BAG);
        Block brown = resolveSleepingBag(BROWN_BAG);
        if (white != null && brown != null) {
            return random.nextBoolean() ? white : brown;
        }
        if (white != null) {
            return white;
        }
        return brown;
    }

    private static Block resolveSleepingBag(ResourceLocation id) {
        if (!BuiltInRegistries.BLOCK.containsKey(id)) {
            return null;
        }
        Block block = BuiltInRegistries.BLOCK.get(id);
        return block == Blocks.AIR ? null : block;
    }

    private static void placeIfClear(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer builder) {
        if (!level.getBlockState(pos).canBeReplaced()) {
            return;
        }
        AABB tallBox = new AABB(pos).expandTowards(0, 1, 0).inflate(0.01);
        if (builder.getBoundingBox().intersects(tallBox)) {
            return;
        }
        level.setBlockAndUpdate(pos, state);
    }
}
