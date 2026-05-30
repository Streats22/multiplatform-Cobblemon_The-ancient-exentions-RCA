package nl.streats1.ancientextensions;

import nl.streats1.ancientextensions.config.PassportConfig;
import nl.streats1.ancientextensions.dex.RegionalSurveyService;
import nl.streats1.ancientextensions.dex.SurveyBackend;
import nl.streats1.ancientextensions.dex.SurveyOriginService;
import nl.streats1.ancientextensions.dex.SurveyOriginTown;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import nl.streats1.ancientextensions.migration.MigrationService;
import net.minecraft.server.level.ServerPlayer;

/**
 * Mod-wide service container, initialized once per platform at startup.
 */
public final class AncientExtensionsContext {

    @FunctionalInterface
    public interface OriginEffectsListener {
        void onOriginApplied(ServerPlayer player, SurveyRegion region, SurveyOriginTown town, boolean announce);
    }

    @FunctionalInterface
    public interface PassportScreenOpener {
        void open(ServerPlayer player);
    }

    @FunctionalInterface
    public interface JournalScreenOpener {
        void open(ServerPlayer player);
    }

    private static AncientExtensionsContext instance;

    private final RegionalSurveyService surveyService;
    private final SurveyOriginService originService;
    private final MigrationService migrationService;
    private OriginEffectsListener originEffects = (player, region, town, announce) -> { };
    private PassportScreenOpener passportOpener = player -> { };
    private JournalScreenOpener journalOpener = player -> { };

    public AncientExtensionsContext(SurveyBackend backend) {
        this.surveyService = new RegionalSurveyService(backend);
        this.migrationService = new MigrationService(surveyService);
        this.surveyService.bindMigration(migrationService);
        this.originService = new SurveyOriginService(surveyService, this::applyOriginEffects);
    }

    public void setOriginEffects(OriginEffectsListener listener) {
        this.originEffects = listener != null ? listener : (player, region, town, announce) -> { };
    }

    public void setPassportOpener(PassportScreenOpener opener) {
        this.passportOpener = opener != null ? opener : player -> { };
    }

    public void setJournalOpener(JournalScreenOpener opener) {
        this.journalOpener = opener != null ? opener : player -> { };
    }

    public void openPassport(ServerPlayer player) {
        passportOpener.open(player);
    }

    /** Opens the origin picker on join when enabled in config and the player has no stamped origin. */
    public void promptOriginIfNeeded(ServerPlayer player) {
        if (!PassportConfig.openOriginPickerOnJoin()) {
            return;
        }
        if (!originService.hasOrigin(surveyService.get(player))) {
            openPassport(player);
        }
    }

    public void openJournal(ServerPlayer player) {
        journalOpener.open(player);
    }

    private void applyOriginEffects(ServerPlayer player, SurveyRegion region, SurveyOriginTown town, boolean announce) {
        originEffects.onOriginApplied(player, region, town, announce);
    }

    public RegionalSurveyService surveys() {
        return surveyService;
    }

    public SurveyOriginService origins() {
        return originService;
    }

    public MigrationService migration() {
        return migrationService;
    }

    public static void init(AncientExtensionsContext context) {
        instance = context;
    }

    public static AncientExtensionsContext get() {
        if (instance == null) {
            throw new IllegalStateException("AncientExtensionsContext has not been initialized");
        }
        return instance;
    }
}
