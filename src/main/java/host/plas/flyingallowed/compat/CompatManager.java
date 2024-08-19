package host.plas.flyingallowed.compat;

import host.plas.bou.compat.EmptyHolder;
import host.plas.bou.compat.HeldHolder;
import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.GPHeld;
import host.plas.flyingallowed.compat.KingdomsHeld;
import host.plas.flyingallowed.compat.LandsHeld;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentSkipListMap;

@Getter @Setter
public class CompatManager {
    @Getter @Setter
    private static ConcurrentSkipListMap<String, HeldHolder> holders = new ConcurrentSkipListMap<>();

    public static void init() {
        String landsIdentifier = "lands";
        HeldHolder landsHolder = new EmptyHolder(landsIdentifier);
        try {
            landsHolder = new LandsHeld();
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("Lands not found, skipping...");
        }
        holders.put(landsIdentifier, landsHolder);

        String griefPreventionIdentifier = "griefprevention";
        HeldHolder griefPreventionHolder = new EmptyHolder(griefPreventionIdentifier);
        try {
            griefPreventionHolder = new GPHeld();
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("GriefPrevention not found, skipping...");
        }
        holders.put(griefPreventionIdentifier, griefPreventionHolder);

        String kingdomsIdentifier = "kingdoms";
        HeldHolder kingdomsHolder = new EmptyHolder(kingdomsIdentifier);
        try {
            kingdomsHolder = new KingdomsHeld();
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("Kingdoms not found, skipping...");
        }
        holders.put(kingdomsIdentifier, kingdomsHolder);

        String ssIdentifier = "superiorskyblock";
        HeldHolder ssHolder = new EmptyHolder(ssIdentifier);
        try {
            ssHolder = new SSkyblockHeld();
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("Kingdoms not found, skipping...");
        }
        holders.put(ssIdentifier, ssHolder);
    }

    public static HeldHolder getHolder(String identifier) {
        return holders.get(identifier);
    }

    public static boolean isEnabled(String identifier) {
        return getHolder(identifier) != null && getHolder(identifier).isEnabled();
    }

    public static HeldHolder getLandsHolder() {
        return getHolder("lands");
    }

    public static HeldHolder getGriefPreventionHolder() {
        return getHolder("griefprevention");
    }

    public static HeldHolder getKingdomsHolder() {
        return getHolder("kingdoms");
    }

    public static HeldHolder getSSHolder() {
        return getHolder("superiorskyblock");
    }

    public static boolean isLandsEnabled() {
        return isEnabled("lands");
    }

    public static boolean isGriefPreventionEnabled() {
        return isEnabled("griefprevention");
    }

    public static boolean isKingdomsEnabled() {
        return isEnabled("kingdoms");
    }

    public static boolean isSSEnabled() {
        return isEnabled("superiorskyblock");
    }
}