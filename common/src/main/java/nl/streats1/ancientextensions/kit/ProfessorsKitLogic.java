package nl.streats1.ancientextensions.kit;

import nl.streats1.ancientextensions.compat.ComfortsCampCompat;
import nl.streats1.ancientextensions.compat.LootrCampChestCompat;
import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.config.CampConfig;
import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;

public final class ProfessorsKitLogic {

    private ProfessorsKitLogic() {
    }

    public static boolean tryDeployKit(ServerPlayer player) {
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        if (data.hasDeployedProfessorsKit() && !canRedeployKit(player)) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.kit.already_used"));
            return false;
        }

        ServerLevel level = player.serverLevel();
        BlockPos preferred = player.blockPosition().relative(player.getDirection(), 3);
        var placement = FieldCampPlacer.placeCamp(
                level,
                preferred,
                player.getDirection(),
                CampConfig.campBedSetsSpawn(),
                player
        );
        if (placement.isEmpty()) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.kit.no_space"));
            return false;
        }

        CampPlacement camp = placement.get();
        movePlayerToSafeStand(player, camp);
        ItemStack briefing = SurveyFieldNotes.create();

        boolean chestOnly = CampConfig.starterSuppliesInChestOnly();
        FieldCampPlacer.fillCampChest(
                level,
                camp.chestPos(),
                ProfessorsKitRewards.createDeployChestStacks(
                        player.registryAccess(),
                        player.getRandom(),
                        chestOnly
                )
        );
        FieldCampPlacer.placeBriefingOnLectern(level, camp.lecternPos(), briefing);
        if (!chestOnly) {
            giveStacks(player, ProfessorsKitRewards.createPlayerStacks(player.registryAccess(), player.getRandom()));
        }

        StarterKitGrant.grantJournalIfMissing(player);
        StarterKitGrant.grantPassportIfMissing(player);

        if (!canRedeployKit(player)) {
            data.markProfessorsKitDeployed();
            AncientExtensionsContext.get().surveys().save(player, data);
        }

        spawnCampParticles(level, camp.campfirePos());
        level.playSound(null, camp.campfirePos(), SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 1.0f, 1.0f);
        player.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 0.5f, 1.0f);

        player.sendSystemMessage(Component.translatable("ancient_extensions.kit.deployed"));
        if (chestOnly) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.kit.comfort_hint"));
            player.sendSystemMessage(Component.translatable("ancient_extensions.kit.chest_only_hint"));
        } else {
            player.sendSystemMessage(Component.translatable("ancient_extensions.kit.inventory_hint"));
        }
        if (CampConfig.campBedSetsSpawn() && !ComfortsCampCompat.isComfortsInstalled()) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.kit.bed_spawn_hint"));
        } else if (ComfortsCampCompat.isComfortsInstalled()) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.kit.sleeping_bag_hint"));
        }
        if (CampConfig.useLootrCampChest() && LootrCampChestCompat.isLootrInstalled()) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.kit.lootr_hint"));
        }
        player.sendSystemMessage(Component.translatable("ancient_extensions.kit.survey_hint"));
        if (chestOnly) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.kit.chest_hint"));
        }
        player.sendSystemMessage(Component.translatable("ancient_extensions.kit.lectern_hint"));
        player.sendSystemMessage(Component.translatable("ancient_extensions.journal.hint"));

        return true;
    }

    private static void movePlayerToSafeStand(ServerPlayer player, CampPlacement camp) {
        BlockPos stand = camp.safeStandPos();
        if (player.blockPosition().distSqr(stand) <= 4) {
            return;
        }
        double x = stand.getX() + 0.5;
        double y = stand.getY();
        double z = stand.getZ() + 0.5;
        BlockPos fire = camp.campfirePos();
        float yaw = (float) (Mth.atan2(
                fire.getZ() + 0.5 - z,
                fire.getX() + 0.5 - x
        ) * (180.0 / Math.PI)) - 90.0F;
        player.teleportTo(player.serverLevel(), x, y, z, Set.of(), yaw, player.getXRot());
    }

    /** Creative and opped players may pitch additional camps for testing. */
    public static boolean canRedeployKit(ServerPlayer player) {
        return player.isCreative() || player.hasPermissions(2);
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
