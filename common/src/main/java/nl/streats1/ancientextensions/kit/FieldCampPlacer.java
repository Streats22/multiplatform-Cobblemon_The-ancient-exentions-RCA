package nl.streats1.ancientextensions.kit;

import nl.streats1.ancientextensions.compat.ComfortsCampCompat;
import nl.streats1.ancientextensions.compat.LootrCampChestCompat;
import nl.streats1.ancientextensions.config.CampConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.phys.AABB;
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
            boolean sleepingBed,
            ServerPlayer builder
    ) {
        BlockPos center = findCampCenter(level, preferredOrigin, builder);
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
        BlockPos safeStand = center.relative(forward, 3);

        clearCampArea(level, center, forward, left);
        layFloor(level, center, forward, left);
        placeCampfire(level, campfire, back);
        placeSmallTent(level, center, forward, back, left, right, builder);
        placeBedroll(level, center, forward, left, sleepingBed, builder);
        placeIfClear(level, lecternPos, Blocks.LECTERN.defaultBlockState(), builder);
        placeIfClear(level, chestPos, Blocks.CHEST.defaultBlockState(), builder);

        return Optional.of(new CampPlacement(campfire, chestPos, lecternPos, bedrollHead, safeStand));
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

    private static void placeSmallTent(
            ServerLevel level,
            BlockPos center,
            Direction forward,
            Direction back,
            Direction left,
            Direction right,
            ServerPlayer builder
    ) {
        BlockPos backRow = center.relative(back, 2);

        for (int side = -1; side <= 1; side++) {
            BlockPos wallFoot = backRow.relative(left, side);
            placeIfClear(level, wallFoot, Blocks.WHITE_WOOL.defaultBlockState(), builder);
            placeIfClear(level, wallFoot.above(), Blocks.WHITE_WOOL.defaultBlockState(), builder);
        }

        BlockPos midBack = center.relative(back);
        placeIfClear(level, midBack.relative(left), Blocks.WHITE_WOOL.defaultBlockState(), builder);
        placeIfClear(level, midBack.relative(right), Blocks.WHITE_WOOL.defaultBlockState(), builder);
        placeIfClear(level, midBack.above(), Blocks.WHITE_WOOL.defaultBlockState(), builder);

        BlockPos leftPole = backRow.relative(left, 2);
        BlockPos rightPole = backRow.relative(right, 2);
        placeIfClear(level, leftPole, Blocks.SPRUCE_FENCE.defaultBlockState(), builder);
        placeIfClear(level, rightPole, Blocks.SPRUCE_FENCE.defaultBlockState(), builder);
        placeIfClear(level, leftPole.above(), Blocks.SPRUCE_FENCE.defaultBlockState(), builder);
        placeIfClear(level, rightPole.above(), Blocks.SPRUCE_FENCE.defaultBlockState(), builder);

        placeIfClear(level, midBack.relative(left).above(), Blocks.WHITE_CARPET.defaultBlockState(), builder);
        placeIfClear(level, midBack.relative(right).above(), Blocks.WHITE_CARPET.defaultBlockState(), builder);

        placeBanner(level, backRow.above(2), forward);
    }

    /** Bedroll in front of the fire — Comforts sleeping bag, vanilla bed, or decorative carpet. */
    private static void placeBedroll(
            ServerLevel level,
            BlockPos center,
            Direction forward,
            Direction left,
            boolean sleepingBed,
            ServerPlayer builder
    ) {
        BlockPos rollFoot = center.relative(forward);
        BlockPos rollHead = center.relative(forward, 2);

        if (ComfortsCampCompat.isComfortsInstalled()) {
            ComfortsCampCompat.placeSleepingBag(level, rollFoot, rollHead, forward, level.random, builder);
            return;
        }

        if (sleepingBed) {
            BlockState foot = Blocks.BROWN_BED.defaultBlockState()
                    .setValue(BedBlock.FACING, forward)
                    .setValue(BedBlock.PART, BedPart.FOOT);
            BlockState head = Blocks.BROWN_BED.defaultBlockState()
                    .setValue(BedBlock.FACING, forward)
                    .setValue(BedBlock.PART, BedPart.HEAD);
            placeIfClear(level, rollFoot, foot, builder);
            placeIfClear(level, rollHead, head, builder);
            return;
        }
        placeIfClear(level, rollFoot, Blocks.BROWN_CARPET.defaultBlockState(), builder);
        placeIfClear(level, rollHead, Blocks.BROWN_CARPET.defaultBlockState(), builder);
        placeIfClear(level, rollHead.relative(left), Blocks.WHITE_CARPET.defaultBlockState(), builder);
    }

    private static BlockPos findCampCenter(ServerLevel level, BlockPos origin, ServerPlayer builder) {
        BlockPos best = null;
        int bestScore = Integer.MIN_VALUE;

        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                BlockPos candidate = level.getHeightmapPos(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        origin.offset(dx, 0, dz)
                );
                int score = scoreCampSite(level, candidate, origin, builder);
                if (score > bestScore) {
                    bestScore = score;
                    best = candidate;
                }
            }
        }

        return bestScore >= 0 ? best : null;
    }

    private static int scoreCampSite(ServerLevel level, BlockPos center, BlockPos preferred, ServerPlayer builder) {
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
        if (campFootprintOverlapsPlayer(center, builder)) {
            return -1;
        }
        return 100 - center.distManhattan(preferred);
    }

    /** True if the 5×5 camp pad would intersect the builder's hitbox. */
    private static boolean campFootprintOverlapsPlayer(BlockPos center, ServerPlayer builder) {
        AABB playerBox = builder.getBoundingBox();
        for (int dx = -FLOOR_RADIUS; dx <= FLOOR_RADIUS; dx++) {
            for (int dz = -FLOOR_RADIUS; dz <= FLOOR_RADIUS; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                AABB blockBox = new AABB(pos);
                if (playerBox.intersects(blockBox)) {
                    return true;
                }
                if (playerBox.intersects(blockBox.move(0, 1, 0))) {
                    return true;
                }
            }
        }
        return false;
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

    private static void placeIfClear(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer builder) {
        if (!level.getBlockState(pos).canBeReplaced()) {
            return;
        }
        if (entityOccupies(level, pos, builder)) {
            return;
        }
        level.setBlockAndUpdate(pos, state);
    }

    private static boolean entityOccupies(ServerLevel level, BlockPos pos, ServerPlayer builder) {
        AABB tallBox = new AABB(pos).expandTowards(0, 1, 0).inflate(0.01);
        return builder.getBoundingBox().intersects(tallBox);
    }
}
