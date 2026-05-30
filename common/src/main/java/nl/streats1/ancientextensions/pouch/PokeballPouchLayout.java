package nl.streats1.ancientextensions.pouch;

/**
 * Vanilla-aligned slot layout for the Poké Ball pouch menu.
 */
public final class PokeballPouchLayout {

    public static final int WIDTH = 176;
    public static final int POUCH_START_X = 8;
    /** Matches slot art in {@code textures/gui/pokeball_pouch.png}. */
    public static final int POUCH_START_Y = 30;
    public static final int PLAYER_INV_GAP = 12;
    public static final int MIN_HEIGHT = 166;

    private PokeballPouchLayout() {
    }

    public record Metrics(int pouchRows, int playerInvY, int hotbarY, int imageHeight, int inventoryLabelY) {
    }

    public static Metrics metrics(int slotCount) {
        int pouchRows = PokeballPouchConstants.rowsForSlots(slotCount);
        int playerInvY = Math.max(84, POUCH_START_Y + pouchRows * 18 + PLAYER_INV_GAP);
        int hotbarY = playerInvY + 58;
        int imageHeight = Math.max(MIN_HEIGHT, hotbarY + 24);
        return new Metrics(pouchRows, playerInvY, hotbarY, imageHeight, playerInvY - 12);
    }
}
