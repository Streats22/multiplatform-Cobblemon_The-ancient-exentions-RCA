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
    private static final int CONTENT_BOTTOM = 224;

    private static final int FOOTER_TOP = 228;
    private static final int FOOTER_H = 24;
    private static final int FOOTER_PAD_X = 14;

    private static final int PAGE_BTN_W = 20;
    private static final int PAGE_BTN_H = 16;
    private static final int PAGE_BTN_LEFT_X = 18;
    private static final int PAGE_BTN_GAP = 4;
    private static final int PAGE_LABEL_X = 62;

    private static final int CLAIM_BTN_W = 108;
    private static final int CLAIM_BTN_H = 16;

    private static final int LINE_HEIGHT = 10;

    private static final int COLOR_HEADER = 0xFFF0E2CC;
    private static final int COLOR_HEADER_SHADOW = 0xFF1A1008;
    private static final int COLOR_PAGE = 0xFF2A1810;
    private static final int COLOR_PAGE_SHADOW = 0xFFF5E8D8;

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

    private boolean hasClaimableRewards() {
        return this.menu.getUnclaimedRewardCount() > 0;
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
                    Component.translatable("ancient_extensions.journal.empty"),
                    CONTENT_W
            ).getFirst()));
        }
        pageIndex = Math.min(pageIndex, pages.size() - 1);
    }

    private void addPageButtons() {
        int footerBtnY = this.topPos + FOOTER_TOP + (FOOTER_H - PAGE_BTN_H) / 2;
        int prevX = this.leftPos + PAGE_BTN_LEFT_X;
        int nextX = prevX + PAGE_BTN_W + PAGE_BTN_GAP;

        addRenderableWidget(Button.builder(Component.literal("◀"), button -> changePage(-1))
                .bounds(prevX, footerBtnY, PAGE_BTN_W, PAGE_BTN_H)
                .build());
        addRenderableWidget(Button.builder(Component.literal("▶"), button -> changePage(1))
                .bounds(nextX, footerBtnY, PAGE_BTN_W, PAGE_BTN_H)
                .build());

        if (hasClaimableRewards()) {
            int claimX = this.leftPos + this.imageWidth - FOOTER_PAD_X - CLAIM_BTN_W;
            int claimY = this.topPos + FOOTER_TOP + (FOOTER_H - CLAIM_BTN_H) / 2;
            addRenderableWidget(new JournalClaimButton(
                    claimX,
                    claimY,
                    CLAIM_BTN_W,
                    CLAIM_BTN_H,
                    Component.translatable(
                            "ancient_extensions.journal.claim_rewards_short",
                            this.menu.getUnclaimedRewardCount()
                    ),
                    button -> AncientExtensionsClientHooks.sendClaimTierRewards()
            ));
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

        List<FormattedCharSequence> page = pages.get(pageIndex);
        int y = CONTENT_Y;
        for (FormattedCharSequence line : page) {
            GuiTextRender.drawStyled(this.font, graphics, line, CONTENT_X, y, false);
            y += LINE_HEIGHT;
        }

        drawFooter(graphics);
    }

    private void drawFooter(GuiGraphics graphics) {
        Component pageLabel = pages.size() > 1
                ? Component.translatable("ancient_extensions.journal.page", pageIndex + 1, pages.size())
                : Component.translatable("ancient_extensions.journal.page_single");
        int labelY = FOOTER_TOP + (FOOTER_H - this.font.lineHeight) / 2;
        graphics.drawString(this.font, pageLabel, PAGE_LABEL_X + 1, labelY + 1, COLOR_PAGE_SHADOW, false);
        graphics.drawString(this.font, pageLabel, PAGE_LABEL_X, labelY, COLOR_PAGE, false);
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
