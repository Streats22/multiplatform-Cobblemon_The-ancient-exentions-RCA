package nl.streats1.ancientextensions.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import nl.streats1.ancientextensions.integration.OptionalIntegrationMods;
import org.jetbrains.annotations.Nullable;

public class FieldSurveyMonitorBlock extends BaseEntityBlock {

    public static final MapCodec<FieldSurveyMonitorBlock> CODEC = simpleCodec(FieldSurveyMonitorBlock::new);

    public FieldSurveyMonitorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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
        if (OptionalIntegrationMods.hasCreate()) {
            player.displayClientMessage(
                    Component.translatable("ancient_extensions.field_monitor.hint_create"),
                    true
            );
        } else {
            player.displayClientMessage(
                    Component.translatable("ancient_extensions.field_monitor.hint_no_create"),
                    true
            );
        }
        return InteractionResult.CONSUME;
    }
}
