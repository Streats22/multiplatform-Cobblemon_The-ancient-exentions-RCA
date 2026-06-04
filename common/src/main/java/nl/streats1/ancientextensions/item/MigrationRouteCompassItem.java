package nl.streats1.ancientextensions.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

import nl.streats1.ancientextensions.integration.map.MapWaypointIntegration;
import nl.streats1.ancientextensions.migration.MigrationBiomeLocator;
import nl.streats1.ancientextensions.migration.MigrationRouteTarget;
import nl.streats1.ancientextensions.util.ItemGuideTooltips;

/**
 * Points toward the nearest biome on the player's active migration leg (like Explorer's Compass).
 * Sneak + use offers a JourneyMap / map waypoint when those mods are present.
 */
public class MigrationRouteCompassItem extends Item {

    private static final int COOLDOWN_TICKS = 60;
    private static final int AUTO_REFRESH_INTERVAL = 40;

    public MigrationRouteCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean selected) {
        if (level.isClientSide() || !selected || !(entity instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (level.getGameTime() % AUTO_REFRESH_INTERVAL != 0) {
            return;
        }
        applyTarget(stack, MigrationBiomeLocator.resolveForPlayer(serverPlayer));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.pass(stack);
        }
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        MigrationRouteTarget target = MigrationBiomeLocator.resolveForPlayer(serverPlayer);

        if (player.isShiftKeyDown() && target.hasCompassTarget()) {
            MapWaypointIntegration.offerWaypoint(
                    serverPlayer,
                    target.position(),
                    "Migration L" + target.legDisplay() + " · " + target.biomeLabel()
            );
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            return InteractionResultHolder.success(stack);
        }

        applyTarget(stack, target);
        sendStatus(serverPlayer, target);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.success(stack);
    }

    private static void applyTarget(ItemStack stack, MigrationRouteTarget target) {
        if (target.hasCompassTarget()) {
            CompassTargetData.write(stack, target.position(), target.biomeLabel());
        } else {
            CompassTargetData.clear(stack);
        }
    }

    private static void sendStatus(ServerPlayer player, MigrationRouteTarget target) {
        switch (target.state()) {
            case ON_ROUTE -> player.displayClientMessage(
                    Component.translatable(
                            "ancient_extensions.compass.on_route",
                            target.biomeLabel(),
                            target.legDisplay(),
                            target.legCount(),
                            target.catchesOnLeg(),
                            target.catchesRequired()
                    ),
                    true
            );
            case SEEKING_BIOME -> player.displayClientMessage(
                    Component.translatable(
                            "ancient_extensions.compass.seeking",
                            target.biomeLabel(),
                            target.distanceBlocks(),
                            target.bearingLabel(),
                            target.legDisplay(),
                            target.legCount()
                    ),
                    true
            );
            case ROUTE_COMPLETE -> player.displayClientMessage(
                    Component.translatable("ancient_extensions.compass.route_complete"),
                    true
            );
            case NOT_FOUND -> player.displayClientMessage(
                    Component.translatable(
                            "ancient_extensions.compass.not_found",
                            target.legDisplay(),
                            target.legCount()
                    ),
                    true
            );
        }
        if (target.hasCompassTarget()) {
            player.displayClientMessage(
                    Component.translatable("ancient_extensions.compass.waypoint_tip")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC),
                    true
            );
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.ancient_extensions.migration_route_compass.description")
                .withStyle(ChatFormatting.GRAY));
        ItemGuideTooltips.append(
                tooltip,
                flag,
                "ancient_extensions.guide.compass_detail1",
                "ancient_extensions.guide.compass_detail2",
                "ancient_extensions.guide.compass_detail3"
        );
        CompassTargetData.read(stack).ifPresent(target -> tooltip.add(
                Component.translatable(
                        "ancient_extensions.compass.tooltip_target",
                        target.biomeLabel(),
                        target.position().getX(),
                        target.position().getZ()
                ).withStyle(ChatFormatting.DARK_AQUA)
        ));
        tooltip.add(Component.translatable("ancient_extensions.compass.tooltip_use")
                .withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return CompassTargetData.read(stack).isPresent();
    }
}
