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
import nl.streats1.ancientextensions.menu.FieldSurveyTabletMenu;
import nl.streats1.ancientextensions.network.TabletActionPayload;

public class FieldSurveyTabletScreen extends AbstractContainerScreen<FieldSurveyTabletMenu> {

    private static final ResourceLocation TEXTURE = AncientExtensionsAssets.GUI_FIELD_SURVEY_TABLET;
    private static final int TEX_SIZE = 256;

    private static final int CONTENT_X = 28;
    private static final int CONTENT_Y = 32;
    private static final int CONTENT_W = 208;
    private static final int CONTENT_BOTTOM = 196;

    private static final int ACTION_TOP = 206;
    private static final int ACTION_ROW_H = 18;
    private static final int ACTION_PAD_X = 14;
    private static final int ACTION_GAP = 6;
    private static final int ACTION_BTN_W = 112;
    private static final int ACTION_BTN_H = 16;

    private static final int LINE_HEIGHT = 10;
    private static final int COLOR_HEADER = 0xFFB8E0F0;

    public FieldSurveyTabletScreen(FieldSurveyTabletMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = FieldSurveyTabletMenu.WIDTH;
        this.imageHeight = FieldSurveyTabletMenu.HEIGHT;
        this.inventoryLabelY = 10000;
        this.titleLabelY = -1000;
    }

    @Override
    protected void init() {
        super.init();
        addActionButtons();
    }

    private void addActionButtons() {
        int row1Y = this.topPos + ACTION_TOP;
        int row2Y = row1Y + ACTION_ROW_H;
        int left = this.leftPos + ACTION_PAD_X;
        int right = left + ACTION_BTN_W + ACTION_GAP;

        addRenderableWidget(actionButton(
                left,
                row1Y,
                "ancient_extensions.tablet.open_journal",
                TabletActionPayload.OPEN_JOURNAL
        ));
        addRenderableWidget(actionButton(
                right,
                row1Y,
                "ancient_extensions.tablet.open_passport",
                TabletActionPayload.OPEN_PASSPORT
        ));
        addRenderableWidget(actionButton(
                left,
                row2Y,
                "ancient_extensions.tablet.open_chart",
                TabletActionPayload.OPEN_CHART
        ));

        if (this.menu.getUnclaimedRewardCount() > 0) {
            addRenderableWidget(new JournalClaimButton(
                    right,
                    row2Y,
                    ACTION_BTN_W,
                    ACTION_BTN_H,
                    Component.translatable(
                            "ancient_extensions.tablet.claim_rewards",
                            this.menu.getUnclaimedRewardCount()
                    ),
                    button -> AncientExtensionsClientHooks.sendTabletAction(TabletActionPayload.CLAIM_REWARDS)
            ));
        }
    }

    private JournalClaimButton actionButton(int x, int y, String labelKey, byte action) {
        return new JournalClaimButton(
                x,
                y,
                ACTION_BTN_W,
                ACTION_BTN_H,
                Component.translatable(labelKey),
                button -> AncientExtensionsClientHooks.sendTabletAction(action)
        );
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, TEX_SIZE, TEX_SIZE);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int cx = this.imageWidth / 2;
        Component header = Component.translatable("ancient_extensions.tablet.header");
        graphics.drawCenteredString(this.font, header, cx + 1, 20, GuiTextRender.SLIGHT_BLACK_SHADOW);
        graphics.drawCenteredString(this.font, header, cx, 19, COLOR_HEADER);

        int y = CONTENT_Y;
        for (FormattedCharSequence line : wrappedLines()) {
            if (y > CONTENT_BOTTOM) {
                break;
            }
            GuiTextRender.drawStyledSoft(this.font, graphics, line, CONTENT_X, y);
            y += LINE_HEIGHT;
        }
    }

    private List<FormattedCharSequence> wrappedLines() {
        List<FormattedCharSequence> wrapped = new ArrayList<>();
        for (Component line : this.menu.getLines()) {
            if (line.getString().isEmpty()) {
                wrapped.add(FormattedCharSequence.EMPTY);
                continue;
            }
            wrapped.addAll(this.font.split(line, CONTENT_W));
        }
        if (wrapped.isEmpty()) {
            wrapped.add(this.font.split(
                    Component.translatable("ancient_extensions.tablet.empty"),
                    CONTENT_W
            ).getFirst());
        }
        return wrapped;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderSlot(GuiGraphics graphics, net.minecraft.world.inventory.Slot slot) {
    }
}
