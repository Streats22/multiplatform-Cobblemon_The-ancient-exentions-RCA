package nl.streats1.ancientextensions.client;

/**
 * Client hooks wired by each platform's client initializer.
 */
public final class AncientExtensionsClientHooks {

    private static RegionSelectSender regionSelectSender = regionId -> { };

    private AncientExtensionsClientHooks() {
    }

    public static void setRegionSelectSender(RegionSelectSender sender) {
        regionSelectSender = sender != null ? sender : regionId -> { };
    }

    public static void sendSelectRegion(String regionId) {
        regionSelectSender.send(regionId);
    }

    @FunctionalInterface
    public interface RegionSelectSender {
        void send(String regionId);
    }
}
