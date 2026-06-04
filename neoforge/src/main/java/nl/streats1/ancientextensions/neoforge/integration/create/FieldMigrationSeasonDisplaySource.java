package nl.streats1.ancientextensions.neoforge.integration.create;

import nl.streats1.ancientextensions.field.FieldSurveyWorldSnapshot;

public final class FieldMigrationSeasonDisplaySource extends AbstractFieldSurveyDisplaySource {

    public static final FieldMigrationSeasonDisplaySource INSTANCE = new FieldMigrationSeasonDisplaySource();

    private FieldMigrationSeasonDisplaySource() {
    }

    @Override
    protected String provideText(FieldSurveyWorldSnapshot snapshot) {
        return snapshot.seasonLine();
    }

    @Override
    protected String getTranslationKey() {
        return "field_migration_season";
    }
}
