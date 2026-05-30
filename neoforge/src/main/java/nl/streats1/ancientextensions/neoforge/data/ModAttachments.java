package nl.streats1.ancientextensions.neoforge.data;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AncientExtensionsConstants.MOD_ID);

    public static final Supplier<AttachmentType<CompoundTag>> REGIONAL_SURVEY = ATTACHMENTS.register(
            "regional_survey",
            () -> AttachmentType.builder(CompoundTag::new)
                    .serialize(CompoundTag.CODEC)
                    .copyOnDeath()
                    .build()
    );

    private ModAttachments() {
    }
}
