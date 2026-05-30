package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.dex.SurveyOriginTown;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import nl.streats1.ancientextensions.menu.RegionalPassportMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
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

    private static final int SEAL_CENTER_X = 168;
    private static final int SEAL_CENTER_Y = 52;

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

    private static final int SELECTION_PANEL_X = 10;
    private static final int SELECTION_PANEL_W = 200;
    private static final int SELECTION_TITLE_Y = 28;
    private static final int SELECTION_HINT_Y = 40;
    private static final int SELECTION_GRID_Y = 54;

    private static final int REGION_BTN_W = 62;
    private static final int REGION_BTN_H = 18;
    private static final int REGION_COLS = 3;
    private static final int TOWN_BTN_W = 94;
    private static final int TOWN_BTN_H = 18;
    private static final int TOWN_COLS = 2;

    private static final int ACTION_BTN_W = 70;
    private static final int ACTION_BTN_H = 18;
    private static final int CANCEL_Y_OFFSET = 22;

    private enum SelectionStep {
        REGION,
        TOWN
    }

    private SelectionStep selectionStep = SelectionStep.REGION;
    private SurveyRegion pendingRegion;

    private static final float STAMP_ANIMATION_TICKS = 16.0F;

    private static final int COLOR_HEADER = 0xFFE8C84A;
    private static final int COLOR_HEADER_SHADOW = 0xFF402010;
    private static final int COLOR_LABEL = 0xFF4A3828;
    private static final int COLOR_INK = 0xFF1A1208;
    private static final int COLOR_INK_SHADOW = 0xFFEBE0D0;
    private static final int COLOR_BLURB = 0xFF2A2018;
    private static final int COLOR_STATS = 0xFF1E3A22;
    private static final int COLOR_MUTED = 0xFF5C4A3A;
    private static final int COLOR_PANEL = 0xD8F3E8D8;

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

    private int cancelButtonY() {
        return this.topPos + this.imageHeight - CANCEL_Y_OFFSET;
    }

    private int cancelButtonX() {
        return this.leftPos + this.imageWidth / 2 - ACTION_BTN_W / 2;
    }

    private void addCancelButton() {
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
                .bounds(cancelButtonX(), cancelButtonY(), ACTION_BTN_W, ACTION_BTN_H)
                .build());
    }

    private void addRegionButtons() {
        SurveyRegion[] regions = SurveyRegion.values();
        int rows = (regions.length + REGION_COLS - 1) / REGION_COLS;
        int gridW = REGION_COLS * REGION_BTN_W + (REGION_COLS - 1) * 4;
        int startX = this.leftPos + (this.imageWidth - gridW) / 2;
        int startY = this.topPos + SELECTION_GRID_Y;

        for (int index = 0; index < regions.length; index++) {
            SurveyRegion region = regions[index];
            int col = index % REGION_COLS;
            int row = index / REGION_COLS;
            int x = startX + col * (REGION_BTN_W + 4);
            int y = startY + row * (REGION_BTN_H + 4);
            String label = region.pickerButtonLabel();
            if (label.length() > 8) {
                label = label.substring(0, 7) + "…";
            }
            addRenderableWidget(Button.builder(
                            Component.literal(label).withStyle(region.nameColor()),
                            button -> chooseRegion(region)
                    )
                    .bounds(x, y, REGION_BTN_W, REGION_BTN_H)
                    .tooltip(Tooltip.create(region.passportPickerTooltip()))
                    .build());
        }

        addCancelButton();
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
        int startY = this.topPos + SELECTION_GRID_Y + 10;

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
                    .tooltip(Tooltip.create(town.displayName()))
                    .build());
        }

        int actionY = cancelButtonY() - ACTION_BTN_H - 6;
        addRenderableWidget(Button.builder(
                        Component.translatable("ancient_extensions.passport.gui.back"),
                        button -> {
                            selectionStep = SelectionStep.REGION;
                            pendingRegion = null;
                            showSelectionButtons();
                        })
                .bounds(this.leftPos + this.imageWidth / 2 - ACTION_BTN_W - 4, actionY, ACTION_BTN_W, ACTION_BTN_H)
                .build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
                .bounds(this.leftPos + this.imageWidth / 2 + 4, actionY, ACTION_BTN_W, ACTION_BTN_H)
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
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight - 36, TEX_SIZE, TEX_SIZE);
        graphics.fill(
                this.leftPos,
                this.topPos + this.imageHeight - 36,
                this.leftPos + this.imageWidth,
                this.topPos + this.imageHeight,
                0xFFF0E2CC
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        drawHeader(graphics);

        if (this.menu.isStamped()) {
            drawPhotoSlot(graphics);
            renderStampedContent(graphics);
        } else {
            renderSelectionContent(graphics);
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
        drawShadowedString(
                graphics,
                Component.translatable("ancient_extensions.passport.gui.holder_label")
                        .withStyle(ChatFormatting.ITALIC),
                nameX,
                PHOTO_Y + 4,
                COLOR_LABEL
        );
        String name = this.menu.getHolderName();
        if (name.length() > 14) {
            name = name.substring(0, 13) + "…";
        }
        drawShadowedString(graphics, Component.literal(name), nameX, PHOTO_Y + 14, COLOR_INK);
    }

    private void renderSelectionContent(GuiGraphics graphics) {
        graphics.fill(SELECTION_PANEL_X, 22, SELECTION_PANEL_X + SELECTION_PANEL_W, SELECTION_GRID_Y - 4, COLOR_PANEL);

        Component title = selectionStep == SelectionStep.TOWN && pendingRegion != null
                ? Component.translatable("ancient_extensions.passport.gui.choose_town").copy().withStyle(ChatFormatting.BOLD)
                : Component.translatable("ancient_extensions.passport.gui.choose_region").copy().withStyle(ChatFormatting.BOLD);
        drawWrappedCentered(graphics, title, SELECTION_TITLE_Y, COLOR_INK);

        if (selectionStep == SelectionStep.TOWN && pendingRegion != null) {
            drawWrappedCentered(
                    graphics,
                    Component.translatable(
                            "ancient_extensions.passport.gui.selected_region",
                            pendingRegion.displayName()
                    ).copy().withStyle(pendingRegion.nameColor()),
                    SELECTION_HINT_Y,
                    COLOR_INK
            );
        } else {
            drawWrappedCentered(
                    graphics,
                    Component.translatable("ancient_extensions.passport.gui.unstamped_hint"),
                    SELECTION_HINT_Y,
                    COLOR_MUTED
            );
        }

        drawWrappedCentered(
                graphics,
                Component.translatable("ancient_extensions.passport.gui.hover_hint")
                        .copy().withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY),
                SELECTION_HINT_Y + 10,
                COLOR_MUTED
        );
    }

    private void drawWrappedCentered(GuiGraphics graphics, Component text, int y, int color) {
        List<FormattedCharSequence> lines = this.font.split(text, SELECTION_PANEL_W - 8);
        int lineY = y;
        int cx = this.imageWidth / 2;
        for (FormattedCharSequence line : lines) {
            int lineW = this.font.width(line);
            int x = cx - lineW / 2;
            graphics.drawString(this.font, line, x + 1, lineY + 1, COLOR_INK_SHADOW, false);
            graphics.drawString(this.font, line, x, lineY, color, false);
            lineY += 10;
        }
    }

    private void drawShadowedString(GuiGraphics graphics, Component text, int x, int y, int color) {
        graphics.drawString(this.font, text, x + 1, y + 1, COLOR_INK_SHADOW, false);
        graphics.drawString(this.font, text, x, y, color, false);
    }

    private void renderStampedContent(GuiGraphics graphics) {
        Optional<SurveyRegion> regionOpt = this.menu.getRegion();
        if (regionOpt.isEmpty()) {
            return;
        }
        SurveyRegion region = regionOpt.get();
        int stampColor = region.nameColor().getColor() != null ? region.nameColor().getColor() : 0xFFFFFF;

        drawShadowedString(graphics, region.displayName(), SEAL_CENTER_X - this.font.width(region.displayName()) / 2, SEAL_CENTER_Y + 22, stampColor);
        this.menu.getTown().ifPresent(town -> drawShadowedString(
                graphics,
                town.displayName(),
                SEAL_CENTER_X - this.font.width(town.displayName()) / 2,
                SEAL_CENTER_Y + 32,
                COLOR_LABEL
        ));

        int blurbY = BLURB_START_Y;
        List<FormattedCharSequence> blurbLines = this.font.split(region.passportBlurb(), BLURB_WIDTH);
        for (FormattedCharSequence line : blurbLines) {
            if (blurbY > STATS_DIVIDER_Y - 12) {
                break;
            }
            graphics.drawString(this.font, line, LEFT_COL_X + 1, blurbY + 1, COLOR_INK_SHADOW, false);
            graphics.drawString(this.font, line, LEFT_COL_X, blurbY, COLOR_BLURB, false);
            blurbY += 10;
        }

        drawStatsPanel(graphics);
    }

    private void drawStatsPanel(GuiGraphics graphics) {
        graphics.hLine(STATS_PANEL_X, STATS_PANEL_X + STATS_PANEL_W, STATS_DIVIDER_Y, 0xFFC9A227);

        drawShadowedString(
                graphics,
                Component.translatable("ancient_extensions.passport.gui.stats_label")
                        .withStyle(ChatFormatting.BOLD),
                STATS_PANEL_X + 2,
                STATS_LABEL_Y,
                COLOR_LABEL
        );

        drawShadowedString(
                graphics,
                Component.translatable(
                        "ancient_extensions.passport.gui.stats_caught",
                        this.menu.getCaughtSpecies(),
                        this.menu.getResearchPoints()
                ),
                STATS_PANEL_X + 2,
                STATS_LINE_Y,
                COLOR_STATS
        );

        drawShadowedString(
                graphics,
                Component.translatable(
                        "ancient_extensions.passport.gui.stats_tier",
                        this.menu.getTier().displayName()
                ),
                STATS_PANEL_X + 2,
                STATS_LINE_Y + 10,
                COLOR_STATS
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
