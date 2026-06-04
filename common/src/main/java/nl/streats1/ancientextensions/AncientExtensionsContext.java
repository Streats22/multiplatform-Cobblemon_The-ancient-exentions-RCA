package nl.streats1.ancientextensions;

import net.minecraft.server.level.ServerPlayer;

import nl.streats1.ancientextensions.config.PassportConfig;
import nl.streats1.ancientextensions.dex.*;
import nl.streats1.ancientextensions.integration.mca.McaIntegration;
import nl.streats1.ancientextensions.migration.MigrationService;

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
    private final TierRewardService tierRewardService;
    private final MigrationService migrationService;
    private OriginEffectsListener originEffects = (player, region, town, announce) -> {
    };
    private PassportScreenOpener passportOpener = player -> {
    };
    private JournalScreenOpener journalOpener = player -> {
    };

    public AncientExtensionsContext(SurveyBackend backend) {
        this.surveyService = new RegionalSurveyService(backend);
        this.migrationService = new MigrationService(surveyService);
        this.surveyService.bindMigration(migrationService);
        this.originService = new SurveyOriginService(surveyService, this::applyOriginEffects);
        this.tierRewardService = new TierRewardService(surveyService);
    }

    public void setOriginEffects(OriginEffectsListener listener) {
        this.originEffects = listener != null ? listener : (player, region, town, announce) -> {
        };
    }

    public void setPassportOpener(PassportScreenOpener opener) {
        this.passportOpener = opener != null ? opener : player -> {
        };
    }

    public void setJournalOpener(JournalScreenOpener opener) {
        this.journalOpener = opener != null ? opener : player -> {
        };
    }

    public void openPassport(ServerPlayer player) {
        passportOpener.open(player);
    }

    /**
     * Opens the origin picker on join when enabled in config and the player has no stamped origin.
     */
    public void promptOriginIfNeeded(ServerPlayer player) {
        if (!PassportConfig.openOriginPickerOnJoin()) {
            return;
        }
        if (McaIntegration.shouldDeferOriginPickerOnJoin(player)) {
            return;
        }
        if (!originService.hasOrigin(surveyService.get(player))) {
            openPassport(player);
        }
    }

    /**
     * Called after MCA Reborn's destiny intro closes (or fallback timer) to open the passport stamp flow.
     */
    public void promptOriginAfterMcaIntro(ServerPlayer player) {
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

    public TierRewardService tierRewards() {
        return tierRewardService;
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
