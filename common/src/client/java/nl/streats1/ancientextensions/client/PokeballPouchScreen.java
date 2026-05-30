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

    private final PokeballPouchLayout.Metrics layout;

    public PokeballPouchScreen(PokeballPouchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.layout = PokeballPouchLayout.metrics(menu.getPouchSlotCount());
        this.imageWidth = PokeballPouchMenu.WIDTH;
        this.imageHeight = layout.imageHeight();
        this.titleLabelY = 6;
        this.inventoryLabelY = layout.inventoryLabelY();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;
        int rows = layout.pouchRows();

        graphics.blit(TEXTURE, left, top, 0, 0, this.imageWidth, PokeballPouchLayout.TEX_HEADER_H, TEX_WIDTH, TEX_HEIGHT);

        int pouchPanelH = layout.pouchAreaBottom() - PokeballPouchLayout.TEX_HEADER_H;
        if (pouchPanelH > 0) {
            graphics.blit(
                    TEXTURE,
                    left + PokeballPouchLayout.TEX_PANEL_X,
                    top + PokeballPouchLayout.TEX_HEADER_H,
                    PokeballPouchLayout.TEX_PANEL_X,
                    PokeballPouchLayout.TEX_PANEL_FILL_Y,
                    PokeballPouchLayout.TEX_PANEL_W,
                    pouchPanelH,
                    TEX_WIDTH,
                    TEX_HEIGHT
            );
        }

        for (int row = 0; row < rows; row++) {
            graphics.blit(
                    TEXTURE,
                    left + PokeballPouchLayout.TEX_SLOT_X,
                    top + PokeballPouchLayout.POUCH_START_Y + row * PokeballPouchLayout.SLOT_STRIDE,
                    PokeballPouchLayout.TEX_SLOT_X,
                    PokeballPouchLayout.TEX_SLOT_ROW_Y,
                    PokeballPouchLayout.TEX_SLOT_W,
                    PokeballPouchLayout.TEX_SLOT_ROW_H,
                    TEX_WIDTH,
                    TEX_HEIGHT
            );
        }

        int gapTop = layout.pouchAreaBottom();
        int gapBottom = layout.playerInvY();
        if (gapBottom > gapTop) {
            graphics.blit(
                    TEXTURE,
                    left + PokeballPouchLayout.TEX_PANEL_X,
                    top + gapTop,
                    PokeballPouchLayout.TEX_PANEL_X,
                    PokeballPouchLayout.TEX_PANEL_FILL_Y,
                    PokeballPouchLayout.TEX_PANEL_W,
                    gapBottom - gapTop,
                    TEX_WIDTH,
                    TEX_HEIGHT
            );
        }

        int playerPanelH = layout.hotbarY() + PokeballPouchLayout.TEX_SLOT_ROW_H - layout.playerInvY() + 6;
        graphics.blit(
                TEXTURE,
                left + PokeballPouchLayout.TEX_PANEL_X,
                top + layout.playerInvY() - 4,
                PokeballPouchLayout.TEX_PANEL_X,
                67,
                PokeballPouchLayout.TEX_PANEL_W,
                playerPanelH,
                TEX_WIDTH,
                TEX_HEIGHT
        );

        for (int row = 0; row < 3; row++) {
            graphics.blit(
                    TEXTURE,
                    left + PokeballPouchLayout.TEX_SLOT_X,
                    top + layout.playerInvY() + row * PokeballPouchLayout.SLOT_STRIDE,
                    PokeballPouchLayout.TEX_SLOT_X,
                    PokeballPouchLayout.TEX_PLAYER_ROW_Y + row * PokeballPouchLayout.SLOT_STRIDE,
                    PokeballPouchLayout.TEX_SLOT_W,
                    PokeballPouchLayout.TEX_SLOT_ROW_H,
                    TEX_WIDTH,
                    TEX_HEIGHT
            );
        }

        graphics.blit(
                TEXTURE,
                left + PokeballPouchLayout.TEX_SLOT_X,
                top + layout.hotbarY(),
                PokeballPouchLayout.TEX_SLOT_X,
                PokeballPouchLayout.TEX_HOTBAR_Y,
                PokeballPouchLayout.TEX_SLOT_W,
                PokeballPouchLayout.TEX_SLOT_ROW_H,
                TEX_WIDTH,
                TEX_HEIGHT
        );

        int frameBottom = layout.hotbarY() + PokeballPouchLayout.TEX_SLOT_ROW_H + 6;
        if (this.imageHeight > frameBottom) {
            graphics.blit(
                    TEXTURE,
                    left,
                    top + frameBottom,
                    0,
                    TEX_HEIGHT - 6,
                    this.imageWidth,
                    this.imageHeight - frameBottom,
                    TEX_WIDTH,
                    TEX_HEIGHT
            );
        }
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
