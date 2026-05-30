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

        graphics.blit(
                TEXTURE,
                left,
                top,
                0,
                0,
                this.imageWidth,
                PokeballPouchLayout.TEX_HEADER_H,
                TEX_WIDTH,
                TEX_HEIGHT
        );

        blitPanelStrip(graphics, left, top + PokeballPouchLayout.TEX_HEADER_H, PokeballPouchLayout.POUCH_START_Y - PokeballPouchLayout.TEX_HEADER_H);

        for (int row = 0; row < layout.pouchRows(); row++) {
            blitSlotRow(graphics, left, top + PokeballPouchLayout.POUCH_START_Y + row * PokeballPouchLayout.SLOT_STRIDE);
        }

        int gapTop = layout.pouchAreaBottom();
        int gapBottom = layout.playerInvY();
        if (gapBottom > gapTop) {
            blitPanelStrip(graphics, left, top + gapTop, gapBottom - gapTop);
        }

        int playerBgTop = layout.playerInvY() - 4;
        int playerBgBottom = layout.hotbarY() + PokeballPouchLayout.TEX_SLOT_ROW_H + 6;
        blitPanelStrip(graphics, left, top + playerBgTop, playerBgBottom - playerBgTop);

        for (int row = 0; row < 3; row++) {
            blitSlotRow(graphics, left, top + layout.playerInvY() + row * PokeballPouchLayout.SLOT_STRIDE);
        }

        blitSlotRow(graphics, left, top + layout.hotbarY());

        int frameBottom = layout.hotbarY() + PokeballPouchLayout.TEX_SLOT_ROW_H + 6;
        if (this.imageHeight > frameBottom) {
            graphics.blit(
                    TEXTURE,
                    left,
                    top + frameBottom,
                    0,
                    PokeballPouchLayout.TEX_FOOTER_Y,
                    this.imageWidth,
                    this.imageHeight - frameBottom,
                    TEX_WIDTH,
                    TEX_HEIGHT
            );
        }
    }

    private static void blitPanelStrip(GuiGraphics graphics, int destX, int destY, int height) {
        if (height <= 0) {
            return;
        }
        graphics.blit(
                TEXTURE,
                destX + PokeballPouchLayout.TEX_PANEL_X,
                destY,
                PokeballPouchLayout.TEX_PANEL_X,
                PokeballPouchLayout.TEX_PANEL_FILL_Y,
                PokeballPouchLayout.TEX_PANEL_W,
                height,
                TEX_WIDTH,
                TEX_HEIGHT
        );
    }

    private static void blitSlotRow(GuiGraphics graphics, int destX, int destY) {
        graphics.blit(
                TEXTURE,
                destX + PokeballPouchLayout.TEX_SLOT_X,
                destY,
                PokeballPouchLayout.TEX_SLOT_X,
                PokeballPouchLayout.TEX_SLOT_TEMPLATE_Y,
                PokeballPouchLayout.TEX_SLOT_W,
                PokeballPouchLayout.TEX_SLOT_ROW_H,
                TEX_WIDTH,
                TEX_HEIGHT
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
