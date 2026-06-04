package nl.streats1.ancientextensions.neoforge.integration.create;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;

import nl.streats1.ancientextensions.migration.MigrationBiomeLocator;
import nl.streats1.ancientextensions.migration.MigrationRouteTarget;

public final class FieldRouteBearingDisplaySource extends SingleLineDisplaySource {

    public static final FieldRouteBearingDisplaySource INSTANCE = new FieldRouteBearingDisplaySource();

    private FieldRouteBearingDisplaySource() {
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    public int getPassiveRefreshTicks() {
        return 80;
    }

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.level() instanceof ServerLevel server)) {
            return EMPTY_LINE;
        }
        MigrationRouteTarget target = MigrationBiomeLocator.resolveNear(server, context.getSourcePos());
        return net.minecraft.network.chat.Component.literal(format(target));
    }

    @Override
    protected String getTranslationKey() {
        return "field_route_bearing";
    }

    private static String format(MigrationRouteTarget target) {
        return switch (target.state()) {
            case ON_ROUTE -> "Here · L" + target.legDisplay() + "/" + target.legCount()
                    + " · " + target.catchesOnLeg() + "/" + target.catchesRequired();
            case SEEKING_BIOME -> target.distanceBlocks() + "m " + target.bearingLabel()
                    + " · L" + target.legDisplay() + " · " + target.biomeLabel();
            case ROUTE_COMPLETE -> "Route complete";
            case NOT_FOUND -> "No biome in range · L" + target.legDisplay();
        };
    }
}
