package nl.streats1.ancientextensions.neoforge.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import nl.streats1.ancientextensions.neoforge.network.SelectSurveyRegionPayload;
import nl.streats1.ancientextensions.neoforge.passport.RegionalPassportMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

public class RegionalPassportScreen extends AbstractContainerScreen<RegionalPassportMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AncientExtensionsConstants.MOD_ID,
            "textures/gui/regional_passport.png"
    );
    private static final int TEX_SIZE = 256;

    private static final int PHOTO_X = 20;
    private static final int PHOTO_Y = 38;
    private static final int PHOTO_SIZE = 36;
    private static final int FRAME_U = 104;
    private static final int FRAME_V = 200;
    private static final int FRAME_W = 40;
    private static final int FRAME_H = 44;

    private static final int SEAL_CENTER_X = 168;
    private static final int SEAL_CENTER_Y = 62;

    private static final int REGION_BTN_W = 64;
    private static final int REGION_BTN_H = 18;
    private static final int REGION_COLS = 3;

    private static final float STAMP_ANIMATION_TICKS = 16.0F;

    private long stampOpenedTick = -1L;

    public RegionalPassportScreen(RegionalPassportMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = RegionalPassportMenu.WIDTH;
        this.imageHeight = RegionalPassportMenu.HEIGHT;
        this.inventoryLabelY = 10000;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft != null && this.minecraft.level != null) {
            this.stampOpenedTick = this.minecraft.level.getGameTime();
        }
        if (!this.menu.isStamped()) {
            addRegionButtons();
        }
    }

    private void addRegionButtons() {
        SurveyRegion[] regions = SurveyRegion.values();
        int rows = (regions.length + REGION_COLS - 1) / REGION_COLS;
        int gridW = REGION_COLS * REGION_BTN_W + (REGION_COLS - 1) * 3;
        int startX = this.leftPos + (this.imageWidth - gridW) / 2;
        int startY = this.topPos + 108;

        for (int index = 0; index < regions.length; index++) {
            SurveyRegion region = regions[index];
            int col = index % REGION_COLS;
            int row = index / REGION_COLS;
            int x = startX + col * (REGION_BTN_W + 3);
            int y = startY + row * (REGION_BTN_H + 3);
            addRenderableWidget(Button.builder(region.labeledName(), button -> stamp(region))
                    .bounds(x, y, REGION_BTN_W, REGION_BTN_H)
                    .build());
        }

        int closeY = startY + rows * (REGION_BTN_H + 3) + 6;
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
                .bounds(this.leftPos + this.imageWidth / 2 - 40, closeY, 80, 18)
                .build());
    }

    private void stamp(SurveyRegion region) {
        PacketDistributor.sendToServer(new SelectSurveyRegionPayload(region.getId()));
        if (this.minecraft != null) {
            this.minecraft.player.closeContainer();
        }
    }

    private float stampProgress(float partialTick) {
        if (!this.menu.isStamped()) {
            return 0.0F;
        }
        if (this.stampOpenedTick < 0L || this.minecraft == null || this.minecraft.level == null) {
            return 1.0F;
        }
        float ticks = this.minecraft.level.getGameTime() - this.stampOpenedTick + partialTick;
        return Mth.clamp(ticks / STAMP_ANIMATION_TICKS, 0.0F, 1.0F);
    }

    private long gameTime() {
        return this.minecraft != null && this.minecraft.level != null ? this.minecraft.level.getGameTime() : 0L;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, TEX_SIZE, TEX_SIZE);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(
                this.font,
                Component.translatable("ancient_extensions.passport.gui.official_header"),
                this.titleLabelX,
                this.titleLabelY,
                0xF5E6C8,
                false
        );

        drawPhotoSlot(graphics);

        if (this.menu.isStamped()) {
            renderStampedContent(graphics);
        } else {
            renderUnstampedContent(graphics);
        }
    }

    private void drawPhotoSlot(GuiGraphics graphics) {
        graphics.blit(TEXTURE, PHOTO_X - 2, PHOTO_Y - 2, FRAME_U, FRAME_V, FRAME_W, FRAME_H, TEX_SIZE, TEX_SIZE);
        PassportFaceRenderer.draw(graphics, PHOTO_X + 2, PHOTO_Y + 2, PHOTO_SIZE - 4);

        graphics.drawString(
                this.font,
                Component.translatable("ancient_extensions.passport.gui.holder_label"),
                PHOTO_X,
                PHOTO_Y + PHOTO_SIZE + 3,
                0x6B5344,
                false
        );
        String name = this.menu.getHolderName();
        if (name.length() > 14) {
            name = name.substring(0, 13) + "…";
        }
        graphics.drawString(this.font, name, PHOTO_X, PHOTO_Y + PHOTO_SIZE + 13, 0x2E2418, false);
    }

    private void renderUnstampedContent(GuiGraphics graphics) {
        int sealX = SEAL_CENTER_X;
        int sealY = SEAL_CENTER_Y;
        float pulse = 0.45F + 0.12F * Mth.sin(gameTime() * 0.12F);

        graphics.pose().pushPose();
        graphics.pose().translate(sealX, sealY, 0.0F);
        graphics.pose().scale(pulse, pulse, 1.0F);
        graphics.setColor(0.55F, 0.45F, 0.4F, 0.35F);
        graphics.blit(TEXTURE, -24, -24, PassportSealRenderer.WAX_U, PassportSealRenderer.WAX_V, 48, 48, TEX_SIZE, TEX_SIZE);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().popPose();

        graphics.drawCenteredString(
                this.font,
                Component.translatable("ancient_extensions.passport.gui.awaiting_stamp")
                        .withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY),
                sealX,
                sealY + 30,
                0x7A6555
        );

        graphics.drawCenteredString(
                this.font,
                Component.translatable("ancient_extensions.passport.gui.unstamped_hint"),
                this.imageWidth / 2,
                96,
                0x6B5344
        );
        graphics.drawCenteredString(
                this.font,
                Component.translatable("ancient_extensions.passport.gui.choose_region"),
                this.imageWidth / 2,
                106,
                0x4A3728
        );
    }

    private void renderStampedContent(GuiGraphics graphics) {
        Optional<SurveyRegion> regionOpt = this.menu.getRegion();
        if (regionOpt.isEmpty()) {
            return;
        }
        SurveyRegion region = regionOpt.get();
        int stampColor = region.nameColor().getColor() != null ? region.nameColor().getColor() : 0xFFFFFF;

        graphics.drawCenteredString(
                this.font,
                region.displayName(),
                SEAL_CENTER_X,
                SEAL_CENTER_Y + 34,
                stampColor
        );

        int blurbX = 14;
        int blurbY = 92;
        int blurbWidth = this.imageWidth - 28;
        List<FormattedCharSequence> blurbLines = this.font.split(region.passportBlurb(), blurbWidth);
        for (FormattedCharSequence line : blurbLines) {
            if (blurbY > 118) {
                break;
            }
            graphics.drawString(this.font, line, blurbX, blurbY, 0x3D2E24, false);
            blurbY += 10;
        }

        graphics.drawString(
                this.font,
                Component.translatable(
                        "ancient_extensions.passport.gui.stats",
                        this.menu.getCaughtSpecies(),
                        this.menu.getResearchPoints(),
                        this.menu.getTier().displayName()
                ),
                16,
                132,
                0x2E4A2E,
                false
        );

        graphics.drawString(
                this.font,
                Component.translatable("ancient_extensions.passport.gui.footer")
                        .withStyle(ChatFormatting.ITALIC),
                16,
                168,
                0x6B5344,
                false
        );

        graphics.drawString(
                this.font,
                Component.translatable("ancient_extensions.passport.gui.official_footer"),
                16,
                180,
                0x9A8878,
                false
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);

        if (this.menu.isStamped()) {
            this.menu.getRegion().ifPresent(region -> PassportSealRenderer.draw(
                    graphics,
                    this.font,
                    region,
                    this.leftPos + SEAL_CENTER_X,
                    this.topPos + SEAL_CENTER_Y,
                    partialTick,
                    stampProgress(partialTick),
                    gameTime()
            ));
        }

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderSlot(GuiGraphics graphics, net.minecraft.world.inventory.Slot slot) {
        // No inventory slots on this screen.
    }
}
