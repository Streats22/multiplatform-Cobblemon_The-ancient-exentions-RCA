package nl.streats1.ancientextensions.block;

import nl.streats1.ancientextensions.registry.ModContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class FieldSurveyMonitorBlockEntity extends BlockEntity {

    private final String[] lines = new String[4];

    public FieldSurveyMonitorBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.FIELD_SURVEY_MONITOR_BE, pos, state);
    }

    public void setLine(int line, List<? extends Component> text) {
        if (line < 0 || line >= lines.length) {
            return;
        }
        String combined = text.stream()
                .map(component -> component.getString(96))
                .reduce((a, b) -> a + " " + b)
                .orElse("");
        if (!combined.equals(lines[line])) {
            lines[line] = combined;
            setChanged();
        }
    }

    public String line(int index) {
        if (index < 0 || index >= lines.length) {
            return "";
        }
        return lines[index] == null ? "" : lines[index];
    }

    public boolean hasAnyText() {
        for (String line : lines) {
            if (line != null && !line.isBlank()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] != null) {
                tag.putString("Line" + i, lines[i]);
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int i = 0; i < lines.length; i++) {
            lines[i] = tag.contains("Line" + i) ? tag.getString("Line" + i) : null;
        }
    }
}
