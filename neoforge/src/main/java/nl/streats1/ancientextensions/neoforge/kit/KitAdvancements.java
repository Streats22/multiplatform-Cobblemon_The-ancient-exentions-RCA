package nl.streats1.ancientextensions.neoforge.kit;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class KitAdvancements {

    private static final ResourceLocation PITCH_CAMP =
            AncientExtensionsConstants.id("survey/pitch_field_camp");

    private KitAdvancements() {
    }

    public static void awardCampPitched(ServerPlayer player) {
        AdvancementHolder advancement = player.server.getAdvancements().get(PITCH_CAMP);
        if (advancement == null) {
            return;
        }
        if (player.getAdvancements().getOrStartProgress(advancement).isDone()) {
            return;
        }
        advancement.value().criteria().keySet().forEach(criterion ->
                player.getAdvancements().award(advancement, criterion)
        );
    }
}
