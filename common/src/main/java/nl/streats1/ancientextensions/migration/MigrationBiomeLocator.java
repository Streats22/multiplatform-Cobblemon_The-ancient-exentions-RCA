package nl.streats1.ancientextensions.migration;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import com.mojang.datafixers.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Finds the nearest migration-route biome (Explorer's Compass style), using the player's active leg.
 */
public final class MigrationBiomeLocator {

    private static final int SEARCH_RADIUS = 12_800;
    private static final int HORIZONTAL_STEP = 64;
    private static final int VERTICAL_STEP = 64;

    private MigrationBiomeLocator() {
    }

    public static MigrationRouteTarget resolveForPlayer(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        MigrationSeason season = MigrationSeasonClock.currentSeason(level);
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        data.syncMigrationSeason(season);

        ResourceLocation biomeId = MigrationBiomeContext.currentBiomeId(player);
        return resolveAt(
                level,
                player.blockPosition(),
                season,
                data.getMigrationLegIndex(),
                data.getCurrentLegCatches(),
                biomeId
        );
    }

    /** Sensor / block readout: nearest player within 48 blocks supplies leg progress. */
    public static MigrationRouteTarget resolveNear(ServerLevel level, BlockPos pos) {
        MigrationSeason season = MigrationSeasonClock.currentSeason(level);
        Player nearby = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 48.0, false);
        if (nearby instanceof ServerPlayer player) {
            RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
            data.syncMigrationSeason(season);
            return resolveAt(
                    level,
                    pos,
                    season,
                    data.getMigrationLegIndex(),
                    data.getCurrentLegCatches(),
                    MigrationBiomeContext.currentBiomeId(player)
            );
        }
        return resolveAt(
                level,
                pos,
                season,
                0,
                0,
                level.getBiome(pos).unwrapKey().map(k -> k.location()).orElse(null)
        );
    }

    private static MigrationRouteTarget resolveAt(
            ServerLevel level,
            BlockPos origin,
            MigrationSeason season,
            int legIndex,
            int currentLegCatches,
            ResourceLocation currentBiomeId
    ) {
        List<MigrationLeg> route = MigrationRoutes.routeFor(season);
        int legCount = route.size();
        int legDisplay = Math.min(legIndex + 1, Math.max(legCount, 1));

        if (legIndex >= legCount) {
            return MigrationRouteTarget.routeComplete(legCount);
        }

        MigrationLeg leg = route.get(legIndex);
        String biomeName = MigrationBiomeContext.prettyBiomeName(currentBiomeId);

        if (leg.matchesBiome(currentBiomeId)) {
            return MigrationRouteTarget.onRoute(
                    biomeName,
                    legDisplay,
                    legCount,
                    currentLegCatches,
                    leg.requiredCatches()
            );
        }

        BlockPos nearest = findNearestMatchingBiome(level, origin, leg.biomeIds());
        if (nearest == null) {
            return MigrationRouteTarget.notFound(legDisplay, legCount);
        }

        String targetBiome = MigrationBiomeContext.prettyBiomeName(level.getBiome(nearest).unwrapKey()
                .map(key -> key.location())
                .orElse(null));
        int distance = (int) Math.sqrt(origin.distSqr(nearest));
        String bearing = formatBearing(origin, nearest);

        return MigrationRouteTarget.seeking(nearest, targetBiome, distance, bearing, legDisplay, legCount);
    }

    private static BlockPos findNearestMatchingBiome(
            ServerLevel level,
            BlockPos origin,
            List<ResourceLocation> biomeIds
    ) {
        if (biomeIds.isEmpty()) {
            return null;
        }
        Set<ResourceLocation> targets = new HashSet<>(biomeIds);
        Predicate<Holder<Biome>> predicate = holder -> holder.unwrapKey()
                .map(key -> targets.contains(key.location()))
                .orElse(false);

        Pair<BlockPos, Holder<Biome>> found = level.findClosestBiome3d(
                predicate,
                origin,
                SEARCH_RADIUS,
                HORIZONTAL_STEP,
                VERTICAL_STEP
        );
        return found == null ? null : found.getFirst();
    }

    static String formatBearing(BlockPos from, BlockPos to) {
        double dx = to.getX() + 0.5 - from.getX();
        double dz = to.getZ() + 0.5 - from.getZ();
        double angle = Math.toDegrees(Math.atan2(dz, dx));
        if (angle < 0) {
            angle += 360;
        }
        if (angle >= 337.5 || angle < 22.5) {
            return "E";
        }
        if (angle < 67.5) {
            return "SE";
        }
        if (angle < 112.5) {
            return "S";
        }
        if (angle < 157.5) {
            return "SW";
        }
        if (angle < 202.5) {
            return "W";
        }
        if (angle < 247.5) {
            return "NW";
        }
        if (angle < 292.5) {
            return "N";
        }
        return "NE";
    }
}
