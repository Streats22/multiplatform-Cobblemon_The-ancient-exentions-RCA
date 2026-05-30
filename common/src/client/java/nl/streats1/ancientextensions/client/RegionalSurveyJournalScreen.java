package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.menu.RegionalSurveyJournalMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class RegionalSurveyJournalScreen extends AbstractContainerScreen<RegionalSurveyJournalMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AncientExtensionsConstants.MOD_ID,
            "textures/gui/regional_survey_journal.png"
    );
    private static final int TEX_SIZE = 256;

    private static final int CONTENT_X = 18;
    private static final int CONTENT_Y = 32;
    private static final int CONTENT_W = 184;
    private static final int CONTENT_TOP = 30;
    private static final int CONTENT_BOTTOM = 188;
    private static final int LINE_HEIGHT = 10;
    private static final int LINES_PER_PAGE = 14;

    private static final int COLOR_HEADER = 0xFFF0E2CC;
    private static final int COLOR_HEADER_SHADOW = 0xFF1A1008;
    private static final int COLOR_BODY = 0xFF2A1810;
    private static final int COLOR_PAGE = 0xFF5C4030;

    private final List<List<FormattedCharSequence>> pages = new ArrayList<>();
    private int pageIndex;

    public RegionalSurveyJournalScreen(RegionalSurveyJournalMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = RegionalSurveyJournalMenu.WIDTH;
        this.imageHeight = RegionalSurveyJournalMenu.HEIGHT;
        this.inventoryLabelY = 10000;
        this.titleLabelY = -1000;
    }

    @Override
    protected void init() {
        super.init();
        buildPages();
        addPageButtons();
    }

    private void buildPages() {
        pages.clear();
        List<FormattedCharSequence> current = new ArrayList<>();
        for (Component line : this.menu.getLines()) {
            if (line.getString().isEmpty()) {
                if (current.size() >= LINES_PER_PAGE) {
                    pages.add(List.copyOf(current));
                    current = new ArrayList<>();
                }
                current.add(FormattedCharSequence.EMPTY);
                continue;
            }
            List<FormattedCharSequence> wrapped = this.font.split(line, CONTENT_W);
            for (FormattedCharSequence wrappedLine : wrapped) {
                if (current.size() >= LINES_PER_PAGE) {
                    pages.add(List.copyOf(current));
                    current = new ArrayList<>();
                }
                current.add(wrappedLine);
            }
        }
        if (!current.isEmpty()) {
            pages.add(List.copyOf(current));
        }
        if (pages.isEmpty()) {
            pages.add(List.of(this.font.split(
                    Component.translatable("ancient_extensions.journal.empty"),
                    CONTENT_W
            ).getFirst()));
        }
        pageIndex = Math.min(pageIndex, pages.size() - 1);
    }

    private void addPageButtons() {
        int buttonY = this.topPos + this.imageHeight - 24;
        int centerX = this.leftPos + this.imageWidth / 2;
        addRenderableWidget(Button.builder(Component.literal("<"), button -> changePage(-1))
                .bounds(centerX - 58, buttonY, 20, 16)
                .build());
        addRenderableWidget(Button.builder(Component.literal(">"), button -> changePage(1))
                .bounds(centerX + 38, buttonY, 20, 16)
                .build());
    }

    private void changePage(int delta) {
        int next = pageIndex + delta;
        if (next < 0 || next >= pages.size()) {
            return;
        }
        pageIndex = next;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, TEX_SIZE, TEX_SIZE);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        Component header = Component.translatable("ancient_extensions.journal.header");
        int cx = this.imageWidth / 2;
        graphics.drawCenteredString(this.font, header, cx + 1, 18, COLOR_HEADER_SHADOW);
        graphics.drawCenteredString(this.font, header, cx, 17, COLOR_HEADER);

        // Flat backdrop so ruled lines do not compete with body text
        graphics.fill(CONTENT_X - 2, CONTENT_TOP, CONTENT_X + CONTENT_W + 2, CONTENT_BOTTOM, 0xC8F0E2CC);

        List<FormattedCharSequence> page = pages.get(pageIndex);
        int y = CONTENT_Y;
        for (FormattedCharSequence line : page) {
            graphics.drawString(this.font, line, CONTENT_X, y, COLOR_BODY, false);
            y += LINE_HEIGHT;
        }

        if (pages.size() > 1) {
            Component pageLabel = Component.translatable(
                    "ancient_extensions.journal.page",
                    pageIndex + 1,
                    pages.size()
            );
            graphics.drawCenteredString(this.font, pageLabel, cx, this.imageHeight - 20, COLOR_PAGE);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderSlot(GuiGraphics graphics, net.minecraft.world.inventory.Slot slot) {
        // No inventory slots on this screen.
    }
}
