package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.menu.PokeballPouchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PokeballPouchScreen extends AbstractContainerScreen<PokeballPouchMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AncientExtensionsConstants.MOD_ID,
            "textures/gui/pokeball_pouch.png"
    );

    public PokeballPouchScreen(PokeballPouchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = PokeballPouchMenu.WIDTH;
        this.imageHeight = menu.getImageHeight();
        this.titleLabelY = 6;
        this.inventoryLabelY = menu.getImageHeight() - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;
        int headerHeight = 34;
        int footerHeight = 94;
        int stretchHeight = Math.max(0, this.imageHeight - headerHeight - footerHeight);

        graphics.blit(TEXTURE, left, top, 0, 0, this.imageWidth, headerHeight, 176, 166);
        if (stretchHeight > 0) {
            graphics.blit(TEXTURE, left, top + headerHeight, 0, headerHeight, this.imageWidth, stretchHeight, 176, 166);
        }
        graphics.blit(
                TEXTURE,
                left,
                top + this.imageHeight - footerHeight,
                0,
                166 - footerHeight,
                this.imageWidth,
                footerHeight,
                176,
                166
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(
                this.font,
                Component.translatable(
                        "container.ancient_extensions.pokeball_pouch.section",
                        this.menu.getPouchSlotCount()
                ),
                8,
                20,
                0xF5E6C8,
                false
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
