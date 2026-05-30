package nl.streats1.ancientextensions.pouch;

/**
 * Layout constants for the Poké Ball pouch menu.
 */
public final class PokeballPouchConstants {

    public static final int COLS = 9;
    public static final int BASE_SLOT_COUNT = 18;

    private PokeballPouchConstants() {
    }

    public static int rowsForSlots(int slotCount) {
        return (slotCount + COLS - 1) / COLS;
    }

    public static int menuHeight(int slotCount) {
        return PokeballPouchLayout.metrics(slotCount).imageHeight();
    }
}
