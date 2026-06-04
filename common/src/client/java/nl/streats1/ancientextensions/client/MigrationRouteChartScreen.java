package nl.streats1.ancientextensions.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

import nl.streats1.ancientextensions.AncientExtensionsAssets;
import nl.streats1.ancientextensions.menu.MigrationRouteChartMenu;

public class MigrationRouteChartScreen extends AbstractContainerScreen<MigrationRouteChartMenu> {

    private static final ResourceLocation TEXTURE = AncientExtensionsAssets.GUI_REGIONAL_SURVEY_JOURNAL;
    private static final int TEX_SIZE = 256;

    private static final int CONTENT_X = 28;
    private static final int CONTENT_Y = 32;
    private static final int CONTENT_W = 208;
    private static final int CONTENT_BOTTOM = 224;

    private static final int FOOTER_TOP = 228;
    private static final int FOOTER_H = 24;

    private static final int PAGE_BTN_LEFT_X = 18;
    private static final int PAGE_BTN_GAP = 6;
    private static final int PAGE_LABEL_GAP = 8;

    private static final int LINE_HEIGHT = 10;

    private static final int COLOR_HEADER = 0xFFF0E2CC;
    private static final int COLOR_PAGE = 0xFF2A1810;

    private final List<List<FormattedCharSequence>> pages = new ArrayList<>();
    private int pageIndex;
    private JournalNavButton prevButton;
    private JournalNavButton nextButton;

    public MigrationRouteChartScreen(MigrationRouteChartMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = MigrationRouteChartMenu.WIDTH;
        this.imageHeight = MigrationRouteChartMenu.HEIGHT;
        this.inventoryLabelY = 10000;
        this.titleLabelY = -1000;
    }

    @Override
    protected void init() {
        super.init();
        buildPages();
        addNavButtons();
    }

    private int linesPerPage() {
        return (CONTENT_BOTTOM - CONTENT_Y) / LINE_HEIGHT;
    }

    private void buildPages() {
        pages.clear();
        int maxLines = linesPerPage();
        List<FormattedCharSequence> current = new ArrayList<>();
        for (Component line : this.menu.getLines()) {
            if (line.getString().isEmpty()) {
                if (current.size() >= maxLines) {
                    pages.add(List.copyOf(current));
                    current = new ArrayList<>();
                }
                current.add(FormattedCharSequence.EMPTY);
                continue;
            }
            List<FormattedCharSequence> wrapped = this.font.split(line, CONTENT_W);
            for (FormattedCharSequence wrappedLine : wrapped) {
                if (current.size() >= maxLines) {
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
                    Component.translatable("ancient_extensions.migration_chart.empty"),
                    CONTENT_W
            ).getFirst()));
        }
        pageIndex = Math.min(pageIndex, pages.size() - 1);
    }

    private void addNavButtons() {
        int footerBtnY = this.topPos + FOOTER_TOP + (FOOTER_H - JournalNavButton.SPRITE_H) / 2;
        int prevX = this.leftPos + PAGE_BTN_LEFT_X;
        int nextX = prevX + JournalNavButton.SPRITE_W + PAGE_BTN_GAP;

        prevButton = addRenderableWidget(new JournalNavButton(prevX, footerBtnY, false, button -> changePage(-1)));
        nextButton = addRenderableWidget(new JournalNavButton(nextX, footerBtnY, true, button -> changePage(1)));
        updateNavButtons();
    }

    private void updateNavButtons() {
        if (prevButton != null) {
            prevButton.active = pageIndex > 0;
        }
        if (nextButton != null) {
            nextButton.active = pageIndex < pages.size() - 1;
        }
    }

    private void changePage(int delta) {
        int next = pageIndex + delta;
        if (next < 0 || next >= pages.size()) {
            return;
        }
        pageIndex = next;
        updateNavButtons();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, TEX_SIZE, TEX_SIZE);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int cx = this.imageWidth / 2;

        Component header = Component.translatable("item.ancient_extensions.migration_route_chart");
        graphics.drawCenteredString(this.font, header, cx + 1, 20, GuiTextRender.SLIGHT_BLACK_SHADOW);
        graphics.drawCenteredString(this.font, header, cx, 19, COLOR_HEADER);

        List<FormattedCharSequence> page = pages.get(pageIndex);
        int y = CONTENT_Y;
        for (FormattedCharSequence line : page) {
            GuiTextRender.drawStyledSoft(this.font, graphics, line, CONTENT_X, y);
            y += LINE_HEIGHT;
        }

        if (pages.size() > 1) {
            String label = (pageIndex + 1) + " / " + pages.size();
            int labelX = PAGE_BTN_LEFT_X + JournalNavButton.SPRITE_W + PAGE_BTN_GAP
                    + JournalNavButton.SPRITE_W + PAGE_LABEL_GAP;
            int labelY = FOOTER_TOP + (FOOTER_H - this.font.lineHeight) / 2;
            graphics.drawString(this.font, label, labelX + 1, labelY + 1, GuiTextRender.SLIGHT_BLACK_SHADOW, false);
            graphics.drawString(this.font, label, labelX, labelY, COLOR_PAGE, false);
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
