package nl.streats1.ancientextensions.client;

import net.minecraft.resources.ResourceLocation;

import nl.streats1.ancientextensions.AncientExtensionsAssets;

/**
 * Atlas for journal/chart footer controls (not baked into the main GUI background).
 */
public final class JournalWidgetSprites {

    public static final ResourceLocation TEXTURE = AncientExtensionsAssets.GUI_REGIONAL_SURVEY_JOURNAL_WIDGETS;
    public static final int TEX_W = 256;
    public static final int TEX_H = 32;

    public static final int CLAIM_U = 0;
    public static final int CLAIM_V = 0;
    public static final int CLAIM_H = 16;

    public static final int PREV_U = 112;
    public static final int NEXT_U = 134;
    public static final int NAV_W = 22;
    public static final int NAV_H = 16;
    public static final int NAV_V = 0;

    private JournalWidgetSprites() {
    }
}
