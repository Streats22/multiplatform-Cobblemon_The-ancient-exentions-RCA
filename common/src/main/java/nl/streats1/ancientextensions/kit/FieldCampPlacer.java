package nl.streats1.ancientextensions.kit;

import nl.streats1.ancientextensions.compat.LootrCampChestCompat;
import nl.streats1.ancientextensions.config.CampConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;

/**
 * Places a small comfort survey camp from the Ancient Professor's kit.
 * Layout (relative to player facing): tent behind the fire, bedroll ahead, chest and lectern to the sides.
 */
public final class FieldCampPlacer {

    private static final int SEARCH_RADIUS = 5;
    private static final int FLOOR_RADIUS = 2;

    private FieldCampPlacer() {
    }

    public static Optional<CampPlacement> placeCamp(
            ServerLevel level,
            BlockPos preferredOrigin,
            Direction facing,
            boolean sleepingBed
    ) {
        BlockPos center = findCampCenter(level, preferredOrigin);
        if (center == null) {
            return Optional.empty();
        }

        Direction forward = facing;
        Direction back = forward.getOpposite();
        Direction left = forward.getCounterClockWise();
        Direction right = forward.getClockWise();

        BlockPos campfire = center;
        BlockPos chestPos = center.relative(left, 2);
        BlockPos lecternPos = center.relative(right, 2);
        BlockPos bedrollHead = center.relative(forward, 2);

        clearCampArea(level, center, forward, left);

        layFloor(level, center, forward, left);
        placeCampfire(level, campfire, back);
        placeSmallTent(level, center, forward, back, left, right);
        placeBedroll(level, center, forward, left, sleepingBed);
        placeIfAir(level, lecternPos, Blocks.LECTERN.defaultBlockState());
        placeIfAir(level, chestPos, Blocks.CHEST.defaultBlockState());

        return Optional.of(new CampPlacement(campfire, chestPos, lecternPos, bedrollHead));
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

        if (CampConfig.useLootrCampChest()) {
            LootrCampChestCompat.convertFilledChest(level, chestPos);
        }
    }

    private static void clearCampArea(ServerLevel level, BlockPos center, Direction forward, Direction left) {
        for (int f = -FLOOR_RADIUS; f <= FLOOR_RADIUS; f++) {
            for (int s = -FLOOR_RADIUS; s <= FLOOR_RADIUS; s++) {
                BlockPos base = center.relative(forward, f).relative(left, s);
                clearColumn(level, base, 4);
            }
        }
    }

    private static void layFloor(ServerLevel level, BlockPos center, Direction forward, Direction left) {
        BlockState plank = Blocks.SPRUCE_PLANKS.defaultBlockState();
        for (int f = -FLOOR_RADIUS; f <= FLOOR_RADIUS; f++) {
            for (int s = -FLOOR_RADIUS; s <= FLOOR_RADIUS; s++) {
                BlockPos floor = center.relative(forward, f).relative(left, s).below();
                if (level.getBlockState(floor).isSolidRender(level, floor) || level.getBlockState(floor).canBeReplaced()) {
                    level.setBlockAndUpdate(floor, plank);
                }
            }
        }
    }

    private static void placeCampfire(ServerLevel level, BlockPos pos, Direction back) {
        BlockState campfireState = Blocks.CAMPFIRE.defaultBlockState()
                .setValue(CampfireBlock.LIT, true)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, back);
        level.setBlockAndUpdate(pos, campfireState);
    }

    /** Lean-to tent behind the campfire — open toward the player. */
    private static void placeSmallTent(
            ServerLevel level,
            BlockPos center,
            Direction forward,
            Direction back,
            Direction left,
            Direction right
    ) {
        BlockPos backRow = center.relative(back, 2);

        for (int side = -1; side <= 1; side++) {
            BlockPos wallFoot = backRow.relative(left, side);
            placeIfAir(level, wallFoot, Blocks.WHITE_WOOL.defaultBlockState());
            placeIfAir(level, wallFoot.above(), Blocks.WHITE_WOOL.defaultBlockState());
        }

        BlockPos midBack = center.relative(back);
        placeIfAir(level, midBack.relative(left), Blocks.WHITE_WOOL.defaultBlockState());
        placeIfAir(level, midBack.relative(right), Blocks.WHITE_WOOL.defaultBlockState());
        placeIfAir(level, midBack.above(), Blocks.WHITE_WOOL.defaultBlockState());

        BlockPos leftPole = backRow.relative(left, 2);
        BlockPos rightPole = backRow.relative(right, 2);
        placeIfAir(level, leftPole, Blocks.SPRUCE_FENCE.defaultBlockState());
        placeIfAir(level, rightPole, Blocks.SPRUCE_FENCE.defaultBlockState());
        placeIfAir(level, leftPole.above(), Blocks.SPRUCE_FENCE.defaultBlockState());
        placeIfAir(level, rightPole.above(), Blocks.SPRUCE_FENCE.defaultBlockState());

        placeIfAir(level, midBack.relative(left).above(), Blocks.WHITE_CARPET.defaultBlockState());
        placeIfAir(level, midBack.relative(right).above(), Blocks.WHITE_CARPET.defaultBlockState());

        placeBanner(level, backRow.above(2), forward);
    }

    /** Bedroll in front of the fire — carpet or a real bed that sets spawn. */
    private static void placeBedroll(
            ServerLevel level,
            BlockPos center,
            Direction forward,
            Direction left,
            boolean sleepingBed
    ) {
        BlockPos rollFoot = center.relative(forward);
        BlockPos rollHead = center.relative(forward, 2);
        if (sleepingBed) {
            BlockState foot = Blocks.BROWN_BED.defaultBlockState()
                    .setValue(BedBlock.FACING, forward)
                    .setValue(BedBlock.PART, BedPart.FOOT);
            BlockState head = Blocks.BROWN_BED.defaultBlockState()
                    .setValue(BedBlock.FACING, forward)
                    .setValue(BedBlock.PART, BedPart.HEAD);
            placeIfAir(level, rollFoot, foot);
            placeIfAir(level, rollHead, head);
            return;
        }
        placeIfAir(level, rollFoot, Blocks.BROWN_CARPET.defaultBlockState());
        placeIfAir(level, rollHead, Blocks.BROWN_CARPET.defaultBlockState());
        placeIfAir(level, rollHead.relative(left), Blocks.WHITE_CARPET.defaultBlockState());
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
        for (int dy = 1; dy <= 3; dy++) {
            if (!level.getBlockState(center.above(dy)).canBeReplaced()) {
                return -1;
            }
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

    private static void placeBanner(ServerLevel level, BlockPos pos, Direction facing) {
        if (!level.getBlockState(pos).canBeReplaced()) {
            return;
        }
        level.setBlockAndUpdate(pos, Blocks.GREEN_BANNER.defaultBlockState()
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
