package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.menu.PokeballPouchMenu;
import nl.streats1.ancientextensions.pouch.PokeballPouchLayout;
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
    private static final int TEX_WIDTH = 176;
    private static final int TEX_HEIGHT = 166;
    private static final int HEADER_HEIGHT = 17;
    private static final int FOOTER_HEIGHT = 94;

    public PokeballPouchScreen(PokeballPouchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        PokeballPouchLayout.Metrics layout = PokeballPouchLayout.metrics(menu.getPouchSlotCount());
        this.imageWidth = PokeballPouchMenu.WIDTH;
        this.imageHeight = layout.imageHeight();
        this.titleLabelY = 6;
        this.inventoryLabelY = layout.inventoryLabelY();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        if (this.imageHeight <= TEX_HEIGHT) {
            graphics.blit(TEXTURE, left, top, 0, 0, this.imageWidth, this.imageHeight, TEX_WIDTH, TEX_HEIGHT);
            return;
        }

        int stretchHeight = this.imageHeight - HEADER_HEIGHT - FOOTER_HEIGHT;
        graphics.blit(TEXTURE, left, top, 0, 0, this.imageWidth, HEADER_HEIGHT, TEX_WIDTH, TEX_HEIGHT);
        graphics.blit(
                TEXTURE,
                left,
                top + HEADER_HEIGHT,
                0,
                HEADER_HEIGHT,
                this.imageWidth,
                stretchHeight,
                TEX_WIDTH,
                TEX_HEIGHT
        );
        graphics.blit(
                TEXTURE,
                left,
                top + this.imageHeight - FOOTER_HEIGHT,
                0,
                TEX_HEIGHT - FOOTER_HEIGHT,
                this.imageWidth,
                FOOTER_HEIGHT,
                TEX_WIDTH,
                TEX_HEIGHT
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
