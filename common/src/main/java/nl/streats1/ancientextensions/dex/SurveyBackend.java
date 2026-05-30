package nl.streats1.ancientextensions.dex;

import net.minecraft.server.level.ServerPlayer;

public interface SurveyBackend {

    RegionalSurveyData get(ServerPlayer player);

    void save(ServerPlayer player, RegionalSurveyData data);
}
