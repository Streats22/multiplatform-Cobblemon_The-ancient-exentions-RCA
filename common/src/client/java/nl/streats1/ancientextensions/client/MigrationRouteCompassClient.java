package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.item.CompassTargetData;
import nl.streats1.ancientextensions.registry.ModContent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class MigrationRouteCompassClient {

    private MigrationRouteCompassClient() {
    }

    public static void registerItemProperties() {
        // Must be minecraft:angle — item models use the "angle" predicate for compass rotation.
        ResourceLocation angle = ResourceLocation.withDefaultNamespace("angle");
        ResourceLocation targeted = AncientExtensionsConstants.id("migration_compass_targeted");

        ItemProperties.register(
                ModContent.MIGRATION_ROUTE_COMPASS,
                angle,
                MigrationRouteCompassClient::computeAngle
        );
        ItemProperties.register(
                ModContent.MIGRATION_ROUTE_COMPASS,
                targeted,
                (stack, level, entity, seed) -> CompassTargetData.read(stack).isPresent() ? 1.0F : 0.0F
        );
    }

    private static float computeAngle(ItemStack stack, Level level, Entity entity, int seed) {
        if (level == null || entity == null) {
            return 0.0F;
        }
        return CompassTargetData.read(stack)
                .map(target -> angleToward(entity, target.position()))
                .orElse(wobble(level, seed));
    }

    private static float angleToward(Entity entity, BlockPos target) {
        Vec3 eyes = entity.getEyePosition(1.0F);
        double dx = (target.getX() + 0.5) - eyes.x;
        double dz = (target.getZ() + 0.5) - eyes.z;
        double targetAngle = Math.atan2(dz, dx);
        double rotation = entity.getYRot() * (Math.PI / 180.0);
        return (float) Mth.positiveModulo(targetAngle / (Math.PI * 2.0) - rotation / (Math.PI * 2.0), 1.0);
    }

    private static float wobble(Level level, int seed) {
        return Mth.frac(level.getGameTime() / 32.0F + seed * 0.1F + 0.25F);
    }
}
