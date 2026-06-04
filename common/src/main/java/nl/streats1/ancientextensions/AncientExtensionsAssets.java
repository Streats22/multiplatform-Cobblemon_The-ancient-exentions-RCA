package nl.streats1.ancientextensions;

import net.minecraft.resources.ResourceLocation;

/**
 * Canonical resource paths for mod assets. All files live under
 * {@code common/src/main/resources/assets/ancient_extensions/} and are packaged into
 * both Fabric and NeoForge jars via the {@code :common} module.
 */
public final class AncientExtensionsAssets {

    /** Mod icon for Fabric ({@code fabric.mod.json}) and NeoForge ({@code logoFile}). */
    public static final String MOD_ICON_PATH = "assets/ancient_extensions/icon.png";

    public static final ResourceLocation GUI_FIELD_SURVEY_TABLET = texture("gui/field_survey_tablet.png");
    public static final ResourceLocation GUI_REGIONAL_SURVEY_JOURNAL = texture("gui/regional_survey_journal.png");
    public static final ResourceLocation GUI_REGIONAL_SURVEY_JOURNAL_WIDGETS = texture("gui/regional_survey_journal_widgets.png");
    public static final ResourceLocation GUI_REGIONAL_PASSPORT = texture("gui/regional_passport.png");
    public static final ResourceLocation GUI_POKEBALL_POUCH = texture("gui/pokeball_pouch.png");

    private AncientExtensionsAssets() {
    }

    public static ResourceLocation texture(String pathUnderTextures) {
        String normalized = pathUnderTextures.startsWith("textures/")
                ? pathUnderTextures
                : "textures/" + pathUnderTextures;
        return AncientExtensionsConstants.id(normalized);
    }
}
