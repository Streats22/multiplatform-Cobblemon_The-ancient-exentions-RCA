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

    private static final int CONTENT_X = 20;
    private static final int CONTENT_Y = 36;
    private static final int CONTENT_W = 216;
    private static final int CONTENT_TOP = 32;
    private static final int CONTENT_BOTTOM = 210;
    private static final int LINE_HEIGHT = 10;
    private static final int LINES_PER_PAGE = 17;

    private static final int FOOTER_TOP = 226;
    private static final int FOOTER_H = 24;

    private static final int PAGE_BTN_W = 20;
    private static final int PAGE_BTN_H = 16;

    private static final int COLOR_HEADER = 0xFFF0E2CC;
    private static final int COLOR_HEADER_SHADOW = 0xFF1A1008;
    private static final int COLOR_BODY = 0xFF1A1208;
    private static final int COLOR_BODY_SHADOW = 0xFFEBE0D0;
    private static final int COLOR_PAGE = 0xFF2A1810;
    private static final int COLOR_PAGE_SHADOW = 0xFFF5E8D8;
    private static final int COLOR_PANEL = 0xD8F0E2CC;
    private static final int COLOR_FOOTER = 0xE8DCC8A8;

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
        int footerCenterY = this.topPos + FOOTER_TOP + FOOTER_H / 2;
        int centerX = this.leftPos + this.imageWidth / 2;

        addRenderableWidget(Button.builder(Component.literal("◀"), button -> changePage(-1))
                .bounds(centerX - 56, footerCenterY - PAGE_BTN_H / 2, PAGE_BTN_W, PAGE_BTN_H)
                .build());
        addRenderableWidget(Button.builder(Component.literal("▶"), button -> changePage(1))
                .bounds(centerX + 36, footerCenterY - PAGE_BTN_H / 2, PAGE_BTN_W, PAGE_BTN_H)
                .build());

        if (this.menu.getUnclaimedRewardCount() > 0) {
            addRenderableWidget(Button.builder(
                            Component.translatable(
                                    "ancient_extensions.journal.claim_rewards",
                                    this.menu.getUnclaimedRewardCount()
                            ),
                            button -> AncientExtensionsClientHooks.sendClaimTierRewards())
                    .bounds(this.leftPos + CONTENT_X, this.topPos + CONTENT_BOTTOM - 22, CONTENT_W, 18)
                    .build());
        }
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
        int cx = this.imageWidth / 2;

        Component header = Component.translatable("ancient_extensions.journal.header");
        graphics.drawCenteredString(this.font, header, cx + 1, 20, COLOR_HEADER_SHADOW);
        graphics.drawCenteredString(this.font, header, cx, 19, COLOR_HEADER);

        graphics.fill(CONTENT_X - 2, CONTENT_TOP, CONTENT_X + CONTENT_W + 2, CONTENT_BOTTOM, COLOR_PANEL);

        List<FormattedCharSequence> page = pages.get(pageIndex);
        int y = CONTENT_Y;
        for (FormattedCharSequence line : page) {
            GuiTextRender.drawStyled(this.font, graphics, line, CONTENT_X, y);
            y += LINE_HEIGHT;
        }

        drawFooter(graphics, cx);
    }

    private void drawFooter(GuiGraphics graphics, int cx) {
        graphics.fill(14, FOOTER_TOP, this.imageWidth - 14, FOOTER_TOP + FOOTER_H, COLOR_FOOTER);

        Component pageLabel = pages.size() > 1
                ? Component.translatable("ancient_extensions.journal.page", pageIndex + 1, pages.size())
                : Component.translatable("ancient_extensions.journal.page_single");
        int labelY = FOOTER_TOP + (FOOTER_H - this.font.lineHeight) / 2;
        graphics.drawCenteredString(this.font, pageLabel, cx + 1, labelY + 1, COLOR_PAGE_SHADOW);
        graphics.drawCenteredString(this.font, pageLabel, cx, labelY, COLOR_PAGE);
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
