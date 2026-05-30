package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.dex.SurveyOriginTown;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import nl.streats1.ancientextensions.menu.RegionalPassportMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
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
    private static final int SEAL_CENTER_Y = 48;
    private static final int SEAL_TEXT_Y = 76;
    private static final int SEAL_TOWN_Y = 86;

    private static final int LEFT_COL_X = 14;
    private static final int BLURB_WIDTH = 130;
    private static final int BLURB_START_Y = 82;

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
    private static final int SELECTION_GRID_Y = 76;

    private static final int REGION_BTN_W = 62;
    private static final int REGION_BTN_H = 18;
    private static final int REGION_COLS = 3;
    private static final int TOWN_BTN_W = 98;
    private static final int TOWN_BTN_H = 18;
    private static final int TOWN_COLS = 2;

    private static final int ACTION_BTN_W = 70;
    private static final int ACTION_BTN_H = 18;
    private static final int FOOTER_BTN_Y = 168;

    private enum SelectionStep {
        REGION,
        TOWN
    }

    private SelectionStep selectionStep = SelectionStep.REGION;
    private SurveyRegion pendingRegion;

    private static final float STAMP_ANIMATION_TICKS = 16.0F;

    /** Bright banner gold — paired with a faint ink halo for contrast. */
    private static final int COLOR_HEADER = 0xFFE8C84A;
    /** Deeper antique gold for step titles (easier to read on parchment). */
    private static final int COLOR_TITLE = 0xFF9A6B14;
    private static final int COLOR_RULE = 0xFFC9A227;

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

    private int footerButtonY() {
        return this.topPos + FOOTER_BTN_Y;
    }

    private int footerButtonX(int slot) {
        int gap = 8;
        int totalW = ACTION_BTN_W * 2 + gap;
        int startX = this.leftPos + (this.imageWidth - totalW) / 2;
        return startX + slot * (ACTION_BTN_W + gap);
    }

    private void addCancelButton(boolean centered) {
        int x = centered
                ? this.leftPos + this.imageWidth / 2 - ACTION_BTN_W / 2
                : footerButtonX(1);
        addRenderableWidget(new FlatTextButton(
                x,
                footerButtonY(),
                ACTION_BTN_W,
                ACTION_BTN_H,
                Component.translatable("gui.cancel").withStyle(ChatFormatting.WHITE),
                button -> onClose()
        ));
    }

    private void addBackButton() {
        addRenderableWidget(new FlatTextButton(
                footerButtonX(0),
                footerButtonY(),
                ACTION_BTN_W,
                ACTION_BTN_H,
                Component.translatable("ancient_extensions.passport.gui.back").withStyle(ChatFormatting.WHITE),
                button -> {
                    selectionStep = SelectionStep.REGION;
                    pendingRegion = null;
                    showSelectionButtons();
                }
        ));
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
            FlatTextButton regionButton = new FlatTextButton(
                    x,
                    y,
                    REGION_BTN_W,
                    REGION_BTN_H,
                    region.displayName().copy().withStyle(region.nameColor()),
                    button -> chooseRegion(region)
            );
            regionButton.setTooltip(Tooltip.create(region.passportPickerTooltip()));
            addRenderableWidget(regionButton);
        }

        addCancelButton(true);
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
        int startY = this.topPos + SELECTION_GRID_Y;

        for (int index = 0; index < towns.size(); index++) {
            SurveyOriginTown town = towns.get(index);
            int col = index % TOWN_COLS;
            int row = index / TOWN_COLS;
            int x = startX + col * (TOWN_BTN_W + 4);
            int y = startY + row * (TOWN_BTN_H + 4);
            FlatTextButton townButton = new FlatTextButton(
                    x,
                    y,
                    TOWN_BTN_W,
                    TOWN_BTN_H,
                    town.displayName().copy().withStyle(pendingRegion.nameColor()),
                    button -> stamp(pendingRegion, town)
            );
            townButton.setTooltip(Tooltip.create(town.displayName()));
            addRenderableWidget(townButton);
        }

        addBackButton();
        addCancelButton(false);
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

        if (this.menu.isStamped()) {
            drawPhotoSlot(graphics);
            renderStampedContent(graphics);
        } else {
            renderSelectionContent(graphics);
        }
    }

    private void drawHeader(GuiGraphics graphics) {
        Component header = Component.translatable("ancient_extensions.passport.gui.official_header")
                .copy()
                .withStyle(ChatFormatting.BOLD);
        GuiTextRender.drawCenteredGold(this.font, graphics, header, this.imageWidth / 2, HEADER_Y, COLOR_HEADER);
    }

    private void drawPhotoSlot(GuiGraphics graphics) {
        graphics.blit(TEXTURE, PHOTO_X - 2, PHOTO_Y - 2, FRAME_U, FRAME_V, FRAME_W, FRAME_H, TEX_SIZE, TEX_SIZE);
        PassportFaceRenderer.draw(graphics, PHOTO_X + 1, PHOTO_Y + 1, PHOTO_SIZE - 2);

        int nameX = PHOTO_X + PHOTO_SIZE + 6;
        drawStyledString(
                graphics,
                Component.translatable("ancient_extensions.passport.gui.holder_label")
                        .withStyle(ChatFormatting.ITALIC, ChatFormatting.WHITE),
                nameX,
                PHOTO_Y + 4
        );
        String name = this.menu.getHolderName();
        if (name.length() > 14) {
            name = name.substring(0, 13) + "…";
        }
        drawStyledString(graphics, Component.literal(name).withStyle(ChatFormatting.WHITE), nameX, PHOTO_Y + 14);
    }

    private void renderSelectionContent(GuiGraphics graphics) {
        int ruleLeft = SELECTION_PANEL_X + 6;
        int ruleRight = SELECTION_PANEL_X + SELECTION_PANEL_W - 6;
        graphics.hLine(ruleLeft, ruleRight, SELECTION_HINT_Y - 4, COLOR_RULE);

        int y = SELECTION_TITLE_Y;
        Component title = selectionStep == SelectionStep.TOWN && pendingRegion != null
                ? Component.translatable("ancient_extensions.passport.gui.choose_town")
                        .copy().withStyle(ChatFormatting.BOLD).withColor(COLOR_TITLE)
                : Component.translatable("ancient_extensions.passport.gui.choose_region")
                        .copy().withStyle(ChatFormatting.BOLD).withColor(COLOR_TITLE);
        GuiTextRender.drawCenteredGold(this.font, graphics, title, this.imageWidth / 2, y, COLOR_TITLE);
        y += 12;

        if (selectionStep == SelectionStep.TOWN && pendingRegion != null) {
            GuiTextRender.drawCenteredStyledSoft(
                    this.font,
                    graphics,
                    Component.empty()
                            .append(Component.translatable("ancient_extensions.passport.gui.region_prefix")
                                    .withStyle(ChatFormatting.WHITE))
                            .append(pendingRegion.labeledName()),
                    this.imageWidth / 2,
                    y
            );
        } else {
            y = drawWrappedCentered(
                    graphics,
                    Component.translatable("ancient_extensions.passport.gui.unstamped_hint")
                            .withStyle(ChatFormatting.WHITE),
                    y
            ) + 2;
            drawWrappedCentered(
                    graphics,
                    Component.translatable("ancient_extensions.passport.gui.hover_hint")
                            .withStyle(ChatFormatting.ITALIC, ChatFormatting.WHITE),
                    y
            );
        }

        graphics.hLine(ruleLeft, ruleRight, SELECTION_GRID_Y - 6, COLOR_RULE);
    }

    private int drawWrappedCentered(GuiGraphics graphics, Component text, int y) {
        return GuiTextRender.drawWrappedCenteredSoft(
                this.font,
                graphics,
                text,
                this.imageWidth / 2,
                y,
                SELECTION_PANEL_W - 8
        );
    }

    private void drawStyledString(GuiGraphics graphics, Component text, int x, int y) {
        GuiTextRender.drawStyledSoft(this.font, graphics, text, x, y);
    }

    private void renderStampedContent(GuiGraphics graphics) {
        Optional<SurveyRegion> regionOpt = this.menu.getRegion();
        if (regionOpt.isEmpty()) {
            return;
        }
        SurveyRegion region = regionOpt.get();

        drawStyledString(
                graphics,
                region.displayName().copy().withStyle(region.nameColor(), ChatFormatting.BOLD),
                SEAL_CENTER_X - this.font.width(region.displayName()) / 2,
                SEAL_TEXT_Y
        );
        this.menu.getTown().ifPresent(town -> drawStyledString(
                graphics,
                town.displayName().copy().withStyle(ChatFormatting.AQUA),
                SEAL_CENTER_X - this.font.width(town.displayName()) / 2,
                SEAL_TOWN_Y
        ));

        GuiTextRender.drawWrappedSoft(
                this.font,
                graphics,
                region.passportBlurb().copy().withStyle(ChatFormatting.WHITE),
                LEFT_COL_X,
                BLURB_START_Y,
                BLURB_WIDTH
        );

        drawStatsPanel(graphics);
    }

    private void drawStatsPanel(GuiGraphics graphics) {
        graphics.hLine(STATS_PANEL_X, STATS_PANEL_X + STATS_PANEL_W, STATS_DIVIDER_Y, 0xFFC9A227);

        GuiTextRender.drawGold(
                this.font,
                graphics,
                Component.translatable("ancient_extensions.passport.gui.stats_label")
                        .withStyle(ChatFormatting.BOLD),
                STATS_PANEL_X + 2,
                STATS_LABEL_Y,
                COLOR_TITLE
        );

        drawStyledString(
                graphics,
                Component.translatable(
                                "ancient_extensions.journal.stats_caught_prefix",
                                this.menu.getCaughtSpecies()
                        ).withStyle(ChatFormatting.WHITE)
                        .append(Component.translatable(
                                "ancient_extensions.journal.stats_rp_suffix",
                                this.menu.getResearchPoints()
                        ).withStyle(ChatFormatting.DARK_GREEN)),
                STATS_PANEL_X + 2,
                STATS_LINE_Y
        );

        drawStyledString(
                graphics,
                Component.translatable(
                        "ancient_extensions.passport.gui.stats_tier",
                        this.menu.getTier().displayName().copy().withStyle(ChatFormatting.GOLD)
                ).withStyle(ChatFormatting.WHITE),
                STATS_PANEL_X + 2,
                STATS_LINE_Y + 10
        );

        GuiTextRender.drawWrappedSoft(
                this.font,
                graphics,
                Component.translatable("ancient_extensions.passport.gui.footer")
                        .withStyle(ChatFormatting.ITALIC, ChatFormatting.WHITE),
                STATS_PANEL_X + 2,
                FOOTER_Y,
                STATS_PANEL_W - 6
        );

        GuiTextRender.drawWrappedSoft(
                this.font,
                graphics,
                Component.translatable("ancient_extensions.passport.gui.official_footer")
                        .withStyle(ChatFormatting.WHITE),
                STATS_PANEL_X + 2,
                OFFICIAL_Y,
                STATS_PANEL_W - 6
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
