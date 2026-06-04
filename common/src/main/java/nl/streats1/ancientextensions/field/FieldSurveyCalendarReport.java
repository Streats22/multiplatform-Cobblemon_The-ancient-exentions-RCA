package nl.streats1.ancientextensions.field;

import nl.streats1.ancientextensions.migration.MigrationBiomeContext;
import nl.streats1.ancientextensions.migration.MigrationSeason;
import nl.streats1.ancientextensions.migration.MigrationSeasonClock;
import nl.streats1.ancientextensions.migration.MigrationSpawnPoolIndex;
import nl.streats1.ancientextensions.migration.MigrationSpecies;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

/** Wall calendar readout — spawn estimates from migration pool data (not live telemetry). */
public final class FieldSurveyCalendarReport {

    private static final int MAX_SPECIES = 10;

    private static final ChatFormatting BODY = ChatFormatting.DARK_GRAY;
    private static final ChatFormatting SECTION = ChatFormatting.DARK_AQUA;
    private static final ChatFormatting EMPHASIS = ChatFormatting.DARK_GREEN;
    private static final ChatFormatting NOTE = ChatFormatting.GRAY;

    private FieldSurveyCalendarReport() {
    }

    public static List<Component> buildLines(Level level, BlockPos pos) {
        List<Component> lines = new ArrayList<>();

        lines.add(Component.translatable("ancient_extensions.field_calendar.disclaimer")
                .withStyle(NOTE, ChatFormatting.ITALIC));
        lines.add(Component.empty());

        if (!(level instanceof ServerLevel server)) {
            lines.add(Component.translatable("ancient_extensions.field_calendar.no_server")
                    .withStyle(BODY));
            return List.copyOf(lines);
        }

        MigrationSeason season = MigrationSeasonClock.currentSeason(server);
        Holder<Biome> biome = server.getBiome(pos);
        ResourceLocation biomeId = biome.unwrapKey().map(key -> key.location()).orElse(null);
        String biomeName = MigrationBiomeContext.prettyBiomeName(biomeId);

        lines.add(Component.translatable(
                        "ancient_extensions.field_calendar.header",
                        season.displayName(),
                        biomeName
                )
                .withStyle(SECTION, ChatFormatting.BOLD));
        lines.add(Component.translatable(
                        "ancient_extensions.field_calendar.calendar_source",
                        MigrationSeasonClock.calendarSource().label()
                )
                .withStyle(BODY));
        lines.add(Component.empty());

        if (biomeId == null) {
            lines.add(Component.translatable("ancient_extensions.field_calendar.biome_unknown")
                    .withStyle(BODY));
            return List.copyOf(lines);
        }

        if (MigrationBiomeContext.isVanillaBiome(biomeId)) {
            lines.add(Component.translatable("ancient_extensions.field_calendar.vanilla_note")
                    .withStyle(NOTE));
            lines.add(Component.empty());
        }

        List<MigrationSpawnPoolIndex.SpawnEstimate> estimates =
                MigrationSpawnPoolIndex.estimateForBiome(season, biomeId, MAX_SPECIES);

        if (estimates.isEmpty()) {
            lines.add(Component.translatable("ancient_extensions.field_calendar.no_spawns")
                    .withStyle(BODY));
        } else {
            lines.add(Component.translatable("ancient_extensions.field_calendar.section_likely")
                    .withStyle(SECTION, ChatFormatting.BOLD));
            for (MigrationSpawnPoolIndex.SpawnEstimate estimate : estimates) {
                lines.add(speciesLine(estimate));
            }
        }

        lines.add(Component.empty());
        lines.add(Component.translatable("ancient_extensions.field_calendar.section_survey")
                .withStyle(SECTION, ChatFormatting.BOLD));
        int shown = 0;
        for (ResourceLocation featured : MigrationSpecies.speciesForSeason(season)) {
            if (shown >= 6) {
                break;
            }
            lines.add(Component.literal("• " + prettySpecies(featured))
                    .withStyle(EMPHASIS));
            shown++;
        }

        lines.add(Component.empty());
        lines.add(Component.translatable("ancient_extensions.field_calendar.sensor_hint")
                .withStyle(NOTE, ChatFormatting.ITALIC));

        return List.copyOf(lines);
    }

    private static Component speciesLine(MigrationSpawnPoolIndex.SpawnEstimate estimate) {
        String tierKey = switch (estimate.tier()) {
            case HIGH -> "ancient_extensions.field_calendar.tier_high";
            case LIKELY -> "ancient_extensions.field_calendar.tier_likely";
            case POSSIBLE -> "ancient_extensions.field_calendar.tier_possible";
        };
        return Component.translatable(
                "ancient_extensions.field_calendar.species_line",
                prettySpecies(estimate.speciesId()),
                Component.translatable(tierKey)
        ).withStyle(BODY);
    }

    private static String prettySpecies(ResourceLocation id) {
        if (id == null) {
            return "?";
        }
        return capitalizePath(id.getPath());
    }

    private static String capitalizePath(String path) {
        String[] words = path.replace('_', ' ').split(" ");
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                name.append(' ');
            }
            String word = words[i];
            if (!word.isEmpty()) {
                name.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    name.append(word.substring(1).toLowerCase());
                }
            }
        }
        return name.toString();
    }
}
