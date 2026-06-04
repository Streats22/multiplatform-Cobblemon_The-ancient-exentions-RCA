package nl.streats1.ancientextensions.integration.mca;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;

import java.lang.reflect.Method;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.config.PassportConfig;
import nl.streats1.ancientextensions.util.ModPresence;

/**
 * Optional integration with Minecraft Comes Alive Reborn ({@code mca}).
 * Defers the passport origin picker until MCA's character / destiny intro finishes.
 */
public final class McaIntegration {

    public static final String MOD_ID = "mca";

    private static final String PLAYER_SAVE_DATA = "net.conczin.mca.server.world.data.PlayerSaveData";

    private static final int POLL_INTERVAL_TICKS = 20;
    private static final int MAX_POLL_ATTEMPTS = 180;

    private static Method playerSaveDataGet;
    private static Method playerSaveDataIsEntityDataSet;

    private McaIntegration() {
    }

    public static boolean isLoaded() {
        return ModPresence.isLoaded(MOD_ID);
    }

    public static boolean shouldDeferOriginPickerOnJoin(ServerPlayer player) {
        if (!isLoaded() || !PassportConfig.deferOriginPickerForMca() || !PassportConfig.openOriginPickerOnJoin()) {
            return false;
        }
        if (AncientExtensionsContext.get().origins().hasOrigin(AncientExtensionsContext.get().surveys().get(player))) {
            return false;
        }
        return !isMcaCharacterRegistered(player);
    }

    /**
     * Polls until MCA's intro invisibility ends (destiny screen closed), then opens the passport picker.
     * MCA applies {@link MobEffects#INVISIBILITY} during the intro and removes it when destiny closes.
     */
    public static void schedulePassportPromptAfterMcaIntro(MinecraftServer server, ServerPlayer player) {
        if (!shouldDeferOriginPickerOnJoin(player)) {
            return;
        }
        poll(server, player, 0);
    }

    private static void poll(MinecraftServer server, ServerPlayer player, int attempt) {
        server.tell(new net.minecraft.server.TickTask(server.getTickCount() + POLL_INTERVAL_TICKS, () -> {
            if (!player.isAlive() || player.hasDisconnected()) {
                return;
            }
            if (!needsPassportStamp(player)) {
                return;
            }
            if (isMcaIntroFinished(player) || attempt >= MAX_POLL_ATTEMPTS) {
                AncientExtensionsContext.get().promptOriginAfterMcaIntro(player);
                return;
            }
            poll(server, player, attempt + 1);
        }));
    }

    private static boolean needsPassportStamp(ServerPlayer player) {
        return PassportConfig.openOriginPickerOnJoin()
                && !AncientExtensionsContext.get().origins().hasOrigin(AncientExtensionsContext.get().surveys().get(player));
    }

    private static boolean isMcaIntroFinished(ServerPlayer player) {
        return !player.hasEffect(MobEffects.INVISIBILITY);
    }

    private static boolean isMcaCharacterRegistered(ServerPlayer player) {
        try {
            ensureReflection();
            if (playerSaveDataGet == null || playerSaveDataIsEntityDataSet == null) {
                return false;
            }
            Object saveData = playerSaveDataGet.invoke(null, player);
            if (saveData == null) {
                return false;
            }
            return Boolean.TRUE.equals(playerSaveDataIsEntityDataSet.invoke(saveData));
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    private static void ensureReflection() throws ReflectiveOperationException {
        if (playerSaveDataGet != null) {
            return;
        }
        Class<?> saveDataClass = Class.forName(PLAYER_SAVE_DATA);
        playerSaveDataGet = saveDataClass.getMethod("get", ServerPlayer.class);
        playerSaveDataIsEntityDataSet = saveDataClass.getMethod("isEntityDataSet");
    }
}
