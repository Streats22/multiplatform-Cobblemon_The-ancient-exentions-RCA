package com.streats.ancientextensions.neoforge.registry;

import com.streats.ancientextensions.AncientExtensionsConstants;
import com.streats.ancientextensions.neoforge.passport.RegionalPassportMenu;
import com.streats.ancientextensions.neoforge.pouch.PokeballPouchMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, AncientExtensionsConstants.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<PokeballPouchMenu>> POKEBALL_POUCH = MENUS.register(
            "pokeball_pouch",
            () -> IMenuTypeExtension.create(PokeballPouchMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<RegionalPassportMenu>> REGIONAL_PASSPORT = MENUS.register(
            "regional_passport",
            () -> IMenuTypeExtension.create(RegionalPassportMenu::new)
    );

    private ModMenus() {
    }

    public static void register(IEventBus modBus) {
        MENUS.register(modBus);
    }
}
