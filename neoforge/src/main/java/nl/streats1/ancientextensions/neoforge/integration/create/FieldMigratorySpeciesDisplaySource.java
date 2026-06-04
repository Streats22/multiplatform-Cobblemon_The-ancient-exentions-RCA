package nl.streats1.ancientextensions.neoforge.integration.create;

import nl.streats1.ancientextensions.field.FieldSurveyWorldSnapshot;

public final class FieldMigratorySpeciesDisplaySource extends AbstractFieldSurveyDisplaySource {

    public static final FieldMigratorySpeciesDisplaySource INSTANCE = new FieldMigratorySpeciesDisplaySource();

    private FieldMigratorySpeciesDisplaySource() {
    }

    @Override
    protected String provideText(FieldSurveyWorldSnapshot snapshot) {
        return snapshot.speciesLine();
    }

    @Override
    protected String getTranslationKey() {
        return "field_migratory_species";
    }
}
