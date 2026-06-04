package nl.streats1.ancientextensions.kit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

import nl.streats1.ancientextensions.compat.ComfortsCampCompat;
import nl.streats1.ancientextensions.compat.LootrCampChestCompat;
import nl.streats1.ancientextensions.config.CampConfig;

/**
 * Places a field survey camp from the Ancient Professor's kit.
 * Layout (relative to player facing): large tent behind the fire, bedroll ahead, chest and lectern to the sides.
 */
public final class FieldCampPlacer {

    private static final int SEARCH_RADIUS = 8;
    /**
     * Search up/down from the builder's feet so caves do not snap to the surface.
     */
    private static final int VERTICAL_SEARCH = 10;
    private static final int CAMP_FORWARD_MIN = -8;
    private static final int CAMP_FORWARD_MAX = 4;
    private static final int CAMP_SIDE_MIN = -5;
    private static final int CAMP_SIDE_MAX = 5;
    /** Depth of the tent canopy behind the campfire. */
    private static final int TENT_LENGTH = 6;
    /** Interior span of the tent (side walls inclusive). */
    private static final int TENT_WIDTH = 7;
    /** Walk-in height under the wool canopy. */
    private static final int TENT_HEIGHT = 5;
    private static final int SIDE_FEATURE_OFFSET = 4;

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
        BlockPos chestPos = center.relative(left, SIDE_FEATURE_OFFSET);
        BlockPos lecternPos = center.relative(right, SIDE_FEATURE_OFFSET);
        BlockPos bedrollHead = center.relative(forward, 2);
        BlockPos safeStand = center.relative(forward, 4);

