package nl.streats1.ancientextensions.fabric.data;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import nl.streats1.ancientextensions.dex.SurveyBackend;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FabricSurveyBackend implements SurveyBackend {

    @Override
    public RegionalSurveyData get(ServerPlayer player) {
        return storage(player.server).getData(player.getUUID());
    }

    @Override
    public void save(ServerPlayer player, RegionalSurveyData data) {
        storage(player.server).putData(player.getUUID(), data);
    }

    private static SurveySavedData storage(MinecraftServer server) {
        DimensionDataStorage dataStorage = server.overworld().getDataStorage();
        return dataStorage.computeIfAbsent(new SavedData.Factory<>(
                SurveySavedData::new,
                SurveySavedData::load,
                null
        ), AncientExtensionsConstants.MOD_ID + "_survey");
    }

    private static final class SurveySavedData extends SavedData {

        private final Map<UUID, CompoundTag> byPlayer = new HashMap<>();

        SurveySavedData() {
        }

        static SurveySavedData load(CompoundTag tag, HolderLookup.Provider registries) {
            SurveySavedData data = new SurveySavedData();
            CompoundTag players = tag.getCompound("players");
            for (String key : players.getAllKeys()) {
                data.byPlayer.put(UUID.fromString(key), players.getCompound(key));
            }
            return data;
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            CompoundTag players = new CompoundTag();
            byPlayer.forEach((uuid, nbt) -> players.put(uuid.toString(), nbt));
            tag.put("players", players);
            return tag;
        }

        RegionalSurveyData getData(UUID uuid) {
            return RegionalSurveyData.load(byPlayer.getOrDefault(uuid, new CompoundTag()));
        }

        void putData(UUID uuid, RegionalSurveyData data) {
            byPlayer.put(uuid, data.save());
            setDirty();
        }
    }
}
