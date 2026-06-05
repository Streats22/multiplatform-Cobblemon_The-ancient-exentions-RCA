package nl.streats1.ancientextensions.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

import nl.streats1.ancientextensions.field.FieldSurveyPower;
import nl.streats1.ancientextensions.field.FieldSurveyStack;
import nl.streats1.ancientextensions.integration.OptionalIntegrationMods;


public class FieldSurveySensorBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Shapes.box(0.0625, 0.0, 0.0625, 0.9375, 0.75, 0.9375);

    public static final MapCodec<FieldSurveySensorBlock> CODEC = simpleCodec(FieldSurveySensorBlock::new);

    public FieldSurveySensorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FieldSurveySensorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return level.isClientSide()
                ? null
                : (world, pos, blockState, blockEntity) -> FieldSurveySensorBlockEntity.serverTick(world, pos, blockState);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return !level.getBlockState(below).isAir();
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
                    Component.translatable("ancient_extensions.field_sensor.hint_no_create"),
                    true
            );
            return InteractionResult.CONSUME;
        }
        if (!FieldSurveyStack.hasMonitorAbove(level, pos)) {
            player.displayClientMessage(
                    Component.translatable("ancient_extensions.field_sensor.hint_stack_monitor"),
                    true
            );
            return InteractionResult.CONSUME;
        }
        if (!FieldSurveyPower.hasShaftPowerFromBelow(level, pos)) {
            player.displayClientMessage(
                    Component.translatable(
                            "ancient_extensions.field_sensor.hint_rpm",
                            32
                    ),
                    true
            );
            return InteractionResult.CONSUME;
        }
        player.displayClientMessage(
                Component.translatable("ancient_extensions.field_sensor.hint_ready"),
                true
        );
        return InteractionResult.CONSUME;
    }
}
