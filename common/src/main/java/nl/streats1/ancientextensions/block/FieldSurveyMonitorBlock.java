package nl.streats1.ancientextensions.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

import nl.streats1.ancientextensions.field.FieldSurveyPower;
import nl.streats1.ancientextensions.field.FieldSurveyStack;
import nl.streats1.ancientextensions.integration.OptionalIntegrationMods;

public class FieldSurveyMonitorBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.25, 0.0, 0.375, 0.75, 0.125, 0.625),
            Shapes.box(0.125, 0.125, 0.3125, 0.875, 0.875, 0.6875),
            Shapes.box(0.0625, 0.1875, 0.28125, 0.9375, 0.8125, 0.34375),
            Shapes.box(0.0, 0.25, 0.375, 0.0625, 0.75, 0.625),
            Shapes.box(0.9375, 0.25, 0.375, 1.0, 0.75, 0.625),
            Shapes.box(0.125, 0.8125, 0.375, 0.875, 0.875, 0.625)
    );

    public static final MapCodec<FieldSurveyMonitorBlock> CODEC = simpleCodec(FieldSurveyMonitorBlock::new);

    public FieldSurveyMonitorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState support = context.getLevel().getBlockState(context.getClickedPos());
        if (!FieldSurveyStack.canPlaceMonitorOn(support, context.getClickedFace())) {
            return null;
        }
        BlockPos above = context.getClickedPos().above();
        if (!context.getLevel().getBlockState(above).canBeReplaced(context)) {
            return null;
        }
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FieldSurveyMonitorBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!OptionalIntegrationMods.hasCreate()) {
            player.displayClientMessage(
                    Component.translatable("ancient_extensions.field_monitor.hint_no_create"),
                    true
            );
            return InteractionResult.CONSUME;
        }
        if (!FieldSurveyStack.isMountedOnSensor(level, pos)) {
            player.displayClientMessage(
                    Component.translatable("ancient_extensions.field_monitor.hint_need_sensor"),
                    true
            );
            return InteractionResult.CONSUME;
        }
        BlockPos sensorPos = pos.below();
        if (!FieldSurveyPower.hasShaftPowerFromBelow(level, sensorPos)) {
            player.displayClientMessage(
                    Component.translatable(
                            "ancient_extensions.field_monitor.hint_waiting_power",
                            32
                    ),
                    true
            );
            return InteractionResult.CONSUME;
        }
        player.displayClientMessage(
                Component.translatable("ancient_extensions.field_monitor.hint_ready"),
                true
        );
        return InteractionResult.CONSUME;
    }
}
