package nl.streats1.ancientextensions.dex;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerSurveyStorage {

    private static SurveyBackend backend = new InMemoryBackend();

    private PlayerSurveyStorage() {
    }

    public static void setBackend(SurveyBackend surveyBackend) {
        backend = surveyBackend;
    }

    public static RegionalSurveyData get(ServerPlayer player) {
        return backend.get(player);
    }

    public static void save(ServerPlayer player, RegionalSurveyData data) {
        backend.save(player, data);
    }

    private static final class InMemoryBackend implements SurveyBackend {
        private final Map<UUID, RegionalSurveyData> byPlayer = new ConcurrentHashMap<>();

        @Override
        public RegionalSurveyData get(ServerPlayer player) {
            return byPlayer.computeIfAbsent(player.getUUID(), id -> new RegionalSurveyData());
        }

        @Override
        public void save(ServerPlayer player, RegionalSurveyData data) {
            byPlayer.put(player.getUUID(), data);
        }
    }
}
