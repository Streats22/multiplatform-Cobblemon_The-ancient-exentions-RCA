package nl.streats1.ancientextensions.neoforge.integration.create;

import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

import nl.streats1.ancientextensions.block.FieldSurveyMonitorBlockEntity;

public final class FieldSurveyMonitorDisplayTarget extends DisplayTarget {

    public static final FieldSurveyMonitorDisplayTarget INSTANCE = new FieldSurveyMonitorDisplayTarget();

    private FieldSurveyMonitorDisplayTarget() {
    }

    @Override
    public void acceptText(int line, List<MutableComponent> text, DisplayLinkContext context) {
        BlockEntity target = context.getTargetBlockEntity();
        if (!(target instanceof FieldSurveyMonitorBlockEntity monitor)) {
            return;
        }
        if (line == 0) {
            reserve(0, monitor, context);
        } else if (isReserved(line, monitor, context)) {
            return;
        }
        monitor.setLine(line, text);
        context.level().sendBlockUpdated(context.getTargetPos(), monitor.getBlockState(), monitor.getBlockState(), 3);
    }

    @Override
    public DisplayTargetStats provideStats(DisplayLinkContext context) {
        return new DisplayTargetStats(4, 24, this);
    }
}
