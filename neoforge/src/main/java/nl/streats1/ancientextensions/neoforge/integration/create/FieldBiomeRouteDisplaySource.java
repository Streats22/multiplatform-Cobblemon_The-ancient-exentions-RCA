package nl.streats1.ancientextensions.neoforge.integration.create;

import nl.streats1.ancientextensions.field.FieldSurveyWorldSnapshot;

public final class FieldBiomeRouteDisplaySource extends AbstractFieldSurveyDisplaySource {

    public static final FieldBiomeRouteDisplaySource INSTANCE = new FieldBiomeRouteDisplaySource();

    private FieldBiomeRouteDisplaySource() {
    }

    @Override
    protected String provideText(FieldSurveyWorldSnapshot snapshot) {
        return snapshot.biomeRouteLine();
    }

    @Override
    protected String getTranslationKey() {
        return "field_biome_route";
    }
}