        clearCampArea(level, center, forward, left);
        layFloor(level, center, forward, left);
        placeCampfire(level, campfire, back);
        placeSurveyTent(level, center, forward, back, left, builder);
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
        for (int f = CAMP_FORWARD_MIN; f <= CAMP_FORWARD_MAX; f++) {
            for (int s = CAMP_SIDE_MIN; s <= CAMP_SIDE_MAX; s++) {
                BlockPos base = center.relative(forward, f).relative(left, s);
                clearColumn(level, base, TENT_HEIGHT + 2);
            }
        }
    }

    private static void layFloor(ServerLevel level, BlockPos center, Direction forward, Direction left) {
        BlockState plank = Blocks.SPRUCE_PLANKS.defaultBlockState();
        for (int f = CAMP_FORWARD_MIN; f <= CAMP_FORWARD_MAX; f++) {
            for (int s = CAMP_SIDE_MIN; s <= CAMP_SIDE_MAX; s++) {
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

    /**
     * Ridge tent behind the fire — A-frame profile (narrow peak, wide base, center pole, corner stakes):
     * <pre>
     *        x
     *       x|x
     *      x | x
     *     x  |  x
     *    x |   | x
     * </pre>
     * Open front faces the campfire; back wall is closed.
     */
    private static void placeSurveyTent(
            ServerLevel level,
            BlockPos center,
            Direction forward,
            Direction back,
            Direction left,
            ServerPlayer builder
    ) {
        BlockState wool = Blocks.WHITE_WOOL.defaultBlockState();
        BlockState frame = Blocks.DARK_OAK_FENCE.defaultBlockState();
        BlockState frameSlab = Blocks.DARK_OAK_SLAB.defaultBlockState()
                .setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP);
        int maxHalfWidth = TENT_WIDTH / 2;
        int doorPostOffset = 1;

        for (int depth = 1; depth <= TENT_LENGTH; depth++) {
            BlockPos row = center.relative(back, depth);
            boolean frontFace = depth == 1;

            for (int h = 0; h < TENT_HEIGHT; h++) {
                placeIfClear(level, row.above(h), frame, builder);
                for (int cornerOffset : new int[]{-maxHalfWidth, maxHalfWidth}) {
                    placeIfClear(level, row.relative(left, cornerOffset).above(h), frame, builder);
                }
                if (frontFace) {
                    for (int doorOffset : new int[]{-doorPostOffset, doorPostOffset}) {
                        placeIfClear(level, row.relative(left, doorOffset).above(h), frame, builder);
                    }
                }
            }
        }

        for (int depth = 1; depth <= TENT_LENGTH; depth++) {
            BlockPos row = center.relative(back, depth);
            boolean frontFace = depth == 1;
            boolean backFace = depth == TENT_LENGTH;

            for (int h = 0; h < TENT_HEIGHT; h++) {
                int halfWidth = tentHalfWidthAtHeight(h);
                if (halfWidth == 0) {
                    continue;
                }

                for (int sideOffset = -halfWidth; sideOffset <= halfWidth; sideOffset++) {
                    if (sideOffset == 0) {
                        continue;
                    }
                    int absOffset = Math.abs(sideOffset);
                    if (absOffset == maxHalfWidth || (frontFace && absOffset == doorPostOffset)) {
                        continue;
                    }
                    if (frontFace && h <= 2 && absOffset < halfWidth) {
                        continue;
                    }

                    BlockPos canvas = row.relative(left, sideOffset).above(h);
                    if (backFace && absOffset <= halfWidth) {
                        placeIfClear(level, canvas, wool, builder);
                    } else if (absOffset == halfWidth) {
                        placeIfClear(level, canvas, wool, builder);
                    }
                }
            }

            if (frontFace) {
                placeEntranceArch(
                        level,
                        row.relative(left, -maxHalfWidth),
                        forward.getClockWise(),
                        builder
                );
                placeEntranceArch(
                        level,
                        row.relative(left, maxHalfWidth),
                        forward.getCounterClockWise(),
                        builder
                );
                placeIfClear(level, row.relative(left, -maxHalfWidth).above(TENT_HEIGHT), frameSlab, builder);
                placeIfClear(level, row.relative(left, maxHalfWidth).above(TENT_HEIGHT), frameSlab, builder);
            }
        }

        BlockPos backRidge = center.relative(back, TENT_LENGTH).above(TENT_HEIGHT);
        placeIfClear(level, backRidge, frameSlab, builder);
        placeBanner(level, backRidge.above(1), forward);
    }

    /** Lateral half-width of the canvas at each height level (0 = floor, peak at {@code TENT_HEIGHT - 1}). */
    private static int tentHalfWidthAtHeight(int heightLevel) {
        int maxHalfWidth = TENT_WIDTH / 2;
        int levelsBelowPeak = TENT_HEIGHT - 1 - heightLevel;
        return Math.max(0, maxHalfWidth - levelsBelowPeak);
    }

    private static void placeEntranceArch(ServerLevel level, BlockPos corner, Direction inward, ServerPlayer builder) {
        BlockState lower = Blocks.DARK_OAK_STAIRS.defaultBlockState()
                .setValue(StairBlock.FACING, inward)
                .setValue(StairBlock.HALF, Half.BOTTOM);
        BlockState upper = lower.setValue(StairBlock.HALF, Half.TOP);
        placeIfClear(level, corner.above(TENT_HEIGHT - 2), lower, builder);
        placeIfClear(level, corner.above(TENT_HEIGHT - 1), upper, builder);
    }

    /**
     * Bedroll in front of the fire — Comforts sleeping bag, vanilla bed, or decorative carpet.
     */
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
        int refY = builder.blockPosition().getY();
        BlockPos best = null;
        int bestScore = Integer.MIN_VALUE;

        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                for (int dy = -VERTICAL_SEARCH; dy <= VERTICAL_SEARCH; dy++) {
                    BlockPos candidate = new BlockPos(x, refY + dy, z);
                    int score = scoreCampSite(level, candidate, origin, builder);
                    if (score > bestScore) {
                        bestScore = score;
                        best = candidate;
                    }
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
        for (int dy = 1; dy <= TENT_HEIGHT + 1; dy++) {
            if (!level.getBlockState(center.above(dy)).canBeReplaced()) {
                return -1;
            }
        }
        Direction forward = builder.getDirection();
        if (campFootprintOverlapsPlayer(center, builder, forward, forward.getCounterClockWise())) {
            return -1;
        }
        int horizontal = center.distManhattan(preferred);
        int vertical = Math.abs(center.getY() - preferred.getY());
        return 120 - horizontal * 4 - vertical * 8;
    }

    private static boolean campFootprintOverlapsPlayer(
            BlockPos center,
            ServerPlayer builder,
            Direction forward,
            Direction left
    ) {
        AABB playerBox = builder.getBoundingBox();
        for (int f = CAMP_FORWARD_MIN; f <= CAMP_FORWARD_MAX; f++) {
            for (int s = CAMP_SIDE_MIN; s <= CAMP_SIDE_MAX; s++) {
                for (int h = 0; h <= TENT_HEIGHT + 1; h++) {
                    BlockPos pos = center.relative(forward, f).relative(left, s).above(h);
                    if (playerBox.intersects(new AABB(pos))) {
                        return true;
                    }
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
