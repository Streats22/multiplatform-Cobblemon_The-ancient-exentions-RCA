package nl.streats1.ancientextensions.dex;

import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Hometown within a {@link SurveyRegion}, chosen on the second passport screen.
 */
public enum SurveyOriginTown {
    // Kanto
    PALLET(SurveyRegion.KANTO, "pallet"),
    VIRIDIAN(SurveyRegion.KANTO, "viridian"),
    PEWTER(SurveyRegion.KANTO, "pewter"),
    CERULEAN(SurveyRegion.KANTO, "cerulean"),
    // Johto
    NEW_BARK(SurveyRegion.JOHTO, "new_bark"),
    CHERRYGROVE(SurveyRegion.JOHTO, "cherrygrove"),
    VIOLET(SurveyRegion.JOHTO, "violet"),
    AZALEA(SurveyRegion.JOHTO, "azalea"),
    // Hoenn
    LITTLEROOT(SurveyRegion.HOENN, "littleroot"),
    OLDALE(SurveyRegion.HOENN, "oldale"),
    PETALBURG(SurveyRegion.HOENN, "petalburg"),
    RUSTBORO(SurveyRegion.HOENN, "rustboro"),
    // Sinnoh
    TWINLEAF(SurveyRegion.SINNOH, "twinleaf"),
    SANDGEM(SurveyRegion.SINNOH, "sandgem"),
    JUBILIFE(SurveyRegion.SINNOH, "jubilife"),
    OREBURGH(SurveyRegion.SINNOH, "oreburgh"),
    // Unova
    NUVEMA(SurveyRegion.UNOVA, "nuvema"),
    ACCUMULA(SurveyRegion.UNOVA, "accumula"),
    STRIATON(SurveyRegion.UNOVA, "striaton"),
    NACRENE(SurveyRegion.UNOVA, "nacrene"),
    // Kalos
    VANIVILLE(SurveyRegion.KALOS, "vaniville"),
    AQUACORDE(SurveyRegion.KALOS, "aquacorde"),
    SANTALUNE(SurveyRegion.KALOS, "santalune"),
    LUMIOSE(SurveyRegion.KALOS, "lumiose"),
    // Alola
    IKI_TOWN(SurveyRegion.ALOLA, "iki_town"),
    HAUOLI(SurveyRegion.ALOLA, "hauoli"),
    HEAHEA(SurveyRegion.ALOLA, "heahea"),
    MALIE(SurveyRegion.ALOLA, "malie"),
    // Galar
    POSTWICK(SurveyRegion.GALAR, "postwick"),
    WEDGEHURST(SurveyRegion.GALAR, "wedgehurst"),
    TURFFIELD(SurveyRegion.GALAR, "turffield"),
    MOTOSTOKE(SurveyRegion.GALAR, "motostoke"),
    // Paldea
    CABO_POCO(SurveyRegion.PALDEA, "cabo_poco"),
    LOS_PLATOS(SurveyRegion.PALDEA, "los_platos"),
    MESAGOZA(SurveyRegion.PALDEA, "mesagoza"),
    ARTAZON(SurveyRegion.PALDEA, "artazon");

    private final SurveyRegion region;
    private final String id;

    SurveyOriginTown(SurveyRegion region, String id) {
        this.region = region;
        this.id = id;
    }

    public SurveyRegion getRegion() {
        return region;
    }

    public String getId() {
        return id;
    }

    public Component displayName() {
        return Component.translatable("ancient_extensions.town." + id);
    }

    public static List<SurveyOriginTown> forRegion(SurveyRegion region) {
        return Arrays.stream(values())
                .filter(town -> town.region == region)
                .toList();
    }

    public static Optional<SurveyOriginTown> fromId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(town -> town.id.equalsIgnoreCase(id))
                .findFirst();
    }

    public static Optional<SurveyOriginTown> fromId(SurveyRegion region, String id) {
        return fromId(id).filter(town -> town.region == region);
    }
}
