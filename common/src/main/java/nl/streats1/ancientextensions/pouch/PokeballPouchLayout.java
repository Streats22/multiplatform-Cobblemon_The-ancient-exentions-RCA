package nl.streats1.ancientextensions.pouch;

/**
 * Vanilla-aligned slot layout for the Poké Ball pouch menu.
 */
public final class PokeballPouchLayout {

    public static final int WIDTH = 176;
    public static final int POUCH_START_X = 8;
    /** Matches slot art in {@code textures/gui/pokeball_pouch.png}. */
    public static final int POUCH_START_Y = 30;
    public static final int PLAYER_INV_GAP = 10;
    public static final int SLOT_STRIDE = 18;
    public static final int MIN_HEIGHT = 166;

    /** Texture UVs for {@code textures/gui/pokeball_pouch.png} (176×166). */
    public static final int TEX_HEADER_H = 17;
    public static final int TEX_SLOT_ROW_Y = 30;
    public static final int TEX_SLOT_ROW_H = 18;
    public static final int TEX_SLOT_X = 7;
    public static final int TEX_SLOT_W = 162;
    public static final int TEX_PANEL_X = 6;
    public static final int TEX_PANEL_W = 164;
    public static final int TEX_PANEL_FILL_Y = 18;
    public static final int TEX_PLAYER_ROW_Y = 84;
    public static final int TEX_HOTBAR_Y = 142;
    public static final int TEX_FOOTER_H = 24;

    private PokeballPouchLayout() {
    }

    public record Metrics(int pouchRows, int playerInvY, int hotbarY, int imageHeight, int inventoryLabelY) {
        public int pouchAreaBottom() {
            return POUCH_START_Y + pouchRows * SLOT_STRIDE + 4;
        }
    }

    public static Metrics metrics(int slotCount) {
        int pouchRows = PokeballPouchConstants.rowsForSlots(slotCount);
        int playerInvY = POUCH_START_Y + pouchRows * SLOT_STRIDE + PLAYER_INV_GAP;
        int hotbarY = playerInvY + 58;
        int imageHeight = Math.max(MIN_HEIGHT, hotbarY + TEX_FOOTER_H);
        return new Metrics(pouchRows, playerInvY, hotbarY, imageHeight, playerInvY - 12);
    }
}
