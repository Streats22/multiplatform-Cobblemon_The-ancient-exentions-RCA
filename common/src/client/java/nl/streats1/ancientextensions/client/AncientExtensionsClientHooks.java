package nl.streats1.ancientextensions.client;

/**
 * Client hooks wired by each platform's client initializer.
 */
public final class AncientExtensionsClientHooks {

    private static OriginSelectSender originSelectSender = (regionId, townId) -> { };
    private static Runnable tierRewardClaimSender = () -> { };
    private static TabletActionSender tabletActionSender = action -> { };

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

    public static void setTabletActionSender(TabletActionSender sender) {
        tabletActionSender = sender != null ? sender : action -> { };
    }

    public static void sendSelectOrigin(String regionId, String townId) {
        originSelectSender.send(regionId, townId);
    }

    public static void sendClaimTierRewards() {
        tierRewardClaimSender.run();
    }

    public static void sendTabletAction(byte action) {
        tabletActionSender.send(action);
    }

    @FunctionalInterface
    public interface TabletActionSender {
        void send(byte action);
    }

    @FunctionalInterface
    public interface OriginSelectSender {
        void send(String regionId, String townId);
    }
}
