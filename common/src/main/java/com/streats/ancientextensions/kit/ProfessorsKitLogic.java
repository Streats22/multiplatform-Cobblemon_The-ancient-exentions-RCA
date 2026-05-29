package com.streats.ancientextensions.kit;

import com.streats.ancientextensions.dex.RegionalSurveyData;
import com.streats.ancientextensions.dex.RegionalSurveyService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ProfessorsKitLogic {

    private ProfessorsKitLogic() {
    }

    public static boolean tryDeployKit(ServerPlayer player) {
        RegionalSurveyData data = RegionalSurveyService.get(player);
        if (data.hasDeployedProfessorsKit()) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.kit.already_used"));
            return false;
        }

        ServerLevel level = player.serverLevel();
        BlockPos preferred = player.blockPosition().relative(player.getDirection());
        var placement = FieldCampPlacer.placeCamp(level, preferred, player.getDirection());
        if (placement.isEmpty()) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.kit.no_space"));
            return false;
        }

        CampPlacement camp = placement.get();
        ItemStack briefing = SurveyFieldNotes.create();

        FieldCampPlacer.fillCampChest(level, camp.chestPos(), ProfessorsKitRewards.createChestStacks());
        FieldCampPlacer.placeBriefingOnLectern(level, camp.lecternPos(), briefing);
        giveStacks(player, ProfessorsKitRewards.createPlayerStacks());

        data.markProfessorsKitDeployed();
        RegionalSurveyService.save(player, data);

        spawnCampParticles(level, camp.campfirePos());
        level.playSound(null, camp.campfirePos(), SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 1.0f, 1.0f);
        player.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 0.5f, 1.0f);

        player.sendSystemMessage(Component.translatable("ancient_extensions.kit.deployed"));
        player.sendSystemMessage(Component.translatable("ancient_extensions.kit.survey_hint"));
        player.sendSystemMessage(Component.translatable("ancient_extensions.kit.chest_hint"));
        player.sendSystemMessage(Component.translatable("ancient_extensions.kit.lectern_hint"));
        player.sendSystemMessage(Component.translatable("ancient_extensions.journal.hint"));

        return true;
    }

    private static void spawnCampParticles(ServerLevel level, BlockPos campfire) {
        for (int i = 0; i < 24; i++) {
            double x = campfire.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 2.5;
            double y = campfire.getY() + 0.5 + level.random.nextDouble() * 1.5;
            double z = campfire.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 2.5;
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 1, 0, 0.02, 0, 0.01);
        }
    }

    private static void giveStacks(ServerPlayer player, List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }
            if (!player.getInventory().add(stack.copy())) {
                player.drop(stack.copy(), false);
            }
        }
    }
}
