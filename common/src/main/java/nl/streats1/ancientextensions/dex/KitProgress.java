package nl.streats1.ancientextensions.dex;

import net.minecraft.nbt.CompoundTag;

/**
 * Professor's kit and starter kit grant flags.
 */
public final class KitProgress {

    private boolean professorsKitDeployed;
    private boolean starterKitGranted;

    void load(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return;
        }
        professorsKitDeployed = tag.getBoolean("professorsKitDeployed");
        starterKitGranted = tag.getBoolean("starterKitGranted");
    }

    void loadLegacy(CompoundTag tag) {
        professorsKitDeployed = tag.getBoolean("professorsKitDeployed");
        if (!tag.contains("professorsKitDeployed") && tag.getBoolean("receivedProfessorsKit")) {
            professorsKitDeployed = true;
        }
        starterKitGranted = tag.getBoolean("starterKitGranted");
    }

    CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("professorsKitDeployed", professorsKitDeployed);
        tag.putBoolean("starterKitGranted", starterKitGranted);
        return tag;
    }

    public boolean hasDeployedProfessorsKit() {
        return professorsKitDeployed;
    }

    public void markProfessorsKitDeployed() {
        professorsKitDeployed = true;
    }

    public boolean hasStarterKitGranted() {
        return starterKitGranted;
    }

    public void markStarterKitGranted() {
        starterKitGranted = true;
    }
}
