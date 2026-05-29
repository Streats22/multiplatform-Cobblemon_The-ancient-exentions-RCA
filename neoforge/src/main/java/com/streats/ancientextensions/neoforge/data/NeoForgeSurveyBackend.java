package com.streats.ancientextensions.neoforge.data;

import com.streats.ancientextensions.dex.RegionalSurveyData;
import com.streats.ancientextensions.dex.SurveyBackend;
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
