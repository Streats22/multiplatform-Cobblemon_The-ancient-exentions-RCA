package nl.streats1.ancientextensions.neoforge.integration.create;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;

import nl.streats1.ancientextensions.field.FieldSurveyWorldSnapshot;

abstract class AbstractFieldSurveyDisplaySource extends SingleLineDisplaySource {

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    public int getPassiveRefreshTicks() {
        return 40;
    }

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.level() instanceof ServerLevel)) {
            return EMPTY_LINE;
        }
        return net.minecraft.network.chat.Component.literal(
                provideText(FieldSurveyWorldSnapshot.at(context.level(), context.getSourcePos()))
        );
    }

    protected abstract String provideText(FieldSurveyWorldSnapshot snapshot);
}
