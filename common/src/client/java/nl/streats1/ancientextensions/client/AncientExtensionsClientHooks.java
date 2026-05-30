package nl.streats1.ancientextensions.client;

/**
 * Client hooks wired by each platform's client initializer.
 */
public final class AncientExtensionsClientHooks {

    private static OriginSelectSender originSelectSender = (regionId, townId) -> { };
    private static Runnable tierRewardClaimSender = () -> { };

    private AncientExtensionsClientHooks() {
    }

    public static void setOriginSelectSender(OriginSelectSender sender) {
        originSelectSender = sender != null ? sender : (regionId, townId) -> { };
    }

    /** @deprecated use {@link #sendSelectOrigin(String, String)} */
    @Deprecated
    public static void setRegionSelectSender(OriginSelectSender sender) {
        setOriginSelectSender(sender);
    }

    public static void setTierRewardClaimSender(Runnable sender) {
        tierRewardClaimSender = sender != null ? sender : () -> { };
    }

    public static void sendSelectOrigin(String regionId, String townId) {
        originSelectSender.send(regionId, townId);
    }

    public static void sendClaimTierRewards() {
        tierRewardClaimSender.run();
    }

    @FunctionalInterface
    public interface OriginSelectSender {
        void send(String regionId, String townId);
    }
}
