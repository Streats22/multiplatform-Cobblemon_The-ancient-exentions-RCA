package nl.streats1.ancientextensions.neoforge.integration.sophisticated;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.*;

import java.util.List;
import java.util.function.Consumer;

import nl.streats1.ancientextensions.field.FieldSurveyTelemetryData;
import nl.streats1.ancientextensions.field.FieldSurveyWorldSnapshot;

public class FieldSurveyTelemetryUpgradeItem extends UpgradeItemBase<FieldSurveyTelemetryUpgradeItem.Wrapper> {

    private static final int TICK_INTERVAL = 40;

    public static final UpgradeType<FieldSurveyTelemetryUpgradeItem.Wrapper> TYPE =
            new UpgradeType<>(FieldSurveyTelemetryUpgradeItem.Wrapper::new);

    public static final List<IUpgradeItem.UpgradeConflictDefinition> UPGRADE_CONFLICT_DEFINITIONS = List.of(
            new IUpgradeItem.UpgradeConflictDefinition(
                    FieldSurveyTelemetryUpgradeItem.class::isInstance,
                    0,
                    Component.translatable("ancient_extensions.sb.telemetry.conflict")
            )
    );

    public FieldSurveyTelemetryUpgradeItem() {
        super(Config.SERVER.maxUpgradesPerStorage);
    }

    @Override
    public UpgradeType<FieldSurveyTelemetryUpgradeItem.Wrapper> getType() {
        return TYPE;
    }

    @Override
    public List<IUpgradeItem.UpgradeConflictDefinition> getUpgradeConflicts() {
        return UPGRADE_CONFLICT_DEFINITIONS;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (!FieldSurveyTelemetryData.hasReadout(stack)) {
            tooltip.add(Component.translatable("ancient_extensions.sb.telemetry.tooltip_pending")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        appendLine(tooltip, FieldSurveyTelemetryData.seasonLine(stack), ChatFormatting.AQUA);
        appendLine(tooltip, FieldSurveyTelemetryData.biomeRouteLine(stack), ChatFormatting.GREEN);
        appendLine(tooltip, FieldSurveyTelemetryData.speciesLine(stack), ChatFormatting.YELLOW);
    }

    private static void appendLine(List<Component> tooltip, @org.jetbrains.annotations.Nullable String line, ChatFormatting color) {
        if (line != null && !line.isEmpty()) {
            tooltip.add(Component.literal(line).withStyle(color));
        }
    }

    public static class Wrapper extends UpgradeWrapperBase<Wrapper, FieldSurveyTelemetryUpgradeItem>
            implements ITickableUpgrade {

        public Wrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
            super(storageWrapper, upgrade, upgradeSaveHandler);
        }

        @Override
        public void tick(Entity entity, Level level, BlockPos pos) {
            if (!isEnabled() || level.isClientSide() || !(level instanceof ServerLevel server)) {
                return;
            }
            if (isInCooldown(level)) {
                return;
            }
            FieldSurveyWorldSnapshot snapshot = FieldSurveyWorldSnapshot.at(server, entity.blockPosition());
            FieldSurveyTelemetryData.write(upgrade, snapshot, level.getGameTime());
            save();
            setCooldown(level, TICK_INTERVAL);
        }

        @Override
        public boolean hideSettingsTab() {
            return true;
        }
    }
}
