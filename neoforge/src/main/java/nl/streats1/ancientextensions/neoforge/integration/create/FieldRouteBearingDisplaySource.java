package nl.streats1.ancientextensions.neoforge.integration.create;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;

import nl.streats1.ancientextensions.field.FieldSurveyStack;
import nl.streats1.ancientextensions.field.FieldSurveyStackLines;
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
        if (!FieldSurveyStack.hasMonitorAbove(server, context.getSourcePos())) {
            return FieldSurveyKineticRequirements.stackRequiredLine();
        }
        if (!FieldSurveyKineticRequirements.hasShaftPowerFromBelow(server, context.getSourcePos())) {
            return FieldSurveyKineticRequirements.rpmRequiredLine();
        }
        MigrationRouteTarget target = MigrationBiomeLocator.resolveNear(server, context.getSourcePos());
        return net.minecraft.network.chat.Component.literal(FieldSurveyStackLines.formatBearing(target));
    }

    @Override
    protected String getTranslationKey() {
        return "field_route_bearing";
    }

}
