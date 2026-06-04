package nl.streats1.ancientextensions.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import nl.streats1.ancientextensions.integration.OptionalIntegrationMods;

public class FieldSurveySensorBlock extends Block {

    public static final MapCodec<FieldSurveySensorBlock> CODEC = simpleCodec(FieldSurveySensorBlock::new);

    public FieldSurveySensorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
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
                    Component.translatable("ancient_extensions.field_sensor.hint_create"),
                    true
            );
        } else {
            player.displayClientMessage(
                    Component.translatable("ancient_extensions.field_sensor.hint_no_create"),
                    true
            );
        }
        return InteractionResult.CONSUME;
    }
}
