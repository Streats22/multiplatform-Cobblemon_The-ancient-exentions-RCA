package nl.streats1.ancientextensions.neoforge.data;

import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import nl.streats1.ancientextensions.dex.SurveyBackend;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public final class NeoForgeSurveyBackend implements SurveyBackend {

    @Override
    public RegionalSurveyData get(ServerPlayer player) {
        CompoundTag tag = player.getData(ModAttachments.REGIONAL_SURVEY);
        return RegionalSurveyData.load(tag);
    }

    @Override
    public void save(ServerPlayer player, RegionalSurveyData data) {
        player.setData(ModAttachments.REGIONAL_SURVEY, data.save());
    }
}
