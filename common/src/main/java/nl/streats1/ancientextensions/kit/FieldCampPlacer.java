package nl.streats1.ancientextensions.kit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;

/**
 * Places a small survey camp from the Ancient Professor's kit (mod item, not a structure datapack).
 */
public final class FieldCampPlacer {

    private static final int SEARCH_RADIUS = 5;

    private FieldCampPlacer() {
    }

    public static Optional<CampPlacement> placeCamp(ServerLevel level, BlockPos preferredOrigin, Direction facing) {
        BlockPos center = findCampCenter(level, preferredOrigin);
        if (center == null) {
            return Optional.empty();
        }

        Direction forward = facing;
        BlockPos campfire = center;
        BlockPos lecternPos = center.relative(forward, 2);
        BlockPos chestPos = center.relative(forward.getClockWise(), 2);

        clearColumn(level, campfire, 3);
        clearColumn(level, lecternPos, 2);
        clearColumn(level, chestPos, 2);

        BlockState plank = Blocks.SPRUCE_PLANKS.defaultBlockState();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos floor = campfire.offset(dx, -1, dz);
                if (level.getBlockState(floor).isSolidRender(level, floor)) {
                    level.setBlockAndUpdate(floor, plank);
                }
            }
        }

        BlockState campfireState = Blocks.CAMPFIRE.defaultBlockState()
                .setValue(CampfireBlock.LIT, true)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, forward.getOpposite());
        level.setBlockAndUpdate(campfire, campfireState);

        placeTentRing(level, campfire);
        placeIfAir(level, campfire.above(), Blocks.LANTERN.defaultBlockState());
        placeIfAir(level, lecternPos, Blocks.LECTERN.defaultBlockState());
        placeIfAir(level, chestPos, Blocks.CHEST.defaultBlockState());
        placeBanner(level, campfire.relative(forward.getCounterClockWise(), 2), forward);

        return Optional.of(new CampPlacement(campfire, chestPos, lecternPos));
    }

    public static void placeBriefingOnLectern(ServerLevel level, BlockPos lecternPos, ItemStack book) {
        if (level.getBlockEntity(lecternPos) instanceof LecternBlockEntity lectern) {
            lectern.setBook(book.copy());
        }
    }

    public static void fillCampChest(ServerLevel level, BlockPos chestPos, Iterable<ItemStack> supplies) {
        if (!(level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest)) {
            return;
        }
        int slot = 0;
        for (ItemStack stack : supplies) {
            if (stack.isEmpty() || slot >= chest.getContainerSize()) {
                continue;
            }
            chest.setItem(slot++, stack.copy());
        }
    }

    private static BlockPos findCampCenter(ServerLevel level, BlockPos origin) {
        BlockPos best = null;
        int bestScore = Integer.MIN_VALUE;

        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                BlockPos candidate = level.getHeightmapPos(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        origin.offset(dx, 0, dz)
                );
                int score = scoreCampSite(level, candidate, origin);
                if (score > bestScore) {
                    bestScore = score;
                    best = candidate;
                }
            }
        }

        return bestScore >= 0 ? best : null;
    }

    private static int scoreCampSite(ServerLevel level, BlockPos center, BlockPos preferred) {
        BlockPos below = center.below();
        if (!level.getBlockState(below).isSolidRender(level, below)) {
            return -1;
        }
        if (!level.getBlockState(center).canBeReplaced()) {
            return -1;
        }
        if (!level.getBlockState(center.above()).canBeReplaced()) {
            return -1;
        }
        return 100 - center.distManhattan(preferred);
    }

    private static void clearColumn(ServerLevel level, BlockPos base, int height) {
        for (int dy = 0; dy < height; dy++) {
            BlockPos pos = base.offset(0, dy, 0);
            if (level.getBlockState(pos).canBeReplaced()) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    private static void placeTentRing(ServerLevel level, BlockPos center) {
        placeIfAir(level, center.north(), Blocks.WHITE_WOOL.defaultBlockState());
        placeIfAir(level, center.south(), Blocks.WHITE_WOOL.defaultBlockState());
        placeIfAir(level, center.east(), Blocks.WHITE_WOOL.defaultBlockState());
        placeIfAir(level, center.west(), Blocks.WHITE_WOOL.defaultBlockState());
        placeIfAir(level, center.north().east(), Blocks.WHITE_CARPET.defaultBlockState());
        placeIfAir(level, center.north().west(), Blocks.WHITE_CARPET.defaultBlockState());
        placeIfAir(level, center.south().east(), Blocks.WHITE_CARPET.defaultBlockState());
        placeIfAir(level, center.south().west(), Blocks.WHITE_CARPET.defaultBlockState());
        placeIfAir(level, center.north(2), Blocks.SPRUCE_FENCE.defaultBlockState());
        placeIfAir(level, center.south(2), Blocks.SPRUCE_FENCE.defaultBlockState());
        placeIfAir(level, center.east(2), Blocks.SPRUCE_FENCE.defaultBlockState());
        placeIfAir(level, center.west(2), Blocks.SPRUCE_FENCE.defaultBlockState());
    }

    private static void placeBanner(ServerLevel level, BlockPos pos, Direction facing) {
        if (!level.getBlockState(pos).canBeReplaced()) {
            return;
        }
        level.setBlockAndUpdate(pos, Blocks.WHITE_BANNER.defaultBlockState()
                .setValue(BlockStateProperties.ROTATION_16, rotationFromDirection(facing)));
    }

    private static int rotationFromDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> 8;
            case EAST -> 12;
            case SOUTH -> 0;
            case WEST -> 4;
            default -> 0;
        };
    }

    private static void placeIfAir(ServerLevel level, BlockPos pos, BlockState state) {
        if (level.getBlockState(pos).canBeReplaced()) {
            level.setBlockAndUpdate(pos, state);
        }
    }
}
