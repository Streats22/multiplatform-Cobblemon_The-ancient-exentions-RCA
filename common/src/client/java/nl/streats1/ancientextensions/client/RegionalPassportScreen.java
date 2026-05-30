package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.dex.SurveyOriginTown;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import nl.streats1.ancientextensions.menu.RegionalPassportMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Optional;

public class RegionalPassportScreen extends AbstractContainerScreen<RegionalPassportMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AncientExtensionsConstants.MOD_ID,
            "textures/gui/regional_passport.png"
    );
    private static final int TEX_SIZE = 256;

    private static final int HEADER_Y = 11;
    private static final int PHOTO_X = 14;
    private static final int PHOTO_Y = 32;
    private static final int PHOTO_SIZE = 34;
    private static final int FRAME_U = 104;
    private static final int FRAME_V = 200;
    private static final int FRAME_W = 40;
    private static final int FRAME_H = 44;

    /** Right column — wax seal area */
    private static final int SEAL_CENTER_X = 168;
    private static final int SEAL_CENTER_Y = 52;

    /** Left column — blurb below holder row, stops before seal column */
    private static final int LEFT_COL_X = 14;
    private static final int BLURB_WIDTH = 130;
    private static final int BLURB_START_Y = 78;

    private static final int STATS_PANEL_X = 12;
    private static final int STATS_PANEL_W = 196;
    private static final int STATS_DIVIDER_Y = 122;
    private static final int STATS_LABEL_Y = 128;
    private static final int STATS_LINE_Y = 140;
    private static final int FOOTER_Y = 158;
    private static final int OFFICIAL_Y = 174;

    private static final int REGION_BTN_W = 62;
    private static final int REGION_BTN_H = 16;
    private static final int REGION_COLS = 3;
    private static final int TOWN_BTN_W = 94;
    private static final int TOWN_BTN_H = 16;
    private static final int TOWN_COLS = 2;

    private enum SelectionStep {
        REGION,
        TOWN
    }

    private SelectionStep selectionStep = SelectionStep.REGION;
    private SurveyRegion pendingRegion;

    private static final float STAMP_ANIMATION_TICKS = 16.0F;

    private static final int COLOR_HEADER = 0xFFE8C84A;
    private static final int COLOR_HEADER_SHADOW = 0xFF402010;
    private static final int COLOR_LABEL = 0xFF6B5344;
    private static final int COLOR_INK = 0xFF2E2418;
    private static final int COLOR_BLURB = 0xFF3D2E24;
    private static final int COLOR_STATS = 0xFF2E4A2E;
    private static final int COLOR_MUTED = 0xFF9A8878;

    private long stampOpenedTick = -1L;

    public RegionalPassportScreen(RegionalPassportMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = RegionalPassportMenu.WIDTH;
        this.imageHeight = RegionalPassportMenu.HEIGHT;
        this.inventoryLabelY = 10000;
        this.titleLabelY = -1000;
    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft != null && this.minecraft.level != null) {
            this.stampOpenedTick = this.minecraft.level.getGameTime();
        }
        if (!this.menu.isStamped()) {
            this.menu.getRegion().ifPresent(region -> {
                this.pendingRegion = region;
                this.selectionStep = SelectionStep.TOWN;
            });
            showSelectionButtons();
        }
    }

    private void showSelectionButtons() {
        clearWidgets();
        if (selectionStep == SelectionStep.REGION) {
            addRegionButtons();
        } else {
            addTownButtons();
        }
    }

    private void addRegionButtons() {
        SurveyRegion[] regions = SurveyRegion.values();
        int rows = (regions.length + REGION_COLS - 1) / REGION_COLS;
        int gridW = REGION_COLS * REGION_BTN_W + (REGION_COLS - 1) * 4;
        int startX = this.leftPos + (this.imageWidth - gridW) / 2;
        int startY = this.topPos + 112;

        for (int index = 0; index < regions.length; index++) {
            SurveyRegion region = regions[index];
            int col = index % REGION_COLS;
            int row = index / REGION_COLS;
            int x = startX + col * (REGION_BTN_W + 4);
            int y = startY + row * (REGION_BTN_H + 4);
            addRenderableWidget(Button.builder(
                            Component.literal("[" + region.getBadgeCode() + "]").withStyle(region.nameColor()),
                            button -> chooseRegion(region)
                    )
                    .bounds(x, y, REGION_BTN_W, REGION_BTN_H)
                    .build());
        }

        int closeY = startY + rows * (REGION_BTN_H + 4) + 8;
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
                .bounds(this.leftPos + this.imageWidth / 2 - 36, closeY, 72, 16)
                .build());
    }

    private void chooseRegion(SurveyRegion region) {
        pendingRegion = region;
        selectionStep = SelectionStep.TOWN;
        showSelectionButtons();
    }

    private void addTownButtons() {
        if (pendingRegion == null) {
            selectionStep = SelectionStep.REGION;
            showSelectionButtons();
            return;
        }

        var towns = SurveyOriginTown.forRegion(pendingRegion);
        int rows = (towns.size() + TOWN_COLS - 1) / TOWN_COLS;
        int gridW = TOWN_COLS * TOWN_BTN_W + (TOWN_COLS - 1) * 4;
        int startX = this.leftPos + (this.imageWidth - gridW) / 2;
        int startY = this.topPos + 112;

        for (int index = 0; index < towns.size(); index++) {
            SurveyOriginTown town = towns.get(index);
            int col = index % TOWN_COLS;
            int row = index / TOWN_COLS;
            int x = startX + col * (TOWN_BTN_W + 4);
            int y = startY + row * (TOWN_BTN_H + 4);
            String label = town.displayName().getString();
            if (label.length() > 11) {
                label = label.substring(0, 10) + "…";
            }
            addRenderableWidget(Button.builder(
                            Component.literal(label).withStyle(pendingRegion.nameColor()),
                            button -> stamp(pendingRegion, town)
                    )
                    .bounds(x, y, TOWN_BTN_W, TOWN_BTN_H)
                    .build());
        }

        int actionY = startY + rows * (TOWN_BTN_H + 4) + 8;
        addRenderableWidget(Button.builder(
                        Component.translatable("ancient_extensions.passport.gui.back"),
                        button -> {
                            selectionStep = SelectionStep.REGION;
                            pendingRegion = null;
                            showSelectionButtons();
                        })
                .bounds(this.leftPos + this.imageWidth / 2 - 76, actionY, 72, 16)
                .build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
                .bounds(this.leftPos + this.imageWidth / 2 + 4, actionY, 72, 16)
                .build());
    }

    private void stamp(SurveyRegion region, SurveyOriginTown town) {
        AncientExtensionsClientHooks.sendSelectOrigin(region.getId(), town.getId());
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
        drawHeader(graphics);
        drawPhotoSlot(graphics);

        if (this.menu.isStamped()) {
            renderStampedContent(graphics);
        } else {
            renderUnstampedContent(graphics);
        }
    }

    private void drawHeader(GuiGraphics graphics) {
        Component header = Component.translatable("ancient_extensions.passport.gui.official_header");
        int cx = this.imageWidth / 2;
        graphics.drawCenteredString(this.font, header, cx + 1, HEADER_Y + 1, COLOR_HEADER_SHADOW);
        graphics.drawCenteredString(this.font, header, cx, HEADER_Y, COLOR_HEADER);
    }

    private void drawPhotoSlot(GuiGraphics graphics) {
        graphics.blit(TEXTURE, PHOTO_X - 2, PHOTO_Y - 2, FRAME_U, FRAME_V, FRAME_W, FRAME_H, TEX_SIZE, TEX_SIZE);
        PassportFaceRenderer.draw(graphics, PHOTO_X + 1, PHOTO_Y + 1, PHOTO_SIZE - 2);

        int nameX = PHOTO_X + PHOTO_SIZE + 6;
        graphics.drawString(
                this.font,
                Component.translatable("ancient_extensions.passport.gui.holder_label")
                        .withStyle(ChatFormatting.ITALIC),
                nameX,
                PHOTO_Y + 4,
                COLOR_LABEL,
                false
        );
        String name = this.menu.getHolderName();
        if (name.length() > 14) {
            name = name.substring(0, 13) + "…";
        }
        graphics.drawString(this.font, name, nameX, PHOTO_Y + 14, COLOR_INK, false);
    }

    private void renderUnstampedContent(GuiGraphics graphics) {
        int sealX = SEAL_CENTER_X;
        int sealY = SEAL_CENTER_Y;
        float pulse = 0.42F + 0.08F * Mth.sin(gameTime() * 0.12F);

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
                sealY + 26,
                0x7A6555
        );

        int cx = this.imageWidth / 2;
        graphics.drawCenteredString(
                this.font,
                Component.translatable("ancient_extensions.passport.gui.unstamped_hint"),
                cx,
                88,
                COLOR_LABEL
        );

        if (selectionStep == SelectionStep.TOWN && pendingRegion != null) {
            graphics.drawCenteredString(
                    this.font,
                    Component.translatable(
                            "ancient_extensions.passport.gui.selected_region",
                            pendingRegion.displayName()
                    ),
                    cx,
                    98,
                    pendingRegion.nameColor().getColor() != null ? pendingRegion.nameColor().getColor() : COLOR_INK
            );
            graphics.drawCenteredString(
                    this.font,
                    Component.translatable("ancient_extensions.passport.gui.choose_town"),
                    cx,
                    108,
                    COLOR_INK
            );
            return;
        }

        graphics.drawCenteredString(
                this.font,
                Component.translatable("ancient_extensions.passport.gui.choose_region"),
                cx,
                106,
                COLOR_INK
        );
    }

    private void renderStampedContent(GuiGraphics graphics) {
        Optional<SurveyRegion> regionOpt = this.menu.getRegion();
        if (regionOpt.isEmpty()) {
            return;
        }
        SurveyRegion region = regionOpt.get();
        int stampColor = region.nameColor().getColor() != null ? region.nameColor().getColor() : 0xFFFFFF;

        // Region name once — under seal, right column (badge code is on the seal itself)
        graphics.drawCenteredString(
                this.font,
                region.displayName(),
                SEAL_CENTER_X,
                SEAL_CENTER_Y + 22,
                stampColor
        );
        this.menu.getTown().ifPresent(town -> graphics.drawCenteredString(
                this.font,
                town.displayName(),
                SEAL_CENTER_X,
                SEAL_CENTER_Y + 32,
                COLOR_LABEL
        ));

        int blurbY = BLURB_START_Y;
        List<FormattedCharSequence> blurbLines = this.font.split(region.passportBlurb(), BLURB_WIDTH);
        for (FormattedCharSequence line : blurbLines) {
            if (blurbY > STATS_DIVIDER_Y - 12) {
                break;
            }
            graphics.drawString(this.font, line, LEFT_COL_X, blurbY, COLOR_BLURB, false);
            blurbY += 10;
        }

        drawStatsPanel(graphics);
    }

    private void drawStatsPanel(GuiGraphics graphics) {
        graphics.hLine(STATS_PANEL_X, STATS_PANEL_X + STATS_PANEL_W, STATS_DIVIDER_Y, 0xFFC9A227);

        graphics.drawString(
                this.font,
                Component.translatable("ancient_extensions.passport.gui.stats_label")
                        .withStyle(ChatFormatting.BOLD),
                STATS_PANEL_X + 2,
                STATS_LABEL_Y,
                COLOR_LABEL,
                false
        );

        graphics.drawString(
                this.font,
                Component.translatable(
                        "ancient_extensions.passport.gui.stats_caught",
                        this.menu.getCaughtSpecies(),
                        this.menu.getResearchPoints()
                ),
                STATS_PANEL_X + 2,
                STATS_LINE_Y,
                COLOR_STATS,
                false
        );

        graphics.drawString(
                this.font,
                Component.translatable(
                        "ancient_extensions.passport.gui.stats_tier",
                        this.menu.getTier().displayName()
                ),
                STATS_PANEL_X + 2,
                STATS_LINE_Y + 10,
                COLOR_STATS,
                false
        );

        List<FormattedCharSequence> footerLines = this.font.split(
                Component.translatable("ancient_extensions.passport.gui.footer")
                        .withStyle(ChatFormatting.ITALIC),
                STATS_PANEL_W - 6
        );
        int footerY = FOOTER_Y;
        for (FormattedCharSequence line : footerLines) {
            graphics.drawString(this.font, line, STATS_PANEL_X + 2, footerY, COLOR_LABEL, false);
            footerY += 9;
        }

        List<FormattedCharSequence> officialLines = this.font.split(
                Component.translatable("ancient_extensions.passport.gui.official_footer"),
                STATS_PANEL_W - 6
        );
        int officialY = OFFICIAL_Y;
        for (FormattedCharSequence line : officialLines) {
            graphics.drawString(this.font, line, STATS_PANEL_X + 2, officialY, COLOR_MUTED, false);
            officialY += 9;
        }
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
