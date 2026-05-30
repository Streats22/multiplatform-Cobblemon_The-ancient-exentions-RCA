package nl.streats1.ancientextensions.dex;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.Arrays;
import java.util.Optional;

/**
 * Pokémon region the player chose as their survey origin (passport stamp).
 */
public enum SurveyRegion {
    KANTO("kanto", "KT", ChatFormatting.RED),
    JOHTO("johto", "JH", ChatFormatting.GOLD),
    HOENN("hoenn", "HN", ChatFormatting.GREEN),
    SINNOH("sinnoh", "SN", ChatFormatting.AQUA),
    UNOVA("unova", "UN", ChatFormatting.DARK_PURPLE),
    KALOS("kalos", "KL", ChatFormatting.LIGHT_PURPLE),
    ALOLA("alola", "AL", ChatFormatting.YELLOW),
    GALAR("galar", "GA", ChatFormatting.BLUE),
    PALDEA("paldea", "PA", ChatFormatting.DARK_GREEN);

    private final String id;
    private final String badgeCode;
    private final ChatFormatting color;

    SurveyRegion(String id, String badgeCode, ChatFormatting color) {
        this.id = id;
        this.badgeCode = badgeCode;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    /** Two-letter code shown beside the player name in multiplayer. */
    public String getBadgeCode() {
        return badgeCode;
    }

    public ChatFormatting nameColor() {
        return color;
    }

    public Component displayName() {
        return Component.translatable("ancient_extensions.region." + id);
    }

    /** Colored {@code [KT]} tag prefix for tab list and name tags. */
    public Component listBadge() {
        return Component.literal("[" + badgeCode + "] ")
                .withStyle(color);
    }

    /** Badge + region name for menus and books. */
    public Component labeledName() {
        MutableComponent name = displayName().copy();
        name.setStyle(Style.EMPTY.withColor(color));
        return listBadge().copy().append(name);
    }

    public Component passportBlurb() {
        return Component.translatable("ancient_extensions.region." + id + ".blurb");
    }

    /** Hover text for the origin picker — full region name, code, and blurb. */
    public Component passportPickerTooltip() {
        return Component.empty()
                .append(labeledName())
                .append("\n")
                .append(passportBlurb().copy().withStyle(ChatFormatting.WHITE));
    }

    /** Short label that fits on a region button. */
    public String pickerButtonLabel() {
        return displayName().getString();
    }

    public static Optional<SurveyRegion> fromId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(region -> region.id.equalsIgnoreCase(id))
                .findFirst();
    }
}
