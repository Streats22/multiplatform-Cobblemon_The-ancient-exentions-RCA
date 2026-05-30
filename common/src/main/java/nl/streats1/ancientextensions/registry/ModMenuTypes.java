package nl.streats1.ancientextensions.registry;

import nl.streats1.ancientextensions.menu.PokeballPouchMenu;
import nl.streats1.ancientextensions.menu.RegionalPassportMenu;
import nl.streats1.ancientextensions.menu.RegionalSurveyJournalMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Populated by each platform loader when menu types are registered.
 */
public final class ModMenuTypes {

    public static MenuType<RegionalPassportMenu> REGIONAL_PASSPORT;
    public static MenuType<RegionalSurveyJournalMenu> REGIONAL_SURVEY_JOURNAL;
    public static MenuType<PokeballPouchMenu> POKEBALL_POUCH;

    private ModMenuTypes() {
    }
}
